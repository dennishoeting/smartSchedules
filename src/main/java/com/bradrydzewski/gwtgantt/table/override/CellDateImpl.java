package main.java.com.bradrydzewski.gwtgantt.table.override;

import java.util.Date;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * A {@link Cell} used to render and edit {@link Date}s. When a cell is selected
 * by clicking on it, a {@link DatePicker} is popped up. When a date is selected
 * using the {@code DatePicker}, the new date is passed to the
 * {@link ValueUpdater#update update} method of the {@link ValueUpdater} that
 * was passed to {@link #onBrowserEvent} for the click event. Note that this
 * means that the call to {@code ValueUpdater.update} will occur after {@code
 * onBrowserEvent} has returned. Pressing the 'escape' key dismisses the {@code
 * DatePicker} popup without calling {@code ValueUpdater.update}.
 *
 * <p>
 * Each {@code DatePickerCell} has a unique {@code DatePicker} popup associated
 * with it; thus, if a single {@code DatePickerCell} is used as the cell for a
 * column in a table, only one entry in that column will be editable at a given
 * time.
 * </p>
 */
public class CellDateImpl extends AbstractEditableCell<Date, Date> {

	interface Template extends SafeHtmlTemplates {
		@Template("<div style='text-align:right;'>{0}</div>")
		SafeHtml div(String value);
	}

        Template template = GWT.create(Template.class);

  private static final int ESCAPE = 27;

  private final DatePicker datePicker;
  private final DateTimeFormat format;
  private int offsetX = 10;
  private int offsetY = 10;
  private Object lastKey;
  private Element lastParent;
  private Date lastValue;
  private PopupPanel panel;
  private final SafeHtmlRenderer<String> renderer;
  private ValueUpdater<Date> valueUpdater;

  /**
   * Constructs a new DatePickerCell that uses the date/time format given by
   * {@link DateTimeFormat#getFullDateFormat}.
   */
  @SuppressWarnings("deprecation")
  public CellDateImpl() {
    this(DateTimeFormat.getFullDateFormat(),
        SimpleSafeHtmlRenderer.getInstance());
  }

  /**
   * Constructs a new DatePickerCell that uses the given date/time format and a
   * {@link SimpleSafeHtmlRenderer}.
   *
   * @param format a {@link DateTimeFormat} instance
   */
  public CellDateImpl(DateTimeFormat format) {
    this(format, SimpleSafeHtmlRenderer.getInstance());
  }

  /**
   * Constructs a new DatePickerCell that uses the date/time format given by
   * {@link DateTimeFormat#getFullDateFormat} and the given
   * {@link SafeHtmlRenderer}.
   *
   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
   */
  public CellDateImpl(SafeHtmlRenderer<String> renderer) {
    this(DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL), renderer);
  }

  /**
   * Constructs a new DatePickerCell that uses the given date/time format and
   * {@link SafeHtmlRenderer}.
   *
   * @param format a {@link DateTimeFormat} instance
   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
   */
  public CellDateImpl(DateTimeFormat format, SafeHtmlRenderer<String> renderer) {
    super("click", "keydown");
    if (format == null) {
      throw new IllegalArgumentException("format == null");
    }
    if (renderer == null) {
      throw new IllegalArgumentException("renderer == null");
    }
    this.format = format;
    this.renderer = renderer;

    this.datePicker = new DatePicker();
    this.panel = new PopupPanel(true, true) {
      @Override
      protected void onPreviewNativeEvent(NativePreviewEvent event) {
        if (Event.ONKEYUP == event.getTypeInt()) {
          if (event.getNativeEvent().getKeyCode() == ESCAPE) {
            // Dismiss when escape is pressed
            panel.hide();
          }
        }
      }
    };
    panel.addCloseHandler(new CloseHandler<PopupPanel>() {
      public void onClose(CloseEvent<PopupPanel> event) {
        lastKey = null;
        lastValue = null;
        if (lastParent != null && !event.isAutoClosed()) {
          // Refocus on the containing cell after the user selects a value, but
          // not if the popup is auto closed.
          lastParent.focus();
        }
        lastParent = null;
      }
    });
    panel.add(datePicker);

    // Hide the panel and call valueUpdater.update when a date is selected
    datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
      public void onValueChange(ValueChangeEvent<Date> event) {
        // Remember the values before hiding the popup.
        Element cellParent = lastParent;
        Date oldValue = lastValue;
        Object key = lastKey;
        panel.hide();

        // Update the cell and value updater.
        Date date = event.getValue();
        
        date = validate(date, oldValue);
        
        setViewData(key, date);
        setValue(cellParent, oldValue, key);
        if (valueUpdater != null) {
          valueUpdater.update(date);
        }
      }
    });
  }
  
  public Date validate(Date newValue, Date originalValue) {
	  return newValue;
  }

  @Override
  public boolean isEditing(Element parent, Date value, Object key) {
    return lastKey != null && lastKey.equals(key);
  }

  @Override
  public void onBrowserEvent(Element parent, Date value, Object key,
      NativeEvent event, ValueUpdater<Date> valueUpdater) {
    super.onBrowserEvent(parent, value, key, event, valueUpdater);
    if ("click".equals(event.getType())) {
      onEnterKeyDown(parent, value, key, event, valueUpdater);
    }
  }

  @Override
  public void render(Date value, Object key, SafeHtmlBuilder sb) {
    // Get the view data.
    Date viewData = getViewData(key);
    if (viewData != null && viewData.equals(value)) {
      clearViewData(key);
      viewData = null;
    }

    String s = null;
    if (viewData != null) {
      s = format.format(viewData);
    } else if (value != null) {
      s = format.format(value);
    }
    if (s != null) {
    	SafeHtml html = renderer.render(s);
        sb.append(template.div(html.asString()));
//      sb.append(renderer.render(s));
    }
  }

  @Override
  protected void onEnterKeyDown(final Element parent, Date value, Object key,
      NativeEvent event, ValueUpdater<Date> valueUpdater) {
    this.lastKey = key;
    this.lastParent = parent;
    this.lastValue = value;
    this.valueUpdater = valueUpdater;

    Date viewData = getViewData(key);
    Date date = (viewData == null) ? value : viewData;
    datePicker.setCurrentMonth(date);
    datePicker.setValue(date);
    panel.setPopupPositionAndShow(new PositionCallback() {
      public void setPosition(int offsetWidth, int offsetHeight) {
        panel.setPopupPosition(parent.getAbsoluteLeft() + offsetX,
            parent.getAbsoluteTop() + offsetY);
      }
    });
  }
}