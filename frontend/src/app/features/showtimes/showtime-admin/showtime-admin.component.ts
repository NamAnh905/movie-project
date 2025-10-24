import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import dayjs from 'dayjs';
import { MovieService } from '../../movies/movie.service';
import { CinemaService } from '../../cinemas/cinema.service';
import { ShowtimeService } from '../showtime.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatTableModule, MatSelectModule, MatInputModule, MatButtonModule],
  template: `
    <h2>Quản lý lịch chiếu</h2>
    <form [formGroup]="fm" style="display:grid; grid-template-columns: 1fr 1fr 1fr 1fr auto auto; gap:8px; margin:12px 0">
      <mat-select placeholder="Phim" formControlName="movieId">
        <mat-option *ngFor="let m of movies" [value]="m.id">{{m.title}}</mat-option>
      </mat-select>
      <mat-select placeholder="Rạp" formControlName="cinemaId">
        <mat-option *ngFor="let c of cinemas" [value]="c.id">{{c.name}}</mat-option>
      </mat-select>
      <mat-form-field appearance="outline"><mat-label>Thời gian (YYYY-MM-DD HH:mm)</mat-label>
        <input matInput formControlName="startTime" />
      </mat-form-field>
      <mat-form-field appearance="outline"><mat-label>Giá</mat-label>
        <input matInput type="number" formControlName="price" />
      </mat-form-field>
      <button mat-raised-button color="primary" (click)="save()" [disabled]="fm.invalid">{{editingId ? 'Cập nhật' : 'Thêm'}}</button>
      <button mat-button type="button" (click)="reset()" *ngIf="editingId">Hủy</button>
    </form>

    <table mat-table [dataSource]="items" class="mat-elevation-z1" style="width:100%">
      <ng-container matColumnDef="id"><th mat-header-cell *matHeaderCellDef>ID</th><td mat-cell *matCellDef="let r">{{r.id}}</td></ng-container>
      <ng-container matColumnDef="movie"><th mat-header-cell *matHeaderCellDef>Phim</th><td mat-cell *matCellDef="let r">{{ movieName(r.movieId) }}</td></ng-container>
      <ng-container matColumnDef="cinema"><th mat-header-cell *matHeaderCellDef>Rạp</th><td mat-cell *matCellDef="let r">{{ cinemaName(r.cinemaId) }}</td></ng-container>
      <ng-container matColumnDef="time"><th mat-header-cell *matHeaderCellDef>Thời gian</th><td mat-cell *matCellDef="let r">{{ r.startTime | date:'short' }}</td></ng-container>
      <ng-container matColumnDef="price"><th mat-header-cell *matHeaderCellDef>Giá</th><td mat-cell *matCellDef="let r">{{ r.price | number }}</td></ng-container>
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef>Thao tác</th>
        <td mat-cell *matCellDef="let r">
          <button mat-button (click)="edit(r)">Sửa</button>
          <button mat-button color="warn" (click)="del(r)">Xóa</button>
        </td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="cols"></tr>
      <tr mat-row *matRowDef="let row; columns: cols;"></tr>
    </table>
  `
})
export class ShowtimeAdminComponent implements OnInit {
  private fb = inject(FormBuilder);
  movies:any[]=[]; cinemas:any[]=[]; items:any[]=[]; cols=['id','movie','cinema','time','price','actions'];
  editingId?: number;
  fm = this.fb.group({
    movieId: [null, Validators.required],
    cinemaId: [null, Validators.required],
    startTime: [dayjs().format('YYYY-MM-DD HH:mm'), Validators.required],
    price: [75000, [Validators.required, Validators.min(0)]]
  });

  constructor(
    private mv: MovieService,
    private ci: CinemaService,
    private st: ShowtimeService,
    private sb: MatSnackBar
  ) {}

  ngOnInit(){
    this.mv.list({page:0,size:500}).subscribe(r => this.movies = r?.content ?? r);
    this.ci.list().subscribe(r => this.cinemas = r);
    this.reload();
  }
  reload(){ this.st.list().subscribe(r => this.items = r); }

  save(){
    const data = this.fm.value;
    const ok = () => { this.sb.open('Đã lưu', 'Đóng', {duration:1200}); this.reset(); this.reload(); };
    if (this.editingId) this.st.update(this.editingId, data).subscribe(ok);
    else this.st.create(data).subscribe(ok);
  }
  edit(r:any){
    this.editingId = r.id;
    this.fm.patchValue({
      movieId: r.movieId, cinemaId: r.cinemaId,
      startTime: dayjs(r.startTime).format('YYYY-MM-DD HH:mm'),
      price: r.price
    });
  }
  del(r:any){ if (confirm('Xóa suất chiếu này?')) this.st.remove(r.id).subscribe(()=>{ this.sb.open('Đã xóa','Đóng',{duration:1000}); this.reload(); }); }
  reset(){ this.editingId = undefined; this.fm.reset({ movieId:null, cinemaId:null, startTime: dayjs().format('YYYY-MM-DD HH:mm'), price:75000 }); }
  movieName(id:number){ return this.movies.find(x=>x.id===id)?.title || '—'; }
  cinemaName(id:number){ return this.cinemas.find(x=>x.id===id)?.name || '—'; }
}
