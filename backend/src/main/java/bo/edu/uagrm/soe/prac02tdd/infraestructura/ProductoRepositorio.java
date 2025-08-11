package bo.edu.uagrm.soe.prac02tdd.infraestructura;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Producto;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long>{

    List<Producto> findAllByGrupoproductoId(Long grupoproductoId);
}
