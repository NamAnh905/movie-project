import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, Validators, FormControl, FormGroup, NonNullableFormBuilder } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

type Status = 'ACTIVE' | 'INACTIVE';
type CinemaFormControls = {
  name: FormControl<string>;
  address: FormControl<string>;
  status: FormControl<Status>;
};

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './admin-cinema-form.component.html',
  styleUrls: ['./admin-cinema-form.component.scss']
})
export class AdminCinemaFormComponent {
  base = environment.baseUrl + environment.api.cinemas;

  loading = false;
  form: FormGroup<CinemaFormControls>;

  constructor(
    private fb: NonNullableFormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    this.form = this.fb.group({
      name: this.fb.control('', { validators: [Validators.required, Validators.minLength(2), Validators.maxLength(255)] }),
      address: this.fb.control('', { validators: [Validators.maxLength(500)] }),
      status: this.fb.control<Status>('ACTIVE', { validators: [Validators.required] })
    });
  }

  // <-- giờ f có kiểu tường minh, dùng f.name/f.address trong template OK
  get f(): CinemaFormControls { return this.form.controls; }

  submit() {
    if (this.form.invalid || this.loading) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.http.post(this.base, this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/admin/cinemas']),
      error: () => (this.loading = false),
      complete: () => (this.loading = false)
    });
  }

  cancel() { this.router.navigate(['/admin/cinemas']); }
}
