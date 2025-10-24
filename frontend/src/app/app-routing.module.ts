// src/app/app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MovieListComponent } from './features/movies/movie-list/movie-list.component';
import { MovieDetailComponent } from './features/movies/movie-detail/movie-detail.component';
import { CinemaListComponent } from './features/cinemas/cinema-list/cinema-list.component';
import { GenreListComponent } from './features/genres/genre-list/genre-list.component';
import { ShowtimePublicComponent } from './features/showtimes/showtime-public/showtime-public.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { MovieAllComponent } from './features/movies/movie-all/movie-all.component';
import { CinemaPublicComponent } from './features/cinemas/cinema-public/cinema-public.component';
import { PromotionPageComponent } from './features/promotions/promotion-page.component';
import { BookingSuccessComponent } from './features/bookings/booking-success.component';
import { BookingFormComponent }    from './features/bookings/form/booking-form.component';
import { BookingHistoryComponent } from './features/bookings/history/booking-history.component';
import { BookingDetailComponent }  from './features/bookings/detail/booking-detail.component';

import { PublicLayoutComponent } from './features/public/public-layout.component';
import { AdminLayoutComponent } from './features/admin/admin-layout.component';

import { AdminGuard } from './features/admin/admin.guard';
import { AdminMovieListComponent } from './features/admin/movies/admin-movie-list.component';
import { AdminMovieFormComponent } from './features/admin/movies/admin-movie-form.component';
import { AdminCinemaListComponent } from './features/admin/cinemas/admin-cinema-list.component';
import { AdminCinemaFormComponent } from './features/admin/cinemas/admin-cinema-form.component';
import { AdminShowtimeListComponent } from './features/admin/showtimes/admin-showtime-list.component';
import { AdminShowtimeFormComponent } from './features/admin/showtimes/admin-showtime-form.component';
import { AdminUserListComponent } from './features/admin/users/admin-user-list.component';
import { AdminRevenueComponent } from './features/admin/revenue/admin-revenue.component';
import { AdminBookingListComponent } from './features/admin/bookings/admin-booking-list.component';
import { AdminBookingDetailComponent } from './features/admin/bookings/admin-booking-detail.component';

import { AccountComponent } from './features/account/account.component';

// Auth pages
import { AuthLayoutComponent } from './features/auth/auth-layout.component';

const routes: Routes = [
  // 👉 Trang mặc định khi mở "/": vào trang public (movies)
  { path: '', pathMatch: 'full', redirectTo: 'movies' },

  // ==== Public (User) ====
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: 'movies', component: MovieListComponent },
      { path: 'movies/all', component: MovieAllComponent },
      { path: 'movies/:id', component: MovieDetailComponent },
      { path: 'movies/:id/showtimes', component: ShowtimePublicComponent },
      // { path: 'cinemas', component: CinemaListComponent },
      { path: 'cinemas', component: CinemaPublicComponent },
      { path: 'genres', component: GenreListComponent },
      { path: 'showtimes', component: ShowtimePublicComponent },
      { path: 'promotions', component: PromotionPageComponent },
      { path: 'account', component: AccountComponent },
      { path: 'bookings/success/:id', component: BookingSuccessComponent },
      { path: 'bookings/success',     component: BookingSuccessComponent },
      { path: 'bookings',                 component: BookingHistoryComponent },
      { path: 'bookings/new/:showtimeId', component: BookingFormComponent },
      { path: 'bookings/:id',             component: BookingDetailComponent },
    ],
  },

  // ==== Auth (no header) ====
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: [
      { path: '', redirectTo: 'login', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
    ]
  },

  // ==== Admin (protected) ====
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AdminGuard],
    // (tuỳ chọn an toàn hơn) có thể thêm: canActivateChild: [AdminGuard],
    children: [
      { path: '', redirectTo: 'revenue', pathMatch: 'full' },
      { path: 'movies', component: AdminMovieListComponent },
      { path: 'movies/new', component: AdminMovieFormComponent },
      { path: 'movies/:id', component: AdminMovieFormComponent },
      { path: 'genres', component: GenreListComponent },
      { path: 'cinemas', component: AdminCinemaListComponent },
      { path: 'cinemas/new', component: AdminCinemaFormComponent },   // <-- thêm
      { path: 'cinemas/:id', component: AdminCinemaFormComponent },
      { path: 'showtimes', component: AdminShowtimeListComponent },   // Danh sách
      { path: 'showtimes/new', component: AdminShowtimeFormComponent },
      { path: 'users', component: AdminUserListComponent },
      { path: 'revenue', component: AdminRevenueComponent },
      { path: 'bookings', component: AdminBookingListComponent },
      { path: 'bookings/:id', component: AdminBookingDetailComponent },
    ],
  },

  // 👉 Fallback: về trang public thay vì đẩy về login
  { path: '**', redirectTo: 'movies' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
