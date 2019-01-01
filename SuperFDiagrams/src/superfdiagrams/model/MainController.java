/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package superfdiagrams.model;

import java.util.ArrayList;
import superfdiagrams.EntityCheck;
import superfdiagrams.model.primitive.*;

import java.util.HashMap;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;
import superfdiagrams.FXMLDocumentController;
import static superfdiagrams.model.ElementState.*;
import static superfdiagrams.model.GeometricUtilities.checkColition;
import static superfdiagrams.model.State.*;
import superfdiagrams.model.action.*;
import superfdiagrams.model.drawer.DrawController;

import static superfdiagrams.model.primitive.Type.*;

/**
 *
 * @author sebca
 */
public class MainController
{
    private static MainController mc;
    private final StateController stateC;
    private final DrawController drawC;
    private final DiagramController diagramC;
    private FXMLDocumentController uiController;
    private final ActionController actionC;
    private final SelectorController selectorC;

    private Element selected;
    private List<Element> selectedRelated;
    private ComplexElement morphingComplex;

    //maps para verificaciones semánticas
    public HashMap<Integer, EntityCheck> weakEntityCheck; //un mapa con un objeto que contiene las verificaciones de entidades débiles
    public HashMap<String, Entity> entityNanes; //un mapa para verificar que entidades no tengan mismo nombre.
    //public HashMap<Integer, Boolean> StrongEntityCheck strongEntity;

    private MoveAction selectedAction;
    private boolean doubleClick;
    private boolean shouldComplexMorph;
    
    private double mouseXPos;
    private double mouseYPos;
    private double zoomFactor;
    private double maxWith;
    private double maxHeight;

    private Element currentElement;

    public static MainController getController()
    {
        if (mc == null)
            mc = new MainController();
        return mc;
    }

    private MainController()
    {
        stateC = StateController.getController();
        drawC = DrawController.getDrawController();
        diagramC = DiagramController.getController();
        actionC = ActionController.getController();
        selectorC = SelectorController.getController();
        weakEntityCheck = new HashMap<Integer, EntityCheck>();
        entityNanes = new HashMap<String, Entity>();

        currentElement = null;
        this.zoomFactor = 1;
        this.doubleClick = false;
        maxWith = 800; //defualt minimum with posible
        maxHeight = 700; //default minimum height posible
        shouldComplexMorph = false;
        morphingComplex = null;

    }
    
    public ActionController getActionC(){
        return actionC;
    }

    public void setUiController(FXMLDocumentController dc)
    {
        uiController = dc;
    }

    public void setContext(GraphicsContext gc)
    {
        drawC.setGraphicsContext(gc);
    }

    public State getState()
    {
        return stateC.getState();
    }

    public void setState(State state)
    {
        stateC.setState(state);
        this.cancelSelection();
    }

    public void setMousePos(double x, double y)
    {
        this.mouseXPos = Math.round(x / zoomFactor);
        this.mouseYPos = Math.round(y / zoomFactor);
    }

    public void toggleDrawVertex()
    {
        drawC.toggleDrawVertex();
    }

    public void newDiagram()
    {
        diagramC.newDiagram();
    }
    
    public Diagram getDiagram(){
        return diagramC.getDiagram();
    }

    public void createNewEntity()
    {
        String label = uiController.getElementLabel("entidad");

        if (label == null)
        {
            stateC.setState(VIEW);
            return;
        }
        if (entityNanes.containsKey(label)) //si ya se encuentra un elemento con ese mismo nombre...
        {
            System.out.println("elemento contenido...   ");
            //volver a pedir name...
            return;
        }

        Type type = uiController.askRoleType();

        if (type == null)
        {
            stateC.setState(VIEW);
            return;
        }

        CreateElementAction create = new CreateElementAction();
        create.createEntity(mouseXPos, mouseYPos, label, type);
        actionC.addToStack(create);
        stateC.setState(VIEW);
    }

    public void createNewRelationship()
    {
        String label = uiController.getElementLabel("relación");

        if (label == null)
        {
            finishAction();
            return;
        }

        Type type = ROLE_STRONG;
        
        List<Element> selectedElements = selectorC.getSelected();
        for(Element element: selectedElements){
            if(element.getPrimitive().getType() == ROLE_WEAK){
                type = uiController.askRoleType();
                break;
            }
        }

        if (type == null)
        {
            finishAction();
            return;
        }

        CreateElementAction create = new CreateElementAction();
        create.createRelationship(mouseXPos, mouseYPos, label, type, selectedElements);

        actionC.addToStack(create);
        finishAction();
    }
    
    public void createNewAttribute(){
        String label = uiController.getElementLabel("atributo");
        Type bla = selectorC.getSelected0().getPrimitive().getType();

       if (label == null){
            finishAction();
            return;
        }
        Type type;
        boolean error = true;

        
       do {
            type = uiController.askAttributeType();
            if((bla == ATTRIBUTE_COMPOSITE)
                    && type == ATTRIBUTE_GENERIC){
                error = false;
            }else if ((bla == ATTRIBUTE_COMPOSITE)
                    && type != ATTRIBUTE_GENERIC){
                error = true;
            
            }else{
                error = false;
            }
            if (type == null){
                finishAction();
                return;
            }
        } while (error);
        
        List<Element> selectedElements = selectorC.getSelected();
        CreateElementAction create = new CreateElementAction();
        create.createAttribute(mouseXPos, mouseYPos, label, type, selectedElements);
        
        actionC.addToStack(create);
        finishAction();
    }
    
    public void createNewHeritage(){
        String name;
        Type type = FXMLDocumentController.askHeritageType();
        
        if(type == null){
            finishAction();
            return;
        }
        
        switch(type){
            case HERITAGE_D:
                name = "D";
                break;
            case HERITAGE_S:
                name = "S";
                break;
            default:
                finishAction();
                return;
        }
        
        List<Element> selectedElements = selectorC.getSelected();
        CreateElementAction create = new CreateElementAction();
        create.createHeritage(mouseXPos, mouseYPos, name, type, selectedElements);
        
        actionC.addToStack(create);
        finishAction();
    }
    
    public void createNewAgregation(){
        String label = uiController.getElementLabel("Agregación");
        
        if(label == null){
            finishAction();
            return;
        }
        
        Element temp = selectorC.getSelected().get(0);
        Vertex center = temp.getCenterVertex();
        List<Element> selectedElements = new ArrayList<>();
        
        selectedElements.add(temp);
        selectedElements.addAll(Finder.findRelatedAttributes(temp));
        for(Element el : temp.getPrimitive().getChildren()){
            selectedElements.add(el);
            Element child = ((Union) el.getPrimitive()).getChild();
            selectedElements.add(child);
            selectedElements.addAll(Finder.findRelatedAttributes(child));
        }
        
        CreateElementAction create = new CreateElementAction();
        create.createAgregation(center.getxPos(), center.getyPos(), label, AGREGATION, selectedElements);
        
        actionC.addToStack(create);
        finishAction();
    }
    
    /**
     * Funcion que dibuja los elementos de la lista.
     * Esta funcion va dibujando constantemente, cuando la lista se encuentra
     * vacia estara limpiando la pantalla.
     * @return true si dibujó algo, false caso contrario.
     */
    public boolean drawElements()
    {
        if (!diagramC.fetchElements().isEmpty())
        {
            drawC.doDrawLoop(fetchElements());
            return true;
        }
        return false;
    }

    public void restart()
    {
        actionC.restart();
        diagramC.newDiagram();
        selectorC.emptySelection();
        selected = null;
        currentElement = null;
        stateC.setState(VIEW);
        zoomFactor = 1;
        NameCounter.restartCounter();
        clearMaps();
        this.shouldComplexMorph = false;
        this.morphingComplex = null;
    }
    
    public void finishEntitySelection(){
        if (selectorC.isEmpty())
            stateC.setState(VIEW);
        else if (stateC.getState() == CHOSING_ENTITY)
            stateC.setState(ATTRIBUTE);
        else if(stateC.getState() == SELECTING_CHILDREN 
             && selectorC.selectionSize() != 1)
            stateC.setState(HERITAGE);
        else if (stateC.getState() == SELECTING_ENTITIES)
            stateC.setState(RELATIONSHIP);
        else if (stateC.getState() == CREATING_AGREGATION)
            createNewAgregation();
        else if (stateC.getState() == ADDING_ENTITY)
            addEntities();
        else
            stateC.setState(VIEW);
    }

    public void runMainLoop()
    {
        if (selected != null && stateC.getState() == MOVING_ELEMENT)
        {
            moveElement();
            if (shouldComplexMorph)
                recursiveComplexMorph(morphingComplex);
        }

        this.setStatusText();
    }
    
    private ComplexElement recursiveComplexMorph(ComplexElement morph){
        VertexGenerator.morphComplex(morph);
        ComplexElement container = Finder.findComplexContained(morph, fetchElements());
        if (container != null)
            return recursiveComplexMorph(container);
        else{
            return morph;
        }
    } 
    
    private void moveElement(){
        if(selected instanceof ComplexElement){
            VertexGenerator.recalculateComplexElement
                                      (selected,
                                      ((ComplexElement)selected).getComposite(),
                                      mouseXPos,
                                      mouseYPos);
        } else {
            VertexGenerator.recalculateVertexes(selected, mouseXPos, mouseYPos);
            if (selectedRelated != null && !selectedRelated.isEmpty())
                VertexGenerator.recalculateNearestVertexes(selectedRelated);
        }
    }

    public void setStatusText()
    {
        switch (stateC.getState())
        {
            case VIEW:
                uiController.setStatusText("");
                break;
            case SELECTING_ENTITIES:
                uiController.setStatusText("Seleccionando entidades...");
                break;
            case RELATIONSHIP:
                uiController.setStatusText("Creando relación...");
                break;
            case MOVING_ELEMENT:
                uiController.setStatusText("Moviendo elemento...");
                break;
            case ENTITY:
                uiController.setStatusText("Creando entidad...");
            case CHOSING_ENTITY:
                uiController.setStatusText("Escogiendo Entidad...");
                break;
            case ATTRIBUTE:
                uiController.setStatusText("Creando Atributo...");
                break;
            case DELETING_ELEMENT:
                uiController.setStatusText("Eliminando elemento...");
                break;
            case HERITAGE:
                uiController.setStatusText("Creando herencia...");
                break;
            case SELECTING_CHILDREN:
                uiController.setStatusText("Seleccionando hijos...");
                break;
            case CREATING_AGREGATION:
                uiController.setStatusText("Creando agregación...");
                break;
            case ADDING_ENTITY:
                uiController.setStatusText("Añadiendo entidades a ...");
                break;
        }
    }

    public void doClickAction()
    {
        if (currentElement != null && stateC.getState() == VIEW)
            currentElement.setElementState(NORMAL);

        currentElement = checkColition(mouseXPos, mouseYPos);
        
        if(currentElement != null){
            switch(stateC.getState()){
                case MOVING_ELEMENT:
                    selected.setElementState(NORMAL);
                    selectedAction.getNewPosition();
                    selected = null;
                    selectedRelated = null;
                    selectedAction = null;
                    this.morphingComplex =  null;
                    this.shouldComplexMorph = false;
                    stateC.setState(VIEW);
                    for (Vertex v : currentElement.getVertexes())
                    {
                        maxWith = Math.max(maxWith, v.getxPos());
                        maxHeight = Math.max(maxHeight, v.getyPos());
                    }
                    break;

                case VIEW:
                    if (!doubleClick)
                        break;

                    selected = currentElement;
                    morphingComplex = Finder.findComplexContained(selected, fetchElements());
                    if (morphingComplex != null)
                        this.shouldComplexMorph = true;
                    
                    if (selected instanceof ComplexElement)
                        selectedAction = new MoveComplexElementAction((ComplexElement) selected);
                    else {
                        if (!shouldComplexMorph){
                            selectedRelated = Finder.findRelatedUnions(diagramC.fetchElements(), selected);
                            selectedAction = new MoveElementAction(selected, selectedRelated);
                        } else {
                            selectedAction = new MoveComplexElementAction(recursiveComplexMorph(morphingComplex));
                        }
                    }
                    
                    actionC.addToStack(selectedAction);
                    
                    if (!(selected.getPrimitive() instanceof Union))
                    {
                        selected.setElementState(HIGHLIGHTED);
                        stateC.setState(MOVING_ELEMENT);
                    }
                    else selected = null;

                    break;
                default:
                    selectorC.add(currentElement);
                    if (!selectorC.isEmpty())
                        uiController.activateFinishButton();
                    break;
            }
        }
        else
        {
            switch (stateC.getState())
            {
                case ENTITY:
                    createNewEntity();
                    break;
                case RELATIONSHIP:
                    createNewRelationship();
                    break;
                case HERITAGE:
                    createNewHeritage();
                    break;
                case ATTRIBUTE:
                    createNewAttribute();
                    break;
            }
        }
    }

    public void cancelSelection()
    {
        selectorC.emptySelection();
        if (currentElement != null)
            currentElement.setElementState(NORMAL);
        currentElement = null;
    }

    public void finishAction()
    {
        stateC.setState(VIEW);
        selectorC.emptySelection();
    }

    public void undo()
    {
        clearMaps();
        actionC.undo();
    }

    public void redo()
    {
        clearMaps();
        actionC.redo();
    }

    public boolean isUndoEmpty()
    {
        return actionC.isUndoEmpty();
    }

    public boolean isRedoEmpty()
    {
        return actionC.isRedoEmpty();
    }

    public void addElement(Element element)
    {

        for (Vertex v : element.getVertexes())
        {
            maxWith = Math.max(maxWith, v.getxPos());
            maxHeight = Math.max(maxHeight, v.getyPos());
        }
        diagramC.addElement(element);
        /*drawC.addToBuffer(element);*/
    }

    public void deleteElement(Element deleted)
    {
        List<Element> related = null;
        
        if (deleted.getPrimitive() instanceof Attribute){
            DeleteAttributeAction action = new DeleteAttributeAction(deleted);
            action.execute();
            actionC.addToStack(action);
            return;
        }
        
        if (deleted.getPrimitive() instanceof Entity){
            related = Finder.findRelatedUnions(diagramC.fetchElements(), deleted); 
        } else if (deleted.getPrimitive() instanceof Relationship
                || deleted.getPrimitive() instanceof Heritage){
            related = deleted.getPrimitive().getChildren();
        }
        else if (deleted.getPrimitive() instanceof Relationship
                || deleted.getPrimitive() instanceof Heritage)
        {
            related = deleted.getPrimitive().getChildren();
        }
        if (related != null)
        {
            DeleteElementAction deleteAction = new DeleteElementAction(deleted, related);
            deleteAction.execute();
            actionC.addToStack(deleteAction);
        }

        clearMaps();
    }

    public void morphElement(List<Element> elementList)
    {
        for (Element element : elementList)
            morphElement(element);
    }

    public void morphElement(Element element){
        Primitive primitive = element.getPrimitive();
        morphElement(element, primitive.getChildren().size());
        if(primitive instanceof Relationship){
            List<Element> contained = primitive.getChildren();
            if (contained.size() == 1)
            {
                Element union = new ElementBuilder().cloneUnion(contained.get(0));
                contained.add(union);
                this.addElement(union);
            }

            if (contained.get(0).equals(contained.get(1)))
            {
                contained.remove(0);
            }
        }
    }
    
    public void morphElement(Element element, int n)
    {
        List<Element> contained = element.getPrimitive().getChildren();

        if (contained.isEmpty())
        {
            removeElement(element);
            return;
        }

        if (element.getPrimitive() instanceof Relationship)
        {
            String name = element.getPrimitive().getLabel();
            double xSizeMultiplier = GeometricUtilities.getSizeMultiplier(name);
            double size = ElementBuilder.getDefaultSize();
            element.setVertexes(VertexGenerator.generateVertexes(
                    n,
                    size * xSizeMultiplier,
                    size,
                    GeometricUtilities.getCenterOfMass(element.getVertexes())));
            
            boolean shouldMorph = true;
            if(element.getPrimitive().getType() == ROLE_WEAK){
                for (Element u : element.getPrimitive().getChildren()){
                    if(u.getPrimitive().getType() == ROLE_WEAK){
                        shouldMorph = false;
                        break;
                    }
                }
            }
            
            if(shouldMorph){
                element.getDrawer().setType(ROLE_STRONG);
            }
        }

        VertexGenerator.recalculateNearestVertexes(contained);

    }

    public void removeElement(Element element)
    {
        diagramC.removeElement(element);
        /*drawC.removeFromBuffer(element);*/
        clearMaps();
    }

    public List<Element> fetchElements()
    {
        return diagramC.fetchElements();
    }

    public Element getCurrentElement()
    {
        return currentElement;
    }

    public void renameCurrentElement(String label){
        if (currentElement.getPrimitive() instanceof Heritage || entityNanes.containsKey(label))
            return;
        RenameElementAction action = new RenameElementAction(currentElement, label);
        action.execute();
        actionC.addToStack(action);

        //para ahorrame trabajo solo limpio los mapas...
        clearMaps();
    }

    public double getZoomFactor() { return zoomFactor;}

    public void setZoomFactor(double _zoomFactor) {zoomFactor = _zoomFactor;}

    public void setDoubleClick(boolean value)
    {
        this.doubleClick = value;
    }

    public double getMaxWith() {return Math.min(Math.max((maxWith * zoomFactor), 800), 2000);} //800 minimum with posible; 2000 maximum

    public double getMaxHeight() {return Math.min(Math.max((maxHeight * zoomFactor), 700), 7000);} // 700 maximum with posible


    public void normalizeDraw() {zoomFactor = 1;}

    public void clearMaps()
    {
        weakEntityCheck = new HashMap<Integer, EntityCheck>();
        this.entityNanes = new HashMap<String, Entity>();
    }

    /**
     * @author Ignacio Martinez
     * @return
     */
    //Magia negra??, brujería pura y asquerosidad que es mejor no tocar...
    public String checkSemantics()
    {
        String message = "";
        List<Element> elements = this.fetchElements();
        for (Element e : elements) // for para todos los elementos...
        {
            //aquí agrega los elementos al hashmap en caso de no estar en él..
            if (e.getPrimitive() instanceof Entity && ! (e instanceof ComplexElement))
            {
                if (!weakEntityCheck.containsKey(e.hashCode()))
                    weakEntityCheck.put(e.hashCode(), new EntityCheck(e.getPrimitive().getLabel(), e.getPrimitive().getType(), e));
                else
                    weakEntityCheck.get(e.hashCode()).setFalse();

                if(!entityNanes.containsKey(e.getPrimitive().getLabel()))
                    entityNanes.put(e.getPrimitive().getLabel(), (Entity)e.getPrimitive());

            }

            Primitive element = e.getPrimitive();
            if (element.getType() == ATTRIBUTE_PARTIAL_KEY)  //verifica que una entidad débil tenga atributo parcial
            {
                // en palabras simples,
                //si el elemento es un atributo parcial... mira el padre  y si el padre es un elemento débil se cumple
                Element entity = ((Union) (element.getChildren().get(0).getPrimitive())).getChild();
                if (entity.getPrimitive().getType() == ROLE_WEAK)
                {
                    int key = entity.hashCode();
                    EntityCheck temp = weakEntityCheck.get(key);
                    temp.partialKey = true;
                    weakEntityCheck.replace(key, temp);
                }
            }
            else if(element.getType() == ATTRIBUTE_KEY)  // verifica que entidad fuerte tenga un atributo clave
            {
                //mismo principio que el anterior..
                Element entity = ((Union) (element.getChildren().get(0).getPrimitive())).getChild();
                if (entity.getPrimitive().getType() == ROLE_STRONG)
                {
                    int key = entity.hashCode();
                    EntityCheck temp = weakEntityCheck.get(key);
                    temp.keyAtribute = true;
                    weakEntityCheck.replace(key, temp);
                }
            }
            else if(element instanceof Relationship) //verifica que una entidad débil esté relacionada con una fuerte por relación débl
            {
                boolean strong = false;
                int key = 0;
                List<Element> elementChildren = element.getChildren();
                for(Element el : elementChildren)
                {
                    //para verificar lo que hace es ver todos los elementos con los que une la relación
                    Union u = (Union) el.getPrimitive();
                    Primitive entity = u.getChild().getPrimitive();
                    if (entity.getType() == ROLE_WEAK && u.getType() == ROLE_WEAK) //si el hijo es débil y están unidos por una relación debil significa que está bien
                        key = u.getChild().hashCode();
                        //key = entity.hashCode();
                    else
                        strong = true; //asume que tiene una entidad fuerte, (ya que con algo debe estar unido si no es una débil...)
                }
                //después de ver todos los hijos, preguta si la key existe, de existir significa que hay almenos una entidad débil relacionada con algo por unión débil
                if(weakEntityCheck.containsKey(key))
                {
                    EntityCheck temp = weakEntityCheck.get(key);
                    temp.strongEntity = strong;
                    weakEntityCheck.replace(key, temp);
                }
            }
            else if(element instanceof Heritage)  // si hay herencia...
            {

                //Obtiene los hijos del padre
                Element father = ((Union) (element.getChildren().get(0).getPrimitive())).getChild();
                List<Element> childs = Finder.findRelatedUnions(this.fetchElements(), father); //hijos padres;

                //crea un mapa con los nombres
                HashMap<String, Boolean> childsNames = new HashMap<String, Boolean>();
                boolean fhatherHeritage = true;
                //recorre los hijos del padre;
                for (Element ch : childs)
                {
                    String _name = ch.getPrimitive().getChildren().get(1).getPrimitive().getLabel();
                    if(_name.compareTo("S") != 0 && _name.compareTo("D") != 0) //si es distinto D o S (nombres reservados)
                    {
                        if (childsNames.containsKey(_name)) fhatherHeritage = false; //si el nombre del atributo se ha agregado antes, hay un error
                        else childsNames.put(_name, true); // si no esta todo bien y lo pone
                    }
                }
                //ahora para cada elemento de la herencia... (sin incluir el padre)
                for (int i = 1; i < element.getChildren().size(); ++i)
                {
                    Element temp = ((Union) (element.getChildren().get(i).getPrimitive())).getChild();
                    List<Element> tempChilds = Finder.findRelatedUnions(this.fetchElements(), temp);
                    HashMap<String, Boolean> tempsNames = new HashMap<String, Boolean>();
                    //hace lo mismo que hice con el padre...
                    boolean temprHeritage = true;
                    for (Element _ch : tempChilds)
                    {
                        String _chName = _ch.getPrimitive().getChildren().get(1).getPrimitive().getLabel();
                        if(_chName.compareTo("S") != 0 && _chName.compareTo("D") != 0)
                        {
                            if (tempsNames.containsKey(_chName)) temprHeritage = false;
                            else tempsNames.put(_chName, true);
                            if (childsNames.containsKey(_chName)) temprHeritage = false; //con la diferencia que ahora pregunta si está tambien en el padre.
                        }
                    }

                    //actualiza los mapas que contienen los errores
                    if (weakEntityCheck.containsKey(temp.hashCode()))
                    {
                        EntityCheck entytemp = weakEntityCheck.get(temp.hashCode());
                        entytemp.heritageName = temprHeritage;
                        weakEntityCheck.replace(temp.hashCode(), entytemp);
                    }
                }
                //actualiza los mapas que contienen los errores
                if (weakEntityCheck.containsKey(father.hashCode()))
                {
                    EntityCheck entytemp = weakEntityCheck.get(father.hashCode());
                    entytemp.heritageName = fhatherHeritage;
                    weakEntityCheck.replace(father.hashCode(), entytemp);
                }
            }
        }

        //recorre los hash obteniendo los mensajes y poniendo colores kawaii ;)
        List<Element> e = this.fetchElements();
        for (HashMap.Entry<Integer, EntityCheck> entry : weakEntityCheck.entrySet())
        {
            EntityCheck temp = entry.getValue();
            if(!temp.isValid())
                message += "\n" + temp.name + temp.toString();

            if(!temp.isValid() && temp.e.getElementState() != HIGHLIGHTED)
                temp.e.setElementState(INVALID);
            else if(temp.e.getElementState() == HIGHLIGHTED)
                temp.e.setElementState(HIGHLIGHTED);
            else
                temp.e.setElementState(NORMAL);
        }

        return message + "\n";
    }
    
    /**
     * @author Sebastian Cancino
     * @param type
     * @param n 
     */
    public void changeType(Type type, int n){
        Element target = currentElement.getPrimitive().getChildren().get(n);
        changeType(target, type);
    }
    
    /**
     * @author Sebastian Cancino
     * @param target
     * @param type 
     */
    public void changeType(Element target, Type type){
        ChangeElementTypeAction action = new ChangeElementTypeAction(target, type);
        action.execute();
        actionC.addToStack(action);
        this.clearMaps();
    }
    
    /**
     * Al ser solo 2 tipos de cardinalidad hace un swap en la union y su drawer.
     * @author Diego Vargas, Sebastian Cancino
     * @param index 
     */
    public void swapCardinality(int index){
        Element target = currentElement.getPrimitive().getChildren().get(index-1);
        ChangeCardinalityAction action = new ChangeCardinalityAction(target);
        action.execute();
        actionC.addToStack(action);
    }

    private void addEntities() {
        Element target = selectorC.getToAdd();
        List<Element> entities =  selectorC.getSelected();
        AddEntityAction action = new AddEntityAction(target, entities);
        if(action.execute())
            actionC.addToStack(action);
        finishAction();
    }
}
