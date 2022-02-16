package hu.herolds.projects.morale.controller.dto.paging

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

interface PagedRequest {
    val page: Int
    val pageSize: Int

    fun toPageRequest(sort: Sort = Sort.unsorted()) = PageRequest.of(page, pageSize, sort);
}