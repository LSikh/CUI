package com.holamundo.ciudaduniversitariainteligente;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Lautaro on 29/11/2016.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {

    public GoogleMap miMapa = null;
    private SensorManager miSensorManager;
    private MarkerOptions miPosicion = null;
    private Marker miPosicionMarcador = null;
    private int cantPisos = 0;
    private int pisoActual = 0;
    private int cantidad_edificios = 2;           //Cantidad de edificios relevados

    private Vector<PolylineOptions> misPolilineas = new Vector<>();
    private Vector<MarkerOptions> marcadoresPiso = new Vector<>();

    private Vector<MarkerOptions> misMarcadores = new Vector<>();

    private Vector<Vector<GroundOverlayOptions>> misOverlays = new Vector<>();

    private Map<String, LatLngBounds> hashMapBounds = new HashMap<>();
    private Map<String, Integer> hashMapID = new HashMap<>();

    private float angle = 0;
    private double lat;
    private double lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

        cargarMapaBounds();
        cargarMapaID();

        //LocationManager
        LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener mlocListener = new MyLocationListener();
        mlocListener.setMainActivity(this);
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) mlocListener);

        //SensorManager
        SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 500000);
        this.miSensorManager = mSensorManager;
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        miMapa = googleMap;

        miMapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("Prueba",latLng.latitude + ", " + latLng.longitude);
            }
        });

        LatLng position = new LatLng(this.lat, this.lon);
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
        miMapa.moveCamera(CameraUpdateFactory.zoomTo(18));
        miPosicion = new MarkerOptions().position(new LatLng(this.lat, this.lon)).title("Usted está aquí");
        miPosicionMarcador = miMapa.addMarker(miPosicion);
        //Hasta aca, muevo la camara hasta mi posicion y agrego un marcador allí

        //Agrego los marcadores adicionales (Edificios, baños, bares,etc), si los hay
        for (int i = 0; i < misMarcadores.size(); i++) {
            String texto;
            if(pisoActual == 0){
                texto = "Planta Baja";
            }
            else{
                texto = "Piso " + pisoActual;
            }
            if(misMarcadores.elementAt(i).getTitle().contains(texto)) {
                miMapa.addMarker(misMarcadores.elementAt(i));
            }
        }

        //Agrego polilinea si la hay
        if(misPolilineas.size() != 0){
            miMapa.addPolyline(misPolilineas.elementAt(pisoActual));
            miMapa.addMarker(marcadoresPiso.elementAt(2*pisoActual));
            miMapa.addMarker(marcadoresPiso.elementAt(2*pisoActual+1));
        }

        //Agrego los overlays
        if(misOverlays.size()!=0) {
            for (int i = 0; i < misOverlays.elementAt(pisoActual).size(); i++) {
                miMapa.addGroundOverlay(misOverlays.elementAt(pisoActual).elementAt(i));
            }
        }
    }

    //Setters y getters de latitud y longitud
    public void setLat(double l) {
        this.lat = l;
    }

    public void setLon(double l) {
        this.lon = l;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public int getCantPisos(){
        return cantPisos;
    }

    public void setPisoActual(int p){this.pisoActual = p;}

    public int getPisoActual(){return pisoActual;}

    public boolean modoPolilinea(){return !misPolilineas.isEmpty();}

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        synchronized (this) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    float degree = Math.round(sensorEvent.values[0]);
                    //Si el angulo de rotación con respecto a la rotación de la muestra anterior es mayor a 20
                    //roto la camara, sino no
                    if (Math.abs(degree - angle) > 20) {
                        angle = degree;
                        CameraPosition oldPos = miMapa.getCameraPosition();
                        CameraPosition pos = CameraPosition.builder(oldPos).bearing(degree).build();
                        miMapa.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                    }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Limpio el mapa de polilineas, marcadores, etc
    public void limpiarMapa() {
        miPosicionMarcador.remove();
        misPolilineas.clear();
        marcadoresPiso.clear();
        misOverlays.clear();
        miMapa.clear();
        miMapa.addMarker(miPosicion);
        setPisoActual(0);
    }

    //Actualizo mi posición si me moví. Quito mi marcador y lo pongo en donde corresponde
    @TargetApi(Build.VERSION_CODES.M)
    void actualizaPosicion() {
        LatLng position = new LatLng(this.lat, this.lon);
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
        miPosicion.position(position);
        miPosicionMarcador.remove();
        miPosicionMarcador = miMapa.addMarker(miPosicion);
        if(!misPolilineas.isEmpty() && pisoActual+1<=misPolilineas.size()){
            cambiarPolilinea(pisoActual);
        }
        else if(pisoActual+1 > misPolilineas.size() && !misPolilineas.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(),"Su objetivo está en un piso inferior",Toast.LENGTH_LONG).show();
        }
        if(!misMarcadores.isEmpty() && pisoActual+1<=misMarcadores.size()){
            cambiarNodos(pisoActual);
        }
        else if(pisoActual+1 > misMarcadores.size() && !misMarcadores.isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(),"Su objetivo está en un piso inferior",Toast.LENGTH_LONG).show();
        }
    }

    //Obtengo mi latitud y longitud en un objeto LatLng
    public LatLng getPosicion() {
        return new LatLng(this.lat, this.lon);
    }

    //Recibo un vector de puntos y creo un polilinea con ellos
    public void dibujaCamino(Vector<Punto> path) {
        misPolilineas.clear();
        misMarcadores.clear();
        marcadoresPiso.clear();
        cantPisos = 0;
        Vector<String> edificios = new Vector<>();
        for(int i=0;i<path.size();i++){
            if(path.elementAt(i).getPiso() > cantPisos){
                cantPisos = path.elementAt(i).getPiso();
            }
        }

        //Creo las polilineas y overlays que voy a usar
        cantPisos = cantPisos +1;
        for(int i=0;i<cantPisos;i++){
            PolylineOptions p = new PolylineOptions().width(7);
            Vector<GroundOverlayOptions> g = new Vector<>();
            misPolilineas.add(p);
            misOverlays.add(g);
        }

        //Agrego puntos a las polilineas segun piso e identifico por que edificios y pisos pasa mi polilinea
        for(int i=0;i<path.size();i++) {
            misPolilineas.elementAt(path.elementAt(i).getPiso()).add(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()));
            for(int j=0;j<cantidad_edificios;j++){
                //Veo si ese marcador está dentro de algun edificio
                if(hashMapBounds.containsKey("ed" + j + "_" + path.elementAt(i).getPiso())){
                    if (dentroDeLimites(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()), hashMapBounds.get("ed" + j + "_" + path.elementAt(i).getPiso()))) {
                        if (!edificios.contains("ed" + j + "_" + path.elementAt(i).getPiso())) {
                            edificios.add("ed" + j + "_" + path.elementAt(i).getPiso());
                        }
                    }
                }
            }
        }

        //Agrego los overlays a mi vector
        for(int i=0;i<edificios.size();i++){
            if(hashMapID.containsKey(edificios.elementAt(i))) {
                misOverlays.elementAt(Integer.parseInt(edificios.elementAt(i).substring(edificios.elementAt(i).indexOf("_") + 1)))
                        .add(new GroundOverlayOptions()
                                .positionFromBounds(hashMapBounds.get(edificios.elementAt(i)))
                                .image(BitmapDescriptorFactory.fromResource(hashMapID.get(edificios.elementAt(i)))));
            }
        }

        //Busco cuales marcadores por piso voy a tener
        marcadoresPiso.add(new MarkerOptions()
                .position(new LatLng(path.elementAt(0).getLatitud(),path.elementAt(0).getLongitud()))
                .title(path.elementAt(0).getNombre())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        for(int i=1;i<path.size()-1;i++){
            if(path.elementAt(i).getPiso() != path.elementAt(i+1).getPiso()){
                marcadoresPiso.add(new MarkerOptions()
                        .position(new LatLng(path.elementAt(i).getLatitud(),path.elementAt(i).getLongitud()))
                        .title(path.elementAt(i).getNombre())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                marcadoresPiso.add(new MarkerOptions()
                        .position(new LatLng(path.elementAt(i+1).getLatitud(),path.elementAt(i+1).getLongitud()))
                        .title(path.elementAt(i+1).getNombre())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
        }

        marcadoresPiso.add(new MarkerOptions()
                .position(new LatLng(path.elementAt(path.size()-1).getLatitud(),path.elementAt(path.size()-1).getLongitud()))
                .title(path.elementAt(path.size()-1).getNombre())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    //Recibo un conjunto de puntos y creo marcadores para todos ellos
    public void mostrarNodos(Vector<Punto> nodos) {
        misMarcadores.clear();
        misPolilineas.clear();
        marcadoresPiso.clear();
        Vector<String> edificios = new Vector<>();
        cantPisos = 0;
        for (int i = 0; i < nodos.size(); i++) {
            String texto;
            if(nodos.elementAt(i).getPiso() ==0){
                texto = "Planta Baja";
            }
            else{
                texto = "Piso " + nodos.elementAt(i).getPiso();
            }
            //Cuento la cantidad de pisos en donde encontre lo que busco
            if(nodos.elementAt(i).getPiso() > cantPisos){
                cantPisos = nodos.elementAt(i).getPiso();
            }
            //Agrego los marcadores
            misMarcadores.add(new MarkerOptions().position(new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud())).title(nodos.elementAt(i).getNombre() + " - " + texto).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            for(int j=0;j<cantidad_edificios;j++){
                //Veo si ese marcador está dentro de algun edificio
                if(hashMapBounds.containsKey("ed" + j + "_" + nodos.elementAt(i).getPiso())) {
                    if (dentroDeLimites(new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud()), hashMapBounds.get("ed" + j + "_" + nodos.elementAt(i).getPiso()))) {
                        if (!edificios.contains("ed" + j + "_" + nodos.elementAt(i).getPiso())) {
                            edificios.add("ed" + j + "_" + nodos.elementAt(i).getPiso());
                        }
                    }
                }
            }
        }

        //Agrego los overlays a mi vector
        for(int i=0;i<edificios.size();i++){
            if(hashMapID.containsKey(hashMapID.get(edificios.elementAt(i)))) {
                misOverlays.elementAt(Integer.parseInt(edificios.elementAt(i).substring(edificios.elementAt(i).indexOf("_") + 1))).
                        add(new GroundOverlayOptions()
                                .positionFromBounds(hashMapBounds.get(edificios.elementAt(i)))
                                .image(BitmapDescriptorFactory.fromResource(hashMapID.get(edificios.elementAt(i)))));
            }
        }
    }

    public void cambiarPolilinea(int piso){
        miMapa.clear();
        miMapa.addMarker(miPosicion);
        miMapa.addPolyline(misPolilineas.elementAt(piso));
        miMapa.addMarker(marcadoresPiso.elementAt(2*piso));
        miMapa.addMarker(marcadoresPiso.elementAt(2*piso+1));

        //Agrego los overlays
        for(int i=0;i<misOverlays.elementAt(piso).size();i++){
            miMapa.addGroundOverlay(misOverlays.elementAt(piso).elementAt(i));
        }
    }

    //Funcion para actualizar los nodos según el piso que se quiera ver
    public void cambiarNodos(int piso){
        miMapa.clear();
        miMapa.addMarker(miPosicion);
        for(int i=0;i<misMarcadores.size();i++){
            if(piso == 0){
                if(misMarcadores.elementAt(i).getTitle().contains("Planta Baja")){
                    miMapa.addMarker(misMarcadores.elementAt(i));
                }
            }
            else{
                if(misMarcadores.elementAt(i).getTitle().contains("Piso "+piso)){
                    miMapa.addMarker(misMarcadores.elementAt(i));
                }
            }
        }

        //Agrego los overlays
        for(int i=0;i<misOverlays.elementAt(piso).size();i++){
            miMapa.addGroundOverlay(misOverlays.elementAt(piso).elementAt(i));
        }
    }

    //Funcion para saber si un punto está dentro de ciertos limites
    public boolean dentroDeLimites(LatLng posicion, LatLngBounds bounds){
        LatLng limiteInfIzquierdo = bounds.southwest;
        LatLng limiteSupDerecho = bounds.northeast;
        boolean esta = true;
        if (posicion.latitude > limiteSupDerecho.latitude || posicion.latitude < limiteInfIzquierdo.latitude || posicion.longitude > limiteSupDerecho.longitude || posicion.longitude < limiteInfIzquierdo.longitude){
            esta = false;
        }
        return esta;
    }

    //hashMap de Edificio - Limites de edificio
    public void cargarMapaBounds(){
        //Edificio 0 - FICH/FCBC
        hashMapBounds.put("ed0_0", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));
        hashMapBounds.put("ed0_1", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));
        hashMapBounds.put("ed0_2", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));
        hashMapBounds.put("ed0_3", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));

        //Edificio 1 - FCM
        hashMapBounds.put("ed1_0", new LatLngBounds(new LatLng(-31.639872, -60.670817), new LatLng(-31.639313, -60.670216)));
    }

    //hasMap de Edificio - Plano del Edificio
    public void cargarMapaID(){
        hashMapID.put("ed0_0", R.drawable.ed0_0);
        hashMapID.put("ed0_1", R.drawable.ed0_1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentManager fm = getActivity().getFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }
}

