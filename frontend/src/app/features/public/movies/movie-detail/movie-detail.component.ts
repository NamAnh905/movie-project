import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MovieService } from '../../../../api/public/movie.service';
import { Movie } from '../../../../models/movie.model';

@Component({
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-detail.component.html',
  styleUrls: ['./movie-detail.component.scss']
})
export class MovieDetailComponent implements OnInit {
  loading = true;
  errorMsg = '';
  movie?: Movie;

  private route = inject(ActivatedRoute);
  private api = inject(MovieService);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.errorMsg = 'Thiếu mã phim.';
      this.loading = false;
      return;
    }

    this.api.getById(id).subscribe({
      next: (res) => {
        this.movie = res.result;
        this.loading = false;
      },
      error: () => {
        this.errorMsg = 'Không tìm thấy thông tin phim.';
        this.loading = false;
      }
    });
  }
}
