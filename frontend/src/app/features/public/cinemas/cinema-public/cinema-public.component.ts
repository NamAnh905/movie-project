import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { CinemaService } from '../../../../api/public/cinema.service';
import { ShowtimeService } from '../../../../api/public/showtime.service';
import { MovieService } from '../../../../api/public/movie.service';
import { Cinema } from '../../../../models/cinema.model';
import { MovieShowtimeResponse } from '../../../../models/showtime.model';
import { Movie } from '../../../../models/movie.model';

@Component({
  standalone: true,
  selector: 'app-cinema-public',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './cinema-public.component.html',
  styleUrls: ['./cinema-public.component.scss']
})
export class CinemaPublicComponent implements OnInit {
  private cinemaApi = inject(CinemaService);
  private showtimeApi = inject(ShowtimeService);
  private movieApi = inject(MovieService);

  readonly FALLBACK_POSTER = 'assets/posters/banner.png';

  // --- STATE ---
  cinemas = signal<Cinema[]>([]);
  selectedCinemaId = signal<number | null>(null);
  activeTab = signal<'now' | 'soon'>('now');

  nowShowing = signal<MovieShowtimeResponse[]>([]);
  comingSoon = signal<Movie[]>([]);
  loading = signal(false);

  selectedCinema = computed(() => {
    const id = this.selectedCinemaId();
    return this.cinemas().find(c => c.id === id) || null;
  });

  ngOnInit() {
    this.loadCinemas();
    this.loadComingSoon();
  }

  loadCinemas() {
    this.cinemaApi.listPublic().subscribe({
      next: (res) => {
        const list = res.result || [];
        this.cinemas.set(list);
        if (list.length > 0) {
          // Mặc định chọn rạp đầu tiên và tải lịch chiếu hôm nay
          this.selectedCinemaId.set(list[0].id);
          this.loadShowtimes(list[0].id);
        }
      }
    });
  }

  loadComingSoon() {
    // Lấy phim sắp chiếu
    this.movieApi.listAllByStatus('COMING_SOON').subscribe(res => {
      this.comingSoon.set(res.result || []);
    });
  }

  onSelectCinema(id: number) {
    this.selectedCinemaId.set(id);
    this.loadShowtimes(id);
  }

  loadShowtimes(cinemaId: number) {
    this.loading.set(true);
    // Lấy ngày hôm nay định dạng yyyy-MM-dd
    const today = new Date().toISOString().slice(0, 10);

    this.showtimeApi.getPublicShowtimes(cinemaId, today).subscribe({
      next: (res) => {
        this.nowShowing.set(res.result || []);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  imgFallback(ev: Event) {
    const img = ev.target as HTMLImageElement;
    if (!img.src.includes(this.FALLBACK_POSTER)) {
      img.src = this.FALLBACK_POSTER;
    }
  }
}
