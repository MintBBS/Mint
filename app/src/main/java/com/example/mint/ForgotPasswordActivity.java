package com.example.mint;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText agentId;
    EditText newPassword;
    EditText confirmPassword;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_forgot_password);

        agentId = (EditText) findViewById (R.id.editTextForgetPasswordAgentId);
        newPassword = (EditText) findViewById (R.id.editTextForgetPasswordNewPassword);
        confirmPassword = (EditText) findViewById (R.id.editTextForgetPasswordConfirmPassword);
        resetButton = (Button) findViewById (R.id.resetPasswordButton);

        final View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);
        final Button btn_okay = (Button)mView.findViewById(R.id.btn_okay);

        Intent intent = getIntent ();
        agentId.setText (intent.getStringExtra ("agentID"));

//        resetButton.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent (getApplicationContext (), ForgetPasswordOtpActivity.class);
//                String AgentID = agentId.getText ().toString ();
//                String Password = confirmPassword.getText ().toString ();
//
//                intent1.putExtra ("agentID", AgentID);
//                intent1.putExtra ("Password", Password);
//                startActivity (intent1);
//            }
//        });


        resetButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (newPassword.getText ().toString ().isEmpty ()) {
                    newPassword.setError ("Password Field must be Filled");
                    newPassword.requestFocus ();
                } else if (confirmPassword.getText ().toString ().isEmpty ()) {
                    confirmPassword.setError ("Password Field must be Filled");
                    confirmPassword.requestFocus ();
                } else if (!newPassword.getText ().toString ().equals (confirmPassword.getText ().toString ())) {
                    confirmPassword.setError ("Please Enter Correct Password");
                    confirmPassword.requestFocus ();
                } else {
                    resetPassword ();
                    final AlertDialog.Builder alert = new AlertDialog.Builder (ForgotPasswordActivity.this);
                    alert.setView (mView);
                    final AlertDialog alertDialog = alert.create ();
                    alertDialog.setCanceledOnTouchOutside (false);

                    btn_okay.setOnClickListener (new View.OnClickListener () {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent (ForgotPasswordActivity.this, MainActivity.class);
                            startActivity (intent1);
                        }
                    });
                    alertDialog.show ();
                }
            }
        });
    }

    private void resetPassword(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl ("http://192.168.42.37:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();

        LoginApi loginApi =retrofit.create (LoginApi.class);
        String AgentId = agentId.getText ().toString ();
        String Password = confirmPassword.getText ().toString ();

        Call<Integer> call = loginApi.resetPassword (AgentId, Password);

        call.enqueue (new Callback<Integer> () {
                          @Override
                          public void onResponse(Call<Integer> call, Response<Integer> response) {

                              if (!response.isSuccessful ()) {
                                  Toast.makeText (getApplicationContext (), "Server Error : " + response.code (), Toast.LENGTH_LONG).show ();
                                  return;
                              }
                              Integer message = response.body ();
                              if (message == 1) {

                              }


//                else{
//                    AlertDialog.Builder builder = new AlertDialog.Builder (getApplicationContext ());
//                    builder.setMessage ("Password not updated !");
//                    builder.setTitle ("Error");
//                    builder.setIcon (android.R.drawable.ic_dialog_alert);
//
//                    builder.setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent2 = new Intent (getApplicationContext (), ForgotPasswordActivity.class);
//                            startActivity (intent2);
//                        }
//                    });
                }


            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }


}
