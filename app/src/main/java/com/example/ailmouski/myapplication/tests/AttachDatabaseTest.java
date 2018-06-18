package com.example.ailmouski.myapplication.tests;

import com.example.ailmouski.myapplication.ZeteticApplication;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

public class AttachDatabaseTest extends SQLCipherTest {
    @Override
    public boolean execute(SQLiteDatabase database) {
        char[] password = new char[]{'請', '뽰', '䒐', '\uDA32', '\uDB0E', '듂', '㯲', 'E',
                '潫', '\uE8CF', '⭐', '\uF6D0', '㖨', '湯', 'ẓ', '⑆', '于', '꽺', '榙', '\uF069', '꺐',
                '뎣', '눌', '纟', '䣰', '譺', '\u1CA7', '\u1ADE', '籞', '迋', '檻', '색'};
        String dbName = "newdb1.db";
        File dbFile = ZeteticApplication.getInstance().getDatabasePath(dbName);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        try {
            SQLiteOpenHelper helper = new SQLiteOpenHelper(ZeteticApplication.getInstance(), dbName, null, 1) {
                @Override
                public void onCreate(SQLiteDatabase sqLiteDatabase) {

                }

                @Override
                public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

                }
            };
            SQLiteDatabase db = helper.getWritableDatabase(password);
            db.close();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return "Attach database test";
    }
}
