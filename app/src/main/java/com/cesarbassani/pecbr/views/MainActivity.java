package com.cesarbassani.pecbr.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.config.GlideApp;
import com.cesarbassani.pecbr.model.Abate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ViewHolder mViewHolder = new ViewHolder();
    FloatingActionButton fabBtn;
    private FirebaseAuth auth;
    private SearchView searchView;
    private List<Abate> listaAbates;
//    private MaterialSearchView searchView;
    private ListaAbatesFragment listaAbatesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = ConfiguracaoFirebase.getDatabase();
        database.getReference("abates").keepSynced(true);

        fabBtn = findViewById(R.id.float_add_guest);
        this.mViewHolder.mActionBar = getSupportActionBar();

        this.mViewHolder.mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        //Init Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        //check already session, if ok-> Dashboard
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        mViewHolder.navigationHeaderView = (LinearLayout) navigationView.getHeaderView(0);
        mViewHolder.mEmail = mViewHolder.navigationHeaderView.findViewById(R.id.profile_email);
        mViewHolder.mName = mViewHolder.navigationHeaderView.findViewById(R.id.profile_name);
        mViewHolder.avatar = mViewHolder.navigationHeaderView.findViewById(R.id.avatar);

        //Recuperar dados do usuário
//        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = user.getPhotoUrl();

        if (url != null) {

            GlideApp.with(this.getApplicationContext())
                    .load(url.toString())
                    .into(mViewHolder.avatar);
        } else {
            mViewHolder.avatar.setImageResource(R.drawable.padrao);
        }

        mViewHolder.mName.setText(user.getDisplayName());
        mViewHolder.mEmail.setText(user.getEmail());

        this.setListeners();

        String menuFragment = getIntent().getStringExtra("menuFragment");
        abrirFragmentNotification(menuFragment);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("FIREBASE", "Key: " + key + " Value: " + value);
            }
        }
    }

    private void setListeners() {
        fabBtn.setOnClickListener(this);
    }

    private void startDefaultFragment() {
        Fragment fragment = null;
        Class fragmentClass = ListaAbatesFragment.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            doExitApp();
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Pressione novamente para sair do aplicativo!", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            finish();
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_lista_abates, menu);

//        MenuItem searchItem = menu.findItem(R.id.menu_pesquisa);
//
//        searchView = (SearchView) searchItem.getActionView();
//        searchView.setQueryHint("Buscar Abates");
//        searchView.setOnQueryTextListener(this);
//        searchView.setIconified(true);

        return super.onCreateOptionsMenu(menu);
    }

//    private void ShowKeyBoard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        }
//    }
//
//    private void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.menu_pesquisa) {
//
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        Class fragmentClass = null;

        int id = item.getItemId();

        if (id == R.id.nav_resumo_abate) {
            fragmentClass = ListaAbatesFragment.class;
        } else if (id == R.id.nav_escala_abate) {
            fragmentClass = PresentFragment.class;
        } else if (id == R.id.nav_relatorios) {
            fragmentClass = CotacoesFragment.class;
        } else if (id == R.id.nav_config) {
            fragmentClass = ConfigFragment.class;
        }

        try {
            if (id == R.id.nav_logout) {
                logoutUser();
            } else {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null)
            fragmentManager.beginTransaction().replace(R.id.frame_content, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.float_add_guest) {
            Intent guestForm = new Intent(this, AbateFormActivity.class);
            this.startActivity(guestForm);
        }
    }

    public FloatingActionButton getFloatingActionButton() {
        return fabBtn;
    }

    private void logoutUser() {
        auth.signOut();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

//    @Override
//    public boolean onQueryTextSubmit(String query) {
//        Toast.makeText(this, "Query Inserted", Toast.LENGTH_SHORT).show();
//        return true;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String newText) {
//        String textoDigitado = newText.toUpperCase();
//        pesquisarAbates(textoDigitado);
//
//        return true;
//    }

    private static class ViewHolder {
        private ActionBar mActionBar;
        private TextView mEmail;
        private TextView mName;
        private FirebaseAuth mAuth;
        private LinearLayout navigationHeaderView;
        private CircleImageView avatar;
    }

    private void abrirFragmentNotification(String menuFragment) {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // If menuFragment is defined, then this activity was launched with a fragment selection
        if (menuFragment != null) {

            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (menuFragment.equals("CotacoesFragment")) {
                Fragment fragment = null;
                Class fragmentClass = CotacoesFragment.class;

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

//                CotacoesFragment cotacoesFragment = new CotacoesFragment();
                fragmentTransaction.replace(android.R.id.content, fragment).commit();
            }
        } else {
            // Activity was not launched with a menuFragment selected -- continue as if this activity was opened from a launcher (for example)
//            ListaAbatesFragment listaAbatesFragment = new ListaAbatesFragment();
//            fragmentTransaction.replace(android.R.id.content, listaAbatesFragment);

            this.startDefaultFragment();
        }
    }
}
