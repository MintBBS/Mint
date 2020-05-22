package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MinistatementActivity extends MySessionActivity {

    Spinner spinner;
    String[] bank = {"Choose Bank", "UCO"};

    List<String> spinnerlist;
    ArrayAdapter<String> arrayadapter;


    EditText editTextAadharNumber;
    EditText editTextAccountNumber;
    Button buttonMiniStatement;

    TextView textViewFingerprint;
    ImageButton imageButtonFIngerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_ministatement);

        editTextAadharNumber = (EditText) findViewById (R.id.editTextMiniStatementAadharNo);
        editTextAccountNumber = (EditText) findViewById (R.id.editTextMiniStatementAccountNo);
        buttonMiniStatement = (Button) findViewById (R.id.buttonMiniStatement);

        textViewFingerprint = (TextView) findViewById (R.id.textViewMiniStatementFingerprint);
        imageButtonFIngerprint = (ImageButton) findViewById (R.id.imageButtonMiniStatementFingerprint);


        spinner = (Spinner) findViewById(R.id.miniStatementSpinner);
        spinnerlist = new ArrayList<> (Arrays.asList(bank));

        arrayadapter = new ArrayAdapter<String>(MinistatementActivity.this,R.layout.spinner_item_textview,spinnerlist) {


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



        imageButtonFIngerprint.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (editTextAadharNumber.getText ().toString ().isEmpty ()) {
                    editTextAadharNumber.setError ("Enter a valid aadhar number ");
                    editTextAadharNumber.requestFocus ();
                } else if (editTextAadharNumber.getText ().toString ().length () < 12 || editTextAadharNumber.getText ().toString ().length () > 12) {
                    editTextAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    editTextAadharNumber.requestFocus ();
                }else {
                    getAadharDetails ();
                }
            }
        });


        buttonMiniStatement.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                String aadharNo = editTextAadharNumber.getText ().toString ();
                String accountNo = editTextAccountNumber.getText ().toString ();

                if(textViewFingerprint.getText().toString().isEmpty())
                {
                    Toast.makeText (MinistatementActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_SHORT).show ();

                }
                else if (editTextAadharNumber.getText ().toString ().isEmpty ()) {
                    editTextAadharNumber.setError ("Enter a valid aadhar number ");
                    editTextAadharNumber.requestFocus ();
                } else if (editTextAadharNumber.getText ().toString ().length () < 12 || editTextAadharNumber.getText ().toString ().length () > 12) {
                    editTextAadharNumber.setError ("Aadhar Number Should Be 12 Digit ");
                    editTextAadharNumber.requestFocus ();
                }
                else if(editTextAccountNumber.getText().toString().isEmpty() )
                {
                    editTextAccountNumber.setError("Please Enter Account Number  ");
                    editTextAccountNumber.requestFocus();
                }
//                else if(textViewFingerprint.getText ().toString () == "")
//                {
//                    Toast.makeText (MinistatementActivity.this, "Fingerprint Authentication Failed", Toast.LENGTH_SHORT).show ();
//                }
                else
                    {
                    Intent intent = new Intent (getApplicationContext (), MiniStatementOutput.class);
                    intent.putExtra ("MiniStatementAadharNumber", aadharNo);
                    intent.putExtra ("MiniStatementAccountNumber", accountNo);
                    startActivity (intent);
                }
            }
        });
    }

    public void getAadharDetails(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl("http://192.168.42.37:8080/AadharApi/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        AadharApi aadharApi = retrofit.create (AadharApi.class);

        final String aadharNo = editTextAadharNumber.getText ().toString ();


        Call<Aadhar> aCall = aadharApi.getAadharDetails (aadharNo);

        aCall.enqueue (new Callback<Aadhar> () {
            @Override
            public void onResponse(Call<Aadhar> aCall, Response<Aadhar> response) {
                if (!response.isSuccessful ()){
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                Aadhar details = response.body ();
                if(details.getAadharNumber ().equals (aadharNo)) {
                    textViewFingerprint.setText (details.getFingerprint ());
                }
//                else{
//                    Toast.makeText (MinistatementActivity.this, "Enter A valid Aadhar Number", Toast.LENGTH_SHORT).show ();
//                }
            }

            @Override
            public void onFailure(Call<Aadhar> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), "Please Enter Valid Aadhar Number", Toast.LENGTH_LONG).show ();
            }
        });
    }


}
