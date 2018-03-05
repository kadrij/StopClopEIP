package com.eip.stopclopeip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class LoginActivity extends Activity
{
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ProgressBar mProgressView;
    private ScrollView mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "http://romain-caldas.fr/api/rest.php?dev=69";

        mProgressView = findViewById(R.id.login_progress_bar);
        mLoginFormView = findViewById(R.id.login_form);
        mEmailView = findViewById(R.id.email_input);
        mPasswordView = findViewById(R.id.password_input);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Boolean[] logError = {false};

        TextView textViewCustom = findViewById(R.id.title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/stopclop_font.ttf");
        textViewCustom.setTypeface(custom_font);

        final Button mEmailSignInButton = findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);

                Log.v("Login", "Email = " + mEmailView.getText().toString() + " | Password = " + mPasswordView.getText().toString());

                if (mEmailView.getText().toString().isEmpty() && mPasswordView.getText().toString().isEmpty() && logError[0].equals(false)) {
                    mEmailView.setError("An Email or Username is requiered");
                    mPasswordView.setError("A password is requiered");
                    Alert("Login and password requiered.");
                    showProgress(false);
                    logError[0] = true;
                } else if (mEmailView.getText().toString().isEmpty() && !mPasswordView.getText().toString().isEmpty() && logError[0].equals(false)) {
                    mEmailView.setError("An Email or Username is requiered");
                    Alert("An Email or Username is requiered.");
                    showProgress(false);
                    logError[0] = true;
                } else if (!mEmailView.getText().toString().isEmpty() && mPasswordView.getText().toString().isEmpty() && logError[0].equals(false)) {
                    mPasswordView.setError("A password is requiered");
                    Alert("A password is requiered.");
                    showProgress(false);
                    logError[0] = true;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                        + "&function=user.connection&email="
                        + mEmailView.getText().toString()
                        + "&password="
                        + mPasswordView.getText().toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(response);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject jsonData = new JSONObject(jsonResponse.getString("data"));
                            if (jsonResponse.getString("reponse").equals("OK")) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("token", jsonData.getString("token"));
                                intent.putExtra("email", mEmailView.getText().toString());
                                showProgress(false);
                                startActivity(intent);
                            }
                            else {
                                mEmailView.setError("Adresse mail ou mot de passe incorrecte");
                                showProgress(false);
                            }
                        }
                        catch (JSONException e) {
                            Alert("Cannot connect to server.");
                            showProgress(false);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Alert("Cannot connect to server.");
                        showProgress(false);
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() {
                        return null;
                    }
                };
                queue.add(stringRequest);
                queue.start();
                logError[0] = false;
            }
        });
    }

    void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    void SignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    void Alert(String Msg) {
        Toast.makeText(LoginActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }
}