package gq.shiwenhao.naiverpc.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SerializeUtil {
    //原生Kryo不保证线程安全, ThreadLocal去线程私有化Kryo
    private static final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>(){
        @Override
        protected Kryo initialValue(){
            Kryo kryo = new Kryo();
            /*
             * Some kryo configs
             */
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);

            ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy())
                    .setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

            return kryo;
        }

    };


    public static Kryo getInstance(){
        return kryoLocal.get();
    }

    public static <T> byte[] writeToByteArray(T obj){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        Kryo kryo = getInstance();
        kryo.writeClassAndObject(output, obj);
        output.flush();

        return byteArrayOutputStream.toByteArray();
    }
    public static <T> T readFromByteArray(byte[] byteArray){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Input input = new Input(byteArrayInputStream);

        Kryo kryo = getInstance();

        return (T)kryo.readClassAndObject(input);
    }




}
