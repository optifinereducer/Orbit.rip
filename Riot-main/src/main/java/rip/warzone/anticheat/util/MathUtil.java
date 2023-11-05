package rip.warzone.anticheat.util;

import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_7_R4.MathHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.sqrt;

@UtilityClass
public class MathUtil {

    public double EXPANDER=Math.pow(2, 24);

    public static float[] getRotationFromPosition(CustomLocation playerLocation, CustomLocation targetLocation) {
        double xDiff=targetLocation.getX() - playerLocation.getX();
        double zDiff=targetLocation.getZ() - playerLocation.getZ();
        double yDiff=targetLocation.getY() - (playerLocation.getY() + 0.12);
        double dist=MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw=(float) (Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch=(float) (-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[]{yaw, pitch};
    }

    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for ( PotionEffect pe : player.getActivePotionEffects() ) {
            if (pe.getType().getName().equals(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public static double getMagnitude(Location from, Location to) {
        if (from.getWorld() != to.getWorld()) return 0.0;

        double deltaX=to.getX() - from.getX();
        double deltaZ=to.getZ() - from.getZ();

        return (deltaX * deltaX + deltaZ * deltaZ);
    }

    public static int getPotionLevel(Player player, PotionEffectType effect) {
        int effectId=effect.getId();

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

    /**
     * @param current  - The current value
     * @param previous - The previous value
     * @return - The GCD of those two values
     */
    public long getGcd(long current, long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public static double getStandardDeviation(Iterable<? extends Number> data) {
        double variance=getVariance(data);

        // The standard deviation is the square root of variance. (sqrt(s^2))
        return Math.sqrt(variance);
    }

    public static <T extends Number> T getMode(Collection<T> collect) {
        Map<T, Integer> repeated=new HashMap<>();

        //Sorting each value by how to repeat into a map.
        collect.forEach(val -> {
            int number=repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(tup -> tup.b(), Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new).a();
    }

    public static double getVariance(Iterable<? extends Number> data) {
        int count=0;

        double sum=0.0;
        double variance=0.0;

        double average;

        // Increase the sum and the count to find the average and the standard deviation
        for ( Number number : data ) {
            sum+=number.doubleValue();
            ++count;
        }

        average=sum / count;

        // Run the standard deviation formula
        for ( Number number : data ) {
            variance+=Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public static double getHorizontalDistance(Location from, Location to) {
        double deltaX=to.getX() - from.getX();
        double deltaZ=to.getZ() - from.getZ();
        return sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public double average(Collection<Integer> numbers) {
        double average=0.0;
        for ( int i : numbers ) {
            average+=i;
        }
        average/=numbers.size();

        return average;
    }

    public double stDeviation(Collection<Integer> numbers) {
        double average=MathUtil.average(numbers);

        double stdDev=0.0;
        for ( int j : numbers ) {
            stdDev+=Math.pow(j - average, 2.0);
        }
        stdDev/=numbers.size();
        stdDev=sqrt(stdDev);

        return stdDev;
    }

    public static float getBaseSpeed(Player player) {
        return 0.25f + (getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }


    public static int pingFormula(long ping) {
        return (int) Math.ceil(ping / 2L / 50.0) + 2;
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float distance=Math.abs(angle1 - angle2) % 360.0f;
        if (distance > 180.0f) {
            distance=360.0f - distance;
        }
        return distance;
    }

}
