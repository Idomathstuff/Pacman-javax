package ProjectPacman;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class PacmanController implements GameLoopListener{
    private PacmanModel model;
    private PacmanView view;
    private PacPlayer pacmanEntity;

    private int gameTickCounter;
    private Timer timer;
    
    public PacmanController(PacmanModel model, PacmanView view, PacPlayer pacmanEntity){
        gameTickCounter = 0;
        this.model = model;
        this.view = view;
        this.pacmanEntity = pacmanEntity;
        this.view.resetButton.addActionListener(resetButtonPressed);
        this.view.addKeyListener(arrowKeyPressed); // You need a frame listen to Keys
        timer = new Timer(80, gameLoop); // 12.5 game ticks per second
        this.view.startButton.addActionListener(startButtonPressed);
        timer.start();
    }
    

    final private ActionListener gameLoop = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            onGameTick();
        };
    };

    final private ActionListener startButtonPressed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            timer.start();
            view.requestFocusInWindow();
        };
    };

    final private ActionListener resetButtonPressed = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
            onGameReset();
        }
    };
    
    final private KeyListener arrowKeyPressed = new KeyListener(){
        @Override
        public void keyTyped(KeyEvent e) {
            // Do nothing
        }
        @Override
        public void keyReleased(KeyEvent e) {
            // Do nothing
        }
     
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_RIGHT) {
                pacmanEntity.setDirection(1);
            } else if (keyCode == KeyEvent.VK_UP) {
                pacmanEntity.setDirection(2);
            }else if (keyCode == KeyEvent.VK_LEFT){
                pacmanEntity.setDirection(3);
            }else if (keyCode == KeyEvent.VK_DOWN){
                pacmanEntity.setDirection(4);
            }
        }
    }; 

    @Override
    public void onGameTick() {
        if (!model.checkLossCondition()) {             
            
            // Move pacman
            model.movePacman();
            model.handleGhostPlayerCollision();
            if (model.checkWinCondition()) {
                onGameWin();
            } else if (pacmanEntity.isDead()){
                onPlayerDeath();
                return;
            }
            }
            view.update();

            // Move Ghosts
            if (gameTickCounter%2==0){
                model.moveGhosts();
            }
            gameTickCounter++;
            
            model.handleGhostPlayerCollision();
            if (model.checkWinCondition()) {
                onGameWin();
            } else if (pacmanEntity.isDead()){
                onPlayerDeath();
                return;
            }
            view.update();

        }
    

    @Override
    public void onGameReset() {
        model.resetGame();
        view.clearMessage();
        view.displayMessage("ProjectPacman/assets/reset.png");
        view.clearMessage(1);
        timer.restart();
        view.requestFocusInWindow();
    }
    
    @Override
    public void onGameWin() {
        timer.stop();
        pacmanEntity.setScore(0);
        view.displayMessage("ProjectPacman/assets/YouWon.png");
    }

    @Override
    public void onGameLoss() {
        timer.stop();
        view.displayMessage("ProjectPacman/assets/GameOver.png");
    }

    public void onPlayerDeath(){
        pacmanEntity.setLives(pacmanEntity.getLives()-1);
        if (model.checkLossCondition()){
            onGameLoss();
        }else{
            timer.stop();
            Timer deathTimer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pacmanEntity.revive();
                    
                    // System.out.println(pacmanEntity.getLives());
                    model.resetPositions();
                    timer.start();
                    view.requestFocusInWindow();
                    view.displayMessage("ProjectPacman/assets/Start.png");
                    view.clearMessage(1);
                }
            });
            deathTimer.setRepeats(false);
            deathTimer.start();
        }

    }

}