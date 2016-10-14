package tech.triumphit.alumni;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.tool.util.L;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.BoolRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.tool.DataBinder;

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
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.login.LoginManager;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.triumphit.alumni.adapter.HomeNewsFeed;
import tech.triumphit.alumni.databinding.DrawerHolderBinding;
import tech.triumphit.alumni.databinding.NavDrawerHeaderBinding;

public class Home extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.textView3)
    TextView name;
    @BindView(R.id.textView4)
    TextView email;
    @BindView(R.id.imageView2)
    ImageView profile;
    @BindView(R.id.textView5)
    TextView industry;
    @BindView(R.id.textView6)
    TextView company;
    @BindView(R.id.textView7)
    TextView title;
    @BindView(R.id.textView8)
    TextView startDate;
    @BindView(R.id.textView9)
    TextView summary;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    DrawerHolderBinding binding;
    NavDrawerHeaderBinding navDrawerHeaderBinding;
    private AlertDialog pd;
    Animation animDown;
    private Animation animDownPostButon, animDownPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.drawer_holder);
        setSupportActionBar(binding.toolbar);


        sp = getSharedPreferences("utils", MODE_PRIVATE);
        editor = sp.edit();

//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setTitle("sdaf");
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();


        //String url = "https://api.linkedin.com/v1/people/~:(email-address, formatted-name, phone-number, public-profile-url, picture-url, picture-urls::(original))";
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,headline,picture-urls::(original),industry,summary,specialties,positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,school-name,field-of-study,start-date,end-date,degree,activities,notes),associations,interests,num-recommenders,date-of-birth,publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)";

        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.app_name,
                R.string.app_name);
        binding.drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        navDrawerHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.nav_drawer_header, binding.shitstuff, true);
        navDrawerHeaderBinding.textView12.setText(sp.getString("name", "Not Found"));
        Picasso.with(this).load(new File(sp.getString("pic", ""))).into(navDrawerHeaderBinding.profileImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.e("From file", "Success");
            }

            @Override
            public void onError() {
                Picasso.with(Home.this).load("http://triumphit.tech/project_alumni/images/" + sp.getString("email", "") + ".jpeg").into(navDrawerHeaderBinding.profileImage);
                Log.e("From file", "Not Success " + "http://triumphit.tech/project_alumni/images/" + sp.getString("email", "") + ".jpeg");
            }
        });
        binding.shitstuff.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.update) {
                    startActivity(new Intent(Home.this, UpdateProfile.class));
                }
                return false;
            }
        });

        binding.postPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Home.this)
                        .setTitle("Photo")
                        .setMessage("Take Photo from camera or folder")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                startActivityForResult(intent, 420);
                            }
                        })
                        .setNegativeButton("Folder", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ArrayList<Image> images = new ArrayList<Image>();
                                ImagePicker.create(Home.this)
                                        .folderMode(true) // folder mode (false by default)
                                        .folderTitle("Folder") // folder selection title
                                        .imageTitle("Tap to select") // image selection title
                                        .single() // single mode
                                        .multi() // multi mode (default mode)
                                        .limit(1) // max images can be selected (99 by default)
                                        .showCamera(false) // show camera or not (true by default)
                                        .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
                                        .origin(images) // original selected images, used in multi mode
                                        .start(500); // start image picker activity with request code
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        binding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.getString("alumni", "non").equals("non")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    builder.append("Update your profile first").append("  ");
                    builder.setSpan(new ImageSpan(Home.this, R.drawable.emoticon_sad), builder.length() - 1, builder.length(), 0);
                    Snackbar snackbar = Snackbar
                            .make(binding.postButton, builder, Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Home.this, UpdateProfile.class));
                                }
                            });
                    snackbar.show();
                } else {
                    if (b == null && binding.post.getText().toString().equals("")) {
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append("Your thought is empty? How?").append("  ");
                        builder.setSpan(new ImageSpan(Home.this, R.drawable.emoticon_poop), builder.length() - 1, builder.length(), 0);
                        //builder.append(" next message");
                        Snackbar snackbar = Snackbar
                                .make(binding.postButton, builder, Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                        snackbar.show();

                    } else {
                        StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/setFeeds.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("match", response);
                                        binding.loadingView.smoothToHide();
                                        animatePosterInvisible();
                                        anim = true;
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        NetworkResponse networkResponse = error.networkResponse;
                                        if (networkResponse != null) {
                                            Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                                        }

                                        if (error instanceof TimeoutError) {
                                            Log.e("Volley", "TimeoutError " + error.toString());
                                        } else if (error instanceof NoConnectionError) {
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
                                }) {
                            @Override
                            protected Map<String, String> getParams() {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                                String currentDateandTime = sdf.format(new Date());
                                Log.e("date", currentDateandTime);
                                String postPic = "";
                                if (b != null) {
                                    //b = getResizedBitmap(b, 500);
                                    Log.e("here for b", currentDateandTime);
                                    postPic = getPropicBase64(b);
                                    Log.e("here for b base", postPic);
                                    b = null;
                                }
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("email", sp.getString("email", "non"));
                                params.put("alumni", sp.getString("alumni", "non"));
                                params.put("date", currentDateandTime);
                                if (postPic.equals("")) {
                                    params.put("postpic", "not");
                                    Log.e("postpic", "postPic null");
                                } else {
                                    params.put("postpic", postPic);
                                }
                                params.put("posttext", binding.post.getText().toString());

                                Log.e("email", sp.getString("email", "asdf"));
                                return params;
                            }
                        };
                        sr.setRetryPolicy(new DefaultRetryPolicy(
                                180000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueue requestQueue = Volley.newRequestQueue(Home.this);
                        requestQueue.add(sr);
                        binding.loadingView.smoothToShow();
                    }
                }

            }
        });

        binding.lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    Log.e("SCROLLING DOWN", "TRUE");
                    //animatePosterInvisible();
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    Log.e("SCROLLING UP", "TRUE");
                    //animatePosterVisible();
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.swipeRefreshLayout.setRefreshing(true);
                                        getFeed();
                                    }
                                }
        );

    }

    private void animatePosterVisible() {
        binding.ril.setVisibility(View.VISIBLE);
        animDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);
        animDownPost = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_child);
        animDownPostButon = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_child);
        animDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                binding.post.startAnimation(animDownPost);
                binding.postButton.startAnimation(animDownPostButon);
                binding.postPic.startAnimation(animDownPostButon);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        binding.ril.startAnimation(animDown);
    }
    private void animatePosterInvisible() {
            animDown = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            animDownPost = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up_child);
            animDownPostButon = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up_child);
            animDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    binding.post.startAnimation(animDownPost);
                    binding.postButton.startAnimation(animDownPostButon);
                    binding.postPic.startAnimation(animDownPostButon);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //binding.ril.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            binding.ril.startAnimation(animDown);

    }

    Bitmap b = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 500 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            try {
                b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file://" + images.get(0).getPath()));
            } catch (IOException e) {
                Log.e("b", e.toString());
                Log.e("b path", images.get(0).getPath());
                b = null;
            }
            Glide.with(Home.this).load(images.get(0).getPath()).asBitmap().animate(R.anim.slide_in_right).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imageView11);
            //Picasso.with(Home.this).load(images.get(0).getPath()).into(binding.imageView11);
            // do your logic ....
        }
        if (requestCode == 420) {
//            try {
//                b = (Bitmap) data.getExtras().get("data");
//                Bitmap bitmap = b;
//                //binding.imageView11.setImageBitmap(photo);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                Glide.with(Home.this).load(byteArrayOutputStream.toByteArray()).animate(R.anim.slide_in_right).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imageView11);
//            } catch (Exception e) {
//                Log.e("b 420", e.toString());
//                b = null;
//            }
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            b = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
            Bitmap bitmap = b;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            Glide.with(Home.this).load(byteArrayOutputStream.toByteArray()).asBitmap().animate(R.anim.slide_in_right).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imageView11);

        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private String getPropicBase64(Bitmap b) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void getFeed() {

        if (sp.getString("alumni", "non").equals("non") || sp.getString("alumni", "non").equals("")) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("Update your profile first").append("  ");
            builder.setSpan(new ImageSpan(Home.this, R.drawable.emoticon_sad), builder.length() - 1, builder.length(), 0);
            Snackbar snackbar = Snackbar
                    .make(binding.postButton, builder, Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Home.this, UpdateProfile.class));
                        }
                    });
            snackbar.show();
        } else {
            final ArrayList name, date, propic, postPic, postText, totalLike, likerProPic;

            name = new ArrayList();
            date = new ArrayList();
            propic = new ArrayList();
            postPic = new ArrayList();
            postText = new ArrayList();
            totalLike = new ArrayList();
            likerProPic = new ArrayList();

            StringRequest sr = new StringRequest(Request.Method.POST, "http://triumphit.tech/project_alumni/getFeeds.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jo;
                            try {
                                jo = new JSONObject(response);
                                JSONArray jr = jo.getJSONArray("FullData");
                                Log.e("error", jr.length() + "");
                                for (int t = 0; t < jr.length(); t++) {
                                    JSONObject jsonObject = jr.getJSONObject(t);
                                    name.add("" + jsonObject.getString("name"));
                                    date.add(jsonObject.getString("date"));
                                    propic.add("http://triumphit.tech/project_alumni/images/" + jsonObject.getString("email") + ".jpeg");
                                    postPic.add(jsonObject.getString("postpic"));
                                    postText.add(jsonObject.getString("posttext"));
                                    totalLike.add(jsonObject.getInt("totalLike"));
                                    likerProPic.add(jsonObject.getString("likerPropic"));
                                    Log.e("error", jsonObject.getString("likerPropic"));
                                }

                                binding.lv.setAdapter(new HomeNewsFeed(Home.this, name, postPic, date, propic, postText, totalLike, likerProPic));
                                binding.swipeRefreshLayout.setRefreshing(false);

                            } catch (JSONException e) {
                                Log.e("error", e.toString());
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null) {
                                Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                            }

                            if (error instanceof TimeoutError) {
                                Log.e("Volley", "TimeoutError " + error.toString());
                            } else if (error instanceof NoConnectionError) {
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
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("alumni", sp.getString("alumni", "non"));
                    return params;
                }
            };
            sr.setRetryPolicy(new DefaultRetryPolicy(
                    180000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(Home.this);
            requestQueue.add(sr);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);
        return true;
    }


    boolean anim = true;
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.postHomeMenu){
            if(anim){
                animatePosterVisible();
                anim = false;
                Log.e("should animante ", "shw");
            }else{
                animatePosterInvisible();
                Log.e("should animante ", "dnt");
                anim = true;
            }
        }
        return true;
    }

    @Override
    public void onRefresh() {
        getFeed();
    }
}
