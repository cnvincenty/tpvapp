export interface Cliente {
  id?: number;
  codigo: string;
  nombre: string;
  tipoDocumento: string;
  numeroDocumento: string;
  email: string;
  grupoclienteId: number;
}
