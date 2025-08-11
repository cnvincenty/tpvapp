package bo.edu.uagrm.soe.prac02tdd.dominio.entidad;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "factura_venta_item")
@Getter
@Setter
@NoArgsConstructor
public class FacturaVentaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "factura_venta_id")
    private FacturaVenta facturaVenta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "cantidad", precision = 10, scale = 2, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "porcentaje_descuento", precision = 10, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "monto_descuento", precision = 10, scale = 2)
    private BigDecimal montoDescuento;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Transient
    public BigDecimal calcularImporteTotal() {
        return precioUnitario.multiply(cantidad).setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public void calcularDescuento(BigDecimal descuentoCliente) {
        BigDecimal descuentoProducto = BigDecimal.ZERO;
        if (producto != null && producto.getGrupoproducto() != null &&
                producto.getGrupoproducto().getDescuento() != null) {
            descuentoProducto = producto.getGrupoproducto().getDescuento();
        }

        if (descuentoCliente == null) {
            descuentoCliente = BigDecimal.ZERO;
        }

        porcentajeDescuento = descuentoCliente.add(descuentoProducto);

        BigDecimal importeTotal = calcularImporteTotal();
        montoDescuento = importeTotal.multiply(porcentajeDescuento)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        subtotal = importeTotal.subtract(montoDescuento);
    }
}