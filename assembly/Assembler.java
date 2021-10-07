package assembly;

import cpu.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Assembler {
	
	private static String jumpOpCode="0001";
	private static String loadOpCode="0010";
	private static String storeOpCode="0011";
	private static String addOpCode="0100";
	private static String subOpCode="0101";
	private static String mulOpCode="0110";
	private static String divOpCode="0111";
	private static String jnzOpCode="1000";
	private static String cmpOpCode="1001";
	
	private static Register R1=new Register("R1","10");
	private static Register R2=new Register("R2","11");
	private static Register R3=new Register("R3","01");
	
	public static ArrayList<String> convert(String filename) {
		
		ArrayList<String>codeList=new ArrayList<String>();
		ArrayList<Integer>indexes=new ArrayList<Integer>();
		    
		ArrayList<String>dbList=new ArrayList<String>();
		HashMap<String,String>nameMap=new HashMap<>();
		
		int addressCounter=0;
		try {
			File myObj = new File(filename);
		    Scanner myReader = new Scanner(myObj);
		    
		    while (myReader.hasNextLine()) {
		    	  
		    	String line = myReader.nextLine();
		        if(line.isEmpty())
		        	continue;
		        
		        String[] array = line.split(" ");
		        
		        if(array.length == 1) {
		        	
		        	if(array[0].equals("HLT")) {
		        		codeList.add("0000000000000000");
		        		addressCounter++;
		        	}
		        	else if(array[0].contains(":")){
		        		
		        		line = myReader.nextLine();
		        		String[] arr = line.split(" ");
		        		
		        		if(arr[0].equals("DB")) {
		        			nameMap.put(array[0].substring(0, array[0].length()-1), "-1");
		        			dbList.add(array[0].substring(0, array[0].length()-1)+" "+line);
		        		}
		        		else if(arr[0].equals("HLT")) {
		        			String number=decToBinary(addressCounter*16);
		        			String newNumber="";
		        			for(int j=0; j<10-number.length(); j++) 
		    					newNumber+="0";
		        			newNumber+=number;
		        			nameMap.put(array[0].substring(0, array[0].length()-1), newNumber);
			        		codeList.add("0000000000000000");
			        		addressCounter++;
			        	}
		        		else {
		        			String number=decToBinary(addressCounter*16);
		        			String newNumber="";
		        			for(int j=0; j<10-number.length(); j++) 
		    					newNumber+="0";
		        			newNumber+=number;
		        			nameMap.put(array[0].substring(0, array[0].length()-1), newNumber);
		        			
		        			ArrayList<String>list=new ArrayList<>();
		        			
		        			if(arr[0].equals("ADD") || arr[0].equals("SUB") || arr[0].equals("MUL") || arr[0].equals("DIV")) {
				        		list=operations(arr);
			        			if(isNumeric(arr[2])) {
			        				nameMap.put(arr[2], "-1");
				        			dbList.add(arr[2]+" DB "+arr[2]);
			        			}
		        			}
		        			
		        			else if(arr[0].equals("CMP")) {
				        		list=compare(arr);
				        		if(isNumeric(arr[2])) {
			        				nameMap.put(arr[2], "-1");
				        			dbList.add(arr[2]+" DB "+arr[2]);
			        			}
				        	}
				        	if(arr[0].equals("MOV")) {
				        		String tmp=loadOrStore(arr);
			        			codeList.add(tmp);
			        			indexes.add(codeList.indexOf(tmp));
			        			if(isNumeric(tmp.substring(6))) {
			        				nameMap.put(tmp.substring(6), "-1");
				        			dbList.add(tmp.substring(6)+" DB "+tmp.substring(6));
			        			}
			        			addressCounter++;
				        		
				        	}else {
				        		int size=list.size();
			        			addressCounter+=size;
			        			for(int i=0; i<list.size(); i++) {
			        				codeList.add(list.get(i));
			        				
			        				boolean added=false;
			        				for(int j=0; j<list.get(i).length(); j++) {
			        					if(list.get(i).charAt(j) >'1' || list.get(i).charAt(j) <'0') {
			        						indexes.add(codeList.indexOf(list.get(i)));
			        						added=true;
			        						break;
			        					}
			        				}
			        				if(added)
		        						continue;
			        				if(list.get(i).length() != 16) {
			        					indexes.add(codeList.indexOf(list.get(i)));
		        					}
			        			}
				        	}
		        		}
		        			
		        	}
		        }
		        
		        else if(array.length == 2) {
		        	if(array[0].equals("JMP")) {
		        		
		        		String tmp=jumpOpCode+""+array[1];
		        		codeList.add(tmp);
		        		indexes.add(codeList.indexOf(tmp));
		        		addressCounter++;
		        	}
		        	else if(array[0].equals("JNZ")) {
		        		String tmp=jnzOpCode+""+array[1];
		        		codeList.add(tmp);
		        		indexes.add(codeList.indexOf(tmp));
		        		addressCounter++;
		        	}
		        }
		        else if(array.length == 3) {
		        	
		        	ArrayList<String>list=new ArrayList<>();
		        	
		        	if(array[0].equals("ADD") || array[0].equals("SUB") || array[0].equals("MUL") || array[0].equals("DIV")) {
		        		list=operations(array);
		        		if(isNumeric(array[2])) {
	        				nameMap.put(array[2], "-1");
		        			dbList.add(array[2]+" DB "+array[2]);
	        			}
		        	}
		        	
		        	else if(array[0].equals("CMP")) {
		        		list=compare(array);
		        		if(isNumeric(array[2])) {
	        				nameMap.put(array[2], "-1");
		        			dbList.add(array[2]+" DB "+array[2]);
	        			}
		        	}
		        	if(array[0].equals("MOV")) {
		        		String tmp=loadOrStore(array);
	        			codeList.add(tmp);
	        			indexes.add(codeList.indexOf(tmp));
	        			if(isNumeric(tmp.substring(6))) {
	        				nameMap.put(tmp.substring(6), "-1");
		        			dbList.add(tmp.substring(6)+" DB "+tmp.substring(6));
	        			}
	        			addressCounter++;
		        		
		        	}else {
		        		int size=list.size();
	        			addressCounter+=size;
	        			
	        			for(int i=0; i<list.size(); i++) {
	        				codeList.add(list.get(i));
	        				
	        				boolean added=false;
	        				for(int j=0; j<list.get(i).length(); j++) {
	        					if(list.get(i).charAt(j) >'1' || list.get(i).charAt(j) <'0') {
	        						indexes.add(codeList.indexOf(list.get(i)));
	        						added=true;
	        						break;
	        					}
	        				}
	        				if(added)
        						continue;
	        				if(list.get(i).length() != 16) {
	        					indexes.add(codeList.indexOf(list.get(i)));
        					}
	        			}
		        	}
		        }
		        
		      }
		      myReader.close();
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		
		codeList.add("0000000000000000");
		addressCounter++;
		
		for(int i=0; i<dbList.size(); i++) {
			String[] array=dbList.get(i).split(" ");
			String binaryNumber=decToBinary(Integer.parseInt(array[2]));
			String number="";
			for(int j=0; j<16-binaryNumber.length(); j++)
				number+="0";
			number+=binaryNumber;
			codeList.add(number);
			
			String num=decToBinary(addressCounter*16);
			String newNumber="";
			for(int j=0; j<10-num.length(); j++)
				newNumber+="0";
			newNumber+=num;
			nameMap.replace(array[0],newNumber);
			addressCounter++;
		}
		
		for(int i=0; i<indexes.size(); i++) {
			String instruction=codeList.get(indexes.get(i));
			
			if(instruction.substring(0, 4).equals(jumpOpCode) || instruction.substring(0, 4).equals(jnzOpCode)) {
				String address=nameMap.get(instruction.substring(4));
				int length=address.length();
				String newInstr="";
				if(instruction.substring(0, 4).equals(jumpOpCode))
					newInstr=jumpOpCode;
				else
					newInstr=jnzOpCode;
				
				for(int j=0; j<12-length; j++)
					newInstr+="0";
				newInstr+=address;
				codeList.set(indexes.get(i), newInstr);
			}
			else {
				if(isNumeric(instruction.substring(4, 6))) {
					String address=nameMap.get(instruction.substring(6));
					String newInstr=instruction.substring(0,6);
					for(int j=0; j<10-address.length(); j++)
						newInstr+="0";
					newInstr+=address;
					codeList.set(indexes.get(i), newInstr);
				}else {
					int index=firstIndex(instruction);
					String address=nameMap.get(instruction.substring(4,index));
					String newInstr=instruction.substring(0,4);
					for(int j=0; j<10-address.length(); j++)
						newInstr+="0";
					newInstr+=address;
					newInstr+=instruction.substring(index);
					codeList.set(indexes.get(i), newInstr);
				}
				
			}		
		}
		return codeList;
	}
	
	public static ArrayList<String>compare(String[]array){
		ArrayList<String>codeList=new ArrayList<String>();
		String tmp="";
		
		if(array[2].contains("[") || isNumeric(array[2])) {
					
			String reg=array[1].substring(0, array[1].length()-1);
			String loc="";
			if(array[2].contains("["))
				loc=array[2].substring(1, array[2].length()-1);
			else
				loc=array[2];
			
			tmp=loadOpCode+""+R3.getAddress()+""+loc;
			codeList.add(tmp);
			tmp="";
			
			if(reg.equals("R1"))
				tmp=cmpOpCode+"0000"+R1.getAddress()+"0000"+R3.getAddress();
			else
				tmp=cmpOpCode+"0000"+R2.getAddress()+"0000"+R3.getAddress();
			codeList.add(tmp);
		}
		else {
			tmp=cmpOpCode+"0000"+R1.getAddress()+"0000"+R2.getAddress();
			codeList.add(tmp);
		}
		return codeList;
	}
	public static ArrayList<String> operations(String[] array) {
		
		ArrayList<String>codeList=new ArrayList<String>();
		String tmp="";
		
		if(array[2].contains("[") || isNumeric(array[2])) {
			
			String reg=array[1].substring(0, array[1].length()-1);
			String loc="";
			if(array[2].contains("["))
				loc=array[2].substring(1, array[2].length()-1);
			else
				loc=array[2];
			
			tmp=loadOpCode+""+R3.getAddress()+""+loc;
			codeList.add(tmp);
			tmp="";
			
			if(reg.equals("R1")) {
			
				if(array[0].equals("ADD"))
					tmp=addOpCode+"0000"+R1.getAddress()+"0000"+R3.getAddress();
				else if(array[0].equals("SUB"))
					tmp=subOpCode+"0000"+R1.getAddress()+"0000"+R3.getAddress();
				else if(array[0].equals("MUL"))
					tmp=mulOpCode+"0000"+R1.getAddress()+"0000"+R3.getAddress();
				else
					tmp=divOpCode+"0000"+R1.getAddress()+"0000"+R3.getAddress();
			}else {
				if(array[0].equals("ADD"))
					tmp=addOpCode+"0000"+R2.getAddress()+"0000"+R3.getAddress();
				else if(array[0].equals("SUB"))
					tmp=subOpCode+"0000"+R2.getAddress()+"0000"+R3.getAddress();
				else if(array[0].equals("MUL"))
					tmp=mulOpCode+"0000"+R2.getAddress()+"0000"+R3.getAddress();
				else
					tmp=divOpCode+"0000"+R2.getAddress()+"0000"+R3.getAddress();
			}
			codeList.add(tmp);
			
		}
		else {
			String reg2=array[2];
			if(array[0].equals("ADD"))
				tmp=addOpCode+"0000"+R1.getAddress()+"0000"+R2.getAddress();
			else if(array[0].equals("SUB"))
				tmp=subOpCode+"0000"+R1.getAddress()+"0000"+R2.getAddress();
			else if(array[0].equals("MUL"))
				tmp=mulOpCode+"0000"+R1.getAddress()+"0000"+R2.getAddress();
			else
				tmp=divOpCode+"0000"+R1.getAddress()+"0000"+R2.getAddress();
			codeList.add(tmp);
		}
		return codeList;
	}
	public static String loadOrStore(String[] array) {
		String string=array[2];
		String tmp="";
		
		if(string.contains("[")) {
			String reg=array[1].substring(0,array[1].length()-1);
			if(reg.equals("R1"))
				tmp=loadOpCode+""+R1.getAddress()+""+array[2].substring(1, array[2].length()-1);
			else	
				tmp=loadOpCode+""+R2.getAddress()+""+array[2].substring(1, array[2].length()-1);
		}
		else if(isNumeric(string)) {
			String reg=array[1].substring(0,array[1].length()-1);
			if(reg.equals("R1"))
				tmp=loadOpCode+""+R1.getAddress()+""+array[2];
			else	
				tmp=loadOpCode+""+R2.getAddress()+""+array[2];
		}
		else{
			String reg=array[2];
			if(reg.equals("R1"))
				tmp=storeOpCode+""+array[1].substring(0, array[1].length()-1)+""+R1.getAddress();
			else
				tmp=storeOpCode+""+array[1].substring(0, array[1].length()-1)+""+R2.getAddress();
		}
		return tmp;
	}

	public static boolean isNumeric(String string) {
		
		 try {
			 int intValue = Integer.parseInt(string);
		     return true;
		 }catch (NumberFormatException e) {
			 
		 }
		 return false;
	}
	
	public static String decToBinary(int n){
        String binaryNumber="";
        int[] binaryNum = new int[1000];
        int i = 0;
        
        while (n > 0) {
            binaryNum[i] = n % 2;
            n = n / 2;
            i++;
        }
        for (int j = i - 1; j >= 0; j--)
            binaryNumber+=String.valueOf(binaryNum[j]);
        
        return binaryNumber;
    }
	public static int firstIndex(String string) {
		for(int i=4; i<string.length(); i++)
			if(string.charAt(i) >='0' && string.charAt(i) <='9')
				return i;
		return -1;
	}
}
