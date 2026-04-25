package android.os;

import android.os.Parcelable;

/* JADX INFO: loaded from: game-lib.jar:android/os/Message.class */
public final class Message implements Parcelable {
    public int a;
    public int b;
    public int c;
    public Object d;
    public Messenger e;
    public int f = -1;
    int g;
    long h;
    Bundle i;
    Handler j;
    Runnable k;
    Message l;
    private static Message o;
    private static final Object n = new Object();
    private static int p = 0;
    private static boolean q = true;
    public static final Parcelable.Creator m = new Parcelable.Creator() { // from class: android.os.Message.1
        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public Message createFromParcel(Parcel parcel) {
            Message messageA = Message.a();
            messageA.a(parcel);
            return messageA;
        }

        @Override // android.os.Parcelable.Creator
        /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
        public Message[] newArray(int i) {
            return new Message[i];
        }
    };

    public static Message a() {
        synchronized (n) {
            if (o != null) {
                Message message = o;
                o = message.l;
                message.l = null;
                message.g = 0;
                p--;
                return message;
            }
            return new Message();
        }
    }

    public static Message a(Handler handler) {
        Message messageA = a();
        messageA.j = handler;
        return messageA;
    }

    public void b() {
        if (f()) {
            if (q) {
                throw new IllegalStateException("This message cannot be recycled because it is still in use.");
            }
        } else {
            c();
        }
    }

    void c() {
        this.g = 1;
        this.a = 0;
        this.b = 0;
        this.c = 0;
        this.d = null;
        this.e = null;
        this.f = -1;
        this.h = 0L;
        this.j = null;
        this.k = null;
        this.i = null;
        synchronized (n) {
            if (p < 50) {
                this.l = o;
                o = this;
                p++;
            }
        }
    }

    public Bundle d() {
        if (this.i == null) {
            this.i = new Bundle();
        }
        return this.i;
    }

    public boolean e() {
        return (this.g & 2) != 0;
    }

    public void a(boolean z) {
        if (z) {
            this.g |= 2;
        } else {
            this.g &= -3;
        }
    }

    boolean f() {
        return (this.g & 1) == 1;
    }

    void g() {
        this.g |= 1;
    }

    public String toString() {
        return a(SystemClock.a());
    }

    String a(long j) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ when=");
        sb.append("corrodinggames-unsupported");
        if (this.j != null) {
            if (this.k != null) {
                sb.append(" callback=");
                sb.append(this.k.getClass().getName());
            } else {
                sb.append(" what=");
                sb.append(this.a);
            }
            if (this.b != 0) {
                sb.append(" arg1=");
                sb.append(this.b);
            }
            if (this.c != 0) {
                sb.append(" arg2=");
                sb.append(this.c);
            }
            if (this.d != null) {
                sb.append(" obj=");
                sb.append(this.d);
            }
            sb.append(" target=");
            sb.append(this.j.getClass().getName());
        } else {
            sb.append(" barrier=");
            sb.append(this.b);
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.k != null) {
            throw new RuntimeException("Can't marshal callbacks across processes.");
        }
        parcel.writeInt(this.a);
        parcel.writeInt(this.b);
        parcel.writeInt(this.c);
        if (this.d != null) {
            try {
                Parcelable parcelable = (Parcelable) this.d;
                parcel.writeInt(1);
                parcel.writeParcelable(parcelable, i);
            } catch (ClassCastException e) {
                throw new RuntimeException("Can't marshal non-Parcelable objects across processes.");
            }
        } else {
            parcel.writeInt(0);
        }
        parcel.writeLong(this.h);
        parcel.writeBundle(this.i);
        Messenger.writeMessengerOrNullToParcel(this.e, parcel);
        parcel.writeInt(this.f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(Parcel parcel) {
        this.a = parcel.readInt();
        this.b = parcel.readInt();
        this.c = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.d = parcel.readParcelable(getClass().getClassLoader());
        }
        this.h = parcel.readLong();
        this.i = parcel.readBundle();
        this.e = Messenger.readMessengerOrNullFromParcel(parcel);
        this.f = parcel.readInt();
    }
}
