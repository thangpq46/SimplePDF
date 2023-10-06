package com.qt46.simplepdf.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.qt46.simplepdf.R
import com.qt46.simplepdf.data.Screen
import com.qt46.simplepdf.ui.theme.Tool

val tools = listOf(
    Tool(R.drawable.ic_merge, R.string.tool_merge,Color(0xFF448657)),
    Tool(R.drawable.ic_split, R.string.tool_split,Color(0xFF792323)),
    Tool(R.drawable.ic_image_to_pdf, R.string.tool_image,Color(0xFF6B471D)),
    Tool(R.drawable.ic_edit, R.string.tool_edit,Color(0xFF606425)),
    Tool(R.drawable.ic_browse, R.string.tool_browse,Color(0xFF30835C)),
    Tool(R.drawable.ic_browse, R.string.tool_optimize,Color(0xFF3D246D)),
    Tool(R.drawable.ic_extract_text, R.string.action_extract_text,Color(0xFF77296A)),
    Tool(R.drawable.ic_reorder, R.string.action_reorder,Color(0xFF923434)),
Tool(R.drawable.ic_extract_img,R.string.tools_extract_img,Color(0xFF922E57))
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
    Screen.Stared,
    Screen.Tools,
    Screen.More
)
const val SCALE_PREVIEW_REORDER=4
val FiraSansFamily = FontFamily(
    Font(R.font.youngrerifregular, FontWeight.Normal),

)
