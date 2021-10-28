package com.example.jheyeleraquel.buttonsstore.Activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jheyeleraquel.buttonsstore.Classes.Cliente;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.Helper.Preferencias;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private BootstrapEditText email;
    private BootstrapEditText senha;
    private TextView cadastrar;
    private BootstrapButton login;
    private TextView recuperarSenha;
    private AlertDialog alerta;

    private Cliente cliente;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (BootstrapEditText) findViewById(R.id.loginEmail);
        senha = (BootstrapEditText) findViewById(R.id.loginSenha);
        cadastrar = (TextView) findViewById(R.id.linkCadastrar);
        login = (BootstrapButton) findViewById(R.id.btnLogin);
        recuperarSenha = (TextView) findViewById(R.id.recuperarSenha);

        final EditText editTextEmail = new EditText(MainActivity.this);
        editTextEmail.setHint("nome@email.com");

        recuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Recuperar Senha");
                builder.setMessage("Informe o seu email.");
                builder.setView(editTextEmail);

                if(!editTextEmail.getText().equals("")){
                    builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            autenticacao = FirebaseAuth.getInstance();

                            String emailRecuperar = editTextEmail.getText().toString();

                            autenticacao.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this,"Em instantes você recebera um email!", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(MainActivity.this,"Falha ao enviar email!", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

                }else {
                    Toast.makeText(MainActivity.this,"Preencha o campo de email!", Toast.LENGTH_SHORT).show();
                }

                alerta = builder.create();
                alerta.show();

            }
        });


        if (usuarioLogado()) {
            autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
            if(autenticacao.getCurrentUser().getEmail().equals("administrador@gmail.com")){
                Intent intentMinhaConta = new Intent(MainActivity.this, AdministradorActivity.class);
                startActivity(intentMinhaConta);
            } else {
                Intent intentMinhaConta = new Intent(MainActivity.this, PrincipalClienteActivity.class);
                startActivity(intentMinhaConta);
            }
        } else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!email.getText().toString().equals("") && !senha.getText().toString().equals("")) {
                        cliente = new Cliente();
                        cliente.setEmail(email.getText().toString());
                        cliente.setSenha(senha.getText().toString());
                        validarLogin();

                    } else {
                        Toast.makeText(MainActivity.this, "Preencha o campos de Email e Senha", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //   cadastrar.setMovementMethod(LinkMovementMethod.getInstance());
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CadastroClienteActivity.class);
                startActivity(intent);
            }
        });

    }

    private void validarLogin() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(cliente.getEmail().toString(), cliente.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        if (cliente.getEmail().toString().equals("administrador@gmail.com") && cliente.getSenha().toString().equals("admin123")) {
                            Preferencias preferencias = new Preferencias(MainActivity.this);
                            preferencias.salvarUsuarioPreferencias(cliente.getEmail(), cliente.getSenha());
                            Intent intent = new Intent(MainActivity.this, AdministradorActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            abrirTelaPrincipal();
                            Preferencias preferencias = new Preferencias(MainActivity.this);
                            preferencias.salvarUsuarioPreferencias(cliente.getEmail(), cliente.getSenha());
                        }
                        Toast.makeText(MainActivity.this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Email e Senha invalidos.Tente novamente!", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }

    public boolean usuarioLogado() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(MainActivity.this, PrincipalClienteActivity.class);
        finish();
        startActivity(intent);
    }


}