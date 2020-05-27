package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AgentBalanceOutput extends AppCompatActivity {

    private static final int STORAGE_CODE =1000 ;
    TextView agentName;
    TextView agentAccountNumber;
    TextView agentBalance;

    Button print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.agent_balance_output);

        agentName = (TextView) findViewById (R.id.textViewAgentName);
        agentAccountNumber = (TextView) findViewById (R.id.textViewAgentAccountNumber);
        agentBalance = (TextView) findViewById (R.id.textViewAgentAccountBalance);

        print = (Button) findViewById (R.id.buttonPrintAgentBalance);

        Intent intent = getIntent ();
        String name = intent.getStringExtra ("name");
        String accountNo = intent.getStringExtra ("accountNo");

        String value1 = accountNo.substring(1,9);
        String value2 = value1.replace(value1,"******") + accountNo.substring(6,9);
        agentAccountNumber.setText(value2);

        String aBalance = intent.getStringExtra ("balance");

        agentName.setText (name);
       // agentAccountNumber.setText (accountNo);
        agentBalance.setText (aBalance + " INR");

        print.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){

                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions, STORAGE_CODE);

                    }
                    else{
                        savePdf();
                    }
                }
                else{
                    savePdf();
                }
            }
        });

    }

    private void savePdf() {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat ("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + "Mint" + "/" + "AgentBalance_" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));
            mDoc.open();
            String heading = "-- Transaction Report --";
            String pdfText = agentName.getText().toString();
            String pdfText1 = agentAccountNumber.getText ().toString ();
            String pdfText2 = agentBalance.getText ().toString ();

            Rectangle rect = new Rectangle (577, 825, 18, 15);
            rect.enableBorderSide (1);
            rect.enableBorderSide (2);
            rect.enableBorderSide (4);
            rect.enableBorderSide (8);

            rect.setBorderColor (BaseColor.BLACK);
            rect.setBorderWidth (2);

            mDoc.add (rect);

            mDoc.add(new Paragraph (heading));

            mDoc.add(new Paragraph("Account Number : " + pdfText));
            mDoc.add (new Paragraph ("Available Balance : " + pdfText1));
            mDoc.add (new Paragraph ("Transaction Type : " + pdfText2));
            mDoc.close ();
            Toast.makeText(this, "saved" + mFilePath,Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:{
                if(grantResults.length >  0 && grantResults.length == PackageManager.PERMISSION_GRANTED){
                    savePdf();
                }
                else{
                    Toast.makeText(this,"Permission Denied..!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


}
