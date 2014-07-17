package com.github.pjpo.pimsdriver.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class DoNothing {

	public DoNothing() {
		List<String> liste = new LinkedList<>();
		liste.forEach(new Consumer<String>() {
			@Override public void accept(String t) {
				System.out.println(t);
			}
		});
	}

}
