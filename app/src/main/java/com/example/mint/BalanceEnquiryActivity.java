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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BalanceEnquiryActivity extends MySessionActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Spinner spinner;
    String[] bank = {"Choose Bank", "UCO"};

    List<String> spinnerlist;
    ArrayAdapter<String> arrayadapter;


    EditText balanceEnquiryAadharNumber;
    EditText balanceEnquiryAccountNumber;
    TextView textViewFingerprint;
    ImageButton imageButtonFIngerprint;
    Button buttonBalanceEnquiry;

    int count = 0;

    private static final String encryptionKey = "ABCDEFGHIJKLMNOP";
    private static final String characterEncoding= "UTF-8";
    private static final String cipherTransformation = "AES/CBC/PKCS5PADDING";
    private static final String aesEncryptionAlgorithem = "AES";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_balance_enquiry);

        balanceEnquiryAadharNumber = (EditText) findViewById (R.id.editTextBalanceEnquiryAadharNo);
        balanceEnquiryAccountNumber = (EditText) findViewById (R.id.editTextBalanceEnquiryAccountNo);
        buttonBalanceEnquiry = (Button) findViewById (R.id.balaceEnquiryButton);
        //textViewFingerprint = (TextView) findViewById (R.id.textViewBalanceEnquiryFingerprint);
       // imageButtonFIngerprint = (ImageButton) findViewById (R.id.imageButtonBalanceEnquiryFingerprint);

//        balanceEnquiryAadharNumber.addTextChangedListener (loginTextWatcher);
//        balanceEnquiryAccountNumber.addTextChangedListener (loginTextWatcher);

        spinner = (Spinner) findViewById(R.id.balanceEnquirySpinner);
        spinnerlist = new ArrayList<> (Arrays.asList(bank));

        arrayadapter = new ArrayAdapter<String>(BalanceEnquiryActivity.this,R.layout.spinner_item_textview,spinnerlist) {


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
//                Toast.makeText (BalanceEnquiryActivity.this, balanceEnquiryAadharNumber.getText ().toString ().replaceAll (" ", ""), Toast.LENGTH_SHORT).show ();
//            }
//        });

//        imageButtonFIngerprint.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                if (balanceEnquiryAadharNumber.getText ().toString ().isEmpty ()) {
//                    balanceEnquiryAadharNumber.setError ("Enter a valid aadhar number ");
//                    balanceEnquiryAadharNumber.requestFocus ();
//                } else if (balanceEnquiryAadharNumber.getText ().toString ().length () < 12 || balanceEnquiryAadharNumber.getText ().toString ().length () > 12) {
//                    balanceEnquiryAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    balanceEnquiryAadharNumber.requestFocus ();
//                } else {
//                    getAadharDetails ();
//                }
//            }
//        });

// fingerprint authentication
        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (BalanceEnquiryActivity.this,
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


                getBalance ();
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
        buttonBalanceEnquiry.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (balanceEnquiryAadharNumber.getText ().toString ().isEmpty ()) {
                    balanceEnquiryAadharNumber.setError ("Enter a valid aadhar number ");
                    balanceEnquiryAadharNumber.requestFocus ();
                } else if (balanceEnquiryAadharNumber.getText ().toString ().length () < 12 || balanceEnquiryAadharNumber.getText ().toString ().length () > 12) {
                    balanceEnquiryAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    balanceEnquiryAadharNumber.requestFocus ();
                }

                else if(balanceEnquiryAccountNumber.getText().toString().isEmpty() )
                {
                    balanceEnquiryAccountNumber.setError("Please Enter amount  ");
                    balanceEnquiryAccountNumber.requestFocus();
                }
//                else if(textViewFingerprint.getText ().toString () == "") {
//                    Toast.makeText (BalanceEnquiryActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                }
                else {
                    biometricPrompt.authenticate (promptInfo);
                }
            }
        });



//        buttonBalanceEnquiry.setOnClickListener (new View.OnClickListener () {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View v) {
//                if (balanceEnquiryAadharNumber.getText ().toString ().isEmpty ()) {
//                    balanceEnquiryAadharNumber.setError ("Enter a valid aadhar number ");
//                    balanceEnquiryAadharNumber.requestFocus ();
//                } else if (balanceEnquiryAadharNumber.getText ().toString ().length () < 12 || balanceEnquiryAadharNumber.getText ().toString ().length () > 12) {
//                    balanceEnquiryAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    balanceEnquiryAadharNumber.requestFocus ();
//                }
//
//                else if(balanceEnquiryAccountNumber.getText().toString().isEmpty() )
//                {
//                    balanceEnquiryAccountNumber.setError("Please Enter amount  ");
//                    balanceEnquiryAccountNumber.requestFocus();
//                }
//                else if(textViewFingerprint.getText ().toString () == "") {
//                    Toast.makeText (BalanceEnquiryActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                } else {
//                    getBalance ();
//                }
//            }
//        });

//    balanceEnquiryAadharNumber.addTextChangedListener (new TextWatcher () {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            int inputlength = balanceEnquiryAadharNumber.getText().toString().length();
//
//            if (count <= inputlength && inputlength == 4 || inputlength == 9) {
//
//                balanceEnquiryAadharNumber.setText(balanceEnquiryAadharNumber.getText().toString() + "/");
//
//                int pos = balanceEnquiryAadharNumber.getText().length();
//                balanceEnquiryAadharNumber.setSelection(pos);
//
//            } else if (count >= inputlength && (inputlength == 4 ||
//                    inputlength == 9)) {
//                balanceEnquiryAadharNumber.setText(balanceEnquiryAadharNumber.getText().toString()
//                        .substring(0, balanceEnquiryAadharNumber.getText()
//                                .toString().length() - 1));
//
//                int pos = balanceEnquiryAadharNumber.getText().length();
//                balanceEnquiryAadharNumber.setSelection(pos);
//            }
//            count = balanceEnquiryAadharNumber.getText().toString().length();
//        }
//    });



    }




//    public void getAadharDetails(){
//            Retrofit retrofit = new Retrofit.Builder ().
//                    baseUrl("http://192.168.42.242:8080/AadharApi/")
//                    .addConverterFactory (GsonConverterFactory.create ())
//                    .build ();
//            AadharApi aadharApi = retrofit.create (AadharApi.class);
//
//            final String aadharNo = balanceEnquiryAadharNumber.getText ().toString ();
//
//
//            Call<Aadhar> aCall = aadharApi.getAadharDetails (aadharNo);
//
//            aCall.enqueue (new Callback<Aadhar> () {
//                @Override
//                public void onResponse(Call<Aadhar> aCall, Response<Aadhar> response) {
//                    if (!response.isSuccessful ()){
//                        Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
//                        return;
//                    }
//
//                    Aadhar details = response.body ();
//                    if(details.getAadharNumber ().equals (aadharNo)) {
//                        textViewFingerprint.setText (details.getFingerprint ());
//                    }else{
//                        Toast.makeText (getApplicationContext (), "Enter a valid Aadhar Number", Toast.LENGTH_LONG).show ();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Aadhar> aCall, Throwable t) {
//                    Toast.makeText (getApplicationContext (), "Enter a Valid Aadhar Number", Toast.LENGTH_LONG).show ();
//                }
//            });
//    }




    public void getBalance(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        BalanceEnquiryApi balaneEnqiryApi = retrofit.create (BalanceEnquiryApi.class);

        String aadharNo = balanceEnquiryAadharNumber.getText ().toString ();
        final String accountNo = balanceEnquiryAccountNumber.getText ().toString ();

        Call<Account> aCall = balaneEnqiryApi.getBalaceByAccount (aadharNo, accountNo);

        aCall.enqueue (new Callback<Account> () {
            @Override
            public void onResponse(Call<Account> aCall, Response<Account> response) {
                if (!response.isSuccessful ()){
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                Account balance = response.body ();
                // name.setText (balance.getCustomerName ());
                String accountNumber = balance.getAccountNumber ();
                String type = "Balance Enquiry";
                String accountBalance = balance.getBalance ().toString ();
                // rrn.setText ("Work in progress");
                if(balance.getAccountNumber () == null){
                    Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :" +response.code (), Toast.LENGTH_LONG).show ();
                }
                else{
                    Intent intent = new Intent (getApplicationContext (), BalanceEnquiryOutput.class);
                    intent.putExtra ("balanceEnquiryAccountNumber", accountNumber );
                    intent.putExtra ("transactionType", type);
                    intent.putExtra ("balanceEnquiryAccountBalance", accountBalance);
                    startActivity (intent);
                }
            }

            @Override
            public void onFailure(Call<Account> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String plainText) {
        String encryptedText = "";
        try {
            Cipher cipher   = Cipher.getInstance(cipherTransformation);
            byte[] key      = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedText = encoder.encodeToString(cipherText);

        } catch (Exception E) {
            System.err.println("Encrypt Exception : "+E.getMessage());
        }
        return encryptedText;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String encryptedText) {
        String decryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encryptedText.getBytes("UTF8"));
            decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");

        } catch (Exception E) {
            System.err.println("decrypt Exception : "+E.getMessage());
        }
        return decryptedText;
    }
}
