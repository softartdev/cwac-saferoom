package com.commonsware.cwac.saferoom.test;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.commonsware.cwac.saferoom.SafeHelperFactory;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class PreHookSqlTest {
    private static final String DB_NAME = "db";
    private static final String PASSPHRASE = "cufflink powerboat mundane vagrancy ragweed waving";
    private static final String PREKEY_SQL = "PRAGMA cipher_default_kdf_iter = 4000";

    @After
    public void tearDown() {
        Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File db = ctxt.getDatabasePath(DB_NAME);
        File parentDir = db.getParentFile();
        if (parentDir != null) {
            File[] files = parentDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    // Ignore delete failures in test cleanup
                    boolean deleted = f.delete();
                    Log.d("PreHookSqlTest", "Deleted " + f.getAbsolutePath() + ": " + deleted);
                }
            }
        }
    }

    @Test
    public void testPreKeySql() throws IOException {
        SafeHelperFactory.Options options = SafeHelperFactory.Options.builder().setPreKeySql(PREKEY_SQL).build();
        SafeHelperFactory factory =
                SafeHelperFactory.fromUser(new SpannableStringBuilder(PASSPHRASE), options);
        SupportSQLiteOpenHelper helper =
                factory.create(InstrumentationRegistry.getInstrumentation().getTargetContext(), DB_NAME,
                        new Callback(1));
        SupportSQLiteDatabase db = helper.getWritableDatabase();

        assertEquals(1, db.getVersion());

        db.close();
    }

    @SuppressWarnings("NewClassNamingConvention")
    private static final class Callback extends SupportSQLiteOpenHelper.Callback {
        public Callback(int version) {
            super(version);
        }

        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE foo (bar, goo);");
            db.execSQL("INSERT INTO foo (bar, goo) VALUES (?, ?)", new Object[]{1, "two"});
        }

        @Override
        public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
