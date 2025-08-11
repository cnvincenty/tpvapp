package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio;

import java.util.List;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.ClienteOTD;

public interface ClienteServicio {

    List<ClienteOTD> obtenerTodos();

    ClienteOTD obtenerPorId(Long id);

    ClienteOTD buscarPorCodigo(String codigo);

    ClienteOTD crear(ClienteOTD otd);

    ClienteOTD actualizar(Long id, ClienteOTD otd);

    void eliminar(Long id);
}
