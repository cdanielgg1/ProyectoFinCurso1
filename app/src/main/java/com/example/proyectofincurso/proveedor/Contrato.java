package com.example.proyectofincurso.proveedor;

import android.net.Uri;
import android.provider.BaseColumns;

public class Contrato {

    public static final String AUTHORITY = "com.example.proyectofincurso.proveedor.ProveedorDeContenido";

    public static final class Libro implements BaseColumns {

        public static final Uri CONTENT_URI = Uri
                .parse("content://"+AUTHORITY+"/Libro");

        // Table column NAME OF DB
        public static final String TITULO = "Titulo";
        public static final String  PAGINAS = "Paginas";
    }
}
