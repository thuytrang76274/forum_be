package com.ptithcm.forum.repository.impl;

import com.ptithcm.forum.dto.PostViewRequest;
import com.ptithcm.forum.entity.Post;
import com.ptithcm.forum.repository.PostRepositoryExtend;
import com.ptithcm.forum.util.StringConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PostRepositoryExtendImpl implements PostRepositoryExtend {

  private EntityManager em;

  public PostRepositoryExtendImpl(EntityManager em) {
    this.em = em;
  }

  private static final String GET_POST_QUERY = " from Post p "
      + "left join fetch p.module m "
      + "left join fetch p.comments cu "
      + "left join fetch m.systemCode sm "
      + "left join fetch p.issue i "
      + "left join fetch i.assignees asi "
      + "left join fetch i.customer c "
      + "left join fetch c.systemCode sc "
      + "left join fetch i.type t "
      + "left join fetch t.systemCode st "
      + "left join fetch i.image im "
      + "where 1 = 1 "
      + "and (p.status <> 'WAITING_APPROVE') "
      + "and (:status is null or p.status = :status) "
      + "and (:typeId is null or t.id = :typeId) "
      + "and (:customerId is null or c.id = :customerId) "
      + "and (:moduleId is null or m.id = :moduleId) "
      + "and (:version is null or i.version = :version) "
      + "and (:isDealCustomer is null or i.isDealCustomer = :isDealCustomer) "
      + "and (:isAppendix is null or i.isAppendix = :isAppendix) "
      + "and (coalesce(:assigneeIds) is null or asi.id in :assigneeIds) "
      + "and (coalesce(:reporter) is null or p.createdBy in :reporter) ";

  @Override
  public Page<Post> getPostsByConditions(PostViewRequest postViewRequest, Pageable pageable) {
    StringBuilder selectQuery = new StringBuilder("select p ");
    StringBuilder countQuery = new StringBuilder("select count(*) ");
    StringBuilder fromQuery = new StringBuilder(GET_POST_QUERY);
    for (String dateType : StringConstants.POST_DATE_TYPE_ACCEPT) {
      if (dateType.equalsIgnoreCase(postViewRequest.getTypeDate())) {
        fromQuery.append("and (:fromDate is null or p.").append(dateType).append(" >= :fromDate) ");
        fromQuery.append("and (:toDate is null or p.").append(dateType).append(" < :toDate) ");
        fromQuery.append("order by p.").append(dateType).append(" desc ");
        break;
      }
    }
    selectQuery.append(fromQuery);
    countQuery.append(fromQuery);
    TypedQuery<Post> typedSelectQuery = em.createQuery(selectQuery.toString(), Post.class);
    appendParameters(typedSelectQuery, postViewRequest);
    List<Post> posts = typedSelectQuery.setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
    TypedQuery<Long> typedCountQuery = em.createQuery(
        countQuery.toString().replaceAll("left join fetch", "left join"), Long.class);
    appendParameters(typedCountQuery, postViewRequest);
    Long totalCount = typedCountQuery.getSingleResult();
    return new PageImpl<>(posts, pageable, totalCount);
  }

  private void appendParameters(TypedQuery<?> query, PostViewRequest postViewRequest) {
    query.setParameter("status", postViewRequest.getStatus())
        .setParameter("reporter", postViewRequest.getReporter())
        .setParameter("assigneeIds", postViewRequest.getAssigneeIds())
        .setParameter("typeId", postViewRequest.getTypeId())
        .setParameter("customerId", postViewRequest.getCustomerId())
        .setParameter("moduleId", postViewRequest.getModuleId())
        .setParameter("version", postViewRequest.getVersion())
        .setParameter("isDealCustomer", postViewRequest.getIsDealCustomer())
        .setParameter("isAppendix", postViewRequest.getIsAppendix())
        .setParameter("fromDate", postViewRequest.getFromDate())
        .setParameter("toDate", null == postViewRequest.getToDate() ? null :
            postViewRequest.getToDate().plusDays(1));
  }
}
