import { Facturaventaitem } from './facturaventaitem';
export interface Facturaventa {
    id?:number;
    fecha: Date;
    clienteId: number;
    clienteNombre: string;
    clienteGrupo: string;
    almacen: string;
    condicionPago: string;
    total: number;
    items: Facturaventaitem[];
}
