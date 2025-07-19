package com.sumukh.debenback.services;

import com.sumukh.debenback.dtos.GroupSummaryDto;
import com.sumukh.debenback.dtos.MembersDto;
import com.sumukh.debenback.entities.Group;
import com.sumukh.debenback.entities.User;
import com.sumukh.debenback.repositories.ExpenseRepository;
import com.sumukh.debenback.repositories.GroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HomeService {
    private final GroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;

    private List<MembersDto> mapMembers(List<User> users) {
        List<MembersDto> members = new ArrayList<>();
        for (User u : users) {
            members.add(new MembersDto(u.getId(), u.getName(), "/placeholder.svg?height=32&width=32"));
        }
        return members;
    }

    public List<GroupSummaryDto> getHomeDataForUser(Long userId) {
        List<Group> groups = groupRepository.findGroupsForUser(userId);
        List<GroupSummaryDto> result = new ArrayList<>();

        for (Group group : groups) {
            Double sum = expenseRepository.sumByGroupId(group.getId());
            double totalExpenses = (sum != null) ? sum : 0.0;
            int expenseCount = expenseRepository.countByGroupId(group.getId());

            List<MembersDto> members = mapMembers(group.getMembers());

            result.add(new GroupSummaryDto(
                    group.getId(),
                    group.getName(),
                    group.getDescription(),
                    members,
                    totalExpenses,
                    expenseCount
            ));
        }

        return result;
    }
}
