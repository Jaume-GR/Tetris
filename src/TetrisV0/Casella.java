package TetrisV0;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Image;

/**
 * Classe Casella, tracta i gestiona un Rectangle2D com un espai ocupable,
 * Permet generar distints tipus de objectes Casella, com poden ser
 * les caselles d'un tauler o els píxels d'un objecte Peça
 *
 * @author JGalmés
 */
public class Casella {

    private Rectangle2D.Double rectangle;
    private Rectangle2D.Double recuadre;
    private Rectangle2D.Double recuContorn;
    private Color color;
    private boolean ocupat;
    private Image textura;

    private double tamCas;
    private double tamRecu;
    private double tamMarge;
    private double tamRecuContorn;
    private double tamMargeContorn;
    private double tamRecu2;

    private double relacioX;
    private double relacioY;

    /**
     * Constructor de la classe Casella, construeix un objecte Casella
     * a partir d'un Rectangle2D, un color i un boolea
     * que indica si la casella està ocupada o no
     *
     * @param rect Rectangle 2D que forma l'àrea de la casella
     * @param col Color determinat de l'area de la casella
     * @param ocup booleà que indica si està ocupada o no
     */
    public Casella(Rectangle2D.Double rect, Color col, boolean ocup) {//Per al tauler
        this.rectangle = rect;//borrar

        tamCas = rectangle.width;
        tamRecu = tamCas * 0.9;
        tamMarge = tamCas * 0.05; // es lo mateix que (tamCas - tamRecu / 2)
        tamRecuContorn = tamRecu;

        this.recuadre = new Rectangle2D.Double(rectangle.x + tamMarge, rectangle.y + tamMarge, tamRecu, tamRecu);
        this.recuContorn = null;
        this.color = col;
        this.ocupat = ocup;
        //this.pesa = null;
        this.textura = null;
    }

    /**
     * Constructor de la classe Casella, construeix un objecte Casella
     * a partir d'un Rectangle2D i un objecte Image
     * que serà pintat sobre l'àrea de la casella
     *
     * @param rect Rectangle 2D que forma l'àrea de la casella
     * @param imgTextura textura de la casella
     */
    public Casella(Rectangle2D.Double rect, Image imgTextura) {
        this.rectangle = rect;
        relacioX = rect.x;
        relacioY = rect.y;

        tamCas = rectangle.width;
        tamRecu = tamCas * 0.9;
        tamMarge = tamCas * 0.05; // és el mateix que (tamCas - tamRecu / 2)

        this.textura = imgTextura;
        this.ocupat = true;
        this.color = null;
        this.recuadre = null;
        this.recuContorn = null;
    }

    /**
     * Pinta un contorn interior dins la casella, s'utilitza per a representar
     * una casella que forma part de l'ombra de la peça
     */
    public void setContorn() {
        //El rectangle i el tamany del recuadre es copien al recuadreContorn
        this.recuContorn = this.recuadre;
        tamRecuContorn = tamRecu;

        //tamMarge = tamCas * 0.175;
        tamMargeContorn = tamCas * 0.18;
        //tamRecu2 = tamCas * 0.65;
        tamRecu2 = tamCas * 0.64;

        this.recuadre = new Rectangle2D.Double(rectangle.x + tamMargeContorn, rectangle.y + tamMargeContorn, tamRecu2, tamRecu2);
    }

    /**
     * Esborra el contorn interior de la casella, s'utilitza per a representar
     * una casella que ja no forma part de l'ombra de la peça
     */
    public void clrContorn() {
        if (this.recuContorn != null) {
            this.recuadre = new Rectangle2D.Double(rectangle.x + tamMarge, rectangle.y + tamMarge, tamRecu, tamRecu);
            tamRecu2 = tamCas * 0.64;
            this.recuContorn = null;
        }
    }

    /**
     * Mètode intern que pinta els components indicats, aquest mètode s'executa 
     * quan inicia el programa o quan és cridat per el mètode repaint()
     *
     * @param g Objecte Graphics
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(34, 34, 34));
        g2d.fill(rectangle);

        if (this.recuContorn != null) {
            g2d.setColor(new Color(102, 102, 102));
            g2d.fill(recuContorn);
        }

        if (this.recuadre != null) {
            g2d.setColor(color);
            g2d.fill(recuadre);
        }

        if (this.ocupat) {
            g2d.drawImage(textura, (int) rectangle.x, (int) rectangle.y, (int) rectangle.width, (int) rectangle.height, null);
        }
    }

    /**
     * Posa la casella en estat ocupat i li assigna un objecte Image
     *
     * @param imgTextura textura de la casella
     */
    public void setOcupada(Image imgTextura) {
        this.textura = imgTextura;
        this.ocupat = true;
    }

    /**
     * Posa la casella a una posició indicada per unes coordenades concretes,
     * el punt de referència és l'extrem superior esquerra del rectangle (pixel)
     *
     * @param x Coordenada X,
     * @param y Coordenada Y
     */
    public void setPosRect(int x, int y) {
        this.rectangle.x = relacioX + x;
        this.rectangle.y = relacioY + y;
    }

    /**
     * Retorna l'objecte Rectangle que conforma la casella
     * 
     * @return Rectangle2D Àrea de la casella
     */
    public Rectangle2D getRect() {
        return rectangle;
    }

    /**
     * Retorna el valor de ocupat de la casella
     * 
     * @return booleà, true si està ocupat, false si no
     */
    public boolean isOcupada() {
        return this.ocupat;
    }

    /**
     * Posa la casella com a no ocupada
     */
    public void buidar() {
        this.ocupat = false;
    }
}
