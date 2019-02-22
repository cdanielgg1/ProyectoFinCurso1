package com.example.proyectofincurso.proveedor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.proyectofincurso.constantes.Utilidades;
import com.example.proyectofincurso.pojos.Libro;

import java.io.IOException;

public class LibroProveedor {

    //metodo para pasar por parametros al proveedor de contenidos

    public static void insertRecord(ContentResolver resolverdor, Libro libro, Context contexto){

        Uri uri = Contrato.Libro.CONTENT_URI;

        ContentValues values = new ContentValues();
        values.put(Contrato.Libro.TITULO, libro.getTitulo());
        values.put(Contrato.Libro.PAGINAS, libro.getPaginas());

        Uri returnUri = resolverdor.insert(uri, values);

        if(libro.getImagen()!=null){
            try {
                Utilidades.storeImage(libro.getImagen(), contexto, "img_" + returnUri.getLastPathSegment() + ".jpg");
            } catch (IOException e) {
                Toast.makeText(contexto, "Hubo un error al guardar la imagen", Toast.LENGTH_LONG).show();
            }
        }
    }

    static  public void deleteRecord(ContentResolver resolver, int libroId){

        Uri uri = Uri.parse(Contrato.Libro.CONTENT_URI + "/" + libroId);
              resolver.delete(uri, null,null);

    }

    static public void updateRecord(ContentResolver resolver, Libro libro, Context contexto){
        Uri uri = Uri.parse(Contrato.Libro.CONTENT_URI + "/" + libro.getID());

        ContentValues values = new ContentValues();
        values.put(Contrato.Libro.TITULO, libro.getTitulo());
        values.put(Contrato.Libro.PAGINAS, libro.getPaginas());

        resolver.update(uri, values, null, null);

        if(libro.getImagen()!=null){
            try {
                Utilidades.storeImage(libro.getImagen(), contexto, "img_" + libro.getID() + ".jpg");
            } catch (IOException e) {
                Toast.makeText(contexto, "Hubo un error al guardar la imagen", Toast.LENGTH_LONG).show();
            }
        }
    }
// se usa para leer un registro
    static public Libro readRecord(ContentResolver resolver, int libroId){
        Uri uri = Uri.parse(Contrato.Libro.CONTENT_URI + "/" + libroId);

        String[] projection ={
                Contrato.Libro.TITULO,
                Contrato.Libro.PAGINAS

        };
        //El Cursor devuelve el conjunto de registros, uno o ninguno.
        Cursor cursor = resolver.query(uri, projection, null, null,null);

        if (cursor.moveToFirst()){

            Libro libro = new Libro();
            libro.setID(libroId);
            //libro.setID(cursor.getInt(cursor.getColumnIndex(Contrato.Libro._ID)));
            libro.setTitulo(cursor.getString(cursor.getColumnIndex(Contrato.Libro.TITULO)));
            libro.setPaginas(cursor.getString(cursor.getColumnIndex(Contrato.Libro.PAGINAS)));

            return libro;
        }
        // en caso de no encontrar el registro devuelve nulo
        return null;
    }
}
