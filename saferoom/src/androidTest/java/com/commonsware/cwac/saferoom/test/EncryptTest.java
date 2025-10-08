package com.commonsware.cwac.saferoom.test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.commonsware.cwac.saferoom.SQLCipherUtils;
import com.commonsware.cwac.saferoom.SafeHelperFactory;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

@RunWith(AndroidJUnit4.class)
public class EncryptTest {
    private static final String DB_NAME = "db";
    private static final String PASSPHRASE = "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people’s hats off—then, I account it high time to get to sea as soon as I can. This is my substitute for pistol and ball. With a philosophical flourish Cato throws himself upon his sword; I quietly take to the ship. There is nothing surprising in this. If they but knew it, almost all men in their degree, some time or other, cherish very nearly the same feelings towards the ocean with me.";

    @After
    public void tearDown() {
        Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File db = ctxt.getDatabasePath(DB_NAME);

        if (db.exists()) {
            boolean deleted = db.delete();
            Log.d("EncryptTest", "Deleted " + db.getAbsolutePath() + ": " + deleted);
        }
        File journal = new File(db.getParentFile(), DB_NAME + "-journal");

        if (journal.exists()) {
            boolean deleted = journal.delete();
            Log.d("EncryptTest", "Deleted " + journal.getAbsolutePath() + ": " + deleted);
        }
    }

    @Test
    public void charEnkey() throws Exception {
        final Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();

        enkey((Callable<Void>) () -> {
            SQLCipherUtils.encrypt(ctxt, ctxt.getDatabasePath(DB_NAME), PASSPHRASE.toCharArray());

            return null;
        });
    }

    @Test
    public void editableEnkey() throws Exception {
        final Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();

        enkey((Callable<Void>) () -> {
            SQLCipherUtils.encrypt(ctxt, DB_NAME, new SpannableStringBuilder(PASSPHRASE));

            return null;
        });
    }

    @Test(expected = FileNotFoundException.class)
    public void fileNotFound() throws Exception {
        final Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();

        enkey((Callable<Void>) () -> {
            SQLCipherUtils.encrypt(ctxt, "/this/does/not/exist", PASSPHRASE.toCharArray());

            return null;
        });
    }

    private void enkey(Callable<?> encrypter) throws Exception {
        final Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals(SQLCipherUtils.State.DOES_NOT_EXIST, SQLCipherUtils.getDatabaseState(ctxt, DB_NAME));

        SQLiteDatabase plainDb =
                SQLiteDatabase.openOrCreateDatabase(ctxt.getDatabasePath(DB_NAME).getAbsolutePath(),
                        null);

        plainDb.execSQL("CREATE TABLE foo (bar, goo);");
        plainDb.execSQL("INSERT INTO foo (bar, goo) VALUES (?, ?)",
                new Object[]{1, "two"});

        assertOriginalContent(plainDb);
        plainDb.close();

        assertEquals(SQLCipherUtils.State.UNENCRYPTED, SQLCipherUtils.getDatabaseState(ctxt, DB_NAME));

        encrypter.call();

        assertEquals(SQLCipherUtils.State.ENCRYPTED, SQLCipherUtils.getDatabaseState(ctxt, DB_NAME));

        SafeHelperFactory factory =
                SafeHelperFactory.fromUser(new SpannableStringBuilder(PASSPHRASE));
        SupportSQLiteOpenHelper helper =
                factory.create(InstrumentationRegistry.getInstrumentation().getTargetContext(), DB_NAME,
                        new Callback(1));
        SupportSQLiteDatabase db = helper.getReadableDatabase();

        assertOriginalContent(db);
        db.close();
    }

    private void assertOriginalContent(SupportSQLiteDatabase db) {
        Cursor c = db.query("SELECT bar, goo FROM foo;");

        assertNotNull(c);
        assertEquals(1, c.getCount());
        assertTrue(c.moveToFirst());
        assertEquals(1, c.getInt(0));
        assertEquals("two", c.getString(1));
        c.close();
    }

    private void assertOriginalContent(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT bar, goo FROM foo;", null);

        assertNotNull(c);
        assertEquals(1, c.getCount());
        assertTrue(c.moveToFirst());
        assertEquals(1, c.getInt(0));
        assertEquals("two", c.getString(1));
        c.close();
    }

    @SuppressWarnings("NewClassNamingConvention")
    private static final class Callback extends SupportSQLiteOpenHelper.Callback {
        public Callback(int version) {
            super(version);
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
