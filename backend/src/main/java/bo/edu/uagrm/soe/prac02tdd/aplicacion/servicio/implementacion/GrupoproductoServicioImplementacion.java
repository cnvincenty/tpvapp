package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.implementacion;

import java.util.List;

import org.springframework.stereotype.Service;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.GrupoproductoOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.GrupoproductoServicio;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupoproducto;
import bo.edu.uagrm.soe.prac02tdd.excepcion.RecursoNoEncontradoException;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.GrupoproductoRepositorio;

@Service
public class GrupoproductoServicioImplementacion implements GrupoproductoServicio {

    private final GrupoproductoRepositorio repositorio;

    public GrupoproductoServicioImplementacion(GrupoproductoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<GrupoproductoOTD> obtenerTodos() {
        return repositorio.findAll().stream()
                .map(this::aOTD)
                .toList();
    }

    @Override
    public GrupoproductoOTD obtenerPorId(Long id) {
        Grupoproducto salida = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupoproducto no encontrado"));
        return aOTD(salida);
    }

    @Override
    public GrupoproductoOTD crear(GrupoproductoOTD otd) {
        Grupoproducto salida = aENTIDAD(otd);
        return aOTD(repositorio.save(salida));
    }

    @Override
    public GrupoproductoOTD actualizar(Long id, GrupoproductoOTD otd) {
        Grupoproducto salida = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupoproducto no encontrado"));
        salida.setNombre(otd.getNombre());
        salida.setDescuento(otd.getDescuento());
        return aOTD(repositorio.save(salida));
    }

    @Override
    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    private GrupoproductoOTD aOTD(Grupoproducto entrada) {
        GrupoproductoOTD salida = new GrupoproductoOTD();
        salida.setId(entrada.getId());
        salida.setNombre(entrada.getNombre());
        salida.setDescuento(entrada.getDescuento());
        return salida;
    }

    private Grupoproducto aENTIDAD(GrupoproductoOTD entrada) {
        Grupoproducto salida = new Grupoproducto();
        salida.setId(entrada.getId());
        salida.setNombre(entrada.getNombre());
        salida.setDescuento(entrada.getDescuento());
        return salida;
    }

}
