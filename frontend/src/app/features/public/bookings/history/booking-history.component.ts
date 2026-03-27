import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookingService } from '../../../../api/public/booking.service';
import { BookingResponse } from '../../../../models/booking.model';

@Component({
  standalone: true,
  selector: 'app-booking-history',
  imports: [CommonModule],
  templateUrl: './booking-history.component.html',
  styleUrls: ['./booking-history.component.scss']
})
export class BookingHistoryComponent implements OnInit {
  // --- STATE LIST ---
  items: BookingResponse[] = [];
  error = '';
  loading = false;

  // --- STATE DETAIL ---
  selectedId: number | null = null;
  detailData?: BookingResponse;
  detailLoading = false;
  detailError = '';

  private api = inject(BookingService);

  ngOnInit(): void { this.load(); }

  // Lấy danh sách lịch sử
  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getMyBookings().subscribe({
      next: res => { this.items = res.result || []; this.loading = false; },
      error: err => {
        this.loading = false;
        this.error = (err.status === 401 || err.status === 403)
          ? 'Vui lòng đăng nhập để xem lịch sử.'
          : 'Không tải được lịch sử đặt vé.';
      }
    });
  }

  // Mở xem chi tiết 1 đơn
  open(id: number) {
    this.selectedId = id;
    this.detailLoading = true;
    this.detailError = '';
    this.api.getOne(id).subscribe({
      next: res => { this.detailData = res.result; this.detailLoading = false; },
      error: () => { this.detailError = 'Không thể tải chi tiết vé này.'; this.detailLoading = false; }
    });
  }

  // Quay lại danh sách
  close() {
    this.selectedId = null;
    this.detailData = undefined;
  }

  statusClass(st: string) {
    if (st === 'PAID' || st === 'CONFIRMED') return 'bg-green';
    if (st === 'PENDING') return 'bg-yellow';
    return 'bg-red';
  }
}
