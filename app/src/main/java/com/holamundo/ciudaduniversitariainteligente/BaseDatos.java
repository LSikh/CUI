package com.holamundo.ciudaduniversitariainteligente;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lautaro on 30/11/2016.
 */
public class BaseDatos extends SQLiteOpenHelper {

    private static final String INSERT_PUNTO_TEMPLATE = new String("INSERT INTO Punto (id, latitud, longitud, edificio, piso, nombre, imagen) VALUES " +
            "('%s', '%s', '%s', '%s', '%s', '%s', '%s')");

    private static final String INSERT_CONEXION_TEMPLATE = new String("INSERT INTO Conexiones (idDesde, idHasta) VALUES (%s, %s)");

    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        cargaDB(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Busquedas");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Punto");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Conexiones");

        //Se crea la nueva versión de las tablas
        cargaDB(sqLiteDatabase);
    }


    public void cargaDB(SQLiteDatabase sqLiteDatabase){
        //Creo las tablas
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Punto");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Conexiones");
        sqLiteDatabase.execSQL("CREATE TABLE Busquedas (nombre TEXT, edificio TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE Punto (id TEXT, latitud TEXT, longitud TEXT, edificio TEXT, piso TEXT, nombre TEXT, imagen TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE Conexiones (idDesde TEXT, idHasta TEXT)");
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 0, "-31.640935", "-60.671913", "Ciudad Universitaria", 0, "Ciudad Universitaria", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 1, "-31.639950", "-60.671896", "FICH", 0, "Entrada FICH", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 2, "-31.639953", "-60.672090", "FCBC", 0, "Aula Magna", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 3, "-31.639962", "-60.672151", "FICH", 0, "Escalera", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 4, "-31.639965", "-60.672261", "FICH", 0, "Fotocopiadora", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 5, "-31.639965", "-60.672306", "FICH", 0, "Baños", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 6, "-31.639963", "-60.6724285", "FICH", 0, "", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 7, "-31.639898", "-60.672430", "FICH", 0, "Cantina", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 8, "-31.639799", "-60.672426", "FICH", 0, "Aula 4", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 9, "-31.639799", "-60.672361", "FICH", 0, "Aula Magna", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 10, "-31.639798", "-60.672181", "FICH", 0, "Aula 5 - Aula 8", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 11, "-31.639801", "-60.672471", "FICH", 0, "Aula 3", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 12, "-31.639799", "-60.672556", "FICH", 0, "", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 13, "-31.639799", "-60.672610", "FICH", 0, "Aula 1 - Aula 2", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 14, "-31.639969", "-60.672544", "FICH", 0, "Escalera", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 15, "-31.639937", "-60.671198", "FICH", 0, "", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 16, "-31.639651", "-60.671193", "FICH", 0, "Aula 7", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 17, "-31.639710", "-60.671893", "FICH", 0, "", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 18, "-31.639713", "-60.671578", "FICH", 0, "", -1));
        sqLiteDatabase.execSQL(String.format(INSERT_PUNTO_TEMPLATE, 19, "-31.639811", "-60.671578", "FICH", 0, "Aula 6", -1));


        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 0, 1));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 1, 0));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 1, 2));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 1, 15));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 1, 17));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 2, 1));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 2, 3));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 3, 2));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 3, 4));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 4, 3));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 4, 5));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 5, 4));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 5, 6));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 6, 5));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 6, 7));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 6, 14));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 7, 6));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 7, 8));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 8, 7));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 8, 9));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 8, 11));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 9, 8));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 9, 10));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 10, 9));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 11, 8));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 11, 12));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 12, 11));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 12, 13));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 13, 12));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 14, 6));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 15, 1));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 15, 16));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 16, 15));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 17, 1));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 17, 18));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 18, 17));
        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 18, 19));

        sqLiteDatabase.execSQL(String.format(INSERT_CONEXION_TEMPLATE, 19, 18));

    }
}
