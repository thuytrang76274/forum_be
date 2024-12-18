package com.ptithcm.forum.service;

import com.ptithcm.forum.dto.IssueDto;
import com.ptithcm.forum.dto.IssueViewRequest;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.ResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface IssueService {

  ResponseDto<IssueDto> createIssue(MultipartFile imageFile, String issueString);

  void getAndSaveIssuesFromPenpot();

  ResponseDto<IssueDto> getSingleIssue(Long issueId);

  ResponseDto<IssueDto> updateIssue(Long issueId, IssueDto issueUpdateDto);

  ResponseDto<IssueDto> dealCustomer(Long issueId);

  ResponseDto<PageDto<IssueDto>> getPageOfIssues(IssueViewRequest issueViewRequest);
}
