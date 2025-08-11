import { environment } from './../../../environments/environment';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Modal } from 'bootstrap';
import { Producto } from '../dominio/producto';
import { Grupoproducto } from '../../grupoproducto/dominio/grupoproducto';
import { ProductoServicio } from '../servicio/producto-servicio';
import { GrupoproductoServicio } from '../../grupoproducto/servicio/grupoproducto-servicio';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-producto-componente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './producto-componente.html',
  styleUrl: './producto-componente.css'
})
export class ProductoComponente implements OnInit{

  private readonly apiURL = `${environment.apiUrl}/v1/producto`;

  readonly rutaImagen = `${this.apiURL}/descargarImagen/`;


  @ViewChild('ventanaModal') ventanaModalRef!: ElementRef; modal?: Modal;

  datos: Producto[] = [];
  dato: Producto = this.crearProductoVacio();
  editando = false;

  gruposproductos: Grupoproducto[] = [];

  archivoSeleccionado!: File;
  imagenSrc!: string;

  constructor(
    private servicio: ProductoServicio,
    private grupoproductoService: GrupoproductoServicio
  ) { }

  ngOnInit(): void {
    this.cargarDatos();
    this.cargarCombos();
  }

  crearProductoVacio(): Producto {
    return {
      codigo: '',
      nombre: '',
      unidadMedida: '',
      preciounitario: 0,
      grupoproductoId: 0,
      ruta: ''
    };
  }

  abrirNuevo(): void {
    this.dato = this.crearProductoVacio();
    this.editando = false;
    if (!this.modal) {
      this.modal = new Modal(this.ventanaModalRef.nativeElement);
    }
    this.modal.show();
  }

  cargarDatos(): void {
    this.servicio.obtenerTodos().subscribe(data => this.datos = data);
  }

  guardar(): void {
    this.dato.codigo = this.dato.codigo.toUpperCase();
    this.dato.nombre = this.dato.nombre.toUpperCase();
    this.dato.unidadMedida = this.dato.unidadMedida.toUpperCase();

    const obs = this.editando && this.dato.id !== undefined
      ? this.servicio.actualizar(this.dato)
      : this.servicio.crear(this.dato);

    obs.subscribe((data) => {
      this.reset();
      this.cargarDatos();
      this.fsubirArchivo(data.id!);
      this.cerrarModal();
    });
  }

  editar(producto: Producto): void {
    this.dato = { ...producto };
    this.editando = true;
    if (!this.modal) {
      this.modal = new Modal(this.ventanaModalRef.nativeElement);
    }
    this.modal.show();
  }

  eliminar(id: number): void {
    if (confirm('¿Deseas eliminar este producto?')) {
      this.servicio.eliminarPorId(id).subscribe(() => this.cargarDatos());
    }
  }

  reset(): void {
    this.dato = this.crearProductoVacio();
    this.editando = false;
  }

  cerrarModal(): void {
    this.modal?.hide();
    this.reset();
  }

  cargarCombos(): void {
    this.grupoproductoService.obtenerTodos().subscribe(data => this.gruposproductos = data);
  }

  fseleccionarArchivo(event: any){
    const reader = new FileReader();
    this.archivoSeleccionado = event.target.files[0];
    if (event.target.files[0] && event.target.files.length) {
      const file = event.target.files[0];
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.imagenSrc = reader.result as string;
      };
    }
  }

  fsubirArchivo(id: number) {
    this.servicio.subirImagen(this.archivoSeleccionado, id).subscribe( data => {
    })
  }


}
