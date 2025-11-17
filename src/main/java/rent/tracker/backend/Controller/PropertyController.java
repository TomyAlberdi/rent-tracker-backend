package rent.tracker.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rent.tracker.backend.DTO.Property.CreatePropertyDTO;
import rent.tracker.backend.Model.Property;
import rent.tracker.backend.Repository.PropertyRepository;
import rent.tracker.backend.Service.PropertyService;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;

    @GetMapping
    public ResponseEntity<?> getIndividualProperties() {
        return ResponseEntity.ok(propertyRepository.findByType(Property.PropertyType.INDIVIDUAL));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProperty(@PathVariable String id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping
    public ResponseEntity<?> addProperty(@RequestBody CreatePropertyDTO dto) {
        return ResponseEntity.ok(propertyService.createProperty(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProperty(
            @PathVariable String id,
            @RequestBody CreatePropertyDTO dto
    ) {
        return ResponseEntity.ok(propertyService.updateProperty(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok().build();
    }

}
