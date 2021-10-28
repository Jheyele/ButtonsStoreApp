package com.example.jheyeleraquel.buttonsstore.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jheyeleraquel.buttonsstore.Classes.Produto;

import java.util.ArrayList;
import java.util.List;

import com.example.jheyeleraquel.buttonsstore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ViewHolder> {

    List<Produto> mProdutoList;
    private Context context;
    private DatabaseReference referenciaFirebase;
    private List<Produto> produtos;
    private Produto todosProdutos;


    public ProdutosAdapter(List<Produto> l , Context c){
        context = c;
        mProdutoList = l;
    }


    @Override
    public ProdutosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_produtos,viewGroup,false);
        return new ProdutosAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProdutosAdapter.ViewHolder viewHolder, int position) {
        final Produto item = mProdutoList.get(position);

        produtos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("produtos").orderByChild("keyProduto").equalTo(item.getKeyProduto()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    todosProdutos = postSnapshot.getValue(Produto.class);

                    produtos.add(todosProdutos);

                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

                    final int height = (displayMetrics.heightPixels/4);
                    final  int width = (displayMetrics.widthPixels/2);
                    Picasso.get().load(todosProdutos.getImagemUrl()).resize(width,height).centerCrop().into(viewHolder.imagemProdutoLista);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.txtViewDescricao.setText(item.getDescricao());
        viewHolder.txtViewNome.setText(item.getNome());
        viewHolder.txtViewPreco.setText("R$ "+String.valueOf(item.getPreco())+"0");

        viewHolder.linearLayoutProdutosLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mProdutoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtViewNome;
        protected TextView txtViewPreco;
        protected TextView txtViewDescricao;
        protected ImageView imagemProdutoLista;
        protected LinearLayout linearLayoutProdutosLista;

        public ViewHolder(View itemView){
            super(itemView);

            txtViewNome = (TextView)itemView.findViewById(R.id.txtViewNome);
            txtViewPreco = (TextView)itemView.findViewById(R.id.txtViewPreco);
            txtViewDescricao = (TextView)itemView.findViewById(R.id.txtViewDescricao);
            imagemProdutoLista = (ImageView)itemView.findViewById(R.id.imagemProdutoLista);
            linearLayoutProdutosLista = (LinearLayout) itemView.findViewById(R.id.layoutListaProdutos);


        }

    }
}
