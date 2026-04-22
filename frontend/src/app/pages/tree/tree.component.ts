import { Component } from '@angular/core';

@Component({
  selector: 'app-tree',
  standalone: true,
  imports: [],
  templateUrl: './tree.component.html',
  styleUrl: './tree.component.scss'
})
export class TreeComponent {
  selectedMember = true; // Show by default for demo
}
