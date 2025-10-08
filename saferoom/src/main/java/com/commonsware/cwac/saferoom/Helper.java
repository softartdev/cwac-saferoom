/*
 * Copyright (C) 2016 The Android Open Source Project
 * Modifications Copyright (c) 2017 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.cwac.saferoom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;

import java.io.File;
import java.util.Arrays;

/**
 * SupportSQLiteOpenHelper implementation that works with SQLCipher for Android
 */
class Helper implements SupportSQLiteOpenHelper {
    private final OpenHelper delegate;
    private final byte[] passphrase;
    private final boolean clearPassphrase;

    Helper(Context context, String name, Callback callback, byte[] passphrase,
           SafeHelperFactory.Options options) {
        System.loadLibrary("sqlcipher");
        clearPassphrase = options.clearPassphrase;
        delegate = createDelegate(context, name, callback, options);
        this.passphrase = passphrase;
    }

    private OpenHelper createDelegate(Context context, String name,
                                      final Callback callback, SafeHelperFactory.Options options) {
        final Database[] dbRef = new Database[1];

        return (new OpenHelper(context, name, dbRef, callback, options));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public String getDatabaseName() {
        return delegate.getDatabaseName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void setWriteAheadLoggingEnabled(boolean enabled) {
        delegate.setWriteAheadLoggingEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: by default, this implementation zeros out the passphrase after opening the
     * database
     */
    @NonNull
    @Override
    synchronized public SupportSQLiteDatabase getWritableDatabase() {
        SupportSQLiteDatabase result;

        try {
            result = delegate.getWritableSupportDatabase(passphrase);
        } catch (Exception e) {
            if (passphrase != null) {
                boolean isCleared = true;

                for (byte b : passphrase) {
                    isCleared = isCleared && (b == (byte) 0);
                }

                if (isCleared) {
                    throw new IllegalStateException("The passphrase appears to be cleared. This happens by" +
                            "default the first time you use the factory to open a database, so we can remove the" +
                            "cleartext passphrase from memory. If you close the database yourself, please use a" +
                            "fresh SafeHelperFactory to reopen it. If something else (e.g., Room) closed the" +
                            "database, and you cannot control that, use SafeHelperFactory.Options to opt out of" +
                            "the automatic password clearing step. See the project README for more information.");
                }
            }
            throw e;
        }
        if (clearPassphrase && passphrase != null) {
            Arrays.fill(passphrase, (byte) 0);
        }
        return (result);
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: this implementation delegates to getWritableDatabase(), to ensure
     * that we only need the passphrase once
     */
    @NonNull
    @Override
    public SupportSQLiteDatabase getReadableDatabase() {
        return (getWritableDatabase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void close() {
        delegate.close();
    }

    static class OpenHelper extends SQLiteOpenHelper {
        private final Database[] dbRef;
        private final Callback callback;
        private volatile boolean migrated;
        private final SafeHelperFactory.Options options;
        private final Context context;
        private final String databaseName;
        private SQLiteDatabase internalDb;

        OpenHelper(Context context, String name, Database[] dbRef, Callback callback,
                   SafeHelperFactory.Options options) {
            super(context, name, null, callback.version);

            this.dbRef = dbRef;
            this.callback = callback;
            this.options = options;
            this.context = context;
            this.databaseName = name;
        }

        synchronized SupportSQLiteDatabase getWritableSupportDatabase(byte[] passphrase) {
            migrated = false;

            // Check if we already have an open database
            if (internalDb != null) {
                if (internalDb.isOpen()) {
                    return getWrappedDb(internalDb);
                } else {
                    // Database was closed, clear references
                    internalDb = null;
                    dbRef[0] = null;
                }
            }

            // Get database file path
            File dbFile = context.getDatabasePath(databaseName);
            
            // Ensure parent directory exists
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            boolean isNewDatabase = !dbFile.exists();

            // Clean up WAL/journal files for existing databases before reopening
            if (!isNewDatabase) {
                File walFile = new File(dbFile.getAbsolutePath() + "-wal");
                File shmFile = new File(dbFile.getAbsolutePath() + "-shm");
                File journalFile = new File(dbFile.getAbsolutePath() + "-journal");
                if (walFile.exists()) walFile.delete();
                if (shmFile.exists()) shmFile.delete();
                if (journalFile.exists()) journalFile.delete();
            }

            // Open the database with encryption
            int flags = SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.OPEN_READWRITE;
            internalDb = SQLiteDatabase.openDatabase(
                    dbFile.getAbsolutePath(), passphrase, null, flags, null, null);

            // Execute preKey SQL if provided
            if (options != null && options.preKeySql != null) {
                internalDb.rawExecSQL(options.preKeySql);
            }

            // Execute postKey SQL if provided  
            if (options != null && options.postKeySql != null) {
                internalDb.rawExecSQL(options.postKeySql);
            }

            // Handle database creation and upgrades
            int version = 0;
            try {
                version = internalDb.getVersion();
            } catch (Exception e) {
                // If we can't get version, treat as new database
                isNewDatabase = true;
            }
            
            if (isNewDatabase || version == 0) {
                callback.onCreate(getWrappedDb(internalDb));
                internalDb.setVersion(callback.version);
            } else if (version != callback.version) {
                if (version > callback.version) {
                    callback.onDowngrade(getWrappedDb(internalDb), version, callback.version);
                } else {
                    callback.onUpgrade(getWrappedDb(internalDb), version, callback.version);
                }
                internalDb.setVersion(callback.version);
                migrated = true;
            }

            if (migrated) {
                internalDb.close();
                internalDb = null;
                dbRef[0] = null;
                return getWritableSupportDatabase(passphrase);
            }

            // Call onOpen callback
            callback.onOpen(getWrappedDb(internalDb));

            return getWrappedDb(internalDb);
        }

        synchronized Database getWrappedDb(SQLiteDatabase db) {
            if (db == null) {
                throw new IllegalStateException("Database is null");
            }

            Database wrappedDb = dbRef[0];

            if (wrappedDb == null) {
                wrappedDb = new Database(db, this);
                dbRef[0] = wrappedDb;
            }

            return wrappedDb;
        }

        synchronized void onDatabaseClosed() {
            // Clear our reference when the database is closed via db.close()
            internalDb = null;
            dbRef[0] = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            callback.onCreate(getWrappedDb(sqLiteDatabase));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            migrated = true;
            callback.onUpgrade(getWrappedDb(sqLiteDatabase), oldVersion, newVersion);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onConfigure(SQLiteDatabase db) {
            callback.onConfigure(getWrappedDb(db));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            migrated = true;
            callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onOpen(SQLiteDatabase db) {
            if (!migrated) {
                // from Google: "if we've migrated, we'll re-open the db so we  should not call the callback."
                callback.onOpen(getWrappedDb(db));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void close() {
            if (dbRef[0] != null) {
                dbRef[0] = null;
            }
            if (internalDb != null && internalDb.isOpen()) {
                internalDb.close();
                internalDb = null;
            }
        }
    }
}
