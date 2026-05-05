#include <jni.h>

#include <Rocket/Core.h>
#include <Rocket/Controls.h>
#include <Rocket/Controls/ElementFormControl.h>
#include <Rocket/Debugger.h>

#include <chrono>
#include <cstdio>
#include <cstring>
#include <memory>
#include <string>
#include <vector>

namespace {

JavaVM* javaVm = nullptr;
JNIEnv* cachedEnv = nullptr;
Rocket::Core::Context* context = nullptr;
Rocket::Core::ElementDocument* activeDocument = nullptr;

JNIEnv* env() {
    if (cachedEnv != nullptr) {
        return cachedEnv;
    }
    if (javaVm == nullptr) {
        return nullptr;
    }
    JNIEnv* result = nullptr;
    if (javaVm->GetEnv(reinterpret_cast<void**>(&result), JNI_VERSION_1_6) != JNI_OK) {
        javaVm->AttachCurrentThread(reinterpret_cast<void**>(&result), nullptr);
    }
    return result;
}

std::string toStdString(JNIEnv* env, jstring value) {
    if (env == nullptr || value == nullptr) {
        return {};
    }
    const char* chars = env->GetStringUTFChars(value, nullptr);
    std::string result = chars != nullptr ? chars : "";
    if (chars != nullptr) {
        env->ReleaseStringUTFChars(value, chars);
    }
    return result;
}

void throwJavaException(JNIEnv* env, const char* message);

jfieldID nativeHandleField(JNIEnv* env, jobject object) {
    jclass cls = env->GetObjectClass(object);
    return env->GetFieldID(cls, "nativeHandle", "J");
}

Rocket::Core::Element* getElement(JNIEnv* env, jobject object) {
    if (env == nullptr || object == nullptr) {
        return nullptr;
    }
    jfieldID field = nativeHandleField(env, object);
    if (field == nullptr) {
        return nullptr;
    }
    return reinterpret_cast<Rocket::Core::Element*>(env->GetLongField(object, field));
}

Rocket::Core::Element* requireElement(JNIEnv* env, jobject object) {
    Rocket::Core::Element* element = getElement(env, object);
    if (element == nullptr) {
        throwJavaException(env, "native==null");
    }
    return element;
}

Rocket::Core::Element* requireChildElement(JNIEnv* env, jobject object) {
    Rocket::Core::Element* element = getElement(env, object);
    if (element == nullptr) {
        throwJavaException(env, "child: native==null");
    }
    return element;
}

Rocket::Core::Element* requireAdjacentElement(JNIEnv* env, jobject object) {
    Rocket::Core::Element* element = getElement(env, object);
    if (element == nullptr) {
        throwJavaException(env, "adjacentElement: native==null");
    }
    return element;
}

Rocket::Controls::ElementFormControl* getFormControl(Rocket::Core::Element* element) {
    return dynamic_cast<Rocket::Controls::ElementFormControl*>(element);
}

Rocket::Core::ElementDocument* getDocument(JNIEnv* env, jobject object) {
    return dynamic_cast<Rocket::Core::ElementDocument*>(getElement(env, object));
}

Rocket::Core::ElementDocument* requireDocument(JNIEnv* env, jobject object) {
    Rocket::Core::ElementDocument* document = getDocument(env, object);
    if (document == nullptr) {
        throwJavaException(env, "native==null");
    }
    return document;
}

bool iterateAttributeSequential(Rocket::Core::Element* element, int targetIndex, Rocket::Core::String& name, Rocket::Core::String& value) {
    int iterator = 0;
    Rocket::Core::Variant* variant = nullptr;
    if (targetIndex < 0) {
        return true;
    }
    for (int i = 0; i <= targetIndex; ++i) {
        if (!element->IterateAttributes(iterator, name, variant)) {
            return false;
        }
        if (variant != nullptr) {
            variant->GetInto(value);
        }
    }
    return true;
}

void setElement(JNIEnv* env, jobject object, Rocket::Core::Element* element) {
    jfieldID field = nativeHandleField(env, object);
    if (field != nullptr) {
        env->SetLongField(object, field, reinterpret_cast<jlong>(element));
    }
}

jobject createLinkedObject(JNIEnv* env, const char* className, Rocket::Core::Element* element) {
    if (env == nullptr || element == nullptr) {
        return nullptr;
    }
    jclass cls = env->FindClass(className);
    if (cls == nullptr) {
        return nullptr;
    }
    jmethodID constructor = env->GetMethodID(cls, "<init>", "()V");
    if (constructor == nullptr) {
        return nullptr;
    }
    jobject object = env->NewObject(cls, constructor);
    setElement(env, object, element);
    return object;
}

jobject createLinkedElement(JNIEnv* env, Rocket::Core::Element* element) {
    return createLinkedObject(env, "com/Element", element);
}

jobject createLinkedDocument(JNIEnv* env, Rocket::Core::ElementDocument* document) {
    return createLinkedObject(env, "com/ElementDocument", document);
}

void callOptionalStringCallback(JNIEnv* env, jobject owner, const char* value) {
    jclass cls = env->GetObjectClass(owner);
    if (cls == nullptr) {
        return;
    }
    jmethodID callback = env->GetMethodID(cls, "callback", "(Ljava/lang/String;)V");
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }
    if (callback == nullptr) {
        return;
    }
    jstring message = env->NewStringUTF(value);
    env->CallVoidMethod(owner, callback, message);
    env->DeleteLocalRef(message);
}

void throwJavaException(JNIEnv* env, const char* message) {
    jclass cls = env->FindClass("java/lang/RuntimeException");
    if (cls != nullptr) {
        env->ThrowNew(cls, message);
    }
}

class ShellFileInterface final : public Rocket::Core::FileInterface {
public:
    Rocket::Core::FileHandle Open(const Rocket::Core::String& path) override {
        return reinterpret_cast<Rocket::Core::FileHandle>(std::fopen(path.CString(), "rb"));
    }

    void Close(Rocket::Core::FileHandle file) override {
        if (file != 0) {
            std::fclose(reinterpret_cast<FILE*>(file));
        }
    }

    size_t Read(void* buffer, size_t size, Rocket::Core::FileHandle file) override {
        if (file == 0) {
            return 0;
        }
        return std::fread(buffer, 1, size, reinterpret_cast<FILE*>(file));
    }

    bool Seek(Rocket::Core::FileHandle file, long offset, int origin) override {
        return file != 0 && std::fseek(reinterpret_cast<FILE*>(file), offset, origin) == 0;
    }

    size_t Tell(Rocket::Core::FileHandle file) override {
        return file == 0 ? 0 : static_cast<size_t>(std::ftell(reinterpret_cast<FILE*>(file)));
    }

    void Release() override {}
};

class ShellSystemInterface final : public Rocket::Core::SystemInterface {
public:
    explicit ShellSystemInterface(JNIEnv* env, jobject owner) : owner(env->NewGlobalRef(owner)) {
        jclass cls = env->GetObjectClass(owner);
        translateMethod = env->GetMethodID(cls, "TranslateString", "(Ljava/lang/String;)Ljava/lang/String;");
        start = std::chrono::steady_clock::now();
    }

    float GetElapsedTime() override {
        return std::chrono::duration<float>(std::chrono::steady_clock::now() - start).count();
    }

    int TranslateString(Rocket::Core::String& translated, const Rocket::Core::String& input) override {
        JNIEnv* localEnv = env();
        if (localEnv == nullptr || owner == nullptr || translateMethod == nullptr) {
            translated = input;
            return 0;
        }
        jstring source = localEnv->NewStringUTF(input.CString());
        auto result = static_cast<jstring>(localEnv->CallObjectMethod(owner, translateMethod, source));
        localEnv->DeleteLocalRef(source);
        if (localEnv->ExceptionCheck() || result == nullptr) {
            translated = input;
            return 0;
        }
        std::string value = toStdString(localEnv, result);
        localEnv->DeleteLocalRef(result);
        translated = value.c_str();
        return value == input.CString() ? 0 : 1;
    }

    void Release() override {}

private:
    jobject owner = nullptr;
    jmethodID translateMethod = nullptr;
    std::chrono::steady_clock::time_point start;
};

class ShellEventListener final : public Rocket::Core::EventListener {
public:
    ShellEventListener(jobject owner, jmethodID method, std::string script) : owner(owner), method(method), script(std::move(script)) {}

    void ProcessEvent(Rocket::Core::Event&) override {
        JNIEnv* localEnv = env();
        if (localEnv == nullptr || owner == nullptr || method == nullptr) {
            return;
        }
        jstring value = localEnv->NewStringUTF(script.c_str());
        localEnv->CallVoidMethod(owner, method, value);
        localEnv->DeleteLocalRef(value);
        if (localEnv->ExceptionCheck()) {
            localEnv->ExceptionDescribe();
            localEnv->ExceptionClear();
        }
    }

private:
    jobject owner = nullptr;
    jmethodID method = nullptr;
    std::string script;
};

class ShellEventInstancer final : public Rocket::Core::EventListenerInstancer {
public:
    explicit ShellEventInstancer(JNIEnv* env, jobject owner) : owner(env->NewGlobalRef(owner)) {
        jclass cls = env->GetObjectClass(owner);
        handleEventMethod = env->GetMethodID(cls, "HandleEvent", "(Ljava/lang/String;)V");
    }

    Rocket::Core::EventListener* InstanceEventListener(const Rocket::Core::String& value, Rocket::Core::Element*) override {
        return new ShellEventListener(owner, handleEventMethod, value.CString());
    }

    void Release() override {}

private:
    jobject owner = nullptr;
    jmethodID handleEventMethod = nullptr;
};

class ShellRenderInterface final : public Rocket::Core::RenderInterface {
public:
    explicit ShellRenderInterface(JNIEnv* env, jobject owner) : owner(env->NewGlobalRef(owner)) {
        jclass cls = env->GetObjectClass(owner);
        enableScissorMethod = env->GetMethodID(cls, "EnableScissorRegion", "(Z)V");
        setScissorMethod = env->GetMethodID(cls, "SetScissorRegion", "(IIII)V");
        renderGeometryMethod = env->GetMethodID(cls, "RenderGeometry", "([F[F[I[IIFF)V");
        compileGeometryMethod = env->GetMethodID(cls, "CompileGeometry", "([F[F[I[II)I");
        renderCompiledGeometryMethod = env->GetMethodID(cls, "RenderCompiledGeometry", "(IFF)V");
        releaseCompiledGeometryMethod = env->GetMethodID(cls, "ReleaseCompiledGeometry", "(I)V");
        loadTextureMethod = env->GetMethodID(cls, "LoadTexture", "(ILjava/lang/String;)Z");
        generateTextureMethod = env->GetMethodID(cls, "GenerateTexture", "(I[B)Z");
        releaseTextureMethod = env->GetMethodID(cls, "ReleaseTexture", "(I)V");
        getNewTextureHolderMethod = env->GetMethodID(cls, "getNewTextureHolder", "()Lcom/LibRocket$TextureHolder;");
        findTextureHolderMethod = env->GetMethodID(cls, "findTextureHolder", "(I)Lcom/LibRocket$TextureHolder;");

        jclass textureHolderClass = env->FindClass("com/LibRocket$TextureHolder");
        textureIndexField = env->GetFieldID(textureHolderClass, "index", "I");
        textureWidthField = env->GetFieldID(textureHolderClass, "width", "I");
        textureHeightField = env->GetFieldID(textureHolderClass, "height", "I");
    }

    void RenderGeometry(Rocket::Core::Vertex* vertices, int numVertices, int* indices, int numIndices, Rocket::Core::TextureHandle texture, const Rocket::Core::Vector2f& translation) override {
        JNIEnv* localEnv = env();
        if (localEnv == nullptr || renderGeometryMethod == nullptr) {
            return;
        }
        jfloatArray xy = localEnv->NewFloatArray(numVertices * 2);
        jfloatArray uv = localEnv->NewFloatArray(numVertices * 2);
        jintArray colors = localEnv->NewIntArray(numVertices);
        jintArray indexArray = localEnv->NewIntArray(numIndices);
        std::vector<jfloat> xyValues(numVertices * 2);
        std::vector<jfloat> uvValues(numVertices * 2);
        std::vector<jint> colorValues(numVertices);
        std::vector<jint> indexValues(indices,indices+numIndices);
        for (int i = 0; i < numVertices; ++i) {
            xyValues[(i * 2) + 0] = vertices[i].position.x;
            xyValues[(i * 2) + 1] = vertices[i].position.y;
            uvValues[(i * 2) + 0] = vertices[i].tex_coord.x;
            uvValues[(i * 2) + 1] = vertices[i].tex_coord.y;
            colorValues[i] = (vertices[i].colour.red << 24) | (vertices[i].colour.green << 16) | (vertices[i].colour.blue << 8) | vertices[i].colour.alpha;
        }
        localEnv->SetFloatArrayRegion(xy, 0, static_cast<jsize>(xyValues.size()), xyValues.data());
        localEnv->SetFloatArrayRegion(uv, 0, static_cast<jsize>(uvValues.size()), uvValues.data());
        localEnv->SetIntArrayRegion(colors, 0, static_cast<jsize>(colorValues.size()), colorValues.data());
        localEnv->SetIntArrayRegion(indexArray, 0, numIndices, indexValues.data());
        localEnv->CallVoidMethod(owner, renderGeometryMethod, xy, uv, colors, indexArray, static_cast<jint>(texture), translation.x, translation.y);
        localEnv->DeleteLocalRef(xy);
        localEnv->DeleteLocalRef(uv);
        localEnv->DeleteLocalRef(colors);
        localEnv->DeleteLocalRef(indexArray);
    }

    Rocket::Core::CompiledGeometryHandle CompileGeometry(Rocket::Core::Vertex* vertices, int numVertices, int* indices, int numIndices, Rocket::Core::TextureHandle texture) override {
        JNIEnv* localEnv = env();
        if (localEnv == nullptr || compileGeometryMethod == nullptr) {
            return 0;
        }
        jfloatArray xy = localEnv->NewFloatArray(numVertices * 2);
        jfloatArray uv = localEnv->NewFloatArray(numVertices * 2);
        jintArray colors = localEnv->NewIntArray(numVertices);
        jintArray indexArray = localEnv->NewIntArray(numIndices);
        std::vector<jfloat> xyValues(numVertices * 2);
        std::vector<jfloat> uvValues(numVertices * 2);
        std::vector<jint> colorValues(numVertices);
        std::vector<jint> indexValues(indices,indices+numIndices);
        for (int i = 0; i < numVertices; ++i) {
            xyValues[(i * 2) + 0] = vertices[i].position.x;
            xyValues[(i * 2) + 1] = vertices[i].position.y;
            uvValues[(i * 2) + 0] = vertices[i].tex_coord.x;
            uvValues[(i * 2) + 1] = vertices[i].tex_coord.y;
            colorValues[i] = (vertices[i].colour.red << 24) | (vertices[i].colour.green << 16) | (vertices[i].colour.blue << 8) | vertices[i].colour.alpha;
        }
        localEnv->SetFloatArrayRegion(xy, 0, static_cast<jsize>(xyValues.size()), xyValues.data());
        localEnv->SetFloatArrayRegion(uv, 0, static_cast<jsize>(uvValues.size()), uvValues.data());
        localEnv->SetIntArrayRegion(colors, 0, static_cast<jsize>(colorValues.size()), colorValues.data());
        localEnv->SetIntArrayRegion(indexArray, 0, numIndices, indexValues.data());
        jint handle = localEnv->CallIntMethod(owner, compileGeometryMethod, xy, uv, colors, indexArray, static_cast<jint>(texture));
        localEnv->DeleteLocalRef(xy);
        localEnv->DeleteLocalRef(uv);
        localEnv->DeleteLocalRef(colors);
        localEnv->DeleteLocalRef(indexArray);
        return static_cast<Rocket::Core::CompiledGeometryHandle>(handle);
    }

    void RenderCompiledGeometry(Rocket::Core::CompiledGeometryHandle geometry, const Rocket::Core::Vector2f& translation) override {
        JNIEnv* localEnv = env();
        if (localEnv != nullptr && renderCompiledGeometryMethod != nullptr) {
            localEnv->CallVoidMethod(owner, renderCompiledGeometryMethod, static_cast<jint>(geometry), translation.x, translation.y);
        }
    }

    void ReleaseCompiledGeometry(Rocket::Core::CompiledGeometryHandle geometry) override {
        JNIEnv* localEnv = env();
        if (localEnv != nullptr && releaseCompiledGeometryMethod != nullptr) {
            localEnv->CallVoidMethod(owner, releaseCompiledGeometryMethod, static_cast<jint>(geometry));
        }
    }

    void EnableScissorRegion(bool enable) override {
        JNIEnv* localEnv = env();
        if (localEnv != nullptr && enableScissorMethod != nullptr) {
            localEnv->CallVoidMethod(owner, enableScissorMethod, static_cast<jboolean>(enable));
        }
    }

    void SetScissorRegion(int x, int y, int width, int height) override {
        JNIEnv* localEnv = env();
        if (localEnv != nullptr && setScissorMethod != nullptr) {
            localEnv->CallVoidMethod(owner, setScissorMethod, x, y, width, height);
        }
    }

    bool LoadTexture(Rocket::Core::TextureHandle& texture, Rocket::Core::Vector2i& dimensions, const Rocket::Core::String& source) override {
        JNIEnv* localEnv = env();
        if (localEnv == nullptr || getNewTextureHolderMethod == nullptr || loadTextureMethod == nullptr) {
            return false;
        }
        jobject holder = localEnv->CallObjectMethod(owner, getNewTextureHolderMethod);
        if (holder == nullptr) {
            return false;
        }
        jint index = localEnv->GetIntField(holder, textureIndexField);
        jstring path = localEnv->NewStringUTF(source.CString());
        bool ok = localEnv->CallBooleanMethod(owner, loadTextureMethod, index, path);
        localEnv->DeleteLocalRef(path);
        if (localEnv->ExceptionCheck() || !ok) {
            localEnv->DeleteLocalRef(holder);
            return false;
        }
        dimensions.x = localEnv->GetIntField(holder, textureWidthField);
        dimensions.y = localEnv->GetIntField(holder, textureHeightField);
        texture = static_cast<Rocket::Core::TextureHandle>(index);
        localEnv->DeleteLocalRef(holder);
        return true;
    }

    bool GenerateTexture(Rocket::Core::TextureHandle& texture, const Rocket::Core::byte* source, const Rocket::Core::Vector2i& dimensions) override {
        JNIEnv* localEnv = env();
        if (localEnv == nullptr || getNewTextureHolderMethod == nullptr || generateTextureMethod == nullptr) {
            return false;
        }
        jobject holder = localEnv->CallObjectMethod(owner, getNewTextureHolderMethod);
        if (holder == nullptr) {
            return false;
        }
        jint index = localEnv->GetIntField(holder, textureIndexField);
        localEnv->SetIntField(holder, textureWidthField, dimensions.x);
        localEnv->SetIntField(holder, textureHeightField, dimensions.y);
        jbyteArray data = localEnv->NewByteArray(dimensions.x * dimensions.y * 4);
        localEnv->SetByteArrayRegion(data, 0, dimensions.x * dimensions.y * 4, reinterpret_cast<const jbyte*>(source));
        bool ok = localEnv->CallBooleanMethod(owner, generateTextureMethod, index, data);
        localEnv->DeleteLocalRef(data);
        texture = static_cast<Rocket::Core::TextureHandle>(index);
        localEnv->DeleteLocalRef(holder);
        return ok && !localEnv->ExceptionCheck();
    }

    void ReleaseTexture(Rocket::Core::TextureHandle texture) override {
        JNIEnv* localEnv = env();
        if (localEnv != nullptr && releaseTextureMethod != nullptr) {
            localEnv->CallVoidMethod(owner, releaseTextureMethod, static_cast<jint>(texture));
        }
    }

    void Release() override {}

private:
    jobject owner = nullptr;
    jmethodID enableScissorMethod = nullptr;
    jmethodID setScissorMethod = nullptr;
    jmethodID renderGeometryMethod = nullptr;
    jmethodID compileGeometryMethod = nullptr;
    jmethodID renderCompiledGeometryMethod = nullptr;
    jmethodID releaseCompiledGeometryMethod = nullptr;
    jmethodID loadTextureMethod = nullptr;
    jmethodID generateTextureMethod = nullptr;
    jmethodID releaseTextureMethod = nullptr;
    jmethodID getNewTextureHolderMethod = nullptr;
    jmethodID findTextureHolderMethod = nullptr;
    jfieldID textureIndexField = nullptr;
    jfieldID textureWidthField = nullptr;
    jfieldID textureHeightField = nullptr;
};

std::unique_ptr<ShellRenderInterface> renderInterface;
std::unique_ptr<ShellSystemInterface> systemInterface;
std::unique_ptr<ShellFileInterface> fileInterface;
std::unique_ptr<ShellEventInstancer> eventInstancer;

}  // namespace

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    javaVm = vm;
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_setup(JNIEnv* env, jobject owner) {
    cachedEnv = env;
    activeDocument = nullptr;
    callOptionalStringCallback(env, owner, "Hello from C++");
    renderInterface = std::make_unique<ShellRenderInterface>(env, owner);
    systemInterface = std::make_unique<ShellSystemInterface>(env, owner);
    fileInterface = std::make_unique<ShellFileInterface>();
    Rocket::Core::SetRenderInterface(renderInterface.get());
    Rocket::Core::SetSystemInterface(systemInterface.get());
    Rocket::Core::SetFileInterface(fileInterface.get());
    Rocket::Core::Initialise();
    Rocket::Controls::Initialise();
    eventInstancer = std::make_unique<ShellEventInstancer>(env, owner);
    Rocket::Core::Factory::RegisterEventListenerInstancer(eventInstancer.get());
    context = Rocket::Core::CreateContext("main", Rocket::Core::Vector2i(1200, 800));
    if (context == nullptr) {
        throwJavaException(env, "context==null");
        return;
    }
    Rocket::Debugger::Initialise(context);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_LibRocket_loadDocument(JNIEnv* env, jobject, jstring path) {
    cachedEnv = env;
    Rocket::Core::Factory::ClearStyleSheetCache();
    activeDocument = context != nullptr ? context->LoadDocument(toStdString(env, path).c_str()) : nullptr;
    if (activeDocument == nullptr) {
        throwJavaException(env, "document==null");
        return nullptr;
    }
    return createLinkedDocument(env, activeDocument);
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_loadDocumentWithContainer(JNIEnv* env, jobject owner, jstring path, jobject container) {
    jobject document = Java_com_LibRocket_loadDocument(env, owner, path);
    if (document != nullptr) {
        setElement(env, container, activeDocument);
        env->DeleteLocalRef(document);
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_loadFont__Ljava_lang_String_2Ljava_lang_String_2(JNIEnv* env, jobject, jstring path, jstring family) {
    cachedEnv = env;
    std::string pathValue = toStdString(env, path);
    std::string familyValue = toStdString(env, family);
    if (familyValue.empty()) {
        Rocket::Core::FontDatabase::LoadFontFace(pathValue.c_str());
    } else {
        Rocket::Core::FontDatabase::LoadFontFace(pathValue.c_str(), familyValue.c_str(), Rocket::Core::Font::STYLE_NORMAL, Rocket::Core::Font::WEIGHT_NORMAL);
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_loadFont(JNIEnv* env, jobject owner, jstring path, jstring family) {
    Java_com_LibRocket_loadFont__Ljava_lang_String_2Ljava_lang_String_2(env, owner, path, family);
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_update(JNIEnv* env, jobject) {
    cachedEnv = env;
    if (context != nullptr) {
        context->Update();
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_render(JNIEnv* env, jobject) {
    cachedEnv = env;
    if (context != nullptr) {
        context->Render();
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_setDimensions(JNIEnv* env, jobject, jint width, jint height) {
    cachedEnv = env;
    if (context != nullptr) {
        context->SetDimensions(Rocket::Core::Vector2i(width, height));
    }
}

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processMouseMove(JNIEnv* env, jobject, jint x, jint y, jint modifiers) { cachedEnv = env; if (context != nullptr) context->ProcessMouseMove(x, y, modifiers); }
extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processMouseButtonDown(JNIEnv* env, jobject, jint button, jint modifiers) { cachedEnv = env; if (context != nullptr) context->ProcessMouseButtonDown(button, modifiers); }
extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processMouseButtonUp(JNIEnv* env, jobject, jint button, jint modifiers) { cachedEnv = env; if (context != nullptr) context->ProcessMouseButtonUp(button, modifiers); }
extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processMouseWheel(JNIEnv* env, jobject, jint wheel, jint modifiers) { cachedEnv = env; if (context != nullptr) context->ProcessMouseWheel(wheel, modifiers); }
extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processTextInputChar(JNIEnv* env, jobject, jint value) { cachedEnv = env; if (context != nullptr) context->ProcessTextInput(static_cast<unsigned short>(value)); }
extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processKeyDown(JNIEnv* env, jobject, jint key, jint modifiers) { cachedEnv = env; if (context != nullptr) context->ProcessKeyDown(static_cast<Rocket::Core::Input::KeyIdentifier>(key), modifiers); }
extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processKeyUp(JNIEnv* env, jobject, jint key, jint modifiers) { cachedEnv = env; if (context != nullptr) context->ProcessKeyUp(static_cast<Rocket::Core::Input::KeyIdentifier>(key), modifiers); }

extern "C" JNIEXPORT void JNICALL Java_com_LibRocket_processTextInput(JNIEnv* env, jobject, jstring text) {
    cachedEnv = env;
    if (context != nullptr) {
        context->ProcessTextInput(toStdString(env, text).c_str());
    }
}

extern "C" JNIEXPORT jobject JNICALL Java_com_Element_getElementById(JNIEnv* env, jobject object, jstring id) {
    Rocket::Core::Element* element = requireElement(env, object);
    if (env->ExceptionCheck()) return nullptr;
    return createLinkedElement(env, element != nullptr ? element->GetElementById(toStdString(env, id).c_str()) : nullptr);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getTagName(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? nullptr : env->NewStringUTF(element->GetTagName().CString()); }
extern "C" JNIEXPORT jboolean JNICALL Java_com_Element_focus(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return !env->ExceptionCheck() && element->Focus(); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_blur(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->Blur(); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_click(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->Click(); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_addReference(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->AddReference(); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_removeReference(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->RemoveReference(); }

extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getAttribute(JNIEnv* env, jobject object, jstring key, jstring defaultValue) {
    Rocket::Core::Element* element = requireElement(env, object);
    std::string fallback = toStdString(env, defaultValue);
    if (env->ExceptionCheck()) {
        return nullptr;
    }
    std::string keyValue = toStdString(env, key);
    if (keyValue == "value") {
        Rocket::Controls::ElementFormControl* control = getFormControl(element);
        if (control != nullptr) {
            return env->NewStringUTF(control->GetValue().CString());
        }
    }
    Rocket::Core::Variant* value = element->GetAttribute(keyValue.c_str());
    if (value == nullptr) {
        return defaultValue == nullptr ? nullptr : env->NewStringUTF(fallback.c_str());
    }
    Rocket::Core::String text = value->Get<Rocket::Core::String>();
    return env->NewStringUTF(text.CString());
}

extern "C" JNIEXPORT void JNICALL Java_com_Element_setAttribute(JNIEnv* env, jobject object, jstring key, jstring value) {
    Rocket::Core::Element* element = requireElement(env, object);
    if (env->ExceptionCheck()) return;
    std::string keyValue = toStdString(env, key);
    std::string valueText = value == nullptr ? "" : toStdString(env, value);
    if (keyValue == "value") {
        Rocket::Controls::ElementFormControl* control = getFormControl(element);
        if (control != nullptr) {
            control->SetValue(valueText.c_str());
            return;
        }
    }
    if (value == nullptr) {
        element->RemoveAttribute(keyValue.c_str());
    } else {
        element->SetAttribute(keyValue.c_str(), valueText.c_str());
        if (keyValue.size() > 2 && keyValue.compare(0, 2, "on") == 0 && eventInstancer != nullptr) {
            Rocket::Core::String eventName(keyValue.substr(2).c_str());
            Rocket::Core::EventListener* listener = eventInstancer->InstanceEventListener(valueText.c_str(), element);
            if (listener != nullptr) {
                element->AddEventListener(eventName, listener);
            }
        }
    }
}

extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getAttributeKey(JNIEnv* env, jobject object, jint index) {
    Rocket::Core::Element* element = requireElement(env, object);
    if (env->ExceptionCheck()) return nullptr;
    Rocket::Core::String name;
    Rocket::Core::String value;
    return iterateAttributeSequential(element, index, name, value) ? env->NewStringUTF(name.CString()) : nullptr;
}

extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getAttributeValue(JNIEnv* env, jobject object, jint index) {
    Rocket::Core::Element* element = requireElement(env, object);
    if (env->ExceptionCheck()) return nullptr;
    Rocket::Core::String name;
    Rocket::Core::String value;
    return iterateAttributeSequential(element, index, name, value) ? env->NewStringUTF(value.CString()) : nullptr;
}

extern "C" JNIEXPORT jint JNICALL Java_com_Element_getNumAttributes(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? -1 : element->GetNumAttributes(); }
extern "C" JNIEXPORT jobject JNICALL Java_com_Element_getChild(JNIEnv* env, jobject object, jint index) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? nullptr : createLinkedElement(env, element->GetChild(index)); }
extern "C" JNIEXPORT jint JNICALL Java_com_Element_getNumChildren(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0 : element->GetNumChildren(); }
extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getInnerRML(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? nullptr : env->NewStringUTF(element->GetInnerRML().CString()); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_setInnerRML(JNIEnv* env, jobject object, jstring value) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->SetInnerRML(toStdString(env, value).c_str()); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_setClassNames(JNIEnv* env, jobject object, jstring value) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->SetClassNames(toStdString(env, value).c_str()); }
extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getClassNames(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? nullptr : env->NewStringUTF(element->GetClassNames().CString()); }
extern "C" JNIEXPORT jobject JNICALL Java_com_Element_m29clone(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? nullptr : createLinkedElement(env, element->Clone()); }
extern "C" JNIEXPORT jobject JNICALL Java_com_Element_clone(JNIEnv* env, jobject object) { return Java_com_Element_m29clone(env, object); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_appendChild(JNIEnv* env, jobject object, jobject childObject) { Rocket::Core::Element* element = requireElement(env, object); if (env->ExceptionCheck()) return; Rocket::Core::Element* child = requireChildElement(env, childObject); if (!env->ExceptionCheck()) element->AppendChild(child); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_insertBefore(JNIEnv* env, jobject object, jobject childObject, jobject adjacentObject) { Rocket::Core::Element* element = requireElement(env, object); if (env->ExceptionCheck()) return; Rocket::Core::Element* child = requireChildElement(env, childObject); if (env->ExceptionCheck()) return; Rocket::Core::Element* adjacent = requireAdjacentElement(env, adjacentObject); if (!env->ExceptionCheck()) element->InsertBefore(child, adjacent); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_removeChild(JNIEnv* env, jobject object, jobject childObject) { Rocket::Core::Element* element = requireElement(env, object); if (env->ExceptionCheck()) return; Rocket::Core::Element* child = requireChildElement(env, childObject); if (!env->ExceptionCheck()) element->RemoveChild(child); }
extern "C" JNIEXPORT jstring JNICALL Java_com_Element_getProperty(JNIEnv* env, jobject object, jstring key, jstring defaultValue) { Rocket::Core::Element* element = requireElement(env, object); if (env->ExceptionCheck()) return nullptr; const Rocket::Core::Property* property = element->GetProperty(toStdString(env, key).c_str()); return property != nullptr ? env->NewStringUTF(property->ToString().CString()) : defaultValue; }
extern "C" JNIEXPORT void JNICALL Java_com_Element_setProperty(JNIEnv* env, jobject object, jstring key, jstring value) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->SetProperty(toStdString(env, key).c_str(), toStdString(env, value).c_str()); }
extern "C" JNIEXPORT jboolean JNICALL Java_com_Element_isPseudoClassSet(JNIEnv* env, jobject object, jstring pseudoClass) { Rocket::Core::Element* element = requireElement(env, object); return !env->ExceptionCheck() && element->IsPseudoClassSet(toStdString(env, pseudoClass).c_str()); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getAbsoluteLeft(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetAbsoluteLeft(); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getAbsoluteTop(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetAbsoluteTop(); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getOffsetLeft(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetOffsetLeft(); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getOffsetTop(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetOffsetTop(); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getOffsetWidth(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetOffsetWidth(); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getOffsetHeight(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetOffsetHeight(); }
extern "C" JNIEXPORT jfloat JNICALL Java_com_Element_getScrollTop(JNIEnv* env, jobject object) { Rocket::Core::Element* element = requireElement(env, object); return env->ExceptionCheck() ? 0.0f : element->GetScrollTop(); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_setScrollTop(JNIEnv* env, jobject object, jfloat value) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->SetScrollTop(value); }
extern "C" JNIEXPORT void JNICALL Java_com_Element_scrollIntoView(JNIEnv* env, jobject object, jboolean top) { Rocket::Core::Element* element = requireElement(env, object); if (!env->ExceptionCheck()) element->ScrollIntoView(top); }

extern "C" JNIEXPORT void JNICALL Java_com_ElementDocument_show(JNIEnv* env, jobject object, jint flags) { Rocket::Core::ElementDocument* document = requireDocument(env, object); if (!env->ExceptionCheck()) document->Show(flags); }
extern "C" JNIEXPORT void JNICALL Java_com_ElementDocument_hide(JNIEnv* env, jobject object) { Rocket::Core::ElementDocument* document = requireDocument(env, object); if (!env->ExceptionCheck()) document->Hide(); }
extern "C" JNIEXPORT void JNICALL Java_com_ElementDocument_close(JNIEnv* env, jobject object) { Rocket::Core::ElementDocument* document = requireDocument(env, object); if (!env->ExceptionCheck()) document->Close(); }
extern "C" JNIEXPORT void JNICALL Java_com_ElementDocument_pullToFront(JNIEnv* env, jobject object) { Rocket::Core::ElementDocument* document = requireDocument(env, object); if (!env->ExceptionCheck()) document->PullToFront(); }
extern "C" JNIEXPORT void JNICALL Java_com_ElementDocument_pushToBack(JNIEnv* env, jobject object) { Rocket::Core::ElementDocument* document = requireDocument(env, object); if (!env->ExceptionCheck()) document->PushToBack(); }
