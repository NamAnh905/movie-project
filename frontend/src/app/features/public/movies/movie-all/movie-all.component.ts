import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MovieService } from '../../../../api/public/movie.service';
import { Movie } from '../../../../models/movie.model';

@Component({
  standalone: true,
  selector: 'app-movie-all',
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-all.component.html',
  styleUrls: ['./movie-all.component.scss'],
})
export class MovieAllComponent implements OnInit {
  title = '';
  items: Movie[] = [];
  loading = true;
  errorMsg = '';
  status = 'RELEASED';
  q = '';

  private route = inject(ActivatedRoute);
  private api = inject(MovieService);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.status = params['status'] || 'RELEASED';
      this.q = (params['q'] || '').trim();

      this.title = this.q
        ? `KẾT QUẢ TÌM “${this.q}”`
        : (this.status === 'RELEASED' ? 'PHIM ĐANG CHIẾU' : 'PHIM SẮP CHIẾU');

      this.load();
    });
  }

  load() {
    this.loading = true;
    this.errorMsg = '';

    const req$ = this.q
      ? this.api.list({ q: this.q, status: this.status, page: 0, size: 60 })
      : this.api.list({ status: this.status, page: 0, size: 60 });

    req$.subscribe({
      next: (res) => {
        this.items = res.result?.items || [];
        this.loading = false;
      },
      error: () => {
        this.errorMsg = 'Lỗi tải danh sách phim.';
        this.loading = false;
      }
    });
  }
}
