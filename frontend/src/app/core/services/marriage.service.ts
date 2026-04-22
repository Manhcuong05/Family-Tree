import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MarriageService {
  private apiUrl = 'http://localhost:8080/api/v1/marriages';

  constructor(private http: HttpClient) {}

  getAllMarriages(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  createMarriage(marriage: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, marriage);
  }
}
