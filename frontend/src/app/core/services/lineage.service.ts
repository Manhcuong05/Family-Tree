import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LineageInfo } from '../models/genealogy.model';

@Injectable({
  providedIn: 'root'
})
export class LineageService {
  private apiUrl = 'http://localhost:8080/api/v1/lineage';

  constructor(private http: HttpClient) {}

  getInfo(branchId: string): Observable<LineageInfo> {
    return this.http.get<LineageInfo>(`${this.apiUrl}/${branchId}/info`);
  }

  updateInfo(info: LineageInfo): Observable<LineageInfo> {
    return this.http.post<LineageInfo>(`${this.apiUrl}/info`, info);
  }

  getStats(branchId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${branchId}/stats`);
  }
}
