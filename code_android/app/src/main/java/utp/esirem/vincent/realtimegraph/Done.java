package utp.esirem.vincent.realtimegraph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import utp.esirem.vincent.realtimegraph.Common.Common;
import utp.esirem.vincent.realtimegraph.Model.QuestionScore;

public class Done extends AppCompatActivity {

    Button btnTryAgain;
    TextView txtResultScore, txtResultScorePlus, getTxtResultQuestion;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference question_score;

    //Shared preferences
    String start_play_time, start_read_time, end_play_time, end_read_time;
    public static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");

        txtResultScore = (TextView) findViewById(R.id.txtTotalScore);
        txtResultScorePlus = (TextView) findViewById(R.id.txtTotalScorePlus);
        getTxtResultQuestion = (TextView) findViewById(R.id.txtTotalQuestion);
        progressBar = (ProgressBar) findViewById(R.id.doneProgressBar);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        //SHARED PREFERENCE GET
        mPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        start_read_time = mPreferences.getString("start_read_time", "00:00");
        end_read_time = mPreferences.getString("end_read_time", "00:00");
        start_play_time = mPreferences.getString("start_play_time", "00:00");
        end_play_time = mPreferences.getString("end_play_time", "00:00");

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Done.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        //Get data from bundle and set view
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            int score = extra.getInt("SCORE");
            int scorePlus = extra.getInt("SCOREPLUS");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");
            Long time1 = extra.getLong("TIME1");
            String time2 = extra.getString("TIME2");


            txtResultScore.setText(String.format("SCORE (fixed): %d", score));
            txtResultScorePlus.setText(String.format("SCORE (fNIRS): %d", scorePlus));
            getTxtResultQuestion.setText(String.format("PASSED : %d / %d", correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);

            //Upload point to DB
            question_score.child(String.format("%s_%s", Common.currentUser.getUserName(),
                                                        Common.categoryId))
                    .setValue(new QuestionScore(String.format("%s_%s", Common.currentUser.getUserName(),
                            Common.categoryId),
                            Common.currentUser.getUserName(),
                            String.valueOf(score),
                            String.valueOf(scorePlus),
                            Common.categoryId,
                            Common.categoryName,
                            String.valueOf(time1),
                            time2,
                            start_read_time,
                            end_read_time,
                            start_play_time,
                            end_play_time));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
