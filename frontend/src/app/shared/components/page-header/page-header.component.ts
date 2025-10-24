import { Component, HostListener, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';

interface Me {
  id: number;
  username: string;
  fullName?: string;
  email?: string;
  role?: 'ADMIN' | 'USER';
  status?: string;
  enabled?: boolean;
}

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, HttpClientModule],
  templateUrl: './page-header.component.html',
  styleUrls: ['./page-header.component.scss'],
})
export class PageHeaderComponent implements OnInit {
  query = '';
  menuOpen = false;
  me: Me | null = null;

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    if (this.isLoggedIn) {
      this.fetchMe();
    }
  }

  get isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  onSearch(): void {
    const q = (this.query || '').trim();
  // Điều hướng sang trang danh sách đầy đủ có xử lý q
  this.router.navigate(['/movies/all'], { queryParams: q ? { q } : {} });
  }

  // ===== Auth nav actions =====
  goLogin(): void { this.router.navigate(['/auth/login']); }
  goRegister(): void { this.router.navigate(['/auth/register']); }
  goAccount(): void { this.router.navigate(['/account']); }

  goBookings(): void {
    this.menuOpen = false;
    this.router.navigate(['/bookings']);   // /bookings (lịch sử)
  }

  logout(): void {
    localStorage.removeItem('token');
    this.me = null;
    this.menuOpen = false;
    this.router.navigate(['/auth/login']);
  }

  // ===== Dropdown =====
  toggleMenu(ev: MouseEvent) {
    ev.stopPropagation();
    this.menuOpen = !this.menuOpen;
  }

  @HostListener('document:click')
  onDocClick() { this.menuOpen = false; }

  // ===== Helpers =====
  displayName(): string {
    if (this.me?.fullName && this.me.fullName.trim()) return this.me.fullName;
    if (this.me?.username) return this.me.username;
    return 'Tài khoản';
  }

  initials(): string {
    const name = this.displayName().trim();
    const parts = name.split(/\s+/);
    const first = parts[0]?.[0] ?? 'U';
    const second = parts[1]?.[0] ?? '';
    return (first + second).toUpperCase();
  }

  private fetchMe() {
    this.http.get<Me>('/api/users/me').subscribe({
      next: (res) => { this.me = res; },
      error: (err) => {
        // Token hỏng/401 → coi như đăng xuất
        if (err?.status === 401 || err?.status === 403) {
          localStorage.removeItem('token');
          this.me = null;
        }
      }
    });
  }
}
