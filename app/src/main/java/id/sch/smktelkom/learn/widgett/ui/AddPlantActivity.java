package id.sch.smktelkom.learn.widgett.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import id.sch.smktelkom.learn.widgett.R;
import id.sch.smktelkom.learn.widgett.provider.PlantContact;

/**
 * Created by rongrong on 27/12/2017.
 */

public class AddPlantActivity extends AppCompatActivity {
    private RecyclerView mTypesRecyclerView;
    private PlantTypesAdapter mTypesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        mTypesAdapter = new PlantTypesAdapter(this);
        mTypesRecyclerView = findViewById(R.id.plant_types_recycler_view);
        mTypesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        mTypesRecyclerView.setAdapter(mTypesAdapter);

    }

    public void onPlantTypeClick(View view) {
        ImageView imgView = view.findViewById(R.id.plant_type_image);
        int plantType = (int) imgView.getTag();
        long timeNow = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantContact.PlantEntry.COLUMN_PLANT_TYPE, plantType);
        contentValues.put(PlantContact.PlantEntry.COLUMN_CREATION_TIME, timeNow);
        contentValues.put(PlantContact.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().insert(PlantContact.PlantEntry.CONTENT_URI, contentValues);
        finish();
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}
