import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../api/public/user.service';

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
  message = '';
  isError = false;

  f = this.fb.group({
    username: [{ value: '', disabled: true }],
    fullName: ['', [Validators.maxLength(255)]],
    email: ['', [Validators.email, Validators.maxLength(255)]],
  });

  ngOnInit(): void {
    this.userApi.getMe().subscribe({
      next: (res) => {
        const me = res.result;
        if (me) {
          this.f.patchValue({
            username: me.username || '',
            fullName: me.fullName || '',
            email: me.email || ''
          });
        }
      },
      error: () => {
        this.message = 'Không thể tải thông tin tài khoản.';
        this.isError = true;
      }
    });
  }

  save(): void {
    if (this.f.invalid) return;

    this.loading = true;
    this.message = '';

    const body = {
      fullName: (this.f.get('fullName')!.value || '').trim(),
      email: (this.f.get('email')!.value || '').trim()
    };

    // Gọi API cập nhật
    this.userApi.updateMe(body).subscribe({
      next: () => {
        this.loading = false;
        this.message = '✅ Cập nhật thông tin thành công!';
        this.isError = false;
      },
      error: (err) => {
        this.loading = false;
        this.message = '❌ ' + (err.error?.message || 'Cập nhật thất bại, vui lòng thử lại.');
        this.isError = true;
      }
    });
  }
}
