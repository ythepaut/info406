package fr.groupe4.clientprojet.communication;

import fr.groupe4.clientprojet.communication.enums.CommunicationType;

import java.util.HashMap;

import static fr.groupe4.clientprojet.communication.enums.CommunicationType.*;

/**
 * Builder de la communication
 */
public final class CommunicationBuilder {
    /**
     * Type de communication
     */
    protected CommunicationType typeOfCommunication;

    /**
     * URL à envoyer à l'API
     */
    protected String url = null;

    /**
     * Se lance tout de suite après le constructeur ou nécessite un comm.start()
     */
    protected boolean startNow;

    /**
     * Attend que la requête soit terminée et bloque le thread
     * Cette variable ne sert que si startNow est à true
     */
    protected boolean sleepUntilFinished;

    /**
     * Data à envoyer en POST pour la requête
     */
    protected HashMap<String, String> requestData;

    /**
     * Constructeur
     */
    public CommunicationBuilder() {
        startNow = false;
        sleepUntilFinished = false;
        requestData = new HashMap<>();
    }

    /**
     * Lance la communication tout de suite
     *
     * @return Reste du builder
     */
    public CommunicationBuilder startNow() {
        startNow = true;
        return this;
    }

    /**
     * Attend que la communication soit terminée
     *
     * @return Reste du builder
     */
    public CommunicationBuilder sleepUntilFinished() {
        sleepUntilFinished = true;
        return this;
    }

    /**
     * Builder final
     *
     * @return Communication bien formée
     */
    public Communication build() {
        return new Communication(this);
    }

    /**
     * Connecte le client au serveur
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder connect(String username, String password) {
        typeOfCommunication = LOGIN;
        url = "auth/connect";
        requestData.put("username", username);
        requestData.put("passwd", password);
        return this;
    }

    /**
     * Vérifie la connexion
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder checkConnection() {
        typeOfCommunication = CHECK_CONNECTION;
        url = "auth/verify";
        requestData.put("token", Communication.getRequestToken(this));
        return this;
    }

    /**
     * Vérifie la connexion
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder updateConnection() {
        typeOfCommunication = UPDATE_CONNECTION;
        url = "auth/renew";
        requestData.put("token", Communication.getRenewToken(this));
        return this;
    }

    /**
     * Récupère la liste des projets
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder getProjectList() {
        typeOfCommunication = LIST_PROJECTS;
        url = "project/list";
        requestData.put("token", Communication.getRequestToken(this));
        return this;
    }
}
