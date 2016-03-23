package sample;

import java.io.File;

public class TestFile {

    private File file;
    private String filename;

    public TestFile(File file, String filename){
        this.file=file;
        this.filename=filename;
    }

    public String getFilename() { return this.filename; }
    public void setFilename(String value) { this.filename = value; }

}
