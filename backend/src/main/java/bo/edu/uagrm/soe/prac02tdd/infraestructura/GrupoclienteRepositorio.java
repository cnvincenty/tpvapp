package bo.edu.uagrm.soe.prac02tdd.infraestructura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupocliente;

@Repository
public interface GrupoclienteRepositorio extends JpaRepository<Grupocliente, Long>{

}
