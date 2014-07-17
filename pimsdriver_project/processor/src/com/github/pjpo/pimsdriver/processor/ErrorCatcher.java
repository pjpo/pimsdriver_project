package com.github.pjpo.pimsdriver.processor;

public class ErrorCatcher {

	public static Throwable execute(Executor ex, Throwable context) {
		try {
			ex.execute();
			return context;
		} catch (Throwable e) {
			if (context != null) {
				e.addSuppressed(context);
			}
			return e;
		}
	}

	public static interface Executor {
		public void execute() throws Throwable;
	}
}
