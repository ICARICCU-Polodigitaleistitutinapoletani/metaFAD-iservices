package com.gruppometa.unimarc.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.gruppometa.unimarc.MarcConvertor;
import com.gruppometa.unimarc.logging.UserLogger;
import com.gruppometa.unimarc.object.DefaultOutput;
import com.gruppometa.unimarc.object.Output;
import com.gruppometa.unimarc.output.MysqlOutputFormatter;
import com.gruppometa.unimarc.output.OutputFormatter;
import com.gruppometa.unimarc.output.XmlOutputFormatter;
import com.gruppometa.unimarc.profile.XmlProfile;

public class MarcConvertorGui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3508785562444529113L;

	public static void main(String args[]) {
		new MarcConvertorGui();
	}

	static String version = "0.9.4 28/01/2014";
	JTextField file = new JTextField();
	JTextField file2 = new JTextField();
	JLabel risultato = new JLabel(
			"Premi il pulsante per eseguire la trasformazione!");

	JButton but2 = new JButton("Esegui la trasformazione!");
	JRadioButton radio = new JRadioButton("MySQL");
	JCheckBox checkBox = new JCheckBox("Crea indici");
	JCheckBox checkBox2 = new JCheckBox("Truncate");
	JCheckBox checkBox3 = new JCheckBox("Drop");
	
	public MarcConvertorGui() {
		this.setLayout(new FlowLayout());
		this.setTitle("MarcConvertor - " + version);
		JLabel jlbHelloWorld = new JLabel("File:");
		add(jlbHelloWorld);
		file.setPreferredSize(new Dimension(400, 20));
		add(file);
		final JButton but = new JButton("Seleziona file");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser choooser = new JFileChooser();
				int returnVal = choooser.showOpenDialog(but);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file.setText(choooser.getSelectedFile().getAbsolutePath());
					file2.setText(choooser.getSelectedFile().getAbsolutePath()+
						(radio.isSelected()?".sql":".xml"));
				}
			}
		});
		add(but);

		JLabel jlbHelloWorld3 = new JLabel("Formato di output:");
		add(jlbHelloWorld3);

		radio.setSelected(true);
		final JRadioButton radio2 = new JRadioButton("XML");
		ButtonGroup group = new ButtonGroup();
		group.add(radio);
		group.add(radio2);
		
		file2.setPreferredSize(new Dimension(300, 20));
		file2.setText(new File("output.sql").getAbsolutePath());

		radio.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JRadioButton but = (JRadioButton) e.getSource();
				if (but.isSelected() && file2.getText().endsWith(".xml")) {
					file2.setText(file2.getText().substring(0,
							file2.getText().length() - 4)
							+ ".sql");
				}

			}
		});
		radio2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JRadioButton but = (JRadioButton) e.getSource();
				if (but.isSelected() && file2.getText().endsWith(".sql")) {
					file2.setText(file2.getText().substring(0,
							file2.getText().length() - 4)
							+ ".xml");
				}

			}
		});
		add(radio);
		add(radio2);
		
		checkBox.setSelected(true);
		checkBox.setToolTipText("Opzione per scrivere le istruzioni SQL per la creazione degli indici.");
		add(checkBox);
		
		checkBox2.setSelected(true);
		checkBox2.setToolTipText("Opzione per scrivere le istruzioni SQL per svuotare completamente le tabelle.");
		add(checkBox2);

		checkBox3.setSelected(true);
		checkBox3.setToolTipText("Opzione per scrivere le istruzioni SQL per cancellare le tabelle.");
		add(checkBox3);
		
		this.setResizable(false);
		JPanel panel = new JPanel();
		// panel.setLayout(new BorderLayout());
		JLabel jlbHelloWorld2 = new JLabel("File da creare:");
		panel.add(jlbHelloWorld2);
		panel.add(file2);
		panel.setPreferredSize(new Dimension(600, 50));
		final JButton but3 = new JButton("Seleziona");
		but3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser choooser = new JFileChooser();
				int returnVal = choooser.showSaveDialog(but);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File fFile = choooser.getSelectedFile();
					if (fFile.exists()) {
						int response = JOptionPane.showConfirmDialog(null,
								"Vuoi soprascrivere il file esistene?",
								"Conferma soprascrittura",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (response == JOptionPane.CANCEL_OPTION)
							return;
					}
					file2.setText(choooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		panel.add(but3);

		add(panel);
		add(but2);
		
		
		
		but2.addActionListener(new TastActionListern(this));

		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel2.setPreferredSize(new Dimension(500, 30));

		panel2.add(risultato);
		add(panel2);

		JPanel statusPanel = new JPanel();
		JLabel lab = new JLabel("Memoria:");
		statusPanel.add(lab);
		memProgressBar.setPreferredSize(new Dimension(40, 15));
		// /memProgressBar.setMaximumSize(new Dimension(30,15));
		memProgressBar.setStringPainted(true);
		// memProgressBar.setBackground(Color.green);
		// memProgressBar.setForeground(ColorProvider.getColorBack());
		memProgressBar.setToolTipText("Memoria allocata");

		memProgressBar.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					System.gc();
					// System.out.println("GC");
					setMemUse();
				}
			}

		});
		statusPanel.add(memProgressBar);
		add(statusPanel);
		setMemUse();

		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				setMemUse();				
			}
			
		};
		Timer timer = new Timer();
		timer.schedule( task, 5000, 5000 ); 
		
		this.setSize(600, 250);
		// pack();
		setVisible(true);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			public void windowClosed(WindowEvent e) {

			}

			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	protected TimerTask makeConvertorTask(){
		TimerTask convertorTask = new TimerTask() {
			
			@Override
			public void run() {
				try {
					risultato.setText("Attendere: trasformazione in corso...");
					but2.setEnabled(false);
					MarcConvertor convertor = new MarcConvertor();
					convertor.setProfile(new XmlProfile());
					convertor.getProfile().init();
					String filename = file.getText();
					InputStream fin = new BufferedInputStream(
							new FileInputStream(new File(filename)));
					UserLogger.logger.info("Si legge il file '" + filename
							+ "'.");
					Output out;
					out = new DefaultOutput();
					// out = convertor.convert(fin);
					Appendable outfile = null;
					outfile = new BufferedWriter(new OutputStreamWriter(  
							new FileOutputStream(file2
									.getText()),
							"UTF-8")
							);
			
					//outfile = new BufferedWriter(new FileWriter(new File(file2
					//		.getText())));
					UserLogger.logger.info("Si scrive il file '"
							+ file2.getText() + "'.");
					OutputFormatter formatter;
					if (radio.isSelected()) {
						formatter = new MysqlOutputFormatter(out);
						((MysqlOutputFormatter) formatter)
								.setCreateIndexes(checkBox.isSelected());
						((MysqlOutputFormatter) formatter).setDropTables(checkBox3.isSelected());
						((MysqlOutputFormatter) formatter)
								.setTruncateTables(checkBox2.isSelected());
						UserLogger.logger
								.info("Si scrive l'output in formato MySQL.");
					} else {
						formatter = new XmlOutputFormatter(out);
						UserLogger.logger
								.info("Si scrive l'output in formato XML.");
					}
					formatter.setOutfile(outfile);
					// formatter.toXml(outfile);
					convertor.convert(fin, formatter);
					if (outfile instanceof BufferedWriter)
						((BufferedWriter) outfile).close();
					risultato
							.setText("Risultato: trasformazione eseguito con successo.");					
				} catch (Exception ex) {
					risultato.setText("Errore: " + ex.getLocalizedMessage());
				}
				but2.setEnabled(true);
			}
		};
		return convertorTask;
	}
	private void setMemUse() {
		setMemUse((int) MarcConvertorGui.getMemoryUse());

	}

	public static float getMemoryUse() {
		long free = Runtime.getRuntime().freeMemory();
		long max = Runtime.getRuntime().maxMemory();
		long total = Runtime.getRuntime().totalMemory();
		float q = total - free;
		q = q / max;
		q = (int) (q * 1000);
		q = q / 10;
		return q;
	}

	JProgressBar memProgressBar = new JProgressBar();

	public void setMemUse(int useInPercent) {

		if (useInPercent > 90)
			memProgressBar.setBackground(Color.red);
		else
			memProgressBar.setBackground(Color.white);
		memProgressBar.setValue(useInPercent);
		memProgressBar.setString(useInPercent + "%");
		memProgressBar.setToolTipText("Memoria allocata del massimo di "
				+ (Runtime.getRuntime().maxMemory() / 1000000) + " Mb");
	}

	class TastActionListern implements ActionListener{
		MarcConvertorGui gui ;
		public TastActionListern(MarcConvertorGui gui){
			this.gui = gui;
		}
		public void actionPerformed(ActionEvent e) {
			Timer timer = new Timer();
			timer.schedule(gui.makeConvertorTask(), 10);			
		}
	}
}
