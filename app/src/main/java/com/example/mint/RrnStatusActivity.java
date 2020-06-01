package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

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

public class RrnStatusActivity extends MySessionActivity {
    EditText rrn;
    Button buttonRrn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rrn_status);

        rrn = (EditText) findViewById (R.id.editTextRrnStatusRrn);
        buttonRrn = (Button) findViewById (R.id.buttonRrn);

        buttonRrn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (rrn.getText ().toString ().isEmpty ()) {
                    rrn.setError ("Enter a valid aadhar RRN ");
                    rrn.requestFocus ();
                } else {
                    getTransactionByRrn ();
                }
            }
        });
    }

    public void getTransactionByRrn(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl ("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        RrnApi rrnApi = retrofit.create (RrnApi.class);

        String Rrn = rrn.getText ().toString ();

        Call<AgentTransaction> aCall = rrnApi.getTransactionDetails (Rrn);

        aCall.enqueue (new Callback<AgentTransaction> () {
            @Override
            public void onResponse(Call<AgentTransaction> aCall, Response<AgentTransaction> response) {
                if (!response.isSuccessful ()) {
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                AgentTransaction details = response.body ();

                if(details.getRrn () == null){
                    Toast.makeText (getApplicationContext (), "RRN is Invalid :"+response.code (), Toast.LENGTH_LONG).show ();
                }
                else{
                    Intent intent = new Intent (getApplicationContext (), RrnOutput.class);
                    intent.putExtra ("accountNumber", details.getAccountNumber ());
                    intent.putExtra("amount", details.getAmount ().toString ());
                    intent.putExtra ("rrn", details.getRrn ());
                    intent.putExtra ("type", details.getTransactionType ());
                    intent.putExtra ("date", details.getTransactionDate ());
                    startActivity (intent);
                }
            }

            @Override
            public void onFailure(Call<AgentTransaction> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }
}
