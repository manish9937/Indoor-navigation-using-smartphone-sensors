package com.example.rishabh.nevdijsktra.ToolBox;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.example.rishabh.nevdijsktra.inventory;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SensorData {

	// accelerometer
	public static double[] lastAcc = new double[] { 0.0, 0.0, 0.0 };
	private static final int sizeFV = 6;
	private double[] five_values = new double[sizeFV];
	private int fv_iterator = 0; // iterator for five_values
	double a = 0.5;
	private SensorManager sensorManager;
	private Context context;
	private List<Sensor> l_sensors;
	private Timer timer;
	public final long INTERVAL = (1000 / 30); // 33.33 ms
	private final long timeout = 600;
	private long lastStepDetectedTime = 0;
	public static int stepCounter = 0;
	public final double differential = 2.6; // experimental value
	double[] oldAcc = new double[3];
	private float gridSize = 20.0f;
	private float stepSize = 0.7f;

	// compass
	public double compValue[] = new double[] { 0.0, 0.0, 0.0 };
	private static double compassValue = -1.0;

	// magnetic field
	private static double magneticValue = -1.0;

	public static double getCompassValue() {
		return compassValue;
	}

	public static double getMagneticValue() {
		return magneticValue;
	}

	// sensor Event Listeners
	public SensorEventListener myCompassEventListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			;
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ORIENTATION:

				compassValue = inventory.lowpassFilter(compassValue,
						event.values[0], a); // pass only the x component of
												// the compass
				break;
			default:
			}
		}
	};

	// accelerometer sensor Event Listener
	public SensorEventListener myAccEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:

				// save last acc data values
				lastAcc[0] = inventory.lowpassFilter(lastAcc[0],
						event.values[0], a);
				lastAcc[1] = inventory.lowpassFilter(lastAcc[1],
						event.values[1], a);
				lastAcc[2] = inventory.lowpassFilter(lastAcc[2],
						event.values[2], a);
				break;
			default:
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	// magnetic field sensor Event Listener
	public SensorEventListener myMagneticListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {

			switch (event.sensor.getType()) {
			case Sensor.TYPE_MAGNETIC_FIELD:

				magneticValue = inventory.lowpassFilter(magneticValue,
						event.values[0], a);
				break;
			default:
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			;
		}
	};

	public SensorData(Context ctx) {
		this.context = ctx;

	}

	// Hook up accelerometer
	public void loadAcc() {

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		l_sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < l_sensors.size(); i++) {
			// Register only compass and accelerometer
			if (l_sensors.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {
				sensorManager.registerListener(myAccEventListener,
						l_sensors.get(i), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}

		// Register timer
		timer = new Timer("UpdateData", false);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				Update();
			}
		};
		timer.schedule(task, 0, INTERVAL);
	}

	// Hook up compass
	public void loadCompass() {

		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		l_sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < l_sensors.size(); i++) {
			// Register only compass and accelerometer
			if (l_sensors.get(i).getType() == Sensor.TYPE_ORIENTATION) {
				sensorManager.registerListener(myCompassEventListener,
						l_sensors.get(i), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
	}

	// Hook up magnetic sensor
	public void loadMagnetic() {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		l_sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < l_sensors.size(); i++) {
			// Register only compass and accelerometer
			if (l_sensors.get(i).getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				sensorManager.registerListener(myMagneticListener,
						l_sensors.get(i), SensorManager.SENSOR_DELAY_FASTEST);
			}
		}
	}

	// unload sensors
	public void unloadComp() {

		sensorManager.unregisterListener(myCompassEventListener);
	}

	public void unloadAcc() {

		sensorManager.unregisterListener(myAccEventListener);
		lastStepDetectedTime = 0;
		stepCounter = 0;
	}

	public void unloadMagnetic() {

		sensorManager.unregisterListener(myMagneticListener);
	}

	protected void Update() {

		long currentime = System.currentTimeMillis();
		double[] oldAcc = new double[3];
		System.arraycopy(lastAcc, 0, oldAcc, 0, 3);
		storeZdata(oldAcc[2]);

		// if current time - last step detected time > timeout range
		if (((currentime - lastStepDetectedTime) > timeout)
				&& checkStep(differential)) {
			lastStepDetectedTime = currentime;
			stepCounter++;
			updateUserPosition();
		}
	}

	public void updateUserPosition() {

		// user displacement by taking x and y component using the angle

		double x = inventory.X;
		double y = inventory.Y;

		// convert angle to radians
		double radvaluecomp = Math.PI -(Math.PI*12/180)- (SensorData.compassValue * Math.PI)
				/ 180;

		inventory.X = (float) (x + (Math.sin(radvaluecomp) * stepSize * gridSize));
		inventory.Y = (float) (y + (Math.cos(radvaluecomp) * stepSize * gridSize));
	}

	// stores revious values of acc's Z componentp
	private void storeZdata(double value) {
		five_values[fv_iterator % sizeFV] = value;
		fv_iterator++;
		fv_iterator = fv_iterator % sizeFV; // fv_iterator never > sizeFV
	}

	private boolean checkStep(double peak) {

		int itr = 5;

		double val = five_values[(fv_iterator - 1 + sizeFV) % sizeFV];

		for (int u = 1; u < itr; u++) {

			double val_delta = five_values[(fv_iterator - 1 - u + sizeFV + sizeFV)
					% sizeFV];
			if ((val - val_delta) > peak) {
				return true;
			}
		}

		return false;
	}
}
