import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { BookingService } from '../../bookings/booking.service';
import { ShowtimeService } from '../showtime.service';

type Mode = 'byCinema' | 'byMovie';

interface TimeVM {
  id: string;          // chỉ dùng trackBy/hiển thị
  startLabel: string;  // "HH:mm"
}

interface CardVM {
  id: number;          // byCinema: movieId; byMovie: cinemaId
  title: string;
  posterUrl?: string | null;
  times: TimeVM[];
  movieId?: number;    // byCinema
  cinemaId?: number;   // byMovie
}

interface CinemaVM { id: number; name: string; }

@Component({
  standalone: true,
  selector: 'app-showtime-public',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './showtime-public.component.html',
  styleUrls: ['./showtime-public.component.scss']
})
export class ShowtimePublicComponent implements OnInit {
  movieIdFromRoute: number | null = null;
  movieInfo?: { id: number; title: string; posterUrl?: string | null };

  mode: Mode = 'byCinema';

  // Filters
  selectedCinemaId: number | null = null;
  selectedDate: string = this.todayYMD(); // luôn "YYYY-MM-DD"

  // Data
  groups: CardVM[] = [];
  cinemas: CinemaVM[] = [];
  errorMsg = '';

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    private booking: BookingService,
    private showtimes: ShowtimeService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(pm => {
      const mid = +(pm.get('id') || pm.get('movieId') || '0');
      if (mid > 0) {
        this.mode = 'byMovie';
        this.movieIdFromRoute = mid;
        this.loadMovieInfo(mid);
      } else {
        this.mode = 'byCinema';
        this.movieIdFromRoute = null;
      }

      if (this.mode === 'byCinema') this.loadCinemas();
      this.load();
    });
  }

  // ======= date binding fix =======
  onDateChange(v: string) {
    // v từ <input type="date"> đã là "YYYY-MM-DD"
    this.selectedDate = (v || '').slice(0, 10);
    this.load();
  }

  // ======= LOAD DATA =======

  private loadMovieInfo(id: number) {
    const url = `${this.apiOrigin()}/api/movies/${id}`;
    this.http.get<any>(url).subscribe({
      next: mv => {
        this.movieInfo = {
          id: Number(mv.id ?? id),
          title: String(mv.title ?? mv.name ?? ''),
          posterUrl: this.fullPosterUrl(mv.posterUrl ?? null),
        };
      },
      error: () => this.movieInfo = undefined
    });
  }

  load(): void {
    this.errorMsg = '';
    this.groups = [];

    if (this.mode === 'byMovie') {
      if (!this.movieIdFromRoute) { this.errorMsg = 'Thiếu movieId.'; return; }

      const url = `${this.apiOrigin()}/api/showtimes/public/by-movie`;
      const params = new HttpParams()
        .set('movieId', String(this.movieIdFromRoute))
        .set('date', this.selectedDate);

      this.http.get<any[]>(url, { params }).subscribe({
        next: (res) => {
          const groups = (Array.isArray(res) ? res : []).map((r: any) => {
            const times = (r.times ?? []).map((t: string) => ({
              id: `${r.cinemaId}_${t}`,
              startLabel: this.fromTimeValue(t)
            }));
            const card: CardVM = {
              id: Number(r.cinemaId),
              cinemaId: Number(r.cinemaId),
              title: String(r.cinemaName ?? ''),
              posterUrl: null,
              times
            };
            return card;
          });
          if (!groups.length) this.errorMsg = 'Phim này chưa có suất cho ngày đã chọn.';
          this.groups = groups;
        },
        error: () => {
          this.errorMsg = 'Không tải được dữ liệu.';
          this.groups = [];
        }
      });
      return;
    }

    // byCinema
    if (!this.selectedCinemaId) { this.errorMsg = 'Chưa chọn rạp.'; this.groups = []; return; }

    const url = `${this.apiOrigin()}/api/showtimes/public`;
    const params = new HttpParams()
      .set('cinemaId', String(this.selectedCinemaId))
      .set('date', this.selectedDate);

    this.http.get<any[]>(url, { params }).subscribe({
      next: (res) => {
        const groups = (Array.isArray(res) ? res : []).map((r: any) => {
          const movieId = Number(r.movieId ?? r.movie_id ?? r.movie?.id ?? 0);
          const movieTitle = String(r.movieTitle ?? r.title ?? r.movie?.title ?? '');
          const posterUrl = this.fullPosterUrl(r.posterUrl ?? r.movie?.posterUrl ?? null);
          const times = (r.times ?? []).map((t: string) => ({
            id: `${movieId}_${t}`,
            startLabel: this.fromTimeValue(t)
          }));
          const card: CardVM = { id: movieId, movieId, title: movieTitle, posterUrl, times };
          return card;
        });
        if (!groups.length) this.errorMsg = 'Rạp này chưa có suất cho ngày đã chọn.';
        this.groups = groups;
      },
      error: () => {
        this.errorMsg = 'Không tải được dữ liệu.';
        this.groups = [];
      }
    });
  }

  /** Tải danh sách rạp (dropdown) cho chế độ byCinema */
  private loadCinemas() {
    const url = `${this.apiOrigin()}/api/cinemas/public`;
    this.http.get<any[]>(url).subscribe({
      next: (res) => {
        this.cinemas = (Array.isArray(res) ? res : []).map((c: any) => ({
          id: Number(c.id ?? c.cinemaId ?? 0),
          name: String(c.name ?? c.cinemaName ?? '')
        })).filter((c: CinemaVM) => !!c.id && !!c.name);
      },
      error: () => { this.cinemas = []; }
    });
  }

  // ======= ACTION: QUICK BOOK =======

  quickBookTime(t: TimeVM, g: CardVM) {
    // Nếu phần tử time đã chứa showtimeId (số), điều hướng thẳng
    const maybeId = Number((t as any)?.id);
    if (Number.isFinite(maybeId) && maybeId > 0) {
      this.router.navigate(['/bookings/new', maybeId]);  // /bookings/new/:showtimeId
      return;
    }

    // Chỉ có HH:mm -> resolve trước rồi điều hướng
    const date = (this.selectedDate || new Date().toISOString().slice(0, 10)).slice(0, 10);
    const movieId  = this.mode === 'byMovie' ? this.movieIdFromRoute : g.movieId!;
    const cinemaId = this.mode === 'byMovie' ? g.cinemaId!         : this.selectedCinemaId!;

    const params = new HttpParams()
      .set('movieId', String(movieId))
      .set('cinemaId', String(cinemaId))
      .set('date', date)
      .set('time', t.startLabel.slice(0, 5));

    this.http.get<{ id: number }>(`${this.apiOrigin()}/api/showtimes/resolve`, { params })
      .subscribe({
        next: res => this.router.navigate(['/bookings/new', res.id]),
        error: _ => alert('Không tìm thấy suất chiếu phù hợp.')
      });
  }

  // ======= helpers =======
  trackByMovieId = (_: number, g: CardVM) => g.id;
  trackByTime = (_: number, t: TimeVM) => t.id;

  private apiOrigin(): string {
    return location.port === '4200'
      ? location.origin.replace(':4200', ':8080')
      : location.origin;
  }

  private fullPosterUrl(p?: string | null): string {
    if (!p) return 'assets/posters/banner.png';
    const base = this.apiOrigin().replace(/\/+$/, '');
    const path = p.startsWith('/') ? p : '/' + p;
    return base + path;
  }

  onImgError(ev: Event) {
    const img = ev.target as HTMLImageElement;
    if (img && img.src && !img.src.includes('assets/posters/banner.png')) {
      img.src = 'assets/posters/banner.png';
    }
  }

  private fromTimeValue(v: any): string {
    if (v == null) return '';
    const s = String(v);
    const m = s.match(/(\d{2}):(\d{2})/);
    return m ? `${m[1]}:${m[2]}` : s;
  }

  private todayYMD(): string {
    const d = new Date();
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }
}
