import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LineageService } from '../../core/services/lineage.service';
import { LineageInfo } from '../../core/models/genealogy.model';

@Component({
  selector: 'app-lineage-info',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lineage-info.component.html',
  styleUrl: './lineage-info.component.scss'
})
export class LineageInfoComponent implements OnInit {
  info: LineageInfo = {
    tenDongHo: 'Trần Vũ Bản',
    loiNgo: '',
    lichSu: '',
    diaChiNhaTho: ''
  };
  activeTab = 'general';
  isEditing = false;

  constructor(private lineageService: LineageService) {}

  ngOnInit(): void {
    const branchId = '00000000-0000-0000-0000-000000000001';
    this.lineageService.getInfo(branchId).subscribe(data => this.info = data);
  }

  save() {
    this.lineageService.updateInfo(this.info).subscribe(() => {
      this.isEditing = false;
      alert('Cập nhật thành công!');
    });
  }
}
