package rent.tracker.backend.Mapper;

import rent.tracker.backend.DTO.Group.CreateGroupDTO;
import rent.tracker.backend.DTO.Group.GroupDTO;
import rent.tracker.backend.Model.Group;
import rent.tracker.backend.Model.Property;

import java.util.List;
import java.util.stream.Collectors;

public class GroupMapper {
    
    public static GroupDTO toDTO(Group pg, List<Property> properties) {
        GroupDTO dto = new GroupDTO();
        dto.setId(pg.getId());
        dto.setName(pg.getName());
        dto.setDescription(pg.getDescription());
        dto.setProperties(properties);
        return dto;
    }
    
    public static Group toEntity(CreateGroupDTO dto) {
        Group pg = new Group();
        updateFromDTO(pg, dto);
        return pg;
    }
    
    public static void updateFromDTO(Group group, CreateGroupDTO dto) {
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
    }
    
}
