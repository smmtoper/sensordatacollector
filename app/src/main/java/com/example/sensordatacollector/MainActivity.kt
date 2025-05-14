package com.example.sensordatacollector

import android.app.Activity
import android.hardware.*
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sensordatacollector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager

    private var lightSensor: Sensor? = null
    private var rotationSensor: Sensor? = null
    private var accelerometer: Sensor? = null

    private var currentSensorType: Int = Sensor.TYPE_LIGHT
    private var dataSensor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.sensText = ""

        sensorManager = getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        findViewById<RadioGroup>(R.id.radioGroup).setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.l -> switchSensor(Sensor.TYPE_LIGHT)
                R.id.r -> switchSensor(Sensor.TYPE_ROTATION_VECTOR)
                R.id.a -> switchSensor(Sensor.TYPE_ACCELEROMETER)
            }
        }

        switchSensor(Sensor.TYPE_LIGHT)
    }

    private fun switchSensor(sensorType: Int) {
        unregisterSensors()
        currentSensorType = sensorType
        when (sensorType) {
            Sensor.TYPE_LIGHT -> {
                if (lightSensor != null) {
                    sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
                } else {
                    showToast(R.string.sensorAbsentL)
                }
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                if (rotationSensor != null) {
                    sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL)
                } else {
                    showToast(R.string.sensorAbsentR)
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                if (accelerometer != null) {
                    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
                } else {
                    showToast(R.string.sensorAbsentA)
                }
            }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_LONG).show()
        dataSensor = getString(messageResId)
        binding.sensText = dataSensor
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            dataSensor = when (event.sensor.type) {
                Sensor.TYPE_LIGHT -> "${getString(R.string.light)}: ${event.values[0]}"
                Sensor.TYPE_ROTATION_VECTOR -> "${getString(R.string.rotor)}: x=${event.values[0]} y=${event.values[1]} z=${event.values[2]}"
                Sensor.TYPE_ACCELEROMETER -> "${getString(R.string.accelerometer)}: x=${event.values[0]} y=${event.values[1]} z=${event.values[2]}"
                else -> "Неизвестный сенсор"
            }
            binding.sensText = dataSensor
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onResume() {
        super.onResume()
        switchSensor(currentSensorType)
    }

    override fun onPause() {
        super.onPause()
        unregisterSensors()
    }

    private fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }
}
