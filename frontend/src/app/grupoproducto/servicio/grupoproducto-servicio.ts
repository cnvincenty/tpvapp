import { environment } from './../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Grupoproducto } from '../dominio/grupoproducto';

@Injectable({
  providedIn: 'root'
})
export class GrupoproductoServicio {

  private readonly url = `${environment.apiUrl}/v1/grupoproducto`;

  constructor(private httpCliente: HttpClient) {}

  obtenerTodos(): Observable<Grupoproducto[]> {
    return this.httpCliente.get<Grupoproducto[]>(this.url);
  }

  obtenerPorId(id: number): Observable<Grupoproducto> {
    return this.httpCliente.get<Grupoproducto>(`${this.url}/${id}`);
  }

  crear(entrada: Grupoproducto): Observable<Grupoproducto> {
    return this.httpCliente.post<Grupoproducto>(this.url, entrada);
  }

  actualizar(entrada: Grupoproducto): Observable<Grupoproducto> {
    return this.httpCliente.put<Grupoproducto>(`${this.url}/${entrada.id}`, entrada);
  }

  eliminarPorId(id: number): Observable<Grupoproducto> {
    return this.httpCliente.delete<Grupoproducto>(`${this.url}/${id}`);
  }
}
