package com.swervedrivespecialties.swervelib;

/**
 * Configuration for an analog absolute encoder connected to an analog input on
 * the roboRIO.
 */
public class AnalogAbsoluteEncoderConfiguration implements AbsoluteEncoderConfiguration<AnalogAbsoluteEncoderConfiguration> {

    /**
     * Analog input channel number on the roboRIO.
     */
    private final int channel;

    /**
     * Analog position offset added to the absolute position in order to calculate angles relative to
     * the wheel facing straight forward.
     */
    private final double offset;

    public AnalogAbsoluteEncoderConfiguration(int channel, double offset) {
        this.channel = channel;
        this.offset = offset;
    }

    public int getChannel() {
        return channel;
    }

    public double getOffset() {
        return offset;
    }

    @Override
    public AbsoluteEncoder create() {
        return new AnalogAbsoluteEncoderFactoryBuilder().build().create(this);
    }
}
