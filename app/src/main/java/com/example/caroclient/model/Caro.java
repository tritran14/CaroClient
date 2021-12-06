package com.example.caroclient.model;

public class Caro {
    public static final int N=10;
    private int[] board;
    public Caro(){
        board=new int[N*N];
        for(int i=0;i<board.length;++i) board[i]=i;
    }

    public int[] getBoard(){
        return board;
    }

    public void setBoard(int id,int val){
        board[id]=val;
    }

    private int convertToInt(int x,int y){
        return x*N+y%N;
    }

    public boolean checkCoordinates(int x,int y){
        int nn=x*N+y%N;
        if(board[nn]==nn&&inSide(x,y)) return true;
        return false;
    }

    private boolean inSide(int x,int y){
        return x>=0&&x<N&&y>=0&&y<N;
    }

    public boolean checkWin(int n){
        return checkWin(n/N,n%N);
    }


    public boolean checkFull(){
        for(int i=0;i<N;++i){
            for(int j=0;j<N;++j){
                if(board[convertToInt(i,j)]==convertToInt(i,j)) return false;
            }
        }
        return true;
    }

    public boolean checkWin(int x,int y){
        // check horizontal
        if(convertToInt(x,y)<0){
            return false;
        }
        int num=board[convertToInt(x,y)];
        int i=y;
        int leftmost=y;
        while(i>=0){
            if(board[convertToInt(x,i)]==num){
                leftmost=i;
                if(Math.abs(leftmost-y)+1>=5){
                    System.out.println("# win left");
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
        while(i<N){
            if(board[convertToInt(x,i)]==num){
                righmost=i;
                if(Math.abs(righmost-y)+1>=5){
                    System.out.println("# win right");
                    return true;
                }
            }
            else{
                break;
            }
            ++i;
        }
        if(Math.abs(righmost-leftmost)+1>=5){
            System.out.println("leftmost : "+leftmost+" rightmost : "+righmost);
            System.out.println("# win horizontal");
            return true;
        }

        // check vertical
        i=x;
        int topmost=x;
        while(i>=0){
            if(board[convertToInt(i,y)]==num){
                topmost=i;
                if(Math.abs(topmost-x)+1>=5) {
                    System.out.println("# win top");
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
        while (i<N){
            if(board[convertToInt(i,y)]==num){
                botmost=i;
                if(Math.abs(botmost-x)+1>=5){
                    System.out.println("# win bot");
                    return true;
                }
            }
            else{
                break;
            }
            ++i;
        }
        if(Math.abs(botmost-topmost)+1>=5) {
            System.out.println("# win vertical");
            return true;
        }

        // check cross left-top -> right-bottom
        // -> left-top
        i=x;
        int j=y;
        Point curPoint=new Point(x,y);
        while(i>=0&&j>=0){
            if(board[convertToInt(i,j)]==num){
                curPoint.setX(i);
                curPoint.setY(j);
                if(Math.abs(curPoint.getX()-x)+1>=5){
                    System.out.println("win left-top");
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
        while(i<N&&j<N){
            if(board[convertToInt(i,j)]==num){
                curPoint1.setX(i);
                curPoint1.setY(j);
                if(Math.abs(curPoint1.getX()-x)+1>=5){
                    System.out.println("win right-bottom");
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
            System.out.println("left-top and right-bottom");
            return true;
        }

        // check cross right-top -> left-bottom
        // -> right-top
        i=x;
        j=y;

        curPoint.setX(x);
        curPoint.setY(y);
        while(i>=0&&j<Caro.N){
            if(board[convertToInt(i,j)]==num){
                curPoint.setX(i);
                curPoint.setY(j);
                if(Math.abs(curPoint.getX()-x)+1>=5){
                    System.out.println("win right-top");
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
            if(board[convertToInt(i,j)]==num){
                curPoint1.setX(i);
                curPoint1.setY(j);
                if(Math.abs(curPoint1.getX()-x)+1>=5){
                    System.out.println("win left-bottom");
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
            System.out.println("win right-top and left-bottom");
            return true;
        }

        return false;

    }

    public void display(){
        System.out.print("|   |");
        for(int i=0;i<Caro.N;++i) System.out.print(" "+i+" |");
        System.out.println();
        for(int i=0;i<N;++i){
            System.out.print("| "+i+" | ");
            for(int j=0;j<N;++j){
                char ch=' ';
                if(board[convertToInt(i,j)]==100) ch='O';
                else if(board[convertToInt(i,j)]==101) ch='X';
                else ch=' ';
                System.out.print(ch+" | ");
            }
            System.out.println();
        }
    }
}
