package com.trailsafe.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class QrProcessor {
    public static void main(String[] args) {
        try {
            File inputFile = new File("C:/Users/ahire/.gemini/antigravity/brain/c6685d16-bfb3-4f4b-a504-b83e0fa3f767/.user_uploaded/media_1792152977507.jpg");
            if (!inputFile.exists()) {
                System.err.println("Input image not found: " + inputFile.getAbsolutePath());
                return;
            }

            BufferedImage img = ImageIO.read(inputFile);
            int width = img.getWidth();
            int height = img.getHeight();
            System.out.println("Image dimensions: " + width + "x" + height);

            // Save original copy
            File originalOutput = new File("public/assets/payment_qr_original.jpg");
            ImageIO.write(img, "jpg", originalOutput);

            // Create edited copy with "Admin - TrailSafe" replacing the name at the bottom
            BufferedImage editedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = editedImg.createGraphics();
            g2d.drawImage(img, 0, 0, null);

            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // The text "Mayur Manohar Wagh" is in the bottom ~10% of the image.
            // Let's cover from height - (int)(height * 0.08) to height with pure black (#000000)
            int rectY = (int) (height * 0.92);
            int rectH = height - rectY;
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, rectY, width, rectH);

            // Draw "Admin - TrailSafe"
            String text = "Admin - TrailSafe";
            int fontSize = Math.max(20, (int) (width * 0.052));
            Font font = new Font("SansSerif", Font.BOLD, fontSize);
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textX = (width - textWidth) / 2;
            int textY = rectY + ((rectH - fm.getHeight()) / 2) + fm.getAscent();

            g2d.drawString(text, textX, textY);
            g2d.dispose();

            File editedOutput = new File("public/assets/payment_qr.jpg");
            ImageIO.write(editedImg, "jpg", editedOutput);
            System.out.println("Successfully generated public/assets/payment_qr.jpg with 'Admin - TrailSafe'!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
