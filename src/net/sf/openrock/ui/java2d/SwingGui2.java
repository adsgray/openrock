package net.sf.openrock.ui.java2d;

import net.sf.openrock.game.Game;
import net.sf.openrock.game.ConfigImporter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridLayout;
import java.awt.Frame;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;

/* Generated with Google WindowBuilder Pro in Eclipse 
   startGamePressed() is manual of course.
*/

public class SwingGui2 extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField networkhost;
	private JTextField team1;
	private JTextField team2;

	
	private Game game;
	private JSpinner ends;
	private JComboBox curltype;
	private JComboBox scoringtype;
	private JComboBox playerstype;
	private JSpinner maxspeederror;
	private JSpinner maxdirerror;
	private JSpinner networkport;
	private JComboBox gametype;
	
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			SwingGui2 dialog = new SwingGui2();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	/**
	 * Create the dialog.
	 */

	public SwingGui2() {
		createGameDialog();
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void showGameDialog(String blah)
	{
		this.setVisible(true);
	}
	
	public void showNetworkDialog(String msg) {}
	public void hideNetworkDialog() {}


	private void startGamePressed() {
		
		this.setVisible(false);
		String teamname1 = team1.getText();
		String teamname2 = team2.getText();

		final int ends = (int)(Integer)this.ends.getValue();
		final int port = (int)(Integer)this.networkport.getValue();
		final int gametype = this.gametype.getSelectedIndex();
		final int curltype = this.curltype.getSelectedIndex();
		final int scoringtype = this.scoringtype.getSelectedIndex();
		final int playerstype = this.playerstype.getSelectedIndex();
		final int maxspeederror = (int)(Integer)this.maxspeederror.getValue();
		final int maxdirerror = (int)(Integer)this.maxdirerror.getValue();

		game.createConfig( new ConfigImporter() {
			public int ends() { return ends; }
			public int curlType() { return curltype; }
			public int scoringType() { return scoringtype; }
			public int playersType() { return playerstype; }
			public int maxSpeedError() { return maxspeederror; }
			public int maxDirError() { return maxdirerror; }
		}
		);
		
		game.newHotSeatMatch(teamname1, teamname2, ends);
	}

	private void createGameDialog()
	{
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{78, 137, 114, 106, 0};
		gbl_contentPanel.rowHeights = new int[]{31, 26, 0, 0, 27, 27, 0, 0, 0, 230, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblgametype = new JLabel("Game Type");
			GridBagConstraints gbc_lblgametype = new GridBagConstraints();
			gbc_lblgametype.anchor = GridBagConstraints.EAST;
			gbc_lblgametype.insets = new Insets(0, 0, 5, 5);
			gbc_lblgametype.gridx = 0;
			gbc_lblgametype.gridy = 0;
			contentPanel.add(lblgametype, gbc_lblgametype);
		}
		{
			gametype = new JComboBox();
			gametype.setModel(new DefaultComboBoxModel(new String[] {"Hot Seat", "Network Server", "Network Client"}));
			gametype.setSelectedIndex(0);
			gametype.setMaximumRowCount(3);
			GridBagConstraints gbc_gametype = new GridBagConstraints();
			gbc_gametype.insets = new Insets(0, 0, 5, 5);
			gbc_gametype.fill = GridBagConstraints.HORIZONTAL;
			gbc_gametype.gridx = 1;
			gbc_gametype.gridy = 0;
			contentPanel.add(gametype, gbc_gametype);
		}
		{
			JLabel lblNetworkHost = new JLabel("Network Host");
			GridBagConstraints gbc_lblNetworkHost = new GridBagConstraints();
			gbc_lblNetworkHost.anchor = GridBagConstraints.EAST;
			gbc_lblNetworkHost.insets = new Insets(0, 0, 5, 5);
			gbc_lblNetworkHost.gridx = 2;
			gbc_lblNetworkHost.gridy = 0;
			contentPanel.add(lblNetworkHost, gbc_lblNetworkHost);
		}
		{
			networkhost = new JTextField();
			GridBagConstraints gbc_networkhost = new GridBagConstraints();
			gbc_networkhost.fill = GridBagConstraints.HORIZONTAL;
			gbc_networkhost.insets = new Insets(0, 0, 5, 0);
			gbc_networkhost.gridx = 3;
			gbc_networkhost.gridy = 0;
			contentPanel.add(networkhost, gbc_networkhost);
			networkhost.setColumns(10);
		}
		{
			JLabel lblNetworkPort = new JLabel("Network Port");
			GridBagConstraints gbc_lblNetworkPort = new GridBagConstraints();
			gbc_lblNetworkPort.anchor = GridBagConstraints.EAST;
			gbc_lblNetworkPort.insets = new Insets(0, 0, 5, 5);
			gbc_lblNetworkPort.gridx = 2;
			gbc_lblNetworkPort.gridy = 1;
			contentPanel.add(lblNetworkPort, gbc_lblNetworkPort);
		}
		{
			networkport = new JSpinner();
			networkport.setModel(new SpinnerNumberModel(new Integer(25000), new Integer(1025), null, new Integer(1)));
			GridBagConstraints gbc_networkport = new GridBagConstraints();
			gbc_networkport.anchor = GridBagConstraints.NORTHWEST;
			gbc_networkport.insets = new Insets(0, 0, 5, 0);
			gbc_networkport.gridx = 3;
			gbc_networkport.gridy = 1;
			contentPanel.add(networkport, gbc_networkport);
		}
		{
			JLabel lblEnds = new JLabel("Ends");
			GridBagConstraints gbc_lblEnds = new GridBagConstraints();
			gbc_lblEnds.anchor = GridBagConstraints.EAST;
			gbc_lblEnds.insets = new Insets(0, 0, 5, 5);
			gbc_lblEnds.gridx = 0;
			gbc_lblEnds.gridy = 2;
			contentPanel.add(lblEnds, gbc_lblEnds);
		}
		{
			ends = new JSpinner();
			ends.setModel(new SpinnerNumberModel(10, 1, 20, 1));
			GridBagConstraints gbc_ends = new GridBagConstraints();
			gbc_ends.anchor = GridBagConstraints.WEST;
			gbc_ends.insets = new Insets(0, 0, 5, 5);
			gbc_ends.gridx = 1;
			gbc_ends.gridy = 2;
			contentPanel.add(ends, gbc_ends);
		}
		{
			JLabel lblCurl = new JLabel("Curl");
			GridBagConstraints gbc_lblCurl = new GridBagConstraints();
			gbc_lblCurl.anchor = GridBagConstraints.EAST;
			gbc_lblCurl.insets = new Insets(0, 0, 5, 5);
			gbc_lblCurl.gridx = 0;
			gbc_lblCurl.gridy = 3;
			contentPanel.add(lblCurl, gbc_lblCurl);
		}
		{
			curltype = new JComboBox();
			curltype.setModel(new DefaultComboBoxModel(new String[] {"Normal", "Straight", "Swingy", "Really Swingy"}));
			GridBagConstraints gbc_curltype = new GridBagConstraints();
			gbc_curltype.insets = new Insets(0, 0, 5, 5);
			gbc_curltype.fill = GridBagConstraints.HORIZONTAL;
			gbc_curltype.gridx = 1;
			gbc_curltype.gridy = 3;
			contentPanel.add(curltype, gbc_curltype);
		}
		{
			JLabel lblscoring = new JLabel("Scoring");
			GridBagConstraints gbc_lblscoring = new GridBagConstraints();
			gbc_lblscoring.insets = new Insets(0, 0, 5, 5);
			gbc_lblscoring.anchor = GridBagConstraints.EAST;
			gbc_lblscoring.gridx = 0;
			gbc_lblscoring.gridy = 4;
			contentPanel.add(lblscoring, gbc_lblscoring);
		}
		{
			scoringtype = new JComboBox();
			scoringtype.setModel(new DefaultComboBoxModel(new String[] {"Standard", "Skins"}));
			scoringtype.setMaximumRowCount(2);
			GridBagConstraints gbc_scoringtype = new GridBagConstraints();
			gbc_scoringtype.insets = new Insets(0, 0, 5, 5);
			gbc_scoringtype.fill = GridBagConstraints.HORIZONTAL;
			gbc_scoringtype.gridx = 1;
			gbc_scoringtype.gridy = 4;
			contentPanel.add(scoringtype, gbc_scoringtype);
		}
		{
			JLabel lblPlayers = new JLabel("Players");
			GridBagConstraints gbc_lblPlayers = new GridBagConstraints();
			gbc_lblPlayers.anchor = GridBagConstraints.EAST;
			gbc_lblPlayers.insets = new Insets(0, 0, 5, 5);
			gbc_lblPlayers.gridx = 0;
			gbc_lblPlayers.gridy = 5;
			contentPanel.add(lblPlayers, gbc_lblPlayers);
		}
		{
			playerstype = new JComboBox();
			playerstype.setModel(new DefaultComboBoxModel(new String[] {"Perfect", "Imperfect", "Perfect Skips"}));
			GridBagConstraints gbc_playerstype = new GridBagConstraints();
			gbc_playerstype.insets = new Insets(0, 0, 5, 5);
			gbc_playerstype.fill = GridBagConstraints.HORIZONTAL;
			gbc_playerstype.gridx = 1;
			gbc_playerstype.gridy = 5;
			contentPanel.add(playerstype, gbc_playerstype);
		}
		{
			JLabel lblMaxSpeedError = new JLabel("Max Speed Error");
			GridBagConstraints gbc_lblMaxSpeedError = new GridBagConstraints();
			gbc_lblMaxSpeedError.anchor = GridBagConstraints.EAST;
			gbc_lblMaxSpeedError.insets = new Insets(0, 0, 5, 5);
			gbc_lblMaxSpeedError.gridx = 2;
			gbc_lblMaxSpeedError.gridy = 5;
			contentPanel.add(lblMaxSpeedError, gbc_lblMaxSpeedError);
		}
		{
			maxspeederror = new JSpinner();
			maxspeederror.setModel(new SpinnerNumberModel(15, 0, 100, 1));
			GridBagConstraints gbc_maxspeederror = new GridBagConstraints();
			gbc_maxspeederror.anchor = GridBagConstraints.WEST;
			gbc_maxspeederror.insets = new Insets(0, 0, 5, 0);
			gbc_maxspeederror.gridx = 3;
			gbc_maxspeederror.gridy = 5;
			contentPanel.add(maxspeederror, gbc_maxspeederror);
		}
		{
			JLabel lblMaxDirectionError = new JLabel("Max Direction Error");
			GridBagConstraints gbc_lblMaxDirectionError = new GridBagConstraints();
			gbc_lblMaxDirectionError.anchor = GridBagConstraints.EAST;
			gbc_lblMaxDirectionError.insets = new Insets(0, 0, 5, 5);
			gbc_lblMaxDirectionError.gridx = 2;
			gbc_lblMaxDirectionError.gridy = 6;
			contentPanel.add(lblMaxDirectionError, gbc_lblMaxDirectionError);
		}
		{
			maxdirerror = new JSpinner();
			maxdirerror.setModel(new SpinnerNumberModel(30, 0, 100, 1));
			GridBagConstraints gbc_maxdirerror = new GridBagConstraints();
			gbc_maxdirerror.anchor = GridBagConstraints.WEST;
			gbc_maxdirerror.insets = new Insets(0, 0, 5, 0);
			gbc_maxdirerror.gridx = 3;
			gbc_maxdirerror.gridy = 6;
			contentPanel.add(maxdirerror, gbc_maxdirerror);
		}
		{
			JLabel lblTeam = new JLabel("Team 1");
			GridBagConstraints gbc_lblTeam = new GridBagConstraints();
			gbc_lblTeam.anchor = GridBagConstraints.EAST;
			gbc_lblTeam.insets = new Insets(0, 0, 5, 5);
			gbc_lblTeam.gridx = 0;
			gbc_lblTeam.gridy = 7;
			contentPanel.add(lblTeam, gbc_lblTeam);
		}
		{
			team1 = new JTextField();
			team1.setText("Martin");
			GridBagConstraints gbc_team1 = new GridBagConstraints();
			gbc_team1.insets = new Insets(0, 0, 5, 5);
			gbc_team1.fill = GridBagConstraints.HORIZONTAL;
			gbc_team1.gridx = 1;
			gbc_team1.gridy = 7;
			contentPanel.add(team1, gbc_team1);
			team1.setColumns(10);
		}
		{
			JLabel lblTeam_1 = new JLabel("Team 2");
			GridBagConstraints gbc_lblTeam_1 = new GridBagConstraints();
			gbc_lblTeam_1.anchor = GridBagConstraints.EAST;
			gbc_lblTeam_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblTeam_1.gridx = 0;
			gbc_lblTeam_1.gridy = 8;
			contentPanel.add(lblTeam_1, gbc_lblTeam_1);
		}
		{
			team2 = new JTextField();
			team2.setText("Howard");
			GridBagConstraints gbc_team2 = new GridBagConstraints();
			gbc_team2.insets = new Insets(0, 0, 5, 5);
			gbc_team2.fill = GridBagConstraints.HORIZONTAL;
			gbc_team2.gridx = 1;
			gbc_team2.gridy = 8;
			contentPanel.add(team2, gbc_team2);
			team2.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				GridBagLayout gbl_buttonPane = new GridBagLayout();
				gbl_buttonPane.columnWidths = new int[]{58, 0, 147, 160, 0};
				gbl_buttonPane.rowHeights = new int[]{23, 0};
				gbl_buttonPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
				buttonPane.setLayout(gbl_buttonPane);
				{
					JButton button = new JButton("Quit");
					button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
					button.setHorizontalAlignment(SwingConstants.LEFT);
					button.setActionCommand("Quit");
					GridBagConstraints gbc_button = new GridBagConstraints();
					gbc_button.anchor = GridBagConstraints.WEST;
					gbc_button.insets = new Insets(0, 0, 0, 5);
					gbc_button.gridx = 0;
					gbc_button.gridy = 0;
					buttonPane.add(button, gbc_button);
				}
				{
					JButton button = new JButton("About");
					GridBagConstraints gbc_button = new GridBagConstraints();
					gbc_button.anchor = GridBagConstraints.WEST;
					gbc_button.insets = new Insets(0, 0, 0, 5);
					gbc_button.gridx = 1;
					gbc_button.gridy = 0;
					buttonPane.add(button, gbc_button);
				}
				{
					JButton startButton = new JButton("Start");
					startButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							startGamePressed();
						}
					});
					startButton.setHorizontalAlignment(SwingConstants.RIGHT);
					startButton.setActionCommand("Start");
					GridBagConstraints gbc_startButton = new GridBagConstraints();
					gbc_startButton.anchor = GridBagConstraints.EAST;
					gbc_startButton.fill = GridBagConstraints.VERTICAL;
					gbc_startButton.gridx = 3;
					gbc_startButton.gridy = 0;
					buttonPane.add(startButton, gbc_startButton);
					getRootPane().setDefaultButton(startButton);
				}
			}
		}
	}
}
