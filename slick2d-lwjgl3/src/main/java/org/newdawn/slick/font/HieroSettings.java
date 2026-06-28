
package org.newdawn.slick.font;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ConfigurableEffect;
import org.newdawn.slick.font.effects.ConfigurableEffect.Value;
import org.newdawn.slick.util.ResourceLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the settings needed to configure a UnicodeFont.
 *
 * @author Nathan Sweet <misc@n4te.com>
 */
public class HieroSettings {
    /**
     * The size of the font to be generated
     */
    private int fontSize = 12;
    /**
     * True if the font is rendered bold
     */
    private boolean bold = false;
    /**
     * True fi the font if rendered italic
     */
    private boolean italic = false;
    /**
     * The padding applied in pixels to the top of the glyph rendered area
     */
    private int paddingTop;
    /**
     * The padding applied in pixels to the left of the glyph rendered area
     */
    private int paddingLeft;
    /**
     * The padding applied in pixels to the bottom of the glyph rendered area
     */
    private int paddingBottom;
    /**
     * The padding applied in pixels to the right of the glyph rendered area
     */
    private int paddingRight;
    /**
     * The padding applied in pixels to horizontal advance for each glyph
     */
    private int paddingAdvanceX;
    /**
     * The padding applied in pixels to vertical advance for each glyph
     */
    private int paddingAdvanceY;
    /**
     * The width of the glyph page generated
     */
    private int glyphPageWidth = 512;
    /**
     * The height of the glyph page generated
     */
    private int glyphPageHeight = 512;
    /**
     * The list of effects applied
     */
    private final List effects = new ArrayList();

    /**
     * Default constructor for injection
     */
    public HieroSettings() {
    }

    /**
     * Create a new set of configuration from a file
     *
     * @param hieroFileRef The file system or classpath location of the Hiero settings file.
     * @throws SlickException if the file could not be read.
     */
    public HieroSettings(String hieroFileRef) throws SlickException {
        this(ResourceLoader.getResourceAsStream(hieroFileRef));
    }

    /**
     * Create a new set of configuration from a file
     *
     * @param in The stream from which to read the settings from
     * @throws SlickException if the file could not be read.
     */
    public HieroSettings(InputStream in) throws SlickException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.length() == 0) continue;
                String[] pieces = line.split("=", 2);
                String name = pieces[0].trim();
                String value = pieces[1];
                if (name.equals("font.size")) {
                    fontSize = Integer.parseInt(value);
                } else if (name.equals("font.bold")) {
                    bold = Boolean.valueOf(value).booleanValue();
                } else if (name.equals("font.italic")) {
                    italic = Boolean.valueOf(value).booleanValue();
                } else if (name.equals("pad.top")) {
                    paddingTop = Integer.parseInt(value);
                } else if (name.equals("pad.right")) {
                    paddingRight = Integer.parseInt(value);
                } else if (name.equals("pad.bottom")) {
                    paddingBottom = Integer.parseInt(value);
                } else if (name.equals("pad.left")) {
                    paddingLeft = Integer.parseInt(value);
                } else if (name.equals("pad.advance.x")) {
                    paddingAdvanceX = Integer.parseInt(value);
                } else if (name.equals("pad.advance.y")) {
                    paddingAdvanceY = Integer.parseInt(value);
                } else if (name.equals("glyph.page.width")) {
                    glyphPageWidth = Integer.parseInt(value);
                } else if (name.equals("glyph.page.height")) {
                    glyphPageHeight = Integer.parseInt(value);
                } else if (name.equals("effect.class")) {
                    try {
                        effects.add(Class.forName(value).newInstance());
                    } catch (Exception ex) {
                        throw new SlickException("Unable to create effect instance: " + value, ex);
                    }
                } else if (name.startsWith("effect.")) {
                    // Set an effect value on the last added effect.
                    name = name.substring(7);
                    ConfigurableEffect effect = (ConfigurableEffect) effects.get(effects.size() - 1);
                    List values = effect.getValues();
                    for (Iterator iter = values.iterator(); iter.hasNext(); ) {
                        Value effectValue = (Value) iter.next();
                        if (effectValue.getName().equals(name)) {
                            effectValue.setString(value);
                            break;
                        }
                    }
                    effect.setValues(values);
                }
            }
            reader.close();
        } catch (Exception ex) {
            throw new SlickException("Unable to load Hiero font file", ex);
        }
    }

    /**
     * @return The padding for the top of the glyph area in pixels
     * @see UnicodeFont#getPaddingTop()
     */
    public int getPaddingTop() {
        return paddingTop;
    }

    /**
     * @param paddingTop The padding for the top of the glyph area in pixels
     * @see UnicodeFont#setPaddingTop(int)
     */
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * @return The padding for the left of the glyph area in pixels
     * @see UnicodeFont#getPaddingLeft()
     */
    public int getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * @param paddingLeft The padding for the left of the glyph area in pixels
     * @see UnicodeFont#setPaddingLeft(int)
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * @return The padding for the bottom of the glyph area in pixels
     * @see UnicodeFont#getPaddingBottom()
     */
    public int getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * @param paddingBottom The padding for the bottom of the glyph area in pixels
     * @see UnicodeFont#setPaddingBottom(int)
     */
    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * @return The padding for the right of the glyph area in pixels
     * @see UnicodeFont#getPaddingRight()
     */
    public int getPaddingRight() {
        return paddingRight;
    }

    /**
     * @param paddingRight The padding for the right of the glyph area in pixels
     * @see UnicodeFont#setPaddingRight(int)
     */
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * @return The padding for the horizontal advance of each glyph
     * @see UnicodeFont#getPaddingAdvanceX()
     */
    public int getPaddingAdvanceX() {
        return paddingAdvanceX;
    }

    /**
     * @param paddingAdvanceX The padding for the horizontal advance of each glyph
     * @see UnicodeFont#setPaddingAdvanceX(int)
     */
    public void setPaddingAdvanceX(int paddingAdvanceX) {
        this.paddingAdvanceX = paddingAdvanceX;
    }

    /**
     * @return The padding for the vertical advance of each glyph
     * @see UnicodeFont#getPaddingAdvanceY()
     */
    public int getPaddingAdvanceY() {
        return paddingAdvanceY;
    }

    /**
     * @param paddingAdvanceY The padding for the vertical advance of each glyph
     * @see UnicodeFont#setPaddingAdvanceY(int)
     */
    public void setPaddingAdvanceY(int paddingAdvanceY) {
        this.paddingAdvanceY = paddingAdvanceY;
    }

    /**
     * @return The width of the generate glyph pages
     * @see UnicodeFont#getGlyphPageWidth()
     */
    public int getGlyphPageWidth() {
        return glyphPageWidth;
    }

    /**
     * @param glyphPageWidth The width of the generate glyph pages
     * @see UnicodeFont#setGlyphPageWidth(int)
     */
    public void setGlyphPageWidth(int glyphPageWidth) {
        this.glyphPageWidth = glyphPageWidth;
    }

    /**
     * @return The height of the generate glyph pages
     * @see UnicodeFont#getGlyphPageHeight()
     */
    public int getGlyphPageHeight() {
        return glyphPageHeight;
    }

    /**
     * @param glyphPageHeight The height of the generate glyph pages
     * @see UnicodeFont#setGlyphPageHeight(int)
     */
    public void setGlyphPageHeight(int glyphPageHeight) {
        this.glyphPageHeight = glyphPageHeight;
    }

    /**
     * @return The point size of the font generated
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize The point size of the font generated
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return True if the font was generated in bold typeface
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * @param bold True if the font was generated in bold typeface
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     */
    public void setBold(boolean bold) {
        this.bold = bold;
    }

    /**
     * @return True if the font was generated in italic typeface
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * @param italic True if the font was generated in italic typeface
     * @see UnicodeFont#UnicodeFont(String, int, boolean, boolean)
     * @see UnicodeFont#UnicodeFont(java.awt.Font, int, boolean, boolean)
     */
    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    /**
     * @return The list of effects applied to the text
     * @see UnicodeFont#getEffects()
     */
    public List getEffects() {
        return effects;
    }

    /**
     * Saves the settings to a file.
     *
     * @param file The file we're saving to
     * @throws IOException if the file could not be saved.
     */
    public void save(File file) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(file));
        out.println("font.size=" + fontSize);
        out.println("font.bold=" + bold);
        out.println("font.italic=" + italic);
        out.println();
        out.println("pad.top=" + paddingTop);
        out.println("pad.right=" + paddingRight);
        out.println("pad.bottom=" + paddingBottom);
        out.println("pad.left=" + paddingLeft);
        out.println("pad.advance.x=" + paddingAdvanceX);
        out.println("pad.advance.y=" + paddingAdvanceY);
        out.println();
        out.println("glyph.page.width=" + glyphPageWidth);
        out.println("glyph.page.height=" + glyphPageHeight);
        out.println();
        for (Iterator iter = effects.iterator(); iter.hasNext(); ) {
            ConfigurableEffect effect = (ConfigurableEffect) iter.next();
            out.println("effect.class=" + effect.getClass().getName());
            for (Iterator iter2 = effect.getValues().iterator(); iter2.hasNext(); ) {
                Value value = (Value) iter2.next();
                out.println("effect." + value.getName() + "=" + value.getString());
            }
            out.println();
        }
        out.close();
    }
}
