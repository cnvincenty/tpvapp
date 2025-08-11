import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Facturaventa } from '../dominio/facturaventa';

@Injectable({
  providedIn: 'root'
})
export class FacturaventaServicio {

  private readonly url = `${environment.apiUrl}/v1/facturaventa`;

  constructor(private httpCliente: HttpClient) {}

  listarFacturasVenta(): Observable<Facturaventa[]> {
    return this.httpCliente.get<Facturaventa[]>(this.url);
  }

  obtenerFacturaVenta(id: number): Observable<Facturaventa> {
    return this.httpCliente.get<Facturaventa>(`${this.url}/${id}`);
  }

  registrarFacturaVenta(entrada: Facturaventa): Observable<Facturaventa> {
    return this.httpCliente.post<Facturaventa>(this.url, entrada);
  }
}
