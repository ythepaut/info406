package fr.groupe4.clientprojet.communication;

/**
 * Codes réponse HTML
 */
public enum HTMLCode {
    HTML_CUSTOM_DEFAULT_ERROR(-1),
    HTML_CUSTOM_TIMEOUT(608),
    HTML_OK(200),
    HTML_BAD_REQUEST(400),
    HTML_UNAUTHORIZED(401),
    HTML_FORBIDDEN(403),
    HTML_NOT_FOUND(404),
    HTML_TIMEOUT(408);

    private int code;

    HTMLCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static HTMLCode fromInt(int code) {
        HTMLCode[] codes = HTMLCode.values();

        HTMLCode htmlCodeResult = HTML_CUSTOM_DEFAULT_ERROR;

        for (HTMLCode htmlCode : codes) {
            if (htmlCode.code == code) {
                htmlCodeResult = htmlCode;
            }
        }

        return htmlCodeResult;
    }
}
