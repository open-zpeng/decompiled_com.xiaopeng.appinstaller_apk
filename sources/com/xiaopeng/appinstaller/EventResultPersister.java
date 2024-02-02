package com.xiaopeng.appinstaller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AtomicFile;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class EventResultPersister {
    private static final String LOG_TAG = EventResultPersister.class.getSimpleName();
    private int mCounter;
    private boolean mIsPersistScheduled;
    private boolean mIsPersistingStateValid;
    private final AtomicFile mResultsFile;
    private final Object mLock = new Object();
    private final SparseArray<EventResult> mResults = new SparseArray<>();
    private final SparseArray<EventResultObserver> mObservers = new SparseArray<>();

    /* loaded from: classes.dex */
    interface EventResultObserver {
        void onResult(int i, int i2, String str);
    }

    public int getNewId() throws OutOfIdsException {
        int i;
        synchronized (this.mLock) {
            if (this.mCounter == Integer.MAX_VALUE) {
                throw new OutOfIdsException();
            }
            this.mCounter++;
            writeState();
            i = this.mCounter - 1;
        }
        return i;
    }

    private static void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                return;
            }
        } while (type != 1);
    }

    private static int readIntAttribute(XmlPullParser parser, String name) {
        return Integer.parseInt(parser.getAttributeValue(null, name));
    }

    private static String readStringAttribute(XmlPullParser parser, String name) {
        return parser.getAttributeValue(null, name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventResultPersister(File resultFile) {
        this.mResultsFile = new AtomicFile(resultFile);
        this.mCounter = -2147483647;
        try {
            FileInputStream stream = this.mResultsFile.openRead();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(stream, StandardCharsets.UTF_8.name());
            nextElement(parser);
            while (parser.getEventType() != 1) {
                String tagName = parser.getName();
                if ("results".equals(tagName)) {
                    this.mCounter = readIntAttribute(parser, "counter");
                } else if ("result".equals(tagName)) {
                    int id = readIntAttribute(parser, "id");
                    int status = readIntAttribute(parser, "status");
                    int legacyStatus = readIntAttribute(parser, "legacyStatus");
                    String statusMessage = readStringAttribute(parser, "statusMessage");
                    if (this.mResults.get(id) != null) {
                        throw new Exception("id " + id + " has two results");
                    }
                    this.mResults.put(id, new EventResult(status, legacyStatus, statusMessage));
                } else {
                    throw new Exception("unexpected tag");
                }
                nextElement(parser);
            }
            if (stream != null) {
                stream.close();
            }
        } catch (Exception e) {
            this.mResults.clear();
            writeState();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onEventReceived(Context context, Intent intent) {
        EventResultObserver observerToCall;
        int i = 0;
        int status = intent.getIntExtra("android.content.pm.extra.STATUS", 0);
        if (status == -1) {
            context.startActivity((Intent) intent.getParcelableExtra("android.intent.extra.INTENT"));
            return;
        }
        int id = intent.getIntExtra("EventResultPersister.EXTRA_ID", 0);
        String statusMessage = intent.getStringExtra("android.content.pm.extra.STATUS_MESSAGE");
        int legacyStatus = intent.getIntExtra("android.content.pm.extra.LEGACY_STATUS", 0);
        String str = LOG_TAG;
        Log.d(str, "onEventReceived id:" + id + "/status:" + status + "/legacyStatus:" + legacyStatus + "/statusMsg:" + statusMessage);
        EventResultObserver observerToCall2 = null;
        synchronized (this.mLock) {
            try {
                int numObservers = this.mObservers.size();
                while (true) {
                    if (i >= numObservers) {
                        break;
                    } else if (this.mObservers.keyAt(i) != id) {
                        i++;
                    } else {
                        observerToCall2 = this.mObservers.valueAt(i);
                        this.mObservers.removeAt(i);
                        break;
                    }
                }
                observerToCall = observerToCall2;
            } catch (Throwable th) {
                th = th;
            }
            try {
                if (observerToCall != null) {
                    observerToCall.onResult(status, legacyStatus, statusMessage);
                } else {
                    this.mResults.put(id, new EventResult(status, legacyStatus, statusMessage));
                    writeState();
                }
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    private void writeState() {
        synchronized (this.mLock) {
            this.mIsPersistingStateValid = false;
            if (!this.mIsPersistScheduled) {
                this.mIsPersistScheduled = true;
                AsyncTask.execute(new Runnable() { // from class: com.xiaopeng.appinstaller.-$$Lambda$EventResultPersister$eASzb3k8-Vi-x2I68XDVZ59zDoU
                    @Override // java.lang.Runnable
                    public final void run() {
                        EventResultPersister.lambda$writeState$0(EventResultPersister.this);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$writeState$0(EventResultPersister eventResultPersister) {
        int counter;
        SparseArray<EventResult> results;
        while (true) {
            synchronized (eventResultPersister.mLock) {
                counter = eventResultPersister.mCounter;
                results = eventResultPersister.mResults.clone();
                eventResultPersister.mIsPersistingStateValid = true;
            }
            FileOutputStream stream = null;
            try {
                stream = eventResultPersister.mResultsFile.startWrite();
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(stream, StandardCharsets.UTF_8.name());
                serializer.startDocument(null, true);
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                serializer.startTag(null, "results");
                serializer.attribute(null, "counter", Integer.toString(counter));
                int numResults = results.size();
                for (int i = 0; i < numResults; i++) {
                    serializer.startTag(null, "result");
                    serializer.attribute(null, "id", Integer.toString(results.keyAt(i)));
                    serializer.attribute(null, "status", Integer.toString(results.valueAt(i).status));
                    serializer.attribute(null, "legacyStatus", Integer.toString(results.valueAt(i).legacyStatus));
                    if (results.valueAt(i).message != null) {
                        serializer.attribute(null, "statusMessage", results.valueAt(i).message);
                    }
                    serializer.endTag(null, "result");
                }
                serializer.endTag(null, "results");
                serializer.endDocument();
                eventResultPersister.mResultsFile.finishWrite(stream);
            } catch (IOException e) {
                if (stream != null) {
                    eventResultPersister.mResultsFile.failWrite(stream);
                }
                Log.e(LOG_TAG, "error writing results", e);
                eventResultPersister.mResultsFile.delete();
            }
            synchronized (eventResultPersister.mLock) {
                if (eventResultPersister.mIsPersistingStateValid) {
                    eventResultPersister.mIsPersistScheduled = false;
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addObserver(int id, EventResultObserver observer) throws OutOfIdsException {
        synchronized (this.mLock) {
            int resultIndex = -1;
            try {
                if (id == Integer.MIN_VALUE) {
                    id = getNewId();
                } else {
                    resultIndex = this.mResults.indexOfKey(id);
                }
                if (resultIndex >= 0) {
                    EventResult result = this.mResults.valueAt(resultIndex);
                    observer.onResult(result.status, result.legacyStatus, result.message);
                    this.mResults.removeAt(resultIndex);
                    writeState();
                } else {
                    this.mObservers.put(id, observer);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeObserver(int id) {
        synchronized (this.mLock) {
            this.mObservers.delete(id);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EventResult {
        public final int legacyStatus;
        public final String message;
        public final int status;

        private EventResult(int status, int legacyStatus, String message) {
            this.status = status;
            this.legacyStatus = legacyStatus;
            this.message = message;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class OutOfIdsException extends Exception {
        OutOfIdsException() {
        }
    }
}
