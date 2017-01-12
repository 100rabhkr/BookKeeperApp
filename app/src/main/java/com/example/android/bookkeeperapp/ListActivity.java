package com.example.android.bookkeeperapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Bundle bundle = getIntent().getExtras();
        String bookJSON = bundle.getString("JsonObject");
        final ArrayList<CustomObject> bookCustomObject = new ArrayList<>();
        String titlename;
        String Authorname;
        String Descriptionlong;
        String previewUrl;
        String imageUrl;
        if(TextUtils.isEmpty(bookJSON)){
            TextView errorTextView = (TextView)findViewById(R.id.errortextview);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("No Valid Result Found Please Go back and try again");
        }
        else {
            try {JSONObject baseJsonResponse = new JSONObject(bookJSON);
                if(baseJsonResponse.has("items")){
                JSONArray itemArray = baseJsonResponse.getJSONArray("items");
                for (int i =0; i<itemArray.length();i++) {

                    JSONObject currentbook = itemArray.getJSONObject(i);
                    JSONObject bookinfo = currentbook.getJSONObject("volumeInfo");
                    if(bookinfo.has("title")){
                        titlename = bookinfo.getString("title");}
                    else {
                        titlename = "Not Found";
                    }
                    if(bookinfo.has("authors")){

                        JSONArray authorarray = bookinfo.getJSONArray("authors");
                        String[] Sauthor = new String[authorarray.length()];

                        for(int j=0;j<authorarray.length();j++){
                            Sauthor[j]= authorarray.getString(j);
                        }

                        Authorname = arrtostrng(Sauthor);}
                    else if (bookinfo.has("publisher")){
                        Authorname = bookinfo.getString("publisher");
                    }
                    else {
                        Authorname = "Not Available";
                    }

                    if(currentbook.has("searchInfo")){
                        JSONObject information = currentbook.getJSONObject("searchInfo");
                        if (information.has("textSnippet")) {
                            Descriptionlong = information.getString("textSnippet");
                        }

                        else {
                            Descriptionlong = "Not Available for this book";
                        }
                    }
                    else {
                        Descriptionlong = "Not Available";
                    }
                    if(currentbook.has("accessInfo")) {
                        JSONObject saleinfo = currentbook.getJSONObject("accessInfo");
                        if(saleinfo.has("webReaderLink")){
                            previewUrl = saleinfo.getString("webReaderLink");}
                        else {
                            previewUrl = "https://www.google.com/null";
                        }
                    }
                    else {
                        previewUrl = "https://www.google.com/null";
                    }

                    if(currentbook.has("imageLinks")){
                        JSONObject imageinfo = currentbook.getJSONObject("imageLinks");
                        if (imageinfo.has("thumbnail")||imageinfo.has("smallThumbnail")){
                            if(imageinfo.has("thumbnail")){
                                imageUrl = imageinfo.getString("thumbnail");
                            }
                            else {
                                imageUrl = imageinfo.getString("smallThumbnail");
                            }
                        }
                        else {
                            imageUrl = null;
                        }
                    }
                    else {
                        imageUrl = null;
                    }
                    CustomObject customObject = new CustomObject(titlename,Authorname,Descriptionlong,previewUrl,imageUrl);
                    bookCustomObject.add(customObject);
                }
                    CustomAdapter adapter = new CustomAdapter(this,bookCustomObject);
                    ListView bookListView = (ListView) findViewById(R.id.list);
                    bookListView.setAdapter(adapter);
                    bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            CustomObject customObject = bookCustomObject.get(i);
                            final String url = customObject.getmPreviewUrl();
                            AlertDialog alertDialog = new AlertDialog.Builder(ListActivity.this).create();
                            alertDialog.setTitle(customObject.getMtitle());
                            alertDialog.setMessage("Preview the book in your Browser");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Proceed",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                    Uri.parse(url));

                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(intent);
                                            }


                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });

                }
                else {
                    TextView errorTextView = (TextView)findViewById(R.id.errortextview);
                    errorTextView.setVisibility(View.VISIBLE);
                    errorTextView.setText("Error Parsing JSON, Please Go back and try again- Enter A Valid Search term like: android...etc");
                }
            } catch (JSONException e) {
                Log.e("List_Error", "Problem parsing the earthquake JSON results", e);
            }


        }
    }

    public String arrtostrng(String[] arr){
        String temp = arr[0] ;
        for(int i =1; i<arr.length; i++){
            if(arr[i]==null){
                break;
            }
            else {
                temp = temp + ", " + arr[i];
            }
        }
        temp = temp+".";
        return temp;
    }
}
