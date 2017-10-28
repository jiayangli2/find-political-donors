import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;


public class Contribution {
	public static void main(String[] args) {
		HashSet<String> set = new HashSet<>();
		for (int i = 1960; i <= 2017; i++) {
			for (int j = 1; j <= 12; j++) {
				int days = days(i, j);
				for (int k = 1; k <= days; k++) {
					StringBuilder sb = new StringBuilder();
					sb.append(String.format("%02d", j));
					sb.append(String.format("%02d", k));
					sb.append(String.format("%04d", i));
					set.add(sb.toString());
				}
			}
		}
		HashMap<repZip, freqSum> map = new HashMap<>();
		HashMap<repDate, freqSum> map2 = new HashMap<>();
		
		try {
			File file = new File(args[0]);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			PrintWriter writer1 = new PrintWriter(args[1], "UTF-8");
			
			while ((line = bufferedReader.readLine()) != null) {
				String[] strs = line.split("\\|");
				// System.out.println(strs.length);
				if (strs.length < 15 || (15 < strs.length && strs[15].length() != 0) || strs[14].length() == 0 || strs[0].length() == 0) {
					continue;
				}
				String flier = strs[0];
				String amtStr = strs[14];
				double amt = Double.parseDouble(amtStr);
				try {
					String zip = strs[10].substring(0, 5);
					repZip key = new repZip(flier, zip);
					freqSum temp = null;
					if (!map.containsKey(key)) {
						temp = new freqSum(1, amt);
						temp.add(amt);
						map.put(key, temp);
					} else {
						temp = map.get(key);
						temp.freq += 1;
						temp.sum += amt;
						temp.add(amt);
					}
					writer1.println(buildZipString(key, temp));
				} catch (Exception e) {
					
				}
				// median_date
				String date = strs[13];
				if (!set.contains(date)) {
					continue;
				}
				repDate key2 = new repDate(flier, date);
				freqSum temp2 = null;
				if (!map2.containsKey(key2)) {
					temp2 = new freqSum(1, amt);
					temp2.add(amt);
					map2.put(key2, temp2);
				} else {
					temp2 = map2.get(key2);
					temp2.freq += 1;
					temp2.sum += amt;
					temp2.add(amt);
				}
			}
			fileReader.close();
			writer1.close();
			PrintWriter writer2 = new PrintWriter(args[2], "UTF-8");
			ArrayList<repDate> list = new ArrayList<>();
			list.addAll(map2.keySet());
			Collections.sort(list, new Comparator<repDate>(){
				public int compare(repDate rd1, repDate rd2) {
					if (!rd1.rep.equals(rd2.rep)) {
						return rd1.rep.compareTo(rd2.rep);
					} else {
						return helper(rd1.date, rd2.date);
					}
				}
			});
			for (repDate rd : list) {
				freqSum temp = map2.get(rd);
				writer2.println(buildDateString(rd, temp));
			}
			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static int helper(String s1, String s2) {
		String y1 = s1.substring(4);
		String y2 = s2.substring(4);
		if (!y1.equals(y2)) {
			return y1.compareTo(y2);
		}
		String m1 = s1.substring(0, 2);
		String m2 = s2.substring(0, 2);
		if (!m1.equals(m2)) {
			return m1.compareTo(m2);
		}
		return s1.substring(2, 4).compareTo(s2.substring(2, 4));
	}
	public static String buildZipString(repZip rz, freqSum fs) {
		StringBuilder sb = new StringBuilder();
		sb.append(rz.rep);
		sb.append("|");
		sb.append(rz.zip);
		sb.append("|");
		long med = Math.round(fs.median());
		sb.append(med);
		sb.append("|");
		sb.append(fs.freq);
		sb.append("|");
		sb.append(Math.round(fs.sum));
		return sb.toString();
	}
	public static String buildDateString(repDate rz, freqSum fs) {
		StringBuilder sb = new StringBuilder();
		sb.append(rz.rep);
		sb.append("|");
		sb.append(rz.date);
		sb.append("|");
		long med = Math.round(fs.median());
		sb.append(med);
		sb.append("|");
		sb.append(fs.freq);
		sb.append("|");
		sb.append(Math.round(fs.sum));
		return sb.toString();
	}
	public static int days(int yr, int mth) {
		Calendar ca = new GregorianCalendar(yr, mth - 1, 1);
		return ca.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
}

class repDate {
	String rep;
	String date;
	public repDate(String rep, String date) {
		this.rep = rep;
		this.date = date;
	}
	@Override
	public int hashCode() {
		return (rep + date).hashCode();
	}
	@Override
	public boolean equals(Object r) {
		return rep.equals(((repDate)r).rep) && date.equals(((repDate)r).date);
	}
}

class repZip {
	String rep;
	String zip;
	public repZip(String rep, String zip) {
		this.rep = rep;
		this.zip = zip;
	}
	@Override
	public int hashCode() {
		return (rep + zip).hashCode();
	}
	@Override
	public boolean equals(Object r) {
		return rep.equals(((repZip)r).rep) && zip.equals(((repZip)r).zip);
	}
}

class freqSum {
	double sum;
	int freq;
	PriorityQueue<Double> minHeap;
	PriorityQueue<Double> maxHeap;
	public freqSum(int freq, double sum) {
		this.freq = freq;
		this.sum = sum;
		this.minHeap = new PriorityQueue<>();
		this.maxHeap = new PriorityQueue<Double>(10, new Comparator<Double>(){
			public int compare(Double a, Double b) {
				return b.compareTo(a);
			}
		});
	}
	public double median() {
		double res = 0;
		if (minHeap.size() == maxHeap.size()) {
			res = (minHeap.peek() + maxHeap.peek()) * 0.5;
		} else if (minHeap.size() > maxHeap.size()) {
			res = minHeap.peek();
		} else {
			res = maxHeap.peek();
		}
		return res;
	}
	public void add(double amt) {
		if (minHeap.isEmpty()) {
			minHeap.add(amt);
			return;
		}
		if (minHeap.size() == maxHeap.size()) {
			double big = minHeap.peek();
			if (amt >= big) {
				minHeap.add(amt);
			} else {
				maxHeap.add(amt);
			}
		} else if (minHeap.size() > maxHeap.size()) {
			if (amt <= minHeap.peek()) {
				maxHeap.add(amt);
			} else {
				maxHeap.add(minHeap.poll());
				minHeap.add(amt);
			}
		} else {
			if (amt >= maxHeap.peek()) {
				minHeap.add(amt);
			} else {
				minHeap.add(maxHeap.poll());
				maxHeap.add(amt);
			}
		}
		
	}
}
