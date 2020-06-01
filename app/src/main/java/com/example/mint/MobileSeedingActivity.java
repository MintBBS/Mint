package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MobileSeedingActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    EditText aadaarNo, confirmMobileNo, accountNo, mobileNo;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_mobile_seeding);


        aadaarNo = (EditText) findViewById(R.id.aadhaar_number);
        confirmMobileNo = (EditText) findViewById(R.id.confirm_mobile_number);
        accountNo = (EditText) findViewById(R.id.account_no);
        mobileNo = (EditText) findViewById(R.id.mobile_number);
        button = (Button) findViewById(R.id.button2);

        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (MobileSeedingActivity.this,
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
                updateNumber ();
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
        button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (aadaarNo.getText ().toString ().isEmpty ()) {
                    aadaarNo.setError ("Please fill the aadhar number");
                    aadaarNo.requestFocus ();
                }
               else if (mobileNo.getText ().toString ().isEmpty ()) {
                    mobileNo.setError ("Please fill the mobile number");
                    mobileNo.requestFocus ();
                }
               else if (confirmMobileNo.getText ().toString ().isEmpty ()) {
                    confirmMobileNo.setError ("Please fill the mobile number");
                    confirmMobileNo.requestFocus ();
                }
               else if (!mobileNo.getText ().toString ().equals (confirmMobileNo.getText ().toString ())) {
                    confirmMobileNo.setError ("Mobile number should be same");
                    confirmMobileNo.requestFocus ();
                }
                else if (accountNo.getText ().toString ().isEmpty ()) {
                    accountNo.setError ("{lease fill the account number}");
                    accountNo.requestFocus ();
                }
                else {
                    biometricPrompt.authenticate (promptInfo);
                }
            }
        });
    }



    private void updateNumber(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl ("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();

        SeedingApi seedingApi =retrofit.create (SeedingApi.class);
        String aadharNumber = aadaarNo.getText ().toString ();
        final String mobileNumber = confirmMobileNo.getText ().toString ();

        Call<Integer> call = seedingApi.updateNumber (aadharNumber, mobileNumber);

        call.enqueue (new Callback<Integer> () {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (!response.isSuccessful ()) {
                    Toast.makeText (getApplicationContext (), "Server Error : " + response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }
                Integer message = response.body ();
                if (message == 1) {
                    Intent intent = new Intent (getApplicationContext (), MobileSeedingOtpActivity.class);
                    intent.putExtra ("number", mobileNumber);
                    startActivity (intent);
                }
                else{
                    Toast.makeText (MobileSeedingActivity.this, "Error :" +response.code (), Toast.LENGTH_SHORT).show ();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }

}
