package com.truyencv.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GenreMappingTableTest {

    @Test
    fun testTotalGenresCount() {
        val genres = GenreMappingTable.getAllCanonicalGenres()
        assertEquals(20, genres.size)
    }

    @Test
    fun testTcvGenreIdLookup() {
        assertEquals(16L, GenreMappingTable.getTcvGenreId("tien-hiep"))
        assertEquals(86L, GenreMappingTable.getTcvGenreId("kiem-hiep"))
        assertEquals(23L, GenreMappingTable.getTcvGenreId("do-thi"))
        assertEquals(72L, GenreMappingTable.getTcvGenreId("huyen-huyen"))
        assertEquals(1148L, GenreMappingTable.getTcvGenreId("huyen-ao"))
        assertEquals(39L, GenreMappingTable.getTcvGenreId("khoa-huyen"))
        assertEquals(167L, GenreMappingTable.getTcvGenreId("vong-du"))
        assertEquals(792L, GenreMappingTable.getTcvGenreId("dong-nhan"))
        assertEquals(145L, GenreMappingTable.getTcvGenreId("lich-su"))
        assertEquals(1121L, GenreMappingTable.getTcvGenreId("quan-su"))
        assertEquals(134L, GenreMappingTable.getTcvGenreId("di-nang"))
        assertEquals(40L, GenreMappingTable.getTcvGenreId("mat-the"))
        assertEquals(19L, GenreMappingTable.getTcvGenreId("ngon-tinh"))
        assertEquals(34L, GenreMappingTable.getTcvGenreId("xuyen-khong"))
        assertEquals(1244L, GenreMappingTable.getTcvGenreId("trung-sinh"))
        assertEquals(1292L, GenreMappingTable.getTcvGenreId("canh-ky"))
        assertEquals(82L, GenreMappingTable.getTcvGenreId("linh-di"))
        assertEquals(57L, GenreMappingTable.getTcvGenreId("he-thong"))
        assertEquals(639L, GenreMappingTable.getTcvGenreId("de-cu"))
        assertEquals(972L, GenreMappingTable.getTcvGenreId("convert"))

        assertNull(GenreMappingTable.getTcvGenreId("unknown-genre"))
    }

    @Test
    fun testMotTruyenCatIdLookup() {
        assertEquals(14, GenreMappingTable.getMotTruyenCatId("tien-hiep"))
        assertEquals(16, GenreMappingTable.getMotTruyenCatId("kiem-hiep"))
        assertEquals(15, GenreMappingTable.getMotTruyenCatId("do-thi"))
        assertEquals(17, GenreMappingTable.getMotTruyenCatId("huyen-huyen"))
        assertEquals(17, GenreMappingTable.getMotTruyenCatId("huyen-ao"))
        assertEquals(22, GenreMappingTable.getMotTruyenCatId("khoa-huyen"))
        assertEquals(20, GenreMappingTable.getMotTruyenCatId("vong-du"))
        assertEquals(28, GenreMappingTable.getMotTruyenCatId("dong-nhan"))
        assertEquals(24, GenreMappingTable.getMotTruyenCatId("lich-su"))
        assertEquals(23, GenreMappingTable.getMotTruyenCatId("quan-su"))
        assertEquals(19, GenreMappingTable.getMotTruyenCatId("di-nang"))
        assertEquals(32, GenreMappingTable.getMotTruyenCatId("mat-the"))
        assertEquals(18, GenreMappingTable.getMotTruyenCatId("ngon-tinh"))
        assertEquals(25, GenreMappingTable.getMotTruyenCatId("xuyen-khong"))
        assertEquals(30, GenreMappingTable.getMotTruyenCatId("trung-sinh"))
        assertEquals(27, GenreMappingTable.getMotTruyenCatId("canh-ky"))
        assertEquals(31, GenreMappingTable.getMotTruyenCatId("linh-di"))
        assertEquals(19, GenreMappingTable.getMotTruyenCatId("he-thong"))
        assertEquals(0, GenreMappingTable.getMotTruyenCatId("de-cu"))
        assertEquals(1, GenreMappingTable.getMotTruyenCatId("convert"))

        assertNull(GenreMappingTable.getMotTruyenCatId("unknown-slug"))
    }

    @Test
    fun testReverseLookups() {
        assertEquals("tien-hiep", GenreMappingTable.findSlugByTcvGenreId(16L))
        assertEquals("kiem-hiep", GenreMappingTable.findSlugByTcvGenreId(86L))
        assertNull(GenreMappingTable.findSlugByTcvGenreId(99999L))

        assertEquals("tien-hiep", GenreMappingTable.findSlugByMotTruyenCatId(14))
        assertEquals("kiem-hiep", GenreMappingTable.findSlugByMotTruyenCatId(16))
        assertNull(GenreMappingTable.findSlugByMotTruyenCatId(9999))
    }

    @Test
    fun testCanonicalNames() {
        assertEquals("Tiên Hiệp", GenreMappingTable.getCanonicalName("tien-hiep"))
        assertEquals("Đề Cử", GenreMappingTable.getCanonicalName("de-cu"))
        assertEquals("Convert", GenreMappingTable.getCanonicalName("convert"))
        assertNull(GenreMappingTable.getCanonicalName("non-existent"))
    }
}
