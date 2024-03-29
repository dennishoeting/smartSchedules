package main.java.com.bradrydzewski.gwtgantt.table;

import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

/**
 * An editable text cell. Click to edit, escape to cancel, return to commit.
 *
 * Important TODO: This cell still treats its value as HTML for rendering
 * purposes, which is entirely wrong. It should be able to treat it as a proper
 * string (especially since that's all the user can enter).
 */
public abstract class TaskGridNameCell extends AbstractEditableCell<Task, TaskGridNameCell.ViewData> {

//    public class TaskGridNameCellDetails<T> {
//        private String name;
//        private boolean collapsed;
//        private int level;
//    }


        interface Template extends SafeHtmlTemplates {
                @Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\"></input>")
                SafeHtml input(String value);

                @Template("<div><button class=\"{0}\" style=\"margin-left:{1}px\"></button>{2}</div>")
                SafeHtml label(String style, int marginLeft, String value);
        }

        /**
         * The view data object used by this cell. We need to store both the text
         * and the state because this cell is rendered differently in edit mode. If
         * we did not store the edit state, refreshing the cell with view data would
         * always put us in to edit state, rendering a text box instead of the new
         * text string.
         */
        static class ViewData {

                private boolean isEditing;

                /**
                 * If true, this is not the first edit.
                 */
                private boolean isEditingAgain;

                /**
                 * Keep track of the original value at the start of the edit, which
                 * might be the edited value from the previous edit and NOT the actual
                 * value.
                 */
                private String original;

                private String text;

                private Task task;

                /**
                 * Construct a new ViewData in editing mode.
                 *
                 * @param text
                 *            the text to edit
                 */
                public ViewData(Task task) {
                        this.task = task;
                        this.original = task.getName();
                        this.text = task.getName();
                        this.isEditing = true;
                        this.isEditingAgain = false;
                }

                @Override
                public boolean equals(Object o) {
                        if (o == null) {
                                return false;
                        }
                        ViewData vd = (ViewData) o;
                        return equalsOrBothNull(original, vd.original)
                                        && equalsOrBothNull(text, vd.text)
                                        && isEditing == vd.isEditing
                                        && isEditingAgain == vd.isEditingAgain;
                }

                public String getOriginal() {
                        return original;
                }

                public String getText() {
                        return text;
                }

                public Task getTask() {
                        return task;
                }

                @Override
                public int hashCode() {
                        return original.hashCode() + text.hashCode()
                                        + Boolean.valueOf(isEditing).hashCode() * 29
                                        + Boolean.valueOf(isEditingAgain).hashCode();
                }

                public boolean isEditing() {
                        return isEditing;
                }

                public boolean isEditingAgain() {
                        return isEditingAgain;
                }

                public void setEditing(boolean isEditing) {
                        boolean wasEditing = this.isEditing;
                        this.isEditing = isEditing;

                        // This is a subsequent edit, so start from where we left off.
                        if (!wasEditing && isEditing) {
                                isEditingAgain = true;
                                original = text;
                        }
                }

                public void setText(String text) {
                        this.text = text;

                }

                private boolean equalsOrBothNull(Object o1, Object o2) {
                        return (o1 == null) ? o2 == null : o1.equals(o2);
                }
        }

        private static Template template;

        private final SafeHtmlRenderer<String> renderer;

        /**
         * Construct a new EditTextCell that will use a
         * {@link SimpleSafeHtmlRenderer}.
         */
        public TaskGridNameCell() {
                this(SimpleSafeHtmlRenderer.getInstance());
        }

        /**
         * Construct a new EditTextCell that will use a given
         * {@link SafeHtmlRenderer} .
         */
        public TaskGridNameCell(SafeHtmlRenderer<String> renderer) {
                super("click", "keyup", "keydown", "blur");
                if (template == null) {
                        template = GWT.create(Template.class);
                }
                if (renderer == null) {
                        throw new IllegalArgumentException("renderer == null");
                }
                this.renderer = renderer;
        }

        @Override
        public boolean isEditing(Element element, Task value, Object key) {
                ViewData viewData = getViewData(key);
                return viewData == null ? false : viewData.isEditing();
        }

        @Override
        public void onBrowserEvent(Element parent, Task value, Object key,
                        NativeEvent event, ValueUpdater<Task> valueUpdater) {
                // System.out.println("event: "+event.getType()+", client x: " +
                // event.getClientX() + ",  screen x: "+event.getScreenX() +
                // "\n  parent left: " + parent.getAbsoluteLeft() +
                // ",  parent scroll left: " +parent.getScrollLeft());


                ViewData viewData = getViewData(key);
                if (viewData != null && viewData.isEditing()) {
                        // Handle the edit event.
                        editEvent(parent, key, viewData, event, valueUpdater);

                } else {
                        String type = event.getType();
                        int keyCode = event.getKeyCode();
                        boolean enterPressed = "keyup".equals(type)
                                        && keyCode == KeyCodes.KEY_ENTER;
                        if ("click".equals(type) || enterPressed) {

                                boolean buttonClicked = false;

//                              if (viewData == null) {
                                        int buttonRight = parent.getFirstChildElement()
                                                        .getFirstChildElement().getAbsoluteRight();
                                        int buttonLeft = parent.getFirstChildElement()
                                                        .getFirstChildElement().getAbsoluteLeft();
                                        int mouseX = event.getClientX();
                                        buttonClicked = (value.isSummary() && buttonRight >= mouseX && buttonLeft <= mouseX);
//                              }

                                if (buttonClicked) {
                                        value.setCollapsed(!value.isCollapsed());
                                        onExpandCollapse(value);
                                        // Go into edit mode.
                                } else if (viewData == null) {
                                        viewData = new ViewData(value);
                                        setViewData(key, viewData);
                                        edit(parent, value, key);
                                } else {
                                        viewData.setEditing(true);
                                        edit(parent, value, key);
                                }

                        }
                }
        }

        public abstract void onExpandCollapse(Task task);

        @Override
        public void render(Task value, Object key, SafeHtmlBuilder sb) {
                // Get the view data.
                ViewData viewData = getViewData(key);
                if (viewData != null && !viewData.isEditing() && value != null
                                && value.equals(viewData.getTask())) {
                        clearViewData(key);
                        viewData = null;
                }

                if (viewData != null) {
                        String text = viewData.getText();
                        SafeHtml html = renderer.render(text);
                        if (viewData.isEditing()) {
                                // Note the template will not treat SafeHtml specially
                                sb.append(template.input(html.asString()));
                        } else {
                                // The user pressed enter, but view data still exists.
                                sb.append(html);
                        }
                } else if (value != null) {
                        SafeHtml html = renderer.render(value.getName());
                        String style=(value.isCollapsed())?"expandButton":"collapseButton";
                        style=(value.isSummary())?style:"noButton";
                        sb.append(template.label(style,value.getLevel()*25,html.asString()));
                }
        }

        @Override
        public boolean resetFocus(Element parent, Task value, Object key) {
                if (isEditing(parent, value, key)) {
                        getInputElement(parent).focus();
                        return true;
                }
                return false;
        }

        /**
         * Convert the cell to edit mode.
         *
         * @param parent
         *            the parent element
         * @param value
         *            the current value
         * @param key
         *            the key of the row object
         */
        protected void edit(Element parent, Task value, Object key) {

                setValue(parent, value, key);
                InputElement input = getInputElement(parent);
                input.focus();
                input.select();
        }

        /**
         * Convert the cell to non-edit mode.
         *
         * @param parent
         *            the parent element
         * @param value
         *            the current value
         */
        private void cancel(Element parent, Task value) {
                clearInput(getInputElement(parent));
                setValue(parent, value, null);
        }

        /**
         * Clear selected from the input element. Both Firefox and IE fire spurious
         * onblur events after the input is removed from the DOM if selection is not
         * cleared.
         *
         * @param input
         *            the input element
         */
        private native void clearInput(Element input) /*-{
                                                                                                        if (input.selectionEnd)
                                                                                                        input.selectionEnd = input.selectionStart;
                                                                                                        else if ($doc.selection)
                                                                                                        $doc.selection.clear();
                                                                                                        }-*/;

        /**
         * Commit the current value.
         *
         * @param parent
         *            the parent element
         * @param viewData
         *            the {@link ViewData} object
         * @param valueUpdater
         *            the {@link ValueUpdater}
         */
        private void commit(Element parent, ViewData viewData,
                        ValueUpdater<Task> valueUpdater) {
                Task value = updateViewData(parent, viewData, false);
                value.setName(viewData.getText());
                clearInput(getInputElement(parent));
                setValue(parent, value, viewData);
                valueUpdater.update(value);
        }

        private void editEvent(Element parent, Object key, ViewData viewData,
                        NativeEvent event, ValueUpdater<Task> valueUpdater) {
                String type = event.getType();
                boolean keyUp = "keyup".equals(type);
                boolean keyDown = "keydown".equals(type);
                if (keyUp || keyDown) {
                        int keyCode = event.getKeyCode();
                        if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
                                // Commit the change.
                                commit(parent, viewData, valueUpdater);
                        } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
                                // Cancel edit mode.
                                Task originalText = viewData.getTask();
                                if (viewData.isEditingAgain()) {
                                        viewData.setText(originalText.getName());
                                        viewData.setEditing(false);
                                } else {
                                        setViewData(key, null);
                                }
                                cancel(parent, originalText);
                        } else {
                                // Update the text in the view data on each key.
                                updateViewData(parent, viewData, true);
                        }
                } else if ("blur".equals(type)) {
                        // Commit the change. Ensure that we are blurring the input element
                        // and
                        // not the parent element itself.
                        EventTarget eventTarget = event.getEventTarget();
                        if (Element.is(eventTarget)) {
                                Element target = Element.as(eventTarget);
                                if ("input".equals(target.getTagName().toLowerCase())) {
                                        commit(parent, viewData, valueUpdater);
                                }
                        }
                }
        }

        /**
         * Get the input element in edit mode.
         */
        private InputElement getInputElement(Element parent) {
                return parent.getFirstChild().<InputElement> cast();
        }

        /**
         * Update the view data based on the current value.
         *
         * @param parent
         *            the parent element
         * @param viewData
         *            the {@link ViewData} object to update
         * @param isEditing
         *            true if in edit mode
         * @return the new value
         */
        private Task updateViewData(Element parent, ViewData viewData,
                        boolean isEditing) {
                InputElement input = (InputElement) parent.getFirstChild();
                String value = input.getValue();
                viewData.setText(value);
                viewData.setEditing(isEditing);
                return viewData.getTask();
        }

        // abstract boolean isCollapsed();
        //
        // abstract boolean hasChildren();

}

