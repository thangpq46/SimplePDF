package com.qt46.simplepdf.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.qt46.simplepdf.R

sealed class Screen(val route: String, @StringRes val resourceId: Int,@DrawableRes val iconId:Int) {
    object Tools : Screen("Tools", R.string.tools,R.drawable.ic_tool)
    object AllPDF : Screen("allPDF", R.string.all_pdf,R.drawable.ic_browse)
    object More : Screen("more", R.string.more,R.drawable.ic_more)
    object Merge : Screen("merge", R.string.merge,R.drawable.ic_more)
    object SelectFile : Screen("select", R.string.merge,R.drawable.ic_more)
    object SplitFile : Screen("split", R.string.merge,R.drawable.ic_more)
    object ImageToPDF : Screen("imgtopdf", R.string.merge,R.drawable.ic_more)
    object ReOrderPage : Screen("reorder", R.string.merge,R.drawable.ic_more)
    object EditMetaData : Screen("metadata", R.string.merge,R.drawable.ic_more)
}