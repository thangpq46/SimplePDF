package com.qt46.simplepdf.data

import com.itextpdf.kernel.geom.LineSegment
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.parser.EventType
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener

import java.util.Collections


public class SimplePositionalTextEventListener : IEventListener {
    private val textWithRectangleList: MutableList<SimpleTextWithRectangle> =
        ArrayList()

    private fun renderText(renderInfo: TextRenderInfo) {
        if (renderInfo.text.trim().length === 0) return
        val ascent: LineSegment = renderInfo.ascentLine
        val descent: LineSegment = renderInfo.descentLine
        val initX: Float = descent.startPoint.get(0)
        val initY: Float = descent.startPoint.get(1)
        val endX: Float = ascent.endPoint.get(0)
        val endY: Float = ascent.endPoint.get(1)
        val rectangle = Rectangle(initX, initY, endX - initX, endY - initY)
        val textWithRectangle = SimpleTextWithRectangle(rectangle, renderInfo.text)
        textWithRectangleList.add(textWithRectangle)
    }

    override fun eventOccurred(data: IEventData, type: EventType) {
        renderText(data as TextRenderInfo)
    }

    override fun getSupportedEvents(): Set<EventType> {
        return Collections.unmodifiableSet(LinkedHashSet(listOf(EventType.RENDER_TEXT)))
    }

    fun getResultantTextWithPosition(): List<SimpleTextWithRectangle> {
        return textWithRectangleList
    }
}

data class SimpleTextWithRectangle(val rectangle: Rectangle, val text: String)