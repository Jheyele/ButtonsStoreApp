package com.example.jheyeleraquel.buttonsstore.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;

public class PrincipalClienteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
    private String emailUsuarioLogado;
    private ImageView cadPedido;
    private ImageView verProduto;
    private ImageView verMeusPedidos;
    private ImageView chatbot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_cliente);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cadPedido = (ImageView) findViewById(R.id.inicioCadPedido);
        verProduto = (ImageView) findViewById(R.id.inicioVerProdutos);
        verMeusPedidos = (ImageView) findViewById(R.id.inicioVerMeusPedidos);
        chatbot = (ImageView) findViewById(R.id.inicioChatbot);

        cadPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalClienteActivity.this, CadastroPedidoActivity.class);
                startActivity(intent);
            }
        });

        verProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalClienteActivity.this, ListaProdutosActivity.class);
                startActivity(intent);
            }
        });

        verMeusPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrincipalClienteActivity.this, ListaMeusPedidosActivity.class);
                startActivity(intent);
            }
        });

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.facebook.orca");
                intent.setData(Uri.parse("https://m.me/378246869438616"));
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.realizar_pedido) {
            cadastrarPedido();
        } else if (id == R.id.visualizar_produtos) {
            visualizarProdutor();
        } else if (id == R.id.menuClienteSair) {
            deslogarCliente();
        } else if (id == R.id.menuClienteConf) {
            Intent intent = new Intent(PrincipalClienteActivity.this, ConfiguracaoClienteActivity.class);
            startActivity(intent);

        }else if (id == R.id.chat) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setPackage("com.facebook.orca");
            intent.setData(Uri.parse("https://m.me/378246869438616"));
            startActivity(intent);
        }else if (id == R.id.meus_pedidos) {
            Intent intent = new Intent(PrincipalClienteActivity.this, ListaMeusPedidosActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deslogarCliente(){
        autenticacao.signOut();
        Intent intent = new Intent(PrincipalClienteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void cadastrarPedido(){
        Intent intent = new Intent(PrincipalClienteActivity.this, CadastroPedidoActivity.class);
        startActivity(intent);
    }

    private void visualizarProdutor(){
        Intent intent = new Intent(PrincipalClienteActivity.this, ListaProdutosActivity.class);
        startActivity(intent);
    }

}
