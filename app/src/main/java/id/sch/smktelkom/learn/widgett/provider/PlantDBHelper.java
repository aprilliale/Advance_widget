package id.sch.smktelkom.learn.widgett.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.sch.smktelkom.learn.widgett.provider.PlantContract.PlantEntry;

/**
 * Created by rongrong on 27/12/2017.
 */

public class PlantDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shushme.db";
    private static final int DATABASE_VERSION = 1;

    public PlantDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_PLANTS_TABLE = "CREATE TABLE " + PlantEntry.TABLE_NAME + " (" +
                PlantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlantEntry.COLUMN_PLANT_TYPE + " INTEGER NOT NULL, " +
                PlantEntry.COLUMN_CREATION_TIME + " TIMESTAMP NOT NULL, " +
                PlantEntry.COLUMN_LAST_WATERED_TIME + " TIMESTAMP NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_PLANTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlantEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

