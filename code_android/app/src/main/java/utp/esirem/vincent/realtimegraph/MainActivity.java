package utp.esirem.vincent.realtimegraph;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pusher.client.Pusher;

import java.util.Calendar;
import java.util.Random;

import utp.esirem.vincent.realtimegraph.BroadcastReceiver.AlarmReceiver;

import static utp.esirem.vincent.realtimegraph.App.CHANNEL_1_ID;
import static utp.esirem.vincent.realtimegraph.App.CHANNEL_2_ID;

public class MainActivity extends Activity {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;

    boolean success = false;
    Boolean EXTRA_ACTIVATION  = false;

    //Shared preferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    Switch switch1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerAlarm();
        seekFunction();
        //BUTTON 1
        ((Button) findViewById(R.id.btn_realtime)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGraphActivity(RealtimeGraph.class);
            }
        });
        //BUTTON 2
        ((Button) findViewById(R.id.btn_realtime2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGraphActivity(BrainGraph.class);
            }
        });
        //BUTTON 3
        ((Button) findViewById(R.id.btn_help)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGraphActivity(SliderHelp.class);
            }
        });
        //BUTTON 4
        ((Button) findViewById(R.id.btn_quiz)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGraphActivity(SignIn.class);
            }
        });
        //BUTTON 5
        ((Button) findViewById(R.id.btn_credits)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGraphActivity(Credit.class);
            }
        });

        //Notification
        notificationManager = NotificationManagerCompat.from(this);
        //editTextTitle = findViewById(R.id.edit_text_title);
        //editTextMessage = findViewById(R.id.edit_text_message);

        //Switch
        switch1 = (Switch) findViewById(R.id.switchMainActivate);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Adaptive-brightness-adjustment active", Toast.LENGTH_SHORT).show();
                    EXTRA_ACTIVATION = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Adaptive-brightness-adjustment inactive", Toast.LENGTH_SHORT).show();
                    EXTRA_ACTIVATION = false;
                }
            }
        });

        //SHARED PREFERENCE FOR SWITCH STATE
        mPreferences = getSharedPreferences("switch_state", Context.MODE_PRIVATE);
        checkSharedPreferences();
    }

    private void startGraphActivity(Class<? extends Activity> activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        if (((RadioButton) findViewById(R.id.radio_bar)).isChecked()) {
            intent.putExtra("type", "bar");
        } else {
            intent.putExtra("type", "line");
        }
        startActivity(intent);
    }

    public void seekFunction() {

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(255);
        seekBar.setProgress(getBrightness());
        final TextView txtPerc = (TextView) findViewById(R.id.progress);

        //Ask permission if first time using the app
        getPermission();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && success) {
                    setBrightness(progress);
                    float perc = ((float) (getBrightness()) / 255) * 100;
                    txtPerc.setText((int) perc + "%");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!success) {
                    Toast.makeText(MainActivity.this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setBrightness(int brightness) {
        if (brightness < 0) {
            brightness = 0;
        } else if (brightness > 255) {
            brightness = 255;
        }
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
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

    private void getPermission() {
        boolean value;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            value = Settings.System.canWrite(getApplicationContext());
            if (value) {
                success = true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, 1000);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean value = Settings.System.canWrite(getApplicationContext());
                if (value) {
                    success = true;
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void sendOnChannel1(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_graphic)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_graphic)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }

    //Alarm reminder to use the App
    private void registerAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,14);
        calendar.set(Calendar.MINUTE,21);
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void checkSharedPreferences(){
        String switch_state = mPreferences.getString("switch_state", "no data");
        if(switch_state.equals("true")){
            switch1.setChecked(true);
        }else{
            switch1.setChecked(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


