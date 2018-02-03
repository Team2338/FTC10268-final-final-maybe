//package org.firstinspires.ftc.teamcode;

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

import org.firstinspires.ftc.robotcontroller.external.samples.ConceptVuforiaNavigation;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
//package org.firstinspires.ftc.robotcontroller.external.samples;


@Autonomous(name = "AutonomousCodeTopBlue", group = "Sensor")
//@Disabled                            // Comment this out to add to the opmode list

public class AutonomousCodeTopBlue extends LinearOpMode {

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

    DcMotor frontLeftMotor;
    DcMotor backLeftMotor;
    DcMotor frontRightMotor;
    DcMotor backRightMotor;
    DcMotor liftMotor;

    Servo Glyph1;
    Servo Glyph2;
    Servo ColorArm;
    Servo ColorSlap;

    public static final String TAG = "Vuforia VuMark Sample";

    OpenGLMatrix lastLocation = null;

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;

    public void runOpMode() throws InterruptedException {

        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");

        String Colour = "stuff";
        double MAX_POS = 0.9;
        double MIN_POS = 0.43;

        Glyph1 = (Servo) hardwareMap.get("servo0");
        Glyph2 = (Servo) hardwareMap.get("servo1");
        ColorArm = (Servo) hardwareMap.get("servo3");
        ColorSlap = (Servo) hardwareMap.get("servo4");
        // get a reference to the color sensor.
        sensorColor = hardwareMap.get(ColorSensor.class, "sensorColor");

        // get a reference to the distance sensor that shares the same name.
        sensorDistance = hardwareMap.get(DistanceSensor.class, "sensorColor");

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

        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View, to save power
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        /*
         * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
         * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
         * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
         * web site at https://developer.vuforia.com/license-manager.
         *
         * Vuforia license keys are always 380 characters long, and look as if they contain mostly
         * random data. As an example, here is a example of a fragment of a valid key:
         *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
         * Once you've obtained a license key, copy the string from the Vuforia web site
         * and paste it in to your code onthe next line, between the double quotes.
         */
        parameters.vuforiaLicenseKey = "ATxYr4z/////AAAAmUsRcM6Nhk5Jhi+5jz+2u6gF/lP1m/B06ZOL3gofLG4ksbbqtPZ5By9A19sZ1PP1QqTCtZMhbGipZ6lWapnk9l1feXtX5qx60xdaQEBCLbzdYVh38pUOYEXuoeWEB5LY+NMI/kNwXJmBzVGYtjGyBDF2onB0Z5qA9iYisLRyBwE9juA1UqaUcvFbV+zW9uw+6c5CzcwGgex5aeL/o0POuzEkI/gsTVT3iZ7L4UEYMtTg532QS9hLv5Jnwx/+J7qY+2DJV4cdi39BwgfPe/AlmijWMwc2DzNZobklOv5fgCP0nCs1Iq+ldazSQBalb11AG5tHXFGwMutQTcHSekr8xR9pb8eSF46qP5MItvsE0OA+";

        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        // wait for the start button to be pressed.
        waitForStart();
        relicTrackables.activate();

        ElapsedTime runtime = new ElapsedTime();

        // LOOP LOOP LOOP
        // loop and read the RGB and distance data.
        // Note we use opModeIsActive() as our loop condition because it is an interruptible method.
        while (runtime.seconds() >= 0 && runtime.seconds() < 30) {

            if (runtime.seconds() >= 0 && runtime.seconds() < 5) {
                /**
                 * See if any of the instances of {@link relicTemplate} are currently visible.
                 * {@link RelicRecoveryVuMark} is an enum which can have the following values:
                 * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
                 * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
                 */
                vuMark = RelicRecoveryVuMark.from(relicTemplate);
                if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

                /* Found an instance of the template. In the actual game, you will probably
                 * loop until this condition occurs, then move on to act accordingly depending
                 * on which VuMark was visible. */
                    telemetry.addData("VuMark", "%s visible", vuMark);
                }
                else {
                    telemetry.addData("VuMark", "not visible");
                }

                telemetry.update();
                Thread.sleep(1500);
            }

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


            if (runtime.seconds() >= 5 && runtime.seconds() < 10) {
                ColorArm.setPosition(MIN_POS);
                Thread.sleep(1500);
            }


            //DROP SERVO

            if (runtime.seconds() >= 10 && runtime.seconds() < 12) {
                //blue
                if (hsvValues[0] > 50 && hsvValues[0] <= 250) {
                    Colour = "blue";


                    telemetry.addData(" In Blue ", hsvValues[0]); //blue jitter

                }
                //red
                if (hsvValues[0] >= 300 || hsvValues[0] <= 30) {
                    Colour = "red";

                    telemetry.addData(" In Red  ", hsvValues[0]);

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
            if (runtime.seconds() >= 12.0 && runtime.seconds() < 14.2) {
                if (Colour == "blue") {
                    Glyph1.setPosition(0);
                    Glyph2.setPosition(1);
                    Thread.sleep(300);
                    liftMotor.setPower(1);
                    Thread.sleep(200);
                    liftMotor.setPower(.05);
                    ColorSlap.setPosition(1);
                    Thread.sleep(500);
                    ColorArm.setPosition(MAX_POS);

                } else if (Colour == "red") {
                    Glyph1.setPosition(0);
                    Glyph2.setPosition(1);
                    Thread.sleep(300);
                    liftMotor.setPower(1);
                    Thread.sleep(200);
                    liftMotor.setPower(.05);
                    ColorSlap.setPosition(0);
                    Thread.sleep(500);
                    ColorArm.setPosition(MAX_POS);

                } else {
                    Glyph1.setPosition(0);
                    Glyph2.setPosition(1);
                    Thread.sleep(300);
                    liftMotor.setPower(1);
                    Thread.sleep(200);
                    liftMotor.setPower(.05);
                    ColorArm.setPosition(MAX_POS);
                    Thread.sleep(500);
                }
            }

            if (runtime.seconds() >= 14.2 && runtime.seconds() < 20) {
                frontLeftMotor.setPower(-.25);
                frontRightMotor.setPower(.25);
                backLeftMotor.setPower(-.25);
                backRightMotor.setPower(.25);
                Thread.sleep(1350);
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                Thread.sleep(1000);
                frontLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                Thread.sleep(575);
                frontLeftMotor.setPower(.25);
                frontRightMotor.setPower(-.25);
                backLeftMotor.setPower(.25);
                backRightMotor.setPower(-.25);
                if (vuMark == RelicRecoveryVuMark.CENTER) {
                    Thread.sleep(600);
                } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(0);
                }
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                Thread.sleep(1000);
                frontLeftMotor.setPower(-0.5);
                frontRightMotor.setPower(-0.5);
                backLeftMotor.setPower(-0.5);
                backRightMotor.setPower(-0.5);
                if (vuMark == RelicRecoveryVuMark.CENTER) {
                    Thread.sleep(575);
                } else {
                    Thread.sleep(550);
                }
                frontLeftMotor.setPower(0);
                frontRightMotor.setPower(0);
                backLeftMotor.setPower(0);
                backRightMotor.setPower(0);
                if (vuMark == RelicRecoveryVuMark.CENTER) {
                    Thread.sleep(1220);
                } else if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    Thread.sleep(820);
                } else {
                    Thread.sleep(1820);
                }
            }
            if ((runtime.seconds() >= 20) && (runtime.seconds() < 25)) {
                liftMotor.setPower(-.5);
                Thread.sleep(300);
                Glyph1.setPosition(1);
                Glyph2.setPosition(0);
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

