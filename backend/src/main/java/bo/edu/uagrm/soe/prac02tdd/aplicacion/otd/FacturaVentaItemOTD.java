package bo.edu.uagrm.soe.prac02tdd.aplicacion.otd;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FacturaVentaItemOTD {
    private Long productoId;
    private String productoCodigo;
    private String productoNombre;
    private String productoGrupo;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal porcentajeDescuento;
    private BigDecimal montoDescuento;
    private BigDecimal subtotal;
}