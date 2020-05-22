package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgentBalanceActivity extends MySessionActivity {

    TextView agentId;
    TextView agentAccountNumber;
    TextView agentAadharNumber;
    TextView fingerprintString;
    Button agentBalanceButton;
    ImageButton fingerprint;

    String agentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_agent_balance);

        agentId = (TextView) findViewById (R.id.textViewAgentId);
        agentAccountNumber = (TextView) findViewById (R.id.textViewAgentAccountNumber);
        fingerprintString = (TextView) findViewById (R.id.textViewAgentBalanceFingerprint);
        agentBalanceButton = (Button) findViewById (R.id.agentBalaceEnquiryButton);
        fingerprint = (ImageButton) findViewById (R.id.imageButtonAgentBalanceFingerprint);
        agentAadharNumber = (TextView) findViewById (R.id.textViewAgentAadharNumber);

        Intent intent = getIntent ();
        final String agentID = intent.getStringExtra ("homepageAgentId");
        agentId.setText (agentID);

        getAgentDetails ();

        fingerprint.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                getAadharDetails ();
            }
        });

        agentBalanceButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(fingerprintString.getText ().toString () == "") {
                    Toast.makeText (AgentBalanceActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
                } else {
                    getAgentBalance ();
                }
            }
        });


        agentAccountNumber.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void getAgentDetails(){
            Retrofit retrofit = new Retrofit.Builder ().
                    baseUrl("http://192.168.42.37:8080/Mint/")
                    .addConverterFactory (GsonConverterFactory.create ())
                    .build ();
            AgentAccountDetailsApi agentAccountDetailsApi = retrofit.create (AgentAccountDetailsApi.class);

            Intent intent = getIntent ();
            final String agentId = intent.getStringExtra ("homepageAgentId");

            Call<Account> aCall = agentAccountDetailsApi.getAgentDetails (agentId);

            aCall.enqueue (new Callback<Account> () {
                @Override
                public void onResponse(Call<Account> aCall, Response<Account> response) {
                    if (!response.isSuccessful ()){
                        Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                        return;
                    }

                    Account details = response.body ();

                    // name.setText (balance.getCustomerName ());
                    agentAadharNumber.setText (details.getAadharNumber ());
                    agentAccountNumber.setText (details.getAccountNumber ());
                }

                @Override
                public void onFailure(Call<Account> aCall, Throwable t) {
                    Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
                }
            });
    }


    public void getAadharDetails(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl("http://192.168.42.37:8080/AadharApi/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        AadharApi aadharApi = retrofit.create (AadharApi.class);

        final String aadharNo = agentAadharNumber.getText ().toString ();


        Call<Aadhar> aCall = aadharApi.getAadharDetails (aadharNo);

        aCall.enqueue (new Callback<Aadhar> () {
            @Override
            public void onResponse(Call<Aadhar> aCall, Response<Aadhar> response) {
                if (!response.isSuccessful ()){
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                Aadhar details = response.body ();
                if(details.getAadharNumber ().equals (aadharNo)) {
                    fingerprintString.setText (details.getFingerprint ());
                    agentName = details.getName ();
                }else{
                    Toast.makeText (getApplicationContext (), "Enter a valid Aadhar Number", Toast.LENGTH_LONG).show ();
                }
            }

            @Override
            public void onFailure(Call<Aadhar> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), "Enter a Valid Aadhar Number", Toast.LENGTH_LONG).show ();
            }
        });
    }

    public void getAgentBalance(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl("http://192.168.42.37:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        AgentAccountDetailsApi agentAccountDetailsApi = retrofit.create (AgentAccountDetailsApi.class);

        final Intent intent = getIntent ();
        final String agentId = intent.getStringExtra ("homepageAgentId");

        Call<Account> aCall = agentAccountDetailsApi.getAgentDetails (agentId);

        aCall.enqueue (new Callback<Account> () {
            @Override
            public void onResponse(Call<Account> aCall, Response<Account> response) {
                if (!response.isSuccessful ()){
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                Account details = response.body ();
                    String name = agentName;
                    String accountNo = details.getAccountNumber ();
                    String balance = details.getBalance ().toString ();

                    Intent intent1 = new Intent (getApplicationContext (), AgentBalanceOutput.class);
                    intent1.putExtra ("name", name);
                    intent1.putExtra ("accountNo", accountNo);
                    intent1.putExtra ("balance", balance);
                    startActivity (intent1);
            }

            @Override
            public void onFailure(Call<Account> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }
}
