package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RupayHomepageActivity extends AppCompatActivity {
    TextView rupayHomepageAgentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_homepage);
        rupayHomepageAgentId = (TextView) findViewById (R.id.textViewRupayHomepageAgentId);

        setTitle ("Rupay");

        Intent intent = getIntent ();
        String rupayAgentId= intent.getStringExtra ("homepageAgentId");
        rupayHomepageAgentId.setText (rupayAgentId);
    }

    public void getBalanceEnquiryPage(View view){
        Intent intent = new Intent (this, RupayBalanceEnquiryActivity.class);
        startActivity (intent);
    }

    public void getWithdrawPage(View view){
        Intent intent = new Intent (this, RupayWithdrawActivity.class);
        intent.putExtra ("rupayAgentId", rupayHomepageAgentId.getText ().toString ());
        startActivity (intent);
    }

    public void getMiniStatementPage(View view){
        Intent intent = new Intent (this, RupayMiniStatementActivity.class);
        startActivity (intent);
    }

}
