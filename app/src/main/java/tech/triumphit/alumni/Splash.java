package tech.triumphit.alumni;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {

    @BindView(R.id.imageView)ImageView logo;
    @BindView(R.id.textView)TextView alumni;
    @BindView(R.id.textView2)TextView aSociety;
    private AccessTokenTracker accessTokenTracker;

    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("utils", MODE_PRIVATE);
        editor = sp.edit();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        ButterKnife.bind(this);

        Animation ufb = AnimationUtils.loadAnimation(this, R.anim.up_from_bottom);
        Animation dft = AnimationUtils.loadAnimation(this, R.anim.down_from_top);

        logo.startAnimation(dft);
        aSociety.startAnimation(ufb);
        alumni.startAnimation(ufb);



        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                if(sp.getBoolean("loggedin", false)){
                    Intent i = new Intent(Splash.this, Home.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

//                try {
//                    PackageInfo info = getPackageManager().getPackageInfo(
//                            "tech.triumphit.alumni",
//                            PackageManager.GET_SIGNATURES);
//                    for (Signature signature : info.signatures) {
//                        MessageDigest md = MessageDigest.getInstance("SHA");
//                        md.update(signature.toByteArray());
//                        Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.NO_WRAP));
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//
//                } catch (NoSuchAlgorithmException e) {
//
//                }

                finish();
            }
        }, 5000);
//
//
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, Home.class);
                    startActivity(i);

                    finish();
                }
            }, 5000);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);

                    finish();
                }
            }, 5000);
        }
    }

}
