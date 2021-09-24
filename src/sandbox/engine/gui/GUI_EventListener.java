package sandbox.engine.gui;

public interface GUI_EventListener {
    void onUserHover(String id);
    void onUserStartInteract(String id);
    void onUserInteract(String id);
    void onUserEndInteract(String id);
}
