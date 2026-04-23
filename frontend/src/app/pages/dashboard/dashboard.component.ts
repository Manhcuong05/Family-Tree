import { Component, OnInit } from '@angular/core';
import { LineageService } from '../../core/services/lineage.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  stats: any = {
    totalMembers: 0,
    males: 0,
    females: 0,
    maxGeneration: 0
  };
  lineageInfo: any = {};

  constructor(private lineageService: LineageService) {}

  ngOnInit(): void {
    // Branch ID fixed to 1 for demo or fetched from Auth
    const branchId = '00000000-0000-0000-0000-000000000001'; 
    
    this.lineageService.getStats(branchId).subscribe({
      next: (data) => this.stats = data,
      error: (err) => console.error('Error fetching stats:', err)
    });

    this.lineageService.getInfo(branchId).subscribe({
      next: (data) => this.lineageInfo = data,
      error: (err) => console.error('Error fetching info:', err)
    });
  }
}
