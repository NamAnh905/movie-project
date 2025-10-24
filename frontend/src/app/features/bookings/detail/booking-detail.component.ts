import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-booking-detail',
  imports: [CommonModule],
  templateUrl: './booking-detail.component.html',
  styleUrls: ['./booking-detail.component.scss']
})
export class BookingDetailComponent {
  data: any;
  constructor(private route: ActivatedRoute){
    const id = Number(this.route.snapshot.paramMap.get('id') || 0);
    const base = location.port==='4200'?location.origin.replace(':4200',':8080'):location.origin;
    fetch(`${base}/api/bookings/${id}`).then(r=>r.json()).then(d=>this.data=d);
  }
}
