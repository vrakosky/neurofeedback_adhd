package utp.esirem.vincent.realtimegraph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import utp.esirem.vincent.realtimegraph.Common.Common;

public class Credit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        ImageView image_credit_0 = (ImageView) findViewById(R.id.image_credit_0);
        ImageView image_credit_1 = (ImageView) findViewById(R.id.image_credit_1);
        ImageView image_credit_2 = (ImageView) findViewById(R.id.image_credit_2);
        ImageView image_credit_3 = (ImageView) findViewById(R.id.image_credit_3);

        Picasso.with(getBaseContext()).load(R.drawable.image_credit_0).into(image_credit_0);
        Picasso.with(getBaseContext()).load(R.drawable.image_credit_1).into(image_credit_1);
        Picasso.with(getBaseContext()).load(R.drawable.image_credit_2).into(image_credit_2);
        Picasso.with(getBaseContext()).load(R.drawable.image_credit_3).into(image_credit_3);

        ((Button) findViewById(R.id.btn_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Credit.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
