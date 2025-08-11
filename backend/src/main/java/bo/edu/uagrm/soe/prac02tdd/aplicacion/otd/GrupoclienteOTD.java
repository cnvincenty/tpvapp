package bo.edu.uagrm.soe.prac02tdd.aplicacion.otd;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoclienteOTD {

    private Long id;

    private String nombre;

    private BigDecimal descuento;
}
