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

public class PmjjbyData extends AppCompatActivity {

    private AwesomeValidation awesomeValidation1;

    EditText aadharrnumber;
    EditText accountnumber;
    EditText nomineeaadhar;
    EditText nomineename;
    //EditText nomineerelation;

    EditText dateofbirth;
    Button pmjjbybutton;

    private PmjjbyApi pmjjbyApi;

    Spinner sp;
    String selectedDataFromSpinner;
    String names[]={"Father","Mother","Son","Daughter","Husband","Wife"};
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pmjjby_data);

        awesomeValidation1 = new AwesomeValidation(ValidationStyle.BASIC);
        aadharrnumber=(EditText) findViewById(R.id.pmjjbyaadhar_number);
        accountnumber=(EditText) findViewById(R.id.pmjjbyaccount_number);
        nomineeaadhar=(EditText) findViewById(R.id.pmjjbynominee_aadhar);
        nomineename=(EditText) findViewById(R.id.pmjjbynominee_name);
        dateofbirth=(EditText) findViewById(R.id.pmjjbydateofbirth);
        pmjjbybutton=(Button)  findViewById(R.id.pmjjbyButton);


        //awesomeValidation1.addValidation(this, R.id.pmjjbyaadhar_number, "^[3-9]{1}[0-9]{11}$", R.string.pmsbyaadhar_number);
        awesomeValidation1.addValidation(this, R.id.pmjjbyaccount_number, "^[1-9]{1}[0-9]{9}$", R.string.pmsbyaccount_number);
        // awesomeValidation1.addValidation(this, R.id.pmjjbynominee_aadhar, "^[3-9]{1}[0-9]{11}$",R.string.pmjjbynominee_aadhar);
        awesomeValidation1.addValidation(this, R.id.pmjjbynominee_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.pmsbynominee_name);
        //     awesomeValidation1.addValidation(this, R.id.pmsbynominee_relation, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.pmsbynominee_relation);
        awesomeValidation1.addValidation(this, R.id.pmjjbydateofbirth, "[0-3]{1}[0-9]{1}-[0-1]{1}[0-9]{1}-[1-2]{1}[0-9]{3}", R.string.pmsbydateofbirth);


        //spinner
        sp = (Spinner)findViewById(R.id.spinner2);
        sp.setPrompt("Select Nominee Relation");
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
        pmjjbyApi =retrofit.create(PmjjbyApi.class);

        pmjjbybutton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (v == pmjjbybutton) {
                    submitfor();

                }
            }

        });
    }

    private void submitfor() {
        if (awesomeValidation1.validate()){
            Pmjjby pmjjby = new Pmjjby(aadharrnumber.getText().toString(), accountnumber.getText().toString(), nomineeaadhar.getText().toString(),
                    nomineename.getText().toString(), selectedDataFromSpinner.toString(),  dateofbirth.getText().toString());
            createPmjjby(pmjjby);

        }
    }


    private void createPmjjby(Pmjjby pmjjby){
        Call<Pmjjby>  call = pmjjbyApi.createPmjjby(pmjjby);
        call.enqueue(new Callback<Pmjjby>() {
            @Override
            public void onResponse(Call<Pmjjby> call, Response<Pmjjby> response) {
                if (!response.isSuccessful()){

                }
                Pmjjby apiResponse=response.body();
                Intent intent=new Intent(PmjjbyData.this,Pmjjbysts.class);
                intent.putExtra("aadhar_number",apiResponse.getAadhar_number());
                startActivity(intent);

            }

            @Override
            public void onFailure(Call<Pmjjby> call, Throwable t) {
                //             Toast.makeText(this,"Server" ,Toast.LENGTH_LONG).show();

            }
        });
    }

}
