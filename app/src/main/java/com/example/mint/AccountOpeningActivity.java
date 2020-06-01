package com.example.mint;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountOpeningActivity extends AppCompatActivity {

    EditText dob;
    EditText age;
    EditText ifsc;
    EditText aadhar;
    EditText email;
    EditText mobile;
    EditText fatherName;
    EditText annualIncome;
    Button register;
    EditText area, city, state, pincode, narea, ncity, nstate, npincode;
    EditText fname, lname, nominee_fname, nominee_lname;
    DatePickerDialog.OnDateSetListener setListener;
    AccountDetails account;

    //for spinner
    Spinner sp;
    String selectedDataFromSpinner;
    String names[] = {"Father", "Mother", "Son", "Daughter"};
    ArrayAdapter<String> adapter;

    private AccountApi accountApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_account_opening);

        getSupportActionBar ().setTitle ("Account Opening Form");

        fname = findViewById (R.id.fname);
        lname = findViewById (R.id.lname);
        age = (EditText) findViewById (R.id.age);
        dob = findViewById (R.id.dob);
        ifsc = (EditText) findViewById (R.id.ifsc);
        aadhar = (EditText) findViewById (R.id.aadhar);
        email = (EditText) findViewById (R.id.email);
        mobile = (EditText) findViewById (R.id.mobile);
        fatherName = (EditText) findViewById (R.id.father_name);
        annualIncome = (EditText) findViewById (R.id.annual_income);
        nominee_fname = findViewById (R.id.nominee_fname);
        nominee_lname = findViewById (R.id.nominee_lname);
        register = (Button) findViewById (R.id.register);

        area = findViewById (R.id.area);
        city = findViewById (R.id.city);
        state = findViewById (R.id.state);
        pincode = findViewById (R.id.pincode);
        narea = findViewById (R.id.nominee_area);
        ncity = findViewById (R.id.nominee_city);
        nstate = findViewById (R.id.nominee_state);
        npincode = findViewById (R.id.nominee_pincode);

        //for spinner
        sp = (Spinner) findViewById (R.id.spinner);
        adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, names);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter (adapter);

        //for getting data from spinner
        sp.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedDataFromSpinner = parent.getItemAtPosition (position).toString ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //        retrofit builder
        Retrofit retrofit = new Retrofit.Builder ()
                .baseUrl ("http://192.168.42.20:8080/Mint/")
                .addConverterFactory (GsonConverterFactory.create ())
                .build ();
        accountApi = retrofit.create (AccountApi.class);


        //Register Button OnClick Action
        register.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (validate ()) {

                    String address = area.getText ().toString () + ", " + city.getText ().toString () + ", " + state.getText ().toString () + ", " + pincode.getText ().toString ();
                    String naddress = narea.getText ().toString () + ", " + ncity.getText ().toString () + ", " + nstate.getText ().toString () + ", " + npincode.getText ().toString ();
                    String name = fname.getText ().toString () + " " + lname.getText ().toString ();
                    String nomineeName = nominee_fname.getText ().toString () + " " + nominee_lname.getText ().toString ();

                    account = new AccountDetails (name, Integer.parseInt (age.getText ().toString ()),
                            dob.getText ().toString (), ifsc.getText ().toString (), aadhar.getText ().toString (), email.getText ().toString (),
                            mobile.getText ().toString (), fatherName.getText ().toString (), Integer.parseInt (annualIncome.getText ().toString ()),
                            address, nomineeName, selectedDataFromSpinner, naddress);

                    new MultiplyTask ().execute ();
//                    createAccount();
                }


            }
        });

        Calendar calender = Calendar.getInstance ();

        final int year = calender.get (Calendar.YEAR);
        final int month = calender.get (Calendar.MONTH);
        final int day = calender.get (Calendar.DAY_OF_MONTH);

        dob.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog (
                        getApplicationContext (), new DatePickerDialog.OnDateSetListener () {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        dob.setText (date);
                    }
                }, year, month, day);
                datePickerDialog.show ();
            }
        });


    }

    public class MultiplyTask extends AsyncTask<AccountDetails, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute ();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute (s);
        }

        @Override
        protected String doInBackground(AccountDetails... accounts) {
            Call<BankResponse> call = accountApi.createAccount (account);
            call.enqueue (new Callback<BankResponse> () {
                @Override
                public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                    String message = null;
                    BankResponse br = response.body ();

                    int status = br.getStatus ();
                    if (status == 200) {
                        AccountDetails ac = br.getAccountDetails ();
                        message = "Hi, " + ac.getName () + "\nyour acoount details is successfully submitted with \nArdaar No:"
                                + ac.getAadhar () + "\nMobile No:" + ac.getMobileNo () + "\nafter verification by the bank " +
                                "you will be notified by email";
                    }
                    if (status == 400) {
                        message = "Your ardhaar no is already registered with another account";
                    }

                    Intent intent = new Intent (getApplicationContext (), RegisterSuccess.class);
                    intent.putExtra ("message", message);
//                intent.putExtra ("name", account.getName());
//                intent.putExtra ("mobile", account.getMobileNo());
//                intent.putExtra ("aadhar",account.getAadhar());

                    startActivity (intent);
                }

                @Override
                public void onFailure(Call<BankResponse> call, Throwable t) {
                    Toast.makeText (getApplicationContext (), "Server is not working now!", Toast.LENGTH_SHORT).show ();
                }
            });
            return null;
        }
    }

    //    private void createAccount() {
////        Account account = new Account("avik",20,"20/10/1994","FD251425","258798745687","a","8","B",50,"KOL","B","father","KOL");
//
//        Call<BankResponse> call = accountApi.createAccount(account);
//        call.enqueue(new Callback<BankResponse>() {
//            @Override
//            public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
//                String message = null;
//                BankResponse br = response.body();
//
//                int status = br.getStatus();
//                if (status == 200) {
//                    Account ac = br.getAccountDetails();
//                    message = "Hi, " + ac.getName() + "\nyour acoount details is successfully submitted with \nArdaar No:"
//                            + ac.getAadhar() + "\nMobile No:" + ac.getMobileNo()+"\nafter verification by the bank " +
//                            "you will be notified by email";
//                }
//                if(status == 400) {
//                    message = "Your ardhaar no is already registered with another account";
//                }
//
//                Intent intent = new Intent(getApplicationContext(), RegisterSuccess.class);
//                intent.putExtra("message", message);
////                intent.putExtra ("name", account.getName());
////                intent.putExtra ("mobile", account.getMobileNo());
////                intent.putExtra ("aadhar",account.getAadhar());
//
//                startActivity(intent);
//            }
//
//            @Override
//            public void onFailure(Call<BankResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(),"Server is not working now!",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private Boolean validate() {

        boolean isError = true;

        if (fname.getText ().toString ().isEmpty ()) {
            isError = false;
            fname.setError ("First Name should not be empty");
            fname.requestFocus ();
        }
        if (lname.getText ().toString ().isEmpty ()) {
            isError = false;
            lname.setError ("Last Name should not be empty");
            lname.requestFocus ();
        }
        if (age.getText ().toString ().isEmpty () || age.getText ().toString ().length () <= 0) {
            isError = false;
            age.setError ("Age should not be empty or zero ");
            age.requestFocus ();
        }
        if (dob.getText ().toString ().isEmpty ()) {
            isError = false;
            dob.setError ("DOB should not be empty ");
            dob.requestFocus ();

        }
        if (!dob.getText ().toString ().isEmpty ()) {
            try {
                Date inputDate = new SimpleDateFormat ("dd/MM/yyyy").parse (dob.getText ().toString ());
                Date currentDate = new Date ();
                if (currentDate.before (inputDate)) {
                    isError = false;
                    dob.setError ("DOB should be lesser than Current Date ");
                    dob.requestFocus ();
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }

        }
        if (ifsc.getText ().toString ().isEmpty ()) {
            isError = false;
            ifsc.setError ("ifsc should not be empty ");
            ifsc.requestFocus ();
        }
        if (aadhar.getText ().toString ().isEmpty () || aadhar.getText ().toString ().length () < 12) {
            isError = false;
            aadhar.setError ("Enter a valid aadhar number ");
            aadhar.requestFocus ();
        }
        if (email.getText ().toString ().isEmpty ()) {
            isError = false;
            email.setError ("email should not be empty ");
            email.requestFocus ();
        }
        if (mobile.getText ().toString ().isEmpty ()) {
            isError = false;
            mobile.setError ("mobile should not be empty ");
            mobile.requestFocus ();
        }
        if (fatherName.getText ().toString ().isEmpty ()) {
            isError = false;
            fatherName.setError ("Father Name should not be empty ");
            fatherName.requestFocus ();
        }
        if (annualIncome.getText ().toString ().isEmpty ()) {
            isError = false;
            annualIncome.setError ("Annual Income should not be empty ");
            annualIncome.requestFocus ();
        }
        if (area.getText ().toString ().isEmpty ()) {
            isError = false;
            area.setError ("Address should not be empty ");
            area.requestFocus ();
        }
        if (city.getText ().toString ().isEmpty ()) {
            isError = false;
            city.setError ("Address should not be empty ");
            city.requestFocus ();
        }
        if (state.getText ().toString ().isEmpty ()) {
            isError = false;
            state.setError ("Address should not be empty ");
            state.requestFocus ();
        }
        if (pincode.getText ().toString ().isEmpty ()) {
            isError = false;
            pincode.setError ("Address should not be empty ");
            pincode.requestFocus ();
        }

        if (nominee_fname.getText ().toString ().isEmpty ()) {
            isError = false;
            nominee_fname.setError ("First Name should not be empty");
            nominee_fname.requestFocus ();
        }
        if (nominee_lname.getText ().toString ().isEmpty ()) {
            isError = false;
            nominee_lname.setError ("Last Name should not be empty");
            nominee_lname.requestFocus ();
        }
        if (narea.getText ().toString ().isEmpty ()) {
            isError = false;
            narea.setError ("Address should not be empty ");
            narea.requestFocus ();
        }
        if (ncity.getText ().toString ().isEmpty ()) {
            isError = false;
            ncity.setError ("Address should not be empty ");
            ncity.requestFocus ();
        }
        if (nstate.getText ().toString ().isEmpty ()) {
            isError = false;
            nstate.setError ("Address should not be empty ");
            nstate.requestFocus ();
        }
        if (npincode.getText ().toString ().isEmpty ()) {
            isError = false;
            npincode.setError ("Address should not be empty ");
            npincode.requestFocus ();
        }

        return isError;
    }
}