package tfar.scribbles.Util;

public class a {
  public static String[] decompose(String resourceName, char splitOn) {
    String[] astring = new String[]{"minecraft", resourceName};
    int i = resourceName.indexOf(splitOn);
    if (i >= 0) {
      astring[1] = resourceName.substring(i + 1);
      if (i >= 1) {
        astring[0] = resourceName.substring(0, i);
      }
    }
    return astring;
  }

  public static String stringtoRL(String path){
    return getRl(decompose(path,':'));
  }

  public static String getRl(String[] strings) {
        return org.apache.commons.lang3.StringUtils.isEmpty(strings[0])?"minecraft":strings[0] +":"+ strings[1];
  }


  public static final String[] chars = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m",
          "n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","3","4","5","6","7","8","9","_"};

}
