import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminShowtimeService } from '../../../api/admin/admin-showtime.service';
import { MovieService } from '../../../api/public/movie.service';
import { CinemaService } from '../../../api/public/cinema.service';
import { ShowtimeResponse } from '../../../models/showtime.model';

@Component({
  standalone: true,
  selector: 'app-admin-showtime',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-showtime.component.html',
  styleUrls: ['./admin-showtime.component.scss']
})
export class AdminShowtimeComponent implements OnInit {
  // Data nguồn
  movies: any[] = [];
  cinemas: any[] = [];

  // STATE LIST
  isFormMode = false;
  items: ShowtimeResponse[] = [];
  qDate = ''; qMovie: number | null = null; qCinema: number | null = null; qState = '';
  page = 0; size = 10; totalPages = 0; totalElements = 0;
  loading = false;

  // STATE FORM
  fDate = ''; fMovie: number | null = null; fCinema: number | null = null;
  price = 85000; capacity = 50;
  newTime = ''; times: string[] = [];
  saving = false;

  constructor(
    private api: AdminShowtimeService,
    private movieApi: MovieService,
    private cinemaApi: CinemaService
  ) {}

  ngOnInit() {
    this.movieApi.list({ size: 200 }).subscribe(r => this.movies = r.result?.items || []);
    this.cinemaApi.listPublic().subscribe(r => this.cinemas = r.result || []);
    this.load(0);
  }

  // ==== LOGIC LIST ====
  load(p: number = this.page) {
    this.loading = true;
    this.api.search({
      date: this.qDate || undefined,
      movieId: this.qMovie || undefined,
      cinemaId: this.qCinema || undefined,
      state: this.qState || undefined,
      page: p, size: this.size
    }).subscribe({
      next: (res) => {
        this.items = res.result?.items || [];
        this.page = res.result?.currentPage || 0;
        this.totalPages = res.result?.totalPages || 0;
        this.totalElements = res.result?.totalElements || 0;
      },
      complete: () => this.loading = false
    });
  }

  search() { this.load(0); }
  goTo(p: number) { if (p >= 0 && p < this.totalPages) this.load(p); }

  remove(it: ShowtimeResponse) {
    if (!confirm(`Xóa suất chiếu ${it.movieTitle} lúc ${it.startTime}?`)) return;
    this.api.remove(it.id).subscribe(() => this.load(0));
  }

  // ==== LOGIC FORM ====
  openForm() {
    this.isFormMode = true;
    this.times = [];
    this.fDate = new Date().toISOString().slice(0, 10);
  }

  closeForm() { this.isFormMode = false; }

  addTime() {
    if (!this.newTime) return;
    if (!this.times.includes(this.newTime)) {
      this.times.push(this.newTime);
      this.times.sort();
    }
    this.newTime = '';
  }

  removeTime(t: string) {
    this.times = this.times.filter(x => x !== t);
  }

  save() {
    if (!this.fDate || !this.fMovie || !this.fCinema || !this.times.length) return;
    this.saving = true;

    this.api.createBatch({
      movieId: this.fMovie,
      cinemaId: this.fCinema,
      date: this.fDate,
      times: this.times,
      price: this.price,
      capacity: this.capacity
    }).subscribe({
      next: (res) => {
        const skipped = res.result?.skippedTimes?.length ? ` (Bỏ qua trùng: ${res.result.skippedTimes.join(', ')})` : '';
        alert('✅ Đã tạo lịch chiếu!' + skipped);
        this.closeForm();
        this.load();
      },
      error: (e) => alert('❌ Lỗi: ' + e.error?.message),
      complete: () => this.saving = false
    });
  }
}
