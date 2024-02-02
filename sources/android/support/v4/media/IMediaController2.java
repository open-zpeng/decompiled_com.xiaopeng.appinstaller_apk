package android.support.v4.media;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.support.v4.media.IMediaSession2;
import java.util.List;
/* loaded from: classes.dex */
public interface IMediaController2 extends IInterface {
    void onAllowedCommandsChanged(Bundle bundle) throws RemoteException;

    void onBufferingStateChanged(Bundle bundle, int i, long j) throws RemoteException;

    void onChildrenChanged(String str, int i, Bundle bundle) throws RemoteException;

    void onConnected(IMediaSession2 iMediaSession2, Bundle bundle, int i, Bundle bundle2, long j, long j2, float f, long j3, Bundle bundle3, int i2, int i3, List<Bundle> list, PendingIntent pendingIntent) throws RemoteException;

    void onCurrentMediaItemChanged(Bundle bundle) throws RemoteException;

    void onCustomCommand(Bundle bundle, Bundle bundle2, ResultReceiver resultReceiver) throws RemoteException;

    void onCustomLayoutChanged(List<Bundle> list) throws RemoteException;

    void onDisconnected() throws RemoteException;

    void onError(int i, Bundle bundle) throws RemoteException;

    void onGetChildrenDone(String str, int i, int i2, List<Bundle> list, Bundle bundle) throws RemoteException;

    void onGetItemDone(String str, Bundle bundle) throws RemoteException;

    void onGetLibraryRootDone(Bundle bundle, String str, Bundle bundle2) throws RemoteException;

    void onGetSearchResultDone(String str, int i, int i2, List<Bundle> list, Bundle bundle) throws RemoteException;

    void onPlaybackInfoChanged(Bundle bundle) throws RemoteException;

    void onPlaybackSpeedChanged(long j, long j2, float f) throws RemoteException;

    void onPlayerStateChanged(long j, long j2, int i) throws RemoteException;

    void onPlaylistChanged(List<Bundle> list, Bundle bundle) throws RemoteException;

    void onPlaylistMetadataChanged(Bundle bundle) throws RemoteException;

    void onRepeatModeChanged(int i) throws RemoteException;

    void onRoutesInfoChanged(List<Bundle> list) throws RemoteException;

    void onSearchResultChanged(String str, int i, Bundle bundle) throws RemoteException;

    void onSeekCompleted(long j, long j2, long j3) throws RemoteException;

    void onShuffleModeChanged(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IMediaController2 {
        public static IMediaController2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("android.support.v4.media.IMediaController2");
            if (iin != null && (iin instanceof IMediaController2)) {
                return (IMediaController2) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg1;
            Bundle _arg3;
            Bundle _arg8;
            Bundle _arg0;
            Bundle _arg12;
            Bundle _arg02;
            if (code == 1598968902) {
                reply.writeString("android.support.v4.media.IMediaController2");
                return true;
            }
            switch (code) {
                case 1:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    Bundle _arg03 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onCurrentMediaItemChanged(_arg03);
                    return true;
                case 2:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    long _arg04 = data.readLong();
                    long _arg13 = data.readLong();
                    int _arg2 = data.readInt();
                    onPlayerStateChanged(_arg04, _arg13, _arg2);
                    return true;
                case 3:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    long _arg05 = data.readLong();
                    long _arg14 = data.readLong();
                    float _arg22 = data.readFloat();
                    onPlaybackSpeedChanged(_arg05, _arg14, _arg22);
                    return true;
                case 4:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    Bundle _arg06 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    int _arg15 = data.readInt();
                    long _arg23 = data.readLong();
                    onBufferingStateChanged(_arg06, _arg15, _arg23);
                    return true;
                case 5:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    List<Bundle> _arg07 = data.createTypedArrayList(Bundle.CREATOR);
                    Bundle _arg16 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onPlaylistChanged(_arg07, _arg16);
                    return true;
                case 6:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    Bundle _arg08 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onPlaylistMetadataChanged(_arg08);
                    return true;
                case 7:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    Bundle _arg09 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onPlaybackInfoChanged(_arg09);
                    return true;
                case 8:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    int _arg010 = data.readInt();
                    onRepeatModeChanged(_arg010);
                    return true;
                case 9:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    int _arg011 = data.readInt();
                    onShuffleModeChanged(_arg011);
                    return true;
                case 10:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    long _arg012 = data.readLong();
                    long _arg17 = data.readLong();
                    long _arg24 = data.readLong();
                    onSeekCompleted(_arg012, _arg17, _arg24);
                    return true;
                case 11:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    int _arg013 = data.readInt();
                    Bundle _arg18 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onError(_arg013, _arg18);
                    return true;
                case 12:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    List<Bundle> _arg014 = data.createTypedArrayList(Bundle.CREATOR);
                    onRoutesInfoChanged(_arg014);
                    return true;
                case 13:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    IMediaSession2 _arg015 = IMediaSession2.Stub.asInterface(data.readStrongBinder());
                    if (data.readInt() != 0) {
                        Bundle _arg19 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                        _arg1 = _arg19;
                    } else {
                        _arg1 = null;
                    }
                    int _arg25 = data.readInt();
                    if (data.readInt() != 0) {
                        Bundle _arg32 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                        _arg3 = _arg32;
                    } else {
                        _arg3 = null;
                    }
                    long _arg4 = data.readLong();
                    long _arg5 = data.readLong();
                    float _arg6 = data.readFloat();
                    long _arg7 = data.readLong();
                    if (data.readInt() != 0) {
                        Bundle _arg82 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                        _arg8 = _arg82;
                    } else {
                        _arg8 = null;
                    }
                    int _arg9 = data.readInt();
                    int _arg10 = data.readInt();
                    List<Bundle> _arg11 = data.createTypedArrayList(Bundle.CREATOR);
                    PendingIntent _arg122 = data.readInt() != 0 ? (PendingIntent) PendingIntent.CREATOR.createFromParcel(data) : null;
                    onConnected(_arg015, _arg1, _arg25, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9, _arg10, _arg11, _arg122);
                    return true;
                case 14:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    onDisconnected();
                    return true;
                case 15:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    List<Bundle> _arg016 = data.createTypedArrayList(Bundle.CREATOR);
                    onCustomLayoutChanged(_arg016);
                    return true;
                case 16:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    Bundle _arg017 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onAllowedCommandsChanged(_arg017);
                    return true;
                case 17:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg12 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    ResultReceiver _arg26 = data.readInt() != 0 ? (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data) : null;
                    onCustomCommand(_arg0, _arg12, _arg26);
                    return true;
                case 18:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    if (data.readInt() != 0) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    String _arg110 = data.readString();
                    Bundle _arg27 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onGetLibraryRootDone(_arg02, _arg110, _arg27);
                    return true;
                case 19:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    String _arg018 = data.readString();
                    Bundle _arg111 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onGetItemDone(_arg018, _arg111);
                    return true;
                case 20:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    String _arg019 = data.readString();
                    int _arg112 = data.readInt();
                    Bundle _arg28 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onChildrenChanged(_arg019, _arg112, _arg28);
                    return true;
                case 21:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    String _arg020 = data.readString();
                    int _arg113 = data.readInt();
                    int _arg29 = data.readInt();
                    List<Bundle> _arg33 = data.createTypedArrayList(Bundle.CREATOR);
                    Bundle _arg42 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onGetChildrenDone(_arg020, _arg113, _arg29, _arg33, _arg42);
                    return true;
                case 22:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    String _arg021 = data.readString();
                    int _arg114 = data.readInt();
                    Bundle _arg210 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onSearchResultChanged(_arg021, _arg114, _arg210);
                    return true;
                case 23:
                    data.enforceInterface("android.support.v4.media.IMediaController2");
                    String _arg022 = data.readString();
                    int _arg115 = data.readInt();
                    int _arg211 = data.readInt();
                    List<Bundle> _arg34 = data.createTypedArrayList(Bundle.CREATOR);
                    Bundle _arg43 = data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null;
                    onGetSearchResultDone(_arg022, _arg115, _arg211, _arg34, _arg43);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IMediaController2 {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.support.v4.media.IMediaController2
            public void onCurrentMediaItemChanged(Bundle item) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (item != null) {
                        _data.writeInt(1);
                        item.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onPlayerStateChanged(long eventTimeMs, long positionMs, int state) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeLong(eventTimeMs);
                    _data.writeLong(positionMs);
                    _data.writeInt(state);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onPlaybackSpeedChanged(long eventTimeMs, long positionMs, float speed) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeLong(eventTimeMs);
                    _data.writeLong(positionMs);
                    _data.writeFloat(speed);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onBufferingStateChanged(Bundle item, int state, long bufferedPositionMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (item != null) {
                        _data.writeInt(1);
                        item.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(state);
                    _data.writeLong(bufferedPositionMs);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onPlaylistChanged(List<Bundle> playlist, Bundle metadata) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeTypedList(playlist);
                    if (metadata != null) {
                        _data.writeInt(1);
                        metadata.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onPlaylistMetadataChanged(Bundle metadata) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (metadata != null) {
                        _data.writeInt(1);
                        metadata.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onPlaybackInfoChanged(Bundle playbackInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (playbackInfo != null) {
                        _data.writeInt(1);
                        playbackInfo.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onRepeatModeChanged(int repeatMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeInt(repeatMode);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onShuffleModeChanged(int shuffleMode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeInt(shuffleMode);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onSeekCompleted(long eventTimeMs, long positionMs, long seekPositionMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeLong(eventTimeMs);
                    _data.writeLong(positionMs);
                    _data.writeLong(seekPositionMs);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onError(int errorCode, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeInt(errorCode);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onRoutesInfoChanged(List<Bundle> routes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeTypedList(routes);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onConnected(IMediaSession2 sessionBinder, Bundle commandGroup, int playerState, Bundle currentItem, long positionEventTimeMs, long positionMs, float playbackSpeed, long bufferedPositionMs, Bundle playbackInfo, int repeatMode, int shuffleMode, List<Bundle> playlist, PendingIntent sessionActivity) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeStrongBinder(sessionBinder != null ? sessionBinder.asBinder() : null);
                    if (commandGroup != null) {
                        _data.writeInt(1);
                        commandGroup.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(playerState);
                    if (currentItem != null) {
                        _data.writeInt(1);
                        currentItem.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    try {
                        _data.writeLong(positionEventTimeMs);
                        try {
                            _data.writeLong(positionMs);
                            try {
                                _data.writeFloat(playbackSpeed);
                                try {
                                    _data.writeLong(bufferedPositionMs);
                                    if (playbackInfo != null) {
                                        _data.writeInt(1);
                                        playbackInfo.writeToParcel(_data, 0);
                                    } else {
                                        _data.writeInt(0);
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th2) {
                                th = th2;
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th5) {
                    th = th5;
                }
                try {
                    _data.writeInt(repeatMode);
                    try {
                        _data.writeInt(shuffleMode);
                        _data.writeTypedList(playlist);
                        if (sessionActivity != null) {
                            _data.writeInt(1);
                            sessionActivity.writeToParcel(_data, 0);
                        } else {
                            _data.writeInt(0);
                        }
                        this.mRemote.transact(13, _data, null, 1);
                        _data.recycle();
                    } catch (Throwable th6) {
                        th = th6;
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                    _data.recycle();
                    throw th;
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onDisconnected() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onCustomLayoutChanged(List<Bundle> commandButtonlist) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeTypedList(commandButtonlist);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onAllowedCommandsChanged(Bundle commands) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (commands != null) {
                        _data.writeInt(1);
                        commands.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onCustomCommand(Bundle command, Bundle args, ResultReceiver receiver) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (command != null) {
                        _data.writeInt(1);
                        command.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (receiver != null) {
                        _data.writeInt(1);
                        receiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onGetLibraryRootDone(Bundle rootHints, String rootMediaId, Bundle rootExtra) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    if (rootHints != null) {
                        _data.writeInt(1);
                        rootHints.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(rootMediaId);
                    if (rootExtra != null) {
                        _data.writeInt(1);
                        rootExtra.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onGetItemDone(String mediaId, Bundle result) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeString(mediaId);
                    if (result != null) {
                        _data.writeInt(1);
                        result.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onChildrenChanged(String parentId, int itemCount, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeString(parentId);
                    _data.writeInt(itemCount);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onGetChildrenDone(String parentId, int page, int pageSize, List<Bundle> result, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeString(parentId);
                    _data.writeInt(page);
                    _data.writeInt(pageSize);
                    _data.writeTypedList(result);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onSearchResultChanged(String query, int itemCount, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeString(query);
                    _data.writeInt(itemCount);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.support.v4.media.IMediaController2
            public void onGetSearchResultDone(String query, int page, int pageSize, List<Bundle> result, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("android.support.v4.media.IMediaController2");
                    _data.writeString(query);
                    _data.writeInt(page);
                    _data.writeInt(pageSize);
                    _data.writeTypedList(result);
                    if (extras != null) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}
