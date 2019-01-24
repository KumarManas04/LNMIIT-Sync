package com.infinitysolutions.lnmiitsync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Developers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers);

        ImageView km = findViewById(R.id.km);
        ImageView st = findViewById(R.id.st);
        ImageView sn = findViewById(R.id.sn);
        ImageView aj = findViewById(R.id.aj);
        ImageView an = findViewById(R.id.an);
        km.setClipToOutline(true);
        st.setClipToOutline(true);
        sn.setClipToOutline(true);
        aj.setClipToOutline(true);
        an.setClipToOutline(true);
    }

    public void openLinkedIn(View view){
        String linkedInLink = "";
        switch (view.getId()){
            case R.id.km_linkedin:
                linkedInLink = "https://www.linkedin.com/in/kumar-manas-7b755b38/";
                break;
            case R.id.st_linkedin:
                linkedInLink = "https://www.linkedin.com/in/sourabh-tripathi-743472152/";
                break;
            case R.id.sn_linkedin:
                linkedInLink = "https://www.linkedin.com/in/subhajit-nandi/";
                break;
            case R.id.aj_linkedin:
                linkedInLink = "https://www.linkedin.com/in/akshat-jain-88434b152/";
                break;
            case R.id.an_linkedin:
                linkedInLink = "https://www.linkedin.com/in/anubhav-natani-127694143/";
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedInLink));
        startActivity(intent);
    }
}
