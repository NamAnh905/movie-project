export interface Cinema {
  id: number;
  name: string;
  address?: string;
  status: 'ACTIVE' | 'INACTIVE';
}
