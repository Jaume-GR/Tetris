package TetrisV0;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import javax.swing.*;

/**
 * La classe del joc Tetris s'encarrega del correcte funcionament de tot el joc i
 * Controla el tauler de caselles, les peces i els botons i indicadors;
 *
 * Aquesta implementa diversos escoltadors i respòn davant diferents inputs
 * del jugador com pot ser l'arrossegament d'una peça o la rotació de la mateixa
 *
 * El JPanel TetrisGame està organitzat amb un GridBagLayout.
 *
 * @author JGalmés
 */
public class TetrisGame extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    private Pesa pesaActual;
    private final Tauler tauler;
    private JPanel panellBotonsLaterals;
    private JButton boto1PL;
    private JButton boto2PL;
    private JPanel scorePanel;
    private JPanel timerPanel;
    public static int MARGE = 50;
    private boolean INSERIT;
    private static final int POSICIO_INICIAL_X = 290;
    private static final int POSICIO_INICIAL_Y = 61;
    private boolean ARROSSEGANT = false;

    /**
     * Constructor de la classe TetrisGame
     * Inicialitza el GridBagLayout, el tauler i les peces
     */
    public TetrisGame() {
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.DARK_GRAY);

        //tauler = new Tauler(300, 400); //la dimensioY ha de ser el doble que la dimensióX
        tauler = new Tauler(400, 500); //la dimensioY ha de ser el doble que la dimensióX

        //Inicialitza i dona forma a tots els components dins el GridBagLayout
        this.initComponents();

        //en aquest ordre
        Pesa.initTextures();
        Pesa.initPredefinedPeces(tauler.getTamanyCaselles(), POSICIO_INICIAL_X, POSICIO_INICIAL_Y);

        //hacer metodo start(), debe tener estos comandos para generar la primera pieza:
        pesaActual = this.generarPesaAleatoria(true);
        tauler.pesaActual = pesaActual;
    }

    public boolean timer() {//METODO QUE DEBE GENERAR UN CONTADOR Y DEVOLVER TRUE CUANDO HAYA TERMINADO
        //el timer debe modificar un componente mientras pasa el tiempo, como una barra o un contador numérico
        return false;
    }

    /*
    Esto es del ajedrez, muestra una pantalla flotante al cumplirse cierta condicion, pued servir como referencia
                    if (this.getNumReines() == TetrisGame.MAX_REINES) {
                    Toolkit.getDefaultToolkit().beep();
                    Icon icon = new ImageIcon("peces/reinaN.png");
                    int act = JOptionPane.showConfirmDialog(this, new JLabel("Has col·locat 8 Reines al tauler!"), "Victoria!", JOptionPane.DEFAULT_OPTION, 0, icon);
                    if ((act == JOptionPane.OK_OPTION) || (act == JOptionPane.CLOSED_OPTION)) {
                        System.exit(0);
                    }    
     */
    /**
     * esta clase se terminará eliminando
     *
     * @param e MouseEvent
     * @deprecated clickedComponent
     */
    @Deprecated
    public void clickedComponent(MouseEvent e) {
        if (e.getSource() == tauler) {
            int posX = e.getX();
            int posY = e.getY();

            System.out.println("\nHas clicat dins el JPanel de Tauler & Subtauler");
            System.out.printf("posX:%d  posY:%d\n", posX, posY);
        }
    }

    /**
     * Mètode que genera un nombre aleatori entre 0 i el nombre total de peces,
     * crida al generador amb el nombre de pesa a generar;
     * i finalment retorna la pesa generada
     *
     * @return Pesa generada
     */
    private Pesa generarPesaAleatoria(boolean visible) {
        Random rand = new Random();

        int numPesaAleatori = rand.nextInt(Pesa.NUMPECES);

        if (pesaActual != null) {
            while (numPesaAleatori == pesaActual.getPesaID()) { //Evita que se genere la misma forma que ya está presente
                numPesaAleatori = rand.nextInt(Pesa.NUMPECES);
            }
        }

        return Pesa.generarPesa(numPesaAleatori, POSICIO_INICIAL_X, POSICIO_INICIAL_Y, visible);
    }

    /**
     * Mètode que respòn davant un esdeveniment de botó de ratoli pressionat
     *
     * @param e Esdeveniment de tipus MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {

        int evtPosX = e.getX();
        int evtPosY = e.getY();

        //Mira si encara no s'ha iniciat un arrossegament i si s'ha pressionat sobre una pesa
        if ((!ARROSSEGANT) && this.pesaActual.esUnPixelPesa(evtPosX, evtPosY)) {
            ARROSSEGANT = true;

            //Calcula la distància relativa de cada un dels píxels al lloc de l'esdeveniment
            this.pesaActual.setDistRelativa(evtPosX, evtPosY);
            System.out.println("\nArrossegant true (mousePressed)");

        }

    }

    /**
     * Mètode que respòn davant un esdeveniment de botó de ratoli alliberat
     *
     * @param e Esdeveniment de tipus MouseEvent
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        boolean canviPesa = false; //Marca quan una pesa s'ha colocat amb éxit al tauler 
        //Mira si s'ha estat realitzant un arrossegament i finalitza l'estat
        if (ARROSSEGANT) {
            ARROSSEGANT = false;

            //Esborra la distància relativa calculada durant l'arrossegament
            this.pesaActual.clearDistRelativa();
            System.out.println("Arrossegant false (mouseReleased)");
            System.out.println("Pesa - PosX:" + this.pesaActual.getPesaPosX() + "  PosY:" + this.pesaActual.getPesaPosY());

            //Com que s'ha alliberat la peça s'esborra la ombra sobre el tauler
            tauler.clrOmbraPesa();

            //Mira si el lloc on s'ha alliberat la pesa pertany al tauler
            if (tauler.esZonaTauler(e.getX(), e.getY())) {
                //Mira si el lloc on s'ha alliberat està disponible i buit
                if (tauler.ubiCasReady() && !tauler.ubiCasOcupat()) {
                    tauler.colocarPesa(this.pesaActual);
                    canviPesa = true;
                }
            }

            //Inserit permet identificar si ha acabat el cicle d'agafar i amollar la peça
            //Si ha acabat genera una nova peça per a agafar
            if (!INSERIT) {
                if (tauler.esZonaTauler(e.getX(), e.getY()) && (tauler.esZonaTauler(this.pesaActual)) && (canviPesa)) {
                    //this.pesaActual.setPesaPos(POSICIO_INICIAL_X + this.pesaActual.getCompX(), POSICIO_INICIAL_Y + this.pesaActual.getCompY());
                    this.pesaActual = this.generarPesaAleatoria(true);
                    tauler.pesaActual = pesaActual;
                }
                this.pesaActual.setPesaPos(POSICIO_INICIAL_X + this.pesaActual.getCompX(), POSICIO_INICIAL_Y + this.pesaActual.getCompY());
                INSERIT = true;
            }
        }
        //tauler.paintImmediately(this.pesaActual.getPesaBounds());
        repaint();
        this.clickedComponent(e);
    }

    /**
     * Mètode que respòn davant un esdeveniment de ratolí arrossegat
     *
     * @param e Esdeveniment de tipus MouseEvent
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (ARROSSEGANT) {
            INSERIT = false;

            int X = e.getX();
            int Y = e.getY();

            if (this.pesaActual.getPesaID() == Pesa.Z) { //Es tracta diferent si la peça es Z
                this.pesaActual.setPesaPosDragZ(X, Y);
            } else {
                this.pesaActual.setPesaPos(X, Y);
            }

            if (tauler.esZonaTauler(X, Y) && (tauler.esZonaTauler(this.pesaActual))) {

                //if (tauler.esZonaTauler(X + tauler.getDesfaseX(), Y + tauler.getDesfaseY())) {
                System.out.println("Es zona tauler ");

                tauler.clrOmbraPesa();
                tauler.setOmbraPesa(this.pesaActual);

            } else {
                tauler.clrOmbraPesa();
                System.out.println("No es zona tauler");
            }

            //tauler.paintImmediately(this.pesaActual.getPesaBounds());
            repaint();
            //tauler.repaint();
            System.out.println("Cursor Arrossegat");
        }
    }

    /**
     * Mètode que respòn davant un esdeveniment d'acció realitzada
     *
     * @param e Esdeveniment de tipus MouseEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        //Identifica pel nom del component de quin es tracta, 
        //posteriorment realitza l'acció corresponent
        switch (((JButton) e.getSource()).getText()) {
            case "Rotar figura":
                System.out.println("ROTAR FIGURA");
                //BAJAR PUNTUACION -1 PUNTO

                if (this.pesaActual.getPesaID() != Pesa.O) { //S'evita rotar si es tracta de la peça cuadrada
                    this.pesaActual.rotarPesa();
                    repaint();
                }
                break;
            case "Nova Figura":
                System.out.println("NOVA FIGURA");
                //BAJAR PUNTUACION -5 PUNTOS

                pesaActual = this.generarPesaAleatoria(true);
                tauler.pesaActual = pesaActual;
                repaint();
                break;
        }
    }

    /**
     * Mètode que inicialitza i disposa tots els components
     * damunt el Layout, emprant GridBagLayout com a LayoutManager
     */
    private void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; // El área de texto empieza en la columna cero.
        constraints.gridy = 0; // El área de texto empieza en la fila cero
        constraints.gridwidth = 2; // El área de texto ocupa dos columnas.
        constraints.gridheight = 2; // El área de texto ocupa 2 filas.
        constraints.weighty = 1.0;
        constraints.insets = new Insets(20, 50, 20, 0);
        this.add(tauler, constraints);
        constraints.insets = new Insets(20, 20, 20, 20);
        constraints.weighty = 0.0;

        panellBotonsLaterals = new JPanel();
        panellBotonsLaterals.setLayout(new BoxLayout(panellBotonsLaterals, BoxLayout.Y_AXIS));
        panellBotonsLaterals.setPreferredSize(new Dimension(150, 700));
        panellBotonsLaterals.setSize(new Dimension(150, 700));
        //panellBotonsLaterals.setBackground(Color.GRAY);
        panellBotonsLaterals.setBackground(new Color(0, 0, 0, 0));
//        panellBotonsLaterals.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        constraints.gridx = 2; // Columna 2. No necesita estirarse, no ponemos weightx
        constraints.gridy = 1; // Fila 1. Necesita estirarse, hay que poner weighty
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.weightx = 1;

        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;

        boto1PL = new JButton();
        boto1PL.setIcon(new ImageIcon(new ImageIcon("icons/iconoBotonRotar.jpg").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        boto1PL.setText("Rotar figura");
        boto1PL.addActionListener(this);

        panellBotonsLaterals.add(boto1PL);
        System.out.println(boto1PL.getSize());

        boto2PL = new JButton();
        boto2PL.setIcon(new ImageIcon(new ImageIcon("icons/iconoBotonNuevaForma.jpg").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
        boto2PL.setText("Nova Figura");
        boto2PL.addActionListener(this);
        panellBotonsLaterals.add(boto2PL);

        constraints.fill = GridBagConstraints.NONE;
        this.add(panellBotonsLaterals, constraints);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0;

        scorePanel = new JPanel();
        scorePanel.setBackground(Color.red);
        constraints.gridx = 0; // El área de texto empieza en la columna cero.
        constraints.gridy = 3; // El área de texto empieza en la fila cero
        constraints.gridwidth = 4; // El área de texto ocupa dos columnas.
        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.SOUTH;

        JLabel text1 = new JLabel("NAME & SCORE: ");
        JLabel text3 = new JLabel("563");
        text3.setFont(new Font("Sans Serif", 1, 36));
//        text1.setHorizontalAlignment(JLabel.CENTER);
//        text1.setVerticalAlignment((int) JLabel.CENTER_ALIGNMENT);
        text1.setFont(new Font("ARIAL", 1, 36));
        scorePanel.add(text1);
        scorePanel.add(text3);

        this.add(scorePanel, constraints);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        timerPanel = new JPanel();
        timerPanel.setBackground(Color.blue);
        constraints.gridx = 0; // El área de texto empieza en la columna cero.
        constraints.gridy = 4; // El área de texto empieza en la fila cero
        constraints.gridwidth = 4; // El área de texto ocupa dos columnas.
        constraints.gridheight = 1; // El área de texto ocupa 2 filas.
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.PAGE_END;

        JLabel text2 = new JLabel("TIMER COUNTDOWN");
//        text2.setHorizontalAlignment(JLabel.BOTTOM);
//        text2.setVerticalAlignment(JLabel.BOTTOM);
        text2.setFont(new Font("ARIAL", 1, 36));
        timerPanel.add(text2);

        this.add(timerPanel, constraints);
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.CENTER;

        //Afegeix els MouseListeners als components del tauler
        Component[] tmp2 = this.getComponents();
        System.out.printf("\nEscacs.getComponents().length = %d\n", tmp2.length);
        for (int i = 0; i < tmp2.length; i++) {
            System.out.println(tmp2[i]);
            tmp2[i].addMouseListener(this);
            tmp2[i].addMouseMotionListener(this);
        }
    }

    //MouseEvent.BUTTON1 - Click Esquerra
    //MouseEvent.BUTTON2 - Click Roda
    //MouseEvent.BUTTON3 - Click Dret
    //Si no es mira quin botó s'ha pressionat el programa accepta qualsevol botó del ratolí
    /**
     * @see MouseListener
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * @see MouseListener
     * @param e MouseEvent
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * @see MouseListener
     * @param e MouseEvent
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * @see MouseMotionListener
     * @param e MouseEvent
     */
    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
