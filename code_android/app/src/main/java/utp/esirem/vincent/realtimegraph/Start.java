package utp.esirem.vincent.realtimegraph;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import utp.esirem.vincent.realtimegraph.Common.Common;
import utp.esirem.vincent.realtimegraph.Model.Question;
import utp.esirem.vincent.realtimegraph.Reading.Reading;

public class Start extends AppCompatActivity {

    Button btnPlay, btnRead;

    FirebaseDatabase database;
    DatabaseReference questions;
    Boolean EXTRA_ACTIVATION  = false;

    //Shared preferences
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    Switch switch1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        database = FirebaseDatabase.getInstance();
        questions = database.getReference("Questions");

        loadQuestion(Common.categoryId);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Start.this, Playing.class);
                intent.putExtra("EXTRA_ACTIVATION", EXTRA_ACTIVATION);
                startActivity(intent);
                finish();
            }
        });

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Start.this, Reading.class);
                startActivity(intent);
                finish();
            }
        });

        //Switch
        switch1 = (Switch) findViewById(R.id.switchActivate);
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
        /*
        //Skip to another Activity after a delay
        new Timer().schedule(new TimerTask() {
                                 @Override
                                 public void run() {
                                     Intent i = new Intent(Start.this, MainActivity.class);
                                     startActivity(i);
                                     finish();
                                 }
        },10000);
        */
    }

    private void loadQuestion(String categoryId) {

        //First, clear List if have old question
        if (Common.questionList.size() > 0)
            Common.questionList.clear();

        questions.orderByChild("CategoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot posSnapshot : dataSnapshot.getChildren()) {
                            Question ques = posSnapshot.getValue(Question.class);
                            Common.questionList.add(ques);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //Random list
        Collections.shuffle(Common.questionList);
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
