package Klassen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse fuer das Spielfeld. Hier ist die Logik des Spieles untergebracht
 * @author steven, tommy
 * @version 0.1
 */
public class Spielfeld {

    // interne Speicherobjekte
    /**
     * Jeder Zug bekommt eine eindeutige fortlaufende Nummer. Das Attribut
     * letzteZugnummer zeigt immer auf die zuletzt verwendete Zugnummer. Diese
     * Nummer kann auch direkt als Array-Zeiger auf den Zug (gespeichert in
     * spielZugCollection) zum zugehoerigen Zug verwendet werden.
     */
    private int letzteZugnummer;
    /**
     * Groesse des Spielfeldes und damit auch Groesse der zweidimensionalen
     * Spielfeldarrays
     */
    private int spielfeldGroesse;
    private List<Spielzug> spielZugCollection;


    private int xPosVerboten;
    private int yPosVerboten;
    private boolean ignoreTime;
    private Spieler spielerSchwarz;
    private Spieler spielerWeiss;

    /* Wenn die Spielerzeit aufgebraucht ist gibt es noch die Periodenzeit.
     * Diese ist fuer alle Spieler gleich. In wirklich heisst dieser Wert Byo-yomi.
     */
    private long periodenZeit;
    // Vorgabezahl
    private int vorgabeZahl;
    // Zustand des Spiels
    private int spielZustand;
    // Chache Funktionen zum schnelleren Spielen
    private int[][] aktuellesSpielfeldCache;
    private int spielfeldCacheMitZugnummerStand;

    /* Damit man sehr einfach mit einer Startformation beginnen kann, muss es
     * ein Initialfeld geben. Wenn mit Vorgabe gespielt wird, werden auf das
     * Initialfeld die Vorgabesteine gesetzt. Bei einer bestimmten Anfangsposition
     * wird diese auf dem Initialfeld Platziert. Somit ist es sehr einfach
     * beim neu Laden des Feldes einfach mit dem Initialfeld zu beginnen und
     * dann der Reihe nach die Zuege auf das Brett zu setzen.
     */
    private int[][] initialfeld;

    /**
     * Wird keine Spielfeldgroesse angegeben, wird eine Standardfeldgroesse
     * eingestellt
     */
    public Spielfeld() {
        this(13);
    }

    /**
     * Das Spielfeld wird initialisiert. Dazu werden alle Variablen initialisiert
     * @param spielfeldGroesse Groesse des Spielfeldes
     */
    public Spielfeld(int spielfeldGroesse) {
        this.vorgabeZahl = 0;
        this.spielfeldGroesse = spielfeldGroesse;
        this.letzteZugnummer = 0;
        this.spielZugCollection = new ArrayList<Spielzug>();
        /* Noch kein Feld verboten */
        this.setXPosVerboten(-1);
        this.setYPosVerboten(-1);

        // Chache Funktionen setzen
        this.aktuellesSpielfeldCache = new int[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];
        this.spielfeldCacheMitZugnummerStand = this.letzteZugnummer;
        /* Feld muss als Leeres Feld initialisiert werden */
        int i, j;
        for (i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (j = 0; j < this.getSpielfeldGroesse(); j++) {
                aktuellesSpielfeldCache[i][j] = Konstante.SCHNITTPUNKT_LEER;
            }
        }

        /* Zunaechst Leer machen */
        this.initialfeld = new int[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];
        for (i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (j = 0; j < this.getSpielfeldGroesse(); j++) {
                this.initialfeld[i][j] = Konstante.SCHNITTPUNKT_LEER;
            }
        }
        this.setIgnoreTime(false);
    }

    /**
     *
     * @return Den schwarzen Spieler mit dem Objekttyp Spieler
     */
    public Spieler getSpielerSchwarz() {
        return this.spielerSchwarz;
    }

    /**
     * @param schwarzerSpieler Setzen des schwarzerSpieler
     */
    public void setSpielerSchwarz(Spieler schwarzerSpieler) {
        this.spielerSchwarz = schwarzerSpieler;
    }

    /**
     * @return Den Weissen Spieler mit dem Objekttyp Spieler
     */
    public Spieler getSpielerWeiss() {
        return this.spielerWeiss;
    }

    /**
     *
     * @param weisserSpieler Setzen des WeisserSpieler
     */
    public void setSpielerWeiss(Spieler weisserSpieler) {
        this.spielerWeiss = weisserSpieler;
    }

    /**
     *
     * @return Gibt den aktuellen Wert der Periodenzeit zurueck.
     * Wenn die Spielerzeit aufgebraucht ist gibt es noch die Periodenzeit.
     * Diese ist fuer alle Spieler gleich. In wirklich heisst dieser Wert Byo-yomi.
     */
    public long getPeriodenZeit() {
        return this.periodenZeit;
    }

    /**
     * @param periodenZeit Speichern der periodenZeit
     */
    public void setPeriodenZeit(long periodenZeit) {
        this.periodenZeit = periodenZeit;
    }

    /**
     *
     * @return Gibt den aktuellen Spielzustand zurueck. Erlaubt sind nur die Konstanten:
     *     - SPIEL_UNVOLLSTAENDIG
    - SPIEL_VALIDIERT
    - SPIEL_LAUEFT
    - SPIEL_PAUSIERT
    - SPIEL_AUFGEGEBEN
    - SPIEL_BEENDET_DURCH_APP
     */
    public int getSpielZustand() {
        return this.spielZustand;
    }

    /**
     * Setzt den Zustand des Spielfeldes
     * @param neuerSpielZustand Setze den Spielzustand
     */
    public void setSpielZustand(int neuerSpielZustand) {

        // Überprüfen ob der Zustand erlaubt ist
        switch (neuerSpielZustand) {
            case Konstante.SPIEL_GEBIETSAUSWERTUNG:
                this.spielZustand = neuerSpielZustand;
                break;
            case Konstante.SPIEL_LAUEFT:
                this.spielZustand = neuerSpielZustand;
                break;
            case Konstante.SPIEL_PAUSIERT:
                this.spielZustand = neuerSpielZustand;
                break;
            case Konstante.SPIEL_BEENDET:
                this.spielZustand = neuerSpielZustand;
                break;
            default:
                throw new UnsupportedOperationException("Fehlerhafter Wert in setSpielZustand.");

        }

    }

    /**
     * 
     * @return Die Nummer des zuletzt gespielten Zuges
     */
    public int getLetzteZugnummer() {
        return this.letzteZugnummer;
    }

    /**
     *
     * @return Größe des aktuellen Spielfeldes
     */
    public int getSpielfeldGroesse() {
        return this.spielfeldGroesse;
    }

    /**
     *
     * @return Gibt das aktuelle Spielfeld als Array zurück
     */
    public int[][] getAktuellesSpielFeld() {

        int spielfeld[][] = new int[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];

        if (this.spielfeldCacheMitZugnummerStand != this.letzteZugnummer) {
            spielfeld = new int[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];

            // Spielfeld initialisieren
            for (int i = this.getSpielfeldGroesse() - 1; i >= 0; i--) {
                for (int j = this.getSpielfeldGroesse() - 1; j >= 0; j--) {
                    spielfeld[j][i] = Konstante.SCHNITTPUNKT_LEER;
                }
            }
            /* Jetzt ist das Brett noch leer. Es muss also mit den richtigen
            Werten gefuellt werden - Fehlt noch*/

            this.aktuellesSpielfeldCache = spielfeld;
            this.spielfeldCacheMitZugnummerStand = this.letzteZugnummer;

            /*
             * Hier muss jetzt noch nach dem initialisieren die Werte aus der Collection
             * abgefragt werden und ins Feld eingetragen werden
             *
             */

        } else {
            for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
                for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                    spielfeld[i][j] = this.aktuellesSpielfeldCache[i][j];
                }
            }
        }

        return spielfeld;
    }

    /**
     *
     * @param zeitpunkt Zeipunkt zu dem das Spielfeld zurückgegeben werden soll.
     * In erster Linie für REDO und UNDO gedacht, nicht zum häufigen aufrufen,
     * da das Spielfeld hierbei immer neu generiert werden muss
     * @return Spielfeld zum übergebenen Zeipunkt in Form eines Arrays
     */
    public int[][] getSpielfeldZumZeitpunkt(int zeitpunkt) {

        int spielfeld[][];
        spielfeld = new int[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];
        /* Wenn der Zeitpunkt dem des Caches entspricht, wird einfach der aktuelle
         * Feld zurueckgegeben.
         */
        if (zeitpunkt == this.spielfeldCacheMitZugnummerStand && zeitpunkt != 0) {
            for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
                for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                    spielfeld[i][j] = this.aktuellesSpielfeldCache[i][j];
                }
            }
            return spielfeld;
        }

        /* Ist der Zeitpunkt kleiner als 0 ist dies Verboten! */
        if (zeitpunkt < 0) {
            throw new UnsupportedOperationException("Zeitpunkt ist Nicht moeglich, da kleiner 0 !");
        }

        /* Wenn der zeitpunkt vor dem Stand im Cache liegt, muss das Feld komplett
         * neu geladen werden. Ansonsten reicht es, vom momentanen Cache auszugehen.
         * Wenn neu geladen wird, muss vom Initialfeld aus gegonnen werden.
         */
        if (zeitpunkt < this.spielfeldCacheMitZugnummerStand) {
            this.spielfeldCacheMitZugnummerStand = 0;
            this.loescheVerbotenenPunkt();
            this.getSpielerSchwarz().setGefangenenAnzahl(0);
            this.getSpielerWeiss().setGefangenenAnzahl(0);
            for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
                for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                    this.aktuellesSpielfeldCache[i][j] = this.initialfeld[i][j];
                }
            }
        }

        /* Wenn der Zeitpunkt gleich 0 ist, so muss das Initialfeld zurueck
         * gegeben werden. */
        if (zeitpunkt == 0) {
            for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
                for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                    spielfeld[i][j] = this.initialfeld[i][j];
                }
            }
            return spielfeld;
        }

        while (zeitpunkt > this.spielfeldCacheMitZugnummerStand) {
            /* Auf Passen abpruefen */
            if (this.spielZugCollection.get(this.spielfeldCacheMitZugnummerStand).getXPosition() == -1
                    && this.spielZugCollection.get(this.spielfeldCacheMitZugnummerStand).getYPosition() == -1) {
                this.loescheVerbotenenPunkt();
                this.spielfeldCacheMitZugnummerStand++;
            } else {
                this.setStein(this.spielZugCollection.get(this.spielfeldCacheMitZugnummerStand).getXPosition(),
                        this.spielZugCollection.get(this.spielfeldCacheMitZugnummerStand).getYPosition(),
                        this.spielZugCollection.get(this.spielfeldCacheMitZugnummerStand).getFarbe());
            }
        }
        for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                spielfeld[i][j] = this.aktuellesSpielfeldCache[i][j];
            }
        }
        return spielfeld;
    }

    /**
     * Diese Funktion loescht die Zuege aus der Liste der Spielzuege bis zum
     * angegebenen Zeitpunkt. Ist der Zeitpunkt nich erlaubt, wird false
     * zurueck gegeben. Ansonsten ist der Rueckgabewert true.
     * @param zeitpunkt Zeitpunkt auf den das Spielfeld FEST gesetzt werden soll.
     * Ein Redo ist dann nicht mehr möglich. Das Spiel wird zu diesem Zeitpunkt
     * weitergespielt.
     * @return True, wenn die Funktion erfolgreich ausgeführt wurde, sonst false
     */
    public boolean setSpielfeldZumZeitpunkt(int zeitpunkt) {
        if (zeitpunkt > this.spielZugCollection.size() || zeitpunkt < 0) {
            return false;
        }
        for (int i = this.spielZugCollection.size() - 1; i >= zeitpunkt; i--) {
            this.spielZugCollection.remove(i);
        }
        this.letzteZugnummer = zeitpunkt;
        /* Gefangenenzahlen anpassen, indem sie auf 0 gesetzt werden und das
         * feld neu aufgebaut wird
        this.spielerSchwarz.setGefangenenAnzahl(0);
        this.spielerWeiss.setGefangenenAnzahl(0);*/

        return true;
    }

    /**
     * Die Funktion setzt im Cache einen Stein, wenn dies auch moeglich ist.
     * Folgende Variablen werden dabei veraendert:
     * - aktuellesSpielfeldCache (Wenn Zug erfolgreich war)
     * - xPosVerboten
     * - yPosVerboten
     * - gefangenenAnzahl der Spieler
     * - spielfeldCacheMitZugnummerStand
     * @param xPos X-Position des Spielfelds.
     * Diese kann Werte zwischen 1 und der Feldlänge enthalten
     * @param yPos Y-Position des Spielfelds.
     * Diese kann Werte zwischen 1 und der Feldlänge enthalten
     * @param spielerfarbe Farbe des setzenden Spielers
     * @return Je nach Situarion signalisiert der Integer, was passiert ist:
     *  1: Zug wurde erfolgreich durchgefuehrt : (OK)
     *  0: Zug liegt nicht auf Spielfeld: (FEHLER)
     * -1: Zug war verboten : (FEHLER)
     * -2: Schnittpunkt schon belegt : (FEHLER)
     * -3: Selbstmord (verboten) : (FEHLER)
     */
    private int setStein(int xPos, int yPos, int spielerfarbe) {
        /* Koordinaten umrechnen, da Array bei 0 beginnt */
        int xKoord = xPos - 1;
        int yKoord = yPos - 1;

        /* zugOk dient dazu um zu ermitteln ob der Zug valide ist. Faengt der
         * Zug einen Stein so ist er gueltig, da der Stein danach mindestens
         * eine Freiheit besitzt. Wird eine Nachbarfreiheit gefunden ist der
         * Zug auch valide. Wenn der zug nicht als Ok markiert wird, muss dies
         * allerdings nicht heissen das der Zug ungueltig ist. Es bedeutet
         * lediglich, dass der Zug weiter untersucht werden muss.
         */
        boolean zugOK = false;
        /* Als erstes wird auf Fehler getestet. Dies beinahltet Folgendes:
         * - Zug ist nicht auf dem Spielfeld
         * - Zug ist verboten (wegen Ko)
         * - Stein steht schon an der Position
         */

        /* 1. Testen ob Zug auf dem Spielfeld ist, sonst Fehler */
        if (xKoord < 0 || yKoord < 0 || xKoord >= this.getSpielfeldGroesse() || yKoord >= this.getSpielfeldGroesse()) {
            return 0;
        }
        /* 2. Testen auf Ko (Also verbotener Zug)*/
        if (this.aktuellesSpielfeldCache[xKoord][yKoord] == Konstante.SCHNITTPUNKT_VERBOTEN) {
            return -1;
        }
        /* 3. Testen ob schon Stein da steht */
        if (this.aktuellesSpielfeldCache[xKoord][yKoord] == Konstante.SCHNITTPUNKT_SCHWARZ
                || this.aktuellesSpielfeldCache[xKoord][yKoord] == Konstante.SCHNITTPUNKT_WEISS) {
            return -2;
        }

        /* Der Schnittpunkt ist also Leer und es ist erlaubt darauf zu spielen.
         * Da nun die Situation auf dem Feld analysiert werden muss, wird extra
         * dafuer ein Analysefeld erstellt. Dies ist wie das richtige Feld ein
         * 2-Dimensionales Array, jedoch besteht es aus AnalyseSchnittpunkten.
         * Damit hat man die moeglichkeit durch die Eigenschaften der Klasse
         * AnalyseSchnittpunkt eine Breitensuche durchzufuehren.
         */

        AnalyseSchnittpunkt anaFeld[][];
        anaFeld = new AnalyseSchnittpunkt[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];

        /* Nun das Feld initialisieren. Dabei wird eine Kopie des aktuellen
         * Feldes erstellt. */
        for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                anaFeld[i][j] = new AnalyseSchnittpunkt(i, j, this.aktuellesSpielfeldCache[i][j]);
            }
        }

        /* Auf die Kopie (AnaFeld) wird jetzt der Stein gesetzt. Das wirkt sich
         * nicht auf das momentane Feld aus, da nur auf der Kopie gearbeitet
         * wird .*/
        anaFeld[xKoord][yKoord].setBelegungswert(spielerfarbe);

        /* Nun werden einige Variablen eingefuehrt die das Auswerten erleichtern.
         * --> gefangeneSteine: Anzahl der Gefangenen Steine. Ist die Funktion
         * erfolgreich wird die Anzahl der gefangenen Steine zu den bereits
         * Gefangenen addiert.
         * --> momGefSteine: Um bei jedem Auswertungsschritt die Anzahl der
         * gefangenen Steine zu ermitteln.
         * --> steinIstEinzeln: Fuer die Ko-Regel ist es wichtig, ob der Stein
         * eine Gruppe aus einem Stein darstellt. Also ob er einzeln ist.
         * Wird waerend des Auswertens festgestellt, das ein Nachbarstein die
         * gleiche Farbe hat, wird dieser Wert auf False gesetzt.
         */
        int gefangeneSteine = 0;
        int momGefSteine = 0;
        boolean steinIstEinzeln = true;

        /* Jetzt werden die Nachbarsteine betrachtet. Wenn der Stein allerdings
         * am Rand ist, besitzt er bezueglich dieses Randes keinen Nachbarn.
         * Daher wird erst getestet ob sich ein Nachbarschnittpunkt befindet.
         * Ist der Nachbarstein ungleich der Spielfarbe, wird Versucht ein Stein
         * vom Brett zu nehmen. Je nach Rueckgabewert der Funktion
         * versucheSteinZuNehmen wird dann weitergearbeitet. Wenn der Wert 0 ist,
         * wurden keine Steine gefangen, es ist nichts zu tun. Ist der Wert -1
         * so ist der Schnittpunkt leer.
         * Besitzt der Nachbarstein die gleiche Farbe, so ist der Stein nicht
         * einzeln.
         * Es ist zu bemerken, dass wenn ein Stein gefangen wurd, das aktuelle
         * Spielfeld veraendert wird. Das ist allerdings in Ordnung, da wenn
         * ein Stein durch diesen Zug gefangen wird, er automatisch mindestens
         * eine Freiheit haben muss. Daher ist der Zug gueltig. */

        /* 1. Linke Seite */
        if (xKoord != 0) {
            if (anaFeld[xKoord - 1][yKoord].getBelegungswert() != spielerfarbe) {
                momGefSteine = versucheSteinZuNehmen(xKoord - 1, yKoord, anaFeld);
                if (momGefSteine > 0) {
                    gefangeneSteine += momGefSteine;
                    zugOK = true;
                } else if (momGefSteine == 0) {/* nichts */ } else if (momGefSteine == -1) {
                    zugOK = true;
                }
            } else {
                steinIstEinzeln = false;
            }
        }
        /* 2. Rechte Seite */
        if (xKoord != this.getSpielfeldGroesse() - 1) {
            if (anaFeld[xKoord + 1][yKoord].getBelegungswert() != spielerfarbe) {
                momGefSteine = versucheSteinZuNehmen(xKoord + 1, yKoord, anaFeld);
                if (momGefSteine > 0) {
                    gefangeneSteine += momGefSteine;
                    zugOK = true;
                } else if (momGefSteine == 0) {/* nichts */

                } else if (momGefSteine == -1) {
                    zugOK = true;
                }
            } else {
                steinIstEinzeln = false;
            }
        }
        /* 3. Untere Seite */
        if (yKoord != 0) {
            if (anaFeld[xKoord][yKoord - 1].getBelegungswert() != spielerfarbe) {
                momGefSteine = versucheSteinZuNehmen(xKoord, yKoord - 1, anaFeld);
                if (momGefSteine > 0) {
                    gefangeneSteine += momGefSteine;
                    zugOK = true;
                } else if (momGefSteine == 0) {/* nichts */

                } else if (momGefSteine == -1) {
                    zugOK = true;
                }
            } else {
                steinIstEinzeln = false;
            }
        }
        /* 4. Obere Seite */
        if (yKoord != this.getSpielfeldGroesse() - 1) {
            if (anaFeld[xKoord][yKoord + 1].getBelegungswert() != spielerfarbe) {
                momGefSteine = versucheSteinZuNehmen(xKoord, yKoord + 1, anaFeld);
                if (momGefSteine > 0) {
                    gefangeneSteine += momGefSteine;
                    zugOK = true;
                } else if (momGefSteine == 0) {/* nichts */

                } else if (momGefSteine == -1) {
                    zugOK = true;
                }
            } else {
                steinIstEinzeln = false;
            }
        }

        /* Ist zugOK immer noch false, hat der Stein weder einen Anderen gefangen,
         * noch besitzt er selbst freiheiten. Daher muss betrachtet werden, ob
         * die Gruppe in der sich der Stein befindet eine Freiheit hat, wenn
         * dies nicht so ist, ist der Zug Selbstmord und somit ungueltig */
        if (zugOK == false) {
            if (versucheSteinZuNehmen(xKoord, yKoord, anaFeld, false) == 0) {
                /* Dann hat der Zug zwar keine Steine gefangen und der Stein
                 * hat keine Freiheiten, aber die Gruppe hat welche. Deshalb
                 * kann der Stein gesetzt werden */
                this.aktuellesSpielfeldCache[xKoord][yKoord] = spielerfarbe;
                this.spielfeldCacheMitZugnummerStand++;
                this.loescheVerbotenenPunkt();
                this.erhoeheGefangenenZahl(spielerfarbe, gefangeneSteine);
                return 1;
            } else {
                /* Die Gruppe zu der der Stein gehoert hat keine Freiheiten.
                 * Es ist also Selbstmord! Es ist zu bemerken, dass dabei die
                 * Brettstellung nicht veraendert wird, da ja nichts gefangen
                 * wurde (also vom richtigen Brett) */
                return -3;
            }
        }

        /* Wenn man bis hier kommt, wurde ein Stein gesetzt der entweder eine
         * Gruppe gefangen hat, oder mindestens eine Freiheit besitzt. Daher
         * kann der Stein einfach gesetzt werden.
         * Zu diesem Zeitpunkt ist der Chache auf jeden Fall veraendert worden
         * Die Zugnummer selbst wird mit steinEintragen eins nach oben gezaehlt.
         * Da zug OK ist, muss der letzte verbotene Punkt geloescht werden!
         */
        this.aktuellesSpielfeldCache[xKoord][yKoord] = spielerfarbe;
        this.spielfeldCacheMitZugnummerStand++;
        this.loescheVerbotenenPunkt();

        /* Jetzt ist noch Ko abzufangen
         * Wenn nichts gefangen wurde, ist es auch kein Ko.
         * Wenn der Stein nicht einzeln ist, ist es auch kein Ko.
         * Wenn mehr als ein Stein gefangen wurde ist es auch kein Ko.*/
        if (gefangeneSteine == 0 || gefangeneSteine > 1 || steinIstEinzeln == false) {
            //this.steinEintragen(xPos, yPos, spielerfarbe);
            this.erhoeheGefangenenZahl(spielerfarbe, gefangeneSteine);
            return 1;
        }

        /* Jetzt ist klar: Es wurde genau ein stein gefangen und der Stein der
         * gesetzt wurde bildet eine Gruppe aus genau einem Stein, naemlich
         * sich selbst. (Ist also einzeln)
         * Nun muss auf Ko geprueft werden. Wenn der Stein genau eine Freiheit
         * besitzt, muss dort das Ko eingezeichnet werden. Daher werden jetzt
         * die Freiheiten des Steins gezaehlt. Sind diese am Ende 1 so ist es Ko
         * Besitzt der Stein nur eine Freiheit, wird sich in freiXPos und freiYPos
         * der Wert dieser Freiheit gemerkt.
         */
        int freiheitDesSteins = 0;
        int freiXPos = -1;
        int freiYPos = -1;

        /* 1. Linke Seite (Wenn sie existiert)*/
        if (xKoord != 0) {
            if (this.aktuellesSpielfeldCache[xKoord - 1][yKoord] == Konstante.SCHNITTPUNKT_LEER) {
                freiheitDesSteins++;
                freiXPos = xKoord - 1;
                freiYPos = yKoord;
            }
        }
        /* 2. Rechte Seite (Wenn sie existerit)*/
        if (xKoord != this.getSpielfeldGroesse() - 1) {
            if (this.aktuellesSpielfeldCache[xKoord + 1][yKoord] == Konstante.SCHNITTPUNKT_LEER) {
                freiheitDesSteins++;
                freiXPos = xKoord + 1;
                freiYPos = yKoord;
            }
        }
        /* 3. Untere Seite (Wenn sie existiert)*/
        if (yKoord != 0) {
            if (this.aktuellesSpielfeldCache[xKoord][yKoord - 1] == Konstante.SCHNITTPUNKT_LEER) {
                freiheitDesSteins++;
                freiXPos = xKoord;
                freiYPos = yKoord - 1;
            }
        }
        /* 4. Obere Seite (Wenn sie existiert)*/
        if (yKoord != this.getSpielfeldGroesse() - 1) {
            if (this.aktuellesSpielfeldCache[xKoord][yKoord + 1] == Konstante.SCHNITTPUNKT_LEER) {
                freiheitDesSteins++;
                freiXPos = xKoord;
                freiYPos = yKoord + 1;
            }
        }

        /* Wenn die Freiheiten nicht genau 1 sind, ist es kein Ko*/
        if (freiheitDesSteins != 1) {
            //this.steinEintragen(xPos, yPos, spielerfarbe);
            this.erhoeheGefangenenZahl(spielerfarbe, gefangeneSteine);
            return 1;
        }

        /* Es ist also Ko. Das Muss markiert werden. Der Wert dafuer ist in
         * freiXPos und freiYPos gespeichert. */
        this.setzeVerbotenenPunkt(freiXPos, freiYPos);
        this.aktuellesSpielfeldCache[freiXPos][freiYPos] = Konstante.SCHNITTPUNKT_VERBOTEN;
        //this.steinEintragen(xPos, yPos, spielerfarbe);
        this.erhoeheGefangenenZahl(spielerfarbe, gefangeneSteine);
        return 1;
    }

    /**
     * In abhaengigkeit davon, wer den letzten Zug gemacht hat, wird ermittelt,
     * wer als naechstes dran ist. Wenn Beispielsweise Schwarz dran war, ist
     * jetzt Weiss an der Reihe.
     * Eine Ausnahme ist es, wenn mit einer Startformation gespielt wird, und
     * es sich um den ersten richtigen Zug handelt. Dann ist weiss dran
     * @return Gibt zurueck wer als naechstes dran ist.
     */
    public int getSpielerFarbeAnDerReihe() {

        int letzterSpieler = getSpielerFarbeVonLetztemZug();

        if (letzterSpieler == Konstante.SCHNITTPUNKT_SCHWARZ) {
            return Konstante.SCHNITTPUNKT_WEISS;
        } else if (letzterSpieler == Konstante.SCHNITTPUNKT_WEISS) {
            return Konstante.SCHNITTPUNKT_SCHWARZ;
        } else {
            // FEHLER
            return Konstante.FEHLER;
        }


    }

    /**
     * @return Gibt zurueck, welche Farbe der Spieler hat, der den letzten Zug
     * setzte.
     */
    public int getSpielerFarbeVonLetztemZug() {
        if (this.spielfeldCacheMitZugnummerStand == 0) {
            //if (this.spielZugCollection.size() == 0){
            /* Wenn ohne Vorgabe gespielt wird, ist Schwarz zu beginn dran.
             * Daher war Weiss theoretisch vorher dran.
             * Wird mit Vorgabe, oder Startformation gespielt, ist Weiss
             * zu beginn an der Reihe. Daher war Schwarz theoretisch
             * vorher dran.
             * Ob mit Startformation gespielt wurde, erkennt man daran, dass
             * das initialfeld nicht leer ist*/
            boolean initFeldLeer = true;
            for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
                for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                    if (this.initialfeld[i][j] != Konstante.SCHNITTPUNKT_LEER) {
                        initFeldLeer = false;
                    }
                }
            }
            if (initFeldLeer == true) {
                return Konstante.SCHNITTPUNKT_WEISS; // da schwarz beginnt
            } else {
                return Konstante.SCHNITTPUNKT_SCHWARZ; // da weiss beginnt
            }
        } else {
            /* return this.spielZugCollection.get(this.spielZugCollection.size()-1).getFarbe();
             * Man darf nicht einfach die Farbe des letzten Steins zurueckgeben,
             * da man im undo-modus sein koennt, aber nicht weiss ob er zug valide
             * ist. Man kann die liste der Stein also nicht veraendern. Daher
             * muss man die information aus dem Cache nehemen */
            return this.spielZugCollection.get(this.spielfeldCacheMitZugnummerStand - 1).getFarbe();
        }
    }

    /**
     *
     * @return Gibt den Spieler zurueck der aktuell an der Reihe ist.
     */
    public Spieler getSpielerAnDerReihe() {
        // Hole den Spieler der als letztes dran war, dann ist der andere Spiele
        // als nächstes an der Reihe
        int spNichtAnDerReihe = this.getSpielerFarbeVonLetztemZug();

        if (spNichtAnDerReihe == Konstante.SCHNITTPUNKT_SCHWARZ) {
            // Weisser Spieler ist gerade am Zug
            return this.getSpielerWeiss();
        } else {
            // Schwarzer Spieler ist gerade am Zug
            return this.getSpielerSchwarz();
        }
    }

    /**
     * Diese Funktion soll Steine vom Spielfeld nehmen, wenn dies moeglich ist.
     * Dabei benoetigt sie eine Koordinate mit der sie beginnen kann, damit klar
     * ist, welche Gruppe gemeint ist. Von dieser Koordinate aus versucht sie dann
     * Steine vom Brett zu entfernen.
     * Folgende Werte werden im Spielfeld veraendert:
     *  - Feld (Die Eingabe)
     *  - aktuellesSpielfeldCache (Nur wenn nehem(Eingabe) true ist)
     * @param xPos Ist die X-Koordinate des Steines, der zur Gruppe gehoert,
     * die vom Brett zu nehmen versucht wird. (Wert 0 bis feldgroesse-1)
     * @param yPos Ist die Y-Koordinate des Steins. (Wert 0 bis feldgroesse-1)
     * @param Feld Ist ein 2-Dimensionales Array vom Typ AnalyseSchnittpunkt. Es
     * dient als Brettstellung, die zu betrachten ist. Dieses Feld wird veraendert
     * @param nehmen Gibt an ob die Steine nur markiert werden, oder auch wirklich
     * vom Brett genommen werden sollen. Sollen die Steine nicht vom Brett genommen
     * werden, so ist der Rueckgabewert 1, wenn die Steine genommen werden koennten,
     * und 0 wenn nicht.
     * @return Der Rueckgabewert kennzeichnet bei Erfolg (also wenn die Gruppe
     * vom Brett genommen werden konnte) wieviele Steine entfernt worden. Bei
     * Misserfolg gibt es 2 Moeglichkeiten: 1. Die Gruppe konnte nicht entfernt
     * werden, da sie noch Freiheiten besitzt (Rueckgabewert 0). 2. Die Gruppe
     * konnte nicht entfernt werden, weil an (xPos, yPos) gar kein Stein liegt
     * (Rueckgabewert -1)
     */
    private int versucheSteinZuNehmen(int xPos, int yPos, AnalyseSchnittpunkt feld[][], boolean nehmen) {
        /* Zuerst testen ob an (x,y) ueberhaupt ein Stein ist */
        if (feld[xPos][yPos].getBelegungswert() == Konstante.SCHNITTPUNKT_LEER || feld[xPos][yPos].getBelegungswert() == Konstante.SCHNITTPUNKT_VERBOTEN) {
            return -1;
        }

        /* Jetzt beginnt die eigentliche Prozedur, da man weiss, dass es sich entweder
         * um einen schwarzen oder einen weissen Stein handelt. Zuerst wird getestet ob die
         * Steine schon betrachtet wurden. Ist dies der Fall wird abgebrochen und
         * gesagt, das keine Steine gefangen worden (Da diese wenn sie schon
         * markiert sind auch schon vom Brett genommen worden, oder nicht)*/
        if (feld[xPos][yPos].getSteinStatus() == Konstante.STEIN_GEFANGEN
                || feld[xPos][yPos].getSteinStatus() == Konstante.STEIN_LEBENDIG) {
            return 0;
        }

        /* Es muss also was veraendert werden, da der Status der Steine noch
         * ungewiss ist. Am ende der Funktion werden entweder alle Steine der
         * Gruppe als lebendig markiert, oder alle als tot markiert und vom Brett
         * (richtigen spielfeld) genommen.
         */
        boolean fangbar = true;
        int farbe = feld[xPos][yPos].getBelegungswert();

        /* Es wird eine Liste angelegt in die die durchsuchten Steine gelegt
         * werden. Dabei wird eine Gruppe von Steinen mit Hilfe von Breitensuche
         * durchsucht.
         */
        List<AnalyseSchnittpunkt> listeSteine;
        listeSteine = new ArrayList<AnalyseSchnittpunkt>();
        int momElement = 0;

        /* Als erstes wird der Stein, an dem begonnen wird zu suchen in die
         * Liste eingenommen
         */
        feld[xPos][yPos].setMarkiert(true);
        listeSteine.add(feld[xPos][yPos]);
        /* Jetzt solange nach Steinen suchen und diese in Liste aufnehmen,
         * bis keine Steine mehr da sind */
        do {
            /* Jetzt, wenn Nachbarsteine existieren und diese noch nicht
             * markiert sind, dann werden sie in die Liste aufgenommen.
             * Ist der Nachbar leer oder verboten, so steht dort kein Stein und
             * die Gruppe hat mindestens eine Freiheit. Sie ist somit nicht
             * fangbar.
             * Wenn der Nachbar ein Stein der gleichen Farbe ist, so muss er
             * in die Liste aufgenommen werden. Allerdings nur, wenn er noch
             * nicht markiert ist. */

            /* Wenn Stein linken Nachbarn hat*/

            if (listeSteine.get(momElement).getXPos() != 0) {
                if (feld[listeSteine.get(momElement).getXPos() - 1][listeSteine.get(momElement).getYPos()].getBelegungswert() == Konstante.SCHNITTPUNKT_LEER
                        || feld[listeSteine.get(momElement).getXPos() - 1][listeSteine.get(momElement).getYPos()].getBelegungswert() == Konstante.SCHNITTPUNKT_VERBOTEN) {
                    fangbar = false;
                } else if (feld[listeSteine.get(momElement).getXPos() - 1][listeSteine.get(momElement).getYPos()].getBelegungswert() == farbe) {
                    if (feld[listeSteine.get(momElement).getXPos() - 1][listeSteine.get(momElement).getYPos()].getMarkiert() == false) {
                        feld[listeSteine.get(momElement).getXPos() - 1][listeSteine.get(momElement).getYPos()].setMarkiert(true);
                        listeSteine.add(feld[listeSteine.get(momElement).getXPos() - 1][listeSteine.get(momElement).getYPos()]);
                    }
                }
            }
            /* Wenn Stein rechten Nachbarn hat */
            if (listeSteine.get(momElement).getXPos() != this.getSpielfeldGroesse() - 1) {
                if (feld[listeSteine.get(momElement).getXPos() + 1][listeSteine.get(momElement).getYPos()].getBelegungswert() == Konstante.SCHNITTPUNKT_LEER
                        || feld[listeSteine.get(momElement).getXPos() + 1][listeSteine.get(momElement).getYPos()].getBelegungswert() == Konstante.SCHNITTPUNKT_VERBOTEN) {
                    fangbar = false;
                } else if (feld[listeSteine.get(momElement).getXPos() + 1][listeSteine.get(momElement).getYPos()].getBelegungswert() == farbe) {
                    if (feld[listeSteine.get(momElement).getXPos() + 1][listeSteine.get(momElement).getYPos()].getMarkiert() == false) {
                        feld[listeSteine.get(momElement).getXPos() + 1][listeSteine.get(momElement).getYPos()].setMarkiert(true);
                        listeSteine.add(feld[listeSteine.get(momElement).getXPos() + 1][listeSteine.get(momElement).getYPos()]);
                    }
                }
            }
            /* Wenn Stein oberen Nachbarn hat (Oben und unten werden vielleicht
             * umdefiniert, ist aber egal fuer den Algorithmus!)*/
            if (listeSteine.get(momElement).getYPos() != this.getSpielfeldGroesse() - 1) {
                if (feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() + 1].getBelegungswert() == Konstante.SCHNITTPUNKT_LEER
                        || feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() + 1].getBelegungswert() == Konstante.SCHNITTPUNKT_VERBOTEN) {
                    fangbar = false;
                } else if (feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() + 1].getBelegungswert() == farbe) {
                    if (feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() + 1].getMarkiert() == false) {
                        feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() + 1].setMarkiert(true);
                        listeSteine.add(feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() + 1]);
                    }
                }
            }
            /* Wenn Stein unteren Nachbarn hat */
            if (listeSteine.get(momElement).getYPos() != 0) {
                if (feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() - 1].getBelegungswert() == Konstante.SCHNITTPUNKT_LEER
                        || feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() - 1].getBelegungswert() == Konstante.SCHNITTPUNKT_VERBOTEN) {
                    fangbar = false;
                } else if (feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() - 1].getBelegungswert() == farbe) {
                    if (feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() - 1].getMarkiert() == false) {
                        feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() - 1].setMarkiert(true);
                        listeSteine.add(feld[listeSteine.get(momElement).getXPos()][listeSteine.get(momElement).getYPos() - 1]);
                    }
                }
            }

            /* Jetzt wurden alle Nachbarn aufgenommen. Der naechste Stein wird
             * also untersucht. Doch zuerst muss der jetzige sauber
             * abgeschlossen werden */
            ++momElement;
        } while (momElement <= listeSteine.size() - 1);

        /* Alle Steine der Gruppe sind jetzt in der Liste. Jetzt wird weiter
         * gemacht, je nachdem die Steine gefangen werden konnten */
        if (fangbar == false) {
            /* Steine als lebendig markieren 
             * da momElement gerade auf leeres Feld zeigt! dekrementieren */
            for (int i = momElement - 1; i >= 0; --i) {
                feld[listeSteine.get(i).getXPos()][listeSteine.get(i).getYPos()].setSteinStatus(Konstante.STEIN_LEBENDIG);

            }
            return 0; // wurde ja kein Stein gefangen
        } else {
            /* Wenn Steine gefangen werder koennen, kommt es auf den
             * Aufrufparameter der Funktion an. Je nachdem, ob Steine vom
             * Richtigen brett genommen werden sollen. */
            if (nehmen == true) {
                for (int i = momElement - 1; i >= 0; --i) {
                    feld[listeSteine.get(i).getXPos()][listeSteine.get(i).getYPos()].setSteinStatus(Konstante.STEIN_GEFANGEN);
                    aktuellesSpielfeldCache[listeSteine.get(i).getXPos()][listeSteine.get(i).getYPos()] = Konstante.SCHNITTPUNKT_LEER;
                }
                return momElement; // momElement ist gerade die Gefangenenzahl ;)
            } else {
                /* Wenn Steine genehmen werden koennen, muessen sie als Tot 
                 * markiert werden */
                for (int i = momElement - 1; i >= 0; --i) {
                    feld[listeSteine.get(i).getXPos()][listeSteine.get(i).getYPos()].setSteinStatus(Konstante.STEIN_GEFANGEN);
                }
                return 1;
            }
        }
    }

    /**
     * Vereinfachte variante der Funktion versucheSteinZuNehmen. Dabei muss nicht
     * uebergeben werden, ob man die Steine nehmen soll. Dieser Wert wird true
     * gesetzt
     * @param XPos X-Position des Steins
     * @param YPos Y-Position des Steins
     * @param feld Spielfeld
     * @return
     */
    private int versucheSteinZuNehmen(int XPos, int YPos, AnalyseSchnittpunkt feld[][]) {
        return versucheSteinZuNehmen(XPos, YPos, feld, true);
    }

    /**
     * Bei Ko muss ein Spielpunkt verboten werden. Zum schnelleren Zugriff wird
     * sich dieser Punkt gemerkt. Hiermit wird die X-Koordinate dieses Punktes
     * gesetzt
     * @param xPosVerboten X-Koordinate (Von 0 bis Feldgroesse-1)
     */
    private void setXPosVerboten(int xPosVerboten) {
        this.xPosVerboten = xPosVerboten;
    }

    /**
     * Bei Ko muss ein Spielpunkt verboten werden. Zum schnelleren Zugriff wird
     * sich dieser Punkt gemerkt. Hiermit wird die Y-Koordinate dieses Punktes
     * gesetzt
     * @param yPosVerboten Y-Koordinate (Von 0 bis Feldgroesse-1)
     */
    private void setYPosVerboten(int yPosVerboten) {
        this.yPosVerboten = yPosVerboten;
    }

    /**
     * @return X-Koordinate des Verbotenen Zugs (Von 0 bis Spielfeldgroesse-1)
     */
    private int getXPosVerboten() {
        return this.xPosVerboten;
    }

    /**
     * @return Y-Koordinate des Verbotenen Zugs (Von 0 bis Spielfeldgroesse-1)
     */
    private int getYPosVerboten() {
        return this.yPosVerboten;
    }

    /**
     * Auf dem Go-Brett ist ein Punkt immer nur fuer einen Zug verboten. Daher
     * muss der Punkt wieder freigegeben werden. Somit kann man beim naechsten
     * Zug wieder auf diesen Spielpunkt spielen.
     * Der verbotene Punkt wird natuerlich nur geloescht wenn er auch wirklich
     * existiert (x- und y-koordinaten sind groesser gleich 0)
     */
    private void loescheVerbotenenPunkt() {
        if (this.getXPosVerboten() >= 0 && this.getYPosVerboten() >= 0) {
            if (this.aktuellesSpielfeldCache[this.getXPosVerboten()][this.getYPosVerboten()] == Konstante.SCHNITTPUNKT_VERBOTEN) {
                this.aktuellesSpielfeldCache[this.getXPosVerboten()][this.getYPosVerboten()] = Konstante.SCHNITTPUNKT_LEER;
                this.setXPosVerboten(-1);
                this.setYPosVerboten(-1);
            }
        }
    }

    /**
     * Setzt den Wert fuer den verbotenen Punkt neu. Der verbotene Punkt entsteht
     * durch eine Ko-Situation.
     * @param xArray X-Koordinate (0 bis Feldgroesse-1)
     * @param yArray Y-Koordinate (0 bis Feldgroesse-1)
     */
    private void setzeVerbotenenPunkt(int xArray, int yArray) {
        this.setXPosVerboten(xArray);
        this.setYPosVerboten(yArray);
    }

    /**
     * Nachdem ein Zug ausgefuehrt wurde, muss dieser in die Liste der bisherigen
     * Zuege eingetragen werden. Somit kann aus der Liste der bisherigen Zuege
     * einfach die Brettsituation wieder hergestellt werden.
     * @param xPos X-Koordinate (1-Feldgroesse) (-1 Bei Passen)
     * @param yPos Y-Koordinate (1-Feldgroesse) (-1 Bei Passen)
     * @param farbe Farbe des Spielers, der Zug ausgefuehrt hat
     */
    private void steinEintragen(int xPos, int yPos, int farbe) {
        this.spielZugCollection.add(new Spielzug(xPos, yPos, farbe));
        this.letzteZugnummer++;
    }

    /**
     * Nachdem Steine von einer bestimmten Farbe gefangen wurden, muss die
     * Anzahl der gefangenen Steine dieser Farbe erhoeht werden. Wenn zum Beispiel
     * 8 schwarze Steine gefangen wurden, muss die Anzahl der gefangenen schwarzen
     * Steine um 8 erhoeht werden
     * @param farbe Farbe der gefangenen Steine
     * @param zahl Anzahl der Steine die gefangen wurden
     */
    private void erhoeheGefangenenZahl(int farbe, int zahl) {
        if (farbe == Konstante.SCHNITTPUNKT_SCHWARZ) {
            this.spielerSchwarz.addGefangenenAnzahl(zahl);
        }
        if (farbe == Konstante.SCHNITTPUNKT_WEISS) {
            this.spielerWeiss.addGefangenenAnzahl(zahl);
        }
    }

    /**
     * Wird im Spiel gepasst, muss das in der Liste der Zuege eingetragen werden.
     */
    public void zugPassen() {
        this.zugPassen(this.getSpielerFarbeAnDerReihe());
    }

    /**
     * Wird im Spiel gepasst, muss das in der Liste der Zuege eingetragen werden.
     * @param spielerFarbe Farbe des Spielers der gepasst hat
     */
    public void zugPassen(int spielerFarbe) {
        /* Da gepasst wurde, muss der Verbotene Zug geloescht werden*/
        this.loescheVerbotenenPunkt();
        /* Der Counter des Caches muss erhoeht werden */
        this.spielfeldCacheMitZugnummerStand = this.letzteZugnummer + 1;
        /* Nun wird der Stein eingetragen, mit Koordinate (-1,-1) fuer Passen */
        this.steinEintragen(-1, -1, spielerFarbe);
    }

    /**
     * Das Spielfeld wird von vorn aufgebaut, und es wird versucht die Zuege
     * aus der ZugCollection auszufuehren. Ist nur ein Zug dabei nicht moeglich,
     * ist das Feld nicht valide. Das Startfeld, darf nur Steine erhalten die
     * mindestens eine Freiheit haben. Die Zuege muessen wie gesagt ausfuehrbar
     * sein, und die Reihenfolge der Spieler muss auch stimmen. Wenn irgendwann
     * 2 mal gepasst wurde, darf danach nicht weiter gespielt werden.
     * @return Gibt einen String zurueck, der eine Fehlermeldung enthaelt.
     *  Ist er null gitbt es keien Fehler
     * Ueberprueft wird:
     * - den Spielzustand
     * - die spielZugCollection mit den gemachten Zügen
     * - die Objekte spielerSchwarz und spielerWeiss
     * - sowie die gefangenenAnzahl der beiden Spieler
     *
     * Die Funktion muss erst noch implementiert werden
     */
    public String spielfeldValidiert() {

        boolean validiert = true;

        // Spieler validieren
        if (this.spielerSchwarz == null || this.spielerWeiss == null) {
            return "Es existieren noch nicht alle Spieler";
        }
        // Warum die Gefangenenanzahl auf 0 setzen?? ... was bei einem geladenen Spiel?
        // this.spielerSchwarz.setGefangenenAnzahl(0);
        // this.spielerWeiss.setGefangenenAnzahl(0);
        if (this.spielerSchwarz.getSpielerName() == null || this.spielerSchwarz.getSpielerName().length() <= 1) {
            this.spielerSchwarz.setSpielerName("Schwarz");
        }
        if (this.spielerWeiss.getSpielerName() == null || this.spielerWeiss.getSpielerName().length() <= 1) {
            this.spielerWeiss.setSpielerName("Weiss");
        }


        /* Als erstes wird getestet, ob alle Steine des Anfangsbrettes mindestens
         * eine Freiheit haben */
        this.aktuellesSpielfeldCache = this.getSpielfeldZumZeitpunkt(0);
        this.spielfeldCacheMitZugnummerStand = 0;
        this.loescheVerbotenenPunkt();
        AnalyseSchnittpunkt feld[][] = new AnalyseSchnittpunkt[this.getSpielfeldGroesse()][this.getSpielfeldGroesse()];
        /* Kopie fuer Anfangstest erstellen */
        for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                feld[i][j] = new AnalyseSchnittpunkt(i, j, this.aktuellesSpielfeldCache[i][j]);
            }
        }
        for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                if (this.versucheSteinZuNehmen(i, j, feld, false) == 1) {
                    validiert = false;
                    return "Anfangsspielstellung nicht valide. Stelle " + (i + 1) + "," + (j + 1) + ". Gruppe hat keine Freiheiten";
                }
            }
        }

        /* Gibt es noch keine Zuege, ist das Feld valide */
        if (this.letzteZugnummer == 0) {
            this.setSpielfeldZumZeitpunkt(0);
            return null;
        }

        /* Wenn man bis hier kommt, ist die Anfangsstellung valide. Jetz muessen
         * zuege gemacht werden. Dabei ist auf die Reihenfolge zu achten, und
         * ob die Zuege ausfuehrbar sind.
         */
        int momSpieler = this.spielZugCollection.get(0).getFarbe();
        boolean letzterZugGepasst = false;
        boolean vorletzterZugGepasst = false;

        for (int i = 0; i < this.spielZugCollection.size(); i++) {
            /* Reihenfolge der Spieler beachten */
            if (this.spielZugCollection.get(i).getFarbe() != momSpieler) {
                validiert = false;
                return "Momentaner Spieler zum Zeitpunkt " + i + " ist falsch.";
            }

            /* Es darf nur ein Zug gemacht werden, wenn die Letzten beiden Zuege
             * nicht passen waren*/
            if (letzterZugGepasst == true && vorletzterZugGepasst == true) {
                validiert = false;
                return "Es wurde 2 mal gepasst und dann weiter gespielt.";
            }

            /* Passen abfangen */
            if (this.spielZugCollection.get(i).getXPosition() == -1
                    && this.spielZugCollection.get(i).getYPosition() == -1) {
                if (letzterZugGepasst == true) {
                    vorletzterZugGepasst = true;
                } else {
                    letzterZugGepasst = true;
                }
                /* Spieler wechseln */
                if (momSpieler == Konstante.SCHNITTPUNKT_SCHWARZ) {
                    momSpieler = Konstante.SCHNITTPUNKT_WEISS;
                } else {
                    momSpieler = Konstante.SCHNITTPUNKT_SCHWARZ;
                }
            } /* Kein passen, also Zug*/ else {
                switch (this.setStein(this.spielZugCollection.get(i).getXPosition(),
                        this.spielZugCollection.get(i).getYPosition(),
                        momSpieler)) {
                    case 1: /* Zug erfolgreich */
                        break;
                    case 0: /* Liegt nicht auf Feld */
                        validiert = false;
                        return "Zug " + (i + 1) + " liegt nicht auf dem Brett. Koordinaten " + this.spielZugCollection.get(i).getXPosition() + "," + this.spielZugCollection.get(i).getYPosition();
                    case -1: /* War verboten (Ko) */
                        validiert = false;
                        return "Zug " + (i + 1) + " ist verboten (Ko-Regel)";
                    case -2: /* Schon belegt gewesen */
                        validiert = false;
                        return "Zug " + (i + 1) + " ist schon Belegt. Koordinaten " + this.spielZugCollection.get(i).getXPosition() + "," + this.spielZugCollection.get(i).getYPosition();
                    case -3: /* Selbstmord */
                        System.out.println("Zug " + (i + 1) + " ist verboten (Selbstmord)");
                        validiert = false;
                        return "Zug " + (i + 1) + " ist verboten (Selbstmord)";
                    default:
                        throw new UnsupportedOperationException("Unbekannter return-Wert von setStein");
                }
                /* Bis hier wenn Zug erfolgreich. SetStei hat folgendes veraendert:
                 * - aktuellesSpielfeldCache (Wenn Zug erfolgreich war)
                 * - xPosVerboten
                 * - yPosVerboten
                 * - gefangenenAnzahl der Spieler
                 * - spielfeldCacheMitZugnummerStand
                 * Jetzt muss noch der Spieler veraendert werden und die
                 * passen-Variablen angepasst werden */
                vorletzterZugGepasst = letzterZugGepasst;
                letzterZugGepasst = false;

                /* Spieler wechseln */
                if (momSpieler == Konstante.SCHNITTPUNKT_SCHWARZ) {
                    momSpieler = Konstante.SCHNITTPUNKT_WEISS;
                } else {
                    momSpieler = Konstante.SCHNITTPUNKT_SCHWARZ;
                }
            }
        }

        /* Wenn man bis hier kommt, ist das Spiel valide. Es wurde die Reihenfolge
         * der Spieler, das Passen und die Zuege beachtet. Alles war korrekt
         */
        return null;
    }

    /**
     *
     * @return Gibt die Anzahl der Spielzüge in denen der
     * aktuelle Spieler direkt hintereinander zuletzt gepasst hat.
     * (Bei GO ist das Spiel nach zweimaligem passen eines Spielers zu Ende).
     * 
     * Die Funktion muss noch implementiert werden
     */
    public int getAnzahlLetzterPassZuege() {
        int anzahlDerZuege;
        int anzahlLetzterPassZuege = 0;
        if(this.spielZugCollection.isEmpty()){
            return 0;
        }
        anzahlDerZuege = this.spielZugCollection.size();
        int i = 1;
        while (this.spielZugCollection.get(anzahlDerZuege - i).getXPosition() == -1
                && this.spielZugCollection.get(anzahlDerZuege - i).getYPosition() == -1
                && anzahlDerZuege - i >= 0) {
            anzahlLetzterPassZuege++;
            i++;
            if(i>anzahlDerZuege){
                break;
            }
        }
        return anzahlLetzterPassZuege;
    }

    /**
     * Es muss ein Zug auf dem Spielfeld gemacht werden. Dazu wird ein Stein
     * gesetzt, und bei erfolgreichem setzen wird die Nummer.
     * Ist das Brett gerade nicht dem aktuellen Cache, wird das Spielfeld
     * dem Cache angepasst.
     * @param xPos X-Position des Spielfelds.
     * Diese kann Werte zwischen 1 und der Feldlänge enthalten
     * @param yPos Y-Position des Spielfelds.
     * Diese kann Werte zwischen 1 und der Feldlänge enthalten
     * @param spielerFarbe Farbe des Spielers
     * @return Je nach Situarion signalisiert der Integer, was passiert ist:
     *  1: Zug wurde erfolgreich durchgefuehrt : (OK)
     *  0: Zug liegt nicht auf Spielfeld: (FEHLER)
     * -1: Zug war verboten : (FEHLER)
     * -2: Schnittpunkt schon belegt : (FEHLER)
     * -3: Selbstmord (verboten) : (FEHLER)
     */
    public int macheZug(int xPos, int yPos, int spielerFarbe) {
        int rueckgabe;
        rueckgabe = this.setStein(xPos, yPos, spielerFarbe);
        /* Wurde der Zug erfolgreich ausgefuehrt, muss das Spielfeld veraendert
         * werden */
        if (rueckgabe == 1) {
            this.setSpielfeldZumZeitpunkt(this.spielfeldCacheMitZugnummerStand - 1);
            this.steinEintragen(xPos, yPos, spielerFarbe);
        }
        return rueckgabe;
    }

    /**
     * Die kurze Variante von macheZug, fuehrt die Lange aus, jedoch mit
     * alternierender Spielerfarbe
     * @param xPos X-Position des Spielfelds.
     * Diese kann Werte zwischen 1 und der Feldlänge enthalten
     * @param yPos Y-Position des Spielfelds.
     * Diese kann Werte zwischen 1 und der Feldlänge enthalten
     * @return Je nach Situarion signalisiert der Integer, was passiert ist:
     *  1: Zug wurde erfolgreich durchgefuehrt : (OK)
     *  0: Zug liegt nicht auf Spielfeld: (FEHLER)
     * -1: Zug war verboten : (FEHLER)
     * -2: Schnittpunkt schon belegt : (FEHLER)
     * -3: Selbstmord (verboten) : (FEHLER)
     */
    public int macheZug(int xPos, int yPos) {

        /* Die Funktion setzt den Stein fuer abhaengig vom letzten gespielten Zug.
         * Wenn also Spieler schwarz den letzten Zug gesetzt hat, wird der neue
         * Zug für Spieler weiss eingetragen.
         */
        return this.macheZug(xPos, yPos, this.getSpielerFarbeAnDerReihe());
        /* Weitere Aufgaben:
         *  - Prüfen des Zuges, ob er Möglich ist (Doppelzüge beachten)
         *      => Ueber die Funktion setSteinMoeglich( ... )
         *  - Prüfen wer am Zug ist
         *  - Cache-Werte INKREMENTELL erneuern.
         */

        //DUmmy
    }

    /**
     *
     * @param vorgabenZahl Anzahl der Vorgaben
     * @return Wurde erfolgreich initialisiert
     */
    public boolean initialisiereFeldMitVorgabenFuerSchwarz(int vorgabenZahl) {
        /* Wenn die Zahl der Vorgaben groesser als 9 ist, so ist die
         * Vorgabezahl Falsch -> Rueckgabewert False
         * Die Zahl der Vorgabesteine darf natuerlich auch nicht negativ sein.
         */
        if (vorgabenZahl < 0 || vorgabenZahl > 9) {
            return false;
        }

        /* Vorgabezahl speichern  */
        this.vorgabeZahl = vorgabenZahl;

        /* In Abhaengigkeit der Feldgroesse werden nun die Vorgabesteine fuer
         * Schwarz gesetzt. Dabei wird kein komplexer Algorithmus verwendet,
         * sondern es werden einfach und stur die Steine gesetzt.
         * Daher wird die Funktion zwar entsprechend lang, wird aber keine Fehler
         * verursachen (hoffe ich mal)
         */
        switch (this.getSpielfeldGroesse()) {
            case 7:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[1][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][1] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;
            case 9:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[4][4] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;
            case 11:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[2][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][2] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[5][5] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;
            case 13:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[6][6] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;
            case 15:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[11][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[11][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][11] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[7][7] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;
            case 17:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[13][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[13][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][13] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[8][8] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;

            case 19:
                switch (vorgabenZahl) {
                    case 0:
                        break; /* nichts machen */
                    case 1:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 2:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 3:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 4:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 5:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 6:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 7:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 8:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    case 9:
                        this.initialfeld[15][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[15][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[3][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][15] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][3] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        this.initialfeld[9][9] = Konstante.SCHNITTPUNKT_SCHWARZ;
                        break;
                    default: /* Bis hierher ist es nicht moeglich zu kommen,
                         * wenn doch -> Fehler */
                        return false;
                }
                break;
            default: /* Die Spielfeldgroesse ist Falsch! Es koennen keine
                 * Vorgabesteine gesetzt werden */
                return false;
        }

        /* Jetzt muss noch das Feld mit den Vorgabesteinen gefuellt werden.*/
        for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                this.aktuellesSpielfeldCache[i][j] = this.initialfeld[i][j];
            }
        }
        return true;
    }

    /**
     * Aus dem Feld, das momentan auf dem Spielbrett liegt, soll eine Initialfeld
     * erstellt werden. Das Bedeutet, das das momentane Initialfeld umgeschrieben
     * werden muss und die Liste der Zuege geloescht wird.
     */
    public void initialisiereFeldMitAktuellemFeld() {
        /* Als erstes wird das aktuelle Feld (Cache) auf das Initialfeld
         * kopiert. */
        for (int i = 0; i < this.getSpielfeldGroesse(); i++) {
            for (int j = 0; j < this.getSpielfeldGroesse(); j++) {
                this.initialfeld[i][j] = this.aktuellesSpielfeldCache[i][j];
            }
        }

        /* Da die Liste der Steine nicht mehr benötigt wird, muss sie geloescht
         * werden */
        this.setSpielfeldZumZeitpunkt(0);
        this.spielfeldCacheMitZugnummerStand = 0;
        this.setXPosVerboten(-1);
        this.setYPosVerboten(-1);
    }

    /**
     * Damit ein Feld markiert werden kann, muss man wissen, zu welchem Zeitpunkt
     * welcher Zug gespielt wurde. Dazu kann man die Spielzuege auslesen
     * @param zeitpunkt Zeitpunkt zu dem Markiert werden soll.
     * @return Punkt der Markiert werden soll
     */
    public Point getMarkiertenSteinZumZeitpunkt(int zeitpunkt) {
        if (zeitpunkt <= 0 || zeitpunkt > this.letzteZugnummer) {
            return null;
        }
        Point rueckgabe = new Point(this.spielZugCollection.get(zeitpunkt - 1).getXPosition(),
                this.spielZugCollection.get(zeitpunkt - 1).getYPosition());
        if(rueckgabe.getX() < 1 || rueckgabe.getX() > this.getSpielfeldGroesse() ||
           rueckgabe.getY() < 1 || rueckgabe.getY() > this.getSpielfeldGroesse()){
            return null;
        }
        return rueckgabe;
    }

    /**
     * Soll die Zeit im Spiel ignoriert werden?
     * @param ignore Ignorieren oder nicht
     */
    public void setIgnoreTime(boolean ignore) {
        this.ignoreTime = ignore;
    }

    /**
     * @return Wird die Zeit gerade ignoriert?
     */
    public boolean getIgnoreTime() {
        return this.ignoreTime;
    }

    /**
     * Damit man das Feld mit klicken initialisieren kann, muss man Steine
     * auf das initialfeld legen. Ob das dann valide ist, muss noch getestet
     * werden.
     * @param xPos von 1-Brettgroesse
     * @param yPos von 1-Brettgroesse
     * @param farbe Schwarz oder Weiss
     */
    public void legeSteinAufInitBrett(int xPos, int yPos, int farbe) {
        int xKoord = xPos - 1;
        int yKoord = yPos - 1;

        if (xKoord < 0 || xKoord >= this.getSpielfeldGroesse()
                || yKoord < 0 || yKoord >= this.getSpielfeldGroesse()
                || (farbe != Konstante.SCHNITTPUNKT_SCHWARZ
                && farbe != Konstante.SCHNITTPUNKT_WEISS)) {
            return;
        }

        switch (this.initialfeld[xKoord][yKoord]) {
            case Konstante.SCHNITTPUNKT_LEER:
                this.initialfeld[xKoord][yKoord] = farbe;
                break;
            case Konstante.SCHNITTPUNKT_SCHWARZ:
                switch (farbe) {
                    case Konstante.SCHNITTPUNKT_SCHWARZ:
                        this.initialfeld[xKoord][yKoord] = Konstante.SCHNITTPUNKT_LEER;
                        break;
                    default:
                        this.initialfeld[xKoord][yKoord] = farbe;
                }
                break;
            case Konstante.SCHNITTPUNKT_WEISS:
                switch (farbe) {
                    case Konstante.SCHNITTPUNKT_WEISS:
                        this.initialfeld[xKoord][yKoord] = Konstante.SCHNITTPUNKT_LEER;
                        break;
                    default:
                        this.initialfeld[xKoord][yKoord] = farbe;
                }
                break;
        }
    }

    /**
     *
     * @return Anzhal der Vorgabesteine
     */
    public int getVorgabeZahl() {
        return this.vorgabeZahl;
    }

    /**
     * Rueckgabe der Liste von Spielzuegen fuer die Klasse Speichern
     * @return Spielzugcollection
     */
    public List<Spielzug> getspielZugCollection() {

        return this.spielZugCollection;

    }

}
