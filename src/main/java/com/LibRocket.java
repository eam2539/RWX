package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: game-lib.jar:com/LibRocket.class */
public class LibRocket {
    String currentDocumentPath;
    ElementDocument currentDocument;
    String[] activeDocumentCss;
    int reloadSkip;
    public boolean debug = false;
    ArrayList lastDocuments = new ArrayList();
    long longLastModified = -1;
    long longLastModifiedNewestCss = -1;
    public String documentPrefix = "assets/gui/";
    public boolean queueExtraUpdate = false;
    public int width = 1000;
    public int height = 1000;
    public int lastMouseX = 0;
    public int lastMouseY = 0;
    ArrayList compiledGeometryList = new ArrayList();
    ArrayList textureHolderList = new ArrayList();

    /* JADX INFO: loaded from: game-lib.jar:com/LibRocket$CompiledGeometry.class */
    public class CompiledGeometry {
        public int id;
        public float[] verticesXY;
        public float[] verticesUV;
        public int[] verticesColors;
        public int[] indices;
        public int textureId;
        public Object bbox;
    }

    public native void setup();

    private native ElementDocument loadDocument(String str);

    private native void loadDocumentWithContainer(String str, ElementDocument elementDocument);

    public native void loadFont(String str, String str2);

    public native void update();

    public native void render();

    private native void setDimensions(int i, int i2);

    public native void processMouseMove(int i, int i2, int i3);

    public native void processMouseButtonDown(int i, int i2);

    public native void processMouseButtonUp(int i, int i2);

    public native void processMouseWheel(int i, int i2);

    public native void processTextInput(String str);

    public native void processTextInputChar(int i);

    public native void processKeyDown(int i, int i2);

    public native void processKeyUp(int i, int i2);

    static {
        boolean z = false;
        if (System.getProperty("os.name").startsWith("Windows")) {
            z = true;
        }
        String property = System.getProperty("os.arch");
        boolean z2 = property.contains("64") || property.startsWith("armv8");
        String str = "rocketConnector";
        if (z && z2) {
            str = "rocketConnector64";
        }
        try {
            System.loadLibrary(str);
        } catch (UnsatisfiedLinkError e) {
            loadLibraryFromJar(str, e);
        }
    }

    private static void loadLibraryFromJar(String str, UnsatisfiedLinkError unsatisfiedLinkError) {
        String strMapLibraryName = System.mapLibraryName(str);
        String str2 = "/natives/" + strMapLibraryName;
        InputStream resourceAsStream = LibRocket.class.getResourceAsStream(str2);
        if (resourceAsStream == null) {
            throw unsatisfiedLinkError;
        }
        File file = null;
        try {
            file = File.createTempFile(str + "-", "-" + strMapLibraryName);
            file.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[16384];
            while (true) {
                int i = resourceAsStream.read(bArr);
                if (i == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, i);
            }
            fileOutputStream.close();
            resourceAsStream.close();
            System.load(file.getAbsolutePath());
        } catch (IOException e) {
            UnsatisfiedLinkError unsatisfiedLinkError2 = new UnsatisfiedLinkError("Failed to extract " + str2);
            unsatisfiedLinkError2.initCause(e);
            throw unsatisfiedLinkError2;
        } catch (UnsatisfiedLinkError e2) {
            if (file != null) {
                file.delete();
            }
            throw e2;
        }
    }

    public void backToLastDocument() {
        if (this.lastDocuments.size() == 0) {
            closeActiveDocument();
        } else {
            ElementDocument elementDocument = (ElementDocument) this.lastDocuments.remove(this.lastDocuments.size() - 1);
            setDocument(elementDocument.documentPath, elementDocument.metadata, false);
        }
    }

    public void clearHistory() {
        this.lastDocuments.clear();
    }

    public ElementDocument setDocument(String str) {
        return setDocument(str, null);
    }

    public ElementDocument setDocument(String str, HashMap map) {
        return setDocument(str, map, true);
    }

    public void reloadDocument() {
        if (this.currentDocument != null && this.currentDocumentPath != null) {
            setDocument(this.currentDocumentPath, getActiveDocumentMetadata(), false);
        }
    }

    public ElementDocument setDocument(String str, HashMap map, boolean z) {
        if (this.currentDocument != null) {
            if (z) {
                this.lastDocuments.add(this.currentDocument);
            }
            closeDocument(this.currentDocument);
            this.currentDocument = null;
        }
        this.currentDocumentPath = str;
        ElementDocument elementDocument = new ElementDocument();
        if (map == null) {
            map = new HashMap();
        }
        elementDocument.metadata = map;
        elementDocument.documentPath = str;
        this.currentDocument = elementDocument;
        this.longLastModified = getLastModified();
        loadDocumentWithContainer(getDocumentPath(this.currentDocumentPath), elementDocument);
        newDocumentLoaded(elementDocument);
        elementDocument.show();
        newDocumentShown(elementDocument);
        return elementDocument;
    }

    public void newDocumentLoaded(ElementDocument elementDocument) {
    }

    public void newDocumentShown(ElementDocument elementDocument) {
    }

    public ElementDocument createPopup(String str, Object obj) {
        ElementDocument elementDocumentLoadDocument = loadDocument(getDocumentPath(str));
        if (elementDocumentLoadDocument != null) {
            elementDocumentLoadDocument.setMetadata("mode", obj);
        }
        return elementDocumentLoadDocument;
    }

    public void closeDocument(ElementDocument elementDocument) {
        elementDocument.closeDocument();
        this.queueExtraUpdate = true;
    }

    public void closeActiveDocument() {
        if (this.currentDocument != null) {
            closeDocument(this.currentDocument);
            this.currentDocument = null;
        }
    }

    public ElementDocument getActiveDocument() {
        return this.currentDocument;
    }

    public HashMap getActiveDocumentMetadata() {
        if (this.currentDocument != null) {
            return this.currentDocument.metadata;
        }
        return null;
    }

    public String getActiveDocumentPath() {
        if (this.currentDocument == null) {
            return "<No Current Document>";
        }
        return this.currentDocument.documentPath;
    }

    public Element getActiveElementById(String str) {
        if (this.currentDocument == null) {
            System.out.println("currentDocument==null on getActiveElementById:" + str);
            return null;
        }
        if (str == null) {
            throw new RuntimeException("id==null");
        }
        return this.currentDocument.getElementById(str);
    }

    public String[] getActiveDocumentCss() {
        if (this.activeDocumentCss == null) {
            ArrayList arrayList = new ArrayList();
            for (File file : new File("assets/gui").listFiles()) {
                if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".rcss")) {
                    String str = "assets/gui/" + file.getName();
                    arrayList.add(str);
                    System.out.println("getActiveDocumentCss: " + str);
                }
            }
            this.activeDocumentCss = (String[]) arrayList.toArray(new String[0]);
        }
        return this.activeDocumentCss;
    }

    public long getFileLastModified(String str) {
        return new File(str).lastModified();
    }

    public long getLastModifiedNewestCss() {
        if (this.currentDocumentPath == null) {
            return -2L;
        }
        long j = -1;
        for (String str : getActiveDocumentCss()) {
            long fileLastModified = getFileLastModified(str);
            if (fileLastModified > j) {
                j = fileLastModified;
            }
        }
        return j;
    }

    public long getLastModified() {
        if (this.currentDocumentPath == null) {
            return -2L;
        }
        return getFileLastModified("assets/gui/" + this.currentDocumentPath);
    }

    public void detectChangesAndReload() {
        this.reloadSkip++;
        if (this.reloadSkip < 20) {
            return;
        }
        this.reloadSkip = 0;
        if (getActiveDocument() != null) {
            boolean z = false;
            long lastModified = getLastModified();
            if (this.longLastModified == -1) {
                this.longLastModified = lastModified;
            } else if (this.longLastModified != lastModified) {
                System.out.println("reloadDocument: '" + this.currentDocumentPath + "' current now:" + lastModified);
                z = true;
                this.longLastModified = lastModified;
            }
            long lastModifiedNewestCss = getLastModifiedNewestCss();
            if (this.longLastModifiedNewestCss == -1) {
                this.longLastModifiedNewestCss = lastModifiedNewestCss;
            } else if (this.longLastModifiedNewestCss != lastModifiedNewestCss) {
                System.out.println("reloadDocument from css: '" + this.currentDocumentPath + "' current now:" + lastModified);
                z = true;
                this.longLastModifiedNewestCss = lastModifiedNewestCss;
            }
            if (z) {
                reloadDocument();
            }
        }
    }

    public String getDocumentPath(String str) {
        return this.documentPrefix + str;
    }

    public void loadFont(String str) {
        loadFont(str, null);
    }

    public void postUpdate() {
        if (this.queueExtraUpdate) {
            this.queueExtraUpdate = false;
            update();
            render();
            processMouseMove(-1, -1, 0);
            processMouseMove(this.lastMouseX, this.lastMouseY, 0);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setDimensionsWrap(int i, int i2) {
        this.width = i;
        this.height = i2;
        setDimensions(i, i2);
    }

    public void mouseMove(int i, int i2, int i3) {
        this.lastMouseX = i;
        this.lastMouseY = i2;
        processMouseMove(i, i2, i3);
    }

    public void RenderGeometry(float[] fArr, float[] fArr2, int[] iArr, int[] iArr2, int i, float f, float f2) {
        RenderGeometryPossiblyCompiled(fArr, fArr2, iArr, iArr2, i, f, f2, null);
    }

    public void RenderGeometryPossiblyCompiled(float[] fArr, float[] fArr2, int[] iArr, int[] iArr2, int i, float f, float f2, CompiledGeometry compiledGeometry) {
        System.out.println("RenderGeometryPossiblyCompiled(" + iArr2.length + ")");
    }

    public int CompileGeometry(float[] fArr, float[] fArr2, int[] iArr, int[] iArr2, int i) {
        CompiledGeometry compiledGeometry = null;
        if (1 != 0) {
            for (int i2 = 1; i2 < this.compiledGeometryList.size(); i2++) {
                CompiledGeometry compiledGeometry2 = (CompiledGeometry) this.compiledGeometryList.get(i2);
                if (compiledGeometry2 != null) {
                    boolean z = true;
                    if (compiledGeometry2.verticesXY != fArr) {
                        if (!Arrays.equals(compiledGeometry2.verticesXY, fArr)) {
                            z = false;
                        } else {
                            fArr = compiledGeometry2.verticesXY;
                        }
                    }
                    if (compiledGeometry2.verticesUV != fArr2) {
                        if (!Arrays.equals(compiledGeometry2.verticesUV, fArr2)) {
                            z = false;
                        } else {
                            fArr2 = compiledGeometry2.verticesUV;
                        }
                    }
                    if (compiledGeometry2.verticesColors != iArr) {
                        if (!Arrays.equals(compiledGeometry2.verticesColors, iArr)) {
                            z = false;
                        } else {
                            iArr = compiledGeometry2.verticesColors;
                        }
                    }
                    if (compiledGeometry2.indices != iArr2) {
                        if (!Arrays.equals(compiledGeometry2.indices, iArr2)) {
                            z = false;
                        } else {
                            iArr2 = compiledGeometry2.indices;
                        }
                    }
                    if (z) {
                    }
                }
            }
        }
        if (0 == 0) {
            compiledGeometry = new CompiledGeometry();
            compiledGeometry.verticesXY = fArr;
            compiledGeometry.verticesUV = fArr2;
            compiledGeometry.verticesColors = iArr;
            compiledGeometry.indices = iArr2;
            compiledGeometry.textureId = i;
        }
        if (this.compiledGeometryList.size() == 0) {
            this.compiledGeometryList.add(null);
        }
        boolean z2 = false;
        int i3 = 1;
        while (true) {
            if (i3 >= this.compiledGeometryList.size()) {
                break;
            }
            if (this.compiledGeometryList.get(i3) != null) {
                i3++;
            } else {
                compiledGeometry.id = i3;
                this.compiledGeometryList.set(i3, compiledGeometry);
                z2 = true;
                break;
            }
        }
        if (!z2) {
            compiledGeometry.id = this.compiledGeometryList.size();
            this.compiledGeometryList.add(compiledGeometry);
        }
        return compiledGeometry.id;
    }

    public void RenderCompiledGeometry(int i, float f, float f2) {
        CompiledGeometry compiledGeometry = (CompiledGeometry) this.compiledGeometryList.get(i);
        if (compiledGeometry == null) {
            warn("CompileGeometry null for " + i);
        } else {
            RenderGeometryPossiblyCompiled(compiledGeometry.verticesXY, compiledGeometry.verticesUV, compiledGeometry.verticesColors, compiledGeometry.indices, compiledGeometry.textureId, f, f2, compiledGeometry);
        }
    }

    public void ReleaseCompiledGeometry(int i) {
        this.compiledGeometryList.set(i, null);
    }

    public void ReleaseCompiledGeometryForTexture(int i) {
        for (int i2 = 1; i2 < this.compiledGeometryList.size(); i2++) {
            CompiledGeometry compiledGeometry = (CompiledGeometry) this.compiledGeometryList.get(i2);
            if (compiledGeometry != null && compiledGeometry.textureId == i) {
                log("ReleaseCompiledGeometryForTexture:" + compiledGeometry.textureId);
                this.compiledGeometryList.set(i2, null);
            }
        }
    }

    public void EnableScissorRegion(boolean z) {
    }

    public void SetScissorRegion(int i, int i2, int i3, int i4) {
    }

    public boolean LoadTexture(int i, String str) throws IOException {
        log("JavaMethod:LoadTexture()");
        return true;
    }

    public boolean GenerateTexture(int i, byte[] bArr) {
        log("JavaMethod:GenerateTexture()");
        return true;
    }

    public void ReleaseTexture(int i) {
        log("JavaMethod:ReleaseTexture");
        ReleaseCompiledGeometryForTexture(i);
    }

    public void HandleEvent(String str) {
        log("JavaMethod:HandleEvent:" + str);
    }

    public String TranslateString(String str) {
        return str;
    }

    private void callback(String str) {
        System.out.println("In Java with: " + str);
    }

    public static void test() {
        LibRocket libRocket = new LibRocket();
        libRocket.setup();
        libRocket.loadDocument(libRocket.getDocumentPath("test.rml"));
        libRocket.update();
        libRocket.render();
    }

    public static void main(String[] strArr) {
        test();
    }

    public TextureHolder getNewTextureHolder() {
        if (this.textureHolderList.size() == 0) {
            this.textureHolderList.add(null);
        }
        TextureHolder fromTextureHolderFactory = getFromTextureHolderFactory();
        boolean z = false;
        int i = 1;
        while (true) {
            if (i >= this.textureHolderList.size()) {
                break;
            }
            if (this.textureHolderList.get(i) != null) {
                i++;
            } else {
                fromTextureHolderFactory.index = i;
                this.textureHolderList.set(i, fromTextureHolderFactory);
                System.out.println("getNewTextureHolder: set:" + fromTextureHolderFactory.index);
                z = true;
                break;
            }
        }
        if (!z) {
            fromTextureHolderFactory.index = this.textureHolderList.size();
            this.textureHolderList.add(fromTextureHolderFactory);
            System.out.println("getNewTextureHolder: append:" + fromTextureHolderFactory.index);
        }
        return fromTextureHolderFactory;
    }

    public TextureHolder findTextureHolder(int i) {
        return (TextureHolder) this.textureHolderList.get(i);
    }

    public boolean removeTextureHolder(int i) {
        if (this.textureHolderList.size() <= i) {
            warn("removeTextureHolder: cannot remove:" + i + " it is out of range");
            return false;
        }
        TextureHolder textureHolderFindTextureHolder = findTextureHolder(i);
        if (textureHolderFindTextureHolder != null) {
            textureHolderFindTextureHolder.remove();
        }
        this.textureHolderList.set(i, null);
        return true;
    }

    public TextureHolder getFromTextureHolderFactory() {
        return new TextureHolder();
    }

    /* JADX INFO: loaded from: game-lib.jar:com/LibRocket$TextureHolder.class */
    public static class TextureHolder {
        public int index;
        public int width;
        public int height;

        public TextureHolder() {
        }

        public void remove() {
        }
    }

    public static void log(String str) {
        System.out.println(str);
    }

    public static void warn(String str) {
        System.out.println(str);
    }
}
