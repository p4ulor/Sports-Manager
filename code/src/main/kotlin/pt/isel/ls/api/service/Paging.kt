package pt.isel.ls.api.service

import org.slf4j.LoggerFactory
import pt.isel.ls.api.utils.BadRequestException
import pt.isel.ls.api.webapi.LIMIT
import pt.isel.ls.api.webapi.UPPERBOUND_LIMIT

private val logger = LoggerFactory.getLogger("pt.isel.ls.service.Paging")

data class Paging(val limit: Int = LIMIT, val skip: Int = 0) {
    init {
        if(!isValidLimit(limit)) throw BadRequestException("Limit must be between 0 and $UPPERBOUND_LIMIT")
        if(!isValidSkip(skip)) throw BadRequestException("Skip must be equal or greater than 0")
        //logger.info("Limit: $limit and skip: $skip")
    }

    companion object {
        fun isValid(limit: Int, skip: Int) = isValidLimit(limit) && isValidSkip(skip)
        private fun isValidLimit(limit: Int) = limit in 0..UPPERBOUND_LIMIT
        private fun isValidSkip(skip: Int) = skip >= 0
    }
}

/**
 * Old Paging functionality aux method:
 * if @startIndex exceeds the list size, an empty list is returned,
 * if @limitIndex is 0, an empty list is returned
 * if @limitIndex exceeds the listSize, the @limitIndex is trimmed down to size-1
 *
 * Note: IntRange(1,0).isEmpty() -> returns true
 */

fun computeRangeForPaging(paging: Paging, listSize: Int) : IntRange? {
    var startIndex = paging.skip
    if(paging.limit==0 || listSize==0 || startIndex >= listSize) return null
    var limitIndex = startIndex + paging.limit -1 //since slice() is inclusive, if limit = 1 and skip = 0
    if(limitIndex >= listSize) limitIndex = listSize-1
    logger.info("range: [$startIndex, $limitIndex]. Listsize: $listSize")
    return IntRange(startIndex, limitIndex) //inclusive [startIndex, limitIndex]
}
