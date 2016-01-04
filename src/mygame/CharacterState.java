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
import com.jme3.audio.Listener;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * CharacterState.java creates the character by:
 * -    loading the Sinbad model, implements AnimEventListener 
 *      and ActionlListener to get input from user, apply the animations
 * -    Make 2D userGUI
 * -    Add emmiter and fire Effect, footsteps sound, casting shadow
 * -    Make character be able to shoot balls
 * -    Control character's movement by mouse click
 *******************************************************************************/

public class CharacterState extends AbstractAppState implements AnimEventListener,ActionListener {
    

    private Camera cam;
    private Node rootNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager state;
    // parameters for aniamtions 
    private static final String IDLE_TOP = "IdleTop";
    private static final String RUN_BASE = "RunBase";
    private static final String RUN_TOP = "RunTop";
    private static final String IDLE_BASE = "IdleBase"; 
    private static final String SLICEHORIZONTAL = "SliceHorizontal";    
    private AnimControl control;
    private AnimChannel channel;
    private AnimChannel channel1;// use multilple channels to combine animations
    // pamameters for character's control
    private BetterCharacterControl playerControl;
    private Spatial character;
    private Spatial target;
    private Vector3f characterDestinateVector = new Vector3f(); // new location of character after click
    private Vector3f vectorTemp = new Vector3f();
    private boolean move;
    private Node playerNode = new Node();
    private float speed = 10f;
    private Vector3f walkDirection = new Vector3f(0,0,0);
    private Vector3f translate = new Vector3f(0,0,0);
    // parameters for Physical effects and character control
    private BulletAppState bulletAppState;    
    private RigidBodyControl ballPhy;
    private Vector3f shootLinear;
    private Vector3f shootingVector;
    // parameters audio effects and 2D UserGuide
    private AudioNode shootingAudio,stepsAudio;
    private Listener listener;

    public CharacterState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;// passing bulletAppState from My3DGame.Class
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// set the inital state
        // declaring the parameter from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();
        this.rootNode = this.app.getRootNode();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();        
        this.listener = this.app.getListener();
        state = stateManager;
        createCharacter();  // creating character
        
        
        inputManager.setCursorVisible(true); // make cursor is visible to use
        // add mapping to user's inputs
        inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Click");
        inputManager.addMapping("SliceVertical", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "SliceVertical");
        inputManager.addMapping("shoot", new KeyTrigger(KeyInput.KEY_SPACE)); 
        inputManager.addListener(this, "shoot");

        control = character.getControl(AnimControl.class); // get animation control for character
        control.addListener(this);    //  add Listener to control 
        //for (String anim : control.getAnimationNames()) {
        //    System.out.println(anim);
        //}
        // set ilde animation for first appearance
        channel = control.createChannel();  // create channel
        channel.setAnim(IDLE_TOP);          // set idle animation on top of character body
        channel1 = control.createChannel(); // create another chanel
        channel1.setAnim(IDLE_BASE);        // set idle animation on base of character body      
        // creating shooting audio and footsteps audio
        shootingAudio = new AudioNode( assetManager,"Sounds/Effects/Shoot.wav");//http://soundbible.com/1601-Mario-Jumping.html
        shootingAudio.setVolume(100); // set volume of shooting
        stepsAudio = new AudioNode(assetManager,"Sounds/Effects/Footsteps.ogg", false);// Music sound suported by JME
        stepsAudio.setVolume(15);   // set volume of footsteps
        stepsAudio.setLooping(true);
        stepsAudio.setPitch(2.0f);  // set time difference between 2 sounds
    }

    public void createCharacter() { // create Sinbad model, rotate, translate, scale, add physical control and cast shadow 
        
        Vector3f loc = new Vector3f(0,10,0);
        character = (Spatial)assetManager.loadModel("Models/Sinbad/Sinbad.mesh.j3o");// loading Sinbad from J3M AssetPack
        //character.rotate(0, FastMath.DEG_TO_RAD * 180, 0);// just keep not rotate
        character.scale(1);  
        playerNode.attachChild(character);     // add character Spatial to character Node
        character.setLocalTranslation(0,5,0);  // set location of character Spatial&
        playerNode.setLocalTranslation(loc);   // translate character Node to make sure character is on top of terrain
        //System.out.println(playerNode.getLocalTranslation());
        playerControl = new BetterCharacterControl(1.5f, 4, 30f); // create plater control
        playerNode.setName("Sinbad");                             // set Name 
        playerControl.setGravity(new Vector3f(0, -10, 0));        // add Gravity
        playerNode.addControl(playerControl);                     // add control to character Node
        bulletAppState.getPhysicsSpace().add(playerControl);      // add character control to main physical controller of game
        rootNode.attachChild(playerNode);                         // add character Node to main rootNode  
        playerNode.setShadowMode(ShadowMode.CastAndReceive);      // set character Node can cast or receive shadow           
    }
    
    public void shootCannonBall() {// create a ball, add shadow, physics (forces && rigid body)
        // Create a cannon ball geometry
        Sphere ballMesh = new Sphere(32,32, 1f);
        ballMesh.setTextureMode(Sphere.TextureMode.Projected);
        Geometry ballGeo = new Geometry("ball", ballMesh);
        //Material stoneMat  = assetManager.loadMaterial("Models/Pebbles/Pebbles.j3m");// model suported by JME
        Material stoneMat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md");

        stoneMat.setBoolean("UseMaterialColors",true);
        stoneMat.setColor("Ambient", ColorRGBA.randomColor());
        stoneMat.setColor("Diffuse", ColorRGBA.randomColor() );
        stoneMat.setColor("Specular", ColorRGBA.randomColor() );
        stoneMat.setFloat("Shininess", 8f);
        ballGeo.setMaterial(stoneMat);
        ballGeo.setLocalTranslation(shootLinear);
        ballGeo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        rootNode.attachChild(ballGeo);
        // Create physical cannon ball and add to physics space        
        ballPhy = new RigidBodyControl(1f);        
        ballGeo.addControl(ballPhy);        
        bulletAppState.getPhysicsSpace().add(ballPhy);    
        ballPhy.setCcdSweptSphereRadius(.1f);
        ballPhy.setCcdMotionThreshold(0.001f);
        ballPhy.setGravity(new Vector3f(0, -20, 0));
        /** Accelerate the physical ball in camera direction to shoot it! */
        ballPhy.setFriction(1.2f);
        ballPhy.setLinearVelocity(shootingVector.mult(2)); // set direction of ball when being shoot
    }
    
    @Override
    public void update(float tpf) { //update tect in userGUI, calculate step vector for smooth movement
                                    //update shooting vector for the balls & make the sound be heard from character's location
        
        
        Vector3f stepVector = new Vector3f(0,0,0);           
        if (move) {         
            Vector3f characterCurrentVector = playerNode.getLocalTranslation();
            translate = characterDestinateVector.subtract(characterCurrentVector);// vector translation        
            float h = (float) Math.sqrt(Math.pow(translate.x,2)+Math.pow(translate.y,2)+Math.pow(translate.z,2)); // distance of translation
            // Nomalize vector translation
            float xMove = translate.x/h;
            float yMove = translate.y/h;
            float zMove = translate.z/h;
            // check if character goes to higher ground, stepVector.y is set 0 to avoid jummbing up to air
            // since gravity effect. For each loop, the stepVector is recalculated and added some y
            // to going up, so set y = 0 to avoid this effect.
            if (characterCurrentVector.y<characterDestinateVector.y){
                yMove = 0; 
            }
            // update new translate vector
            stepVector = new Vector3f(xMove,yMove,zMove);  
            walkDirection.addLocal(stepVector.mult(speed));
            if (h>0.3){ // check to avoid rapidly rotating of lookAt when character arrives
                character.lookAt(stepVector.mult(1000), Vector3f.UNIT_Y);// keep upvector is unit Y so character keeps standing on ground
            }            
        }
        playerControl.setWalkDirection(walkDirection); // make character move
        walkDirection.set(0, 0, 0);      

        shootingVector = translate;                             // set shooting vector
        shootLinear = new Vector3f(playerNode.getLocalTranslation().x,
                                   playerNode.getLocalTranslation().y+7, // shift up for further shooting
                                   playerNode.getLocalTranslation().z);
        listener.setLocation(playerNode.getLocalTranslation());          // add sound listener right on character location
    }
    @Override
    public void cleanup() { // make dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {// track the cycle of animation
                                                                                            // & shooting ball, make shooting sound
        if (animName.equals(SLICEHORIZONTAL)) {
            shootCannonBall();           // Create ball and shooting 
            shootingAudio.playInstance();// macthing with shooting sound 
        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {// track the change of animation
        // not used yet
    }

    public void onAction(String name, boolean isPressed, float tpf) {// track the user input to update the character movement location
                                                                     // & do animation with sound
                                            
        if (name.equals("Click")&& isPressed){ //isPressed to test if button is only pressed
            CollisionResults results = new CollisionResults(); // new collision
            // convert 2D vector from clicking to 3D 
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 0f);// right on CCD
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 1f).subtractLocal(click3d); //before CCD 1 UNIT in Z        
            Ray ray = new Ray(click3d, dir.normalizeLocal());// should normalized in order to collide with mesh of terrain
            rootNode.collideWith(ray, results);
            if (results.size() > 0) {// results size is the number of the faces collided with
                target = results.getClosestCollision().getGeometry().getParent();// get parent as spatial                     
                if (target.getName().contains("terrain")){
                     characterDestinateVector = results.getClosestCollision().getContactPoint();
                     vectorTemp.x = characterDestinateVector.x;
                     vectorTemp.y = characterDestinateVector.y+5.5f;// shift up from ground to get real coordination
                     vectorTemp.z = characterDestinateVector.z;
                     channel.setAnim(RUN_TOP);      // do running animation on top and base when destination is set
                     channel1.setAnim(RUN_BASE);                     
                     move = true;                   // set false to activate moving process in update loop                   
                     stepsAudio.setTimeOffset(0.5f); // set time offset of sound for matching with initial animation
                     stepsAudio.play();              // play as prebuffered instance
                }
             }           
        }
        if (name.equals("SliceVertical") && isPressed) {// check T button if ispresed
            if (!channel.getAnimationName().equals(SLICEHORIZONTAL)) {
                channel.setAnim(SLICEHORIZONTAL);// Add slice animation to SPACE key            
            }             
        }
        if(!isPressed){// check button is pressed of not
            channel.setAnim(IDLE_TOP);         // do idle animation
            channel1.setAnim(IDLE_BASE);
            move = false;                      // set false to deactivate moving process in update loop 
            stepsAudio.stop();                 // stop footsteps audio during idle animation
        }
    }

    public Node returnCharacter(){// return the character geometry to update the current location of character to NPC's control
        return playerNode;
    }
    public Spatial returnTargetOfMouseClick(){
        return target;
    }
}