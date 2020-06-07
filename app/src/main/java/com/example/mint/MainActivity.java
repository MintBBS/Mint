package com.example.mint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Base64;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import androidx.biometric.BiometricPrompt;

public class MainActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    TextView agentID;
    EditText password;
    Button loginButton;
    ImageButton imgBtn;
    TextView fingerprintString;
    CheckBox showHidePass;
    TextView forgotPassword;
    TextView textViewTimer;

    ProgressDialog progressDialog;

    int attempt_counter = 3 ;
    public int counter;


    static final int PERMISION_READ_STATE = 123;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private int loginAttempts = 3;

    private static final String encryptionKey = "ABCDEFGHIJKLMNOP";
    private static final String characterEncoding= "UTF-8";
    private static final String cipherTransformation = "AES/CBC/PKCS5PADDING";
    private static final String aesEncryptionAlgorithem = "AES";


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        setTitle ("Mint");


        agentID = (TextView) findViewById (R.id.textViewImeiNumber);
        password = (EditText) findViewById (R.id.editTextPassword);
        loginButton = (Button) findViewById (R.id.buttonLogin);
        imgBtn = (ImageButton) findViewById (R.id.imageButtonMainpageFingerprint);
        fingerprintString = (TextView) findViewById (R.id.textViewMainPageFingerprintString);
        showHidePass = (CheckBox) findViewById (R.id.checkboxPassword);
        forgotPassword = (TextView) findViewById (R.id.textViewForgetPassword);
        textViewTimer = (TextView) findViewById (R.id.textViewTimer);
        final ProgressBar progressBar = findViewById (R.id.progressBarMainActivity);

        agentID.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (getApplicationContext (), HomepageActivity.class);
                startActivity (i);
            }
        });

        textViewTimer.setText ("");

        executor = ContextCompat.getMainExecutor (this);
        biometricPrompt = new BiometricPrompt (MainActivity.this,
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
                //progressBar.setVisibility(View.VISIBLE);
                progressDialog = new ProgressDialog (MainActivity.this);
                progressDialog.setMax (100);
                progressDialog.setIndeterminate (true);
                progressDialog.setMessage ("Loging you in...");
                progressDialog.setInverseBackgroundForced (false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                    validateUserCredential ();
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
        loginButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                if (password.getText ().toString ().isEmpty ()) {
                    password.setError ("Please Fill the Password");
                    password.requestFocus ();
                } else {
                    biometricPrompt.authenticate (promptInfo);
                }
            }
        });


//        fingerprintString.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (getApplicationContext (), HomepageActivity.class);
//                startActivity (intent);
//            }
//        });

        int permissionCheck = ContextCompat.checkSelfPermission (this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            MyTelephonyManager ();
        } else {
            ActivityCompat.requestPermissions (this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISION_READ_STATE);
        }


        //Hide and show password button

        showHidePass.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // show password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        // login button for validate and proceed to otp page

//        loginButton.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                if(password.getText ().toString ().isEmpty ()) {
//                    password.setError ("Please Fill the Password");
//                    password.requestFocus ();
//                }
//                else if(fingerprintString.getText ().toString ().isEmpty ()){
//                    Toast.makeText (MainActivity.this, "fingerprint Authentication Failed", Toast.LENGTH_LONG).show ();
//                }
//                else {
//                    validateUser ();
//                }
//
//            }
//        });


        // fingerprint button
//        imgBtn.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                validateFingerprint ();
//            }
//        });

        //forget password button
        forgotPassword.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MainActivity.this, ForgotPasswordActivity.class);
                String agentid = agentID.getText ().toString ();
                intent.putExtra ("agentID", agentid);
                startActivity (intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(mBackPressed + TIME_INTERVAL > System.currentTimeMillis ())
        {
            super.onBackPressed ();
            return;
        }
        else{
            Toast.makeText (this, "Tap back again to exit", Toast.LENGTH_SHORT).show ();
        }
        mBackPressed = System.currentTimeMillis ();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISION_READ_STATE: {
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyTelephonyManager ();
                } else {
                    Toast.makeText (this, "Permission Denied !", Toast.LENGTH_LONG).show ();
                }
            }
        }
    }

    private void MyTelephonyManager() {
        TelephonyManager manager = (TelephonyManager) getSystemService (Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        String imeiNumber = manager.getDeviceId ();
        agentID.setText (imeiNumber);
    }


//login without fingerprint string
    public void validateUserCredential(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl ("http://192.168.42.173:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();

        LoginApi loginApi =retrofit.create (LoginApi.class);

        final String userName = agentID.getText ().toString ();
        String passWord = password.getText ().toString ();

        Call<User> call = loginApi.validateUserCredential (userName, passWord);

        call.enqueue (new Callback<User> () {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

//                if(!response.isSuccessful ()) {
//                    Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :" + response.code (), Toast.LENGTH_LONG).show ();
//                    return;
//                }
                User message = response.body ();

                String passWord = password.getText ().toString ();
                String agentId = agentID.getText ().toString ();


                String mobileNumber = message.getMobileNumber ();


                if(passWord.equals (message.getPassword ()) && agentId.equals (message.getAgentId ())) {
                    // Toast.makeText (MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show ();

                    Intent intent = new Intent (getApplicationContext (), HomepageActivity.class);
                    intent.putExtra ("agentId", userName);
                    intent.putExtra ("mobileNumber", mobileNumber);
                    startActivity (intent);
                }
                else if(message.getPassword () == null){

                    attempt_counter--;
                    textViewTimer.setTextColor (Color.RED);
                    textViewTimer.setText(String.valueOf("Attempt left : " + attempt_counter));

                    if (attempt_counter == 0) {
                        loginButton.setEnabled(false);
                        counter = 30;
                        new CountDownTimer (30000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                loginButton.setEnabled(false);
                                textViewTimer.setTextColor (Color.rgb (255,157,0));
                                textViewTimer.setText(("You can login in - " + "00:"+ counter));
                                counter--;
                            }

                            public void onFinish() {
                                loginButton.setEnabled(true);
                                attempt_counter = 3;
                                textViewTimer.setTextColor (Color.rgb (50,205,50));
                                textViewTimer.setText("You Can Login Now");
                            }
                        }.start();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }



////login with fingerprint string
//    public void validateUser(){
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl ("http://192.168.42.242:8080/Mint/")
//                .addConverterFactory (GsonConverterFactory.create ())
//                .build ();
//
//        LoginApi loginApi =retrofit.create (LoginApi.class);
//
//        final String userName = agentID.getText ().toString ();
//        String passWord = password.getText ().toString ();
//        String fingerprint = fingerprintString.getText ().toString ();
//
//        Call<User> call = loginApi.validateUser (userName, passWord, fingerprint);
//
//        call.enqueue (new Callback<User> () {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//
////                if(!response.isSuccessful ()) {
////                    Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :" + response.code (), Toast.LENGTH_LONG).show ();
////                    return;
////                }
//                User message = response.body ();
//
//                String passWord = password.getText ().toString ();
//                String fingerPrint = fingerprintString.getText ().toString ();
//
//                String decodedFingerprint = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    decodedFingerprint = decrypt (fingerPrint);
//                }
//
//                String mobileNumber = message.getMobileNumber ();
//
//                if(loginAttempts == 0) {
//
//                    Toast.makeText(MainActivity.this, "Your attempt reach 0, please wait 3 minutes to log in again", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//
//                if(passWord.equals (message.getPassword ()) && decodedFingerprint.equals (message.getFingerprint ())) {
//                   // Toast.makeText (MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show ();
//
//                    Intent intent = new Intent (getApplicationContext (), OtpActivity.class);
//                    intent.putExtra ("agentId", userName);
//                    intent.putExtra ("mobileNumber", mobileNumber);
//                    startActivity (intent);
//                }
//                else if(message.getPassword () == null){
//
//                    attempt_counter--;
//                    textViewTimer.setText(String.valueOf("Attempt left : " + attempt_counter));
//
//                    if (attempt_counter == 0) {
//                        loginButton.setEnabled(false);
//                        counter = 30;
//                        new CountDownTimer (30000, 1000) {
//                            public void onTick(long millisUntilFinished) {
//                                loginButton.setEnabled(false);
//                                textViewTimer.setText(("You can login in : " + counter + " Sec"));
//                                counter--;
//                            }
//
//                            public void onFinish() {
//                                loginButton.setEnabled(true);
//                                attempt_counter = 3;
//
//                                textViewTimer.setText("You Can Login Now");
//                            }
//                        }.start();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
//            }
//        });
//    }

//    public void validateFingerprint(){
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl ("http://192.168.42.242:8080/Mint/")
//                .addConverterFactory (ScalarsConverterFactory.create ())
//                .addConverterFactory (GsonConverterFactory.create ())
//                .build ();
//
//        FingerprintApi fingerprintApi =retrofit.create (FingerprintApi.class);
//
//        final String userName = agentID.getText ().toString ();
//
//
//        Call<User> call = fingerprintApi.validateFingerPrint (userName);
//
//        call.enqueue (new Callback<User> () {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//
//                if(!response.isSuccessful ()) {
//                    Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials :" + response.code (), Toast.LENGTH_LONG).show ();
//                    return;
//                }
//                User message = response.body ();
//                if(message.getFingerprint () != null) {
//                    String encodedFingerprintString = encrypt (message.getFingerprint ());
//                    fingerprintString.setText (encodedFingerprintString);
//                   // fingerprintString.setText (message.getFingerprint ());
//                }else{
//                    Toast.makeText (getApplicationContext (), "Fingerprint Authentication Error", Toast.LENGTH_SHORT).show ();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText (getApplicationContext (),t.getMessage (), Toast.LENGTH_LONG).show ();
//            }
//        });
//    }

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
