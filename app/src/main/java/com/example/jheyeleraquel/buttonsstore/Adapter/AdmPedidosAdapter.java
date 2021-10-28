package com.example.jheyeleraquel.buttonsstore.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jheyeleraquel.buttonsstore.Activity.AdministradorActivity;
import com.example.jheyeleraquel.buttonsstore.Activity.CadastroPedidoActivity;
import com.example.jheyeleraquel.buttonsstore.Activity.ConfiguracaoClienteActivity;
import com.example.jheyeleraquel.buttonsstore.Activity.ListaPedidosActivity;
import com.example.jheyeleraquel.buttonsstore.Activity.PrincipalClienteActivity;
import com.example.jheyeleraquel.buttonsstore.Classes.Pedido;
import com.example.jheyeleraquel.buttonsstore.DAO.ConfiguracaoFirebase;
import com.example.jheyeleraquel.buttonsstore.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class AdmPedidosAdapter extends RecyclerView.Adapter<AdmPedidosAdapter.ViewHolder>{

    List<Pedido> mPedidoList;
    private Context context;
    private DatabaseReference referenciaFirebase;
    private List<Pedido> pedidos;
    private Pedido todosPedidos;


    public AdmPedidosAdapter(List<Pedido> l , Context c){
        context = c;
        mPedidoList = l;
    }


    @Override
    public AdmPedidosAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_pedidos_adm,viewGroup,false);
        return new AdmPedidosAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final AdmPedidosAdapter.ViewHolder viewHolder, int position) {
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
        viewHolder.txtValor.setText("Valor do Pedido: R$ "+String.valueOf(item.getValorTotalPedido()));
        viewHolder.txtViewPedidoObservacao.setText("Obs: "+item.getObservacao());


        viewHolder.layoutListaPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_detalhes_pedido);

                final TextView nomeCliente = (TextView) dialog.findViewById(R.id.txtAlertAdminNomeCliente);
                final TextView enderecoCliente = (TextView) dialog.findViewById(R.id.txtAlertAdminEnderecoCliente);
                final TextView contatoCliente = (TextView) dialog.findViewById(R.id.txtAlertAdminContatoCliente);

                nomeCliente.setText(item.getNomeCliente());
                enderecoCliente.setText(item.getEnderecoCliente());
                contatoCliente.setText(item.getTelefoneCliente());

                final BootstrapButton btnDownload = (BootstrapButton) dialog.findViewById(R.id.btnAlertAdminDownload);
                final BootstrapButton btnStatus = (BootstrapButton) dialog.findViewById(R.id.btnAlertAdminStatusPedido);



                btnStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Pedido pedido = new Pedido();
                        pedido.setNomeCliente(item.getNomeCliente());
                        pedido.setTelefoneCliente(item.getTelefoneCliente());
                        pedido.setEnderecoCliente(item.getEnderecoCliente());
                        pedido.setKeyCliente(item.getKeyCliente());
                        pedido.setQuantidade(item.getQuantidade());
                        pedido.setImagemUrl(item.getImagemUrl());
                        pedido.setObservacao(item.getObservacao());
                        pedido.setProduto(item.getProduto());
                        pedido.setKeyPedido(item.getKeyPedido());

                        if (item.getStatus().equals("Espera")) {
                            pedido.setStatus("Pronto");
                        } else if (item.getStatus().equals("Pronto")) {
                            pedido.setStatus("Espera");
                        }

                        try {
                            referenciaFirebase = ConfiguracaoFirebase.getFirebase().child("pedidos");
                            pedido.setKeyPedido(item.getKeyPedido());
                            referenciaFirebase.child(item.getKeyPedido()).setValue(pedido);

                            Toast.makeText(context, "Deu Certo", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                });

                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       final DownloadManager downloadManeger = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                        Uri uri = Uri.parse(item.getImagemUrl());

                        DownloadManager.Request request = new DownloadManager.Request(uri);

                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        final long id = downloadManeger.enqueue(request);
                        Timer myTimer = new Timer();
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterById(id);
                                Cursor cursor = downloadManeger.query(q);
                                cursor.moveToFirst();
                                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                cursor.close();
                                final int dl_progress = (bytes_downloaded * 100 / bytes_total);


                            }

                        }, 0, 10);
                    dialog.dismiss();
                    }

                });

                dialog.show();

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
        protected TextView txtValor;
        protected ImageView imagemPedidoLista;
        protected LinearLayout layoutListaPedidos;

        public ViewHolder(View itemView){
            super(itemView);

            txtViewPedidoNomeProduto = (TextView)itemView.findViewById(R.id.txtViewPedidoNomeProdutoAdm);
            txtViewPedidoQuantidade = (TextView)itemView.findViewById(R.id.txtViewPedidoQuantidadeAdm);
            txtViewPedidoObservacao = (TextView)itemView.findViewById(R.id.txtViewPedidoObservacaoAdm);
            txtValor = (TextView)itemView.findViewById(R.id.txtValor);
            imagemPedidoLista = (ImageView)itemView.findViewById(R.id.imagemPedidoListaAdm);
            layoutListaPedidos = (LinearLayout) itemView.findViewById(R.id.layoutListaPedidosAdm);

        }
    }

}