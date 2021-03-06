/*
 * @(#)Main.java  1.0  June 10, 2006
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and  
 * contributors of the JHotDraw project ("the copyright holders").  
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * the copyright holders. For details see accompanying license terms. 
 */

package plcedit;

import javax.swing.*;
import org.jhotdraw.app.*;

/**
 * Main.
 *
 * @author Werner Randelshofer.
 * @version 1.0 June 10, 2006 Created.
 */
public class Main {
    
    /** Creates a new instance. */
    public static void main(String[] args) {
        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new DefaultOSXApplication();
        } else if (os.startsWith("win")) {
            //app = new DefaultMDIApplication();
            app = new DefaultSDIApplication();
        } else {
            app = new DefaultSDIApplication();
        }
        
        DrawApplicationModel model = new DrawApplicationModel();
        model.setName("PLC Editor Draw");
        model.setVersion(Main.class.getPackage().getImplementationVersion());
        model.setCopyright("By FanFan Huang 2008-2009");
        model.setViewClassName("plcedit.DrawView");
      
        app.setModel(model);
        app.launch(args);
    }
    
}
