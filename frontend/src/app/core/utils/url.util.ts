import { environment } from '../../../environments/environment';

export function toApi(path: string) {
  return `${environment.baseUrl}${path}`;
}

export function toAbs(url?: string) {
  if (!url) return '';
  return url.startsWith('http') ? url : `${environment.baseUrl}${url}`;
}
