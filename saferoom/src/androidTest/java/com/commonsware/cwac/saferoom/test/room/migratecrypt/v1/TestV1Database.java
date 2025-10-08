package com.commonsware.cwac.saferoom.test.room.migratecrypt.v1;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TestEntity.class}, version = 1, exportSchema = false)
abstract public class TestV1Database extends RoomDatabase {
    public abstract TestDao testStore();
}
