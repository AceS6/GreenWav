package model.db.internal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Jambo is an internal database implementation of the gorilla database.
 *
 * Created by Alexis on 17/02/2015.
 */
public class Jambo extends SQLiteOpenHelper {


    // ---------- DATABASE

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
    public static final String LIGNE_NOM = "nom";
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


    // ---------- CREATE TABLE
    private static final String RESEAU_CREATE = "CREATE TABLE " + RESEAU + "("
            + RESEAU_ID + " INTEGER PRIMARY KEY, "
            + RESEAU_NOM + " TEXT NOT NULL DEFAULT 'unknown',"
            + RESEAU_IMAGE + " TEXT DEFAULT NULL,"
            + RESEAU_BUS + " INTEGER NOT NULL,"
            + RESEAU_VELO + " INTEGER NOT NULL,"
            + RESEAU_VOITURE + " INTEGER NOT NULL,"
            + RESEAU_BUSDAYBYDAY + " INTEGER NOT NULL,"
            + RESEAU_LATITUDE + " REAL NOT NULL,"
            + RESEAU_LONGITUDE + " REAL NOT NULL"
            + ");";

    private static final String LIGNE_CREATE = "CREATE TABLE " + LIGNE + "("
            + LIGNE_ID + " INTEGER PRIMARY KEY, "
            + LIGNE_NOM + " TEXT NOT NULL DEFAULT 'unknown',"
            + LIGNE_COULEUR + " INTEGER DEFAULT 0, "
            + LIGNE_RESEAU + " INTEGER NOT NULL CONSTRAINT fkligne_reseau REFERENCES " + RESEAU + " (id), "
            + LIGNE_FAVORIS + " INTEGER DEFAULT 0,"
            + LIGNE_ETAT + " INTEGER NOT NULL,"
            + LIGNE_DOWNLOAD + " INTEGER DEFAULT 0"
            + ");";


    private static final String ROUTE_CREATE = "CREATE TABLE " + ROUTE + "("
            + ROUTE_ID + " INTEGER PRIMARY KEY, "
            + ROUTE_LIGNE + " INTEGER NOT NULL CONSTRAINT fkroute_ligne REFERENCES " + LIGNE + " (id), "
            + ROUTE_NOM + " TEXT NOT NULL DEFAULT 'unknown'"
            + ");";


    private static final String ARRET_CREATE = "CREATE TABLE " + ARRET + "("
            + ARRET_ID + " INTEGER PRIMARY KEY, "
            + ARRET_NOM + " TEXT NOT NULL DEFAULT 'unknown',"
            + ARRET_LATITUDE + " REAL NOT NULL,"
            + ARRET_LONGITUDE + " REAL NOT NULL,"
            + ARRET_RESEAU + " INTEGER NOT NULL CONSTRAINT fkarret_reseau REFERENCES " + RESEAU + " (id), "
            + ARRET_FAVORIS + " INTEGER DEFAULT 0"
            + ");";


    private static final String APPARTIENT_CREATE = "CREATE TABLE " + APPARTIENT + "("
            + APPARTIENT_ID + " INTEGER PRIMARY KEY, "
            + APPARTIENT_ROUTE + " INTEGER NOT NULL CONSTRAINT fkappartient_route REFERENCES " + ROUTE + " (id),"
            + APPARTIENT_ARRET + " INTEGER NOT NULL CONSTRAINT fkappartient_arret REFERENCES " + ARRET + " (id),"
            + APPARTIENT_POSITION + " INTEGER DEFAULT 0,"
            + APPARTIENT_DOWNLOAD + " INTEGER DEFAULT 0"
            + ");";

    private static final String HORAIRE_CREATE = "CREATE TABLE " + HORAIRE + "("
            + HORAIRE_ID + " INTEGER PRIMARY KEY, "
            + HORAIRE_HORAIRE + " TEXT NOT NULL,"
            + HORAIRE_CALENDRIER + " TINYINT NOT NULL,"
            + HORAIRE_APPARTIENT + " INTEGER NOT NULL CONSTRAINT fkhoraire_appartient REFERENCES " + APPARTIENT + " (id)"
            + ");";

    private static final String STATION_CREATE = "CREATE TABLE " + STATION + "("
            + STATION_ID + " INTEGER PRIMARY KEY, "
            + STATION_NOM + " TEXT NOT NULL DEFAULT 'unknown',"
            + STATION_ADRESSE + " TEXT NOT NULL DEFAULT '',"
            + STATION_LATITUDE + " REAL NOT NULL,"
            + STATION_LONGITUDE + " REAL NOT NULL,"
            + STATION_RESEAU + " INTEGER NOT NULL CONSTRAINT fkstation_reseau REFERENCES " + RESEAU + " (id), "
            + STATION_IDEXT + " INTEGER DEFAULT 0"
            + ");";

    private static final String BORNE_CREATE = "CREATE TABLE " + BORNE + "("
            + BORNE_ID + " INTEGER PRIMARY KEY, "
            + BORNE_NOM + " TEXT NOT NULL DEFAULT 'no name',"
            + BORNE_ADRESSE + " TEXT NOT NULL DEFAULT 'no adress',"
            + BORNE_LATITUDE + " REAL NOT NULL,"
            + BORNE_LONGITUDE + " REAL NOT NULL,"
            + BORNE_NOMPORTEUR + " TEXT NOT NULL DEFAULT 'no name',"
            + BORNE_TYPECHARGE + " TEXT NOT NULL DEFAULT 'unknown',"
            + BORNE_NBREPDC + " INTEGER NOT NULL DEFAULT 0,"
            + BORNE_TYPECONNECTEUR + " TEXT NOT NULL DEFAULT 'unknown',"
            + BORNE_OBSERVATIONS + " TEXT NOT NULL DEFAULT 'no observations',"
            + BORNE_RESEAU + " INTEGER NOT NULL CONSTRAINT fkstation_reseau REFERENCES " + RESEAU + " (id) "
            + ");";


    // ---------- CONSTRUCTOR

    public Jambo(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    // ---------- METHODS

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(RESEAU_CREATE);
        database.execSQL(LIGNE_CREATE);
        database.execSQL(ROUTE_CREATE);
        database.execSQL(ARRET_CREATE);
        database.execSQL(APPARTIENT_CREATE);
        database.execSQL(HORAIRE_CREATE);
        database.execSQL(STATION_CREATE);
        database.execSQL(BORNE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(JamboDAO.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + RESEAU);
        db.execSQL("DROP TABLE IF EXISTS " + LIGNE);
        db.execSQL("DROP TABLE IF EXISTS " + ROUTE);
        db.execSQL("DROP TABLE IF EXISTS " + ARRET);
        db.execSQL("DROP TABLE IF EXISTS " + APPARTIENT);
        db.execSQL("DROP TABLE IF EXISTS " + HORAIRE);
        db.execSQL("DROP TABLE IF EXISTS " + STATION);
        db.execSQL("DROP TABLE IF EXISTS " + BORNE);
        onCreate(db);
    }

}

