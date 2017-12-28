package id.sch.smktelkom.learn.widgett;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import id.sch.smktelkom.learn.widgett.provider.PlantContact;
import id.sch.smktelkom.learn.widgett.utils.PlantUtils;

import static id.sch.smktelkom.learn.widgett.provider.PlantContact.BASE_CONTENT_URI;
import static id.sch.smktelkom.learn.widgett.provider.PlantContact.PATH_PLANTS;

/**
 * Created by rongrong on 28/12/2017.
 */

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER_PLANTS = "id.sch.smktelkom.learn.action.water_plants";
    public static final String ACTION_UPDATE_PLANT_WIDGETS = "id.sch.smktelkom.learn.action.update_plant_widgets";

    public PlantWateringService() {
        super("PlantWateringService");
    }

    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
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
            if (ACTION_WATER_PLANTS.equals(action)) {
                handleActionWaterPlants();
            } else if (ACTION_UPDATE_PLANT_WIDGETS.equals(action)) {
                handleActionUpdatePlantWidgets();
            }
        }
    }

    private void handleActionWaterPlants() {
        Uri PLANTS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        contentValues.put(PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        // Update only plants that are still alive
        getContentResolver().update(
                PLANTS_URI,
                contentValues,
                PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME + ">?",
                new String[]{String.valueOf(timeNow - PlantUtils.MAX_AGE_WITHOUT_WATER)});
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
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int createTimeIndex = cursor.getColumnIndex(PlantContact.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = cursor.getColumnIndex(PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContact.PlantEntry.COLUMN_PLANT_TYPE);
            long timeNow = System.currentTimeMillis();
            long wateredAt = cursor.getLong(waterTimeIndex);
            long createdAt = cursor.getLong(createTimeIndex);
            int plantType = cursor.getInt(plantTypeIndex);
            cursor.close();
            imgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, timeNow - wateredAt, plantType);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PlantWidgetProvider.class));
        PlantWidgetProvider.updatePlantWidgets(this, appWidgetManager, imgRes, appWidgetIds);
    }
}
