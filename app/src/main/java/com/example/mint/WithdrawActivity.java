package com.example.mint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WithdrawActivity extends MySessionActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Spinner spinner;
    String[] bank = {"Choose Bank", "UCO"};

    List<String> spinnerlist;
    ArrayAdapter<String> arrayadapter;


    EditText withdrawAadharNumber;
    EditText withdrawAccountNumber;
    EditText withdrawAmount;
    Button buttonWithdraw;

    TextView textViewFingerprint;
    ImageButton imageButtonFIngerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_withdraw);

        withdrawAadharNumber = (EditText) findViewById (R.id.editTextwithdrawAadharNo);
        withdrawAccountNumber = (EditText) findViewById (R.id.editTextWithdrawAccountNo);
        withdrawAmount = (EditText) findViewById (R.id.editTextWithdrawAmount);
        buttonWithdraw = (Button) findViewById (R.id.buttonWithdraw);

//        imageButtonFIngerprint = (ImageButton) findViewById (R.id.imageButtonWithdrawFingerprint);
//        textViewFingerprint = (TextView) findViewById (R.id.textViewWithdrawFingerprint);


        spinner = (Spinner) findViewById (R.id.withdrawSpinner);
        spinnerlist = new ArrayList<> (Arrays.asList (bank));

        arrayadapter = new ArrayAdapter<String> (WithdrawActivity.this, R.layout.spinner_item_textview, spinnerlist) {


            //Creating the ArrayAdapter instance having the country list
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    //Disable the third item of spinner.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View spinnerview = super.getDropDownView (position, convertView, parent);
                TextView spinnertextview = (TextView) spinnerview;
                if (position == 0) {
                    //Set the disable spinner item color fade .
                    spinnertextview.setTextColor (Color.parseColor ("#9b9b9b"));
                } else {
                    spinnertextview.setTextColor (Color.BLACK);
                }
                return spinnerview;
            }
        };

        arrayadapter.setDropDownViewResource (R.layout.spinner_item_textview);
        spinner.setAdapter (arrayadapter);

//        imageButtonFIngerprint.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                if (withdrawAadharNumber.getText ().toString ().isEmpty ()) {
//                    withdrawAadharNumber.setError ("Enter a valid aadhar number ");
//                    withdrawAadharNumber.requestFocus ();
//                } else if (withdrawAadharNumber.getText ().toString ().length () < 12 || withdrawAadharNumber.getText ().toString ().length () > 12) {
//                    withdrawAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    withdrawAadharNumber.requestFocus ();
//                }else {
//                    getAadharDetails ();
//                }
//            }
//        });


// fingerprint authentication
        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (WithdrawActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback () {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError (errorCode, errString);
                Toast.makeText (getApplicationContext (),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show ();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded (result);

                //Custom logic
                withdrawMoney ();
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
                .setNegativeButtonText ("Cancel")
                .build ();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        buttonWithdraw.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (withdrawAadharNumber.getText ().toString ().isEmpty ()) {
                    withdrawAadharNumber.setError ("Enter a valid aadhar number ");
                    withdrawAadharNumber.requestFocus ();
                } else if (withdrawAadharNumber.getText ().toString ().length () < 12 || withdrawAadharNumber.getText ().toString ().length () > 12) {
                    withdrawAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    withdrawAadharNumber.requestFocus ();
                } else if (withdrawAccountNumber.getText ().toString ().isEmpty ()) {
                    withdrawAccountNumber.setError ("Please Enter Account Number  ");
                    withdrawAccountNumber.requestFocus ();
                } else if (withdrawAmount.getText ().toString ().isEmpty ()) {
                    withdrawAmount.setError ("Please Enter amount  ");
                    withdrawAmount.requestFocus ();
                } else if ((Double.parseDouble (withdrawAmount.getText ().toString ()) > 10000)) {

                    withdrawAmount.setError ("Maximum amount should be  Rs 10000 ");
                    withdrawAmount.requestFocus ();
                } else if ((Integer.parseInt (withdrawAmount.getText ().toString ()) < 100)) {
                    withdrawAmount.setError ("Minimum amount should be  Rs 100 ");
                    withdrawAmount.requestFocus ();}
                else {
                    biometricPrompt.authenticate (promptInfo);
                }
            }
        });


//        buttonWithdraw.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                if (withdrawAadharNumber.getText ().toString ().isEmpty ()) {
//                    withdrawAadharNumber.setError ("Enter a valid aadhar number ");
//                    withdrawAadharNumber.requestFocus ();
//                } else if (withdrawAadharNumber.getText ().toString ().length () < 12 || withdrawAadharNumber.getText ().toString ().length () > 12) {
//                    withdrawAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    withdrawAadharNumber.requestFocus ();
//                }
//                else if(withdrawAccountNumber.getText().toString().isEmpty() )
//                {
//                    withdrawAccountNumber.setError("Please Enter Account Number  ");
//                    withdrawAccountNumber.requestFocus();
//                }
//                else if(withdrawAmount.getText().toString().isEmpty() )
//                {
//                    withdrawAmount.setError("Please Enter amount  ");
//                    withdrawAmount.requestFocus();
//                }else if((Double.parseDouble(withdrawAmount.getText().toString())>10000)){
//
//                    withdrawAmount.setError("Maximum amount should be  Rs 10000 ");
//                    withdrawAmount.requestFocus();
//                }
//                else if (  (Integer.parseInt(withdrawAmount.getText().toString())<100)){
//                    withdrawAmount.setError("Minimum amount should be  Rs 100 ");
//                    withdrawAmount.requestFocus();
//                }
//                else if (textViewFingerprint.getText ().toString () == "") {
//                    Toast.makeText (WithdrawActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                }else
//                    withdrawMoney ();
//            }
//        });
    }

//    public void getAadharDetails(){
//        Retrofit retrofit = new Retrofit.Builder ().
//                baseUrl("http://192.168.42.37:8080/AadharApi/")
//                .addConverterFactory (GsonConverterFactory.create ())
//                .build ();
//        AadharApi aadharApi = retrofit.create (AadharApi.class);
//
//        final String aadharNo = withdrawAadharNumber.getText ().toString ();
//
//
//        Call<Aadhar> aCall = aadharApi.getAadharDetails (aadharNo);
//
//        aCall.enqueue (new Callback<Aadhar> () {
//            @Override
//            public void onResponse(Call<Aadhar> aCall, Response<Aadhar> response) {
//                if (!response.isSuccessful ()){
//                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
//                    return;
//                }
//
//                Aadhar details = response.body ();
//                if(details.getAadharNumber ().equals (aadharNo)) {
//
//                    textViewFingerprint.setText (details.getFingerprint ());
//                }else{
//                    Toast.makeText (getApplicationContext (), "Enter a valid aadhar number", Toast.LENGTH_SHORT).show ();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Aadhar> aCall, Throwable t) {
//                Toast.makeText (getApplicationContext (), "Please Enter Valid Aadhar Number", Toast.LENGTH_LONG).show ();
//            }
//        });
//}



        public void withdrawMoney () {
            Retrofit retrofit = new Retrofit.Builder ().
                    baseUrl ("http://192.168.42.20:8080/Mint/")
                    .addConverterFactory (GsonConverterFactory.create ())
                    .build ();
            WithdrawApi withdrawApi = retrofit.create (WithdrawApi.class);
            Intent intent1 = getIntent ();
            String withdrawOutputAgentId = intent1.getStringExtra ("withdrawAgentId");

            String aadharNo = withdrawAadharNumber.getText ().toString ();
            final String accountNo = withdrawAccountNumber.getText ().toString ();
            String amount = withdrawAmount.getText ().toString ();

            Call<Transaction> aCall = withdrawApi.withdrawMoney (aadharNo, accountNo, amount, withdrawOutputAgentId);

            aCall.enqueue (new Callback<Transaction> () {
                @Override
                public void onResponse(Call<Transaction> aCall, Response<Transaction> response) {
                    if (!response.isSuccessful ()) {
                        Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                        return;
                    }

                    Transaction withdraw = response.body ();

                    if (withdraw.getRrn () == null) {
                        Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :" + response.code (), Toast.LENGTH_LONG).show ();
                    } else {
                        Intent intent = new Intent (getApplicationContext (), WithdrawOutputActivity.class);
                        intent.putExtra ("withdrawAccountNumber", withdraw.getAccountNumber ());
                        intent.putExtra ("withdrawAmount", withdraw.getAmount ().toString ());
                        intent.putExtra ("withdrawRrn", withdraw.getRrn ());
                        intent.putExtra ("withdrawDate", withdraw.getTransactionDate ());
                        startActivity (intent);
                    }
                }

                @Override
                public void onFailure(Call<Transaction> aCall, Throwable t) {
                    Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
                }
            });
        }

//    public void getBalance(){
//        Retrofit retrofit = new Retrofit.Builder ().
//                baseUrl("http://192.168.42.37:8080/Mint/")
//                .addConverterFactory (GsonConverterFactory.create ())
//                .build ();
//        BalanceEnquiryApi balaneEnqiryApi = retrofit.create (BalanceEnquiryApi.class);
//        final String aadharNo = withdrawAadharNumber.getText ().toString ();
//        String accountNo = withdrawAccountNumber.getText ().toString ();
//
//        final Double amount = Double.parseDouble (withdrawAmount.getText ().toString ());
//
//
//        Call<Account> aCall = balaneEnqiryApi.getBalaceByAccount (aadharNo, accountNo);
//
//        aCall.enqueue (new Callback<Account> () {
//            @Override
//            public void onResponse(Call<Account> aCall, Response<Account> response) {
//                if (!response.isSuccessful ()){
//                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
//                    return;
//                }
//
//                Account balance = response.body ();
//                Double accountBalance = Double.parseDouble(balance.getBalance ().toString ());
//                if(accountBalance < amount){
//                    Toast.makeText (getApplicationContext (), "In Sufficient Balance :" +response.code (), Toast.LENGTH_LONG).show ();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Account> aCall, Throwable t) {
//                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
//            }
//        });
//    }
}
