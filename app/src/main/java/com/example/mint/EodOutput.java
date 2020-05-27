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
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EodOutput extends AppCompatActivity  {

    private static final int STORAGE_CODE =1000 ;
    TextView eodReport;
    Button print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_eod_output);

        eodReport = (TextView) findViewById (R.id.textViewEodReport);
        print = (Button) findViewById (R.id.buttonPrintEodReport);

        getEodReport ();

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
        String mFilePath = Environment.getExternalStorageDirectory() + "/" + "Mint" + "/" + "AepsWithdraw_" + mFileName + ".pdf";
        try {
            PdfWriter.getInstance(mDoc, new FileOutputStream (mFilePath));;
            mDoc.open();
            String heading = "-- Transaction Report --";
            List<AgentTransaction> report = null;
            String pdfText = eodReport.getText().toString();
            mDoc.add (new Paragraph (heading));
            for(AgentTransaction at : report){
               // mDoc.add (at.getAccountNumber ());
            }



            Rectangle rect = new Rectangle (577, 825, 18, 15);
            rect.enableBorderSide (1);
            rect.enableBorderSide (2);
            rect.enableBorderSide (4);
            rect.enableBorderSide (8);

            rect.setBorderColor (BaseColor.BLACK);
            rect.setBorderWidth (2);

            mDoc.add (rect);
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

    public void getEodReport(){
        Retrofit retrofit = new Retrofit.Builder ().
                baseUrl ("http://192.168.42.37:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        EodApi eodApi = retrofit.create (EodApi.class);

        Call<List<AgentTransaction>> aCall = eodApi.getEodReport ();

        aCall.enqueue (new Callback<List<AgentTransaction>> () {
            @Override
            public void onResponse(Call<List<AgentTransaction>> aCall, Response<List<AgentTransaction>> response) {
                if (!response.isSuccessful ()) {
                    Toast.makeText (getApplicationContext (), response.code (), Toast.LENGTH_LONG).show ();
                    return;
                }

                List<AgentTransaction> report = response.body ();

                int count = 0 ;

                for(AgentTransaction aT: report){
                    String acctNo = aT.getAccountNumber ();
                    count = count + 1;
                    eodReport.append ("------------------------------------------------------------" + "\n");


                    String value1 = acctNo.substring(1,9);
                    String value2 = value1.replace(value1,"******") + acctNo.substring(6,9);
                    //accountNumberBalanceEnquiry.setText(value2);

                    eodReport.append ("Account No : " + value2 + "\n" + "\n" );

                    eodReport.append ("RRN : " + aT.getRrn () + "\n" + "\n");
                    eodReport.append ("Amount : " + aT.getAmount () + " INR" + "\n" + "\n");
                    eodReport.append ("Transaction Type : " + aT.getTransactionType () + "\n");
                    eodReport.append ("------------------------------------------------------------" + "\n");
                    count ++;
                }
            }

            @Override
            public void onFailure(Call<List<AgentTransaction>> aCall, Throwable t) {
                Toast.makeText (getApplicationContext (), t.getMessage (), Toast.LENGTH_LONG).show ();
            }
        });
    }


}
