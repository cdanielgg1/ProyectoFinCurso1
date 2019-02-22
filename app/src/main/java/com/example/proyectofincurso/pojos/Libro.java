package com.example.proyectofincurso.pojos;

import android.graphics.Bitmap;

import com.example.proyectofincurso.constantes.G;

public class Libro {
    private int ID;
   private String titulo;
   private String paginas;
    private Bitmap imagen;

    public Libro() {
        this.ID = G.SIN_VALOR_INT;
        this.titulo = G.SIN_VALOR_STRING;
        this.setPaginas(G.SIN_VALOR_STRING);
        this.setImagen(null);
    };

    public Libro(int ID, String titulo, String paginas, Bitmap imagen){

        this.ID = ID;
        this.titulo= titulo;
        this.setPaginas(paginas);
        this.setImagen(imagen);
    }
    public  int getID(){return ID; }

    public void setID (int ID){this.ID = ID;}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPaginas() {
        return paginas;
    }

    public void setPaginas(String paginas) {
        this.paginas = paginas;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}
