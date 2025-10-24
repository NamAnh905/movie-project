import { Routes } from '@angular/router';

export const MOVIE_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./movie-list/movie-list.component').then(m => m.MovieListComponent) },
  { path: 'movies/:id', loadComponent: () => import('./movie-detail/movie-detail.component').then(m => m.MovieDetailComponent) },
  { path: 'admin/movies/new', loadComponent: () => import('../admin/movies/admin-movie-form.component').then(m => m.AdminMovieFormComponent) },
  { path: 'admin/movies/:id', loadComponent: () => import('../admin/movies/admin-movie-form.component').then(m => m.AdminMovieFormComponent) },
];
