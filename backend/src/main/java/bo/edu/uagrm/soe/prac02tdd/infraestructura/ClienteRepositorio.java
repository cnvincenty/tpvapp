package bo.edu.uagrm.soe.prac02tdd.infraestructura;

import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCodigo(String codigo);
}
