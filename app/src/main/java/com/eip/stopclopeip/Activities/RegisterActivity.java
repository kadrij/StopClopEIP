package com.eip.stopclopeip.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

public class RegisterActivity extends Activity {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.eip.stopclopeip.R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(com.eip.stopclopeip.R.color.colorAccent));
        }
    }

    public void datePicker(View view) {
        Calendar myCalendar = Calendar.getInstance();

        int myDay = myCalendar.get(Calendar.DAY_OF_MONTH);
        int myMonth = myCalendar.get(Calendar.MONTH);
        int myYear = myCalendar.get(Calendar.YEAR);

        final TextView myDateOfBirth = findViewById(com.eip.stopclopeip.R.id.birth_date_picker);

        DatePickerDialog myDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myDateOfBirth.setText(String.format("%02d-%02d-%d", day, month + 1, year));
            }
        }, myYear, myMonth, myDay);
        myDateDialog.show();
    }

    public void registerAccount(View view) {
        final AutoCompleteTextView mEmail = findViewById(com.eip.stopclopeip.R.id.email_input);
        final EditText mPassword = findViewById(com.eip.stopclopeip.R.id.password_input);
        final EditText mPasswordConf = findViewById(com.eip.stopclopeip.R.id.password_conf_input);
        final TextView mDateOfBirth = findViewById(com.eip.stopclopeip.R.id.birth_date_picker);
        final RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("Pass",mPassword.getText().toString());
        Log.d("Pass Conf",mPasswordConf.getText().toString());

        if (mEmail.getText().toString().isEmpty())
            mEmail.setError("Une adresse e-mail est requis");
        else if (mPassword.getText().toString().isEmpty())
            mPassword.setError("Un mot de passe est requis");
        else if (mPassword.getText().length() < 6)
            mPassword.setError("Le mot de passe doit avoir un minimum de 6 caracteres");
        else if (mDateOfBirth.getText().toString().equals("00-00-0000"))
            mDateOfBirth.setError("Une date de naissance correcte est requis");
        else if (!mPassword.getText().toString().equals(mPasswordConf.getText().toString()))
            mPassword.setError("Vous devez confirmer votre mot de passe");
        else {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                    + "&function=user.create&email="
                    + mEmail.getText().toString()
                    + "&password="
                    + mPassword.getText().toString()
                    + "&born="
                    + mDateOfBirth.getText().toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("reponse").equals("OK")) {
                            Alert("Compte créé");
                            finish();
                        } else {
                            Alert(jsonResponse.getString("reponse"));
                        }
                    } catch (JSONException e) {
                        Alert("Impossible de se connecter au serveur");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Alert("Impossible de se connecter au serveur");
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return null;
                }
            };
            queue.add(stringRequest);
            queue.start();
        }
    }

    public void Alert(String Msg) {
        Toast.makeText(RegisterActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }
}
