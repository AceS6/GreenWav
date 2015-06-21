package model.db.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.Borne;
import model.Line;
import model.Network;
import model.Route;
import model.Schedule;
import model.Station;
import model.Stop;

/**
 * Created by Alexis on 17/02/2015.
 */
public class JamboDAO {

    // ---------- ATTRIBUTES

    protected final static int VERSION = 2;
    protected final static String NOM = "jambo1.db";
    protected SQLiteDatabase db;
    protected Jambo handler;

    // ---------- TABLES
    // ----- reseau
    public static final String RESEAU = "reseau";
    public static final String RESEAU_ID = "id";
    public static final String RESEAU_NOM = "nom";
    public static final String RESEAU_IMAGE = "image";
    public static final String RESEAU_BUS = "bus";
    public static final String RESEAU_VELO = "velo";
    public static final String RESEAU_VOITURE = "voiture";
    public static final String RESEAU_BUSDAYBYDAY = "busdaybyday";
    public static final String RESEAU_LATITUDE = "latitude";
    public static final String RESEAU_LONGITUDE = "longitude";

    // ----- ligne
    public static final String LIGNE = "ligne";
    public static final String LIGNE_ID = "id";
    public static final String LIGNE_NUMERO = "numero";
    public static final String LIGNE_DESCRIPTION = "description";
    public static final String LIGNE_COULEUR = "couleur";
    public static final String LIGNE_RESEAU = "reseau";
    public static final String LIGNE_ETAT = "etat";
    public static final String LIGNE_FAVORIS = "favoris";
    public static final String LIGNE_DOWNLOAD = "download";


    // ----- route
    public static final String ROUTE = "route";
    public static final String ROUTE_ID = "id";
    public static final String ROUTE_LIGNE = "ligne";
    public static final String ROUTE_NOM = "nom";


    // ----- arret
    public static final String ARRET = "arret";
    public static final String ARRET_ID = "id";
    public static final String ARRET_NOM = "nom";
    public static final String ARRET_LATITUDE = "latitude";
    public static final String ARRET_LONGITUDE = "longitude";
    public static final String ARRET_RESEAU = "reseau";
    public static final String ARRET_FAVORIS = "favoris";

    // ----- appartient
    public static final String APPARTIENT = "appartient";
    public static final String APPARTIENT_ID = "id";
    public static final String APPARTIENT_ROUTE = "route";
    public static final String APPARTIENT_ARRET = "arret";
    public static final String APPARTIENT_POSITION = "position";
    public static final String APPARTIENT_DOWNLOAD = "download";

    // ----- horaire
    public static final String HORAIRE = "horaire";
    public static final String HORAIRE_ID = "id";
    public static final String HORAIRE_HORAIRE = "horaire";
    public static final String HORAIRE_CALENDRIER = "calendrier";
    public static final String HORAIRE_APPARTIENT = "appartient";

    // ----- station
    public static final String STATION = "station";
    public static final String STATION_ID = "id";
    public static final String STATION_NOM = "nom";
    public static final String STATION_ADRESSE = "adresse";
    public static final String STATION_LATITUDE = "latitude";
    public static final String STATION_LONGITUDE = "longitude";
    public static final String STATION_RESEAU = "reseau";
    public static final String STATION_IDEXT = "idext";

    // ----- borne
    public static final String BORNE = "borne";
    public static final String BORNE_ID = "id";
    public static final String BORNE_NOM = "nom";
    public static final String BORNE_ADRESSE = "adresse";
    public static final String BORNE_LATITUDE = "latitude";
    public static final String BORNE_LONGITUDE = "longitude";
    public static final String BORNE_NOMPORTEUR = "nomporteur";
    public static final String BORNE_TYPECHARGE = "typecharge";
    public static final String BORNE_NBREPDC = "nbrepdc";
    public static final String BORNE_TYPECONNECTEUR = "typeconnecteur";
    public static final String BORNE_OBSERVATIONS = "observations";
    public static final String BORNE_RESEAU = "reseau";

    private DAOCallback callback;


    // ---------- CONSTRUCTOR

    public JamboDAO(Context context) {
        this.handler = new Jambo(context, NOM, null, VERSION);
    }


    // ---------- METHODS

    public SQLiteDatabase open() {
        db = handler.getWritableDatabase();
        return db;
    } // ---------------------------------------------------------- open()

    public void close() {
        db.close();
    } // ---------------------------------------------------------- close()

    public SQLiteDatabase getDb() {
        return db;
    } // ---------------------------------------------------------- getDb()


    // ----- reseau

    public long insertReseau(Network network, DAOCallback callback) {
        this.callback = callback;
        long ret = 0;
        HashMap<Integer, Line> lignes = null;
        HashMap<Integer, Stop> arrets = null;
        HashMap<Integer, Station> stations = null;
        HashMap<Integer, Borne> bornes = null;

        // On cherche si le reseau a deja ete integre, si oui on le supprime avant de le re-rentre dans la base
        Cursor c = db.query(RESEAU, new String[]{RESEAU_ID}, RESEAU_ID + " LIKE '" + network.getIdBdd() + "'", null, null, null, null);

        if (c != null && c.moveToFirst()) {
            this.removeReseau(network.getIdBdd());
        }

        if (network != null) {

            // On rentre le reseau dans la bdd
            ContentValues values = new ContentValues();
            values.put(RESEAU_ID, network.getIdBdd());
            values.put(RESEAU_NOM, network.getNom());
            values.put(RESEAU_IMAGE, network.getImage());
            values.put(RESEAU_BUS, network.getBus());
            values.put(RESEAU_VELO, network.getVelo());
            values.put(RESEAU_VOITURE, network.getVoiture());
            values.put(RESEAU_BUSDAYBYDAY, network.getDaybyday());
            values.put(RESEAU_LATITUDE, network.getPosition().latitude);
            values.put(RESEAU_LONGITUDE, network.getPosition().longitude);
            ret = db.insert(RESEAU, null, values);

            // On recupere la liste des lignes et des arrets pour les rentrer dans la bdd
            arrets = network.getArrets();
            if(arrets != null) {
                Iterator<Stop> itArret = arrets.values().iterator();
                int i = 0;
                int size = arrets.values().size();
                while (itArret.hasNext()) {
                    insertArret(itArret.next());
                    callback.insertionArretPerformed(i, size);
                    i++;
                }
            }

            lignes = network.getLignes();
            if(lignes != null) {
                Iterator<Line> itLigne = lignes.values().iterator();
                int iLine = 0;
                int sizeLine = lignes.values().size();
                while (itLigne.hasNext()) {
                    insertLigne(itLigne.next());
                    callback.insertionLignePerformed(iLine, sizeLine);
                    iLine++;
                }
            }

            // On recupere la liste des stations pour les rentrer dans la bdd
            stations = network.getStations();
            if(stations != null) {
                Iterator<Station> itStation = stations.values().iterator();
                int iStation = 0;
                int sizeStation = stations.values().size();
                while (itStation.hasNext()) {
                    insertStation(itStation.next());
                    callback.insertionStationPerformed(iStation, sizeStation);
                    iStation++;
                }
            }

            // On recupere la liste des bornes pour les rentrer dans la bdd
            bornes = network.getBornes();
            if(bornes != null) {
                Iterator<Borne> itBorne = bornes.values().iterator();
                int iBorne = 0;
                int sizeBorne = bornes.values().size();
                while (itBorne.hasNext()) {
                    insertBorne(itBorne.next());
                    callback.insertionBornePerformed(iBorne, sizeBorne);
                    iBorne++;
                }
            }
        }

        Log.d(network.getNom() + ", VERSION BUS, VELO, VOITURE= " + network.getBus() + "," + network.getVelo() + "," + network.getVoiture(), "=====> JAMBO : Insertion reseau");
        return ret;
    }

    public ArrayList<Network> findReseaux() {
        ArrayList<Network> ret = new ArrayList<Network>();
        Network tmp = null;
        int tmpId, tmpBus, tmpVelo, tmpVoiture, tmpDayByDay;
        String tmpNom, tmpImage;
        double tmpLat, tmpLng;

        Cursor c = db.query(RESEAU, new String[]{RESEAU_ID, RESEAU_NOM, RESEAU_IMAGE, RESEAU_BUS, RESEAU_VELO, RESEAU_VOITURE, RESEAU_BUSDAYBYDAY, RESEAU_LATITUDE, RESEAU_LONGITUDE}, null, null, null, null, null);

        while (c.moveToNext()) {
            tmpId = c.getInt(0);
            tmpNom = c.getString(1);
            tmpImage = c.getString(2);
            tmpBus = c.getInt(3);
            tmpVelo = c.getInt(4);
            tmpVoiture = c.getInt(5);
            tmpDayByDay = c.getInt(6);
            tmpLat = c.getFloat(7);
            tmpLng = c.getFloat(8);

            tmp = new Network(tmpId, tmpLat, tmpLng, tmpNom, tmpImage, tmpBus, tmpVelo, tmpVoiture, tmpDayByDay);

            //On cherche les lignes et les arrets correspondants au reseau
            // On les ajoute que si nécessaire ( très grosse quantité de données )
            //tmp.setArrets(findArrets(tmpId));
            //tmp.setLignes(findLignes(tmpId));

            ret.add(tmp);

            Log.d(tmp.getNom() + ", VERSION BUS, VELO, VOITURE= " + tmp.getBus() + "," + tmp.getVelo() + "," + tmp.getVoiture(), "=====> JAMBO : SELECT ALL reseaux");
        }

        c.close();

        return ret;
    }

    public int removeReseau(int id) {

        this.removeLigne(id);
        this.removeArret(id);
        this.removeStation(id);
        this.removeBorne(id);

        Log.d("" + id, "=====> JAMBO : Suppression reseau");

        return db.delete(RESEAU, RESEAU_ID + " = " + id, null);
    }

    // ---------- BUS

    // ----- ligne

    public long insertLigne(Line line) {
        long ret = 0;
        ArrayList<Route> routes = null;

        if (line != null) {

            // On insere la ligne en elle-meme

            ContentValues values = new ContentValues();
            values.put(LIGNE_ID, line.getIdBdd());
            values.put(LIGNE_NUMERO, line.getNumero());
            values.put(LIGNE_DESCRIPTION, line.getDescription());
            values.put(LIGNE_COULEUR, line.getColor());
            values.put(LIGNE_RESEAU, line.getReseau());
            values.put(LIGNE_ETAT, line.getState());
            ret = db.insert(LIGNE, null, values);

            // On insere les routes de la lignes
            routes = line.getRoutes();
            Iterator<Route> itRoute = routes.iterator();
            while (itRoute.hasNext()) {
                insertRoute(itRoute.next());
            }

            Log.d(line.getDescription(), "=====> JAMBO : Insertion ligne");

        }
        return ret;
    }

    public ArrayList<Line> findLignes(int reseau) {
        ArrayList<Line> ret = new ArrayList<Line>();
        Line tmp = null;
        int tmpId, tmpFavoris, tmpEtat, tmpDownload;
        String tmpNum, tmpDesc, tmpCouleur;

        if (reseau != 0) {
            Cursor c = db.query(LIGNE, new String[]{LIGNE_ID, LIGNE_NUMERO, LIGNE_DESCRIPTION, LIGNE_COULEUR, LIGNE_FAVORIS, LIGNE_ETAT, LIGNE_DOWNLOAD}, LIGNE_RESEAU + " LIKE '" + reseau + "'", null, null, null, null);

            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                tmpNum = c.getString(1);
                tmpDesc = c.getString(2);
                tmpCouleur = c.getString(3);
                tmpFavoris = c.getInt(4);
                tmpEtat = c.getInt(5);
                tmpDownload = c.getInt(6);
                tmp = new Line(tmpId, tmpNum, tmpDesc, tmpCouleur, reseau, tmpFavoris, tmpEtat, tmpDownload);
                ret.add(tmp);
            }
            c.close();
        }

        Log.d("", "=====> JAMBO : SELECT ALL ligne");

        return ret;
    }

    public void setLigneFavoris(int idLigne) {
        if (idLigne != 0) {
            ContentValues values = new ContentValues();
            values.put(LIGNE_FAVORIS, 1);
            db.update(LIGNE, values, LIGNE_ID + " LIKE " + idLigne, null);
        }
    }

    public void setLigneNotFavoris(int idLigne) {
        if (idLigne != 0) {
            ContentValues values = new ContentValues();
            values.put(LIGNE_FAVORIS, 0);
            db.update(LIGNE, values, LIGNE_ID + " LIKE " + idLigne, null);
        }
    }

    public void setLigneDownload(int idLigne) {
        if (idLigne != 0) {
            ContentValues values = new ContentValues();
            values.put(LIGNE_DOWNLOAD, 1);
            db.update(LIGNE, values, LIGNE_ID + " LIKE " + idLigne, null);
        }
    }

    public void setLigneNotDownload(int idLigne) {

        int tmpRoute = 0;

        if (idLigne != 0) {
            ContentValues values = new ContentValues();
            values.put(LIGNE_DOWNLOAD, 0);
            db.update(LIGNE, values, LIGNE_ID + " LIKE " + idLigne, null);


            // On supprime les horaires d'une ligne
            Cursor c = db.query(LIGNE, new String[]{ROUTE_ID}, ROUTE_LIGNE + " LIKE '" + idLigne + "'", null, null, null, null);
            while (c.moveToNext()) {
                tmpRoute = c.getInt(0);
                this.removeAssociation(tmpRoute);
            }
            c.close();
        }
    }

    public int removeLigne(int idReseau) {

        int tmpId;

        // Pour chaque ligne du reseau, on supprime les routes
        if (idReseau != 0) {
            Cursor c = db.query(LIGNE, new String[]{LIGNE_ID}, LIGNE_RESEAU + " LIKE '" + idReseau + "'", null, null, null, null);
            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                this.removeRoute(tmpId);
            }
            c.close();
        }

        // On supprime ensuite chaque ligne
        return db.delete(LIGNE, LIGNE_RESEAU + " = " + idReseau, null);
    }


    // ----- route

    public long insertRoute(Route route) {
        long ret = 0;
        if (route != null) {

            // On insere la route en elle-meme

            ContentValues values = new ContentValues();
            values.put(ROUTE_ID, route.getIdBdd());
            values.put(ROUTE_LIGNE, route.getLigne());
            values.put(ROUTE_NOM, route.getNom());
            ret = db.insert(ROUTE, null, values);

            // On insere les associations route-arret
            insertAssociation(route);

            Log.d(route.getNom(), "=====> JAMBO : Insertion route");

        }
        return ret;
    }


    public ArrayList<Route> findRoutes(int line) {
        ArrayList<Route> ret = new ArrayList<Route>();
        Route tmp = null;
        int tmpId, tmpLigne;
        String tmpNom;

        if (line != 0) {
            Cursor c = db.query(ROUTE, new String[]{ROUTE_ID, ROUTE_LIGNE, ROUTE_NOM,}, ROUTE_LIGNE + " LIKE '" + line + "'", null, null, null, null);

            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                tmpLigne = c.getInt(1);
                tmpNom = c.getString(2);

                tmp = new Route(tmpId, tmpLigne, tmpNom);
                tmp.setStop(findAssociateArrets(tmp, "ASC"));
                ret.add(tmp);
            }
            c.close();
        }

        Log.d("", "=====> JAMBO : SELECT ALL route");

        return ret;
    }

    public Route findRoute(int idBdd){
        Route tmp = null;
        int tmpId, tmpLigne;
        String tmpNom;
        Cursor c = db.query(ROUTE, new String[]{ROUTE_ID, ROUTE_LIGNE, ROUTE_NOM,}, ROUTE_ID + " LIKE '" + idBdd + "'", null, null, null, null);
        c.moveToFirst();
        tmpId = c.getInt(0);
        tmpLigne = c.getInt(1);
        tmpNom = c.getString(2);

        return new Route(tmpId, tmpLigne, tmpNom);
    }

    public int removeRoute(int idLigne) {

        int tmpId;

        // Pour chaque route de la ligne , on supprime les associations
        if (idLigne != 0) {
            Cursor c = db.query(ROUTE, new String[]{ROUTE_ID}, ROUTE_LIGNE + " LIKE '" + idLigne + "'", null, null, null, null);
            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                this.removeAssociation(tmpId);
            }
            c.close();
        }

        // On supprime ensuite chaque route de la ligne
        return db.delete(ROUTE, ROUTE_LIGNE + " = " + idLigne, null);
    }

    // ----- arret

    public long insertArret(Stop stop) {
        long ret = 0;
        if (stop != null) {
            ContentValues values = new ContentValues();
            values.put(ARRET_ID, stop.getIdBdd());
            values.put(ARRET_NOM, stop.getNom());
            values.put(ARRET_LATITUDE, stop.getLatLng().latitude);
            values.put(ARRET_LONGITUDE, stop.getLatLng().longitude);
            values.put(ARRET_RESEAU, stop.getReseau());
            ret = db.insert(ARRET, null, values);
        }

        Log.d(stop.getNom() + ", etat = " + ret + ", id = " + stop.getIdBdd(), "=====> JAMBO : Insertion arret");
        return ret;
    }

    public HashMap<Integer, Stop> findArrets(int reseau) {
        HashMap<Integer, Stop> ret = new HashMap<Integer, Stop>();
        Stop tmp = null;
        int tmpId, tmpFavoris;
        String tmpNom;
        LatLng tmpLatLng;

        if (reseau != 0) {
            Cursor c = db.query(ARRET, new String[]{ARRET_ID, ARRET_NOM, ARRET_LATITUDE, ARRET_LONGITUDE, ARRET_FAVORIS}, ARRET_RESEAU + " LIKE '" + reseau + "'", null, null, null, null);

            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                tmpNom = c.getString(1);
                tmpLatLng = new LatLng(c.getFloat(2), c.getFloat(3));
                tmpFavoris = c.getInt(4);
                tmp = new Stop(tmpId, tmpNom, tmpLatLng, reseau, tmpFavoris);
                ret.put(tmpId, tmp);
            }
            c.close();
        }

        return ret;
    }


    public void setArretFavoris(int idArret) {
        if (idArret != 0) {
            ContentValues values = new ContentValues();
            values.put(ARRET_FAVORIS, 1);
            db.update(ARRET, values, ARRET_ID + " LIKE " + idArret, null);
        }
    }

    public void setArretNotFavoris(int idArret) {
        if (idArret != 0) {
            ContentValues values = new ContentValues();
            values.put(ARRET_FAVORIS, 0);
            db.update(ARRET, values, ARRET_ID + " LIKE " + idArret, null);
        }
    }

    public int removeArret(int idReseau) {
        return db.delete(ARRET, ARRET_RESEAU + " = " + idReseau, null);
    }


    // ----- APPARTIENT

    public long insertAssociation(Route route) {
        long ret = 0;

        if (route != null) {
            ArrayList<Stop> stops = new ArrayList<Stop>(route.getStop());
            Stop tmpStop = null;
            int i = 0;
            int size = stops.size();
            Iterator<Stop> it = stops.iterator();
            while(it.hasNext()){
                tmpStop = it.next();
                ContentValues values = new ContentValues();
                values.put(APPARTIENT_ID, tmpStop.getIdAppartient());
                values.put(APPARTIENT_ROUTE, route.getIdBdd());
                values.put(APPARTIENT_ARRET, tmpStop.getIdBdd());
                values.put(APPARTIENT_POSITION, tmpStop.getPosition());
                ret = db.insert(APPARTIENT, null, values);
                callback.associationPerformed(i, size);
                Log.d("route" + route.getNom() + ", arret " + tmpStop.getNom(), "=====> JAMBO : Nouvelle association");
                i++;
            }
        }
        return ret;
    }

    public ArrayList<Stop> findAssociateArrets(Route route, String order) {
        ArrayList<Stop> ret = new ArrayList<Stop>();
        Stop tmp = null;
        int tmpId, tmpReseau, tmpFavoris, tmpIdAppartient, tmpPosition, tmpDownload;
        String tmpNom;
        LatLng tmpLatLng;

        Log.d(route.getNom(), "=====> JAMBO : Recuperation des associations");

        if (route != null) {

            // On recupere les id des arrets associes a la route
            Cursor c1 = db.query(APPARTIENT, new String[]{APPARTIENT_ID, APPARTIENT_ROUTE, APPARTIENT_ARRET, APPARTIENT_POSITION, APPARTIENT_DOWNLOAD}, APPARTIENT_ROUTE + " LIKE '" + route.getIdBdd() + "'", null, null, null, APPARTIENT_POSITION + " "+order);
            while (c1.moveToNext()) {

                Log.d("" + c1.getInt(2), "=====> JAMBO : Nouvelle association trouvee pour la route " + route.getNom());

                // On recupere un a un chaque arret
                Cursor c2 = db.query(ARRET, new String[]{ARRET_ID, ARRET_NOM, ARRET_LATITUDE, ARRET_LONGITUDE, ARRET_RESEAU, ARRET_FAVORIS}, ARRET_ID + " LIKE '" + c1.getInt(2) + "'", null, null, null, null);

                while (c2.moveToNext()) {
                    tmpId = c2.getInt(0);
                    tmpNom = c2.getString(1);
                    tmpLatLng = new LatLng(c2.getFloat(2), c2.getFloat(3));
                    tmpReseau = c2.getInt(4);
                    tmpFavoris = c2.getInt(5);
                    tmpIdAppartient = c1.getInt(0);
                    tmpPosition = c1.getInt(3);
                    Log.d("position="+tmpPosition, "position="+tmpPosition);
                    tmpDownload = c1.getInt(4);
                    tmp = new Stop(tmpId, tmpNom, tmpLatLng, tmpReseau, tmpFavoris, tmpIdAppartient, tmpPosition, tmpDownload);
                    ret.add(tmp);

                    Log.d(tmpNom, "=====> JAMBO : Nouvelle association faite pour la route " + route.getNom());
                }
                c2.close();
            }
            c1.close();
        }

        return ret;
    }

    public int removeAssociation(int idRoute) {

        int tmpId;

        // Pour chaque association de la route , on supprime les horaires
        if (idRoute != 0) {
            Cursor c = db.query(APPARTIENT, new String[]{APPARTIENT_ID}, APPARTIENT_ROUTE + " LIKE '" + idRoute + "'", null, null, null, null);
            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                this.removeHoraireByAppartient(tmpId);
            }
            c.close();
        }

        return db.delete(APPARTIENT, APPARTIENT_ROUTE + " = " + idRoute, null);
    }

    public void setAssociationDownload(int idAppartient) {
        if (idAppartient != 0) {
            ContentValues values = new ContentValues();
            values.put(APPARTIENT_DOWNLOAD, 1);
            db.update(APPARTIENT, values, APPARTIENT_ID + " LIKE " + idAppartient, null);
        }
    }

    public void setAssociationNotDownload(int idAppartient) {
        if (idAppartient != 0) {
            ContentValues values = new ContentValues();
            values.put(APPARTIENT_DOWNLOAD, 0);
            db.update(APPARTIENT, values, APPARTIENT_ID + " LIKE " + idAppartient, null);

            this.removeHoraireByAppartient(idAppartient);
        }
    }

    // ----- HORAIRE

    public long insertHoraires(ArrayList<Schedule> horaires) {
        long ret = 0;
        Schedule tmpHoraire = null;
        if (!(horaires.isEmpty())) {
            Iterator<Schedule> it = horaires.iterator();
            while (it.hasNext()) {
                tmpHoraire = it.next();
                this.removeHoraire(tmpHoraire.getId());
                ContentValues values = new ContentValues();
                values.put(HORAIRE_ID, tmpHoraire.getId());
                values.put(HORAIRE_HORAIRE, tmpHoraire.toString());
                values.put(HORAIRE_CALENDRIER, tmpHoraire.getCalendar());
                values.put(HORAIRE_APPARTIENT, tmpHoraire.getIdAppartient());
                ret = db.insert(HORAIRE, null, values);
            }
        }
        return ret;
    }


    public ArrayList<Schedule> findHoraire(int idAppartient, int calendrier) {
        ArrayList<Schedule> ret = new ArrayList<Schedule>();

        if (idAppartient != 0 && calendrier != 0) {
            Cursor c = db.query(HORAIRE, new String[]{HORAIRE_ID, HORAIRE_HORAIRE}, HORAIRE_APPARTIENT + " LIKE '" + idAppartient + "' AND " + HORAIRE_CALENDRIER + " LIKE '" + calendrier + "'", null, null, null, null);

            while (c.moveToNext()) {
                ret.add(new Schedule(c.getInt(0), c.getString(1), calendrier, idAppartient));
            }
            c.close();
        }
        return ret;
    }

    public int removeHoraireByAppartient(int idAppartient) {
        return db.delete(HORAIRE, HORAIRE_APPARTIENT + " = " + idAppartient, null);
    }

    public int removeHoraire(int idHoraire) {
        return db.delete(HORAIRE, HORAIRE_ID + " = " + idHoraire, null);
    }

    // ---------- BIKE

    // ----- station

    public long insertStation(Station station) {
        long ret = 0;
        if (station != null) {
            ContentValues values = new ContentValues();
            values.put(STATION_ID, station.getIdBdd());
            values.put(STATION_NOM, station.getNom());
            values.put(STATION_ADRESSE, station.getAdresse());
            values.put(STATION_LATITUDE, station.getLatLng().latitude);
            values.put(STATION_LONGITUDE, station.getLatLng().longitude);
            values.put(STATION_RESEAU, station.getReseau());
            values.put(STATION_IDEXT, station.getIdExt());
            ret = db.insert(STATION, null, values);
        }

        Log.d(station.getNom() + ", etat = " + ret + ", id = " + station.getIdBdd(), "=====> JAMBO : Insertion station");
        return ret;
    }

    public ArrayList<Station> findStation(int reseau) {
        ArrayList<Station> ret = new ArrayList<Station>();
        Station tmp = null;
        int tmpId, tmpIdExt;
        String tmpNom, tmpAdresse;
        LatLng tmpLatLng;

        if (reseau != 0) {
            Cursor c = db.query(STATION, new String[]{STATION_ID, STATION_NOM, STATION_ADRESSE, STATION_LATITUDE, STATION_LONGITUDE, STATION_RESEAU, STATION_IDEXT}, STATION_RESEAU + " LIKE '" + reseau + "'", null, null, null, null);

            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                tmpNom = c.getString(1);
                tmpAdresse = c.getString(2);
                tmpLatLng = new LatLng(c.getFloat(3), c.getFloat(4));
                tmpIdExt = c.getInt(6);
                tmp = new Station(tmpId, tmpNom, tmpAdresse, tmpLatLng, reseau, tmpIdExt);
                ret.add(tmp);
                Log.d(tmp.getNom() + ", etat = " + ret + ", id = " + tmp.getIdBdd(), "=====> JAMBO : find station");
            }
            c.close();
        }

        return ret;
    }

    public int removeStation(int idReseau) {
        return db.delete(STATION, STATION_RESEAU + " = " + idReseau, null);
    }

    // ---------- CAR

    // ----- borne
    public long insertBorne(Borne borne) {
        long ret = 0;
        if (borne != null) {
            ContentValues values = new ContentValues();
            values.put(BORNE_ID, borne.getIdBdd());
            values.put(BORNE_NOM, borne.getNom());
            values.put(BORNE_ADRESSE, borne.getAdresse());
            values.put(BORNE_LATITUDE, borne.getLatLng().latitude);
            values.put(BORNE_LONGITUDE, borne.getLatLng().longitude);
            values.put(BORNE_NOMPORTEUR, borne.getNomPorteur());
            values.put(BORNE_TYPECHARGE, borne.getTypeChargeur());
            values.put(BORNE_NBREPDC, borne.getNbrePdc());
            values.put(BORNE_TYPECONNECTEUR, borne.getTypeConnecteur());
            values.put(BORNE_OBSERVATIONS, borne.getObservations());
            values.put(BORNE_RESEAU, borne.getReseau());
            ret = db.insert(BORNE, null, values);
        }

        Log.d(borne.getNom() + ", etat = " + ret + ", id = " + borne.getIdBdd(), "=====> JAMBO : Insertion borne");
        return ret;
    }

    public HashMap<Integer, Borne> findBorne(int reseau) {
        HashMap<Integer, Borne> ret = new HashMap<Integer, Borne>();
        Borne tmp = null;
        int tmpId, tmpIdExt, tmpNbrePdc;
        String tmpNom, tmpAdresse, tmpNomPorteur, tmpTypeChargeur, tmpTypeConnecteur, tmpObservations;
        LatLng tmpLatLng;

        if (reseau != 0) {
            Cursor c = db.query(BORNE, new String[]{BORNE_ID, BORNE_NOM, BORNE_ADRESSE, BORNE_LATITUDE, BORNE_LONGITUDE, BORNE_NOMPORTEUR, BORNE_TYPECHARGE, BORNE_NBREPDC, BORNE_TYPECONNECTEUR, BORNE_OBSERVATIONS, BORNE_RESEAU}, BORNE_RESEAU + " LIKE '" + reseau + "'", null, null, null, null);

            while (c.moveToNext()) {
                tmpId = c.getInt(0);
                tmpNom = c.getString(1);
                tmpAdresse = c.getString(2);
                tmpLatLng = new LatLng(c.getFloat(3), c.getFloat(4));
                tmpNomPorteur = c.getString(5);
                tmpTypeChargeur = c.getString(6);
                tmpNbrePdc = c.getInt(7);
                tmpTypeConnecteur = c.getString(8);
                tmpObservations = c.getString(9);
                tmp = new Borne(tmpId, tmpNom, tmpAdresse, tmpLatLng, tmpNomPorteur, tmpTypeChargeur, tmpNbrePdc, tmpTypeConnecteur, tmpObservations, reseau);
                ret.put(tmpId, tmp);
                Log.d(tmp.getNom() + ", etat = " + ret + ", id = " + tmp.getIdBdd(), "=====> JAMBO : find borne");
            }
            c.close();
        }

        return ret;
    }

    public int removeBorne(int idReseau) {
        return db.delete(BORNE, BORNE_RESEAU + " = " + idReseau, null);
    }
}
