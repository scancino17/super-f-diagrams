package superfdiagrams;

import superfdiagrams.model.primitive.Type;

public class EntityCheck
{
    public String name;
    public boolean partialKey;
    public boolean strongEntity;
    public boolean keyAtribute;
    Type type;
    public EntityCheck(String _name, Type _type)
    {
        name = _name;
        partialKey = false;
        strongEntity = false;
        keyAtribute = false;
        type = _type;
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
        if(type == Type.ROLE_WEAK)
        {
            if (!partialKey )
                message += "\nDebe tener una clave parcial.";
            if (!strongEntity)
                message += "\nDebe estar relacionadas con una entidad fuerte";
            return message;
        }
        else if(type == Type.ROLE_STRONG)
            if(!keyAtribute)
                message += "\nDebe tener atributo clave";
        return message;
    }

    public boolean isValid(){
        return  type == Type.ROLE_WEAK ? partialKey && strongEntity : keyAtribute;
    }
}
