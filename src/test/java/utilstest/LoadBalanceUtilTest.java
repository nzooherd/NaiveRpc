package utilstest;

import gq.shiwenhao.naiverpc.utils.LoadBalanceUtil;
import org.junit.Test;

public class LoadBalanceUtilTest {
    @Test
    public void getIndexTest(){
        int[] weight = new int[]{0, 2, 3, 8, 10, 32, 34, 76};
        assert (LoadBalanceUtil.getIndex(weight, 9) == 3);
        assert (LoadBalanceUtil.getIndex(weight, 8) == 3);
        assert (LoadBalanceUtil.getIndex(weight, 0) == 0);
        assert (LoadBalanceUtil.getIndex(weight, 35) == 6);
    }

}
