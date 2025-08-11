import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Modal } from 'bootstrap';
import { Cliente } from '../dominio/cliente';
import { Grupocliente } from '../../grupocliente/dominio/grupocliente';
import { ClienteServicio } from '../servicio/cliente-servicio';
import { GrupoclienteServicio } from '../../grupocliente/servicio/grupocliente-servicio';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cliente-componente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-componente.html',
  styleUrl: './cliente-componente.css'
})
export class ClienteComponente implements OnInit {

  @ViewChild('ventanaModal') ventanaModalRef!: ElementRef;
  modal?: Modal;

  datos: Cliente[] = [];
  dato: Cliente = this.crearClienteVacio();
  editando = false;

  gruposclientes: Grupocliente[] = [];

  tipoDocumentos: { codigo: string, descripcion: string }[] = [
    { codigo: 'CI', descripcion: 'Carnet de Identidad' },
    { codigo: 'CEX', descripcion: 'Carnet de Extranjería' },
    { codigo: 'PAS', descripcion: 'Pasaporte' },
    { codigo: 'OD', descripcion: 'Otro Documento' },
    { codigo: 'NIT', descripcion: 'NIT (Número de Identificación Tributaria)' }
  ];

  constructor(
    private servicio: ClienteServicio,
    private grupoclienteService: GrupoclienteServicio
  ) { }

  ngOnInit(): void {
    this.cargarDatos();
    this.cargarCombos();
  }

  crearClienteVacio(): Cliente {
    return {
      codigo: '',
      nombre: '',
      tipoDocumento: '',
      numeroDocumento: '',
      email: '',
      grupoclienteId: 0
    };
  }

  abrirNuevo(): void {
    this.dato = this.crearClienteVacio();
    this.editando = false;
    this.mostrarModal();
  }

  editar(cliente: Cliente): void {
    this.dato = { ...cliente };
    this.editando = true;
    this.mostrarModal();
  }

  mostrarModal(): void {
    if (!this.modal) {
      this.modal = new Modal(this.ventanaModalRef.nativeElement);
    }
    this.modal.show();
  }

  cargarDatos(): void {
    this.servicio.obtenerTodos().subscribe(data => this.datos = data);
  }

  guardar(): void {
    if (!this.dato) return;

    const obs = this.editando && this.dato.id
      ? this.servicio.actualizar(this.dato)
      : this.servicio.crear(this.dato);

    obs.subscribe(() => {
      this.reset();
      this.cargarDatos();
      this.cerrarModal();
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Deseas eliminar este cliente?')) {
      this.servicio.eliminarPorId(id).subscribe(() => this.cargarDatos());
    }
  }

  reset(): void {
    this.dato = this.crearClienteVacio();
    this.editando = false;
  }

  cerrarModal(): void {
    this.modal?.hide();
    this.reset();
  }

  cargarCombos(): void {
    this.grupoclienteService.obtenerTodos().subscribe(data => this.gruposclientes = data);
  }
}
