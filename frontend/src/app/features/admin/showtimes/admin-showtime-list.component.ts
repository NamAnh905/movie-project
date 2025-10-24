import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

import { ShowtimeService, ShowtimeItem } from '../../showtimes/showtime.service';
import { MovieService } from '../../movies/movie.service';
import { CinemaService, Cinema } from '../../cinemas/cinema.service';

type Movie = { id: number; title: string; status?: string | null };

@Component({
  selector: 'app-admin-showtime-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  providers: [DatePipe],
  templateUrl: './admin-showtime-list.component.html',
  styleUrls: ['./admin-showtime-list.component.scss']
})
export class AdminShowtimeListComponent implements OnInit {
  private svc = inject(ShowtimeService);
  private moviesApi = inject(MovieService);
  private cinemasApi = inject(CinemaService);
  private datePipe = inject(DatePipe);
  private router = inject(Router);

  // filters
  date: string = '';
  movieId: number | null = null;
  cinemaId: number | null = null;

  items: ShowtimeItem[] = [];
  allItems: ShowtimeItem[] = [];
  movies: Movie[] = [];
  cinemas: Cinema[] = [];
  loading = false;

    // paging
  page = 0;
  size = 8;                               // ⬅️ ~8 lịch chiếu / trang
  total = 0;
  totalPages = 0;
  pages: number[] = [];

  // maps tra cứu O(1)
  private movieNameById: Record<number, string> = {};
  private cinemaNameById: Record<number, string> = {};

  private pickArray<T = any>(res: any): T[] {
    const cand =
      res?.content ??
      res?.items ??
      res?.results ??
      res?.data?.content ??
      res?.data?.items ??
      res?.data?.results ??
      res?.data ??
      res;
    return Array.isArray(cand) ? cand : [];
  }

  /** Nhận biết phim đang chiếu */
  private isReleased(x: any): boolean {
    const s = String(x?.status ?? x?.movieStatus ?? x?.state ?? '').toUpperCase();
    return s === 'RELEASED' || s === 'NOW_SHOWING' || s === 'DANG_CHIEU';
  }

  ngOnInit(): void {
    this.date = this.datePipe.transform(new Date(), 'yyyy-MM-dd') || '';
    this.loadFilters();
    this.search();
  }

  loadFilters() {
    // PHIM: chỉ lấy phim đang chiếu để đưa vào dropdown
    this.moviesApi.list({ page: 0, size: 1000 }).subscribe(r => {
      const raw = this.pickArray<any>(r);
      const released = raw.filter((x: any) => this.isReleased(x));
      this.movies = released.map((x: any) => ({ id: x.id, title: x.title, status: x.status }));
      this.movieNameById = Object.fromEntries(this.movies.map(m => [m.id, m.title]));
    });

    // RẠP
    this.cinemasApi.list({ page: 0, size: 1000 }).subscribe(r => {
      this.cinemas = this.pickArray<Cinema>(r);
      this.cinemaNameById = Object.fromEntries(
        this.cinemas.map(c => [Number((c as any).id), (c as any).name])
      );
    });
  }

  search() {
    this.loading = true;
    this.page = 0;                        // ⬅️ reset về trang 1 mỗi lần lọc
    this.svc
      .list({
        date: this.date || undefined,
        movieId: this.movieId || undefined,
        cinemaId: this.cinemaId || undefined,
        page: 0,                           // BE không dùng, ta sẽ phân trang phía client
        size: 500
      })
      .subscribe({
        next: list => {
          this.allItems = list || [];
          this.total = this.allItems.length;
          this.applyPage();
        },
        complete: () => (this.loading = false)
      });
  }

  private applyPage() {
    const start = this.page * this.size;
    const end = Math.min(start + this.size, this.total);
    this.items = this.allItems.slice(start, end);

    this.totalPages = Math.max(1, Math.ceil(this.total / this.size));
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i);
  }

  goPage(p: number) {
    if (p < 0 || p >= this.totalPages) return;
    this.page = p;
    this.applyPage();
  }
  next() { this.goPage(this.page + 1); }
  prev() { this.goPage(this.page - 1); }

  fromIdx(): number { return this.total ? this.page * this.size + 1 : 0; }
  toIdx(): number { return Math.min((this.page + 1) * this.size, this.total); }

  // ----- helpers hiển thị tên -----
  getMovieName = (it: ShowtimeItem): string => {
    const nested = (it as any)?.movie?.title;
    if (nested) return nested;
    const id = Number((it as any)?.movieId);
    return this.movieNameById[id] ?? (Number.isFinite(id) ? String(id) : '');
  };

  getCinemaName = (it: ShowtimeItem): string => {
    const nested = (it as any)?.cinema?.name;
    if (nested) return nested;
    const id = Number((it as any)?.cinemaId);
    return this.cinemaNameById[id] ?? (Number.isFinite(id) ? String(id) : '');
  };

  remove(it: ShowtimeItem) {
    const hhmm = this.datePipe.transform((it as any).startTime, 'HH:mm') || '';
    const movieName = this.getMovieName(it);
    if (!confirm(`Xóa suất ${movieName} @ ${hhmm}?`)) return;
    this.svc.remove((it as any).id).subscribe(() => this.search());
  }

  goCreate() {
    this.router.navigate(['/admin/showtimes/new']);
  }

  trackById = (_: number, r: ShowtimeItem) => (r as any).id;
  startDate = (it: ShowtimeItem) =>
    this.datePipe.transform((it as any).startTime, 'yyyy-MM-dd') || '';
  startTime = (it: ShowtimeItem) => this.datePipe.transform((it as any).startTime, 'HH:mm') || '';
}
