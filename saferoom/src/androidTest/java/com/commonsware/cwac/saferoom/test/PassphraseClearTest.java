package com.commonsware.cwac.saferoom.test;

import static org.junit.Assert.assertArrayEquals;

import android.content.Context;
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
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class PassphraseClearTest {
    private static final String DB_NAME = "db";
    private static final String PASSPHRASE = "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people’s hats off—then, I account it high time to get to sea as soon as I can. This is my substitute for pistol and ball. With a philosophical flourish Cato throws himself upon his sword; I quietly take to the ship. There is nothing surprising in this. If they but knew it, almost all men in their degree, some time or other, cherish very nearly the same feelings towards the ocean with me.";
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @After
    public void tearDown() {
        File db = context.getDatabasePath(DB_NAME);
        File parentDir = db.getParentFile();
        if (parentDir != null) {
            File[] files = parentDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    boolean deleted = f.delete();
                    Log.d("PassphraseClearTest", "Deleted " + f.getAbsolutePath() + ": " + deleted);
                }
            }
        }
    }

    @Test
    public void charArrayCleared() {
        char[] charArray = PASSPHRASE.toCharArray();
        SafeHelperFactory factory = new SafeHelperFactory(charArray);
        SupportSQLiteOpenHelper helper = factory.create(context, DB_NAME, new Callback(1));
        SupportSQLiteDatabase db = helper.getWritableDatabase();
        Log.d("PassphraseClearTest", "DB Path: " + db.getPath());

        helper.close();

        char[] expected = new char[charArray.length];
        Arrays.fill(expected, (char) 0);

        assertArrayEquals(expected, charArray);
    }

    @Test
    public void byteArrayCleared() {
        char[] charArray = PASSPHRASE.toCharArray();
        byte[] byteArray = new String(charArray).getBytes();
        SafeHelperFactory factory = new SafeHelperFactory(byteArray);
        SupportSQLiteOpenHelper helper = factory.create(context, DB_NAME, new Callback(1));
        SupportSQLiteDatabase db = helper.getWritableDatabase();
        Log.d("PassphraseClearTest", "DB Path: " + db.getPath());

        helper.close();

        byte[] expected = new byte[byteArray.length];
        Arrays.fill(expected, (byte) 0);

        assertArrayEquals(expected, byteArray);
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
