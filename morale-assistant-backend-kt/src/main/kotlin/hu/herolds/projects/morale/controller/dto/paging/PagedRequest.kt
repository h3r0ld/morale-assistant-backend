package hu.herolds.projects.morale.controller.dto.paging

import org.springframework.data.domain.PageRequest

interface PagedRequest {
    val page: Int
    val pageSize: Int

    fun toPageRequest() = PageRequest.of(page, pageSize);
}