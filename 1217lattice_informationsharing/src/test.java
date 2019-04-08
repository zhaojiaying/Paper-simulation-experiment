
public class test {
	public static void main(String[] args) {
		System.out.println(getNum(1.3));
	}
	
	public static double getNum(double number) {
		//payoffdiff/Math.abs(getNumCount(payoffdiff))
		int count=0;
		if (number < 1 && number>=0) {
			return number;
		}
		else {
			int k = (int) number;
			while (k > 0) {
				k = k / 10;
				count++;
			}
			return number/Math.pow(10,count);
			
		}
	}
}
