package com.eip.stopclopeip;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setTitle("Accueil");
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("token", getIntent().getStringExtra("token"));
        bundle.putString("email", getIntent().getStringExtra("email"));
        HomeFragment mFragment = new HomeFragment();
        mFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_scan) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toolbar toolbar = findViewById(R.id.toolbar);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("token", getIntent().getStringExtra("token"));
        bundle.putString("email", getIntent().getStringExtra("email"));

        if (id == R.id.nav_home) {
            toolbar.setTitle("Accueil");
            HomeFragment mFragment = new HomeFragment();
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();
        } else if (id == R.id.nav_button) {
            toolbar.setTitle("Bouton");
            ButtonFragment mFragment = new ButtonFragment();
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();
        } else if (id == R.id.nav_graph) {
            toolbar.setTitle("Statistiques");
            StatFragment mFragment = new StatFragment();
            mFragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, mFragment).commit();
        } else if (id == R.id.nav_advice) {

        } else if (id == R.id.nav_contacts) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showNotification(String Msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.stopclop_logo)
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
}
