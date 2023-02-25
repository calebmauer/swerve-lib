package com.swervedrivespecialties.swervelib.ttb;

public class TtbAbsoluteEncoderConfiguration {

    /**
     * Analog input channel number on the roboRIO.
     */
    private final int channel;
    private final double offset;

    public TtbAbsoluteEncoderConfiguration(int channel, double offset) {
        this.channel = channel;
        this.offset = offset;
    }

    public int getChannel() {
        return channel;
    }

    public double getOffset() {
        return offset;
    }
}
