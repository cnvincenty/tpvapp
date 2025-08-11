package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio;

import java.util.List;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.FacturaVentaOTD;

public interface FacturaVentaServicio {
    FacturaVentaOTD registrarFacturaVenta(FacturaVentaOTD facturaVentaOTD);
    FacturaVentaOTD obtenerFacturaVenta(Long id);
    List<FacturaVentaOTD> listarFacturasVenta();
}