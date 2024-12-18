package com.ptithcm.forum.repository.impl;

import com.ptithcm.forum.dto.IssueViewRequest;
import com.ptithcm.forum.entity.Issue;
import com.ptithcm.forum.repository.IssueRepositoryExtend;
import com.ptithcm.forum.util.StringConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class IssueRepositoryExtendImpl implements IssueRepositoryExtend {

  EntityManager em;

  public IssueRepositoryExtendImpl(EntityManager em) {
    this.em = em;
  }

  private static final String GET_ISSUES_QUERY = " from Issue i "
      + "left join fetch i.customer c "
      + "left join fetch c.systemCode sc "
      + "left join fetch i.image im "
      + "left join fetch i.type t "
      + "left join fetch t.systemCode st "
      + "left join fetch i.assignees asi "
      + "where 1=1 "
      + "and (:status is null or i.status = :status) "
      + "and (:typeId is null or t.id = :typeId) "
      + "and (:customerId is null or c.id = :customerId) "
      + "and (:version is null or i.version = :version) "
      + "and (coalesce(:assigneeIds) is null or asi.id in :assigneeIds) ";

  @Override
  public Page<Issue> getIssuesByConditions(IssueViewRequest issueViewRequest, Pageable pageable) {
    StringBuilder selectQuery = new StringBuilder("select i ");
    StringBuilder countQuery = new StringBuilder("select count(*) ");
    StringBuilder fromQuery = new StringBuilder(GET_ISSUES_QUERY);
    for (String dateType : StringConstants.ISSUE_DATE_TYPE_ACCEPT) {
      if (dateType.equalsIgnoreCase(issueViewRequest.getTypeDate())) {
        fromQuery.append("and (:fromDate is null or i.").append(dateType).append(" >= :fromDate) ");
        fromQuery.append("and (:toDate is null or i.").append(dateType).append(" < :toDate) ");
        fromQuery.append("order by i.").append(dateType).append(" desc ");
        break;
      }
    }
    selectQuery.append(fromQuery);
    countQuery.append(fromQuery);
    TypedQuery<Issue> typedSelectQuery = em.createQuery(selectQuery.toString(), Issue.class);
    appendParameters(typedSelectQuery, issueViewRequest);
    List<Issue> issues = typedSelectQuery.setFirstResult(pageable.getPageNumber())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
    TypedQuery<Long> typedCountQuery = em.createQuery(
        countQuery.toString().replaceAll("left join fetch", "left join"), Long.class);
    appendParameters(typedCountQuery, issueViewRequest);
    Long totalCount = typedCountQuery.getSingleResult();
    return new PageImpl<>(issues, pageable, totalCount);
  }

  private void appendParameters(TypedQuery<?> query, IssueViewRequest issueViewRequest) {
    query.setParameter("status", issueViewRequest.getStatus())
        .setParameter("typeId", issueViewRequest.getTypeId())
        .setParameter("customerId", issueViewRequest.getCustomerId())
        .setParameter("assigneeIds", issueViewRequest.getAssigneeIds())
        .setParameter("version", issueViewRequest.getVersion())
        .setParameter("fromDate", issueViewRequest.getFromDate())
        .setParameter("toDate", null == issueViewRequest.getToDate() ? null :
            issueViewRequest.getToDate().plusDays(1));
  }
}
