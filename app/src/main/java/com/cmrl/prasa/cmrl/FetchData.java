package com.cmrl.prasa.cmrl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by prasa on 06-May-16.
 * AsyncTask to Fetch the Data from the Website
 * Views are created Dynamically using createViews function of the CreateLayout class
 */

public class FetchData extends AsyncTask<String,String,String> {
    String response = "",infoRead;
    LinearLayout linearLayout;
    Context context;
    Toolbar toolbar;
    JSONObject obj,jsonObject;
    static List<EditText> editTextList;
    static List<CheckedTextView> checkedList;
    String createdViews[][];

    List<String> tunnelParameters;
    List<String> tunnelLabels;

    //Constructor to initialize values
    public FetchData(Context context,LinearLayout linearLayout,Toolbar toolbar){
        this.context = context;
        this.linearLayout = linearLayout;
        this.toolbar = toolbar;
        editTextList = new ArrayList<EditText>();
        checkedList = new ArrayList<CheckedTextView>();
        tunnelParameters = new ArrayList<String>();
        tunnelLabels = new ArrayList<String>();

        createdViews= new String[20][2];
        for(int i = 0; i< createdViews.length; i++){
            createdViews[i][0] = "";
            createdViews[i][1] = "";
        }
    }

    //All activities to be done in background will be present inside doInBackground
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        URL url = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            url = new URL(params[0]);
            //Open Http Connection
            connection = (HttpURLConnection) url.openConnection();

            //Use Http GET to fetch Data
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //Authorize connection using Username and Password
            final String basicAuth = "Basic " + Base64.encodeToString("cgmei:cmrl".getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty ("Authorization", basicAuth);

            //Read the fetched data
            String line = "";
            isr = new InputStreamReader(connection.getInputStream());
            br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            response = sb.toString();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(connection!=null)connection.disconnect();
        }
        return null;
    }

    //Changes to UI have to be done inside onPostExecute
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s!=null){
            try {
                obj = new JSONObject(s);
                jsonObject = obj.getJSONObject("data");
                Log.i("showArray", jsonObject.toString());
                Iterator<String> keys = jsonObject.keys();

                //Iterate through the keys
                while(keys.hasNext()){
                    String parameter = keys.next();
                    //Add key's name to List of Parameters
                    tunnelParameters.add(parameter);

                    //Parse the key name & replace '_' with SPACE and Replace lowercase letter following it with Uppercase letter
                    String label = parameter.replace("_"," ");
                    String[] tokens = label.split(" ");
                    label = "";
                    for(String str: tokens)
                      label += Character.toUpperCase(str.charAt(0)) + str.substring(1) + " ";
                    tunnelLabels.add(label);
                    //Log.i("showArray","tunnelParameters: " + parameter + "  tunnelLabels: " + label);
                }
                //Set Toolbar Title
                switch (tunnelParameters.get(0)){
                    case "tvd_id":
                        toolbar.setTitle("Tunnel Ventilation Dampers");
                        createViews(tunnelParameters,tunnelLabels,"dampers/create");
                        break;

                    case "tvf_id":
                        toolbar.setTitle("Tunnel Ventilation Fans");
                        createViews(tunnelParameters,tunnelLabels,"fans/create");
                        break;

                    case "escl_id":
                        toolbar.setTitle("Escalators");
                        createViews(tunnelParameters,tunnelLabels,"fans/create");
                        break;

                    default: // Added
                        toolbar.setTitle("Equipment unidentified");
                        createViews(tunnelParameters,tunnelLabels,"fans/create");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Function for Dynamic Creation of Views --> Checkboxes, EditTexts and Buttons
    private void createViews(List<String> tunnelParameters, List<String> tunnelLabels, String s) {
        int viewCount=0;
        //Iterate through all the parameters and create Views accordingly
        for (int i = 0; i < tunnelParameters.size(); i++) {
            try {
                //Retrieve value for each parameter from JSONObject
                infoRead = jsonObject.getString(tunnelParameters.get(i));
                //Create CheckBox whenever there's a "1" and add it to the 2D array of created Views
                if (infoRead.equals("1")) {
                    createdViews[viewCount][0] = tunnelParameters.get(i);
                    createdViews[viewCount++][1] ="Check";
                    new CreateLayout(context, linearLayout).createCheckedTextView(tunnelLabels.get(i));
                }
                //Create EditText whenever there's a "yes" and add it to the 2D array of created Views
                else if (infoRead.equals("yes")) {
                    createdViews[viewCount][0] = tunnelParameters.get(i);
                    createdViews[viewCount++][1] ="Edit";
                    new CreateLayout(context, linearLayout).createEditText();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new CreateLayout(context,linearLayout).createSubmitButton(s, createdViews, tunnelParameters);
    }
}