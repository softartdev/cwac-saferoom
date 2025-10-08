package com.commonsware.cwac.saferoom.test.room.simple;

import static org.junit.Assert.assertFalse;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

public class DaoTruncateTests extends DaoTests {
    @Before
    public void setUp() {
        db = StuffDatabase.create(InstrumentationRegistry.getInstrumentation().getTargetContext(), false, true);
        store = db.stuffStore();
    }

    @Test
    public void confirmWalOff() {
        assertFalse(db.getOpenHelper().getWritableDatabase().isWriteAheadLoggingEnabled());
    }
}
