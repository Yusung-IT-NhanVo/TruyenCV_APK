package com.truyencv.app.util

data class CanonicalGenre(
    val slug: String,
    val name: String,
    val tcvGenreId: Long,
    val motTruyenCatId: Int
)

object GenreMappingTable {
    val GENRES: List<CanonicalGenre> = listOf(
        CanonicalGenre("tien-hiep", "Tiên Hiệp", 16L, 14),
        CanonicalGenre("kiem-hiep", "Kiếm Hiệp", 86L, 16),
        CanonicalGenre("do-thi", "Đô Thị", 23L, 15),
        CanonicalGenre("huyen-huyen", "Huyền Huyễn", 72L, 17),
        CanonicalGenre("huyen-ao", "Huyền Ảo", 1148L, 17),
        CanonicalGenre("khoa-huyen", "Khoa Huyễn", 39L, 22),
        CanonicalGenre("vong-du", "Võng Du", 167L, 20),
        CanonicalGenre("dong-nhan", "Đồng Nhân", 792L, 28),
        CanonicalGenre("lich-su", "Lịch Sử", 145L, 24),
        CanonicalGenre("quan-su", "Quân Sự", 1121L, 23),
        CanonicalGenre("di-nang", "Dị Năng", 134L, 19),
        CanonicalGenre("mat-the", "Mạt Thế", 40L, 32),
        CanonicalGenre("ngon-tinh", "Ngôn Tình", 19L, 18),
        CanonicalGenre("xuyen-khong", "Xuyên Không", 34L, 25),
        CanonicalGenre("trung-sinh", "Trùng Sinh", 1244L, 30),
        CanonicalGenre("canh-ky", "Cạnh Kỹ", 1292L, 27),
        CanonicalGenre("linh-di", "Linh Dị", 82L, 31),
        CanonicalGenre("he-thong", "Hệ Thống", 57L, 19),
        CanonicalGenre("de-cu", "Đề Cử", 639L, 0),
        CanonicalGenre("convert", "Convert", 972L, 1)
    )

    private val slugMap: Map<String, CanonicalGenre> = GENRES.associateBy { it.slug }
    private val tcvIdMap: Map<Long, CanonicalGenre> = GENRES.associateBy { it.tcvGenreId }
    private val motTruyenCatMap: Map<Int, List<CanonicalGenre>> = GENRES.groupBy { it.motTruyenCatId }

    fun getTcvGenreId(slug: String): Long? = slugMap[slug]?.tcvGenreId

    fun getMotTruyenCatId(slug: String): Int? = slugMap[slug]?.motTruyenCatId

    fun findSlugByTcvGenreId(id: Long): String? = tcvIdMap[id]?.slug

    fun findSlugByMotTruyenCatId(catId: Int): String? = motTruyenCatMap[catId]?.firstOrNull()?.slug

    fun getCanonicalName(slug: String): String? = slugMap[slug]?.name

    fun getAllCanonicalGenres(): List<CanonicalGenre> = GENRES
}
