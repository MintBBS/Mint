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

public class FundTransferOutputActivity extends AppCompatActivity {

    private static final int STORAGE_CODE =1000 ;
    TextView fundTransferAccountNumber;
    TextView transactionType;
    TextView fundTransferAvailableBalance;
    TextView fundTransferRrn;
    TextView fundTransferAmount;
    TextView fundTransferDate;
    TextView fundTransferStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_fund_transfer_output);

        fundTransferAccountNumber = (TextView) findViewById (R.id.textViewFundTransferAccountNumber);
        transactionType = (TextView) findViewById (R.id.textViewFundTransferTransactionType);
//        withdrawAvailableBalance = (TextView) findViewById (R.id.textViewWithdrawAccountBalance);
        fundTransferRrn = (TextView) findViewById (R.id.textViewFundTransferRRN);
        fundTransferAmount = (TextView) findViewById (R.id.textViewFundTransferAmount);
        fundTransferDate = (TextView) findViewById (R.id.textViewFundTransferDate);

        fundTransferStatus = (TextView)findViewById (R.id.fundTransferStatus);

        Button buttonPrintFundsTransfer = findViewById (R.id.buttonPrintFundsTransfer);

        Intent intent = getIntent ();
        String fundTransferAccountNo =  intent.getStringExtra ("transferAccountNumber");

        String value1 = fundTransferAccountNo.substring(1,9);
        String value2 = value1.replace(value1,"******") + fundTransferAccountNo.substring(6,9);
        fundTransferAccountNumber.setText(value2);

        String fundTransferRRN = intent.getStringExtra ("transferRrn");
        String fundTransferAmount1 = intent.getStringExtra ("transferAmount");
        String fundTransferDate1 = intent.getStringExtra ("transferDate");


        //fundTransferAccountNumber.setText (fundTransferAccountNo);
        transactionType.setText ("AEPS Funds Transfer");
        //withdrawAvailableBalance.setText (withdraw.);
        fundTransferRrn.setText (fundTransferRRN);
        fundTransferAmount.setText (fundTransferAmount1 + " INR");
        fundTransferDate.setText (fundTransferDate1);
        fundTransferStatus.setText ("Successful");

        buttonPrintFundsTransfer.setOnClickListener (new View.OnClickListener () {
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
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + "Mint" + "/" + "AepsFundsTransfer_" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));;
            mDoc.open();
            String heading = "-- Transaction Report -- \n \n";
            String pdfText = fundTransferAccountNumber.getText().toString();
            String pdfText1 = fundTransferRrn.getText ().toString ();
            String pdfText2 = transactionType.getText ().toString ();
            String pdfText3 = fundTransferAmount.getText ().toString ();
            String pdfText4 = fundTransferDate.getText ().toString ();

            Rectangle rect = new Rectangle (577, 825, 18, 15);
            rect.enableBorderSide (1);
            rect.enableBorderSide (2);
            rect.enableBorderSide (4);
            rect.enableBorderSide (8);

            rect.setBorderColor (BaseColor.BLACK);
            rect.setBorderWidth (2);

            mDoc.add (rect);

            mDoc.add (new Paragraph (heading));

            mDoc.add(new Paragraph("Account Number : " + pdfText));
            mDoc.add (new Paragraph ("RRN : " + pdfText1));
            mDoc.add (new Paragraph ("Transaction Type" + pdfText2));
            mDoc.add (new Paragraph ("Transfered Amount : " + pdfText3));
            mDoc.add (new Paragraph ("Transfer Date : " + pdfText4));
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
