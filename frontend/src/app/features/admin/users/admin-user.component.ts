import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService } from '../../../api/admin/admin-user.service';
import { AdminUserResponse } from '../../../models/user.model';

type UserRow = AdminUserResponse & { _roleDraft: string; _saving?: boolean };

@Component({
  selector: 'app-admin-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user.component.html',
  styleUrls: ['./admin-user.component.scss']
})
export class AdminUserComponent implements OnInit {
  items: UserRow[] = [];
  q = '';
  page = 0; size = 10; totalPages = 0; totalElements = 0;
  loading = false;

  constructor(private api: AdminUserService) {}

  ngOnInit() { this.load(0); }

  load(p: number = this.page) {
    this.loading = true;
    this.api.list({ q: this.q || undefined, page: p, size: this.size }).subscribe({
      next: (res) => {
        const content = res.result?.items || [];
        this.items = content.map(it => ({ ...it, _roleDraft: it.role }));
        this.page = res.result?.currentPage || 0;
        this.totalPages = res.result?.totalPages || 0;
        this.totalElements = res.result?.totalElements || 0;
      },
      complete: () => this.loading = false
    });
  }

  onSearch() { this.load(0); }
  goTo(p: number) { if (p >= 0 && p < this.totalPages) this.load(p); }
  prev() { if (this.page > 0) this.goTo(this.page - 1); }
  next() { if (this.page < this.totalPages - 1) this.goTo(this.page + 1); }

  get pager(): number[] {
    const max = 5, total = this.totalPages || 0;
    if (total <= 1) return [];
    let start = Math.max(0, this.page - Math.floor(max / 2));
    let end = Math.min(total - 1, start + max - 1);
    start = Math.max(0, end - max + 1);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }

  saveRole(u: UserRow) {
    u._saving = true;
    this.api.updateRole(u.id, u._roleDraft).subscribe({
      next: () => {
        u.role = u._roleDraft;
        alert('✅ Đổi role thành công');
      },
      error: e => alert('❌ Lỗi đổi role: ' + (e?.error?.message || 'Unknown')),
      complete: () => u._saving = false
    });
  }
}
