package com.stuypulse.robot.commands.auton;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.stuypulse.robot.commands.swerve.SwerveDriveFollowTrajectory;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class PattyFlipper extends SequentialCommandGroup {
    public PattyFlipper() {
        PathConstraints CONSTRAINTS = new PathConstraints(5, 2);

        addCommands(
            new SwerveDriveFollowTrajectory(
                PathPlanner.loadPath("1 Piece Patty Flipper", CONSTRAINTS)
            ).robotRelative()
        );
    }
}
