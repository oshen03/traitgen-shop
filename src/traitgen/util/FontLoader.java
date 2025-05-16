package traitgen.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public class FontLoader {
    public static Font loadMaterialIconsFont() {
        try {
            InputStream is = FontLoader.class.getResourceAsStream("/dashboard/swing/icon/MaterialIcons-Regular.ttf");
            if (is != null) {
                Font materialIconsFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(materialIconsFont);
                System.out.println("Material Icons font loaded successfully.");
                return materialIconsFont;
            } else {
                System.err.println("Material Icons font not found.");
            }
        } catch (Exception e) {
            System.err.println("Error loading Material Icons font.");
            e.printStackTrace();
        }
        return null;
    }
}
