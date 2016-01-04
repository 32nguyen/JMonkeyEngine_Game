package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.util.TangentBinormalGenerator;

/** 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * TankState.java loaded tank Model using normal, diffuse, specular, glow texture
 *******************************************************************************/
public class TankState extends AbstractAppState{
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    float time = 0f;
    private Node tank;
    private Vector3f d1,d2,d3,d4;
    public TankState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;// passing bulletAppState from My3DGame.Class
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) { //initalize the state
        // declaring the parameter from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode(); 
        createTank(); // call method to create the tank
    }

    public void createTank() {
        
        Vector3f loc = new Vector3f(60, 50, 60);// vector location of the tank
        tank = (Node) assetManager.loadModel("Models/HoverTank/Tank.j3o");
        Material mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");
        // set diffuse texture
        TextureKey tankDiffuse = new TextureKey("Models/HoverTank/tank_diffuse.jpg", false);
        mat.setTexture("DiffuseMap", assetManager.loadTexture(tankDiffuse));
        // set normal texture
        TangentBinormalGenerator.generate(tank);
        TextureKey tankNormal = new TextureKey("Models/HoverTank/tank_normals.png", false);
        mat.setTexture("NormalMap", assetManager.loadTexture(tankNormal));
        // set specular texture
        TextureKey tankSpecular = new TextureKey("Models/HoverTank/tank_specular.jpg", false);
        mat.setTexture("SpecularMap", assetManager.loadTexture(tankSpecular));
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Gray);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 120f); 
        // set glow map texture
        TextureKey tankGlow = new TextureKey("Models/HoverTank/tank_glow_map.jpg", false);
        mat.setTexture("GlowMap", assetManager.loadTexture(tankGlow));
        mat.setColor("GlowColor", ColorRGBA.White);

        tank.setMaterial(mat); // set map
        tank.scale(2);  // scale
        tank.setLocalTranslation(loc);  // traslate the tank
        RigidBodyControl elephantPhy = new RigidBodyControl(0f);
        tank.addControl(elephantPhy);
        bulletAppState.getPhysicsSpace().add(tank);

        tank.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(tank);  // add to rootNode
        // 4 looped destination of the tank's path
        d1 = new Vector3f(-200, 50, 70);
        d2 = new Vector3f(-70, 50, -200);
        d3 = new Vector3f(200, 50, -70);
        d4 = new Vector3f(70, 50, 200);
    }

    @Override
    public void update(float tpf) { // make the tank moving in loops through 4 positions
        time +=tpf; 
        //Vector3f tankMove = destinate.subtract(tank.getLocalTranslation());
        if (time < 15 && tank.getLocalTranslation().x> -70){
            time += tpf;
            tank.move(-tpf*20,0,0);
            tank.lookAt(d1, Vector3f.UNIT_Y);
        }if (time < 30 && time>=15 && tank.getLocalTranslation().z > -70){
            time += tpf;
            tank.move(0,0,-tpf*20);
            tank.lookAt(d2, Vector3f.UNIT_Y);
        }if (time < 45 && time>=30 && tank.getLocalTranslation().x< 70){
            time += tpf;
            tank.move(tpf*20,0,0);
            tank.lookAt(d3, Vector3f.UNIT_Y);
        }if (time < 60 && time>=45 &&tank.getLocalTranslation().z < 70){
            time += tpf;
            tank.move(0,0,tpf*20);
            tank.lookAt(d4, Vector3f.UNIT_Y);
        }if (time>=60){
            time = 0; // reset the timer for new loop
        }
    }

    @Override
    public void cleanup() { // use this method to disappear this state
        super.cleanup();
        rootNode.detachAllChildren();
    }    
    public Node returnTank(){ // return the tankNode
        return tank;
    }
}