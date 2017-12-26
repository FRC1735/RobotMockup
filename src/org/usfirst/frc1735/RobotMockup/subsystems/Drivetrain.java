// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc1735.RobotMockup.subsystems;

import org.usfirst.frc1735.RobotMockup.RobotMap;
import org.usfirst.frc1735.RobotMockup.commands.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 *
 */
public class Drivetrain extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private final SpeedController speedControllerL = RobotMap.drivetrainSpeedControllerL;
    private final SpeedController speedControllerR = RobotMap.drivetrainSpeedControllerR;
    private final RobotDrive robotDrive21 = RobotMap.drivetrainRobotDrive21;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS


    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new DriveWithJoysticks());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }
    
    public void arcadeDrive(double moveValue, double rotateValue) {
    	// This function is a Y axis handling wrapper for the underlying wpilib function
    	// The WPI library assumes "joystick value" inputs, where Y = up = negative = forward
    	//   Move:  FWD is negative
    	//   Rotate:  clockwise is negative (?)
    	// However, for other programming, we tend to think of FWD as positive, and clockwise as positive.
    	// So, this function flips both signs to go from programming mentality to wpilib mentality.
    	
    	// Choose whether we  square the inputs (for increased sensitivity at low speeds) based on SmartDashboard settings.
    	boolean squaredInputs = SmartDashboard.getBoolean("SquaredInputs", true); // Default to true if setting not found
    	robotDrive21.arcadeDrive(-moveValue, -rotateValue, squaredInputs);
    }
    
    public void tankDrive(double leftValue, double rightValue) {
    	// This function is a Y axis handling wrapper for the underlying wpilib function
    	// The WPI library assumes "joystick value" inputs, where Y = up = negative = forward
    	//   Move:  FWD is negative
    	// However, for other programming, we tend to think of FWD as positive
    	// So, this function flips both signs to go from programming mentality to wpilib mentality.
    	// Choose whether we  square the inputs (for increased sensitivity at low speeds) based on SmartDashboard settings.
    	boolean squaredInputs = SmartDashboard.getBoolean("SquaredInputs", true); // Default to true if setting not found
    	robotDrive21.tankDrive(-leftValue, -rightValue, squaredInputs);
    }
    
    public void selectableDriveWithJoysticks(Joystick joyLeft, Joystick joyRight) {
		// Extract the joystick values
		double joyLeftX, joyLeftY, joyRightX, joyRightY;
		
		// If an Xbox controller, try using the two sticks on controller 1 (Right side) instead of using two joysticks
		if (joyLeft.getIsXbox()) {// if Xbox, it's probably the only input and would be on input0.
			joyLeftX = joyLeft.getRawAxis(0);  // Left stick X
			joyLeftY = joyLeft.getRawAxis(1);  // Left stick Y
			joyRightX = joyLeft.getRawAxis(4); // Right stick X
			joyRightY = joyLeft.getRawAxis(5); // Right stick Y
		}
		else {
			joyLeftX  = joyLeft.getX();
			joyLeftY  = joyLeft.getY();
			joyRightX = joyRight.getX();
			joyRightY = joyRight.getY();
		}

		// Print the raw joystick inputs
		System.out.println("Raw Values:  joyLeftY="+joyLeftY+" joyLeftX="+joyLeftX + " joyRightY="+joyRightY+" joyRightX="+joyRightX);
		
		// Apply the 'dead zone' guardband to the joystick inputs:
		// Centered joysticks may not actually read as zero due to spring variances.
		// Therfore, remove any small values as being "noise".
		double joystickDeadzone = SmartDashboard.getNumber("Joystick Deadzone", 0); // default to zero if entry not found
		if (Math.abs(joyLeftX) < joystickDeadzone)
			joyLeftX = 0;
		if (Math.abs(joyLeftY) < joystickDeadzone)
			joyLeftY = 0;
		if (Math.abs(joyRightX) < joystickDeadzone)
			joyRightX = 0;
		if (Math.abs(joyRightY) < joystickDeadzone)
			joyRightY = 0;

		// Find out which operating mode is requested and send the appropriate joystick values to the appropriate sub-function
		// We could access the local mode variable, but better to use an accessor function to hide implementation details
		if (this.isInTankMode()) {
			// We call the "local" wrapper, which uses programming assumptions that FWD = positive.		
			// However, because the joystick Y axis "up" is negative, we swap the sign on both left and right
			this.tankDrive(-joyLeftY, -joyRightY); // Tank uses Y axis of left and right joysticks as independent left and right wheel controls
		}
		// Because we may add more drive modes in the future, use specific accessor functions rather than relying on assumptions of implementation.
		else if (this.isInArcadeMode()) {
			// We call the "local" wrapper, which uses programming assumptions that FWD = positive and Clockwise = positive.			
			// However, because the joystick Y axis "up" is negative, we swap the sign on both left and right
			this.arcadeDrive(-joyRightY, -joyRightX); // Arcade uses one joystick:  Y axis for forward/backwards, and X axis for spin cw/ccw
		}
		// Error check.  If we have corrupted the mode somehow, and don't match any option, default to tank
		else {
			System.err.println("selectableDrive error:  Unknown mode detected.  Defaulting to Tank.");
			this.setTankMode();
			this.tankDrive(-joyLeftY, -joyRightY); // See rules above for sign calculation.
		}
    }
    
    // Function to stop the robot
    public void stop() {
    	robotDrive21.stopMotor();
    }
    
    // Function to put us in tank mode (and update Smart Dashboard indicator)
    // Hides boolean implementation from users
    public void setTankMode() {
    	m_isInTankMode = true;
    	SmartDashboard.putString("Drivetrain Mode", "TANK");
    }
    
    // Function to hide boolean implmentation from users
    public boolean isInTankMode() {
    	return m_isInTankMode;
    }
    
    // Function to put us in tank mode (and update Smart Dashboard indicator)
   public void setArcadeMode() {
    	m_isInTankMode = false;
    	SmartDashboard.putString("Drivetrain Mode", "ARCADE");
    }
    
   // Function to hide boolean implmentation from users
   public boolean isInArcadeMode() {
	   // Because current implentation is a single boolean, if we aren't in tank mode...we're in arcade mode.
	   return !m_isInTankMode;
   }
      
   // Member Variables
    boolean m_isInTankMode = true; // true = tank; false = arcade
}

