package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 * NPCMonkeyState.java loads the monkey model as a NPC in game, implements animation
 * casts,receives the shadow from other elements of game and check if the battle of 
 * game is started or not
 *******************************************************************************/
public class NPCMonkeyState extends AbstractAppState implements AnimEventListener,PhysicsCollisionListener,ActionListener {
    private BulletAppState bulletAppState;
    private Node rootNode, guiNode;
    private Node NPCNode = new Node();
    private SimpleApplication app;
    private AssetManager assetManager;
    private Spatial NPC;
    private BetterCharacterControl NPCControl;
    private AnimControl control;
    private AnimChannel channel;
    private static final String ANI_WALK = "Idle";
    private AppStateManager state;
    private Spatial targetOfMouseClick;
    private boolean battleStart = false;
    private Node playerNode = new Node();
    private int enemyPassingNum;
    private InputManager inputManager;
    private float timeBattleStart = 0f;
    public NPCMonkeyState(BulletAppState bulletAppStater) {
        this.bulletAppState = bulletAppStater;//passing bulletAppState from My3DGame.Class
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initialize the first appearance
        // declaring the parameters from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();        
        this.rootNode = this.app.getRootNode();        
        this.inputManager = this.app.getInputManager();
        this.guiNode = this.app.getGuiNode();
        state = stateManager;
        inputManager.addMapping("ResetGame", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, "ResetGame");
        createNPC(); // creating NPC
    }

    public void createNPC() { // load model boat
        Vector3f loc = new Vector3f(0,5,0);
        NPC = assetManager.loadModel("Models/monkeyExport/Jaime.j3o");
        NPC.setName("NPC");   // setName
        NPC.scale(8f);        // scaling
        NPCNode.attachChild(NPC);     // add character Spatial to NPC Node
        NPC.setLocalTranslation(35,6,-5);  // set location of NPC Spatial&
        NPCNode.setLocalTranslation(loc);
        rootNode.attachChild(NPCNode); // add NPC to NPC Node
        NPCControl = new BetterCharacterControl(1.5f, 4, 30f);  // create NPC control       
        NPCControl.setGravity(new Vector3f(0, -10, 0));     // add gravity
        NPCControl.setViewDirection(new Vector3f(1,0,1));
        NPC.addControl(NPCControl);                       // add control to NPC
        control = NPC.getControl(AnimControl.class);        // get control of animation
        control.addListener(this);                            
        channel = control.createChannel();                   // create a channel to do animation
        channel.setAnim(ANI_WALK);                           // aet walk animation
        bulletAppState.getPhysicsSpace().add(NPCControl);  // add NPC control to main physical controller of game
        NPC.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);  // make NPC can cast or receive shadow
    }

    @Override
    public void update(float tpf) {   // not used yet     
        targetOfMouseClick = state.getState(CharacterState.class).returnTargetOfMouseClick();
        playerNode = state.getState(CharacterState.class).returnCharacter();// get player position
        enemyPassingNum = state.getState(RobotState.class).returnEnemyPassingNum();
        timeBattleStart = state.getState(RobotState.class).returnTimeBattleStart();
        if (NPC.getLocalTranslation().distance(playerNode.getLocalTranslation())<12 && targetOfMouseClick.getName().contains("NPC")){// check if sinbad is close and click on NPC
            battleStart = true;
        }if(enemyPassingNum >= 3){
            battleStart = false;
        }
        if (timeBattleStart>=44){
            battleStart = false;
        }
        System.out.println(battleStart);
    }

    @Override
    public void cleanup() {           // this is for make geo dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }    

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    public void collision(PhysicsCollisionEvent event) {
    }
    
    public boolean returnBattleStartCheck(){
        return battleStart;
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("ResetGame") && isPressed){
            battleStart = false;
        }
    }
}