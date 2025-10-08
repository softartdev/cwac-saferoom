package com.commonsware.cwac.saferoom.test;

import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.commonsware.cwac.saferoom.SafeHelperFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MigratingImportTest {
    private static final String DB_NAME = "test.db";
    private static final String PASSPHRASE = "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people’s hats off—then, I account it high time to get to sea as soon as I can. This is my substitute for pistol and ball. With a philosophical flourish Cato throws himself upon his sword; I quietly take to the ship. There is nothing surprising in this. If they but knew it, almost all men in their degree, some time or other, cherish very nearly the same feelings towards the ocean with me.";

    @Before
    public void setUp() {
        InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase(DB_NAME);
    }

    @Test
    public void safe() {
        final Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();

        System.loadLibrary("sqlcipher");
        SafeHelperFactory.Options options = SafeHelperFactory.Options.builder()
                .setClearPassphrase(false)
                .build();
        SafeHelperFactory factory = new SafeHelperFactory(PASSPHRASE.toCharArray(), options);
        androidx.sqlite.db.SupportSQLiteOpenHelper helper = factory.create(ctxt, DB_NAME, new androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                // should not need any tables to reproduce the problem
            }

            @Override
            public void onUpgrade(@NonNull SupportSQLiteDatabase db, int oldVersion, int newVersion) {
                throw new IllegalStateException("Wait, wut?");
            }
        });

        helper.getWritableDatabase();
        helper.close();

        ImportingSafeDatabase room = ImportingSafeDatabase.gimme(ctxt, options);
        SupportSQLiteDatabase db = room.getOpenHelper().getWritableDatabase();

        try {
            // Enable WAL if it's not already enabled
            if (!db.isWriteAheadLoggingEnabled()) {
                db.enableWriteAheadLogging();
            }
            assertTrue(db.isWriteAheadLoggingEnabled());
        } finally {
            room.close();
        }
    }

    @Test
    public void notQuiteAsSafeButStillNice() {
        final Context ctxt = InstrumentationRegistry.getInstrumentation().getTargetContext();

        android.database.sqlite.SQLiteOpenHelper helper = new LessSafeNonRoomHelper(ctxt);

        helper.getWritableDatabase();
        helper.close();

        ImportingLessSafeDatabase room = ImportingLessSafeDatabase.gimme(ctxt);
        SupportSQLiteDatabase db = room.getOpenHelper().getWritableDatabase();

        try {
            // Enable WAL if it's not already enabled
            if (!db.isWriteAheadLoggingEnabled()) {
                db.enableWriteAheadLogging();
            }
            assertTrue(db.isWriteAheadLoggingEnabled());
        } finally {
            room.close();
        }
    }


    @SuppressWarnings("NewClassNamingConvention")
    private static class LessSafeNonRoomHelper extends android.database.sqlite.SQLiteOpenHelper {
        LessSafeNonRoomHelper(@NonNull Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(android.database.sqlite.SQLiteDatabase db) {
            // should not need any tables to reproduce the problem
        }

        @Override
        public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
            throw new IllegalStateException("Wait, wut?");
        }
    }

    @Entity
    @SuppressWarnings("NewClassNamingConvention")
    static class SillyEntity {
        @PrimaryKey(autoGenerate = true)
        @SuppressWarnings("unused")
        long id;
        
        @SuppressWarnings("unused")
        public long getId() {
            return id;
        }
    }

    @Database(entities = {SillyEntity.class}, version = 2, exportSchema = false)
    static abstract class ImportingSafeDatabase extends RoomDatabase {
        static ImportingSafeDatabase gimme(Context ctxt, SafeHelperFactory.Options options) {
            return Room.databaseBuilder(ctxt, ImportingSafeDatabase.class, DB_NAME)
                    .openHelperFactory(new SafeHelperFactory(PASSPHRASE.toCharArray(), options))
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
    }

    @Database(entities = {SillyEntity.class}, version = 2, exportSchema = false)
    static abstract class ImportingLessSafeDatabase extends RoomDatabase {
        static ImportingLessSafeDatabase gimme(Context ctxt) {
            return Room.databaseBuilder(ctxt, ImportingLessSafeDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `SillyEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        }
    };
}
