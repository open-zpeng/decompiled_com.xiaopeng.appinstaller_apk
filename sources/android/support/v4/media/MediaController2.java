package android.support.v4.media;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaSession2;
import java.util.List;
@TargetApi(19)
/* loaded from: classes.dex */
public class MediaController2 implements AutoCloseable {
    private final SupportLibraryImpl mImpl;

    /* loaded from: classes.dex */
    interface SupportLibraryImpl extends AutoCloseable {
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        try {
            this.mImpl.close();
        } catch (Exception e) {
        }
    }

    /* loaded from: classes.dex */
    public static abstract class ControllerCallback {
        public void onConnected(MediaController2 controller, SessionCommandGroup2 allowedCommands) {
        }

        public void onDisconnected(MediaController2 controller) {
        }

        public void onCustomLayoutChanged(MediaController2 controller, List<MediaSession2.CommandButton> layout) {
        }

        public void onPlaybackInfoChanged(MediaController2 controller, PlaybackInfo info) {
        }

        public void onAllowedCommandsChanged(MediaController2 controller, SessionCommandGroup2 commands) {
        }

        public void onCustomCommand(MediaController2 controller, SessionCommand2 command, Bundle args, ResultReceiver receiver) {
        }

        public void onPlayerStateChanged(MediaController2 controller, int state) {
        }

        public void onPlaybackSpeedChanged(MediaController2 controller, float speed) {
        }

        public void onBufferingStateChanged(MediaController2 controller, MediaItem2 item, int state) {
        }

        public void onSeekCompleted(MediaController2 controller, long position) {
        }

        public void onError(MediaController2 controller, int errorCode, Bundle extras) {
        }

        public void onCurrentMediaItemChanged(MediaController2 controller, MediaItem2 item) {
        }

        public void onPlaylistChanged(MediaController2 controller, List<MediaItem2> list, MediaMetadata2 metadata) {
        }

        public void onPlaylistMetadataChanged(MediaController2 controller, MediaMetadata2 metadata) {
        }

        public void onShuffleModeChanged(MediaController2 controller, int shuffleMode) {
        }

        public void onRepeatModeChanged(MediaController2 controller, int repeatMode) {
        }

        public void onRoutesInfoChanged(MediaController2 controller, List<Bundle> routes) {
        }
    }

    /* loaded from: classes.dex */
    public static final class PlaybackInfo {
        private final AudioAttributesCompat mAudioAttrsCompat;
        private final int mControlType;
        private final int mCurrentVolume;
        private final int mMaxVolume;
        private final int mPlaybackType;

        PlaybackInfo(int playbackType, AudioAttributesCompat attrs, int controlType, int max, int current) {
            this.mPlaybackType = playbackType;
            this.mAudioAttrsCompat = attrs;
            this.mControlType = controlType;
            this.mMaxVolume = max;
            this.mCurrentVolume = current;
        }

        static PlaybackInfo createPlaybackInfo(int playbackType, AudioAttributesCompat attrs, int controlType, int max, int current) {
            return new PlaybackInfo(playbackType, attrs, controlType, max, current);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static PlaybackInfo fromBundle(Bundle bundle) {
            if (bundle == null) {
                return null;
            }
            int volumeType = bundle.getInt("android.media.audio_info.playback_type");
            int volumeControl = bundle.getInt("android.media.audio_info.control_type");
            int maxVolume = bundle.getInt("android.media.audio_info.max_volume");
            int currentVolume = bundle.getInt("android.media.audio_info.current_volume");
            AudioAttributesCompat attrs = AudioAttributesCompat.fromBundle(bundle.getBundle("android.media.audio_info.audio_attrs"));
            return createPlaybackInfo(volumeType, attrs, volumeControl, maxVolume, currentVolume);
        }
    }
}
