package example;

import java.util.*;

public class SupportedMimeTypes {
    public enum MimeTypes {
        AUDIO, IMAGE, VIDEO, TEXT, APPLICATION;

        public String toString() {
            return name().toLowerCase();
        }
    }

    private static Map<MimeTypes, List<String>> mimeTypes = new LinkedHashMap<>();

    private static Map<MimeTypes, List<String>> extensions = new HashMap<>();

    public static List<String> getExtensions(MimeTypes mimeType) {
        return extensions.get(mimeType);
    }

    static {
        mimeTypes.put(MimeTypes.AUDIO, Arrays.asList(
                "audio/mp4",
                "audio/webm",
                "audio/acc",
                "audio/mpeg",
                "audio/wav",
                "audio/ogg",
                "audio/x-flac"
        ));

        extensions.put(MimeTypes.AUDIO, Arrays.asList(
                "m4a",
                "weba",
                "acc",
                "mp3",
                "wav",
                "ogg",
                "oga",
                "flac",
                "opus"
        ));

        mimeTypes.put(MimeTypes.IMAGE, Arrays.asList(
                "image/jpeg",
                "image/gif",
                "image/png",
                "image/apng",
                "image/avif",
                "image/webp",
                "image/svg+xml"
        ));

        extensions.put(MimeTypes.IMAGE, Arrays.asList(
                "jpg",
                "jpeg",
                "jfif",
                "pjp",
                "pjpeg",
                "gif",
                "png",
                "apng",
                "avif",
                "webp",
                "svg",
                "svgz"
        ));

        mimeTypes.put(MimeTypes.VIDEO, Arrays.asList(
                "video/mp4",
                "video/quicktime", // Modern times means these are basically mp4
                "video/webm",
                "video/ogg"
        ));

        extensions.put(MimeTypes.VIDEO, Arrays.asList(
                "mp4",
                "m4v",
                "mov",
                "webm",
                "ogm",
                "ogv"
        ));

        mimeTypes.put(MimeTypes.TEXT, Arrays.asList(
                "text/html",
                "text/plain"
        ));

        extensions.put(MimeTypes.TEXT, Arrays.asList(
                "htm",
                "html",
                "txt"
        ));

        mimeTypes.put(MimeTypes.APPLICATION, Arrays.asList(
                "application/json"
        ));

        extensions.put(MimeTypes.APPLICATION, Arrays.asList(
                "json"
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

    public static String getExtensionsAsCommaSeparatedString(MimeTypes type) {
        List<String> extensions = getExtensions(type);

        if (extensions == null) {
            return "";
        } else {
            List<String> dottedExtensions = new ArrayList<>();
            for (String extension : extensions) {
                dottedExtensions.add('.' + extension);
            }

            return String.join(",", dottedExtensions);
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

    public static boolean isSupportedExtension(MimeTypes requiredType, String fileName) {
        String[] parts = fileName.split("\\.");

        if (parts.length < 2) {
            return false;
        }

        if (getExtensions(requiredType).contains(parts[parts.length - 1].toLowerCase())) {
            return true;
        } else {
            return false;
        }
    }
}