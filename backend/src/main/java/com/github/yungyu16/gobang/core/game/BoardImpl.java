package com.github.yungyu16.gobang.core.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** 棋盘 */
public class BoardImpl implements Board{

    private final int size;
    private final int[] boardArr;
    private LinkedList<PathNode> trace = new LinkedList<>();

    public BoardImpl(int size){
        this.size = size;
        boardArr = new int[size*size];
    }

    public boolean isFull(){
        return trace.size() == size*size;
    }

    @Override
    public boolean put(Color color, Point point) {
        int x = point.getX();
        int y = point.getY();
        if (x < 0 || x >= size || y < 0 || y >= size){
            return false;
        }
        if (getVal(x, y)>0) {
            return false;
        }
        set(x, y, color.getValue());
        addRecord(new PathNode(color, x, y));
        return true;
    }


    public int size() {
        return size;
    }

    /** 悔棋 */
    public void cancel(int step){
        for (int i = 0; i < step && !trace.isEmpty(); i++) {
            PathNode tail = trace.pop();
            set(tail.getX(),tail.getY(),Color.Blank.getValue());
        }
    }

    private void set(int x, int y, int val){
        int index = x + y * size;
        boardArr[index] = val;
    }

    public Color get(int x, int y){
        return Color.valueOf(getVal(x,y));
    }

    private int getVal(int x, int y){
        int index = x + y * size;
        return getByIndex(index);
    }

    private int getByIndex(int index){
        if ( index < 0 || index >= boardArr.length) {
            return Color.Blank.getValue();
        }
        return boardArr[index];
    }

    private void addRecord(PathNode pathNode){
        trace.push(pathNode);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < boardArr.length; i++) {
            int x = i % size;
            int y = i / size;
            if (x == 0) {
                builder.append("\n\n");
            }
            int val = boardArr[i];
            if (val == Color.Blank.getValue()) {
                if (x == 0 && y == 0) {
                    builder.append("┌");
                } else if (x < size-1 && y == 0) {
                    builder.append("┬");
                } else if (x == size-1 && y == 0){
                    builder.append("┐");
                } else if (x == 0 && y < size - 1){
                    builder.append("├");
                } else if (x == 0 && y == size - 1){
                    builder.append("└");
                } else if (x < size-1 && y == size - 1){
                    builder.append("┴");
                } else if (x == size-1 && y == size - 1){
                    builder.append("┘");
                } else if (x == size-1 && y < size - 1){
                    builder.append("┤");
                } else {
                    builder.append("┼");
                }
            } else {
                if (val == Color.Black.getValue()) {
                    builder.append("●");
                } else {
                    builder.append("○");
                }
            }
            builder.append('\t');
        }
        return builder.toString();
    }

    public List<PathNode> getTrace() {
        return new ArrayList<>(trace); //防止机器人篡改数据
    }

    public int steps() {
        return trace.size();
    }

    public PathNode getLatest(){
        if (trace.isEmpty()) {
            return null;
        }
        return trace.getFirst();
    }

}
