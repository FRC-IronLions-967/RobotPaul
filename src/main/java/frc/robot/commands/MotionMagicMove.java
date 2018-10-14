/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class MotionMagicMove extends Command {
  Double Distance;

  public MotionMagicMove(double distance) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.powerSubsystem);
    Distance = distance;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {

  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    // Robot.driveSubsystem.motionMagic(-Robot.m_oi.getXbox0().getRawAxis(1), Robot.m_oi.getXbox0().getRawAxis(4));
    Robot.driveSubsystem.motionMagic(Distance, 0);

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return true;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
