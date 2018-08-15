package com.example.kamin.thecarpentrytutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Information extends AppCompatActivity {

    int length;
    int width;
    int height;

    String itemValue = "Testing";
    ListView listView;
    String blockID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        listView = findViewById(R.id.listView);
        //Calls getJSON and gives it the url
        getJSON("https://thecarpentrytutor.000webhostapp.com/TestPull.php");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemValue = (String) listView.getItemAtPosition(position);

                Intent intent = new Intent();
                System.out.println(position);
                intent.putExtra("blockID", String.valueOf(position + 1));
                intent.putExtra("blockName", itemValue);

                setResult(RESULT_OK, intent);
                finish();

            }
        });


    }




    private void getJSON(final String scriptURL) {

        class DownloadJSON extends AsyncTask<String, String, String> {

            ProgressDialog pd;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd = new ProgressDialog(Information.this);
                pd.setMessage("Please Wait");
                pd.show();
            }


            @Override
            protected void onPostExecute(String s) {
                if(pd.isShowing()){
                    pd.dismiss();
                }
                try {
                    System.out.println(s);
                    loadIntoListView(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... strings) {
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
        }


        // Creates a class that processes the json
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }


    private void loadIntoListView(String blockNamesJSON) throws JSONException {

        //Creates a JSON array that is populated from the JSON string returned from the python script
        JSONArray jsonArray = new JSONArray(blockNamesJSON);


        //Creates a string array that is the length of the JSON array
        String[] blockInfo = new String[jsonArray.length()];

        //This will loop through the json array and create an individual object for each object in the JSON Array
        // It wil then take that json object and get each individual piece and seperate it via get String
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            blockInfo[i] = obj.getString("blockName");
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, blockInfo);
        listView.setAdapter(arrayAdapter);
    }
}