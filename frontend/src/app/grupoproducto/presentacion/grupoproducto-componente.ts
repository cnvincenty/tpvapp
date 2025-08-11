import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Grupoproducto } from '../dominio/grupoproducto';
import { GrupoproductoServicio } from '../servicio/grupoproducto-servicio';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Modal } from 'bootstrap';

@Component({
  selector: 'app-grupoproducto-componente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './grupoproducto-componente.html',
  styleUrl: './grupoproducto-componente.css'
})
export class GrupoproductoComponente implements OnInit{

  @ViewChild('ventanaModal') ventanaModalRef!: ElementRef; modal?: Modal;

  datos: Grupoproducto[] = [];
  dato: Grupoproducto = { nombre: '', descuento: 0 };
  editando: boolean = false;

  constructor(private servicio: GrupoproductoServicio) { }

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

  editar(dato: Grupoproducto): void {
    this.dato = { ...dato };
    this.editando = true;

    if (!this.modal) {
      this.modal = new Modal(this.ventanaModalRef.nativeElement);
    }
    this.modal.show();
  }


  eliminar(id: number): void {
    if (confirm('¿Deseas eliminar este grupoproducto?')) {
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
