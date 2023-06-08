package name.martingeisse.grumpyrest;

public interface Handler {

    Object handle(RequestCycle requestCycle);

}
