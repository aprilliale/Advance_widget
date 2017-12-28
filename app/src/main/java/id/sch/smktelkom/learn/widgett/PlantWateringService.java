package id.sch.smktelkom.learn.widgett;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

    public PlantWateringService() {
        super("PlantWateringService");
    }

    public static void startActionWaterPlants(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANTS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANTS.equals(action)) {
                handleActionWaterPlants();
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
}
