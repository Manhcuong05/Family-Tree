package com.giapha.api.service;

import com.giapha.api.dto.TreeNodeDto;
import com.giapha.api.entity.Marriage;
import com.giapha.api.entity.Member;
import com.giapha.api.entity.Relationship;
import com.giapha.api.enums.Gender;
import com.giapha.api.enums.RelationshipType;
import com.giapha.api.exception.CircularDependencyException;
import com.giapha.api.exception.InvalidAgeException;
import com.giapha.api.repository.MarriageRepository;
import com.giapha.api.repository.MemberRepository;
import com.giapha.api.repository.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RelationshipRepository relationshipRepository;
    private final MarriageRepository marriageRepository;

    @Transactional
    public Member createMember(Member member, UUID parentId, RelationshipType type) {
        if (member.getBranchId() == null) {
            member.setBranchId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }

        if (parentId != null) {
            Member relative = memberRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Thành viên liên quan không tồn tại"));

            if (type == RelationshipType.VO_CHONG) {
                member.setSoDoi(relative.getSoDoi());
            } else {
                member.setSoDoi(relative.getSoDoi() + 1);
                validateAgeDifference(relative, member);
            }

            validateGeneration(member.getSoDoi());
            member = memberRepository.save(member);

            if (type == RelationshipType.VO_CHONG) {
                // Tự động tạo Marriage record
                Marriage marriage = new Marriage();
                if (relative.getGioiTinh() == Gender.NAM) {
                    marriage.setHusband(relative);
                    marriage.setWife(member);
                } else {
                    marriage.setHusband(member);
                    marriage.setWife(relative);
                }
                // Tìm xem chồng đã có bao nhiêu vợ để đặt thuTuVo
                List<Marriage> existing = marriageRepository.findByHusband(marriage.getHusband());
                marriage.setThuTuVo(existing.size() + 1);
                marriageRepository.save(marriage);
            } else {
                if (type != RelationshipType.VO_CHONG) {
                    // If the selected relative is a spouse (not in main lineage), try to link to the blood member
                    boolean isMainLineage = relationshipRepository.existsByChildId(parentId) || memberRepository.count() == 1 || relative.getSoDoi() == 1;
                    
                    Member targetParent = relative;
                    if (!isMainLineage) {
                        // Try to find the spouse who is in the lineage
                        List<Marriage> marriages = marriageRepository.findByMemberIds(List.of(parentId));
                        if (!marriages.isEmpty()) {
                            Marriage m = marriages.get(0);
                            targetParent = m.getHusband().getId().equals(parentId) ? m.getWife() : m.getHusband();
                        }
                    }

                    Relationship relationship = Relationship.builder()
                            .parent(targetParent)
                            .child(member)
                            .loaiQuanHe(type != null ? type : RelationshipType.RUOT)
                            .build();
                    relationshipRepository.save(relationship);
                }
            }
        } else {
            if (member.getSoDoi() == null) {
                member.setSoDoi(1);
            }
            member = memberRepository.save(member);
        }
        return member;
    }

    @Transactional
    public Relationship addRelationship(UUID parentId, UUID childId, RelationshipType type) {
        if (parentId.equals(childId)) {
            throw new CircularDependencyException("A member cannot be their own parent.");
        }

        Member parent = memberRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Member child = memberRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Child not found"));

        if (type == RelationshipType.VO_CHONG) {
            // Xử lý tạo Marriage thay vì Relationship nếu type là VO_CHONG
            Marriage marriage = new Marriage();
            if (parent.getGioiTinh() == Gender.NAM) {
                marriage.setHusband(parent);
                marriage.setWife(child);
            } else {
                marriage.setHusband(child);
                marriage.setWife(parent);
            }
            marriageRepository.save(marriage);
            return null; // Hoặc trả về một Relationship giả nếu cần
        }

        validateAgeDifference(parent, child);
        checkCircularDependency(parentId, childId);

        if (child.getSoDoi() == null || child.getSoDoi() <= parent.getSoDoi()) {
            child.setSoDoi(parent.getSoDoi() + 1);
            memberRepository.save(child);
        }

        Relationship relationship = Relationship.builder()
                .parent(parent)
                .child(child)
                .loaiQuanHe(type)
                .build();
        return relationshipRepository.save(relationship);
    }

    @Transactional
    public Member updateMember(UUID id, Member updatedData, UUID parentId, RelationshipType relType) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thành viên không tồn tại"));

        // 1. Cập nhật các thông tin cơ bản
        existing.setHoTen(updatedData.getHoTen());
        existing.setGioiTinh(updatedData.getGioiTinh());
        existing.setIsAlive(updatedData.getIsAlive());
        existing.setNgaySinhAmLich(updatedData.getNgaySinhAmLich());
        existing.setNgayMatAmLich(updatedData.getNgayMatAmLich());
        existing.setTieuSu(updatedData.getTieuSu());
        existing.setAvatarUrl(updatedData.getAvatarUrl());
        existing.setTenGoiKhac(updatedData.getTenGoiKhac());
        existing.setNgaySinh(updatedData.getNgaySinh());
        existing.setNamSinhDuDoan(updatedData.getNamSinhDuDoan());
        existing.setNgayGio(updatedData.getNgayGio());

        // 2. Xử lý thay đổi quan hệ / cha mẹ
        List<Relationship> currentRels = relationshipRepository.findByChildId(id);
        Relationship currentRel = currentRels.isEmpty() ? null : currentRels.get(0);

        if (parentId != null) {
            // Kiểm tra xem parentId có thay đổi không
            if (currentRel == null || !currentRel.getParent().getId().equals(parentId) || 
                (relType != null && currentRel.getLoaiQuanHe() != relType)) {
                
                Member newParent = memberRepository.findById(parentId)
                        .orElseThrow(() -> new IllegalArgumentException("Cha/Mẹ mới không tồn tại"));
                
                // Tránh vòng lặp và tự tham chiếu
                if (parentId.equals(id)) {
                    throw new CircularDependencyException("Một thành viên không thể là cha/mẹ của chính mình.");
                }
                checkCircularDependency(parentId, id);

                if (currentRel == null) {
                    currentRel = new Relationship();
                    currentRel.setChild(existing);
                }
                currentRel.setParent(newParent);
                currentRel.setLoaiQuanHe(relType != null ? relType : RelationshipType.RUOT);
                relationshipRepository.save(currentRel);

                // Cập nhật số đời dựa trên cha mẹ mới
                if (currentRel.getLoaiQuanHe() == RelationshipType.VO_CHONG) {
                    existing.setSoDoi(newParent.getSoDoi());
                } else {
                    existing.setSoDoi(newParent.getSoDoi() + 1);
                }
            }
        } else if (currentRel != null) {
            // Nếu set parentId = null (cắt đứt quan hệ cũ)
            relationshipRepository.delete(currentRel);
            // Mặc định về Đời 1 nếu không có cha mẹ
            existing.setSoDoi(1);
        }

        // 3. LƯU THÀNH VIÊN TRƯỚC
        Member saved = memberRepository.save(existing);

        // 4. LAN TRUYỀN THAY ĐỔI ĐỜI XUỐNG CÁC CON (RECURSIVE)
        updateGenerationRecursively(saved, new HashSet<>());

        return saved;
    }

    private void updateGenerationRecursively(Member parent, Set<UUID> visited) {
        if (visited.contains(parent.getId())) {
            return;
        }
        visited.add(parent.getId());

        List<Relationship> childrenRels = relationshipRepository.findByParentId(parent.getId());
        for (Relationship rel : childrenRels) {
            Member child = rel.getChild();
            int newSoDoi;
            if (rel.getLoaiQuanHe() == RelationshipType.VO_CHONG) {
                newSoDoi = parent.getSoDoi();
            } else {
                newSoDoi = parent.getSoDoi() + 1;
            }

            if (child.getSoDoi() != newSoDoi) {
                child.setSoDoi(newSoDoi);
                memberRepository.save(child);
                // Đệ quy xuống đời sau của con
                updateGenerationRecursively(child, visited);
            }
        }
    }

    @Transactional
    public void deleteMember(UUID id) {
        boolean hasChildren = relationshipRepository.existsByParentId(id);
        if (hasChildren) {
            throw new IllegalStateException("Không thể xóa thành viên đã có đời sau (con cái) để tránh đứt gãy phả hệ.");
        }
        relationshipRepository.deleteByChildId(id);
        memberRepository.deleteById(id);
    }

    private void validateAgeDifference(Member parent, Member child) {
        long ageDiff = 0;
        if (parent.getNgaySinh() != null && child.getNgaySinh() != null) {
            ageDiff = ChronoUnit.YEARS.between(parent.getNgaySinh(), child.getNgaySinh());
        } else if (parent.getNamSinhDuDoan() != null && child.getNamSinhDuDoan() != null) {
            ageDiff = child.getNamSinhDuDoan() - parent.getNamSinhDuDoan();
        } else {
            return;
        }

        if (ageDiff < 0) {
            throw new InvalidAgeException("Con không thể sinh trước cha/mẹ!");
        }

        if (ageDiff < 12 || ageDiff > 70) {
            throw new InvalidAgeException("Cảnh báo: Khoảng cách tuổi giữa cha/mẹ và con (" + ageDiff + " tuổi) không hợp lệ (quy định 12-70).");
        }
    }

    private void validateGeneration(Integer soDoi) {
        if (soDoi < 1 || soDoi > 99) {
            throw new IllegalArgumentException("Số đời không hợp lệ (quy định từ 1 đến 99).");
        }
    }

    private void checkCircularDependency(UUID newParentId, UUID newChildId) {
        List<Object[]> descendants = memberRepository.findDescendantsFlat(newChildId);
        for (Object[] row : descendants) {
            UUID descendantId = (UUID) row[0];
            if (descendantId.equals(newParentId)) {
                throw new CircularDependencyException("Vòng lặp phả hệ detected!");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<TreeNodeDto> getFamilyTreeFlat(UUID rootId) {
        List<Object[]> results = memberRepository.findDescendantsFlat(rootId);

        List<UUID> memberIds = results.stream()
                .map(row -> (UUID) row[0])
                .collect(Collectors.toList());

        List<Marriage> marriages = marriageRepository.findByMemberIds(memberIds);

        // Map memberId -> List of marriages (to support multiple wives/husbands)
        Map<UUID, List<Marriage>> memberToMarriages = new HashMap<>();
        // Map to help re-link children of spouses back to the lineage member
        Map<UUID, UUID> spouseToMainMember = new HashMap<>();

        for (Marriage m : marriages) {
            UUID hId = m.getHusband().getId();
            UUID wId = m.getWife().getId();
            
            memberToMarriages.computeIfAbsent(hId, k -> new ArrayList<>()).add(m);
            memberToMarriages.computeIfAbsent(wId, k -> new ArrayList<>()).add(m);

            boolean husbandInLineage = memberIds.contains(hId);
            boolean wifeInLineage = memberIds.contains(wId);

            if (husbandInLineage && !wifeInLineage) {
                spouseToMainMember.put(wId, hId);
            } else if (!husbandInLineage && wifeInLineage) {
                spouseToMainMember.put(hId, wId);
            }
        }

        List<TreeNodeDto> dtos = new ArrayList<>();

        for (Object[] row : results) {
            UUID id       = (UUID)    row[0];
            String hoTen  = (String)  row[1];
            Integer soDoi = (Integer) row[2];
            UUID parentId = (UUID)    row[3];

            // If parentId is a spouse (not in main lineage), link child to the main partner
            if (parentId != null && spouseToMainMember.containsKey(parentId)) {
                parentId = spouseToMainMember.get(parentId);
            }
            String avUrl  = (String)  row[4];
            String gender = (String)  row[5];
            Boolean alive = row[6] != null ? (Boolean) row[6] : true;
            String ngaySinhAL = (String) row[7];
            String ngayMatAL  = (String) row[8];
            String tieuSu     = (String) row[9];
            String loaiQH     = (String) row[10];

            TreeNodeDto dto = TreeNodeDto.builder()
                    .id(id.toString())
                    .hoTen(hoTen)
                    .soDoi(soDoi)
                    .parentId(parentId != null ? parentId.toString() : null)
                    .avatarUrl(avUrl)
                    .gioiTinh(gender)
                    .isAlive(alive)
                    .ngaySinhAmLich(ngaySinhAL)
                    .ngayMatAmLich(ngayMatAL)
                    .tieuSu(tieuSu)
                    .loaiQuanHe(loaiQH != null ? loaiQH : "RUOT")
                    .build();

            // Tìm spouse. Ưu tiên lấy vợ cả (thuTuVo=1) hoặc người đầu tiên trong list
            List<Marriage> mList = memberToMarriages.get(id);
            if (mList != null && !mList.isEmpty()) {
                // Sắp xếp theo thuTuVo
                mList.sort(Comparator.comparing(m -> m.getThuTuVo() != null ? m.getThuTuVo() : 99));
                Marriage m = mList.get(0);
                Member spouse = m.getHusband().getId().equals(id) ? m.getWife() : m.getHusband();
                
                dto.setSpouseId(spouse.getId().toString());
                dto.setSpouseHoTen(spouse.getHoTen());
                dto.setSpouseAvatarUrl(spouse.getAvatarUrl());
                dto.setSpouseGioiTinh(spouse.getGioiTinh() != null ? spouse.getGioiTinh().name() : "NU");
                dto.setSpouseIsAlive(spouse.getIsAlive());
                dto.setThuTuVo(m.getThuTuVo());
                dto.setMarriageStatus(m.getTrangThai() != null ? m.getTrangThai().name() : "DANG_KET_HON");
            }

            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<com.giapha.api.dto.MemberListItemDto> getAllMembersWithParent() {
        List<Member> members = memberRepository.findAll();
        List<Relationship> allRels = relationshipRepository.findAll();
        
        Map<UUID, Relationship> childToRel = allRels.stream()
                .collect(Collectors.toMap(r -> r.getChild().getId(), r -> r, (r1, r2) -> r1));

        return members.stream().map(m -> {
            Relationship rel = childToRel.get(m.getId());
            return com.giapha.api.dto.MemberListItemDto.builder()
                    .id(m.getId())
                    .hoTen(m.getHoTen())
                    .avatarUrl(m.getAvatarUrl())
                    .gioiTinh(m.getGioiTinh())
                    .soDoi(m.getSoDoi())
                    .isAlive(m.getIsAlive())
                    .ngaySinhAmLich(m.getNgaySinhAmLich())
                    .ngayMatAmLich(m.getNgayMatAmLich())
                    .tieuSu(m.getTieuSu())
                    .parentId(rel != null ? rel.getParent().getId() : null)
                    .loaiQuanHe(rel != null ? rel.getLoaiQuanHe() : null)
                    .build();
        }).collect(Collectors.toList());
    }
}
