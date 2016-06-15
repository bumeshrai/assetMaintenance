package com.cmrl.prasa.cmrl;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    LinearLayout linearLayout;
    Toolbar toolbar;
    String URLparams,url = "http://52.90.177.177/assetMaint/api/web/maintenance-next-dues/?assetCode=" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linearLayout = (LinearLayout) findViewById(R.id.ll);

        //Starting the ZXing Scanner
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();

    /*
       new FetchData(this,linearLayout,toolbar).execute("" +
               "http://52.90.177.177/assetMaint/api/web/maintenance-next-dues/?assetCode=0200250005000103");
    */
    }

    //Prevent Activity from restarting when Screen Orientation changes
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //Fetch Data from URL once the QRCode is scanned
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            URLparams = scanningResult.getContents();
            new FetchData(this,linearLayout,toolbar).execute(url + URLparams);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
