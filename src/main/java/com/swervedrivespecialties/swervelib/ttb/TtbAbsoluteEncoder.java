package com.swervedrivespecialties.swervelib.ttb;

import com.swervedrivespecialties.swervelib.AbsoluteEncoder;
import edu.wpi.first.wpilibj.AnalogEncoder;

public class TtbAbsoluteEncoder implements AbsoluteEncoder {
    AnalogEncoder encoder;

    public TtbAbsoluteEncoder(TtbAbsoluteEncoderConfiguration config) {
        // Create a new AnalogEncoder attached to the analog port number from the config
        encoder = new AnalogEncoder(config.getChannel());

        // Set the offset so that zero can be defined as the wheel facing exactly forward
        // This must be a number between zero and one
        encoder.setPositionOffset(config.getOffset());

        // Tell the encoder that it moves 360 units per rotation so that absolute position will be in degrees
        //encoder.setDistancePerRotation(Math.PI*2);
    }

    @Override
    public double getAbsoluteAngle() {
        //double absoluteposition = encoder.getAbsolutePosition();
        double relativeangle = encoder.getAbsolutePosition() - encoder.getPositionOffset();
        double relativeanglefixed = relativeangle<0 ? 1+relativeangle : relativeangle;
        return (relativeanglefixed)*Math.PI*2;
    }
}
