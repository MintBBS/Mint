package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

}
