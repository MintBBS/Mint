package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomepageActivity extends MySessionActivity {
    ImageView aeps;
    ImageView rupay;
    ImageView acOpening;
    ImageView scheme;
    ImageView mobileSeeding;
    ImageView agentBalance;
    ImageView eodReport;
    ImageView rrnStatus;

    TextView agentId;

    ActionBar actionBar;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView customTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_homepage);

        agentId = (TextView) findViewById (R.id.textViewHomepageAgentId) ;
        aeps  = (ImageView) findViewById (R.id.imageViewAeps);
        rupay = (ImageView) findViewById (R.id.imageViewRupay);
        acOpening = (ImageView) findViewById (R.id.imageViewAcct);
        scheme =  (ImageView) findViewById (R.id.imageViewScheme);
        mobileSeeding =  (ImageView) findViewById (R.id.imageViewMobile);
        agentBalance =  (ImageView) findViewById (R.id.imageViewAgent);
        eodReport =  (ImageView) findViewById (R.id.imageViewEod);
        rrnStatus =  (ImageView) findViewById (R.id.imageViewRrnStatus);


        actionBar = getSupportActionBar ();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setIcon (R.drawable.location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient (this);

        if (ActivityCompat.checkSelfPermission (HomepageActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }else{
            ActivityCompat.requestPermissions (HomepageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        Intent intent = getIntent ();
        String agentID = intent.getStringExtra ("agentId");
        agentId.setText (agentID);


        aeps.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getAepsHomepage ();
            }
        });

        rupay.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
               getRupayHomepage();
            }
        });

        agentBalance.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getAgentBalance ();
            }
        });

        rrnStatus.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getRrnStatus ();
            }
        });

        eodReport.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getEodReport ();
            }
        });

        scheme.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getSchemeHomePage ();
            }
        });

        mobileSeeding.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getMobileSeedingPage ();
            }
        });

        acOpening.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getAccountOpeningPage ();
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent (getApplicationContext (), MainActivity.class);
                        startActivity (intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    public void getAepsHomepage(){
        String agentID = agentId.getText ().toString ();
        Intent intent = new Intent (getApplicationContext (), AEPSHomepageActivity.class);
        intent.putExtra ("homepageAgentId", agentID);
        startActivity (intent);
    }

    public void getRupayHomepage(){
        String agentID = agentId.getText ().toString ();
        Intent intent = new Intent (getApplicationContext (), RupayHomepageActivity.class);
        intent.putExtra ("homepageAgentId", agentID);
        startActivity (intent);
    }

    public void getAgentBalance(){
        String agentID = agentId.getText ().toString ();
        Intent intent = new Intent (getApplicationContext (), AgentBalanceActivity.class);
        intent.putExtra ("homepageAgentId", agentID);
        startActivity (intent);
    }

    public void getRrnStatus(){
        Intent intent = new Intent (getApplicationContext (), RrnStatusActivity.class);
        startActivity (intent);
    }

    public void getEodReport(){
        Intent intent = new Intent (getApplicationContext (), EodActivity.class);
        startActivity (intent);
    }

    public void getSchemeHomePage(){
        Intent intent = new Intent (getApplicationContext (), SchemeHomepageActivity.class);
        startActivity (intent);
    }

    public void getMobileSeedingPage(){
        Intent intent = new Intent (getApplicationContext (), MobileSeedingActivity.class);
        startActivity (intent);
    }

    public void getAccountOpeningPage(){
        Intent intent = new Intent (getApplicationContext (), AccountOpeningActivity.class);
        startActivity (intent);
    }




    private void getLocation() {
        fusedLocationProviderClient.getLastLocation ().addOnCompleteListener (new OnCompleteListener<Location> () {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult ();
                if (location != null){
                    try {
                        Geocoder geocoder = new Geocoder (HomepageActivity.this
                                , Locale.getDefault ());

                        List<Address> addresses = geocoder.getFromLocation (
                                location.getLatitude (), location.getLongitude (), 1
                        );
                        actionBar.setTitle (" " + addresses.get (0).getSubAdminArea () + ", " + addresses.get (0).getAdminArea () + ", " + addresses.get (0).getPostalCode () );
                        } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            }
        });
    }
}
