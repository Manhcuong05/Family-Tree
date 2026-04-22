import { Component, OnInit } from '@angular/core';
import { MemberService } from '../../core/services/member.service';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [],
  templateUrl: './members.component.html',
  styleUrl: './members.component.scss'
})
export class MembersComponent implements OnInit {
  members: any[] = [];

  constructor(private memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  loadMembers() {
    this.memberService.getAllMembers().subscribe({
      next: (data) => {
        // Assume API returns a list of members directly or inside a content array
        this.members = Array.isArray(data) ? data : data.content || [];
      },
      error: (err) => console.error('Error fetching members:', err)
    });
  }
}
