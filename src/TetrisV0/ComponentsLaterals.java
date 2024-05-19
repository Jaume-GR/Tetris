package TetrisV0;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Classe que conté les barres laterals del JFrame com poden ser la Barra de navegació,
 * la barra d'eines (Toolbar) o el Menú (MenuBar), implementa els components
 * i els hi assigna el seu respectiu listener,
 * els esdeveniments dels components es gestionen a la classe principal
 *
 * @author JGalmés
 */
public class ComponentsLaterals {

    private final ActionListener actListener;
    private JPanel barraLateral; //JPanel on van els botons de l'esquerra   
    private final JButton[] botons = new JButton[5];
    private final String[] nomBotons = {"NOVA PARTIDA", "CONFIGURACIÓ", "HISTORIAL", "INFORMACIÓ", "SORTIR"};

    //Llista els IDs de les diferentes funcions que duen a terme el components
    private final String[] IDs = {"NPAR", "CONF", "HIST", "INFO", "EXIT"};

    private JToolBar toolBar;
    private final String[] nomIcones = {"iconoNuevaPartida.jpg", "iconoConfiguracion.jpg", "iconoHistorial.jpg", "iconoInformacion.jpg", "iconoSalir.jpg"};
    private final String[] toolTipTextTB = {"Inicia una nova partida", "Accedeix a l'apartat de configuració", "Accedeix a l'apartat de l'historial", "Apartat Informació", "Sortir"};
    private final JButton[] botonsTB = new JButton[nomIcones.length];

    private JMenuBar menuBar;
    private JMenu menu1;
    private final JMenuItem[] menuItems = new JMenuItem[2];
    private final String[] nomMenuItems = {"Nova Partida", "Sortir"};

    /**
     * Constructor ComponentsLaterals
     * Execua els mètodes que inicialitzen
     * els components passant l'ActionListener per paràmetre
     * 
     * @param actLis Listener que rebrà els esdeveniments
     */
    public ComponentsLaterals(ActionListener actLis) {
        //this.add(Box.createVerticalGlue(), constraints);
        //this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        //this.setForeground(Color.GRAY);
        actListener = actLis;
        this.initMenuBar(actListener);
        this.initToolBar(actListener);
        this.initBarraLateral(actListener);

    }

    /**
     * Inicialitza el JPanel barraLateral i els botons del mateix,
     * Assigna l'escoltador passat per paràmetre als botons
     *
     * @param actListener Listener que rebrà els esdeveniments
     */
    private void initBarraLateral(ActionListener actListener) {
        barraLateral = new JPanel();

        barraLateral.setBackground(Color.GRAY);

        barraLateral.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;

        for (int i = 0; i < botons.length; i++) {
            botons[i] = new JButton(nomBotons[i]);
            botons[i].addActionListener(actListener);
            botons[i].setName(IDs[i]);
            System.out.printf("botons[%d].getName(): '%s'\n", i, botons[i].getName());
            constraints.gridy = i;
            barraLateral.add(botons[i], constraints);
        }
    }

    /**
     * Inicialitza el toolBar i els botons del mateix,
     * Assigna l'escoltador passat per paràmetre als botons
     *
     * @param actList Listener que rebrà els esdeveniments
     */
    private void initToolBar(ActionListener actList) {
        toolBar = new JToolBar();

        for (int i = 0; i < botonsTB.length; i++) {
            botonsTB[i] = new JButton(new ImageIcon("icons/" + nomIcones[i]));
            botonsTB[i].setToolTipText(toolTipTextTB[i]);
            botonsTB[i].setName(IDs[i]);
            System.out.printf("botonsTB[%d].getName(): '%s'\n", i, botonsTB[i].getName());
            botonsTB[i].addActionListener(actList);
            toolBar.add(botonsTB[i]);
        }

    }

    /**
     * Inicialitza el menuBar, els menús i els menuItems del mateix,
     * Assigna l'escoltador passat per paràmetre als menuItems
     *
     * @param actList Listener que rebrà els esdeveniments
     */
    private void initMenuBar(ActionListener actList) {
        menuBar = new JMenuBar();
        menu1 = new JMenu("Menú");
        int inc = IDs.length - 1;
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i] = new JMenuItem(nomMenuItems[i]);
            menuItems[i].addActionListener(actList);
            menuItems[i].setName(IDs[i * inc]);
            System.out.printf("menuItems[%d].getName(): '%s'\n", i, menuItems[i].getName());
            menu1.add(menuItems[i]);
        }

        //menuItem2.setEnabled(true); //setEnabled() Només posa el text en gris, no desactiva cap listener que tengui associat el boto
        menuBar.add(menu1);
    }

    /**
     * Retorna l'array de ID's predefinit
     *
     * @return Array amb els noms predefinits per als components
     */
    public String[] getIDs() {
        return IDs;
    }

    /**
     * Retorna el JToolbar inicialitzat i amb tot preparat
     *
     * @return component JToolbar
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Retorna el JPanel inicialitzat i amb tot preparat
     *
     * @return component JPanel
     */
    public JPanel getBarraLateral() {
        return barraLateral;
    }

    /**
     * Retorna el JMenuBar inicialitzat i amb tot preparat
     *
     * @return component JMenuBar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Retorna els JMenuItems del menuBar,
     * inicialitzats i amb tot preparat
     *
     * @return Array de tots els JMenuItems
     */
    public JMenuItem[] getMenuItems() {
        return menuItems;
    }

}
