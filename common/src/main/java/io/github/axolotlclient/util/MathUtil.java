package io.github.axolotlclient.util;

public class MathUtil {
	public static int clamp(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}

	public static long clamp(long value, long max, long min) {
		return Math.min(Math.max(value, max), min);
	}

	public static float clamp(float value, float min, float max) {
		return value < min ? min : Math.min(value, max);
	}

	public static double clamp(double value, double min, double max) {
		return value < min ? min : Math.min(value, max);
	}
}
