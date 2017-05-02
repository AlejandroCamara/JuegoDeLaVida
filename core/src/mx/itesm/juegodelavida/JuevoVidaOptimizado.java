package mx.itesm.juegodelavida;

/**
 * Created by Alejandro on 24/04/2017.
 */


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;


class JuegoVidaOptimizado extends Pantalla
{
    // Dimensiones del mundo
    private final int COLUMNAS = 64;
    private final int RENGLONES = 40;
    private final int TAMANO_CELDA = 20;    // 64x20 = 1280, 40x20 = 800
    private final Principal juego;

    // Matriz para representar el mundo
    private Estado[][] mundo;
    private Estado[][] temporal;    // Calcula siguiente generación

    // Texturas
    private Texture celdaViva;
    private Texture celdaMuerta;

    public JuegoVidaOptimizado(Principal principal) {
        this.juego = principal;
    }

    @Override
    public void show() {
        generarMundo();
        celdaViva = crearCelda(Estado.VIVA);
        celdaMuerta = crearCelda(Estado.MUERTA);
    }

    private void generarMundo() {
        // El mundo
        mundo = new Estado[RENGLONES][COLUMNAS];
        // Inicializa el mundo de manera aleatoria
        for (int i=0; i<mundo.length; i++){
            for (int j=0; j<mundo[i].length; j++) {
                if (i==0 || j==0 || i==mundo.length-1 || j==mundo[i].length-1) {
                    mundo[i][j] = Estado.MUERTA;    // en el marco no hay vidas
                } else {
                    Estado estado = Estado.MUERTA;
                    if (MathUtils.random(1f) < 0.1f) {    // 10% vivas
                        estado = Estado.VIVA;
                    }
                    mundo[i][j] = estado;
                }
            }
        }
        // Mundo temporal para siguiente generación
        temporal = new Estado[RENGLONES][COLUMNAS];
    }

    @Override
    public void render(float delta) {
        borrarPantalla(0,0.2f,0);
        // Dibujar
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        dibujarMundo();
        batch.end();

        calcularSiguienteGeneracion();

        juego.logger.log();
    }

    private void calcularSiguienteGeneracion() {
        // No recorre el marco
        for (int i=1; i<mundo.length-1; i++) {
            for (int j=1; j < mundo[i].length-1; j++) {
                int vecinos = contarVecinos(i, j);
                if (mundo[i][j]==Estado.VIVA) {
                    if (vecinos==2 || vecinos==3) {
                        temporal[i][j] = Estado.VIVA;   // 1. Sobrevive
                    } else {
                        temporal[i][j] = Estado.MUERTA; // 2. Fallece
                    }
                } else {
                    if (vecinos==3) {
                        temporal[i][j] = Estado.VIVA;   // 3. Nace
                    } else {
                        temporal[i][j] = Estado.MUERTA;
                    }
                }
            }
        }
        // Copia el mundo temporal en el mundo
        long inicio = System.nanoTime();
        for (int i=0; i<mundo.length; i+=2) {
            for (int j = 0; j < mundo[i].length; j+=2) {
                mundo[i][j] = temporal[i][j];
                mundo[i+1][j+1] = temporal[i+1][j+1];
                mundo[i][j+1] = temporal[i][j+1];
                mundo[i+1][j] = temporal[i+1][j];
            }
        }
        long fin = System.nanoTime();
        Gdx.app.log("copiando","Tiempo: " + (fin-inicio)/1000);
    }

    private int contarVecinos(int i, int j) {
        return esVecino(i-1,j-1) + esVecino(i-1,j) + esVecino(i-1,j+1)
                + esVecino(i,j-1) + esVecino(i,j+1)
                + esVecino(i+1,j-1) + esVecino(i+1,j) + esVecino(i+1,j+1);
    }

    private int esVecino(int i, int j) {
        //if (i>=0 && i<mundo.length-1) {
        //if (j>=0 && j<mundo[i].length-1) {
        if (mundo[i][j]==Estado.VIVA) {
            return 1;
        }
        //}
        //}
        return 0;
    }

    private void dibujarMundo() {
        // Dibuja el estado actual del mundo
        long inicio = System.nanoTime();
        int x, y=0;
        for (int i=0; i<mundo.length; i++){
            x = 0;
            for (int j=0; j<mundo[i].length; j++) {
                if (mundo[i][j] == Estado.VIVA) {
                    batch.draw(celdaViva, x, y);
                } else {
                    batch.draw(celdaMuerta, x, y);
                }
                x += TAMANO_CELDA;
            }
            y += TAMANO_CELDA;
        }
        long fin = System.nanoTime();
        Gdx.app.log("dibujandoMundo","Tiempo: " + (fin-inicio)/1000);
    }

    private Texture crearCelda(Estado estado) {

        Pixmap pixmap = new Pixmap(TAMANO_CELDA, TAMANO_CELDA, Pixmap.Format.RGBA8888);
        if (estado==Estado.VIVA) {
            pixmap.setColor(1,1,1,1);
        } else {
            pixmap.setColor(0,0,0,1);
        }
        pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
        Texture textura = new Texture(pixmap);
        pixmap.dispose();
        return textura;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public enum Estado {
        VIVA,
        MUERTA
    }
}
