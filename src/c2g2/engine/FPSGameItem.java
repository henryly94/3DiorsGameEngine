package c2g2.engine;

import c2g2.engine.graph.Mesh;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Vector;

public class FPSGameItem extends GameItem {

    private int hp;

    private float height;
    private float radiant;

    private boolean bg;

    private String name;

    private float turnVelocity;

    private float moveVelocity;

    private enum TURN{
        CLOCKWISE,
        COUNTERCLOCKWISE,
        NONE
    }

    private enum MOVE{
        FORWARD,
        LEFT,
        RIGHT,
        BACK,
        NONE
    }

    private TURN turnStatus;

    private Vector3f direction;

    private MOVE moveStatus;


    public FPSGameItem(Mesh mesh, String name) {
        super(mesh);
        setAABB();
        this.name = name;
        hp = 100;
        bg = false;
        turnStatus = TURN.NONE;
        moveStatus = MOVE.NONE;
        turnVelocity = 5;
        moveVelocity = 0.1f;
        direction = new Vector3f(0, 0, 1);
    }

    public void setBg(boolean bg){
        this.bg = bg;
    }

    public boolean isBg() {
        return bg;
    }

    public void setAABB(){
        Mesh mesh = getMesh();
        float[] mesh_pos = mesh.getPos();
        if (mesh_pos == null || mesh_pos.length < 3) return;
        Vector3f max, min;
        max = new Vector3f(mesh_pos[0], mesh_pos[1], mesh_pos[2]);
        min = new Vector3f(mesh_pos[0], mesh_pos[1], mesh_pos[2]);
        for (int i=1; i<mesh_pos.length/3; i++){
            max.x = Math.max(max.x, mesh_pos[i*3]);
            min.x = Math.min(min.x, mesh_pos[i*3]);
            max.y = Math.max(max.y, mesh_pos[i*3+1]);
            min.y = Math.min(min.y, mesh_pos[i*3+1]);
            max.z = Math.max(max.z, mesh_pos[i*3+2]);
            min.z = Math.min(min.z, mesh_pos[i*3+2]);
        }

        height = max.y - min.y;
        radiant = ((max.x - min.x) + (max.z - min.z)) / 4;

        height *= getScale();
        radiant *= getScale();

    }

    public boolean FPSGameItemIntersect(){
        return true;
    }

    public boolean rayIntersect(Vector3f rayPos, Vector3f rayDir){
        boolean res = false;
        Vector3f position = getPosition();

        Vector3f rayP = new Vector3f(rayPos);
        rayP.y = 0;
        Vector3f rayD = new Vector3f(rayDir);
        rayD.y = 0;
        Vector3f o = new Vector3f(position);  // Center of circle
        o.y = 0;
        // t = \dfrac{o \cdot rayD - rayP \cdot rayD}{d^2}
        // t > 0  \rightarrow in the right direction
        if (o.dot(rayD) <= rayP.dot(rayD)){
            return false;
        } else {
            float t = (o.dot(rayD) - rayP.dot(rayD))/rayD.lengthSquared();

            // r = o - (p + td)
            Vector3f r = new Vector3f();
            rayD.mul(t, r);
            rayP.add(r, r);
            o.sub(r, r);

            if (r.length() > radiant){
                return false;
            } else {
                Vector3f ray = new Vector3f();
                rayDir.mul(t, ray);
                rayPos.add(ray, ray);
                return ray.y <= position.y + height && ray.y >= position.y;
            }
        }

    }

    public void rayBack(Vector3f rayDir, float offset){
        Vector3f pos = new Vector3f(getPosition());
        Vector3f dir = new Vector3f(rayDir);
        dir.normalize();
        dir.mul(offset);
        dir.y = 0;
        pos.add(dir);
        setPosition(pos.x, pos.y, pos.z);
    }

    public void changeHp(int delta){
        hp += delta;
    }

    public void updateStatus(){
        double turnChangeDice = Math.random();
        if (turnChangeDice < 0.1){
            double turnDirectionDice = Math.random();
            if (turnDirectionDice < 0.4){
                turnStatus = TURN.CLOCKWISE;
            } else if (turnDirectionDice >= 0.4 && turnDirectionDice < 0.8){
                turnStatus = TURN.COUNTERCLOCKWISE;
            } else {
                turnStatus = TURN.NONE;
            }
        }
//            turnStatus = TURN.NONE;
            moveStatus = MOVE.FORWARD;
//        double moveChangeDice = Math.random();
//        if (moveChangeDice < 0.05){
//            double moveDirectionDice = Math.random();
//            if (moveDirectionDice < 0.5){
//                moveStatus = MOVE.FORWARD;
//            } else if (moveDirectionDice >= 0.5 && moveDirectionDice < 0.6){
//                moveStatus = MOVE.BACK;
//            } else if (moveDirectionDice >= 0.6 && moveDirectionDice < 0.7){
//                moveStatus = MOVE.LEFT;
//            } else if (moveDirectionDice >= 0.7 && moveDirectionDice < 0.8){
//                moveStatus = MOVE.RIGHT;
//            } else {
//                moveStatus = MOVE.NONE;
//            }
//        }
    }

    public int getHp(){
        return hp;
    }

    public String getName(){
        return name;
    }

    public void update(){
        updateStatus();
        switch (turnStatus){
            case CLOCKWISE:
                turn(-turnVelocity);
                break;
            case COUNTERCLOCKWISE:
                turn(turnVelocity);
                break;
            case NONE:
                break;
        }
        Vector3f pos = new Vector3f(getPosition());
        Vector3f forward = new Vector3f(direction);
        Vector3f side = new Vector3f();
        side.x = -forward.z;
        side.z = forward.x;
        forward.mul(moveVelocity);
        side.mul(moveVelocity);
        switch (moveStatus){
            case FORWARD:
                pos.add(forward);
                setPosition(pos.x, pos.y, pos.z);
                break;
            case BACK:
                pos.sub(forward);
                setPosition(pos.x, pos.y, pos.z);
                 break;
            case LEFT:
                pos.add(side);
                setPosition(pos.x, pos.y, pos.z);
                break;
            case RIGHT:
                pos.sub(side);
                setPosition(pos.x, pos.y, pos.z);
                break;
            case NONE:
                break;
        }
    }

    public void turn(float angle){
        Matrix4f rot = new Matrix4f();
        rot.identity();
        Matrix3f r = new Matrix3f();
        rot.rotateY((float)Math.toRadians(angle));
//        rot.rotateAffineXYZ(0, (float)Math.toRadians(angle), 0);
        rot.transformDirection(direction);
        direction.normalize();
        getMesh().rotateMesh(new Vector3f(0, 1, 0), (float)Math.toRadians(angle));
    }
}
