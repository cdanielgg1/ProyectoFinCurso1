package com.example.proyectofincurso.proveedor;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;

public class ProveedorDeContenido extends ContentProvider {
    //private static final String LOGTAG = "Tiburcio - ProveedorDeContenido";

    private static final int LIBRO_ONE_REG = 1; //content://com.example.carlos.pclibros.proveedor.ProveedorDeContenido/Libro/#
    private static final int LIBRO_ALL_REGS = 2; //content://com.example.carlos.pclibros.proveedor.ProveedorDeContenido/Libro

    private SQLiteDatabase sqlDB;
    public DatabaseHelper dbHelper;
    private static final String DATABASE_NAME = "Libros.db";
    private static final int DATABASE_VERSION = 101;

    private static final String LIBRO_TABLE_NAME = "Libro";

    // Indicates an invalid content URI
    public static final int INVALID_URI = -1;

    // Defines a helper object that matches content URIs to table-specific parameters
    private static final UriMatcher sUriMatcher;

    // Stores the MIME types served by this provider
    private static final SparseArray<String> sMimeTypes;

    /*
     * Initializes meta-data used by the content provider:
     * - UriMatcher that maps content URIs to codes
     * - MimeType array that returns the custom MIME type of a table
     */
    static {

        // Creates an object that associates content URIs with numeric codes
        sUriMatcher = new UriMatcher(0);

        /*
         * Sets up an array that maps content URIs to MIME types, via a mapping between the
         * URIs and an integer code. These are custom MIME types that apply to tables and rows
         * in this particular provider.
         */
        sMimeTypes = new SparseArray<String>();

        // Adds a URI "match" entry that maps picture URL content URIs to a numeric code

        sUriMatcher.addURI(
                Contrato.AUTHORITY,
                LIBRO_TABLE_NAME,
                LIBRO_ALL_REGS);
        sUriMatcher.addURI(
                Contrato.AUTHORITY,
                LIBRO_TABLE_NAME + "/#",
                LIBRO_ONE_REG);

        // Specifies a custom MIME type for the picture URL table

        sMimeTypes.put(
                LIBRO_ALL_REGS,
                "vnd.android.cursor.dir/vnd." +
                        Contrato.AUTHORITY + "." + LIBRO_TABLE_NAME);
        sMimeTypes.put(
                LIBRO_ONE_REG,
                "vnd.android.cursor.item/vnd."+
                        Contrato.AUTHORITY + "." + LIBRO_TABLE_NAME);
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);

            //if (!db.isReadOnly()){
            //Habilitamos la integridad referencial
            db.execSQL("PRAGMA foreign_keys=ON;");
            //}
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create table to store

            db.execSQL("Create table "
                            + LIBRO_TABLE_NAME
                            + "( _id INTEGER PRIMARY KEY ON CONFLICT ROLLBACK AUTOINCREMENT, "
                            + Contrato.Libro.TITULO + " TEXT , "
                            + Contrato.Libro.PAGINAS + " INTEGER ); "
            );

            inicializarDatos(db);

        }

        void inicializarDatos(SQLiteDatabase db){

            db.execSQL("INSERT INTO " + LIBRO_TABLE_NAME + " (" +  Contrato.Libro._ID + "," + Contrato.Libro.TITULO + "," + Contrato.Libro.PAGINAS + ") " +
                    "VALUES (1,'Suerte de haberte conocido','283')");
            db.execSQL("INSERT INTO " + LIBRO_TABLE_NAME + " (" +  Contrato.Libro._ID + "," + Contrato.Libro.TITULO + "," + Contrato.Libro.PAGINAS + ") " +
                    "VALUES (2,'Cuestionar la metafisica','120')");
            db.execSQL("INSERT INTO " + LIBRO_TABLE_NAME + " (" +  Contrato.Libro._ID + "," + Contrato.Libro.TITULO + "," + Contrato.Libro.PAGINAS + ") " +
                    "VALUES (3,'La vida del soltero','58')");
            db.execSQL("INSERT INTO " + LIBRO_TABLE_NAME + " (" +  Contrato.Libro._ID + "," + Contrato.Libro.TITULO + "," + Contrato.Libro.PAGINAS + ") " +
                    "VALUES (4,'Vistas en el paraiso','253')");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + LIBRO_TABLE_NAME);

            onCreate(db);
        }

    }

    public ProveedorDeContenido() {
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return (dbHelper == null) ? false : true;
    }

    public void resetDatabase() {
        dbHelper.close();
        dbHelper = new DatabaseHelper(getContext());
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        sqlDB = dbHelper.getWritableDatabase();

        String table = "";
        switch (sUriMatcher.match(uri)) {
            case LIBRO_ALL_REGS:
                table = LIBRO_TABLE_NAME;
                break;
        }

        long rowId = sqlDB.insert(table, "", values);

        if (rowId > 0) {
            Uri rowUri = ContentUris.appendId(
                    uri.buildUpon(), rowId).build();
            getContext().getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
        throw new SQLException("Failed to insertRecord row into " + uri);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        sqlDB = dbHelper.getWritableDatabase();
        // insertRecord record in user table and get the row number of recently inserted record

        String table = "";
        switch (sUriMatcher.match(uri)) {
            case LIBRO_ONE_REG:
                if (null == selection) selection = "";
                selection += Contrato.Libro._ID + " = "
                        + uri.getLastPathSegment();
                table = LIBRO_TABLE_NAME;
                break;
            case LIBRO_ALL_REGS:
                table = LIBRO_TABLE_NAME;
                break;
        }
        int rows = sqlDB.delete(table, selection, selectionArgs);
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return rows;
        }
        throw new SQLException("Failed to deleteRecord row into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = null;

        switch (sUriMatcher.match(uri)) {
            case LIBRO_ONE_REG:
                if (null == selection) selection = "";
                selection += Contrato.Libro._ID + " = "
                        + uri.getLastPathSegment();
                qb.setTables(LIBRO_TABLE_NAME);
                break;
            case LIBRO_ALL_REGS:
                if (TextUtils.isEmpty(sortOrder)) sortOrder =
                        Contrato.Libro._ID + " ASC";
                qb.setTables(LIBRO_TABLE_NAME);
                break;
        }

        Cursor c;
        c = qb.query(db, projection, selection, selectionArgs, null, null,
                        sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        sqlDB = dbHelper.getWritableDatabase();
        // insertRecord record in user table and get the row number of recently inserted record

        String table = "";
        switch (sUriMatcher.match(uri)) {
            case LIBRO_ONE_REG:
                if (null == selection) selection = "";
                selection += Contrato.Libro._ID + " = "
                        + uri.getLastPathSegment();
                table = LIBRO_TABLE_NAME;
                break;
            case LIBRO_ALL_REGS:
                table = LIBRO_TABLE_NAME;
                break;
        }

        int rows = sqlDB.update(table, values, selection, selectionArgs);
        if (rows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            return rows;
        }
        throw new SQLException("Failed to updateRecord row into " + uri);
    }
}
