package TetrisV0;

import java.awt.geom.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * La classe Tauler gestiona un array bidimensional de caselles
 * S'encarrega de la generació inicial del tauler,
 * la col·locació de peces sobre el mateix
 * i del càlcul de l'ombra produida per una pesa
 *
 * @author JGalmés
 */
public final class Tauler extends JPanel {

    //20 ALTURA X 10 AMPLADA
    private static final int DIMENSIOY = 20; //Dimensió del tauler en caselles
    private static final int DIMENSIOX = 10;
    private static final int DESFASE_X = 7;
    private static final int DESFASE_Y = 7;
    private final int dimY, dimX;
    private final int COSTAT; //Tamany de cada casella
    private final Rectangle2D.Double taulerBounds;
    private final Color NEGRE1 = new Color(43, 43, 43);
    private final Color NEGRE2 = new Color(47, 47, 47);
    private final Casella casella[][];
    private final Casella cuadrePecesNoves;
    private Rectangle2D.Double rectangle;
    private Point[] ubiCas; //Emmagatzema la ubicació (Fila/Columna) de les caselles de la ombra
    private Point[] lastUbiCas;
    private int lastFila = -1;
    private int lastColumna = -1;
    private int posX = 0, posY = 0;

    //Només serveix per a pintar la peça amb el mètode intern paintComponents
    //tota la gestió de les peces es fa a la classe principal
    public Pesa pesaActual;

    /**
     * Constructor de la classe tauler, dibuixa el tauler principal
     * i el recuadre on apareixen les peçes
     *
     * @param x Posicio incial del tauler
     * @param y Posicio incial del tauler
     */
    public Tauler(int x, int y) {
        dimX = x;
        dimY = y;
        this.setMinimumSize(new Dimension(x, y));

//        if (dimX != dimY) {
//            System.out.println("Error les dimensions han de ser iguals, el tauler és cuadrat!");
//            System.exit(0);
//        }
        casella = new Casella[DIMENSIOY][DIMENSIOX];

        COSTAT = dimY / 20; //Calcula la dimensió de les caselles en funció del tamany d'entrada

        //Genera el tauler, alternant entre dos colors
        for (int i = 0; i < DIMENSIOY; i++) {
            posX = 0;
            for (int j = 0; j < DIMENSIOX; j++) {
//                System.out.printf("\n casella [i]=%d [j]=%d : %d o/o %d = %d\n", i, j, i, j, (i + j) % 2);
                rectangle = new Rectangle2D.Double(posX, posY, COSTAT, COSTAT);
                //recuadre = new Rectangle2D.Double(posX, posY, TOP_ALIGNMENT, TOP_ALIGNMENT)
                Color col; //Inicialitza a cada cicle el Color col que no té color assignat

                if ((i + j) % 2 == 0) {
                    col = NEGRE1;
                } else {
                    col = NEGRE2;
                }
                casella[i][j] = new Casella(rectangle, col, false);
//                casella[i][j] = new Casella(rectangle, col, imatge);
                posX = posX + COSTAT;
            }
            posY = posY + COSTAT;
        }

        rectangle = new Rectangle2D.Double(posX + 3, 0, COSTAT * 5.9, COSTAT * 6);

        //Es crea el cuadre on sortiràn les peçes noves
        cuadrePecesNoves = new Casella(rectangle, this.NEGRE2, false);

        double TOP_LEFT_X = casella[0][0].getRect().getX();
        double TOP_LEFT_Y = casella[0][0].getRect().getY();
        taulerBounds = new Rectangle2D.Double(TOP_LEFT_X, TOP_LEFT_Y, DIMENSIOX * COSTAT, DIMENSIOY * COSTAT);

        //this.repaint();
    }

    /**
     * Calcula l'ombra que produeix una pesa passada per paràmetre
     * al trobar-se sobre el tauler
     *
     * @param pesaActual pesa sobre la que volem calcular l'ombra
     */
    public void setOmbraPesa(Pesa pesaActual) {

        //Inicialitza l'array que contindrà les coordenades (fila i columna)
        //de cada una de les caselles involucrades amb l'ombra
        ubiCas = new Point[Pesa.NUMPIXELS];

        boolean pixelsDinsTauler = true;

        for (int i = 0; i < Pesa.NUMPIXELS; i++) {
            Point posPixel = pesaActual.getPixelPos(i);  //Obté la posició de cada uns dels píxels de la peça

            //Calcula a quina casella del tauler correspon la posició del píxel en qüestió aplicant un desfassament
            ubiCas[i] = getCasellaLocat(posPixel.x + DESFASE_X, posPixel.y + DESFASE_Y);

            if (ubiCas[i] == null) {
                pixelsDinsTauler = false;
                //System.out.println("Pixel[" + i + "] ubiCas.y= NULL  ubiCas.x=NULL   Pixels dins tauler false");
            }
        }

        //Si tota la peça es troba situada sobre el tauler i les caselles estàn disponibles
        if ((pixelsDinsTauler) && (!this.ubiCasOcupat())) {
            System.out.println("Pixels dins tauler true");

            int fila = ubiCas[0].y;
            int columna = ubiCas[0].x;
            System.out.printf("\nCasella seleccionada [%d][%d]\n", fila, columna);

            if (lastFila == -1) {//Si no s'han utilitzat (=-1)
                lastFila = fila;
                lastColumna = columna;
                lastUbiCas = ubiCas;
            }

            //Si s'ha mogut la peça s'esborra l'ombra de la posició anterior
            if ((fila != lastFila) || (columna != lastColumna)) {
                System.out.println("lastFila: " + lastFila + "  lastColumna: " + lastColumna);
                this.clrOmbraPesa();
            }

            //Dibuixa la nova ombra a les coordenades calculades
            for (int i = 0; i < Pesa.NUMPIXELS; i++) {
                casella[ubiCas[i].y][ubiCas[i].x].setContorn();
            }

            lastUbiCas = ubiCas;
            lastFila = ubiCas[0].y;
            lastColumna = ubiCas[0].x;

        }
    }

    /**
     * Esborra l'ombra de l'anterior posició
     */
    public void clrOmbraPesa() {
        if ((lastFila != -1) && (lastColumna != -1)) {
            for (int i = 0; i < Pesa.NUMPIXELS; i++) {
                casella[lastUbiCas[i].y][lastUbiCas[i].x].clrContorn();
            }
        }
    }

    /**
     * Mètode intern que pinta els components indicats, aquest mètode s'executa 
     * quan inicia el programa o quan és cridat per el mètode repaint()
     *
     * @param g Objecte Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        //Pinta el tauler, el cuadre de les peçes noves i la peça actual
        for (int i = 0; i < DIMENSIOY; i++) {
            for (int j = 0; j < DIMENSIOX; j++) {
                casella[i][j].paintComponent(g);
            }
        }

        cuadrePecesNoves.paintComponent(g);
        pesaActual.paintComponent(g);
    }

    /**
     * Mètode que retorna la dimensió preferida del JPanel
     *
     * @return dimensio del panell dimX, dimY
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(dimX, dimY);
    }

    /**
     * Mètode que retorna un objecte Rectangle que conté
     * tota l'àrea del tauler
     *
     * @return Rectangle que conté el tauler
     */
    public Rectangle getTaulerBounds() {
        return taulerBounds.getBounds();
    }

    /**
     * Mira si unes coordenades passades per paràmetre es troben
     * dins l'àrea del tauler
     *
     * @param coordX Coordenada X
     * @param coordY Coordenada Y
     * @return Retorna true si el contè, false si no.
     */
    public boolean esZonaTauler(int coordX, int coordY) {
        return taulerBounds.contains(coordX, coordY);
    }

    /**
     * Mira si un objecte Pesa passat per paràmetre es troba
     * dins l'àrea del tauler
     *
     * @param pesaActual Peça que es vol comprovar
     * @return Retorna true si el contè, false si no.
     */
    public boolean esZonaTauler(Pesa pesaActual) {
        boolean zonaTauler = true;

        //Es podría haver emprat pesa.getBounds() però el mètode intern Rectangle2D.getBounds 
        //retorna false si el cost del càlcul és excessiu, tot i estar dins l'àrea
        for (int i = 0; i < Pesa.NUMPIXELS; i++) {
            if (!taulerBounds.contains(pesaActual.getPesaPosX(), pesaActual.getPesaPosY())) {
                zonaTauler = false;
            }
        }
        return zonaTauler;
    }

    /**
     * Mètode que identifica si unes coordenades pertanyen a una casella particular
     *
     * @param coordX Coordenada X
     * @param coordY Coordenada Y
     * @param i Fila de la casella dins l'array del tauler
     * @param j Columna de la casella dins l'array del tauler
     * @return valor booleà, val true si conté les coordenades, false si no
     */
    public boolean esUnaCasella(int coordX, int coordY, int i, int j) {
        return casella[i][j].getRect().contains(coordX, coordY);
    }

    /**
     * Mètode que identifica si unes coordenades pertanyen a qualcuna
     * de les caselles del tauler
     * Retorna true si qualque casella conté les coordenades, false si no
     *
     * @param coordX Coordenada X
     * @param coordY Coordenada Y
     * @return valor booleà
     */
    public boolean esUnaCasella(int coordX, int coordY) {
        boolean esUnaCas = false;

        for (int i = 0; i < DIMENSIOY; i++) {
            for (int j = 0; j < DIMENSIOX; j++) {
                esUnaCas = casella[i][j].getRect().contains(coordX, coordY);
            }
        }

        return esUnaCas;
    }

    /**
     * Mètode que obté l'ubicació (fila i columna) d'una casella
     * dins l'array del tauler a partir d'unes coordenades passades per paràmetre
     * Retorna l'ubicació si hi ha qualque casella amb les coordenades,
     * null si no n'hi ha cap
     *
     * @param coordX Coordenada X
     * @param coordY Coordenada Y
     * @return Objecte Point que emmagatzema la fila i columna
     */
    public Point getCasellaLocat(int coordX, int coordY) {
        Point ubiCasella = new Point();
        boolean trobada = false;

        for (int i = 0; (i < DIMENSIOY) && (!trobada); i++) {
            for (int j = 0; (j < DIMENSIOX) && (!trobada); j++) {
                trobada = casella[i][j].getRect().contains(coordX, coordY);
                if (trobada) {
                    ubiCasella.y = i; //Fila
                    ubiCasella.x = j; //Columnna
                    //System.out.printf("Casella trobada [%d][%d]\n", i, j);
                }
            }
        }

        return trobada == true ? ubiCasella : null;
    }

    /**
     * Verifica si l'array que emmagatzema les ubicacions de les caselles
     * que formen l'ombra ha estat inicialitzat.
     *
     * @return booleà, true si conté valors, false si no està llest
     */
    public boolean ubiCasReady() {
        boolean ubiCasValid = true;

        for (int i = 0; i < ubiCas.length; i++) {
            if (ubiCas[i] == null) {
                ubiCasValid = false;
            }
        }
        return ubiCasValid;
    }

    /**
     * Verifica si les caselles que formen l'ombra estàn ocupades.
     *
     * @return booleà, true si estàn ocupades, false si estàn lliures
     */
    public boolean ubiCasOcupat() {
        boolean espaiOcupat = false;

        for (int i = 0; (i < ubiCas.length) && (!espaiOcupat); i++) {
            if (casella[ubiCas[i].y][ubiCas[i].x].isOcupada()) {
                espaiOcupat = true;
            }
        }
        return espaiOcupat;
    }

    /**
     * Col·loca una objecte Pesa passat per paràmetre a les caselles on es troba l'ombra
     *
     * @param pesaActual Objecte Pesa a col·locar
     */
    public void colocarPesa(Pesa pesaActual) {
        for (int i = 0; i < ubiCas.length; i++) {
            casella[ubiCas[i].y][ubiCas[i].x].setOcupada(pesaActual.getTextura());
        }

        this.repaint();
    }

    /**
     * Retorna les dimensions de les caselles
     *
     * @return COSTAT, un enter ja que les caselles són cuadrades
     */
    public int getTamanyCaselles() {
        return COSTAT;
    }

    /**
     * Verifica si una casella del tauler està ocupada,
     * indicada per paràmetre per fila i columna
     *
     * @param i Fila de la casella dins l'array del tauler
     * @param j Columna de la casella dins l'array del tauler
     * @return true si està ocupada, false si no
     */
    public boolean isCasellaOcupada(int i, int j) {
        return casella[i][j].isOcupada();
    }

    /**
     * Retorna un objecte Rectangle que conté l'àrea de la casella
     * indicada per paràmetre
     *
     * @param i Fila de la casella dins l'array del tauler
     * @param j Columna de la casella dins l'array del tauler
     * @return Rectangle, àrea de la casella
     */
    public Rectangle getZonaRectangle(int i, int j) {
        return casella[i][j].getRect().getBounds();
    }

}
