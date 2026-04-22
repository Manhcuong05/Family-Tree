package com.giapha.api.service;

import com.giapha.api.entity.Member;
import com.giapha.api.entity.Relationship;
import com.giapha.api.enums.Gender;
import com.giapha.api.enums.RelationshipType;
import com.giapha.api.exception.CircularDependencyException;
import com.giapha.api.exception.InvalidAgeException;
import com.giapha.api.repository.MemberRepository;
import com.giapha.api.repository.RelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @InjectMocks
    private MemberService memberService;

    private Member parent;
    private Member child;
    private UUID parentId;
    private UUID childId;

    @BeforeEach
    void setUp() {
        parentId = UUID.randomUUID();
        childId = UUID.randomUUID();

        parent = Member.builder()
                .id(parentId)
                .hoTen("Parent")
                .soDoi(1)
                .gioiTinh(Gender.MALE)
                .build();

        child = Member.builder()
                .id(childId)
                .hoTen("Child")
                .soDoi(2)
                .gioiTinh(Gender.MALE)
                .build();
    }

    @Test
    void addRelationship_Success() {
        // Arrange
        parent.setNgaySinh(LocalDate.of(1980, 1, 1));
        child.setNgaySinh(LocalDate.of(2005, 1, 1)); // 25 years difference

        when(memberRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(memberRepository.findById(childId)).thenReturn(Optional.of(child));
        when(memberRepository.findDescendantsFlat(childId)).thenReturn(new ArrayList<>());
        when(relationshipRepository.save(any(Relationship.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Relationship result = memberService.addRelationship(parentId, childId, RelationshipType.RUOT);

        // Assert
        assertNotNull(result);
        assertEquals(parent, result.getParent());
        assertEquals(child, result.getChild());
        verify(relationshipRepository, times(1)).save(any(Relationship.class));
    }

    @Test
    void addRelationship_InvalidAge_ThrowsException() {
        // Arrange: Age difference is only 10 years (must be >= 12)
        parent.setNgaySinh(LocalDate.of(1990, 1, 1));
        child.setNgaySinh(LocalDate.of(2000, 1, 1));

        when(memberRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(memberRepository.findById(childId)).thenReturn(Optional.of(child));

        // Act & Assert
        assertThrows(InvalidAgeException.class, () -> {
            memberService.addRelationship(parentId, childId, RelationshipType.RUOT);
        });
        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void addRelationship_CircularDependency_ThrowsException() {
        // Arrange: We are trying to add (parent -> child), but parent is already a descendant of child
        when(memberRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(memberRepository.findById(childId)).thenReturn(Optional.of(child));
        
        // Mock that child's descendants include the parent
        List<Object[]> descendants = new ArrayList<>();
        descendants.add(new Object[]{parentId, "Parent", 3, childId}); // Mock CTE return
        when(memberRepository.findDescendantsFlat(childId)).thenReturn(descendants);

        // Act & Assert
        assertThrows(CircularDependencyException.class, () -> {
            memberService.addRelationship(parentId, childId, RelationshipType.RUOT);
        });
        verify(relationshipRepository, never()).save(any());
    }

    @Test
    void addRelationship_SelfParenting_ThrowsException() {
        // Act & Assert
        assertThrows(CircularDependencyException.class, () -> {
            memberService.addRelationship(parentId, parentId, RelationshipType.RUOT);
        });
    }
}
