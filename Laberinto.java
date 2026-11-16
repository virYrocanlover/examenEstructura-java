public class Laberinto {

    static final int RESOLVER = 0,
                    IMPRIMIR = 1,
                    CONTIENE = 2,
                    VOLCAR = 3;
    static final int LEJOS = 1_000_000; //para celdas inutiles

    static int N,M;
    static long semilla;
    static int inicioX = 1, inicioY = 1; //S:esquina superior derecha
    static int finX = 1, finY; //Esquina supeior derechaa

    //Portales: posicion y desplazamiento
    static int p0x, p0y, p0dx, p0dy;
    static int p1x, p1y, p1dx, p1dy;
    static int p2x, p2y, p2dx, p2dy;

    static Pila ruta = new Pila();
    static Pila mejorRuta = null; 
    static int mejor = Integer.MAX_VALUE; // pasos del mejor camino

    static void configurar(int n, int m, long s){
        N = n;
        M = m;
        semilla = s;
        finY = m-2;

        p0x = filaDeIdx(celdaPortal(0)); p0y = colDeIdx(celdaPortal(0));
        p1x = filaDeIdx(celdaPortal(1)); p1y = colDeIdx(celdaPortal(1));
        p2x = filaDeIdx(celdaPortal(2)); p2y = colDeIdx(celdaPortal(2));

        p0dx = filaDeIdx(idxDestino(0)) - p0x; p0dy = colDeIdx(idxDestino(0))-p0y;
        p1dx = filaDeIdx(idxDestino(1)) - p1x; p1dy = colDeIdx(idxDestino(1))-p1y;
        p2dx = filaDeIdx(idxDestino(2)) - p2x; p2dy = colDeIdx(idxDestino(2))-p2y;

        //Funcion recursiva
        static int procesar(int modo, int a, int b, int c, int d, int e){

            //Resolver
            if(modo == RESOLVER){
                int x = a, y=b, pasos=c, desde = d, usados = e; //pared fuera del mapa
                if (esPared(x,y)) return 0;

                int p = indicePortal(x,y);
                if (p != -1 && ((usados >> p) & 1) != 0) return 0; //portal ya usado
                if (pasos + promesa(x,y, usados) >= mejor) return 0; //regla 2

                ruta.push(x, y);

                if (x == finX && y ==finY){
                    mejor = pasos;
                    mejorRuta = ruta.instantanea(); //solo una referencia

                } else if (p != -1 && destinoValido(p)) {

                    //Regla 3: portal
                    procesar(RESOLVER, x + pDX(p), y + pDY(p), pasos, -1, usados |(1<<p));
                    
                } else {
                    //Regla 1
                    int k0 = promesa(x + dx(0), y + dy(0), usados)* 4+0;
                    int k1 = promesa(x + dx(1), y + dy(1), usados)* 4+1;
                    int k2 = promesa(x + dx(2), y + dy(2), usados)* 4+2;
                    int k3 = promesa(x + dx(3), y + dy(3), usados)* 4+3;
                    int t;
                    t = Math.min(k0, k1); k1 = Math.max(k0, k1); k0=t;
                    t = Math.min(k2, k3); k3 = Math.max(k2, k3); k2 = t;
                    t = Math.min(k0, k2); k2 = Math.max(k0, k2); k0 = t;
                    t = Math.min(k1, k3); k3 = Math.max(k1, k3); k1 = t;
                    t = Math.min(k1, k2); k2 = Math.max(k1, k2); k1 = t;

                    int atras = desde ^1;

                    if ((k0 & 3) != atras) procesar(RESOLVER, x + dx(k0 & 3),
                    y + dy(k0 & 3), pasos +1, k0 & 3, usados);
                    if ((k1 & 3)!= atras) procesar(RESOLVER, x + dx(k1 & 3),
                    y + dy(k1 & 3), pasos + 1, k1 & 3, usados);
                    if ((k2 & 3)!= atras) procesar(RESOLVER, x + dx(k2 & 3),
                    y + dy(k2 & 3), pasos +1, k2 & 3, usados);
                    if ((k3 & 3) != atras) procesar(RESOLVER, x + dx(k3 & 3),
                    y +  dy(k2 & 3), pasos +1,k3 & 3, usados);

                }

                ruta.pop(); //retrocede
                return 0;
            }

            
            //Imprimir
            if (modo == IMPRIMIR){
                if(a>= N) return 0;

                char ch = celda(a,b);
                if (c == 1 && ch == ' ' && procesar(CONTIENE, a, b,0,0,0)== 1)
                    ch = '*'
                System.out.print(ch);
                System.out.print(' ');

                if (b == M-1){
                    System.out.println();
                    procesar(IMPRIMIR, a+1, 0, c,0,0);
                } else {
                    procesar(IMPRIMIR, a,b+1,c,0,0);
                }
                return 0;
            }

            //Recorremos la pila sacando elememtnos y los devuelve al volver
            if (modo == CONTIENE){
                if (mejorRuta.vacia()) return 0;
                int tx = mejorRuta.topX(), tx = mejorRuta.topY();
                if (tx == a && ty == b) return 1;
                mejorRuta.pop();
                int r = procesar(CONTIENE, A, B, 0,0,0);
                mejorRuta.push(tx, ty);
                return r; //restaura la pila
            }

            //saca todo el camino y lo escribe al volver (asi sale de inicio a fin)
            if (modo == VOLCAR){
                if (mejorRuta.vacia()) return 0;
                int x = mejorRuta.topX(), y= mejorRuta.topY();
                mejorRuta.pop();
                boolean primero = mejorRuta.vacia();

                procesar(VOLCAR, O, O, O, O, O);
                System.out.println((primero ? "": "->") + "(" + x + "," + y + ")");
                mejorRuta.push(x, y);
                return 0; //restaura la pila
            }

            return 0;

        }

        //Promesa
        static int promesa(int x, int y, int usados){
            if (esPared(x,y)) return LEJOS;
            int m = Math.abs(x-finX) + Math.abs(y-finY);
            m = Math.min(m, atraccion(0, x, y, usados));
            m = Math.min(m, atraccion(1, x,y, usados));
            m = Math.min(m, atraccion(2, x, y, usados));
            return m;
        }

        //Distancia al portal k si es valido y aun no se uso, sino LEJOS
        static int atraccion(int k, int x, int y, int usados){
            if (((usados >> k) & 1) != 0 || !destinoValido(k)) return LEJOS;
            return Math.abs(x - pX(k)) + Math.abs(y - pY(k));

        }

        //Portales
        static int pX (int k){ return k == 0 ? p0x: (k == 1 ? p1x: p2x);}
        static int pY (int k){ return k == 0 ? p0y: (k == 1 ? p1y: p2y);}
        static int pDX (int k){ return k == 0 ? p0dx: (k == 1 ? p1dx: p2dx);}
        static int pDY (int k){ return k == 0 ? p0dy: (k == 1 ? p1dy: p2dy);}

        static int indicePortal(int x, int y){
            if (p0x == x && p0y == y) return 0;
            if (p1x == x && p1y == y) return 1;
            if (p2x == x && p2y == y) return 2;
            return -1;
        }

        //La casilla p+x, p+y es valida? (no es el mismo sitio y no es pared)
        static boolean destinoValido(int k){
            if (pDX(k)==0 && pDY(k) == 0) return false;
            return !esPared(pX(k)+ pDX(k), py(k)+ pDY(k));
        }



        //Elige celdas distintas sin contar S ni E a partir de la semilla
        static int celdaPortal(int k){
            int kp = filas()* columnas()-2 ;//Celdas sin S ni E
            int a = (int)(mezclar(100)%kp);
            int b = (int) (mezclar(101) % (kp -1));
            if (b >= a) b++;
            int c = (int)(mezclar(102)% (kp-2));
            int lo = Math.min(a, b), hi = Math.max(a, b);
            if(c >= lo) c++;
            if(c >= hi) c++;
            int idx = (k == 0 ? a:(k ==1 ? b:c))+1; //Salta S(indice 0)

            if(idx >= columnas()-1) idx++; //Salta E(indice C-1)
            return idx;

        }

        //celd de destino aleatoria
        static int idxDestino(int k){
            return (int)(mezclar(200 + k)% (filas()*columnas()));

        }

        // LABERINTO VIRTUAL (árbol con raíz en el centro, generado por semilla)
        // Las celdas están en posiciones (fila impar, columna impar). Cada celda,
        // salvo la central, se conecta con UN vecino que está más cerca del centro
        // (vertical u horizontal, según un bit aleatorio de la semilla). Como cada
        // celda tiene un solo "padre", no existen ciclos.
        
        static int filas() {return (N-1)/2;}
        static int columnas() {return (M-1)/2;}
        static int filaDeIdx(int idx) {return 1+2 * (idx/columnas());}
        static int colDeIdx(int idx){ return 1+2 *(idx % columnas());}

        static boolean bit(int i, int j){
            return ((mezclar(((long) (i + 1) << 32) | j) >>> 17) & 1L) == 1L;
        }

        static boolean vert(int i, int j){
            return i != filas()/2 &&(j == columnas()/2 || bit(i,j));
        }

        static boolean esPared(int r, int c){
            if(r <= 0 || c <= 0 || r >= N - 1 || c >= M - 1) return true;   // borde
            if(r % 2 == 1 && c % 2 == 1) return false;                      // celda
            if  (r % 2 == 0 && c % 2 == 0) return true;                       // pilar
            if (r % 2) {
                // muro entre (r-1,c) y (r+1,c)
                int j = (c-1)/2, iu =(r-2)/2, il = r / 2, ci = filas() / 2;
                return !((iu < ci && vert(iu, j)) || (il > ci && vert(il, j)));
                
            }
            // muro entre (r,c-1) y (r,c+1)
            int i = (r - 1) / 2, jl = (c - 2) / 2, jr = c / 2, cj = columnas() / 2;
            return !((jl < cj && horiz(i, jl)) || (jr > cj && horiz(i, jr)));

        }

        static char celda(int r, int c){
            if (r == inicioX && c == inicioY) return 'S';
            if (r == finX && c == finY) return 'E';
            if (indicePortal(r, c) != -1) return 'P';
            return esPared(r,c)? '#': ' ';
            
        }

        static long mezclar(long v){
            long z = v + semilla * 0x9E3779B97F4A7C15L + 0x9E3779B97F4A7C15L;
             z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
             z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
             z = z ^ (z >>> 31);
             return z & Long.MAX_VALUE;
        }

         // Direcciones: 0 arriba, 1 abajo, 2 izquierda, 3 derecha (opuesta = d ^ 1)

        static  int dx(int d) { return d == 0 ? -1 : (d == 1 ? 1 : 0); }
        static int dy(int d) { return d == 2 ? -1 : (d == 3 ? 1 : 0); }

        static String describirPortal(int k){
            return "P" + (k + 1) + " (" + pX(k) + "," + pY(k) + ") -> (" 
            + (pX(k) + pDX(k)) + "," + (pY(k) + pDY(k)) + ")"
            + (destinoValido(k) ? "" : "  [destino inválido: no teletransporta]");


        }


        
    }


    
}