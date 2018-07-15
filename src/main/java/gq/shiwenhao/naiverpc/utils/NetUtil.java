package gq.shiwenhao.naiverpc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetUtils {
    private static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    public static String getHost(){
        InetAddress candidateAddress = null;
        try {
            for(Enumeration ifaces = NetworkInterface.getNetworkInterfaces();
                ifaces.hasMoreElements(); ){
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }

            if(candidateAddress != null){
                return candidateAddress.getHostAddress();
            }

            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            return jdkSuppliedAddress.getHostAddress();
        } catch (SocketException e) {
            logger.error("Get local host error");
        } catch (UnknownHostException e){
            logger.error("The JDK InetAddress.getLocalHost() method unexpectedly returned null");
        }
        return null;
    }
}
