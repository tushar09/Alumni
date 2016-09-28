package tech.triumphit.alumni;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class Regestration extends AppCompatActivity {

    @BindView(R.id.editText3) EditText name;
    @BindView(R.id.editText4) EditText email;
    @BindView(R.id.editText5) EditText phone;
    @BindView(R.id.editText6) EditText pass;
    @BindView(R.id.editText7) EditText conPass;
    @BindView(R.id.proPic) CircleImageView civ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(getIntent().getStringExtra("name") != null){
            name.setText(getIntent().getStringExtra("name"));
            email.setText(getIntent().getStringExtra("email"));
            Picasso.with(this).load(getIntent().getStringExtra("pic")).into(civ);
        }

    }

}
