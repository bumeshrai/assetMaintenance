package com.cmrl.prasa.cmrl;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    String responseCode;
    TextView resultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
           responseCode = extras.getString("response_code","0");
        }
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //If Result Code is 201 set Text to Submission successful, otherwise set Text to submission failed
        resultText = (TextView) findViewById(R.id.resultText);
        if(responseCode.equals("201"))
            resultText.setText("Submission Successful");
        else
            resultText.setText("Submission failed");

    }

}
