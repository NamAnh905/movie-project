import { Component, OnInit, AfterViewInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminRevenueService, RevenueOverview } from './admin-revenue.service';

import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  standalone: true,
  selector: 'app-admin-revenue',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-revenue.component.html',
  styleUrls: ['./admin-revenue.component.scss']
})
export class AdminRevenueComponent implements OnInit, AfterViewInit, OnDestroy {
  from!: string; to!: string;
  groupBy: 'DAY' | 'MONTH' = 'DAY';
  onlyPaid = false;

  loading = false;
  error = '';
  data?: RevenueOverview;

  private viewReady = false;
  private pendingRender = false;
  private chartSeries?: Chart;
  private chartCinema?: Chart;
  private chartMovie?: Chart;

  @ViewChild('seriesChart') seriesChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('cinemaChart') cinemaChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('movieChart')  movieChartRef!:  ElementRef<HTMLCanvasElement>;

  constructor(private api: AdminRevenueService) {}

  ngOnInit(): void {
    const today = new Date();
    const d30 = new Date(today); d30.setDate(today.getDate() - 29);
    this.from = d30.toISOString().slice(0,10);
    this.to = today.toISOString().slice(0,10);
    this.load();
  }

  ngAfterViewInit(): void {
    this.viewReady = true;
    // Nếu data đã về trước đó, render ngay sau khi view khởi tạo
    setTimeout(() => {               // đảm bảo #canvas đã có
      if (this.pendingRender || this.data) {
        this.renderCharts();
        this.pendingRender = false;
      }
    });
  }

  ngOnDestroy(): void { this.destroyCharts(); }

  load() {
    this.loading = true; this.error = '';
    this.api.getOverview({
      from: this.from, to: this.to, groupBy: this.groupBy, onlyPaid: this.onlyPaid
    }).subscribe({
      next: res => {
        this.data = res.data; this.loading = false;
        if (this.viewReady) {  setTimeout(() => this.renderCharts()); }
        else this.pendingRender = true;
      },
      error: err => { this.error = err?.error?.message || 'Lỗi tải doanh thu'; this.loading = false; }
    });
  }

  vnd(n?: number) { return (n ?? 0).toLocaleString('vi-VN'); }

  private formatPeriod(iso: string) {
    const d = new Date(iso);
    return this.groupBy === 'DAY'
      ? d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' })
      : d.toLocaleDateString('vi-VN', { month: '2-digit', year: 'numeric' });
  }

  private destroyCharts() {
    this.chartSeries?.destroy();
    this.chartCinema?.destroy();
    this.chartMovie?.destroy();
  }

  private renderCharts() {
    if (!this.data) return;
    this.destroyCharts();

    // ===== 1) Series: Revenue (bar) + Tickets (line) =====
    const labels = (this.data.series ?? []).map(p => this.formatPeriod(p.period));
    const revenue = (this.data.series ?? []).map(p => p.revenue || 0);
    const tickets = (this.data.series ?? []).map(p => p.tickets || 0);

    this.chartSeries = new Chart(this.seriesChartRef.nativeElement.getContext('2d')!, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            type: 'bar',
            label: 'Doanh thu (đ)',
            data: revenue,
            borderWidth: 0,
            backgroundColor: 'rgba(48, 213, 200, 0.6)',
            yAxisID: 'yRevenue'
          },
          {
            type: 'line',
            label: 'Số vé',
            data: tickets,
            tension: 0.3,
            pointRadius: 3,
            borderWidth: 2,
            borderColor: '#3d7bfd',
            yAxisID: 'yTickets'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#ddd' } },
          tooltip: {
            callbacks: {
              label: (ctx) => {
                const v = Number(ctx.parsed.y || 0);
                return ctx.dataset.label?.includes('Doanh thu')
                  ? ` ${v.toLocaleString('vi-VN')} đ`
                  : ` ${v.toLocaleString('vi-VN')} vé`;
              }
            }
          }
        },
        scales: {
          x: { ticks: { color: '#aaa' }, grid: { color: 'rgba(255,255,255,0.06)' } },
          yRevenue: {
            type: 'linear', position: 'left',
            ticks: { color: '#aaa', callback: (v) => Number(v).toLocaleString('vi-VN') },
            grid: { color: 'rgba(255,255,255,0.06)' }
          },
          yTickets: {
            type: 'linear', position: 'right',
            ticks: { color: '#aaa' }, grid: { drawOnChartArea: false }
          }
        }
      }
    });

    // ===== 2) Top Cinema (horizontal bar) =====
    const topC = (this.data.byCinema ?? []).slice(0, 10);
    this.chartCinema = new Chart(this.cinemaChartRef.nativeElement.getContext('2d')!, {
      type: 'bar',
      data: {
        labels: topC.map(x => x.cinemaName),
        datasets: [{
          label: 'Doanh thu (đ)',
          data: topC.map(x => x.revenue || 0),
          backgroundColor: 'rgba(61, 123, 253, 0.6)'
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#ddd' } },
          tooltip: { callbacks: { label: ctx => ` ${Number(ctx.parsed.x).toLocaleString('vi-VN')} đ` } }
        },
        scales: {
          x: { ticks: { color: '#aaa', callback: v => Number(v).toLocaleString('vi-VN') }, grid: { color: 'rgba(255,255,255,0.06)' } },
          y: { ticks: { color: '#aaa' }, grid: { color: 'rgba(255,255,255,0.06)' } }
        }
      }
    });

    // ===== 3) Top Movie (horizontal bar) =====
    const topM = (this.data.byMovie ?? []).slice(0, 10);
    this.chartMovie = new Chart(this.movieChartRef.nativeElement.getContext('2d')!, {
      type: 'bar',
      data: {
        labels: topM.map(x => x.movieTitle),
        datasets: [{
          label: 'Doanh thu (đ)',
          data: topM.map(x => x.revenue || 0),
          backgroundColor: 'rgba(144, 238, 144, 0.6)'
        }]
      },
      options: {
        indexAxis: 'y',
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#ddd' } },
          tooltip: { callbacks: { label: ctx => ` ${Number(ctx.parsed.x).toLocaleString('vi-VN')} đ` } }
        },
        scales: {
          x: { ticks: { color: '#aaa', callback: v => Number(v).toLocaleString('vi-VN') }, grid: { color: 'rgba(255,255,255,0.06)' } },
          y: { ticks: { color: '#aaa' }, grid: { color: 'rgba(255,255,255,0.06)' } }
        }
      }
    });
  }
}
