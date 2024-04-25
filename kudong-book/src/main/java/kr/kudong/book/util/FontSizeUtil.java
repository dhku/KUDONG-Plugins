package kr.kudong.book.util;

import net.md_5.bungee.api.ChatColor;

public class FontSizeUtil
{
	
	
	public static int getByteSize(String name) {
		
      	int en = 0;
        int ko = 0;
        int etc = 0;
        
        String temp = ChatColor.stripColor(name);
        
        char[] txtChar = temp.toCharArray();
        for (int j = 0; j < txtChar.length; j++) {
            if (txtChar[j] >= 'A' && txtChar[j] <= 'z') {
                en++;
            } else if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
                ko++;
                ko++;
            } else {
                etc++;
            }
        }
		
		return en+ko+etc;
		
	}
	
	public static int getIndex(String name,int index) {
		
      	int en = 0;
        int ko = 0;
        int etc = 0;
        index += 1;
        String temp = ChatColor.stripColor(name);
        
        char[] txtChar = temp.toCharArray();
        for (int j = 0; j < txtChar.length; j++) {
            if (txtChar[j] >= 'A' && txtChar[j] <= 'z') {
                en++;
            } else if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
                ko++;
                if(index <= en+ko+etc) {
                	return j;
                }
                ko++;
            } else {
                etc++;
            }
            
            if(index <= en+ko+etc) {
            	return j;
            }
        }
        
        return txtChar.length-1;
		
	}
	
	public static void main(String[] args) {
		System.out.println(getByteSize("ssdfsfssff시발 테스트"));
		
		System.out.println(getIndex("<알다르 채팅 채널 리스트>dfg",0));
		
		
	}
	
	

}
