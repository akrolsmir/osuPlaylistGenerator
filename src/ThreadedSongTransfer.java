import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingWorker;

import entagged.audioformats.AudioFile;
import entagged.audioformats.AudioFileIO;


// import org.jaudiotagger.audio.mp3;

public class ThreadedSongTransfer extends SwingWorker<Void, String>
{
    // Lengths of "AudioFilename: ", "Title:", "Artist:"
    private final int FILENAME_POS = 15, TITLE_POS = 6, ARTIST_POS = 7;

    private int total = 0, processed = 0;

    private String songsFolderLoc;

    private PrintWriter out;

    // for songs already processed once
    private Set<String> toSkip;

    private boolean makePlaylist, fixTags;

    private PlaylistGeneratorWindow pgw;
    
    private String errors;


    // private JTextArea TextArea;

    // private String songsFolderLoc;

    // TODO write in skipping entire folders
    // TODO fix printout to file, since console cuts things off.

    // Testing only
    public static void main( String[] args ) throws Exception
    {

        // new SongTransfer(
        // "C:\\Users\\Akrolsmir\\Desktop\\Gaming Programs\\osu!\\Songs",
        // false,
        // false );

        // mod( new File(
        // "C:\\Users\\Akrolsmir\\Desktop\\24496 Nightwish - Amaranth\\Amaranth.mp3"
        // ), "hi", "title" );
    }


    public ThreadedSongTransfer(
        PlaylistGeneratorWindow p,
        String songsFolderLoc,
        boolean makePlaylist,
        boolean fixTags ) throws Exception
    {
        pgw = p;
        this.songsFolderLoc = songsFolderLoc;
        this.makePlaylist = makePlaylist;
        this.fixTags = fixTags;

    }


    @Override
    protected Void doInBackground() throws Exception
    {
        toSkip = new HashSet<String>();

        publish( "Processing..." );
        if ( makePlaylist )
        {
            out = new PrintWriter( new BufferedWriter( new FileWriter( "playlist.m3u" ) ) );
            out.println( "#EXTM3U" );
        }

        File dir = new File( songsFolderLoc );
        if ( !dir.isDirectory() )
        {
            publish( "Invalid location, try again" );
            return null;
        }
        else
        {
            pgw.initializeProgressBar( dir.listFiles().length );
            try
            {
                processAllFiles( dir );
            }
            catch ( InterruptedException e )
            {
                publish( "Stopped." );
            }
        }

        if ( makePlaylist )
            out.close();
        publish( "Finished." );
        // System.out.println( processed ); TODO remove
        return null;
    }


    public void processAllFiles( File dir ) throws InterruptedException
    {
        // System.out.println( dir.listFiles().length );
        for ( File f : dir.listFiles() )
        {
            if ( f.isFile() && Util.getExtension( f ).equals( "osu" ) )
                processOsuFile( f, dir );
            else if ( f.isDirectory() )
                processAllFiles( f );
            if ( Thread.interrupted() )
            {
                System.out.println( "Oh Dear!" );
                throw new InterruptedException();
            }
        }
    }


    public void tag( String songLoc, String title, String artist )
        throws InterruptedException
    {
        try
        {
            File songFile = new File( songLoc );
            AudioFile af = AudioFileIO.read( songFile );
            af.getTag().setArtist( artist );
            af.getTag().setTitle( title );
            af.commit();
            publish( "Tagged: " + Util.cutPath( songLoc ) );
        }
        catch ( StringIndexOutOfBoundsException s )
        {
            System.out.println( "wtf" );
        }
        catch ( Exception e )
        {
            // if ( e instanceof CannotReadException )
            // publish( "Could not read: " + Util.cutPath( songLoc ) );
            // else
            if ( Thread.interrupted() )
                throw new InterruptedException();
            else
            {
                e.printStackTrace();
                Util.errorException( e, "Couldn't Tag" );
            }
        }

    }


    public void playlist( String songLoc, String artist, String title )
    {
        out.println( "#EXTINF:" + "," + artist + " - " + title );
        out.println( songLoc );
        publish( "Added: " + Util.cutPath( songLoc ) );
    }


    public void processOsuFile( File file, File dir )
        throws InterruptedException
    {
        try
        {
            BufferedReader f = new BufferedReader( new FileReader( file ) );
            // Finds the line of "Audio Filename: ";
            String song = "";
            while ( !song.contains( "name" ) )
                song = f.readLine();
            song = song.substring( FILENAME_POS );

            if ( toSkip.contains( song ) )
                return;
            toSkip.add( song );

            // Extracts title and artist names
            String findMeta = "";
            while ( !findMeta.equals( "[Metadata]" ) )
                findMeta = f.readLine();
            String title = f.readLine().substring( TITLE_POS );
            String artist = f.readLine().substring( ARTIST_POS );
            String songLoc = dir.toString() + "\\" + song;

            if ( makePlaylist )
                playlist( songLoc, artist, title );
            if ( fixTags )
                tag( songLoc, title, artist );
            processed++;
            pgw.setProgressBar( processed );
        }
        // catch(InterruptedException i)
        // {
        // throw new InterruptedException();
        // }
        catch ( Exception e )
        {
            if ( Thread.interrupted() )
                throw new InterruptedException();
            else
            {
                System.out.println( file.getAbsolutePath() );
                Util.errorException( e, "?" );
            }
        }
    }


    @Override
    protected void process( List<String> toPrint )
    {
        // textArea.append(toPrint + "\n");
        for ( String line : toPrint )
            pgw.println( line );
    }


    @Override
    protected void done()
    {
        pgw.enableGUI();
    }

    // public void modify( File file, String title, String Artist )
    // throws Exception
    // {
    // MP3File mp3 = new MP3File( file );
    // ID3v24Tag tag = new ID3v24Tag();
    // // new OggTag();
    // // tag.createField( id3Key, value )
    // // mp3.setID3v2Tag( )
    //
    // }
}
