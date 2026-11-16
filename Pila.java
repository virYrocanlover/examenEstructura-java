class Pila{
    private  static class Nodo{
        final int x, y;
        final Nodo sig;
        Nodo(int x, int y, Nodo sig){this.x = x; this.y = y; this.sig = sig;}
    }

    private Nodo tope;
    void push(int x, int y) { tope = new Nodo(x, y, tope);}
    void  pop() {tope = tope.sig;}
    boolean vacia() {return tope == null;}
    int topX() {return tope.x;}
    int topY() {return tope.y;}
  
}