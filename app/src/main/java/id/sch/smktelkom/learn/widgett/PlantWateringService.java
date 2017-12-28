package id.sch.smktelkom.learn.widgett;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import id.sch.smktelkom.learn.widgett.provider.PlantContact;
import id.sch.smktelkom.learn.widgett.utils.PlantUtils;

import static id.sch.smktelkom.learn.widgett.provider.PlantContact.BASE_CONTENT_URI;
import static id.sch.smktelkom.learn.widgett.provider.PlantContact.INVALID_PLANT_ID;
import static id.sch.smktelkom.learn.widgett.provider.PlantContact.PATH_PLANTS;

/**
 * Created by rongrong on 28/12/2017.
 */

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER_PLANT = "id.sch.smktelkom.learn.action.water_plants";
    public static final String ACTION_UPDATE_PLANT_WIDGETS = "id.sch.smktelkom.learn.action.update_plant_widgets";
    public static final String EXTRA_PLANT_ID = "id.sch.smktelkom.learn.extra.PLANT_ID";

    public PlantWateringService() {
        super("PlantWateringService");
    }

    public static void startActionWaterPlant(Context context, long plantId) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANT);
        intent.putExtra(EXTRA_PLANT_ID, plantId);
        context.startService(intent);
    }

    public static void startActionUpdatePlantWidgets(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_UPDATE_PLANT_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANT.equals(action)) {
                final long plantId = intent.getLongExtra(EXTRA_PLANT_ID,
                        PlantContact.INVALID_PLANT_ID);
                handleActionWaterPlant(plantId);
            } else if (ACTION_UPDATE_PLANT_WIDGETS.equals(action)) {
                handleActionUpdatePlantWidgets();
            }
        }
    }

    private void handleActionWaterPlant(long plantId) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), plantId);
        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        contentValues.put(PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(
                SINGLE_PLANT_URI,
                contentValues,
                PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME + ">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});
        startActionUpdatePlantWidgets(this);
    }

    private void handleActionUpdatePlantWidgets() {
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME
        );
        int imgRes = R.drawable.grass;
        boolean canWater = false;
        long plantId = INVALID_PLANT_ID;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(PlantContact.PlantEntry._ID);
            int createTimeIndex = cursor.getColumnIndex(PlantContact.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = cursor.getColumnIndex(PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContact.PlantEntry.COLUMN_PLANT_TYPE);
            plantId = cursor.getLong(idIndex);
            long timeNow = System.currentTimeMillis();
            long wateredAt = cursor.getLong(waterTimeIndex);
            long createdAt = cursor.getLong(createTimeIndex);
            int plantType = cursor.getInt(plantTypeIndex);
            cursor.close();
            canWater = (timeNow - wateredAt) > PlantUtils.MIN_AGE_BETWEEN_WATER &&
                    (timeNow - wateredAt) < PlantUtils.MAX_AGE_WITHOUT_WATER;
            imgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, timeNow - wateredAt, plantType);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PlantWidgetProvider.class));
        PlantWidgetProvider.updatePlantWidgets(this, appWidgetManager, imgRes, plantId, canWater, appWidgetIds);
    }
}