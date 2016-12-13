package br.com.silva.Tools;

public class TimeTools {

	public static String formatTime(int elapsed) {
		int ss = elapsed % 60;
		elapsed /= 60;
		int min = elapsed % 60;
		elapsed /= 60;
		int hh = elapsed % 24;
		return strZero(hh) + ":" + strZero(min) + ":" + strZero(ss);
	}

	private static String strZero(int n) {
		if (n < 10)
			return "0" + String.valueOf(n);
		return String.valueOf(n);
	}
}
