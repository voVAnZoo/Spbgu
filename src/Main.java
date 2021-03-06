import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vova on 19.02.2019.
 */
public class Main extends JFrame{

    public int friq = 0;
    public Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
    public int mouseX = 0;
    public int mouseY = 0;
    public int touch = -1;
    public List<Body> allBody = new ArrayList<Body>();
    public List<Double> l = new ArrayList<Double>();
    public List<Double> k = new ArrayList<Double>();
    public double energy = 0;
    public boolean shift = false;
    public static int type = 1;

    public double mu = 0.5;
    public int delay = 1;

    public double energyLossFloor = 0;
    public double energyLossRoof = 0;
    public double energyLossLeftWall = 0;
    public double energyLossRightWall = 0;

    public boolean floor = false;
    public boolean roof = false;
    public boolean leftWall = false;
    public boolean rightWall = false;

    public boolean timerStart = false;

    public static void main(String[] args)  {
        int n = 0;
        double dt = 0.0015;
        type = 1;
        try {
            n = Integer.parseInt(args[0]);
            dt = Double.parseDouble(args[1]);
            type = Integer.parseInt(args[2]);
        }catch (Exception e){
            if(n == 0){
                n = 1000;
            }
        }

        Main start = new Main(n, dt);
    }

    public Main(int n, double dt){
        super("SPBGU");

        Body.dt = dt;

        init(n);
        GUIBuild();

        Timer timer = new Timer(friq, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double en = 0;
                for(int i = 0;i < allBody.size();i++){

                    if (i != 0){
                        double dl = Math.pow(allBody.get(i).getX()-allBody.get(i - 1).getX(),2) +
                                Math.pow(allBody.get(i).getY()-allBody.get(i - 1).getY(),2);
                        dl = Math.sqrt(dl);
                        double sin = (allBody.get(i).getY()-allBody.get(i - 1).getY())/dl;
                        double cos = (allBody.get(i).getX()-allBody.get(i - 1).getX())/dl;
                        double f = (-1)*k.get(i - 1)*(dl - l.get(i - 1));

                        allBody.get(i).action(f*cos,f*sin);
                        allBody.get(i - 1).action(-1*f*cos,-1*f*sin);
                        allBody.get(i).action(0, Body.g*allBody.get(i).getMass());
                        allBody.get(i).action(-mu*allBody.get(i).getSpeedX(),-mu*allBody.get(i).getSpeedY());

                        en += (k.get(i - 1)*(dl - l.get(i - 1))*(dl - l.get(i - 1)))/2;
                        en += allBody.get(i).getKineticEnergy();

                        en -= allBody.get(i).getMass()*Body.g*allBody.get(i).getY();
                    }
                }

                for(int i = 0;i < allBody.size();i++) {
                    allBody.get(i).action();
                }

                energy = en;
            }
        });

        Thread thread = new Thread(new Runnable() {
            public void run() {

                while (true) {
                    if (timer.isRunning()) {
                        if (!timerStart) {
                            timer.stop();
                        }
                    } else {
                        if (timerStart) {
                            timer.start();
                        }
                    }
                }
            }
        });
        thread.start();

    }

    public void init(int n){
        switch (type){
            case 0:
                initD(n);
                break;
            case 1:
                initN(n);
                break;
            default:
                initD(n);
                break;
        }
    }

    public void initD(int n){
        allBody.clear();
        l.clear();
        k.clear();

        for(int i = 0; i <= n;i++){
            Body a = new Body(sSize.getWidth()/2,sSize.getHeight()/3 + (i*sSize.getHeight()/(3*n)));

            if(i == 0){
                a.setPillar(true);
            }else{
                l.add(sSize.getHeight()/(3*n));
                k.add(70.0);
            }

            allBody.add(a);
        }

    }

    public void initN(int n){
        allBody.clear();
        l.clear();
        k.clear();
        Body.g = 0.0;

        for(int i = 0; i <= n;i++){
            Body a = new Body(20 + (i* sSize.getWidth() - 50*i)/n,sSize.getHeight()/2);

            if(i == 0){
                a.setPillar(true);
            }else{
                l.add(0.0);
                k.add(900.0);
            }

            allBody.add(a);
        }

        allBody.get(n).setPillar(true);
    }

    public void GUIBuild(){
        setBounds(0, 0, sSize.width, sSize.height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        final Font font = new Font("Verdana", Font.PLAIN, 13);
        final JMenuBar menuBar = new JMenuBar();

        final JMenu settingsMenu = new JMenu("settings");
        settingsMenu.setFont(font);

        final JMenu boxMenu = new JMenu("box");
        boxMenu.setFont(font);
//        settingsMenu.add(boxMenu);

        final JCheckBox cbf = new JCheckBox("floor");
        final JSlider sf = new JSlider(JSlider.HORIZONTAL,0,100,0);
        sf.setEnabled(false);
        cbf.addChangeListener(e -> {
            if(cbf.isSelected()){
                floor = true;
                sf.setEnabled(true);
            }else {
                floor = false;
                sf.setEnabled(false);
            }
        });
        sf.addChangeListener(e -> energyLossFloor = ((double) sf.getValue())/100);
        boxMenu.add(cbf);
        boxMenu.add(sf);
        boxMenu.addSeparator();

        final JCheckBox cbr = new JCheckBox("roof");
        final JSlider sr = new JSlider(JSlider.HORIZONTAL,0,100,0);
        sr.setEnabled(false);
        cbr.addChangeListener(e -> {
            if(cbr.isSelected()){
                roof = true;
                sr.setEnabled(true);
            }else {
                roof = false;
                sr.setEnabled(false);
            }
        });
        sr.addChangeListener(e -> energyLossRoof = ((double) sr.getValue())/100);
        boxMenu.add(cbr);
        boxMenu.add(sr);
        boxMenu.addSeparator();

        final JCheckBox cbwr = new JCheckBox("left wall");
        final JSlider swr = new JSlider(JSlider.HORIZONTAL,0,100,0);
        swr.setEnabled(false);
        cbwr.addChangeListener(e -> {
            if(cbwr.isSelected()){
                rightWall = true;
                swr.setEnabled(true);
            }else {
                rightWall = false;
                swr.setEnabled(false);
            }
        });
        swr.addChangeListener(e -> energyLossRightWall = ((double) sf.getValue())/100);
        boxMenu.add(cbwr);
        boxMenu.add(swr);
        boxMenu.addSeparator();

        final JCheckBox cbwl = new JCheckBox("right wall");
        final JSlider swl = new JSlider(JSlider.HORIZONTAL,0,100,0);
        swl.setEnabled(false);
        cbwl.addChangeListener(e -> {
            if(cbwl.isSelected()){
                leftWall = true;
                swl.setEnabled(true);
            }else {
                leftWall = false;
                swl.setEnabled(false);
            }
        });
        swl.addChangeListener(e -> energyLossLeftWall = ((double) swl.getValue())/100);
        boxMenu.add(cbwl);
        boxMenu.add(swl);

        final JMenuItem restartItem = new JMenuItem("restart");
        restartItem.setFont(font);
        settingsMenu.add(restartItem);
        restartItem.addActionListener(e -> init(allBody.size()-1));

        final JMenuItem amountItem = new JMenuItem("set amount");
        amountItem.setFont(font);
        settingsMenu.add(amountItem);
        amountItem.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(null,"enter the amount of bodies");
            int n;
            try {
                n = Integer.parseInt(s);
                init(n);
            }catch (Exception ignored){}
        });

        settingsMenu.addSeparator();

        final JMenu gMenu = new JMenu("g ( " + Body.g /10 + " )");
        gMenu.setFont(font);
        settingsMenu.add(gMenu);
        final JSlider gSlider = new JSlider(JSlider.HORIZONTAL,0,500, (int)Body.g);
        gSlider.addChangeListener(e -> {
            Body.g = gSlider.getValue();
            gMenu.setText("g ( " + Body.g/10 + " )");
        });
        gMenu.add(gSlider);

        final JMenu muMenu = new JMenu("nu (" + mu + " )");
        gMenu.setFont(font);
        settingsMenu.add(muMenu);
        final JSlider muSlider = new JSlider(JSlider.HORIZONTAL,0,100, (int)(mu*100));
        muSlider.addChangeListener(e -> {
            mu = (double)muSlider.getValue() / 100;
            muMenu.setText("nu (" + mu + " )");
        });
        muMenu.add(muSlider);

        final JMenu delayMenu = new JMenu("delay (" + delay + " )");
        delayMenu.setFont(font);
        settingsMenu.add(delayMenu);
        final JSlider delaySlider = new JSlider(JSlider.HORIZONTAL, 0, 30, delay);
        delaySlider.addChangeListener(e -> {
            delay = delaySlider.getValue();
            delayMenu.setText("delay (" + delay + " )");
        });
        delayMenu.add(delaySlider);

        settingsMenu.addSeparator();

        final JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(font);
        settingsMenu.add(exitItem);
        exitItem.addActionListener(e -> System.exit(0));

        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);
        setLocationRelativeTo(null);

        final JButton b = new JButton("start");
        b.setBounds(0,0,100,100);
        b.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    shift = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    shift = false;
                }
            }
        });
        b.addActionListener(e -> {
            if (timerStart) {
                b.setText("start");
            } else {
                b.setText("stop ");
            }
            timerStart = !timerStart;
        });

        final JMenu viewMenu = new JMenu("view");
        viewMenu.setFont(font);
        menuBar.add(viewMenu);

        final JCheckBox energyChek = new JCheckBox("energy");
        viewMenu.add(energyChek);

        final JCheckBox pilarChek = new JCheckBox("show pillar");
        pilarChek.setSelected(true);
        viewMenu.add(pilarChek);

        final JCheckBox bodyChek = new JCheckBox("show body");
//        bodyChek.setSelected(true);
        viewMenu.add(bodyChek);

        final JCheckBox springChek = new JCheckBox("show spring");
        springChek.setSelected(true);
        viewMenu.add(springChek);

        final JMenu conditionsMenu = new JMenu("initial conditions");
        conditionsMenu.setFont(font);
        menuBar.add(conditionsMenu);

        final JMenuItem restNItem = new JMenuItem("restart as string");
        restNItem.addActionListener(e -> {
            type = 1;
            init(allBody.size()-1);
        });
        conditionsMenu.add(restNItem);

        final JMenuItem restDItem = new JMenuItem("restart as spring");
        restDItem.addActionListener(e -> {
            type = 0;
            init(allBody.size()-1);
        });
        conditionsMenu.add(restDItem);

        final JSlider js = new JSlider(JSlider.HORIZONTAL,0,100,1);
        js.setValue((int) (Body.dt * 10000));

        js.addChangeListener(e -> Body.dt = ((double) js.getValue()) /10000);
        menuBar.add(js);

        final JPanel l = new JPanel() {
            Graphics2D g2;

            protected void paintComponent(Graphics g) {

                super.paintComponent(g);
                g2 = (Graphics2D) g;
                for(int i = 0;i < allBody.size();i++){

                    g2.setColor(Color.black);
                    if(pilarChek.isSelected()){
                        if(allBody.get(i).isPillar()){
                            allBody.get(i).draw(g2);
                        }
                    }

                    if(bodyChek.isSelected()) {
                        if(!allBody.get(i).isPillar()){
                            allBody.get(i).draw(g2);
                        }
                    }

                    if (springChek.isSelected()) {
                        if (i != 0) {
                            g2.drawLine((int) allBody.get(i).getX(), (int) allBody.get(i).getY(),
                                    (int) allBody.get(i - 1).getX(), (int) allBody.get(i - 1).getY());
                        }
                    }
                }

                g2.setColor(Color.RED);

                if(floor){
                    g2.fillRect(-30,getRootPane().getHeight() - 30,
                            getRootPane().getWidth() + 30,getRootPane().getHeight() - 30);
                }

                if(roof){
                    g2.fillRect(-30,getRootPane().getHeight() - 30,
                            getRootPane().getWidth() + 30,getRootPane().getHeight() - 30);
                }

                if(leftWall){
                    g2.fillRect(-30,getRootPane().getHeight() - 30,
                            getRootPane().getWidth() + 30,getRootPane().getHeight() - 30);
                }

                if(rightWall){
                    g2.fillRect(-30,getRootPane().getHeight() - 30,
                            getRootPane().getWidth() + 30,getRootPane().getHeight() - 30);
                }

                if(energyChek.isSelected()) {
                    g2.drawString(Double.toString(energy), 10, 10);
                }

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                repaint();
            }
        };
        menuBar.add(b);
        l.setLayout(null);
        setContentPane(l);

        l.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {

                if (shift){

                    if (Math.abs(mouseX - e.getX()) > Math.abs(mouseY - e.getY())){
                        if (touch != -1) {
                            allBody.get(touch).setXY(e.getX(), mouseY);
                        }
                    }else {
                        if (Math.abs(mouseX - e.getX()) < Math.abs(mouseY - e.getY())){
                            if (touch != -1) {
                                allBody.get(touch).setXY(mouseX, e.getY());
                            }
                        }else {
                            if (touch != -1) {
                                allBody.get(touch).setXY(e.getX(), e.getY());
                            }
                        }
                    }

                }else {
                    if (touch != -1) {

                        allBody.get(touch).setXY(e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        l.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                for(int i = 0;i < allBody.size();i++){
                    if(Math.pow(allBody.get(i).getX() - e.getX(),2) +
                            Math.pow(allBody.get(i).getY() - e.getY(),2) <= 100){
                        touch = i;
                        allBody.get(i).setTaken(true);

                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (touch != -1) {
                    allBody.get(touch).setTaken(false);
                    touch = -1;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        setVisible(true);
    }
}
