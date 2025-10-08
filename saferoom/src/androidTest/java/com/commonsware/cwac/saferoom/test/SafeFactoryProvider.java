package com.commonsware.cwac.saferoom.test;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.commonsware.dbtest.FactoryProvider;

import java.io.File;

class SafeFactoryProvider implements FactoryProvider {

    @NonNull
    @Override
    public SupportSQLiteOpenHelper.Factory getFactory() {
        return SafeHelperFactory.fromUser(new SpannableStringBuilder("sekrit"));
    }

    @Override
    public void tearDownDatabase(Context context,
                                 @NonNull SupportSQLiteOpenHelper.Factory factory,
                                 SupportSQLiteOpenHelper helper) {
        String name = helper.getDatabaseName();
        if (name != null) {
            File db = context.getDatabasePath(name);
            if (db.exists()) {
                // Ignore delete failures in test cleanup
                boolean deleted = db.delete();
                Log.d("SafeFactoryProvider", "Deleted " + db.getAbsolutePath() + ": " + deleted);
            }
            File journal = new File(db.getParentFile(), name + "-journal");
            if (journal.exists()) {
                // Ignore delete failures in test cleanup
                boolean deleted = journal.delete();
                Log.d("SafeFactoryProvider", "Deleted " + journal.getAbsolutePath() + ": " + deleted);
            }
        }
    }
}
