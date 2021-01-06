package hu.herolds.projects.morale.controller.dto.paging

import hu.herolds.projects.morale.domain.enums.Language

class JokeSearchRequest(
    page: Page,
    val text: String? = null,
    val language: Language? = null,
): PagedRequest(page)