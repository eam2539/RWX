package com.corrodinggames.rts.gameFramework.graphics.opengl;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.m.p */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/m/p.class */
public enum GraphicsOperation {
    clipPath_Path_Op { // from class: com.corrodinggames.rts.gameFramework.m.p.1
    },
    clipPath_Path { // from class: com.corrodinggames.rts.gameFramework.m.p.12
    },
    clipRect_float_float_float_float_Op { // from class: com.corrodinggames.rts.gameFramework.m.p.23
    },
    clipRect_float_float_float_float { // from class: com.corrodinggames.rts.gameFramework.m.p.34
    },
    clipRect_int_int_int_int { // from class: com.corrodinggames.rts.gameFramework.m.p.45
    },
    clipRect_Rect_Op { // from class: com.corrodinggames.rts.gameFramework.m.p.56
    },
    clipRect_Rect { // from class: com.corrodinggames.rts.gameFramework.m.p.67
    },
    clipRect_RectF_Op { // from class: com.corrodinggames.rts.gameFramework.m.p.71
    },
    clipRect_RectF { // from class: com.corrodinggames.rts.gameFramework.m.p.72
    },
    concat_Matrix { // from class: com.corrodinggames.rts.gameFramework.m.p.2
    },
    drawARGB_int_int_int_int { // from class: com.corrodinggames.rts.gameFramework.m.p.3
    },
    drawArc_RectF_float_float_boolean_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.4
    },
    drawBitmap_Bitmap_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.5
    },
    drawBitmap_Bitmap_Matrix_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.6
    },
    drawBitmap_Bitmap_Rect_Rect_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.7
    },
    drawBitmap_Bitmap_Rect_RectF_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.8
    },
    drawBitmap_intarray_int_int_float_float_int_int_boolean_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.9
    },
    drawBitmap_intarray_int_int_int_int_int_int_boolean_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.10
    },
    drawBitmapMesh_Bitmap_int_int_floatarray_int_intarray_int_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.11
    },
    drawCircle_float_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.13
    },
    drawColor_int_Mode { // from class: com.corrodinggames.rts.gameFramework.m.p.14
    },
    drawColor_int { // from class: com.corrodinggames.rts.gameFramework.m.p.15
    },
    drawLine_float_float_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.16
    },
    drawLines_floatarray_int_int_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.17
    },
    drawLines_floatarray_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.18
    },
    drawOval_RectF_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.19
    },
    drawPaint_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.20
    },
    drawPath_Path_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.21
    },
    drawPicture_Picture_Rect { // from class: com.corrodinggames.rts.gameFramework.m.p.22
    },
    drawPicture_Picture_RectF { // from class: com.corrodinggames.rts.gameFramework.m.p.24
    },
    drawPicture_Picture { // from class: com.corrodinggames.rts.gameFramework.m.p.25
    },
    drawPoint_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.26
    },
    drawPoints_floatarray_int_int_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.27
    },
    drawPoints_floatarray_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.28
    },
    drawPosText_chararray_int_int_floatarray_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.29
    },
    drawPosText_String_floatarray_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.30
    },
    drawRGB_int_int_int { // from class: com.corrodinggames.rts.gameFramework.m.p.31
    },
    drawRect_float_float_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.32
    },
    drawRect_Rect_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.33
    },
    drawRect_RectF_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.35
    },
    drawRoundRect_RectF_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.36
    },
    drawText_chararray_int_int_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.37
    },
    drawText_CharSequence_int_int_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.38
    },
    drawText_String_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.39
    },
    drawText_String_int_int_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.40
    },
    drawTextOnPath_chararray_int_int_Path_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.41
    },
    drawTextOnPath_String_Path_float_float_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.42
    },
    drawVertices_VertexMode_int_floatarray_int_floatarray_int_intarray_int_shortarray_int_int_Paint { // from class: com.corrodinggames.rts.gameFramework.m.p.43
    },
    restore { // from class: com.corrodinggames.rts.gameFramework.m.p.44
    },
    restoreToCount_int { // from class: com.corrodinggames.rts.gameFramework.m.p.46
    },
    rotate_float { // from class: com.corrodinggames.rts.gameFramework.m.p.47
    },
    rotate_float_float_float { // from class: com.corrodinggames.rts.gameFramework.m.p.48
    },
    save { // from class: com.corrodinggames.rts.gameFramework.m.p.49
    },
    saveLayer_float_float_float_float_Paint_int { // from class: com.corrodinggames.rts.gameFramework.m.p.50
    },
    saveLayer_RectF_Paint_int { // from class: com.corrodinggames.rts.gameFramework.m.p.51
    },
    saveLayerAlpha_float_float_float_float_int_int { // from class: com.corrodinggames.rts.gameFramework.m.p.52
    },
    saveLayerAlpha_RectF_int_int { // from class: com.corrodinggames.rts.gameFramework.m.p.53
    },
    scale_float_float { // from class: com.corrodinggames.rts.gameFramework.m.p.54
    },
    scale_float_float_float_float { // from class: com.corrodinggames.rts.gameFramework.m.p.55
    },
    setBitmap_Bitmap { // from class: com.corrodinggames.rts.gameFramework.m.p.57
    },
    setDensity_int { // from class: com.corrodinggames.rts.gameFramework.m.p.58
    },
    setDrawFilter_DrawFilter { // from class: com.corrodinggames.rts.gameFramework.m.p.59
    },
    setMatrix_Matrix { // from class: com.corrodinggames.rts.gameFramework.m.p.60
    },
    skew_float_float { // from class: com.corrodinggames.rts.gameFramework.m.p.61
    },
    translate_float_float { // from class: com.corrodinggames.rts.gameFramework.m.p.62
    },
    runDrawTimeCallback_DrawTimeCallback { // from class: com.corrodinggames.rts.gameFramework.m.p.63
    },
    runDrawTimeCallback_DrawTimeCallback_float_float_float_paint { // from class: com.corrodinggames.rts.gameFramework.m.p.64
    },
    flushBitmap { // from class: com.corrodinggames.rts.gameFramework.m.p.65
    },
    enterLock_object { // from class: com.corrodinggames.rts.gameFramework.m.p.66
    },
    leaveLock_object { // from class: com.corrodinggames.rts.gameFramework.m.p.68
    },
    compileShader_object { // from class: com.corrodinggames.rts.gameFramework.m.p.69
    },
    setShader_object { // from class: com.corrodinggames.rts.gameFramework.m.p.70
    }
}
