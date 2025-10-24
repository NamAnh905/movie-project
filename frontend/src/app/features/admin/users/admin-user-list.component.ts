import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService, UserRow } from './admin-user.service';

@Component({
  selector: 'app-admin-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-user-list.component.html',
  styleUrls: ['./admin-user-list.component.scss']
})
export class AdminUserListComponent implements OnInit {
  private api = inject(AdminUserService);
  items: (UserRow & {_roleDraft:string,_saving?:boolean})[] = [];
  q = ''; loading = false;

  ngOnInit(){ this.load(); }

  load(){
    this.loading = true;
    this.api.list({q:this.q || undefined, page:0, size:200}).subscribe({
      next: (res:any) => {
        const content: UserRow[] = (res?.content ?? res) || [];
        this.items = content.map(it=>({...it, _roleDraft: it.role}));
      },
      complete: () => this.loading = false
    });
  }

  saveRole(u:any){
    u._saving = true;
    this.api.updateRole(u.id, u._roleDraft).subscribe({
      next: _ => { u.role = u._roleDraft; alert('✅ Đổi role thành công'); },
      error: e => { alert('❌ Lỗi đổi role: ' + (e?.error?.message || e?.message || 'Unknown')); },
      complete: () => u._saving = false
    });
  }
}
