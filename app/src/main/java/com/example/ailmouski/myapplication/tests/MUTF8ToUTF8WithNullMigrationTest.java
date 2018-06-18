package com.example.ailmouski.myapplication.tests;

import android.util.Log;

import com.example.ailmouski.myapplication.ZeteticApplication;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.File;

public class MUTF8ToUTF8WithNullMigrationTest extends SQLCipherTest {

    private int keyCount = 0;
    private String filename = "newdb2.db";

    private char[] password = new char[]{'請', '뽰', '䒐', '\uDA32', '\uDB0E', '듂', '㯲', 'E',
            '潫', '\uE8CF', '⭐', '\uF6D0', '㖨', '湯', 'ẓ', '⑆', '于', '꽺', '榙', '\uF069', '꺐',
        '뎣', '눌', '纟', '䣰', '譺', '\u1CA7', '\u1ADE', '籞', '迋', '檻', '색'};

    SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
        public void preKey(SQLiteDatabase sqLiteDatabase) {
        }

        public void postKey(SQLiteDatabase sqLiteDatabase) {

        }
    };

    @Override
    public boolean execute(SQLiteDatabase database) {
        boolean status = false;
        database.close();
        try {
            File databaseFile = ZeteticApplication.getInstance().getDatabasePath(filename);
            if (databaseFile.exists()) {
                databaseFile.delete();
            }
            ZeteticApplication.getInstance().extractAssetToDatabaseDirectory(filename);
            database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), password, null,
                    SQLiteDatabase.OPEN_READWRITE, hook);
            if (keyCount == 1) {
               return true;
            }
            database.close();
            keyCount = 0;
            database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), password, null,
                    SQLiteDatabase.OPEN_READWRITE, hook);
            status = keyCount == 1;
        } catch (Exception e) {
            Log.e("error", String.format("Error during modified UTF-8 to UTF-8 migration:%s", e.toString()));
        }
        return status;
    }

    @Override
    public String getName() {
        return "Migrate from modified UTF-8 to UTF-8 with null test";
    }
}
