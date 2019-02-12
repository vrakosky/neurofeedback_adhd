package utp.esirem.vincent.realtimegraph.Reading;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.biubiubiu.justifytext.library.JustifyTextView;
import utp.esirem.vincent.realtimegraph.Common.Common;
import utp.esirem.vincent.realtimegraph.Model.Category;
import utp.esirem.vincent.realtimegraph.Playing;
import utp.esirem.vincent.realtimegraph.R;

import static java.lang.String.valueOf;

public class Reading extends AppCompatActivity {


    //Pulling data from Firebase
    FirebaseDatabase database;
    DatabaseReference readingTextDatabase, imageDatabase;

    TextView titleTask;
    long elapsedMillis;
    ImageView imageCategory;
    JustifyTextView readingTask;
    private Chronometer mChronometer;

    //Shared preferences
    String elapsedMillisString;
    public static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        titleTask = (TextView) findViewById(R.id.txt_subject);
        imageCategory = (ImageView) findViewById(R.id.image_category);
        readingTask = (JustifyTextView) findViewById(R.id.txt_reading);


        database = FirebaseDatabase.getInstance();
        //Chronometer
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.start();

        loadReadingText();
        loadCategoryiesImage();

        //Shared preferences for switch-state (brightness)
        mPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        //Time T1 : begin to read
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String start_read_time = date.format(cal.getTime());
        //Toast.makeText(Reading.this, start_read_time, Toast.LENGTH_SHORT).show();
        mEditor.putString("start_read_time", start_read_time);
        mEditor.apply();
    }

    private void loadReadingText() {
        readingTextDatabase = database.getReference().child("Category").child(String.format("%s", Common.categoryId));
        readingTextDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Category value = dataSnapshot.getValue(Category.class);
                titleTask.setText(String.format("Text %s : " + valueOf(value.getName()), Common.categoryId));
                readingTask.setText(valueOf(value.getReadingTask()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        private void loadCategoryiesImage() {
        imageDatabase = database.getReference().child("Category").child(String.format("%s", Common.categoryId));
        imageDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Category value = dataSnapshot.getValue(Category.class);
                Picasso.with(getBaseContext())
                        .load(value.getImage())
                        .into(imageCategory);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        //Time between T1 & T2
        mChronometer.stop();
        elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
        elapsedMillisString = String.valueOf(elapsedMillis/1000);
        mEditor.putString("reading_time", elapsedMillisString);   //Save the switch state into preferences
        mEditor.apply();

        //Time T2 : end reading
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String end_read_time = date.format(cal.getTime());
        //Toast.makeText(Reading.this, end_read_time, Toast.LENGTH_SHORT).show();
        mEditor.putString("end_read_time", end_read_time);
        mEditor.apply();
        super.onPause();
    }
}
