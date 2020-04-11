import net.sf.clipsrules.jni.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TestClips
{
    public static void main(String args[]) throws CLIPSException {
        Environment clips = new Environment();

        try
        {
            clips.loadFromResource("/resources/bikers-companion.clp");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        clips.reset();

        clips.assertString("(attribute (name tourism-type) " +
                "                       (value balneare) " +
                "                       (certainty 100.0))");

        clips.run();

        String evalStr = "(MAIN::get-hotel-attribute-list)";

        MultifieldValue mv = (MultifieldValue) clips.eval(evalStr);

        for (PrimitiveValue pv : mv)
        {
            FactAddressValue fv = (FactAddressValue) pv;

            String hotelName = ((LexemeValue) fv.getSlotValue("name")).getValue();
            float certainty = ((NumberValue) fv.getSlotValue("certainty")).floatValue();
            float freePercent = ((NumberValue) fv.getSlotValue("free-percent")).intValue();

            System.out.println(hotelName);
            System.out.println(certainty);
            System.out.println(freePercent);
        }
    }
}
