package utilstest;

import org.junit.Test;

public class LoadBalanceUtils {
    @Test
    public void getIndexTest(){
        int[] weight = new int[]{0, 2, 3, 8, 10, 32, 34, 76};
        int random = 9;
        assert (LoadBalanceUtils.getIndex(random) == 3);
    }

}
