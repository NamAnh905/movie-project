import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { forkJoin } from 'rxjs';                         // NEW
import { CinemaService, Cinema, Showtime, Movie } from '../cinema.service';
import { environment } from '../../../../environments/environment';

type Group = { movie: Movie; showtimes: Showtime[] };

@Component({
  standalone: true,
  selector: 'app-cinema-public',
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  templateUrl: './cinema-public.component.html',
  styleUrls: ['./cinema-public.component.scss']
})
export class CinemaPublicComponent implements OnInit {
  private readonly FALLBACK_POSTER = 'assets/posters/banner.png';
  private svc = inject(CinemaService);

  // state
  cinemas = signal<Cinema[]>([]);
  selectedCinemaId = signal<number | null>(null);

  selectedCinema = computed(() => {
    const id = this.selectedCinemaId();
    const list = this.cinemas();
    return list.find(c => c.id === id) || list[0] || null;
  });

  onSelectCinema(val: any) {
    const id = Number(val);
    if (!Number.isFinite(id)) return;
    if (this.selectedCinemaId() !== id) {
      this.selectedCinemaId.set(id);
      this.onReload(); // nạp lại lịch chiếu theo rạp mới
    }
  }

  /** Mặc định hôm nay; KHÔNG hiển thị ở UI nữa */
  selectedDate = signal<string>(''); // yyyy-MM-dd (LOCAL)

  loading = signal<boolean>(false);
  rawShowtimes = signal<any[]>([]);

  activeTab = signal<'now' | 'soon' | 'special' | 'price'>('now');

  // === NEW: cache danh sách phim theo status
  released = signal<Movie[]>([]);
  coming   = signal<Movie[]>([]);

  // === NEW: set id nhanh
  releasedIds = computed(() => new Set(this.released().map(m => m.id)));
  comingIds   = computed(() => new Set(this.coming().map(m => m.id)));

  // ===== Utils =====
  private fmtYMDLocal(d: Date) {
    const y = d.getFullYear();
    const m = (d.getMonth() + 1).toString().padStart(2, '0');
    const day = d.getDate().toString().padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  /** Gom dữ liệu lịch chiếu thành Group[] (KHÔNG phân tab ở đây) */
  private groupsAll = computed<Group[]>(() => {
    const src = this.rawShowtimes();
    if (!Array.isArray(src) || src.length === 0) return [];

    const d = this.selectedDate() || this.fmtYMDLocal(new Date());
    const first: any = src[0];

    // ==== KIỂU MỚI: [{ movieId, movieTitle, posterUrl, times: string[] }]
    if (first && ('movieId' in first) && Array.isArray(first.times)) {
      return src.map((it: any) => {
        const movieId = Number(it.movieId);
        const title = it.movieTitle ?? 'Phim';
        const posterUrl = this.toAbsolute(it.posterUrl || '');

        const mov: Movie = { id: movieId, title, posterUrl } as Movie;
        const showtimes: Showtime[] = (it.times || []).map((t: string, idx: number) => {
          const iso = `${d}T${t}:00`;
          let start = new Date(iso);
          if (isNaN(start.getTime())) start = new Date(`${d} ${t}:00`);
          return { id: `${movieId}_${idx}`, start_time: start } as unknown as Showtime;
        });
        return { movie: mov, showtimes };
      });
    }

    // ==== KIỂU CŨ: mảng suất chiếu — gom theo movie
    const byMovie = new Map<number, Group>();
    for (const st of src) {
      const s: any = st;
      const mid = Number(s?.movie?.id ?? s['movie_id'] ?? s['movieId']);
      if (!Number.isFinite(mid)) continue;

      const title: string =
        s?.movie?.title ??
        s['movie_title'] ??
        s['movieTitle'] ??
        s['title'] ?? 'Phim';

      const posterUrl =
        s?.movie?.posterUrl ??
        s?.movie?.poster ??
        s['posterUrl'] ?? s['poster'] ?? '';

      const mv: Movie = s?.movie?.id ? s.movie : ({ id: mid, title, posterUrl: this.toAbsolute(posterUrl) } as Movie);
      if (!byMovie.has(mid)) byMovie.set(mid, { movie: mv, showtimes: [] });
      byMovie.get(mid)!.showtimes.push(st as Showtime);
    }
    return Array.from(byMovie.values());
  });

  // === NEW: nhóm cho tab "ĐANG CHIẾU"
  groupsNow = computed<Group[]>(() => {
    const ids = this.releasedIds();
    return this.groupsAll().filter(g => ids.has(g.movie.id));
  });

  // === NEW: nhóm cho tab "SẮP CHIẾU"
  groupsSoon = computed<Group[]>(() => {
    const base = new Map(this.groupsAll().map(g => [g.movie.id, g]));
    const out: Group[] = [];
    for (const mv of this.coming()) {
      const g = base.get(mv.id);
      if (g) out.push({ movie: { ...g.movie, ...mv }, showtimes: g.showtimes });
      else  out.push({ movie: mv, showtimes: [] }); // chưa có suất -> vẫn show poster/tên
    }
    return out;
  });

  ngOnInit(): void {
    if (!this.selectedDate()) this.selectedDate.set(this.fmtYMDLocal(new Date()));
    // tải danh sách rạp & phim theo status
    this.loadCinemas();
    this.loadMoviesByStatus();
  }

  private loadMoviesByStatus() {
    forkJoin({
      released: this.svc.getMoviesByStatus('RELEASED'),
      coming:   this.svc.getMoviesByStatus('COMING_SOON')
    }).subscribe({
      next: ({ released, coming }) => {
        this.released.set(released || []);
        this.coming.set(coming || []);
      },
      error: (e) => {
        console.warn('getMoviesByStatus error', e);
        this.released.set([]); this.coming.set([]);
      }
    });
  }

  loadCinemas() {
    this.loading.set(true);
    this.svc.getCinemasPublic()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (rs) => {
          const list = this.normalizeArray(rs);
          this.cinemas.set(list || []);
          if (!this.selectedCinemaId() && this.cinemas().length) {
            this.selectedCinemaId.set(this.cinemas()[0].id);
            this.onReload();
          }
        },
        error: (err) => {
          console.error('getCinemasPublic error:', err);
          this.cinemas.set([]);
        }
      });
  }

  onReload() {
    const cid = this.selectedCinemaId();
    if (!cid) return;
    const raw = this.selectedDate();
    const d = this.toDateSafe(raw);
    const ymd = this.fmtYMDLocal(d);

    this.loading.set(true);
    this.svc.getShowtimesByCinema(cid, ymd)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (rs) => {
          const arr = this.normalizeArray(rs);
          this.rawShowtimes.set(arr);
        },
        error: (err) => {
          console.error('getShowtimesByCinema error:', err);
          this.rawShowtimes.set([]);
        }
      });
  }

  private toDateSafe(v: any): Date {
    if (v instanceof Date) return v;
    const s = String(v ?? '').trim();
    const m1 = /^(\d{4})-(\d{2})-(\d{2})$/.exec(s);
    if (m1) return new Date(+m1[1], +m1[2] - 1, +m1[3]);
    const m2 = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/.exec(s);
    if (m2) return new Date(+m2[3], +m2[1] - 1, +m2[2]);
    const m3 = /^(\d{1,2})-(\d{1,2})-(\d{4})$/.exec(s) || /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/.exec(s);
    if (m3) return new Date(+m3[3], +m3[2] - 1, +m3[1]);
    return new Date();
  }

  private normalizeArray(rs: any): any[] {
    if (Array.isArray(rs)) return rs;
    if (!rs || typeof rs !== 'object') return [];
    if (Array.isArray(rs.content)) return rs.content;
    if (Array.isArray(rs.data)) return rs.data;
    if (Array.isArray(rs.items)) return rs.items;
    if (Array.isArray(rs.result)) return rs.result;
    if (Array.isArray(rs.rows)) return rs.rows;
    const d = rs.data || {};
    if (Array.isArray(d.content)) return d.content;
    if (Array.isArray(d.items)) return d.items;
    if (Array.isArray(d.result)) return d.result;
    if (Array.isArray(d.rows)) return d.rows;
    return [];
  }

  movieTitle(g: any): string {
    return g?.movie?.title || g?.movieTitle || g?.times?.[0]?.movieTitle || 'Phim';
  }

  posterOf(g: any): string {
    const url =
      g?.movie?.posterUrl ||
      g?.movie?.poster ||
      g?.posterUrl ||
      g?.times?.[0]?.posterUrl || '';
    return url ? this.toAbsolute(url) : this.FALLBACK_POSTER;
  }

  imgFallback(ev: Event) {
    const img = ev.target as HTMLImageElement;
    if (!img) return;
    if (img.src.includes(this.FALLBACK_POSTER)) return;
    img.src = this.FALLBACK_POSTER;
  }

  private apiOrigin(): string {
    const fromEnv = (environment as any)?.apiBaseUrl || (environment as any)?.baseUrl;
    if (fromEnv) return String(fromEnv).replace(/\/+$/,'');
    return location.port === '4200'
      ? location.origin.replace(':4200', ':8080')
      : location.origin;
  }

  private toAbsolute(u: string): string {
    if (!u) return u;
    if (/^https?:\/\//i.test(u)) return u;
    return `${this.apiOrigin()}/${u.replace(/^\/+/, '')}`;
  }

  trackByMovie = (_: number, g: Group) => g.movie.id;
  trackBySt = (_: number, st: Showtime) => (st as any).id ?? (st as any).start_time?.toString?.() ?? _;
}
