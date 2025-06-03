package main.visitor.optimizer;

public class TypeDefPair {
    private String t1 = null;
    private String t2 = null;

    public TypeDefPair(String t1) {
        this.t1 = t1;
    }

    public void sett2(String t2){
        this.t2 = t2;
    }

    public Boolean is_t2_empty(){
        if(t2 == null){
            return true;
        }
        return false;
    }

    public String getT2() {
        return t2;
    }

    public String getT1() {
        return t1;
    }

    public Boolean is_full(){
        if(t1 != null && t2 != null){
            return true;
        }
        return false;
    }



}
