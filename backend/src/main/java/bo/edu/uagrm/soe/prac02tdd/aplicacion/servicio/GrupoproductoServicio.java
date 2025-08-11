package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio;

import java.util.List;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.GrupoproductoOTD;

public interface GrupoproductoServicio {

    List<GrupoproductoOTD> obtenerTodos();

    GrupoproductoOTD obtenerPorId(Long id);

    GrupoproductoOTD crear(GrupoproductoOTD otd);

    GrupoproductoOTD actualizar(Long id, GrupoproductoOTD otd);

    void eliminar(Long id);
}
