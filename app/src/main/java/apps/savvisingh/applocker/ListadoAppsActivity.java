package apps.savvisingh.applocker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import apps.savvisingh.applocker.Fragments.todasAppFragment;
import apps.savvisingh.applocker.Utils.CONSTANTES;

public class ListadoAppsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = getSharedPreferences(CONSTANTES.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fragmentManager = getSupportFragmentManager();

        getSupportActionBar().setTitle(R.string.TOD_APP);
        Fragment f = todasAppFragment.newInstance(CONSTANTES.ALL_APPS);
        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getCurrentFragment() instanceof todasAppFragment) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
                getSupportActionBar().setTitle(R.string.TOD_APP);
                Fragment f = todasAppFragment.newInstance(CONSTANTES.ALL_APPS);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
            }
        }
    }

    /**
     * Returns currentfragment
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_apps) {
            getSupportActionBar().setTitle(R.string.TOD_APP);
            Fragment f = todasAppFragment.newInstance(CONSTANTES.ALL_APPS);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
        } else if (id == R.id.nav_locked_apps) {
            getSupportActionBar().setTitle(R.string.LOCK_APP);
            Fragment f = todasAppFragment.newInstance(CONSTANTES.BLOQUEADO);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();


        } else if (id == R.id.nav_unlocked_apps) {
            getSupportActionBar().setTitle(R.string.UNLOCK_APP);
            Fragment f = todasAppFragment.newInstance(CONSTANTES.NOBLOQUEADO);
            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
