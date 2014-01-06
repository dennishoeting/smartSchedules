package main.java.com.bradrydzewski.gwtgantt.gantt;

import java.util.Date;
import java.util.List;

import main.java.com.bradrydzewski.gwtgantt.DateUtil;
import main.java.com.bradrydzewski.gwtgantt.geometry.Rectangle;
import main.java.com.bradrydzewski.gwtgantt.model.DurationFormat;

import com.google.gwt.i18n.client.DateTimeFormat;

public class GanttChartPresenterMinuteImpl<T> extends GanttChartPresenter<T> {


	protected static final int UNIT_WIDTH = 15*6;
	protected static final int UNIT_WIDTH_OFFSET = 3;
	
	
    @SuppressWarnings("deprecation")
    protected void renderBackground() {
        Date date = (Date) start.clone();
        int diff = DateUtil.differenceInDays(start, finish);

        
        for(int i=0;i<diff;i++) {
        	
    		int width = UNIT_WIDTH * 24 - 2; //24 hours in a day
    		int left =  UNIT_WIDTH * 24 * i;

            Rectangle topTimescaleBounds = new Rectangle(left, 0, width, 25);
            String topTimescaleString = DateTimeFormat.getMediumDateFormat().format(date);
            view.renderTopTimescaleCell(topTimescaleBounds, topTimescaleString);
        	
            left -= UNIT_WIDTH/2;
            
            for(int h=0;h<24;h++) {
            	width = UNIT_WIDTH - 2;
                Rectangle bottomTimescaleBounds = new Rectangle(left, 0, width, 25);
                // TODO ROFFEL!
                String bottomTimescaleString = (h<=12)?((h==12)?("12 pm"):(h+" am")):((h-12)+" pm");
                view.renderBottomTimescaleCell(bottomTimescaleBounds, bottomTimescaleString);

            	left += width+2;
                /*
                 * Hintergrund bei Freizeit
                 * 
                //draw the non-working hours
                //for h==0, draw 12am - 8am, for h==1 draw 4pm - 12am
                int colTop = 0;
                int colLeft = left + (h * 4 * UNIT_WIDTH);
                int colWidth = UNIT_WIDTH * 8 - 2;
                int colHeight = -1; //not used... 100% defined by style
                Rectangle colBounds = new Rectangle(colLeft, colTop, colWidth, colHeight);
                view.renderColumn(colBounds, (h==1)?SATURDAY:SUNDAY);
                */
            }
            
        	date = DateUtil.addDays(date, 1);
        }

    }

    
    
    protected void renderTask(T task, int level) {
        int top = TASK_ROW_HEIGHT * level + TASK_PADDING_TOP;//order * TASK_HEIGHT + ((order+1) * TASK_PADDING) + (order * TASK_PADDING);
        int left = (int)((float)(DateUtil.differenceInMinutes(start,provider.getStart(task)) / (60.0*24.0)) * UNIT_WIDTH*24);
        int width = (int)((float)(DateUtil.differenceInMinutes(provider.getStart(task),provider.getFinish(task)) / (60.0*24.0)) * UNIT_WIDTH*24-2);
        int height = TASK_HEIGHT;
        
        //render the task
        Rectangle taskBounds = new Rectangle(left, top, width, height);
        view.renderTask(task, taskBounds);

        //render the label
        Rectangle labelBounds = new Rectangle(taskBounds.getRight(), top - 2, -1, -1);
        view.renderTaskLabel(task, labelBounds);

        //if task is selected, make sure it is rendered as selected
        if (selectionModel.isSelected(task)) {
            view.doTaskSelected(task);
        }
    }
    


    protected void renderTaskSummary(T task, int order) {

        int daysFromStart = DateUtil.differenceInDays(start, provider.getStart(task));//+1
        int daysInLength = DateUtil.differenceInDays(provider.getStart(task), provider.getFinish(task)) + 1;

        daysInLength = Math.max(daysInLength, 1);

        int top = TASK_ROW_HEIGHT * order + SUMMARY_PADDING_TOP;
        //order * TASK_HEIGHT + ((order+1) * TASK_PADDING) + (order * TASK_PADDING);
        int left = daysFromStart * UNIT_WIDTH*24;
        int width = daysInLength * UNIT_WIDTH*24-2;
       
        int height = SUMMARY_HEIGHT;

        //adjust width & height
        width = width - UNIT_WIDTH * 16;
        left = left + UNIT_WIDTH * 8;
        
        //adjust finish if duration is based on hours
        if(provider.getDurationFormat(task) == DurationFormat.HOURS) {
    		daysInLength = daysInLength - 1;
    		int hoursWorked = (int)(8d / provider.getDuration(task));
    		left = left - (8 - hoursWorked * UNIT_WIDTH);
    	}
        
        //render the task
        Rectangle taskBounds = new Rectangle(left, top, width, height);
        view.renderTaskSummary(task, taskBounds);

        //render the label
        Rectangle labelBounds = new Rectangle(taskBounds.getRight(), top - 2, -1, -1);
        view.renderTaskLabel(task, labelBounds);

        //if task is selected, make sure it is rendered as selected
        if (selectionModel.isSelected(task)) {
            view.doTaskSelected(task);
        }
    }

    protected void renderTaskMilestone(T task, int order) {

        int daysFromStart = DateUtil.differenceInDays(start, provider.getStart(task));//+1;

        int top = TASK_ROW_HEIGHT * order + MILESTONE_PADDING_TOP;//order * TASK_HEIGHT + ((order+1) * TASK_PADDING) + (order * TASK_PADDING);
        int left = daysFromStart * UNIT_WIDTH*24;
        int width = MILESTONE_WIDTH;
        int height = TASK_HEIGHT;


        //adjust width & height
//        width = width - UNIT_WIDTH * 8;
        left = left + UNIT_WIDTH * 8;
        
        //render the task
        Rectangle taskBounds = new Rectangle(left, top, width, height);
        view.renderTaskMilestone(task, taskBounds);

        //render the label
        Rectangle labelBounds = new Rectangle(taskBounds.getRight(), top - 2, -1, -1);
        view.renderTaskLabel(task, labelBounds);

        //if task is selected, make sure it is rendered as selected
        if (selectionModel.isSelected(task)) {
            view.doTaskSelected(task);
        }
    }

    
    
    
    @Override
	public void scrollToItem(T item) {
    	Rectangle rect = view.getTaskRectangle(provider.getUID(item));
    	if(rect != null) {
    		int row = (int)Math.floor(rect.getTop()/TASK_ROW_HEIGHT);
    		int top = (row-1)*TASK_ROW_HEIGHT;
    		int left = rect.getLeft()-UNIT_WIDTH;//IS THIS RIGHT?
    		left = Math.max(0, left);
    		top = Math.max(0, top);
    		view.doScroll(left, top);
    	}
    }

    @Override
    protected Date calculateStartDate(List<? extends T> values) {

        Date adjustedStart = display.getStart();

        //if the gantt chart's start date is null, let's set one automatically
        if (display.getStart() == null) {
            adjustedStart = new Date();
        }

        //if the first task in the gantt chart is before the gantt charts
        // project start date ...
        if (values != null && !values.isEmpty()
                && provider.getStart(values.get(0)).before(adjustedStart)) {

            adjustedStart = provider.getStart(values.get(0));
        }

        adjustedStart = DateUtil.addDays(adjustedStart, -1);
        return DateUtil.reset(adjustedStart);
    }

    @Override
    protected Date calculateFinishDate(List<? extends T> values) {
        Date adjustedFinish = display.getFinish();

        //if the gantt chart's finish date is null, let's set one automatically
        if (adjustedFinish == null) {
            adjustedFinish = new Date();
        }

        //if the last task in the gantt chart is after the gantt charts
        // project finish date ...
        if (values != null && !values.isEmpty() && provider.getFinish(values.get(
                values.size() - 1)).after(adjustedFinish)) {

            adjustedFinish = provider.getFinish(values.get(values.size() - 1));
        }

        adjustedFinish = DateUtil.addDays(adjustedFinish, +90);
        return DateUtil.reset(adjustedFinish);
    }

}
