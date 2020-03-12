package fr.groupe4.clientprojet.communication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.HashMap;

import fr.groupe4.clientprojet.model.message.Message;
import org.jetbrains.annotations.NotNull;

import fr.groupe4.clientprojet.logger.Logger;
import fr.groupe4.clientprojet.model.resource.human.User;
import fr.groupe4.clientprojet.model.room.Room;
import fr.groupe4.clientprojet.model.task.Task;
import fr.groupe4.clientprojet.communication.enums.CommunicationType;
import fr.groupe4.clientprojet.model.message.enums.MessageResource;
import fr.groupe4.clientprojet.model.project.enums.ProjectStatus;

/**
 * Builder de la communication
 */
@SuppressWarnings("unused")
public final class CommunicationBuilder {
    /**
     * Type de communication
     */
    @NotNull
    protected CommunicationType typeOfCommunication;

    /**
     * URL à envoyer à l'API
     */
    @NotNull
    protected String url;

    /**
     * Se lance tout de suite après le constructeur ou nécessite un comm.start()
     */
    protected boolean startNow;

    /**
     * Laisse tourner en daemon
     */
    protected boolean keepAlive;

    /**
     * Attend que la requête soit terminée et bloque le thread
     * Cette variable ne sert que si startNow est à true
     */
    protected boolean sleepUntilFinished;

    /**
     * Data à envoyer en POST pour la requête
     */
    @NotNull
    protected HashMap<String, Object> requestData;

    /**
     * Constructeur
     */
    public CommunicationBuilder() {
        startNow = false;
        keepAlive = false;
        sleepUntilFinished = false;
        requestData = new HashMap<>();
        url = "";
        typeOfCommunication = CommunicationType.DEFAULT;
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
     * Reste actif ou non
     *
     * @return Reste du builder
     */
    protected CommunicationBuilder keepAlive() {
        keepAlive = true;
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
        typeOfCommunication = CommunicationType.LOGIN;
        url = "auth/connect";
        requestData.put("username", username);
        requestData.put("passwd", password);
        return this;
    }

    public CommunicationBuilder createProject(String name, String description, Temporal deadline, ProjectStatus status) {
        long deadlineSecond = 0;

        if (deadline instanceof LocalDate) deadlineSecond = ((LocalDate) deadline).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        else if (deadline instanceof LocalDateTime) deadlineSecond = ((LocalDateTime) deadline).atZone(ZoneId.systemDefault()).toEpochSecond();
        else Logger.error("From : type incorrect");

        typeOfCommunication = CommunicationType.CREATE_PROJECT;
        url = "project/create";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("name", name);
        requestData.put("description", description);
        requestData.put("deadline", deadlineSecond);
        requestData.put("status", status.toString());
        return this;
    }

    /**
     * Récupère la liste des créneaux entre deux dates
     * Si un créneau commence avant t1 mais se termine entre t1 et t2 il sera pris en compte
     * Fonctionne avec des dates pures (LocalDate) et des dates + temps (LocalDateTime)
     *
     * Exemple d'utilisation :
     *      LocalDateTime from = LocalDateTime.of(2020, 1, 1, 15, 30); // Date et heure, 1er janvier 2020 à 15h30
     *      LocalDate to = LocalDate.of(2020, 12, 31); // Date seulement, 31 décembre 2020
     *
     *      Communication c = Communication.builder()
     *          .getUserTimeSlotList(from, to)
     *          .sleepUntilFinished()
     *          .startNow()
     *          .build();
     *
     * @param from Date de début
     * @param to Date de fin
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder getUserTimeSlotList(Temporal from, Temporal to) {
        return getTimeSlotList(from, to, "hresource", User.getUser().getResourceId());
    }

    private CommunicationBuilder getTimeSlotList(Temporal from, Temporal to, String what, long id) {
        long t1 = 0, t2 = 0;

        if (from instanceof LocalDate) t1 = ((LocalDate) from).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        else if (from instanceof LocalDateTime) t1 = ((LocalDateTime) from).atZone(ZoneId.systemDefault()).toEpochSecond();
        else Logger.error("From : type incorrect");

        if (to instanceof LocalDate) t2 = ((LocalDate) to).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        else if (to instanceof LocalDateTime) t2 = ((LocalDateTime) to).atZone(ZoneId.systemDefault()).toEpochSecond();
        else Logger.error("To : type incorrect");

        typeOfCommunication = CommunicationType.GET_TIME_SLOT_LIST;
        url = "timeslot/list";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("from", t1);
        requestData.put("to", t2);
        requestData.put(what, id);
        return this;
    }

    /**
     * Ajoute un créneau
     * @see #getUserTimeSlotList getUserTimeSlotList pour exemple détaillé d'utilisation de from et de to
     *
     * @param from Date de début
     * @param to Date de fin
     * @param task Tâche
     * @param room Salle
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder addTimeSlot(Temporal from, Temporal to, Task task, Room room) {
        long t1 = 0, t2 = 0;

        if (from instanceof LocalDate) t1 = ((LocalDate) from).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        else if (from instanceof LocalDateTime) t1 = ((LocalDateTime) from).atZone(ZoneId.systemDefault()).toEpochSecond();
        else Logger.error("From : type incorrect");

        if (to instanceof LocalDate) t2 = ((LocalDate) to).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        else if (to instanceof LocalDateTime) t2 = ((LocalDateTime) to).atZone(ZoneId.systemDefault()).toEpochSecond();
        else Logger.error("To : type incorrect");

        typeOfCommunication = CommunicationType.ADD_TIME_SLOT;
        url = "timeslot/create";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("start", t1);
        requestData.put("end", t2);
        requestData.put("task", task.getId());
        requestData.put("room", room.getId());
        return this;
    }

    public CommunicationBuilder getUserMessageList(int page) {
        typeOfCommunication = CommunicationType.LIST_USER_MESSAGES;
        url = "message/list";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("origin", MessageResource.ORIGIN_HUMANRESOURCE.toString());
        requestData.put("id", User.getUser().getResourceId());
        requestData.put("page", page);
        return this;
    }

    public CommunicationBuilder sendMessage(String content, MessageResource dst, long id) {
        typeOfCommunication = CommunicationType.SEND_MESSAGE;
        url = "message/create";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("content", content);
        requestData.put("destination", dst.toString());
        requestData.put("id", id);
        return this;
    }

    /**
     * Récupère les infos de l'utilisateur
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder getUserInfos() {
        typeOfCommunication = CommunicationType.GET_USER_INFOS;
        url = "auth/verify";
        requestData.put("token", Communication.getRequestToken(this));
        return this;
    }

    public CommunicationBuilder getTaskList(long projectId) {
        typeOfCommunication = CommunicationType.GET_TASK_LIST;
        url = "task/list";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("project", projectId);
        return this;
    }

    /**
     * Récupère une ressource humaine
     *
     * @param id Id de la ressource humaine
     *
     * @return Builder non terminé avec URL
     */
    public CommunicationBuilder getHumanResource(long id) {
        typeOfCommunication = CommunicationType.GET_HUMAN_RESOURCE;
        url = "resource/h/get";
        requestData.put("token", Communication.getRequestToken(this));
        requestData.put("id", id);
        return this;
    }

    /**
     * Vérifie la connexion
     *
     * @return Builder non terminé avec URL
     */
    protected CommunicationBuilder updateConnection() {
        typeOfCommunication = CommunicationType.UPDATE_CONNECTION;
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
        typeOfCommunication = CommunicationType.LIST_PROJECTS;
        url = "project/list";
        requestData.put("token", Communication.getRequestToken(this));
        return this;
    }
}
