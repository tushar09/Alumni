package tech.triumphit.alumni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Environment;

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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tech.triumphit.alumni.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private CallbackManager callbackManager;
    String id;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        List< String > permissionNeeds = Arrays.asList("user_photos", "email",
                "user_birthday", "public_profile");
        binding.sign.logbutton.setReadPermissions(permissionNeeds);
        binding.toolbar.setTitle("Alumni Sign Up");
//        setContentView(R.layout.activity_sign_up);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        sp = getSharedPreferences("utils", MODE_PRIVATE);
        editor = sp.edit();

        callbackManager = CallbackManager.Factory.create();

        binding.sign.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
            }
        });

        binding.sign.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String main_data[] = {"data1", "is_primary", "data3", "data2", "data1", "is_primary", "photo_uri", "mimetype"};
                Object object = getContentResolver().query(Uri.withAppendedPath(android.provider.ContactsContract.Profile.CONTENT_URI, "data"),
                        main_data, "mimetype=?",
                        new String[]{"vnd.android.cursor.item/phone_v2"},
                        "is_primary DESC");
                if (object != null) {
                    do {
                        if (!((Cursor) (object)).moveToNext())
                            break;
                        String s1 = ((Cursor) (object)).getString(4);
                        Log.e("number", s1);
                    } while (true);
                    ((Cursor) (object)).close();
                }
                binding.sign.logbutton.performClick();
            }
        });

        binding.sign.logbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("onSuccess");

                String accessToken = loginResult.getAccessToken()
                        .getToken();
                Log.e("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {@Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {

                            Log.e("LoginActivity",
                                    response.toString());
                            try {
                                id = object.getString("id");
                                try {
                                    URL profile_pic = new URL(
                                            "https://graph.facebook.com/" + id + "/picture?type=large");
                                    Log.e("profile_pic",
                                            profile_pic + "");
                                    Picasso.with(SignUp.this).load("https://graph.facebook.com/" + id + "/picture?width=9999").fit().centerCrop().into(binding.pro);

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                String name = object.getString("first_name");
                                binding.sign.inputName.setText(name);
                                TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                                String mPhoneNumber = tMgr.getLine1Number();
                                binding.sign.inputPhone.setText(mPhoneNumber);
                                String email = object.getString("email");
                                binding.sign.inputEmail.setText(email);
                                String gender = object.getString("gender");
                                String birthday = object.getString("birthday");
                                Log.e("FB info", name + " " + email + " " + gender + " " + birthday);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("FB Error", e.toString());
                            }
                        }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("FB Exception", error.toString());
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    MaterialDialog dialog;
    private void checkValidity() {
        if(!binding.sign.inputName.getText().toString().equals("")
                && !binding.sign.inputEmail.getText().toString().equals("")
                && !binding.sign.inputPassword.getText().toString().equals("")
                && !binding.sign.inputPasswordConfirm.getText().toString().equals("")){

            if(binding.sign.inputPassword.getText().toString().equals(binding.sign.inputPasswordConfirm.getText().toString())){
                if(validate(binding.sign.inputEmail.getText().toString())){

                    StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/signup.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(SignUp.this, response, Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    if(response.startsWith("Account created successfully")){
                                        editor.putString("name", binding.sign.inputName.getText().toString());
                                        BitmapDrawable drawable = (BitmapDrawable) binding.pro.getDrawable();
                                        Bitmap bitmap = drawable.getBitmap();
                                        String pic = storeImage(bitmap);
                                        String picSecondary = response.replace("Account created successfully----------", "");
                                        editor.putString("pic", pic);
                                        editor.putString("email", binding.sign.inputEmail.getText().toString());
                                        editor.putString("picSecondary", picSecondary);
                                        Log.e("Pic path", pic);
                                        Log.e("picSecondary path", picSecondary);
                                        editor.commit();
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
                            params.put("name", binding.sign.inputName.getText().toString());
                            params.put("email", binding.sign.inputEmail.getText().toString());
                            params.put("pass", binding.sign.inputPassword.getText().toString());
                            params.put("phone", binding.sign.inputPhone.getText().toString());
                            params.put("propic", getPropicBase64(binding.pro));
                            Log.e("email", binding.sign.inputEmail.getText().toString() + "adf");
                            return params;
                        }
                    };
                    sr.setRetryPolicy(new DefaultRetryPolicy(
                            180000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(sr);

                    MaterialDialog.Builder b = new MaterialDialog.Builder(this)
                            .title("Creating Account")
                            .content("please wait")
                            .progress(true, 0)
                            .cancelable(false)
                            .progressIndeterminateStyle(true);
                    dialog = b.build();
                    dialog.show();
                }else{
                    Toast.makeText(this, "Email address is not right", Toast.LENGTH_LONG).show();
                }

            }else{
                Toast.makeText(this, "Password did not match", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Please fill all fields with *", Toast.LENGTH_LONG).show();
        }
    }

    private String storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.e("error",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return "error";
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("error", "Error accessing file: " + e.getMessage());
        }
        return pictureFile.getPath();
    }

    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private String getPropicBase64(ImageView pro) {
        BitmapDrawable drawable = (BitmapDrawable) pro.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

}
