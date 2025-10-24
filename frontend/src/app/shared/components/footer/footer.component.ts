import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CinemaService, Cinema } from '../../../features/cinemas/cinema.service';

@Component({
  selector: 'app-site-footer',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent implements OnInit {
  private cinemaSvc = inject(CinemaService);
  private router = inject(Router);
  currentYear = new Date().getFullYear();

  cinemas = signal<Cinema[]>([]);

  ngOnInit(): void {
    this.cinemaSvc.getCinemasPublic().subscribe({
      next: (res) => this.cinemas.set(this.pick(res)),
      error: () => this.cinemas.set([]),
    });
  }

  // Chuẩn hoá mảng từ nhiều kiểu response
  private pick<T = any>(res: any): T[] {
    if (Array.isArray(res)) return res;
    if (Array.isArray(res?.content)) return res.content;
    if (Array.isArray(res?.items)) return res.items;
    if (Array.isArray(res?.data)) return res.data;
    if (Array.isArray(res?.data?.content)) return res.data.content;
    if (Array.isArray(res?.data?.items)) return res.data.items;
    return [];
  }

  goNow() {
    // giống viewMore('RELEASED')
    this.router.navigate(['/movies/all'], { queryParams: { status: 'RELEASED', page: 0 } });
  }

  goSoon() {
    // giống viewMore('COMING_SOON')
    this.router.navigate(['/movies/all'], { queryParams: { status: 'COMING_SOON', page: 0 } });
  }
  goLogin(): void { this.router.navigate(['/auth/login']); }
  goRegister(): void { this.router.navigate(['/auth/register']); }
  goCinemas() { this.router.navigate(['/cinemas']); }
  openCinema(c: Cinema) {
    this.router.navigate(['/cinemas'], { queryParams: { cinemaId: c.id } });
  }
}
