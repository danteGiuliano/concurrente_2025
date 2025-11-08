package parque.areaPremios;

import java.util.ArrayList;
import java.util.List;
import parque.Visitante;

/**
 * Área de Premios del parque.
 * Los visitantes pueden canjear fichas obtenidas en las atracciones por premios.
 * Cada premio tiene un costo en fichas y un stock limitado.
 */
public class AreaPremios {
    private final Encargado encargado;
    private final List<Premio> catalogoPremios;

    public AreaPremios() {
        this.catalogoPremios = inicializarCatalogo();
        this.encargado = new Encargado(catalogoPremios);
    }

    /**
     * Inicializa el catálogo de premios disponibles con sus costos y stock
     */
    private List<Premio> inicializarCatalogo() {
        List<Premio> catalogo = new ArrayList<>();
        
        // Premios económicos (1-5 fichas)
        catalogo.add(new Premio("Llavero", 1, 50));
        catalogo.add(new Premio("Sticker", 1, 100));
        catalogo.add(new Premio("Gorra", 3, 30));
        catalogo.add(new Premio("Remera", 5, 25));
        
        // Premios medios (6-15 fichas)
        catalogo.add(new Premio("Peluche pequeño", 8, 20));
        catalogo.add(new Premio("Taza", 10, 15));
        catalogo.add(new Premio("Mochila", 12, 10));
        catalogo.add(new Premio("Peluche grande", 15, 8));
        
        // Premios premium (16-30 fichas)
        catalogo.add(new Premio("Auriculares", 20, 5));
        catalogo.add(new Premio("Reloj", 25, 3));
        catalogo.add(new Premio("Consola portátil", 30, 2));
        
        return catalogo;
    }

    /**
     * Permite que un visitante intente canjear sus fichas por un premio
     * @param v Visitante que quiere canjear fichas
     * @return true si obtuvo un premio, false si no pudo
     */
    public boolean canjearPremio(Visitante v) throws InterruptedException {
        return encargado.atenderVisitante(v);
    }

    /**
     * Obtiene el catálogo de premios disponibles (solo lectura)
     */
    public List<Premio> getCatalogoPremios() {
        return new ArrayList<>(catalogoPremios);
    }

    /**
     * Obtiene estadísticas del área de premios
     */
    public int getTotalVisitantesAtendidos() {
        return encargado.getVisitantesAtendidos();
    }
}