package com.stuypulse.robot.commands.auton;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.stuypulse.robot.commands.arm.ArmFollowTrajectory;
import com.stuypulse.robot.commands.intake.IntakeDeacquireCone;
import com.stuypulse.robot.commands.intake.IntakeDeacquireCube;
import com.stuypulse.robot.commands.arm.ArmFollowTrajectory;
import com.stuypulse.robot.commands.swerve.SwerveDriveFollowTrajectory;
import com.stuypulse.robot.constants.Settings.Swerve.Motion;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class OnePiece extends SequentialCommandGroup {
    private static final double INTAKE_DEACQUIRE_TIME = 1.0;

    private static final PathConstraints CONSTRAINTS = new PathConstraints(2, 2);

    public OnePiece() {
        addCommands(
            // new ArmFollowTrajectory(),
            new IntakeDeacquireCone(),
            new WaitCommand(INTAKE_DEACQUIRE_TIME),
            new SwerveDriveFollowTrajectory(
                PathPlanner.loadPath("1 Piece + Mobility", CONSTRAINTS)
            ).robotRelative()
        );
    }
    
}