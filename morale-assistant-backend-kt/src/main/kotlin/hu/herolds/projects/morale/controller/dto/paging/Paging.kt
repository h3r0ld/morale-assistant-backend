package hu.herolds.projects.morale.controller.dto.paging

open class Page(
    val pageIndex: Int,
    val pageSize: Int
)

class Paging(
    pageIndex: Int,
    pageSize: Int,
    val totalPages: Int,
    val totalElements: Long
): Page(pageIndex, pageSize)

open class PagedRequest(
    val page: Page
)

class PagedResponse<T>(
    val content: List<T>,
    val paging: Paging
)

fun <Entity, Dto> org.springframework.data.domain.Page<Entity>.toPagedResponse(mapper: (Entity) -> Dto): PagedResponse<Dto> {
    return PagedResponse(content = this.content.map(mapper), paging = Paging(
        pageIndex = this.number,
        pageSize = this.numberOfElements,
        totalPages = this.totalPages,
        totalElements = this.totalElements)
    )
}