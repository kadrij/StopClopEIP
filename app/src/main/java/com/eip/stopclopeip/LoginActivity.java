package com.eip.stopclopeip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
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

public class LoginActivity extends Activity {
    String url = "http://romain-caldas.fr/api/rest.php?dev=69";

    private AutoCompleteTextView mEmail;
    private EditText mPassword;
    private ProgressBar mProgress;
    private ScrollView mLoginForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final RequestQueue queue = Volley.newRequestQueue(this);

        mProgress = findViewById(R.id.login_progress_bar);
        mLoginForm = findViewById(R.id.login_form);
        mEmail = findViewById(R.id.email_input);
        mPassword = findViewById(R.id.password_input);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final Boolean[] logError = {false};

        final Button mEmailSignInButton = findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                if (mEmail.getText().toString().isEmpty() && mPassword.getText().toString().isEmpty() && logError[0].equals(false)) {
                    mEmail.setError("Une adresse mail est requise");
                    mPassword.setError("Un mot de passe est requis");
                    showProgress(false);
                    logError[0] = true;
                } else if (mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty() && logError[0].equals(false)) {
                    mEmail.setError("Une adresse mail est requise");
                    showProgress(false);
                    logError[0] = true;
                } else if (!mEmail.getText().toString().isEmpty() && mPassword.getText().toString().isEmpty() && logError[0].equals(false)) {
                    mPassword.setError("Un mot de passe est requis");
                    showProgress(false);
                    logError[0] = true;
                } else {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                            + "&function=user.connection&email="
                            + mEmail.getText().toString()
                            + "&password="
                            + mPassword.getText().toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonResponse = null;

                            try {
                                jsonResponse = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                JSONObject jsonData = new JSONObject(jsonResponse.getString("data"));
                                if (jsonResponse.getString("reponse").equals("OK")) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("token", jsonData.getString("token"));
                                    intent.putExtra("email", mEmail.getText().toString());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    mEmail.setError("Adresse mail ou mot de passe incorrecte");
                                    showProgress(false);
                                }
                            } catch (JSONException e) {
                                Alert("Impossible de se connecter au serveur");
                                showProgress(false);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Alert("Impossible de se connecter au serveur");
                            showProgress(false);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            return null;
                        }
                    };
                    queue.add(stringRequest);
                    queue.start();
                    logError[0] = false;
                }
            }
        });
    }

    void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
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