import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MovieService } from '../../../../api/public/movie.service';
import { Movie } from '../../../../models/movie.model';

@Component({
  standalone: true,
  selector: 'app-movie-list',
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-list.component.html',
  styleUrls: ['./movie-list.component.scss'],
})
export class MovieListComponent implements OnInit, OnDestroy {
  private api = inject(MovieService);

  loading = false;
  nowPlaying: Movie[] = [];
  comingSoon: Movie[] = [];
  errorMsg = '';
  private router = inject(Router);
  // Phân trang slider
  pageSize = 4;
  pageNow = 0;
  pageSoon = 0;

  // Hiệu ứng animation
  animMs = 240;
  animNow: '' | 'out-left' | 'in-right' | 'out-right' | 'in-left' = '';
  animSoon: '' | 'out-left' | 'in-right' | 'out-right' | 'in-left' = '';
  isAnimatingNow = false;
  isAnimatingSoon = false;

  // Banner trang chủ
  banners = [
    { imageUrl: 'assets/posters/banner.png', link: '/movies', alt: 'Khuyến mãi phim mới' },
    { imageUrl: 'assets/posters/Cinemas_banner.jpg', link: '/cinemas', alt: 'Hệ thống rạp chiếu' }
  ];
  currentBanner = 0;
  bannerTimer: any;

  ngOnInit() {
    this.loadMovies();
    this.startBanner();
  }

  ngOnDestroy() { this.stopBanner(); }

  loadMovies() {
    this.loading = true;
    this.api.list({ status: 'RELEASED', size: 20 }).subscribe(res => {
      this.nowPlaying = res.result?.items || [];
    });
    this.api.list({ status: 'COMING_SOON', size: 20 }).subscribe(res => {
      this.comingSoon = res.result?.items || [];
      this.loading = false;
    });
  }

  viewMore(status: string) {
    this.router.navigate(['/movies/all'], { queryParams: { status } });
  }

  // --- Logic Banner ---
  startBanner() {
    this.stopBanner();
    this.bannerTimer = setInterval(() => this.nextBanner(), 5000);
  }
  stopBanner() { clearInterval(this.bannerTimer); }
  nextBanner() { this.currentBanner = (this.currentBanner + 1) % this.banners.length; }
  prevBanner() { this.currentBanner = (this.currentBanner - 1 + this.banners.length) % this.banners.length; }
  goBanner(i: number) { this.currentBanner = i; }
  onTouchStart(e: any) {} onTouchEnd(e: any) {}

  // --- Helpers Slider ---
  get nowTotalPages() { return Math.ceil(this.nowPlaying.length / this.pageSize); }
  get soonTotalPages() { return Math.ceil(this.comingSoon.length / this.pageSize); }
  get nowVisible() { return this.nowPlaying.slice(this.pageNow * this.pageSize, (this.pageNow + 1) * this.pageSize); }
  get soonVisible() { return this.comingSoon.slice(this.pageSoon * this.pageSize, (this.pageSoon + 1) * this.pageSize); }

  // --- Animation Chuyển Slider ---
  prevNow()  { if (this.pageNow > 0 && !this.isAnimatingNow) this.animatePage('now','prev'); }
  nextNow()  { if (this.pageNow < this.nowTotalPages - 1 && !this.isAnimatingNow) this.animatePage('now','next'); }
  prevSoon() { if (this.pageSoon > 0 && !this.isAnimatingSoon) this.animatePage('soon','prev'); }
  nextSoon() { if (this.pageSoon < this.soonTotalPages - 1 && !this.isAnimatingSoon) this.animatePage('soon','next'); }

  private animatePage(block: 'now'|'soon', dir: 'next'|'prev') {
    const outClass = dir === 'next' ? 'out-left' : 'out-right';
    const inClass  = dir === 'next' ? 'in-right' : 'in-left';

    if (block === 'now') {
      this.isAnimatingNow = true; this.animNow = outClass;
      setTimeout(() => {
        this.pageNow += (dir === 'next' ? 1 : -1);
        this.animNow = inClass;
        setTimeout(() => { this.animNow = ''; this.isAnimatingNow = false; }, this.animMs);
      }, this.animMs);
    } else {
      this.isAnimatingSoon = true; this.animSoon = outClass;
      setTimeout(() => {
        this.pageSoon += (dir === 'next' ? 1 : -1);
        this.animSoon = inClass;
        setTimeout(() => { this.animSoon = ''; this.isAnimatingSoon = false; }, this.animMs);
      }, this.animMs);
    }
  }
}
