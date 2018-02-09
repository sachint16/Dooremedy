package com.techbros.sachin.dooremedy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QueryActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText qmail;
    private EditText qmobile;
    private EditText qAddress;
    private Button qsubmit;

    String name;
    String detail;

    final String URL="http://my171database.890m.com/account_profile.php";
    String q_email = "";
    String q_mobile = "";

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        qmail = (EditText) findViewById(R.id.emailField);
        qmobile = (EditText) findViewById(R.id.mobField);
        qAddress = (EditText) findViewById(R.id.addressField);
        qsubmit = (Button) findViewById(R.id.queryButton);

        HashMap<String,String> user = new SessionManager(this).getUserDetails();
        String email = user.get("name");
        //Toast.makeText(getApplicationContext(),email,Toast.LENGTH_LONG).show();

        qsubmit.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("employeeName");
        detail = bundle.getString("employeeDetail");

        getData(email);

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

                q_email = data.getString("email");
                q_mobile = data.getString("mobile_no");

                qmail.setText(q_email.toString().trim().toLowerCase());
                qmobile.setText(q_mobile.toString().trim().toLowerCase());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onClick(View v) {
        if (v == qsubmit) {
            mailUser();
        }
    }

    private void mailUser() {

        String email = qmail.getText().toString().trim().toLowerCase();
        String mobile = qmobile.getText().toString().trim().toLowerCase();
        String address = qAddress.getText().toString().trim().toLowerCase();
        String eName = name.toString().trim();
        String eDetail = detail.toString().trim();

        mail(email,mobile,address,eName,eDetail);

    }

    private void mail(final String email, final String mobile, final String address, final String eName , final String eDetail) {

        class MailUser extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(QueryActivity.this, "Please wait", "Processing...");
            }

            @Override
            protected String doInBackground(String... params) {
                String mail = params[0];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("email", mail));
                nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
                nameValuePairs.add(new BasicNameValuePair("address", address));
                nameValuePairs.add(new BasicNameValuePair("empName", eName));
                nameValuePairs.add(new BasicNameValuePair("empDetail", eDetail));
                String result = null;

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://my171database.890m.com/query.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();
                loadingDialog.dismiss();
                /*if (s.equalsIgnoreCase("success")) {

                    // Creating user login session
                    //String email = forgotEmail.getText().toString();


                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else */if (email.equals("") || mobile.equals("") || address.equals("")) {
                    //Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(QueryActivity.this, "Booking Failed", "Please enter all details", false);
                } else {

                    // username / password doesn't match
                    Toast.makeText(getApplicationContext(), "Please check your mail for booking confirmation: "+ email, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(QueryActivity.this, BookingConfirmationActivity.class);
                    startActivity(intent);
                    finish();
                    //alert.showAlertDialog(ForgotPasswordActivity.this, "Login failed..", "email not found", false);

                }
            }
        }

        MailUser mu = new MailUser();
        mu.execute(email);
    }
}
