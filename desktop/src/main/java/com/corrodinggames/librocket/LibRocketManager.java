package com.corrodinggames.librocket;

import android.graphics.Rect;
import android.graphics.RectF;
import com.Element;
import com.ElementDocument;
import com.LibRocket;
import com.corrodinggames.librocket.scripts.ScriptEngine;
import com.corrodinggames.rts.game.units.UnitType;
import com.corrodinggames.rts.game.units.UnitTypeEnum;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.FileChangeEngine;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.ProfilerTimer;
import com.corrodinggames.rts.gameFramework.Utility;
import com.corrodinggames.rts.gameFramework.file.FileHelper;
import com.corrodinggames.rts.gameFramework.local.Locale;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: renamed from: com.corrodinggames.librocket.b */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/librocket/b.class */
public abstract class LibRocketManager extends LibRocket {

    /* JADX INFO: renamed from: a */
    public static ProfilerTimer loadResourcesTimer = new ProfilerTimer("LoadResources");

    /* JADX INFO: renamed from: b */
    public static String basePath = VariableScope.nullOrMissingString;

    /* JADX INFO: renamed from: e */
    public boolean isHandlingEvent;

    /* JADX INFO: renamed from: j */
    private DialogData currentAlert;

    /* JADX INFO: renamed from: k */
    private DialogData currentPopup;

    /* JADX INFO: renamed from: d */
    protected int renderCount = 0;

    /* JADX INFO: renamed from: f */
    protected Rect scissorRect = new Rect();

    /* JADX INFO: renamed from: g */
    protected RectF scissorRectF = new RectF();
    protected boolean h = false;

    /* JADX INFO: renamed from: i */
    Pattern variablePattern = Pattern.compile("\\$\\{([^\\}]*?)\\}");

    /* JADX INFO: renamed from: c */
    public ScriptEngine scriptEngine = ScriptEngine.createScriptEngine(this);

    @Override // com.LibRocket
    public abstract void EnableScissorRegion(boolean z);

    /* JADX INFO: renamed from: a */
    public void resetRenderCount() {
        this.renderCount = 0;
    }

    /* JADX INFO: renamed from: a */
    public static String convertTexturePath(String str) {
        GameEngine.log("convertTexturePath for: " + str);
        String strUrlDecode = Utility.unescapeHtml(str);
        if (strUrlDecode.startsWith("base:")) {
            return basePath + strUrlDecode.substring("base:".length());
        }
        if (strUrlDecode.startsWith("drawable:")) {
            return basePath + "res/drawable/" + strUrlDecode.substring("drawable:".length());
        }
        if (strUrlDecode.startsWith("assets:")) {
            String strSubstring = strUrlDecode.substring("assets:".length());
            String strConvertAbstractPath = FileHelper.convertAbstractPath(strSubstring);
            boolean z = true;
            if (GameEngine.isIOSVersion && strConvertAbstractPath != null && strConvertAbstractPath.startsWith(basePath)) {
                z = false;
            }
            if (GameEngine.isIOSVersion && strConvertAbstractPath != null && strConvertAbstractPath.startsWith("/private")) {
                z = false;
            }
            GameEngine.log("convertTexturePath  (basePath:" + z + "):" + strSubstring + " > " + strConvertAbstractPath);
            if (z) {
                return basePath + strConvertAbstractPath;
            }
            return strConvertAbstractPath;
        }
        if (strUrlDecode.startsWith(basePath + "assets/gui/")) {
            GameEngine.log("convertTexturePath already had path:" + strUrlDecode);
            return strUrlDecode;
        }
        return basePath + "assets/gui/" + strUrlDecode;
    }

    /* JADX INFO: renamed from: a */
    public Matcher match(String str, String str2) {
        Matcher matcher = Pattern.compile(str).matcher(str2);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }

    @Override // com.LibRocket
    public void ReleaseTexture(int i) {
        removeTextureHolder(i);
    }

    @Override // com.LibRocket
    public boolean LoadTexture(int i, String str) throws IOException {
        loadResourcesTimer.a();
        UITextureHolder uITextureHolder = (UITextureHolder) findTextureHolder(i);
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        UnitType unitTypeByName = null;
        if (str.startsWith("lazy:")) {
            str = str.substring("lazy:".length());
            z = true;
        }
        if (str.startsWith("nocolor:")) {
            str = str.substring("nocolor:".length());
            z2 = true;
        }
        if (str.startsWith("unit:")) {
            str = str.substring("unit:".length());
            unitTypeByName = UnitTypeEnum.getUnitTypeByName(str);
            z = true;
        }
        if (str.startsWith("thumbnail:")) {
            str = str.substring("thumbnail:".length());
            z3 = true;
        }
        Matcher matcherMatch = match("^(alpha\\((.*)\\):).*", str);
        if (matcherMatch != null) {
            String strGroup = matcherMatch.group(1);
            String strGroup2 = matcherMatch.group(2);
            GameEngine.log("alpha=" + strGroup2);
            uITextureHolder.alpha = Float.parseFloat(strGroup2);
            str = str.substring(strGroup.length());
        }
        String strConvertTexturePath = convertTexturePath(str);
        uITextureHolder.lazyLoad = z;
        uITextureHolder.isThumbnail = z3;
        uITextureHolder.noColor = z2;
        uITextureHolder.unitType = unitTypeByName;
        uITextureHolder.texturePath = strConvertTexturePath;
        if (!z && !uITextureHolder.loadTexture()) {
            loadResourcesTimer.b();
            return false;
        }
        loadResourcesTimer.b();
        return true;
    }

    @Override // com.LibRocket
    public void SetScissorRegion(int i, int i2, int i3, int i4) {
        this.scissorRect.a(i, i2, i + i3, i2 + i4);
        this.scissorRectF.a(i, i2, i + i3, i2 + i4);
        EnableScissorRegion(true);
    }

    /* JADX INFO: renamed from: b */
    public boolean isGuiVisible() {
        if (getActiveDocument() != null || this.currentPopup != null || this.currentAlert != null) {
            return false;
        }
        return true;
    }

    @Override // com.LibRocket
    public void HandleEvent(String str) {
        this.isHandlingEvent = true;
        try {
            this.scriptEngine.processScript(str);
        } finally {
            this.isHandlingEvent = false;
        }
    }

    /* JADX INFO: renamed from: b */
    public Object getActiveDocumentMetadata(String str) {
        HashMap activeDocumentMetadata = getActiveDocumentMetadata();
        if (activeDocumentMetadata == null) {
            return null;
        }
        return activeDocumentMetadata.get(str);
    }

    @Override // com.LibRocket
    public void newDocumentLoaded(ElementDocument elementDocument) {
        this.scriptEngine.getRootNoCheck().convertTextOnPage();
    }

    @Override // com.LibRocket
    public void newDocumentShown(ElementDocument elementDocument) {
        if (this.currentPopup != null) {
            this.currentPopup.document.pullToFront();
        }
        if (this.currentAlert != null) {
            this.currentAlert.document.pullToFront();
        }
    }

    /* JADX INFO: renamed from: c */
    public ElementDocument getCurrentPopup() {
        if (this.currentPopup != null) {
            return this.currentPopup.document;
        }
        return null;
    }

    /* JADX INFO: renamed from: d */
    public ElementDocument getCurrentAlert() {
        if (this.currentAlert != null) {
            return this.currentAlert.document;
        }
        return null;
    }

    /* JADX INFO: renamed from: e */
    public ElementDocument getTopmostPopup() {
        ElementDocument currentAlert = getCurrentAlert();
        if (currentAlert != null) {
            return currentAlert;
        }
        return getCurrentPopup();
    }

    /* JADX INFO: renamed from: f */
    public ElementDocument getActiveOrPopup() {
        ElementDocument currentPopup = getCurrentPopup();
        if (currentPopup != null) {
            return currentPopup;
        }
        return getActiveDocument();
    }

    /* JADX INFO: renamed from: g */
    public ElementDocument getTopmostDocument() {
        ElementDocument currentAlert = getCurrentAlert();
        if (currentAlert != null) {
            return currentAlert;
        }
        ElementDocument currentPopup = getCurrentPopup();
        if (currentPopup != null) {
            return currentPopup;
        }
        return getActiveDocument();
    }

    /* JADX INFO: renamed from: c */
    public void showMessageBox2(String str) {
        DialogData dialogData = new DialogData();
        dialogData.title = null;
        dialogData.message = str;
        createAndShowDialog(dialogData);
    }

    /* JADX INFO: renamed from: b */
    public void showMessageBox(String str, String str2) {
        DialogData dialogData = new DialogData();
        dialogData.title = str;
        dialogData.message = str2;
        createAndShowDialog(dialogData);
    }

    /* JADX INFO: renamed from: a */
    public void showDialog(String str, String str2, String str3, String str4, String str5, boolean z) {
        DialogData dialogData = new DialogData();
        dialogData.title = str;
        dialogData.message = str2;
        dialogData.textInputValue = str3;
        dialogData.button1 = str4;
        dialogData.button2 = str5;
        dialogData.showBackButton = z;
        createAndShowDialog(dialogData);
    }

    /* JADX INFO: renamed from: a */
    public void showDialogWithActions(String str, String str2, String str3, ButtonAction buttonAction, ButtonAction buttonAction2, boolean z) {
        DialogData dialogData = new DialogData();
        dialogData.title = str;
        dialogData.message = str2;
        dialogData.textInputValue = str3;
        dialogData.button1 = buttonAction;
        dialogData.button2 = buttonAction2;
        dialogData.showBackButton = z;
        createAndShowDialog(dialogData);
    }

    /* JADX INFO: renamed from: a */
    public ElementDocument showDialogInternal(String str, String str2, String str3, Object obj, Object obj2, boolean z, boolean z2) {
        DialogData dialogData = new DialogData();
        dialogData.title = str;
        dialogData.message = str2;
        dialogData.textInputValue = str3;
        dialogData.button1 = obj;
        dialogData.button2 = obj2;
        dialogData.isAlert = z;
        dialogData.showBackButton = z2;
        return createAndShowDialog(dialogData);
    }

    /* JADX INFO: renamed from: a */
    public ElementDocument createAndShowDialog(DialogData dialogData) {
        ScriptEngine.checkThreadAccess();
        ElementDocument elementDocumentCreatePopup = createPopup("messagebox.rml", null);
        HashMap metadataMap = elementDocumentCreatePopup.getMetadataMap();
        elementDocumentCreatePopup.getElementById("message").setTextWithNewlines(dialogData.message);
        if (dialogData.title == null) {
            dialogData.title = VariableScope.nullOrMissingString;
        }
        elementDocumentCreatePopup.getElementById("title").setText(dialogData.title);
        setupDialogButton(elementDocumentCreatePopup, "button_1", dialogData.button1, metadataMap);
        setupDialogButton(elementDocumentCreatePopup, "button_2", dialogData.button2, metadataMap);
        Element elementById = elementDocumentCreatePopup.getElementById("button_back");
        elementById.loadCharsetIfNeededWithCurrentText();
        String str = "closePopup();";
        if (dialogData.textInputValue != null) {
            str = str + "hideKeyboard();";
        }
        elementById.setAttribute("onclick", str);
        if (!dialogData.showBackButton) {
            elementById.hide();
        }
        if (dialogData.button1 == null && dialogData.button2 == null) {
            elementById.setText(Locale.get("menus.common.ok", new Object[0]));
            elementById.focus();
        }
        if (dialogData.textInputValue != null) {
            elementDocumentCreatePopup.getElementById("textInputWrapper").show();
            Element elementById2 = elementDocumentCreatePopup.getElementById("textInput");
            elementById2.setAttribute("value", dialogData.textInputValue);
            elementById2.focus();
        }
        dialogData.document = elementDocumentCreatePopup;
        if (dialogData.isAlert) {
            if (showAlert(dialogData)) {
                return elementDocumentCreatePopup;
            }
            closeDocument(elementDocumentCreatePopup);
            return null;
        }
        return elementDocumentCreatePopup;
    }

    /* JADX INFO: renamed from: b */
    public boolean showAlert(DialogData dialogData) {
        if (this.currentAlert != null) {
            GameEngine.log("AlertPopup already visible closing");
            closeDocument(this.currentAlert.document);
            this.currentAlert = null;
        }
        this.currentAlert = dialogData;
        GameEngine.log("Showing popup: " + dialogData.title);
        if (!this.isHandlingEvent) {
            update();
        } else {
            GameEngine.log("insideEvent");
        }
        GameEngine.log("popup ready..");
        dialogData.document.show(4);
        GameEngine.log("Popup shown..");
        return true;
    }

    /* JADX INFO: renamed from: a */
    public boolean showPopupFromDocument(ElementDocument elementDocument) {
        return showPopup(new DialogData(elementDocument));
    }

    /* JADX INFO: renamed from: c */
    public boolean showPopup(DialogData dialogData) {
        if (this.currentPopup != null) {
            GameEngine.log("Popup already visible, cannot show: " + dialogData.document.getMetadata("sourceDocumentId"));
            if (this.currentAlert != null) {
                this.currentAlert.document.pullToFront();
                return false;
            }
            this.currentPopup.document.pullToFront();
            return false;
        }
        this.currentPopup = dialogData;
        GameEngine.log("Showing popup: " + dialogData.title);
        if (!this.isHandlingEvent) {
            update();
        } else {
            GameEngine.log("insideEvent");
        }
        GameEngine.log("popup ready..");
        dialogData.document.show(4);
        GameEngine.log("Popup shown..");
        return true;
    }

    /* JADX INFO: renamed from: a */
    public ElementDocument createPopupWithRML(String str, Object obj, String str2, boolean z) {
        ScriptEngine.checkThreadAccess();
        ElementDocument elementDocumentCreatePopup = createPopup("messagebox.rml", obj);
        elementDocumentCreatePopup.setMetadata("sourceDocumentId", str);
        String strJoin = Utility.readFileToString(new File(convertTexturePath(str)));
        elementDocumentCreatePopup.getElementById("mainButtons").hide();
        Element elementById = elementDocumentCreatePopup.getElementById("message");
        elementById.setInnerRML(strJoin);
        loadCharsetOnChildren(elementById, false);
        if (str2 == null) {
            str2 = VariableScope.nullOrMissingString;
        }
        elementDocumentCreatePopup.getElementById("title").setText(str2);
        if (z) {
            if (showPopupFromDocument2(elementDocumentCreatePopup)) {
                return elementDocumentCreatePopup;
            }
            return null;
        }
        return elementDocumentCreatePopup;
    }

    /* JADX INFO: renamed from: b */
    public boolean showPopupFromDocument2(ElementDocument elementDocument) {
        if (showPopup(new DialogData(elementDocument))) {
            return true;
        }
        closeDocument(elementDocument);
        return false;
    }

    /* JADX INFO: renamed from: a */
    public void loadCharsetOnChildren(Element element, boolean z) {
        if (element == null) {
            GameEngine.log("loadCharsetIfNeededOnChildren: root is null");
            return;
        }
        for (Element element2 : element.getAllNestedChildren()) {
            boolean z2 = false;
            String tagName = element2.getTagName();
            if (tagName.equals("p") || tagName.startsWith("h") || tagName.startsWith("label") || tagName.startsWith("button") || tagName.startsWith("select")) {
                z2 = true;
            }
            if (z && tagName.equals("option")) {
                z2 = true;
            }
            if (z2) {
                element2.loadCharsetIfNeededWithCurrentText();
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void setupDialogButton(ElementDocument elementDocument, String str, Object obj, HashMap map) {
        Element elementById = elementDocument.getElementById(str);
        if (obj == null) {
            elementById.hide();
            return;
        }
        if (obj instanceof String) {
            String str2 = (String) obj;
            int iIndexOf = str2.indexOf(":");
            String strSubstring = str2.substring(0, iIndexOf);
            String strSubstring2 = VariableScope.nullOrMissingString;
            if (iIndexOf + 1 < str2.length()) {
                strSubstring2 = str2.substring(iIndexOf + 1);
            }
            if (strSubstring.startsWith("[onenter]")) {
                strSubstring = strSubstring.substring("[onenter]".length());
                elementDocument.getElementById("textInput").setAttribute("onenter", strSubstring2);
            }
            elementById.setText(strSubstring);
            elementById.setAttribute("onclick", strSubstring2);
            return;
        }
        if (obj instanceof ButtonAction) {
            ButtonAction buttonAction = (ButtonAction) obj;
            String str3 = "action_" + str;
            map.put(str3, buttonAction.runnable);
            elementById.setText(buttonAction.text);
            elementById.setAttribute("onclick", "runRunnable(" + str3 + ");");
            if (buttonAction.closesDialog) {
                elementDocument.getElementById("textInput").setAttribute("onenter", "runRunnable(" + str3 + ");");
                return;
            }
            return;
        }
        GameEngine.logWarningAndStack("Unhandled type:" + obj);
    }

    /* JADX INFO: renamed from: h */
    public boolean canClosePopupOrAlert() {
        if (closeAlert() || closePopup()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: i */
    public boolean closeAlert() {
        DialogData dialogData = this.currentAlert;
        if (dialogData != null) {
            GameEngine.log("Closing alert");
            closeDocument(dialogData.document);
            this.currentAlert = null;
            if (dialogData.onClose != null) {
                dialogData.onClose.run();
                return true;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: j */
    public boolean closePopup() {
        DialogData dialogData = this.currentPopup;
        if (dialogData != null) {
            GameEngine.log("Closing popup");
            closeDocument(dialogData.document);
            this.currentPopup = null;
            if (dialogData.onClose != null) {
                dialogData.onClose.run();
                return true;
            }
            return true;
        }
        return false;
    }

    /* JADX INFO: renamed from: k */
    public String getTextInputValue() {
        return getTopmostPopup().getElementById("textInput").getAttribute("value");
    }

    /* JADX INFO: renamed from: d */
    public String parseTextVariables(String str) {
        String strReplaceFirst;
        Object objProcessArg;
        String str2 = null;
        if (str != null && str.contains("class=\"log-entry\"")) {
            System.out.println("parseText: skipping log line:" + str);
            return null;
        }
        int i = 0;
        Matcher matcher = this.variablePattern.matcher(str);
        while (matcher.find()) {
            i++;
            if (i > 100) {
                System.out.println("parseText too many loops!!");
                return null;
            }
            String strGroup = matcher.group(1);
            String string = null;
            if (this.debug) {
                System.out.println("parseText:" + strGroup);
            }
            if (strGroup.startsWith("i:")) {
                string = Locale.get(strGroup.substring(2), new Object[0]);
            }
            if (string == null && (objProcessArg = this.scriptEngine.processArg(strGroup)) != null) {
                string = objProcessArg.toString();
            }
            if (string == null) {
                strReplaceFirst = matcher.replaceFirst("(unhandled:" + strGroup + ")");
            } else {
                strReplaceFirst = matcher.replaceFirst(string);
            }
            if (string != null) {
                ElementDocument activeDocument = getActiveDocument();
                if (activeDocument != null && !activeDocument.translatedToUnicode && Utility.containsNonAscii(string)) {
                    activeDocument.translatedToUnicode = true;
                }
                ElementDocument topmostDocument = getTopmostDocument();
                if (topmostDocument != null && !topmostDocument.translatedToUnicode && Utility.containsNonAscii(string)) {
                    topmostDocument.translatedToUnicode = true;
                }
            }
            matcher = this.variablePattern.matcher(strReplaceFirst);
            str2 = strReplaceFirst;
        }
        return str2;
    }

    @Override // com.LibRocket
    public String TranslateString(String str) {
        try {
            String textVariables = parseTextVariables(str);
            if (textVariables != null) {
                return textVariables;
            }
            return null;
        } catch (Exception e) {
            ScriptEngine.throwDelayedException("TranslateString exception on: " + str, e);
            GameEngine.log("Exception in TranslateString", (Throwable) e);
            GameEngine.logColored("start");
            e.printStackTrace();
            GameEngine.logColored("end");
            GameEngine.logColored("start logStack");
            GameEngine.printStackTrace();
            GameEngine.logColored("end logStack");
            System.err.flush();
            System.out.flush();
            return null;
        }
    }

    @Override // com.LibRocket
    public long getFileLastModified(String str) {
        return FileChangeEngine.a(str, false);
    }

    @Override // com.LibRocket
    public void postUpdate() {
        boolean z = this.queueExtraUpdate;
        super.postUpdate();
        if (z) {
            this.scriptEngine.checkForErrors();
        }
    }
}
