package rent.tracker.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rent.tracker.backend.DTO.Group.CreateGroupDTO;
import rent.tracker.backend.DTO.Group.GroupDTO;
import rent.tracker.backend.Repository.GroupRepository;
import rent.tracker.backend.Service.GroupService;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {
    
    private final GroupService groupService;
    private final GroupRepository groupRepository;
    
    @GetMapping("/list")
    public ResponseEntity<?> getGroups() {
        return ResponseEntity.ok(groupService.getGroups());
    }
    
    @GetMapping("/list/light")
    public ResponseEntity<?> getLightGroups() {
        return ResponseEntity.ok(groupService.getLightGroups());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable String id) {
        GroupDTO groupDTO = groupService.getGroupById(id);
        return ResponseEntity.ok(groupDTO);
    }
    
    @PostMapping
    public ResponseEntity<?> addGroup(@RequestBody CreateGroupDTO dto) {
        return ResponseEntity.ok(groupService.createGroup(dto));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(
            @PathVariable String id,
            @RequestBody CreateGroupDTO dto
    ) {
        return ResponseEntity.ok(groupService.updateGroup(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable String id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
    
}
