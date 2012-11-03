import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public final class Util {

    // Converts a file to a String with StringBuilder
    public static String readFile(File file) {
	StringBuilder result = new StringBuilder();
	try {
	    BufferedReader f = new BufferedReader(new FileReader(file));
	    try {
		char c;
		while (f.ready()) {
		    c = (char) f.read();

		    result.append(c);
		}
	    }
	    finally {
		f.close();
	    }
	}
	catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	return result.toString();
    }

    public static String readFile(String filename) {
	return readFile(new File(filename));
    }

    // extracts the String following title, until \n, in String searchThrough
    public static String extract(String title, String searchThrough) {
	int start = searchThrough.indexOf(title);
	if (start < 0)
	    return "";
	int end = searchThrough.indexOf('\n', start + 1);
	if (start + title.length() == end) // If the string is "" TODO
	    return "";
	return searchThrough.substring(start + title.length(), end - 1);
    }

    public static String extract(String start, String end, String searchThrough) {
	int s = searchThrough.indexOf(start);
	int e = searchThrough.indexOf(end);
	if (s < 0 || e < 0)
	    return "";
	return searchThrough.substring(s + start.length(), e);

    }

    public static void copyStringToClipboard(String s) {
	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	clipboard.setContents(new StringSelection(s), null);
    }

    public static ArrayList<File> getAllFiles(File dir) {
	ArrayList<File> allFiles = new ArrayList<File>();
	for (File f : dir.listFiles()) {
	    if (f.isFile())
		allFiles.add(f);
	    else if (f.isDirectory())
		allFiles.addAll(getAllFiles(f));
	}
	return allFiles;
    }

    public static ArrayList<File> getFiles(File dir) {
	ArrayList<File> allFiles = new ArrayList<File>();
	for (File f : dir.listFiles()) {
	    if (f.isFile())
		allFiles.add(f);
	    // recursive loop that finds all stuff- left out
	    // else if ( f.isDirectory() )
	    // allFiles.addAll( getAllFiles( f ) );
	}
	return allFiles;
    }

    public static String getExtension(File f) {
	return getExtension(f.getName());
    }

    public static String getExtension(String s) {
	String ext = null;
	int i = s.lastIndexOf('.');

	if (i > 0 && i < s.length() - 1)
	    ext = s.substring(i + 1).toLowerCase();

	if (ext == null)
	    return "";
	return ext;
    }

    public static String cutPath(String toCut) {
	// TODO replace AIBAT.cutpath
	return toCut.substring(toCut.lastIndexOf("\\") + 1);
    }

    // public static String cutPathAndExt( String toCut )
    // {
    // return cutPath( cutExt( toCut ) );
    // }

    public static String cutExt(String toCut) {
	if (toCut.indexOf('.') > 0)
	    return toCut.substring(0, toCut.lastIndexOf('.'));
	return toCut;
    }

    public static void openFileEditor(String fileName) throws IOException {
	if (Desktop.isDesktopSupported()) {
	    Desktop.getDesktop().edit(new File(fileName));
	}

    }

    public static void openFolder(String fileName) throws IOException {
	File file = new File(fileName);
	if (Desktop.isDesktopSupported() && file.isDirectory()) {
	    Desktop.getDesktop().open(file);
	}
    }

    // Opens a JFileChooser, which returns the selected directory or null.
    public static String chooseDirectory(String initDirectory, String type) {
	return chooseDirectory(initDirectory, type, type);
    }

    // Opens a JFileChooser, which returns the selected directory or null if
    // canceled or missed
    public static String chooseDirectory(String initDirectory, String title,
	    String buttonText) {
	JFileChooser fileChooser = new JFileChooser(initDirectory);
	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	fileChooser.setDialogTitle(title);
	int result = fileChooser.showDialog(null, buttonText);
	if (result == JFileChooser.CANCEL_OPTION)
	    return null;

	File file = fileChooser.getSelectedFile();
	if (file != null)
	    return file.getAbsolutePath();
	return null;
    }

    public static void errorMessage(String text) {
	JOptionPane.showMessageDialog(null, text, "Error",
		JOptionPane.ERROR_MESSAGE);
    }

    public static void errorSettings() {
	errorMessage("The file settings.txt could not be found\n"
		+ "(Make sure it is in the same folder as AIBat.jar)");
    }

    public static void errorException(Exception e) {
	errorException(e, "");
    }

    public static void errorException(Exception e, String addText) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	e.printStackTrace(pw);
	// Util.errorMessage(
	// "Please report this error along with the map that caused it:\n\n"
	// + sw.toString());

	textPane(
		"Error",
		"Please report this error:\n" + addText + "\n\n"
			+ sw.toString());
    }

    public static void textPane(String title, String text) {
	JFrame frame = new JFrame(title);
	JTextArea textArea = new JTextArea();
	// textArea.setEditable(false);
	textArea.setText(text);
	frame.getContentPane().add(textArea);
	frame.pack();
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }

    // Test
    public static void main(String args[]) {
    }

}
