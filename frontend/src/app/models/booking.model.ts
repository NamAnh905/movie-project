// ==========================================
// DÀNH CHO ADMIN
// ==========================================
export interface BookingListItem {
  id: number;
  status: string;
  quantity: number;
  totalPrice: number;
  paymentMethod?: string;
  paymentTxnId?: string;
  customerName?: string;
  customerEmail?: string;
  createdAt: string;
  paidAt?: string;
  showtimeId: number;
  startTime: string;
  cinemaId: number;
  cinemaName: string;
  movieId: number;
  movieTitle: string;
}

export interface BookingDetail extends BookingListItem {
  unitPrice: number;
  showtimePrice: number;
  userId?: number;
  timeline: { type: string; at: string; note: string }[];
}

// ==========================================
// DÀNH CHO KHÁCH (CLIENT)
// ==========================================
export interface ShowtimeAvailabilityResponse {
  capacity: number;
  booked: number;      // Đổi từ sold -> booked cho chuẩn BE
  remaining: number;   // Thêm trường remaining cho chuẩn BE
}

export interface BookingCreationRequest {
  showtimeId: number;
  quantity: number;
  customerName?: string;
  customerEmail?: string;
}

export interface BookingResponse {
  id: number;
  status: string; // PENDING/CONFIRMED/CANCELLED/PAID
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  customerName?: string;
  customerEmail?: string;
  movieTitle?: string;
  cinemaName?: string;
  startTime?: string;
  remainingSeats?: number;
}
