import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminBookingService } from '../../../api/admin/admin-booking.service';
import { BookingListItem, BookingDetail } from '../../../models/booking.model';

@Component({
  standalone: true,
  selector: 'app-admin-booking',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-booking.component.html',
  styleUrls: ['./admin-booking.component.scss']
})
export class AdminBookingComponent implements OnInit {
  // --- LIST ---
  from!: string;
  to!: string;
  q = '';
  status = '';
  paymentMethod = '';

  // 👉 3 biến đã được bổ sung để dùng cho bộ lọc
  cinemaId?: number;
  movieId?: number;
  showtimeId?: number;

  page = 0;
  size = 10;
  loading = false;
  items: BookingListItem[] = [];

  // --- DETAIL ---
  selectedId: number | null = null;
  detailData?: BookingDetail;
  detailLoading = false;

  constructor(private api: AdminBookingService) {}

  ngOnInit() {
    const d = new Date();
    this.to = d.toISOString().slice(0,10);
    d.setDate(d.getDate() - 6);
    this.from = d.toISOString().slice(0,10);
    this.load();
  }

  load(p?: number) {
    if (p !== undefined) this.page = p;
    this.loading = true;

    this.api.list({
      from: this.from,
      to: this.to,
      cinemaId: this.cinemaId,
      movieId: this.movieId,
      showtimeId: this.showtimeId,
      status: this.status || undefined,
      paymentMethod: this.paymentMethod || undefined,
      q: this.q || undefined,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (res) => this.items = res.result?.items || [],
      complete: () => this.loading = false
    });
  }

  viewDetail(id: number) {
    this.selectedId = id;
    this.detailLoading = true;
    this.api.get(id).subscribe({
      next: (res) => this.detailData = res.result,
      complete: () => this.detailLoading = false
    });
  }

  closeDetail() {
    this.selectedId = null;
    this.detailData = undefined;
  }

  vnd(n?: number) {
    return (n ?? 0).toLocaleString('vi-VN');
  }
}
