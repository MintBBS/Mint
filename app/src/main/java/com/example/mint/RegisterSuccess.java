package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RegisterSuccess extends AppCompatActivity {

    TextView textView;
    String msg;
    String name,mobile,aadhar;
    TextView message;
    Button home;
    //    String value2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_register_success);

        message = findViewById(R.id.message_print);


        msg = getIntent().getExtras().getString("message");
//        name = getIntent().getExtras().getString("name");
//        mobile = getIntent().getExtras().getString("mobile");
//        aadhar = getIntent().getExtras().getString("aadhar");
//        value2 = getIntent().getExtras().getInt("key2");

        message.setText(msg);
    }
}
