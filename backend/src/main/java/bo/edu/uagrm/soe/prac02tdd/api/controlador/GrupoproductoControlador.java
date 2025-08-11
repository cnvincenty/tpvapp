package bo.edu.uagrm.soe.prac02tdd.api.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.GrupoproductoOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.GrupoproductoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/grupoproducto")
@Tag(name = "Grupoproductos", description = "Gestión de Grupoproducto")
@CrossOrigin
public class GrupoproductoControlador {

    private final GrupoproductoServicio servicio;

    public GrupoproductoControlador(GrupoproductoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los Grupoproductos")
    public ResponseEntity<List<GrupoproductoOTD>> obtenerTodos() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Grupoproducto por ID")
    public ResponseEntity<GrupoproductoOTD> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo Grupoproducto")
    public ResponseEntity<GrupoproductoOTD> crear(@RequestBody GrupoproductoOTD dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Grupoproducto")
    public ResponseEntity<GrupoproductoOTD> actualizar(@PathVariable Long id, @RequestBody GrupoproductoOTD dto) {
        return ResponseEntity.ok(servicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Grupoproducto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
