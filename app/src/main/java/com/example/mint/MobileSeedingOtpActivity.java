package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MobileSeedingOtpActivity extends MySessionActivity {

    EditText editTextOtp;
    Button buttonOtp;
    String mVerificationId;

    FirebaseAuth Auth;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_mobile_seeding_otp);

        editTextOtp = (EditText) findViewById (R.id.editTextMobileSeedingOtp);
        buttonOtp = (Button) findViewById (R.id.buttonMobileSeedingOtp);

        Intent intent = getIntent ();
       number = intent.getStringExtra ("number");

        Auth = FirebaseAuth.getInstance ();

        sendVerificationCode ("+91"+number);

        buttonOtp.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String code = editTextOtp.getText ().toString ().trim ();
                if (code.isEmpty () || code.length () < 6) {
                    Toast.makeText (MobileSeedingOtpActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show ();
                    editTextOtp.requestFocus ();
                    return;
                }
                verifyCode (code);
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential (mVerificationId, code);
        signInWithPhoneAuthCredential (credential);
    }

    private void sendVerificationCode(String number) {
        //progressBar.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance ().verifyPhoneNumber (
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)

                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks () {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String code = credential.getSmsCode ();
            if (code != null) {
                editTextOtp.setText (code);
                verifyCode (code);
            }

            signInWithPhoneAuthCredential (credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.


            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText (getApplicationContext (), "Invalid Number", Toast.LENGTH_LONG).show ();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText (getApplicationContext (), "The SMS quota for the project has been exceeded", Toast.LENGTH_LONG).show ();
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
        Auth.signInWithCredential (credential)
                .addOnCompleteListener (this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {
                            // Sign in success, update UI with the signed-in user's information
                            new AlertDialog.Builder(MobileSeedingOtpActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Updated")
                                    .setMessage("Mobile Number Updated To " + number)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent (getApplicationContext (), HomepageActivity.class);
                                            startActivity (intent);
                                        }

                                    })
                                    .setNegativeButton("", null)
                                    .show();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException () instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
