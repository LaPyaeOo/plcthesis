package plcedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.FigureEvent;
import org.jhotdraw.draw.FigureListener;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.HorizontalLayouter;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.VerticalLayouter;
import org.jhotdraw.draw.Figure;


import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;
import java.io.IOException;

import java.util.List;

/**
 *
 * @author Vortex-5
 */
public class StoreBlockFigure extends CodeBlockFigure{  
    
    private TextChangedListener txtchange;
    private ActionListener updatelistener;

    private StoreBlock SavedBlock = null;
    
    @Override
    protected void createModel() {
        if (SavedBlock == null) {
            accociatedcode = new StoreBlock();
        }
        else {
            accociatedcode = SavedBlock;
            SavedBlock = null;           
        }
        txtchange = new TextChangedListener(this,(StoreBlock)accociatedcode);
        updatelistener = new UpdateListener(this);
    }

    @Override
    protected void initialize_component() {  
        update();
    }
    
    
    public void update()
    {
        this.willChange();

        ((StoreBlock)accociatedcode).updateEntries();

        createAttributeDisplay(); //create our display area

        attrib.setLayouter(new VerticalLayouter());
        GraphicalCompositeFigure line;
        for(StoreObj store : ((StoreBlock)accociatedcode).getStores())
        {
            line = new GraphicalCompositeFigure();
            line.setLayouter(new HorizontalLayouter());
            line.add(spacer);


            TextFigure storetype = new TextFigureWithClickEvent(store.type.toDisplayString(), updatelistener);
            storetype.setEditable(false);

            //add link information so we can edit it
            storetype.setAttribute(SpecialAttributeKeys.MODIFY_LINK_CREATESCONTEXTMENU, store.type);
            line.add(storetype);

            TextFigure space = new TextFigure(" ");
            line.add(space);



            TextFigure storeid = new TextFigure(store.identifier);
            storeid.addFigureListener(txtchange);
            storeid.setAttribute(SpecialAttributeKeys.MODIFY_LINK_STORE_ID, store);
            line.add(storeid);

            TextFigure eq = new TextFigure(" := ");
            eq.setEditable(false);

            line.add(eq);

            TextFigure storeval = new TextFigure(store.value);
            storeval.addFigureListener(txtchange);
            storeval.setAttribute(SpecialAttributeKeys.MODIFY_LINK_STORE_VAL, store);
            line.add(storeval);

            line.add(spacer);
            attrib.add(line);
        }

        update_base();

        this.changed();

    }

    @Override
    public StoreBlock getModel()
    {
        return (StoreBlock)accociatedcode;
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
        }

        public void attributeChanged(FigureEvent e) {
        }

        public void figureAdded(FigureEvent e) {  
        }

        public void figureChanged(FigureEvent e) {
            StoreObj edit;
            
            if (e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_STORE_ID) != null)
            {
                edit = (StoreObj)e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_STORE_ID);
                //TODO: add variable name checking for variable names here
                edit.identifier = ((TextFigure)e.getFigure()).getText().trim();
            }
            else if(e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_STORE_VAL) != null)
            {
                edit = (StoreObj)e.getFigure().getAttribute(SpecialAttributeKeys.MODIFY_LINK_STORE_VAL);
                //TODO: add variable checking for variable values here (might choose to omit)
                edit.value = ((TextFigure)e.getFigure()).getText().trim();
            }
            
            //accociatedcode.updateEntries();

            //update regardless since we recieved a change in a figure
            caller.update();
        }

        public void figureHandlesChanged(FigureEvent e) {
        }

        public void figureRemoved(FigureEvent e) {   
            
        }

        public void figureRequestRemove(FigureEvent e) {
        }
    }

    protected class UpdateListener implements ActionListener
    {
        StoreBlockFigure _sender;

        public UpdateListener(StoreBlockFigure sender) {
            _sender = sender;
        }

        public void actionPerformed(ActionEvent arg0) {
            _sender.update();
        }
    }


    private static final boolean DEBUG = true;
    @Override
    public boolean handleMouseClick(Double p, MouseEvent evt, DrawingView view) {
        try
        {
            Figure target = this.findFigureInside(p);
            target.handleMouseClick(p, evt, view);  //propigate the event downwards
        }
        catch (Exception ex)
        {
        }

        return super.handleMouseClick(p, evt, view);
    }

    @Override
    public void write(DOMOutput out) throws IOException {
        writeBoundingBox(out); //save our position data

        writeUID(out);

        //get rid of garbage
        getModel().removeUnsavedEntries();

        //prepare for export
        List<StoreObj> store = getModel().getStores();

        for (int i=0;i<store.size();i++){
            out.addAttribute("data_type" + Integer.toString(i), store.get(i).type.getSaveString());
            out.addAttribute("data_id" + Integer.toString(i), store.get(i).identifier);
            out.addAttribute("data_val" + Integer.toString(i), store.get(i).value);
        }


        //writeAttributes(out); //export all attributes
    }

    @Override
    public void read(DOMInput in) throws IOException {
        readSavedBounds(in);

        SavedBlock = new StoreBlock(readUID(in));

        String strType = "INIT";
        String strId = "INIT";
        String strVal = "INIT";

        int i=0;
        while (!strType.equals("UNDEFINED") && !strId.equals("UNDEFINED") && ! strVal.equals("UNDEFINED")) {
                strType = in.getAttribute("data_type" + Integer.toString(i), "UNDEFINED");
                strId = in.getAttribute("data_id" + Integer.toString(i), "UNDEFINED");
                strVal = in.getAttribute("data_val" + Integer.toString(i), "UNDEFINED");

                if (!strType.equals("UNDEFINED") && !strId.equals("UNDEFINED") && ! strVal.equals("UNDEFINED"))
                {
                    CodeVarType newtype = new CodeVarType(CodeVarType.VarType.Undefined);
                    newtype.setFromSaveString(strType);
                    SavedBlock.addItem(strId, strVal, newtype);
                }
                i++;
        }

        //readAttributes(in);
    }
}
