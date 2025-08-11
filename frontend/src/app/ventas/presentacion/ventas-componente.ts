import { environment } from './../../../environments/environment';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Producto } from '../../producto/dominio/producto';
import { GrupoproductoServicio } from '../../grupoproducto/servicio/grupoproducto-servicio';
import { ProductoServicio } from '../../producto/servicio/producto-servicio';
import { ClienteServicio } from '../../cliente/servicio/cliente-servicio';
import { Cliente } from '../../cliente/dominio/cliente';
import { FacturaventaServicio } from '../servicio/facturaventa-servicio';
import { Facturaventa } from '../dominio/facturaventa';
import { Facturaventaitem } from '../dominio/facturaventaitem';
import { Grupoproducto } from '../../grupoproducto/dominio/grupoproducto';
import { Grupocliente } from '../../grupocliente/dominio/grupocliente';
import { GrupoclienteServicio } from '../../grupocliente/servicio/grupocliente-servicio';

@Component({
  selector: 'app-ventas-componente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventas-componente.html',
  styleUrl: './ventas-componente.css'
})
export class VentasComponente implements OnInit{

  private readonly apiURL = `${environment.apiUrl}/v1/producto`;

  readonly rutaImagen = `${this.apiURL}/descargarImagen/`;

  gruposproductos: any[] = [];

  grupoSeleccionado: number | null = null;

  productosPorGrupo: Producto[] = [];

  productosSeleccionados: { producto: Producto; cantidad: number }[] = [];

  codigoBusqueda: string = '';

  clienteEncontrado: any = null;

  gruposClientes: Grupocliente[] = [];
  gruposProductos: Grupoproducto[] = [];

  condicionPago: 'EFECTIVO' | 'CREDITO' | null = null;

  constructor(
    private grupoproductoServicio: GrupoproductoServicio,
    private grupoclienteServicio: GrupoclienteServicio,
    private productoServicio: ProductoServicio,
    private clienteServicio: ClienteServicio,
    private facturaventaServicio: FacturaventaServicio
  ){}

  ngOnInit(): void {
    this.cargarGrupos();
    this.cargarGruposClientes();
  }

  cargarGruposClientes(): void {
    this.grupoclienteServicio.obtenerTodos().subscribe((res) => {
      this.gruposClientes = res;
    });

    this.grupoproductoServicio.obtenerTodos().subscribe((res) => {
      this.gruposProductos = res;
    });
  }

  cargarGrupos(): void {
    this.grupoproductoServicio.obtenerTodos().subscribe((res) => {
      this.gruposproductos = res;
    });
  }

  cargarProductosPorGrupo(): void {
    if (this.grupoSeleccionado != null) {
      this.productoServicio.obtenerPorIdGrupoproducto(this.grupoSeleccionado).subscribe((res) => {
        this.productosPorGrupo = res;
      });
    } else {
      this.productosPorGrupo = [];
    }
  }

  agregarProducto(producto: Producto): void {
    const existe = this.productosSeleccionados.find(item => item.producto.id === producto.id);
    if (!existe) {
      this.productosSeleccionados.push({
        producto,
        cantidad: 1
      });
    }
  }

  eliminarProducto(index: number): void {
    this.productosSeleccionados.splice(index, 1);
  }

  buscarCliente(): void {
  const codigo = this.codigoBusqueda.trim();
  if (!codigo) {
    this.clienteEncontrado = null;
    return;
  }

  this.clienteServicio.obtenerPorCodigo(codigo).subscribe({
    next: (cliente) => {
      this.clienteEncontrado = cliente;
    },
    error: () => {
      this.clienteEncontrado = null;
    }
    });
  }

  pagar(): void {
    if (!this.clienteEncontrado || this.productosSeleccionados.length === 0) {
      return;
    }

    const items: Facturaventaitem[] = this.productosSeleccionados.map(item => ({
      productoId: item.producto.id!,
      productoCodigo: item.producto.codigo,
      productoNombre: item.producto.nombre,
      productoGrupo: '',
      cantidad: item.cantidad,
      precioUnitario: item.producto.preciounitario,
      porcentajeDescuento: 0,
      montoDescuento: 0,
      subtotal: item.cantidad * item.producto.preciounitario
    }));

    const total = items.reduce((sum, item) => sum + item.subtotal, 0);

    const factura: Facturaventa = {
      fecha: new Date(),
      clienteId: this.clienteEncontrado.id,
      clienteNombre: this.clienteEncontrado.nombre,
      clienteGrupo: this.clienteEncontrado.grupo || '',
      almacen: 'Almacén Principal',
      condicionPago: 'Contado',
      total,
      items
    };

    this.facturaventaServicio.registrarFacturaVenta(factura).subscribe({
      next: () => {
        alert('Factura registrada con éxito');
        this.productosSeleccionados = [];
        this.codigoBusqueda = '';
        this.clienteEncontrado = null;
      },
      error: (err) => {
        console.error('Error al registrar la factura:', err);
        alert('Ocurrió un error al registrar la factura');
      }
    });
  }

  get total(): number {
    return this.productosSeleccionados.reduce((sum, item) => {
      return sum + (item.producto.preciounitario * item.cantidad);
    }, 0);
  }

  get descuentoTotal(): number {
    return this.productosSeleccionados.reduce((sum, item) => {
      const grupoProducto = this.gruposProductos.find(g => g.id === item.producto.grupoproductoId);
      const descuentoProducto = grupoProducto?.descuento || 0;

      const grupoCliente = this.gruposClientes.find(g => g.id === this.clienteEncontrado?.grupoclienteId);
      const descuentoCliente = grupoCliente?.descuento || 0;

      const descuentoTotal = descuentoProducto + descuentoCliente;
      const descuentoMonto = (item.producto.preciounitario * item.cantidad) * (descuentoTotal / 100);

      return sum + descuentoMonto;
    }, 0);
  }

  get totalAPagar(): number {
    return this.total - this.descuentoTotal;
  }

}
