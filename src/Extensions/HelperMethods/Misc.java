package Extensions.HelperMethods;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

public class Misc {



    static char[] alphabet = new char[]{
            'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    /**
     * For variable notation.
     * @param index Index of the variable alphabet; 0-25 = lowercase a-z | 26-51 = uppercase A-Z. Numbers up to 51 are valid.
     * @return Letter corresponding to index (e.g. 0 = 'a', 3 = 'd', etc.)
     */
    public static char GetAlphabetCode(int index){

        return alphabet[index];
    }

    public static int GetNumberCode(char alphabetCharacter){
        for(int i = 0; i <alphabet.length; i++){
            if(alphabet[i] == alphabetCharacter)
                return i;
        }
        // if this happens, something's (possibly) gone horribly wrong
        return -1;
    }

    public static ImageIcon GetImageIconByName(String imageName){
        String workingDirectory = System.getProperty("user.dir");
        String imageDirectory = workingDirectory + "/src/Images/";

        ImageIcon i = new ImageIcon(imageDirectory + imageName);

        return i;
    }

    public static BufferedImage GetBufferedImageByName(String imageName){
        String workingDirectory = System.getProperty("user.dir");
        String imageDirectory = workingDirectory + "/src/Images/";

        try{
            File img = new File(imageDirectory + imageName);
            BufferedImage i = ImageIO.read(img);
            return i;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static BufferedImage GetMergedImage(BufferedImage img1, BufferedImage img2){

        BufferedImage combinedImage = new BufferedImage(img1.getWidth(null),img1.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        g.drawImage(img1,0,0,null);
        g.drawImage(img2,0,0,null);
        g.dispose();

        return combinedImage;
    }

    public static BufferedImage GetRotatedImage(BufferedImage img, double rotation){

        int width = img.getWidth();
        int height = img.getHeight();


        BufferedImage dest = new BufferedImage(height, width, img.getType());

        Graphics2D graphics2D = dest.createGraphics();
        graphics2D.translate((height - width) / 2, (height - width) / 2);

        // * .5 = right, * 1 = bot, 1.5 = left
        graphics2D.rotate(Math.PI * (rotation / 2), height / 2, width / 2);
        graphics2D.drawRenderedImage(img, null);

        return dest;
    }

    public static void RotatedImage (BufferedImage img){
        AffineTransform at = new AffineTransform();
        double rads = Math.toRadians(90);
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage rotatedImg = new BufferedImage(w, h, img.getType());
        at.translate(w / 2, h / 2);
        at.rotate(rads,0, 0);
        at.translate(-img.getWidth() / 2, -img.getHeight() / 2);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(img,rotatedImg);
    }

}
