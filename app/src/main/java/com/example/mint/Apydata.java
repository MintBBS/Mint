package com.example.mint;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class Apydata extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;
    EditText aadharrnumber;

    EditText accountnumber;
    EditText nomineeaadhar;
    EditText nomineename;
    //   EditText nomineerelation;
    EditText amount;
    EditText pensionamount;
    EditText dateofbirth;
    Button apybutton;

    ApyApi apyApi;
    Apy apy;
    Spinner sp;
    String selectedDataFromSpinner;
    String names[]={"Father","Mother","Son","Daughter","Husband","Wife"};
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apydata);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        aadharrnumber=(EditText) findViewById(R.id.apyaadhar_number);
        accountnumber=(EditText) findViewById(R.id.apyaccount_number);
        nomineeaadhar=(EditText) findViewById(R.id.apynominee_aadhar);
        nomineename=(EditText) findViewById(R.id.apynominee_name);
        amount=(EditText) findViewById(R.id.apyamount);
        pensionamount=(EditText) findViewById(R.id.apypension_amount);
        dateofbirth=(EditText) findViewById(R.id.apydateofbirth);
        apybutton=(Button)  findViewById(R.id.apybutton);

        sp = (Spinner)findViewById(R.id.spinner3);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setPrompt("Select Nominee Relation");
        sp.setAdapter(adapter);

        //for getting data from spinner
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedDataFromSpinner = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//

        //data validator

        awesomeValidation.addValidation(this, R.id.apyaadhar_number, "^[3-9]{1}[0-9]{11}$", R.string.apyaadhar_number);
        awesomeValidation.addValidation(this, R.id.apyaccount_number, "^[1-9]{1}[0-9]{9}$", R.string.apyaccount_number);
        awesomeValidation.addValidation(this, R.id.apynominee_aadhar, "^[3-9]{1}[0-9]{11}$", R.string.apynominee_aadhar);
        awesomeValidation.addValidation(this, R.id.apynominee_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.apynominee_name);
        //  awesomeValidation.addValidation(this, R.id.apynominee_relation, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.apynominee_relation);
        awesomeValidation.addValidation(this, R.id.apyamount, "^[0-9]{5}$", R.string.apyamount);
        awesomeValidation.addValidation(this, R.id.apypension_amount, "^[0-9]{4}$", R.string.apypension_amount);
        awesomeValidation.addValidation(this, R.id.apydateofbirth, "[0-3]{1}[0-9]{1}-[0-1]{1}[0-9]{1}-[1-2]{1}[0-9]{1}[0-9]{2}", R.string.apydateofbirth);
        //retrofit builder

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apyApi =retrofit.create(ApyApi.class);

        apybutton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (v == apybutton) {
                    submitForm();
                }


            }
        });
    }

    private void submitForm() {
        //first validate the form then move ahead
        //if this becomes true that means validation is successfull
        if (awesomeValidation.validate()) {
            apy=new Apy(aadharrnumber.getText().toString(),accountnumber.getText().toString(),nomineeaadhar.getText().toString(),
                    nomineename.getText().toString(),selectedDataFromSpinner,amount.getText().toString(),
                    pensionamount.getText().toString(),dateofbirth.getText().toString());

            new Multithread().execute();


        }
    }


    /*private void createApy(Apy apy){
//        Apy apy = new Apy("34556456","35366","3466","ghfh","fghg","fghg","fghgf","fggfg");
        Call<Apy> call= apyApi.createApy(apy);
        call.enqueue(new Callback<Apy>() {
            @Override
            public void onResponse(Call<Apy> call, Response<Apy> response) {
                if(!response.isSuccessful()){
//                        textViewResult.setText("Code: "+response.code());
                    //Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }
                Apy apiResponse= response.body();
                Intent intent = new Intent(Apydata.this,Apysts.class);
                intent.putExtra("aadhar_number",apiResponse.getAadhar_number());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Apy> call, Throwable t) {
//                Toast.makeText(this,,Toast.LENGTH_LONG).show();
//                    textViewResult.setText(t.getMessage());
            }
        });
    }*/
    public class Multithread extends AsyncTask<Apy, String, String>{

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }


        @Override
        protected String doInBackground(Apy... apies) {
            Call<Apy> call= apyApi.createApy(apy);
            call.enqueue(new Callback<Apy>() {
                @Override
                public void onResponse(Call<Apy> call, Response<Apy> response) {
                    if(!response.isSuccessful()){
//                        textViewResult.setText("Code: "+response.code());
                        //Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
                        return;
                    }
                    Apy apiResponse= response.body();
                    Intent intent = new Intent(Apydata.this,Apysts.class);
                    intent.putExtra("aadhar_number",apiResponse.getAadhar_number());
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Apy> call, Throwable t) {
//                Toast.makeText(this,,Toast.LENGTH_LONG).show();
//                    textViewResult.setText(t.getMessage());
                }
            });
            return null;
        }
    }
}

