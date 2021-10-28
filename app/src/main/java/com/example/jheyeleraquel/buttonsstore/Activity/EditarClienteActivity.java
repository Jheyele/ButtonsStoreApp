package com.example.jheyeleraquel.buttonsstore.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jheyeleraquel.buttonsstore.Classes.Cliente;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class EditarClienteActivity extends AppCompatActivity {

    private BootstrapEditText editarClienteNome;
    private BootstrapEditText editarClienteTelefone;
    private BootstrapEditText editarClienteCidade;
    private BootstrapEditText editarClienteEstado;
    private BootstrapEditText editarClienteBairro;
    private BootstrapEditText editarClienteRua;
    private BootstrapEditText editarClienteSenha1;
    private BootstrapEditText editarClienteSenha2;

    private BootstrapButton editarConfirma;
    private BootstrapButton editarCancela;

    private String txtorigem = "";
    private String txtnome = "";
    private String txttelefone = "";
    private String txtrua = "";
    private String txtbairro = "";
    private String txtcidade = "";
    private String txtestado = "";
    private String txtkeyCliente = "";
    private String txtemail = "";


    private DatabaseReference reference;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);

        editarClienteNome = (BootstrapEditText) findViewById(R.id.editarClienteNome);
        editarClienteTelefone = (BootstrapEditText) findViewById(R.id.editarClienteTelefone);
        editarClienteCidade = (BootstrapEditText) findViewById(R.id.editarClienteCidade);
        editarClienteEstado = (BootstrapEditText) findViewById(R.id.editarClienteEstado);
        editarClienteBairro = (BootstrapEditText) findViewById(R.id.editarClienteBairro);
        editarClienteRua = (BootstrapEditText) findViewById(R.id.editarClienteRua);
        editarClienteSenha1 = (BootstrapEditText) findViewById(R.id.editarClienteSenha1);
        editarClienteSenha2 = (BootstrapEditText) findViewById(R.id.editarClienteSenha2);

        editarCancela = (BootstrapButton) findViewById(R.id.btnEditarClienteCancelar);
        editarConfirma = (BootstrapButton) findViewById(R.id.btnEditarClienteConfirma);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        txtorigem = bundle.getString("origem");

        if(txtorigem.equals("editarCliente")){
            txtnome = bundle.getString("nome");
            txttelefone = bundle.getString("telefone");
            txtbairro = bundle.getString("bairro");
            txtcidade = bundle.getString("cidade");
            txtestado = bundle.getString("estado");
            txtrua = bundle.getString("rua");
            txtkeyCliente = bundle.getString("keyCliente");
            txtemail = bundle.getString("email");

            editarClienteNome.setText(txtnome);
            editarClienteTelefone.setText(txttelefone);
            editarClienteRua.setText(txtrua);
            editarClienteBairro.setText(txtbairro);
            editarClienteCidade.setText(txtcidade);
            editarClienteEstado.setText(txtestado);
        }

        editarConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editarClienteSenha1.getText().toString().equals(editarClienteSenha2.getText().toString())) {

                    Cliente cliente = new Cliente();

                    cliente.setNome(editarClienteNome.getText().toString());
                    cliente.setTelefone(editarClienteTelefone.getText().toString());
                    cliente.setRua(editarClienteRua.getText().toString());
                    cliente.setBairro(editarClienteBairro.getText().toString());
                    cliente.setCidade(editarClienteCidade.getText().toString());
                    cliente.setEstado(editarClienteEstado.getText().toString());
                    cliente.setKey(txtkeyCliente);
                    cliente.setSenha(editarClienteSenha1.getText().toString());
                    cliente.setEmail(txtemail);

                    atualizarDados(cliente);
                }else {
                    Toast.makeText(EditarClienteActivity.this, "Senhas não se correspondem!", Toast.LENGTH_SHORT).show();
                }


                Intent x = new Intent(EditarClienteActivity.this, PrincipalClienteActivity.class);
                startActivity(x);
                finish();

            }
        });

        editarCancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditarClienteActivity.this,PrincipalClienteActivity.class);
                startActivity(intent);
            }
        });



    }


    private boolean atualizarDados(final Cliente cliente){

        editarConfirma.setEnabled(false);

        try{
            reference = ConfiguracaoFirebase.getFirebase().child("clientes");

            atualizaSenha();

            reference.child(txtkeyCliente).setValue(cliente);

            Toast.makeText(EditarClienteActivity.this, "Atualização realizada com sucesso!", Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    private void atualizaSenha(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(editarClienteSenha1.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("NOVA_SENHA_ATUALIZADA", "Senha atualizada com sucesso!");
                        }
                    }
                });
    }
}
