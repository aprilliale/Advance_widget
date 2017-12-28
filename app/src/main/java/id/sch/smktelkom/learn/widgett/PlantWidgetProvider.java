package id.sch.smktelkom.learn.widgett;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import id.sch.smktelkom.learn.widgett.provider.PlantContact;
import id.sch.smktelkom.learn.widgett.ui.MainActivity;
import id.sch.smktelkom.learn.widgett.ui.PlantDetailActivity;

/**
 * Created by rongrong on 27/12/2017.
 */

public class PlantWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int imgRes, long plantId, boolean showWater, int appWidgetId) {

        Intent intent;
        if (plantId == PlantContact.INVALID_PLANT_ID) {
            intent = new Intent(context, MainActivity.class);
        } else { // Set on click to open the corresponding detail activity
            Log.d(PlantWidgetProvider.class.getSimpleName(), "plantId=" + plantId);
            intent = new Intent(context, PlantDetailActivity.class);
            intent.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, plantId);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        views.setImageViewResource(R.id.widget_plant_image, imgRes);
        views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
        if (showWater) views.setViewVisibility(R.id.widget_water_button, View.VISIBLE);
        else views.setViewVisibility(R.id.widget_water_button, View.INVISIBLE);
        views.setOnClickPendingIntent(R.id.widget_plant_image, pendingIntent);
        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        wateringIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int imgRes, long plantId, boolean showWater, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, imgRes, plantId, showWater, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PlantWateringService.startActionUpdatePlantWidgets(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

}
