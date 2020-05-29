package com.example.mint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Header;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class BalanceEnquiryOutput extends AppCompatActivity {
    private static final int STORAGE_CODE = 1000;
    TextView nameBalanceEnquiry;
    TextView accountNumberBalanceEnquiry;
    TextView transactionTypeBalanceEnquiry;
    TextView accountBalanceBalanceEnquiry;
    Button buttonPrintBalanceEnquiry;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    TextView currentDate;


    String mFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_balance_enquiry_output);

        //nameBalanceEnquiry = (TextView) findViewById (R.id.textViewAccountHolderName);
        accountNumberBalanceEnquiry = (TextView) findViewById (R.id.textViewAccountNumber);
        transactionTypeBalanceEnquiry = (TextView) findViewById (R.id.textViewTransactionType);
        accountBalanceBalanceEnquiry = (TextView) findViewById (R.id.textViewAccountBalance);
        currentDate = findViewById (R.id.textViewBalanceEnquiryDate);
        buttonPrintBalanceEnquiry = (Button) findViewById (R.id.buttonPrintBalanceEnquiry);

        calendar = Calendar.getInstance ();

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
        currentDate.setText(date);

        Intent intent = getIntent ();
        String accountNo = intent.getStringExtra ("balanceEnquiryAccountNumber");

        String value1 = accountNo.substring(1,9);
        String value2 = value1.replace(value1,"******") + accountNo.substring(6,9);
        accountNumberBalanceEnquiry.setText(value2);


        String type = intent.getStringExtra ("transactionType");
        String accountBalance = intent.getStringExtra ("balanceEnquiryAccountBalance");

       // accountNumberBalanceEnquiry.setText (accountNo);
        transactionTypeBalanceEnquiry.setText (type);
        accountBalanceBalanceEnquiry.setText (accountBalance + " INR");


        buttonPrintBalanceEnquiry.setOnClickListener (new View.OnClickListener () {
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
        String mFileName = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        mFilePath = Environment.getExternalStorageDirectory() + "/" + "Mint" + "/" + "AepsBalanceEnquiry_" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            mDoc.open();
            String heading = "-- Transaction Report --";
            String pdfText = accountNumberBalanceEnquiry.getText().toString();
            String pdfText1 = accountBalanceBalanceEnquiry.getText ().toString ();
            String pdfText2 = transactionTypeBalanceEnquiry.getText ().toString ();
            String pdfText3 = currentDate.getText ().toString ();

            mDoc.add(new Paragraph (heading));

            Rectangle rect = new Rectangle (577, 825, 18, 15);
            rect.enableBorderSide (1);
            rect.enableBorderSide (2);
            rect.enableBorderSide (4);
            rect.enableBorderSide (8);

            rect.setBorderColor (BaseColor.BLACK);
            rect.setBorderWidth (2);

            mDoc.add (rect);

            mDoc.add(new Paragraph("Account Number : " + pdfText));
            mDoc.add (new Paragraph ("Available Balance : " + pdfText1));
            mDoc.add (new Paragraph ("Transaction Type : " + pdfText2));
            mDoc.add (new Paragraph ("Date : " + pdfText3));
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
