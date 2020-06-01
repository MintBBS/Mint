package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RdData extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;

    EditText aadharrnumber;
    EditText accountnumber;
    EditText nomineeaadhar;
    EditText nomineename;
    //  EditText nomineerelation;
    EditText amount;
    EditText installmentamount;
    EditText dateofbirth;
    Button rdbutton;

    private RdApi rdApi;

    //for spinner
    Spinner sp;
    String selectedDataFromSpinner;
    String names[]={"Father","Mother","Son","Daughter","Husband","Wife"};

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_rd_data);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        aadharrnumber=(EditText) findViewById(R.id.rdaadhar_number);
        accountnumber=(EditText) findViewById(R.id.rdaccount_number);
        nomineeaadhar=(EditText) findViewById(R.id.rdnominee_aadhar);
        nomineename=(EditText) findViewById(R.id.rdnominee_name);
        // nomineerelation=(EditText) findViewById(R.id.rdnominee_relation);
        amount=(EditText) findViewById(R.id.rdamount);
        installmentamount=(EditText) findViewById(R.id.rdinstallment_amount);
        dateofbirth=(EditText)findViewById(R.id.rddateofbirth);
        rdbutton=(Button)  findViewById(R.id.rdbutton);

        awesomeValidation.addValidation(this, R.id.rdaadhar_number, "^[3-9]{1}[0-9]{11}$", R.string.rdaadhar_number);
        awesomeValidation.addValidation(this, R.id.rdaccount_number, "^[1-9]{1}[0-9]{9}$", R.string.rdaccount_number);
        awesomeValidation.addValidation(this, R.id.rdnominee_aadhar, "^[3-9]{1}[0-9]{11}$", R.string.rdnominee_aadhar);
        awesomeValidation.addValidation(this, R.id.rdnominee_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.rdnominee_name);
        //  awesomeValidation.addValidation(this, R.id.rdnominee_relation, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.rdnominee_relation);
        awesomeValidation.addValidation(this, R.id.rdamount, "^[0-9]{5}$", R.string.rdamount);
        awesomeValidation.addValidation(this, R.id.rdinstallment_amount, "^[0-9]{4}$", R.string.rdinstallment_amount);
        awesomeValidation.addValidation(this, R.id.rddateofbirth, "[0-3]{1}[0-9]{1}-[0-1]{1}[0-9]{1}-[1-2]{1}[0-9]{1}[0-9]{2}", R.string.rddateofbirth);


        //for spinner
        sp = (Spinner)findViewById(R.id.spinner5);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.42.20:8080/Mint/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        rdApi =retrofit.create(RdApi.class);
        rdbutton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if(v==rdbutton) {
                    submitform();
                }
            }
        });
    }

    private void submitform() {
        if (awesomeValidation.validate()){
            Rd rd = new Rd(aadharrnumber.getText().toString(), accountnumber.getText().toString() , dateofbirth.getText().toString(),
                    nomineeaadhar.getText().toString(),
                    nomineename.getText().toString(), selectedDataFromSpinner.toString(), amount.getText().toString(),
                    installmentamount.getText().toString());
            //  Rd rd = new Rd("345","35366","3466","ghfh","fghg","fghg","fghgf","fggfg");
            createRd(rd);
        }
    }

    private void createRd(Rd rd){
        Call<Rd> call = rdApi.createRd(rd);
        call.enqueue(new Callback<Rd> () {
            @Override
            public void onResponse(Call<Rd> call, Response<Rd> response) {
                if (!response.isSuccessful()){
                }
                Rd apiResponse=response.body();
                Intent intent=new Intent(RdData.this,Rdsts.class);
                intent.putExtra("aadhar_number",apiResponse.getAadhar_number());
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<Rd> call, Throwable t) {
            }
        });
    }
}
