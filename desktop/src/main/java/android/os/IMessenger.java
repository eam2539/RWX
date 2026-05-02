package android.os;

/* JADX INFO: loaded from: game-lib.jar:android/os/IMessenger.class */
public interface IMessenger extends IInterface {
    void a(Message message);

    /* JADX INFO: loaded from: game-lib.jar:android/os/IMessenger$Stub.class */
    public abstract class Stub extends Binder implements IMessenger {
        public Stub() {
            attachInterface(this, "android.os.IMessenger");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            Message message;
            switch (i) {
                case 1:
                    parcel.enforceInterface("android.os.IMessenger");
                    if (0 != parcel.readInt()) {
                        message = (Message) Message.m.createFromParcel(parcel);
                    } else {
                        message = null;
                    }
                    a(message);
                    break;
                case 1598968902:
                    parcel2.writeString("android.os.IMessenger");
                    break;
            }
            return true;
        }
    }
}
