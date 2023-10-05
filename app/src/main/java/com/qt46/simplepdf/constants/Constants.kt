package com.qt46.simplepdf.constants

import com.qt46.simplepdf.R
import com.qt46.simplepdf.data.Screen
import com.qt46.simplepdf.ui.theme.Tool

val tools = listOf(
    Tool(R.drawable.ic_merge, R.string.tool_merge),
    Tool(R.drawable.ic_split, R.string.tool_split),
    Tool(R.drawable.ic_image_to_pdf, R.string.tool_image),
    Tool(R.drawable.ic_edit, R.string.tool_edit),
    Tool(R.drawable.ic_browse, R.string.tool_browse),
    Tool(R.drawable.ic_browse, R.string.tool_optimize),
    Tool(R.drawable.ic_extract_text, R.string.action_extract_text),
    Tool(R.drawable.ic_reorder, R.string.action_reorder),
Tool(R.drawable.ic_extract_img,R.string.tools_extract_img)
)
const val TOOL_BROWSE_PDF=4
const val TOOL_MERGE_PDF=0
const val TOOL_SPLIT_PDF=1
const val TOOL_IMAGE_TO_PDF=2
const val TOOL_OPTIMIZE=5
const val TOOL_REORDER=7
const val TOOL_EXTRACT_TEXT=6
const val TOOL_EDIT_META=3
val items = listOf(
    Screen.AllPDF,

    Screen.Tools,

    Screen.More
)
const val SCALE_PREVIEW_REORDER=4