/*
 * Copyright (c) 2018. This Code is created and Writed by Komang Candra Brata (k.candra.brata@ub.ac.id).
 * Inform the writer if you willing to edit or modify it for commercial purpose.
 */

package onex7.akhirbulanku;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PinjamActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText editTextnamaDebitur, editTextNamaIbu, editTextNamaAyah, editTextNoHP, editTextLine, editTextAlamat, editTextTujuan, editTextnamaRekening, editTextbankTujuan, editTextnomorRekening;
    private String namaDebitur, namaIbu, namaAyah, noHP, Line, Alamat, Tujuan, Paket, namaRekening, bankTujuan, noRekening;
    private RadioButton radioButtonPaket1, radioButtonPaket2, radioButtonPaket3;
    private Button buttonAjukanPeminjaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjam);

        editTextnamaDebitur = findViewById(R.id.editTextDataDebitur);
        editTextNamaIbu = findViewById(R.id.editTextIbu);
        editTextNamaAyah = findViewById(R.id.editTextAyah);
        editTextNoHP = findViewById(R.id.editTextNoHp);
        editTextLine = findViewById(R.id.editTextLine);
        editTextAlamat = findViewById(R.id.editTextAlamatDebitur);
        radioButtonPaket1= findViewById(R.id.radioPaket1);
        radioButtonPaket2= findViewById(R.id.radioPaket2);
        radioButtonPaket3= findViewById(R.id.radioPaket3);
        editTextTujuan = findViewById(R.id.editTextTujuan);
        editTextnamaRekening = findViewById(R.id.editTextNamaRekening);
        editTextbankTujuan = findViewById(R.id.editTextBank);
        editTextnomorRekening = findViewById(R.id.editTextNoRekening);
        buttonAjukanPeminjaman = findViewById(R.id.buttonPeminjaman);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        Button buttonLogout = findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logging out the user
                firebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(PinjamActivity.this, LoginActivity.class));
            }
        });
    }
}