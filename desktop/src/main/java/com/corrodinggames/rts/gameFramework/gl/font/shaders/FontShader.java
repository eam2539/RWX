package com.corrodinggames.rts.gameFramework.gl.font.shaders;

import com.corrodinggames.rts.gameFramework.gl.font.ShaderAttributeType;

/* JADX INFO: renamed from: com.corrodinggames.rts.gameFramework.b.a.a.a */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/gameFramework/b/a/a/a.class */
public class FontShader extends ShaderProgramBase {
    private static final ShaderAttributeType[] a = {ShaderAttributeType.A_Position, ShaderAttributeType.A_TexCoordinate};

    @Override // com.corrodinggames.rts.gameFramework.gl.font.shaders.ShaderProgramBase
    public void a() {
        super.a("uniform mat4 u_MVPMatrix;      \nattribute vec4 a_Position;     \nattribute vec2 a_TexCoordinate;\nvarying   vec2 v_TexCoordinate;\nvoid main()                    \n{                              \n   v_TexCoordinate = a_TexCoordinate; \n   gl_Position = u_MVPMatrix   \n               * a_Position;   \n}                              \n", "uniform sampler2D u_Texture;       \nprecision mediump float;       \nuniform lowp vec4 u_Color;          \nvarying vec2 v_TexCoordinate;  \nvoid main()                    \n{                              \n   gl_FragColor = texture2D(u_Texture, v_TexCoordinate).w * u_Color;\n}                             \n", a);
    }
}
