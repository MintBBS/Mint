package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RupayBalanceEnquiryActivity extends AppCompatActivity {
    private AwesomeValidation awesomeValidation1;
    EditText balanceEnquiryCardNumber;
    EditText balanceEnquiryHolderName;
    EditText balanceEnquiryCvv;
    EditText balanceEnquiryExpireDate;
    EditText balanceEnquiryPin;
    Button balanceEnquirybutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_balance_enquiry);

        setTitle ("Rupay Balance Enquiry");

        awesomeValidation1 = new AwesomeValidation(ValidationStyle.BASIC);
        balanceEnquiryCardNumber = (EditText) findViewById (R.id.editTextBalanceEnquiryCardNo);
        balanceEnquiryHolderName = (EditText) findViewById (R.id.editTextRupayBalanceEnquiryHolderName);
        balanceEnquiryCvv = (EditText) findViewById (R.id.editTextBalanceEnquiryCvv);
        balanceEnquiryExpireDate = (EditText) findViewById (R.id.editTextBalanceEnquiryExpireDate);
        balanceEnquiryPin = (EditText) findViewById (R.id.editTextBalanceEnquiryPin);


        balanceEnquirybutton = (Button) findViewById (R.id.rupayBalaceEnquiryButton);
        awesomeValidation1.addValidation(this,R.id.editTextBalanceEnquiryCardNo, "^[0-9]{16}$", R.string.card_number);
        awesomeValidation1.addValidation(this,R.id.editTextRupayBalanceEnquiryHolderName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.holder_name);
        awesomeValidation1.addValidation(this,R.id.editTextBalanceEnquiryCvv, "^[0-9]{3}$", R.string.cvv);
        awesomeValidation1.addValidation(this,R.id.editTextBalanceEnquiryExpireDate, "[0-1]{1}[0-9]{1}-[1-2]{1}[0-9]{3}", R.string.expiredate);
        awesomeValidation1.addValidation(this,R.id.editTextBalanceEnquiryPin, "^[0-9]{4}$", R.string.pin);


        balanceEnquirybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==balanceEnquirybutton) {
                    submitform();

                }
            }
        });
    }

    private void submitform() {
        if (awesomeValidation1.validate()){
            getBalanceByAccount();
        }
    }

    public void getBalanceByAccount() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final String cardNumber = balanceEnquiryCardNumber.getText().toString();
        final String cardHolderName = balanceEnquiryHolderName.getText().toString();
        final String cvv = balanceEnquiryCvv.getText().toString();
        final String expireDate = balanceEnquiryExpireDate.getText().toString();
        final String pin = balanceEnquiryPin.getText().toString();
        BalanceEnquiryApi balanceEnquiryApi = retrofit.create(BalanceEnquiryApi.class);

        Intent intent = new Intent(getApplicationContext (), RupayBalanceEnquiryOutput.class);
        intent.putExtra("cardNumber",cardNumber);
        intent.putExtra("cardHolderName",cardHolderName);
        intent.putExtra("cvv",cvv);
        intent.putExtra("expireDate",expireDate);
        intent.putExtra("pin",pin);
        startActivity(intent);
    }
}
