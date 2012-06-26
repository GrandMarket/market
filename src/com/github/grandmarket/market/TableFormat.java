package com.github.grandmarket.market;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class TableFormat {
	
	// TableFormat is a way of storing values or strings in a printable table
	// After the table has been set up, calling the .print(sender); method
	// will print it to the given sender.
	
	// Codes at the start of a cell
	// -m will merge the cell with the cell to the left DOESNT WORK YET
	// -a will align the cell to the right
	// -r will use red text DOESNT WORK YET
	// -g will use green text DOESNT WORK YET
	// -b will use blue text DOESNT WORK YET
	
	public String[][] cell;
	public int w, h;
	
	public TableFormat(int width, int height) {
		w = width;
		h = height;
		cell = new String[w][h];
	}
		
	public void editCell(int x, int y, String val) {
		cell[x][y] = val;
		
	}
		
	public int getColumnWidth(int x) {
		int max = 0;
		for(int i = 0; i<h; i++) {
			int length = 0;
			String real = withoutControlCode(cell[x][i]);
			length = real.length();
			if(length > max) max = length;
		}
		return max;
	}
	
	private String withoutControlCode(String o) {
		String n = o;
		while(o.startsWith("-")) {
			n = n.substring(2);
		}
		return n;
	}
	
	public char getControlChar(String o) throws Exception {
		if(o.startsWith("-")) {
			return o.charAt(1);
		}
		throw new Exception("No control character");
	}
	
	private String addSpaces(String o, int n) {
		String spaces = "";
		for(int i = 0; i<n; ) {
			spaces = spaces.concat(" ");
		}
		return o + spaces;
	}
	
	private String addSpacesAtStart(String o, int n) {
		String spaces = "";
		for(int i = 0; i<n; ) {
			spaces = spaces.concat(" ");
		}
		return spaces + o;
	}
	
	private String stringDupe(String string, int number) {
		String ret = "";
		for(int i = 0; i<number; i++) {
			ret = ret.concat(string);
		}
		return ret;
	}
	
	public void print(CommandSender sender) {
		
		for(int line = 0; line<h; line++) {
			
			String print = "|";
			for(int x = 0; x<w; x++) {
				String o = cell[x][line];
				String p = withoutControlCode(o);
				boolean a = false;
				try {
					char cc = getControlChar(o);
					
					switch(cc) {
					case 'a' :
						a = true;
					}
				} catch (Exception e) {

				}
				p = setToColumnWidth(p, x, a);
				print = print.concat(p);
			}
			print = print.concat("|");
			sender.sendMessage(print);
		}
	}
	
	private String setToColumnWidth(String val, int column, boolean alignRight) {
		int targetWidth = getColumnWidth(column);
		int currentWidth = val.length();
		if(targetWidth == currentWidth) return val;
		int numberOfSpacesToAdd = targetWidth - currentWidth;
		if(alignRight) {
			val = addSpacesAtStart(val, numberOfSpacesToAdd);
		} else {
			val = addSpacesAtStart(val, numberOfSpacesToAdd);			
		}
		return val;
	}

	private int getTotalWidth() {
		int totalWidth = 0;
		for(int i = 0; i<w; i++) {
			totalWidth += getColumnWidth(i);
		}
		return totalWidth;
	}
}
