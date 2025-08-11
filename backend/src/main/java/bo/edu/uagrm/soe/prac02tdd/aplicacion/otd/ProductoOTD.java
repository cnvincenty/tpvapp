package bo.edu.uagrm.soe.prac02tdd.aplicacion.otd;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoOTD {

    private Long id;

    private String codigo;

    private String nombre;

    private Long grupoproductoId;

    private String unidadMedida;

    private BigDecimal preciounitario;

    private BigDecimal descuento;

    private String ruta;

}
