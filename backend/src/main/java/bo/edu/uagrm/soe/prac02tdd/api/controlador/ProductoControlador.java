package bo.edu.uagrm.soe.prac02tdd.api.controlador;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.ProductoOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.ProductoServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/producto")
@Tag(name = "Productos", description = "Gestión de Producto")
@CrossOrigin
public class ProductoControlador {

    private final ProductoServicio servicio;

    public ProductoControlador(ProductoServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los Productos")
    public ResponseEntity<List<ProductoOTD>> obtenerTodos() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/porGrupo/{id}")
    @Operation(summary = "Obtener todos los Productos Por Grupo")
    public ResponseEntity<List<ProductoOTD>> obtenerTodos(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorIdGrupoproducto(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Producto por ID")
    public ResponseEntity<ProductoOTD> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo Producto")
    public ResponseEntity<ProductoOTD> crear(@RequestBody ProductoOTD dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Producto")
    public ResponseEntity<ProductoOTD> actualizar(@PathVariable Long id, @RequestBody ProductoOTD dto) {
        return ResponseEntity.ok(servicio.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/descargarImagen/{nombreArchivo}")
    ResponseEntity<?> descargarImagen(@PathVariable String nombreArchivo) {
        Resource archivo = servicio.descargarImagen(nombreArchivo);

        if (archivo == null || !archivo.exists() || !archivo.isReadable()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No se pudo encontrar o leer la imagen solicitada.");
        }

        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getFilename() + "\"");

        return new ResponseEntity<Resource>(archivo, cabecera, HttpStatus.OK);
    }

    @PostMapping("/subirImagen/{id}")
    ResponseEntity<?> subirImagen(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        servicio.cargarImagen(id, archivo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}