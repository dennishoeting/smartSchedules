package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;
import com.smartgwt.client.widgets.viewer.DetailViewerField;
import com.smartgwt.client.widgets.viewer.DetailViewerRecord;

import de.bachelor.smartSchedules.client.view.CurrentScheduleView;
import de.bachelor.smartSchedules.client.view.InvokeEventView;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewResourceEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;

/**
 * Presenter für den aktuellen Ablaufplan (Haupt-View)
 * @author Dennis
 */
public class CurrentSchedulePresenter {
	/**
	 * dazugehöriger View
	 */
	private final CurrentScheduleView view;
	
	/**
	 * Hilfskonstanten für Repräsentation der Baumtiefen
	 */
	public static final int MAIN_TIER = 0,
							ORDER_TIER = 1,
							PRODUCT_TIER = 2,
							VARIANT_TIER = 3,
							OPERATION_TIER = 4;
	
	/**
	 * Zählvariable für eingetroffene Events
	 */
	private int newEventsArrived;
	
	/**
	 * Baumstruktur des Scenarios
	 */
	private Tree tree;
	
	private final ScheduleAlternativesPresenter alternativeScheduleListPresenter;
	
	/**
	 * Konstruktor
	 */
	public CurrentSchedulePresenter() {
		tree = new Tree();
		view = new CurrentScheduleView();
		alternativeScheduleListPresenter = new ScheduleAlternativesPresenter();
		
		this.resetNewEventsCounter();
		this.addListeners();
		
		this.fillResourceList(ScenarioPresenter.getInstance().getChosenSchedule());
		this.fillGanttGrid(ScenarioPresenter.getInstance().getChosenSchedule());
		this.fillTree(ScenarioPresenter.getInstance().getChosenSchedule());
		this.showKeyFigures(ScenarioPresenter.getInstance().getKeyFigureList());
		this.showAlgorithmInformation(ScenarioPresenter.getInstance().getChosenSchedule().getAlgorithmInformations());
		this.alternativeScheduleListPresenter.update(ScenarioPresenter.getInstance().getScenario().getCurrentSchedules());
	}	
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		this.view.getGantt().addScrollHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				CurrentSchedulePresenter.this.view.getResourceListGrid().scrollTo(0, CurrentSchedulePresenter.this.view.getGantt().getScrollX());
			}
		});
		
		/**
		 * Bei Selektion einer Allocation im Gantt-Diagramm
		 */
		this.view.getSelectionModel().addSelectionChangeHandler(new Handler() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				/*
				 * Alle Tasks deselektieren
				 */
				for(Task a : view.getDataProvider().getList()) {
					a.setSelected(false);
				}
				
				/*
				 * Selektierte Task holen und (optisch) selektieren
				 */
				Task selectedTask = ((Task)((SingleSelectionModel)event.getSource()).getSelectedObject());
				selectedTask.setSelected(true);
				
				/*
				 * In Baumstruktur zu selektierter Task springen
				 */
				for(TreeNode tn : tree.getAllNodes()) {
					if(tn.getAttributeAsInt("tier")==OPERATION_TIER
					&& tn.getAttributeAsInt("id")==selectedTask.getUID()) {
						view.getTreeCanvas().deselectAllRecords();
						tree.closeAll();
						
						view.getTreeCanvas().selectRecord(tn);								//Selektieren
						tree.openFolder(tree.getParent(tn));								//Produkt öffnen
						tree.openFolder(tree.getParent(tree.getParent(tn)));				//Auftrag öffnen
						tree.openFolder(tree.getParent(tree.getParent(tree.getParent(tn))));//Hauptordner öffnen
						
						view.getTreeCanvas().scrollToRow((view.getTreeCanvas().getRecordIndex(tn)));
					}
				}
				
				/*
				 * Neuzeichnung
				 */
				view.getGantt().redraw();
			}
		});
		
		/**
		 * Bei Anzeige der Alternativen
		 */
		this.view.getCheckOtherSchedulesButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				alternativeScheduleListPresenter.getView().show();
				alternativeScheduleListPresenter.getView().getChart().setVisible(true);
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
		 * Bei RollUü
		 */
		this.view.getRollUpButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getGantt().zoomOut();
			}
		});
		
		/**
		 * Bei Switch der Anzeige der Konnektoren
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
				 * Aktuellen Ablaufplan holen
				 */
				Schedule currSchedule = ScenarioPresenter.getInstance().getChosenSchedule();
				
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
					selectedOrder = currSchedule.getOrder(ScenarioPresenter.getInstance().getScenario(), clickedNode.getAttributeAsInt("id"));
					
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
					 * Richtige ID herausfinden
					 */
					selectedTask = null;
					for(Task t : view.getDataProvider().getList()) {
						if(t.getUID() == clickedNode.getAttributeAsInt("id")) {
							selectedTask = t;
							break;
						}
					}
					
					/*
					 * Selektierte Operation holen und selektieren (optisch, in Gantt)
					 */
					if(selectedTask != null) {
						selectedTask.setSelected(true);
						view.getGantt().scrollToItem(selectedTask);
					}
					break;
				/*
				 * Andernfalls auf "jetzt" scrollen
				 */
				default:
					view.getGantt().scrollToItem(
							ScenarioPresenter.getInstance().getChosenSchedule().getAllocationNow(ScenarioPresenter.getInstance().getScenario())
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
		
		/**
		 * Bei Click auf Update
		 */
		view.getUpdateGanttButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateClickAction();
			}
		});
	}
	
	public void updateClickAction() {
		/*
		 * Events ausführen, danach Liste leeren
		 */
		for(ScheduleEvent e : ScenarioPresenter.getInstance().getUninvokedScheduleEvents()) {
			/*
			 * Letzten chosenSchedule speichern
			 */
			Schedule lastChosenSchedule = ScenarioPresenter.getInstance().getChosenSchedule();
			
			/*
			 * Scenario ändern
			 */
			e.changeScenario(ScenarioPresenter.getInstance().getScenario());
			
			/*
			 * Schedule in History
			 */
			TabPresenter.getInstance().getScheduleTabPresenter().getScheduleHistoryPresenter().insertIntoListGrid(lastChosenSchedule);
			
			/*
			 * InvokeEvent-View anpassen
			 */
			InvokeEventView invokeEventView = TabPresenter.getInstance().getEventTabPresenter().getInvokeEventPresenter().getView();
			/*
			 * Bei newOrder den entsprechenden Auswahllisten den neuen Auftrag hinzufügen
			 */
			if(e instanceof NewOrdersEvent
			|| e instanceof RemoveOrdersEvent) {
				final LinkedHashMap<String, String> valueMapForOrderChooser = new LinkedHashMap<String, String>();
				
				/*
				 * Bestehende Aufträge 
				 */
		        for(ScheduleOrder order : ScenarioPresenter.getInstance().getScenario().getOrders()) {
		        	valueMapForOrderChooser.put((order.getOrderID()+""), order.getName());
		        }

		        /*
		         * Neue Map verteilen
		         */
				invokeEventView.getOrderChooserForDueTimeComboBoxItem().setValueMap(valueMapForOrderChooser);
				invokeEventView.getOrderChooserForPriorityComboBoxItem().setValueMap(valueMapForOrderChooser);
				invokeEventView.getOrderChooserForRemovement().setValueMap(valueMapForOrderChooser);
				invokeEventView.getOrderChooserForProductChange().setValueMap(valueMapForOrderChooser);
			/*
			 * Bei newProduct den entsprechenden Auswahllisten das neue Produkt hinzufügen
			 */
			} else if(e instanceof NewProductsEvent
				   || e instanceof RemoveProductsEvent) {
				final LinkedHashMap<String, String> valueMapForProductChooser = new LinkedHashMap<String, String>();
				
				/*
				 * Produkt-ListGrid leeren
				 */
				for(ListGridRecord lgr : invokeEventView.getNewOrderLeftListGrid().getRecords()) {
					invokeEventView.getNewOrderLeftListGrid().removeData(lgr);
				}
				
				/*
				 * Bestehende Produkte in Map einfügen
				 * Produkt-ListGrid füllen
				 */
		        for(Product product : new ArrayList<Product>(ScenarioPresenter.getInstance().getScenario().getProducts().values())) {
		        	valueMapForProductChooser.put((product.getProductID()+""), product.getName());
		        	
		        	invokeEventView.getNewOrderLeftListGrid().addData(
		        			TabPresenter.getInstance().getEventTabPresenter().getInvokeEventPresenter().new ProductRecord(product, product.getName()));
		        }
		        
		        /*
		         * Neue Map verteilen
		         */
		        invokeEventView.getProductChooserForRemovement().setValueMap(valueMapForProductChooser);
		        invokeEventView.getProductChooserForVariantRemovementComboBoxItem().setValueMap(valueMapForProductChooser);
		        invokeEventView.getNewVariantProductChooser().setValueMap(valueMapForProductChooser);
		        invokeEventView.getProductChooserForVariantRemovementComboBoxItem().setValueMap(valueMapForProductChooser);
		    } else if(e instanceof NewResourceEvent
		    	   || e instanceof MachineBreakDownEvent
				   || e instanceof MachineRepairedEvent) { 
		        final LinkedHashMap<String, String> valueMapForResourceChooser = new LinkedHashMap<String, String>();
		        ListGridRecord tempRecord;

				final LinkedHashMap<String, String> valueMapForBreakDownResourceChooser = new LinkedHashMap<String, String>();
			    final LinkedHashMap<String, String> valueMapForRepairResourceChooser = new LinkedHashMap<String, String>();
			    
		        /*
		         * Bestehende Ressourcen in Map
		         */
		        for(Resource resource : new ArrayList<Resource>(ScenarioPresenter.getInstance().getScenario().getResources().values())) {
		        	valueMapForResourceChooser.put((resource.getResourceID()+""), resource.getName());
		        	
		        	tempRecord = new ListGridRecord();
		        	tempRecord.setAttribute("name", resource.getName());
		        	tempRecord.setAttribute("isSelected", false);
		        	tempRecord.setAttribute("resource", resource);
		        	invokeEventView.getResourceListGridForNewProduct().addData(tempRecord);
		        	invokeEventView.getResourceListGridForNewVariant().addData(tempRecord);
		        	invokeEventView.getResourceListGridForOperationChange().addData(tempRecord);
		        	
		        	if(resource.isInBreakDown()) {
			    		valueMapForRepairResourceChooser.put((resource.getResourceID()+""), resource.getName());
			    	} else {
			    		valueMapForBreakDownResourceChooser.put((resource.getResourceID()+""), resource.getName());
			    	}
		        }

		        /*
		         * Neue Map verteilen
		         */
		        invokeEventView.getResourceChooserForAvaiabilityComboBoxItem().setValueMap(valueMapForResourceChooser);
		        invokeEventView.getResourceChooserForMaintainacePeriod().setValueMap(valueMapForResourceChooser);
			    invokeEventView.getResourceChooserForMachineBreakdown().setValueMap(valueMapForBreakDownResourceChooser);
			    invokeEventView.getResourceChooserForMachineRepair().setValueMap(valueMapForRepairResourceChooser);
		    /*
		     * Bei MachineBreakdown oder MachineRepair die Auswahllisten jeweils ändern
		     */
			} else if(e instanceof MachineBreakDownEvent
				   || e instanceof MachineRepairedEvent) {
				 final LinkedHashMap<String, String> valueMapForBreakDownResourceChooser = new LinkedHashMap<String, String>();
			     final LinkedHashMap<String, String> valueMapForRepairResourceChooser = new LinkedHashMap<String, String>();
			     
			     
			}
		}
		ScenarioPresenter.getInstance().clearUninvokedScheduleEvents();
		
		/*
		 * Iterative Verbesserung mit neu ausgewähltem Plan füttern
		 */
		TabPresenter.getInstance().getScheduleTabPresenter().getIterativeAdvancementPresenter().fillGanttComponents();
		
		/*
		 * Komponenten aktualisieren
		 */
		updateGanttComponents();
	}
	
	public void updateGanttComponents() {
		/*
		 * Einzelteile neuzeichnen (siehe JDoc der Methoden)
		 */
		fillGanttGrid(ScenarioPresenter.getInstance().getChosenSchedule());
		view.getGantt().redraw();
		showKeyFigures(ScenarioPresenter.getInstance().getScenario().getKeyFigureList());
		showAlgorithmInformation(ScenarioPresenter.getInstance().getChosenSchedule().getAlgorithmInformations());
		fillResourceList(ScenarioPresenter.getInstance().getChosenSchedule());
		fillTree(ScenarioPresenter.getInstance().getChosenSchedule());
		alternativeScheduleListPresenter.update(ScenarioPresenter.getInstance().getScenario().getCurrentSchedules());
		
		/*
		 * Anzeige der Meldung (...other schedules)
		 */
		int altSize = (ScenarioPresenter.getInstance().getScenario().getCurrentSchedules().size()-1);
		view.getCheckOtherSchedulesButton().setTitle("There "+((altSize==1)?"is":"are")+" <b>"+((altSize>0)?(altSize):"no")+"other schedule"+((altSize>1)?("s"):"")+"</b>. Check them.");
		view.getCheckOtherSchedulesButton().setDisabled(!(altSize>0 && UserPresenter.getInstance().isAdvancedUser()));
		
		/*
		 * newEvents resetten
		 */
		resetNewEventsCounter();
	}
	
	/**
	 * Baumstruktur füllen
	 * @param schedule Ablaufplan
	 */
	public void fillTree(Schedule schedule) {
		tree = new Tree();
		final TreeNode root = new TreeNode("Scenario");
		final TreeNode orders = new TreeNode("Orders");
		orders.setAttribute("tier", MAIN_TIER);
		tree.setRoot(root);
		tree.add(orders, root);
		TreeNode orderNode, variantNode, operationNode;
		/*
		 * Für jede Order
		 */
		for(ScheduleOrder o : schedule.getPlannedOrders(ScenarioPresenter.getInstance().getScenario())) {
			orderNode = new TreeNode(o.getName()+ " (id:"+o.getOrderID()+")"); 
			orderNode.setAttribute("tier", ORDER_TIER);
			orderNode.setAttribute("id", o.getOrderID());
			tree.add(orderNode, orders);
			/*
			 * Für jede Variante je Order (Produkt)
			 */
			for(PlannedVariant v : schedule.getPlannedVariantsForOrder(ScenarioPresenter.getInstance().getScenario(), o)) {
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
					operationNode.setAttribute("id", a.getUID());//TODO
					tree.add(operationNode, variantNode);
				}
			}
		}
		
		//FIXME:
		//Nicht verplante Aufträge anzeigen
		
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
		ResourceListRecord[] data = new ResourceListRecord[schedule.getResources
		                                                   (ScenarioPresenter.
		                                                		   getInstance().
		                                                		   getScenario()).
		                                                		   size()];
		
		/*
		 * Resourcen holen und nach ID ordnen
		 */
		List<Resource> resourceList = new ArrayList<Resource>(schedule.getResources(ScenarioPresenter.getInstance().getScenario()).keySet());
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
		view.getDataProvider().setList(GanttManager.visualizeSchedule(ScenarioPresenter.getInstance().getScenario(), schedule));
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
				recordArray[0].setAttribute(keyFigureList.get(i).getName(), keyFigureList.get(i).calculate(ScenarioPresenter.getInstance().getScenario(), ScenarioPresenter.getInstance().getChosenSchedule()));
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
	 * newEvents erhöhen
	 */
	public void increaseNewEventsCounter() {
		this.view.setNewEventsArived(++this.newEventsArrived);
	}
	
	/**
	 * newEvents zurücksetzen
	 */
	public void resetNewEventsCounter() {
		this.newEventsArrived = 0;
		this.view.setNewEventsArived(newEventsArrived);
	}
	
	public CurrentScheduleView getView() {
		return view;
	}
	
	/**
	 * Record für die Resourcenliste
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
