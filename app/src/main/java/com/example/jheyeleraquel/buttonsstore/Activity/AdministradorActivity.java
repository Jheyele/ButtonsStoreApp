package com.example.jheyeleraquel.buttonsstore.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jheyeleraquel.buttonsstore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class AdministradorActivity extends AppCompatActivity {

    private LinearLayout verPedidos;
    private LinearLayout addProduto;
    private LinearLayout verPedidosProntos;

    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        usuarioLogado();

        autenticacao = FirebaseAuth.getInstance();

        verPedidos = (LinearLayout) findViewById(R.id.imagemVerPedidos);
        verPedidosProntos = (LinearLayout) findViewById(R.id.imagemVerPedidosProntos);
        addProduto = (LinearLayout) findViewById(R.id.imagemAddProduto);

        verPedidosProntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministradorActivity.this, ListaPedidosProntosActivity.class);
                startActivity(intent);
            }
        });

        verPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministradorActivity.this, ListaPedidosActivity.class);
                startActivity(intent);
            }
        });

        addProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdministradorActivity.this, CadastroProdutoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.menuAdminSair){
            deslogar();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogar(){
        autenticacao.signOut();
        Intent intent = new Intent(AdministradorActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean usuarioLogado() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }
}
