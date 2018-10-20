/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

/**
 * An example command.  You can replace me with your own command.
 */
public class MoveStright extends Command {
    private double counts;
	private double power;
	
    public MoveStright(double Distance, double Power) {
    	requires(Robot.driveSubsystem);
    	counts = Distance;
    	power = Power;
    	if(counts > 0){
    		power = -power;
    	}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	// Robot.driveSubsystem.zeroEncoders();
    	Robot.driveSubsystem.pidSetPoint(Robot.driveSubsystem.getYaw());
    	Robot.driveSubsystem.pidTurnControllerChangeState("Enable");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.driveSubsystem.move(power + 2*Robot.driveSubsystem.PIDOutput, power + -2*Robot.driveSubsystem.PIDOutput);
//    	Robot.kDriveBaseSubsystem.driveDistance(counts);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
        // return Robot.driveSubsystem.driveDistance(counts);
    }

    // Called once after isFinished returns true
    protected void end() {
    	// Robot.driveSubsystem.zeroEncoders();
    	Robot.driveSubsystem.pidTurnControllerChangeState("Disable");
    	Robot.driveSubsystem.move(0, 0);
    	// Robot.driveSubsystem.countsmeet = true;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {

    }
}