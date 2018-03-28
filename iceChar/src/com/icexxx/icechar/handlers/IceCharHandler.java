package com.icexxx.icechar.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class IceCharHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelection selection = window.getSelectionService().getSelection();
		String className = selection.getClass().getName();
		if ("org.eclipse.jface.text.TextSelection".equals(className)) {
			Object document = get(window.getSelectionService().getSelection(), "getDocument");
			String text = get(selection, "getText") + "";
			int length = (int) get(selection, "getLength");
			int offset = (int) get(selection, "getOffset");
			// int startLine = (int) get(selection, "getStartLine");
			// int endLine = (int) get(selection, "getEndLine");
			if (text == null || "".equals(text.trim())) {

			} else {
				text = cast(text);
				set(document, "replace", offset, length, text);
			}
		} else if ("org.eclipse.jface.viewers.TreeSelection".equals(className)) {

		}
//		MessageDialog.openInformation(window.getShell(), "IceChar", "IceChar");
		return null;
	}

	private void setClip(String text) {
		java.awt.datatransfer.Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(text);
		clip.setContents(tText, null);
	}

	private String cast(String text, int startLine) {
		int index = text.indexOf("=");
		String left = text.substring(0, index);
		String leftTrim = left.trim();
		int spaceIndex = leftTrim.indexOf(" ");
		String name = "";
		if (spaceIndex == -1) {
			return text;
		} else {
			String type = leftTrim.substring(0, spaceIndex).trim();
			name = leftTrim.substring(spaceIndex + 1).trim();
		}
		left = text.substring(0, index) + "= null;\r\n";
		String right = repeat(startLine) + name + " = " + text.substring(index + 1).trim();
		return left + right;
	}

	private Object get(Object selection, String name) {
		Class<?> class1 = selection.getClass();
		try {
			Method declaredMethod = class1.getDeclaredMethod(name);
			if (!declaredMethod.isAccessible()) {
				declaredMethod.setAccessible(true);
			}
			Object invoke = declaredMethod.invoke(selection);
			return invoke;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object get(Object document, String name, int startLine) {
		Class<?> class1 = document.getClass();
		try {
			Method declaredMethod = class1.getDeclaredMethod(name, int.class);
			if (declaredMethod == null) {
				declaredMethod = class1.getSuperclass().getDeclaredMethod(name, int.class);
			}
			if (!declaredMethod.isAccessible()) {
				declaredMethod.setAccessible(true);
			}
			Object invoke = declaredMethod.invoke(document, startLine);
			return invoke;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void set(Object document, String name, int i, int j, String string2) {
		try {
			Class<? extends Object> class1 = document.getClass();
			Method declaredMethod = class1.getDeclaredMethod(name, int.class, int.class, String.class);
			if (!declaredMethod.isAccessible()) {
				declaredMethod.setAccessible(true);
				declaredMethod.invoke(document, i, j, string2);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private String repeat(int notSpace) {
		if (notSpace <= 0) {
			return "";
		}
		char[] chs = new char[notSpace];
		for (int i = 0; i < chs.length; i++) {
			chs[i] = ' ';
		}
		return new String(chs);
	}

	private String cast(String str) {
		String strTrim = str.trim();
		if (strTrim.contains("\r\n")) {
			StringBuilder sb = new StringBuilder();
			sb.append(firstChar(str));
			sb.append("    ");
			sb.append(strTrim.replace("\r\n", "\r\n    "));
			return sb.toString();
		} else if (strTrim.contains("\n")) {
			StringBuilder sb = new StringBuilder();
			sb.append(firstChar(str));
			sb.append("    ");
			sb.append(strTrim.replace("\n", "    \n"));
			return sb.toString();
		} else {
			if (strTrim.contains("=")) {
				return cast(strTrim, 8);
			} else if (strTrim.length() > 2 && ":".equals(strTrim.charAt(1) + "") && strTrim.contains("\\")) {
				return strTrim.replace("\\", "/");
			} else if (strTrim.length() > 0 && isAllChar(strTrim)) {
				return strTrim.substring(0, 1).toLowerCase() + strTrim.substring(1);
			}
		}
		return "";
	}

	private static boolean isAllChar(String str) {
		if (str == null || "".equals(str.trim())) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (!Character.isLetterOrDigit(ch) && ch != '_') {
				return false;
			}
		}
		return true;
	}
	private static Object firstChar(String str) {
		if (str == null || "".equals(str)) {
			return str;
		}
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch != ' ' && ch != '\t') {
				return str.substring(0, i);
			}
		}
		return "";
	}
}
