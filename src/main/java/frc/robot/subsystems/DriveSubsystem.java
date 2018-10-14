package frc.robot.subsystems;

import java.text.DecimalFormat;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.commands.ArcadeDriveLookUp;


public class DriveSubsystem extends Subsystem implements PIDOutput {

	private AHRS navxgyro;
	private PIDController pidTurnController;

	public double PIDOutput;

	static final double kP = Constants.DriveSubsystem_kP;
	static final double kI = Constants.DriveSubsystem_kI;
	static final double kD = Constants.DriveSubsystem_kD;

    static final double kToleranceDegrees = Constants.DriveSubsystem_kToleranceDegrees;

    private TalonSRX driveLeftMaster;
	private TalonSRX driveLeftFollowerA;
	private TalonSRX driveLeftFollowerB;
	
	private TalonSRX driveRightMaster;
	private TalonSRX driveRightFollowerA;
    private TalonSRX driveRightFollowerB;

	private static final double deadBand = Constants.DriveSubsystem_deadBand;

    private DecimalFormat df = new DecimalFormat("#.##");
	//follows (x*.9)^2
	private double[] turnLookUp = new double[]{	 0
												,0.000081
												,0.000324
												,0.000729
												,0.001296
												,0.002025
												,0.002916
												,0.003969
												,0.005184
												,0.006561
												,0.0081
												,0.009801
												,0.011664
												,0.013689
												,0.015876
												,0.018225
												,0.020736
												,0.023409
												,0.026244
												,0.029241
												,0.0324
												,0.035721
												,0.039204
												,0.042849
												,0.046656
												,0.050625
												,0.054756
												,0.059049
												,0.063504
												,0.068121
												,0.0729
												,0.077841
												,0.082944
												,0.088209
												,0.093636
												,0.099225
												,0.104976
												,0.110889
												,0.116964
												,0.123201
												,0.1296
												,0.136161
												,0.142884
												,0.149769
												,0.156816
												,0.164025
												,0.171396
												,0.178929
												,0.186624
												,0.194481
												,0.2025
												,0.210681
												,0.219024
												,0.227529
												,0.236196
												,0.245025
												,0.254016
												,0.263169
												,0.272484
												,0.281961
												,0.2916
												,0.301401
												,0.311364
												,0.321489
												,0.331776
												,0.342225
												,0.352836
												,0.363609
												,0.374544
												,0.385641
												,0.3969
												,0.408321
												,0.419904
												,0.431649
												,0.443556
												,0.455625
												,0.467856
												,0.480249
												,0.492804
												,0.505521
												,0.5184
												,0.531441
												,0.544644
												,0.558009
												,0.571536
												,0.585225
												,0.599076
												,0.613089
												,0.627264
												,0.641601
												,0.6561
												,0.670761
												,0.685584
												,0.700569
												,0.715716
												,0.731025
												,0.746496
												,0.762129
												,0.777924
												,0.793881
												,0.81};

    public DriveSubsystem(){

        driveLeftMaster = new TalonSRX(RobotMap.driveLeftMaster);
		driveLeftFollowerA = new TalonSRX(RobotMap.driveLeftFollowerA);
		driveLeftFollowerB = new TalonSRX(RobotMap.driveLeftFollowerB);
		driveRightMaster = new TalonSRX(RobotMap.driveRightMaster);
		driveRightFollowerA = new TalonSRX(RobotMap.driveRightFollowerA);
		driveRightFollowerB = new TalonSRX(RobotMap.driveRightFollowerB);
		
		/** Feedback Sensor Configuration */
		
		/* Configure the left Talon's selected sensor to a QuadEncoder*/
		driveLeftMaster.configSelectedFeedbackSensor(	FeedbackDevice.QuadEncoder,					// Local Feedback Source
														Constants.PID_PRIMARY,						// PID Slot for Source [0, 1]
														Constants.kTimeoutMs);						// Configuration Timeout

		/* Configure the Remote Talon's selected sensor as a remote sensor for the right Talon */
		driveRightMaster.configRemoteFeedbackFilter(	driveLeftMaster.getDeviceID(),				// Device ID of Source
														RemoteSensorSource.TalonSRX_SelectedSensor,	// Remote Feedback Source
														Constants.REMOTE_0,							// Source number [0, 1]
														Constants.kTimeoutMs);						// Configuration Timeout

														/* Setup Sum signal to be used for Distance */
		driveRightMaster.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, Constants.kTimeoutMs);				// Feedback Device of Remote Talon
		driveRightMaster.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kTimeoutMs);	// Quadrature Encoder of current Talon

		/* Configure Sum [Sum of both QuadEncoders] to be used for Primary PID Index */
		driveRightMaster.configSelectedFeedbackSensor(	FeedbackDevice.SensorSum, 
														Constants.PID_PRIMARY,
														Constants.kTimeoutMs);

		/* Scale Feedback by 0.5 to half the sum of Distance */
		driveRightMaster.configSelectedFeedbackCoefficient(	0.5, 						// Coefficient
															Constants.PID_PRIMARY,		// PID Slot of Source 
															Constants.kTimeoutMs);		// Configuration Timeout

		/* Configure output and sensor direction */
		driveRightMaster.setSensorPhase(true);
		driveLeftMaster.setInverted(true);
		driveLeftFollowerA.setInverted(true);
		driveLeftFollowerB.setInverted(true);
		
		driveLeftFollowerA.follow(driveLeftMaster);
		driveLeftFollowerB.follow(driveLeftMaster);
		
		driveRightFollowerA.follow(driveRightMaster);
		driveRightFollowerB.follow(driveRightMaster);

		/* Set status frame periods to ensure we don't have stale data */
		driveRightMaster.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, Constants.kTimeoutMs);
		driveRightMaster.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, Constants.kTimeoutMs);
		driveLeftMaster.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, Constants.kTimeoutMs);

		/* Configure neutral deadband */
		driveRightMaster.configNeutralDeadband(Constants.kNeutralDeadband, Constants.kTimeoutMs);
		driveLeftMaster.configNeutralDeadband(Constants.kNeutralDeadband, Constants.kTimeoutMs);
		
		/* Motion Magic Configurations */
		driveRightMaster.configMotionAcceleration(1000, Constants.kTimeoutMs); //changed from 2000
		driveRightMaster.configMotionCruiseVelocity(2000, Constants.kTimeoutMs); // changed from 2000

		/* Max out the peak output (for all modes).  
		 * However you can limit the output of a given PID object with configClosedLoopPeakOutput().
		 */
		driveLeftMaster.configPeakOutputForward(+1.0, Constants.kTimeoutMs);
		driveLeftMaster.configPeakOutputReverse(-1.0, Constants.kTimeoutMs);
		driveRightMaster.configPeakOutputForward(+1.0, Constants.kTimeoutMs);
		driveRightMaster.configPeakOutputReverse(-1.0, Constants.kTimeoutMs);

		/* FPID Gains for Motion Magic servo */
		driveRightMaster.config_kP(Constants.kSlot_Distanc, Constants.kGains_Distanc.kP, Constants.kTimeoutMs);
		driveRightMaster.config_kI(Constants.kSlot_Distanc, Constants.kGains_Distanc.kI, Constants.kTimeoutMs);
		driveRightMaster.config_kD(Constants.kSlot_Distanc, Constants.kGains_Distanc.kD, Constants.kTimeoutMs);
		driveRightMaster.config_kF(Constants.kSlot_Distanc, Constants.kGains_Distanc.kF, Constants.kTimeoutMs);
		driveRightMaster.config_IntegralZone(Constants.kSlot_Distanc, (int)Constants.kGains_Distanc.kIzone, Constants.kTimeoutMs);
		driveRightMaster.configClosedLoopPeakOutput(Constants.kSlot_Distanc, Constants.kGains_Distanc.kPeakOutput, Constants.kTimeoutMs);
			
		/* 1ms per loop.  PID loop can be slowed down if need be.
		 * For example,
		 * - if sensor updates are too slow
		 * - sensor deltas are very small per update, so derivative error never gets large enough to be useful.
		 * - sensor movement is very slow causing the derivative error to be near zero.
		 */
		int closedLoopTimeMs = 1;
		driveRightMaster.configSetParameter(ParamEnum.ePIDLoopPeriod, closedLoopTimeMs, 0x00, 0, Constants.kTimeoutMs);
		driveRightMaster.configSetParameter(ParamEnum.ePIDLoopPeriod, closedLoopTimeMs, 0x00, 1, Constants.kTimeoutMs);

		/* configAuxPIDPolarity(boolean invert, int timeoutMs)
		 * false means talon's local output is PID0 + PID1, and other side Talon is PID0 - PID1
		 * true means talon's local output is PID0 - PID1, and other side Talon is PID0 + PID1
		 */
		driveRightMaster.configAuxPIDPolarity(false, Constants.kTimeoutMs);

		try { 
			navxgyro = new AHRS(SPI.Port.kMXP); // setting the navx to the mxp port 
		} 
		catch (RuntimeException ex )  // catching if an error was called.
		{
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true); // sending a message to the driver station telling that the navx is not working.
		}

		navxgyro.zeroYaw();

		zeroEncoders();

		pidTurnController = new PIDController(kP, kI, kD, navxgyro, this);
		pidTurnController.disable();
		pidTurnController.setInputRange(-180.0f, 180.0f);
		pidTurnController.setOutputRange(-1.0, 1.0);
		pidTurnController.setAbsoluteTolerance(kToleranceDegrees);
		pidTurnController.setContinuous(true); 

    }

    public void tankDrive(double left, double right) {
    	left = left*Math.abs(left);
    	right = right*Math.abs(right);
    	move(left,right);
    }

    public void arcadeDrive(double yAxis, double xAxis) {
    	//square the values for better control at low speeds
    	yAxis = yAxis*Math.abs(yAxis);
    	xAxis = xAxis*Math.abs(xAxis);
    
    	if((yAxis< deadBand) && (yAxis > -deadBand)){ yAxis=0;}
    	if((xAxis< deadBand) && (xAxis > -deadBand)){ xAxis=0;}
    	double L = yAxis + xAxis;
    	double R = yAxis - xAxis;
    	double max = Math.abs(L);
    	if(Math.abs(R) > max) max = Math.abs(R);
    	if((Math.abs(yAxis) <= 1) && (Math.abs(xAxis) <= 1) && (max < 1)){
      	move(L,R);
    	}else
    		move(L/max, R/max);
    }

     public void arcadeDriveLookUp(double yAxis, double xAxisCurve) {	 
		double x = Math.abs(xAxisCurve);
		//square the values for better control at low speeds
		yAxis = yAxis*Math.abs(yAxis);
		double xAxis = turnLookUp[(int)(Double.valueOf(df.format(x))*100)];
		if(xAxisCurve > 0){
			xAxis = -xAxis;
		}
		if((yAxis < deadBand) && (yAxis > -deadBand)){ yAxis=0;}
    	if((xAxis < deadBand) && (xAxis > -deadBand)){ xAxis=0;}
    	double L = yAxis + xAxis;
    	double R = yAxis - xAxis;
    	double max = Math.abs(L);
    	if(Math.abs(R) > max) max = Math.abs(R);
    	if((Math.abs(yAxis) <= 1) && (Math.abs(xAxis) <= 1) && (max < 1)){
    		move(L,R);
    	}else
    		move(L/max, R/max);
    }

    public void move(double leftPower, double rightPower) {
        driveLeftMaster.set(ControlMode.PercentOutput, leftPower);
        driveRightMaster.set(ControlMode.PercentOutput, rightPower);
	}

	public void motionMagic(double forward, double turn) {
		/* Determine which slot affects which PID */
		driveRightMaster.selectProfileSlot(Constants.kSlot_Distanc, Constants.PID_PRIMARY);

		/* calculate targets from gamepad inputs */
		double target_sensorUnits = forward * Constants.kSensorUnitsPerRotation * Constants.kRotationsToTravel;
		double feedFwdTerm = turn * 0.25; /* how much to add to the close loop output */
		
		driveRightMaster.set(ControlMode.MotionMagic, target_sensorUnits, DemandType.ArbitraryFeedForward, feedFwdTerm);
		driveLeftMaster.follow(driveRightMaster);

	}
	
	public void pidTurnControllerChangeState(String state) {
		if (state == "Enable") {
    		pidTurnController.enable();
    	}
    	else if (state == "Disable") {
			pidTurnController.disable();
		}
	}

	public void pidSetPoint(double input) {
		pidTurnController.setSetpoint(input);
	}

	public boolean pidDone() {
    	if(Math.abs(Math.abs(pidTurnController.getSetpoint()) - Math.abs(navxgyro.getYaw())) < 7){
			return true;
    	}
    	else {
    		return false;
    	}
}

	public void pidWrite(double output) {
    	if(pidTurnController.getDeltaSetpoint() < 0) {
    		PIDOutput = output;
    	}
    	else {
    		PIDOutput = -output;
    	}
	}

	public double getYaw() {
		return navxgyro.getYaw();
    }
    
    public void gyroZero() {
    	navxgyro.zeroYaw();
	}

	public void zeroEncoders(){
		driveRightMaster.getSensorCollection().setQuadraturePosition(0, Constants.kTimeoutMs);
		driveLeftMaster.getSensorCollection().setQuadraturePosition(0 , Constants.kTimeoutMs);
		System.out.println("[QuadEncoders] Zeroed. \n");	
	}

    @Override
    public void initDefaultCommand() {
      // Set the default command for a subsystem here.
	  // setDefaultCommand(new MySpecialCommand());
	  setDefaultCommand(new ArcadeDriveLookUp());
    }

    public void log() {

    }
}
