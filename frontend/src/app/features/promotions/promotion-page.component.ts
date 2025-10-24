import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Promotion {
  title: string;
  summary: string;
  conditions: string[];
  notes: string[];
  banner: string;
}

@Component({
  selector: 'app-promotion-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './promotion-page.component.html',
  styleUrls: ['./promotion-page.component.scss']
})
export class PromotionPageComponent {
  promotions: Promotion[] = [
    {
      title: "C'SCHOOL • Ưu đãi vé 45K dành riêng cho HSSV/GV/U22",
      summary:
        'Áp dụng đồng giá 45K/vé 2D tại các cụm rạp hệ thống 4SCinema cho Học sinh, Sinh viên, Giảng viên và khách U22.',
      conditions: [
        'Xuất trình thẻ HSSV hoặc CCCD < 22 tuổi; GV xuất trình thẻ giảng viên.',
        'Áp dụng các suất trước 17:00 các ngày T2–T5.',
        'Không áp dụng Lễ/Tết, Suất chiếu sớm/đặc biệt.'
      ],
      notes: [
        'Giá vé chưa bao gồm phụ thu ghế đôi/ghế VIP/3D.',
        'Mỗi khách tối đa 02 vé/ lần giao dịch.',
        'Rạp có quyền từ chối nếu không đủ giấy tờ theo quy định.'
      ],
      banner: 'assets/posters/promotion2.webp'
    },
    {
      title: "HAPPY HOUR • Trước 10h sáng & sau 22h • Chỉ từ 45K",
      summary:
        'Áp dụng giá 45K/2D (55K/3D) cho khách xem phim trước 10:00 sáng hoặc sau 22:00 tại một số cụm rạp.',
      conditions: [
        'Áp dụng T2–T6 (trừ Lễ/Tết).',
        'Mỗi giao dịch tối đa 04 vé.',
        'Không kèm các ưu đãi khác.'
      ],
      notes: [
        'Khung giờ có thể thay đổi theo từng cụm rạp.',
        'Vui lòng kiểm tra lịch chiếu thực tế tại rạp.'
      ],
      banner: 'assets/posters/promotion3.webp'
    }
  ];

  imgFallback(e: Event) {
    (e.target as HTMLImageElement).src = 'assets/posters/promotion3.webp';
  }
}
