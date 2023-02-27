package com.swervedrivespecialties.swervelib;

import edu.wpi.first.wpilibj.AnalogEncoder;

public class AnalogAbsoluteEncoderFactoryBuilder {
    public AbsoluteEncoderFactory<AnalogAbsoluteEncoderConfiguration> build() {
        return configuration -> {
            // Create a new AnalogEncoder attached to the analog port number from the config
            AnalogEncoder encoder = new AnalogEncoder(configuration.getChannel());

            // Set the offset so that zero can be defined as the wheel facing exactly forward
            // This must be a number between zero and one
            encoder.setPositionOffset(configuration.getOffset());

            return new EncoderImplementation(encoder);
        };
    }

    private static class EncoderImplementation implements AbsoluteEncoder {

        AnalogEncoder encoder;

        public EncoderImplementation(AnalogEncoder encoder) {
            this.encoder = encoder;
        }
    
        @Override
        public double getAbsoluteAngle() {
            // Add offset to position to get angle relative to the wheel facing straight forward
            double relativeAngle = encoder.getAbsolutePosition() - encoder.getPositionOffset();

            // If the previous number was negative, add 1 to get a positive angle
            double relativeAngleFixed = relativeAngle < 0 ? 1 + relativeAngle : relativeAngle;

            // Return encoder position in radians
            return relativeAngleFixed * Math.PI * 2;
        }

        @Override
        public Object getInternal() {
            return this.encoder;
        }
    }
}
