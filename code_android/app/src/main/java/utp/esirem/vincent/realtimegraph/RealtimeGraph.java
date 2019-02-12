package utp.esirem.vincent.realtimegraph;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.pusher.client.Pusher;

import java.util.ArrayList;
import java.util.List;

public class RealtimeGraph extends Activity implements SensorEventListener {
    private final Handler mHandler = new Handler();
    int dataCount = 1;
    private Runnable mTimer1;
    private Runnable mTimer2;
    private Runnable mTimer3;
    private Runnable mTimer4;
    private GraphView graphView1;
    private GraphView graphView2;
    private GraphView graphView3;
    private GraphView graphView4;
    private GraphViewSeries exampleSeries1;
    private GraphViewSeries exampleSeries2;
    private GraphViewSeries exampleSeries3;
    private double sensorX = 0;
    private double sensorY = 0;
    private double sensorZ = 0;
    private List<GraphViewData> seriesX;
    private List<GraphViewData> seriesY;
    private List<GraphViewData> seriesZ;
    //the Sensor Manager
    private SensorManager sManager;

    //Pusher variable
    private Pusher pusher;

    private static final String PUSHER_APP_KEY = "0be1557f329eb466398f";
    private static final String PUSHER_APP_CLUSTER = "mt1";
    private static final String CHANNEL_NAME = "stats";
    private static final String EVENT_NAME = "new_memory_stat";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_graph);

        seriesX = new ArrayList<GraphViewData>();
        seriesY = new ArrayList<GraphViewData>();
        seriesZ = new ArrayList<GraphViewData>();

        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // ----------GRAPH 1-------------
        exampleSeries1 = new GraphViewSeries(new GraphViewData[]{});
        if (getIntent().getStringExtra("type").equals("bar")) {
            graphView1 = new BarGraphView(this, "Gyroscope - SensorX");
        } else {
            graphView1 = new LineGraphView(this, "Gyroscope - SensorX");
        }
        graphView1.addSeries(exampleSeries1); // data
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView1);

        // ----------GRAPH 2-------------
        exampleSeries2 = new GraphViewSeries(new GraphViewData[]{});
        if (getIntent().getStringExtra("type").equals("bar")) {
            graphView2 = new BarGraphView(this, "Gyroscope - SensorY");
        } else {
            graphView2 = new LineGraphView(this, "Gyroscope - SensorY");
            ((LineGraphView) graphView2).setDrawBackground(true);
        }
        graphView2.addSeries(exampleSeries2); // data
        layout = (LinearLayout) findViewById(R.id.graph2);
        layout.addView(graphView2);


        // ----------GRAPH 3-------------
        exampleSeries3 = new GraphViewSeries(new GraphViewData[]{});
        if (getIntent().getStringExtra("type").equals("bar")) {
            graphView3 = new BarGraphView(this, "Gyroscope - SensorZ");
        } else {
            graphView3 = new LineGraphView(this, "Gyroscope - SensorZ");
        }
        graphView3.addSeries(exampleSeries3); // data
        layout = (LinearLayout) findViewById(R.id.graph3);
        layout.addView(graphView3);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return;
        }
        sensorX = event.values[2];
        sensorY = event.values[1];
        sensorZ = event.values[0];

        seriesX.add(new GraphViewData(dataCount, sensorX));
        seriesY.add(new GraphViewData(dataCount, sensorY));
        seriesZ.add(new GraphViewData(dataCount, sensorZ));

        dataCount++;

        if (seriesX.size() > 500) {
            seriesX.remove(0);
            seriesY.remove(0);
            seriesZ.remove(0);
            graphView1.setViewPort(dataCount - 500, 500);
            graphView2.setViewPort(dataCount - 500, 500);
            graphView3.setViewPort(dataCount - 500, 500);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mTimer1);
        mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                GraphViewData[] gvd = new GraphViewData[seriesX.size()];
                seriesX.toArray(gvd);
                exampleSeries1.resetData(gvd);
                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer1, 100);

        mTimer2 = new Runnable() {
            @Override
            public void run() {

                GraphViewData[] gvd = new GraphViewData[seriesY.size()];
                seriesY.toArray(gvd);
                exampleSeries2.resetData(gvd);

                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer2, 100);

        mTimer3 = new Runnable() {
            @Override
            public void run() {

                GraphViewData[] gvd = new GraphViewData[seriesZ.size()];
                seriesZ.toArray(gvd);
                exampleSeries3.resetData(gvd);

                mHandler.post(this);
            }
        };
        mHandler.postDelayed(mTimer3, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
