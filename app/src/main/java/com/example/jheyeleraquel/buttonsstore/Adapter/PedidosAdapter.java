package com.example.jheyeleraquel.buttonsstore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jheyeleraquel.buttonsstore.Activity.PrincipalClienteActivity;
import com.example.jheyeleraquel.buttonsstore.Classes.Pedido;
import com.example.jheyeleraquel.buttonsstore.Classes.Produto;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {

    List<Pedido> mPedidoList;
    private Context context;
    private DatabaseReference referenciaFirebase;
    private List<Pedido> pedidos;
    private Pedido todosPedidos;


    public PedidosAdapter(List<Pedido> l , Context c){
        context = c;
        mPedidoList = l;
    }


    @Override
    public PedidosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_pedidos,viewGroup,false);
        return new PedidosAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PedidosAdapter.ViewHolder viewHolder, int position) {
        final Pedido item = mPedidoList.get(position);

        pedidos = new ArrayList<>();

        referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        referenciaFirebase.child("pedidos").orderByChild("keyPedido").equalTo(item.getKeyPedido()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();

                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                
                        todosPedidos = postSnapshot.getValue(Pedido.class);

                        pedidos.add(todosPedidos);

                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

                        final int height = (displayMetrics.heightPixels / 4);
                        final int width = (displayMetrics.widthPixels / 2);
                        Picasso.get().load(todosPedidos.getImagemUrl()).resize(width, height).centerCrop().into(viewHolder.imagemPedidoLista);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.txtViewPedidoNomeProduto.setText(item.getProduto());
        viewHolder.txtViewPedidoQuantidade.setText("Quantidade: "+String.valueOf(item.getQuantidade()));
        viewHolder.txtValorTotal.setText("Valor: R$ "+String.valueOf(item.getValorTotalPedido()));
        viewHolder.txtStatus.setText("Situação: "+item.getStatus());
        viewHolder.txtViewPedidoObservacao.setText(item.getObservacao());

        if(item.getStatus().equals("Pronto")){
            viewHolder.excluirPedido.setVisibility(View.INVISIBLE);
        }

        viewHolder.excluirPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference;
                reference = ConfiguracaoFirebase.getFirebase();
                reference.child("pedidos").child(item.getKeyPedido()).removeValue();
                Toast.makeText(context,"Pedido excluido com sucesso!",Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, PrincipalClienteActivity.class);
                context.startActivity(i);
            }
        });

        viewHolder.layoutListaPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPedidoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtViewPedidoNomeProduto;
        protected TextView txtViewPedidoQuantidade;
        protected TextView txtViewPedidoObservacao;
        protected TextView txtStatus;
        protected TextView txtValorTotal;
        protected ImageView imagemPedidoLista;
        protected LinearLayout layoutListaPedidos;
        protected TextView excluirPedido;

        public ViewHolder(View itemView){
            super(itemView);

            txtViewPedidoNomeProduto = (TextView)itemView.findViewById(R.id.txtViewPedidoNomeProduto);
            txtViewPedidoQuantidade = (TextView)itemView.findViewById(R.id.txtViewPedidoQuantidade);
            txtStatus = (TextView)itemView.findViewById(R.id.txtStatus);
            excluirPedido = (TextView)itemView.findViewById(R.id.excluirPedido);
            txtValorTotal = (TextView)itemView.findViewById(R.id.txtValorTotal);
            txtViewPedidoObservacao = (TextView)itemView.findViewById(R.id.txtViewPedidoObservacao);
            imagemPedidoLista = (ImageView)itemView.findViewById(R.id.imagemPedidoLista);
            layoutListaPedidos = (LinearLayout) itemView.findViewById(R.id.layoutListaPedidos);

        }
    }
}
