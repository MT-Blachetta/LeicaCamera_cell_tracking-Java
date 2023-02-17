

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHashMap {
	public static Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();

	public static void main(String[] args) {
		
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		List<String> list3 = new ArrayList<String>();
		List<String> list4 = new ArrayList<String>();
		
		list1.add("a");
		list1.add("a");
		list1.add("a");
		
		list2.add("b");
		list2.add("b");
		list2.add("b");
		list2.add("b");
		
		list3.add("c");
		list3.add("c");
		
		list4.add("d");
		
		map.put(1, list2);
		map.put(2, list4);
		map.put(3, list1);
		map.put(9, list3);
		
		System.out.print("Before Removing: ");
		for(int i = 0; i < 5; i++) {
			System.out.print(map.get(i));
		}
		
		map.remove(0);
		
		System.out.print("\nAfter Removing: ");
		for(int i = 0; i < 5; i++) {
			System.out.print(map.get(i));
		}

		System.out.println(map.get(9));
	}
	
}
