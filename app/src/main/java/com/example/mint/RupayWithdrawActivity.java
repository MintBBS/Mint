package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.common.collect.Range;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RupayWithdrawActivity extends AppCompatActivity {

    private AwesomeValidation awesomeValidation1;
    String withdrawAgentId = "869145032077219";
    EditText withdrawPin;
    EditText WithdrawCardNumber;
    EditText WithdrawHolderName;
    EditText WithdrawCvv;
    EditText WithdrawExpireDate;
    EditText withdrawAmount;
    Button buttonWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_withdraw);

        awesomeValidation1 = new AwesomeValidation(ValidationStyle.BASIC);
        WithdrawCardNumber = (EditText) findViewById(R.id.editTextRupayWithdrawCardNo);
        WithdrawHolderName = (EditText) findViewById (R.id.editTextRupayWithdrawCardHolderName);
        WithdrawCvv = (EditText) findViewById (R.id.editTextRupayWithdrawCvv);
        WithdrawExpireDate = (EditText) findViewById (R.id.editTextRupayWithdrawExpireDate);
        withdrawPin = (EditText) findViewById(R.id.editTextRupayWithdrawPin);
        withdrawAmount = (EditText) findViewById(R.id.editTextRupayWithdrawAmount);
        buttonWithdraw = (Button) findViewById(R.id.buttonRupayWithdraw);

        awesomeValidation1.addValidation(this,R.id.editTextRupayWithdrawCardNo, "^[0-9]{16}$", R.string.card_number);
        awesomeValidation1.addValidation(this,R.id.editTextRupayWithdrawCardHolderName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.holder_name);
        awesomeValidation1.addValidation(this,R.id.editTextRupayWithdrawCvv, "^[0-9]{3}$", R.string.cvv);
        awesomeValidation1.addValidation(this,R.id.editTextRupayWithdrawExpireDate, "[0-1]{1}[0-9]{1}-[1-2]{1}[0-9]{3}", R.string.expiredate);
        awesomeValidation1.addValidation(this,R.id.editTextRupayWithdrawPin, "^[0-9]{4}$", R.string.pin);
        awesomeValidation1.addValidation(this,R.id.editTextRupayWithdrawAmount, Range.closed(100,10000),R.string.amount);
        buttonWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(v==buttonWithdraw) {
                    submitform();

                }
            }
        });
    }

    private void submitform() {
        if (awesomeValidation1.validate()){
            transferFund();
        }
    }

    public void transferFund() {
        //String agentId = withdrawAgentId;
        String cardNumber = WithdrawCardNumber.getText().toString();
        String cardHolderName=WithdrawHolderName.getText().toString();
        String cvv=WithdrawCvv.getText().toString();
        String expireDate=WithdrawExpireDate.getText().toString();
        String pin = withdrawPin.getText().toString();
        String amount = withdrawAmount.getText().toString();


        Intent intent2 = new Intent(this, RupayWithdrawOutput.class);

       // intent2.putExtra("withdrawAgentId", withdrawAgentId);
        intent2.putExtra("WithdrawCardNumber", cardNumber);
        intent2.putExtra("withdrawCardHolderName", cardHolderName);
        intent2.putExtra("WithdrawCvv", cvv);
        intent2.putExtra("WithdrawExpireDate", expireDate);
        intent2.putExtra("withdrawPin", pin);
        intent2.putExtra("withdrawAmount", amount);
        startActivity(intent2);

    }

}


