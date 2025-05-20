package example;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SupportedMimeTypes {
    public enum MimeTypes {
        IMAGE, AUDIO, VIDEO, TEXT, APPLICATION;

        public String toString() {
            return name().toLowerCase();
        }
    }

    static Map<MimeTypes, List<String>> mimeTypes = new LinkedHashMap<>();

    static {
        mimeTypes.put(MimeTypes.IMAGE, Arrays.asList(
                "image/jpeg",
                "image/gif",
                "image/png",
                "image/apng",
                "image/avif",
                "image/webp",
                "image/svg+xml"
        ));

        mimeTypes.put(MimeTypes.AUDIO, Arrays.asList(
                "audio/mp4",
                "audio/webm",
                "audio/acc",
                "audio/mpeg",
                "audio/wav",
                "audio/ogg"
        ));

        mimeTypes.put(MimeTypes.VIDEO, Arrays.asList(
                "video/mp4",
                "video/webm",
                "video/ogg"
        ));

        mimeTypes.put(MimeTypes.TEXT, Arrays.asList(
                "text/html",
                "text/plain"
        ));

        mimeTypes.put(MimeTypes.APPLICATION, Arrays.asList(
                "application/json"
        ));
    }

    public static String getSubtypesAsCommaSeparatedString(MimeTypes type) {
        List<String> subtypes = mimeTypes.get(type);
        if (subtypes == null) {
            return "";
        } else {
            return String.join(",", subtypes);
        }
    }

    public static boolean isSupportedMimeType(MimeTypes requiredType, String mimeTypeSubtype) {
        if (mimeTypeSubtype == null) {
            return false;
        }

        String[] parts = mimeTypeSubtype.split("/");

        if (parts.length != 2) {
            return false;
        }

        if (mimeTypes.containsKey(requiredType) && mimeTypes.get(requiredType).contains(mimeTypeSubtype)) {
            return true;
        } else {
            return false;
        }
    }
}