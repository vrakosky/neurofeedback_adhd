package utp.esirem.vincent.realtimegraph;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import utp.esirem.vincent.realtimegraph.Reading.Reading;

public class BrainGraph extends AppCompatActivity {
    private final Handler mHandler = new Handler();
    int dataCount = 1;
    private double data1 = 0;
    private double data2 = 0;
    private double data3 = 0;
    private Runnable mTimer1;
    private Runnable mTimer2;
    private Runnable mTimer3;
    private GraphView graphView1;
    private GraphView graphView2;
    private GraphView graphView3;
    private GraphViewSeries exampleSeries1;
    private GraphViewSeries exampleSeries2;
    private GraphViewSeries exampleSeries3;
    private List<GraphView.GraphViewData> series1;
    private List<GraphView.GraphViewData> series2;
    private List<GraphView.GraphViewData> series3;
    private double previousData, actualData;

    //BRIGHTNESS
    int progress = 130;
    public int brightnessPublic;

    //SWITCH
    private Boolean switchState = false;

    //PUSHER
    private Pusher pusher;
    private static final String PUSHER_APP_KEY = "6e710bfa15232092ed2d";
    private static final String PUSHER_APP_CLUSTER = "mt1";
    private static final String CHANNEL_NAME = "stats";
    private static final String EVENT_NAME = "new_memory_stat";

    //Shared preferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brain_graph);

        series1 = new ArrayList<GraphView.GraphViewData>();
        series2 = new ArrayList<GraphView.GraphViewData>();
        series3 = new ArrayList<GraphView.GraphViewData>();

        // ----------Curves parameters-------------

        exampleSeries1 = new GraphViewSeries("Hbt_left", new GraphViewSeries.GraphViewSeriesStyle(Color.RED, 4), new GraphView.GraphViewData[]{});
        exampleSeries2 = new GraphViewSeries("Hbt_right", new GraphViewSeries.GraphViewSeriesStyle(Color.GREEN, 4), new GraphView.GraphViewData[]{});
        exampleSeries3 = new GraphViewSeries(new GraphView.GraphViewData[]{});

        // ----------GRAPH 1-------------

        if (getIntent().getStringExtra("type").equals("bar")) {
            graphView1 = new BarGraphView(this, "Brain index in realtime (actual signal)");
        } else {
            graphView1 = new LineGraphView(this, "Brain index in realtime (actual signal)");
        }
        graphView1.addSeries(exampleSeries1); // data
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView1);

        // ----------GRAPH 2-------------
        //exampleSeries2 = new GraphViewSeries(new GraphView.GraphViewData[] {});
        if (getIntent().getStringExtra("type").equals("bar")) {
            graphView2 = new BarGraphView(this, "Brain index extended (whole signal)");
        } else {
            graphView2 = new LineGraphView(this, "Brain index extended (whole signal)");
            //((LineGraphView) graphView2).setDrawBackground(true);
            //((LineGraphView) graphView2).setBackgroundColor(Color.YELLOW);
        }
        graphView2.addSeries(exampleSeries1); // data
        layout = (LinearLayout) findViewById(R.id.graph2);
        layout.addView(graphView2);

        // ----------GRAPH 3-------------
        if (getIntent().getStringExtra("type").equals("bar")) {
            graphView3 = new BarGraphView(this, "Heart Rate");
        } else {
            graphView3 = new LineGraphView(this, "Heart Rate");
        }
        graphView3.addSeries(exampleSeries3); // data
        layout = (LinearLayout) findViewById(R.id.graph3);
        layout.addView(graphView3);

        //-------------------Pusher---------------------

        PusherOptions options = new PusherOptions();
        options.setCluster(PUSHER_APP_CLUSTER);
        pusher = new Pusher(PUSHER_APP_KEY, options);
        Channel channel = pusher.subscribe(CHANNEL_NAME);

        SubscriptionEventListener eventListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, final String event, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Received event with data: " + data);
                        Gson gson = new Gson();
                        Stat stat = gson.fromJson(data, Stat.class);
                        addEntry(stat);
                    }
                });
            }
        };
        channel.bind(EVENT_NAME, eventListener);
        pusher.connect();

        //Shared preferences for switch-state (brightness)
        mPreferences = getSharedPreferences("switch_state", Context.MODE_PRIVATE);
        String switch_state = mPreferences.getString("switch_state", "no data");
        mEditor = mPreferences.edit();


        //Switch
        Switch switch1 = (Switch) findViewById(R.id.switch1);
        if(switch_state.equals("true")){
            switch1.setChecked(true);
        }else{
            switch1.setChecked(false);
        }
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Adaptive-brightness-adjustment active", Toast.LENGTH_SHORT).show();
                    mEditor.putString("switch_state", "true");   //Save the switch state into preferences
                    switchState = true;
                    mEditor.apply();
                } else {
                    Toast.makeText(getApplicationContext(), "Adaptive-brightness-adjustment inactive", Toast.LENGTH_SHORT).show();
                    mEditor.putString("switch_state", "false");   //Save the switch state into preferences
                    switchState = false;
                    mEditor.apply();
                }
            }
        });

        //BUTTON (back to menu)
        ((Button) findViewById(R.id.btn_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BrainGraph.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        mHandler.removeCallbacks(mTimer2);
        mHandler.removeCallbacks(mTimer3);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[series1.size()];
                series1.toArray(gvd);
                exampleSeries1.resetData(gvd);
                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer1, 100);

        mTimer2 = new Runnable() {
            @Override
            public void run() {
                GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[series2.size()];
                series2.toArray(gvd);
                exampleSeries2.resetData(gvd);
                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer2, 100);

        mTimer3 = new Runnable() {
            @Override
            public void run() {
                GraphView.GraphViewData[] gvd = new GraphView.GraphViewData[series3.size()];
                series3.toArray(gvd);
                exampleSeries3.resetData(gvd);
                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer3, 100);
    }

    private void addEntry(Stat stat) {
        data1 = stat.getData1();
        data2 = stat.getData2();
        data3 = stat.getData3();

        series1.add(new GraphView.GraphViewData(dataCount, data1));
        series2.add(new GraphView.GraphViewData(dataCount, data2));
        series3.add(new GraphView.GraphViewData(dataCount, data3));

        //Ajout de courbes à chaque graphview
        graphView1.addSeries(exampleSeries2);
        graphView2.addSeries(exampleSeries2);

        dataCount++;

        //To update graph every 100 data (begin to first)
        if (series1.size() > 100) {
            //series1.remove(0);
            graphView1.setViewPort(dataCount - 100, 100);
        }

        if (series3.size() > 100) {
            series3.remove(0);
            graphView3.setViewPort(dataCount - 100, 100);
        }

        getDifference();

        //-------------------DEBUGGING------------------------
        Log.d(String.valueOf(stat.getData1()), "data1");
        Log.d(String.valueOf(stat.getData2()), "data2");
        Log.d(String.valueOf(stat.getData3()), "data3");

        Log.d(String.valueOf(previousData), "previousData");
        Log.d(String.valueOf(actualData), "actualData");
        //----------------------------------------------------
    }

    private void autoBrightness() {
        if ((actualData - previousData) > 0) {
            progress += 15;
            setBrightness(progress);
        } else {
            progress -= 10;
            setBrightness(progress);
        }
        //-------------------DEBUGGING------------------------
        Log.d(String.valueOf(actualData - previousData), "actualData - previousData");
        Log.d(String.valueOf(progress), "Brightness");
        //----------------------------------------------------
    }

    private void autoBrightness2() {
        if ((actualData - previousData) > 0) {
            progress += 2;
            setBrightness2(progress);
        } else {
            progress -= 2;
            setBrightness2(progress);
        }
        //-------------------DEBUGGING------------------------
        Log.d(String.valueOf(actualData - previousData), "actualData - previousData");
        Log.d(String.valueOf(progress), "Brightness");
        //----------------------------------------------------
    }

    //Brightness control 1
    private void setBrightness(int brightness) {
        if (brightness < 0) {
            brightness = 0;
            progress = 0;
        } else if (brightness > 255) {
            brightness = 255;
            progress = 255;
        }
        brightnessPublic = brightness;

        //wait 5 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //put only the two line outside to don't wait 5 seconds
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessPublic);
            }
        },5000);
    }

    //Brightness control 2
    private void setBrightness2(int brightness) {
        if ((brightness > 0 && brightness < 60)) {
            brightness += 5;
            progress += 5;
        } else if (brightness > 210 && brightness < 255) {
            brightness -= 5;
            progress -= 5;
        }
        brightnessPublic = brightness;

        //wait 5 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //put only the two line outside to don't wait 5 seconds
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessPublic);
            }
        },5000);
    }

    private int getBrightness() {
        int brightness = 100;
        try {
            ContentResolver contentResolver = getApplicationContext().getContentResolver();
            brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    private void getDifference() {
        //data 1 : before & after
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                previousData = data1;
            }
        },3000);
        actualData = data1;
        if (switchState) {
            autoBrightness2();
        }
    }
}

