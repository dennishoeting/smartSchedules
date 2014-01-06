package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

import de.bachelor.smartSchedules.client.view.InvokeEventView;
import de.bachelor.smartSchedules.client.view.InvokeEventView.OperationContextMenu;
import de.bachelor.smartSchedules.client.view.InvokeEventView.OperationsInNewVariantListGrid;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.ResourceRestriction;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleOrder;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOperationResourcesEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderDueTimeEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeOrderPriorityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ChangeResourceAvailabilityEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineBreakDownEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MachineRepairedEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.MaintenancePeriodsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewResourceEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.NewVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveOrdersProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveProductsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.RemoveVariantsEvent;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;

/**
 * Presenter für das Werfen eines Ereignisses
 * @author Dennis
 */
public class InvokeEventPresenter {
	
	/**
	 * zugehöriger View
	 */
	private final InvokeEventView view;
	
	/**
	 * Hilfsvariable für aktuelle View-Einstellung
	 */
	private int currentSelection;
	
	/**
	 * Hilfsfeld mit EventTypes
	 */
	private final EventTypeRecord[] eventTypes;
	
	private int newVariantID;
	
	/**
	 * Konstruktor
	 */
	public InvokeEventPresenter() {
		this.view = new InvokeEventView(UserPresenter.getInstance().isAdvancedUser());
		this.currentSelection = -1;
		this.eventTypes = new EventTypeRecord[15];
		
		this.newVariantID = ScenarioPresenter.getInstance().getScenario().getNewIncreasedVariantIDCount();
		
		addEventTypes();
		
		this.addListeners();
	}
	
	/**
	 * Hinzufügen der EventTypes in das zugehörige ListGrid
	 */
	private void addEventTypes() {
		eventTypes[new NewOrdersEvent().getType()] = new EventTypeRecord(new NewOrdersEvent(), "Creation Events");
		eventTypes[new NewProductsEvent().getType()] = new EventTypeRecord(new NewProductsEvent(), "Creation Events");
		eventTypes[new NewVariantsEvent().getType()] = new EventTypeRecord(new NewVariantsEvent(), "Creation Events");
		eventTypes[new NewResourceEvent().getType()] = new EventTypeRecord(new NewResourceEvent(), "Creation Events");
		eventTypes[new ChangeOrderDueTimeEvent().getType()] = new EventTypeRecord(new ChangeOrderDueTimeEvent(), "Order Changes");
		eventTypes[new ChangeOrderPriorityEvent().getType()] = new EventTypeRecord(new ChangeOrderPriorityEvent(), "Order Changes");
		eventTypes[new RemoveOrdersProductsEvent().getType()] = new EventTypeRecord(new RemoveOrdersProductsEvent(), "Order Changes");
		eventTypes[new ChangeOperationResourcesEvent().getType()] = new EventTypeRecord(new ChangeOperationResourcesEvent(), "Order Changes");
		eventTypes[new MachineBreakDownEvent().getType()] = new EventTypeRecord(new MachineBreakDownEvent(), "Resource Events");
		eventTypes[new MachineRepairedEvent().getType()] = new EventTypeRecord(new MachineRepairedEvent(), "Resource Events");
		eventTypes[new MaintenancePeriodsEvent().getType()] = new EventTypeRecord(new MaintenancePeriodsEvent(), "Resource Events");
		eventTypes[new ChangeResourceAvailabilityEvent().getType()] = new EventTypeRecord(new ChangeResourceAvailabilityEvent(), "Resource Events");
		eventTypes[new RemoveOrdersEvent().getType()] = new EventTypeRecord(new RemoveOrdersEvent(), "Removement Events");
		eventTypes[new RemoveProductsEvent().getType()] = new EventTypeRecord(new RemoveProductsEvent(), "Removement Events");
		eventTypes[new RemoveVariantsEvent().getType()] = new EventTypeRecord(new RemoveVariantsEvent(), "Removement Events");
		
		this.view.getEventTypeListGrid().setData(eventTypes);
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		this.view.getSubmitButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Invoke-Button sperren
				 */
				view.getInvokeButton().setDisabled(true);
				
				view.getFileUpload().getScenarioBox().setValue("" +ScenarioPresenter.getInstance().getScenario().getScenarioID());
				view.getFileUpload().getForm().submit();
			}
		});
		
		this.view.getNewProductRightListGrid().addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				final OperationsInNewVariantListGrid grid = InvokeEventPresenter.this.view.getNewProductRightListGrid();
				final OperationContextMenu menu = InvokeEventPresenter.this.view.new OperationContextMenu(grid, (OperationRecord)(event.getRecord()), grid.getPredecessorMap().get(((OperationRecord)(event.getRecord())).getName()));
				menu.getDeleteOperation().addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					@Override
					public void onClick(MenuItemClickEvent event) {
						grid.remove(menu.getRecord());
					}
				});
				for(MenuItem item : menu.getPredecessorsMenu().getItems()) {
					item.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
						@Override
						public void onClick(MenuItemClickEvent event) {
							((MenuItem)event.getSource()).setChecked(!((MenuItem)event.getSource()).getChecked());
							menu.getPredecessorMap().put(((MenuItem)(event.getSource())).getTitle(), ((MenuItem)event.getSource()).getChecked());
							
							List<Operation> predecessorList = new ArrayList<Operation>();
							for(ListGridRecord r : grid.getRecords()) {
								for(String str : menu.getPredecessorMap().keySet()) {
									if(menu.getPredecessorMap().get(str) && str.equals(((OperationRecord)r).getName())) {
										predecessorList.add(((OperationRecord)r).getOperation());
									}
								}
							}
							menu.getRecord().getOperation().setPredecessors(predecessorList);
						}
					});
				}
				((OperationsInNewVariantListGrid)(event.getSource())).setContextMenu(menu);
			}
		});
		
		this.view.getNewVariantRightListGrid().addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				final OperationsInNewVariantListGrid grid = InvokeEventPresenter.this.view.getNewVariantRightListGrid();
				final OperationContextMenu menu = InvokeEventPresenter.this.view.new OperationContextMenu(grid, (OperationRecord)(event.getRecord()), grid.getPredecessorMap().get(((OperationRecord)(event.getRecord())).getName()));
				menu.getDeleteOperation().addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					@Override
					public void onClick(MenuItemClickEvent event) {
						grid.remove(menu.getRecord());
					}
				});
				for(MenuItem item : menu.getPredecessorsMenu().getItems()) {
					item.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
						@Override
						public void onClick(MenuItemClickEvent event) {
							((MenuItem)event.getSource()).setChecked(!((MenuItem)event.getSource()).getChecked());
							menu.getPredecessorMap().put(((MenuItem)(event.getSource())).getTitle(), ((MenuItem)event.getSource()).getChecked());
							
							List<Operation> predecessorList = new ArrayList<Operation>();
							for(ListGridRecord r : grid.getRecords()) {
								for(String str : menu.getPredecessorMap().keySet()) {
									if(menu.getPredecessorMap().get(str) && str.equals(((OperationRecord)r).getName())) {
										predecessorList.add(((OperationRecord)r).getOperation());
									}
								}
							}
							menu.getRecord().getOperation().setPredecessors(predecessorList);
						}
					});
				}
				((OperationsInNewVariantListGrid)(event.getSource())).setContextMenu(menu);
			}
		});
		
		/**
		 * Bei Selektion des Tabs Komponenten füllen
		 */
		this.view.addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				/*
				 * Auftrags-ComboBoxes
				 */
				final LinkedHashMap<String, String> valueMapForOrderChooser = new LinkedHashMap<String, String>();
		        for(ScheduleOrder order : ScenarioPresenter.getInstance().getScenario().getOrders()) {
		        	valueMapForOrderChooser.put((order.getOrderID()+""), order.getName());
		        }
				view.getOrderChooserForDueTimeComboBoxItem().setValueMap(valueMapForOrderChooser);
				view.getOrderChooserForPriorityComboBoxItem().setValueMap(valueMapForOrderChooser);
				view.getOrderChooserForRemovement().setValueMap(valueMapForOrderChooser);
				view.getOrderChooserForProductChange().setValueMap(valueMapForOrderChooser);
				
				/*
				 * Produkt-ComboBoxes und ListGrid
				 */
				final LinkedHashMap<String, String> valueMapForProductChooser = new LinkedHashMap<String, String>();
		        for(Product product : new ArrayList<Product>(ScenarioPresenter.getInstance().getScenario().getProducts().values())) {
		        	valueMapForProductChooser.put((product.getProductID()+""), product.getName());
		      
		        	view.getNewOrderLeftListGrid().addData(new ProductRecord(product, product.getName()));
		        }
		        view.getProductChooserForRemovement().setValueMap(valueMapForProductChooser);
		        view.getProductChooserForVariantRemovementComboBoxItem().setValueMap(valueMapForProductChooser);
		        view.getNewVariantProductChooser().setValueMap(valueMapForProductChooser);
		        view.getProductChooserForOperationChange().setValueMap(valueMapForProductChooser);
		        
		        /*
		         * Resourcen-ComboBoxes und ListGrids
		         */
		        final LinkedHashMap<String, String> valueMapForResourceChooser = new LinkedHashMap<String, String>();
		        final LinkedHashMap<String, String> valueMapForBreakDownResourceChooser = new LinkedHashMap<String, String>();
		        final LinkedHashMap<String, String> valueMapForRepairResourceChooser = new LinkedHashMap<String, String>();
		        ListGridRecord tempRecord;
		        for(Resource resource : new ArrayList<Resource>(ScenarioPresenter.getInstance().getScenario().getResources().values())) {
		        	valueMapForResourceChooser.put((resource.getResourceID()+""), resource.getName());
		        	if(resource.isInBreakDown()) {
		        		valueMapForRepairResourceChooser.put((resource.getResourceID()+""), resource.getName());
		        	} else {
		        		valueMapForBreakDownResourceChooser.put((resource.getResourceID()+""), resource.getName());
		        	}

		        	tempRecord = new ListGridRecord();
		        	tempRecord.setAttribute("name", resource.getName());
		        	tempRecord.setAttribute("isSelected", false);
		        	tempRecord.setAttribute("resource", resource);
		        	view.getResourceListGridForNewProduct().addData(tempRecord);
		        	view.getResourceListGridForNewVariant().addData(tempRecord);
		        	view.getResourceListGridForOperationChange().addData(tempRecord);
		        }
		        view.getResourceChooserForAvaiabilityComboBoxItem().setValueMap(valueMapForResourceChooser);
		        view.getResourceChooserForMaintainacePeriod().setValueMap(valueMapForResourceChooser);
		        view.getResourceChooserForMachineBreakdown().setValueMap(valueMapForBreakDownResourceChooser);
		        view.getResourceChooserForMachineRepair().setValueMap(valueMapForRepairResourceChooser);
			}
		});
		
		/**
		 * Bei Selektion eines EventType entsprechende ViewEinstellung anzeigen
		 */
		this.view.getEventTypeListGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				/*
				 * Wenn valide Selektion, Description füllen und ViewEinstellung vornehmen
				 */
				if(event.getState()) {
					view.getDescriptionWindow().setTitle("Description - " 
							+ ((EventTypeRecord)(event.getSelectedRecord())).getEvent().getName());
					view.getDescriptionPane().setContents(
							((EventTypeRecord)(event.getSelectedRecord())).getEvent().getDescription());
					view.setSpecialInputCanvas(((EventTypeRecord)(event.getSelectedRecord())).getEvent().getType());
					resetAll();
					
					currentSelection = ((EventTypeRecord)(event.getSelectedRecord())).getEvent().getType();
				}
				
				/*
				 * Button deaktivieren, wenn null, ansonsten aktivieren
				 */
				view.getIncludeButton().setDisabled(event.getSelectedRecord() == null);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getThrowImmediatelyCheckboxItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getThrowDateItem().setValue(new Date());
				view.getThrowTimeField().setTime(new Date());
				view.getThrowDateItem().setDisabled((Boolean)event.getValue());
				view.getThrowTimeField().setDisabled((Boolean)event.getValue());
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getProductChooserForVariantRemovementComboBoxItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				final LinkedHashMap<String, String> valueMapForVariantChooser = new LinkedHashMap<String, String>();
		        for(Variant variant : ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf((String)(event.getValue()))).getVariants()) {
		        	valueMapForVariantChooser.put((variant.getVariantID()+""), ("Variant " + variant.getVariantID()));
		        }
		        view.getVariantChooserForRemovement().setValueMap(valueMapForVariantChooser);
		        
		        view.getVariantChooserForRemovement().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getOrderChooserForDueTimeComboBoxItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Date dueTime = ScenarioPresenter.getInstance().getScenario().getOrder(Integer.valueOf(event.getValue().toString())).getEarlistDueTime();
				view.getOrderDueDateItem().setValue(dueTime);
				view.getOrderDueTimeField().setTime(dueTime);
				view.getOrderDueDateItem().setDisabled(false);
				view.getOrderDueTimeField().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getResourceChooserForMaintainacePeriod().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getMaintenaceStartDateItem().setValue(new Date());
				view.getMaintenaceStartTimeField().setTime(new Date());
				view.getMaintenaceDueDateItem().setValue(new Date(new Date().getTime() + 1800000));
				view.getMaintenaceDueTimeField().setTime(new Date(new Date().getTime() + 1800000));
				view.getMaintenaceStartDateItem().setDisabled(false);
				view.getMaintenaceStartTimeField().setDisabled(false);
				view.getMaintenaceDueDateItem().setDisabled(false);
				view.getMaintenaceDueTimeField().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getOrderChooserForPriorityComboBoxItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getPriorityChooser().setValue(
						ScenarioPresenter.getInstance().getScenario().getOrder(
								Integer.valueOf((String)event.getValue())
						).getPriority()
						
				);
				view.getPriorityChooser().setDisabled(false);
			}
		});
		
		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getResourceChooserForAvaiabilityComboBoxItem().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getResourceAwailabilitySlider().setValue(
						ScenarioPresenter.getInstance().getScenario().getResources()
							.get(Integer.valueOf(event.getValue().toString()))
							.getAvailability()	
				);
				view.getResourceAwailabilitySlider().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getOrderChooserForProductChange().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				view.getChangeOrderProductLeftListGrid().setData(new Record[]{});
				for(Product p : ScenarioPresenter.getInstance().getScenario().getOrder(Integer.valueOf(event.getValue().toString())).getProducts(ScenarioPresenter.getInstance().getScenario())) {
					view.getChangeOrderProductLeftListGrid().addData(new ProductRecord(p, p.getName()));
				}
				view.getChangeOrderProductLeftListGrid().setDisabled(false);
				view.getChangeOrderProductRightListGrid().setDisabled(false);
				view.getChangeOrderProductArrowRight().setDisabled(false);
				view.getChangeOrderProductArrowLeft().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getProductChooserForOperationChange().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
				for(Variant v : ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf(view.getProductChooserForOperationChange().getValueAsString())).getVariants()) {
					valueMap.put(v.getVariantID()+"", "Variant"+v.getVariantID());
				}
				view.getVariantChooserForOperationChange().setValueMap(valueMap);
				view.getVariantChooserForOperationChange().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getVariantChooserForOperationChange().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
				for(Operation o : ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf(view.getProductChooserForOperationChange().getValueAsString())).getVariant(Integer.valueOf(view.getVariantChooserForOperationChange().getValueAsString())).getOperations()) {
					valueMap.put(o.getOperationID()+"", o.getName());
				}
				view.getOperationChooserForChange().setValueMap(valueMap);
				view.getOperationChooserForChange().setDisabled(false);
			}
		});

		/**
		 * Bei Wechsel von Auswahl entsprechende Komponenten füllen und aktivieren
		 */
		this.view.getOperationChooserForChange().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Operation selectedOperation = ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf(view.getProductChooserForOperationChange().getValueAsString())).getVariant(Integer.valueOf(view.getVariantChooserForOperationChange().getValueAsString())).getOperation(Integer.valueOf(view.getOperationChooserForChange().getValueAsString()));
				for(ListGridRecord r : view.getResourceListGridForOperationChange().getRecords()) {
					r.setAttribute("isSelected", false);
					for(Integer resID : selectedOperation.getResourceAlternatives()) {
						if(((Resource)(r.getAttributeAsObject("resource"))).getResourceID() == resID) {
							r.setAttribute("isSelected", true);
						}
					}
				}
				view.getDurationSpinnerForOperationChange().setValue(selectedOperation.getDuration());
				view.getResourceListGridForOperationChange().redraw();
				view.getDurationSpinnerForOperationChange().setDisabled(false);
				view.getResourceListGridForOperationChange().setDisabled(false);
			}
		});
		
		/**
		 * Datentransfer
		 */
		this.view.getChangeOrderProductArrowRight().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getChangeOrderProductRightListGrid().transferSelectedData(view.getChangeOrderProductLeftListGrid());
			}
		});

		/**
		 * Datentransfer
		 */
		this.view.getChangeOrderProductArrowLeft().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getChangeOrderProductLeftListGrid().transferSelectedData(view.getChangeOrderProductRightListGrid());
			}
		});

		/**
		 * Datentransfer
		 */
		this.view.getChangeOrderProductLeftListGrid().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				view.getChangeOrderProductRightListGrid().transferSelectedData(view.getChangeOrderProductLeftListGrid());
			}
		});

		/**
		 * Datentransfer
		 */
		this.view.getChangeOrderProductRightListGrid().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				view.getChangeOrderProductLeftListGrid().transferSelectedData(view.getChangeOrderProductRightListGrid());
			}
		});

		/**
		 * Datentransfer
		 */
		this.view.getNewOrderArrow().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getNewOrderRightListGrid().transferSelectedData(view.getNewOrderLeftListGrid());
			}
		});

		/**
		 * Datentransfer
		 */
		this.view.getNewOrderLeftListGrid().addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				view.getNewOrderRightListGrid().transferSelectedData(view.getNewOrderLeftListGrid());	
			}
		});
		
		/**
		 * DurationSpinner-Wertabfangen
		 */
		this.view.getNewOperationInNewProductDurationSpinner().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(Integer.valueOf(event.getValue().toString()) > 0) {
					view.getNewOperationInNewProductDurationSpinner().setValue(event.getValue());
				} else {
					view.getNewOperationInNewProductDurationSpinner().setValue(1);
				}
			}
		});
		
		/**
		 * DurationSpinner-Wertabfangen
		 */
		this.view.getNewOperationDurationSpinner().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(Integer.valueOf(event.getValue().toString()) > 0) {
					view.getNewOperationDurationSpinner().setValue(event.getValue());
				} else {
					view.getNewOperationDurationSpinner().setValue(1);
				}
			}
		});
		
		/**
		 * DurationSpinner-Wertabfangen
		 */
		this.view.getDurationSpinnerForOperationChange().addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if(Integer.valueOf(event.getValue().toString()) > 0) {
					view.getDurationSpinnerForOperationChange().setValue(event.getValue());
				} else {
					view.getDurationSpinnerForOperationChange().setValue(1);
				}
			}
		});
		
		/**
		 * Neue Operation für initiale Variante in neuem Produkt erstellen
		 */
		this.view.getNewProductArrow().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Selektierte Resourcen als Alternativresourcen
				 */
				List<Integer> resourceIDList = new ArrayList<Integer>();
				for(Record lgr : view.getResourceListGridForNewProduct().getDataAsRecordList().toArray()) {
					if(lgr.getAttributeAsBoolean("isSelected")) {
						resourceIDList.add(((Resource) lgr.getAttributeAsObject("resource")).getResourceID());
					}
				}
				
				/*
				 * Neue Operation in ListGrid einfügen
				 */
				if(view.getNewOperationInNewProductName().getValue() != null
						&& !view.getNewOperationInNewProductName().getValueAsString().equals("")
						&& resourceIDList.size()>0
						&& view.getNewOperationInNewProductDurationSpinner().getValue() != null
						&& Integer.valueOf(view.getNewOperationInNewProductDurationSpinner().getValueAsString()) > 0) {
					view.getNewProductRightListGrid().addData(
							new OperationRecord(
									new Operation(ScenarioPresenter.getInstance().getScenario().getScenarioID(),
											ScenarioPresenter.getInstance().getScenario().getNewIncreasedOperationIDCount(), 
											resourceIDList, 
											Integer.valueOf(view.getNewOperationInNewProductDurationSpinner().getValueAsString()), 
											newVariantID,
											view.getNewOperationInNewProductName().getValueAsString()),
									view.getNewOperationInNewProductName().getValueAsString()							)
					);
					
					/*
					 * Alles resetten
					 */
					view.getNewOperationInNewProductName().setValue("");
					for(ListGridRecord lgr : view.getResourceListGridForNewProduct().getRecords()) {
						lgr.setAttribute("isSelected", false);
					}
					view.getResourceListGridForNewProduct().deselectAllRecords();
					view.getResourceListGridForNewProduct().redraw();
					view.getNewOperationInNewProductDurationSpinner().setValue(0);
				} else {
					SC.say("Input error");
				}
			}
		});
		
		/**
		 * Neue Operation für neue Variante in bestehendem Produkt erstellen
		 */
		this.view.getNewVariantArrow().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Selektierte Resourcen als Alternativresourcen
				 */
				List<Integer> resourceIDList = new ArrayList<Integer>();
				for(Record lgr : view.getResourceListGridForNewVariant().getDataAsRecordList().toArray()) {
					if(lgr.getAttributeAsBoolean("isSelected")) {
						resourceIDList.add(((Resource) lgr.getAttributeAsObject("resource")).getResourceID());
					}
				}
				
				/*
				 * Neue Operation in ListGrid einfügen
				 */
				if(view.getNewOperationName().getValue() != null
				&& !view.getNewOperationName().getValueAsString().equals("")
				&& resourceIDList.size()>0
				&& view.getNewOperationDurationSpinner().getValue() != null
				&& Integer.valueOf(view.getNewOperationDurationSpinner().getValueAsString()) > 0) {
					view.getNewVariantRightListGrid().addData(
						new OperationRecord(
								new Operation(ScenarioPresenter.getInstance().getScenario().getScenarioID(),
										ScenarioPresenter.getInstance().getScenario().getNewIncreasedOperationIDCount(), 
										resourceIDList, 
										Integer.valueOf(view.getNewOperationDurationSpinner().getValueAsString()), 
										newVariantID,
										view.getNewOperationName().getValueAsString()),
								view.getNewOperationName().getValueAsString())
					);

					/*
					 * Alles resetten
					 */
					view.getNewOperationName().setValue("");
					for(ListGridRecord lgr : view.getResourceListGridForNewVariant().getRecords()) {
						lgr.setAttribute("isSelected", false);
					}
					view.getResourceListGridForNewVariant().deselectAllRecords();
					view.getResourceListGridForNewVariant().redraw();
					view.getNewOperationDurationSpinner().setValue(0);
				} else {
					SC.say("Input error");
				}
			}
		});
		
		/**
		 * Wechsel in CheckboxItem in entsprechendem Record mit Resourcen (NewProductEvent)
		 */
		this.view.getResourceListGridForNewProduct().addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				event.getRecord().setAttribute("isSelected", !event.getRecord().getAttributeAsBoolean("isSelected"));
				view.getResourceListGridForNewProduct().redraw();
			}
		});

		/**
		 * Wechsel in CheckboxItem in entsprechendem Record mit Resourcen (NewVariantEvent)
		 */
		this.view.getResourceListGridForNewVariant().addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				event.getRecord().setAttribute("isSelected", !event.getRecord().getAttributeAsBoolean("isSelected"));
				view.getResourceListGridForNewVariant().redraw();
			}
		});

		/**
		 * Wechsel in CheckboxItem in entsprechendem Record mit Resourcen (OperationChange)
		 */
		this.view.getResourceListGridForOperationChange().addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				event.getRecord().setAttribute("isSelected", !event.getRecord().getAttributeAsBoolean("isSelected"));
				view.getResourceListGridForOperationChange().redraw();
			}
		});
		
		/**
		 * Bei Selektierung eines Events in rechter ListGrid entsprechend Details anzeigen
		 */
		this.view.getSelectedEventsListGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				if(event.getState()) {
					view.visualizeEventDetails((EventsToInvokeRecord) event.getSelectedRecord());
				}
			}
		});
		
		/**
		 * Einfügen des entsprechenden Events in rechtes ListGrid
		 * Fallunterscheidung nach Eventtyp
		 */
		this.view.getIncludeButton().addClickHandler(new ClickHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Behandlung der ThrowTime (immediately oder nach Einstellung)
				 */
				Date throwTime;
				if((Boolean)(view.getThrowImmediatelyCheckboxItem().getValue())) {
					throwTime = new Date();
				} else {
					throwTime = view.getThrowDateItem().getValueAsDate();
					throwTime.setHours(view.getThrowTimeField().getHours());
					throwTime.setMinutes(view.getThrowTimeField().getMinutes());
					throwTime.setSeconds(view.getThrowTimeField().getSeconds());
				}
								
				if(throwTime.before(new Date())) {
					SC.say("Throw time in the past not possible");
					return;
				}
				
				/*
				 * Erstellung der Events mit Fallunterscheidung
				 */
				switch(currentSelection) {
				/*
				 * NewOrdersEvent
				 */
				case ScheduleEvent.TYPE_NEW_ORDERS_EVENT:
					/*
					 * Hilfsliste für Produkte in neuer Order 
					 */
					List<Product> productsForNewOrder = new ArrayList<Product>();
					for(ListGridRecord r : view.getNewOrderRightListGrid().getRecords()) {
						ProductRecord pr = (ProductRecord)r;
						productsForNewOrder.add(pr.getProduct());
					}
					
					/*
					 * Randfallabsicherung
					 */
					if(view.getNewOrderTextItem().getValue()!=null
					&& !view.getNewOrderTextItem().getValueAsString().equals("")
					&& view.getNewOrderDueDateItem().getValue()!=null
					&& view.getNewOrderDueTimeField().getTime()>0
					&& !productsForNewOrder.isEmpty()) {
						/*
						 * EDD aus Eingaben errechnen
						 */
						Date edd = view.getNewOrderDueDateItem().getValueAsDate();
						edd.setHours(view.getNewOrderDueTimeField().getHours());
						edd.setMinutes(view.getNewOrderDueTimeField().getMinutes());
						edd.setSeconds(view.getNewOrderDueTimeField().getSeconds());
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new NewOrdersEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
									new ScheduleOrder(
										ScenarioPresenter.getInstance().getScenario().getNewIncreasedOrderIDCount(),
										ScenarioPresenter.getInstance().getScenario().getScenarioID(),
										productsForNewOrder,
										view.getNewOrderTextItem().getValueAsString(),
										throwTime,
										edd,
										Integer.valueOf(view.getNewOrderPriorityChooser().getValueAsString().substring(0,1))),
										throwTime
								),
								currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alle Felder resetten
						 */
						view.getNewOrderTextItem().setValue("");
						view.getNewOrderPriorityChooser().setValue("1");
						view.getNewOrderDueDateItem().setValue(new Date());
						view.getNewOrderDueTimeField().setTime(new Date());
						for(int i=view.getNewOrderRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
							view.getNewOrderRightListGrid().removeData(view.getNewOrderRightListGrid().getDataAsRecordList().get(i));
						}
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * NewProductsEvent
				 */
				case ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT: 
					/*
					 * Hilfsliste mit Operationen (initiale Variante) für Produkt
					 */
					List<Operation> operationsForInitialVariant = new ArrayList<Operation>();
					for(ListGridRecord r : view.getNewProductRightListGrid().getRecords()) {
						OperationRecord or = (OperationRecord)r;
						operationsForInitialVariant.add(or.getOperation());
					}
					
					/*
					 * Randfallabsicherung
					 */
					if(view.getNewProductTextItem().getValue()!=null
					&& !view.getNewProductTextItem().getValueAsString().equals("")
					&& !operationsForInitialVariant.isEmpty()) {
						/*
						 * Produkt erstellen
						 */
						Product p = new Product(ScenarioPresenter.getInstance().getScenario().getScenarioID(),
											ScenarioPresenter.getInstance().getScenario().getNewIncreasedProductIDCount(),
											view.getNewProductTextItem().getValueAsString());
						p.addVariant(new Variant(newVariantID, operationsForInitialVariant, p));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new NewProductsEvent(
										ScenarioPresenter.getInstance().getScenario().getScenarioID(),
										p,
										throwTime),
									currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getNewProductTextItem().setValue("");
						view.getNewOperationInNewProductName().setValue("");
						for(ListGridRecord lgr : view.getResourceListGridForNewProduct().getRecords()) {
							lgr.setAttribute("isSelected", false);
						}
						view.getResourceListGridForNewProduct().deselectAllRecords();
						view.getResourceListGridForNewProduct().redraw();
						view.getNewOperationInNewProductDurationSpinner().setValue(0);
						for(int i=view.getNewProductRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
							view.getNewProductRightListGrid().removeData(view.getNewProductRightListGrid().getDataAsRecordList().get(i));
						}
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * NewVariantsEvent
				 */
				case ScheduleEvent.TYPE_NEW_VARIANTS_EVENT: 
					/*
					 * Hilfsliste mit Operationen für neue Variante
					 */
					List<Operation> operationsForNewVariant = new ArrayList<Operation>();
					for(ListGridRecord r : view.getNewVariantRightListGrid().getRecords()) {
						OperationRecord or = (OperationRecord)r;
						operationsForNewVariant.add(or.getOperation());
					}
					
					/*
					 * Randfallabsicherung
					 */
					if(view.getNewVariantProductChooser().getValue()!=null
					&& !view.getNewVariantProductChooser().getValueAsString().equals("")
					&& !operationsForNewVariant.isEmpty()) {
						/*
						 * Produkt holen
						 */
						Product product = ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf(view.getNewVariantProductChooser().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new NewVariantsEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(),
									new Variant(
										newVariantID, 
										operationsForNewVariant, 
										product), 
										throwTime),
								currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getNewVariantProductChooser().setValue("");
						view.getNewOperationName().setValue("");
						for(ListGridRecord lgr : view.getResourceListGridForNewVariant().getRecords()) {
							lgr.setAttribute("isSelected", false);
						}
						view.getResourceListGridForNewVariant().deselectAllRecords();
						view.getResourceListGridForNewVariant().redraw();
						view.getNewOperationDurationSpinner().setValue(0);
						for(int i=view.getNewVariantRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
							view.getNewVariantRightListGrid().removeData(view.getNewVariantRightListGrid().getDataAsRecordList().get(i));
						}
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * NewResourceEvent
				 */
				case ScheduleEvent.TYPE_NEW_RESOURCE_EVENT:
					/*
					 * Randfallabsicherung
					 */
					if(view.getNewResourceTextItem().getValue() != null
					&& !view.getNewResourceTextItem().getValueAsString().equals("")) {
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new NewResourceEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
									new Resource(ScenarioPresenter.getInstance().getScenario().getScenarioID(),
										ScenarioPresenter.getInstance().getScenario().getNewIncreasedResourceIDCount(), 
										view.getNewResourceTextItem().getValueAsString(), 
										(int)view.getResourceAwailabilitySlider2().getValue()), 
									throwTime), 
								currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getNewResourceTextItem().setValue("");
						view.getResourceAwailabilitySlider2().setValue(100);
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * ChangeOrdersDueTimesEvent
				 */
				case ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT: 
					/*
					 * DueTime aus Eingaben errechnen
					 */
					Date newDueDate = view.getOrderDueDateItem().getValueAsDate();
					newDueDate.setHours(view.getOrderDueTimeField().getHours());
					newDueDate.setMinutes(view.getOrderDueTimeField().getMinutes());
					newDueDate.setSeconds(view.getOrderDueTimeField().getSeconds());
					
					/*
					 * Randfallabsicherung
					 */
					if(view.getOrderChooserForDueTimeComboBoxItem().getValue()!=null
					&& newDueDate.after(new Date())) {
						ScheduleOrder order = ScenarioPresenter.getInstance().getScenario().getOrder(Integer.valueOf(view.getOrderChooserForDueTimeComboBoxItem().getValueAsString())).clone();
						order.setEarlistDueTime(newDueDate);
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new ChangeOrderDueTimeEvent(
										ScenarioPresenter.getInstance().getScenario().getScenarioID(),
										order,
										throwTime),
								currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getOrderChooserForDueTimeComboBoxItem().setValue("");
						view.getOrderDueDateItem().setValue(new Date());
						view.getOrderDueTimeField().setTime(new Date());
						view.getOrderDueDateItem().setDisabled(true);
						view.getOrderDueTimeField().setDisabled(true);
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * ChangeResourceAvaiabilityEvent
				 */
				case ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT:
					/*
					 * Randfallabsicherung
					 */
					if(view.getResourceChooserForAvaiabilityComboBoxItem().getValue() != null) {
						/*
						 * Resource erstellen
						 */
						Resource r = ScenarioPresenter.getInstance().getScenario().getResources().get(Integer.valueOf(view.getResourceChooserForAvaiabilityComboBoxItem().getValueAsString())).clone();
						
						if(r.getAvailability() == (int)(view.getResourceAwailabilitySlider().getValue())) {
							/*
							 * Fehlerausgabe als Warnung, wenn Slider nicht verändert wurde
							 */
							SC.say("No changes performed");
						} else {
							/*
							 * Ansonsten Avaiability ändern
							 */
							r.setAvailability((int)(view.getResourceAwailabilitySlider().getValue()));
							
							/*
							 * Event erstellen und hinzufügen
							 */
							view.getSelectedEventsListGrid().addData(
								new EventsToInvokeRecord(
									new ChangeResourceAvailabilityEvent(
											ScenarioPresenter.getInstance().getScenario().getScenarioID(),
											r,
											throwTime), 
									currentSelection)
							);
							
							/*
							 * Nix anzeigen und Selektion aufheben
							 */
							view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
							view.getEventTypeListGrid().deselectAllRecords();
							
							/*
							 * Alles resetten
							 */
							view.getResourceChooserForAvaiabilityComboBoxItem().setValue("");
							view.getResourceAwailabilitySlider().setValue(0);
							view.getResourceAwailabilitySlider().setDisabled(true);
						}
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * RemoveOrdersProductsEvent
				 */
				case ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT: 
					/*
					 * Randfallabsicherung
					 */
					if(view.getOrderChooserForProductChange().getValue() != null) {
						if(view.getChangeOrderProductRightListGrid().getDataAsRecordList().isEmpty()) {
							/*
							 * Fehlerausgabe als Warnung wenn keine Produkte zum Löschen hinzugefügt
							 */
							SC.say("No products to delete selected");
						} else {
							/*
							 * Ansonsten Hilfsliste mit Produkten erstellen
							 */
							List<Product> productsToRemove = new ArrayList<Product>();
							for(ListGridRecord lgr : view.getChangeOrderProductRightListGrid().getRecords()) {
								ProductRecord pr = (ProductRecord) lgr;
								productsToRemove.add(pr.getProduct());
							}

							/*
							 * Event erstellen und hinzufügen
							 */
							view.getSelectedEventsListGrid().addData(
								new EventsToInvokeRecord(
									new RemoveOrdersProductsEvent(
										ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
										ScenarioPresenter.getInstance().getScenario().getOrder(Integer.valueOf(view.getOrderChooserForProductChange().getValueAsString())), 
										productsToRemove, 
										throwTime), 
									currentSelection)
							);
							/*
							 * Nix anzeigen und Selektion aufheben
							 */
							view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
							view.getEventTypeListGrid().deselectAllRecords();
							
							/*
							 * Alles resetten
							 */
							view.getOrderChooserForProductChange().setValue("");
							for(int i=view.getChangeOrderProductLeftListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
								view.getChangeOrderProductLeftListGrid().removeData(view.getChangeOrderProductLeftListGrid().getDataAsRecordList().get(i));
							}
							view.getChangeOrderProductLeftListGrid().setDisabled(true);
							for(int i=view.getChangeOrderProductRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
								view.getChangeOrderProductRightListGrid().removeData(view.getChangeOrderProductRightListGrid().getDataAsRecordList().get(i));
							}
							view.getChangeOrderProductRightListGrid().setDisabled(true);
							view.getChangeOrderProductArrowLeft().setDisabled(true);
							view.getChangeOrderProductArrowRight().setDisabled(true);
						}
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * ChangeOrdersPriorityEvent
				 */
				case ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT: 
					/*
					 * Randfallabsicherung
					 */
					if(view.getOrderChooserForPriorityComboBoxItem().getValue() != null
					&& view.getPriorityChooser().getValue() != null) {
						/*
						 * Auftrag erstellen
						 */
						ScheduleOrder order = ScenarioPresenter.getInstance().getScenario().getOrder(Integer.valueOf(view.getOrderChooserForPriorityComboBoxItem().getValueAsString())).clone();
						order.setPriority(Integer.valueOf(view.getPriorityChooser().getValueAsString().substring(0,1)));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new ChangeOrderPriorityEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(),
									order,
									throwTime),
							currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getOrderChooserForPriorityComboBoxItem().setValue("");
						view.getPriorityChooser().setValue("1");
						view.getPriorityChooser().setDisabled(true);
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * ChangeOperationResourceEvent
				 */
				case ScheduleEvent.TYPE_CHANGE_OPERATION_RESOURCES_EVENT:
					List<Integer> resourcesForChangedOperation = new ArrayList<Integer>();
					for(ListGridRecord r : view.getResourceListGridForOperationChange().getRecords()) {
						if(r.getAttributeAsBoolean("isSelected")) {
							resourcesForChangedOperation.add(((Resource) r.getAttributeAsObject("resource")).getResourceID());
						}
					}
					
					if(view.getOperationChooserForChange().getValue()!=null
					&& Integer.valueOf(view.getDurationSpinnerForOperationChange().getValueAsString())>0
					&& resourcesForChangedOperation.size()>0) {
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new ChangeOperationResourcesEvent(
										ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
										Integer.valueOf(view.getProductChooserForOperationChange().getValueAsString()),
										Integer.valueOf(view.getVariantChooserForOperationChange().getValueAsString()),
										Integer.valueOf(view.getOperationChooserForChange().getValueAsString()),
										Integer.valueOf(view.getDurationSpinnerForOperationChange().getValueAsString()),
										resourcesForChangedOperation,
										throwTime), 
								currentSelection)
							);
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getProductChooserForOperationChange().setValue("");
						view.getVariantChooserForOperationChange().setValue("");
						view.getVariantChooserForOperationChange().setDisabled(true);
						view.getOperationChooserForChange().setValue("");
						view.getOperationChooserForChange().setDisabled(true);
						view.getDurationSpinnerForOperationChange().setValue(0);
						view.getDurationSpinnerForOperationChange().setDisabled(true);
						for(ListGridRecord lgr : view.getResourceListGridForOperationChange().getRecords()) {
							lgr.setAttribute("isSelected", false);
						}
						view.getResourceListGridForOperationChange().deselectAllRecords();
						view.getResourceListGridForOperationChange().redraw();
						view.getResourceListGridForOperationChange().setDisabled(true);
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * MachineBreakDownsEvent
				 */
				case ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT: 
					/*
					 * Randfallabsicherung
					 */
					if(view.getResourceChooserForMachineBreakdown().getValue() != null) {
						/*
						 * Resource erstellen
						 */
						Resource r = ScenarioPresenter.getInstance().getScenario().getResources().get(Integer.valueOf(view.getResourceChooserForMachineBreakdown().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new MachineBreakDownEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
									r, 
									throwTime), 
							currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getResourceChooserForMachineBreakdown().setValue("");
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * MachinesRepairedEvent
				 */
				case ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT:
					/*
					 * Randfallabsicherung
					 */
					if(view.getResourceChooserForMachineRepair().getValue() != null) {
						/*
						 * Resource erstellen
						 */
						Resource r = ScenarioPresenter.getInstance().getScenario().getResources().get(Integer.valueOf(view.getResourceChooserForMachineRepair().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new MachineRepairedEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
									r, 
									throwTime), 
							currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getResourceChooserForMachineRepair().setValue("");
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * MaintenacePeriodsEvent
				 */
				case ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT: 
					/*
					 * Start aus Eingaben errechnen
					 */
					Date startDate = view.getMaintenaceStartDateItem().getValueAsDate();
					startDate.setHours(view.getMaintenaceStartTimeField().getHours());
					startDate.setMinutes(view.getMaintenaceStartTimeField().getMinutes());
					startDate.setSeconds(view.getMaintenaceStartTimeField().getSeconds());
					
					/*
					 * Ende aus Eingaben errechnen
					 */
					Date dueDate = view.getMaintenaceDueDateItem().getValueAsDate();
					dueDate.setHours(view.getMaintenaceDueTimeField().getHours());
					dueDate.setMinutes(view.getMaintenaceDueTimeField().getMinutes());
					dueDate.setSeconds(view.getMaintenaceDueTimeField().getSeconds());
					
					/*
					 * Randfallabsicherung
					 */
					if(view.getResourceChooserForMaintainacePeriod().getValue() != null
					&& startDate.after(new Date())
					&& dueDate.after(new Date())
					&& startDate.before(dueDate)) {
						/*
						 * Resource erstellen
						 */
						Resource r = ScenarioPresenter.getInstance().getScenario().getResources().get(Integer.valueOf(view.getResourceChooserForMaintainacePeriod().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new MaintenancePeriodsEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
									new ResourceRestriction(ScenarioPresenter.getInstance().getScenario().getScenarioID(),
										startDate, 
										dueDate, 
										r.getResourceID()),
									throwTime), 
								ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getResourceChooserForMaintainacePeriod().setValue("");
						view.getMaintenaceDueDateItem().setValue(new Date());
						view.getMaintenaceDueDateItem().setDisabled(true);
						view.getMaintenaceDueTimeField().setTime(new Date());
						view.getMaintenaceDueTimeField().setDisabled(true);
						view.getMaintenaceStartDateItem().setValue(new Date());
						view.getMaintenaceStartDateItem().setDisabled(true);
						view.getMaintenaceStartTimeField().setTime(new Date());
						view.getMaintenaceStartTimeField().setDisabled(true);
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * RemoveOrdersEvent
				 */
				case ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT: 
					/*
					 * Randfallabsicherung
					 */
					if(view.getOrderChooserForRemovement().getValue() != null) {
						/*
						 * Auftrag erstellen
						 */
						ScheduleOrder order = ScenarioPresenter.getInstance().getScenario().getOrder(Integer.valueOf(view.getOrderChooserForRemovement().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new RemoveOrdersEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(),
									order,
									throwTime),
							currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getOrderChooserForRemovement().setValue("");
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * RemoveProductsEvent
				 */
				case ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT: 
					/*
					 * Randfallabsicherung
					 */
					if(view.getProductChooserForRemovement().getValue() != null) {
						/*
						 * Produkt erstellen
						 */
						Product product = ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf(view.getProductChooserForRemovement().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new RemoveProductsEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(),
									product,
									throwTime),
							currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getProductChooserForRemovement().setValue("");
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				/*
				 * RemoveVariantsEvent
				 */
				case ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT: 
					/*
					 * Randfallabsicherung
					 */
					if(view.getProductChooserForVariantRemovementComboBoxItem().getValue() != null
					&& view.getVariantChooserForRemovement().getValue() != null) {
						/*
						 * Produkt und zu löschende Variante erstellen
						 */
						Product product = ScenarioPresenter.getInstance().getScenario().getProduct(Integer.valueOf(view.getProductChooserForVariantRemovementComboBoxItem().getValueAsString()));
						Variant variantToRemove = product.getVariant(Integer.valueOf(view.getVariantChooserForRemovement().getValueAsString()));
						
						/*
						 * Event erstellen und hinzufügen
						 */
						view.getSelectedEventsListGrid().addData(
							new EventsToInvokeRecord(
								new RemoveVariantsEvent(
									ScenarioPresenter.getInstance().getScenario().getScenarioID(), 
									variantToRemove, 
									throwTime), 
							currentSelection)
						);
						
						/*
						 * Nix anzeigen und Selektion aufheben
						 */
						view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						view.getEventTypeListGrid().deselectAllRecords();
						
						/*
						 * Alles resetten
						 */
						view.getProductChooserForVariantRemovementComboBoxItem().setValue("");
						view.getVariantChooserForRemovement().setValue("");
						view.getVariantChooserForRemovement().setDisabled(true);
					} else {
						/*
						 * Fehlerausgabe als Warnung
						 */
						SC.say("Input error");
					}
					break;
				}
			}
		});
		
		/*
		 * Abschicken der Events
		 */
		view.getInvokeButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * Hilfsliste mit Events
				 */
				List<ScheduleEvent> eventsToInvoke = new ArrayList<ScheduleEvent>();
				for(ListGridRecord r : view.getSelectedEventsListGrid().getRecords()) {
					eventsToInvoke.add(((EventsToInvokeRecord)r).getEvent());
				}
				
				/*
				 * Wenn Events zum werfen da sind
				 */
				if(eventsToInvoke.size() > 0) {
					/*
					 * Stelle Serververbindung her
					 */
					ServerCommunicationsManager.getInstance().addScheduleEvents(eventsToInvoke, new AsyncCallback<Void>() {
						@Override
						/*
						 * Bei Fehlschlag: Fehler als Hinweis
						 */
						public void onFailure(Throwable caught) {
							SC.say("Events not invoked.</br></br>"+caught.getMessage());
						}
	
						@Override
						/*
						 * Bei Erfolg, Meldung als Hinweis
						 */
						public void onSuccess(Void result) {
							SC.say("Events invoked!</br></br><b>Note:</b> Be aware that you may have to update the schedule in current schedule view.");
							
							/*
							 * ListGrid leeren
							 */
							for(Record r : view.getSelectedEventsListGrid().getDataAsRecordList().toArray()) {
								view.getSelectedEventsListGrid().removeData(r);
							}

							view.getEventTypeListGrid().deselectAllRecords();
							view.setSpecialInputCanvas(InvokeEventView.NO_SELECTION);
						}
					});
				} else {
					SC.say("No records to invoke.");
				}
			}
		});
		
		/*
		 * VariantID erhöhen
		 */
		newVariantID = ScenarioPresenter.getInstance().getScenario().getNewVariantIDCount();
	}

	private void resetAll() {
		/*
		 * NewOrder
		 */
		view.getNewOrderTextItem().setValue("");
		view.getNewOrderPriorityChooser().setValue("1");
		view.getNewOrderDueDateItem().setValue(new Date());
		view.getNewOrderDueTimeField().setTime(new Date());
		for(int i=view.getNewOrderRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
			view.getNewOrderRightListGrid().removeData(view.getNewOrderRightListGrid().getDataAsRecordList().get(i));
		}
		
		/*
		 * NewProduct
		 */
		view.getNewProductTextItem().setValue("");
		view.getNewOperationInNewProductName().setValue("");
		for(ListGridRecord lgr : view.getResourceListGridForNewProduct().getRecords()) {
			lgr.setAttribute("isSelected", false);
		}
		view.getResourceListGridForNewProduct().deselectAllRecords();
		view.getResourceListGridForNewProduct().redraw();
		view.getNewOperationInNewProductDurationSpinner().setValue(0);
		for(int i=view.getNewProductRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
			view.getNewProductRightListGrid().removeData(view.getNewProductRightListGrid().getDataAsRecordList().get(i));
		}
		
		/*
		 * NewVariant
		 */
		view.getNewVariantProductChooser().setValue("");
		view.getNewOperationName().setValue("");
		for(ListGridRecord lgr : view.getResourceListGridForNewVariant().getRecords()) {
			lgr.setAttribute("isSelected", false);
		}
		view.getResourceListGridForNewVariant().deselectAllRecords();
		view.getResourceListGridForNewVariant().redraw();
		view.getNewOperationDurationSpinner().setValue(0);
		for(int i=view.getNewVariantRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
			view.getNewVariantRightListGrid().removeData(view.getNewVariantRightListGrid().getDataAsRecordList().get(i));
		}
		
		/*
		 * NewResource
		 */
		view.getNewResourceTextItem().setValue("");
		view.getResourceAwailabilitySlider2().setValue(100);
	
		/*
		 * ChangeOrdersDueTime
		 */
		view.getOrderChooserForDueTimeComboBoxItem().setValue("");
		view.getOrderDueDateItem().setValue(new Date());
		view.getOrderDueTimeField().setTime(new Date());
		view.getOrderDueDateItem().setDisabled(true);
		view.getOrderDueTimeField().setDisabled(true);
	
		/*
		 * ChangeResourceAvaiability
		 */
		view.getResourceChooserForAvaiabilityComboBoxItem().setValue("");
		view.getResourceAwailabilitySlider().setValue(0);
		view.getResourceAwailabilitySlider().setDisabled(true);
	
		/*
		 * RemoveOrdersProduct
		 */
		view.getOrderChooserForProductChange().setValue("");
		for(int i=view.getChangeOrderProductLeftListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
			view.getChangeOrderProductLeftListGrid().removeData(view.getChangeOrderProductLeftListGrid().getDataAsRecordList().get(i));
		}
		view.getChangeOrderProductLeftListGrid().setDisabled(true);
		for(int i=view.getChangeOrderProductRightListGrid().getDataAsRecordList().getLength()-1; i>=0; i--) {
			view.getChangeOrderProductRightListGrid().removeData(view.getChangeOrderProductRightListGrid().getDataAsRecordList().get(i));
		}
		view.getChangeOrderProductRightListGrid().setDisabled(true);
		view.getChangeOrderProductArrowLeft().setDisabled(true);
		view.getChangeOrderProductArrowRight().setDisabled(true);
		
		/*
		 * ChangeOrderPriority
		 */
		view.getOrderChooserForPriorityComboBoxItem().setValue("");
		view.getPriorityChooser().setValue("1");
		view.getPriorityChooser().setDisabled(true);
	
		/*
		 * ChangeOperation
		 */
		view.getProductChooserForOperationChange().setValue("");
		view.getVariantChooserForOperationChange().setValue("");
		view.getVariantChooserForOperationChange().setDisabled(true);
		view.getOperationChooserForChange().setValue("");
		view.getOperationChooserForChange().setDisabled(true);
		view.getDurationSpinnerForOperationChange().setValue(0);
		view.getDurationSpinnerForOperationChange().setDisabled(true);
		for(ListGridRecord lgr : view.getResourceListGridForOperationChange().getRecords()) {
			lgr.setAttribute("isSelected", false);
		}
		view.getResourceListGridForOperationChange().deselectAllRecords();
		view.getResourceListGridForOperationChange().redraw();
		view.getResourceListGridForOperationChange().setDisabled(true);
		
		/*
		 * MachineBreakdown
		 */
		view.getResourceChooserForMachineBreakdown().setValue("");
		
		/*
		 * MachineRepaired
		 */
		view.getResourceChooserForMachineRepair().setValue("");
		
		/*
		 * MaintenacePeriod
		 */
		view.getResourceChooserForMaintainacePeriod().setValue("");
		view.getMaintenaceDueDateItem().setValue(new Date());
		view.getMaintenaceDueDateItem().setDisabled(true);
		view.getMaintenaceDueTimeField().setTime(new Date());
		view.getMaintenaceDueTimeField().setDisabled(true);
		view.getMaintenaceStartDateItem().setValue(new Date());
		view.getMaintenaceStartDateItem().setDisabled(true);
		view.getMaintenaceStartTimeField().setTime(new Date());
		view.getMaintenaceStartTimeField().setDisabled(true);
		
		/*
		 * RemoveOrders
		 */
		view.getOrderChooserForRemovement().setValue("");
		
		/*
		 * RemoveProducts
		 */
		view.getProductChooserForRemovement().setValue("");
		
		/*
		 * RemoveVariants
		 */
		view.getProductChooserForVariantRemovementComboBoxItem().setValue("");
		view.getVariantChooserForRemovement().setValue("");
		view.getVariantChooserForRemovement().setDisabled(true);
	}
	
	public InvokeEventView getView() {
		return view;
	}
	
	/**
	 * Record für die EventTypen
	 * @author Dennis
	 */
	private class EventTypeRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param event Ereignis
		 * @param category Kathegorie als String
		 */
		private EventTypeRecord(ScheduleEvent event, String category) {
			this.setEvent(event);
			this.setName(event.getName());
			this.setCategory(category);
		}
		
		private void setEvent(ScheduleEvent event) {
			this.setAttribute("event", event);
		}
		
		private ScheduleEvent getEvent() {
			return (ScheduleEvent)(this.getAttributeAsObject("event"));
		}
		
		private void setName(String event) {
			this.setAttribute("name", event);
		}
		
		private void setCategory(String event) {
			this.setAttribute("category", event);
		}
	}
	
	/**
	 * Record für zu werfende Ereignisse
	 * @author Dennis
	 */
	public class EventsToInvokeRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param event Ereignis
		 * @param eventType Typ als int
		 * @see ScheduleEvent
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private EventsToInvokeRecord(ScheduleEvent event, int eventType) {
			this.setEvent(event);
			this.setType(eventType);
			this.setTypeString(eventTypes[eventType].getEvent().getName());
			this.setAttribute("throwTime", event.getThrowTime());
			
			/*
			 * Definition des Records je nach EventTyp
			 */
			switch(eventType) {
			case ScheduleEvent.TYPE_NEW_ORDERS_EVENT: 
				ScheduleOrder order = ((NewOrdersEvent)event).getNewOrdersList().get(0);
				this.setAttribute("name", "New order ("+order.getName()+")");
				this.setAttribute("orderName", order.getName());
				this.setAttribute("orderPriority", order.getPriority());
				this.setAttribute("orderDueTime", order.getEarlistDueTime());
				this.setAttribute("productAmount", order.getProducts(ScenarioPresenter.getInstance().getScenario()).size());
				this.setAttribute("productList", order.getProducts(ScenarioPresenter.getInstance().getScenario()));
				break;
			case ScheduleEvent.TYPE_NEW_PRODUCTS_EVENT: 
				Product product = ((NewProductsEvent)event).getNewProducts().get(0);
				this.setAttribute("name", "New product ("+product.getName()+")");
				this.setAttribute("productName", product.getName());
				this.setAttribute("operationAmount", product.getVariants().size());
				this.setAttribute("operationList", product.getVariants().get(0).getOperations());
				break;
			case ScheduleEvent.TYPE_NEW_VARIANTS_EVENT: 
				Variant variant = ((NewVariantsEvent)event).getNewVariants().get(0);
				this.setAttribute("name", "New variant for "+variant.getProduct().getName());
				this.setAttribute("productName", variant.getProduct().getName());
				this.setAttribute("operationAmount", variant.getOperations().size());
				this.setAttribute("operationList", variant.getOperations());
				break;
			case ScheduleEvent.TYPE_NEW_RESOURCE_EVENT:
				Resource resource2 = ((NewResourceEvent)event).getNewResources().get(0);
				this.setAttribute("name", "New resource ("+resource2.getName()+")");
				this.setAttribute("awailability", resource2.getAvailability()+"%");
				break;
			case ScheduleEvent.TYPE_CHANGE_ORDERS_DUE_TIMES_EVENT:
				ScheduleOrder order2 = ((ChangeOrderDueTimeEvent)event).getChangedOrders().get(0);
				this.setAttribute("name", "Due date change for "+order2.getName());
				this.setAttribute("orderName", order2.getName());
				this.setAttribute("newDueDate", order2.getEarlistDueTime());
				break;
			case ScheduleEvent.TYPE_CHANGE_ORDERS_PRIORITY_EVENT: 
				ScheduleOrder order3 = ((ChangeOrderPriorityEvent)event).getChangeOrders().get(0);
				this.setAttribute("name", "Priority change for "+order3.getName());
				this.setAttribute("orderName", order3.getName());
				this.setAttribute("newPriority", order3.getPriority());
				break;
			case ScheduleEvent.TYPE_REMOVE_ORDERS_PRODUCTS_EVENT: 
				ScheduleOrder order7 = (ScheduleOrder) new ArrayList(((RemoveOrdersProductsEvent)event).getRemoveMap().keySet()).get(0);
				this.setAttribute("name", "Removement of products of "+order7.getName());
				this.setAttribute("orderName", order7.getName());
				this.setAttribute("removedProductsAmount", ((RemoveOrdersProductsEvent)event).getRemoveMap().values().size());
				this.setAttribute("removedProducts", new ArrayList(((RemoveOrdersProductsEvent)event).getRemoveMap().values()));
				break;
			case ScheduleEvent.TYPE_CHANGE_RESOURCE_AVAILABILITY_EVENT: 
				Resource resource = (Resource) ScenarioPresenter.getInstance().getScenario().getResources().get(((ChangeResourceAvailabilityEvent)event).getChangedResourceAvailabilities().keySet().toArray()[0]);
				this.setAttribute("name", "Change of awailability of "+resource.getName());
				this.setAttribute("resourceName", resource.getName());
				this.setAttribute("newAwailability", (int)resource.getAvailability()+"%");
				break;
			case ScheduleEvent.TYPE_REMOVE_ORDERS_EVENT: 
				ScheduleOrder order6 = ((RemoveOrdersEvent)event).getRemoveOrdersList().get(0);
				this.setAttribute("name", "Removement of "+order6.getName());
				this.setAttribute("orderName", order6.getName());
				break;
			case ScheduleEvent.TYPE_REMOVE_PRODUCTS_EVENT: 
				Product product2 = ((RemoveProductsEvent)event).getRemovedProducts().get(0);
				this.setAttribute("name", "Removement of "+product2.getName());
				this.setAttribute("productName", product2.getName());
				break;
			case ScheduleEvent.TYPE_REMOVE_VARIANTS_EVENT: 
				Variant variant2 = ((RemoveVariantsEvent)event).getRemovedVariants().get(0);
				this.setAttribute("name", "Removement of variant of "+variant2.getProduct().getName());
				this.setAttribute("productName", variant2.getProduct().getName());
				this.setAttribute("operationAmount", variant2.getOperations().size());
				this.setAttribute("operations", variant2.getOperations());
				break;
			case ScheduleEvent.TYPE_MACHINE_BREAK_DOWNS_EVENT: 
				Resource resource3 = ((MachineBreakDownEvent)event).getResourceBreakDowns().get(0);
				this.setAttribute("name", "Machine breakdown ("+resource3.getName()+")");
				this.setAttribute("machineName", resource3.getName());
				this.setAttribute("breakdownDate", ((MachineBreakDownEvent)event).getThrowTime());
				break;
			case ScheduleEvent.TYPE_MACHINES_REPAIRED_EVENT:
				Resource resource4 = ((MachineRepairedEvent)event).getResourceRepairs().get(0);
				this.setAttribute("name", "Machine repair ("+resource4.getName()+")");
				this.setAttribute("machineName", resource4.getName());
				this.setAttribute("repairDate", ((MachineRepairedEvent)event).getThrowTime());
				break;
			case ScheduleEvent.TYPE_MAINTENACE_PERIODS_EVENT: 
				Resource resource5 = ScenarioPresenter.getInstance().getScenario().getResources().get(((MaintenancePeriodsEvent)event).getMaintenancePeriods().get(0).getResourceID());
				this.setAttribute("name", "Maintenace period ("+resource5.getName()+")");
				this.setAttribute("machineName", resource5.getName());
				this.setAttribute("beginDate", ((MaintenancePeriodsEvent)event).getMaintenancePeriods().get(0).getStartTime());
				this.setAttribute("endDate", ((MaintenancePeriodsEvent)event).getMaintenancePeriods().get(0).getDueTime());
				break;
			case ScheduleEvent.TYPE_CHANGE_OPERATION_RESOURCES_EVENT:
				ChangeOperationResourcesEvent changeOperationResourcesEvent = (ChangeOperationResourcesEvent)event;
				Product productForOperation = ScenarioPresenter.getInstance().getScenario().getProduct(changeOperationResourcesEvent.getProductID());
				Variant variantForOperation = productForOperation.getVariant(changeOperationResourcesEvent.getVariantID());
				Operation operation = variantForOperation.getOperation(changeOperationResourcesEvent.getOperationID());
				this.setAttribute("name", "Operation Change ("+operation.getName()+")");
				this.setAttribute("productName", productForOperation.getName());
				this.setAttribute("variantName", "Variant"+variantForOperation.getVariantID());
				this.setAttribute("operationName", operation.getName());
				this.setAttribute("duration", changeOperationResourcesEvent.getDuration());
				this.setAttribute("resourceAlternativesAmount", changeOperationResourcesEvent.getNewResourceList().size());
				this.setAttribute("resourceAlternativesIDs", changeOperationResourcesEvent.getNewResourceList());
				break;
			}
		}
		
		public void setEvent(ScheduleEvent event) {
			this.setAttribute("event", event);
		}
		
		public ScheduleEvent getEvent() {
			return (ScheduleEvent) this.getAttributeAsObject("event");
		}
		
		public void setType(int type) {
			this.setAttribute("type", type);
		}
		
		public int getType() {
			return this.getAttributeAsInt("type");
		}
		
		public void setTypeString(String name) {
			this.setAttribute("typeString", name);
		}
		
		public String getName() {
			return this.getAttributeAsString("typeString");
		}
	}
	
	/**
	 * Record für Operationen
	 * @author Dennis
	 */
	public class OperationRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param operation Operation
		 * @param name Name der Operation
		 */
		private OperationRecord(Operation operation, String name) {
			this.setOperation(operation);
			this.setName(name);
		}
		
		private void setOperation(Operation operation) {
			this.setAttribute("operation", operation);
		}
		
		private void setName(String name) {
			this.setAttribute("name", name);
		}
		
		public Operation getOperation() {
			return (Operation) this.getAttributeAsObject("operation");
		}
		
		@SuppressWarnings("unused")
		public String getName() {
			return this.getAttribute("name");
		}
	}
	
	/**
	 * Record für Produkte
	 * @author Dennis
	 */
	public class ProductRecord extends ListGridRecord {
		/**
		 * Konstruktor
		 * @param product Produkt
		 * @param name Name des Produkts
		 */
		public ProductRecord(Product product, String name) {
			this.setProduct(product);
			this.setName(name);
		}
		
		private void setProduct(Product product) {
			this.setAttribute("product", product);
		}
		
		private void setName(String name) {
			this.setAttribute("name", name);
		}
		
		private Product getProduct() {
			return (Product) this.getAttributeAsObject("product");
		}
		
		@SuppressWarnings("unused")
		private String getName() {
			return this.getAttribute("name");
		}
	}
}
