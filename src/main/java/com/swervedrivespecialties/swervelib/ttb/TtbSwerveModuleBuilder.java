package com.swervedrivespecialties.swervelib.ttb;

import com.swervedrivespecialties.swervelib.DriveControllerFactory;
import com.swervedrivespecialties.swervelib.MechanicalConfiguration;
import com.swervedrivespecialties.swervelib.MkModuleConfiguration;
import com.swervedrivespecialties.swervelib.MotorType;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SteerConfiguration;
import com.swervedrivespecialties.swervelib.SteerControllerFactory;
import com.swervedrivespecialties.swervelib.SwerveModule;
import com.swervedrivespecialties.swervelib.SwerveModuleFactory;
import com.swervedrivespecialties.swervelib.rev.*;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class TtbSwerveModuleBuilder {

    private static DriveControllerFactory<?, Integer> getNeoDriveFactory(MkModuleConfiguration configuration) {
        return new NeoDriveControllerFactoryBuilder()
                .withVoltageCompensation(configuration.getNominalVoltage())
                .withCurrentLimit(configuration.getDriveCurrentLimit())
                .build();
    }

    private static SteerControllerFactory<?, SteerConfiguration<TtbAbsoluteEncoderConfiguration>> getNeoSteerFactory(MkModuleConfiguration configuration) {
        return new NeoSteerControllerFactoryBuilder()
                .withVoltageCompensation(configuration.getNominalVoltage())
                .withPidConstants(configuration.getSteerKP(), configuration.getSteerKI(), configuration.getSteerKD())
                .withCurrentLimit(configuration.getSteerCurrentLimit())
                .build(new TtbEncoderFactoryBuilder()
                        .build());
    }

    private final MkModuleConfiguration configuration;
    private final boolean useDefaultSteerConfiguration;
    private ShuffleboardLayout container = null;
    private MechanicalConfiguration mechConfig = null;

    private DriveControllerFactory<?, Integer> driveFactory = null;
    private SteerControllerFactory<?, SteerConfiguration<TtbAbsoluteEncoderConfiguration>> steerFactory = null;

    private int driveMotorPort = -1;
    private String driveCanbus = "";
    private int steerMotorPort = -1;
    private String steerCanbus = "";

    private MotorType steerMotorType;
    private int steerEncoderPort = -1;
    private double steerOffset = 0;

    /**
     * Creates a new swerve module builder with the default values.
     */
    public TtbSwerveModuleBuilder() {
        this.configuration = new MkModuleConfiguration();
        this.useDefaultSteerConfiguration = true;
    }

    /**
     * Creates a new swerve module builder.
     * <p>
     * Recommended values to pass in are
     * {@link MkModuleConfiguration#getDefaultSteerFalcon500()} or
     * {@link MkModuleConfiguration#getDefaultSteerNEO()}, but you can use any custom module
     * values by instantiating a new {@link MkModuleConfiguration}.
     * 
     * @param configuration configured values for the module
     */
    public TtbSwerveModuleBuilder(MkModuleConfiguration configuration) {
        this.configuration = configuration;
        this.useDefaultSteerConfiguration = false;
    }

    /**
     * (Optional) Specify a shuffleboard container in which to print debug values
     * for the swerve module.
     * 
     * @param container the shuffleboard container
     * @return the builder
     */
    public TtbSwerveModuleBuilder withLayout(ShuffleboardLayout container) {
        this.container = container;
        return this;
    }

    /**
     * Specify a gear ratio to use with this swerve module.
     * <p>
     * Presets for SDS modules can be found in {@link SdsModuleConfigurations}.
     * 
     * @param mechConfig the swerve module's mechanical configuration
     * @return the builder
     */
    public TtbSwerveModuleBuilder withGearRatio(MechanicalConfiguration mechConfig) {
        this.mechConfig = mechConfig;
        return this;
    }

    /**
     * Specify details about the drive motor.
     * 
     * @param motorType the {@link MotorType} of the motor
     * @param motorPort the CAN ID of the motor
     * @param motorCanbus the canbus of the motor, "" for the roboRIO bus
     * @return the builder
     */
    public TtbSwerveModuleBuilder withDriveMotor(MotorType motorType, int motorPort, String motorCanbus) {
        switch (motorType) {
            case NEO:
                this.driveFactory = getNeoDriveFactory(this.configuration);
                break;
            default:
                break;
        }
        this.driveMotorPort = motorPort;
        this.driveCanbus = motorCanbus;
        return this;
    }

    /**
     * Specify details about the drive motor. The drive motor must be on the
     * roboRIO canbus.
     * 
     * @param motorType the {@link MotorType} of the motor
     * @param motorPort the CAN ID of the motor
     * @return the builder
     */
    public TtbSwerveModuleBuilder withDriveMotor(MotorType motorType, int motorPort) {
        return this.withDriveMotor(motorType, motorPort, "");
    }

    /**
     * Specify details about the steer motor.
     * 
     * @param motorType the {@link MotorType} of the motor
     * @param motorPort the CAN ID of the motor
     * @param motorCanbus the canbus of the motor, "" for the roboRIO bus
     * @return the builder
     */
    public TtbSwerveModuleBuilder withSteerMotor(MotorType motorType, int motorPort, String motorCanbus) {
        switch (motorType) {
            case NEO:
                if (this.useDefaultSteerConfiguration)
                    this.steerFactory = getNeoSteerFactory(MkModuleConfiguration.getDefaultSteerNEO());
                else
                    this.steerFactory = getNeoSteerFactory(this.configuration);
                break;
            default:
                break;
        }
        this.steerMotorType = motorType;
        this.steerMotorPort = motorPort;
        this.steerCanbus = motorCanbus;
        return this;
    }

    /**
     * Specify details about the steer motor. The steer motor must be on the
     * roboRIO canbus.
     * 
     * @param motorType the {@link MotorType} of the motor
     * @param motorPort the CAN ID of the motor
     * @return the builder
     */
    public TtbSwerveModuleBuilder withSteerMotor(MotorType motorType, int motorPort) {
        return this.withSteerMotor(motorType, motorPort, "");
    }

    /**
     * Specify details about the module's absolute encoder.
     * 
     * @param encoderPort the CAN ID of the encoder
     * @return the builder
     */
    public TtbSwerveModuleBuilder withSteerEncoderPort(int encoderPort) {
        this.steerEncoderPort = encoderPort;
        return this;
    }

    /**
     * Specify the module's absolute encoder offset.
     * 
     * @param offset the offset, in radians, to apply to the absolute encoder's position
     * @return the builder
     */
    public TtbSwerveModuleBuilder withSteerOffset(double offset) {
        this.steerOffset = offset;
        return this;
    }

    /**
     * Build the created swerve module.
     * 
     * @return the built swerve module
     */
    public SwerveModule build() {
        if (mechConfig == null) {
            throw new RuntimeException("Mechanical Config should not be null!");
        }

        if (driveFactory == null) {
            throw new RuntimeException("Drive Motor should not be null!");
        }

        if (steerFactory == null) {
            throw new RuntimeException("Steer Motor should not be null!");
        }

        if (driveMotorPort < 0) {
            throw new RuntimeException("Drive Motor Port should be greater than 0!");
        }

        if (steerMotorPort < 0) {
            throw new RuntimeException("Steer Motor Port should be greater than 0!");
        }

        if (steerEncoderPort < 0) {
            throw new RuntimeException("Steer Encoder Port should be greater than 0!");
        }

        SwerveModuleFactory<Integer, SteerConfiguration<TtbAbsoluteEncoderConfiguration>> factory = new SwerveModuleFactory<>(
                mechConfig, 
                driveFactory, 
                steerFactory
        );

        SteerConfiguration<TtbAbsoluteEncoderConfiguration> steerConfig;

        if (steerMotorType == MotorType.NEO) {
            steerConfig = new SteerConfiguration<>(
                    steerMotorPort, 
                    new TtbAbsoluteEncoderConfiguration(
                            steerEncoderPort, 
                            steerOffset
                    )
            );
        } else {
            throw new RuntimeException("Steer Motor Type should not be null!");
        }

        if (container == null) {
            return factory.create(
                    driveMotorPort, 
                    driveCanbus, 
                    steerConfig, 
                    steerCanbus
            );
        } else {
            return factory.create(
                    container, 
                    driveMotorPort, 
                    driveCanbus, 
                    steerConfig, 
                    steerCanbus
            );
        }
    }
}
