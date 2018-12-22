package me.creese.sport.util.chartformat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import me.creese.sport.map.Route;

public class YAxisFormatDistance implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return Route.makeDistance(value);
    }
}
