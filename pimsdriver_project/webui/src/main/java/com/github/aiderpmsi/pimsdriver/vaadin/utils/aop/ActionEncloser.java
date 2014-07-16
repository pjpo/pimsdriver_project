package com.github.aiderpmsi.pimsdriver.vaadin.utils.aop;

import java.util.function.Function;

import com.vaadin.ui.Notification;

public class ActionEncloser<R> {

	public static void executeVoid(final Function<Throwable, String> errorMsgSupplier,
			final ExecuterVoid actionSupplier) {
		try {
			actionSupplier.execute();
		} catch (Throwable e) {
			showError(errorMsgSupplier.apply(e));
		}
	}

	@FunctionalInterface
	public interface ExecuterVoid {
		public void execute() throws Throwable;
	}

	public static <R> R execute(final Function<Throwable, String> errorMsgSupplier,
			final Executer<R> actionSupplier) {
		try {
			return actionSupplier.execute();
		} catch (Throwable e) {
			showError(errorMsgSupplier.apply(e));
		}
		return null;
	}

	@FunctionalInterface
	public interface Executer<R> {
		public R execute() throws Throwable;
	}
	
	private static void showError(final String message) {
		Notification.show(message, Notification.Type.WARNING_MESSAGE);
	}
}
