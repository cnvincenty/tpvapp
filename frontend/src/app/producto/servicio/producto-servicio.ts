import { HttpClient } from '@angular/common/http';
import { environment } from './../../../environments/environment.development';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Producto } from '../dominio/producto';

@Injectable({
  providedIn: 'root'
})
export class ProductoServicio {

  private readonly url = `${environment.apiUrl}/v1/producto`;

  constructor(private httpProducto: HttpClient) {}

  obtenerTodos(): Observable<Producto[]> {
    return this.httpProducto.get<Producto[]>(this.url);
  }

  obtenerPorIdGrupoproducto(id: number): Observable<Producto[]> {
    return this.httpProducto.get<Producto[]>(`${this.url}/porGrupo/${id}`);
  }

  obtenerPorId(id: number): Observable<Producto> {
    return this.httpProducto.get<Producto>(`${this.url}/${id}`);
  }

  crear(entrada: Producto): Observable<Producto> {
    return this.httpProducto.post<Producto>(this.url, entrada);
  }

  actualizar(entrada: Producto): Observable<Producto> {
    return this.httpProducto.put<Producto>(`${this.url}/${entrada.id}`, entrada);
  }

  eliminarPorId(id: number): Observable<Producto> {
    return this.httpProducto.delete<Producto>(`${this.url}/${id}`);
  }

  subirImagen(archivo: File, id: number): Observable<any>{
    const formArchivo = new FormData();
    formArchivo.append("archivo", archivo);
    return this.httpProducto.post<void>(`${this.url}/subirImagen/${id}`, formArchivo, {
      reportProgress: true,
      responseType: 'json',
    });
  }

  descargarImagen(nombreArchivo: string){
    return this.httpProducto.get(`${this.url}/descargarImagen/${nombreArchivo}`, {
      responseType: 'blob',
    });
  }


}
