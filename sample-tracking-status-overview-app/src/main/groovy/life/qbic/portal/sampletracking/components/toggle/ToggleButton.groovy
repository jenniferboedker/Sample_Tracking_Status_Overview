package life.qbic.portal.sampletracking.components.toggle


import com.vaadin.ui.Button

/**
 * <b>A button that can be toggled and has multiple states different listeners can be added to.</b>
 *
 * @since 1.0.0
 */
class ToggleButton extends Button {

    /**
     * The possible button states
     */
    public static enum State {
        ONE,
        TWO
    }

    private final String captionOne
    private final String captionTwo
    private State state = State.ONE
    Collection<ClickListener> clickListenersOne = []
    Collection<ClickListener> clickListenersTwo = []


    /**
     * Toggles the button programmatically. This will not trigger the click listeners.
     */
    public void toggle() {
        this.state = state == State.ONE ? State.TWO : State.ONE
        this.caption = state == State.ONE ? captionOne : captionTwo
    }

    /**
     * Inform the click listeners for the current state and toggle the view afterwards
     * @param clickEvent the click event that triggered the toggle
     */
    private void toggleWithEvent(ClickEvent clickEvent) {
        if (state == State.ONE) {
            clickListenersOne.forEach({ it.buttonClick(clickEvent) })
        } else if (state == State.TWO) {
            clickListenersTwo.forEach({ it.buttonClick(clickEvent) })
        }
        toggle()
    }

    /**
     * Creates a Toggle button with the captions provided
     * @param captionOne the caption to show when in state one
     * @param captionTwo the caption to show when in state two
     */
    ToggleButton(String captionOne, String captionTwo) {
        super(captionOne)
        this.state = State.ONE
        this.captionOne = captionOne
        this.captionTwo = captionTwo
        registerListener()
    }

    private void registerListener() {
        this.addClickListener({
            toggleWithEvent(it)
        })
    }

    /**
     * Adds a click listener to the button when in the given state.
     * @param listener the listener to inform
     * @param state the state the listener should be active
     */
    public void addClickListener(ClickListener listener, State state) {
        if (state == State.ONE) {
            clickListenersOne.add(listener)
        } else if (state == State.TWO) {
            clickListenersTwo.add(listener)
        }
    }

}
