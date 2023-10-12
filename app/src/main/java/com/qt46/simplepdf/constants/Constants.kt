package com.qt46.simplepdf.constants

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.qt46.simplepdf.R
import com.qt46.simplepdf.data.Screen
import com.qt46.simplepdf.ui.theme.Tool

val tools = listOf(
    Tool(R.drawable.ic_merge, R.string.tool_merge,Color(0xFF67BE80)),
    Tool(R.drawable.ic_split, R.string.tool_split,Color(0xFFCA4646)),
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
const val TOOL_EXTRACT_IMAGE = 8
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
const val SPLIT_FOLDER="split"
const val REORDER_FOLDER="reorder"
const val EX_IMG_FOLDER="reorder"
const val EX_TXT_FOLDER="reorder"
const val DEFAULT_FILENAME="abc"

const val IMAGE_TO_EDIT = "image_to_edit"
const val TYPE_ALL_IMAGE = "image/*"
const val EDIT_IMAGE_TOOl = 1
const val FRAME_TOOL = 2
const val STICKER_TOOL = 6
const val TEXT_TOOL = 7
const val FILTERS_TOOl = 3
const val STORAGE_STICKERS = "stickers"
const val ZERO = 0f
const val ITEM_ACTIVE_NULL = -1
const val RECT_ITEM_EDIT_SIZE = 40
const val FIRST_INDEX = 0
const val DETECT_OBJECT_MODE = 0
const val BRUSH_MODE = 1
const val LASSO_MODE = 2
const val LOCAL_MODEL_FILE_PATH = "custom_models/object_labeler.tflite"
const val LABEL_PERSON = "Person"
const val TYPE_PEOPLE = 0
const val TYPE_OTHERS = 1
const val MAIN_TOOl_REMOVE_OBJECT = 1
const val MAIN_TOOL_STICKERS = 6
const val MAIN_TOOL_TEXT = 7
const val IMG_QUALITY = 100
const val IMAGE_FILE = "image_file"
const val MASK_FILE = "mask_file"
const val FILE_NAME = "image"
const val FILE_NAME_MASK = "mask.png"
const val MEDIA_PARSE_TYPE = "image/*"
const val HEADER_AUTH_KEY = "x-api-key"
const val IN_PAINTING_RADIUS = 5.0
const val DRAW_ALPHA = 0.6f
const val LOADING = false
const val INDILE = true
const val STORAGE_FILTERS = "filters"
const val STORAGE_FRAMES = "frames"
const val STORAGE_FONTS = "text_fonts"
const val ASSET_PATH = "file:///android_asset/"
const val BASE_URL = "https://apis.clipdrop.co/"
const val OLD_EXTENSION = "webp"
const val NEW_EXTENSION = "png"
const val FILTER_ALPHA = 0.2f
const val ONE_SECOND = 1000L
const val TOOL_INIT_INDEX = "ToolActive"
const val ADJUST_TOOL = 4

const val FIFTH_TI = 50
const val HUREND = 100
const val ADJUST_BRIGHTNESS = 0
const val ADJUST_WARMTH = 1
const val ADJUST_CONTRAST = 2
const val ADJUST_SATURATION = 3
const val URI_SAVED_IMAGE = "uri"

const val OWNER_FACEBOOK = "https://www.facebook.com/quangthang46/"
const val OWNER_INSTAGRAM = "https://www.instagram.com/pqt46/"
const val OWNER_TIKTOK = "https://www.tiktok.com/@qthang46"
const val OWNER_GMAIL = "pqt4621@gmail.com"
const val MAIL_TO = "mailto:"
const val MAIL_SUBJECT = "QTOR Support"
const val INTENT_TITLE = "Send email"
const val DEFAULT_MESSAGE_VALUE = "   "

const val QTOR_SHARED = "qtor"
const val LANGUAGE_SHARED = "language"
val colors = mutableListOf(
    Color(0xffffffff), Color(0xffbbbbbb), Color(0xff4e4e4e),
    Color(0xFF212121), Color(0xFF010101), Color(0xffffd7cd),
    Color(0xfffaab9c), Color(0xffcd3041), Color(0xfffff3c3),
    Color(0xfffee46b), Color(0xfff5af57), Color(0xfff48432),
    Color(0xfff43d03), Color(0xfffff1f4), Color(0xfffee1e3),
    Color(0xfff7aab4), Color(0xfffc2777), Color(0xffebd4e8),
    Color(0xffba63b2), Color(0xffa8368e), Color(0xff93d3f9),
    Color(0xff80aced), Color(0xff2260a9), Color(0xff93d3f9),
    Color(0xffdeefe9), Color(0xffb0d0c3), Color(0xff4aaea2),
    Color(0xff0e8c77), Color(0xff05674e), Color(0xffd2e5a3),
    Color(0xffaecd7d), Color(0xffa2ae1a), Color(0xff6e8618),
    Color(0xff446240), Color(0xffe4d8be), Color(0xffd5c68f),
    Color(0xffa58258), Color(0xff74462c)
)
const val API_KEY =
    "6e5026a54275eee7d07f3c8f2742c8cec7da474d2b3f34432f678c421526d39d7856f28e1c7142f775cfd9fa92cada45"
const val GOOGLE_URL = "https://www.google.com/"
const val APP_NAME = "qtor"
const val FRAME_FOLDER = "frames"
val FRAME_TITLES = listOf<Int>(
    R.string.frame_1,
    R.string.frame_2,
    R.string.frame_3,
    R.string.frame_4,
    R.string.frame_5,
    R.string.frame_6,
    R.string.frame_7
)

const val BITMAP_MAX_PIXEL_CLOUD = 12000000
const val MAIN_TOOL_FILTER = 3
const val BLUR = 2.0
const val CANNY_THRESHOLD_1 = 125.0
const val CANNY_THRESHOLD_2 = 175.0
const val PERCENT_INCREASE_HEIGHT_AI_PEOPLE = 20
const val MAIN_TOOL_TIMESTAMP = 8
const val TEXT_ADD_TEXT = 0
const val TEXT_ADD_EDITTEXT = 1