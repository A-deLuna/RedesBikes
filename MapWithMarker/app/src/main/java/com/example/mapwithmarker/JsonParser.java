package com.example.mapwithmarker;

import java.util.HashMap;
import java.util.Map;
public class JsonParser {
	public static Map<Integer, Pair<Float, Float>> parse(String S) {
		Map<Integer, Pair<Float, Float>> map = new HashMap<Integer,Pair<Float, Float>>();
		int state = 0;
		int i = 0;
		int key = 0;
		boolean running = true;
		Pair<Float, Float> p = new Pair(0f,0f);
		while (running) {
			switch (state) {
				case 0:{
					String s = "";
					while(S.charAt(i) != '"') {
						i++;
					}
					i++;
					while (S.charAt(i) != '"') {
						s = s + S.charAt(i);
						i++;
					}
					key = Integer.parseInt(s);
					state = 1;
					p = new Pair<Float, Float>(0f,0f);
					break;
				}
				case 1: {
					while(S.charAt(i) != '-' && !Character.isDigit(S.charAt(i))){
						i++;
					}
					String s = "";
					while(S.charAt(i) != ',' && S.charAt(i) != ']') {
						s = s + S.charAt(i);
						i++;
					}
					if(S.charAt(i) == ',') {
						p.first = Float.parseFloat(s);
					}
					else {
						p.second = Float.parseFloat(s);
						map.put(key, p);
						if(S.charAt(i+1) == '}') {
							running = false;
						}
						else {
							state = 0;
						}
					}

				}
			}
		}
		return map;
	}
}
