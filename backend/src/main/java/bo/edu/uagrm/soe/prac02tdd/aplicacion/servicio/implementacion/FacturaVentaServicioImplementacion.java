package bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.implementacion;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.FacturaVentaItemOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.FacturaVentaOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.FacturaVentaServicio;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Cliente;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.FacturaVenta;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.FacturaVentaItem;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Producto;
import bo.edu.uagrm.soe.prac02tdd.excepcion.RecursoNoEncontradoException;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.ClienteRepositorio;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.FacturaVentaRepositorio;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.ProductoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FacturaVentaServicioImplementacion implements FacturaVentaServicio {

    private final FacturaVentaRepositorio facturaVentaRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private final ProductoRepositorio productoRepositorio;

    @Override
    @Transactional
    public FacturaVentaOTD registrarFacturaVenta(FacturaVentaOTD facturaVentaOTD) {

        Cliente cliente = obtenerClientePorId(facturaVentaOTD.getClienteId());

        FacturaVenta facturaVenta = new FacturaVenta();
        facturaVenta.setCliente(cliente);
        facturaVenta.setAlmacen(facturaVentaOTD.getAlmacen());
        facturaVenta.setCondicionPago(facturaVentaOTD.getCondicionPago());

        for (FacturaVentaItemOTD itemOTD : facturaVentaOTD.getItems()) {
            Producto producto = obtenerProductoPorId(itemOTD.getProductoId());

            FacturaVentaItem item = new FacturaVentaItem();
            item.setProducto(producto);
            item.setCantidad(itemOTD.getCantidad());
            item.setPrecioUnitario(itemOTD.getPrecioUnitario());

            facturaVenta.agregarItem(item);
        }

        facturaVenta.calcularTotales();
        facturaVentaRepositorio.save(facturaVenta);

        return convertirADTO(facturaVenta);
    }

    @Override
    public FacturaVentaOTD obtenerFacturaVenta(Long id) {
        FacturaVenta facturaVenta = facturaVentaRepositorio.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Factura con ID %d no encontrada".formatted(id)));
        return convertirADTO(facturaVenta);
    }

    @Override
    public List<FacturaVentaOTD> listarFacturasVenta() {
        return facturaVentaRepositorio.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    // -----------------------------
    // Métodos auxiliares privados
    // -----------------------------

    private Cliente obtenerClientePorId(Long clienteId) {
        return clienteRepositorio.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con ID %d no encontrado".formatted(clienteId)));
    }

    private Producto obtenerProductoPorId(Long productoId) {
        return productoRepositorio.findById(productoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID %d no encontrado".formatted(productoId)));
    }

    private FacturaVentaOTD convertirADTO(FacturaVenta facturaVenta) {
        FacturaVentaOTD dto = new FacturaVentaOTD();

        dto.setId(facturaVenta.getId());
        dto.setFecha(facturaVenta.getFecha());
        dto.setClienteId(facturaVenta.getCliente().getId());
        dto.setClienteCodigo(facturaVenta.getCliente().getCodigo());
        dto.setClienteNombre(facturaVenta.getCliente().getNombre());

        dto.setClienteGrupo(
            facturaVenta.getCliente().getGrupocliente() != null
                ? facturaVenta.getCliente().getGrupocliente().getNombre()
                : null
        );

        dto.setAlmacen(facturaVenta.getAlmacen());
        dto.setCondicionPago(facturaVenta.getCondicionPago());
        dto.setTotal(facturaVenta.getTotal());

        List<FacturaVentaItemOTD> items = facturaVenta.getItems().stream().map(item -> {
            FacturaVentaItemOTD itemDTO = new FacturaVentaItemOTD();
            itemDTO.setProductoId(item.getProducto().getId());
            itemDTO.setProductoCodigo(item.getProducto().getCodigo());
            itemDTO.setProductoNombre(item.getProducto().getNombre());

            itemDTO.setProductoGrupo(
                item.getProducto().getGrupoproducto() != null
                    ? item.getProducto().getGrupoproducto().getNombre()
                    : null
            );

            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioUnitario(item.getPrecioUnitario());
            itemDTO.setPorcentajeDescuento(item.getPorcentajeDescuento());
            itemDTO.setMontoDescuento(item.getMontoDescuento());
            itemDTO.setSubtotal(item.getSubtotal());
            return itemDTO;
        }).toList();

        dto.setItems(items);
        return dto;
    }
}