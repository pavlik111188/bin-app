package ua.binotel.www.binotelmobile;



public class GetMD5ForFile {

    /*private File file;

    public GetMD5ForFile(String filePath) {
        this.file = new File(filePath);
    }

    public GetMD5ForFile(File file) {
        this.file = file;
    }

    public String getMD5() {
        String md5 = null;

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(this.file);

            // md5Hex converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
            // The returned array will be double the length of the passed array, as it takes two characters to represent any given byte.

            md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));

            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return md5;
    }*/
}