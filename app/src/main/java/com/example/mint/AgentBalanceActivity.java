package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

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

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AgentBalanceActivity extends MySessionActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


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

//        fingerprint.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                getAadharDetails ();
//            }
//        });

       // getAadharDetails ();

        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (AgentBalanceActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback () {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError (errorCode, errString);
                Toast.makeText (getApplicationContext (),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show ();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded (result);

                //Custom logic
                getAgentBalance ();

                Toast.makeText (getApplicationContext (),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show ();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed ();
                Toast.makeText (getApplicationContext (), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show ();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder ()
                .setTitle ("Biometric Authentication for Mint")
                .setSubtitle ("Log in using your biometric credential")
                .setNegativeButtonText ("Use account password")
                .build ();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        agentBalanceButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate (promptInfo);
            }
        });
    }

    public void getAgentDetails(){
            Retrofit retrofit = new Retrofit.Builder ().
                    baseUrl("http://192.168.42.20:8080/Mint/")
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
                baseUrl("http://192.168.42.20:8080/AadharApi/")
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
                    agentName = details.getName ();

            }

            @Override
            public void onFailure(Call<Aadhar> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }


    public void getAgentBalance(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl("http://192.168.42.20:8080/Mint/")
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
                    intent1.putExtra ("name", "Rohan Kumar Das");
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
