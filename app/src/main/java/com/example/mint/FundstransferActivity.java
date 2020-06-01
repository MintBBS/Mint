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

public class FundstransferActivity extends MySessionActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Spinner spinner;
    String[] bank = {"Choose Bank", "UCO"};

    List<String> spinnerlist;
    ArrayAdapter<String> arrayadapter;


    EditText editTextFundTransferAadharNumber;
    EditText editTextFundTransferAccountNumber;
    EditText editTextFundTransferAmount;
    EditText editTextFundTransferBeneficiaryAadharNumber;
    EditText editTextFundTransferBeneficiaryAccountNumber;
    EditText fundTransferDeviceId;
    Button buttonFundTransfer;

    TextView textViewFingerprint;
    ImageButton imageButtonFIngerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_fundstransfer);

        editTextFundTransferAadharNumber = (EditText) findViewById (R.id.editTextFundTransferAadharNumber);
        editTextFundTransferAccountNumber = (EditText) findViewById (R.id.editTextFundTransferAccountNumber);
        editTextFundTransferAmount = (EditText) findViewById (R.id.editTextFundTransferAmount);
        editTextFundTransferBeneficiaryAadharNumber = (EditText) findViewById (R.id.editTextFundTransferBenificiaryAadhar);
        editTextFundTransferBeneficiaryAccountNumber = (EditText) findViewById (R.id.editTextFundTransferBenificiaryAccount);
        buttonFundTransfer = (Button) findViewById (R.id.buttonFundTransfer);

       // textViewFingerprint = (TextView) findViewById (R.id.textViewFundTransferFingerprint);
       // imageButtonFIngerprint = (ImageButton) findViewById (R.id.imageButtonFundTransferFingerprint);


        spinner = (Spinner) findViewById(R.id.fundTransferSpinner);
        spinnerlist = new ArrayList<> (Arrays.asList(bank));

        arrayadapter = new ArrayAdapter<String>(FundstransferActivity.this,R.layout.spinner_item_textview,spinnerlist) {


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

        arrayadapter.setDropDownViewResource(R.layout.spinner_item_textview);
        spinner.setAdapter(arrayadapter);


//        textViewFingerprint.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (getApplicationContext (), FundTransferOutputActivity.class);
//                startActivity (intent);
//            }
//        });


//        imageButtonFIngerprint.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                if (editTextFundTransferAadharNumber.getText ().toString ().isEmpty ()) {
//                    editTextFundTransferAadharNumber.setError ("Enter a valid aadhar number ");
//                    editTextFundTransferAadharNumber.requestFocus ();
//                } else if (editTextFundTransferAadharNumber.getText ().toString ().length () < 12 || editTextFundTransferAadharNumber.getText ().toString ().length () > 12) {
//                    editTextFundTransferAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    editTextFundTransferAadharNumber.requestFocus ();
//                }else {
//                    getAadharDetails ();
//                }
//            }
//        });


        //fingerprint authentication
        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (FundstransferActivity.this,
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


                fundTransfer ();
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
        buttonFundTransfer.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (editTextFundTransferAadharNumber.getText ().toString ().isEmpty ()) {
                    editTextFundTransferAadharNumber.setError ("Enter a valid aadhar number ");
                    editTextFundTransferAadharNumber.requestFocus ();
                }

                else if (editTextFundTransferAadharNumber.getText ().toString ().length () < 12 || editTextFundTransferAadharNumber.getText ().toString ().length () > 12) {
                    editTextFundTransferAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    editTextFundTransferAadharNumber.requestFocus ();
                }

                else if (editTextFundTransferAccountNumber.getText ().toString ().isEmpty ()) {
                    editTextFundTransferAccountNumber.setError ("Please Enter Account Number  ");
                    editTextFundTransferAccountNumber.requestFocus ();
                }

                else  if (editTextFundTransferBeneficiaryAadharNumber.getText ().toString ().isEmpty ()) {
                    editTextFundTransferBeneficiaryAadharNumber.setError ("Enter a valid aadhar number ");
                    editTextFundTransferBeneficiaryAadharNumber.requestFocus ();
                }
                else if (editTextFundTransferBeneficiaryAadharNumber.getText ().toString ().length () < 12 || editTextFundTransferBeneficiaryAadharNumber.getText ().toString ().length () > 12) {
                    editTextFundTransferBeneficiaryAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    editTextFundTransferBeneficiaryAadharNumber.requestFocus ();
                }
                else if (editTextFundTransferBeneficiaryAccountNumber.getText ().toString ().isEmpty ()) {
                    editTextFundTransferBeneficiaryAccountNumber.setError ("Enter a valid account number ");
                    editTextFundTransferBeneficiaryAccountNumber.requestFocus ();
                }

                else if (editTextFundTransferAccountNumber.getText ().toString ().equals (editTextFundTransferBeneficiaryAccountNumber.getText ().toString ())){
                    editTextFundTransferBeneficiaryAccountNumber.setError ("Account Number Should Not Same");
                    editTextFundTransferBeneficiaryAccountNumber.requestFocus ();
                }

                else if (editTextFundTransferAmount.getText ().toString ().isEmpty ()) {
                    editTextFundTransferAmount.setError ("Please Enter amount  ");
                    editTextFundTransferAmount.requestFocus ();
                }
                else if ((Double.parseDouble (editTextFundTransferAmount.getText ().toString ()) > 10000)) {
                    editTextFundTransferAmount.setError ("Maximum amount should be  Rs 10000 ");
                    editTextFundTransferAmount.requestFocus ();
                }
                else if ((Double.parseDouble (editTextFundTransferAmount.getText ().toString ()) < 100)) {
                    editTextFundTransferAmount.setError ("Minimum amount should be  Rs 100 ");
                    editTextFundTransferAmount.requestFocus ();

                }
                else {
                    biometricPrompt.authenticate (promptInfo);
                }
            }
        });

//        buttonFundTransfer.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
////                if(textViewFingerprint.getText().toString().isEmpty())
////                {
////                    Toast.makeText (FundstransferActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_SHORT).show ();
////                }
//
//                 if (editTextFundTransferAadharNumber.getText ().toString ().isEmpty ()) {
//                    editTextFundTransferAadharNumber.setError ("Enter a valid aadhar number ");
//                    editTextFundTransferAadharNumber.requestFocus ();
//                }
//
//                else if (editTextFundTransferAadharNumber.getText ().toString ().length () < 12 || editTextFundTransferAadharNumber.getText ().toString ().length () > 12) {
//                    editTextFundTransferAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    editTextFundTransferAadharNumber.requestFocus ();
//                }
//
//                else if (editTextFundTransferAccountNumber.getText ().toString ().isEmpty ()) {
//                    editTextFundTransferAccountNumber.setError ("Please Enter Account Number  ");
//                    editTextFundTransferAccountNumber.requestFocus ();
//                }
//
//                else  if (editTextFundTransferBeneficiaryAadharNumber.getText ().toString ().isEmpty ()) {
//                    editTextFundTransferBeneficiaryAadharNumber.setError ("Enter a valid aadhar number ");
//                    editTextFundTransferBeneficiaryAadharNumber.requestFocus ();
//                }
//                else if (editTextFundTransferBeneficiaryAadharNumber.getText ().toString ().length () < 12 || editTextFundTransferBeneficiaryAadharNumber.getText ().toString ().length () > 12) {
//                    editTextFundTransferBeneficiaryAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    editTextFundTransferBeneficiaryAadharNumber.requestFocus ();
//                }
//                else if (editTextFundTransferBeneficiaryAccountNumber.getText ().toString ().isEmpty ()) {
//                    editTextFundTransferBeneficiaryAccountNumber.setError ("Enter a valid account number ");
//                    editTextFundTransferBeneficiaryAccountNumber.requestFocus ();
//                }
//
//                else if (editTextFundTransferAccountNumber.getText ().toString ().equals (editTextFundTransferBeneficiaryAccountNumber.getText ().toString ())){
//                    editTextFundTransferBeneficiaryAccountNumber.setError ("Account Number Should Not Same");
//                    editTextFundTransferBeneficiaryAccountNumber.requestFocus ();
//                }
//
//                else if (editTextFundTransferAmount.getText ().toString ().isEmpty ()) {
//                    editTextFundTransferAmount.setError ("Please Enter amount  ");
//                    editTextFundTransferAmount.requestFocus ();
//                }
//                else if ((Double.parseDouble (editTextFundTransferAmount.getText ().toString ()) > 10000)) {
//                    editTextFundTransferAmount.setError ("Maximum amount should be  Rs 10000 ");
//                    editTextFundTransferAmount.requestFocus ();
//                }
//                else if ((Integer.parseInt (editTextFundTransferAmount.getText ().toString ()) < 100)) {
//                    editTextFundTransferAmount.setError ("Minimum amount should be  Rs 100 ");
//                    editTextFundTransferAmount.requestFocus ();
//
//                }
//                else if (textViewFingerprint.getText ().toString ().trim () == ""){
//                    Toast.makeText (FundstransferActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                }
//                else {
//                    fundTransfer ();
//                }
//                }
//        });

    }


//    public void getAadharDetails(){
//        Retrofit retrofit = new Retrofit.Builder ().
//                baseUrl("http://192.168.42.37:8080/AadharApi/")
//                .addConverterFactory (GsonConverterFactory.create ())
//                .build ();
//        AadharApi aadharApi = retrofit.create (AadharApi.class);
//
//        final String aadharNo = editTextFundTransferAadharNumber.getText ().toString ();
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
//                    textViewFingerprint.setText (details.getFingerprint ());
//                }
////                else if(editTextFundTransferAadharNumber.getText().toString () == ""){
////                    Toast.makeText (getApplicationContext (), "Enter A valid Aadhar Number", Toast.LENGTH_SHORT).show ();
////                }
//            }
//
//            @Override
//            public void onFailure(Call<Aadhar> aCall, Throwable t) {
//                Toast.makeText (getApplicationContext (),"Please Enter Valid Aadhar Number", Toast.LENGTH_LONG).show ();
//            }
//        });
//    }

    public void fundTransfer(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl ("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
         FundsTransferApi fundTransferApi = retrofit.create (FundsTransferApi.class);


        final String aadharNo = editTextFundTransferAadharNumber.getText ().toString ();
        String accountNo = editTextFundTransferAccountNumber.getText ().toString ();
        String amount = editTextFundTransferAmount.getText ().toString ();
        String bAadharNo = editTextFundTransferBeneficiaryAadharNumber.getText ().toString ();
        String bAccountNo = editTextFundTransferBeneficiaryAccountNumber.getText ().toString ();

        Call<Transaction> aCall = fundTransferApi.transferMoney( aadharNo, amount, accountNo, bAccountNo, bAadharNo);

        aCall.enqueue (new Callback<Transaction> () {
            @Override
            public void onResponse(Call<Transaction> aCall, Response<Transaction> response) {
                if (!response.isSuccessful ()) {
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                Transaction transfer = response.body ();

                if(transfer.getRrn () == null){
                    Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :"+response.code (), Toast.LENGTH_LONG).show ();
                }
                else{

                    Intent intent = new Intent (getApplicationContext (), FundTransferOutputActivity.class);
                    intent.putExtra ("transferAccountNumber", transfer.getAccountNumber ());
                    intent.putExtra("transferAmount", transfer.getAmount ().toString ());
                    intent.putExtra ("transferRrn", transfer.getRrn ());
                    intent.putExtra ("transferDate", transfer.getTransactionDate ());
                    startActivity (intent);
                }
            }

            @Override
            public void onFailure(Call<Transaction> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }
}
