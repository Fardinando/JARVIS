package com.fernando.jarvis.system

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        var instance: JarvisAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Eventos de acessibilidade sao processados aqui
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    fun findAndClick(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val node = findNodeByText(root, text) ?: return false

        val rect = Rect()
        node.getBoundsInScreen(rect)
        node.recycle()

        return clickAt(rect.centerX().toFloat(), rect.centerY().toFloat())
    }

    fun clickAt(x: Float, y: Float): Boolean {
        val path = Path().apply {
            moveTo(x, y)
            lineTo(x + 1, y + 1)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 50))
            .build()

        return dispatchGesture(gesture, null, null)
    }

    fun clickOnView(viewId: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val node = findNodeByViewId(root, viewId) ?: return false

        val rect = Rect()
        node.getBoundsInScreen(rect)
        node.recycle()

        return clickAt(rect.centerX().toFloat(), rect.centerY().toFloat())
    }

    private fun findNodeByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        if (node.text?.toString()?.lowercase()?.contains(text.lowercase()) == true) {
            return node
        }
        if (node.contentDescription?.toString()?.lowercase()?.contains(text.lowercase()) == true) {
            return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findNodeByText(child, text)
            if (result != null) {
                child.recycle()
                return result
            }
            child.recycle()
        }
        return null
    }

    private fun findNodeByViewId(node: AccessibilityNodeInfo, viewId: String): AccessibilityNodeInfo? {
        if (node.viewIdResourceName?.contains(viewId) == true) {
            return node
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findNodeByViewId(child, viewId)
            if (result != null) {
                child.recycle()
                return result
            }
            child.recycle()
        }
        return null
    }

    fun getCurrentPackageName(): String? {
        return rootInActiveWindow?.packageName?.toString()
    }
}
