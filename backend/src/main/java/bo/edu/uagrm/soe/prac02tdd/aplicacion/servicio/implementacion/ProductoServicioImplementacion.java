package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.implementacion;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.ProductoOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.ProductoServicio;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupoproducto;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Producto;
import bo.edu.uagrm.soe.prac02tdd.excepcion.RecursoNoEncontradoException;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.GrupoproductoRepositorio;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.ProductoRepositorio;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductoServicioImplementacion implements ProductoServicio {

    private final ProductoRepositorio repositorio;
    private final GrupoproductoRepositorio grupoproductoRepositorio;

    @Value("${ruta.archivos}")
    private String rutaprincipal;

    @Override
    public List<ProductoOTD> obtenerTodos() {
        return repositorio.findAll().stream()
                .map(this::aOTD)
                .toList();
    }

    @Override
    public ProductoOTD obtenerPorId(Long id) {
        Producto salida = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        return aOTD(salida);
    }

    @Override
    public ProductoOTD crear(ProductoOTD otd) {
        Producto salida = aENTIDAD(otd);
        return aOTD(repositorio.save(salida));
    }

    @Override
    public ProductoOTD actualizar(Long id, ProductoOTD otd) {
        Producto salida = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        salida.setCodigo(otd.getCodigo());
        salida.setNombre(otd.getNombre());
        salida.setPreciounitario(otd.getPreciounitario());
        salida.setUnidadMedida(otd.getUnidadMedida());

        if (otd.getGrupoproductoId() != null) {
            Grupoproducto grupoproducto = grupoproductoRepositorio.findById(otd.getGrupoproductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Grupoproducto no encontrado"));
            salida.setGrupoproducto(grupoproducto);
        }

        return aOTD(repositorio.save(salida));
    }

    @Override
    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    private ProductoOTD aOTD(Producto entrada) {
        ProductoOTD salida = new ProductoOTD();
        salida.setId(entrada.getId());
        salida.setNombre(entrada.getNombre());
        salida.setPreciounitario(entrada.getPreciounitario());
        salida.setUnidadMedida(entrada.getUnidadMedida());
        salida.setRuta(entrada.getRuta());
        salida.setGrupoproductoId(entrada.getGrupoproducto() != null ? entrada.getGrupoproducto().getId() : null);
        return salida;
    }

    private Producto aENTIDAD(ProductoOTD entrada) {
        Producto salida = new Producto();
        salida.setId(entrada.getId());
        salida.setCodigo(entrada.getCodigo());
        salida.setNombre(entrada.getNombre());
        salida.setPreciounitario(entrada.getPreciounitario());
        salida.setUnidadMedida(entrada.getUnidadMedida());
        salida.setRuta(entrada.getRuta());
        if (entrada.getGrupoproductoId() != null) {
            Grupoproducto grupoproducto = grupoproductoRepositorio.findById(entrada.getGrupoproductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Grupoproducto no encontrado"));
            salida.setGrupoproducto(grupoproducto);
        }
        return salida;
    }

    @Override
    public void cargarImagen(Long id, MultipartFile archivo) {
        if (!archivo.isEmpty()) {
            String ruta = rutaprincipal + "/productos/imagenes";
            String nombreArchivo = UUID.randomUUID().toString() + "."
                    + StringUtils.getFilenameExtension(archivo.getOriginalFilename());

            Producto entrada = repositorio.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
            ;

            entrada.setRuta(nombreArchivo);
            repositorio.save(entrada);

            Path rutaArchivo = Paths.get(ruta).resolve(nombreArchivo).toAbsolutePath();
            Path rutaArchivoAnterior = Paths.get(ruta).resolve(entrada.getRuta()).toAbsolutePath();
            try {
                Files.deleteIfExists(rutaArchivoAnterior);
                Files.copy(archivo.getInputStream(), rutaArchivo);
            } catch (Exception e) {
                throw new RuntimeException("No se pudo almacenar el archivo. Error: " + e.getMessage());
            }
            redimensionar(nombreArchivo);
        }
    }

    private void redimensionar(String nombreArchivo) {
        String ruta = rutaprincipal + "/item/imagenes";
        int ancho = 300;
        int alto = 300;
        try {
            fredimensionar(nombreArchivo, ruta, ancho, alto);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo redimensionar el archivo. Error: " + e.getMessage());
        }
    }

    private static void fredimensionar(String nombreArchivo, String ruta, int ancho, int alto) throws IOException {
        try {
            File entradaarchivo = new File(ruta + "/" + nombreArchivo);
            File salidaarchivo = new File(ruta + "/" + nombreArchivo);
            BufferedImage entradaImagen = ImageIO.read(entradaarchivo);
            BufferedImage salidaImagen = new BufferedImage(ancho, alto, entradaImagen.getType());
            Graphics2D g2d = salidaImagen.createGraphics();
            g2d.drawImage(entradaImagen, 0, 0, ancho, alto, null);
            g2d.dispose();
            ImageIO.write(salidaImagen, "png", salidaarchivo);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public Resource descargarImagen(String nombreArchivo) {
        String ruta = rutaprincipal + "/productos/imagenes";
        Path rutaArchivo = Paths.get(ruta).resolve(nombreArchivo).toAbsolutePath();
        Path rutaArchivoSinImagen = Paths.get(ruta).resolve("sinimagen.png").toAbsolutePath();

        try {
            Resource recurso = new UrlResource(rutaArchivo.toUri());
            if (recurso.exists() && recurso.isReadable()) {
                return recurso;
            }

            Resource sinImagen = new UrlResource(rutaArchivoSinImagen.toUri());
            if (sinImagen.exists() && sinImagen.isReadable()) {
                return sinImagen;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<ProductoOTD> obtenerPorIdGrupoproducto(Long idgrupoproducto) {
        return repositorio.findAllByGrupoproductoId(idgrupoproducto).stream()
                .map(this::aOTD)
                .toList();
    }

}
