import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userApi = inject(UserService);

  loading = false;
  f = this.fb.group({
    username: [{value:'', disabled:true}],
    fullName: ['', [Validators.maxLength(255)]],
    email: ['', [Validators.email, Validators.maxLength(255)]],
  });

  ngOnInit(): void {
    this.userApi.getMe().subscribe({
      next: (me:any) => {
        this.f.patchValue({
          username: me?.username || '',
          fullName: me?.fullName || '',
          email: me?.email || ''
        });
      }
    });
  }

  save(): void {
    if (this.f.invalid) return;
    this.loading = true;
    const body = {
      fullName: (this.f.get('fullName')!.value || '').trim(),
      email: (this.f.get('email')!.value || '').trim()
    };
    this.userApi.updateMe(body).subscribe({
      next: _ => { alert('✅ Cập nhật thành công'); },
      error: e => { alert('❌ Lỗi cập nhật: ' + (e?.error?.message || e?.message || 'Unknown')); },
      complete: () => this.loading = false
    });
  }
}
