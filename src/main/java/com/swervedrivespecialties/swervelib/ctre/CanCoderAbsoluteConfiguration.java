package com.swervedrivespecialties.swervelib.ctre;

import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.swervedrivespecialties.swervelib.AbsoluteEncoder;
import com.swervedrivespecialties.swervelib.AbsoluteEncoderConfiguration;

public class CanCoderAbsoluteConfiguration implements AbsoluteEncoderConfiguration<CanCoderAbsoluteConfiguration> {
    private final int id;
    private final double offset;
    private final String canbus;
    private final SensorInitializationStrategy initStrategy;
    private final int readingUpdatePeriodMS = 100;
    /**
     * Number of tries for getting correct position.
     */
    private final int attempts = 3; // TODO: Allow changing number of tries for getting correct position

    public CanCoderAbsoluteConfiguration(int id, double offset, String canbus, SensorInitializationStrategy initStrategy) {
        this.id = id;
        this.offset = offset;
        this.canbus = canbus;
        this.initStrategy = initStrategy;
    }

    public CanCoderAbsoluteConfiguration(int id, double offset, String canbus) {
        this(id, offset, canbus, SensorInitializationStrategy.BootToAbsolutePosition);
    }

    public CanCoderAbsoluteConfiguration(int id, double offset) {
        this(id, offset, "");
    }

    public int getId() {
        return id;
    }

    public double getOffset() {
        return offset;
    }

    public String getCanbus() {
        return canbus;
    }

    public int getReadingUpdatePeriodMS() {
        return readingUpdatePeriodMS;
    }

    public int getAttempts() {
        return attempts;
    }

    public SensorInitializationStrategy getInitStrategy() {
        return initStrategy;
    }

    @Override
    public AbsoluteEncoder create() {
        return new CanCoderFactoryBuilder().build().create(this);
    }
}
