package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.implementacion;

import java.util.List;

import org.springframework.stereotype.Service;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.GrupoclienteOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.GrupoclienteServicio;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupocliente;
import bo.edu.uagrm.soe.prac02tdd.excepcion.RecursoNoEncontradoException;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.GrupoclienteRepositorio;

@Service
public class GrupoclienteServicioImplementacion implements GrupoclienteServicio{

    private final GrupoclienteRepositorio repositorio;

    public GrupoclienteServicioImplementacion(GrupoclienteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<GrupoclienteOTD> obtenerTodos() {
        return repositorio.findAll().stream()
                .map(this::aOTD)
                .toList();
    }

    @Override
    public GrupoclienteOTD obtenerPorId(Long id) {
        Grupocliente salida = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupocliente no encontrado"));
        return aOTD(salida);
    }

    @Override
    public GrupoclienteOTD crear(GrupoclienteOTD otd) {
        Grupocliente salida = aENTIDAD(otd);
        return aOTD(repositorio.save(salida));
    }

    @Override
    public GrupoclienteOTD actualizar(Long id, GrupoclienteOTD otd) {
        Grupocliente salida = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupocliente no encontrado"));
        salida.setNombre(otd.getNombre());
        salida.setDescuento(otd.getDescuento());
        return aOTD(repositorio.save(salida));
    }

    @Override
    public void eliminar(Long id) {
        repositorio.deleteById(id);
    }

    private GrupoclienteOTD aOTD(Grupocliente entrada) {
        GrupoclienteOTD salida = new GrupoclienteOTD();
        salida.setId(entrada.getId());
        salida.setNombre(entrada.getNombre());
        salida.setDescuento(entrada.getDescuento());
        return salida;
    }

    private Grupocliente aENTIDAD(GrupoclienteOTD entrada) {
        Grupocliente salida = new Grupocliente();
        salida.setId(entrada.getId());
        salida.setNombre(entrada.getNombre());
        salida.setDescuento(entrada.getDescuento());
        return salida;
    }
}
