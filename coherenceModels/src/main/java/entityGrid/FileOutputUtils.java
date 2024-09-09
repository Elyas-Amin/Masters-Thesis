package entityGrid;

import edu.stanford.nlp.ling.HasWord;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class FileOutputUtils {

    /**
     * Prints the grid to the file of filename
     * Replaces empty cells with '-'
     *
     * @param filename
     * @param grid     two dimensional array of chars
     */
    public static void writeGridToFile(String filename, char[][] grid) {
        int idx = filename.lastIndexOf(File.pathSeparator);

        writeGridToFile(filename.substring(idx + 1), filename.substring(0, idx), grid, false, null, false);
    }

    public static void writeDocToFile(String directory, String filename, List<List<HasWord>> sentences, boolean append, String docid, boolean compress) {
        OutputStream output = null;
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            File newFile = new File(directory);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            String outputFile = directory + File.separator + filename;
            if (compress) {
                GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(outputFile + ".gz"), append));
                //output = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
                output = new BufferedOutputStream(zip);
            } else {
                output = new BufferedOutputStream(new FileOutputStream(new File(outputFile), append));
            }
            if (docid != null) {
                char[] id = docid.toCharArray();
                for (int i = 0; i < id.length; i++) {
                    output.write(id[i]);
                }
                output.write('\n');
            }
            //for (String sentence: sentences){
            for (List<HasWord> sentence : sentences) {
                for (HasWord word : sentence) {
                    output.write(word.word().getBytes());
                    output.write(' ');
                }
                output.write('\n');
                //output.write(sentence.getBytes());
            }
            output.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                output.close();
            } catch (Exception e) { /* log it ?*/ }
        }
    }

    /**
     * Prints the grid to the file of filename
     * Replaces empty cells with '-'
     *
     * @param filename
     * @param grid     two dimensional array of chars
     */
    public static void writeGridToFile(String directory, String filename, char[][] grid, boolean append, String docid, boolean compress) {
        OutputStream output = null;
        try {
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            File newFile = new File(directory);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }
            String outputFile = directory + File.separator + filename;
            if (compress) {
                GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(new File(outputFile + ".gz"), append));
                //output = new BufferedWriter(new OutputStreamWriter(zip, "UTF-8"));
                output = new BufferedOutputStream(zip);
            } else {
                output = new BufferedOutputStream(new FileOutputStream(new File(outputFile), append));
            }


            if (docid != null) {
                char[] id = docid.toCharArray();

                output.write('#');
                output.write(' ');
                output.write('i');
                output.write('d');
                output.write('=');
                for (int i = 0; i < id.length; i++) {

                    output.write(id[i]);
                }
                output.write('\n');
            }
            for (int i = 0; i < grid.length; i++) {//each sentence
                for (int j = 0; j < grid[i].length; j++) {//each char representing entity
                    if (grid[i][j] == 0) {
                        output.write('-');
                    } else {
                        output.write(grid[i][j]);
                    }
                }
                output.write('\n');
            }
            output.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                output.close();
            } catch (Exception e) { /* log it ?*/ }
        }
    }

    public static void writeGridsToFile(String path, CharBuffer buffer) {
        FileChannel channel = null;
        try {
            //CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            channel = new FileOutputStream(new File(path)).getChannel();
            ByteBuffer bb = ByteBuffer.allocate(1024 * 15);
            bb.asCharBuffer().put(buffer);
            channel.write(bb);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                channel.close();
            } catch (Exception e1) { /* log it ?*/ }
            System.exit(1);

        } finally {
            try {
                channel.close();
            } catch (Exception e) { /* log it ?*/ }
        }

    }

    public static void streamGridToBuffer(CharBuffer buffer, char[][] grid, String string) {

        buffer.append(string);
        buffer.append('\n');
        for (int i = 0; i < grid.length; i++) {//each sentence
            for (int j = 0; j < grid[i].length; j++) {//each char representing entity
                if (grid[i][j] == 0) {
                    buffer.append('-');
                } else {
                    buffer.append(grid[i][j]);
                }
            }
            buffer.append('\n');
        }
    }

    public static void writeDebugToFile(String filename, String debugstatement) {
        //OutputStream output = null;
        BufferedWriter writer = null;
        try {
            //StringBuilder contents = new StringBuilder();
            //CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
            //output = new BufferedOutputStream(new FileOutputStream(new File(filename)));
            writer.write(debugstatement);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

        } finally {
            try {
                writer.close();
            } catch (Exception e) { /* log it ?*/ }
        }


    }

    public static void writeLexicalisedGridToFile(String filename, char[][] grid) {


    }

    public static void writeGridsToFile(String path, char[][][] grids) {

        for (int i = 0; i < grids.length; i++) {
            writeGridToFile(path + "file" + i, grids[i]);
        }
    }


    /**
     * outputs given transition probablilities to file, in tablular format
     *
     * @param filename
     * @param transitionProbabilities
     */
    public static void writeEntityTransitionsToTable(String filename, Map<String, Double> transitionProbabilities) {
        //OutputStream output = null;
        BufferedWriter writer = null;
        try {
            //CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            //output = new BufferedOutputStream(new FileOutputStream(new File(filename)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));

            //for each transition, output the probability for this document
            for (String key : transitionProbabilities.keySet()) {
                writer.write(key + " : ");
                writer.write(transitionProbabilities.get(key).toString());
                writer.write(key + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

        } finally {
            try {
                writer.close();
            } catch (Exception e) { /* log it ?*/ }
        }

    }

    public static void writeGraphCoherenceToFile(String filename, int fileidx, BipartiteGraph bipartitegraph, int projection) {
        OutputStream output = null;
        try {

            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            output = new BufferedOutputStream(new FileOutputStream(new File(filename)));

            output.write(fileidx);
            output.write(' ');
            output.write((byte) bipartitegraph.getLocalCoherence(BipartiteGraph.UNWEIGHTED_PROJECTION));
            output.write('\n');

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

        } finally {
            try {
                output.close();
            } catch (Exception e) { /* log it ?*/ }
        }

    }

    public static void streamToFile(String outputfile, StringBuffer buffer) {
        OutputStream output = null;
        try {

            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            output = new BufferedOutputStream(new FileOutputStream(new File(outputfile)));
            System.out.print("streaming- " + buffer.toString());
            output.write(buffer.toString().getBytes());


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);

        } finally {
            try {
                output.close();
            } catch (Exception e) { /* log it ?*/ }
        }
    }

    public static String getDirectory(String outputdirectory, String folder1) {

        String path = outputdirectory +
                File.separator +
                folder1 +
                File.separator;
        return path;
    }

    public static String getDirectory(String outputdirectory, String folder1, String folder2) {

        String path = outputdirectory +
                File.separator +
                folder1 +
                File.separator +
                folder2 +
                File.separator;

        return path;
    }

    public static String getFilenameWithoutExtensions(String filename) {

        if (filename != null && filename.contains(".")) {
            int idx = filename.lastIndexOf('.');
            return filename.substring(0, idx);
        }
        return filename;
    }

    public static boolean isCompressed(String filename) {
        return filename.endsWith("gz");
    }

}
