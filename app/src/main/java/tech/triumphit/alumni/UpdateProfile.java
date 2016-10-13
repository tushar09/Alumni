package tech.triumphit.alumni;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ScrollingTabContainerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tech.triumphit.alumni.databinding.ActivityUpdateProfileBinding;

public class UpdateProfile extends AppCompatActivity {

    ActivityUpdateProfileBinding activityUpdateProfileBinding;
    ProgressDialog pdialog;
    private MaterialDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private ArrayList<String> list_of_countries;
    String[] countries;
    String[] uni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUpdateProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        setSupportActionBar(activityUpdateProfileBinding.toolbar);
        sp = getSharedPreferences("utils", MODE_PRIVATE);
        editor = sp.edit();

//        pdialog = new ProgressDialog(this);
//        pdialog.setCancelable(false);
//        pdialog.setMessage("Setting Stream");
//        pdialog.show();

        MaterialDialog.Builder b = new MaterialDialog.Builder(this)
                .title("Loading Data")
                .content("please wait")
                .progress(true, 0)
                .cancelable(false)
                .progressIndeterminateStyle(true);
        dialog = b.build();
        dialog.show();

        list_of_countries = new ArrayList<>();

        StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/country.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("country", response);
                        dialog.setTitle("Loading Profile");
                        try {
                            Log.e("con", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("FullData");
                            countries = new String[jsonArray.length()];
                            for(int t = 0; t < jsonArray.length(); t++){
                                JSONObject jObject = jsonArray.getJSONObject(t);
                                countries[t] = (jObject.getString("country"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateProfile.this, android.R.layout.simple_list_item_1, countries);
                            activityUpdateProfileBinding.content.inputCountry.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadProfile();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                        }

                        if (error instanceof TimeoutError) {
                            Log.e("Volley", "TimeoutError " + error.toString());
                        }else if(error instanceof NoConnectionError){
                            Log.e("Volley", "NoConnectionError");
                        } else if (error instanceof AuthFailureError) {
                            Log.e("Volley", "AuthFailureError");
                        } else if (error instanceof ServerError) {
                            Log.e("Volley", "ServerError");
                        } else if (error instanceof NetworkError) {
                            Log.e("Volley", "NetworkError");
                        } else if (error instanceof ParseError) {
                            Log.e("Volley", "ParseError");
                        }
                    }
                }){
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);

        activityUpdateProfileBinding.content.inputCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadUniversity(activityUpdateProfileBinding.content.inputCountry.getText().toString());
                MaterialDialog.Builder b = new MaterialDialog.Builder(UpdateProfile.this)
                        .title("Loading Alumni")
                        .content("please wait")
                        .progress(true, 0)
                        .cancelable(false)
                        .progressIndeterminateStyle(true);
                dialog = b.build();
                dialog.show();
            }
        });

        activityUpdateProfileBinding.content.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activityUpdateProfileBinding.content.inputAlumni.getText().toString().equals("")){
                    if(!activityUpdateProfileBinding.content.inputDob.getText().toString().equals("")){
                        StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/updateProfile.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("update", response);
                                        if(response.equals("suc")){
                                            editor.putString("alumni", activityUpdateProfileBinding.content.inputAlumni.getText().toString());
                                            editor.commit();
                                            Snackbar snackbar = Snackbar
                                                    .make(activityUpdateProfileBinding.content.button3, "Profile Updated Successfully", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("OK", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            onBackPressed();
                                                        }
                                                    });

                                            snackbar.show();
                                            //Toast.makeText(UpdateProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                            //onBackPressed();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null) {
                                            Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                                        }

                                        if (error instanceof TimeoutError) {
                                            Log.e("Volley", "TimeoutError " + error.toString());
                                        }else if(error instanceof NoConnectionError){
                                            Log.e("Volley", "NoConnectionError");
                                        } else if (error instanceof AuthFailureError) {
                                            Log.e("Volley", "AuthFailureError");
                                        } else if (error instanceof ServerError) {
                                            Log.e("Volley", "ServerError");
                                        } else if (error instanceof NetworkError) {
                                            Log.e("Volley", "NetworkError");
                                        } else if (error instanceof ParseError) {
                                            Log.e("Volley", "ParseError");
                                        }
                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams(){
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("alumni", activityUpdateProfileBinding.content.inputAlumni.getText().toString());
                                params.put("dob", activityUpdateProfileBinding.content.inputDob.getText().toString());
                                if(!activityUpdateProfileBinding.content.inputAbout.getText().toString().equals("")){
                                    params.put("proSum", activityUpdateProfileBinding.content.inputAbout.getText().toString());
                                }
                                params.put("email", sp.getString("email", ""));
                                Log.e("email", sp.getString("email", "asdf"));
                                return params;
                            }
                        };
                        sr.setRetryPolicy(new DefaultRetryPolicy(
                                180000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueue requestQueue = Volley.newRequestQueue(UpdateProfile.this);
                        requestQueue.add(sr);
                    }else{

                    }
                }else{

                }
            }
        });

    }

    private void loadUniversity(final String country){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/uni.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("profile", response);
                        dialog.dismiss();
                        JSONObject jsonObject = null;
                        try {
                            Log.e("uni", country);
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("FullData");
                            uni = new String[jsonArray.length()];
                            for(int t = 0; t < jsonArray.length(); t++){
                                JSONObject jObject = jsonArray.getJSONObject(t);
                                uni[t] = (jObject.getString("uni"));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateProfile.this, android.R.layout.simple_list_item_1, uni);
                            activityUpdateProfileBinding.content.inputAlumni.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                        }

                        if (error instanceof TimeoutError) {
                            Log.e("Volley", "TimeoutError " + error.toString());
                        }else if(error instanceof NoConnectionError){
                            Log.e("Volley", "NoConnectionError");
                        } else if (error instanceof AuthFailureError) {
                            Log.e("Volley", "AuthFailureError");
                        } else if (error instanceof ServerError) {
                            Log.e("Volley", "ServerError");
                        } else if (error instanceof NetworkError) {
                            Log.e("Volley", "NetworkError");
                        } else if (error instanceof ParseError) {
                            Log.e("Volley", "ParseError");
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("con", country);
                Log.e("email", sp.getString("email", "asdf"));
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);
    }

    private void loadProfile(){
        StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/profile.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("profile", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("FullData");
                            JSONObject jObject = jsonArray.getJSONObject(0);
                            Picasso.with(UpdateProfile.this).load(jObject.getString("propic")).fit().centerCrop().into(activityUpdateProfileBinding.content.proPic);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateProfile.this, android.R.layout.simple_list_item_1, countries);
                            activityUpdateProfileBinding.content.inputCountry.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null) {
                            Log.e("Volley", "Error. HTTP Status Code:"+networkResponse.statusCode);
                        }

                        if (error instanceof TimeoutError) {
                            Log.e("Volley", "TimeoutError " + error.toString());
                        }else if(error instanceof NoConnectionError){
                            Log.e("Volley", "NoConnectionError");
                        } else if (error instanceof AuthFailureError) {
                            Log.e("Volley", "AuthFailureError");
                        } else if (error instanceof ServerError) {
                            Log.e("Volley", "ServerError");
                        } else if (error instanceof NetworkError) {
                            Log.e("Volley", "NetworkError");
                        } else if (error instanceof ParseError) {
                            Log.e("Volley", "ParseError");
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", sp.getString("email", ""));
                Log.e("email", sp.getString("email", "asdf"));
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);
    }

}
