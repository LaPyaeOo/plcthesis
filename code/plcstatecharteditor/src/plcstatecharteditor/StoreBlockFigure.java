/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plcstatecharteditor;

import com.sun.tools.javac.resources.javac;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;
import java.util.Collection;
import javax.swing.Action;
import javax.swing.JComboBox;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.FigureEvent;
import org.jhotdraw.draw.FigureListener;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.HorizontalLayouter;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.VerticalLayouter;
import org.jhotdraw.draw.Figure;

import plcstatecharteditor.StoreBlock.storeobj;
/**
 *
 * @author Vortex-5
 */
public class StoreBlockFigure extends CodeBlockFigure{  
    
    private TextChangedListener txtchange;
    private ActionListener updatelistener;

    @Override
    public GraphicalCompositeFigure clone() {
        return super.clone();
    }
    
    
    @Override
    protected void createModel() {
        accociatedcode = new StoreBlock();
        txtchange = new TextChangedListener(this,(StoreBlock)accociatedcode);
        updatelistener = new UpdateListener(this);
    }

    @Override
    protected void initialize_component() {  
        update();
    }
    
    
    
    protected void updateAttributesFromData()
    {
        ((StoreBlock)accociatedcode).updateEntries();
       
        attrib = new GraphicalCompositeFigure();
        
        attrib.setLayouter(new VerticalLayouter());
        GraphicalCompositeFigure line;
        for(storeobj store : ((StoreBlock)accociatedcode).getStores())
        {
            line = new GraphicalCompositeFigure();
            line.setLayouter(new HorizontalLayouter());

            //TODO: Figure out how to get a combo box to show up for types
            TextFigure storetype = new TextFigureWithClickEvent(store.type.toDisplayString(), updatelistener);
            storetype.setEditable(false);

            //add link information so we can edit it
            storetype.setAttribute(SpecialAttributeKeys.MODIFY_LINK_TYPEOBJ, store.type);
            line.add(storetype);

            TextFigure space = new TextFigure(" ");
            line.add(space);


            
            TextFigure storeid = new TextFigure(store.identifier);
            storeid.addFigureListener(txtchange);
            storeid.setAttribute(SpecialAttributeKeys.MODIFY_LINK_ID, store);
            line.add(storeid);
            
            TextFigure eq = new TextFigure(" = ");
            eq.setEditable(false);
            
            line.add(eq);
            
            TextFigure storeval = new TextFigure(store.value);
            storeval.addFigureListener(txtchange);
            storeval.setAttribute(SpecialAttributeKeys.MODIFY_LINK_VAL, store);
            line.add(storeval);
            attrib.add(line);
        }
        
        System.out.println(accociatedcode.getCode());
        
        update_base();
    }
    
    public void update()
    {
        this.willChange();
        updateAttributesFromData();
        this.changed();

    }


    //Event handler handles when text is changed in any of the boxes.
    
    protected class TextChangedListener implements FigureListener
    {
        StoreBlockFigure caller;
        StoreBlock accociatedcode;
        
        public TextChangedListener(StoreBlockFigure fig, StoreBlock code)
        {
            caller = fig;
            accociatedcode = code;
        }

        public void areaInvalidated(FigureEvent e) {
            System.out.println("detected area invalid");
        }

        public void attributeChanged(FigureEvent e) {
            System.out.println("detected attrib changed");
        }

        public void figureAdded(FigureEvent e) {  
            System.out.println("detected fig added");
        }

        public void figureChanged(FigureEvent e) {
            storeobj edit;
            
            System.out.println("Got Change Event");
            if (e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_ID) != null)
            {
                edit = (storeobj)e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_ID);
                edit.identifier = ((TextFigure)e.getFigure()).getText();
                
                System.out.println("got ID");
                System.out.println(edit.identifier);
            }
            else if(e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_VAL) != null)
            {
                edit = (storeobj)e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_VAL);
                edit.value = ((TextFigure)e.getFigure()).getText();
                
                System.out.println("got Val");
            }
            
            //accociatedcode.updateEntries();

            //update regardless since we recieved a change in a figure
            caller.update();
            //System.out.println(accociatedcode.getCode());
        }

        public void figureHandlesChanged(FigureEvent e) {
            System.out.println("Detected handles changed");
        }

        public void figureRemoved(FigureEvent e) {   
            
        }

        public void figureRequestRemove(FigureEvent e) {
            System.out.println("Detected removed req");
        }
    }

    protected class UpdateListener implements ActionListener
    {
        StoreBlockFigure _sender;

        public UpdateListener(StoreBlockFigure sender) {
            _sender = sender;
        }

        public void actionPerformed(ActionEvent arg0) {
            System.out.println("Got update action request executing");
            _sender.update();
        }
    }


    private static final boolean DEBUG = true;
    @Override
    public boolean handleMouseClick(Double p, MouseEvent evt, DrawingView view) {
        if (DEBUG) System.out.println("Got click event form composite figure StoreBlockFigure class");
            Figure target = this.findFigureInside(p);
            target.handleMouseClick(p, evt, view);  //propigate the event downwards



        return super.handleMouseClick(p, evt, view);
    }    
}