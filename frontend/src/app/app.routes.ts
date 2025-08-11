import { Routes } from '@angular/router';
import { Index } from './principal/index';

export const routes: Routes = [
  {
    path: '',
    component: Index,
  },
  {
    path: 'grupoproducto',
    loadComponent: () =>
      import('./grupoproducto/presentacion/grupoproducto-componente').then(
        (m) => m.GrupoproductoComponente
      ),
  },
  {
    path: 'producto',
    loadComponent: () =>
      import('./producto/presentacion/producto-componente').then(
        (m) => m.ProductoComponente
      ),
  },
  {
    path: 'grupocliente',
    loadComponent: () =>
      import('./grupocliente/presentacion/grupocliente-componente').then(
        (m) => m.GrupoclienteComponente
      ),
  },
  {
    path: 'cliente',
    loadComponent: () =>
      import('./cliente/presentacion/cliente-componente').then(
        (m) => m.ClienteComponente
      ),
  },
  {
    path: 'venta',
    loadComponent: () =>
      import('./ventas/presentacion/ventas-componente').then(
        (m) => m.VentasComponente
      ),
  },
];
