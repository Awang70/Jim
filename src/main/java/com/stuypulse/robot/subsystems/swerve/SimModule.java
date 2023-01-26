package com.stuypulse.robot.subsystems.swerve;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.stuypulse.robot.constants.Motors;
import com.stuypulse.robot.constants.Settings.Swerve.Drive;
import com.stuypulse.robot.constants.Settings.Swerve.Encoder;
import com.stuypulse.robot.constants.Settings.Swerve.Turn;

import com.stuypulse.robot.subsystems.ISwerveModule;
import com.stuypulse.stuylib.control.Controller;
import com.stuypulse.stuylib.control.angle.AngleController;
import com.stuypulse.stuylib.control.angle.feedback.AnglePIDController;
import com.stuypulse.stuylib.control.feedback.PIDController;
import com.stuypulse.stuylib.control.feedforward.Feedforward;
import com.stuypulse.stuylib.math.Angle;
import com.stuypulse.stuylib.network.SmartAngle;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.wpilibj.simulation.LinearSystemSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class SimModule extends ISwerveModule {
    
    private static LinearSystem<N2, N1, N2> identifyVelocityPositionSystem(double kV, double kA) {
        if (kV <= 0.0) {
            throw new IllegalArgumentException("Kv must be greater than zero.");
          }
          if (kA <= 0.0) {
            throw new IllegalArgumentException("Ka must be greater than zero.");
          }
      
          return new LinearSystem<N2, N1, N2>(
              Matrix.mat(Nat.N2(), Nat.N2()).fill(0.0, 1.0, 0.0, -kV / kA),
              Matrix.mat(Nat.N2(), Nat.N1()).fill(0.0, 1.0 / kA),
              Matrix.mat(Nat.N2(), Nat.N2()).fill(1.0, 0.0, 0.0, 1.0),
              Matrix.mat(Nat.N2(), Nat.N1()).fill(0.0, 0.0));
    }

    // module data
    private final String id;
    private final Translation2d location;
    private SwerveModuleState targetState;

    // turn
    private final LinearSystemSim<N2, N1, N1> turnSim;

    // drive
    private final LinearSystemSim<N2, N1, N2> driveSim;
    
    // controllers
    private Controller driveController;
    private AngleController turnController;

    public SimModule(String id, Translation2d location) {
        
        // module data
        this.id = id;
        this.location = location;

        // turn 
        turnSim = new LinearSystemSim<>(LineaSystemId.identifyPositionSystem(Turn.kV, Turn.kA));

        turnController = new AnglePIDController(Turn.kP, Turn.kI, Turn.kD);

        // drive
        driveSim = new LinearSystemSim<>(LinearSystemId.identifyVelocityPositionSystem(Drive.kV, Drive.kA));
        
        driveController = new PIDController(Drive.kP, Drive.kI, Drive.kD)
            .add(new Feedforward.Motor(Drive.kS, Drive.kV, Drive.kA).velocity());
        
        targetState = new SwerveModuleState();
    }   
    
    @Override
    public String getID() {
        return id;
    }
    
    @Override
    public Translation2d getLocation() {
        return location;
    }
    
    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(getSpeed(), getAngle());
    }
    
    private double getSpeed() {
        return driveSim.getOutput(1);
    }

    private double getDistance() {
        return driveSim.getOutput(0);
    }
    
    private Rotation2d getAngle() {
        return Rotation2d.fromRadians(turnSim.getOutput(0));
    } 

    @Override 
    public void setTargetState(SwerveModuleState state) {
        targetState = SwerveModuleState.optimize(state, getAngle());
    }
    
    @Override
    public SwerveModulePosition getModulePosition() {
        return new SwerveModulePosition(getDistance(), getAngle());
    }

    @Override
    public void periodic() {
        // turn
        turnController.update(
            Angle.fromRotation2d(targetState.angle), 
            Angle.fromRotation2d(getAngle()));

        // drive
        driveController.update(
            targetState.speedMetersPerSecond, 
            getSpeed());

        SmartDashboard.putNumber(id + "/Target Angle", targetState.angle.getDegrees());
        SmartDashboard.putNumber(id + "/Angle", getAngle().getDegrees());
        SmartDashboard.putNumber(id + "/Angle Error", turnController.getError().toDegrees());
        SmartDashboard.putNumber(id + "/Angle Voltage", turnController.getOutput());
        SmartDashboard.putNumber(id + "/Target Speed", targetState.speedMetersPerSecond);
        SmartDashboard.putNumber(id + "/Speed", getSpeed());
        SmartDashboard.putNumber(id + "/Speed Error", driveController.getError());
        SmartDashboard.putNumber(id + "/Speed Voltage", driveController.getOutput());
    }

    @Override
    public void simulationPeriodic() {
        // drive
        driveSim.setInput(driveController.getOutput());
        driveSim.update(Settings.DT);
        
        // turn
        turnSim.setInput(turnController.getOutput());
        turnSim.update(Settings.DT);
        
       // turn simulation
       RoboRioSim.setVInVoltage(BatterySim.calculateDefaultBatteryLoadedVoltage(
           turnSim.getCurrentDrawAmps() + driveSim.getCurrentDrawAmps()
       ));

    }
}
