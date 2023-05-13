public class Token {
    private String tokenType;
    private String value;
    private int column;
    private int row;

    public String getTokenType(){
        return tokenType;
    }
    public String getValue(){
        if (value.equals(null)){
            return "token has no value";
        }
        return value;
    }
    public void setTokenType(String input){
        tokenType = input;
    }
    public void setValue(String input){
        value = input;
    }
    public void setLocation(int[] arr){
        column = arr[0];
        row = arr[1];
    }
    public int[] getLocation(){
        int arr[] = {column,row};
        return arr;
    }
    public String getString(){
        return new String(tokenType + " " + (column + 1) + ":" + (row + 1));
    }
}
