package superfdiagrams;

import superfdiagrams.model.Element;
import superfdiagrams.model.primitive.Type;

public class EntityCheck
{
    public String name;
    public boolean partialKey;
    public boolean strongEntity;
    public boolean keyAtribute;
    public boolean heritageName;
    public Element e;
    boolean isComplex;
    Type type;
    public EntityCheck(String _name, Type _type, Element _e, boolean _isComplex)
    {
        name = _name;
        partialKey = false;
        strongEntity = false;
        keyAtribute = false;
        type = _type;
        e  = _e;
        heritageName = true;
        isComplex = _isComplex;
    }

    public void  setFalse()
    {
        partialKey = false;
        strongEntity = false;
        keyAtribute = false;

    }

    @Override
    public String toString()
    {
        String message = "";
        if(!isComplex)
        {
            if (type == Type.ROLE_WEAK)
            {
                if (!partialKey)
                    message += "\nDebe tener una clave parcial.";
                if (!strongEntity)
                    message += "\nDebe estar relacionadas con una entidad fuerte";
                return message;
            }
            else if (!keyAtribute)
                message += "\nDebe tener atributo clave";
        }
        if(!heritageName)
            message += "\nAtributos con mismo nombre que en Entidad padre";
        return message;
    }

    public boolean isValid()
    {
        return isComplex ? heritageName : (type == Type.ROLE_WEAK ? partialKey && strongEntity : keyAtribute) && heritageName;
    }
}
