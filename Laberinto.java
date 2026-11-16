public class Laberinto {

    static final int EVALUAR_RUTAS = 0,
                    IMPRIMIR = 1,
                    CONTIENE = 2,
                    VOLCAR = 3,
                    TRAZAR_TRAMO =4,
                    SUBIR_HASTA = 5,
                    PROFUNDIDAD = 6,
                    SUBIR_LCA = 7,
                    INVERTIR_SEQ = 8,
                    RECORRER_SEQ = 9;
    
    static int N,M;
    static long semilla;
    static int inicioX = 1, inicioY = 1; //S:esquina superior derecha
    static int finX = 1, finY; //Esquina supeior derechaa

    //Portales: posicion y desplazamiento
    static int p0x, p0y, p0dx, p0dy;
    static int p1x, p1y, p1dx, p1dy;
    static int p2x, p2y, p2dx, p2dy;

    static Pila mejorRuta = new Pila(); 
    static int mejor = Integer.MAX_VALUE; // pasos del mejor camino
    static int mejorSecuencia= 0;

    static void configurar(int n, int m, long s){
        N = n;
        M = m;
        semilla = s;
        finX = 1;
        finY = ((m - 2) % 2 == 1) ? (m - 2) : (m - 3);

        mejorRuta = new Pila();
        mejor = Integer.MAX_VALUE;
        mejorSecuencia = 0;

        p0x = filaDeIdx(celdaPortal(0)); p0y = colDeIdx(celdaPortal(0));
        p1x = filaDeIdx(celdaPortal(1)); p1y = colDeIdx(celdaPortal(1));
        p2x = filaDeIdx(celdaPortal(2)); p2y = colDeIdx(celdaPortal(2));

        p0dx = filaDeIdx(idxDestino(0)) - p0x; p0dy = colDeIdx(idxDestino(0))-p0y;
        p1dx = filaDeIdx(idxDestino(1)) - p1x; p1dy = colDeIdx(idxDestino(1))-p1y;
        p2dx = filaDeIdx(idxDestino(2)) - p2x; p2dy = colDeIdx(idxDestino(2))-p2y;
    }

        //Funcion recursiva
        static int procesar(int modo, int a, int b, int c, int d, int e){

            //EVALUAR_RUTAS: evalua las combinaciones de portales LCA
            if(modo == EVALUAR_RUTAS){
                int distMeta = distanciaLCA(a,b, finX, finY); //pared fuera del mapa
                if (distMeta >= 0 && c + distMeta < mejor) {
                    mejor = c + distMeta;
                    mejorSecuencia = e;
                }

                //Portal 0
                if ((d & 1)== 0 && destinoValido(0)) {
                    int dist0 = distanciaLCA(a,b,p0x,p0y);
                    if (dist0 >= 0){
                        procesar(EVALUAR_RUTAS, p0x+p0dx, p0y+p0dy,c+dist0,d|1,(e <<2)|1);
                    }
                    
                }

                //Portal 1
                if ((d & 2)== 0 && destinoValido(1)) {
                    int dist1 = distanciaLCA(a,b,p1x, p1y);
                    if (dist1 >= 0) {
                        procesar(EVALUAR_RUTAS, p1x+p1dx, p1y+p1dy, c+dist1, d|2,(e<<2)|2);
                        
                    }
                    
                }

                //Portal 2
                if ((d & 4)== 0 && destinoValido(2)) {
                    int dist2 = distanciaLCA(a,b,p2x,p2y);
                    if (dist2 >= 0) {
                        procesar(EVALUAR_RUTAS, p2x + p2dx, p2y + p2dy, c + dist2, d | 4, (e << 2) | 3);
                        
                    }
                    
                }

                return 0;
            }

            
            //Imprimir, dibuja el laberinto celda por celda
            if (modo == IMPRIMIR){
                if(a>= N) return 0;

                char ch = celda(a,b);
                if (c == 1 && ch == ' ' && procesar(CONTIENE, a, b,0,0,0)== 1)ch = '*';
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

            //CONTIENE, busca si (a,b) esta en la pila mejorRuta
            if (modo == CONTIENE){
                if (mejorRuta.vacia()) return 0;
                int tx = mejorRuta.topX(), ty = mejorRuta.topY();
                if (tx == a && ty == b) return 1;
                mejorRuta.pop();
                int r = procesar(CONTIENE, a, b, 0,0,0);
                mejorRuta.push(tx, ty);
                return r; //restaura la pila
            }

            //imprime el recorrido guardado en la pila desde el inicio
            if (modo == VOLCAR){
                if (mejorRuta.vacia()) return 0;
                int x = mejorRuta.topX(), y= mejorRuta.topY();
                mejorRuta.pop();
                boolean primero = mejorRuta.vacia();

                procesar(VOLCAR, 0, 0, 0, 0, 0);
                System.out.print((primero ? "": "->") + "(" + x + "," + y + ")");
                mejorRuta.push(x, y);
                return 0; //restaura la pila    
            }

            if (modo == TRAZAR_TRAMO){
                int lca = encontrarLCA(a, b, c, d);
                int lcaX = filaDeIdx(lca), lcaY = colDeIdx(lca);
                procesar(SUBIR_HASTA, a, b, lcaX, lcaY, 1);
                procesar(SUBIR_HASTA, c, d, lcaX, lcaY, 0);
                return 0;
            }


            //SUBIR_HASTA: Apila en mejorRuta el camino directo hacia/desde el LCA
            if (modo == SUBIR_HASTA){
                if (a == c && b == d) return 0;
                int padre = padreDe(a,b);
                int px = filaDeIdx(padre), py = colDeIdx(padre);
                int mx = (a + px) / 2, my = (b + py) / 2;
                if (e == 1) {
                    mejorRuta.push(mx, my);
                    mejorRuta.push(px, py);
                    procesar(SUBIR_HASTA, px, py, c, d, 1);
                    
                } else {
                    procesar(SUBIR_HASTA, px, py, c, d, 0);
                        mejorRuta.push(mx, my);
                        mejorRuta.push(a, b);
                    
                }
                 return 0;

            }

            //PROFUNDIDAD: Nivel del nodo (a,b) respecto a la raíz central del árbol
            if (modo == PROFUNDIDAD){
                int i = (a - 1) / 2, j = (b - 1) / 2;
                if (i == filas() / 2 && j == columnas() / 2) return 0;
                int padre = padreDe(a, b);
                return 1 + procesar(PROFUNDIDAD, filaDeIdx(padre), colDeIdx(padre), 0, 0, 0);
            }

            //SUBIR_LCA: Sube (a,b) y (c,d) sincronizados hasta hallar su ancestro común
            if (modo == SUBIR_LCA) {
                if (a == c && b == d) return ((a - 1) / 2) * columnas() + ((b - 1) / 2);
            if (e > 0) {
                int p = padreDe(a, b);
                return procesar(SUBIR_LCA, filaDeIdx(p), colDeIdx(p), c, d, e - 1);
            }
            if (e < 0) {
                int p = padreDe(c, d);
                return procesar(SUBIR_LCA, a, b, filaDeIdx(p), colDeIdx(p), e + 1);
            }
                int p1 = padreDe(a, b), p2 = padreDe(c, d);
                return procesar(SUBIR_LCA, filaDeIdx(p1), colDeIdx(p1), filaDeIdx(p2), colDeIdx(p2), 0);
                
            }

            //INVERTIR_SEQ Invierte los bloques de 2 bits de la secuencia de portales
            if (modo == INVERTIR_SEQ){
                if(a == 0) return b;
                return procesar(INVERTIR_SEQ, a >> 2, (b << 2) | (a & 3), 0, 0, 0);
            }

            //RECORRER_SEQ Recorre tramo a tramo sobre mejorRuta desde (a,b)
            if (modo == RECORRER_SEQ){
                if (c == 0) {
                procesar(TRAZAR_TRAMO, a, b, finX, finY, 0);
                return 0;
            }
            int k = (c & 3) - 1;
            procesar(TRAZAR_TRAMO, a, b, pX(k), pY(k), 0);
            int destX = pX(k) + pDX(k), destY = pY(k) + pDY(k);
            mejorRuta.push(destX, destY);
            procesar(RECORRER_SEQ, destX, destY, c >> 2, 0, 0);
            return 0;

            }

            return 0;

        }

        //METODOS AUXILIARES
        static int distanciaLCA(int x1, int y1, int x2, int y2) {
            int d1 = procesar(PROFUNDIDAD, x1, y1, 0, 0, 0);
            int d2 = procesar(PROFUNDIDAD, x2, y2, 0, 0, 0);
            int lca = procesar(SUBIR_LCA, x1, y1, x2, y2, d1 - d2);
           int dLca = procesar(PROFUNDIDAD, filaDeIdx(lca), colDeIdx(lca), 0, 0, 0);
            return (d1 + d2 - 2 * dLca) * 2;
    }

        static int encontrarLCA (int x1, int y1, int x2, int y2){
            int d1 = procesar(PROFUNDIDAD, x1, y1, 0, 0, 0);
            int d2 = procesar(PROFUNDIDAD, x2, y2, 0, 0, 0);
            return procesar(SUBIR_LCA, x1, y1, x2, y2, d1 - d2);

        }

        static int padreDe(int r, int c){
            int i = (r - 1) / 2, j = (c - 1) / 2;
            int ci = filas() / 2, cj = columnas() / 2;
            if (vert(i, j)) {
            int ni = (i < ci) ? i + 1 : i - 1;
            return ni * columnas() + j;
            } else {
            int nj = (j < cj) ? j + 1 : j - 1;
            return i * columnas() + nj;
            }

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
            return !esPared(pX(k)+ pDX(k), pY(k)+ pDY(k));
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
            int idxFin = ((finX - 1) / 2) * columnas() + ((finY - 1) / 2);
            if(idx >= idxFin) idx++;
            return idx;

        }

        //celd de destino aleatoria
        static int idxDestino(int k){
            return (int) (mezclar(200 + k) % (filas() * columnas()));

        }

        // LABERINTO VIRTUAL (árbol con raíz en el centro, generado por semilla)
        
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

        static boolean horiz(int i, int j){
            return j != columnas() / 2 && (i == filas() / 2 || !bit(i, j));
        }

        static boolean esPared(int r, int c){
            if(r <= 0 || c <= 0 || r >= filas() * 2 || c >= columnas() * 2) return true;  // borde
            if(r % 2 == 1 && c % 2 == 1) return false;                      // celda
            if  (r % 2 == 0 && c % 2 == 0) return true;                       // pilar
            if (r % 2 == 0)  {
                // muro entre (r-1,c) y (r+1,c)
                int j = (c - 1) / 2, iu = (r - 2) / 2, il = r / 2, ci = filas() / 2;
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

        static String describirPortal(int k){
            return "P" + (k + 1) + " (" + pX(k) + "," + pY(k) + ") -> (" 
            + (pX(k) + pDX(k)) + "," + (pY(k) + pDY(k)) + ")"
            + (destinoValido(k) ? "" : "  [destino inválido: no teletransporta]");


        }
    
}