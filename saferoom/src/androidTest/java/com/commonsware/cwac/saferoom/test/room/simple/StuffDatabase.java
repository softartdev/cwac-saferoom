/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _Android's Architecture Components_
 https://commonsware.com/AndroidArch
 */

package com.commonsware.cwac.saferoom.test.room.simple;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.commonsware.cwac.saferoom.SafeHelperFactory;

@Database(
        entities = {Customer.class, VersionedThingy.class, Category.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({TypeTransmogrifier.class})
abstract class StuffDatabase extends RoomDatabase {
    abstract StuffStore stuffStore();

    static final String DB_NAME = "stuff.db";

    @SuppressWarnings("SameParameterValue")
    static StuffDatabase create(Context context, boolean memoryOnly, boolean truncate) {
        RoomDatabase.Builder<StuffDatabase> b;
        if (memoryOnly) {
            b = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), StuffDatabase.class);
        } else {
            b = Room.databaseBuilder(context.getApplicationContext(), StuffDatabase.class, DB_NAME);
        }
        if (truncate) {
            // Note: setJournalMode may not be available in Room 2.8.1
            // b.setJournalMode(JournalMode.TRUNCATE);
            Log.d("StuffDatabase", "Truncate journal mode not set - API not available");
        }
        b.openHelperFactory(SafeHelperFactory.fromUser(new SpannableStringBuilder("sekrit")));

        return (b.build());
    }
}
