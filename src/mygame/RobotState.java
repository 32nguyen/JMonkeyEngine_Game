package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * RobotState.java loads the robot model, makes walk animation & checks the collision
 * with the shooted balls, automatically direct to the fire, be disappeared if hit 
 * by the balls or go through the fire. Also, adding the explostion effect when the ball
 * hit the robot
 *******************************************************************************/

public class RobotState extends AbstractAppState implements AnimEventListener,PhysicsCollisionListener,ActionListener {// 
    private BulletAppState bulletAppState;
    private BetterCharacterControl robotControl;
    private Node rootNode,tank;
    private Node robotNode = new Node();
    private SimpleApplication app;
    private AssetManager assetManager;
    private AnimControl control;
    private AnimChannel channel;
    private AppStateManager state;
    private static final String ANI_WALK = "Walk";
    
    private Spatial robot;
    private Vector3f walkDirection = new Vector3f(0,0,0);
    private Vector3f destinationRobotVector = new Vector3f(10,15,-20);
    private float timeDropRobot, timeExplostion = 0f;
    private float timeBattleStart = 0f;
    private float speed = 10f;
    private int enemyPassingNum = 0;
    private AudioNode destroyAudio;
    private Spatial collisionNode  = new Node();
    private Boolean checkCollision = false;
    private ParticleEmitter sparksEmitter, burstEmitter,
          shockwaveEmitter, debrisEmitter,
          fireEmitter, smokeEmitter, embersEmitter;
    private boolean battleStart = false;
    private InputManager inputManager;
    
    public RobotState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;// passing bulletAppState from My3DGame.Class
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initialize the state
        // declaring the parameter from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.inputManager = this.app.getInputManager();
        inputManager.addMapping("ResetGame", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, "ResetGame");
        state = stateManager;
        bulletAppState.getPhysicsSpace().addCollisionListener(this); // add collisionListener fo physical Space      
        destroyAudio = new AudioNode( assetManager,"Sounds/Effects/Bomb.wav"); //http://soundbible.com/1601-Mario-Jumping.html
        destroyAudio.setVolume(10); // set volume of shooting
        initSparks();
        initBurst();
        initDebris();
        initSmoke();
        initFire();
        initEmbers();
        initShockwave();
    }

    public void createRobot(Vector3f loc) {// loading model robot and add physical effects
        //Vector3f loc = new Vector3f(0,60,0);
        robot = assetManager.loadModel("Models/Oto/Oto.mesh.j3o");       
        robot.setName("robot"); // setName
        robot.scale(1f);        // scaling
        robotNode.attachChild(robot);
        robotNode.setName("robotNode");
        rootNode.attachChild(robotNode); // add robot to robot Node
        robot.setLocalTranslation(loc); // translate robot Node
        robotControl = new BetterCharacterControl(5f, 4, 30f);  // create robot control       
        robotControl.setGravity(new Vector3f(0, -10, 0));     // add gravity
        robot.addControl(robotControl);                       // add control to robot
        control = robot.getControl(AnimControl.class);        // get control of animation
        control.addListener(this);                            
        channel = control.createChannel();                   // create a channel to do animation
        channel.setAnim(ANI_WALK);                           // aet walk animation
        bulletAppState.getPhysicsSpace().add(robotControl);  // add robot control to main physical controller of game
        robotNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);  // make robot can cast or receive shadow
    }

    @Override
    public void update(float tpf) { // update the location of the tank && drop a robot down to the terrain
                                    // from flying tank  in each 5s
        tank = state.getState(TankState.class).returnTank(); // keep update the tankNode
        battleStart = state.getState(NPCMonkeyState.class).returnBattleStartCheck();
        if (battleStart){
            timeBattleStart+=tpf;
            if (timeBattleStart>13){
                timeDropRobot += tpf;         // update the timer
                if (timeDropRobot>2){         // creat robot every 2s
                    createRobot(new Vector3f(tank.getLocalTranslation().x,
                                             tank.getLocalTranslation().y-10, // shift down under the tank 10WU - better view
                                             tank.getLocalTranslation().z));
                    timeDropRobot = 0;
                }
            }
            if (timeBattleStart>45){
                robotNode.detachChildNamed("robot");
                timeBattleStart = 0;
            }
        }else{
            robotNode.detachChildNamed("robot");
            timeBattleStart = 0;
        }
        
        for (int i = 1; i<= robotNode.getQuantity();++i){  // in process in final game           
            if(robotNode.getChild(i-1).getName().equals("robot")){
                Vector3f robotCurrentVector = robotNode.getChild(i-1).getLocalTranslation();
                Vector3f translate = destinationRobotVector.subtract(robotCurrentVector);// vector translation        
                float h = (float) Math.sqrt(Math.pow(translate.x,2)+Math.pow(translate.y,2)+Math.pow(translate.z,2)); // distance of translation
                // Nomalize vector translation
                float xMove = translate.x/h;
                float yMove = translate.y/h;
                float zMove = translate.z/h;
                Vector3f stepVector = new Vector3f(xMove,yMove,zMove);  
                if (h>10){
                    walkDirection.addLocal(stepVector.mult(speed));
                    robotControl.setWalkDirection(walkDirection); // make robots move
                    robotControl.setViewDirection(stepVector);
                }else {
                    robotNode.detachChildAt(i-1);
                    enemyPassingNum =enemyPassingNum + 1;
                }                       
                walkDirection.set(0, 0, 0);
            }
        }

        if (checkCollision){
            timeExplostion += tpf;
            if (timeExplostion>2){
                burstEmitter.killAllParticles();
                sparksEmitter.killAllParticles();
                debrisEmitter.killAllParticles();
                shockwaveEmitter.killAllParticles();
                smokeEmitter.killAllParticles();
                embersEmitter.killAllParticles();
                fireEmitter.killAllParticles();             
                timeExplostion = 0;
                checkCollision = false;
            }
        }
    }
    
    @Override
    public void cleanup() { // function for dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }
    
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) { // not used yet       
    }
    
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {   // not used yet      
    }

    public void collision(PhysicsCollisionEvent event) { // detect the collision of the ball and robot
                                                         // make the collied robot disappear 
        //System.out.println(event.getNodeA().getName());
        //System.out.println(event.getNodeB().getName());
        if ((event.getNodeA().getName().equals("ball")
                && event.getNodeB().getName().equals("robot"))
                || (event.getNodeA().getName().equals("robot")
                && event.getNodeB().getName().equals("ball"))) {                
            if (event.getNodeA().getName().equals("robot")){                     
                bulletAppState.getPhysicsSpace().remove(event.getNodeA().getControl(2));
                event.getNodeA().removeFromParent();                    
                destroyAudio.playInstance();
                collisionNode = event.getNodeA();
                checkCollision = true;
                Vector3f loc = collisionNode.getLocalTranslation();                 
                sparksEmitter.setLocalTranslation(loc);sparksEmitter.emitAllParticles();
                burstEmitter.setLocalTranslation(loc);burstEmitter.emitAllParticles();
                shockwaveEmitter.setLocalTranslation(loc);shockwaveEmitter.emitAllParticles();
                debrisEmitter.setLocalTranslation(loc);debrisEmitter.emitAllParticles();
                fireEmitter.setLocalTranslation(loc);fireEmitter.emitAllParticles();
                embersEmitter.setLocalTranslation(loc);embersEmitter.emitAllParticles();
                smokeEmitter.setLocalTranslation(loc);smokeEmitter.emitAllParticles();
            }            
        }        
    }
    
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("ResetGame") && isPressed){
             enemyPassingNum = 0;
             timeBattleStart = 0;
             
        }
    }
    
    private void initFire() {
        fireEmitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 100);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flame.png"));
        fireEmitter.setMaterial(fireMat);
        fireEmitter.setImagesX(2);
        fireEmitter.setImagesY(2);
        fireEmitter.setRandomAngle(true);
        fireEmitter.setSelectRandomImage(true);
        fireEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        rootNode.attachChild(fireEmitter);

        fireEmitter.setStartColor(new ColorRGBA(1f, 1f, .5f, 1f));
        fireEmitter.setEndColor(new ColorRGBA(1f, 0f, 0f, 0f));
        fireEmitter.setGravity(0, -.5f, 0);
        fireEmitter.setStartSize(1f);
        fireEmitter.setEndSize(0.05f);
        fireEmitter.setLowLife(.5f);
        fireEmitter.setHighLife(2f);
        fireEmitter.getParticleInfluencer().setVelocityVariation(0.3f);
        fireEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 3f, 0));
        fireEmitter.setParticlesPerSec(0);
    }

    private void initBurst() {
        burstEmitter = new ParticleEmitter("Flash", ParticleMesh.Type.Triangle, 5);
        Material burstMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        burstMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flash.png"));
        burstEmitter.setMaterial(burstMat);
        burstEmitter.setImagesX(2);
        burstEmitter.setImagesY(2);
        burstEmitter.setSelectRandomImage(true);
        burstEmitter.setRandomAngle(true);
        rootNode.attachChild(burstEmitter);

        burstEmitter.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1f));
        burstEmitter.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, .25f));
        burstEmitter.setStartSize(.1f);
        burstEmitter.setEndSize(6.0f);
        burstEmitter.setGravity(0, 0, 0);
        burstEmitter.setLowLife(.75f);
        burstEmitter.setHighLife(.75f);
        burstEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2f, 0));
        burstEmitter.getParticleInfluencer().setVelocityVariation(1);
        burstEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO, .5f));
        burstEmitter.setParticlesPerSec(0);

    }

    private void initEmbers() {
        embersEmitter = new ParticleEmitter("embers", ParticleMesh.Type.Triangle, 50);
        Material embersMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        embersMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/embers.png"));
        embersEmitter.setMaterial(embersMat);
        embersEmitter.setImagesX(1);
        embersEmitter.setImagesY(1);
        rootNode.attachChild(embersEmitter);

        embersEmitter.setStartColor(new ColorRGBA(1f, 0.29f, 0.34f, 1.0f));
        embersEmitter.setEndColor(new ColorRGBA(0, 0, 0, 0.5f));
        embersEmitter.setStartSize(1.2f);
        embersEmitter.setEndSize(1.8f);
        embersEmitter.setGravity(0, -.5f, 0);
        embersEmitter.setLowLife(1.8f);
        embersEmitter.setHighLife(5f);
        embersEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 3, 0));
        embersEmitter.getParticleInfluencer().setVelocityVariation(.5f);
        embersEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 2f));
        embersEmitter.setParticlesPerSec(0);

    }

    private void initSparks() {
        sparksEmitter = new ParticleEmitter("Spark", ParticleMesh.Type.Triangle, 20);
        Material sparkMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        sparkMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/spark.png"));
        sparksEmitter.setMaterial(sparkMat);
        sparksEmitter.setImagesX(1);
        sparksEmitter.setImagesY(1);
        rootNode.attachChild(sparksEmitter);

        sparksEmitter.setStartColor(new ColorRGBA(1f, 0.8f, 0.36f, 1.0f)); // orange
        sparksEmitter.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, .5f));
        sparksEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 10, 0));
        sparksEmitter.getParticleInfluencer().setVelocityVariation(1);
        sparksEmitter.setFacingVelocity(true);
        sparksEmitter.setGravity(0, 15, 0);
        sparksEmitter.setStartSize(.5f);
        sparksEmitter.setEndSize(.5f);
        sparksEmitter.setLowLife(.9f);
        sparksEmitter.setHighLife(1.1f);
        sparksEmitter.setParticlesPerSec(0);

    }

    private void initSmoke() {
        smokeEmitter = new ParticleEmitter("Smoke emitter", ParticleMesh.Type.Triangle, 20);
        Material smokeMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        smokeMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/smoketrail.png"));
        smokeEmitter.setMaterial(smokeMat);
        smokeEmitter.setImagesX(1);
        smokeEmitter.setImagesY(3);
        smokeEmitter.setSelectRandomImage(true);
        rootNode.attachChild(smokeEmitter);

        smokeEmitter.setStartColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1f));
        smokeEmitter.setEndColor(new ColorRGBA(.1f, 0.1f, 0.1f, .5f));
        smokeEmitter.setLowLife(4f);
        smokeEmitter.setHighLife(4f);
        smokeEmitter.setGravity(0,2,0);
        smokeEmitter.setFacingVelocity(true);
        smokeEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 6f, 0));
        smokeEmitter.getParticleInfluencer().setVelocityVariation(1);
        smokeEmitter.setStartSize(.5f);
        smokeEmitter.setEndSize(3f);
        smokeEmitter.setParticlesPerSec(0);
    }

    private void initDebris() {
        debrisEmitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 15);
        Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        debrisMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/debris.png"));
        debrisEmitter.setMaterial(debrisMat);
        debrisEmitter.setImagesX(3);
        debrisEmitter.setImagesY(3);
        debrisEmitter.setSelectRandomImage(true);
        debrisEmitter.setRandomAngle(true);
        rootNode.attachChild(debrisEmitter);

        debrisEmitter.setRotateSpeed(FastMath.TWO_PI * 2);
        debrisEmitter.setStartColor(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        debrisEmitter.setEndColor(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        debrisEmitter.setStartSize(.2f);
        debrisEmitter.setEndSize(1f);
        debrisEmitter.setGravity(0,10f,0);
        debrisEmitter.setLowLife(1f);
        debrisEmitter.setHighLife(1.1f);
        debrisEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 15, 0));
        debrisEmitter.getParticleInfluencer().setVelocityVariation(.60f);
        debrisEmitter.setParticlesPerSec(0);

    }

    private void initShockwave() {
        shockwaveEmitter = new ParticleEmitter("Shockwave", ParticleMesh.Type.Triangle, 2);
        Material shockwaveMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        shockwaveMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/shockwave.png"));
        shockwaveEmitter.setImagesX(1);
        shockwaveEmitter.setImagesY(1);
        shockwaveEmitter.setMaterial(shockwaveMat);
        rootNode.attachChild(shockwaveEmitter);

        /* The shockwave faces upward (along the Y axis) to make it appear as
         * a horizontally expanding circle. */
        shockwaveEmitter.setFaceNormal(Vector3f.UNIT_Y);
        shockwaveEmitter.setStartColor(new ColorRGBA(.68f, 0.77f, 0.61f, 1f));
        shockwaveEmitter.setEndColor(new ColorRGBA(.68f, 0.77f, 0.61f, 0f));
        shockwaveEmitter.setStartSize(1f);
        shockwaveEmitter.setEndSize(7f);
        shockwaveEmitter.setGravity(0, 0, 0);
        shockwaveEmitter.setLowLife(1f);
        shockwaveEmitter.setHighLife(1f);
        shockwaveEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        shockwaveEmitter.getParticleInfluencer().setVelocityVariation(0f);
        shockwaveEmitter.setParticlesPerSec(0);
    }
    
    public int returnEnemyPassingNum(){
        return enemyPassingNum;
    }
    
    public int returnTimeBattleStart(){
        return (int)timeBattleStart;
    }
}
