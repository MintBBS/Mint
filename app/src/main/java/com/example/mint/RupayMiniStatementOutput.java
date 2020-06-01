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

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RupayMiniStatementOutput extends AppCompatActivity {
    private static final int STORAGE_CODE =1000 ;
    TextView cardNo;
    TextView accountNumber;
    TextView availableBalance;
    TextView transactionDate;
    TextView recordType;
    TextView amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_mini_statement_output);

        cardNo = (TextView) findViewById (R.id.textViewRupayMiniStatementCardNumber);
        accountNumber = findViewById (R.id.textViewRupayMiniStatementAccountNumber);
        transactionDate = (TextView) findViewById (R.id.textViewRupayMiniStatementDate);
        recordType = (TextView) findViewById (R.id.textViewRupayMiniStatementRecordType);
        amount = (TextView) findViewById (R.id.textViewRupayMiniStatementAmount);

        Button buttonPrintMiniStatement = findViewById (R.id.buttonPrintRupayMiniStatement);

        getMiniStatement ();

        buttonPrintMiniStatement.setOnClickListener (new View.OnClickListener () {
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

    public void getMiniStatement(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        MiniStatementApi miniStatementApi = retrofit.create (MiniStatementApi.class);

        Intent intent= getIntent ();
        final String cardNumber= intent.getStringExtra ("MiniStatementcardNumber");
        final String cardHolderName = intent.getStringExtra ("MiniStatementcardHolderName");
        final String  cvv= intent.getStringExtra ("MiniStatementcvv");
        final String expireDate = intent.getStringExtra ("MiniStatementexpireDate");
        final String pin= intent.getStringExtra ("MiniStatementpin");


        Call<List<Transaction>> call = miniStatementApi.getminiStatement (cardNumber,cardHolderName,cvv,expireDate,pin);

        call.enqueue (new Callback<List<Transaction>> () {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (!response.isSuccessful ()){
                    Toast.makeText (getApplicationContext (), "Error Code: "+response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }
                List<Transaction> transaction = response.body ();
               // cardNo.setText (cardNumber);
                String value3 = cardNumber.substring(1,16);
                String value4 = value3.replace(value3,"************") + cardNumber.substring(12,16);
                cardNo.setText(value4);

                for (Transaction report: transaction){

                    String acctNo = report.getAccountNumber ();

                    String value1 = acctNo.substring(1,9);
                    String value2 = value1.replace(value1,"******") + acctNo.substring(6,9);
                    accountNumber.setText(value2);

                    transactionDate.append (report.getTransactionDate () + "\n" + "\n" + "\n");
                    recordType.append (report.getTransactionType () + "\n" + "\n" + "\n");
                    amount.append (report.getAmount().toString () + " INR" + "\n" + "\n" + "\n");
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }

    private void savePdf() {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat ("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));;
            mDoc.open();
            String heading = "-- Transaction Report -- \n \n";
            String pdfText = accountNumber.getText().toString();
            String pdfText1 = transactionDate.getText ().toString ();
            String pdfText2 = recordType.getText ().toString ();
            String pdfText3 = amount.getText ().toString ();

            mDoc.add (new Paragraph (heading));
            mDoc.add(new Paragraph("Card Number : " + pdfText));
            mDoc.add (new Paragraph ("Date : " + pdfText1 + "\n" + "\n" + "\n"));
            mDoc.add (new Paragraph ("Transaction Type" + pdfText2 + "\n" + "\n" + "\n"));
            mDoc.add (new Paragraph ("Amount : " + pdfText3 + "\n" + "\n" + "\n"));

            mDoc.open ();
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