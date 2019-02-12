package utp.esirem.vincent.realtimegraph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import utp.esirem.vincent.realtimegraph.Common.Common;
import utp.esirem.vincent.realtimegraph.Model.Question;
import utp.esirem.vincent.realtimegraph.Model.QuestionScore;
import utp.esirem.vincent.realtimegraph.Reading.Reading;

public class Playing extends AppCompatActivity implements View.OnClickListener {
    final static long INTERVAL = 1000; //1 sec
    final static long TIMEOUT = 120000; //2 sec
    int progressValue = 0;
    long elapsedMillis;
    private Chronometer mChronometer;
    private int previousScore=0;
    private int previousScorePlus=0;
    private boolean brightnessActivation = false;
    CountDownTimer mCountDown;

    //Pulling data from Firebaser
    FirebaseDatabase database;
    DatabaseReference questionScore;

    //Shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";
    String reading_time, end_play_time, start_play_time;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    int index = 0, score = 0, thisQuestion = 0, totalQuestion, correctAnswer;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestionNum, question_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //1 - Access Database and table (question_score) to pull SCORE
        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference().child("Question_Score").child(String.format("%s_%s", Common.currentUser.getUserName(), Common.categoryId));
        questionScore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    QuestionScore value = dataSnapshot.getValue(QuestionScore.class);
                    Log.d("SCORE", String.valueOf(value.getScore()));
                    previousScore = Integer.parseInt(value.getScore());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //2 - Access Database and table (question_score) to pull SCOREPLUS
        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference().child("Question_Score").child(String.format("%s_%s", Common.currentUser.getUserName(), Common.categoryId));
        questionScore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    QuestionScore value = dataSnapshot.getValue(QuestionScore.class);
                Log.d("SCOREPLUS", String.valueOf(value.getScorePlus()));
                previousScorePlus = Integer.parseInt(value.getScorePlus());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Know if fixed or with auto-brightness
        brightnessActivation = getIntent().getExtras().getBoolean("EXTRA_ACTIVATION");
        Log.d("AUTO", String.valueOf(brightnessActivation));

        //Chronometer
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.start();

        //View
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtQuestionNum = (TextView) findViewById(R.id.txtTotalQuestion);
        question_text = (TextView) findViewById(R.id.question_text);
        question_image = (ImageView) findViewById(R.id.question_image);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnA = (Button) findViewById(R.id.btnAnswerA);
        btnB = (Button) findViewById(R.id.btnAnswerB);
        btnC = (Button) findViewById(R.id.btnAnswerC);
        btnD = (Button) findViewById(R.id.btnAnswerD);

        //SHARED PREFERENCE INSTANCIATION
        mPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        //SHARED PREFERENCE GET
        reading_time = mPreferences.getString("reading_time", "00:00");
        mEditor = mPreferences.edit();

        //Time T1 : begin to play
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        start_play_time = date.format(cal.getTime());
        //Toast.makeText(Playing.this, start_play_time, Toast.LENGTH_SHORT).show();
        mEditor.putString("start_play_time", start_play_time);
        mEditor.apply();
    }


    @Override
    public void onClick(View view) {
        mCountDown.cancel();
        if (index < totalQuestion) // still have question in list
        {
            Button clickedButton = (Button) view;
            if (clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer())) {
                //Choose correct answer
                score += 10;
                correctAnswer++;
                showQuestion(++index);
                Toast.makeText(Playing.this, "Correct", Toast.LENGTH_SHORT).show();

            } else {
                //Choose wrong answer
                showQuestion(++index);
                Toast.makeText(Playing.this, "Wrong answer. Focus.", Toast.LENGTH_SHORT).show();
            }
            txtScore.setText(String.format("%d", score));
        }
    }


    private void showQuestion(int index) {
        if (index < totalQuestion) {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            if (Common.questionList.get(index).getIsImageQuestion().equals("true")) {
                //If is image
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            } else {
                question_text.setText(Common.questionList.get(index).getQuestion());

                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            btnA.setOnClickListener(this);
            btnB.setOnClickListener(this);
            btnC.setOnClickListener(this);
            btnD.setOnClickListener(this);

            //Start timer
            mCountDown.start();
        } else if (brightnessActivation == true) {
            //If it is final question
            //Stop the Chronometer
            mChronometer.stop();
            elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
            //Send data to Done.class
            Intent intent = new Intent(this, Done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE",previousScore);
            dataSend.putInt("SCOREPLUS", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            dataSend.putLong("TIME1", elapsedMillis/1000);
            dataSend.putString("TIME2", reading_time);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        } else {
            //If it is final question
            //Stop the Chronometer
            mChronometer.stop();
            elapsedMillis = SystemClock.elapsedRealtime() - mChronometer.getBase();
            //Send data to Done.class
            Intent intent = new Intent(this, Done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("SCOREPLUS", previousScorePlus);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            dataSend.putLong("TIME1", elapsedMillis/1000);
            dataSend.putString("TIME2", reading_time);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long minisec) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        //Time T2 :  playing ending
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        end_play_time = date.format(cal.getTime());
        //Toast.makeText(Playing.this, end_play_time, Toast.LENGTH_SHORT).show();
        mEditor.putString("end_play_time", end_play_time);
        mEditor.apply();
        super.onPause();
    }
}
