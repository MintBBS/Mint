package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

public class RupayMiniStatementActivity extends AppCompatActivity {
    private AwesomeValidation awesomeValidation1;
    EditText miniStatementCardNumber;
    EditText miniStatementHolderName;
    EditText miniStatementCvv;
    EditText miniStatementExpireDate;
    EditText miniStatementPin;
    Button miniStatementbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_mini_statement);

        setTitle ("Rupay Mini Statement");

        awesomeValidation1 = new AwesomeValidation(ValidationStyle.BASIC);
        miniStatementCardNumber = (EditText) findViewById(R.id.editTextRupayMiniStatementCardNo);
        miniStatementHolderName = (EditText) findViewById(R.id.editTextRupayMiniStatementHolderName);
        miniStatementCvv = (EditText) findViewById(R.id.editTextRupayMiniStatementCvv);
        miniStatementExpireDate = (EditText) findViewById(R.id.editTextRupayMiniStatementExpireDate);
        miniStatementPin = (EditText) findViewById(R.id.editTextRupayMiniStatementPin);
        miniStatementbutton = (Button) findViewById(R.id.rupayMiniStatementButton);
        awesomeValidation1.addValidation(this, R.id.editTextRupayMiniStatementCardNo, "^[0-9]{16}$", R.string.card_number);
        awesomeValidation1.addValidation(this, R.id.editTextRupayMiniStatementHolderName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.holder_name);
        awesomeValidation1.addValidation(this, R.id.editTextRupayMiniStatementCvv, "^[0-9]{3}$", R.string.cvv);
        awesomeValidation1.addValidation(this, R.id.editTextRupayMiniStatementExpireDate, "[0-1]{1}[0-9]{1}-[1-2]{1}[0-9]{3}", R.string.expiredate);
        awesomeValidation1.addValidation(this, R.id.editTextRupayMiniStatementPin, "^[0-9]{4}$", R.string.pin);

        miniStatementbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == miniStatementbutton) {
                    submitform();
                }
            }
        });
    }

    private void submitform() {
        if (awesomeValidation1.validate()){
            ministatement();
        }
    }

    public void ministatement(){
        final String cardNumber = miniStatementCardNumber.getText().toString();
        final String cardHolderName = miniStatementHolderName.getText().toString();
        final String cvv = miniStatementCvv.getText().toString();
        final String expireDate = miniStatementExpireDate.getText().toString();
        final String pin = miniStatementPin.getText().toString();
        Intent intent = new Intent(getApplicationContext(), RupayMiniStatementOutput.class);
        intent.putExtra("MiniStatementcardNumber", cardNumber);
        intent.putExtra("MiniStatementcardHolderName", cardHolderName);
        intent.putExtra("MiniStatementcvv", cvv);
        intent.putExtra("MiniStatementexpireDate", expireDate);
        intent.putExtra("MiniStatementpin", pin);
        startActivity(intent);

    }
}
