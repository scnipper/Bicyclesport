package me.creese.sport.util.chartformat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import me.creese.sport.util.UpdateInfo;

public class XAxisFormatTime implements IAxisValueFormatter {


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return UpdateInfo.formatTime((long) value*60);
    }
}
