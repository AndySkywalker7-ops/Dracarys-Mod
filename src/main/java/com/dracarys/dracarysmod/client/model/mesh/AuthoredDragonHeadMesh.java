package com.dracarys.dracarysmod.client.model.mesh;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * STEP 5.10R2 — authored polygon head runtime mesh loader.
 *
 * The previous STEP 5.10R embedded every authored vertex/index/normal directly
 * in Java array literals. javac compiled those literals into the class static
 * initializer and exceeded the JVM's 64 KiB bytecode limit for one method
 * ("code too large").
 *
 * Geometry now lives in the packaged binary resource:
 * assets/dracarysmod/models/entity/dragon/head/dracarys_head.mesh
 *
 * The editable OBJ/MTL source asset remains separate. This class contains only
 * the small renderer/loader required to consume the authored mesh.
 */
public final class AuthoredDragonHeadMesh {
    private static final float PX = 1.0F / 16.0F;
    private static final String MESH_RESOURCE =
            "assets/dracarysmod/models/entity/dragon/head/dracarys_head.mesh";
    private static final int MAGIC = 0x4452484D; // "DRHM"
    private static final int VERSION = 1;

    private AuthoredDragonHeadMesh() {}

    public static void renderSkull(
            PoseStack stack,
            VertexConsumer consumer,
            int light,
            int overlay
    ) {
        MeshBundle bundle = Holder.BUNDLE;
        render(
                stack,
                consumer,
                light,
                overlay,
                bundle.skullVertices,
                bundle.skullIndices,
                bundle.skullNormals
        );
    }

    public static void renderJaw(
            PoseStack stack,
            VertexConsumer consumer,
            int light,
            int overlay
    ) {
        MeshBundle bundle = Holder.BUNDLE;
        render(
                stack,
                consumer,
                light,
                overlay,
                bundle.jawVertices,
                bundle.jawIndices,
                bundle.jawNormals
        );
    }

    private static final class Holder {
        private static final MeshBundle BUNDLE = loadMeshBundle();
    }

    private static MeshBundle loadMeshBundle() {
        ClassLoader loader = AuthoredDragonHeadMesh.class.getClassLoader();

        try (InputStream raw = loader.getResourceAsStream(MESH_RESOURCE)) {
            if (raw == null) {
                throw new IllegalStateException(
                        "Missing Dracarys authored head mesh resource: " + MESH_RESOURCE
                );
            }

            try (DataInputStream in = new DataInputStream(new BufferedInputStream(raw))) {
                int magic = in.readInt();
                if (magic != MAGIC) {
                    throw new IllegalStateException(
                            "Invalid Dracarys head mesh magic: 0x"
                                    + Integer.toHexString(magic)
                    );
                }

                int version = in.readInt();
                if (version != VERSION) {
                    throw new IllegalStateException(
                            "Unsupported Dracarys head mesh version: " + version
                    );
                }

                MeshData skull = readMesh(in, "skull");
                MeshData jaw = readMesh(in, "jaw");

                validateMesh(skull, "skull");
                validateMesh(jaw, "jaw");

                return new MeshBundle(
                        skull.vertices,
                        skull.indices,
                        skull.normals,
                        jaw.vertices,
                        jaw.indices,
                        jaw.normals
                );
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to read Dracarys authored head mesh: " + MESH_RESOURCE,
                    exception
            );
        }
    }

    private static MeshData readMesh(DataInputStream in, String name) throws IOException {
        float[] vertices = readFloatArray(in, name + " vertices");
        int[] indices = readIntArray(in, name + " indices");
        float[] normals = readFloatArray(in, name + " normals");
        return new MeshData(vertices, indices, normals);
    }

    private static float[] readFloatArray(
            DataInputStream in,
            String label
    ) throws IOException {
        int length = readSafeLength(in, label, 5_000_000);
        float[] values = new float[length];
        for (int i = 0; i < length; i++) {
            values[i] = in.readFloat();
        }
        return values;
    }

    private static int[] readIntArray(
            DataInputStream in,
            String label
    ) throws IOException {
        int length = readSafeLength(in, label, 5_000_000);
        int[] values = new int[length];
        for (int i = 0; i < length; i++) {
            values[i] = in.readInt();
        }
        return values;
    }

    private static int readSafeLength(
            DataInputStream in,
            String label,
            int maximum
    ) throws IOException {
        int length = in.readInt();
        if (length < 0 || length > maximum) {
            throw new IOException(
                    "Invalid " + label + " length " + length + " (max " + maximum + ")"
            );
        }
        return length;
    }

    private static void validateMesh(MeshData mesh, String name) {
        if (mesh.vertices.length % 5 != 0) {
            throw new IllegalStateException(
                    "Invalid " + name + " vertex stream: expected XYZUV groups"
            );
        }
        if (mesh.indices.length % 3 != 0) {
            throw new IllegalStateException(
                    "Invalid " + name + " index stream: expected triangles"
            );
        }
        if (mesh.normals.length != mesh.indices.length) {
            throw new IllegalStateException(
                    "Invalid " + name + " normal stream: expected one XYZ normal per triangle"
            );
        }

        int vertexCount = mesh.vertices.length / 5;
        for (int index : mesh.indices) {
            if (index < 0 || index >= vertexCount) {
                throw new IllegalStateException(
                        "Invalid " + name + " index " + index
                                + " for vertex count " + vertexCount
                );
            }
        }
    }

    private static void render(
            PoseStack stack,
            VertexConsumer consumer,
            int light,
            int overlay,
            float[] vertices,
            int[] indices,
            float[] normals
    ) {
        PoseStack.Pose pose = stack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        for (int tri = 0; tri < indices.length / 3; tri++) {
            float nx = normals[tri * 3];
            float ny = normals[tri * 3 + 1];
            float nz = normals[tri * 3 + 2];

            int i0 = indices[tri * 3];
            int i1 = indices[tri * 3 + 1];
            int i2 = indices[tri * 3 + 2];

            emit(
                    consumer, matrix, normalMatrix, vertices,
                    i0, nx, ny, nz, light, overlay
            );
            emit(
                    consumer, matrix, normalMatrix, vertices,
                    i1, nx, ny, nz, light, overlay
            );
            emit(
                    consumer, matrix, normalMatrix, vertices,
                    i2, nx, ny, nz, light, overlay
            );

            // Entity render types are quad-based in the current renderer path.
            // Repeating the final vertex yields a degenerate fourth corner
            // without changing the authored triangle.
            emit(
                    consumer, matrix, normalMatrix, vertices,
                    i2, nx, ny, nz, light, overlay
            );
        }
    }

    private static void emit(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normalMatrix,
            float[] vertices,
            int index,
            float nx,
            float ny,
            float nz,
            int light,
            int overlay
    ) {
        int offset = index * 5;

        consumer.vertex(
                        matrix,
                        vertices[offset] * PX,
                        vertices[offset + 1] * PX,
                        vertices[offset + 2] * PX
                )
                .color(255, 255, 255, 255)
                .uv(vertices[offset + 3], vertices[offset + 4])
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
    }

    private record MeshData(
            float[] vertices,
            int[] indices,
            float[] normals
    ) {}

    private record MeshBundle(
            float[] skullVertices,
            int[] skullIndices,
            float[] skullNormals,
            float[] jawVertices,
            int[] jawIndices,
            float[] jawNormals
    ) {}
}
