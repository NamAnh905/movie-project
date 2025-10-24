import { Routes } from '@angular/router';

export const SHOWTIME_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./showtime-public/showtime-public.component').then(m => m.ShowtimePublicComponent) },
  { path: 'admin', loadComponent: () => import('./showtime-admin/showtime-admin.component').then(m => m.ShowtimeAdminComponent) }
];
