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
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.itextpdf.text.pdf.PdfFormField.createList;

public class MiniStatementOutput extends AppCompatActivity {
    private static final int STORAGE_CODE =1000 ;
    TextView accountNumber;
    TextView availableBalance;
    TextView date;
    TextView recordType;
    TextView amount;
    List<Transaction> miniStatement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_ministatement_output);

        accountNumber = (TextView) findViewById (R.id.textViewMiniStatementAccountNumber);
        date = (TextView) findViewById (R.id.textViewMiniStatementDate);
        recordType = (TextView) findViewById (R.id.textViewMiniStatementRecordType);
        amount = (TextView) findViewById (R.id.textViewMiniStatementAmount);
        miniStatement = new ArrayList<Transaction>(5);

        Button buttonPrintMiniStatement = findViewById (R.id.buttonPrintMiniStatement);

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
                baseUrl("http://192.168.42.173:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        MiniStatementApi miniStatementApi = retrofit.create (MiniStatementApi.class);

        Intent intent= getIntent ();
        String aadharNo = intent.getStringExtra ("MiniStatementAadharNumber");
        final String accountNo = intent.getStringExtra ("MiniStatementAccountNumber");

        Call<List<Transaction>> call = miniStatementApi.getMiniStatement (aadharNo, accountNo);

        call.enqueue (new Callback<List<Transaction>> () {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (!response.isSuccessful ()){
                    Toast.makeText (getApplicationContext (), "Please Enter Valid Credential: "+response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }
                List<Transaction> transaction = response.body ();
                //accountNumber.setText (accountNo);

                String value1 = accountNo.substring(1,9);
                String value2 = value1.replace(value1,"******") + accountNo.substring(6,9);
                accountNumber.setText(value2);

                for (Transaction report: transaction){
                    miniStatement.add (new Transaction ( report.getTransactionType (), report.getAmount (), report.getTransactionDate ()));
                    date.append (report.getTransactionDate () + "\n" + "\n" + "\n");
                    recordType.append (report.getTransactionType () + "\n" + "\n" + "\n");
                    amount.append (report.getAmount().toString () + " INR" + "\n" + "\n" + "\n");
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText (getApplicationContext (), "Please Enter Valid Credentials", Toast.LENGTH_LONG).show ();
            }
        });
    }

    private void savePdf() {
        Document mDoc = new Document();
        String mFileName = new SimpleDateFormat ("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + "Mint" + "/" + "AepsMiniStatement_" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));;
            mDoc.open();
            String heading = "-- Transaction Report -- \n \n";
            String pdfText = accountNumber.getText().toString();

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

            mDoc.add (new Paragraph ("Date           " + "Record Type           " +"Amount" ));
            for(int i = 0; i<miniStatement.size (); i++){
                mDoc.add (new Paragraph (miniStatement.get (i).getTransactionDate ()+ "       " + miniStatement.get (i).getTransactionType ()+ "             " + miniStatement.get (i).getAmount ()));
            }

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
