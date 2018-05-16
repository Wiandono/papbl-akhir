/*
 * Copyright (c) 2018. This Code is created and Writed by Komang Candra Brata (k.candra.brata@ub.ac.id).
 * Inform the writer if you willing to edit or modify it for commercial purpose.
 */

package onex7.akhirbulanku;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Random;

import onex7.akhirbulanku.Model.TransaksiModel;

public class PeminjamanActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseTransaksi;

    private EditText editTextNama, editTextBank, editTextNoRek;
    private RadioGroup radioGroupPaket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjaman);

        editTextNama = findViewById(R.id.editTextNama);
        radioGroupPaket = findViewById(R.id.radioPaket);
        editTextBank = findViewById(R.id.editTextBank);
        editTextNoRek = findViewById(R.id.editTextNoRekening);
        Button buttonPinjam = findViewById(R.id.buttonPeminjaman);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseTransaksi = FirebaseDatabase.getInstance().getReference("transaction");

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.textView);
        navUsername.setText(user != null ? user.getEmail() : null);

        navigationView.setNavigationItemSelectedListener(this);

        buttonPinjam.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                tambahTransaksi();
            }
        });
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
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
            startActivity(new Intent(PeminjamanActivity.this, HomeActivity.class));
        } else if (id == R.id.nav_dashboard) {
            finish();
            startActivity(new Intent(PeminjamanActivity.this, DashboardActivity.class));
        } else if (id == R.id.nav_about) {
            finish();
            startActivity(new Intent(PeminjamanActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(PeminjamanActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void tambahTransaksi() {
        String paket;
        int selectedId = radioGroupPaket.getCheckedRadioButtonId();

        Random rnd = new Random();
        int kode = 100000 + rnd.nextInt(900000);
        Date jatuhTempo = new Date();

        RadioButton radioButtonPaket = findViewById(selectedId);

        if (radioButtonPaket.getText().toString().equals("Paket 16 SKS - Rp. 150.000")) {
            paket = "16";
        } else if (radioButtonPaket.getText().toString().equals("Paket 20 SKS - Rp. 300.000")) {
            paket = "20";
        } else {
            paket = "24";
        }

        if (paket.equals("16")) {
            jatuhTempo.setTime(jatuhTempo.getTime() + 14L * 24 * 60 * 60 * 1000);
        } else if (paket.equals("20")) {
            jatuhTempo.setTime(jatuhTempo.getTime() + 21L * 24 * 60 * 60 * 1000);
        } else {
            jatuhTempo.setTime(jatuhTempo.getTime() + 28L * 24 * 60 * 60 * 1000);
        }

        String nama = editTextNama.getText().toString();
        String bank = editTextBank.getText().toString();
        String noRek = editTextNoRek.getText().toString();

        if (!TextUtils.isEmpty(nama) && !TextUtils.isEmpty(bank) && !TextUtils.isEmpty(noRek)) {
            TransaksiModel transaksi = new TransaksiModel(firebaseAuth.getCurrentUser().getEmail(), String.valueOf(kode), nama, noRek, bank, paket, jatuhTempo.getTime(), "0");

            databaseTransaksi.child(String.valueOf(kode)).setValue(transaksi);

            editTextNama.setText("");
            editTextBank.setText("");
            editTextNoRek.setText("");

            finish();
            startActivity(new Intent(this, DashboardActivity.class));
        }

        Toast.makeText(this, "Peminjaman berhasil diajukan", Toast.LENGTH_SHORT).show();
    }
}