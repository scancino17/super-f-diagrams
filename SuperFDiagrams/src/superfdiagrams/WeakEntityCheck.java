package superfdiagrams;

public class WeakEntityCheck
{
    public String name;
    public boolean partialKey;
    public boolean strongEntity;

    public WeakEntityCheck(String _name)
    {
        name = _name;
        partialKey = false;
        strongEntity = false;
    }

    @Override
    public String toString()
    {
        String message = "";
        if(partialKey == false)
            message += "\nDebe tener una clave parcial.";
        if(strongEntity == false)
            message += "\nDeben estar relacionadas con una entidad fuerte";
        return message;
    }
}
