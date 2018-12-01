package com.eip.stopclopeip.Activities;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eip.stopclopeip.Achievment.AchievmentFragment;
import com.eip.stopclopeip.Advice.AdviceFragment;
import com.eip.stopclopeip.Button.ButtonFragment;
import com.eip.stopclopeip.Contact.ContactFragment;
import com.eip.stopclopeip.Home.HomeFragment;
import com.eip.stopclopeip.R;
import com.eip.stopclopeip.Stat.StatFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends /*BlunoLibrary*/ AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String url = "http://romain-caldas.fr/api/rest.php?dev=69";
    private double lat = 0, lon = 0;
    private boolean onButtonFragment = false;
    private LocationManager locationManager;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.eip.stopclopeip.R.layout.activity_main);
        toolbar = findViewById(com.eip.stopclopeip.R.id.toolbar);
        toolbarTitle = toolbar.findViewById(com.eip.stopclopeip.R.id.toolbar_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }

        /*Intent zone_service = new Intent("com.hayes.android.MyService");
        zone_service.putExtra("email", getIntent().getStringExtra("email"));
        zone_service.putExtra("token", getIntent().getStringExtra("token"));
        startService(zone_service);*/

        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.BlackButton));
        }

        /*
        BLE:

        onCreateProcess();
        serialBegin(115200);*/

        statusCheck();

        DrawerLayout drawer = findViewById(com.eip.stopclopeip.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.eip.stopclopeip.R.string.navigation_drawer_open, com.eip.stopclopeip.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(com.eip.stopclopeip.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbarTitle.setText("Accueil");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("token", getIntent().getStringExtra("token"));
        bundle.putString("email", getIntent().getStringExtra("email"));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        HomeFragment mFragment = new HomeFragment();
        mFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(com.eip.stopclopeip.R.id.content_frame, mFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(com.eip.stopclopeip.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.eip.stopclopeip.R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*
        BLE

        if (id == R.id.action_scan) {
            buttonScanOnClickProcess();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        onButtonFragment = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("token", getIntent().getStringExtra("token"));
        bundle.putString("email", getIntent().getStringExtra("email"));

        Fragment mFragment = null;
        if (id == com.eip.stopclopeip.R.id.nav_home) {
            toolbarTitle.setText("Accueil");
            mFragment = new HomeFragment();
        } else if (id == com.eip.stopclopeip.R.id.nav_button) {
            toolbarTitle.setText("Boutons");
            onButtonFragment = true;
            mFragment = new ButtonFragment();
        } else if (id == com.eip.stopclopeip.R.id.nav_graph) {
            toolbarTitle.setText("Statistiques");
            mFragment = new StatFragment();
        } else if (id == com.eip.stopclopeip.R.id.nav_advice) {
            toolbarTitle.setText("Conseils");
            mFragment = new AdviceFragment();
        } else if (id == com.eip.stopclopeip.R.id.nav_contacts) {
            toolbarTitle.setText("Contacts");
            mFragment = new ContactFragment();
        } else if (id == R.id.nav_achievment) {
            toolbarTitle.setText("Succès");
            mFragment = new AchievmentFragment();
        } else if (id == com.eip.stopclopeip.R.id.nav_disconnect) {
            disconnect();
        }

        if (mFragment != null) {
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(com.eip.stopclopeip.R.id.content_frame, mFragment).commit();
        }
        DrawerLayout drawer = findViewById(com.eip.stopclopeip.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendObjectPression(final String color) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            statusCheck();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (lon == 0 || lat == 0) {
                                Alert("L'application n'est pas parvenu à obtenir votre position.");
                                return;
                            }
                            Log.v("Lon", "" + lon);
                            addToCount(color);
                            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                                    + "&function=coordonnee.add&email="
                                    + getIntent().getStringExtra("email")
                                    + "&token="
                                    + getIntent().getStringExtra("token")
                                    + "&x="
                                    + lon
                                    + "&y="
                                    + lat
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
                                    reduceToCount(color);
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
                    });
                }
            }, 250);
        }
    }

    private void addToCount(String button) {
        TextView red_count = findViewById(com.eip.stopclopeip.R.id.red_count);
        TextView blue_count = findViewById(com.eip.stopclopeip.R.id.blue_count);
        TextView black_count = findViewById(com.eip.stopclopeip.R.id.black_count);

        if (button.equals("RED"))
            red_count.setText("" + (Integer.valueOf(red_count.getText().toString()) + 1));
        else if (button.equals("BLUE"))
            blue_count.setText("" + (Integer.valueOf(blue_count.getText().toString()) + 1));
        else if (button.equals("BLACK"))
            black_count.setText("" + (Integer.valueOf(black_count.getText().toString()) + 1));
    }

    private void reduceToCount(String button) {
        TextView red_count = findViewById(com.eip.stopclopeip.R.id.red_count);
        TextView blue_count = findViewById(com.eip.stopclopeip.R.id.blue_count);
        TextView black_count = findViewById(com.eip.stopclopeip.R.id.black_count);

        if (button.equals("RED"))
            red_count.setText("" + (Integer.valueOf(red_count.getText().toString()) - 1));
        else if (button.equals("BLUE"))
            blue_count.setText("" + (Integer.valueOf(blue_count.getText().toString()) - 1));
        else if (button.equals("BLACK"))
            black_count.setText("" + (Integer.valueOf(black_count.getText().toString()) - 1));
    }

    public void showNotification(String Msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.eip.stopclopeip.R.drawable.stopclop_logo)
                .setContentTitle("StopClop\'")
                .setContentText(Msg);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(Msg);

        Intent resultIntent = new Intent(this, MainActivity.class);
        mBuilder.setStyle(inboxStyle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public void Alert(String Msg) {
        Toast.makeText(this, Msg, Toast.LENGTH_SHORT).show();
    }

    public void statusCheck() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            }} catch (NullPointerException e) {}
    }

    public void disconnect() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            lon = location.getLongitude();
            lat = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    /*@Override
    protected void onResume() {
        super.onResume();
        onResumeProcess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();
    }

    @Override
    public void onConectionStateChange(BlunoLibrary.connectionStateEnum theConnectionState) {
        switch (theConnectionState) {
            case isConnected:

                break;
            case isConnecting:
                break;
            case isToScan:
                break;
            case isScanning:
                break;
            case isDisconnecting:
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString) {
        if (theString.equals("red"))
            sendObjectPression("RED");
        else if (theString.equals("blue"))
            sendObjectPression("BLUE");
        else if (theString.equals("black"))
            sendObjectPression("BLACK");
    }*/
}


        /*final RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                + "&function=coordonnee.get&email="
                + getIntent().getStringExtra("email")
                + "&token="
                + getIntent().getStringExtra("token")
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
                            TextView red_count = findViewById(R.id.red_count);
                            TextView blue_count = findViewById(R.id.blue_count);
                            TextView black_count = findViewById(R.id.black_count);
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
                                    if (userData.getString("button").equals("BLUE"))
                                        blue++;
                                    else if (userData.getString("button").equals("RED"))
                                        red++;
                                    else
                                        black++;
                                }
                            }

                            red_count.setText("" + red);
                            blue_count.setText("" + blue);
                            black_count.setText("" + black);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
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
                Map<String, String> params = new HashMap();
                params.put("myData", "{}");
                return params;
            }
        };
        queue.add(stringRequest);
        queue.start();*/
