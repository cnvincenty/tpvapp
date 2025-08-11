package bo.edu.uagrm.soe.prac02tdd.api.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.FacturaVentaOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.FacturaVentaServicio;

@RestController
@RequestMapping("/api/v1/facturaventa")
public class FacturaVentaControlador {

    @Autowired
    private FacturaVentaServicio facturaVentaServicio;

    @PostMapping
    public ResponseEntity<FacturaVentaOTD> registrarFacturaVenta(@RequestBody FacturaVentaOTD facturaVentaOTD) {
        FacturaVentaOTD facturaRegistrada = facturaVentaServicio.registrarFacturaVenta(facturaVentaOTD);
        return new ResponseEntity<>(facturaRegistrada, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaVentaOTD> obtenerFacturaVenta(@PathVariable Long id) {
        FacturaVentaOTD facturaVentaOTD = facturaVentaServicio.obtenerFacturaVenta(id);
        return ResponseEntity.ok(facturaVentaOTD);
    }

    @GetMapping
    public ResponseEntity<List<FacturaVentaOTD>> listarFacturasVenta() {
        List<FacturaVentaOTD> facturas = facturaVentaServicio.listarFacturasVenta();
        return ResponseEntity.ok(facturas);
    }
}