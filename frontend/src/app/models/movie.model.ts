export interface Movie {
  id: number;
  title: string;
  description?: string;
  duration?: number;
  releaseDate?: string;
  language?: string;
  country?: string;
  status: string;
  ageRating?: string;
  posterUrl?: string;
  year?: number;
  genreIds?: number[];
  genreNames?: string[];
}

export interface AdminMovieRequest {
  title: string;
  year?: number;
  description?: string;
  duration?: number;
  releaseDate?: string;
  language?: string;
  country?: string;
  status: string;
  ageRating?: string;
  posterUrl?: string;
  genreIds?: number[];
}
