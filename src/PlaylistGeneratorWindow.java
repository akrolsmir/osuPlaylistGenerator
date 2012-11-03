import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.ScrollPaneConstants;
import javax.swing.JLabel;


public class PlaylistGeneratorWindow extends JFrame
{
    private PlaylistGeneratorWindow frame;

    private JPanel contentPane;

    private JTextField txtOsuSongsFolder;

    private JProgressBar progressBar;

    private JScrollPane scrollPane;

    private JTextArea txtrProgress;

    private JButton btnBrowse;

    private JCheckBox chckbxFixTags;

    private JCheckBox chckbxMakePlaylist;

    private JPanel panel;

    private JButton btnGo;

    private List<JComponent> enableable;

    private final JLabel lblProgress = new JLabel( "   Progress:" );

    private ThreadedSongTransfer Song;


    /**
     * Launch the application.
     */
    public static void main( String[] args )
    {
        try
        {
            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
        EventQueue.invokeLater( new Runnable()
        {
            public void run()
            {
                try
                {
                    PlaylistGeneratorWindow frame = new PlaylistGeneratorWindow();
                    frame.setVisible( true );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        } );
    }


    /**
     * Create the frame.
     */
    public PlaylistGeneratorWindow()
    {
        setTitle( "Playlist Generator v1.0" );
        setResizable( false );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setBounds( 100, 100, 400, 300 );
        contentPane = new JPanel();
        contentPane.setBackground( Color.LIGHT_GRAY );
        contentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        setContentPane( contentPane );
        contentPane.setLayout( null );

        panel = new JPanel();
        panel.setBounds( 10, 11, 374, 68 );
        contentPane.add( panel );
        panel.setLayout( null );

        txtOsuSongsFolder = new JTextField();
        txtOsuSongsFolder.setText( "osu! Songs folder" );
        txtOsuSongsFolder.setBounds( 10, 11, 267, 20 );
        panel.add( txtOsuSongsFolder );
        txtOsuSongsFolder.setColumns( 10 );

        btnGo = new JButton( "Go!" );
        btnGo.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                if ( btnGo.getText().equals( "Go!" ) )
                    go();
                // else //if btnGo's text is Stop
                else
                    stop();

            }
        } );

        btnBrowse = new JButton( "Browse" );
        btnBrowse.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                txtOsuSongsFolder.setText( Util.chooseDirectory( txtOsuSongsFolder.getText(),
                    "Browse",
                    "Select" ) );
            }
        } );
        btnBrowse.setBounds( 287, 10, 77, 23 );
        panel.add( btnBrowse );
        btnGo.setEnabled( false );
        btnGo.setBounds( 287, 38, 77, 23 );
        panel.add( btnGo );

        chckbxMakePlaylist = new JCheckBox( "Make Playlist" );
        chckbxMakePlaylist.addActionListener( new goEnabler() );
        chckbxMakePlaylist.setBounds( 10, 38, 89, 23 );
        panel.add( chckbxMakePlaylist );

        chckbxFixTags = new JCheckBox( "Fix Tags" );
        chckbxFixTags.addActionListener( new goEnabler() );
        chckbxFixTags.setBounds( 101, 38, 65, 23 );
        panel.add( chckbxFixTags );

        progressBar = new JProgressBar();
        progressBar.setStringPainted( true );
        progressBar.setBounds( 10, 247, 374, 14 );
        contentPane.add( progressBar );

        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
        scrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        scrollPane.setBounds( 10, 90, 374, 158 );
        contentPane.add( scrollPane );

        txtrProgress = new JTextArea();
        txtrProgress.setEditable( false );
        scrollPane.setViewportView( txtrProgress );

        scrollPane.setColumnHeaderView( lblProgress );
        
        enableable = new ArrayList<JComponent>();
        enableable.add( txtOsuSongsFolder );
        enableable.add( btnBrowse );
        enableable.add( chckbxMakePlaylist );
        enableable.add( chckbxFixTags );
    }


    public void println( String toPrint )
    {
        txtrProgress.append( toPrint + "\n" );
    }


    public void initializeProgressBar( int total )
    {
        progressBar.setMinimum( 0 );
        progressBar.setMaximum( total );
        progressBar.setValue( 0 );
    }


    public void setProgressBar( int completed )
    {
        progressBar.setValue( completed );
    }


    private void go()
    {
        long start = System.currentTimeMillis();
        try
        {
            disableGUI();
            Song = new ThreadedSongTransfer( this,
                txtOsuSongsFolder.getText(),
                chckbxMakePlaylist.isSelected(),
                chckbxFixTags.isSelected() );
            Song.execute();
        }
        catch ( Exception e )
        {
            Util.errorException( e );
        }
        System.out.println( System.currentTimeMillis() - start );
    }


    private void stop()
    {
        Song.cancel( true );
        enableGUI();
    }


    public void disableGUI()
    {
        invalidate();
        for ( JComponent c : enableable )
        {
            c.setEnabled( false );
        }
        btnGo.setText( "Stop" );
        validate();
    }


    public void enableGUI()
    {
        invalidate();
        for ( JComponent c : enableable )
        {
            c.setEnabled( true );
        }
        btnGo.setText( "Go!" );
        validate();
    }


    public class goEnabler implements ActionListener
    {
        @Override
        public void actionPerformed( ActionEvent e )
        {
            if ( chckbxFixTags.isSelected() || chckbxMakePlaylist.isSelected() )
                btnGo.setEnabled( true );
            else
                btnGo.setEnabled( false );
        }
    }
}
