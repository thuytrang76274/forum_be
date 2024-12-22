package com.ptithcm.forum.controller;

import com.ptithcm.forum.dto.IssueDto;
import com.ptithcm.forum.dto.IssueViewRequest;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.entity.IssueStatus;
import com.ptithcm.forum.service.IssueService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${rest.issue}")
@Validated
public class IssueController {

  private final IssueService issueService;

  public IssueController(IssueService issueService) {
    this.issueService = issueService;
  }

  @GetMapping
  public ResponseDto<PageDto<IssueDto>> findAll(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "typeDate", defaultValue = "dueDate") String typeDate,
      @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
      @RequestParam(name = "toDate", required = false) LocalDate toDate,
      @RequestParam(name = "status", required = false) IssueStatus status,
      @RequestParam(name = "assignee", required = false) List<Long> assigneeIds,
      @RequestParam(name = "type", required = false) Long typeId,
      @RequestParam(name = "customer", required = false) Long customerId,
      @RequestParam(name = "version", required = false) String version) {
    return issueService.getPageOfIssues(IssueViewRequest.builder()
        .page(page).size(size).typeDate(typeDate).fromDate(fromDate).toDate(toDate).status(status)
        .assigneeIds(assigneeIds).typeId(typeId).customerId(customerId).version(version).build());
  }

  @PostMapping
  public ResponseEntity<ResponseDto<IssueDto>> createIssue(
      @RequestPart("image") MultipartFile imageFile,
      @RequestPart("issue") String issueString) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(issueService.createIssue(imageFile, issueString));
  }

  @GetMapping("/versions")
  public ResponseDto<List<String>> getVersions() {
    return issueService.getVersions();
  }

  @GetMapping("/call-penpot")
  public ResponseEntity<Void> callPenpot() {
    issueService.getAndSaveIssuesFromPenpot();
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PatchMapping("/{id}")
  public ResponseDto<IssueDto> updateIssue(@PathVariable("id") Long issueId,
      @RequestBody IssueDto issueUpdateDto) {
    return issueService.updateIssue(issueId, issueUpdateDto);
  }

  @PatchMapping("/{id}/deal-customer")
  public ResponseDto<IssueDto> dealCustomer(@PathVariable("id") Long issueId) {
    return issueService.dealCustomer(issueId);
  }

  @GetMapping("/{id}")
  public ResponseDto<IssueDto> getIssue(@PathVariable("id") Long issueId) {
    return issueService.getSingleIssue(issueId);
  }
}
