package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;


//package org.firstinspires.ftc.robotcontroller.external.samples;


@Autonomous(name = "AutonomousCodeBottomBlue", group = "Sensor")
//@Disabled                            // Comment this out to add to the opmode list

public class AutonomousCodeBottomBlue extends LinearOpMode {

    /**
     * Note that the REV Robotics Color-Distance incorporates two sensors into one device.
     * It has a light/distance (range) sensor.  It also has an RGB color sensor.
     * The light/distance sensor saturates at around 2" (5cm).  This means that targets that are 2"
     * or closer will display the same value for distance/light detected.
     * <p>
     * Although you configure a single REV Robotics Color-Distance sensor in your configuration file,
     * you can treat the sensor as two separate sensors that share the same name in your op mode.
     * <p>
     * In this example, we represent the detected color by a hue, saturation, and value color
     * model (see https://en.wikipedia.org/wiki/HSL_and_HSV).  We change the background
     * color of the screen to match the detected color.
     * <p>
     * In this example, we  also use the distance sensor to display the distance
     * to the target object.  Note that the distance sensor saturates at around 2" (5 cm).
     */
    ColorSensor sensorColor;
    DistanceSensor sensorDistance;

    private ElapsedTime runtime = new ElapsedTime();

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotor liftMotor;

    Servo servo0;
    Servo servo1;
    Servo servo3;
    Servo servo4;


    public void runOpMode() throws InterruptedException {

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");

        String Colour = "stuff";
        double MAX_POS = 0.9;
        double MIN_POS = 0.44;

        servo0 = (Servo) hardwareMap.get("servo0");
        servo1 = (Servo) hardwareMap.get("servo1");
        servo3 = (Servo) hardwareMap.get("servo3");
        servo4 = (Servo) hardwareMap.get("servo4");
        // get a reference to the color sensor.
        sensorColor = hardwareMap.get(ColorSensor.class, "sensor_color_distance");

        // get a reference to the distance sensor that shares the same name.
        sensorDistance = hardwareMap.get(DistanceSensor.class, "sensor_color_distance");

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        // wait for the start button to be pressed.
        waitForStart();

        // LOOP LOOP LOOP
        // loop and read the RGB and distance data.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (runtime.seconds() >= 0 && runtime.seconds() < 30) {
            // convert the RGB values to HSV values.
            // multiply by the SCALE_FACTOR.
            // then cast it back to int (SCALE_FACTOR is a double)
            Color.RGBToHSV((int) (sensorColor.red()),
                    (int) (sensorColor.green()),
                    (int) (sensorColor.blue()),
                    hsvValues);

            // send the info back to driver station using telemetry function.
            telemetry.addData("Distance (cm)",
                    String.format(Locale.US, "%.02f", sensorDistance.getDistance(DistanceUnit.CM)));
            telemetry.addData("Alpha", sensorColor.alpha());
            telemetry.addData("Red  ", sensorColor.red());
            telemetry.addData("Green", sensorColor.green());
            telemetry.addData("Blue ", sensorColor.blue());
            telemetry.addData("Hue", hsvValues[0]);


            if (runtime.seconds() >= 0 && runtime.seconds() < 5) {
                servo3.setPosition(MIN_POS);
                Thread.sleep(1500);
            }


            //DROP SERVO

            if (runtime.seconds() >= 5 && runtime.seconds() < 7) {
                //blue
                if (hsvValues[0] > 80 && hsvValues[0] <= 250) {
                    Colour = "blue";


                    telemetry.addData(" In Blue ", sensorColor.blue()); //blue jitter

                }
                //red
                if (hsvValues[0] >= 300 || hsvValues[0] <= 20) {
                    Colour = "red";

                    telemetry.addData(" In Red  ", sensorColor.blue());

                }


                // change the background color to match the color detected by the RGB sensor.
                // pass a reference to the hue, saturation, and value array as an argument
                // to the HSVToColor method.
                relativeLayout.post(new Runnable() {
                    public void run() {
                        relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                    }
                });

                telemetry.update();
            }

            // Set the panel back to the default color
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.WHITE);

                }
            });
            if (runtime.seconds() >= 7.0 && runtime.seconds() < 9.2) {
                if (Colour == "blue") {
                    servo0.setPosition(0);
                    servo1.setPosition(1);
                    Thread.sleep(300);
                    liftMotor.setPower(1);
                    Thread.sleep(200);
                    liftMotor.setPower(.05);
                    servo4.setPosition(1);
                    Thread.sleep(500);
                    servo3.setPosition(MAX_POS);

                } else if (Colour == "red") {
                    servo0.setPosition(0);
                    servo1.setPosition(1);
                    Thread.sleep(300);
                    liftMotor.setPower(1);
                    Thread.sleep(200);
                    liftMotor.setPower(.05);
                    servo4.setPosition(0);
                    Thread.sleep(500);
                    servo3.setPosition(MAX_POS);

                } else {
                    servo0.setPosition(0);
                    servo1.setPosition(1);
                    Thread.sleep(300);
                    liftMotor.setPower(1);
                    Thread.sleep(200);
                    liftMotor.setPower(.05);
                    servo3.setPosition(MAX_POS);
                    Thread.sleep(500);
                }
            }

            if (runtime.seconds() >= 9.2 && runtime.seconds() < 13) {
                frontLeftMotor.setPower(-.25);
                frontRightMotor.setPower(.25);
                backLeftMotor.setPower(-.25);
                backRightMotor.setPower(.25);
                Thread.sleep(1550);
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                Thread.sleep(1000);
                frontLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                Thread.sleep(613);
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                Thread.sleep(1000);
            }
            if ((runtime.seconds() >= 13) && (runtime.seconds() < 20)) {
                liftMotor.setPower(-.5);
                Thread.sleep(300);
                servo0.setPosition(1);
                servo1.setPosition(0);
                Thread.sleep(250);
                liftMotor.setPower(0);
                backLeftMotor.setPower(0.5);
                backRightMotor.setPower(-0.5);
                frontLeftMotor.setPower(0.5);
                frontRightMotor.setPower(-0.5);
                Thread.sleep(600);
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                Thread.sleep(500);
                frontLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                Thread.sleep(100);
                frontLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                Thread.sleep(100);
                frontLeftMotor.setPower(0.5);
                frontRightMotor.setPower(0.5);
                backLeftMotor.setPower(0.5);
                backRightMotor.setPower(0.5);
                Thread.sleep(100);
                frontLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                Thread.sleep(100);
                backLeftMotor.setPower(-0.5);
                backRightMotor.setPower(0.5);
                frontLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(0.5);
                Thread.sleep(100);
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                stop();
            }

        }
    }
    //Exits Loop

    //Rest of Autonomous
    /*
    {
        while (runtime.seconds() >= 9 && runtime.seconds() < 10) {
            frontLeftMotor.setPower(-.50);
            frontRightMotor.setPower(.50);
            backLeftMotor.setPower(-.50);
            backRightMotor.setPower(.50);
        }
        while (runtime.seconds() >= 10 && runtime.seconds() < 10.5) {
            frontLeftMotor.setPower(.50);
            frontRightMotor.setPower(.50);
            backLeftMotor.setPower(.50);
            backRightMotor.setPower(.50);
        }
        while (runtime.seconds() >= 10.5 && runtime.seconds() < 11) {
            servo0.setPosition(1);
            servo1.setPosition(0);
            frontLeftMotor.setPower(-.50);
            frontRightMotor.setPower(.50);
            backLeftMotor.setPower(-.50);
            backRightMotor.setPower(.50);
        }
        while (runtime.seconds() >= 10.5 && runtime.seconds() < 30) {
            servo0.setPosition(1);
            servo1.setPosition(0);
        }
    }
    */
}