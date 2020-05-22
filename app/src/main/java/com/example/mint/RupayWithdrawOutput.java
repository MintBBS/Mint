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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RupayWithdrawOutput extends AppCompatActivity {
    private static final int STORAGE_CODE=1000;
    TextView withdrawAccountNumber;
    TextView transactionType;
    TextView withdrawRrn;
    TextView withdrawAmount;
    TextView withdrawRemark;
    String agentId;
    String cardNumber;
    String cardHolderName;
    String cvv;
    String expireDate;
    String pin;
    String amount;
    TextView withdrawDate;
    TextView withdrawTransactionStatus;
    Button printbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_withdraw_output);

        printbutton = findViewById(R.id.buttonPrint);
        withdrawAccountNumber = (TextView) findViewById(R.id.textViewRupayWithdrawAccountNumber);
        withdrawRrn = (TextView) findViewById(R.id.textViewRupayWithdrawRRN);
        transactionType = (TextView) findViewById(R.id.textViewRupayWithdrawTransactionType);
        withdrawAmount = (TextView) findViewById(R.id.textViewRupayWithdrawAmount);
        withdrawRemark = (TextView) findViewById(R.id.textViewWithdrawRemark);
        withdrawDate = (TextView) findViewById(R.id.textViewWithdrawDate);
        withdrawTransactionStatus = (TextView) findViewById(R.id.textViewWithdrawTransactionStatus);


        Intent intent = getIntent();
        agentId = intent.getStringExtra("withdrawAgentId");
        cardNumber = intent.getStringExtra("WithdrawCardNumber");
        cardHolderName=intent.getStringExtra("withdrawCardHolderName");
        cvv=intent.getStringExtra("WithdrawCvv");
        expireDate=intent.getStringExtra("WithdrawExpireDate");
        pin = intent.getStringExtra("withdrawPin");
        amount = intent.getStringExtra("withdrawAmount");
        getReport();
        printbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,STORAGE_CODE);
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

    public void savePdf()
    {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat ("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));
            mDoc.open();
            String heading = "-- Balance Enquiry Report --";
            String pdfText = withdrawAccountNumber.getText().toString();
            String pdfText1 = withdrawRrn.getText ().toString ();
            String pdfText2 = transactionType.getText ().toString ();
            String pdfText3 = withdrawAmount.getText().toString();
            String pdfText4 = withdrawRemark.getText().toString();
            String pdfText5 = withdrawDate.getText().toString();
            String pdftext6 =  withdrawTransactionStatus.getText().toString();

            mDoc.addTitle (String.valueOf (new Paragraph (heading)));

            mDoc.add(new Paragraph(" Card Name: " + pdfText));
            mDoc.add (new Paragraph ("Rrn Number : " + pdfText1));
            mDoc.add (new Paragraph ("Transaction Type : " + pdfText2));
            mDoc.add(new Paragraph("Amount :" + pdfText3));
            mDoc.add(new Paragraph("Remark :" + pdfText4));
            mDoc.add(new Paragraph("Transaction Date :" + pdfText5));
            mDoc.add(new Paragraph("Transaction Status :" + pdftext6));
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

    public void getReport(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.6:8080/MintApi2/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WithdrawApi withdrawApi = retrofit.create(WithdrawApi.class);
        Call<Transaction> call = withdrawApi.transferFund(agentId, cardNumber,cardHolderName,cvv,expireDate, pin, amount);
        call.enqueue(new Callback<Transaction> () {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if(!response.isSuccessful()){
                    withdrawTransactionStatus.setText("Unsuccessful Transaction");
                    Toast.makeText (getApplicationContext (), "INVALID DETAIL!!! Unsuccessful Transaction", Toast.LENGTH_LONG).show ();
                    return;
                }
                Transaction transactions = response.body();

                withdrawAccountNumber.append(" "+transactions.getAccountNumber());
                withdrawRrn.append(" " + transactions.getRrn());
                transactionType.append(" " + transactions.getTransactionType());
                withdrawAmount.append(" " + transactions.getAmount());
                withdrawRemark.append(" " + transactions.getRemark());
                withdrawDate.append(" "+transactions.getTransactionDate());
                withdrawTransactionStatus.setText("Transaction Successful");

            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();

            }
        });
    }
    public void getHomepage(View view){
        Intent intent = new Intent(this, HomepageActivity.class);
        startActivity (intent);
    }
}