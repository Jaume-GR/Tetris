package TetrisV0;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Aquesta classe es la principal del projecte i s'encarrega de gestionar el
 * JFrame i el layout dels components que conté, addicionalment
 * també inicialitza la classe del Joc Tetris
 *
 * Com a JFrame aquest disposa d'un menuBar, un JToolbar i un panell de botons
 * laterals i per tant també s'encarrega de gestionar 
 * els esdeveniments que aquests generen.
 *
 * @author Jaume
 */
public class TetrisV0 extends JFrame implements ActionListener {

    private final static String PANTALLA_INICI = "Pantalla Inicial";
    private final static String PANTALLA_JOC = "Pantalla de Joc";
    private final static String PANTALLA_HISTORIAL = "Pantalla de Historial";
    private final static String PANTALLA_INFORMACIO = "Pantalla de Informacio";

    public final static Dimension RESOLUCION_VENTANA = new Dimension(800, 750); //1280 x 720, 854 x 480

    private final JPanel pantalles;
    private final JPanel pantallaInici;
    private final TetrisGame tetris;

    private final ComponentsLaterals componentsLaterals;
    private final JPanel barraLatEsq;
    private final JToolBar toolBar;
    private final JMenuBar menuBar;

    /**
     * Mètode de la classe principal, inicialitza els components del JFrame
     */
    public TetrisV0() {
        this.setTitle("Tetris V0");
        this.setLayout(new BorderLayout());
        this.setPreferredSize(RESOLUCION_VENTANA);
        //this.setMinimumSize(RESOLUCION_VENTANA);

        //Els components s'inicialitzen a la classe ComponentsLaterals 
        //i posteriorment es retornen a la classe principal
        componentsLaterals = new ComponentsLaterals(this);
        menuBar = componentsLaterals.getMenuBar();
        this.setJMenuBar(menuBar);

        toolBar = componentsLaterals.getToolBar();
        this.getContentPane().add(toolBar, BorderLayout.NORTH);

        barraLatEsq = componentsLaterals.getBarraLateral();
        this.getContentPane().add(barraLatEsq, BorderLayout.WEST);

        //Es crea un JPanel que treballarà com a CardLayout entre pantalles
        pantalles = new JPanel();
        //pantalles.setPreferredSize(RESOLUCION_VENTANA);
        pantalles.setLayout(new CardLayout());
        this.getContentPane().add(pantalles, BorderLayout.CENTER);

        //Pantalla que aparece al inicio del programa (portada)
        pantallaInici = new JPanel();
        pantallaInici.setPreferredSize(new Dimension(800, 750));
        pantallaInici.setBackground(Color.DARK_GRAY);
        pantalles.add(pantallaInici, "Pantalla Inicial");
        this.pack();

        //Redimensiona la imatge de la portada per a cobrir el tamany del JPanel pantalles
        ImageIcon imgPortada = new ImageIcon(new ImageIcon("media/TETRIS_UIB.jpg").getImage().getScaledInstance(pantalles.getWidth(), pantalles.getHeight(), Image.SCALE_SMOOTH));
        JLabel fons = new JLabel(imgPortada);
        fons.setAlignmentX(CENTER_ALIGNMENT);
        fons.setAlignmentY(TOP_ALIGNMENT);
        pantallaInici.add(fons);

        //De primeres mostra la pantalla de inici
        ((CardLayout) pantalles.getLayout()).show(pantalles, PANTALLA_INICI); 

        tetris = new TetrisGame(); //Inicialitza la classe Tetris

        this.setSize(this.getPreferredSize());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    public static void main(String[] args) {
        TetrisV0 tetrisV0 = new TetrisV0();
        tetrisV0.setVisible(true);
    }
    

    /**
     * Mètode que respòn davant un esdeveniment d'acció realitzada
     * 
     * @param e Esdeveniment de tipus MouseEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //Mirar si ha acabat el temps del timer o no 
        if (!tetris.timer()) {//ESTO DE .timer debe de implementarse de forma que no solo se compruebe al hacer un actionPerfomed sino constantemente

            //S'obté el nom del component que ha generat l'esdeveniment i s'identifica amb un switch
            String componentID = ((JComponent) e.getSource()).getName(); 
            switch (componentID) {         
                case "NPAR":
                    System.out.println("NPAR");

                    pantalles.add(tetris, "Pantalla de Joc");

                    ((CardLayout) pantalles.getLayout()).show(pantalles, PANTALLA_JOC);
                    break;
                case "CONF":
                    System.out.println("CONF");
                    //SE EJECUTA VENTANA DE CONFIGURACIÓN
                    //DEBE SER UNA VENTANA FLOTANTE, QUIZÁS JOPTIONPANE, 
                    //NO HAY QUE MODIFICAR LOS LAYOUTS EXISTENTES

                    break;
                case "HIST":
                    //SE EJECUTA PANTALLA DE HISTORIAL
                    System.out.println("HIST");

                    JPanel pantallaHistorial = new JPanel();
                    pantallaHistorial.setBackground(Color.BLACK);
                    pantalles.add(pantallaHistorial, "Pantalla de Historial");
                    ((CardLayout) pantalles.getLayout()).show(pantalles, PANTALLA_HISTORIAL);
                    break;
                case "INFO":
                    //SE EJECUTA PANTALLA DE INFORMACION
                    System.out.println("INFO");

                    JPanel pantallaInformacio = new JPanel();
                    pantallaInformacio.setBackground(Color.DARK_GRAY);
                    pantalles.add(pantallaInformacio, "Pantalla de Informacio");
                    ((CardLayout) pantalles.getLayout()).show(pantalles, PANTALLA_INFORMACIO);
                    break;
                case "EXIT":
                    System.out.println("EXIT");
                    ((CardLayout) pantalles.getLayout()).show(pantalles, PANTALLA_INICI);
                    break;
            }
        }
    }
}
