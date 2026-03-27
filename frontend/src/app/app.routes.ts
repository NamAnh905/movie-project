import { Routes } from '@angular/router';

// ==========================================
// 1. LAYOUTS
// ==========================================
import { PublicLayoutComponent } from './features/public/public-layout.component';
import { AdminLayoutComponent } from './features/admin/admin-layout.component';
import { AuthLayoutComponent } from './features/auth/auth-layout.component';

// ==========================================
// 2. GUARDS
// ==========================================
import { AdminGuard } from './core/guards/admin.guard';

// ==========================================
// 3. FEATURES: PUBLIC
// ==========================================
import { MovieListComponent } from './features/public/movies/movie-list/movie-list.component';
import { MovieDetailComponent } from './features/public/movies/movie-detail/movie-detail.component';
import { MovieAllComponent } from './features/public/movies/movie-all/movie-all.component';
import { CinemaPublicComponent } from './features/public/cinemas/cinema-public/cinema-public.component';
import { ShowtimePublicComponent } from './features/public/showtimes/showtime-public.component';
import { PromotionPageComponent } from './features/public/promotions/promotion-page.component';
import { AccountComponent } from './features/public/account/account.component';

// Phân hệ Đặt vé Khách hàng (Đã dọn dẹp sạch sẽ)
import { BookingHistoryComponent } from './features/public/bookings/history/booking-history.component';
import { BookingFormComponent } from './features/public/bookings/form/booking-form.component';
import { BookingSuccessComponent } from './features/public/bookings/booking-success.component';

// ==========================================
// 4. FEATURES: AUTH
// ==========================================
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';

// ==========================================
// 5. FEATURES: ADMIN (Đã được Gộp Component 100%)
// ==========================================
import { AdminRevenueComponent } from './features/admin/revenue/admin-revenue.component';
import { AdminMovieComponent } from './features/admin/movies/admin-movie.component';
import { AdminGenreComponent } from './features/admin/genres/admin-genre.component';
import { AdminCinemaComponent } from './features/admin/cinemas/admin-cinema.component';
import { AdminUserComponent } from './features/admin/users/admin-user.component';
import { AdminBookingComponent } from './features/admin/bookings/admin-booking.component';
import { AdminShowtimeComponent } from './features/admin/showtimes/admin-showtime.component';

export const routes: Routes = [
  // ==== Public ====
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', redirectTo: 'movies', pathMatch: 'full' },
      { path: 'movies', component: MovieListComponent },
      { path: 'movies/all', component: MovieAllComponent },
      { path: 'movies/:id', component: MovieDetailComponent },
      { path: 'cinemas', component: CinemaPublicComponent },
      { path: 'showtimes', component: ShowtimePublicComponent },
      { path: 'promotions', component: PromotionPageComponent },
      { path: 'account', component: AccountComponent },

      // Các route Đặt vé
      { path: 'bookings', component: BookingHistoryComponent },          // Trang lịch sử đơn hàng
      { path: 'bookings/new', component: BookingFormComponent },         // Form thanh toán (nhận query ?showtimeId=...)
      { path: 'bookings/success', component: BookingSuccessComponent },  // Hứng callback từ VNPAY
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
    children: [
      { path: '', redirectTo: 'revenue', pathMatch: 'full' },
      { path: 'revenue', component: AdminRevenueComponent },
      { path: 'movies', component: AdminMovieComponent },
      { path: 'genres', component: AdminGenreComponent },
      { path: 'cinemas', component: AdminCinemaComponent },
      { path: 'users', component: AdminUserComponent },
      { path: 'bookings', component: AdminBookingComponent },
      { path: 'showtimes', component: AdminShowtimeComponent }, // Đã gộp thành 1 dòng duy nhất!
    ],
  },

  // 👉 Fallback: về trang chủ thay vì màn hình trắng
  { path: '**', redirectTo: '' }
];
