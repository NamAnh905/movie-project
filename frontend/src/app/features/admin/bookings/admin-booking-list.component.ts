import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AdminBookingService, ListItem, PageResponse } from './admin-booking.service';

@Component({
  standalone: true,
  selector: 'app-admin-booking-list',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-booking-list.component.html',
  styleUrls: ['./admin-booking-list.component.scss']
})
export class AdminBookingListComponent implements OnInit {
  from!: string; to!: string;
  cinemaId?: number; movieId?: number; showtimeId?: number;
  status = ''; paymentMethod = ''; q = '';
  page = 0; size = 10;

  loading = false; error = '';
  res?: PageResponse<ListItem>;

  constructor(private api: AdminBookingService, private router: Router) {}

  ngOnInit(): void {
    const today = new Date();
    const d7 = new Date(today); d7.setDate(today.getDate() - 6);
    this.from = d7.toISOString().slice(0,10);
    this.to = today.toISOString().slice(0,10);
    this.load();
  }

  load(p?:number) {
    if (p!==undefined) this.page = p;
    this.loading = true; this.error = '';
    this.api.list({
      from: this.from, to: this.to,
      cinemaId: this.cinemaId, movieId: this.movieId, showtimeId: this.showtimeId,
      status: this.status || undefined, paymentMethod: this.paymentMethod || undefined,
      q: this.q || undefined, page: this.page, size: this.size
    }).subscribe({
      next: r => { this.res = r.data; this.loading = false; },
      error: e => { this.error = e?.error?.message || 'Lỗi tải danh sách'; this.loading = false; }
    });
  }

  vnd(n?: number){ return (n ?? 0).toLocaleString('vi-VN'); }
  goto(item: ListItem){ this.router.navigate(['/admin/bookings', item.id]); }
}
