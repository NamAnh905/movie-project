import { Routes } from '@angular/router';

export const GENRE_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./genre-list/genre-list.component').then(m => m.GenreListComponent) }
];
