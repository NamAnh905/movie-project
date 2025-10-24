import { Routes } from '@angular/router';

export const CINEMA_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./cinema-list/cinema-list.component').then(m => m.CinemaListComponent) }
];
