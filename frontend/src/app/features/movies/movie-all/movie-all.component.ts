// src/app/features/movies/movie-all/movie-all.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { catchError, of, map } from 'rxjs';
import { MovieService } from '../movie.service';

type MovieStatus = 'RELEASED' | 'COMING_SOON';

@Component({
  standalone: true,
  selector: 'app-movie-all',
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-all.component.html',
  styleUrls: ['./movie-all.component.scss'],
})
export class MovieAllComponent implements OnInit {
  title = '';
  items: any[] = [];
  loading = true;
  errorMsg = '';
  status: MovieStatus = 'RELEASED';
  q = '';

  constructor(private route: ActivatedRoute, private movieService: MovieService) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.status = (params['status'] as any) || 'RELEASED';
      this.q = (params['q'] || '').trim();  // 👈 đọc q

      this.title = this.q
        ? `KẾT QUẢ TÌM “${this.q}”`
        : (this.status === 'RELEASED' ? 'PHIM ĐANG CHIẾU' : 'PHIM SẮP CHIẾU');

      this.load();
    });
  }

  load() {
    this.loading = true; this.errorMsg = '';

    const req$ = this.q
      ? this.movieService.list({ q: this.q, status: this.status, page: 0, size: 60 }) // 👈 dùng API có q
      : this.movieService.listAllByStatus(this.status);                               // fallback cũ

    req$
      .pipe(
        map((res: any) => Array.isArray(res) ? res
             : (res.content ?? res.data?.content ?? res.items ?? res.data?.items ?? [])),
        catchError(() => { this.errorMsg = 'Không tải được danh sách phim.'; return of([]); })
      )
      .subscribe(items => { this.items = items; this.loading = false; });
  }
}
