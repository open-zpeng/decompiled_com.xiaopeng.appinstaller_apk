package android.support.v7.app;
/* loaded from: classes.dex */
class TwilightCalculator {
    private static TwilightCalculator sInstance;
    public int state;
    public long sunrise;
    public long sunset;

    TwilightCalculator() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static TwilightCalculator getInstance() {
        if (sInstance == null) {
            sInstance = new TwilightCalculator();
        }
        return sInstance;
    }

    public void calculateTwilight(long time, double latitude, double longitude) {
        float daysSince2000 = ((float) (time - 946728000000L)) / 8.64E7f;
        float meanAnomaly = 6.24006f + (0.01720197f * daysSince2000);
        double trueAnomaly = meanAnomaly + (0.03341960161924362d * Math.sin(meanAnomaly)) + (3.4906598739326E-4d * Math.sin(2.0f * meanAnomaly)) + (5.236000106378924E-6d * Math.sin(3.0f * meanAnomaly));
        double solarLng = 1.796593063d + trueAnomaly + 3.141592653589793d;
        double arcLongitude = (-longitude) / 360.0d;
        float n = (float) Math.round((daysSince2000 - 9.0E-4f) - arcLongitude);
        double solarTransitJ2000 = 9.0E-4f + n + arcLongitude + (0.0053d * Math.sin(meanAnomaly)) + ((-0.0069d) * Math.sin(2.0d * solarLng));
        double solarDec = Math.asin(Math.sin(solarLng) * Math.sin(0.4092797040939331d));
        double latRad = 0.01745329238474369d * latitude;
        double cosHourAngle = (Math.sin(-0.10471975803375244d) - (Math.sin(latRad) * Math.sin(solarDec))) / (Math.cos(latRad) * Math.cos(solarDec));
        if (cosHourAngle >= 1.0d) {
            this.state = 1;
            this.sunset = -1L;
            this.sunrise = -1L;
        } else if (cosHourAngle <= -1.0d) {
            this.state = 0;
            this.sunset = -1L;
            this.sunrise = -1L;
        } else {
            float hourAngle = (float) (Math.acos(cosHourAngle) / 6.283185307179586d);
            this.sunset = Math.round((hourAngle + solarTransitJ2000) * 8.64E7d) + 946728000000L;
            this.sunrise = Math.round((solarTransitJ2000 - hourAngle) * 8.64E7d) + 946728000000L;
            if (this.sunrise < time && this.sunset > time) {
                this.state = 0;
            } else {
                this.state = 1;
            }
        }
    }
}
