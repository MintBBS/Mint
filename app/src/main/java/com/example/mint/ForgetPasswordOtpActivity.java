package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgetPasswordOtpActivity extends AppCompatActivity {

    EditText editTextOtp;
    Button buttonOtp;
    String mVerificationId;

    FirebaseAuth Auth;
    String number = "+919776431580";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_forget_password_otp);

        editTextOtp = (EditText) findViewById (R.id.editTextForgetPasswordOtp);
        buttonOtp = (Button) findViewById (R.id.buttonForgetPasswordOtp);


        Auth = FirebaseAuth.getInstance();

        sendVerificationCode(number);

        buttonOtp.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String code = editTextOtp.getText().toString().trim();
                if (code.isEmpty() || code.length()<6 ){
                    Toast.makeText (ForgetPasswordOtpActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show ();
                    editTextOtp.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });
    }
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendVerificationCode(String number) {
        //progressBar.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)

                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code = credential.getSmsCode();
            if (code!=null){
                editTextOtp.setText(code);
                verifyCode(code);
            }

            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(getApplicationContext(), "Invalid Number", Toast.LENGTH_LONG).show();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getApplicationContext(),"The SMS quota for the project has been exceeded",Toast.LENGTH_LONG).show();
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            mVerificationId = verificationId;
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                                resetPassword ();


                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void resetPassword(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl ("http://192.168.42.37:8080/Mint/")
                .build ();

        LoginApi loginApi =retrofit.create (LoginApi.class);

        Intent intent = getIntent ();
        String agentId= intent.getStringExtra ("agentID");
        String password = intent.getStringExtra ("Password");

        Call<Integer> call = loginApi.resetPassword (agentId, password);

        call.enqueue (new Callback<Integer> () {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if(!response.isSuccessful ()) {
                    Toast.makeText (getApplicationContext (), "Server Error : " + response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }
                Integer message = response.body ();

                Toast.makeText (ForgetPasswordOtpActivity.this, "Password Updated !", Toast.LENGTH_SHORT).show ();

                if(message == 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder (getApplicationContext ());
                    builder.setMessage ("Password updated !");
                    builder.setTitle ("Done");
                    builder.setIcon (android.R.drawable.ic_menu_save);

                    builder.setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent (getApplicationContext (), MainActivity.class);
                            startActivity (intent1);
                        }
                    });
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder (getApplicationContext ());
                    builder.setMessage ("Password not updated !");
                    builder.setTitle ("Error");
                    builder.setIcon (android.R.drawable.ic_dialog_alert);

                    builder.setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent2 = new Intent (getApplicationContext (), ForgotPasswordActivity.class);
                            startActivity (intent2);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }
}
