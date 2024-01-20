package com.swervedrivespecialties.swervelib;

import com.swervedrivespecialties.swervelib.rev.*;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class MkSwerveModuleBuilder {

    private static DriveControllerFactory<?, Integer> getNeoDriveFactory(MkModuleConfiguration configuration) {
        return new NeoDriveControllerFactoryBuilder()
                .withVoltageCompensation(configuration.getNominalVoltage())
                .withCurrentLimit(configuration.getDriveCurrentLimit())
                .build();
    }

    private static SteerControllerFactory<?, SteerConfiguration> getNeoSteerFactory(MkModuleConfiguration configuration) {
        return new NeoSteerControllerFactoryBuilder()
                .withVoltageCompensation(configuration.getNominalVoltage())
                .withPidConstants(configuration.getSteerKP(), configuration.getSteerKI(), configuration.getSteerKD())
                .withCurrentLimit(configuration.getSteerCurrentLimit())
                .build();
    }

    private final MkModuleConfiguration configuration;
    private final boolean useDefaultSteerConfiguration;
    private ShuffleboardLayout container = null;
    private MechanicalConfiguration mechConfig = null;

    private DriveControllerFactory<?, Integer> driveFactory = null;
    private SteerControllerFactory<?, SteerConfiguration> steerFactory = null;

    private int driveMotorPort = -1;
    private String driveCanbus = "";
    private int steerMotorPort = -1;
    private String steerCanbus = "";

    private MotorType steerMotorType;
    private EncoderType steerEncoderType = EncoderType.CANCoder;
    private int steerEncoderPort = -1;
    private double steerOffset = 0;

    /**
     * Creates a new swerve module builder with the default values.
     */
    public MkSwerveModuleBuilder() {
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
    public MkSwerveModuleBuilder(MkModuleConfiguration configuration) {
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
    public MkSwerveModuleBuilder withLayout(ShuffleboardLayout container) {
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
    public MkSwerveModuleBuilder withGearRatio(MechanicalConfiguration mechConfig) {
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
    public MkSwerveModuleBuilder withDriveMotor(MotorType motorType, int motorPort, String motorCanbus) {
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
    public MkSwerveModuleBuilder withDriveMotor(MotorType motorType, int motorPort) {
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
    public MkSwerveModuleBuilder withSteerMotor(MotorType motorType, int motorPort, String motorCanbus) {
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
    public MkSwerveModuleBuilder withSteerMotor(MotorType motorType, int motorPort) {
        return this.withSteerMotor(motorType, motorPort, "");
    }

    /**
     * Specify details about the module's absolute encoder. Tells the module to
     * get the steer angle via a CANCoder. 
     * 
     * @param encoderPort the CAN ID of the encoder
     * @param canbus the canbus of the encoder, "" for the roboRIO bus
     * @return the builder
     */
    public MkSwerveModuleBuilder withSteerEncoderPort(int encoderPort, String canbus) {
        this.steerEncoderType = EncoderType.CANCoder;
        this.steerEncoderPort = encoderPort;
        //this.steerEncoderCanbus = canbus;
        return this;
    }

    /**
     * Specify details about the module's absolute encoder. The encoder must 
     * be on the roboRIO canbus. Tells the module to get the steer angle via a CANCoder. 
     * 
     * @param encoderPort the CAN ID of the encoer
     * @return the builder
     */
    public MkSwerveModuleBuilder withSteerEncoderPort(int encoderPort) {
        return this.withSteerEncoderPort(encoderPort, "");
    }

    /**
     * Specify details about the module's absolute encoder. Tells the module
     * to get the steer angle via an encoder connected to an analog channel.
     * 
     * @param encoderChannel roboRIO analog channel that the steer encoder is connected to.
     * @return the builder
     */
    public MkSwerveModuleBuilder withSteerEncoderAnalogChannel(int encoderChannel) {
        this.steerEncoderType = EncoderType.Analog;
        this.steerEncoderPort = encoderChannel;
        return this;
    }

    /**
     * Specify the module's absolute encoder offset.
     * 
     * @param offset the offset, in radians, to apply to the absolute encoder's position
     * @return the builder
     */
    public MkSwerveModuleBuilder withSteerOffset(double offset) {
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

        SwerveModuleFactory<Integer, SteerConfiguration> factory = new SwerveModuleFactory<>(
                mechConfig, 
                driveFactory, 
                steerFactory
        );

        AbsoluteEncoderConfiguration<?> encoderConfig;

        if (steerEncoderType == EncoderType.Analog) {
            encoderConfig = new AnalogAbsoluteEncoderConfiguration(
                steerEncoderPort,
                steerOffset
            );
        } else {
            throw new RuntimeException("Steer Encoder Type should not be null!");
        }

        SteerConfiguration steerConfig;

        if (steerMotorType == MotorType.NEO) {
            steerConfig = new SteerConfiguration(
                    steerMotorPort, 
                    encoderConfig
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
