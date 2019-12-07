/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Code to track the pdp curent for each port
 */
public class PowerSubsystem extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.

  PowerDistributionPanel pdp = new PowerDistributionPanel();

  public PowerSubsystem(){}

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  public void log() {
    for(int i =0; i <= 15; i++) {
      SmartDashboard.putNumber("PDP Port " + i, pdp.getCurrent(i));
    }
  }
}
