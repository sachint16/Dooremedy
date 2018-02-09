package com.techbros.sachin.dooremedy;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class EmployeeActivity extends AppCompatActivity {

    //    ListView mListView;
    RecyclerView employeeRecyclerView;
    ArrayList<EmployeeListItem> data;
    EmployeeActivityAdapter adapter;
    private  String requested_service = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        employeeRecyclerView = (RecyclerView) findViewById(R.id.lv_employees);
        data = new ArrayList<>();
        adapter = new EmployeeActivityAdapter(data,this);

        employeeRecyclerView.setAdapter(adapter);
//
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        employeeRecyclerView.setLayoutManager(linearLayoutManager);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
//        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        employeeRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("serviceId");

        requested_service = message;
        String cid = new SendCityId().getCityId().toString();
        //TextView txt = (TextView) findViewById(R.id.temp_text);
        //txt.setText(message);

        // URL to the JSON data
        String strUrl = "";

        //Toast.makeText(getApplicationContext(),"sid="+requested_service,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),"cid="+cid,Toast.LENGTH_LONG).show();

        strUrl = ("http://my171database.890m.com/employees.php?sid="+requested_service+"&"+"cid="+cid).toString();

        /*if(requested_service.equals("1"))
        {
            strUrl = "http://my171database.890m.com/employees_maid.php";
        }
        else if(requested_service.equals("2"))
        {
            strUrl = "http://my171database.890m.com/employees_plumber.php";
        }
        else if(requested_service.equals("3"))
        {
            strUrl = "http://my171database.890m.com/employees_electrician.php";
        }
        else if(requested_service.equals("4"))
        {
            strUrl = "http://my171database.890m.com/employees_rp_mn.php";
        }
        else if(requested_service.equals("5"))
        {
            strUrl = "http://my171database.890m.com/employees_event.php";
        }
        else if(requested_service.equals("6"))
        {
            strUrl = "http://my171database.890m.com/employees_developer.php";
        }
        else if(requested_service.equals("7"))
        {
            strUrl = "http://my171database.890m.com/employees_pac_mov.php";
        }*/

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);


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
    private class ListViewLoaderTask extends AsyncTask<String, Void, ArrayList<EmployeeListItem>> {

        JSONObject jObject;

        // Doing the parsing of xml data in a non-ui thread
        @Override
        protected ArrayList<EmployeeListItem> doInBackground(String... strJson) {
            try {
                jObject = new JSONObject(strJson[0]);
                EmployeeJSONParser employeeJsonParser = new EmployeeJSONParser();
                employeeJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("JSON Exception1", e.toString());
            }
            ArrayList<EmployeeListItem> employeeListItems = new ArrayList<>();
            // Instantiating json parser class
            EmployeeJSONParser employeeJsonParser = new EmployeeJSONParser();

            // A list object to store the parsed services list
//            List<HashMap<String, Object>> services = null;

            try {
                // Getting the parsed data as a List construct
                employeeListItems = employeeJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }

            return employeeListItems;
        }

        /**
         * Invoked by the Android on "doInBackground" is executed
         */
        @Override
        protected void onPostExecute(ArrayList<EmployeeListItem> employeeListItems) {
            data.clear();
            // Setting adapter for the listview
//            mListView.setAdapter(adapter);
            for(EmployeeListItem i : employeeListItems)
                data.add(i);

//            data = serviceListItems;
            adapter.notifyDataSetChanged();

        }
    }


}