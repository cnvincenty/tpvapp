package bo.edu.uagrm.soe.prac02tdd.aplicacion.otd;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FacturaVentaOTD {
    private Long id;
    private LocalDateTime fecha;
    private Long clienteId;
    private String clienteCodigo;
    private String clienteNombre;
    private String clienteGrupo;
    private String almacen;
    private String condicionPago;
    private BigDecimal total;
    private List<FacturaVentaItemOTD> items = new ArrayList<>();
}