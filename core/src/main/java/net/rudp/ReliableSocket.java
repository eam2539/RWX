package net.rudp;

import net.rudp.impl.*;
import net.rudp.socket.ReliableSocketListener;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/* JADX INFO: renamed from: a.a.h */
/* JADX INFO: loaded from: game-lib.jar:a/a/h.class */
public class ReliableSocket extends Socket {

    /* JADX INFO: renamed from: c */
    protected DatagramSocket datagramSocket;

    /* JADX INFO: renamed from: d */
    protected SocketAddress peerAddress;

    /* JADX INFO: renamed from: e */
    protected ConnectionInputStream inputStream;

    /* JADX INFO: renamed from: f */
    protected ConnectionOutputStream outputStream;

    /* JADX INFO: renamed from: a */
    private byte[] receiveBuffer;

    /* JADX INFO: renamed from: b */
    private boolean closed;

    /* JADX INFO: renamed from: i */
    private boolean connected;

    /* JADX INFO: renamed from: j */
    private boolean outputFlowControlEnabled;

    /* JADX INFO: renamed from: k */
    private boolean keepAliveEnabled;

    /* JADX INFO: renamed from: l */
    private int connectionState;

    /* JADX INFO: renamed from: m */
    private int soTimeoutMs;

    /* JADX INFO: renamed from: n */
    private boolean inputShutdown;

    /* JADX INFO: renamed from: o */
    private boolean outputShutdown;

    /* JADX INFO: renamed from: p */
    private int previousState;

    /* JADX INFO: renamed from: q */
    private Object closeLock;

    /* JADX INFO: renamed from: r */
    private Object sendLock;

    /* JADX INFO: renamed from: s */
    private ArrayList sentPacketsLog;

    /* JADX INFO: renamed from: t */
    private ArrayList receivedPacketsLog;

    /* JADX INFO: renamed from: g */
    protected ReliableSocketProfile socketProfile;

    /* JADX INFO: renamed from: u */
    ArrayList<Segment> unacknowledgedPackets;

    /* JADX INFO: renamed from: v */
    private ArrayList outOfOrderPackets;

    /* JADX INFO: renamed from: w */
    private ArrayList acknowledgmentHistory;

    /* JADX INFO: renamed from: x */
    private Object inputLock;

    /* JADX INFO: renamed from: y */
    ConnectionStats connectionStats;

    /* JADX INFO: renamed from: z */
    private Thread packetReaderThread;

    /* JADX INFO: renamed from: A */
    private int sendWindow;

    /* JADX INFO: renamed from: B */
    private int receiveWindow;

    /* JADX INFO: renamed from: C */
    private int sendBufferSize;

    /* JADX INFO: renamed from: D */
    private int receiveBufferSize;

    /* JADX INFO: renamed from: h */
    public boolean isCongestionControlEnabled;

    /* JADX INFO: renamed from: E */
    private CustomTimerTask nullSegmentTimer;

    /* JADX INFO: renamed from: F */
    private CustomTimerTask retransmissionTimer;

    /* JADX INFO: renamed from: G */
    private CustomTimerTask cumulativeAckTimer;

    /* JADX INFO: renamed from: H */
    private CustomTimerTask keepAliveTimer;

    /* JADX INFO: renamed from: I */
    static final boolean DEBUG = Boolean.getBoolean("net.rudp.debug");

    public ReliableSocket() throws SocketException {
        this(new ReliableSocketProfile());
    }

    public ReliableSocket(ReliableSocketProfile reliableSocketProfile) throws SocketException {
        this(new DatagramSocket(), reliableSocketProfile);
    }

    protected ReliableSocket(DatagramSocket datagramSocket) {
        this(datagramSocket, new ReliableSocketProfile());
    }

    protected ReliableSocket(DatagramSocket datagramSocket, ReliableSocketProfile reliableSocketProfile) {
        this.closed = false;
        this.connected = false;
        this.outputFlowControlEnabled = false;
        this.keepAliveEnabled = true;
        this.connectionState = 0;
        this.soTimeoutMs = 0;
        this.inputShutdown = false;
        this.outputShutdown = false;
        this.previousState = -1;
        this.closeLock = new Object();
        this.sendLock = new Object();
        this.sentPacketsLog = new ArrayList();
        this.receivedPacketsLog = new ArrayList();
        this.socketProfile = ReliableSocketProfile.DEFAULT_PROFILE;
        this.unacknowledgedPackets = new ArrayList();
        this.outOfOrderPackets = new ArrayList();
        this.acknowledgmentHistory = new ArrayList();
        this.inputLock = new Object();
        this.connectionStats = new ConnectionStats();
        this.sendWindow = 32;
        this.receiveWindow = 32;
        this.isCongestionControlEnabled = false;
        this.nullSegmentTimer = new CustomTimerTask("rudp-NullSegmentTimer", new ReliableSocketCloseIfIdleTask(this));
        this.retransmissionTimer = new CustomTimerTask("rudp-RetransmissionTimer", new ReliableSocketRestartPacketTimerTask(this));
        this.cumulativeAckTimer = new CustomTimerTask("rudp-CumulativeAckTimer", new ReliableSocketKeepAliveTask(this));
        this.keepAliveTimer = new CustomTimerTask("rudp-KeepAliveTimer", new ReliableSocketRetransmitTask(this));
        if (datagramSocket == null) {
            throw new NullPointerException("sock");
        }
        init(datagramSocket, reliableSocketProfile);
    }

    /* JADX INFO: renamed from: a */
    protected void init(DatagramSocket datagramSocket, ReliableSocketProfile reliableSocketProfile) {
        this.datagramSocket = datagramSocket;
        this.socketProfile = reliableSocketProfile;
        this.sendBufferSize = (this.socketProfile.getMaxSegmentSize() - 6) * 32;
        this.receiveBufferSize = (this.socketProfile.getMaxSegmentSize() - 6) * 32;
        if (this.packetReaderThread == null) {
            this.packetReaderThread = new ReliableSocketPacketHandlerThread(this);
            this.packetReaderThread.start();
        }
    }

    @Override // java.net.Socket
    public void bind(SocketAddress socketAddress) throws SocketException {
        this.datagramSocket.bind(socketAddress);
    }

    @Override // java.net.Socket
    public void connect(SocketAddress socketAddress) throws SocketException, SocketTimeoutException {
        connect(socketAddress, 0);
    }

    @Override // java.net.Socket
    public void connect(SocketAddress socketAddress, int i) throws SocketException, SocketTimeoutException {
        if (socketAddress == null) {
            throw new IllegalArgumentException("connect: The address can't be null");
        }
        if (i < 0) {
            throw new IllegalArgumentException("connect: timeout can't be negative");
        }
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (isConnected()) {
            throw new SocketException("already connected");
        }
        if (!(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
        }
        this.peerAddress = (InetSocketAddress) socketAddress;
        startCongestionControlTimers();
        this.connectionState = 2;
        closeImpl(new SYNSegment(this.connectionStats.setNextSequenceToSend(new Random(System.currentTimeMillis()).nextInt(255)), this.socketProfile.getMaxOutstandingSegments(), this.socketProfile.getMaxSegmentSize(), this.socketProfile.getRetransmissionTimeoutMs(), this.socketProfile.getCumulativeAckTimeoutMs(), this.socketProfile.getNullSegmentTimeoutMs(), this.socketProfile.getMaxRetransmissions(), this.socketProfile.getMaxCumulativeAcks(), this.socketProfile.getMaxOutOfOrder(), this.socketProfile.getMaxAutoResets()));
        boolean z = false;
        synchronized (this) {
            if (!isConnected()) {
                try {
                    if (i == 0) {
                        wait();
                    } else {
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        wait(i);
                        if (System.currentTimeMillis() - jCurrentTimeMillis >= i) {
                            z = true;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (this.connectionState == 3) {
            return;
        }
        synchronized (this.unacknowledgedPackets) {
            this.unacknowledgedPackets.clear();
            this.unacknowledgedPackets.notifyAll();
        }
        this.connectionStats.reset();
        this.retransmissionTimer.cancel();
        switch (this.connectionState) {
            case 0:
            case 4:
                this.connectionState = 0;
                throw new SocketException("Socket closed");
            case 1:
            case 3:
            default:
                return;
            case 2:
                processConnectionRefused();
                this.connectionState = 0;
                if (z) {
                    throw new SocketTimeoutException();
                }
                throw new SocketException("Connection refused");
        }
    }

    @Override // java.net.Socket
    public SocketChannel getChannel() {
        return null;
    }

    @Override // java.net.Socket
    public InetAddress getInetAddress() {
        if (!isConnected()) {
            return null;
        }
        return ((InetSocketAddress) this.peerAddress).getAddress();
    }

    @Override // java.net.Socket
    public int getPort() {
        if (!isConnected()) {
            return 0;
        }
        return ((InetSocketAddress) this.peerAddress).getPort();
    }

    @Override // java.net.Socket
    public SocketAddress getRemoteSocketAddress() {
        if (!isConnected()) {
            return null;
        }
        return new InetSocketAddress(getInetAddress(), getPort());
    }

    /* JADX INFO: renamed from: c */
    public SocketAddress getPeerAddress() {
        return this.peerAddress;
    }

    @Override // java.net.Socket
    public InetAddress getLocalAddress() {
        return this.datagramSocket.getLocalAddress();
    }

    @Override // java.net.Socket
    public int getLocalPort() {
        return this.datagramSocket.getLocalPort();
    }

    @Override // java.net.Socket
    public SocketAddress getLocalSocketAddress() {
        return this.datagramSocket.getLocalSocketAddress();
    }

    @Override // java.net.Socket
    public synchronized InputStream getInputStream() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected()) {
            throw new SocketException("Socket is not connected");
        }
        if (isInputShutdown()) {
            throw new SocketException("Socket input is shutdown");
        }
        if (this.inputStream == null) {
            this.inputStream = new ConnectionInputStream(this);
        }
        return this.inputStream;
    }

    @Override // java.net.Socket
    public synchronized OutputStream getOutputStream() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected()) {
            throw new SocketException("Socket is not connected");
        }
        if (isOutputShutdown()) {
            throw new SocketException("Socket output is shutdown");
        }
        if (this.outputStream == null) {
            this.outputStream = new ConnectionOutputStream(this);
        }
        return this.outputStream;
    }

    /* JADX INFO: renamed from: d */
    public void forceClose() {
        this.closed = true;
        this.connectionState = 0;
        this.datagramSocket.close();
    }

    @Override // java.net.Socket
    public synchronized void close() {
        Object object = this.closeLock;
        synchronized (object) {
            Object object2;
            if (this.isClosed()) {
                return;
            }
            this.stopCongestionControlTimers();
            switch (this.connectionState) {
                case 2: {
                    object2 = this;
                    synchronized (object2) {
                        this.notify();
                        break;
                    }
                }
                case 1:
                case 3:
                case 4: {
                    this.sendPacket(new FINSegment(this.connectionStats.nextSequenceToSendAndIncrement()));
                    this.handleSynPacket();
                    break;
                }
                case 0: {
                    this.datagramSocket.close();
                }
            }
            if (this.connectionState != 0) {
                this.previousState = this.connectionState;
            }
            this.closed = true;
            this.connectionState = 0;
            this.cleanupSession();
            object2 = this.unacknowledgedPackets;
            synchronized (object2) {
                this.unacknowledgedPackets.notify();
            }
            object2 = this.acknowledgmentHistory;
            synchronized (object2) {
                this.acknowledgmentHistory.notify();
            }
        }
    }

    @Override // java.net.Socket
    public boolean isBound() {
        return this.datagramSocket.isBound();
    }

    @Override // java.net.Socket
    public boolean isConnected() {
        return this.connected;
    }

    @Override // java.net.Socket
    public boolean isClosed() {
        boolean z;
        synchronized (this.closeLock) {
            z = this.closed;
        }
        return z;
    }

    @Override // java.net.Socket
    public void setSoTimeout(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        this.soTimeoutMs = i;
    }

    @Override // java.net.Socket
    public synchronized void setSendBufferSize(int i) throws SocketException {
        if (i <= 0) {
            throw new IllegalArgumentException("negative receive size");
        }
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (isConnected()) {
            return;
        }
        this.sendBufferSize = i;
    }

    @Override // java.net.Socket
    public synchronized int getSendBufferSize() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        return this.sendBufferSize;
    }

    @Override // java.net.Socket
    public synchronized void setReceiveBufferSize(int i) throws SocketException {
        if (i <= 0) {
            throw new IllegalArgumentException("negative send size");
        }
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (isConnected()) {
            return;
        }
        this.receiveBufferSize = i;
    }

    @Override // java.net.Socket
    public synchronized int getReceiveBufferSize() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        return this.receiveBufferSize;
    }

    @Override // java.net.Socket
    public void setTcpNoDelay(boolean z) {
    }

    @Override // java.net.Socket
    public boolean getTcpNoDelay() {
        return false;
    }

    @Override // java.net.Socket
    public synchronized void setKeepAlive(boolean z) throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!(this.keepAliveEnabled ^ z)) {
            return;
        }
        this.keepAliveEnabled = z;
        if (isConnected()) {
            if (this.keepAliveEnabled) {
                this.keepAliveTimer.schedule(this.socketProfile.getNullSegmentTimeoutMs() * 6, this.socketProfile.getNullSegmentTimeoutMs() * 6);
            } else {
                this.keepAliveTimer.cancel();
            }
        }
    }

    @Override // java.net.Socket
    public synchronized boolean getKeepAlive() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        return this.keepAliveEnabled;
    }

    @Override // java.net.Socket
    public void shutdownInput() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected()) {
            throw new SocketException("Socket is not connected");
        }
        if (isInputShutdown()) {
            throw new SocketException("Socket input is already shutdown");
        }
        this.inputShutdown = true;
        synchronized (this.inputLock) {
            this.inputLock.notify();
        }
    }

    @Override // java.net.Socket
    public void shutdownOutput() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (!isConnected()) {
            throw new SocketException("Socket is not connected");
        }
        if (isOutputShutdown()) {
            throw new SocketException("Socket output is already shutdown");
        }
        this.outputShutdown = true;
        synchronized (this.unacknowledgedPackets) {
            this.unacknowledgedPackets.notifyAll();
        }
    }

    @Override // java.net.Socket
    public boolean isInputShutdown() {
        return this.inputShutdown;
    }

    @Override // java.net.Socket
    public boolean isOutputShutdown() {
        return this.outputShutdown;
    }

    /* JADX INFO: renamed from: a */
    protected void sendDatagramBytes(byte[] bArr, int i, int i2) throws IOException {
        sendDataBytes(bArr, i, i2, false);
    }

    /* JADX INFO: renamed from: a */
    public void sendDataBytes(byte[] bArr, int i, int i2, boolean z) throws IOException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (isOutputShutdown()) {
            throw new IOException("Socket output is shutdown");
        }
        if (!isConnected()) {
            throw new SocketException("Connection reset");
        }
        int i3 = 0;
        while (i3 < i2) {
            synchronized (this.sendLock) {
                while (this.outputFlowControlEnabled) {
                    try {
                        this.sendLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                int iMin = Math.min(this.socketProfile.getMaxSegmentSize() - 6, i2 - i3);
                DATSegment dATSegment = new DATSegment(this.connectionStats.nextSequenceToSendAndIncrement(), this.connectionStats.getLastSequenceReceived(), bArr, i + i3, iMin);
                closeImpl(dATSegment);
                if (z) {
                    sendPacket(dATSegment);
                }
                i3 += iMin;
            }
        }
    }


    /* JADX INFO: renamed from: b */
    protected int receiveDatagramBytes(byte[] arr, int integer2, int integer3) throws IOException {
        int var4 = 0;
        synchronized (this.inputLock) {
            while (true) {
                while (!this.acknowledgmentHistory.isEmpty()) {
                    Iterator var6 = this.acknowledgmentHistory.iterator();

                    while (true) {
                        if (var6.hasNext()) {
                            Segment var7 = (Segment) var6.next();
                            if (var7 instanceof RSTSegment) {
                                var6.remove();
                            } else if (var7 instanceof FINSegment) {
                                if (var4 <= 0) {
                                    var6.remove();
                                    return -1;
                                }
                            } else {
                                if (!(var7 instanceof DATSegment)) {
                                    continue;
                                }

                                byte[] var8 = ((DATSegment) var7).getPayloadBytes();
                                if (var8.length + var4 <= integer3) {
                                    System.arraycopy(var8, 0, arr, integer2 + var4, var8.length);
                                    var4 += var8.length;
                                    var6.remove();
                                    continue;
                                }

                                if (var4 <= 0) {
                                    throw new IOException("insufficient buffer space");
                                }
                            }
                        }

                        if (var4 > 0) {
                            return var4;
                        }
                        break;
                    }
                }

                if (this.isClosed()) {
                    throw new SocketException("Socket is closed");
                }

                if (this.isInputShutdown()) {
                    throw new EOFException();
                }

                if (!this.isConnected()) {
                    throw new SocketException("Connection reset");
                }

                try {
                    if (this.soTimeoutMs == 0) {
                        this.inputLock.wait();
                    } else {
                        long var12 = System.currentTimeMillis();
                        this.inputLock.wait(this.soTimeoutMs);
                        if (System.currentTimeMillis() - var12 >= this.soTimeoutMs) {
                            throw new SocketTimeoutException();
                        }
                    }
                } catch (InterruptedException var10) {
                    if (DEBUG) {
                        var10.printStackTrace();
                    }
                }
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void sendPacket(ReliableSocketListener reliableSocketListener) {
        if (reliableSocketListener == null) {
            throw new NullPointerException("stateListener");
        }
        synchronized (this.receivedPacketsLog) {
            if (!this.receivedPacketsLog.contains(reliableSocketListener)) {
                this.receivedPacketsLog.add(reliableSocketListener);
            }
        }
    }

    /* JADX INFO: renamed from: a */
    private void sendPacket(Segment segment) {
        if ((segment instanceof DATSegment) || (segment instanceof RSTSegment) || (segment instanceof FINSegment) || (segment instanceof NULSegment)) {
            handleRstPacket(segment);
        }
        if ((segment instanceof DATSegment) || (segment instanceof RSTSegment) || (segment instanceof FINSegment)) {
            this.nullSegmentTimer.pause();
        }
        if (DEBUG) {
            a("sent " + segment);
        }
        handleData(segment);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: i */
    public Segment dequeueIncomingPacketAndLog() {
        Segment segmentReceiveSegment = receiveSegment();
        if (segmentReceiveSegment != null) {
            if (DEBUG) {
                a("recv " + segmentReceiveSegment);
            }
            if ((segmentReceiveSegment instanceof DATSegment) || (segmentReceiveSegment instanceof NULSegment) || (segmentReceiveSegment instanceof RSTSegment) || (segmentReceiveSegment instanceof FINSegment) || (segmentReceiveSegment instanceof SYNSegment)) {
                this.connectionStats.incrementSentPackets();
            }
            if (this.keepAliveEnabled) {
                this.keepAliveTimer.pause();
            }
        }
        return segmentReceiveSegment;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: e */
    void closeImpl(final Segment h) throws SocketException {
        synchronized (this.unacknowledgedPackets) {
            while (this.unacknowledgedPackets.size() >= this.sendWindow || this.connectionStats.getDroppedPacketCount() > this.socketProfile.getMaxOutstandingSegments()) {
                if (this.closed) {
                    throw new SocketException("Socket is closed");
                }
                try {
                    this.unacknowledgedPackets.wait(10000L);
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            this.connectionStats.incrementDroppedPackets();
            this.unacknowledgedPackets.add(h);
        }
        if (this.closed) {
            throw new SocketException("Socket is closed");
        }
        if (!(h instanceof EAKSegment) && !(h instanceof ACKSegment)) {
            synchronized (this.retransmissionTimer) {
                if (this.retransmissionTimer.isNotScheduled()) {
                    this.retransmissionTimer.schedule(this.socketProfile.getRetransmissionTimeoutMs(), this.socketProfile.getRetransmissionTimeoutMs());
                }
            }
        }
        this.sendPacket(h);
        if (h instanceof DATSegment) {
            synchronized (this.sentPacketsLog) {
                final Iterator iterator = this.sentPacketsLog.iterator();
                while (iterator.hasNext()) {
                    ((ConnectionListener) iterator.next()).onConnected();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: f */
    public void startPacketTimers(Segment segment) {
        if (this.socketProfile.getMaxRetransmissions() > 0) {
            segment.setChannel(segment.getChannel() + 1);
        }
        if (this.socketProfile.getMaxRetransmissions() != 0 && segment.getChannel() > this.socketProfile.getMaxRetransmissions()) {
            retransmitPending();
            return;
        }
        sendPacket(segment);
        if (segment instanceof DATSegment) {
            synchronized (this.sentPacketsLog) {
                for (Object o : this.sentPacketsLog) {
                    ((ConnectionListener) o).onClosed();
                }
            }
        }
    }

    /* JADX INFO: renamed from: j */
    private void handleConnectionReset() {
        if (isConnected()) {
            this.nullSegmentTimer.cancel();
            if (this.keepAliveEnabled) {
                this.keepAliveTimer.cancel();
            }
            synchronized (this.sendLock) {
                this.outputFlowControlEnabled = false;
                this.sendLock.notify();
            }
        } else {
            synchronized (this) {
                startCongestionControlTimers();
                this.connected = true;
                this.connectionState = 3;
                notify();
            }
            synchronized (this.receivedPacketsLog) {
                Iterator it = this.receivedPacketsLog.iterator();
                while (it.hasNext()) {
                    ((ReliableSocketListener) it.next()).onConnected(this);
                }
            }
        }
        this.nullSegmentTimer.schedule(0L, this.socketProfile.getNullSegmentTimeoutMs());
        if (this.keepAliveEnabled) {
            this.keepAliveTimer.schedule(this.socketProfile.getNullSegmentTimeoutMs() * 6, this.socketProfile.getNullSegmentTimeoutMs() * 6);
        }
    }

    /* JADX INFO: renamed from: k */
    private void processConnectionRefused() {
        synchronized (this.receivedPacketsLog) {
            Iterator it = this.receivedPacketsLog.iterator();
            while (it.hasNext()) {
                ((ReliableSocketListener) it.next()).onConnectFailed(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: l */
    public void cleanupSession() {
        synchronized (this.receivedPacketsLog) {
            Iterator it = this.receivedPacketsLog.iterator();
            while (it.hasNext()) {
                ((ReliableSocketListener) it.next()).onClosed(this);
            }
        }
    }


    /* JADX INFO: renamed from: m */
    void retransmitPending() {
        synchronized (this.closeLock) {
            if (this.isClosed()) {
                return;
            }
            switch (this.connectionState) {
                case 2: {
                    synchronized (this) {
                        this.notify();
                    }
                    break;
                }
                case 1:
                case 3:
                case 4: {
                    this.connected = false;
                    synchronized (this.unacknowledgedPackets) {
                        this.unacknowledgedPackets.notifyAll();
                    }
                    synchronized (this.inputLock) {
                        this.inputLock.notify();
                    }
                    this.handleSynPacket();
                    break;
                }
            }
            this.connectionState = 0;
            this.closed = true;
        }
        synchronized (this.receivedPacketsLog) {
            final Iterator iterator = this.receivedPacketsLog.iterator();
            while (iterator.hasNext()) {
                ((ReliableSocketListener) iterator.next()).onConnectionLost(this);
            }
        }
    }

    /* JADX INFO: renamed from: n */
    private void notifyReceiveListeners() {
        synchronized (this.receivedPacketsLog) {
            Iterator it = this.receivedPacketsLog.iterator();
            while (it.hasNext()) {
                ((ReliableSocketListener) it.next()).onRemoteReset(this);
            }
        }
    }

    /* JADX INFO: renamed from: a */
    protected void handlePacket(final SYNSegment g) throws IOException {
        switch (this.connectionState) {
            case 0: {
                this.connectionState = 1;
                this.socketProfile = new ReliableSocketProfile(this.sendWindow, this.receiveWindow, g.getMaxSegmentSize(), g.getGameVersion(), g.getMaxRetransmissions(), g.getMaxCumulativeAcks(), g.getMaxOutOfOrderPackets(), g.getMaxAutoResets(), g.getNullPacketTimeoutMs(), g.getRetransmissionTimeoutMs(), g.getCumulativeAckTimeoutMs());
                this.connectionStats.setLastSequenceReceived(g.getSequenceNumber());
                final SYNSegment h = new SYNSegment(this.connectionStats.setNextSequenceToSend(new Random(System.currentTimeMillis()).nextInt(255)), this.socketProfile.getMaxOutstandingSegments(), this.socketProfile.getMaxSegmentSize(), this.socketProfile.getRetransmissionTimeoutMs(), this.socketProfile.getCumulativeAckTimeoutMs(), this.socketProfile.getNullSegmentTimeoutMs(), this.socketProfile.getMaxRetransmissions(), this.socketProfile.getMaxCumulativeAcks(), this.socketProfile.getMaxOutOfOrder(), this.socketProfile.getMaxAutoResets());
                h.setAcknowledgmentNumber(g.getSequenceNumber());
                this.closeImpl(h);
                break;
            }
            case 1: {
                synchronized (this.unacknowledgedPackets) {
                    for (final Segment h2 : this.unacknowledgedPackets) {
                        try {
                            this.startPacketTimers(h2);
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
            }
            case 2: {
                this.connectionStats.setLastSequenceReceived(g.getSequenceNumber());
                this.connectionState = 3;
                this.sendKeepAlivePacket();
                this.handleConnectionReset();
                break;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: a */
    public void log(EAKSegment eAKSegment) {
        int[] selectiveAckList = eAKSegment.getSelectiveAckList();
        int iN = eAKSegment.getAcknowledgmentNumber();
        int i = selectiveAckList[selectiveAckList.length - 1];
        synchronized (this.unacknowledgedPackets) {
            Iterator it = this.unacknowledgedPackets.iterator();
            while (it.hasNext()) {
                Segment segment = (Segment) it.next();
                if (readPacketData(segment.getSequenceNumber(), iN) <= 0) {
                    it.remove();
                } else {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= selectiveAckList.length) {
                            break;
                        }
                        if (readPacketData(segment.getSequenceNumber(), selectiveAckList[i2]) != 0) {
                            i2++;
                        } else {
                            it.remove();
                            break;
                        }
                    }
                }
            }
            for (Segment segment2 : this.unacknowledgedPackets) {
                if (readPacketData(iN, segment2.getSequenceNumber()) < 0 && readPacketData(i, segment2.getSequenceNumber()) > 0) {
                    try {
                        startPacketTimers(segment2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            this.unacknowledgedPackets.notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: g */
    void stopPacketTimers(final Segment h) {
        if (h instanceof RSTSegment) {
            synchronized (this.sendLock) {
                this.outputFlowControlEnabled = true;
            }
            this.notifyReceiveListeners();
        }
        if (h instanceof FINSegment) {
            switch (this.connectionState) {
                case 2: {
                    synchronized (this) {
                        this.notify();
                    }
                    break;
                }
                case 0: {
                    break;
                }
                default: {
                    this.connectionState = 4;
                    break;
                }
            }
        }
        boolean b = false;
        synchronized (this.inputLock) {
            if (this.readPacketData(h.getSequenceNumber(), this.connectionStats.getLastSequenceReceived()) > 0) {
                if (this.readPacketData(h.getSequenceNumber(), incrementSequenceModulo255(this.connectionStats.getLastSequenceReceived())) == 0) {
                    b = true;
                    if (this.acknowledgmentHistory.size() == 0 || this.acknowledgmentHistory.size() + this.outOfOrderPackets.size() < this.receiveWindow) {
                        this.connectionStats.setLastSequenceReceived(h.getSequenceNumber());
                        if (h instanceof DATSegment || h instanceof RSTSegment || h instanceof FINSegment) {
                            this.acknowledgmentHistory.add(h);
                        }
                        if (h instanceof DATSegment) {
                            synchronized (this.sentPacketsLog) {
                                final Iterator iterator = this.sentPacketsLog.iterator();
                                while (iterator.hasNext()) {
                                    ((ConnectionListener) iterator.next()).onPacketReceived();
                                }
                            }
                        }
                        this.processOutOfOrderPackets();
                    }
                } else if (this.acknowledgmentHistory.size() + this.outOfOrderPackets.size() < this.receiveWindow) {
                    int n = 0;
                    for (int n2 = 0; n2 < this.outOfOrderPackets.size() && n == 0; ++n2) {
                        final int packetData = this.readPacketData(h.getSequenceNumber(), ((Segment) this.outOfOrderPackets.get(n2)).getSequenceNumber());
                        if (packetData == 0) {
                            n = 1;
                        } else if (packetData < 0) {
                            this.outOfOrderPackets.add(n2, h);
                            n = 1;
                        }
                    }
                    if (n == 0) {
                        this.outOfOrderPackets.add(h);
                    }
                    this.connectionStats.incrementReceivedPackets();
                    if (h instanceof DATSegment) {
                        synchronized (this.sentPacketsLog) {
                            final Iterator iterator2 = this.sentPacketsLog.iterator();
                            while (iterator2.hasNext()) {
                                ((ConnectionListener) iterator2.next()).onPacketSent();
                            }
                        }
                    }
                }
            }
            if (b && (h instanceof RSTSegment || h instanceof NULSegment || h instanceof FINSegment)) {
                this.sendKeepAlivePacket();
            } else if (this.connectionStats.getReceivedPacketCount() > 0 && (this.socketProfile.getMaxOutOfOrder() == 0 || this.connectionStats.getReceivedPacketCount() > this.socketProfile.getMaxOutOfOrder())) {
                this.sendSelectiveAcknowledgment();
            } else if (this.connectionStats.getSentPacketCount() > 0 && (this.socketProfile.getMaxCumulativeAcks() == 0 || this.connectionStats.getSentPacketCount() > this.socketProfile.getMaxCumulativeAcks())) {
                this.sendCumulativeAckIfNeeded();
            } else {
                synchronized (this.cumulativeAckTimer) {
                    if (this.cumulativeAckTimer.isNotScheduled()) {
                        this.cumulativeAckTimer.scheduleAtFixedRate(this.socketProfile.getCumulativeAckTimeoutMs());
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: o */
    public void sendKeepAlivePacket() {
        synchronized (this.inputLock) {
            if (!this.outOfOrderPackets.isEmpty()) {
                sendSelectiveAcknowledgment();
            } else {
                sendCumulativeAckIfNeeded();
            }
        }
    }

    /* JADX INFO: renamed from: p */
    private void sendSelectiveAcknowledgment() {
        synchronized (this.inputLock) {
            if (this.outOfOrderPackets.isEmpty()) {
                return;
            }
            this.connectionStats.drainSentPacketCount();
            this.connectionStats.drainReceivedPacketCount();
            int[] iArr = new int[this.outOfOrderPackets.size()];
            for (int i = 0; i < iArr.length; i++) {
                iArr[i] = ((Segment) this.outOfOrderPackets.get(i)).getSequenceNumber();
            }
            try {
                int lastSequenceReceived = this.connectionStats.getLastSequenceReceived();
                sendPacket((Segment) new EAKSegment(incrementSequenceModulo255(lastSequenceReceived), lastSequenceReceived, iArr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: renamed from: q */
    private void sendCumulativeAckIfNeeded() {
        if (this.connectionStats.drainSentPacketCount() == 0) {
            return;
        }
        try {
            int lastSequenceReceived = this.connectionStats.getLastSequenceReceived();
            sendPacket(new ACKSegment(incrementSequenceModulo255(lastSequenceReceived), lastSequenceReceived));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: renamed from: h */
    private void handleRstPacket(Segment segment) {
        if (this.connectionStats.drainSentPacketCount() == 0) {
            return;
        }
        segment.setAcknowledgmentNumber(this.connectionStats.getLastSequenceReceived());
    }

    /* JADX INFO: renamed from: b */
    protected boolean isValidAcknowledgment(Segment segment) {
        int acknowledgmentNumber = segment.getAcknowledgmentNumber();
        if (acknowledgmentNumber < 0) {
            return false;
        }
        Iterator it = this.unacknowledgedPackets.iterator();
        while (it.hasNext()) {
            if (readPacketData(((Segment) it.next()).getSequenceNumber(), acknowledgmentNumber) <= 0) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: renamed from: c */
    protected void processPacket(Segment segment) {
        int acknowledgmentNumber = segment.getAcknowledgmentNumber();
        if (acknowledgmentNumber < 0) {
            return;
        }
        this.connectionStats.drainDroppedPacketCount();
        synchronized (this.unacknowledgedPackets) {
            Iterator it = this.unacknowledgedPackets.iterator();
            while (it.hasNext()) {
                if (readPacketData(((Segment) it.next()).getSequenceNumber(), acknowledgmentNumber) <= 0) {
                    it.remove();
                }
            }
            if (this.connectionState == 1) {
                boolean z = false;
                if (!this.unacknowledgedPackets.isEmpty()) {
                    Iterator it2 = this.unacknowledgedPackets.iterator();
                    while (it2.hasNext()) {
                        if (((Segment) it2.next()) instanceof SYNSegment) {
                            z = true;
                        }
                    }
                }
                if (z) {
                    a("Bad first ack: " + acknowledgmentNumber);
                    return;
                } else {
                    this.connectionState = 3;
                    handleConnectionReset();
                }
            }
            if (this.unacknowledgedPackets.isEmpty()) {
                this.retransmissionTimer.cancel();
            }
            this.unacknowledgedPackets.notifyAll();
        }
    }

    /* JADX INFO: renamed from: r */
    private void processOutOfOrderPackets() {
        synchronized (this.inputLock) {
            Iterator it = this.outOfOrderPackets.iterator();
            while (it.hasNext()) {
                Segment segment = (Segment) it.next();
                if (readPacketData(segment.getSequenceNumber(), incrementSequenceModulo255(this.connectionStats.getLastSequenceReceived())) == 0) {
                    this.connectionStats.setLastSequenceReceived(segment.getSequenceNumber());
                    if ((segment instanceof DATSegment) || (segment instanceof RSTSegment) || (segment instanceof FINSegment)) {
                        this.acknowledgmentHistory.add(segment);
                    }
                    it.remove();
                }
            }
            this.inputLock.notify();
        }
    }

    /* JADX INFO: renamed from: d */
    protected void handleData(Segment segment) {
        try {
            this.datagramSocket.send(new DatagramPacket(segment.encodePayload(), segment.length(), this.peerAddress));
        } catch (IOException e) {
            if (!isClosed()) {
                e.printStackTrace();
            }
        }
    }

    /* JADX INFO: renamed from: a */
    protected Segment receiveSegment() {
        try {
            if (this.receiveBuffer == null) {
                this.receiveBuffer = new byte[65535];
            }
            DatagramPacket datagramPacket = new DatagramPacket(this.receiveBuffer, this.receiveBuffer.length);
            this.datagramSocket.receive(datagramPacket);
            return Segment.decodeHeader(datagramPacket.getData(), 0, datagramPacket.getLength());
        } catch (IOException e) {
            if (!isClosed()) {
                e.printStackTrace();
                return null;
            }
            return null;
        }
    }

    /* JADX INFO: renamed from: b, reason: collision with other method in class */
    protected void mo1b() {
        this.datagramSocket.close();
    }

    /* JADX INFO: renamed from: e */
    protected void handleSynPacket() {
        this.nullSegmentTimer.cancel();
        this.keepAliveTimer.cancel();
        this.connectionState = 4;
        Thread thread = new Thread() { // from class: a.a.h.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                ReliableSocket.this.keepAliveTimer.stop();
                ReliableSocket.this.nullSegmentTimer.stop();
                try {
                    Thread.sleep(ReliableSocket.this.socketProfile.getNullSegmentTimeoutMs() * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ReliableSocket.this.retransmissionTimer.stop();
                ReliableSocket.this.cumulativeAckTimer.stop();
                ReliableSocket.this.mo1b();
                ReliableSocket.this.cleanupSession();
            }
        };
        thread.setName("ReliableSocket-Closing");
        thread.setDaemon(true);
        thread.start();
    }

    protected synchronized void a(String str) {
        System.out.println(getLocalPort() + ": " + str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: b */
    public static int incrementSequenceModulo255(int i) {
        return (i + 1) % 255;
    }

    /* JADX INFO: renamed from: a */
    private int readPacketData(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        if (i < i2 && i2 - i > 127) {
            return 1;
        }
        if (i > i2 && i - i2 < 127) {
            return 1;
        }
        return -1;
    }

    /* JADX INFO: renamed from: f */
    public synchronized void startCongestionControlTimers() {
        if (!this.isCongestionControlEnabled) {
            this.isCongestionControlEnabled = true;
            this.nullSegmentTimer.start();
            this.retransmissionTimer.start();
            this.cumulativeAckTimer.start();
            this.keepAliveTimer.start();
        }
    }

    /* JADX INFO: renamed from: g */
    public synchronized void stopCongestionControlTimers() {
        if (this.isCongestionControlEnabled) {
            this.isCongestionControlEnabled = false;
            this.retransmissionTimer.stop();
            this.cumulativeAckTimer.stop();
            this.keepAliveTimer.stop();
            this.nullSegmentTimer.stop();
        }
    }
}
