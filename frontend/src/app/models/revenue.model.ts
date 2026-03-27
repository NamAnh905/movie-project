export interface RevenueSummary {
  revenue: number;
  tickets: number;
  bookings: number;
}

export interface RevenuePoint {
  period: string;
  revenue: number;
  tickets: number;
  bookings: number;
}

export interface RevenueByCinema {
  cinemaId: number;
  cinemaName: string;
  revenue: number;
  tickets: number;
  bookings: number;
}

export interface RevenueByMovie {
  movieId: number;
  movieTitle: string;
  revenue: number;
  tickets: number;
  bookings: number;
}

export interface RevenueOverview {
  summary: RevenueSummary;
  series: RevenuePoint[];
  byCinema: RevenueByCinema[];
  byMovie: RevenueByMovie[];
}
