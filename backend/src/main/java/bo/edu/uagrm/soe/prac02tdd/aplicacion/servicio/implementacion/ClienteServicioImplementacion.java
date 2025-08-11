package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.implementacion;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.ClienteOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.ClienteServicio;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Cliente;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupocliente;
import bo.edu.uagrm.soe.prac02tdd.excepcion.RecursoNoEncontradoException;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.ClienteRepositorio;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.GrupoclienteRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServicioImplementacion implements ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;
    private final GrupoclienteRepositorio grupoclienteRepositorio;

    @Override
    public List<ClienteOTD> obtenerTodos() {
        return clienteRepositorio.findAll().stream()
            .map(this::mapToOtd)
            .toList();
    }

    @Override
    public ClienteOTD obtenerPorId(Long id) {
        Cliente cliente = obtenerClientePorId(id);
        return mapToOtd(cliente);
    }

    @Override
    public ClienteOTD buscarPorCodigo(String codigo) {
        Cliente cliente = clienteRepositorio.findByCodigo(codigo)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con código '%s' no encontrado".formatted(codigo)));
        return mapToOtd(cliente);
    }

    @Override
    public ClienteOTD crear(ClienteOTD otd) {
        Cliente cliente = mapToEntidad(otd);
        clienteRepositorio.save(cliente);
        return mapToOtd(cliente);
    }

    @Override
    public ClienteOTD actualizar(Long id, ClienteOTD otd) {
        Cliente cliente = obtenerClientePorId(id);

        BeanUtils.copyProperties(otd, cliente, "id", "grupocliente"); // no sobrescribir grupocliente directamente

        if (otd.getGrupoclienteId() != null) {
            Grupocliente grupocliente = obtenerGrupoPorId(otd.getGrupoclienteId());
            cliente.setGrupocliente(grupocliente);
        }

        clienteRepositorio.save(cliente);
        return mapToOtd(cliente);
    }

    @Override
    public void eliminar(Long id) {
        if (!clienteRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("No se puede eliminar: cliente con ID %d no encontrado".formatted(id));
        }
        clienteRepositorio.deleteById(id);
    }

    // -----------------------------
    // Métodos auxiliares privados
    // -----------------------------

    private Cliente obtenerClientePorId(Long id) {
        return clienteRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con ID %d no encontrado".formatted(id)));
    }

    private Grupocliente obtenerGrupoPorId(Long id) {
        return grupoclienteRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Grupo de cliente con ID %d no encontrado".formatted(id)));
    }

    private ClienteOTD mapToOtd(Cliente cliente) {
        final ClienteOTD otd = new ClienteOTD();
        BeanUtils.copyProperties(cliente, otd);
        if (cliente.getGrupocliente() != null) {
            otd.setGrupoclienteId(cliente.getGrupocliente().getId());
        }
        return otd;
    }

    private Cliente mapToEntidad(ClienteOTD otd) {
        final Cliente cliente = new Cliente();
        BeanUtils.copyProperties(otd, cliente);

        if (otd.getGrupoclienteId() != null) {
            final Grupocliente grupocliente = obtenerGrupoPorId(otd.getGrupoclienteId());
            cliente.setGrupocliente(grupocliente);
        }

        return cliente;
    }
}
