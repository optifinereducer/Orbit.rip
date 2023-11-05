package net.frozenorb.foxtrot.events;

public interface Event {

    String getName();

    boolean isActive();

    void tick();

    void setActive(boolean active);

    boolean isHidden();

    void setHidden(boolean hidden);

    boolean activate();

    boolean deactivate();

    EventType getType();

}
