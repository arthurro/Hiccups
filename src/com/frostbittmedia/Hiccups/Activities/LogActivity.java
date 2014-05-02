package com.frostbittmedia.Hiccups.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.frostbittmedia.Hiccups.Fragments.DetailFragment;
import com.frostbittmedia.Hiccups.Fragments.NoteFragment;
import com.frostbittmedia.Hiccups.Objects.LogEvent;
import com.frostbittmedia.Hiccups.R;
import com.frostbittmedia.Hiccups.SQL.DBClient;
import com.frostbittmedia.Hiccups.Utils.LogEventListAdapter;
import com.frostbittmedia.Hiccups.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class LogActivity extends ListActivity implements SensorEventListener{

    // ===========================================================
    // Fields
    // ===========================================================
    private static final String TAG = "LogActivity";

    private Button feedingBtn;
    private Button sleepingBtn;
    private Button diaperChangeBtn;
    private Button noteBtn;
    private ListView listView;
    private FragmentTransaction transaction;
    private Sensor accelerometer;
    private SensorManager sensorManager;
    private long shakeTimestamp;

    public static DBClient dbClient;
    public static BaseAdapter listAdapter;
    public static List<LogEvent> logEventList;
    public static LinearLayout fragmentContainer;
    public static LinearLayout listContainer;
    public static LinearLayout buttonPanel;
    public static FragmentManager fragmentManager;
    public static LogEvent listViewItemClicked;
    public static String fragment; // Back stack fragment tag
    public static int listViewItemClickedPosition;

    // ===========================================================
    // Methods from superclass/interface
    // ===========================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logactivity_layout);

        fragmentContainer = (LinearLayout)findViewById(R.id.fragmentContainer);
        listContainer = (LinearLayout)findViewById(R.id.listContainer);
        buttonPanel = (LinearLayout)findViewById(R.id.buttonPanel);
        dbClient = new DBClient(this);
        fragmentManager = getFragmentManager();

        // Fetching log events from db
        logEventList = new ArrayList<LogEvent>(dbClient.getLogEvents());
        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                listViewItemClicked = (LogEvent)listView.getItemAtPosition(position);
                listViewItemClickedPosition = position;
                displayDetailFragment();
            }
        });
        attachAdapter(); // Attaching list adapter to listView so data can populate from db
        assignButtons(); // Assigning buttons and OnClickListeners
        startSensors();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Disconnecting SensorEventListener");
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Connecting SensorEventListener");
        startSensors();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try{
            fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(1, 0);
            fragmentContainer.setVisibility(View.GONE);
            listContainer.setVisibility(View.VISIBLE);
            buttonPanel.setVisibility(View.VISIBLE);
        }catch (Exception e){
            Log.w(TAG, "Fragment switching generated an exception as expected");
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear_log:
                Log.i(TAG, "Clearing log entries");
                clearLog();
                return true;
        }
        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float SHAKE_THRESHOLD_GRAVITY = 1.2f; // "Shake sensitivity" - Nexus 5 = 2.3f
        final int SHAKE_SLOP_TIME_MS = 500;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        // gForce will be close to 1 when there is no movement.
        float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }

            shakeTimestamp = now;
            clearLog();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void assignButtons() {
        feedingBtn = (Button)findViewById(R.id.feedingBtn);
        feedingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbClient.addLogEvent(LogEvent.FEEDING_EVENT, "", Utils.getDateTimeString());
                logEventList.add(0, dbClient.getLastEvent());
                listAdapter.notifyDataSetChanged();
            }
        });

        sleepingBtn = (Button)findViewById(R.id.sleepBtn);
        sleepingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbClient.getLastSleepEvent().equals(LogEvent.SLEEPING_EVENT)){
                    dbClient.addLogEvent(LogEvent.WOKE_UP_EVENT, "", Utils.getDateTimeString());
                    sleepingBtn.setBackgroundResource(R.drawable.sleeping_button);
                }
                else{
                    dbClient.addLogEvent(LogEvent.SLEEPING_EVENT, "", Utils.getDateTimeString());
                    sleepingBtn.setBackgroundResource(R.drawable.wokeup_button);
                }

                logEventList.add(0, dbClient.getLastEvent());
                listAdapter.notifyDataSetChanged();
            }
        });

        diaperChangeBtn = (Button)findViewById(R.id.diaperBtn);
        diaperChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbClient.addLogEvent(LogEvent.DIAPER_CHANGE_EVENT, "", Utils.getDateTimeString());
                logEventList.add(0, dbClient.getLastEvent());
                listAdapter.notifyDataSetChanged();
            }
        });

        noteBtn = (Button)findViewById(R.id.noteBtn);
        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listContainer.setVisibility(View.GONE);
                buttonPanel.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);

                fragment = "note";
                transaction = fragmentManager.beginTransaction();
                NoteFragment noteFragment = new NoteFragment();
                transaction.add(R.id.fragmentContainer, noteFragment);
                transaction.addToBackStack(fragment);
                transaction.commit();
            }
        });
    }

    private void attachAdapter() {
        listAdapter = new LogEventListAdapter(logEventList, this);
        listView.setAdapter(listAdapter);
    }

    public static void saveNote(String note) {
        dbClient.addLogEvent(LogEvent.NOTE_EVENT, note, Utils.getDateTimeString());
        logEventList.add(0, dbClient.getLastEvent());
        listAdapter.notifyDataSetChanged();
    }

    private void displayDetailFragment() {
        listContainer.setVisibility(View.GONE);
        buttonPanel.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        fragment = "detail";
        transaction = fragmentManager.beginTransaction();
        DetailFragment detailFragment = new DetailFragment();
        transaction.add(R.id.fragmentContainer, detailFragment);
        transaction.addToBackStack(fragment);
        transaction.commit();
    }

    private void startSensors(){
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void clearLog(){
        logEventList.clear();
        listAdapter.notifyDataSetChanged();
        dbClient.deleteTableRows();
    }
}
