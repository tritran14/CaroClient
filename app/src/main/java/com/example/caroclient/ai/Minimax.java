package com.example.caroclient.ai;

import com.example.caroclient.model.AIScore;
import com.example.caroclient.model.Caro;
import com.example.caroclient.model.Point;

import java.util.*;

public class Minimax {
    private static final int DEEP=4;
    private static final int INF=2000000005;
    private static int[][] board=new int[Caro.N][Caro.N];
    private static int[][] used=new int[Caro.N][Caro.N];

    private final int FiveInRow=10000000;
    private final int LiveFour=150000;
    private final int DeadFour=75000;
    private final int LiveThree=7500;
    private final int DeadThree=1500;
    private final int LiveTwo=760;
    private final int DeadTwo=390;
    private final int LiveOne=10;
    private final int DeadOne=6;

    private Random random=new Random();
    boolean[][][] vis=new boolean[Caro.N][Caro.N][4];

    int reverse(int x){
        if(x==100) return 101;
        return 100;
    }

    public Point CPUMinimaxTurn(int boardd[],boolean isMaximizingPlayer,int alpha,int beta,int playerSign){
        convertToBoard2D(boardd);
        AIScore ret=minimax(board,0,isMaximizingPlayer,alpha,beta,playerSign,-1,-1);
        return new Point(ret.getX(),ret.getY());
    }


    int getValue(int board[][],int playerSign){
        resetVis();
        int ans=0;
        for(int i=0;i<Caro.N;++i){
            for(int j=0;j<Caro.N;++j){
                int coaf=playerSign==board[i][j]?1:-1;
                ans+=coaf*calcScore(board,vis,i,j);
            }
        }
        return ans;
    }

    AIScore minimax(int board[][], int depth, boolean isMaximizingPlayer, int alpha, int beta, int playerSign, int prevX, int prevY){
        if((depth==DEEP)||((prevX!=-1&&prevY!=-1)&&checkWin(prevX,prevY))){
            int vl=getValue(board,playerSign);
            return new AIScore(vl,-1,-1);
        }
        int bestVal=0;
        int x=-1;
        int y=-1;
        if(isMaximizingPlayer){
            bestVal=-INF;
            List<Point> candidates=spreadCandidates(board);
            if(candidates.isEmpty()&&board[0][0]==0){
                int randomX=random.nextInt(1)+(Caro.N/2-1);
                int randomY=random.nextInt(1)+(Caro.N/2-1);
                candidates.add(new Point(randomX,randomY));
            }
            for(Point point:candidates){
                board[point.x][point.y]=playerSign;
                int val=minimax(board,depth+1,false,alpha,beta,playerSign, point.x, point.y).getVal();
                board[point.x][point.y]=convertToInt(point.x, point.y);
//                bestVal=Math.max(bestVal,val);
                if(val>bestVal){
                    bestVal=val;
                    x= point.x;
                    y= point.y;
                }
                alpha=Math.max(alpha,bestVal);
                if(beta<=alpha) break;
            }
        }
        else{
            bestVal=INF;
            List<Point> candidates=spreadCandidates(board);
            if(candidates.isEmpty()&&board[0][0]==0){
                int randomX=random.nextInt(1)+(Caro.N/2-1);
                int randomY=random.nextInt(1)+(Caro.N/2-1);
                candidates.add(new Point(randomX,randomY));
            }
            for(Point point:candidates){
                board[point.x][point.y]=reverse(playerSign);
                int val=minimax(board,depth+1,true,alpha,beta,playerSign, point.x, point.y).getVal();
                board[point.x][point.y]=convertToInt(point.x, point.y);
//                bestVal=Math.min(bestVal,val);
                if(val<bestVal){
                    bestVal=val;
                    x= point.x;
                    y= point.y;
                }
                beta=Math.min(beta,bestVal);
                if(beta<=alpha) break;
            }
        }
        return new AIScore(bestVal,x,y);
    }

    void resetVis(){
        for(int i=0;i<Caro.N;++i){
            for(int j=0;j<Caro.N;++j){
                for(int k=0;k<4;++k){
                    vis[i][j][k]=false;
                }
            }
        }
    }

    void resetUsed(){
        for(int i=0;i<Caro.N;++i){
            for(int j=0;j<Caro.N;++j) used[i][j]=0;
        }
    }

    int convertToInt(int x,int y){
        return x*Caro.N+y%Caro.N;
    }

    void convertToBoard2D(int boardd[]){
        for(int i=0;i<Caro.N;++i){
            for(int j=0;j<Caro.N;++j){
                board[i][j]=boardd[i*Caro.N+j%Caro.N];
            }
        }
    }

    boolean isUsed(int x,int y){
        return board[x][y]!=convertToInt(x,y);
    }

    List<Point> spreadCandidates(int board[][]){
        List<Point> ret=new ArrayList<>();
        boolean[][] vis=new boolean[Caro.N][Caro.N];
        for(int x=0;x< Caro.N;++x){
            for(int y=0;y<Caro.N;++y){
                if(board[x][y]>=100){
                    for(int dx=x-1;dx<=x+1;++dx){
                        for(int dy=y-1;dy<=y+1;++dy){
                            if(inSide(dx,dy)){
                                if(board[dx][dy]<100&&!vis[dx][dy]){
                                    ret.add(new Point(dx,dy));
                                    vis[dx][dy]=true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    // visDir[N][N][4] 4 dir for continuous line




    // ..X.XXX...XXX xu ly diem cac doan roi rac
    private int getValue2SegmentLines(int lenStartLine,int lenSpace,int lenEndLine,int borderCnt){
        if(lenStartLine>3&&lenEndLine>1&&lenSpace==1&&borderCnt==2){
            return DeadFour*2/3;
        }
        else if(lenStartLine>1&&lenEndLine>3&&lenSpace==1&&borderCnt==2){
            return DeadFour*2/3;
        }

        else if(lenStartLine==3&&lenEndLine==1&&lenSpace==1&&borderCnt==2){
            return DeadFour*2/3;
        }
        else if(lenStartLine==1&&lenEndLine==3&&lenSpace==1&&borderCnt==2){
            return DeadFour*2/3;
        }

        else if(lenStartLine==3&&lenEndLine==1&&lenSpace==1&&borderCnt==1){
            return DeadThree*2/3;
        }
        else if(lenStartLine==1&&lenEndLine==3&&lenSpace==1&&borderCnt==1){
            return DeadThree*2/3;
        }

        else if(lenStartLine==2&&lenEndLine==1&&lenSpace==1&&borderCnt==2){
            return DeadThree*2/3;
        }
        else if(lenStartLine==1&&lenEndLine==2&&lenSpace==1&&borderCnt==1){
            return DeadThree*2/3;
        }

        else if(lenStartLine==2&&lenEndLine==2&&lenSpace==1&&borderCnt==2){
            return LiveThree*3/4;
        }

        else if(lenStartLine==2&&lenEndLine==2&&lenSpace==1&&borderCnt==1){
            return DeadThree*3/4;
        }

        else if(lenStartLine==1&&lenEndLine==1&&lenSpace==1&&borderCnt==2){
            return LiveTwo*2/3;
        }

        else if(lenStartLine==1&&lenEndLine==1&&lenSpace==1&&borderCnt==1){
            return DeadTwo*2/3;
        }
        return 0;
    }
    // ...xxxxx.. xu ly diem cac doan lien tuc
    private int getValueContinuousLine(int lenLine,int borderCnt){
        if(lenLine>=5){
            return FiveInRow;
        }
        else if(lenLine==4&&borderCnt==2){
            return LiveFour;
        }
        else if(lenLine==4&&borderCnt==1){
            return DeadFour;
        }
        else if(lenLine==3&&borderCnt==2){
            return LiveThree;
        }
        else if(lenLine==3&&borderCnt==1){
            return DeadThree;
        }
        else if(lenLine==2&&borderCnt==2){
            return LiveTwo;
        }
        else if(lenLine==2&&borderCnt==1){
            return DeadTwo;
        }
        else if(lenLine==1&&borderCnt==2){
            return LiveOne;
        }
        else if(lenLine==1&&borderCnt==1){
            return DeadOne;
        }
        return 0;
    }

    public int calcScore(int[][] board,boolean vis[][][],int x,int y){
        int lenLine=0;
        int borderCnt=0;
        int tot=0;

        // check horizontal
        int num=board[x][y];
        int i=y;
        int leftmost=y;
        while(i>=0){
            if(board[x][i]==num&&!vis[x][i][0]){
                leftmost=i;
                vis[x][i][0]=true;
            }
            else{
                break;
            }
            --i;
        }
        i=y;
        int righmost=y;

        // reset
        vis[x][i][0]=false;

        while(i<Caro.N){
            if(board[x][i]==num&&!vis[x][i][0]){
                righmost=i;
                vis[x][i][0]=true;
            }
            else{
                break;
            }
            ++i;
        }
        lenLine=Math.abs(righmost-leftmost)+1;
        if(isGoodBorder(board,x,leftmost-1)) ++borderCnt;
        if(isGoodBorder(board,x,righmost+1)) ++borderCnt;

        tot+=getValueContinuousLine(lenLine,borderCnt);

//        if(Math.abs(righmost-leftmost)+1>=5){
//            System.out.println("leftmost : "+leftmost+" rightmost : "+righmost);
//            System.out.println("# win horizontal");
//            return FiveInRow;
//        }


        // ----------------------------fixxxxxxxx---------------------------------------------

        lenLine=0;
        borderCnt=0;


        // check vertical
        i=x;
        int topmost=x;
        while(i>=0){
            if(board[i][y]==num&&!vis[i][y][2]){
                topmost=i;
                vis[i][y][2]=true;
            }
            else{
                break;
            }
            --i;
        }
        i=x;
        int botmost=x;

        // reset
        vis[i][y][2]=false;

        while (i<Caro.N){
            if(board[i][y]==num&&!vis[i][y][2]){
                botmost=i;
                vis[i][y][2]=true;
            }
            else{
                break;
            }
            ++i;
        }

        lenLine=Math.abs(botmost-topmost)+1;
        if(isGoodBorder(board,topmost-1,y)) ++borderCnt;
        if(isGoodBorder(board,botmost+1,y)) ++borderCnt;

        tot+=getValueContinuousLine(lenLine,borderCnt);

//        if(Math.abs(botmost-topmost)+1>=5) {
//            System.out.println("# win vertical");
//            return true;
//        }

        // ----------------------------fixxxxxxxx---------------------------------------------
        // check cross left-top -> right-bottom
        // -> left-top

        lenLine=0;
        borderCnt=0;

        i=x;
        int j=y;
        Point curPoint=new Point(x,y);
        while(i>=0&&j>=0){
            if(board[i][j]==num&&!vis[i][j][3]){
                curPoint.setX(i);
                curPoint.setY(j);
                vis[i][j][3]=true;
            }
            else{
                break;
            }
            --i;
            --j;
        }
        i=x;
        j=y;

        // reset
        vis[i][j][3]=false;

        // -> right-bottom
        Point curPoint1=new Point(x,y);
        while(i<Caro.N&&j<Caro.N){
            if(board[i][j]==num&&!vis[i][j][3]){
                curPoint1.setX(i);
                curPoint1.setY(j);
                vis[i][j][3]=true;
            }
            else{
                break;
            }
            ++i;
            ++j;
        }

        lenLine=Math.abs(curPoint.getX()-curPoint1.getX())+1;
        if(isGoodBorder(board,curPoint.getX()-1, curPoint.getY()-1)) ++borderCnt;
        if(isGoodBorder(board,curPoint1.getX()+1, curPoint1.getY()+1)) ++borderCnt;

        tot+=getValueContinuousLine(lenLine,borderCnt);

//        if(Math.abs(curPoint.getX()-curPoint1.getX())+1>=5){
//            System.out.println("left-top and right-bottom");
//            return true;
//        }

        // -----------------------------------------fixx------------------------------
        // check cross right-top -> left-bottom
        // -> right-top

        lenLine=0;
        borderCnt=0;

        i=x;
        j=y;

        curPoint.setX(x);
        curPoint.setY(y);
        while(i>=0&&j<Caro.N){
            if(board[i][j]==num&&!vis[i][j][1]){
                curPoint.setX(i);
                curPoint.setY(j);
                vis[i][j][1]=true;
            }
            else{
                break;
            }
            --i;
            ++j;
        }
        // -> left-bottom
        i=x;
        j=y;

        vis[i][j][1]=false;

        curPoint1.setX(x);
        curPoint1.setY(y);
        while(i<Caro.N&&j>=0){
            if(board[i][j]==num&&!vis[i][j][1]){
                curPoint1.setX(i);
                curPoint1.setY(j);
                vis[i][j][1]=true;
            }
            else{
                break;
            }
            ++i;
            --j;
        }

        lenLine=Math.abs(curPoint.getX()- curPoint1.getX())+1;
        if(isGoodBorder(board,curPoint.getX()-1, curPoint.getY()+1)) ++borderCnt;
        if(isGoodBorder(board,curPoint1.getX()+1, curPoint1.getY()-1)) ++borderCnt;

        tot+=getValueContinuousLine(lenLine,borderCnt);

//        if(Math.abs(curPoint.getX()- curPoint1.getX())+1>=5){
//            System.out.println("win right-top and left-bottom");
//            return true;
//        }

        return tot;

    }

    public boolean checkWin(int x,int y){
        // check horizontal
        int num=board[x][y];
        int i=y;
        int leftmost=y;
        while(i>=0){
            if(board[x][i]==num){
                leftmost=i;
                if(Math.abs(leftmost-y)+1>=5){
//                    System.out.println("# win left");
                    return true;
                }
            }
            else{
                break;
            }
            --i;
        }
        i=y;
        int righmost=y;
        while(i<Caro.N){
            if(board[x][i]==num){
                righmost=i;
                if(Math.abs(righmost-y)+1>=5){
//                    System.out.println("# win right");
                    return true;
                }
            }
            else{
                break;
            }
            ++i;
        }
        if(Math.abs(righmost-leftmost)+1>=5){
//            System.out.println("leftmost : "+leftmost+" rightmost : "+righmost);
//            System.out.println("# win horizontal");
            return true;
        }

        // check vertical
        i=x;
        int topmost=x;
        while(i>=0){
            if(board[i][y]==num){
                topmost=i;
                if(Math.abs(topmost-x)+1>=5) {
//                    System.out.println("# win top");
                    return true;
                }
            }
            else{
                break;
            }
            --i;
        }
        i=x;
        int botmost=x;
        while (i<Caro.N){
            if(board[i][y]==num){
                botmost=i;
                if(Math.abs(botmost-x)+1>=5){
//                    System.out.println("# win bot");
                    return true;
                }
            }
            else{
                break;
            }
            ++i;
        }
        if(Math.abs(botmost-topmost)+1>=5) {
//            System.out.println("# win vertical");
            return true;
        }

        // check cross left-top -> right-bottom
        // -> left-top
        i=x;
        int j=y;
        Point curPoint=new Point(x,y);
        while(i>=0&&j>=0){
            if(board[i][j]==num){
                curPoint.setX(i);
                curPoint.setY(j);
                if(Math.abs(curPoint.getX()-x)+1>=5){
//                    System.out.println("win left-top");
                    return true;
                }
            }
            else{
                break;
            }
            --i;
            --j;
        }
        i=x;
        j=y;
        // -> right-bottom
        Point curPoint1=new Point(x,y);
        while(i<Caro.N&&j<Caro.N){
            if(board[i][j]==num){
                curPoint1.setX(i);
                curPoint1.setY(j);
                if(Math.abs(curPoint1.getX()-x)+1>=5){
//                    System.out.println("win right-bottom");
                    return true;
                }
            }
            else{
                break;
            }
            ++i;
            ++j;
        }
        if(Math.abs(curPoint.getX()-curPoint1.getX())+1>=5){
//            System.out.println("left-top and right-bottom");
            return true;
        }

        // check cross right-top -> left-bottom
        // -> right-top
        i=x;
        j=y;

        curPoint.setX(x);
        curPoint.setY(y);
        while(i>=0&&j<Caro.N){
            if(board[i][j]==num){
                curPoint.setX(i);
                curPoint.setY(j);
                if(Math.abs(curPoint.getX()-x)+1>=5){
//                    System.out.println("win right-top");
                    return true;
                }
            }
            else{
                break;
            }
            --i;
            ++j;
        }
        // -> left-bottom
        i=x;
        j=y;

        curPoint1.setX(x);
        curPoint1.setY(y);
        while(i<Caro.N&&j>=0){
            if(board[i][j]==num){
                curPoint1.setX(i);
                curPoint1.setY(j);
                if(Math.abs(curPoint1.getX()-x)+1>=5){
//                    System.out.println("win left-bottom");
                    return true;
                }
            }
            else{
                break;
            }
            ++i;
            --j;
        }
        if(Math.abs(curPoint.getX()- curPoint1.getX())+1>=5){
//            System.out.println("win right-top and left-bottom");
            return true;
        }

        return false;

    }

    boolean isGoodBorder(int[][] board,int x,int y){
        if(inSide(x,y)){
            return !isUsed(x,y);
        }
        return false;
    }

    boolean inSide(int x,int y){
        return x>=0&&x<Caro.N&&y>=0&&y<Caro.N;
    }
}
