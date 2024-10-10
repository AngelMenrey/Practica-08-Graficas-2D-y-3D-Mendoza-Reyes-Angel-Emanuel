import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Practica08MendozaReyesAngelEmanuel extends JFrame {
    public Practica08MendozaReyesAngelEmanuel() {
        configurarVentana();
        establecerCursorPersonalizado();
        agregarPanelPersonalizado();
    }

    private void configurarVentana() {
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon("topo.png").getImage());
    }

    private void establecerCursorPersonalizado() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ImageIcon iconoCursor = new ImageIcon("mazo.png");
        Cursor cursorPersonalizado = toolkit.createCustomCursor(iconoCursor.getImage(), new Point(0, 0), "Cursor Personalizado");
        setCursor(cursorPersonalizado);
    }

    private void agregarPanelPersonalizado() {
        PanelPersonalizado panel = new PanelPersonalizado();
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Practica08MendozaReyesAngelEmanuel frame = new Practica08MendozaReyesAngelEmanuel();
            frame.setVisible(true);
        });
    }
}

class PanelPersonalizado extends JPanel {
    private Image imagenArbol;
    private Image imagenTopo;
    private BufferedImage imagenFondo;
    private Random aleatorio;
    private int[][] posicionesHoyos;
    private int indiceHoyoActual;
    private int contadorGolpes;
    private boolean mostrarMensajeGolpe;

    public PanelPersonalizado() {
        inicializarVariables();
        crearImagenFondo();
        iniciarTemporizador();
        agregarManejadorDeRaton();
    }

    private void inicializarVariables() {
        imagenArbol = new ImageIcon("arbol.png").getImage();
        imagenTopo = new ImageIcon("topo2.png").getImage();
        aleatorio = new Random();
        posicionesHoyos = new int[6][2];
        indiceHoyoActual = -1;
        contadorGolpes = 0;
        mostrarMensajeGolpe = false;
    }

    private void crearImagenFondo() {
        int ancho = 800;
        int alto = 600;
        imagenFondo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagenFondo.createGraphics();

        dibujarCielo(g2d, ancho, (int) (alto * 0.6));
        dibujarPasto(g2d, ancho, alto);
        dibujarNubes(g2d, ancho, alto);
        dibujarArboles(g2d, ancho, alto);
        dibujarHoyos(g2d, ancho, alto);

        g2d.dispose();
    }

    private void iniciarTemporizador() {
        Timer temporizador = new Timer(1000, e -> moverTopo());
        temporizador.start();
    }

    private void agregarManejadorDeRaton() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                verificarGolpe(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(imagenFondo, 0, 0, null);

        if (indiceHoyoActual != -1) {
            int topoX = posicionesHoyos[indiceHoyoActual][0];
            int topoY = posicionesHoyos[indiceHoyoActual][1];
            g2d.drawImage(imagenTopo, topoX, topoY, 50, 50, this);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("Golpes: " + contadorGolpes, getWidth() - 100, 20);

        if (mostrarMensajeGolpe) {
            g2d.setColor(Color.RED);
            g2d.drawString("¡Golpe!", posicionesHoyos[indiceHoyoActual][0], posicionesHoyos[indiceHoyoActual][1] - 10);
            mostrarMensajeGolpe = false;
        }
    }

    private void dibujarCielo(Graphics2D g2d, int ancho, int alto) {
        for (int y = 0; y < alto; y += 10) {
            for (int x = 0; x < ancho; x += 10) {
                int valorAzul = 200 + (int) (Math.random() * 55);
                g2d.setColor(new Color(135, 206, valorAzul));
                g2d.fillRect(x, y, 10, 10);
            }
        }
    }

    private void dibujarPasto(Graphics2D g2d, int ancho, int alto) {
        for (int y = (int) (alto * 0.6); y < alto; y += 10) {
            for (int x = 0; x < ancho; x += 10) {
                g2d.setColor(new Color(0, 100 + (int) (Math.random() * 50), 0));
                g2d.fillRect(x, y, 10, 10);
            }
        }
    }

    private void dibujarNubes(Graphics2D g2d, int ancho, int alto) {
        int cantidadNubes = 5;
        int espacioNubes = ancho / (cantidadNubes + 1);
        boolean zigzag = false;
        for (int i = 1; i <= cantidadNubes; i++) {
            int x = i * espacioNubes - 30;
            int y = zigzag ? (int) (Math.random() * (alto / 8)) : (int) (Math.random() * (alto / 6));
            zigzag = !zigzag;
            dibujarNube(g2d, x, y);
        }
    }

    private void dibujarNube(Graphics2D g2d, int x, int y) {
        int[][] partesNube = {
            {0, 0, 50, 30},
            {40, 10, 60, 40},
            {80, 0, 50, 30},
            {20, 20, 60, 40},
            {60, 20, 50, 30}
        };

        for (int[] parte : partesNube) {
            for (int py = 0; py < parte[3]; py += 5) {
                for (int px = 0; px < parte[2]; px += 5) {
                    int valorGris = 200 + (int) (Math.random() * 55);
                    g2d.setColor(new Color(valorGris, valorGris, valorGris));
                    g2d.fillRect(x + parte[0] + px, y + parte[1] + py, 5, 5);
                }
            }
        }
    }

    private void dibujarArboles(Graphics2D g2d, int ancho, int alto) {
        int cantidadArboles = 3;
        int espacioArboles = ancho / (cantidadArboles + 1);
        int yArbol = (int) ((alto * 0.85) - (imagenArbol.getHeight(null) / 3.3));
        int anchoArbol = imagenArbol.getWidth(null) / 6;
        int altoArbol = imagenArbol.getHeight(null) / 5;

        for (int i = 1; i <= cantidadArboles; i++) {
            int x = i * espacioArboles - anchoArbol / 2;
            g2d.drawImage(imagenArbol, x, yArbol, anchoArbol, altoArbol, null);
        }
    }

    private void dibujarHoyos(Graphics2D g2d, int ancho, int alto) {
        int cantidadHoyos = 6;
        int diametroHoyo = 50;
        int inicioPastoY = (int) (alto * 0.6);
        int finPastoY = alto - diametroHoyo;
        int inicioPastoX = diametroHoyo / 2;
        int finPastoX = ancho - diametroHoyo;

        int[][] posicionesFijas = {
            {100, inicioPastoY + 50},
            {250, inicioPastoY + 100},
            {400, inicioPastoY + 50},
            {550, inicioPastoY + 100},
            {700, inicioPastoY + 50},
            {325, inicioPastoY + 150}
        };

        for (int i = 0; i < cantidadHoyos; i++) {
            int x = posicionesFijas[i][0];
            int y = posicionesFijas[i][1];

            posicionesHoyos[i][0] = x;
            posicionesHoyos[i][1] = y;

            g2d.setColor(new Color(100, 50, 0));
            g2d.fillOval(x, y, diametroHoyo, diametroHoyo);

            for (int py = 0; py < diametroHoyo; py += 5) {
                for (int px = 0; px < diametroHoyo; px += 5) {
                    int dx = x + px;
                    int dy = y + py;
                    if (Math.pow(dx - (x + diametroHoyo / 2), 2) + Math.pow(dy - (y + diametroHoyo / 2), 2) <= Math.pow(diametroHoyo / 2, 2)) {
                        int valorMarron = 100 + (int) (Math.random() * 50);
                        g2d.setColor(new Color(valorMarron, 50, 0));
                        g2d.fillRect(dx, dy, 5, 5);
                    }
                }
            }
        }
    }

    private void verificarGolpe(int mouseX, int mouseY) {
        if (indiceHoyoActual != -1) {
            int topoX = posicionesHoyos[indiceHoyoActual][0];
            int topoY = posicionesHoyos[indiceHoyoActual][1];
            if (mouseX >= topoX && mouseX <= topoX + 50 && mouseY >= topoY && mouseY <= topoY + 50) {
                contadorGolpes++;
                mostrarMensajeGolpe = true;
                reproducirSonido("pow.wav");
                repaint();
                if (contadorGolpes >= 3) {
                    mostrarDialogoGanador();
                }
            }
        }
    }

    private void reproducirSonido(String archivoSonido) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(archivoSonido).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.out.println("Error al reproducir el sonido.");
            ex.printStackTrace();
        }
    }

    private void mostrarDialogoGanador() {
        int opcion = JOptionPane.showOptionDialog(this, "¡Has ganado! ¿Qué quieres hacer?", "Juego Terminado",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Reiniciar", "Salir"}, "Reiniciar");

        if (opcion == JOptionPane.YES_OPTION) {
            reiniciarJuego();
        } else if (opcion == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    private void reiniciarJuego() {
        contadorGolpes = 0;
        indiceHoyoActual = -1;
        repaint();
    }

    private void moverTopo() {
        indiceHoyoActual = aleatorio.nextInt(posicionesHoyos.length);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}