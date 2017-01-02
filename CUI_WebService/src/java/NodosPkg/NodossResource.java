/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NodosPkg;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Lautaro
 */
@Path("/nodos")
public class NodossResource {

    private Vector<Punto> oArmaCamino = new Vector<>();
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of NodossResource
     */
    public NodossResource() {
        Punto P1 = new Punto(-31.640740, -60.671861, "Ciudad Universitaria", 0, "Entrada Ciudad Universitaria");
        Punto P2 = new Punto(-31.640142, -60.671858, " ", 0, "Cajero");
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

        oArmaCamino.add(P1);
        oArmaCamino.add(P2);
        oArmaCamino.add(P3);
        oArmaCamino.add(P4);
        oArmaCamino.add(P5);
        oArmaCamino.add(P6);
        oArmaCamino.add(P7);
        oArmaCamino.add(P8);
        oArmaCamino.add(P9);
        oArmaCamino.add(P10);
        oArmaCamino.add(P11);
        oArmaCamino.add(P12);
        oArmaCamino.add(P13);
        oArmaCamino.add(P14);
        oArmaCamino.add(P15);
        oArmaCamino.add(P16);
        oArmaCamino.add(P17);
        oArmaCamino.add(P18);
        oArmaCamino.add(P19);
        oArmaCamino.add(P20);
        oArmaCamino.add(P21);
        oArmaCamino.add(P22);
        oArmaCamino.add(P23);
        oArmaCamino.add(P24);
        oArmaCamino.add(P25);
        oArmaCamino.add(P26);
        oArmaCamino.add(P27);
        oArmaCamino.add(P28);
        
    }

    /**
     * Retrieves representation of an instance of NodosPkg.NodossResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        
        return "hola mundo";
        //TODO return proper representation object
        //throw new UnsupportedOperationException();
    }
    
    public String oArmaCaminoToString(){
        String res = "";       
        for(int i=0;i<oArmaCamino.size();i++){
            
        }
        return res;
    }
}
