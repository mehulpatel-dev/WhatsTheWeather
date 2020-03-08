package com.mehul.whatstheweather;

/*
    Title: What's The Weather
    Author: Mehul Patel
    Version: 1.0
    Date: 03/08/2020
    Description: This app takes the user input of whatever city they enter and outputs the weather
                for that city using the JSON API from openweathermap.org
*/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    //create the EditText (user inputted text) and the TextView(to display the output of the
    //weather) variables globally
    EditText cityName;
    TextView resultTextView;

    //DownloadTask class to retrieve the contents of the weather api from openweathermap.org
    //Accepts string, returns no method, and returns string)
    public class DownloadTask extends AsyncTask <String, Void, String>{

        //doInBackground method to pull the data from the openweathermap.org url which contains the
        //JSON data on the webpage
        @Override
        protected String doInBackground(String... strings) {

            //create string variable to hold store the contents of the website
            //create the url variable to use it as a URL
            //Creating the httpurlconnection variable to fetch the contents of the website
            String result = "";
            URL url = null;
            HttpURLConnection urlConnection = null;

            //convert url that is passed to the class as string result
            //could result in errors so surround it with try catch
            try {
                //assign url passed to this class from the onCreate method to the url variable
                url = new URL(strings[0]);

                //assign the url to the HttpURLConnection variable to fetch the contents/establish connection
                urlConnection = (HttpURLConnection) url.openConnection();

                //create InputStream variable hold the input of data that comes in
                InputStream in = urlConnection.getInputStream();

                //create InputStreamReader variable to read the data that comes in in the InputStream to read the contents of the URL
                InputStreamReader reader = new InputStreamReader(in);

                //create variable to keep track of location throughout the HTML in order to read the data from the InputStreamReader one character at a time
                int data = reader.read();

                //loop to read the data. Once it reads through all the data will equal -1 so in this case the loop will end once data is read to -1.
                while(data != -1){
                    //create variable to obtain current character that is being downloaded
                    char current = (char) data;

                    //add current to the result as it is being read.
                    result += current;

                    //tells data to read the next character
                    data = reader.read();
                }

                return  result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather for that city", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        //takes the results from the doIinBackground method and passes through the onPostExecute
        //method to process JSON in order to interact with UI
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);


            try {
                //create string variable to hold the message/output
                String message = " ";
                JSONObject jsonObject = new JSONObject(s);

                //convert the contents of "weather" part/variable in JSON to string
                String weatherInfo = jsonObject.getString("weather");

                //take the weather info and convert it into JSON array
                JSONArray arr = new JSONArray(weatherInfo);

                //run a for loop for the length of the JSONArray and gets the JSON part/variable for
                //main and description and assigns/appends it to the message string if it is not
                //empty in JSON
                for (int i = 0; i < arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";

                    main =  jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != ""){
                        message += main + ": " + description + "\r\n";
                    }
                }

                //if message is not empty it will set the TextView to the message so it will display
                //the weather info
                if (message != " "){
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather for that city", Toast.LENGTH_LONG).show();
                }

            //catch will output a message as toast to the user if it catches an exception
            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather for that city", Toast.LENGTH_LONG).show();
            }


        }
    }

    //onClick Method for when the button is pressed by user
    public void checkWeatherButton(View v){
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather for that city", Toast.LENGTH_LONG).show();
        }




    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declare the EditText (user inputted text) and the TextView(to display the output of the weather) when app opens up (onCreate)
        cityName = (EditText) findViewById(R.id.cityNameEnteredText);
        resultTextView = (TextView) findViewById(R.id.weatherTextView);

    }
}
