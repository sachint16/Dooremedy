package com.techbros.sachin.dooremedy;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AccountActivity extends AppCompatActivity {

    TextView account_name;
    TextView account_email;
    TextView account_mobile;
    Button callUs;
    Button shareApp;
    Button aboutApp;


    final String URL="http://my171database.890m.com/account_profile.php";
    final String LINK_URL="http://my171database.890m.com/app_link.php";
    String app_link = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        account_name = (TextView) findViewById(R.id.account_name);
        account_email = (TextView) findViewById(R.id.account_email);
        account_mobile = (TextView) findViewById(R.id.account_mobile);
        callUs = (Button)findViewById(R.id.callUs);
        shareApp = (Button) findViewById(R.id.shareApp);
        aboutApp = (Button) findViewById(R.id.aboutApp);

        Intent i=getIntent();
        HashMap<String,String> email=new SessionManager(this).getUserDetails();
        String Email=email.get("name");
        //Toast.makeText(getApplicationContext(),email,Toast.LENGTH_LONG).show();
        getData(Email);

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = "+919818650312";

                //String uri = "tel:" + mobile.trim() ;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobile, null));
                //intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AboutAppActivity.class);
                startActivity(intent);
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                getLink();

                String share_link = app_link;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, share_link);
                intent.setType("text/plain");

                final Intent chooser=Intent.createChooser(intent,"Select Application");
                startActivity(chooser);
            }
        });
    }
    public void getData(String email){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL + "?email=" + email,
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
                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void parseData(JSONArray response) {

        for(int i= 0;i< response.length();i++)
        {

            JSONObject data = null;
            try
            {

                data = response.getJSONObject(i);

                String tname = data.getString("name");
                account_name.setText(tname.substring(0,1).toUpperCase()+tname.substring(1));
                account_email.setText(data.getString(("email")));
                account_mobile.setText(data.getString(("mobile_no")));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void getLink(){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(LINK_URL ,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                        parseLink(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void parseLink(JSONArray response) {

        for(int i= 0;i< response.length();i++)
        {

            JSONObject data = null;
            try
            {

                data = response.getJSONObject(i);

                app_link = data.getString("share_link");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}
