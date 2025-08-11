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

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.GrupoclienteOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.GrupoclienteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/grupocliente")
@Tag(name = "Grupoclientes", description = "Gestión de Grupocliente")
@CrossOrigin
public class GrupoclienteControlador {

    private final GrupoclienteServicio servicio;

    public GrupoclienteControlador(GrupoclienteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los Grupoclientes")
    public ResponseEntity<List<GrupoclienteOTD>> obtenerTodos() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Grupocliente por ID")
    public ResponseEntity<GrupoclienteOTD> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo Grupocliente")
    public ResponseEntity<GrupoclienteOTD> crear(@RequestBody GrupoclienteOTD dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Grupocliente")
    public ResponseEntity<GrupoclienteOTD> actualizar(@PathVariable Long id, @RequestBody GrupoclienteOTD dto) {
        return ResponseEntity.ok(servicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Grupocliente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
