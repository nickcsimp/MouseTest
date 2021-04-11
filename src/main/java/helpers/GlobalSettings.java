package helpers;

// A Singleton enum that stores the app settings
// To get a setting you go helpers.GlobalSettings.INSTANCE.getSamplingFrequency() (or getWhateverOtherSetting())
public enum GlobalSettings {

    INSTANCE();

    private final int samplingFrequency;
    private int outlierMin;
    private int outlierMax;
    private boolean localised;
    private int timeLimit;

    GlobalSettings() {
        this.samplingFrequency = 4;
        this.outlierMin = 0;
        this.outlierMax = 1000;
        this.localised = true;
        this.timeLimit = 20;
    }

    public int getSamplingFrequency() {
        return this.samplingFrequency;
    }

    public int getOutlierMin() {
        return this.outlierMin;
    }

    public int getOutlierMax() {
        return this.outlierMax;
    }

    public void setOutlierMin(int outlierMin) {
        this.outlierMin = outlierMin;
    }

    public void setOutlierMax(int outlierMax) {
        this.outlierMax = outlierMax;
    }

    public boolean isLocalised() {
        return localised;
    }

    public void setLocalised(boolean localised) {
        this.localised = localised;
    }

    public int getTimeLimitNumSamples() {
        return timeLimit;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimit = timeLimitSeconds*samplingFrequency;
    }
}