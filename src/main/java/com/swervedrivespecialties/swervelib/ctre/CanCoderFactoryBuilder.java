package com.swervedrivespecialties.swervelib.ctre;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import com.swervedrivespecialties.swervelib.AbsoluteEncoder;
import com.swervedrivespecialties.swervelib.AbsoluteEncoderFactory;

public class CanCoderFactoryBuilder {
    private Direction direction = Direction.COUNTER_CLOCKWISE;

    public CanCoderFactoryBuilder withDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public AbsoluteEncoderFactory<CanCoderAbsoluteConfiguration> build() {
        return configuration -> {
            CANCoderConfiguration config = new CANCoderConfiguration();
            config.absoluteSensorRange = AbsoluteSensorRange.Unsigned_0_to_360;
            config.magnetOffsetDegrees = Math.toDegrees(configuration.getOffset());
            config.sensorDirection = direction == Direction.CLOCKWISE;
            config.initializationStrategy = configuration.getInitStrategy();

            WPI_CANCoder encoder = new WPI_CANCoder(configuration.getId(), configuration.getCanbus());
            CtreUtils.checkCtreError(encoder.configAllSettings(config, 250), "Failed to configure CANCoder");

            CtreUtils.checkCtreError(encoder.setStatusFramePeriod(CANCoderStatusFrame.SensorData, configuration.getReadingUpdatePeriodMS(), 250), "Failed to configure CANCoder update rate");

            return new EncoderImplementation(encoder, configuration.getAttempts());
        };
    }

    private static class EncoderImplementation implements AbsoluteEncoder {
        private final int attempts; 

        private final WPI_CANCoder encoder;

        private EncoderImplementation(WPI_CANCoder encoder, int attempts) {
            this.encoder = encoder;
            this.attempts = attempts;
        }

        @Override
        public double getAbsoluteAngle() {
            double angle = Math.toRadians(encoder.getPosition());

            ErrorCode code = encoder.getLastError();

            for (int i = 0; i < attempts; i++) {
                if (code == ErrorCode.OK) break;
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }
                angle = Math.toRadians(encoder.getPosition());
                code = encoder.getLastError();
            }

            CtreUtils.checkCtreError(code, "Failed to retrieve CANcoder "+encoder.getDeviceID()+" absolute position after "+attempts+" tries");

            angle %= 2.0 * Math.PI;
            if (angle < 0.0) {
                angle += 2.0 * Math.PI;
            }

            return angle;
        }

        @Override
        public Object getInternal() {
            return this.encoder;
        }
    }

    public enum Direction {
        CLOCKWISE,
        COUNTER_CLOCKWISE
    }
}
