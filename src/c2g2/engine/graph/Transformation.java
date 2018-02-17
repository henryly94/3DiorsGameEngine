package c2g2.engine.graph;

import c2g2.engine.GameItem;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix;
    
    private final Matrix4f viewMatrix;
    
    private final Matrix4f modelMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        projectionMatrix.identity();
        //// --- student code ---
        // Since r+l = t+b = 0, There's only 5 entries need to be changed
        float tan = (float)Math.tan((double)fov * 0.5f);
        projectionMatrix.m00(height / (tan * width));
        projectionMatrix.m11(1/tan);
        projectionMatrix.m22((zNear+zFar)/(zNear-zFar));
        projectionMatrix.m23(-1f);
        projectionMatrix.m32(zNear*zFar/(zFar-zNear));
        return projectionMatrix;
    }
    
    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraTarget = camera.getTarget();
        Vector3f up = camera.getUp();
        viewMatrix.identity();
        //// --- student code ---

        // This part all comes from book 7.1.3
        Vector3f rotation = camera.getRotation();

        Matrix4f rotMatrix=new Matrix4f();
        rotMatrix.identity();
        rotMatrix.rotateAffineXYZ(rotation.x, rotation.y, rotation.z);

        Vector3f cameraRotatedTarget=rotMatrix.transformDirection(cameraTarget);
        Vector3f cameraRotatedUp=rotMatrix.transformDirection(up);

        Vector3f w = new Vector3f();
        cameraRotatedTarget.negate(w);
        w.normalize();

        Vector3f u = new Vector3f();
        cameraRotatedUp.cross(w, u);
        u.normalize();

        Vector3f v = new Vector3f();
        w.cross(u, v);

        viewMatrix.set3x3(new Matrix3f(u, v, w));
        viewMatrix.transpose();
        viewMatrix.m30(-u.dot(cameraPos));
        viewMatrix.m31(-v.dot(cameraPos));
        viewMatrix.m32(-w.dot(cameraPos));

        return viewMatrix;
    }
    
    public Matrix4f getModelMatrix(GameItem gameItem){
        Vector3f rotation = gameItem.getRotation();
        Vector3f position = gameItem.getPosition();
        modelMatrix.identity();
        //// --- student code ---

        //Translate
        Matrix4f transMatrix=new Matrix4f();
        transMatrix.identity();
        transMatrix.translate(new Vector3f(position.x,position.y,position.z));
        modelMatrix.mul(transMatrix);

        //Rotate
        Matrix4f rotMatrix=new Matrix4f();
        rotMatrix.identity();
        rotMatrix.rotateAffineXYZ(
                (float)Math.toRadians(rotation.x),
                (float)Math.toRadians(rotation.y),
                (float)Math.toRadians(rotation.z)
        );
        modelMatrix.mul(rotMatrix);

        //Scale
        Matrix4f scaleMatrix=new Matrix4f();
        scaleMatrix.identity();
        float scale = gameItem.getScale();
        scaleMatrix.scale(scale);
        modelMatrix.mul(scaleMatrix);


        return modelMatrix;
    }

    public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(getModelMatrix(gameItem));
    }
}
