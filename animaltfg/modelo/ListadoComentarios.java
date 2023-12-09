package com.example.animaltfg.modelo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.animaltfg.R;

import java.util.List;
public class ListadoComentarios extends RecyclerView.Adapter<ListadoComentarios.ComentarioViewHolder> {

    List<String> listaPublicaciones;

    // Constructor
    public ListadoComentarios(List<String> listaPublicaciones) {
        this.listaPublicaciones = listaPublicaciones;
    }

    // Método para actualizar la lista de publicaciones
    public void setListaPublicaciones(List<String> listaPublicaciones) {
        this.listaPublicaciones = listaPublicaciones;
        notifyDataSetChanged(); // Notificar al RecyclerView sobre los cambios en los datos
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listacoment, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        String comentario = listaPublicaciones.get(position);
        holder.bind(comentario);
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView textoComentario;

        public ComentarioViewHolder(View itemView) {
            super(itemView);
            textoComentario = itemView.findViewById(R.id.editTextTextPersonName);
        }

        public void bind(String comentario) {
            textoComentario.setText(comentario);
        }
    }
}
