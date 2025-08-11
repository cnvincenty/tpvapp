package bo.edu.uagrm.soe.prac02tdd.dominio.entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "factura_venta")
@Getter
@Setter
@NoArgsConstructor
public class FacturaVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "almacen", length = 100)
    private String almacen;

    @Column(name = "condicion_pago", length = 50)
    private String condicionPago;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "facturaVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaVentaItem> items = new ArrayList<>();

    public void agregarItem(FacturaVentaItem item) {
        items.add(item);
        item.setFacturaVenta(this);
    }

    public void calcularTotales() {
        BigDecimal descuentoCliente = BigDecimal.ZERO;
        if (cliente != null && cliente.getGrupocliente() != null &&
            cliente.getGrupocliente().getDescuento() != null) {
            descuentoCliente = cliente.getGrupocliente().getDescuento();
        }

        total = BigDecimal.ZERO;
        for (FacturaVentaItem item : items) {
            item.calcularDescuento(descuentoCliente);
            total = total.add(item.getSubtotal());
        }
    }
}