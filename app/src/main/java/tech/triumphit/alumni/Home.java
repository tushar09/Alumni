package tech.triumphit.alumni;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.tool.DataBinder;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.triumphit.alumni.databinding.DrawerHolderBinding;

public class Home extends AppCompatActivity {

    @BindView(R.id.textView3)TextView name;
    @BindView(R.id.textView4)TextView email;
    @BindView(R.id.imageView2)ImageView profile;
    @BindView(R.id.textView5)TextView industry;
    @BindView(R.id.textView6)TextView company;
    @BindView(R.id.textView7)TextView title;
    @BindView(R.id.textView8)TextView startDate;
    @BindView(R.id.textView9)TextView summary;

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    DrawerHolderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.drawer_holder);
        setSupportActionBar(binding.toolbar);

        //String url = "https://api.linkedin.com/v1/people/~:(email-address, formatted-name, phone-number, public-profile-url, picture-url, picture-urls::(original))";
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,headline,picture-urls::(original),industry,summary,specialties,positions:(id,title,summary,start-date,end-date,is-current,company:(id,name,type,size,industry,ticker)),educations:(id,school-name,field-of-study,start-date,end-date,degree,activities,notes),associations,interests,num-recommenders,date-of-birth,publications:(id,title,publisher:(name),authors:(id,name),date,url,summary),patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(id,name),date,url),languages:(id,language:(name),proficiency:(level,name)),skills:(id,skill:(name)),certifications:(id,name,authority:(name),number,start-date,end-date),courses:(id,name,number),recommendations-received:(id,recommendation-type,recommendation-text,recommender),honors-awards,three-current-positions,three-past-positions,volunteer)";

        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.app_name,
                R.string.app_name);
        binding.drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
        binding.shitstuff.setItemIconTintList(null);

    }

}
