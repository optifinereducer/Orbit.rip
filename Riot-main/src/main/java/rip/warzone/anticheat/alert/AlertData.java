package rip.warzone.anticheat.alert;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class AlertData {

    private String name;
    private Object value;

    public AlertData(String name, Object value) {
        this.name=name;

        if (value.getClass() == float.class || value instanceof Float) {
            this.value=round(BigDecimal.valueOf((float) value), 2);
        } else if (value.getClass() == double.class || value instanceof Double) {
            this.value=round(BigDecimal.valueOf((double) value), 2);
        } else {
            this.value=value;
        }
    }

    public AlertData(String s) {
    }

    private static double round(BigDecimal bigDecimal, int places) {
        if (places < 0) throw new IllegalArgumentException();
        bigDecimal=bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

}
