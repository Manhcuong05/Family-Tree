export interface Member {
  id?: string;
  hoTen: string;
  tenGoiKhac?: string;
  gioiTinh: 'NAM' | 'NU';
  ngaySinh?: string;
  ngaySinhAmLich?: string;
  namSinhDuDoan?: number;
  ngayMat?: string;
  ngayMatAmLich?: string;
  ngayGio?: string;
  soDoi: number;
  tieuSu?: string;
  avatarUrl?: string;
  isAlive: boolean;
  branchId?: string;
}

export interface LineageInfo {
  id?: string;
  tenDongHo: string;
  loiNgo?: string;
  lichSu?: string;
  diaChiNhaTho?: string;
  bannerUrl?: string;
  branchId?: string;
}

export interface Article {
  id?: string;
  title: string;
  content: string;
  thumbnailUrl?: string;
  category: 'PHA_KY' | 'TIN_TUC' | 'SU_KIEN';
  branchId: string;
}

export interface FamilyEvent {
  id?: string;
  title: string;
  description?: string;
  startTime: string;
  endTime?: string;
  isLunar: boolean;
  lunarDate?: string;
  memberId?: string;
  branchId: string;
}
