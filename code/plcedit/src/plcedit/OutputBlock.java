package plcedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 *
 * @author huangkf
 */
public class OutputBlock extends CodeBlock {
    
    private String value;

    public OutputBlock() {
        this(UIDGenerator.getNext());
    }

    public OutputBlock(int uid) {
        this.uid = uid;
        this.ctype = CodeType.Output;


        //TODO: determine if we want a default port at all
        this.value = "0x0";
    }

    public String getDisplayedPort() {
        return "PORTOUT";
    }


    public String getCompiledPort() {
        return "PORTOUT";
    }

    public void setValue (String value) {
        this.value = value;
    }

    public String getValue () {
        return this.value;
    }

    @Override
    public String getTypeString() {
        return "OUTPUT";
    }

    @Override
    public String getCode() {
        return getUIDStringLabel() + newline +
                "//////////////////////////////////////" + newline +
                "//        OUTPUT                    //" + newline +
                "//////////////////////////////////////" + newline +
                getCompiledPort() + " = " + getValue() + ";";
    }



}