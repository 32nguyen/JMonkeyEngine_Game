package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/******************************************************************************* 
 * Name: ThanhNguyen   
 * ID  : 2451232
 * 31/3/2015
 *******************************************************************************/
public class UserGuiState extends AbstractAppState implements ActionListener{

    private Node rootNode,guiNode;
    private SimpleApplication app;
    private AssetManager assetManager;
    private int enemyPassingNum, timeBattleStart;
    private BitmapText enemyPassingNumText,timeBattleStartText,countBattleTimeText;
    private BitmapFont guiFont,guiFontScroll;
    private InputManager inputManager;
    private AppStateManager state;
    private boolean battleStart = false;
    private AppSettings setting = new AppSettings(true);
    private Picture scroll = new Picture("Scroll");
    private Picture endGameLose = new Picture("endGameLose");
    private Picture endGameWin = new Picture("endGameWin");
    private Picture banner = new Picture("banner");
    private int gameState = 0;
    
    public UserGuiState(AppSettings settings){
        this.setting = settings;
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {// initialize the first appearance
        // declaring the parameters from app
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.assetManager = this.app.getAssetManager();        
        this.rootNode = this.app.getRootNode();        
        this.guiNode = this.app.getGuiNode();
        this.inputManager = this.app.getInputManager();
        state = stateManager;
        inputManager.addMapping("ResetGame", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, "ResetGame");
        userGUI(); // creating 2D user Guide
        
    }

    public void userGUI(){ // make 2D userGUI include Sinbad icon && update enemy passing number
        // Display number of passing enemies text
        guiFont = assetManager.loadFont("Interface/Fonts/Chiller.fnt"); // default set up from book's example
        enemyPassingNumText = new BitmapText(guiFont);
        enemyPassingNumText.setSize(setting.getWidth()/30);
        enemyPassingNumText.move(100, enemyPassingNumText.getLineHeight(),0);
        
        // Display battle starting time counting text
        guiFontScroll = assetManager.loadFont("Interface/Fonts/VinerHandITC.fnt"); 
        timeBattleStartText = new BitmapText(guiFontScroll);
        timeBattleStartText.setSize(setting.getWidth()/20);
        timeBattleStartText.move(setting.getWidth()*0.8f, setting.getHeight()*0.625f,0);
        
        // Display runtime of game text
        countBattleTimeText = new BitmapText(guiFontScroll);
        countBattleTimeText.setSize(setting.getWidth()/20);
        countBattleTimeText.move(setting.getWidth()*3/4, setting.getHeight()*0.1f,0);
        
        
        Picture sinbadLogo = new Picture("Sinbad_logo");
        sinbadLogo.setImage(assetManager, "Interface/Sinbad_logo.png", true);// sinbadLogo is downloaded from http://www.ogre3d.org/
        sinbadLogo.move(2, 2, 0);
        sinbadLogo.setWidth(95);
        sinbadLogo.setHeight(75);
        guiNode.attachChild(sinbadLogo); // attach sinbadLogo to 2D guiNode
        
        // scroll of description
        scroll.setImage(assetManager, "Interface/ScrollGame.png", true);// scroll is downloaded from http://bgfons.com/download/324
        scroll.move(setting.getWidth()*2.5f/4, setting.getHeight()*1/4, 0);
        scroll.setWidth(setting.getWidth()*1.5f/4);
        scroll.setHeight(setting.getHeight()*3/4);
        
        // Guiding Banner
        banner.setImage(assetManager, "Interface/Banner.png", true);// scroll is downloaded from http://bgfons.com/download/324
        banner.move(setting.getWidth()*0.05f, setting.getHeight()*0.85f, 0);
        banner.setWidth(setting.getWidth()*3/4);
        banner.setHeight(setting.getHeight()/10);
        banner.setName("endGameWin");
        guiNode.attachChild(banner);
        
        // ending game announcement with lost
        endGameLose.setImage(assetManager, "Interface/Lose.png", true);// scroll is downloaded from http://bgfons.com/download/324
        endGameLose.move(setting.getWidth()/4, setting.getHeight()/4, 0);
        endGameLose.setWidth(setting.getWidth()*3/4);
        endGameLose.setHeight(setting.getHeight()*3/4);
        endGameLose.setName("endGameLose");
        
        // ending game announcement with victory
        endGameWin.setImage(assetManager, "Interface/Win.png", true);// scroll is downloaded from http://bgfons.com/download/324
        endGameWin.move(setting.getWidth()/4, setting.getHeight()/4, 0);
        endGameWin.setWidth(setting.getWidth()*3/4);
        endGameWin.setHeight(setting.getHeight()*3/4);
        endGameWin.setName("endGameWin");
    }

    @Override
    public void update(float tpf) {   // not used yet     
        enemyPassingNum = state.getState(RobotState.class).returnEnemyPassingNum();
        timeBattleStart = state.getState(RobotState.class).returnTimeBattleStart();
        battleStart = state.getState(NPCMonkeyState.class).returnBattleStartCheck();
        
        if (timeBattleStart>14){
            guiNode.attachChild(countBattleTimeText); 
            countBattleTimeText.setText(""+(timeBattleStart-14));
            countBattleTimeText.setColor(ColorRGBA.Green);
            guiNode.attachChild(enemyPassingNumText);
            enemyPassingNumText.setText("Number Of Passing Enemy  " + enemyPassingNum + "/3");
            enemyPassingNumText.setColor(ColorRGBA.Yellow);
        }    
        System.out.println(timeBattleStart);
        if (battleStart){
            gameState = 0;
            guiNode.detachChild(banner);
            if (timeBattleStart<=14){
                guiNode.attachChild(scroll); // attach sinbadLogo to 2D guiNode
                timeBattleStartText.setText("" +(15-timeBattleStart));
                timeBattleStartText.setColor(ColorRGBA.Red);
                guiNode.attachChild(timeBattleStartText);
            }else {
                guiNode.detachChild(scroll); // deattach sinbadLogo to 2D guiNode
                guiNode.detachChild(timeBattleStartText);                
            }            
        }
        if (enemyPassingNum ==3){
            guiNode.attachChild(endGameLose);
            guiNode.detachChild(banner);
            gameState = 1;
        }else if (timeBattleStart >= 44){
            guiNode.attachChild(endGameWin);
            gameState = 2;
            guiNode.detachChild(banner);
        }        
    }

    @Override
    public void cleanup() {           // this is for make geo dispear from rootNode
        super.cleanup();
        rootNode.detachAllChildren();
    }    

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("ResetGame") && isPressed){
            guiNode.detachChild(endGameLose);
            guiNode.detachChild(endGameWin);
            guiNode.attachChild(banner);
            guiNode.detachChild(enemyPassingNumText);
        }
    }
    
    public int returnGameState(){
        return gameState;
    }
}
