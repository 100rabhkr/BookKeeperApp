package com.example.android.bookkeeperapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String URL1 = "https://www.googleapis.com/books/v1/volumes?q=";
    private String URL2 = "&maxResults=10&prettyPrint=false";
    private String URL_FINAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String URL_ID = URL+urlmid+URL2;
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = (EditText) findViewById(R.id.editText);
                String urlmid = editText.getText().toString().trim();
                String Furlmid = urlmid;
                Log.v("Middle", urlmid);
                if (urlmid.isEmpty()) {
                    editText.setError("Search Term Is Required");
                } else {
                    if (urlmid.contains(" ")) {
                        String temp = urlmid.replaceAll(" ", "%20");
                        Furlmid = temp;
                    }
                    if (isNetworkAvailable()) {
                        String URL_ID = URL1 + Furlmid + URL2;
                        URL_FINAL = URL_ID;
                        Log.v("URL", URL_ID);
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute();
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("No Internet Connection Available");
                        alertDialog.setMessage("We can not proceed further there is no Internet Conection");
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }

        });
    }

   private class BookAsyncTask extends AsyncTask<URL, Void, String>{
        @Override
        protected String doInBackground(URL... urls) {
            URL url = createUrl(URL_FINAL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                return;
            }
            sendlist(s);
        }

       private URL createUrl(String stringUrl) {
           URL url = null;
           try {
               url = new URL(stringUrl);
           } catch (MalformedURLException exception) {
               Log.e("myerror", "Error with creating URL", exception);
               return null;
           }
           return url;
       }

       private String makeHttpRequest(URL url) throws IOException {
           String jsonResponse = "";
           if(url == null){
               return jsonResponse;
           }

           HttpURLConnection urlConnection = null;
           InputStream inputStream = null;
           try {
               urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestMethod("GET");
               urlConnection.setReadTimeout(10000 /* milliseconds */);
               urlConnection.setConnectTimeout(15000 /* milliseconds */);
               urlConnection.connect();
               inputStream = urlConnection.getInputStream();
               jsonResponse = readFromStream(inputStream);
           } catch (IOException e) {
               // TODO: Handle the exception
           } finally {
               if (urlConnection != null) {
                   urlConnection.disconnect();
               }
               if (inputStream != null) {
                   // function must handle java.io.IOException here
                   inputStream.close();
               }
           }
           return jsonResponse;
       }

       private String readFromStream(InputStream inputStream) throws IOException {
           StringBuilder output = new StringBuilder();
           if (inputStream != null) {
               InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
               BufferedReader reader = new BufferedReader(inputStreamReader);
               String line = reader.readLine();
               while (line != null) {
                   output.append(line);
                   line = reader.readLine();
               }
           }
           return output.toString();
       }

    }

    private void sendlist(String sobject){
        if(sobject==null){
            TextView urltext = (TextView) findViewById(R.id.textView);
            urltext.setText("No Valid Data Found. Kindly enter valid search terms like android...etc. to populate the ListView");
        }
        else {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra("JsonObject", sobject);
            startActivity(intent);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
