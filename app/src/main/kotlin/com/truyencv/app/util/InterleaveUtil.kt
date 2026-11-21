package com.truyencv.app.util

/**
 * Thuật toán xen kẽ kết quả (interleave) từ 2 danh sách nguồn.
 * Khi người dùng chọn chế độ 'Tất cả nguồn' (Unified Source).
 */
fun <T> interleave(list1: List<T>, list2: List<T>): List<T> {
    val result = ArrayList<T>(list1.size + list2.size)
    val maxLen = maxOf(list1.size, list2.size)
    for (i in 0 until maxLen) {
        if (i < list1.size) result.add(list1[i])
        if (i < list2.size) result.add(list2[i])
    }
    return result
}
