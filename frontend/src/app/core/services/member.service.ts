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

  getMemberById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  getTree(rootId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/tree/${rootId}`);
  }

  addMember(memberData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, memberData);
  }

  uploadAvatar(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post('http://localhost:8080/api/files/upload', formData);
  }

  addRelationship(relationshipData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/relationship`, relationshipData);
  }

  updateMember(id: string, member: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, member);
  }

  deleteMember(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
