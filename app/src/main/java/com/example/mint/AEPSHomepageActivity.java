package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AEPSHomepageActivity extends MySessionActivity {
    TextView aepsHomepageAgentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_aepshomepage);
        aepsHomepageAgentId = (TextView) findViewById (R.id.textViewAepsHomepageAgentId);

        Intent intent = getIntent ();
        String aepsAgentId= intent.getStringExtra ("homepageAgentId");
        aepsHomepageAgentId.setText (aepsAgentId);


    }


    public void getMinistatement(View view){
        Intent intent = new Intent (this,MinistatementActivity.class);
        startActivity (intent);
    }

    public void getFundsTransfer(View view){
        Intent intent = new Intent (this, FundstransferActivity.class);
        String aepsAgentID = aepsHomepageAgentId.getText ().toString ();
        intent.putExtra ("fundTransferAgentId", aepsAgentID );
        startActivity (intent);
    }

    public void getBalance(View view){
        Intent intent = new Intent (this, BalanceEnquiryActivity.class);
        startActivity (intent);
    }

    public void getWithdrawPage(View view){
        String aepsAgentID = aepsHomepageAgentId.getText ().toString ();
        Intent intent = new Intent (this, WithdrawActivity.class);
        intent.putExtra ("withdrawAgentId", aepsAgentID );
        startActivity (intent);
    }

    public void getDepositPage(View view){
        String aepsAgentID = aepsHomepageAgentId.getText ().toString ();
        Intent intent = new Intent (this, DepositActivity.class);
        intent.putExtra ("depositAgentId", aepsAgentID );
        startActivity (intent);
    }
}
