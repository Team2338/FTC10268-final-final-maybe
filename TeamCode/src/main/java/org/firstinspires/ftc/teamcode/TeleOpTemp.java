package org.firstinspires.ftc.teamcode;
//package org.firstinspires.ftc.teamcode;
//package org.firstinspires.ftc.teamcode;

/**
 * Created by Lucas H on 10/14/2017.
 */

/*
Copyright (c) 2016 Robert Atkinson
All rights reserved.
Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:
Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.
NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
//package org.firstinspires.ftc.teamcode;

//import com.qualcomm.robotcore.eventloop.opmode.Disabled;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.Gamepad;
//import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "TeleOpTemp", group = "Iterative OpMode")  // @Autonomous(...) is the other common choice
//@Disabled
public class TeleOpTemp extends OpMode {

    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor liftMotor;
    DcMotor raiseMotor;
    DcMotor extendMotor;

    Servo servo0;
    Servo servo1;
    Servo servo2;
    Servo servo3;

    double oporclo0 = 0;
    double oporclo1 = 1;
    double tspdo = 0;
    double tspdn = 0;

    int grorno = 0;
    double tafo = 0;
    double tafn = 0;

    double MAX_POS = 0.9;
    double MIN_POS = 0.44;

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    //private DcMotor leftMotor;
    //private DcMotor rightMotor;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        raiseMotor = hardwareMap.dcMotor.get("raiseMotor");
        extendMotor = hardwareMap.dcMotor.get("extendMotor");

        servo0 = (Servo) hardwareMap.get("servo0");
        servo1 = (Servo) hardwareMap.get("servo1");
        servo2 = (Servo) hardwareMap.get("servo2");
        servo3 = (Servo) hardwareMap.get("servo3");

        raiseMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized");

        /* eg: Initialize the hardware variables. Note that the strings used here as parameters
         * to 'get' must correspond to the names assigned during the robot configuration
         * step (using the FTC Robot Controller app on the phone).
         */
        //leftMotor  = hardwareMap.dcMotor.get("left motor");
        //rightMotor = hardwareMap.dcMotor.get("right motor");

        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.FORWARD); // Set to REVERSE if using AndyMark motors
        //  rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors
        // telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());



        if (gamepad1.left_bumper) {
            frontLeftMotor.setPower(-gamepad1.left_stick_y);
            frontRightMotor.setPower(gamepad1.right_stick_y);
            backLeftMotor.setPower(-gamepad1.left_stick_y);
            backRightMotor.setPower(gamepad1.right_stick_y);
        }
        else {
            frontLeftMotor.setPower(-gamepad1.left_stick_y * .5);
            frontRightMotor.setPower(gamepad1.right_stick_y * .5);
            backLeftMotor.setPower(-gamepad1.left_stick_y * .5);
            backRightMotor.setPower(gamepad1.right_stick_y * .5);
        }
        liftMotor.setPower(25*gamepad2.right_trigger - 25*gamepad2.left_trigger);

        /*
        if (gamepad1.dpad_up) {
            liftMotor.setPower(25);
        } else if (gamepad1.dpad_down) {
            liftMotor.setPower(-25);
          } else liftMotor.setPower(0);
        }
        */

        /*
        if (gamepad1.right_bumper) {
            servo0.setPosition(0);
            servo1.setPosition(1);
        } else {
            servo0.setPosition(1);
            servo1.setPosition(0);
        }
        */

        if (gamepad2.right_bumper || gamepad2.left_bumper) {
            tspdn = runtime.seconds();
        }
        if (gamepad2.right_bumper && tspdn > tspdo + 0.5) {
            if (oporclo0 == 0) {
                oporclo0 = 1;
                oporclo1 = 0;
            }
            else if (oporclo0 == 1 || oporclo0 == 0.4) {
                oporclo0 = 0;
                oporclo1 = 1;
            }
            tspdo = runtime.seconds();
        } else if (gamepad2.left_bumper && tspdn > tspdo + 0.5) {
            oporclo0 = 0.4;
            oporclo1 = 0.6;
            tspdo = runtime.seconds();
        }

        servo0.setPosition(oporclo0);
        servo1.setPosition(oporclo1);

        // eg: Run wheels in tank mode (note: The joystick goes negative when pushed forwards)
        //leftMotor.setPower(-gamepad1.left_stick_y);
        //rightMotor.setPower(gamepad1.right_stick_y);
        raiseMotor.setPower(0.5 * gamepad2.right_stick_y);
        extendMotor.setPower(0.5 * gamepad2.left_stick_y);

        if (gamepad2.a) {
            tafn = runtime.seconds();
        }
        if (gamepad2.a && tafn > tafo + 0.5) {
            if (grorno == 0) {
                grorno = 1;
            } else if (grorno == 1) {
                grorno = 0;
            }
            tafo = runtime.seconds();
        }

        servo2.setPosition(grorno);

        if (gamepad2.y) {
            servo3.setPosition(MAX_POS);
        }

    /*
     * Code to run ONCE after the driver hits STOP
     */
        //@Override
        //public void stop();



    }}
