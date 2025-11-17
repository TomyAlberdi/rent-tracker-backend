package rent.tracker.backend.Mapper;

import rent.tracker.backend.DTO.Property.CreatePropertyDTO;
import rent.tracker.backend.Model.Property;
import rent.tracker.backend.Model.Group;

public class PropertyMapper {
    
    public static Property toEntity(CreatePropertyDTO dto) {
        Property property = new Property();
        updateFromDTO(property, dto);
        return property;
    }
    
    public static void updateFromDTO(Property property, CreatePropertyDTO dto) {
        property.setName(dto.getName());
        property.setDescription(dto.getDescription());
        property.setType(dto.getType());
        if (dto.getType().equals(Property.PropertyType.GROUPED)) {
            property.setGroupId(dto.getGroupId());
        } else {
            property.setGroupId(null);
        }
    }
    
}
