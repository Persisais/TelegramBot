package com.persisais.telegrambot.Service;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageConverter {

    public String convertToFile (byte[] photo, Long id) throws IOException {
        // create the object of ByteArrayInputStream class
        // and initialized it with the byte array.
        ByteArrayInputStream inStreambj = new ByteArrayInputStream(photo);

        DataBuffer buffer = new DataBufferByte(photo, photo.length);
        int width = 900;
        int height = 625;

        //3 bytes per pixel: red, green, blue
        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[] {0, 1, 2}, (Point)null);
        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        BufferedImage image = new BufferedImage(cm, raster, true, null);


        // read image from byte array
        BufferedImage newImage = ImageIO.read(inStreambj);
        System.out.println(inStreambj);
        System.out.println(newImage);

        // write output image
        String pathname = id+".png";
        ImageIO.write(newImage, "png", new File(pathname));
        System.out.println("Image generated from the byte array.");
        return pathname;
    }
    // read the image from the file

}
