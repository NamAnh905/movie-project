import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MovieService } from '../../movies/movie.service';
import { GenreService, Genre } from '../../genres/genre.service';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: `./admin-movie-list.component.html`,
  styleUrls:[`./admin-movie-list.component.scss`]
})
export class AdminMovieListComponent implements OnInit {
  items: any[] = [];
  q = '';

  genres: Genre[] = [];
  selectedGenreId: number | null = null;

  // Phân trang
  page = 0;
  size = 5;                // <= 5 phim / trang
  totalPages = 0;
  totalElements = 0;

  loading = false;

  constructor(
    private svc: MovieService,
    private genreSvc: GenreService,
    public router: Router
  ) {}

  ngOnInit() {
    this.genreSvc.all().subscribe(list => (this.genres = list));
    this.load(0);
  }

  // Tải dữ liệu (ưu tiên phân trang trên BE; có fallback nếu BE trả array)
  load(p: number = this.page) {
    this.loading = true;

    this.svc.list({
      q: this.q || undefined,
      page: p,
      size: this.size,
      genreId: this.selectedGenreId ?? undefined
    })
    .subscribe(res => {
      if (res && typeof res === 'object' && 'content' in res) {
        // BE trả kiểu Page
        this.items = res.content || [];
        this.totalElements = res.totalElements ?? this.items.length ?? 0;
        this.totalPages = res.totalPages ?? Math.ceil(this.totalElements / this.size);
        this.page = res.number ?? p ?? 0;
      } else {
        // Fallback: BE trả mảng -> tự phân trang client
        const list: any[] = Array.isArray(res) ? res : [];
        this.totalElements = list.length;
        this.totalPages = Math.max(1, Math.ceil(this.totalElements / this.size));
        this.page = Math.min(p, this.totalPages - 1);
        const start = this.page * this.size;
        this.items = list.slice(start, start + this.size);
      }
      this.loading = false;
    }, _ => this.loading = false);
  }

  onSearch() {
    this.page = 0;
    this.load(0);
  }

  onGenreChange() {
    this.page = 0;
    this.load(0);
  }

  // Điều hướng trang
  goTo(p: number) {
    if (p < 0 || p >= this.totalPages) return;
    this.load(p);
  }
  prev() { if (this.page > 0) this.goTo(this.page - 1); }
  next() { if (this.page < this.totalPages - 1) this.goTo(this.page + 1); }

  // Tính danh sách nút trang hiển thị (tối đa 5 nút)
  get pager(): number[] {
    const maxButtons = 5;
    const total = this.totalPages || 0;
    if (total <= 1) return [];
    let start = Math.max(0, this.page - Math.floor(maxButtons / 2));
    let end = Math.min(total - 1, start + maxButtons - 1);
    start = Math.max(0, end - maxButtons + 1);
    const arr: number[] = [];
    for (let i = start; i <= end; i++) arr.push(i);
    return arr;
  }

  remove(id: number) {
    if (!confirm('Xóa phim này?')) return;
    this.svc.remove(id).subscribe(() => {
      // Nếu xóa xong trang hiện tại trống và không phải trang đầu -> lùi 1 trang
      const willBeEmpty = this.items.length === 1 && this.page > 0;
      const target = willBeEmpty ? this.page - 1 : this.page;
      this.load(target);
    });
  }
}
