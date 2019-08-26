package Server;

import java.io.*; 
import java.util.HashMap; 
import java.util.Map;
//import Server.XML;
public class Dictionary {
	public Map<String,String> map;
	private String path = "/Users/szg/Desktop/Dictionary.dat";
	@SuppressWarnings("unchecked")
	public Dictionary() {
		map = (Map<String, String>) readObjectFromFile();
	}
	public String Search(String index) {
		System.out.println(index+"-index length: "+ index.length());
		if(map.containsKey(index)) {
			return map.get(index);
		}
		else {
			return "Input word does not exist in Dictionary: " + index;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String Add(String index, String str) {
		System.out.println(index+"-index length: "+ index.length());
		System.out.println(str+ "-str length: "+ str.length());
		if(!map.containsKey(index)) {
			map.put(index, str);
			writeObjectToFile(this.map);
			this.map = (Map<String, String>) readObjectFromFile();
			System.out.println("map size: " + map.size() );
			return "Add successfully:" + index;
		}
		else {
			return "the word is existing in the Dictionary: "+ index;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String Delete(String index) {
		System.out.println(index+"-index length: "+ index.length());
		if(map.containsKey(index)) {
			map.remove(index);
			writeObjectToFile(this.map);
			this.map = (Map<String, String>) readObjectFromFile();
			return "Delete successfully: "+ index;
		}
		else {
			return "The word does not exist in the Dictionary:"+ index; 
		}
	}
	
	public static void writeObjectToFile(Object obj)
    {
	//using fileOutputStrea to write map to a dat file
        File file =new File("dictionary.dat");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            System.out.println("Dictionary writes success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static Object readObjectFromFile()
    {
        Object temp=null;
        File file =new File("dictionary.dat");
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
            System.out.println("map reads success!");
        } catch (IOException e) {        
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }
	//@SuppressWarnings("unchecked")
//	public static void main(String args[]) {
//		Dictionary dic = new Dictionary();
//		dic.Add("123", "onetwothree");
//		dic.Add("456", "fourfivesiz");
//		Dictionary.writeObjectToFile(dic.map);
//		dic.Add("789","seveneightnine");
//		Dictionary.writeObjectToFile(dic.map);
//		Dictionary dic1 = new Dictionary();
//		dic1.map = (Map<String, String>) Dictionary.readObjectFromFile();
//		System.out.println(dic1.map.size());
//	}
}
