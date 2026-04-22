import { Component, OnInit } from '@angular/core';
import { MemberService } from '../../core/services/member.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  totalMembers = 0;

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    this.memberService.getAllMembers().subscribe({
      next: (data) => this.totalMembers = data.length,
      error: (err) => console.error('Error fetching stats:', err)
    });
  }
}
