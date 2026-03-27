import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminRevenueService, RevenueOverview } from '../../../api/admin/admin-revenue.service';

import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  standalone: true,
  selector: 'app-admin-revenue',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-revenue.component.html',
  styleUrls: ['./admin-revenue.component.scss']
})
export class AdminRevenueComponent implements OnInit, AfterViewInit {
  from!: string; to!: string;
  groupBy: 'DAY' | 'MONTH' = 'DAY';
  onlyPaid = true;

  loading = false;
  error = '';
  data?: RevenueOverview;

  private chartSeries?: Chart;
  private chartCinema?: Chart;
  private chartMovie?: Chart;

  @ViewChild('seriesChart') seriesChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('cinemaChart') cinemaChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('movieChart')  movieChartRef!:  ElementRef<HTMLCanvasElement>;

  constructor(private api: AdminRevenueService) {}

  ngOnInit(): void {
    const d = new Date();
    this.to = d.toISOString().slice(0, 10);
    d.setDate(d.getDate() - 29); // Mặc định lấy 30 ngày
    this.from = d.toISOString().slice(0, 10);
  }

  ngAfterViewInit(): void {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = '';
    this.api.getOverview({
      from: this.from,
      to: this.to,
      groupBy: this.groupBy,
      onlyPaid: this.onlyPaid
    }).subscribe({
      next: (res) => {
        this.data = res.result;
        // Dùng setTimeout để đợi Angular render xong thẻ <canvas> (do có *ngIf="data") rồi mới vẽ biểu đồ
        setTimeout(() => this.renderCharts(), 0);
      },
      error: (err) => {
        this.error = 'Lỗi tải dữ liệu doanh thu';
        console.error(err);
      },
      complete: () => this.loading = false
    });
  }

  renderCharts() {
    if (!this.data) return;

    // Hủy biểu đồ cũ nếu có để tránh lỗi "Canvas is already in use"
    if (this.chartSeries) this.chartSeries.destroy();
    if (this.chartCinema) this.chartCinema.destroy();
    if (this.chartMovie) this.chartMovie.destroy();

    // 1. Biểu đồ Đường (Doanh thu theo thời gian)
    const series = this.data.series || [];
    this.chartSeries = new Chart(this.seriesChartRef.nativeElement.getContext('2d')!, {
      type: 'line',
      data: {
        labels: series.map(x => x.period),
        datasets: [{
          label: 'Doanh thu (VNĐ)',
          data: series.map(x => x.revenue),
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.2)',
          fill: true,
          tension: 0.3
        }]
      },
      options: {
        responsive: true, maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#e5e7eb' } },
          tooltip: { callbacks: { label: ctx => ` ${Number(ctx.parsed.y).toLocaleString('vi-VN')} đ` } }
        },
        scales: {
          x: { ticks: { color: '#9fb0c4' }, grid: { color: 'rgba(255,255,255,0.05)' } },
          y: { ticks: { color: '#9fb0c4' }, grid: { color: 'rgba(255,255,255,0.05)' } }
        }
      }
    });

    // 2. Biểu đồ Cột Ngang (Top Rạp)
    const topC = (this.data.byCinema || []).slice(0, 10);
    this.chartCinema = new Chart(this.cinemaChartRef.nativeElement.getContext('2d')!, {
      type: 'bar',
      data: {
        labels: topC.map(x => x.cinemaName),
        datasets: [{ label: 'Doanh thu', data: topC.map(x => x.revenue), backgroundColor: '#10b981' }]
      },
      options: {
        indexAxis: 'y', responsive: true, maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { callbacks: { label: ctx => ` ${Number(ctx.parsed.x).toLocaleString('vi-VN')} đ` } }
        },
        scales: {
          x: { ticks: { color: '#9fb0c4' }, grid: { color: 'rgba(255,255,255,0.05)' } },
          y: { ticks: { color: '#9fb0c4' }, grid: { display: false } }
        }
      }
    });

    // 3. Biểu đồ Cột Ngang (Top Phim)
    const topM = (this.data.byMovie || []).slice(0, 10);
    this.chartMovie = new Chart(this.movieChartRef.nativeElement.getContext('2d')!, {
      type: 'bar',
      data: {
        labels: topM.map(x => x.movieTitle),
        datasets: [{ label: 'Doanh thu', data: topM.map(x => x.revenue), backgroundColor: '#f59e0b' }]
      },
      options: {
        indexAxis: 'y', responsive: true, maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: { callbacks: { label: ctx => ` ${Number(ctx.parsed.x).toLocaleString('vi-VN')} đ` } }
        },
        scales: {
          x: { ticks: { color: '#9fb0c4' }, grid: { color: 'rgba(255,255,255,0.05)' } },
          y: { ticks: { color: '#9fb0c4' }, grid: { display: false } }
        }
      }
    });
  }

  vnd(n?: number) { return (n || 0).toLocaleString('vi-VN'); }
}
