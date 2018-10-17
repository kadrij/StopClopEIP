package com.eip.stopclopeip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.internal.zzahf.runOnUiThread;

public class ButtonFragment extends Fragment {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private ProgressBar mProgress;
    private ConstraintLayout mButtonForm;
    private ConstraintLayout mErrorForm;
    private FloatingActionButton red_button;
    private FloatingActionButton blue_button;
    private FloatingActionButton black_button;
    private LocationManager locationManager;

    public ButtonFragment() {
    }

    public static ButtonFragment newInstance() {
        ButtonFragment fragment = new ButtonFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        red_button = view.findViewById(R.id.red_button);
        blue_button = view.findViewById(R.id.blue_button);
        black_button = view.findViewById(R.id.black_button);
        mProgress = view.findViewById(R.id.progressBar);
        mButtonForm = view.findViewById(R.id.button_form);
        mErrorForm = view.findViewById(R.id.error_form);

        showProgress(true);
        getCount(view);
        statusCheck();

        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPression(view, "RED");
            }
        });

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPression(view, "BLUE");
            }
        });

        black_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPression(view, "BLACK");
            }
        });
    }

    private void buttonDelay() {
        red_button.setEnabled(false);
        blue_button.setEnabled(false);
        black_button.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        red_button.setEnabled(true);
                        blue_button.setEnabled(true);
                        black_button.setEnabled(true);
                    }
                });
            }
        }, 750);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Votre GPS semble désactivé, pour le bon fonctionnement de l'application, il est préférable de l'activer.")
                .setCancelable(false)
                .setPositiveButton("Activer", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendPression(final View view, final String color){
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Alert("Veuillez activer votre GPS pour le bon fonctionnement de l'application.");
        } else {
            buttonDelay();
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location == null) {
                Alert("Veuillez activer votre GPS pour le bon fonctionnement de l'application.");
                return;
            }
            addToCount(view, color);
            final RequestQueue queue = Volley.newRequestQueue(this.getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                    + "&function=coordonnee.add&email="
                    + getArguments().getString("email")
                    + "&token="
                    + getArguments().getString("token")
                    + "&x="
                    + location.getLongitude()
                    + "&y="
                    + location.getLatitude()
                    +"&button="
                    + color,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Alert("Impossible de se connecter au serveur");
                    reduceToCount(view, color);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap();
                    params.put("myData", "{}");
                    return params;
                }
            };
            queue.add(stringRequest);
            queue.start();
        }
        //getCount(view);
    }

    private void addToCount(View view, String button) {
        final TextView red_count = view.findViewById(R.id.red_count);
        final TextView blue_count = view.findViewById(R.id.blue_count);
        final TextView black_count = view.findViewById(R.id.black_count);

        if (button.equals("RED"))
            red_count.setText("" + (Integer.parseInt(red_count.getText().toString()) + 1));
        else if (button.equals("BLUE"))
            blue_count.setText("" + (Integer.parseInt(blue_count.getText().toString()) + 1));
        else if (button.equals("BLACK"))
            black_count.setText("" + (Integer.parseInt(black_count.getText().toString()) + 1));
    }

    private void reduceToCount(View view, String button) {
        final TextView red_count = view.findViewById(R.id.red_count);
        final TextView blue_count = view.findViewById(R.id.blue_count);
        final TextView black_count = view.findViewById(R.id.black_count);

        if (button.equals("RED"))
            red_count.setText("" + (Integer.parseInt(red_count.getText().toString()) - 1));
        else if (button.equals("BLUE"))
            blue_count.setText("" + (Integer.parseInt(blue_count.getText().toString()) - 1));
        else if (button.equals("BLACK"))
            black_count.setText("" + (Integer.parseInt(black_count.getText().toString()) - 1));
    }

    private void getCount(final View view) {
        final RequestQueue queue = Volley.newRequestQueue(this.getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=coordonnee.get&email="
                + getArguments().getString("email")
                + "&token="
                + getArguments().getString("token")
                + "&time=1j",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            TextView red_count = view.findViewById(R.id.red_count);
                            TextView blue_count = view.findViewById(R.id.blue_count);
                            TextView black_count = view.findViewById(R.id.black_count);
                            Date currentDate = new Date();
                            Date date;
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                            String data = jsonResponse.getString("data");
                            JSONArray userArray = new JSONArray(data);

                            int red = 0;
                            int blue = 0;
                            int black = 0;

                            for (int i = 0; i < userArray.length(); i++) {
                                JSONObject userData = userArray.getJSONObject(i);
                                date = format.parse(userData.getString("date"));
                                long diff = Math.abs(currentDate.getTime() - date.getTime());
                                int day = Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toDays(diff)));
                                if (TimeUnit.MILLISECONDS.toDays(diff) == 0) {
                                    if (userData.getString("button").equals("BLUE") && blue <= 99999)
                                        blue++;
                                    else if (userData.getString("button").equals("RED") && red <= 99999)
                                        red++;
                                    else if (userData.getString("button").equals("BLACK") && black <= 99999)
                                        black++;
                                }
                            }

                            red_count.setText("" + red);
                            blue_count.setText("" + blue);
                            black_count.setText("" + black);
                            showProgress(false);
                        } catch (JSONException e) {
                            showProgress(false);
                            e.printStackTrace();
                        } catch (ParseException e) {
                            showProgress(false);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                showError(true);
                Alert("Impossible de se connecter au serveur");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("myData", "{}");
                return params;
            }
        };
        queue.add(stringRequest);
        queue.start();
    }

    void showProgress(final boolean show) {
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mButtonForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } catch (IllegalStateException e) {
        System.out.println(e);
        }
    }

    void showError(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mButtonForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mButtonForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mErrorForm.setVisibility(show ? View.VISIBLE : View.GONE);
        mErrorForm.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mErrorForm.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void Alert(String Msg) {
        Toast.makeText(this.getActivity(), Msg, Toast.LENGTH_SHORT).show();
    }
}
