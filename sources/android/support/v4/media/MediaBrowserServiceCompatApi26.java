package android.support.v4.media;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.os.Parcel;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserServiceCompatApi23;
import android.util.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
class MediaBrowserServiceCompatApi26 {
    private static Field sResultFlags;

    /* loaded from: classes.dex */
    public interface ServiceCompatProxy extends MediaBrowserServiceCompatApi23.ServiceCompatProxy {
        void onLoadChildren(String str, ResultWrapper resultWrapper, Bundle bundle);
    }

    static {
        try {
            sResultFlags = MediaBrowserService.Result.class.getDeclaredField("mFlags");
            sResultFlags.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Log.w("MBSCompatApi26", e);
        }
    }

    public static Object createService(Context context, ServiceCompatProxy serviceProxy) {
        return new MediaBrowserServiceAdaptor(context, serviceProxy);
    }

    /* loaded from: classes.dex */
    static class ResultWrapper {
        MediaBrowserService.Result mResultObj;

        ResultWrapper(MediaBrowserService.Result result) {
            this.mResultObj = result;
        }

        public void sendResult(List<Parcel> result, int flags) {
            try {
                MediaBrowserServiceCompatApi26.sResultFlags.setInt(this.mResultObj, flags);
            } catch (IllegalAccessException e) {
                Log.w("MBSCompatApi26", e);
            }
            this.mResultObj.sendResult(parcelListToItemList(result));
        }

        List<MediaBrowser.MediaItem> parcelListToItemList(List<Parcel> parcelList) {
            if (parcelList == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (Parcel parcel : parcelList) {
                parcel.setDataPosition(0);
                arrayList.add(MediaBrowser.MediaItem.CREATOR.createFromParcel(parcel));
                parcel.recycle();
            }
            return arrayList;
        }
    }

    /* loaded from: classes.dex */
    static class MediaBrowserServiceAdaptor extends MediaBrowserServiceCompatApi23.MediaBrowserServiceAdaptor {
        MediaBrowserServiceAdaptor(Context context, ServiceCompatProxy serviceWrapper) {
            super(context, serviceWrapper);
        }

        @Override // android.service.media.MediaBrowserService
        public void onLoadChildren(String parentId, MediaBrowserService.Result<List<MediaBrowser.MediaItem>> result, Bundle options) {
            ((ServiceCompatProxy) this.mServiceProxy).onLoadChildren(parentId, new ResultWrapper(result), options);
        }
    }
}
