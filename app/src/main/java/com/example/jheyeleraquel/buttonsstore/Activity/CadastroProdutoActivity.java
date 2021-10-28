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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jheyeleraquel.buttonsstore.Classes.Produto;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class CadastroProdutoActivity extends AppCompatActivity {
    private BootstrapEditText nome;
    private BootstrapEditText preco;
    private BootstrapEditText descricao;
    private ImageView imagem;
    private BootstrapButton cadastrar;

    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference referenciaFirebase;
    private FirebaseAuth autenticacao;
    private String emailUsuarioLogado;

    private Produto produto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        nome = (BootstrapEditText) findViewById(R.id.cadProdutoNome);
        preco = (BootstrapEditText) findViewById(R.id.cadProdutoPreco);
        imagem = (ImageView) findViewById(R.id.cadProdutoImagem);
        descricao = (BootstrapEditText) findViewById(R.id.cadProdutoDescricao);
        cadastrar = (BootstrapButton) findViewById(R.id.btnCadastrarProduto);

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
                cadastroFotoProduto(nome.getText().toString());
            }
        });
    }

    private boolean cadastrarProduto(Produto produto){

        produto.setNome(nome.getText().toString());
        produto.setDescricao(descricao.getText().toString());
        produto.setPreco(Float.parseFloat(preco.getText().toString()));

        try{
            referenciaFirebase = ConfiguracaoFirebase.getFirebase().child("produtos");
            String key = referenciaFirebase.push().getKey();
            produto.setKeyProduto(key);
            referenciaFirebase.child(key).setValue(produto);
            finish();
            Intent intent = new Intent(CadastroProdutoActivity.this, AdministradorActivity.class);
            startActivity(intent);
            Toast.makeText(CadastroProdutoActivity.this, "Produto cadastrado com sucesso", Toast.LENGTH_SHORT).show();
            return true;
        }catch (Exception e){
            Toast.makeText(CadastroProdutoActivity.this, "Erro ao cadastrar produto", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private void cadastroFotoProduto(String nome){

        StorageReference montaImagemReferencia = storageReference.child("Produtos/"+nome+".jpeg");


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
                produto = new Produto();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                produto.setImagemUrl(downloadUrl.toString());
                Toast.makeText(CadastroProdutoActivity.this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                cadastrarProduto(produto);
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

    /*
    private void carregaImagemPadrao(){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final StorageReference storageReference = storage.getReferenceFromUrl("gs://buttonsstore-3b0fb.appspot.com/").child("imagem.png");
        final int heigth = 300;
        final int width = 300;

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(width,heigth).centerCrop().into(imagem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
  */
}
