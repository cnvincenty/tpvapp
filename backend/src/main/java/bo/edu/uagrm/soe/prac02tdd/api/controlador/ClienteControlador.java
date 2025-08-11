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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.ClienteOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.ClienteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/cliente")
@Tag(name = "Clientes", description = "Gestión de Cliente")
@CrossOrigin
public class ClienteControlador {

    private final ClienteServicio servicio;

    public ClienteControlador(ClienteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los Clientes")
    public ResponseEntity<List<ClienteOTD>> obtenerTodos() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Cliente por ID")
    public ResponseEntity<ClienteOTD> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @GetMapping("/buscarPorCodigo")
    @Operation(summary = "buscar Cliente por Codigo")
    public ResponseEntity<ClienteOTD> buscarPorCodigo(@RequestParam String codigo) {
        return ResponseEntity.ok(servicio.buscarPorCodigo(codigo));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo Cliente")
    public ResponseEntity<ClienteOTD> crear(@RequestBody ClienteOTD dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Cliente")
    public ResponseEntity<ClienteOTD> actualizar(@PathVariable Long id, @RequestBody ClienteOTD dto) {
        return ResponseEntity.ok(servicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Cliente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
