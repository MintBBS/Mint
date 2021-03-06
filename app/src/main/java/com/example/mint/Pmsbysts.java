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

public class Pmsbysts extends AppCompatActivity {

    private static final int STORAGE_CODE=1000;
    TextView acountno;
    TextView nomineename;
    TextView nomineeaadhar;
    TextView scheme_id;
    Button printpmsby;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    TextView currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pmsbysts);

        String aadhar_number= getIntent().getStringExtra("aadhar_number");

        acountno = findViewById(R.id.textViewPmsbyAccountNumber);
        nomineename = findViewById(R.id.textViewPmsbyNomineeName);
        nomineeaadhar = findViewById(R.id.textViewPmsbyNomineeAadhar);
        scheme_id = findViewById(R.id.textViewPmsbySchemeId);
        printpmsby=findViewById(R.id.printpmsby);
        currentDate = findViewById (R.id.textViewPmsbyDate);

        calendar = Calendar.getInstance ();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());
        currentDate.setText(date);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PmsbyApi pmsbyapi = retrofit.create(PmsbyApi.class);
        Call<Pmsby> call = pmsbyapi.getAadharnbr(aadhar_number);
        call.enqueue(new Callback<Pmsby> () {
            @Override
            public void onResponse(Call<Pmsby> call, Response<Pmsby> response) {
                if (!response.isSuccessful()) {
                    acountno.setText("Error Code: " + response.code());
                    nomineename.setText("Error Code: " + response.code());
                    nomineeaadhar.setText("Error Code: " + response.code());
                    scheme_id.setText("Error Code: " + response.code());
                    return;
                }


                Pmsby pmsby = response.body();
                {
                    acountno.append(" " + pmsby.getAccount_number());
                    nomineename.append(" " + pmsby.getNominee_name());
                    nomineeaadhar.append(" " + pmsby.getNominee_aadhar());
                    scheme_id.append(" " + pmsby.getScheme_id());

                }
            }

            @Override
            public void onFailure(Call<Pmsby> call, Throwable t) {
                acountno.setText(t.getMessage());
                nomineename.setText(t.getMessage());
                nomineeaadhar.setText(t.getMessage());
                scheme_id.setText(t.getMessage());

            }


        });

        printpmsby.setOnClickListener(new View.OnClickListener() {
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
        String mFilePath = Environment.getExternalStorageDirectory() + "/MINT/PMSBY_applied" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));
            mDoc.open();
            String heading = "-- Transaction Report --";
            String pdfText = acountno.getText().toString();
            String pdfText1 = nomineename.getText ().toString ();
            String pdfText2 =nomineeaadhar.getText ().toString ();
            String pdfText3 =scheme_id.getText ().toString ();
            String pdfText4 = currentDate.getText ().toString ();


            mDoc.addTitle (String.valueOf (new Paragraph (heading)));
            mDoc.add(new Paragraph("----Transaction Report---- "));
            mDoc.add(new Paragraph("Scheme Id: "+pdfText3));
            mDoc.add(new Paragraph("Scheme Type - PMSBY "));
            mDoc.add(new Paragraph(" Customer Account Number: " + pdfText));
            mDoc.add (new Paragraph ("Nominee Name : " + pdfText1));
            mDoc.add (new Paragraph ("Nominee Aadhar Number : " + pdfText2));
            mDoc.add (new Paragraph ("Date : " + pdfText4));
            mDoc.close ();
            Toast.makeText(this, "saved" + mFilePath,Toast.LENGTH_LONG).show();

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
