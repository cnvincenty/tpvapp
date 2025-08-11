import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Modal } from 'bootstrap';
import { Grupocliente } from '../dominio/grupocliente';
import { GrupoclienteServicio } from '../servicio/grupocliente-servicio';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-grupocliente-componente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './grupocliente-componente.html',
  styleUrl: './grupocliente-componente.css'
})
export class GrupoclienteComponente implements OnInit{

  @ViewChild('ventanaModal') ventanaModalRef!: ElementRef; modal?: Modal;

  datos: Grupocliente[] = [];
  dato: Grupocliente = { nombre: '', descuento: 0 };
  editando: boolean = false;

  constructor(private servicio: GrupoclienteServicio) { }

  ngOnInit(): void {
    this.cargarDatos();
  }

  abrirNuevo(): void {
    this.dato = { nombre: '', descuento: 0 };
    this.editando = false;

    if (!this.modal) {
      this.modal = new Modal(this.ventanaModalRef.nativeElement);
    }

    this.modal.show();
  }

  cargarDatos(): void {
    this.servicio.obtenerTodos().subscribe((data) => (this.datos = data));
  }

  guardar(): void {
    this.dato.nombre = this.dato.nombre.toUpperCase();

    if (this.editando && this.dato.id) {
      this.servicio.actualizar(this.dato).subscribe(() => {
        this.reset();
        this.cargarDatos();
        this.cerrarModal();
      });
    } else {
      this.servicio.crear(this.dato).subscribe(() => {
        this.reset();
        this.cargarDatos();
        this.cerrarModal();
      });
    }
  }

  editar(dato: Grupocliente): void {
    this.dato = { ...dato };
    this.editando = true;

    if (!this.modal) {
      this.modal = new Modal(this.ventanaModalRef.nativeElement);
    }
    this.modal.show();
  }


  eliminar(id: number): void {
    if (confirm('¿Deseas eliminar este grupocliente?')) {
      this.servicio.eliminarPorId(id).subscribe(() => {
        this.cargarDatos();
      });
    }
  }

  reset(): void {
    this.dato = { nombre: '', descuento: 0 };
    this.editando = false;
  }

  cerrarModal(): void {
    this.modal?.hide();
    this.reset();
  }

}
