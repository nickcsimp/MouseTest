// A singleton enum that stores the app settings
// To retrieve this enum you use Settings.INSTANCE.getInstance()
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