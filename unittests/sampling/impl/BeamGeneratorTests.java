package sampling.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import sampling.api.Beam;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class {@link BeamGenerator}.
 * Verifies that 3D beams of rays are correctly generated from 2D sample patterns.
 *
 * @author David &amp; Yehuda
 */
public class BeamGeneratorTests {

    /**
     * Default constructor for BeamGeneratorTests.
     */
    public BeamGeneratorTests() {
    }

    /**
     * Main reference ray used for beam generation tests.
     */
    private static final Ray CENTER_RAY = new Ray(Point.ZERO, Vector.AXIS_Z);

    /**
     * Test method for {@link BeamGenerator#generateBeam(Ray, double, double, int)}.
     */
    @Test
    void testGenerateBeam() {
        BeamGenerator generator = new BeamGenerator();

        // ============ Equivalence Partitions Tests ==============

        // TC01: Standard beam generation using default sampler (JitteredSampler)
        Beam beam = generator.generateBeam(CENTER_RAY, 2.0, 10.0, 9);

        assertNotNull(beam, "ERROR: generateBeam() returned null");
        assertNotNull(beam.getRays(), "ERROR: Beam rays list is null");
        assertFalse(beam.getRays().isEmpty(), "ERROR: Beam rays list should not be empty");

        // Verify all generated rays share the same origin as the central ray
        Point origin = CENTER_RAY.origin();
        for (Ray ray : beam.getRays()) {
            assertEquals(origin, ray.origin(), "ERROR: All beam rays must originate from the center ray origin");
        }

        // TC02: Custom Sampler injection test (using GridSampler)
        generator.setSampler(new RandomSampler());
        Beam gridBeam = generator.generateBeam(CENTER_RAY, 2.0, 10.0, 81);
        assertNotNull(gridBeam, "ERROR: generateBeam() with custom sampler failed");

        // =============== Boundary Values Tests ==================

        // BV11: Single ray requested (amount = 1) -> should return just the center ray
        Beam singleRayBeam = generator.generateBeam(CENTER_RAY, 2.0, 10.0, 1);
        assertEquals(1, singleRayBeam.getRays().size(), "ERROR: Beam should contain exactly 1 ray when amount = 1");
        assertEquals(CENTER_RAY, singleRayBeam.getRays().getFirst(), "ERROR: Single ray beam should return the central ray");

        // BV12: Size = 0 -> should return just the center ray
        Beam zeroSizeBeam = generator.generateBeam(CENTER_RAY, 0.0, 10.0, 9);
        assertEquals(1, zeroSizeBeam.getRays().size(), "ERROR: Beam should contain 1 ray when size = 0");
        assertEquals(CENTER_RAY, zeroSizeBeam.getRays().getFirst(), "ERROR: Zero size beam should return the central ray");

        // BV13: Ray directed along Z-axis (testing helper vector selection logic)
        Ray zRay = new Ray(Point.ZERO, Vector.AXIS_Z);
        assertDoesNotThrow(() -> generator.generateBeam(zRay, 1.0, 5.0, 4),
                "ERROR: Beam generator failed when ray direction is parallel to AXIS_Z");

        // BV14: Ray directed along Y-axis (testing helper vector selection logic)
        Ray yRay = new Ray(Point.ZERO, Vector.AXIS_Y);
        assertDoesNotThrow(() -> generator.generateBeam(yRay, 1.0, 5.0, 4),
                "ERROR: Beam generator failed when ray direction is parallel to AXIS_Y");
    }
}