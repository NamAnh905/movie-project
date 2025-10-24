// src/app/features/movies/movie-list/movie-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MovieService } from '../movie.service';
import { catchError, of } from 'rxjs';

type MovieStatus = 'RELEASED' | 'COMING_SOON';

interface Banner {
  imageUrl: string;
  alt?: string;
  link?: string; // có thể là /movies/:id hoặc /promotions
}


@Component({
  standalone: true,
  selector: 'app-movie-list',
  imports: [CommonModule, RouterModule],
  templateUrl: './movie-list.component.html',
  styleUrls: ['./movie-list.component.scss'],
})
export class MovieListComponent implements OnInit {
  loading = false;
  errorMsg = '';

  nowPlaying: any[] = [];
  comingSoon: any[] = [];

  // phân trang: 4 phim/trang
  pageSize = 4;
  pageNow = 0;
  pageSoon = 0;

  // hiệu ứng chuyển trang
  animMs = 240; // thời lượng animation (ms)
  animNow: '' | 'out-left' | 'in-right' | 'out-right' | 'in-left' = '';
  animSoon: '' | 'out-left' | 'in-right' | 'out-right' | 'in-left' = '';
  isAnimatingNow = false;
  isAnimatingSoon = false;

  constructor(private svc: MovieService, private router: Router) {}

  banners: Banner[] = [
    { imageUrl: 'assets/posters/promotion1.webp', alt: 'Ưu đãi tháng này', link: '/promotions' },
    { imageUrl: 'assets/posters/promotion2.webp', alt: 'Phim mới ra rạp',  link: '/movies/all?status=RELEASED' },
    { imageUrl: 'assets/posters/promotion3.webp', alt: 'Sắp chiếu hot',    link: '/movies/all?status=COMING_SOON' },
    { imageUrl: 'assets/posters/promotion4.webp', alt: 'Sắp chiếu hot',    link: '/movies/all?status=COMING_SOON' },
  ];

  currentBanner = 0;
  autoplayMs = 3000;
  private bannerTimer?: any;
  private touchStartX = 0;

  ngOnInit(): void {
    this.fetchData();
    this.startBanner();
  }

  ngOnDestroy(): void {
    // ... (giữ nguyên nếu có)
    this.stopBanner();
  }

  startBanner() {
    this.stopBanner();
    this.bannerTimer = setInterval(() => this.nextBanner(), this.autoplayMs);
  }
  stopBanner() {
    if (this.bannerTimer) { clearInterval(this.bannerTimer); this.bannerTimer = undefined; }
  }
  nextBanner() {
    if (!this.banners?.length) return;
    this.currentBanner = (this.currentBanner + 1) % this.banners.length;
  }
  prevBanner() {
    if (!this.banners?.length) return;
    this.currentBanner = (this.currentBanner - 1 + this.banners.length) % this.banners.length;
  }
  goBanner(i: number) {
    if (i>=0 && i<this.banners.length) this.currentBanner = i;
  }

  onTouchStart(ev: TouchEvent) { this.touchStartX = ev.changedTouches[0].clientX; }
  onTouchEnd(ev: TouchEvent) {
    const dx = ev.changedTouches[0].clientX - this.touchStartX;
    if (Math.abs(dx) > 40) { dx < 0 ? this.nextBanner() : this.prevBanner(); }
  }

  private fetchData() {
    this.loading = true;
    this.errorMsg = '';

    // Đang chiếu
    this.svc
      .list({ status: 'RELEASED', page: 0, size: 100 })
      .pipe(
        catchError((err) => {
          this.errorMsg = 'Không tải được danh sách phim đang chiếu.';
          console.error(err);
          return of({ content: [] as any[], items: [] as any[] });
        })
      )
      .subscribe((res: any) => {
        const arr = (res?.content ?? res?.items ?? res) || [];
        this.nowPlaying = this.normalizeList(arr, 'RELEASED');
        this.loading = false;
        this.pageNow = 0;
      });

    // Sắp chiếu
    this.svc
      .list({ status: 'COMING_SOON', page: 0, size: 100 })
      .pipe(
        catchError((err) => {
          this.errorMsg += (this.errorMsg ? '\n' : '') + 'Không tải được danh sách phim sắp chiếu.';
          console.error(err);
          return of({ content: [] as any[], items: [] as any[] });
        })
      )
      .subscribe((res: any) => {
        const arr = (res?.content ?? res?.items ?? res) || [];
        this.comingSoon = this.normalizeList(arr, 'COMING_SOON');
        this.pageSoon = 0;
      });
  }

  private normalizeList(arr: any[], status: MovieStatus): any[] {
    const norm = (v: any) => String(v ?? '').toUpperCase();
    return (arr || [])
      .filter((x: any) => !status || norm(x?.status ?? x?.movieStatus) === status)
      .map((x: any) => ({
        id: x?.id ?? x?.movieId ?? x?.code,
        title: x?.title ?? x?.name ?? 'Không rõ tên',
        posterUrl: x?.posterUrl ?? x?.poster ?? x?.imageUrl ?? '/assets/placeholder-poster.webp',
        duration: x?.duration ?? x?.runtime,
        releaseDate: x?.releaseDate ?? x?.openingDate ?? x?.premiereDate,
        status: norm(x?.status ?? x?.movieStatus) as MovieStatus,
      }));
  }

  // slice hiển thị 4 phim
  get nowVisible() {
    const start = this.pageNow * this.pageSize;
    return this.nowPlaying.slice(start, start + this.pageSize);
  }
  get soonVisible() {
    const start = this.pageSoon * this.pageSize;
    return this.comingSoon.slice(start, start + this.pageSize);
  }

  // tổng số trang
  get nowTotalPages() {
    return Math.max(1, Math.ceil(this.nowPlaying.length / this.pageSize));
  }
  get soonTotalPages() {
    return Math.max(1, Math.ceil(this.comingSoon.length / this.pageSize));
  }

  // điều hướng trang với animation
  prevNow()  { if (this.pageNow  > 0 && !this.isAnimatingNow)  this.animatePage('now','prev'); }
  nextNow()  { if (this.pageNow  < this.nowTotalPages  - 1 && !this.isAnimatingNow)  this.animatePage('now','next'); }
  prevSoon() { if (this.pageSoon > 0 && !this.isAnimatingSoon) this.animatePage('soon','prev'); }
  nextSoon() { if (this.pageSoon < this.soonTotalPages - 1 && !this.isAnimatingSoon) this.animatePage('soon','next'); }

  private animatePage(block: 'now'|'soon', dir: 'next'|'prev') {
    const outClass = dir==='next' ? 'out-left' : 'out-right';
    const inClass  = dir==='next' ? 'in-right' : 'in-left';
    if (block === 'now') {
      this.isAnimatingNow = true; this.animNow = outClass;
      setTimeout(() => {
        this.pageNow += (dir==='next'? 1 : -1);
        this.animNow = inClass;
        setTimeout(() => { this.animNow = ''; this.isAnimatingNow = false; }, this.animMs);
      }, this.animMs);
    } else {
      this.isAnimatingSoon = true; this.animSoon = outClass;
      setTimeout(() => {
        this.pageSoon += (dir==='next'? 1 : -1);
        this.animSoon = inClass;
        setTimeout(() => { this.animSoon = ''; this.isAnimatingSoon = false; }, this.animMs);
      }, this.animMs);
    }
  }

  // XEM THÊM -> tới danh sách đầy đủ theo trạng thái (giữ đúng query)
  viewMore(status: 'RELEASED'|'COMING_SOON') {
    this.router.navigate(['/movies/all'], { queryParams: { status, page: 0 } });
  }
}
