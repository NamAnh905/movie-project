export interface ShowtimeResponse {
  id: number;
  movieId: number;
  movieTitle: string;
  cinemaId: number;
  cinemaName: string;
  startTime: string;
  endTime: string;
  price: number;
  status: string;
  computedState: string;
}

export interface AdminShowtimeRequest {
  movieId: number;
  cinemaId: number;
  date: string;
  times: string[];
  price: number;
  capacity: number;
}

export interface MovieShowtimeResponse {
  movieId: number;
  movieTitle: string;
  posterUrl: string;
  times: string[];
}

export interface CinemaShowtimeResponse {
  cinemaId: number;
  cinemaName: string;
  times: string[];
}
