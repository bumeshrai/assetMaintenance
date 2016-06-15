package com.cmrl.prasa.cmrl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by prasa on 06-May-16.
 * Class for creating Layout dynamically
 */
public class CreateLayout{
    CheckedTextView checkedTextView;
    EditText editText;
    LinearLayout linearLayout;
    Button b;
    String alertText[];
    int checkedId = 100;
    String url = "http://52.90.177.177/assetMaint/api/web/tunnel-ventilation-";

    Context context;
    LinearLayout.LayoutParams textParams, editParams,checkBoxParams,buttonParams;

    //Constructor to initialize values
    public CreateLayout(Context context, LinearLayout linearLayout){
        this.context = context;
        this.linearLayout = linearLayout;
        alertText = new String[2];

        //Layout Parameters for different Views
        textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(15, 20, 0, 15);

        checkBoxParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        checkBoxParams.setMargins(0, 10, 0, 10);

        editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        editParams.setMargins(0, 10, 0, 10);

        buttonParams = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(0, 50, 0, 20);
        buttonParams.gravity = Gravity.CENTER_HORIZONTAL;

    }

    //Function to create CheckedTextView (Check box)
    void createCheckedTextView(String checkName){
        checkedTextView = new CheckedTextView(context);
        FetchData.checkedList.add(checkedTextView);
        checkedTextView.setChecked(false);
        checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);

        checkedTextView.setBackgroundResource(R.drawable.rounded_lightblue);
        checkedTextView.setText(checkName);
        checkedTextView.setTextColor(Color.BLACK);
        checkedTextView.setTextSize(17);
        checkedTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        checkedTextView.setGravity(Gravity.CENTER_VERTICAL);
        checkedTextView.setPadding(20, 20, 20, 20);

        checkedTextView.setId(checkedId++);
        checkedTextView.setClickable(true);
        checkedTextView.setFocusable(true);

        /*OnClick of CheckBox:
        It is checked if it's already not checked
        It is unchecked if it's already checked
        */
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    checkedTextView.setChecked(false);
                    checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                } else {
                    checkedTextView.setChecked(true);
                    checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                }
            }
        });
        checkedTextView.setLayoutParams(checkBoxParams);
        linearLayout.addView(checkedTextView);

    }

    //Function to create EditText (Comment box)
    void createEditText() {
        editText = new EditText(context);
        FetchData.editTextList.add(editText);
        editText.setLayoutParams(editParams);
        editText.setTextColor(Color.BLACK);
        editText.setTextSize(17);
        editText.setHint("Comments");
        editText.setHintTextColor(Color.DKGRAY);
        editText.setPadding(20, 20, 20, 20);
        editText.setBackgroundResource(R.drawable.rounded_white);
        linearLayout.addView(editText);
    }

    //Function to create Submit button
    public void createSubmitButton(final String URLParams, final String[][] createdViews,final List<String> tunnelParameters) {
        b = new Button(context);
        b.setText("SUBMIT");
        //Create JSON String onclick of submit button and Show Alert window
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertText = new SubmitData(context, linearLayout).createJSONString(createdViews, tunnelParameters);
                show_alert(URLParams);
            }
        });
        b.setBackgroundResource(R.drawable.rounded_button);
        b.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        b.setTextColor(Color.WHITE);
        b.setLayoutParams(buttonParams);
        linearLayout.addView(b);
    }


    //Function to show alert before submitting the form
    private void show_alert(final String URLParams) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Spanned message = Html.fromHtml("<b>" + "List of Items checked: " + "</b><br>" + alertText[0] + "<br><br><b>" + "List of Items not checked: " + "</b><br>" + alertText[1]);
                builder.setTitle("Do you want to proceed?")
                        .setMessage(message)
                        .setCancelable(false)
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Dismiss Dialog onclick of NO
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Submit to server onclick of YES
                                new SubmitData(context, linearLayout).execute(url + URLParams);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }
}
