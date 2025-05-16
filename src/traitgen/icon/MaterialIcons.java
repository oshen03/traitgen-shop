package traitgen.icon;

import dashboard.swing.icon.IconFont;
import java.io.InputStream;

public enum MaterialIcons implements IconFont {
    HOME('\ue88a'); // Add other icons as needed

    private final char character;

    MaterialIcons(char character) {
        this.character = character;
    }

    @Override
    public String getFontFamily() {
        return "Material Icons";
    }

    public char getCharacter() {
        return character;
    }

    public String getKey() {
        return name();
    }

    @Override
    public InputStream getFontInputStream() {
        // Ensure the path matches where the font is located in your project
        return MaterialIcons.class.getResourceAsStream("/fonts/MaterialIcons-Regular.ttf");
    }
}
