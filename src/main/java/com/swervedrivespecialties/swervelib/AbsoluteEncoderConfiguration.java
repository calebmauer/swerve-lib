package com.swervedrivespecialties.swervelib;

public interface AbsoluteEncoderConfiguration<EncoderConfigurationType> {
    /**
     * Gets a new AbsoluteEncoder created using this configuration.
     *
     * @return A new AbsoluteEncoder.
     */
    AbsoluteEncoder create();
}
