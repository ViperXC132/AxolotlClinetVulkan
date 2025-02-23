package io.github.axolotlclient.bridge.math;

public record Vec3(double x, double y, double z) {
	public Vec3 x(double x) {
		return new Vec3(x, y, z);
	}

	public Vec3 y(double y) {
		return new Vec3(x, y, z);
	}

	public Vec3 z(double z) {
		return new Vec3(x, y, z);
	}

	public double lenSq() {
		return x * x + y * y + z * z;
	}

	public double len() {
		return Math.sqrt(lenSq());
	}

	public Vec3 add(Vec3 rhs) {
		return new Vec3(x + rhs.x, y + rhs.y, z + rhs.z);
	}

	public Vec3 sub(Vec3 rhs) {
		return new Vec3(x - rhs.x, y - rhs.y, z - rhs.z);
	}

	public double distSq(Vec3 rhs) {
		double dx = x - rhs.x;
		double dy = y - rhs.y;
		double dz = z - rhs.z;

		return dx * dx + dy * dy + dz * dz;
	}

	public double dist(Vec3 rhs) {
		return Math.sqrt(distSq(rhs));
	}
}
