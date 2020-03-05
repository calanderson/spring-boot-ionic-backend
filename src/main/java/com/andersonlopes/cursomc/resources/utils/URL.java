package com.andersonlopes.cursomc.resources.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class URL {
	
	public static String decodeParam(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static List<Integer> decodeIntList(String s){
		List<Integer> lista = new ArrayList<>();
		if(s.length() > 0) {
			String[] values = s.split(",");
		
			for(String value : values) {
				lista.add(Integer.parseInt(value));
			}
		}
		return lista;
	}
}
