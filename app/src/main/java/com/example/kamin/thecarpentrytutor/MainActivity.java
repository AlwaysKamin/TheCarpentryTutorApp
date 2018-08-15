package com.example.kamin.thecarpentrytutor;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private EditText usernameField,passwordField;
    private TextView status,role,method;
    int nameRequest;

    String mInfo;
    String length, width, height, blockID, blockName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final Button getBlocks = findViewById(R.id.getBlockButton);
        final Button getBlockData = findViewById(R.id.getBlockDataButton);

        final TextView blockResults = findViewById(R.id.blockName);

        final TextView blockNameText = findViewById(R.id.blockName);
        final TextView blockIDText = findViewById(R.id.blockID);
        final TextView blockLengthText = findViewById(R.id.blockLength);
        final TextView blockWidthText = findViewById(R.id.blockWidth);
        final TextView blockHeightText = findViewById(R.id.blockHeight);


        //Code layout for button click is from android documentation
        getBlockData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String blockInfo = "BlockID: " + blockID + ": Length: " + length + " Width: " + width + " Height: " + height;
                blockResults.setText(blockInfo);
                blockNameText.setText("Block Name: " + blockName);
                blockIDText.setText("Block ID: " + blockID);
                blockLengthText.setText("Length: : " + length);
                blockWidthText.setText("Width: " + width);
                blockHeightText.setText("Height: " + height);
            }
        });


        getBlocks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent i = new Intent(MainActivity.this, Information.class);
                startActivityForResult(i, nameRequest);
            }
        });


        setSupportActionBar(toolbar);




    }

    private void getFromPHP(final String scriptURL){
        class getBlockData extends AsyncTask<String, String, String>{
            @Override
            protected void onPreExecute(){

            }

            @Override
            protected String doInBackground(String... strings){
                try {
                    URL url = new URL(scriptURL);
                    // Opens the url connection and casts it as HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();


                    // String builders are more like c++ strings, remember from cs249 strings are immutable
                    StringBuilder sb = new StringBuilder();

                    // Buffered reader will read the input from the php script and place it in the bufferedreader
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    //The buffered reader will read a line and store it in the string json which will then be appended into the string builder seperated by a line return
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.e("Error", "Something went really really wrong");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s){
                try {
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        getBlockData getData = new getBlockData();
        getData.execute();
    }

    private void loadIntoListView(String blockNamesJSON) throws JSONException {

        //Creates a JSON array that is populated from the JSON string returned from the python script
        JSONArray jsonArray = new JSONArray(blockNamesJSON);


        //Creates a string array that is the length of the JSON array
        String[] blockInfo = new String[jsonArray.length()];

        //This will loop through the json array and create an individual object for each object in the JSON Array
        // It wil then take that json object and get each individual piece and seperate it via get String

            JSONObject obj = jsonArray.getJSONObject(0);
            length = obj.getString("length");
            width = obj.getString("width");
            height = obj.getString("height");


    }

    /// CODE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == nameRequest && resultCode == RESULT_OK){
            blockName = data.getStringExtra("blockName");
            blockID = data.getStringExtra("blockID");

            getFromPHP("https://thecarpentrytutor.000webhostapp.com/blockData.php?blockID=" + blockID);
        }
    }



}
