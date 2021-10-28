package com.example.jheyeleraquel.buttonsstore.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroClienteActivity extends AppCompatActivity {


    private BootstrapEditText nome;
    private BootstrapEditText email;
    private BootstrapEditText senha;
    private BootstrapEditText senha2;
    private BootstrapEditText telefone;
    private BootstrapEditText cidade;
    private BootstrapEditText estado;
    private BootstrapEditText rua;
    private BootstrapEditText bairro;
    private BootstrapButton cadastrar;
    private Cliente cliente;
    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    private BootstrapButton cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        nome = (BootstrapEditText) findViewById(R.id.cadNome);
        email = (BootstrapEditText) findViewById(R.id.cadEmail);
        senha = (BootstrapEditText) findViewById(R.id.cadSenha);
        senha2 = (BootstrapEditText) findViewById(R.id.cadSenha2);
        telefone = (BootstrapEditText) findViewById(R.id.cadTelefone);
        cidade = (BootstrapEditText) findViewById(R.id.cadCidade);
        estado = (BootstrapEditText) findViewById(R.id.cadEstado);
        rua = (BootstrapEditText) findViewById(R.id.cadRua);
        bairro = (BootstrapEditText) findViewById(R.id.cadBairro);
        cadastrar = (BootstrapButton) findViewById(R.id.btnCadastrarCliente);
        cancelar = (BootstrapButton) findViewById(R.id.btnCancelar);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(CadastroClienteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!(senha.getText().toString().equals("") || email.getText().toString().equals("") || nome.getText().toString().equals("") || bairro.getText().toString().equals("") || cidade.getText().toString().equals("") || estado.getText().toString().equals("") || rua.getText().toString().equals("") || telefone.getText().toString().equals(""))) {
                    if (senha.getText().toString().equals(senha2.getText().toString())) {

                        cliente = new Cliente();

                        cliente.setSenha(senha.getText().toString());
                        cliente.setEmail(email.getText().toString());
                        cliente.setNome(nome.getText().toString());
                        cliente.setBairro(bairro.getText().toString());
                        cliente.setCidade(cidade.getText().toString());
                        cliente.setEstado(estado.getText().toString());
                        cliente.setRua(rua.getText().toString());
                        cliente.setTelefone(telefone.getText().toString());

                        cadastrarCliente();

                    } else {
                        Toast.makeText(CadastroClienteActivity.this, "As senhas não se correspondem", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroClienteActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cadastrarCliente(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                cliente.getEmail(),
                cliente.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    insereCliente(cliente);
                    finish();
                }else{
                    String erroExecao = "";

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExecao = "Digite uma senha mais forte, com no minimo 8 caracteres e cotenha letras e numeros";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erroExecao = "Email invalido, digite corretamente.";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExecao = "Esse email já esta cadastrado.";
                    }catch (Exception e){
                        erroExecao = "Erro ao efetuar cadastro.";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroClienteActivity.this, "Erro" + erroExecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean insereCliente(Cliente cliente){
        try{
            reference = ConfiguracaoFirebase.getFirebase().child("clientes");
            String key = reference.push().getKey();
            cliente.setKey(key);
            reference.child(key).setValue(cliente);
            finish();
            Intent intent = new Intent(CadastroClienteActivity.this, PrincipalClienteActivity.class);
            startActivity(intent);
            Toast.makeText(CadastroClienteActivity.this, "Usuario cadastrado com sucesso", Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e){
            Toast.makeText(CadastroClienteActivity.this, "Erro ao gravar usuario", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

}
