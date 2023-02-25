package com.swervedrivespecialties.swervelib.ttb;

import com.swervedrivespecialties.swervelib.AbsoluteEncoderFactory;

public class TtbEncoderFactoryBuilder {

    public AbsoluteEncoderFactory<TtbAbsoluteEncoderConfiguration> build() {
        return configuration -> {
            return new TtbAbsoluteEncoder(configuration);
        };
    }

    public enum Direction {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }
}
