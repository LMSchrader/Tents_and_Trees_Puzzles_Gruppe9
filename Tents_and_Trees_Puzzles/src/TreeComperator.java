import java.util.Comparator;

public class TreeComperator implements Comparator<Tree> {
		public static final TreeComperator INSTANCE = new TreeComperator();

		private TreeComperator() {}

		@Override
		public int compare(Tree o1, Tree o2) {
			Integer[] o1TreePos = new Integer[] {o1.getPosition()[0], o1.getPosition()[1]};
			Integer[] o2TreePos = new Integer[] {o2.getPosition()[0], o2.getPosition()[1]};
			
            int firstComp = o1TreePos[0].compareTo(o2TreePos[0]);

            if (firstComp != 0) {
               return firstComp;
            } 


            return o1TreePos[1].compareTo(o2TreePos[1]);
		}
}
