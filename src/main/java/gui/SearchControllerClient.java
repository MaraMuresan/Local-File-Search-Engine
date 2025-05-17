package gui;

public class SearchControllerClient {
    private final SearchControllerFacade searchControllerFacade;

    public SearchControllerClient(gui.SearchControllerFacade facade) {
        this.searchControllerFacade = facade;
    }
    public void sendSearchQuery(String query) {
        searchControllerFacade.handleSearch(query);
    }
}
