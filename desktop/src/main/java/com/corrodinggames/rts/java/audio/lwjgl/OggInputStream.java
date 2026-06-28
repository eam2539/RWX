package com.corrodinggames.rts.java.audio.lwjgl;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.utility.SlickToAndroidKeycodes;
import com.corrodinggames.rts.java.audio.util.AudioException;
import com.corrodinggames.rts.java.audio.util.StreamUtils;
import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/java/audio/lwjgl/OggInputStream.class */
public class OggInputStream extends InputStream {
    private static final int BUFFER_SIZE = 512;
    private int convsize;
    private byte[] convbuffer;
    private InputStream input;
    private Info oggInfo;
    private boolean endOfStream;
    private SyncState syncState;
    private StreamState streamState;
    private Page page;
    private Packet packet;
    private Comment comment;
    private DspState dspState;
    private Block vorbisBlock;
    byte[] buffer;
    int bytes;
    boolean bigEndian;
    boolean endOfBitStream;
    boolean inited;
    private int readIndex;
    private byte[] outBuffer;
    private int outIndex;
    private int total;

    public OggInputStream(InputStream inputStream) {
        this(inputStream, null);
    }

    public OggInputStream(InputStream inputStream, OggInputStream oggInputStream) {
        this.convsize = 2048;
        this.oggInfo = new Info();
        this.syncState = new SyncState();
        this.streamState = new StreamState();
        this.page = new Page();
        this.packet = new Packet();
        this.comment = new Comment();
        this.dspState = new DspState();
        this.vorbisBlock = new Block(this.dspState);
        this.bytes = 0;
        this.bigEndian = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN);
        this.endOfBitStream = true;
        this.inited = false;
        if (oggInputStream == null) {
            this.convbuffer = new byte[this.convsize];
            this.outBuffer = new byte[2048000];
        } else {
            this.convbuffer = oggInputStream.convbuffer;
            this.outBuffer = oggInputStream.outBuffer;
        }
        this.input = inputStream;
        try {
            this.total = inputStream.available();
            init();
        } catch (IOException e) {
            throw new AudioException(e);
        }
    }

    public int getLength() {
        return this.total;
    }

    public int getChannels() {
        return this.oggInfo.channels;
    }

    public int getSampleRate() {
        return this.oggInfo.rate;
    }

    private void init() {
        initVorbis();
        readPCM();
    }

    @Override // java.io.InputStream
    public int available() {
        return this.endOfStream ? 0 : 1;
    }

    private void initVorbis() {
        this.syncState.init();
    }

    private boolean getPageAndPacket() {
        int iPageout;
        int iPacketout;
        int iBuffer = this.syncState.buffer(BUFFER_SIZE);
        if (iBuffer == -1) {
            return false;
        }
        this.buffer = this.syncState.data;
        if (this.buffer == null) {
            this.endOfStream = true;
            return false;
        }
        try {
            this.bytes = this.input.read(this.buffer, iBuffer, BUFFER_SIZE);
            this.syncState.wrote(this.bytes);
            if (this.syncState.pageout(this.page) != 1) {
                if (this.bytes < BUFFER_SIZE) {
                    return false;
                }
                throw new AudioException("Input does not appear to FastArrayList an Ogg bitstream.");
            }
            this.streamState.init(this.page.serialno());
            this.oggInfo.init();
            this.comment.init();
            if (this.streamState.pagein(this.page) < 0) {
                throw new AudioException("Error reading first page of Ogg bitstream.");
            }
            if (this.streamState.packetout(this.packet) != 1) {
                throw new AudioException("Error reading initial header packet.");
            }
            if (this.oggInfo.synthesis_headerin(this.comment, this.packet) < 0) {
                throw new AudioException("Ogg bitstream does not contain Vorbis audio data.");
            }
            int i = 0;
            while (i < 2) {
                while (i < 2 && (iPageout = this.syncState.pageout(this.page)) != 0) {
                    if (iPageout == 1) {
                        this.streamState.pagein(this.page);
                        while (i < 2 && (iPacketout = this.streamState.packetout(this.packet)) != 0) {
                            if (iPacketout == -1) {
                                throw new AudioException("Corrupt secondary header.");
                            }
                            this.oggInfo.synthesis_headerin(this.comment, this.packet);
                            i++;
                        }
                    }
                }
                int iBuffer2 = this.syncState.buffer(BUFFER_SIZE);
                if (iBuffer2 == -1) {
                    return false;
                }
                this.buffer = this.syncState.data;
                try {
                    this.bytes = this.input.read(this.buffer, iBuffer2, BUFFER_SIZE);
                    if (this.bytes == 0 && i < 2) {
                        throw new AudioException("End of file before finding all Vorbis headers.");
                    }
                    this.syncState.wrote(this.bytes);
                } catch (Exception e) {
                    throw new AudioException("Failed to read Vorbis.", e);
                }
            }
            this.convsize = BUFFER_SIZE / this.oggInfo.channels;
            this.dspState.synthesis_init(this.oggInfo);
            this.vorbisBlock.init(this.dspState);
            return true;
        } catch (Exception e2) {
            throw new AudioException("Failure reading Vorbis.", e2);
        }
    }


    private void readPCM() {
        boolean var1 = false;

        while (true) {
            if (this.endOfBitStream) {
                if (!this.getPageAndPacket()) {
                    this.syncState.clear();
                    this.endOfStream = true;
                    return;
                }

                this.endOfBitStream = false;
            }

            if (!this.inited) {
                this.inited = true;
                return;
            }

            float[][][] var2 = new float[1][][];
            int[] var3 = new int[this.oggInfo.channels];

            while (!this.endOfBitStream) {
                while (!this.endOfBitStream) {
                    int var4 = this.syncState.pageout(this.page);
                    if (var4 == 0) {
                        break;
                    }

                    if (var4 == -1) {
                        GameEngine.log("gdx-audio", "Error reading OGG: Corrupt or missing data in bitstream.");
                    } else {
                        this.streamState.pagein(this.page);

                        while (true) {
                            var4 = this.streamState.packetout(this.packet);
                            if (var4 == 0) {
                                if (this.page.eos() != 0) {
                                    this.endOfBitStream = true;
                                }

                                if (!this.endOfBitStream && var1) {
                                    return;
                                }
                                break;
                            }

                            if (var4 != -1) {
                                if (this.vorbisBlock.synthesis(this.packet) == 0) {
                                    this.dspState.synthesis_blockin(this.vorbisBlock);
                                }

                                int var5;
                                while ((var5 = this.dspState.synthesis_pcmout(var2, var3)) > 0) {
                                    float[][] var6 = var2[0];
                                    int var7 = var5 < this.convsize ? var5 : this.convsize;

                                    for (int var8 = 0; var8 < this.oggInfo.channels; var8++) {
                                        int var9 = var8 * 2;
                                        int var10 = var3[var8];

                                        for (int var11 = 0; var11 < var7; var11++) {
                                            int var12 = (int) (var6[var8][var10 + var11] * 32767.0);
                                            if (var12 > 32767) {
                                                var12 = 32767;
                                            }

                                            if (var12 < -32768) {
                                                var12 = -32768;
                                            }

                                            if (var12 < 0) {
                                                var12 |= 32768;
                                            }

                                            if (this.bigEndian) {
                                                this.convbuffer[var9] = (byte) (var12 >>> 8);
                                                this.convbuffer[var9 + 1] = (byte) var12;
                                            } else {
                                                this.convbuffer[var9] = (byte) var12;
                                                this.convbuffer[var9 + 1] = (byte) (var12 >>> 8);
                                            }

                                            var9 += 2 * this.oggInfo.channels;
                                        }
                                    }

                                    int var16 = 2 * this.oggInfo.channels * var7;
                                    if (this.outIndex + var16 > this.outBuffer.length) {
                                        throw new AudioException("Ogg block too big to FastArrayList buffered: " + var16 + ", " + (this.outBuffer.length - this.outIndex));
                                    }

                                    System.arraycopy(this.convbuffer, 0, this.outBuffer, this.outIndex, var16);
                                    this.outIndex += var16;
                                    var1 = true;
                                    this.dspState.synthesis_read(var7);
                                }
                            }
                        }
                    }
                }

                if (!this.endOfBitStream) {
                    this.bytes = 0;
                    int var15 = this.syncState.buffer(512);
                    if (var15 >= 0) {
                        this.buffer = this.syncState.data;

                        try {
                            this.bytes = this.input.read(this.buffer, var15, 512);
                        } catch (Exception var13) {
                            throw new AudioException("Error during Vorbis decoding.", var13);
                        }
                    } else {
                        this.bytes = 0;
                    }

                    this.syncState.wrote(this.bytes);
                    if (this.bytes == 0) {
                        this.endOfBitStream = true;
                    }
                }
            }

            this.streamState.clear();
            this.vorbisBlock.clear();
            this.dspState.clear();
            this.oggInfo.clear();
        }
    }

    @Override // java.io.InputStream
    public int read() {
        if (this.readIndex >= this.outIndex) {
            this.outIndex = 0;
            readPCM();
            this.readIndex = 0;
            if (this.outIndex == 0) {
                return -1;
            }
        }
        int i = this.outBuffer[this.readIndex];
        if (i < 0) {
            i = SlickToAndroidKeycodes.AndroidCodes.KEYCODE_TV_CONTENTS_MENU + i;
        }
        this.readIndex++;
        return i;
    }

    public boolean atEnd() {
        return this.endOfStream && this.readIndex >= this.outIndex;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = read();
            if (i4 >= 0) {
                bArr[i3] = (byte) i4;
            } else {
                if (i3 == 0) {
                    return -1;
                }
                return i3;
            }
        }
        return i2;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) {
        return read(bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        StreamUtils.a(this.input);
    }
}
