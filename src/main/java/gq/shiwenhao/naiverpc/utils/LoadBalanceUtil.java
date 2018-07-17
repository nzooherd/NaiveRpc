package gq.shiwenhao.naiverpc.utils;

public class LoadBalanceUtil {
    public static int getIndex(int[] weightsSum, int random){
        int left = 0, right = weightsSum.length - 1;
        int medium;
        while(left <= right){
            medium = (right + left) / 2;

            if(weightsSum[medium] > random) {
                right = medium - 1;
            } else if(weightsSum[medium] == random){
                return medium;
            } else if(weightsSum[medium] < random){
                if(medium == weightsSum.length - 1 || weightsSum[medium + 1] > random){
                    return medium;
                }
                left = medium + 1;
            }
        }
        return  -1;
    }
}
