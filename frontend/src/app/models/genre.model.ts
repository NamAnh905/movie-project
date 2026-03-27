export interface Genre {
  id: number;
  name: string;
  slug?: string;
}

export interface AdminGenreRequest {
  name: string;
  slug?: string;
}
