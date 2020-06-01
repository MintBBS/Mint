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
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RupayBalanceEnquiryOutput extends AppCompatActivity {

    private static final int STORAGE_CODE=1000;
    private TextView viewaccountNo;
    private TextView viewtransactionType;
    private TextView viewBalance;
    private Button printbutton;
    String cardNumber;
    String cardHolderName;
    String cvv;
    String expireDate;
    String pin;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    TextView currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rupay_balance_enquiry_output);

        viewaccountNo=findViewById(R.id.textViewRupayAccountNumber);
        viewtransactionType=findViewById(R.id.textViewRupayTransactionType);
        viewBalance=findViewById(R.id.textViewRupayAccountBalance);
        final TextView rupayCardNumber = findViewById (R.id.textViewRupayBalanceEnquiryCardNumber);
        printbutton = findViewById(R.id.buttonRupayBalancePrint);
        currentDate = findViewById (R.id.textViewRupayBalanceEnquiryDate);

        calendar = Calendar.getInstance ();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
        currentDate.setText(date);

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

        Intent intent =getIntent();
        cardNumber=intent.getStringExtra("cardNumber");
        cardHolderName=intent.getStringExtra("cardHolderName");
        cvv=intent.getStringExtra("cvv");
        expireDate=intent.getStringExtra("expireDate");
        pin=intent.getStringExtra("pin");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BalanceEnquiryApi balanceEnquiryApi = retrofit.create(BalanceEnquiryApi.class);
        Call<Account> call = balanceEnquiryApi.getRupayBalanceByAccount(cardNumber,cardHolderName,cvv,expireDate,pin);
        call.enqueue(new Callback<Account> () {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if(!response.isSuccessful()){
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }
                Account details = response.body();
                String accountNo = details.getAccountNumber ();

                String value1 = accountNo.substring(1,9);
                String value2 = value1.replace(value1,"******") + accountNo.substring(6,9);
                viewaccountNo.setText(value2);


                //viewaccountNo.append(" " + details.getAccountNumber());
                viewtransactionType.append("Rupay Balance Enquiry");
                String value3 = cardNumber.substring(1,16);
                String value4 = value3.replace(value3,"************") + cardNumber.substring(12,16);
                rupayCardNumber.setText(value4);

                viewBalance.append(" " + details.getBalance() + " INR");

            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }

    private void savePdf() {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat ("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/CustomerBalance" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));
            mDoc.open();
            String heading = "-- Transaction Report --";
            String pdfText = viewaccountNo.getText().toString();
            String pdfText1 = viewtransactionType.getText ().toString ();
            String pdfText2 =     viewBalance.getText ().toString ();
            String pdfText3 = currentDate.getText ().toString ();

            mDoc.addTitle (String.valueOf (new Paragraph (heading)));

            mDoc.add(new Paragraph(" Account Number: " + pdfText));
            mDoc.add (new Paragraph ("Type : " + pdfText1));
            mDoc.add (new Paragraph ("Available Balance : " + pdfText2));
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
