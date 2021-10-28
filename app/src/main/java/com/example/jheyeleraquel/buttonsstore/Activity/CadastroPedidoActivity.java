package com.example.jheyeleraquel.buttonsstore.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jheyeleraquel.buttonsstore.Adapter.ProdutosAdapter;
import com.example.jheyeleraquel.buttonsstore.Classes.Cliente;
import com.example.jheyeleraquel.buttonsstore.Classes.Pedido;
import com.example.jheyeleraquel.buttonsstore.Classes.Produto;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CadastroPedidoActivity extends AppCompatActivity {

    private Pedido pedido;
    private ImageView imagem;
    private BootstrapEditText quantidade;
    private BootstrapEditText observacao;
    private BootstrapButton cadastrar;

    private String idCliente;
    private String telefoneCliente;
    private String enderecoCliente;
    private String nomeCliente;
    private static Spinner produtosArray;

    private static String nome;
    private float preco;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference referenciaFirebase;
    private FirebaseAuth autenticacao;
    private String emailUsuarioLogado;
    private static List<String> produtosList;
    private String nomeP;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pedido);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();
        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        produtosArray = (Spinner) findViewById(R.id.spinner);
        produtosList = new ArrayList<String>();

        referenciaFirebase.child("produtos").orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    nomeP = postSnapshot.child("nome").getValue().toString();
                    produtosList.add(nomeP);
                }
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, produtosList);
                ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                produtosArray.setAdapter(spinnerArrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        referenciaFirebase.child("clientes").orderByChild("email").equalTo(emailUsuarioLogado.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    idCliente = postSnapshot.child("key").getValue().toString();
                    nomeCliente = postSnapshot.child("nome").getValue().toString();
                    telefoneCliente = postSnapshot.child("telefone").getValue().toString();
                    enderecoCliente = postSnapshot.child("rua").getValue().toString().concat(" ").concat(postSnapshot.child("bairro").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        imagem = (ImageView) findViewById(R.id.cadPedidoImagem);
        quantidade = (BootstrapEditText) findViewById(R.id.cadPedidoQuantidade);
        observacao = (BootstrapEditText) findViewById(R.id.cadPedidoObservacao);
        cadastrar = (BootstrapButton) findViewById(R.id.btnCadastrarPedido);

        imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), 123);
            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nome = produtosArray.getSelectedItem().toString();

                referenciaFirebase.child("produtos").orderByChild("nome").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            if((postSnapshot.child("nome").getValue().equals(nome))){
                                preco =   Float.valueOf(postSnapshot.child("preco").getValue().toString());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                cadastroFotoPedido();
            }
        });


    }



    private boolean cadastrarPedido(final Pedido pedido) {


        pedido.setObservacao(observacao.getText().toString());
        pedido.setQuantidade(Integer.parseInt(quantidade.getText().toString()));
        pedido.setProduto(nome);
        pedido.setValorTotalPedido(preco*pedido.getQuantidade());
        pedido.setKeyCliente(idCliente);
        pedido.setEnderecoCliente(enderecoCliente);
        pedido.setTelefoneCliente(telefoneCliente);
        pedido.setNomeCliente(nomeCliente);
        pedido.setStatus("Espera");


            try {
                referenciaFirebase = ConfiguracaoFirebase.getFirebase().child("pedidos");
                String key = referenciaFirebase.push().getKey();
                pedido.setKeyPedido(key);
                referenciaFirebase.child(key).setValue(pedido);
                finish();
                Intent intent = new Intent(CadastroPedidoActivity.this, PrincipalClienteActivity.class);
                startActivity(intent);
                Toast.makeText(CadastroPedidoActivity.this, "Produto cadastrado com sucesso ", Toast.LENGTH_SHORT).show();
                return true;
            } catch (Exception e) {
                Toast.makeText(CadastroPedidoActivity.this, "Erro ao cadastrar produto", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }

    }


    private void cadastroFotoPedido(){


        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");

        Date dat = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(dat);
        Date data_atual = cal.getTime();

        String data_completa = dateFormat_hora.format(data_atual);

        StorageReference montaImagemReferencia = storageReference.child("Pedidos/"+emailUsuarioLogado.toString()+"/"+data_completa+".jpeg");


        imagem.setDrawingCacheEnabled(true);
        imagem.buildDrawingCache();

        Bitmap bitmap = imagem.getDrawingCache();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagemReferencia.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pedido = new Pedido();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                pedido.setImagemUrl(downloadUrl.toString());
                Toast.makeText(CadastroPedidoActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                cadastrarPedido(pedido);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final int heigth = 300;
        final int width = 300;

        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                Uri imagemSelecionada = data.getData();
                Picasso.get().load(imagemSelecionada).resize(width, heigth).centerCrop().into(imagem);
            }
        }
    }
}
