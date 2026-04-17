package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.dto.request.branch.AddBranchRequest;
import vn.edu.fpt.dto.request.branch.BranchEditRequest;
import vn.edu.fpt.dto.response.branch.BranchEditResponse;
import vn.edu.fpt.dto.response.branch.BranchViewResponse;
import vn.edu.fpt.dto.response.branch.AddBranchResponse;
import vn.edu.fpt.dto.response.branch.BranchListResponse;
import vn.edu.fpt.entity.Branch;
import vn.edu.fpt.service.branch.BranchService;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping
    public ResponseEntity<BranchListResponse> getBranches(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name
    ) {
        List<Branch> branches = branchService.getBranches(code, name);

        return ResponseEntity.ok(
                new BranchListResponse(branches, "Danh sách chi nhánh", branches.size())
        );
    }
    @PostMapping
    public ResponseEntity<AddBranchResponse> addBranch(
            @RequestBody AddBranchRequest request
    ) {
        // Gửi yêu cầu tới service để thêm chi nhánh
        AddBranchResponse response = branchService.addBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchViewResponse> getBranchDetail(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchDetail(id));
    }

    @PutMapping("/{branchId}")
    public ResponseEntity<BranchEditResponse> editBranch(
            @PathVariable Long branchId,
            @RequestBody BranchEditRequest request
    ) {
        BranchEditResponse response = branchService.editBranch(branchId, request);
        return ResponseEntity.ok(response);
    }
}