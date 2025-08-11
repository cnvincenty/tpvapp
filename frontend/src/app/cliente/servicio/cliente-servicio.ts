import { HttpClient } from '@angular/common/http';
import { environment } from './../../../environments/environment.development';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cliente } from '../dominio/cliente';

@Injectable({
  providedIn: 'root'
})
export class ClienteServicio {

  private readonly url = `${environment.apiUrl}/v1/cliente`;

  constructor(private httpCliente: HttpClient) {}

  obtenerTodos(): Observable<Cliente[]> {
    return this.httpCliente.get<Cliente[]>(this.url);
  }

  obtenerPorId(id: number): Observable<Cliente> {
    return this.httpCliente.get<Cliente>(`${this.url}/${id}`);
  }

  obtenerPorCodigo(codigo: string): Observable<Cliente> {
    return this.httpCliente.get<Cliente>(`${this.url}/buscarPorCodigo?codigo=${codigo}`);
  }

  crear(entrada: Cliente): Observable<Cliente> {
    return this.httpCliente.post<Cliente>(this.url, entrada);
  }

  actualizar(entrada: Cliente): Observable<Cliente> {
    return this.httpCliente.put<Cliente>(`${this.url}/${entrada.id}`, entrada);
  }

  eliminarPorId(id: number): Observable<Cliente> {
    return this.httpCliente.delete<Cliente>(`${this.url}/${id}`);
  }
}
