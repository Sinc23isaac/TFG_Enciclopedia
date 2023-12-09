package com.example.animaltfg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.lights.LightState;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.animaltfg.modelo.ListadoComentarios;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Perfil_usa extends AppCompatActivity {

    //llamar a los editext que tenemos
    TextView usuarioNom;
    TextView nombreApe;
    String usuarioReg;
    //boton para imagen
    Button imagMost;
    ImageView imagen;
    //variable para la imagen
    private static final int SELECT_IMAGE = 1001;
    //obtenemos referencias de donde almacenaos la base de datos firebase Storage

    //para las publicaciones
    Button btnPubli;
    EditText textPublica;
    //creamos lista
    List<String> listaPublicaciones = new ArrayList<>(); // Tu lista de publicaciones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usa);




        //llamar para el editext
        nombreApe=findViewById(R.id.textNombreApellido);
        usuarioNom=findViewById(R.id.textusua);

        // Obtener el nombre de usuario pasado desde la actividad anterior
        Intent intent = getIntent();

        if (intent.hasExtra("USUARIO_LOGUEADO")) {
            String usuarioLogueado = intent.getStringExtra("USUARIO_LOGUEADO");
            //mensaje
           // Toast.makeText(this, "Usuario "+usuarioLogueado, Toast.LENGTH_SHORT).show();
            // nombreApe.setText(usuarioLogueado);
            //declarar el usuario
            usuarioNom.setText(usuarioLogueado);
            //guardar el usuario
            usuarioReg=usuarioLogueado;

            //inicio de firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("RegistrosUsuarios");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean res=false;
                    //preguntamos si existe ese usuario en nuestra base de datos
                    for (DataSnapshot x : snapshot.getChildren()) {
                        // Verificar si el nombreUsuario coincide con el usuario logueado

                        if(usuarioLogueado.equalsIgnoreCase(x.child("nombreUsuario").getValue().toString())){
                            res=true;
                            // Obtener el nombreApellido del usuario
                            String nombreApellido = x.child("nombreApellido").getValue().toString();
                            // Mostrar el nombreApellido en el TextView
                            nombreApe.setText(nombreApellido);
                            break;
                        }
                    }

                    if(res==false){
                        Toast.makeText(Perfil_usa.this, "This user does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            Toast.makeText(this, "Error getting username", Toast.LENGTH_SHORT).show();
        }

        //imagen para el boton
        imagen=findViewById(R.id.imagePerf);
        //funcion para el boton de imagen
        imagMost=(Button) findViewById(R.id.btnCmabioImag);
        imagMost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un Intent para abrir la galería
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Seleccionar solo imágenes
                startActivityForResult(intent, SELECT_IMAGE);


            }
        });

        // Para las publicaciones
        RecyclerView recyleData = findViewById(R.id.recyclerViewQ);
        recyleData.setLayoutManager(new LinearLayoutManager(this));


        ListadoComentarios adaptadorPublicaciones = new ListadoComentarios(listaPublicaciones);
        recyleData.setAdapter(adaptadorPublicaciones);

        textPublica=findViewById(R.id.editTextPublicacion);
        btnPubli=(Button) findViewById(R.id.btnPublicar);
        btnPubli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //obtener el texto de la publicacion
                String publicacion=textPublica.getText().toString();
                //hacemos controlador de errores por si no se ha escrito nada
                if(!publicacion.isEmpty()){
                    //ahora lo añadimos a nuestra lista
                    listaPublicaciones.add(publicacion);
                    adaptadorPublicaciones.notifyDataSetChanged(); // Notificar al adaptador sobre los cambios
                    textPublica.setText("");
                }

            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //se ejecuta cuando el usuario ha seleccionado una imagen de la galería de forma exitosa
        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            //verfiicar que data no este null
            if (data != null) {
                // Obtener la URI de la imagen seleccionada
                Uri selectedImageUri = data.getData();
                // Ahora puedes usar esta URI para establecer la imagen en tu ImageView
                imagen.setImageURI(selectedImageUri);
            }
        }

    }




    //metodo para eliminar la cuenta del usuario actual y redirigir al inicio de sesion
    private void irEliminarCuenta(){
        //mensaje de ventana emergente donde pregunto si quiere eliminar
        AlertDialog.Builder a = new AlertDialog.Builder(Perfil_usa.this);
        a.setCancelable(false);
        a.setTitle("Ask");
        a.setMessage("Are you sure you want to delete the account?");
        //aqui sera la parte de que pasa i le doy a cancelar xd
        a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción al cancelar
                Toast.makeText(Perfil_usa.this, "Do not delete your account", Toast.LENGTH_SHORT).show();

            }
        });
        //aqui si le doy a la parte de aceptar se eliminara ese dato de la bd de firebase
        a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //conectar a la base de firebase
                FirebaseDatabase dt = FirebaseDatabase.getInstance();
                DatabaseReference ref = dt.getReference("RegistrosUsuarios");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean encontrado = false;
                        for (DataSnapshot x : snapshot.getChildren()) {
                            //preguntamo's si existe ese usuario
                            if(usuarioReg.equalsIgnoreCase(x.child("nombreUsuario").getValue().toString())){
                                encontrado = true;
                                // Eliminar el registro del usuario
                                x.getRef().removeValue();
                                // Mostrar mensaje de éxito
                                Toast.makeText(Perfil_usa.this, "your "+usuarioReg+" account  no longer exists", Toast.LENGTH_SHORT).show();
                                // Redirigir al inicio de sesión
                                Intent intent = new Intent(Perfil_usa.this, Login_reg.class);
                                startActivity(intent);
                                // Cerrar la actividad actual
                                finish();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        a.show();

    }
//meotod con modificar

    //metodo para dirigirme a modificacion_datos
    private  void irAModificarDatos(){
        Intent intent = new Intent(Perfil_usa.this, Modificar_dat.class);
        //para pasar el nombre de usuario a la otra actividad
        intent.putExtra("USUARIO_LOGUEADO", usuarioReg);
        startActivity(intent);
    }

    //metodo para dirigirme a la busqueda de animales
    private  void irABuscarAnimales(){
        Intent intent = new Intent(this, Busqueda_animal.class);
        intent.putExtra("USUARIO_LOGUEADO", usuarioReg);
        startActivity(intent);
    }

    // Método para cerrar sesión y redirigir al inicio de sesión
    private void cerrarSesion() {
        // Realiza cualquier otra acción necesaria para cerrar la sesión del usuario
        //mensaje de cerrar sesion
        Toast.makeText(Perfil_usa.this, "Successful logout", Toast.LENGTH_SHORT).show();

        // Redirigir al inicio de sesión
        irAInicioSesion();
    }

    // Método para redirigir al usuario a la pantalla de inicio de sesión
    private void irAInicioSesion() {
        Intent intent = new Intent(this, Login_reg.class);
        startActivity(intent);
        finish(); // Cerrar la actividad actual
    }


    // Configurar el menú de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú
        getMenuInflater().inflate(R.menu.menu_perfil, menu);
        return true;
    }

    // Manejar la selección de elementos del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salirLogin:
                // Cerrar sesión
                cerrarSesion();
                return true;
            case R.id.eliminarCuenta:
                // Eliminar cuenta
                irEliminarCuenta();
                return true;
            case R.id.busqueda:
                //ir a la busqueda de animales
                irABuscarAnimales();
                return true;
            case R.id.modificar:
                //ir a modificar datos
                irAModificarDatos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}