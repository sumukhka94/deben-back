package com.sumukh.debenback.services;

import com.sumukh.debenback.dtos.CreateGroupRequest;
import com.sumukh.debenback.entities.Group;
import com.sumukh.debenback.entities.User;
import com.sumukh.debenback.repositories.GroupRepository;
import com.sumukh.debenback.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Group createGroup(CreateGroupRequest request) {
        User creator = userRepository.findById(request.getCreatedBy())
            .orElseThrow(() -> new RuntimeException("Creator user not found"));
        
        List<User> members = userRepository.findAllById(request.getMemberIds());
        if (members.size() != request.getMemberIds().size()) {
            throw new RuntimeException("Some member users not found");
        }
        
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setCreatedBy(creator);
        group.setMembers(members);
        
        return groupRepository.save(group);
    }
    
    @Transactional
    public void deleteGroup(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new RuntimeException("Group not found");
        }
        groupRepository.deleteById(groupId);
    }
}