package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio;

import java.util.List;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.GrupoclienteOTD;

public interface GrupoclienteServicio {

    List<GrupoclienteOTD> obtenerTodos();

    GrupoclienteOTD obtenerPorId(Long id);

    GrupoclienteOTD crear(GrupoclienteOTD otd);

    GrupoclienteOTD actualizar(Long id, GrupoclienteOTD otd);

    void eliminar(Long id);
}
