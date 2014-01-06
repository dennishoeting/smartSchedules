package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MinimizeClickEvent;
import com.smartgwt.client.widgets.events.MinimizeClickHandler;
import com.smartgwt.client.widgets.events.RestoreClickEvent;
import com.smartgwt.client.widgets.events.RestoreClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;
import com.smartgwt.client.widgets.viewer.DetailViewerField;
import com.smartgwt.client.widgets.viewer.DetailViewerRecord;

import de.bachelor.smartSchedules.client.view.SchedulePopupView;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;

/**
 * Presenter für Popup mit altem/alternativen Ablaufplan
 * @author Dennis
 */
public class SchedulePopupPresenter {
	/**
	 * dazugehöriger View
	 */
	private final SchedulePopupView view;
	
	/**
	 * dazugehöriger Ablaufplan
	 */
	private final Schedule schedule;
	
	private final Scenario scenario;

	/**
	 * Hilfsvariable für Baumtiefe
	 */
	public static final int MAIN_TIER = 0,
							ORDER_TIER = 1,
							PRODUCT_TIER = 2,
							VARIANT_TIER = 3,
							OPERATION_TIER = 4;
	
	/**
	 * Hilfsvariable
	 */
	public static final int TYPE_OLD = 0,
							TYPE_ALTERNATIVE = 1;
	
	/**
	 * Konstruktor
	 * @param schedule Ablaufplan
	 * @param popupType Typ
	 */
	public SchedulePopupPresenter(Scenario scenario, Schedule schedule, int popupType) {
		this.scenario = scenario;
		this.schedule = schedule;
		
		/*
		 * Titel des View entsprechend setzen
		 */
		this.view = new SchedulePopupView(((popupType==TYPE_OLD)?"Old":"Alternative")+" schedule ("+this.schedule.getAlgorithmInformations().getDueTime()+")");
		
		/*
		 * Komponenten füllen
		 */
		this.fillGanttGrid(schedule);
		this.fillResourceList(schedule);
		this.fillTree(schedule);
		//KeyFigures aus ScenarioPresenter (nicht von Scenario abhängig)
		this.showKeyFigures(ScenarioPresenter.getInstance().getKeyFigureList());
		this.showAlgorithmInformation(schedule.getAlgorithmInformations());
		
		this.addListeners();
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	public void addListeners() {
		/**
		 * Bei Minimierung
		 */
		this.view.addMinimizeClickHandler(new MinimizeClickHandler() {
			@Override
			public void onMinimizeClick(MinimizeClickEvent event) {
				view.setWidth(250);
				view.minimize();
			}
		});
		
		/**
		 * Bei Ent-Minimierung
		 */
		this.view.addRestoreClickHandler(new RestoreClickHandler() {
			@Override
			public void onRestoreClick(RestoreClickEvent event) {
				view.setWidth(900);
				view.maximize();	
			}
		});
		
		/**
		 * Bei DrillDown
		 */
		this.view.getDrillDownButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getGantt().zoomIn();
			}
		});
		
		/**
		 * Bei RollUp
		 */
		this.view.getRollUpButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getGantt().zoomOut();
			}
		});
		
		/**
		 * Bei Wechsel des Flags "showConnectors"
		 */
		this.view.getCheckShowConnectors().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				GanttManager.setShowConnectors((Boolean)(event.getValue()));
				view.getGantt().redraw();
			}
		});

		/**
		 * Bei Klick auf Knoten in Baumstruktur
		 */
		this.view.getTreeCanvas().addNodeClickHandler(new NodeClickHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public void onNodeClick(NodeClickEvent event) {
				/*
				 * Selektierten Knoten holen
				 */
				TreeNode clickedNode = event.getNode();
				
				/*
				 * Alles wieder unselektiert zeichnen
				 */
				for(Task a : view.getDataProvider().getList()) {
					a.setSelected(false);
				}
				
				/*
				 * Aktion, abhängig von Baumtiefe
				 */
				ScheduleOrder selectedOrder;
				Task selectedTask;
				Task taskToScrollTo = null;
				switch(clickedNode.getAttributeAsInt("tier")) {
				/*
				 * Bei Klick auf einen Auftrag
				 */
				case ORDER_TIER:
					/*
					 * Selektierten Auftrag holen
					 */
					selectedOrder = schedule.getOrder(scenario, clickedNode.getAttributeAsInt("id"));
					
					/*
					 * Jede Allokation in Auftrag selektieren
					 */
					for(Task t : view.getDataProvider().getList()) {
						if(t.getOrder() == selectedOrder.getOrderID()) {
							t.setSelected(true);
							
							if(taskToScrollTo == null
							|| t.getStart().before(taskToScrollTo.getStart())) {
								taskToScrollTo = t;
							}
						}
					}
					break;
				/*
				 * Bei Klick auf Variante
				 */
				case VARIANT_TIER:
					/*
					 * Jede Allokation in Variante selektieren
					 */
					for(Allocation a : (List<Allocation>)(clickedNode.getAttributeAsObject("allocations"))) {
						for(Task t : view.getDataProvider().getList()) {
							if(a.getUID() == t.getUID()) {
								t.setSelected(true);
								
								if(taskToScrollTo == null || t.getStart().before(taskToScrollTo.getStart())) {
									taskToScrollTo = t;
								}
							}
						}
					}
					break;
				/*
				 * Bei Klick auf Operation
				 */
				case OPERATION_TIER:
					/*
					 * Selektierte Operation holen und selektieren (optisch, in Gantt)
					 */
					selectedTask = view.getDataProvider().getList().get(clickedNode.getAttributeAsInt("id"));
					selectedTask.setSelected(true);
					view.getGantt().scrollToItem(selectedTask);
					break;
				/*
				 * Andernfalls auf "jetzt" scrollen
				 */
				default:
					view.getGantt().scrollToItem(schedule.getAllocationNow(scenario)
					);
				}
				
				/*
				 * Neuzeuchnung
				 */
				view.getGantt().redraw();
				
				/*
				 * Wenn geeignetes Objekt definiert, scrolle dort hin 
				 */
				if(taskToScrollTo != null)
					view.getGantt().scrollToItem(taskToScrollTo);
			}
		});
	}

	/**
	 * Baumstruktur füllen
	 * @param schedule Ablaufplan
	 */
	public void fillTree(Schedule schedule) {
		final Tree tree = new Tree();
		final TreeNode root = new TreeNode("Scenario");
		final TreeNode orders = new TreeNode("Orders");
		orders.setAttribute("tier", MAIN_TIER);
		tree.setRoot(root);
		tree.add(orders, root);
		TreeNode orderNode, variantNode, operationNode;
		/*
		 * Für jede Order
		 */
		for(ScheduleOrder o : schedule.getPlannedOrders(scenario)) {
			orderNode = new TreeNode(o.getName()+ " (id:"+o.getOrderID()+")"); 
			orderNode.setAttribute("tier", ORDER_TIER);
			orderNode.setAttribute("id", o.getOrderID());
			tree.add(orderNode, orders);
			/*
			 * Für jede Variante je Order (Produkt)
			 */
			for(PlannedVariant v : schedule.getPlannedVariantsForOrder(scenario, o)) {
				variantNode = new TreeNode(v.getProduct().getName() + "&nbsp;(var"+v.getPlannedVariant().getVariantID()+")");
				variantNode.setAttribute("tier", VARIANT_TIER);
				variantNode.setAttribute("allocations", v.getAllocationList());
				tree.add(variantNode, orderNode);
				/*
				 * Für jede Operation in Produkt/Variante
				 */
				for(Allocation a : v.getAllocationList()) {
					operationNode = new TreeNode(a.getName());
					operationNode.setAttribute("tier", OPERATION_TIER);
					operationNode.setAttribute("id", a.getUID());
					tree.add(operationNode, variantNode);
				}
			}
		}
		
		this.view.getTreeCanvas().setData(tree);
	}

	
	/**
	 * Maschinenliste füllen
	 * @param schedule Ablaufplan
	 */
	public void fillResourceList(Schedule schedule) {
		/*
		 * Record-Feld für GanttGridRecords
		 */
		ResourceListRecord[] data = new ResourceListRecord[schedule.getResources(scenario).size()];
		
		/*
		 * Resourcen holen und nach ID ordnen
		 */
		List<Resource> resourceList = new ArrayList<Resource>(schedule.getResources(scenario).keySet());
		Collections.sort(resourceList, new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				if(o1.equals(o2))
					return 0;
				if(o1.getResourceID() < o2.getResourceID()) 
					return -1;
				return 1;
			}
		});
		
		/*
		 * Record-Feld füllen
		 */
		for(int i=0; i<data.length; i++) {
			data[i] = new ResourceListRecord(resourceList.get(i).getName());
		}
		this.view.getResourceListGrid().setData(data);
	}
	
	/**
	 * Gantt-Diagramm füllen
	 * @param schedule Ablaufplan
	 * @see GanttManager
	 */
	public void fillGanttGrid(Schedule schedule) {
		view.getDataProvider().setList(GanttManager.visualizeSchedule(scenario, schedule));
	}
	
	/**
	 * Kennzahlen aktualisieren
	 * @param keyFigureList Liste von Kennzahlen
	 */
	public void showKeyFigures(List<KeyFigure> keyFigureList) {
		DetailViewerField[] fieldArray = new DetailViewerField[keyFigureList.size()];
		DetailViewerRecord[] recordArray  = new DetailViewerRecord[1];
		recordArray[0] = new DetailViewerRecord();
		/*
		 * Für jede Kennzahl: In Kennzahlen-Feld füllen
		 */
		for(int i=0; i<keyFigureList.size(); i++) {
			fieldArray[i] = new DetailViewerField(keyFigureList.get(i).getName());
			try {
				//FIXME: Nicht errechnen sondern gespeicherten Wert nehmen
				recordArray[0].setAttribute(keyFigureList.get(i).getName(), keyFigureList.get(i).calculate(scenario, schedule));
			} catch (NotPlannedException e) {
				GWT.log("Error: " +e.getMessage());
			}
		}
		
		/*
		 * Einfügen und visualisieren
		 */
		view.getKeyFigureViewer().setData(recordArray);
		view.getKeyFigureViewer().selectRecord(recordArray[0]);
		view.getKeyFigureViewer().setFields(fieldArray);
	}
	
	/**
	 * Algorithmus-Informationen (Kennzahlen) füllen
	 * @param algorithmInformation Algorithmus-Informationen (Aus Schedule)
	 */
	public void showAlgorithmInformation(AlgorithmInformation algorithmInformation) {
		/*
		 * Record erstellen
		 */
		final DetailViewerRecord record = new DetailViewerRecord();
		record.setAttribute("startDate", algorithmInformation.getStartTime());
		record.setAttribute("dueDate", algorithmInformation.getDueTime());
		record.setAttribute("duration", algorithmInformation.getDurationInMS());
		record.setAttribute("algName", algorithmInformation.getAlgorithmName());
		
		/*
		 * Einfügen und visualisieren
		 */
		view.getAlgorithmInformationsViewer().setData(new DetailViewerRecord[]{record});
		view.getAlgorithmInformationsViewer().selectRecord(record);
	}
	
	/**
	 * Record für RecourcenListe
	 * @author Dennis
	 */
	private class ResourceListRecord extends ListGridRecord {
		private ResourceListRecord(String name) {
			this.setName(name);
		}
		
		public void setName(String name) {
			this.setAttribute("name", name);
		}

		@SuppressWarnings("unused")
		public String getName() {
			return this.getAttribute("name");
		}
	}
}
