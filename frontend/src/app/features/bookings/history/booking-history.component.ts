// src/app/features/bookings/history/booking-history.component.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

interface BookingHistoryItem {
  id: number;
  movieTitle: string;
  cinemaName: string;
  startTime: string;
  quantity: number;
  totalPrice: number;
  status: string;
}

@Component({
  standalone: true,
  selector: 'app-booking-history',
  imports: [CommonModule, RouterModule],
  templateUrl: './booking-history.component.html',
  styleUrls: ['./booking-history.component.scss']
})
export class BookingHistoryComponent implements OnInit {
  items: BookingHistoryItem[] = [];
  error = '';
  loading = false;

  private http = inject(HttpClient);
  private router = inject(Router);

  // Ưu tiên dùng env.apiUrl nếu có; fallback 4200 -> 8080
  private readonly BASE =
    (window as any).env?.apiUrl ??
    (location.port === '4200'
      ? location.origin.replace(':4200', ':8080')
      : location.origin);

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.error = '';

    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;

    this.http.get<BookingHistoryItem[]>(`${this.BASE}/api/bookings/mine`, { headers })
      .subscribe({
        next: data => {
          this.items = Array.isArray(data) ? data : [];
          this.loading = false;
        },
        error: err => {
          this.items = [];            // tránh hiển thị "empty" khi lỗi
          this.loading = false;
          this.error = err.status === 401
            ? 'Vui lòng đăng nhập để xem lịch sử đặt vé.'
            : 'Không tải được lịch sử đặt vé.';
        }
      });
  }

  open(id: number): void {
    this.router.navigate(['/bookings', id]);
  }
  trackById = (_: number, item: any) => item.id;

  statusClass(s: string) {
    const v = (s || '').toUpperCase();
    return {
      'success': v === 'PAID',
      'warn':    v === 'PENDING',
      'danger':  v === 'FAILED'
    };
  }
}
