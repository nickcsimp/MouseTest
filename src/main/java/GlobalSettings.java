// A Singleton enum that stores the app settings
// To get a setting you go GlobalSettings.INSTANCE.getSamplingFrequency() (or getWhateverOtherSetting())
public enum GlobalSettings {

    INSTANCE(4);

    private int samplingFrequency;

    private GlobalSettings(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    public void setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    public int getSamplingFrequency() {
        return this.samplingFrequency;
    }
}