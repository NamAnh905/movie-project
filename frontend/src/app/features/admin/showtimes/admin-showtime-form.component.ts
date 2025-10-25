import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

import { MovieService } from '../../movies/movie.service';
import { CinemaService, Cinema } from '../../cinemas/cinema.service';
import { ShowtimeService, ShowtimeCreate } from '../../showtimes/showtime.service';

type Movie = { id: number; title: string; duration?: number | null; status?: string | null };

@Component({
  selector: 'app-admin-showtime-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  providers: [DatePipe],
  templateUrl: './admin-showtime-form.component.html',
  styleUrls: ['./admin-showtime-form.component.scss']
})
export class AdminShowtimeFormComponent implements OnInit {
  private moviesApi = inject(MovieService);
  private cinemasApi = inject(CinemaService);
  private showtimesApi = inject(ShowtimeService); // giữ lại nếu bạn còn dùng ở nơi khác
  private datePipe = inject(DatePipe);
  private router = inject(Router);
  private http = inject(HttpClient);

  // ===== form fields =====
  date: string = '';                 // yyyy-MM-dd
  movieId: number | null = null;
  cinemaId: number | null = null;
  newTime: string = '';              // HH:mm
  times: string[] = [];

  // ➕ mới: giá & số vé
  price: number = 85000;
  capacity: number = 50;

  movies: Movie[] = [];
  cinemas: Cinema[] = [];
  saving = false;

  private pickArray<T = any>(res: any): T[] {
    const cand =
      res?.content ?? res?.items ?? res?.results ??
      res?.data?.content ?? res?.data?.items ?? res?.data?.results ??
      res?.data ?? res;
    return Array.isArray(cand) ? cand : [];
  }

  /** Nhận biết một record phim là đang chiếu */
  private isReleased(x: any): boolean {
    const s = String(x?.status ?? x?.movieStatus ?? x?.state ?? '').toUpperCase();
    return s === 'RELEASED' || s === 'NOW_SHOWING' || s === 'DANG_CHIEU';
  }

  ngOnInit(): void {
    this.date = this.datePipe.transform(new Date(), 'yyyy-MM-dd') || '';

    // PHIM: lọc chỉ phim đang chiếu
    this.moviesApi.list({ page: 0, size: 1000 }).subscribe({
      next: (r) => {
        const raw = this.pickArray<any>(r);
        const released = raw.filter((x: any) => this.isReleased(x));
        this.movies = released.map(x => ({
          id: x.id, title: x.title, duration: x.duration, status: x.status
        }));
        if (!this.movieId && this.movies.length) this.movieId = this.movies[0].id;
      }
    });

    // RẠP
    this.cinemasApi.list({ page: 0, size: 1000 }).subscribe({
      next: (r) => {
        this.cinemas = this.pickArray<Cinema>(r);
        if (!this.cinemaId && this.cinemas.length) this.cinemaId = this.cinemas[0].id;
      }
    });
  }

  addTime() {
    const t = (this.newTime || '').trim();
    if (!t) return;
    if (!/^\d{2}:\d{2}$/.test(t)) return;
    if (this.times.includes(t)) return;
    this.times = [...this.times, t];
    this.newTime = '';
  }

  removeTime(t: string) {
    this.times = this.times.filter(x => x !== t);
  }

  get rows() {
    if (!this.date || !this.movieId || !this.cinemaId) return [];
    const m = this.movies.find(x => x.id === this.movieId);
    const c = this.cinemas.find(x => x.id === this.cinemaId);
    if (!m || !c) return [];
    return [...this.times].sort().map((tm, i) => ({
      idx: i + 1, date: this.date, time: tm, movie: m.title, cinema: c.name
    }));
  }

  get canSave(): boolean {
    return !!(this.date && this.movieId && this.cinemaId && this.times.length && this.price > 0 && this.capacity > 0);
  }

  /** origin BE: FE 4200 -> BE 8080 khi dev; prod dùng cùng origin */
  private apiOrigin(): string {
    return environment.baseUrl;
  }

  save() {
    if (!this.canSave) return;

    // 👉 dùng endpoint admin mới: POST /api/admin/showtimes
    const url = `${this.apiOrigin()}/api/admin/showtimes`;
    const body = {
      cinemaId: this.cinemaId!,
      movieId: this.movieId!,
      date: this.date,
      times: [...this.times].sort(),
      price: Number(this.price),
      capacity: Number(this.capacity),
    };

    this.saving = true;
    this.http.post<any>(url, body).subscribe({
      next: (res) => {
        const skipped = Array.isArray(res?.skippedTimes) && res.skippedTimes.length
          ? ` (bỏ qua trùng: ${res.skippedTimes.join(', ')})` : '';
        alert('✅ Đã tạo lịch chiếu!' + skipped);
        this.router.navigate(['/admin/showtimes']);
      },
      error: (e) => {
        if (e?.status === 409) {
          alert('❌ Trùng lịch chiếu ở 1 hoặc nhiều khung giờ.');
        } else {
          alert('❌ Lỗi tạo lịch chiếu: ' + (e?.error?.message || e?.message || 'Unknown error'));
        }
      },
      complete: () => this.saving = false
    });
  }

  trackByIdx = (_: number, r: any) => r.idx;
}
