package vn.edu.fpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<BranchListResponse> getAllBranches() {
        List<Branch> branches = branchService.getAllBranches();

        if (branches.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new BranchListResponse(branches, "Không có chi nhánh nào", 0));
        }

        return ResponseEntity.ok(new BranchListResponse(branches, "Danh sách chi nhánh", branches.size()));
    }
}