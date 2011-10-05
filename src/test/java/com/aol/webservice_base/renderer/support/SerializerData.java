package com.aol.webservice_base.renderer.support;

import java.util.ArrayList;

public class SerializerData {
	public static class SimpleClass {
		public int i=1;
		protected int hidden1 = 2;
		private int hidden2 = 2;
	}

	public static class NullClass {
		public String s = null;
	}	
	
	public static class NestedClass {
		public SimpleClass zero = new SimpleClass();
		public String hello = "hello";
		public SimpleClass one = new SimpleClass();
		public SimpleClass two = new SimpleClass();
	}

	public static class IntArrayClass {
		public int[] items = new int[2];
		
		public IntArrayClass() {
			items[0] = 1;
			items[1] = 2;
		}
	}		
	
	public static class ArrayClass {
		public SimpleClass[] items = new SimpleClass[2];
		
		public ArrayClass(boolean hasNull) {
			SimpleClass one = new SimpleClass();
			items[0] = one;
			if (hasNull)
				items[1] = null;
			else {
				SimpleClass two = new SimpleClass();
				two.i = 2;
				items[1] = two;
			}
		}
	}	

	public static class ListClass_S {
		public ArrayList<SimpleClass> items = new ArrayList<SimpleClass>();
		
		public ListClass_S() {
			SimpleClass one = new SimpleClass();
			items.add(one);
			SimpleClass two = new SimpleClass();
			two.i = 2;
			items.add(two);
		}
	}

	public static class ListClass_IES {
		public ArrayList<SimpleClass> bunnies = new ArrayList<SimpleClass>();
		
		public ListClass_IES() {
			SimpleClass one = new SimpleClass();
			bunnies.add(one);
			SimpleClass two = new SimpleClass();
			two.i = 2;
			bunnies.add(two);
		}
	}
	
	public static class ListClass_ES {
		public ArrayList<SimpleClass> messages = new ArrayList<SimpleClass>();
		
		public ListClass_ES() {
			SimpleClass one = new SimpleClass();
			messages.add(one);
			SimpleClass two = new SimpleClass();
			two.i = 2;
			messages.add(two);
		}
	}
	
	public static class ListClass_SES {
		public ArrayList<SimpleClass> classes = new ArrayList<SimpleClass>();
		
		public ListClass_SES() {
			SimpleClass one = new SimpleClass();
			classes.add(one);
			SimpleClass two = new SimpleClass();
			two.i = 2;
			classes.add(two);
		}
	}

	public static class QuotesTest {
		public String quoted = "This string has quotes \" and '";
	}

	public static class LessThanTest {
		public String lessThan = "This string has <";
	}

	public static class GreaterThanTest {
		public String greaterThan = "This string has >";
	}

	public static class CDATATest {
		public String cdata = "This string has <![CDATA[";
	}

}
