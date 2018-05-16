package onex7.akhirbulanku;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import onex7.akhirbulanku.Model.TransaksiModel;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private TransaksiModel transaksiModel;
    private ArrayList<TransaksiModel> transaksiList = new ArrayList<TransaksiModel>();

    Query transaksi;

    int besarPinjaman, biayaLayanan, biayaTransfer, biayaDenda, jumlahHariDenda, totalBiaya;

    DatabaseReference databaseTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final TextView textViewKodeTransaksi = findViewById(R.id.kode_transaksi);
        final TextView textViewNama = findViewById(R.id.nama_debitur);
        final TextView textViewBank = findViewById(R.id.bank);
        final TextView textViewTanggalJatuhTempo = findViewById(R.id.tanggal_jatuh_tempo);
        final TextView textViewBesarPinjaman = findViewById(R.id.besar_pinjaman);
        final TextView textViewBiayaLayanan = findViewById(R.id.biaya_layanan);
        final TextView textViewBiayaTransfer = findViewById(R.id.biaya_transfer);
        final TextView textViewDenda = findViewById(R.id.denda);
        final TextView textViewTotal = findViewById(R.id.total);
        final Button buttonBatal = findViewById(R.id.buttonBatal);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseTransaksi = FirebaseDatabase.getInstance().getReference("transaction");
        transaksi = databaseTransaksi.orderByChild("kode");

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

        transaksi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (String.valueOf(postSnapshot.child("user").getValue()).equals(firebaseAuth.getCurrentUser().getEmail()) && String.valueOf(postSnapshot.child("status").getValue()).equals("0")) {
                        transaksiModel = postSnapshot.getValue(TransaksiModel.class);
                    } else if (String.valueOf(postSnapshot.child("user").getValue()).equals(firebaseAuth.getCurrentUser().getEmail()) && String.valueOf(postSnapshot.child("status").getValue()).equals("1")) {
                        transaksiModel = null;
                    }
                }

                if (transaksiModel != null) {
                    Date now = new Date();
                    Date jatuhTempo = new Date(transaksiModel.getJatuhTempo());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");

                    if (transaksiModel.getPaket().equals("16")) {
                        besarPinjaman = 150000;
                        biayaLayanan = 25000;
                    } else if (transaksiModel.getPaket().equals("20")) {
                        besarPinjaman = 300000;
                        biayaLayanan = 45000;
                    } else {
                        besarPinjaman = 500000;
                        biayaLayanan = 70000;
                    }

                    if (transaksiModel.getBank().equals("BNI")) {
                        biayaTransfer = 0;
                    } else {
                        biayaTransfer = 7500;
                    }

                    if (now.compareTo(jatuhTempo) > 0) {
                        biayaDenda = 3000 * now.compareTo(jatuhTempo);
                        jumlahHariDenda = now.compareTo(jatuhTempo);
                    } else {
                        biayaDenda = 0;
                        jumlahHariDenda = 0;
                    }

                    totalBiaya = besarPinjaman + biayaLayanan + biayaTransfer + biayaDenda;

                    textViewKodeTransaksi.setText(transaksiModel.getKode());
                    textViewNama.setText(transaksiModel.getNama());
                    textViewBank.setText(transaksiModel.getNorek() + " (" + transaksiModel.getBank() + ")");
                    textViewTanggalJatuhTempo.setText(dateFormat.format(jatuhTempo));
                    textViewBesarPinjaman.setText("Rp. " + besarPinjaman);
                    textViewBiayaLayanan.setText("Rp. " + biayaLayanan);
                    textViewBiayaTransfer.setText("Rp. " + biayaTransfer);
                    textViewDenda.setText("Rp. " + biayaDenda + " (" + jumlahHariDenda + " Hari)");
                    textViewTotal.setText("Rp. " + totalBiaya);
                    buttonBatal.setText(R.string.button_batalkan);

                    buttonBatal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            batalTransaksi(transaksiModel.getKode());
                        }
                    });
                } else {
                    textViewKodeTransaksi.setText(R.string.dashboard_belum_transaksi);
                    textViewNama.setText("-");
                    textViewBank.setText("-");
                    textViewTanggalJatuhTempo.setText("-");
                    textViewBesarPinjaman.setText(R.string.rupiah);
                    textViewBiayaLayanan.setText(R.string.rupiah);
                    textViewBiayaTransfer.setText(R.string.rupiah);
                    textViewDenda.setText(R.string.rupiah);
                    textViewTotal.setText(R.string.rupiah);
                    buttonBatal.setText(R.string.button_ajukan_peminjaman);

                    buttonBatal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(DashboardActivity.this, PeminjamanActivity.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            startActivity(new Intent(DashboardActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_dashboard) {

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(DashboardActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void batalTransaksi(String id) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("transaction").child(id);

        dR.removeValue();

        finish();
        startActivity(new Intent(DashboardActivity.this, HomeActivity.class));

        Toast.makeText(this, "Transaksi berhasil dibatalkan", Toast.LENGTH_SHORT).show();
    }
}
