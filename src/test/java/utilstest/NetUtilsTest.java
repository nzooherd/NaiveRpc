package utilstest;

import gq.shiwenhao.naiverpc.utils.SystemInfo;
import org.junit.Test;

public class SystemInfoTest {
    @Test
    public void getHostTest(){
        System.out.println(SystemInfo.getHost());
    }
}
