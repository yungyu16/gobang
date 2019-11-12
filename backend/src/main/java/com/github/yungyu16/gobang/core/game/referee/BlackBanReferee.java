package com.github.yungyu16.gobang.core.game.referee;

import com.github.yungyu16.gobang.core.game.Board;
import com.github.yungyu16.gobang.core.game.PathNode;
import com.github.yungyu16.gobang.core.game.Winner;
import com.github.yungyu16.gobang.core.game.WinnerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 有黑棋禁手的裁判规则
 * 禁手规则：
 * 禁售图文详解 https://wenku.baidu.com/view/9a9a26d950e2524de5187e8d.html
 * 1 当落子即为五连胜利时 不计算禁手
 * 2 黑棋落子不能形成两个或以上的 "活3" 即 "_●●●_" or "_●●_●_" 或者"冲4" "○●●●●_"
 * 3 长连
 *
 */
public class BlackBanReferee extends BaseGobangReferee implements GobangReferee {
    private static final Logger log = LoggerFactory.getLogger(BlackBanReferee.class);
    enum BanStatus{
        /** 长连禁手 超过五连为长连*/
        OverLine,
        /** 离长连只差一步 有些特殊的状况要考虑 */
        PreOverLine,
        /** 在一条线上的两个冲4， 模式为（x为落子位置）
         "_●●●_x_●●●_"
         "_●●_●x_●●_"
         "_●_●x●_●_"
         */
        DoubleChong4,
        Chong4,
        Live4,
        Live3
    }

    static {
        Map<String,Pair> patternBanStatusMap = new TreeMap<>();
        // 一条线上的状态截取的截止状态为遇到： >2个0  对手棋子 边界
        // 注意这里分数的确定，以离确保胜利的步数作为分数的给定标准
        // 离胜利少一步是多一步的两倍，因为棋盘总共有四条线，分数可以叠加
        // 最典型的 “活三”即 01110 如果形成两个方向的“双三” 实际上已经同于确保胜利,因此双三的分数和等于胜利
        // 约定0代表1个空位  1代表我方棋子
        // 匹配时的逻辑，举个栗子：
        // 模式存在为 a:001011 b:0010110
        // 输入串为 001011 00101121211 匹配a  0010110 匹配b，即当串匹配到尾端或者匹配不到合适的模式时结束
        // 左侧不忽略而只忽略右侧是为了方便构件搜索树

        // 活3
        patternBanStatusMap.put("001110",new Pair(BanStatus.Live3, 0,1,5));
        patternBanStatusMap.put("011100",new Pair(BanStatus.Live3, 0,4,5));
        patternBanStatusMap.put("011010",new Pair(BanStatus.Live3, 0,3,5));
        patternBanStatusMap.put("0011010",new Pair(BanStatus.Live3, 0,1,5));
        patternBanStatusMap.put("0010110",new Pair(BanStatus.Live3, 0,1,3,6));
        patternBanStatusMap.put("010110",new Pair(BanStatus.Live3, 0,2,5));
        // 活4
        patternBanStatusMap.put("011110",new Pair(BanStatus.Live4)); // 活4不用检查点，左右肯定都是五连
        patternBanStatusMap.put("0011110",new Pair(BanStatus.Live4)); //
        // 冲4
//        patternBanStatusMap.put("01111",BanStatus.Chong4);
//        patternBanStatusMap.put("001111",BanStatus.Chong4);
//        patternBanStatusMap.put("11110",BanStatus.Chong4);
//
//        // TODO 如何判断一侧为禁手的冲4 如 _●_●●●●_ 这其实时冲4不是活4，左侧长连禁手
//        patternBanStatusMap.put("10111",BanStatus.Chong4);
//        patternBanStatusMap.put("010111",BanStatus.Chong4);
//        patternBanStatusMap.put("0010111",BanStatus.Chong4);
//        patternBanStatusMap.put("11011",BanStatus.Chong4);
//        patternBanStatusMap.put("011011",BanStatus.Chong4);
//        patternBanStatusMap.put("0011011",BanStatus.Chong4);
//        patternBanStatusMap.put("11101",BanStatus.Chong4);
//        patternBanStatusMap.put("011101",BanStatus.Chong4);
//        patternBanStatusMap.put("0011101",BanStatus.Chong4);
//        // 双冲4禁手
//        patternBanStatusMap.put("111010111",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("0111010111",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("00111010111",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("11011011",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("011011011",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("0011011011",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("1011101",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("01011101",BanStatus.DoubleChong4);
//        patternBanStatusMap.put("001011101",BanStatus.DoubleChong4);

    }

    /** 禁手类型和检查点的匹配对，方便给map使用,检查点就是用于检查下一发黑棋的禁手的点 */
    private static class Pair{
        private final BanStatus banStatus;
        private final int[] indexes;


        private Pair(BanStatus banStatus, int ... indexes) {
            this.banStatus = banStatus;
            this.indexes = indexes;
        }
    }
    private class Line {
        static final int EMPTY = 0;
        static final int MINE = 1;
        private LinkedList<Integer> line = new LinkedList<>();


        void leftAddEmpty(){
            line.addFirst(EMPTY);
        }

        void addEmpty(){
            line.add(EMPTY);
        }

        void leftAddMine(){
            line.addFirst(MINE);
        }
        void addMine(){
            line.add(MINE);
        }
    }
    Set<Integer> blackBanSet = new HashSet<>();// 黑棋禁手列表

    @Override
    public WinnerMessage judge(Board board) {
        PathNode latest = board.getLatest();
        if (latest.getColor().equals(Board.Color.Black)) {
            return judgeBlack(board);
        } else {
            return judgeWhite(board);
        }
    }

    private WinnerMessage judgeWhite(Board board){
        return judge(board,false);
    }

    private WinnerMessage judgeBlack(Board board){
        WinnerMessage winnerMessage;
        if ((winnerMessage = winOrOverline(board))!=null) {
            return winnerMessage;
        }
        String banMessage = checkBan(board);
        if (banMessage != null) { // 如果下在了禁手中
            log.info(banMessage);
            return new WinnerMessage(Winner.White,banMessage);
        }
        addNewBan(board);// 如果无人获胜 根据当前棋局给黑棋禁手列表中加入新的禁手
        return null;
    }

    /**
     * 判断胜利或者时长连禁手
     * @return 正常五连黑棋胜 长连禁手白棋胜
     */
    private WinnerMessage winOrOverline(Board board){
        return judge(board,true);
    }

    private String checkBan(Board board){
        // todo 如何判断禁手 目前的问题是 不知道如何进行复杂禁手的判断 如旁边有禁手导致当前位置不算禁手的情况
        return null;
    }

    private void addNewBan(Board board){
        // todo 将禁手插入
    }

}
