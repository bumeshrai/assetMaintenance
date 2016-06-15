package com.cmrl.prasa.cmrl;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

/**
 * Created by prasa on 08-May-16.
 * AsyncTask to submit data to the server
 * JSON String is formed using createJSONString
 */
public class SubmitData extends AsyncTask<String,String,String> {
    Context context;
    LinearLayout linearLayout;
    static String str;
    JSONObject parent;
    String alertText[];

    int checkValue;

    //Constructor to initialize values
    public SubmitData(Context context,LinearLayout ll){
        this.context = context;
        this.linearLayout = ll;
        alertText = new String[2];
        alertText[0] = "";
        alertText[1] = "";
    }

    //All activities to be done in background will be present inside doInBackground
    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            final String basicAuth = "Basic " + Base64.encodeToString("cgmei:cmrl".getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setFixedLengthStreamingMode(str.getBytes().length);
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(str);
            out.close();

            int responseCode = connection.getResponseCode();
            InputStream is = connection.getErrorStream();
            return String.valueOf(responseCode);

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
        /*Intent intent = new Intent(context,ResultActivity.class);
        intent.putExtra("response_code",s);
        context.startActivity(intent);*/
        if(s.equals("201"))
            Toast.makeText(context, "Submission Successful", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "Submission failed",Toast.LENGTH_LONG).show();
    }


    //Function that returns a String for display in the alert window
    public String[] createJSONString(String[][] createdViews, List<String> tunnelParameters) {
        int i= 0, j = 0;
        try {
            //JSONObject to be submitted
            parent = new JSONObject();
            parent.put("freq_id",0);

            //Initialize all keys with a blank value
            for(int k=0; k<tunnelParameters.size();k++)
                parent.put(tunnelParameters.get(i),"");

            //Iterate through the created views which is a 2D array
            for(int l=0; l<createdViews.length; l++)
                switch (createdViews[l][1]){
                    case "Check":
                        //For a checkbox write the parameter name and checked value to the JSONObject
                        //Call checked() function to see if checkbox is ticked or not
                        checked(i++);
                        parent.put(createdViews[l][0],checkValue);
                        break;
                    case "Edit":
                        //For an EditText write the parameter name and Text value to the JSONObject
                        parent.put(createdViews[l][0], FetchData.editTextList.get(j++).getText().toString());
                        break;
                }
            //Get Date of the phone and write to JSONObject
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH)+1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            String date = year + "-" + month + "-" + day;
            parent.put("created_at",date);
            str = parent.toString();

            //Set List of checked Items to NONE if none have been checked
            if(alertText[0].equals(""))
                alertText[0]= "(None)";
            //Set List of unchecked Items to NONE if none are unchecked
            if(alertText[1].equals(""))
                alertText[1]= "(None)";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alertText;
    }


    //Function to check if a checkbox has been ticked or not
    private void checked(int i) {
        //alertText[0] stores checked Items; alertText[1] stores unchecked Items
        if(FetchData.checkedList.get(i).isChecked()){
            checkValue = 1;
            alertText[0] += FetchData.checkedList.get(i).getText().toString() + "<br>";
        }
        else{
            checkValue = 0;
            alertText[1] += FetchData.checkedList.get(i).getText().toString() + "<br>";
        }
    }
}