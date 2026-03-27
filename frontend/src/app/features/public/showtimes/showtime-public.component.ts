import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { ShowtimeService } from '../../../api/public/showtime.service';
import { CinemaService } from '../../../api/public/cinema.service';
import { MovieService } from '../../../api/public/movie.service';

@Component({
  standalone: true,
  selector: 'app-showtime-public',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './showtime-public.component.html',
  styleUrls: ['./showtime-public.component.scss']
})
export class ShowtimePublicComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private showtimeApi = inject(ShowtimeService);
  private cinemaApi = inject(CinemaService);
  private movieApi = inject(MovieService);

  // --- STATE ---
  mode = signal<'byCinema' | 'byMovie'>('byCinema');
  selectedDate = signal(new Date().toISOString().slice(0, 10));

  // Data nguồn
  cinemas = signal<any[]>([]);
  selectedCinemaId = signal<number | null>(null);
  movieInfo = signal<any>(null);

  // Data hiển thị
  groups = signal<any[]>([]);
  loading = signal(false);
  errorMsg = signal('');

  ngOnInit() {
    // 1. Kiểm tra xem có movieId trên URL không? (Nếu có là mode 'byMovie')
    this.route.queryParams.subscribe(params => {
      const mid = params['movieId'];
      if (mid) {
        this.mode.set('byMovie');
        this.loadMovieInfo(Number(mid));
      } else {
        this.mode.set('byCinema');
        this.loadCinemas();
      }
    });
  }

  loadCinemas() {
    this.cinemaApi.listPublic().subscribe(res => {
      this.cinemas.set(res.result || []);
      if (this.cinemas().length > 0) {
        this.selectedCinemaId.set(this.cinemas()[0].id);
        this.load();
      }
    });
  }

  loadMovieInfo(id: number) {
    this.movieApi.getById(id).subscribe(res => {
      this.movieInfo.set(res.result);
      this.load();
    });
  }

  load() {
    const date = this.selectedDate();
    this.loading.set(true);
    this.errorMsg.set('');

    if (this.mode() === 'byMovie' && this.movieInfo()) {
      this.showtimeApi.getPublicByMovie(this.movieInfo().id, date).subscribe({
        next: (res) => this.groups.set(res.result?.map(c => ({ id: c.cinemaId, title: c.cinemaName, times: c.times })) || []),
        error: () => this.errorMsg.set('Không tìm thấy lịch chiếu cho phim này.'),
        complete: () => this.loading.set(false)
      });
    } else if (this.selectedCinemaId()) {
      this.showtimeApi.getPublicShowtimes(this.selectedCinemaId()!, date).subscribe({
        next: (res) => this.groups.set(res.result?.map(m => ({ id: m.movieId, title: m.movieTitle, posterUrl: m.posterUrl, times: m.times })) || []),
        error: () => this.errorMsg.set('Không tìm thấy lịch chiếu tại rạp này.'),
        complete: () => this.loading.set(false)
      });
    }
  }

  onDateChange(val: string) {
    this.selectedDate.set(val);
    this.load();
  }

  // Nút đặt vé nhanh
  quickBookTime(time: string, group: any) {
    const cinemaId = this.mode() === 'byMovie' ? group.id : this.selectedCinemaId();
    const movieId = this.mode() === 'byMovie' ? this.movieInfo().id : group.id;

    this.showtimeApi.resolveId(cinemaId!, movieId, this.selectedDate(), time).subscribe({
      next: (res) => {
        if (res.result) {
          this.router.navigate(['/bookings/new'], { queryParams: { showtimeId: res.result } });
        }
      },
      error: () => alert('Rất tiếc, không tìm thấy mã suất chiếu.')
    });
  }

  onImgError(ev: Event) {
    (ev.target as HTMLImageElement).src = 'assets/posters/banner.png';
  }
}
