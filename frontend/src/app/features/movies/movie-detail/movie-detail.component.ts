// src/app/features/movies/movie-detail/movie-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatChipsModule } from '@angular/material/chips';
import { MovieService } from '../movie.service';

@Component({
  standalone: true,
  // CẦN import RouterModule cho [routerLink]/[queryParams] trong template
  imports: [CommonModule, RouterModule, MatChipsModule],
  templateUrl: './movie-detail.component.html',
  styleUrls: ['./movie-detail.component.scss']
})
export class MovieDetailComponent implements OnInit {
  loading = true;
  errorMsg = '';
  movie: any;

  constructor(
    private route: ActivatedRoute,
    private svc: MovieService,
    private location: Location,   // <— NEW
    private router: Router        // <— NEW (để fallback)
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.loading = false;
      this.errorMsg = 'Thiếu mã phim.';
      return;
    }

    const n = Number(idParam);
    const id: any = Number.isFinite(n) ? n : idParam;

    this.svc.get(id).subscribe({
      next: (res) => { this.movie = res; this.loading = false; },
      error: () => { this.errorMsg = 'Không tìm thấy phim.'; this.loading = false; }
    });
  }

  /** Quay về trang ngay trước đó; nếu không có lịch sử thì về danh sách phim */
  goBack(): void {
    // history.length > 1: có trang trước để quay lại
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/movies']);
    }
  }
}
