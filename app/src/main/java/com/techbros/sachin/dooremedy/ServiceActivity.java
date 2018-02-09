package com.techbros.sachin.dooremedy;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

public class ServiceActivity extends AppCompatActivity {

    //    ListView mListView;
    RecyclerView serviceRecyclerView;
    ArrayList<ServiceListItem> data;
    ServiceActivityAdapter adapter;
    String id;
    final String URL="http://my171database.890m.com/fetch_cid.php";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        serviceRecyclerView = (RecyclerView) findViewById(R.id.lv_services);
        data = new ArrayList<>();
        adapter = new ServiceActivityAdapter(data, this);

        serviceRecyclerView.setAdapter(adapter);


//
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        serviceRecyclerView.setLayoutManager(linearLayoutManager);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
//        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        serviceRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        String city_name = bundle.getString("selected_area");

        //Toast.makeText(getApplicationContext(), city_name, Toast.LENGTH_LONG).show();

        //TextView txt = (TextView) findViewById(R.id.temp_text);
        //txt.setText(message);

        // URL to the JSON data
        String strUrl = "http://my171database.890m.com/services.php";

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);

        getData(city_name);

        // Getting a reference to ListView of activity_main
//        mListView = (ListView) findViewById(R.id.lv_services);
//        TextView tv_service_id = (TextView) findViewById(R.id.tv_service_id);
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//
//                //String sid = tv_service_id.getText().toString().trim();
//                //String sid = "hello";
//
//                Intent intent = new Intent(ServiceActivity.this, EmployeeActivity.class);
//                //intent.putExtra("service_id",service_id);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    public void getData(String city) {
        String city1;
        if(city.contains(" ")) {
            String[] data = city.split(" ");
            String first = data[0];
            String sec = data[1];
             city1 = first + "%20" + sec;
        }else{
            city1=city;
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL+"?city="+city1,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void parseData(JSONArray response) {

        for (int i = 0; i < response.length(); i++) {

            JSONObject data = null;
            try {

                data = response.getJSONObject(i);

                String cid = data.getString("cid");
                //Toast.makeText(getApplicationContext(), cid, Toast.LENGTH_LONG).show();
                new SendCityId().setCityId(cid);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception in dwn url", e.toString());
        } finally {
            iStream.close();
        }

        return data;
    }


    /**
     * AsyncTask to download json data
     */
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            // The parsing of the xml data is done in a non-ui thread
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

            // Start parsing xml data
            listViewLoaderTask.execute(result);

        }
    }

    /**
     * AsyncTask to parse json data and load ListView
     */
    private class ListViewLoaderTask extends AsyncTask<String, Void, ArrayList<ServiceListItem>> {

        JSONObject jObject;

        // Doing the parsing of xml data in a non-ui thread
        @Override
        protected ArrayList<ServiceListItem> doInBackground(String... strJson) {
            try {
                jObject = new JSONObject(strJson[0]);
                ServiceJSONParser serviceJsonParser = new ServiceJSONParser();
                serviceJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("JSON Exception1", e.toString());
            }
            ArrayList<ServiceListItem> serviceListItems = new ArrayList<>();
            // Instantiating json parser class
            ServiceJSONParser serviceJsonParser = new ServiceJSONParser();

            // A list object to store the parsed services list
//            List<HashMap<String, Object>> services = null;

            try {
                // Getting the parsed data as a List construct
                serviceListItems = serviceJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }

//            // Keys used in Hashmap
//            String[] from = {"name", "serviceImg","details"};
//
//            // Ids of views in listview_layout
//            int[] to = {R.id.tv_service, R.id.iv_img, R.id.tv_service_id};

            // Instantiating an adapter to store each items
            // R.layout.listview_layout defines the layout of each item
//            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), services, R.layout.service_list, from, to);

            return serviceListItems;
        }

        /**
         * Invoked by the Android on "doInBackground" is executed
         */
        @Override
        protected void onPostExecute(ArrayList<ServiceListItem> serviceListItems) {
            data.clear();
            // Setting adapter for the listview
//            mListView.setAdapter(adapter);
            for (ServiceListItem i : serviceListItems)
                data.add(i);

//            data = serviceListItems;
            adapter.notifyDataSetChanged();
//            for (int i = 0; i < adapter.getCount(); i++) {
//                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
//                String imgUrl = (String) hm.get("service_img");
//                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();
//
//                HashMap<String, Object> hmDownload = new HashMap<String, Object>();
//                hm.put("service_img", imgUrl);
//                hm.put("position", i);
//
//                // Starting ImageLoaderTask to download and populate image in the listview
//                imageLoaderTask.execute(hm);
//            }
        }
    }

    /**
     * AsyncTask to download and load an image in ListView
     */
//    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {
//
//        @Override
//        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {
//
//            InputStream iStream = null;
//            String imgUrl = (String) hm[0].get("service_img");
//            int position = (Integer) hm[0].get("position");
//
//            URL url;
//            try {
//                url = new URL(imgUrl);
//
//                // Creating an http connection to communicate with url
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//
//                // Connecting to url
//                urlConnection.connect();
//
//                // Reading data from url
//                iStream = urlConnection.getInputStream();
//
//                // Getting Caching directory
//                File cacheDirectory = getBaseContext().getCacheDir();
//
//                // Temporary file to store the downloaded image
//                File tmpFile = new File(cacheDirectory.getPath() + "/wpta_" + position + ".jpeg");
//
//                // The FileOutputStream to the temporary file
//                FileOutputStream fOutStream = new FileOutputStream(tmpFile);
//
//                // Creating a bitmap from the downloaded inputstream
//                Bitmap b = BitmapFactory.decodeStream(iStream);
//
//                // Writing the bitmap to the temporary file as png file
//                b.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);
//
//                // Flush the FileOutputStream
//                fOutStream.flush();
//
//                //Close the FileOutputStream
//                fOutStream.close();
//
//                // Create a hashmap object to store image path and its position in the listview
//                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();
//
//                // Storing the path to the temporary image file
//                hmBitmap.put("serviceImg", tmpFile.getPath());
//
//                // Storing the position of the image in the listview
//                hmBitmap.put("position", position);
//
//                // Returning the HashMap object containing the image path and position
//                return hmBitmap;
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(HashMap<String, Object> result) {
//            // Getting the path to the downloaded image
//            String path = (String) result.get("serviceImg");
//
//            // Getting the position of the downloaded image
//            int position = (Integer) result.get("position");
//
//            // Getting adapter of the listview
//            SimpleAdapter adapter = (SimpleAdapter) mListView.getAdapter();
//
//            // Getting the hashmap object at the specified position of the listview
//            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
//
//            // Overwriting the existing path in the adapter
//            hm.put("serviceImg", path);
//
//            // Noticing listview about the dataset changes
//            adapter.notifyDataSetChanged();
//        }
//    }
}