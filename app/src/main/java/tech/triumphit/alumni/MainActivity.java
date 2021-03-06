package tech.triumphit.alumni;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import tech.triumphit.alumni.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.input_email) EditText input_email;
    @BindView(R.id.profile_image) CircleImageView profile_image;
    @BindView(R.id.input_password) EditText input_password;
    @BindView(R.id.signup) Button signUp;

    private CallbackManager callbackManager;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    ActivityMainBinding binding;

    @Override
    protected void onStart() {
        super.onStart();
        Picasso.with(this).load(new File(sp.getString("pic", "damn"))).into(binding.content.profileImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.e("From file", "Success");
            }

            @Override
            public void onError() {
                Picasso.with(MainActivity.this).load(new File(sp.getString("picSecondary", "damn"))).into(binding.content.profileImage);
                Log.e("From file", "Not Success");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.toolbar.setTitle("Alumni Login");

        ButterKnife.bind(this);

        sp = getSharedPreferences("utils", MODE_PRIVATE);
        editor = sp.edit();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });
        binding.content.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.content.checkBox.isChecked()){


                new MaterialDialog.Builder(MainActivity.this)
                        .title("Alumni")
                        .content("Are you sure to keep you logged in?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                binding.content.checkBox.setChecked(true);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                binding.content.checkBox.setChecked(false);
                            }
                        })
                        .show();
                }
            }
        });


        binding.content.textView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.content.inputEmail.getText().toString().equals("") && !binding.content.inputPassword.getText().toString().equals("") ){
                    StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/login.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                                    if(response.startsWith("we are good")){
                                        if(binding.content.checkBox.isChecked()){
                                            editor.putBoolean("loggedin", true);
                                            editor.putString("email", binding.content.inputEmail.getText().toString());
                                            editor.commit();
                                        }

                                        editor.putString("email", binding.content.inputEmail.getText().toString());
                                        response = response.replace("we are good", "");
                                        Log.e("aluj", response.replace("we are good", ""));
                                        if(!response.equals("")){
                                            editor.putString("alumni", response);
                                        }else{
                                            editor.putString("alumni", "non");
                                        }
                                        editor.commit();
                                        startActivity(new Intent(MainActivity.this, Home.class));
                                    }
                                    Log.e("php error", response);
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

                            params.put("email", binding.content.inputEmail.getText().toString());
                            params.put("pass", binding.content.inputPassword.getText().toString());

                            return params;
                        }
                    };
                    sr.setRetryPolicy(new DefaultRetryPolicy(
                            180000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    requestQueue.add(sr);
                }else{
                    Toast.makeText(MainActivity.this, "Pelase fill up all the field", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.content.inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("here", "focus gained");
                Glide.with(MainActivity.this).load("http://triumphit.tech/project_alumni/images/" + binding.content.inputEmail.getText().toString() + ".jpeg").diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(binding.content.profileImage);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
