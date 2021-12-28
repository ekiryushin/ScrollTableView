package io.github.ekiryushin.scrolltableview.scrolled

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView

class HorizontalScrollTableView: HorizontalScrollView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    var listener: OnScrollListener? = null

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldy: Int) {
        listener?.onScrollChanged(this, x, y, oldX, oldy)
        super.onScrollChanged(x, y, oldX, oldy)
    }
}