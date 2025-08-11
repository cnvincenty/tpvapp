package bo.edu.uagrm.soe.prac02tdd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.FacturaVentaItemOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.otd.FacturaVentaOTD;
import bo.edu.uagrm.soe.prac02tdd.aplicacion.servicio.implementacion.FacturaVentaServicioImplementacion;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Cliente;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.FacturaVenta;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupocliente;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Grupoproducto;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.Producto;
import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.TipoDocumento;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.ClienteRepositorio;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.FacturaVentaRepositorio;
import bo.edu.uagrm.soe.prac02tdd.infraestructura.ProductoRepositorio;

public class FacturaVentaServicioTest {

    @Mock
    private FacturaVentaRepositorio facturaVentaRepositorio;

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @InjectMocks
    private FacturaVentaServicioImplementacion facturaVentaServicio;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void debeRegistrarFacturaVentaConDescuentos() {
        Grupocliente grupoClienteAlMayor = new Grupocliente();
        grupoClienteAlMayor.setId(1L);
        grupoClienteAlMayor.setNombre("Ventas al Mayor");
        grupoClienteAlMayor.setDescuento(new BigDecimal("8.0"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Jose Miranda");
        cliente.setCodigo("CL11101110");
        cliente.setTipoDocumento(TipoDocumento.CI);
        cliente.setNumeroDocumento("11101110");
        cliente.setEmail("jjsmm@gmail.com");
        cliente.setGrupocliente(grupoClienteAlMayor);

        Grupoproducto grupoHerramientas = new Grupoproducto();
        grupoHerramientas.setId(1L);
        grupoHerramientas.setNombre("Herramientas");
        grupoHerramientas.setDescuento(new BigDecimal("3.0"));

        Producto taladro = new Producto();
        taladro.setId(1L);
        taladro.setNombre("Taladro BOSCH");
        taladro.setGrupoproducto(grupoHerramientas);
        taladro.setPreciounitario(new BigDecimal("100.00"));

        when(clienteRepositorio.findById(1L)).thenReturn(Optional.of(cliente));
        when(productoRepositorio.findById(1L)).thenReturn(Optional.of(taladro));
        when(facturaVentaRepositorio.save(any(FacturaVenta.class))).thenAnswer(invocation -> {
            FacturaVenta factura = invocation.getArgument(0);
            factura.setId(1L);
            return factura;
        });

        FacturaVentaOTD facturaVentaOTD = new FacturaVentaOTD();
        facturaVentaOTD.setClienteId(1L);
        facturaVentaOTD.setAlmacen("Almacén Central");
        facturaVentaOTD.setCondicionPago("Contado");

        FacturaVentaItemOTD itemOTD = new FacturaVentaItemOTD();
        itemOTD.setProductoId(1L);
        itemOTD.setCantidad(new BigDecimal("1"));
        itemOTD.setPrecioUnitario(new BigDecimal("100.00"));

        List<FacturaVentaItemOTD> items = new ArrayList<>();
        items.add(itemOTD);
        facturaVentaOTD.setItems(items);

        FacturaVentaOTD resultado = facturaVentaServicio.registrarFacturaVenta(facturaVentaOTD);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Jose Miranda", resultado.getClienteNombre());
        assertEquals("Ventas al Mayor", resultado.getClienteGrupo());
        assertEquals("Almacén Central", resultado.getAlmacen());
        assertEquals("Contado", resultado.getCondicionPago());
        assertEquals(1, resultado.getItems().size());

        FacturaVentaItemOTD itemResultado = resultado.getItems().get(0);
        assertEquals("Taladro BOSCH", itemResultado.getProductoNombre());
        assertEquals("Herramientas", itemResultado.getProductoGrupo());
        assertEquals(new BigDecimal("11.0"), itemResultado.getPorcentajeDescuento());
        assertEquals(new BigDecimal("11.00"), itemResultado.getMontoDescuento());
        assertEquals(new BigDecimal("89.00"), itemResultado.getSubtotal());
        assertEquals(new BigDecimal("89.00"), resultado.getTotal());
    }
}