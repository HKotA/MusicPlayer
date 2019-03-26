package com.example.hkota.musics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText email;
    EditText password;
    Button login;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email_edittextTwo);
        password = findViewById(R.id.password_edittextTwo);
        login = findViewById(R.id.login_button);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Ожидание...");
                progressDialog.show();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (email.getText().toString().equals("") || password.getText().toString().equals("")){
                            progressDialog.dismiss();
                            Toast.makeText(Login.this,"Введите данные",Toast.LENGTH_SHORT).show();
                        }else{
                            if (dataSnapshot.child(email.getText().toString()).exists()){
                                progressDialog.dismiss();
                                User user = dataSnapshot.child(email.getText().toString()).getValue(User.class);

                                if (user.getPassword().equals(password.getText().toString())){

                                    CurrentUser.currentUser = user;

                                    Intent intent = new Intent(Login.this,PlayList.class);
                                    startActivity(intent);



                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Пользовтель не найден", Toast.LENGTH_SHORT).show();
                            }
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
