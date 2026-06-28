package io.github.rwx.ui.component

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.scene.geometry.TextProps
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

/** Kool 0.19.0 ComboBox, using [RwxAutoPopup] for the Android IME fix. */
interface RwxComboBoxScope : UiScope {
    override val modifier: RwxComboBoxModifier
    val isHovered: Boolean
    val popupMenu: RwxAutoPopup
}

open class RwxComboBoxModifier(surface: UiSurface) : UiModifier(surface) {
    var font: Font by property { it.sizes.normalText }
    var items: List<Any> by property(emptyList())
    var selectedIndex: Int by property(0)
    var maxNumVisibleItems: Int by property(10)

    var textColor: Color by property { it.colors.onBackground }
    var textBackgroundColor: Color by property { it.colors.secondaryVariantAlpha(0.5f) }
    var textBackgroundHoverColor: Color by property { it.colors.secondaryAlpha(0.5f) }
    var expanderColor: Color by property { it.colors.secondaryVariant }
    var expanderHoverColor: Color by property { it.colors.secondary }
    var expanderArrowColor: Color by property { it.colors.onSecondary }

    var popupTextColor: Color by property { it.colors.onBackground }
    var popupBackgroundColor: Color by property { it.colors.backgroundVariant }
    var popupHoverColor: Color by property { it.colors.secondary }
    var popupHoverTextColor: Color by property { it.colors.onSecondary }
    var popupBorderColor: Color by property { it.colors.primaryVariantAlpha(0.5f) }

    var onItemSelected: ((Int) -> Unit)? by property(null)
}

fun <T : RwxComboBoxModifier> T.font(font: Font): T {
    this.font = font; return this
}

fun <T : RwxComboBoxModifier> T.textColor(color: Color): T {
    textColor = color; return this
}

fun <T : RwxComboBoxModifier> T.items(items: List<Any>): T {
    this.items = items; return this
}

fun <T : RwxComboBoxModifier> T.selectedIndex(index: Int): T {
    this.selectedIndex = index; return this
}

fun <T : RwxComboBoxModifier> T.onItemSelected(block: ((Int) -> Unit)?): T {
    onItemSelected = block; return this
}

fun <T : RwxComboBoxModifier> T.colors(
    textColor: Color = this.textColor,
    textBackgroundColor: Color = this.textBackgroundColor,
    textBackgroundHoverColor: Color = this.textBackgroundHoverColor,
    expanderColor: Color = this.expanderColor,
    expanderHoverColor: Color = this.expanderHoverColor,
    expanderArrowColor: Color = this.expanderArrowColor,
): T {
    this.textColor = textColor
    this.textBackgroundColor = textBackgroundColor
    this.textBackgroundHoverColor = textBackgroundHoverColor
    this.expanderColor = expanderColor
    this.expanderHoverColor = expanderHoverColor
    this.expanderArrowColor = expanderArrowColor
    return this
}

fun <T : RwxComboBoxModifier> T.popupColors(
    popupTextColor: Color = this.popupTextColor,
    popupBackgroundColor: Color = this.popupBackgroundColor,
    popupHoverColor: Color = this.popupHoverColor,
    popupHoverTextColor: Color = this.popupHoverTextColor,
    popupBorderColor: Color = this.popupBorderColor,
): T {
    this.popupTextColor = popupTextColor
    this.popupBackgroundColor = popupBackgroundColor
    this.popupHoverColor = popupHoverColor
    this.popupHoverTextColor = popupHoverTextColor
    this.popupBorderColor = popupBorderColor
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun UiScope.RwxComboBox(
    scopeName: String? = null,
    block: RwxComboBoxScope.() -> Unit,
): RwxComboBoxScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val comboBox = uiNode.createChild(scopeName, RwxComboBoxNode::class, RwxComboBoxNode.factory)
    comboBox.modifier
        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
        .hoverListener(comboBox)
        .onClick(comboBox)
        .onWheelY {
            if (it.pointer.scroll.y > 0.0 && comboBox.modifier.selectedIndex > 0) {
                comboBox.modifier.onItemSelected?.invoke(comboBox.modifier.selectedIndex - 1)
            } else if (
                it.pointer.scroll.y < 0.0 &&
                comboBox.modifier.selectedIndex < comboBox.modifier.items.lastIndex
            ) {
                comboBox.modifier.onItemSelected?.invoke(comboBox.modifier.selectedIndex + 1)
            }
        }
    comboBox.block()
    comboBox.popupMenu()
    return comboBox
}

open class RwxComboBoxNode(parent: UiNode?, surface: UiSurface) :
    UiNode(parent, surface), RwxComboBoxScope, Clickable, Hoverable {

    override val modifier = RwxComboBoxModifier(surface)
    override val isHovered: Boolean get() = isHoveredState.value

    private val textProps = TextProps(Font.DEFAULT_FONT)
    private val textCache = CachedTextGeometry(this)
    private val isHoveredState = mutableStateOf(false)

    override val popupMenu = RwxAutoPopup()

    private val selectedText: String
        get() = if (modifier.items.isEmpty()) {
            ""
        } else {
            modifier.items[modifier.selectedIndex.clamp(0, modifier.items.lastIndex)].toString()
        }

    override fun applyDefaults() {
        super.applyDefaults()
        popupMenu.popupContent = defaultComboBoxPopupMenu
    }

    override fun measureContentSize(ctx: KoolContext) {
        surface.applyFontScale(modifier.font, ctx)
        val textMetrics = textCache.getTextMetrics(selectedText, modifier.font)
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) {
            modWidth.px
        } else {
            textMetrics.width + paddingStartPx + paddingEndPx + sizes.largeGap.px * 1.5f
        }
        val measuredHeight = if (modHeight is Dp) {
            modHeight.px
        } else {
            textMetrics.height + paddingTopPx + paddingBottomPx
        }
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun render(ctx: KoolContext) {
        val customClip = MutableVec4f()
        val expanderWidth = sizes.largeGap.px * 1.5f
        val expanderBorder = round(rightPx - expanderWidth)

        var textBgColor = modifier.textBackgroundColor
        var arrowBgColor = modifier.expanderColor
        if (isHoveredState.use()) {
            textBgColor = modifier.textBackgroundHoverColor
            arrowBgColor = modifier.expanderHoverColor
        }

        if (modifier.background == null) {
            modifier.background(UiRenderer {
                val cornerRadius = sizes.smallGap.px
                val draw = getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                draw.localRoundRect(0f, 0f, widthPx, heightPx, cornerRadius, textBgColor)

                customClip.set(clipBoundsPx)
                customClip.x = max(clipLeftPx, expanderBorder)
                draw.roundRect(
                    rightPx - expanderWidth - cornerRadius,
                    topPx,
                    expanderWidth + cornerRadius,
                    heightPx,
                    cornerRadius,
                    customClip,
                    arrowBgColor,
                )
            })
        }

        super.render(ctx)

        textProps.apply {
            font = modifier.font
            text = selectedText
            isYAxisUp = false
            val textMetrics = textCache.textMetrics
            val oriX = paddingStartPx
            val oriY = (heightPx - textMetrics.height) / 2f + textMetrics.yBaseline
            origin.set(oriX, oriY, 0f)
        }
        customClip.set(clipBoundsPx)
        customClip.z = min(clipRightPx, expanderBorder)
        textCache.addTextGeometry(
            getTextBuilder(modifier.font).geometry,
            textProps,
            modifier.textColor,
            textClip = customClip,
        )

        getPlainBuilder().configured(modifier.expanderArrowColor) {
            val cx = widthPx - expanderWidth * 0.5f
            val cy = heightPx * 0.5f
            val d = sizes.smallGap.px * 2.5f
            arrow(cx, cy, d, 90f)
        }
    }

    override fun onEnter(ev: PointerEvent) {
        isHoveredState.set(true)
    }

    override fun onExit(ev: PointerEvent) {
        isHoveredState.set(false)
    }

    override fun onClick(ev: PointerEvent) {
        if (!popupMenu.isVisible.value) {
            popupMenu.show(Vec2f(uiNode.leftPx, uiNode.bottomPx))
        }
    }

    val defaultComboBoxPopupMenu = Composable {
        var hoveredIndex by remember(-1)
        val cbModifier = this@RwxComboBoxNode.modifier

        modifier
            .zLayer(cbModifier.zLayer + UiSurface.LAYER_POPUP)
            .background(RoundRectBackground(cbModifier.popupBackgroundColor, sizes.smallGap))
            .border(RoundRectBorder(cbModifier.popupBorderColor, sizes.smallGap, sizes.borderWidth))
            .padding(sizes.smallGap)
            .height(
                (Dp.fromPx(sizes.normalText.lineHeight) + sizes.smallGap) *
                        min(cbModifier.maxNumVisibleItems, cbModifier.items.size) + sizes.gap
            )

        LazyColumn(
            withHorizontalScrollbar = false,
            isScrollableHorizontal = false,
            vScrollbarModifier = {
                it.zLayer(cbModifier.zLayer + UiSurface.LAYER_POPUP + UiSurface.LAYER_FLOATING)
            },
        ) {
            itemsIndexed(cbModifier.items) { i, item ->
                Text(item.toString()) {
                    modifier
                        .width(Grow.Std)
                        .padding(horizontal = sizes.gap, vertical = sizes.smallGap * 0.5f)
                        .textColor(cbModifier.popupTextColor)
                        .onEnter { hoveredIndex = i }
                        .onExit { hoveredIndex = -1 }
                        .onClick {
                            cbModifier.onItemSelected?.invoke(i)
                            popupMenu.hide()
                            hoveredIndex = -1
                        }
                    if (i == hoveredIndex) {
                        modifier
                            .background(RoundRectBackground(cbModifier.popupHoverColor, sizes.smallGap))
                            .textColor(cbModifier.popupHoverTextColor)
                    }
                    if (cbModifier.items.size > cbModifier.maxNumVisibleItems) {
                        modifier.margin(end = sizes.gap)
                    }
                }
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> RwxComboBoxNode = { parent, surface ->
            RwxComboBoxNode(parent, surface)
        }
    }
}

fun interface RwxComboBoxPopupMenu {
    fun UiScope.composePopupMenu(items: List<Any>): Any
}
