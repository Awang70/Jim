package com.stuypulse.robot.subsystems.arm;

import com.stuypulse.robot.util.ArmVisualizer;

import edu.wpi.first.math.geometry.Rotation2d;

public class NoArm extends Arm {

    ArmVisualizer visualizer = new ArmVisualizer();

    public ArmVisualizer getVisualizer() {
        return visualizer;
    }

    @Override
    public Rotation2d getShoulderAngle() {
        // TODO Auto-generated method stub
        return new Rotation2d();
    }

    @Override
    public Rotation2d getWristAngle() {
        // TODO Auto-generated method stub
        return new Rotation2d();
    }

    @Override
    public Rotation2d getShoulderTargetAngle() {
        // TODO Auto-generated method stub
        return new Rotation2d();
    }

    @Override
    public Rotation2d getWristTargetAngle() {
        // TODO Auto-generated method stub
        return new Rotation2d();
    }


    @Override
    public void setTargetShoulderAngle(Rotation2d angle) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTargetWristAngle(Rotation2d angle) {
        // TODO Auto-generated method stub
        
    }


    public void setFeedbackEnabled(boolean enabled) {
        
    }
    
}
