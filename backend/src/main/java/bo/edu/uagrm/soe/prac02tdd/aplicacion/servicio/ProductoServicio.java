package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.ProductoOTD;

public interface ProductoServicio {

    List<ProductoOTD> obtenerTodos();

    ProductoOTD obtenerPorId(Long id);

    ProductoOTD crear(ProductoOTD otd);

    ProductoOTD actualizar(Long id, ProductoOTD otd);

    void eliminar(Long id);

    void cargarImagen(Long id, MultipartFile archivo);

    Resource descargarImagen(String nombreArchivo);

    List<ProductoOTD> obtenerPorIdGrupoproducto(Long idgrupoproducto);
}
