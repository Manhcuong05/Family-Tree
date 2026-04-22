import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MemberService {
  private apiUrl = 'http://localhost:8080/api/v1/members';

  constructor(private http: HttpClient) {}

  getAllMembers(): Observable<any> {
    return this.http.get(`${this.apiUrl}`);
  }

  getTree(rootId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/tree/${rootId}`);
  }

  addMember(memberData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, memberData);
  }

  addRelationship(relationshipData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/relationship`, relationshipData);
  }
}
