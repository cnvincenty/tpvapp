import { HttpClient } from '@angular/common/http';
import { environment } from './../../../environments/environment.development';
import { Injectable } from '@angular/core';
import { Grupocliente } from '../dominio/grupocliente';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GrupoclienteServicio {
  private readonly url = `${environment.apiUrl}/v1/grupocliente`;

  constructor(private httpCliente: HttpClient) {}

  obtenerTodos(): Observable<Grupocliente[]> {
    return this.httpCliente.get<Grupocliente[]>(this.url);
  }

  obtenerPorId(id: number): Observable<Grupocliente> {
    return this.httpCliente.get<Grupocliente>(`${this.url}/${id}`);
  }

  crear(entrada: Grupocliente): Observable<Grupocliente> {
    return this.httpCliente.post<Grupocliente>(this.url, entrada);
  }

  actualizar(entrada: Grupocliente): Observable<Grupocliente> {
    return this.httpCliente.put<Grupocliente>(`${this.url}/${entrada.id}`, entrada);
  }

  eliminarPorId(id: number): Observable<Grupocliente> {
    return this.httpCliente.delete<Grupocliente>(`${this.url}/${id}`);
  }
}
