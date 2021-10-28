package com.example.jheyeleraquel.buttonsstore.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jheyeleraquel.buttonsstore.Classes.Cliente;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracaoClienteActivity extends AppCompatActivity {

    private TextView confClienteNome;
    private TextView confClienteEmail;
    private TextView confClienteTelefone;
    private TextView confClienteRua;
    private TextView confClienteBairro;
    private TextView confClienteCidade;
    private TextView confClienteEstado;

    private BootstrapButton confClienteEditar;
    private BootstrapButton confClienteExcluirConta;


    private FirebaseAuth autenticacao;
    private DatabaseReference reference;
    String emailUsuarioLogado;

    private String txtnome = "";
    private String txttelefone = "";
    private String txtrua = "";
    private String txtbairro = "";
    private String txtcidade = "";
    private String txtestado = "";
    private String txtkeyCliente = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao_cliente);

        autenticacao = FirebaseAuth.getInstance();
        reference = ConfiguracaoFirebase.getFirebase();

        confClienteNome = (TextView) findViewById(R.id.confClienteNome);
        confClienteEmail = (TextView) findViewById(R.id.confClienteEmail);
        confClienteTelefone = (TextView) findViewById(R.id.confClienteTelefone);
        confClienteRua = (TextView) findViewById(R.id.confClienteRua);
        confClienteBairro = (TextView) findViewById(R.id.confClienteBairro);
        confClienteCidade = (TextView) findViewById(R.id.confClienteCidade);
        confClienteEstado = (TextView) findViewById(R.id.confClienteEstado);

        confClienteEditar = (BootstrapButton) findViewById(R.id.confClienteEditar);
        confClienteExcluirConta = (BootstrapButton) findViewById(R.id.confClienteExcluirConta);

        confClienteEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarCliente();
            }
        });

        confClienteExcluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogConfimaExclusao();
            }
        });



        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        reference.child("clientes").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Cliente cliente = postSnapshot.getValue(Cliente.class);

                    confClienteNome.setText(cliente.getNome());
                    confClienteEmail.setText(cliente.getEmail());
                    confClienteTelefone.setText(cliente.getTelefone());
                    confClienteRua.setText(cliente.getRua());
                    confClienteBairro.setText(cliente.getBairro());
                    confClienteCidade.setText(cliente.getCidade());
                    confClienteEstado.setText(cliente.getEstado());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void excluirCliente(){
        reference = ConfiguracaoFirebase.getFirebase();

        reference.child("clientes").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    final Cliente cliente =  postSnapshot.getValue(Cliente.class);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfiguracaoClienteActivity.this,"Sua conta foi excluida com sucesso!", Toast.LENGTH_SHORT).show();

                                        reference = ConfiguracaoFirebase.getFirebase();

                                        reference.child("clientes").child(cliente.getKey()).removeValue();

                                        autenticacao.signOut();

                                        finish();
                                        Intent intent = new Intent(ConfiguracaoClienteActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    }else {

                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void abrirDialogConfimaExclusao(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.alert_personalizado_excluircliente);

        final BootstrapButton btnSim = (BootstrapButton) dialog.findViewById(R.id.btnSim);
        final BootstrapButton btnNao = (BootstrapButton) dialog.findViewById(R.id.btnNao);

        btnSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirCliente();
                dialog.dismiss();
            }
        });

        btnNao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfiguracaoClienteActivity.this, PrincipalClienteActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void editarCliente(){
        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        reference = ConfiguracaoFirebase.getFirebase();

        reference.child("clientes").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Cliente cliente = postSnapshot.getValue(Cliente.class);

                    final Intent intent = new Intent(ConfiguracaoClienteActivity.this, EditarClienteActivity.class);

                    final Bundle bundle = new Bundle();

                    bundle.putString("origem","editarCliente");

                    bundle.putString("nome",cliente.getNome());
                    bundle.putString("telefone",cliente.getTelefone());
                    bundle.putString("email",cliente.getEmail());
                    bundle.putString("rua",cliente.getRua());
                    bundle.putString("bairro",cliente.getBairro());
                    bundle.putString("cidade",cliente.getCidade());
                    bundle.putString("estado",cliente.getEstado());
                    bundle.putString("keyCliente",cliente.getKey());

                    intent.putExtras(bundle);

                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

