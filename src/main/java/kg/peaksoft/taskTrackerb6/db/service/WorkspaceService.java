package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserWorkSpaceRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final UserWorkSpaceRepository userWorkSpaceRepository;
    private final JavaMailSender mailSender;
    private final BoardRepository boardRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest) throws MessagingException {
        User user = getAuthenticateUser();
        Workspace workspace = convertToEntity(workspaceRequest);
        UserWorkSpace userWorkSpace = new UserWorkSpace();
        userWorkSpace.setUser(user);
        userWorkSpace.setWorkspace(workspace);
        userWorkSpace.setRole(Role.ADMIN);
        user.addUserWorkSpace(userWorkSpace);
        workspace.addUserWorkSpace(userWorkSpace);
        workspace.setLead(user);
        userWorkSpaceRepository.save(userWorkSpace);
        return convertToResponse(workspaceRepository.save(workspace));
    }


    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workspace with id: " + id + " not found!")
        );

        return convertToResponse(workspace);
    }


    public SimpleResponse deleteWorkspaceById(Long id) {
        User user = getAuthenticateUser();

        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workspace with id: " + id + " not found!")
        );

        if (!user.getEmail().equals(workspace.getLead().getEmail())) {
            throw new BadCredentialException("You can not delete this workspace!");
        }

        workspaceRepository.delete(workspace);
        return new SimpleResponse("workspace with id: " + id + " is deleted!", "DELETE");
    }


    public WorkspaceResponse changeWorkspacesAction(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workspace with id: " + id + " not found!")
        );

        workspace.setIsFavorite(!workspace.getIsFavorite());
        Workspace workspace1 = workspaceRepository.save(workspace);
        return convertToResponse(workspace1);
    }


    public List<WorkspaceResponse> getAllUserWorkspaces() {
        User user = getAuthenticateUser();

        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();
        List<Workspace> workspaces = new ArrayList<>();

        for (UserWorkSpace userWorkSpace : user.getUserWorkSpaces()) {
            if (userWorkSpace.getUser().equals(user)) {
                workspaces.add(userWorkSpace.getWorkspace());
            }
        }

        for (Workspace workspace : workspaces) {
            workspaceResponses.add(convertToResponse(workspace));
        }
        return workspaceResponses;
    }


    public List<FavoritesResponse> getAllFavorites() {
        List<FavoritesResponse> getFavorites = new ArrayList<>();
        getFavorites.add(new FavoritesResponse(getFavoriteWorkspacesList(), getFavoriteBoardsList()));
        return getFavorites;
    }


    private List<FavoriteWorkspaceResponse> getFavoriteWorkspacesList() {
        List<FavoriteWorkspaceResponse> favoriteWorkspaces = new ArrayList<>();
        List<Workspace> workspaces = workspaceRepository.findAllByFavorites();
        for (Workspace workspace : workspaces) {
            favoriteWorkspaces.add(convertToFavoriteWorkspaceResponse(workspace));
        }

        return favoriteWorkspaces;
    }


    private List<FavoriteBoardResponse> getFavoriteBoardsList() {
        List<FavoriteBoardResponse> favoriteBoards = new ArrayList<>();
        List<Board> boards = boardRepository.findAllByFavorites();
        for (Board board : boards) {
            favoriteBoards.add(convertToFavoriteBoardResponse(board));
        }

        return favoriteBoards;
    }


    private Workspace convertToEntity(WorkspaceRequest request) throws MessagingException {
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setIsFavorite(workspace.getIsFavorite());

        if (request.getEmails().isEmpty() || request.getEmails().get(0).equals("") || request.getEmails().get(0).isBlank()) {

        } else {
            for (String email : request.getEmails()) {
                boolean exists = userRepository.existsUserByEmail(email);
                if (!exists) {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setSubject("[Task tracker] invitation to my workspace");
                    helper.setFrom("tasktracker.b6@gmail.com");
                    helper.setTo(email);
                    helper.setText(request.getLink());
                    mailSender.send(mimeMessage);
                } else {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setSubject("[Task tracker] invitation to my workspace");
                    helper.setFrom("tasktracker.b6@gmail.com");
                    helper.setTo(email);
                    helper.setText(request.getLink()); 
                    mailSender.send(mimeMessage);
                }
            }
        }

        return workspace;
    }


    private WorkspaceResponse convertToResponse(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                convertToResponseCreator(workspace.getLead()),
                workspace.getIsFavorite()
        );
    }


    private CreatorResponse convertToResponseCreator(User user) {
        CreatorResponse creatorResponse = new CreatorResponse();
        creatorResponse.setId(user.getId());
        creatorResponse.setFirstName(user.getFirstName());
        creatorResponse.setLastName(user.getLastName());
        creatorResponse.setImage(user.getImage());
        return creatorResponse;
    }


    private FavoriteWorkspaceResponse convertToFavoriteWorkspaceResponse(Workspace workspace) {
        return new FavoriteWorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getIsFavorite()
        );
    }


    private FavoriteBoardResponse convertToFavoriteBoardResponse(Board board) {
        return new FavoriteBoardResponse(
                board.getId(),
                board.getTitle(),
                board.getBackground(),
                board.getIsFavorite()
        );
    }
}