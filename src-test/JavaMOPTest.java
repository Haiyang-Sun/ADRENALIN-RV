import java.util.HashSet;
import java.util.Iterator;
public class JavaMOPTest {
	public static void hasnext(){
		HashSet<Integer> arr = new HashSet<Integer>();
		arr.add(1);
		arr.add(2);
		Iterator<Integer> iter = arr.iterator();
		iter.next();
	}
	public static void main(String[] args){
		System.out.println("in javamop test");
		hasnext();
	}
}
