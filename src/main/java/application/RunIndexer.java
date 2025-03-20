package application;

import filesystem.FileIndexer;

import java.nio.file.Paths;

public class RunIndexer {
    public static void main(String[] args) {
        FileIndexer indexer = new FileIndexer();
        //indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2\\LOGIC PROGRAMMING\\Laboratory"));
        indexer.indexFolder(Paths.get("C:\\Users\\Mara\\Documents\\UTCN AN 3 SEM 2\\SOFTWARE DESIGN\\Laboratory\\examples"));
    }
}
