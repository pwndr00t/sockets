 
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
 
import javax.swing.*;
import java.awt.*;
 
import static javafx.concurrent.Worker.State.FAILED;
  
public class Browser extends JFrame {
 
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JPanel panel = new JPanel(new BorderLayout());

    private final JProgressBar progressBar = new JProgressBar();
 
    public Browser() {
        super();
        initComponents();
    }

    
    private void initComponents() {
        createScene();
   
        progressBar.setPreferredSize(new Dimension(150, 18));
   
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(progressBar, BorderLayout.EAST);
 
        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        
        getContentPane().add(panel);
        
        setPreferredSize(new Dimension(480, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

    }
 
    private void createScene() {
 
        Platform.runLater(new Runnable() {
            public void run() {
 
                WebView view = new WebView();
                engine = view.getEngine();
 
                engine.titleProperty().addListener(new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> observable, String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Browser.this.setTitle(newValue);
                            }
                        });
                    }
                });
 
                engine.getLoadWorker().workDoneProperty().addListener(new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                progressBar.setValue(newValue.intValue());
                                progressBar.setVisible(true);
                                if(newValue.intValue() == 100){
                                	progressBar.setVisible(false);
                                } 
                            }
                        });
                    }
                });

                engine.getLoadWorker()
                        .exceptionProperty()
                        .addListener(new ChangeListener<Throwable>() {
 
                            public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
                                if (engine.getLoadWorker().getState() == FAILED) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            JOptionPane.showMessageDialog(
                                                    panel,
                                                    (value != null) ?
                                                    engine.getLocation() + "\n" + value.getMessage() :
                                                    engine.getLocation() + "\nUnexpected error.",
                                                    "Loading error...",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    });
                                }
                            }
                        });

                jfxPanel.setScene(new Scene(view));
            }
        });
    }
 
    public void loadURL() {
        Platform.runLater(new Runnable() {
            public void run() {
                engine.load("https://mede-chat-app.herokuapp.com/");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Browser browser = new Browser();
                browser.setVisible(true);
                browser.loadURL();
           }     
       });
    }
}