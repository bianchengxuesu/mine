import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.swing.*;
/**
 * 扫雷主页面，框架
 * @author xuesu
 */
public class MainFrame implements ActionListener {
    private JFrame frame;
    /**
     *重新开始游戏按键
     */
    private JButton reset;
    //private JButton timer,counter;
    /**
     * 定义十行十列的扫雷
     * 定义数组判断是否为雷，定义round数组显示该块儿周围的雷数
     * BOMB为true代表是雷
     */
    private final int row = 10;
    private final int col = 10;
    private final int bombCount = 10 ;
    private final int BOMB = -1;
    /**
     * roundNum 为负数时，此处为零
     */
    private int[][] roundNum = new int[row][col];
    JButton[][] block = new JButton[row][col];
    private boolean[][] isClicked = new boolean[row][col];
    /**
     *  用于bfs
     */
    private int[][] dir={{0,1}, {0,-1}, {1,0}, {-1,0}};

    public MainFrame(){
        /**
         * 设置窗口大小位置和标题
         */
        init();
    }

    public void init(){
        frame = new JFrame("扫雷");
        frame.setBackground(Color.white);
        frame.setSize(500,500);
        frame.setLocation(450,180);
        Container con1 = new Container();
        //JPanel con1 = new JPanel();
        con1.setLayout(new BorderLayout());
        /**
         * 设置重新开始按钮大小和布局
         */
        reset = new JButton("重新开始");

        reset.setSize(10,20);
        con1.add(reset);
        reset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Reset();
            }
        });
//        timer = new JButton("用时");
//        timer.setBounds(10,10,10,20);
//        con1.add(timer);
        frame.add(con1,BorderLayout.NORTH);

        addBlock();
        AddBomb();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    //添加点击块儿
    public void addBlock(){

        Container con = new Container();
        con.setLayout(new GridLayout(row,col));
        frame.add(con,BorderLayout.CENTER);
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                JButton b = new JButton();
                b.setBackground(Color.white);
                //设置为不透明
                b.setOpaque(true);
                b.addActionListener( this);
                block[i][j] = b;
                con.add(b);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton but = (JButton)e.getSource();
        for(int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(but.equals(block[i][j])) {
                    checkLose(i,j);
                }
            }
        }

    }

    /**
     * 添加炸弹
     */
    private void AddBomb(){
        Random rand = new Random();
        int randRow,randCol;
        for(int i = 0; i < bombCount; i++){
            randRow = rand.nextInt(row);
            randCol = rand.nextInt(col);
            if(roundNum[randRow][randCol] != BOMB){
                roundNum[randRow][randCol] = BOMB;
            }else {
                //当位置已被设置，该次计数无效
                i--;
            }
        }
        /*
         * 添加完雷后，将每个方块儿周围的雷的数量初始化
         */
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                //如果当前数
                if(roundNum[i][j] >= 0) {
                    /*
                     * 判断该方块儿周围有多少雷
                     */
                    int temp = 0;
                    /*
                     * 依次为 左，上，左上，右，下，右下，左下，右上
                     */
                    if (i - 1 >= 0 && roundNum[i - 1][j] < 0) temp++;
                    if (j - 1 >= 0 && roundNum[i][j - 1] < 0) temp++;
                    if (i - 1 >= 0 && j - 1 >= 0 && roundNum[i - 1][j - 1] < 0) temp++;
                    if (i + 1 < 10 && roundNum[i + 1][j] < 0) temp++;
                    if (j + 1 < 10 && roundNum[i][j + 1] < 0) temp++;
                    if (i + 1 < 10 && j + 1 < 10 && roundNum[i + 1][j + 1] < 0) temp++;

                    if (i - 1 >= 0 && j + 1 < 10 && roundNum[i - 1][j + 1] < 0) temp++;
                    if (i + 1 < 10 && j - 1 >= 0 && roundNum[i + 1][j - 1] < 0) temp++;

                    roundNum[i][j] = temp;
                }
            }
        }
    }

    private void checkLose(int i,int j){
        if(roundNum[i][j]<0){
            block[i][j].setText("X");
            block[i][j].setBackground(Color.red);
            //设置为不可用
            block[i][j].setEnabled(false);
            int result = JOptionPane.showConfirmDialog(null, "重新开始游戏?", "确认",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if(result == JOptionPane.OK_OPTION){

            }else {
                System.exit(0);
            }
        }else{
            if(roundNum[i][j]>0) {
                block[i][j].setBackground(Color.gray);
                block[i][j].setText(roundNum[i][j] + "");
                isClicked[i][j]=true;

                checkWin();
            }else{
                bfs(i,j,roundNum);

            }

        }
    }

    Queue<int[][]> q = new LinkedList<>();
    public void bfs(int i,int j,int[][] nums){
        if(nums[i][j]>0 || isClicked[i][j] || nums[i][j]<0) return;
        q.offer(nums);
        isClicked[i][j] = true;
        block[i][j].setBackground(Color.gray);
        block[i][j].setText(nums[i][j]+"");

        expand(i,j);
        checkWin();
        while (!q.isEmpty()){
            q.poll();
            for(int a = 0; a < 4; a++){
                if(i+dir[a][0]>=0&&i+dir[a][0]<row&&j+dir[a][1]>=0&&j+dir[a][1]<col)
                    bfs(i+dir[a][0],j+dir[a][1],nums);
            }
        }
    }

    /**
     * 扩展0周围八个方块儿，如果出现非零非雷的，将方块儿打开
     * @param i
     * @param j
     */
    public void expand(int i,int j){
        //上下左右 左上 左下 右下 右上
        int[][] dir2 = {{0,1}, {0,-1}, {1,0}, {-1,0},{-1,1},{-1,-1},{1,-1},{1,1}};
        for(int a = 0; a < 8; a++){
            if(i+dir2[a][0]>=0&&i+dir2[a][0]<row&&j+dir2[a][1]>=0&&j+dir2[a][1]<col) {
                if (roundNum[i + dir2[a][0]][j + dir2[a][1]] > 0) {
                    isClicked[i + dir2[a][0]][j + dir2[a][1]] = true;
                    block[i + dir2[a][0]][j + dir2[a][1]].setBackground(Color.gray);
                    block[i + dir2[a][0]][j + dir2[a][1]].setText(roundNum[i + dir2[a][0]][j + dir2[a][1]] + "");
                }
            }
        }

    }

    public void checkWin(){
        int ans = 0;
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(isClicked[i][j]) {
                    ans++;
                }
            }
        }
        System.out.println(ans);
        if(ans==90){
            int result = JOptionPane.showConfirmDialog(null, "你赢了，重新开始游戏?", "确认",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if(result == JOptionPane.OK_OPTION){
                
            }else {
                System.exit(0);
            }
        }
    }
    /**
     * 计时器
     */
//    static class Timer extends Thread{
//
//    }

    public void Reset() {
        for(int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                isClicked[i][j] = false;
                block[i][j].setBackground(Color.white);
                block[i][j].setText("");
                block[i][j].setEnabled(true);
                roundNum[i][j] = 0;
            }
        }
        AddBomb();
    }
}
