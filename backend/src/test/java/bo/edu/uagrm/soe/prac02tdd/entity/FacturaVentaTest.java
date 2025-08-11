package bo.edu.uagrm.soe.prac02tdd.entity;

import bo.edu.uagrm.soe.prac02tdd.dominio.entidad.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class FacturaVentaTest {

    @Test
    public void debeCrearFacturaVentaConItemsYCalcularDescuentos() {
        Grupocliente grupoClienteAlMayor = new Grupocliente();
        grupoClienteAlMayor.setId(1L);
        grupoClienteAlMayor.setNombre("Ventas al Mayor");
        grupoClienteAlMayor.setDescuento(new BigDecimal("8.0"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Miranda");
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

        String almacen = "Almacén Central";
        String condicionPago = "Contado";

        FacturaVenta factura = new FacturaVenta();
        factura.setCliente(cliente);
        factura.setAlmacen(almacen);
        factura.setCondicionPago(condicionPago);

        FacturaVentaItem item = new FacturaVentaItem();
        item.setProducto(taladro);
        item.setCantidad(new BigDecimal("1"));
        item.setPrecioUnitario(taladro.getPreciounitario());

        factura.agregarItem(item);
        factura.calcularTotales();

        assertEquals(1, factura.getItems().size());
        assertEquals(new BigDecimal("11.0"), item.getPorcentajeDescuento());
        assertEquals(new BigDecimal("11.00"), item.getMontoDescuento());
        assertEquals(new BigDecimal("89.00"), item.getSubtotal());
        assertEquals(new BigDecimal("89.00"), factura.getTotal());
    }

    @Test
    public void debeCalcularDescuentosCuandoSoloHayDescuentoDeCliente() {
        Grupocliente grupoClienteAlMayor = new Grupocliente();
        grupoClienteAlMayor.setId(1L);
        grupoClienteAlMayor.setNombre("Ventas al Mayor");
        grupoClienteAlMayor.setDescuento(new BigDecimal("8.0"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Miranda");
        cliente.setCodigo("CL11101110");
        cliente.setTipoDocumento(TipoDocumento.CI);
        cliente.setNumeroDocumento("11101110");
        cliente.setEmail("jjsmm@gmail.com");
        cliente.setGrupocliente(grupoClienteAlMayor);

        Grupoproducto grupoHerramientas = new Grupoproducto();
        grupoHerramientas.setNombre("Herramientas");
        grupoHerramientas.setDescuento(null); // Sin descuento de grupo producto

        Producto taladro = new Producto();
        taladro.setNombre("Taladro BOSCH");
        taladro.setGrupoproducto(grupoHerramientas);
        taladro.setPreciounitario(new BigDecimal("100.00"));

        FacturaVenta factura = new FacturaVenta();
        factura.setCliente(cliente);

        FacturaVentaItem item = new FacturaVentaItem();
        item.setProducto(taladro);
        item.setCantidad(new BigDecimal("1"));
        item.setPrecioUnitario(taladro.getPreciounitario());

        factura.agregarItem(item);
        factura.calcularTotales();

        assertEquals(new BigDecimal("8.0"), item.getPorcentajeDescuento());
        assertEquals(new BigDecimal("8.00"), item.getMontoDescuento());
        assertEquals(new BigDecimal("92.00"), item.getSubtotal());
    }

    @Test
    public void debeCalcularDescuentosCuandoSoloHayDescuentoDeProducto() {
        Grupocliente grupoClienteNormal = new Grupocliente();
        grupoClienteNormal.setNombre("Normal");
        grupoClienteNormal.setDescuento(null); // Sin descuento de grupo cliente

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Miranda");
        cliente.setCodigo("CL11101110");
        cliente.setTipoDocumento(TipoDocumento.CI);
        cliente.setNumeroDocumento("11101110");
        cliente.setEmail("jjsmm@gmail.com");
        cliente.setGrupocliente(grupoClienteNormal);

        Grupoproducto grupoHerramientas = new Grupoproducto();
        grupoHerramientas.setNombre("Herramientas");
        grupoHerramientas.setDescuento(new BigDecimal("3.0"));

        Producto taladro = new Producto();
        taladro.setNombre("Taladro BOSCH");
        taladro.setGrupoproducto(grupoHerramientas);
        taladro.setPreciounitario(new BigDecimal("100.00"));

        FacturaVenta factura = new FacturaVenta();
        factura.setCliente(cliente);

        FacturaVentaItem item = new FacturaVentaItem();
        item.setProducto(taladro);
        item.setCantidad(new BigDecimal("1"));
        item.setPrecioUnitario(taladro.getPreciounitario());

        factura.agregarItem(item);
        factura.calcularTotales();

        assertEquals(new BigDecimal("3.0"), item.getPorcentajeDescuento());
        assertEquals(new BigDecimal("3.00"), item.getMontoDescuento());
        assertEquals(new BigDecimal("97.00"), item.getSubtotal());
    }

    @Test
    public void debeCalcularTotalesConMultiplesItems() {
        Grupocliente grupoClienteAlMayor = new Grupocliente();
        grupoClienteAlMayor.setId(1L);
        grupoClienteAlMayor.setNombre("Ventas al Mayor");
        grupoClienteAlMayor.setDescuento(new BigDecimal("8.0"));

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Miranda");
        cliente.setCodigo("CL11101110");
        cliente.setTipoDocumento(TipoDocumento.CI);
        cliente.setNumeroDocumento("11101110");
        cliente.setEmail("jjsmm@gmail.com");
        cliente.setGrupocliente(grupoClienteAlMayor);

        Grupoproducto grupoHerramientas = new Grupoproducto();
        grupoHerramientas.setNombre("Herramientas");
        grupoHerramientas.setDescuento(new BigDecimal("3.0"));

        Producto taladro = new Producto();
        taladro.setNombre("Taladro BOSCH");
        taladro.setGrupoproducto(grupoHerramientas);
        taladro.setPreciounitario(new BigDecimal("100.00"));

        Producto destornillador = new Producto();
        destornillador.setNombre("Destornillador BOSCH");
        destornillador.setGrupoproducto(grupoHerramientas);
        destornillador.setPreciounitario(new BigDecimal("20.00"));

        FacturaVenta factura = new FacturaVenta();
        factura.setCliente(cliente);

        FacturaVentaItem item1 = new FacturaVentaItem();
        item1.setProducto(taladro);
        item1.setCantidad(new BigDecimal("1"));
        item1.setPrecioUnitario(taladro.getPreciounitario());
        factura.agregarItem(item1);

        FacturaVentaItem item2 = new FacturaVentaItem();
        item2.setProducto(destornillador);
        item2.setCantidad(new BigDecimal("2"));
        item2.setPrecioUnitario(destornillador.getPreciounitario());
        factura.agregarItem(item2);

        factura.calcularTotales();

        assertEquals(2, factura.getItems().size());
        assertEquals(new BigDecimal("11.0"), item1.getPorcentajeDescuento());
        assertEquals(new BigDecimal("11.00"), item1.getMontoDescuento());
        assertEquals(new BigDecimal("89.00"), item1.getSubtotal());

        assertEquals(new BigDecimal("11.0"), item2.getPorcentajeDescuento());
        assertEquals(new BigDecimal("4.40"), item2.getMontoDescuento()); // 11% de 40 (2 * 20)
        assertEquals(new BigDecimal("35.60"), item2.getSubtotal());

        assertEquals(new BigDecimal("124.60"), factura.getTotal());
    }
}