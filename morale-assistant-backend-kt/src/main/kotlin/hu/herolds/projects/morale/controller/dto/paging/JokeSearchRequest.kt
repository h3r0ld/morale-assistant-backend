package hu.herolds.projects.morale.controller.dto.paging

import hu.herolds.projects.morale.domain.enums.Language

data class JokeSearchRequest(
    override val page: Int,
    override val pageSize: Int,
    val text: String? = null,
    val language: Language? = null,
): PagedRequest