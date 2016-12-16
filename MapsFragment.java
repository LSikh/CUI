package com.holamundo.ciudaduniversitariainteligente;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

    private Vector<PolylineOptions> misPolilineas = new Vector<>();
    private Vector<MarkerOptions> misMarcadores = new Vector<>();
    private Vector<MarkerOptions> marcadoresPiso = new Vector<>();


    private float angle = 0;
    private double lat;
    private double lon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        MapFragment fragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

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
        miMapa.clear();
        miMapa.addMarker(miPosicion);
    }

    //Actualizo mi posición si me moví. Quito mi marcador y lo pongo en donde corresponde
    void actualizaPosicion() {
        LatLng position = new LatLng(this.lat, this.lon);
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
        miPosicion.position(position);
        miPosicionMarcador.remove();
        miPosicionMarcador = miMapa.addMarker(miPosicion);
        if(misPolilineas.size()!= 0){
            cambiarPolilinea(pisoActual);
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
        for(int i=0;i<path.size();i++){
            if(path.elementAt(i).getPiso() > cantPisos){
                cantPisos = path.elementAt(i).getPiso();
            }
        }
        cantPisos = cantPisos +1;
        for(int i=0;i<cantPisos;i++){
            PolylineOptions p = new PolylineOptions().width(7);
            misPolilineas.add(p);
        }
        for(int i=0;i<path.size();i++) {
            misPolilineas.elementAt(path.elementAt(i).getPiso()).add(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()));

        }

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


            /*if(path.elementAt(path.size()-1).getEdificio().equals("FICH")){
            LatLngBounds newarkBounds = new LatLngBounds(
                    new LatLng(-31.640041, -60.672696),       // South west corner
                    new LatLng(-31.639671, -60.671973));      // North east corner
            GroundOverlayOptions overlay = new GroundOverlayOptions()
                    .positionFromBounds(newarkBounds)
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.planta_baja));
            miMapa.addGroundOverlay(overlay);
        }*/
    }

    //Recibo un conjunto de puntos y creo marcadores para todos ellos
    public void mostrarNodos(Vector<Punto> nodos) {
        misMarcadores.clear();
        misPolilineas.clear();
        marcadoresPiso.clear();
        cantPisos = 0;
        for (int i = 0; i < nodos.size(); i++) {
            String texto;
            if(nodos.elementAt(i).getPiso() ==0){
                texto = "Planta Baja";
            }
            else{
                texto = "Piso " + nodos.elementAt(i).getPiso();
            }
            if(nodos.elementAt(i).getPiso() > cantPisos){
                cantPisos = nodos.elementAt(i).getPiso();
            }
            misMarcadores.add(new MarkerOptions().position(new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud())).title(nodos.elementAt(i).getNombre() + " - " + texto).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
    }

    public void cambiarPolilinea(int piso){
        miMapa.clear();
        miMapa.addMarker(miPosicion);
        miMapa.addPolyline(misPolilineas.elementAt(piso));
        miMapa.addMarker(marcadoresPiso.elementAt(2*piso));
        miMapa.addMarker(marcadoresPiso.elementAt(2*piso+1));
    }

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

