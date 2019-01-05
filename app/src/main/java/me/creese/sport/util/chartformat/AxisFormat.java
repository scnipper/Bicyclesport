package me.creese.sport.util.chartformat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import me.creese.sport.map.Route;
import me.creese.sport.util.AppSettings;
import me.creese.sport.util.UpdateInfo;


public class AxisFormat implements IAxisValueFormatter {

    private final TypeAxis typeAxis;

    public enum TypeAxis {
        DISTANCE,
        TIME,
        CALORIES,
        SPEED
    }

    public AxisFormat(TypeAxis typeAxis) {
        this.typeAxis = typeAxis;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String valueReturn = "";

        switch (typeAxis) {
            case DISTANCE:
                valueReturn = Route.makeDistance(value);
                break;
            case TIME:
                valueReturn = UpdateInfo.formatTime((long) (value*0.6f));
                break;
            case CALORIES:
                valueReturn = (int)value+" cal";
                break;
            case SPEED:
                if(AppSettings.UNIT_SYSTEM.equals(AppSettings.UnitsSystem.METRIC))
                valueReturn = (int) value+" км/ч";
                if (AppSettings.UNIT_SYSTEM.equals(AppSettings.UnitsSystem.IMPERIAL))
                    valueReturn = (int) (value * AppSettings.IMP_COEF* 1000)+" миль/ч";
                break;
        }

        return valueReturn;
    }
}
