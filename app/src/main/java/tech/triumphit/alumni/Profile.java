package tech.triumphit.alumni;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import tech.triumphit.alumni.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding activityProfileBinding;
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(activityProfileBinding.toolbar);

        sp = getSharedPreferences("utils", MODE_PRIVATE);
        editor = sp.edit();

        Picasso.with(this).load(new File(sp.getString("pic", "damn"))).into(activityProfileBinding.content.profileImage, new Callback() {
            @Override
            public void onSuccess() {
                Log.e("From file", "Success");
            }

            @Override
            public void onError() {
                Picasso.with(Profile.this).load(new File(sp.getString("picSecondary", "damn"))).into(activityProfileBinding.content.profileImage);
                Log.e("From file", "Not Success");
            }
        });

        activityProfileBinding.content.textView21.setText(sp.getString("name", ""));

    }

}
