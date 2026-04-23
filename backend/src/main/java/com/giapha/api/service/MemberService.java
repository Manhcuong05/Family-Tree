package com.giapha.api.service;

import com.giapha.api.dto.MemberFlatDto;
import com.giapha.api.entity.Member;
import com.giapha.api.entity.Relationship;
import com.giapha.api.enums.RelationshipType;
import com.giapha.api.exception.CircularDependencyException;
import com.giapha.api.exception.InvalidAgeException;
import com.giapha.api.repository.MemberRepository;
import com.giapha.api.repository.RelationshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RelationshipRepository relationshipRepository;

    @Transactional
    public Member createMember(Member member, UUID parentId, RelationshipType type) {
        // Default branch ID for now
        if (member.getBranchId() == null) {
            member.setBranchId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }

        if (parentId != null) {
            Member relative = memberRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Relative (Parent/Spouse) not found"));
            
            if (type == RelationshipType.VO_CHONG) {
                // Spouse belongs to the same generation
                member.setSoDoi(relative.getSoDoi());
            } else {
                // Child belongs to the next generation
                member.setSoDoi(relative.getSoDoi() + 1);
                // Validate Age for parent-child relationship
                validateAgeDifference(relative, member);
            }
            
            validateGeneration(member.getSoDoi());
            member = memberRepository.save(member);
            
            Relationship relationship = Relationship.builder()
                    .parent(relative) // Repurposing 'parent' field for 'member1' in spouse case
                    .child(member)   // Repurposing 'child' field for 'member2'
                    .loaiQuanHe(type != null ? type : RelationshipType.RUOT)
                    .build();
            relationshipRepository.save(relationship);
        } else {
            if (member.getSoDoi() == null) {
                member.setSoDoi(1); // Thủy tổ default
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

        validateAgeDifference(parent, child);
        checkCircularDependency(parentId, childId);

        // Update child generation if it's currently incorrect or empty
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

    private void validateAgeDifference(Member parent, Member child) {
        long ageDiff = 0;
        if (parent.getNgaySinh() != null && child.getNgaySinh() != null) {
            ageDiff = ChronoUnit.YEARS.between(parent.getNgaySinh(), child.getNgaySinh());
        } else if (parent.getNamSinhDuDoan() != null && child.getNamSinhDuDoan() != null) {
            ageDiff = child.getNamSinhDuDoan() - parent.getNamSinhDuDoan();
        } else {
            return; // Cannot validate without dates
        }

        if (ageDiff < 0) {
            throw new InvalidAgeException("Con không thể sinh trước cha/mẹ!");
        }

        if (ageDiff < 12 || ageDiff > 70) {
            // According to rule 3.1: Warning if outside 12-70, but we'll throw for now as a strict rule
            // In a real app, this would be a warning flag in the response
            throw new InvalidAgeException("Cảnh báo: Khoảng cách tuổi giữa cha/mẹ và con (" + ageDiff + " tuổi) không hợp lệ (quy định 12-70).");
        }
    }

    private void validateGeneration(Integer soDoi) {
        if (soDoi < 1 || soDoi > 99) {
            throw new IllegalArgumentException("Số đời không hợp lệ (quy định từ 1 đến 99).");
        }
    }

    private void checkCircularDependency(UUID newParentId, UUID newChildId) {
        // Simple check: Is the new parent actually a descendant of the new child?
        // If so, adding this relationship would create a cycle.
        List<Object[]> descendants = memberRepository.findDescendantsFlat(newChildId);
        for (Object[] row : descendants) {
            UUID descendantId = (UUID) row[0];
            if (descendantId.equals(newParentId)) {
                throw new CircularDependencyException("Circular dependency detected: The parent is already a descendant of the child.");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<MemberFlatDto> getFamilyTreeFlat(UUID rootId) {
        List<Object[]> results = memberRepository.findDescendantsFlat(rootId);
        List<MemberFlatDto> dtos = new ArrayList<>();
        
        for (Object[] row : results) {
            UUID id = (UUID) row[0];
            String name = (String) row[1];
            Integer soDoi = (Integer) row[2];
            UUID pId = (UUID) row[3];
            
            dtos.add(MemberFlatDto.builder()
                    .id(id != null ? id.toString() : null)
                    .name(name)
                    .soDoi(soDoi)
                    .pid(pId != null ? pId.toString() : null)
                    .build());
        }
        return dtos;
    }
}
