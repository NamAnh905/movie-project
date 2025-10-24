import { Component, OnInit, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CinemaService } from '../cinema.service';

@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatTableModule, MatInputModule, MatButtonModule],
  templateUrl: './cinema-list.component.html',
  styleUrls: ['./cinema-list.component.scss']
})
export class CinemaListComponent implements OnInit {
  private fb = inject(FormBuilder)
  items:any[]=[]; cols=['id','name','address','actions']; editingId?:number;
  fm = this.fb.group({ name:['', Validators.required], address:[''] });

  constructor(private svc: CinemaService, private sb: MatSnackBar) {}
  ngOnInit(){ this.load(); }
  load(){ this.svc.list().subscribe(r=> this.items=r); }
  save(){
    const data = this.fm.value;
    const ok = () => { this.sb.open('Đã lưu', 'Đóng', {duration:1200}); this.reset(); this.load(); };
    if (this.editingId) this.svc.update(this.editingId, data).subscribe(ok);
    else this.svc.create(data).subscribe(ok);
  }
  edit(r:any){ this.editingId=r.id; this.fm.patchValue({ name:r.name, address:r.address }); }
  del(r:any){ if (confirm('Xóa rạp này?')) this.svc.remove(r.id).subscribe(()=>{ this.sb.open('Đã xóa','Đóng',{duration:1000}); this.load(); }); }
  reset(){ this.editingId=undefined; this.fm.reset({ name:'', address:'' }); }
}
