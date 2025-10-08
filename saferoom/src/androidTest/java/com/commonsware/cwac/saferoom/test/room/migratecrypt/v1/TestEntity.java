package com.commonsware.cwac.saferoom.test.room.migratecrypt.v1;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
@SuppressWarnings("ClassCanBeRecord")
public class TestEntity {
    @PrimaryKey
    @NonNull
    public final String id;

    public TestEntity(@NonNull String id) {
        this.id = id;
    }
}
