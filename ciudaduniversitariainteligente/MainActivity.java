package com.holamundo.ciudaduniversitariainteligente;


import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /* Atributos de la clase*/
    private ArmaCamino oArmaCamino = null;
    private MapsFragment mapsFragment = null;
    private FragmentManager fm = getFragmentManager();
    private FloatingActionButton qrBoton = null;
    private IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    private ultimasBusquedas ultimasBusquedas = null;
    private Menu menu = null;
    /*Funciones*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ciudad Inteligente");
        setSupportActionBar(toolbar);

        //Instancio los objetos para ArmaCamino y el MapFragment
        oArmaCamino = new ArmaCamino(this);
        mapsFragment = new MapsFragment();
        ultimasBusquedas = new ultimasBusquedas();  ultimasBusquedas.setMainActivity(this);

        //Agrego Nodos a mi vector de nodos en oArmaCamino
        cargaNodos();

        //Boton Flotante que está abajo a la derecha, para leer QR
        qrBoton = (FloatingActionButton) findViewById(R.id.fab);
        qrBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se procede con el proceso de scaneo
                scanIntegrator.initiateScan();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Cambio el fragment por defecto por mi mapFragment
        fm.beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();

    }

    //Boton para volver atras del telefono
    //Si estoy viendo otro Fragment distinto a MapFragment, vuelvo atras en la pila. Si estoy en MapFragment, salgo de la aplicacion
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(fm.getBackStackEntryCount() == 0){
                super.onBackPressed();
            }
            else{
                if(fm.findFragmentById(R.id.fragment_container) instanceof MapsFragment){
                    finish();
                }
                else {
                    fm.popBackStack();
                    mapsFragment.limpiarMapa();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        return true;
    }

    //Menu que está arriba a la derecha, para maenjar los pisos que tiene el camino y en cual estoy parado
    //Mejorar esto
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //El item que tiene un * al principio, es el piso que estoy viendo
        for(int i=0;i<menu.size();i++){
            if(menu.getItem(i).getTitle().charAt(0) == '*'){
                menu.getItem(i).setTitle(menu.getItem(i).getTitle().toString().substring(1));
                item.setTitle("*"+item.getTitle());
                break;
            }
        }
        /*Si estoy mostrando una polilinea, la cambio segun la opcion de piso seleccionada*/
        if(mapsFragment.modoPolilinea()) {
            //Planta Baja
            if (item.toString().contains("Baja")) {
                mapsFragment.cambiarPolilinea(0);
                return true;
            //El resto de los pisos
            } else {
                mapsFragment.cambiarPolilinea(Integer.parseInt(item.toString().substring(item.toString().indexOf(' ') + 1)));
            }
        }
        /*Esto es si estoy mostrando marcadores sueltos en el mapa*/
        else{
            if (item.toString().contains("Baja")) {
                mapsFragment.cambiarNodos(0);
                return true;
            } else {
                mapsFragment.cambiarNodos(Integer.parseInt(item.toString().substring(item.toString().indexOf(' ') + 1)));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Switch segun en que opcion del menu desplegable se selecciona
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.buscar) {
            //Fragment de busqueda, para hacer una busqueda nueva
            if (!(fm.findFragmentById(R.id.fragment_container) instanceof Busqueda)) {
                qrBoton.hide();
                menu.clear();
                Busqueda busqueda = new Busqueda();
                fm.popBackStack();
                fm.beginTransaction().replace(R.id.fragment_container, busqueda).addToBackStack(null).commit();
            }

        } else if (id == R.id.mapa_completo) {
            //Vuelvo al mapa de Google y le quito todo. Solo dejo el marcador de mi posicion
            mapsFragment.limpiarMapa();
            menu.clear();
            if (!(fm.findFragmentById(R.id.fragment_container) instanceof MapsFragment)) {
                qrBoton.show();
                fm.beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
            }

        } else if (id == R.id.ultimas) {
            //Fragment para ver las ultimas busquedas
            if (!(fm.findFragmentById(R.id.fragment_container) instanceof ultimasBusquedas)) {
                qrBoton.hide();
                menu.clear();
                fm.popBackStack();
                fm.beginTransaction().replace(R.id.fragment_container, ultimasBusquedas).addToBackStack(null).commit();
            }

        }/* else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Mostrar busqueda llama a las funciones del mapFragment que:
    -muestran un conjunto de puntos en el mapa
    -muestran una polilinea desde el punto mas cercano hasta el objetivo
    *setPuntoMasCercano setea en oArmaCamino el nodo mas cercano a la posición donde estoy parado
    Luego reemplazo el fragment de Busqueda por el de mapa
    */
    public void mostrarBusqueda(String Edificio, String Nombre) {
        mapsFragment.setPisoActual(0);
        if (Edificio.equals("*")) {
            mapsFragment.mostrarNodos(oArmaCamino.nodosMapa(Nombre));
            menu.clear();
            menu.add("Planta Baja");
            for(int i=1;i<mapsFragment.getCantPisos();i++){
                menu.add("Piso "+i);
            }
            menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            oArmaCamino.setPuntoMasCercano(mapsFragment.getPosicion(),mapsFragment.getPisoActual());
            mapsFragment.dibujaCamino(oArmaCamino.camino(Edificio, Nombre));
            menu.clear();
            menu.add("Planta Baja");
            for(int i=1;i<mapsFragment.getCantPisos();i++){
                menu.add("Piso "+i);
            }
            menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
            getMenuInflater().inflate(R.menu.main, menu);
            String texto = "Su objetivo está en " + oArmaCamino.getPisoObjetivo();
            Toast.makeText(getApplicationContext(),texto,Toast.LENGTH_LONG).show();
        }
        fm.beginTransaction().replace(R.id.fragment_container, mapsFragment).addToBackStack(null).commit();
        qrBoton.show();
    }

    //Funcion que le pasa a oArmaCamino un edificio y devuelve un Vector con todas las aulas de ese edificio
    public Vector<Punto> verAulasPorEdificio(String Edificio) {
        return oArmaCamino.verAulasPorEdificio(Edificio);
    }


    //Funcion para crear nodos del mapa y sus conexiones
    private void cargaNodos() {
        Punto P1 = new Punto(-31.640740, -60.671861, "Ciudad Universitaria", 0, "Entrada Ciudad Universitaria",R.drawable.p1);
        Punto P2 = new Punto(-31.640142, -60.671858, " ", 0, "Cajero",R.drawable.p2);
        Punto P3 = new Punto(-31.639968, -60.671869, "FICH", 0, "Entrada FICH");
        Punto P4 = new Punto(-31.639962, -60.672141, "FICH", 0, "Aula 8");

        Punto P25 = new Punto (-31.639936,-60.672263,"FICH",1,"Escalera");
        Punto P26 = new Punto (-31.639934, -60.672147,"FICH",1,"");
        Punto P27 = new Punto(-31.639753,-60.672159,"FICH",1,"Baños");
        Punto P28 = new Punto(-31.639755,  -60.672330,"FICH",1,"Aula Laboratorio 1 y 2");


        Punto P5 = new Punto(-31.639970, -60.672441, "FICH", 0, "Fotocopiadora - Baño");
        Punto P6 = new Punto(-31.639841, -60.672446, "FICH", 0, "Cantina");
        Punto P7 = new Punto(-31.639759, -60.672446, "FICH", 0, "Aula Magna - Aula 3");
        Punto P8 = new Punto(-31.639755, -60.672220, "FICH", 0, "Aula 5");
        Punto P9 = new Punto(-31.639761, -60.672502, "FICH", 0, "Aula 3");
        Punto P10 = new Punto(-31.639652, -60.672495, "FICH", 0, "Bicicletero");
        Punto P11 = new Punto(-31.639763, -60.672643, "FICH", 0, "Aula 1 - Aula 2");
        Punto P12 = new Punto(-31.639976, -60.672759, "FCBC", 0, "Fotocopiadora");
        Punto P13 = new Punto(-31.639985, -60.673129, "FCBC", 0, "Entrada FCBC");
        Punto P14 = new Punto(-31.640214, -60.673140, " ", 0, "Fuente");
        Punto P15 = new Punto(-31.640214, -60.673322, "FADU - FHUC ", 0, "Entrada FADU - FHUC");
        Punto P16 = new Punto(-31.640450, -60.673316, " ", 0, " ");
        Punto P17 = new Punto(-31.640459, -60.673917, "ISM", 0, "Entrada ISM");
        Punto P18 = new Punto(-31.639978, -60.673879, "Cubo", 0, "Entrada Cubo");
        Punto P19 = new Punto(-31.639697, -60.673139, " ", 0, "");
        Punto P20 = new Punto(-31.639910, -60.670980, " ", 0, " ");
        Punto P21 = new Punto(-31.639609, -60.670975, "FCM", 0, "Entrada FCM - Cantina");

        Punto P22 = new Punto(-31.639605, -60.671808, " ", 0, " ");
        Punto P23 = new Punto(-31.640545, -60.673222, " ", 0, " ");
        Punto P24 = new Punto(-31.640975, -60.673189, "Ciudad Universitaria", 0, "Salida");

        P1.addVecino(P2);
        P2.addVecino(P1);
        P2.addVecino(P3);
        P3.addVecino(P2);
        P3.addVecino(P4);
        P3.addVecino(P20);
        P3.addVecino(P22);
        P4.addVecino(P3);
        P4.addVecino(P5);

        P4.addVecino(P25);
        P25.addVecino(P4); P25.addVecino(P26);
        P26.addVecino(P25); P26.addVecino(P27);
        P27.addVecino(P26); P27.addVecino(P28);
        P28.addVecino(P27);

        P5.addVecino(P4);
        P5.addVecino(P6);
        P5.addVecino(P12);
        P6.addVecino(P5);
        P6.addVecino(P7);
        P7.addVecino(P6);
        P7.addVecino(P8);
        P7.addVecino(P9);
        P8.addVecino(P7);
        P9.addVecino(P7);
        P9.addVecino(P10);
        P9.addVecino(P11);
        P10.addVecino(P9);
        P10.addVecino(P19);
        P10.addVecino(P22);
        P11.addVecino(P9);
        P12.addVecino(P5);
        P12.addVecino(P13);
        P13.addVecino(P12);
        P13.addVecino(P14);
        P13.addVecino(P18);
        P13.addVecino(P19);
        P14.addVecino(P13);
        P14.addVecino(P15);
        P14.addVecino(P23);
        P15.addVecino(P14);
        P15.addVecino(P16);
        P16.addVecino(P15);
        P16.addVecino(P17);
        P17.addVecino(P16);
        P18.addVecino(P13);
        P19.addVecino(P13);
        P19.addVecino(P10);
        P20.addVecino(P3);
        P20.addVecino(P21);
        P21.addVecino(P20);
        P21.addVecino(P22);
        P22.addVecino(P21);
        P22.addVecino(P10);
        P23.addVecino(P14);
        P23.addVecino(P24);
        P24.addVecino(P23);

        oArmaCamino.addNodo(P1);
        oArmaCamino.addNodo(P2);
        oArmaCamino.addNodo(P3);
        oArmaCamino.addNodo(P4);
        oArmaCamino.addNodo(P5);
        oArmaCamino.addNodo(P6);
        oArmaCamino.addNodo(P7);
        oArmaCamino.addNodo(P8);
        oArmaCamino.addNodo(P9);
        oArmaCamino.addNodo(P10);
        oArmaCamino.addNodo(P11);
        oArmaCamino.addNodo(P12);
        oArmaCamino.addNodo(P13);
        oArmaCamino.addNodo(P14);
        oArmaCamino.addNodo(P15);
        oArmaCamino.addNodo(P16);
        oArmaCamino.addNodo(P17);
        oArmaCamino.addNodo(P18);
        oArmaCamino.addNodo(P19);
        oArmaCamino.addNodo(P20);
        oArmaCamino.addNodo(P21);
        oArmaCamino.addNodo(P22);
        oArmaCamino.addNodo(P23);
        oArmaCamino.addNodo(P24);
        oArmaCamino.addNodo(P25);
        oArmaCamino.addNodo(P26);
        oArmaCamino.addNodo(P27);
        oArmaCamino.addNodo(P28);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //Se obtiene el resultado del proceso de scaneo y se parsea
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //Quiere decir que se obtuvo resultado pro lo tanto:
            //Actualizo los datos en MapsFragment
            String scanContent = scanningResult.getContents();
            mapsFragment.setLat(Double.parseDouble(scanContent.toString().substring(0, (scanContent.toString().indexOf(',')))));
            mapsFragment.setLon(Double.parseDouble(scanContent.toString().substring((scanContent.toString().indexOf(',')) + 1, scanContent.length()-2)));
            mapsFragment.setPisoActual(Integer.parseInt(scanContent.toString().substring(scanContent.toString().length()-1)));
            mapsFragment.actualizaPosicion();

            //Actualizo el * del menu de pisos cuando cambio el piso por QR, buscar como mejorar esto
            if(mapsFragment.getPisoActual()+1 <= mapsFragment.getCantPisos()) {
                for (int i = 0; i < menu.size(); i++) {
                    if (menu.getItem(i).getTitle().charAt(0) == '*') {
                        menu.getItem(i).setTitle(menu.getItem(i).getTitle().toString().substring(1));
                        menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                        break;
                    }
                }
            }
        } else {
            //Quiere decir que NO se obtuvo resultado
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
