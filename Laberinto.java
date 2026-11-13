import java.util.Random;
import java.util.Scanner;

/**
 * Laberinto N x M generado con una semilla, con 3 portales "P".
 *
 * Camino más corto de S a E con UNA sola función recursiva (resolver),
 * apoyada en UNA pila. Sin matriz de visitados, sin colas/listas/mapas,
 * sin BFS / DFS / Dijkstra / A*. El laberinto NO se modifica al resolver.
 */
public class Laberinto {

    static final int PORTALES = 3;
    // Movimientos: arriba, abajo, izquierda, derecha
    static final int[] MX = {-1, 1, 0, 0};
    static final int[] MY = {0, 0, -1, 1};

    static int N, M;
    static char[][] lab;                       // '#' pared, ' ' libre, S, E, P
    static int inicioX = 1, inicioY = 1, finX, finY;

    // Definición de cada portal: posición y desplazamiento (p+x, p+y)
    static int[] pX = new int[PORTALES], pY = new int[PORTALES];
    static int[] pDX = new int[PORTALES], pDY = new int[PORTALES];

    static Pila ruta = new Pila();             // camino que se recorre ahora
    static Pila mejorRuta = null;              // referencia al mejor camino (no es copia)
    static int mejor = Integer.MAX_VALUE;      // pasos del mejor camino

    // ------------------------------------------------------------------
    // GENERACIÓN (iterativa con pila). Laberinto "perfecto": SIN ciclos,
    // un único camino entre cualquier par de celdas.
    // ------------------------------------------------------------------
    static void generar(Random rnd) {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++) lab[i][j] = '#';

        Pila p = new Pila();
        lab[1][1] = ' ';
        p.push(1, 1);

        while (!p.vacia()) {
            int x = p.topX(), y = p.topY();

            int[] dirs = {0, 1, 2, 3};                    // orden aleatorio
            for (int i = 3; i > 0; i--) {
                int k = rnd.nextInt(i + 1);
                int t = dirs[i]; dirs[i] = dirs[k]; dirs[k] = t;
            }

            boolean avanzo = false;
            for (int d : dirs) {
                int nx = x + 2 * MX[d], ny = y + 2 * MY[d];
                if (nx > 0 && ny > 0 && nx < N - 1 && ny < M - 1 && lab[nx][ny] == '#') {
                    lab[x + MX[d]][y + MY[d]] = ' ';      // derriba el muro
                    lab[nx][ny] = ' ';
                    p.push(nx, ny);
                    avanzo = true;
                    break;
                }
            }
            if (!avanzo) p.pop();                         // callejón: retrocede
        }

        finX = N - 2;
        finY = M - 2;
        lab[inicioX][inicioY] = 'S';
        lab[finX][finY] = 'E';
    }

    // 3 portales en celdas libres, con desplazamiento (x, y) aleatorio.
    // Se procura que el destino sea válido; resolver() lo vuelve a comprobar.
    static void colocarPortales(Random rnd) {
        for (int i = 0; i < PORTALES; i++) {
            int px, py;
            do {
                px = rnd.nextInt(N);
                py = rnd.nextInt(M);
            } while (lab[px][py] != ' ');

            int dx, dy, intentos = 0;
            boolean ok;
            do {
                dx = rnd.nextInt(2 * N - 1) - (N - 1);
                dy = rnd.nextInt(2 * M - 1) - (M - 1);
                int tx = px + dx, ty = py + dy;
                ok = (dx != 0 || dy != 0) && tx >= 0 && ty >= 0 && tx < N && ty < M
                        && lab[tx][ty] == ' ';
                intentos++;
            } while (!ok && intentos < 500);

            pX[i] = px; pY[i] = py; pDX[i] = dx; pDY[i] = dy;
            lab[px][py] = 'P';
        }
    }

    // ------------------------------------------------------------------
    // LA ÚNICA FUNCIÓN RECURSIVA
    //  - La pila guarda el camino actual.
    //  - Si la celda ya está en la pila, sería un ciclo: se descarta.
    //    (No se marca ninguna celda como "visitada".)
    //  - Poda: si ya se igualó o superó el mejor, se corta la rama.
    //  - Al llegar a E se guarda una REFERENCIA a la pila (O(1), sin copiar).
    // ------------------------------------------------------------------
    static void resolver(int x, int y, int pasos) {
        if (x < 0 || y < 0 || x >= N || y >= M) return;   // fuera del laberinto
        if (lab[x][y] == '#') return;                     // pared
        if (ruta.contiene(x, y)) return;                  // ciclo
        if (pasos >= mejor) return;                       // poda

        ruta.push(x, y);

        if (x == finX && y == finY) {
            mejor = pasos;
            mejorRuta = ruta.instantanea();
        } else {
            int p = indicePortal(x, y);
            if (p != -1 && destinoValido(p)) {
                // Portal: salta a (x+dx, y+dy) sin costo extra
                resolver(x + pDX[p], y + pDY[p], pasos);
            } else {
                for (int d = 0; d < 4; d++)
                    resolver(x + MX[d], y + MY[d], pasos + 1);
            }
        }

        ruta.pop();                                       // backtracking
    }

    // ---------------------- utilidades pequeñas ------------------------
    static int indicePortal(int x, int y) {
        for (int i = 0; i < PORTALES; i++)
            if (pX[i] == x && pY[i] == y) return i;
        return -1;
    }

    static boolean destinoValido(int p) {
        int tx = pX[p] + pDX[p], ty = pY[p] + pDY[p];
        return tx >= 0 && ty >= 0 && tx < N && ty < M && lab[tx][ty] != '#';
    }

    // Solo para mostrar: dibuja '*' si la celda pertenece al mejor camino
    static void imprimir(boolean conCamino) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                char c = lab[i][j];
                if (conCamino && c == ' ' && mejorRuta.contiene(i, j)) c = '*';
                sb.append(c).append(' ');
            }
            sb.append('\n');
        }
        System.out.print(sb);
    }

    static void ejecutar() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Filas (N): ");
        N = sc.nextInt();
        System.out.print("Columnas (M): ");
        M = sc.nextInt();
        System.out.print("Semilla: ");
        long semilla = sc.nextLong();

        if (N < 5) N = 5;
        if (M < 5) M = 5;
        if (N % 2 == 0) N++;          // el generador necesita dimensiones impares
        if (M % 2 == 0) M++;

        lab = new char[N][M];
        Random rnd = new Random(semilla);
        generar(rnd);
        colocarPortales(rnd);

        System.out.println("\nLaberinto " + N + " x " + M + " (semilla " + semilla + "):\n");
        imprimir(false);

        System.out.println("\nPortales:");
        for (int i = 0; i < PORTALES; i++) {
            System.out.println("  P" + (i + 1) + " (" + pX[i] + "," + pY[i] + ") -> ("
                    + (pX[i] + pDX[i]) + "," + (pY[i] + pDY[i]) + ")"
                    + (destinoValido(i) ? "" : "  [destino inválido: no teletransporta]"));
        }

        resolver(inicioX, inicioY, 0);

        System.out.println();
        if (mejorRuta == null) {
            System.out.println("No existe camino.");
        } else {
            System.out.println("Camino más corto: " + mejor + " pasos ('*' = camino)\n");
            imprimir(true);
            System.out.println("\nRecorrido: " + mejorRuta.recorrido());
        }
    }

    public static void main(String[] args) {
        // Hilo con pila grande: la recursión puede ser profunda en laberintos grandes
        new Thread(null, Laberinto::ejecutar, "solver", 1L << 27).start();
    }
}

// ----------------------------------------------------------------------
// Pila propia (lista enlazada con nodos inmutables).
// Como los nodos nunca cambian, guardar "la pila tal como está" es solo
// guardar el puntero al tope: el camino queda intacto aunque después
// se hagan pop() en la pila original.
// ----------------------------------------------------------------------
class Pila {
    private static class Nodo {
        final int x, y;
        final Nodo sig;
        Nodo(int x, int y, Nodo sig) { this.x = x; this.y = y; this.sig = sig; }
    }

    private Nodo tope;
    private int tam;

    void push(int x, int y) { tope = new Nodo(x, y, tope); tam++; }
    void pop()              { tope = tope.sig; tam--; }
    boolean vacia()         { return tope == null; }
    int topX()              { return tope.x; }
    int topY()              { return tope.y; }

    boolean contiene(int x, int y) {
        for (Nodo n = tope; n != null; n = n.sig)
            if (n.x == x && n.y == y) return true;
        return false;
    }

    // Misma cadena de nodos, no se copia nada: solo se guarda el puntero.
    Pila instantanea() {
        Pila c = new Pila();
        c.tope = tope;
        c.tam = tam;
        return c;
    }

    // Del inicio al fin (el tope es el final del camino)
    String recorrido() {
        StringBuilder sb = new StringBuilder();
        for (Nodo n = tope; n != null; n = n.sig) {
            String celda = "(" + n.x + "," + n.y + ")";
            sb.insert(0, n.sig == null ? celda : " -> " + celda);
        }
        return sb.toString();
    }
}
