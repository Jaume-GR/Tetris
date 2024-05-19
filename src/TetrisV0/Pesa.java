package TetrisV0;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Aquesta classe permet crear objectes Peça i posteriorment treballar sobre elles
 * Les peces son un array de objectes Casella anomenats Píxels
 *
 * Al tractarse de Tetris s'ha decidit que els objectes peça siguin les peces del joc
 * es per això que aquesta classe presenta alguns mètodes estàtics que generen les
 * peces a partir d'unes plantilles predefinides i d'altres que gestionen les instàncies
 *
 * Les peçes (i tots els seus mètodes de posicionament) treballen sobre un punt de
 * referència, en aquest cas, el cantó lateral esquerre (similar als objectes Rectangle);
 * No obstant, per dur a terme la rotació s'han creat mètodes que permeten
 * posar temporalment l'eix de rotació sobre el centre de la peça
 *
 * @author JGalmés
 */
public class Pesa {

    public final static String BLAUCEL = "media/blaucel.png";

    public final static String BLAUCELV2 = "media/blaucelV2.png";
    public final static String BLAU = "media/blau.png";
    public final static String TARONJA = "media/taronja.png";
    public final static String VERMELL = "media/vermell.png";
    public final static String VERD = "media/verd.png";
    public final static String GROC = "media/groc.png";
    public final static String ROSA = "media/rosa.png";
    public final static String BLANC = "media/blanc.png";

    private final static String[] dirTextures = new String[]{BLAUCELV2, BLAU, TARONJA, GROC, VERD, ROSA, VERMELL};
    private final static Image[] textures = new Image[dirTextures.length];

    //Variables que identifiquen una forma concreta, la lletra representa la seva forma
    public final static int I = 0;/*Peça0 I*/
    public final static int J = 1;/*Peça1 J*/
    public final static int L = 2;/*Peça2 L*/
    public final static int O = 3;/*Peça3 O*/
    public final static int S = 4;/*Peça4 S*/
    public final static int T = 5;/*Peça5 T*/
    public final static int Z = 6;/*Peça6 Z*/

    private static int compX = 0; //Serveixen per fer un canvi de base de referencia
    private static int compY = 0; //permetent que l'eix de rotació sigui el píxel central

    private static int posXPesa;
    private static int posYPesa;
    private static int pixelWidth; //Amplària de cada píxel de la peça

    public final static int NUMPECES = 7;
    public final static int NUMPIXELS = 4; //Num de píxels de cada peça
    private static Casella[][] peces = new Casella[NUMPECES][NUMPIXELS];
    private static int[][][] pesaPosxy;
    private static int[][] factorZ; //Factor de correció per a la peça Z

    private Casella[] pixels; //Píxels de la peça
    private int numID; //Identificador del tipus de peça
    private int numRot = 0; //Estat de rotació de la peça
    private boolean visible;

    //Emmagatzema la distància que hi ha entre cada píxel i el punt on s'ha produit 
    //l'esdeveniment, permet moure la peça sense produïr bots de recol·locació 
    //independentment de a quina part de la peça s'hagui pitjat 
    private int[] distRelativaX = new int[NUMPIXELS];
    private int[] distRelativaY = new int[NUMPIXELS];

    /**
     * Constructor de la classe Pesa, crea un array de caselles i
     * dona valors inicials als atributs
     */
    public Pesa() {
        this.pixels = new Casella[NUMPIXELS];
        this.numRot = 0;
        this.visible = true;
    }

    /**
     * Constructor de la classe Pesa, dona valors als atributs
     * a partir d'un array de caselles i un valor booleà
     *
     * @param pxls Array d'objectes Casella que conformen la peça
     * @param vis Valor booleà que indica si la peça és visible o no
     */
    public Pesa(Casella[] pxls, boolean vis) {
        this.pixels = pxls;
        this.numRot = 0;
        this.visible = vis;
    }

    /**
     * Mètode que actualitza l'estat de la peça i la rota posteriorment
     */
    public void rotarPesa() {

        if (this.numRot != 3) {
            this.numRot++; //Augmenta el num de Rotacio
        } else {
            this.numRot = 0;
        }

        System.out.println("numRot:" + numRot);

        int COSTAT = pixelWidth;
        int numPesa = this.numID;

        //Es posa la compensació a 0
        compX = 0;
        compY = 0;
        Rectangle2D.Double rect = null;

        for (int j = 0; j < NUMPIXELS; j++) {

            //En funció de l'estat de rotació, la disposició dels píxels és diferent
            switch (numRot) {
                case 0:
                    rect = new Rectangle2D.Double(pesaPosxy[numPesa][0][j], pesaPosxy[numPesa][1][j], COSTAT, COSTAT);
                    compY = 0;
                    compX = 0;
                    break;
                case 1:
//                  pesaPosxy[NumPesa][Coordenada(X o Y)][NumPixel]
                    rect = new Rectangle2D.Double(-(pesaPosxy[numPesa][1][j]), (pesaPosxy[numPesa][0][j]), COSTAT, COSTAT);
                    compX = COSTAT; //S'aplica un ajustament
                    compY = -COSTAT; //per a que l'eix de rotació sigui el píxel central
                    break;
                case 2:
                    rect = new Rectangle2D.Double(-(pesaPosxy[numPesa][0][j]), -(pesaPosxy[numPesa][1][j]), COSTAT, COSTAT);
                    compX = COSTAT * 2;
                    break;
                case 3:
                    rect = new Rectangle2D.Double(pesaPosxy[numPesa][1][j], -(pesaPosxy[numPesa][0][j]), COSTAT, COSTAT);
                    compX = COSTAT;
                    compY = COSTAT;
                    break;
            }

            this.pixels[j] = new Casella(rect, textures[numPesa]);
            this.setPesaPos(posXPesa + compX, posYPesa + compY);

        }

    }

    /**
     * Mètode que identifica si les coordenades passades per paràmetre
     * corresponen a un píxel de la peça
     *
     * @param coordX Coordenada X
     * @param coordY Coordenada Y
     * @return booleà, true si és un píxel, false si no
     */
    public boolean esUnPixelPesa(int coordX, int coordY) {
        boolean esUnPixel = false;

        for (int i = 0; i < this.pixels.length; i++) {
            if (this.pixels[i].getRect().contains(coordX, coordY)) {
                esUnPixel = true;
            }
        }

        return esUnPixel;
    }

    /**
     * Calcula la distància relativa entre cada un dels píxels i el punt on s'ha
     * produït l'esdeveniment, permet mantenir un moviment suau de la peça durant
     * l'arrossegament
     *
     * @param coordClickX Coordenada X
     * @param coordClickY Coordenada Y
     */
    public void setDistRelativa(int coordClickX, int coordClickY) {
        distRelativaX[0] = (int) (this.pixels[0].getRect().getX() - coordClickX);
        distRelativaY[0] = (int) (this.pixels[0].getRect().getY() - coordClickY);
        System.out.println("Pixel[" + 0 + "] distRelativaX:" + distRelativaX[0] + "  distRelativaY:" + distRelativaY[0]);
    }

    /**
     * Esborra la distància relativa calculada prèviament de cada un dels píxels
     */
    public void clearDistRelativa() {
        distRelativaX = new int[NUMPIXELS];
        distRelativaY = new int[NUMPIXELS];
        System.out.println("DistRelativa arrays cleared");
        System.out.println("distRelativaX: " + distRelativaX[0] + "  distRelativaY: " + distRelativaY[0]);
    }

    /**
     * Posa la peça a una posició concreta, aplicant els ajustaments de la distància relativa
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    public void setPesaPos(int x, int y) {
        for (int i = 0; i < this.pixels.length; i++) {
            this.pixels[i].setPosRect(x + distRelativaX[0], y + distRelativaY[0]);
        }
    }

    /**
     * Posa la peça Z a una posició concreta, aplicant els ajustaments
     * de la distància relativa i el factor de correció Z
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    public void setPesaPosDragZ(int x, int y) {
        for (int i = 0; i < NUMPIXELS; i++) {
            this.pixels[i].setPosRect(x + distRelativaX[0] + factorZ[0][numRot], y + distRelativaY[0] + factorZ[1][numRot]);
        }
    }

    /**
     * Assigna un identificador del tipus de peça a la peça en qüestió
     * a partir d'un enter passat per paràmetre
     *
     * @param numPesa Tipus de pesa predefinida
     */
    public void setPesaID(int numPesa) {
        this.numID = numPesa;
    }

    /**
     * Retorna el tipus de peça, permet identificar de quina peça predefinida es tracta
     *
     * @return numID Identificador de la peça
     */
    public int getPesaID() {
        return this.numID;
    }

    /**
     * Posa l'estat de rotació al valor passat per paràmetre
     *
     * @param rot Valor nou de rotació
     */
    public void setNumRot(int rot) {
        this.numRot = rot;
    }

    /**
     * Retorna l'estat de rotació de la peça
     *
     * @return numRot Estat de rotació
     */
    public int getNumRot() {
        return this.numRot;
    }

    /**
     * Retorna l'amplària de píxel de la peça
     *
     * @return pixelWidth Amplària dels píxels
     */
    public int getPixelWidth() {
        return pixelWidth;
    }

    /**
     * Retorna la posició d'un píxel de la peça, especificat per paràmetre
     *
     * @param numPixel Nombre de píxel a mirar
     * @return aux Objecte Point que conté les coordenades x i y del píxel
     */
    public Point getPixelPos(int numPixel) {

        if (numPixel > NUMPIXELS) {
            return null;
        }
        Point aux = new Point();
        aux.x = (int) this.pixels[numPixel].getRect().getX();
        aux.y = (int) this.pixels[numPixel].getRect().getY();

        return aux;
    }

    /**
     * Retorna el valor X utilitzat durant el canvi de base al píxel central
     * per a que a l'hora de calcular la posició de la pesa aquest es pugui
     * compensar correctament
     *
     * @return compX Valor de compensació de l'eix X
     */
    public int getCompX() {
        return compX;
    }

    /**
     * Retorna el valor Y utilitzat durant el canvi de base al píxel central
     * per a que a l'hora de calcular la posició de la pesa aquest es pugui
     * compensar correctament
     *
     * @return compY Valor de compensació de l'eix Y
     */
    public int getCompY() {
        return compY;
    }

    /**
     * Retorna l'àrea que ocupa tota la peça
     *
     * @return Rectangle Àrea que ocupa la peça
     */
    public Rectangle getPesaBounds() {

        Rectangle.Double rect = new Rectangle2D.Double(this.getPesaPosX(), this.getPesaPosY(), this.getPesaWidth() * this.getPixelWidth(), this.getPesaHeight() * this.getPixelWidth());

        return rect.getBounds();
    }

    /**
     * Retorna la posició X de la peça
     * (Cantó inferior esquerre)
     *
     * @return pXpesa Coordenada X de la peça
     */
    public int getPesaPosX() {

        int pXPesa = (int) this.pixels[0].getRect().getX();

        return pXPesa;
    }

    /**
     * Retorna la posició Y de la peça
     * (Cantó inferior esquerre)
     *
     * @return pYpesa Coordenada Y de la peça
     */
    public int getPesaPosY() {

        int pYPesa = (int) this.pixels[0].getRect().getY();

        return pYPesa;
    }

    /**
     * Retorna el valor de l'amplària de la peça,
     *
     * El mètode mira quants de píxels tenen altura menor a 0,
     * per cada píxel amb altura menor a 0 el comptador decrementa
     *
     * resulta útil per a realitzar càlculs de posicionament
     *
     * @return pWidth Valor d'amplària total
     */
    public int getPesaWidth() {
        int pWidth = 4; //inicialment s'assumeix que és un pal (pWidth = 4)
        double pixelPosY;

        for (int i = 0; (i < this.pixels.length) && (pWidth == 4); i++) {
            pixelPosY = this.pixels[i].getRect().getY();
            if (pixelPosY < 0) {
                pWidth--;
            }
        }
        return pWidth;
    }

    /**
     * Retorna el valor de l'alçada de la peça,
     *
     * El mètode mira si hi ha algun píxel que estigui més adalt de la posició
     * base de la pesa, p.ex un pal té posY 0 per tant l'alçada en caselles = 1
     *
     * Resulta útil per a realitzar càlculs de posicionament
     *
     * @return pHeight Valor de l'alçada
     */
    public int getPesaHeight() {
        int pHeight = 1;
        double pixelPosY;

        for (int i = 0; (i < this.pixels.length) && (pHeight == 1); i++) {
            pixelPosY = this.pixels[i].getRect().getY();
            if (pixelPosY < 0) {
                pHeight = 2;
            }
        }

        return pHeight;
    }

    /**
     * Retorna la textura associada a la peça
     *
     * @return Image textura de la peça
     */
    public Image getTextura() {
        return textures[numID];
    }

    /**
     * Mètode intern que pinta els components indicats, aquest mètode s'executa
     * quan inicia el programa o quan és cridat per el mètode repaint()
     *
     * @param g Objecte Graphics
     */
    public void paintComponent(Graphics g) {
        //Si la peça té l'atribut visible a cert, es pinta
        if (this.visible) {
            for (int i = 0; i < this.pixels.length; i++) {
                this.pixels[i].paintComponent(g);
            }
        }
    }

/////////////////////////// Mètodes Estàtics ///////////////////////////
    /**
     * Mètode que genera una peça determinada, a una posició i un estat
     * de visibilitat concrets
     *
     * @param num Nombre de peça predefinida
     * @param posX Coordenada X
     * @param posY Coordenada Y
     * @param visible booleà que indica si la peça ha de ser visible o no
     * @return tmpPesa Pesa generada
     */
    public static Pesa generarPesa(int num, int posX, int posY, boolean visible) {
        Casella[] pxls = new Casella[NUMPIXELS];

        //converteix una nova Pesa a una de les peces predefinides, triada per paràmetre
        for (int i = 0; i < NUMPIXELS; i++) {
            pxls[i] = peces[num][i];
            //pxls[i].setPosRect(posX, posY);
        }
        //Crea una nova pesa a partir de l'array de pixels (caselles) que la formen
        Pesa tmpPesa = new Pesa(pxls, visible);
        tmpPesa.setPesaID(num);

        tmpPesa.setPesaPos(posX, posY);

        compX = 0;
        compY = 0;

        return tmpPesa;
    }

    /**
     * Mètode que inicialitza l'array de peces (array bidimensional de píxels)
     * a partir d'un altre array que indica la disposició de les peçes predefinides;
     *
     * Aquest array conté la distància relativa al primer píxel esquerre de la peça
     * de cada un dels píxels, sobre un tamany de píxel i una posició inicial
     * passats per paràmetre
     *
     * @param COSTAT Tamany del píxel
     * @param POSICIO_INICIAL_X Coordenada inicial X
     * @param POSICIO_INICIAL_Y Coordenada inicial Y
     */
    public static void initPredefinedPeces(int COSTAT, int POSICIO_INICIAL_X, int POSICIO_INICIAL_Y) {
        posXPesa = POSICIO_INICIAL_X;
        posYPesa = POSICIO_INICIAL_Y;
        pixelWidth = COSTAT;
        Rectangle2D.Double rect;

        //distància relativa respecte del primer píxel de cada un dels píxels [x][y]
        int[][][] disposPesaxy = {
            /*              { {f1C1, f1C2} ,      {f2C1, f2C2}}     */
            /*Peça1 I*/{{0, COSTAT, COSTAT * 2, COSTAT * 3}, {0, 0, 0, 0}},
            /*Peça2 J*/ {{0, 0, COSTAT, COSTAT * 2}, {0, -COSTAT, 0, 0}},
            /*Peça3 L*/ {{0, COSTAT, COSTAT * 2, COSTAT * 2}, {0, 0, 0, -COSTAT}},
            /*Peça4 O*/ {{0, 0, COSTAT, COSTAT}, {0, -COSTAT, -COSTAT, 0}},
            /*Peça5 S*/ {{0, COSTAT, COSTAT, COSTAT * 2}, {0, 0, -COSTAT, -COSTAT}},
            /*Peça6 T*/ {{0, COSTAT, COSTAT, COSTAT * 2}, {0, 0, -COSTAT, 0}},
            /*Peça7 Z*/ {{0, COSTAT, COSTAT, COSTAT * 2}, {-COSTAT, -COSTAT, 0, 0}}};

        pesaPosxy = disposPesaxy;

        
        /*La majoria de les peces tenen com a base de referencia el píxel esquerre inferior
        La excepció és la peça Z, ja que és l'única que no segueix la base de referència
        al no tenir píxel dret inferior s'agafa el píxel dret superior, és per això 
        que al moment d'establir la seva posició se li dona un tracte diferent*/
             
        //Procura i corregeix el desplaçament de la peça Z
        int[][] procZ = {{0, -pixelWidth, 0, pixelWidth}, {pixelWidth, 0, -pixelWidth, 0}};
        factorZ = procZ;

        //Posa a cada fila (a cada 'peça') els seus píxels concrets
        for (int i = 0; i < NUMPECES; i++) {
            for (int j = 0; j < NUMPIXELS; j++) {
                rect = new Rectangle2D.Double(pesaPosxy[i][0][j], pesaPosxy[i][1][j], COSTAT, COSTAT);
                peces[i][j] = new Casella(rect, textures[i]);
            }
        }

    }

    /**
     * Inicialitza l'array de textures a partir de l'array amb els directoris
     */
    public static void initTextures() {
        for (int i = 0; i < dirTextures.length; i++) {
            try {
                textures[i] = ImageIO.read(new File(dirTextures[i]));
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

}
