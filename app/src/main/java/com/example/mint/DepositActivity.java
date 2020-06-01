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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class DepositActivity extends MySessionActivity implements
        AdapterView.OnItemSelectedListener{

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Spinner spinner;
    String[] bank = {"Choose Bank", "UCO"};

    List<String> spinnerlist;
    ArrayAdapter<String> arrayadapter;

    EditText depositAadharNumber;
    EditText depositAccountNumber;
    EditText depositAmount;
    Button buttonDeposit;

    ImageButton imageButtonFIngerprint;
    TextView textViewDepositFingerprint;


    private static final String encryptionKey = "ABCDEFGHIJKLMNOP";
    private static final String characterEncoding= "UTF-8";
    private static final String cipherTransformation = "AES/CBC/PKCS5PADDING";
    private static final String aesEncryptionAlgorithem = "AES";

    String UCO = "123456793";
    String DENA = "";

    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_deposit);

        depositAadharNumber = (EditText) findViewById (R.id.editTextDepositAadharNo);
        depositAccountNumber = (EditText) findViewById (R.id.editTextDepositAccountNo);
        depositAmount = (EditText) findViewById (R.id.editTextDepositAmount);
        buttonDeposit = (Button) findViewById (R.id.buttonDeposit);

        //imageButtonFIngerprint = (ImageButton) findViewById (R.id.imageButtonDepositFingerprint);
       // textViewDepositFingerprint = (TextView) findViewById (R.id.textViewDepositFingerprint);

        final TextView spinnerItem = findViewById (R.id.spinnerItem);




        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spinner = (Spinner) findViewById(R.id.depositSpinner);
        spinnerlist = new ArrayList<> (Arrays.asList(bank));

        arrayadapter = new ArrayAdapter<String>(DepositActivity.this,R.layout.spinner_item_textview,spinnerlist) {


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



        String aadhar = depositAadharNumber.getText ().toString ();
        String amount = depositAmount.getText ().toString ();
        String account = depositAccountNumber.getText ().toString ();

//        imageButtonFIngerprint.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                if (depositAadharNumber.getText ().toString ().isEmpty ()) {
//                    depositAadharNumber.setError ("Enter a valid aadhar number ");
//                    depositAadharNumber.requestFocus ();
//                } else if (depositAadharNumber.getText ().toString ().length () < 12 || depositAadharNumber.getText ().toString ().length () > 12) {
//                    depositAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    depositAadharNumber.requestFocus ();
//                } else {
//                    getAadharDetails ();
//                }
//            }
//        });

//fingerprint authentication
        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (DepositActivity.this,
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


                depositMoney ();
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
        buttonDeposit.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (depositAadharNumber.getText ().toString ().isEmpty ()) {
                    depositAadharNumber.setError ("Enter a valid aadhar number ");
                    depositAadharNumber.requestFocus ();
                } else if (depositAadharNumber.getText ().toString ().length () < 12 || depositAadharNumber.getText ().toString ().length () > 12) {
                    depositAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    depositAadharNumber.requestFocus ();
                }
                else if(depositAccountNumber.getText().toString().isEmpty() )
                {
                    depositAccountNumber.setError("Please Enter Account Number  ");
                    depositAccountNumber.requestFocus();
                }
                else if(depositAmount.getText().toString().isEmpty() )
                {
                    depositAmount.setError("Please Enter amount  ");
                    depositAmount.requestFocus();
                }else if((Double.parseDouble(depositAmount.getText().toString())>50000)){

                    depositAmount.setError("Maximum amount should be  Rs 50000 ");
                    depositAmount.requestFocus();
                }
                else if (  (Integer.parseInt(depositAmount.getText().toString())<100)){
                    depositAmount.setError("Minimum amount should be  Rs 100 ");
                    depositAmount.requestFocus();
                }
//               else if(textViewDepositFingerprint.getText().toString().isEmpty())
//                {
//                    //Toast.makeText (DepositActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                    textViewDepositFingerprint.requestFocus();
//                }
//                else if(textViewDepositFingerprint.getText ().toString () == "") {
//                    Toast.makeText (DepositActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                }
                else {
                    biometricPrompt.authenticate (promptInfo);
                }
            }
        });


//        buttonDeposit.setOnClickListener (new View.OnClickListener () {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View v) {
//                if (depositAadharNumber.getText ().toString ().isEmpty ()) {
//                    depositAadharNumber.setError ("Enter a valid aadhar number ");
//                    depositAadharNumber.requestFocus ();
//                } else if (depositAadharNumber.getText ().toString ().length () < 12 || depositAadharNumber.getText ().toString ().length () > 12) {
//                    depositAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
//                    depositAadharNumber.requestFocus ();
//                }
//                else if(depositAccountNumber.getText().toString().isEmpty() )
//                {
//                    depositAccountNumber.setError("Please Enter Account Number  ");
//                    depositAccountNumber.requestFocus();
//                }
//                else if(depositAmount.getText().toString().isEmpty() )
//                {
//                    depositAmount.setError("Please Enter amount  ");
//                    depositAmount.requestFocus();
//                }else if((Double.parseDouble(depositAmount.getText().toString())>50000)){
//
//                    depositAmount.setError("Maximum amount should be  Rs 50000 ");
//                    depositAmount.requestFocus();
//                }
//                else if (  (Integer.parseInt(depositAmount.getText().toString())<100)){
//                    depositAmount.setError("Minimum amount should be  Rs 100 ");
//                    depositAmount.requestFocus();
//                }
////               else if(textViewDepositFingerprint.getText().toString().isEmpty())
////                {
////                    //Toast.makeText (DepositActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
////                    textViewDepositFingerprint.requestFocus();
////                }
//                else if(textViewDepositFingerprint.getText ().toString () == "") {
//                    Toast.makeText (DepositActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                }
//                else {
//                    depositMoney ();
//                }
//            }
//        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        data = String.valueOf(spinner.getSelectedItem());
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


//    public void getAadharDetails(){
//        Retrofit retrofit = new Retrofit.Builder ().
//                baseUrl("http://192.168.42.242:8080/AadharApi/")
//                .addConverterFactory (GsonConverterFactory.create ())
//                .build ();
//        AadharApi aadharApi = retrofit.create (AadharApi.class);
//
//        final String aadharNo = depositAadharNumber.getText ().toString ();
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
//                    textViewDepositFingerprint.setText (details.getFingerprint ());
//                }else{
//                    Toast.makeText (DepositActivity.this, "Enter a valid aadhar number", Toast.LENGTH_SHORT).show ();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Aadhar> aCall, Throwable t) {
//                Toast.makeText (getApplicationContext (), "Please Enter Valid Aadhar Number", Toast.LENGTH_LONG).show ();
//            }
//        });
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void depositMoney(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl ("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        DepositApi depositApi = retrofit.create (DepositApi.class);

        Intent intent1 = getIntent ();
        String agentId = intent1.getStringExtra ("depositAgentId");

        final String aadharNo = depositAadharNumber.getText ().toString ();
        String accountNo = depositAccountNumber.getText ().toString ();
        String amount = depositAmount.getText ().toString ();

        Call<Transaction> aCall = depositApi.depositMoney (aadharNo, accountNo, amount, agentId);

        aCall.enqueue (new Callback<Transaction> () {
            @Override
            public void onResponse(Call<Transaction> aCall, Response<Transaction> response) {
                if (!response.isSuccessful ()) {
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                Transaction deposit = response.body ();

                if(deposit.getRrn () == null){
                   Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :"+response.code (), Toast.LENGTH_LONG).show ();
                }
                else{

                    depositAccountNumber.setText (deposit.getAccountNumber ());
                    Intent intent = new Intent (getApplicationContext (), DepositOutputActivity.class);
                    intent.putExtra ("depositAccountNumber", deposit.getAccountNumber ());
                    intent.putExtra("depositAmount1", deposit.getAmount ().toString ());
                    intent.putExtra ("depositRrn", deposit.getRrn ());
                    intent.putExtra ("depositDate", deposit.getTransactionDate ());
                    startActivity (intent);
                }
            }

            @Override
            public void onFailure(Call<Transaction> aCall, Throwable t) {
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
