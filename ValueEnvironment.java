import java.util.*;

class ValueEnvironment{

private LinkedList<ValueList> liste;

public ValueEnvironment (){}

public void addList(ValueList v){
  liste.add(0,v);
}

public ValueList getList(){ return liste.getFirst();}

public void removeList(){ liste.removeFirst();}




}
