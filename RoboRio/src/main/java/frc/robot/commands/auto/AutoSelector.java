/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.auto;

import frc.robot.commands.auto.AutoDefault;
import frc.robot.commands.auto.AutoBlueLeft;
import frc.robot.commands.auto.AutoBlueCenter;
import frc.robot.commands.auto.AutoBlueRight;
import frc.robot.commands.auto.AutoRedLeft;
import frc.robot.commands.auto.AutoRedCenter;
import frc.robot.commands.auto.AutoRedRight;


import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.DriverStation;

public class AutoSelector extends CommandGroup {
  /**
   *  Command group to choose what auto to run based on starting side(Red or blue)
   *  And input from our custome dashboard
   * 
   * 
   */
  public AutoSelector(String position) {
    //saddSequential(new Start());

    // Geting the Driverstation Alliance
    // DriverStation.Alliance color;
    // color = DriverStation.getInstance().getAlliance();
    // if (color == DriverStation.Alliance.KBlue && position == "Left" )        { addSequential(new AutoBlueLeft());   }
    // else if (color == DriverStation.Alliance.KBlue && position == "Center" ) { addSequential(new AutoBlueCenter()); }
    // else if (color == DriverStation.Alliance.KBlue && position == "Right" )  { addSequential(new AutoBlueRight());  }
    // else if (color == DriverStation.Alliance.KRed && position == "Left" )    { addSequential(new AutoRedLeft());    }
    // else if (color == DriverStation.Alliance.KRed && position == "Center" )  { addSequential(new AutoRedCenter());  }
    // else if (color == DriverStation.Alliance.KRed && position == "Right" )   { addSequential(new AutoRedRight());   }

    if (position == "Blue Left")                                             { addSequential(new AutoBlueLeft());   }
    else if (position == "Blue Center")                                      { addSequential(new AutoBlueCenter()); }
    else if (position == "Blue Right")                                       { addSequential(new AutoBlueRight());  }
    else if (position == "Red Left")                                         { addSequential(new AutoRedLeft());    }
    else if (position == "Red Center")                                       { addSequential(new AutoRedCenter());  }
    else if (position == "Red Right")                                        { addSequential(new AutoRedRight());   }

    else                                                                     { addSequential(new AutoDefault());    }
    
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.
  }
}
