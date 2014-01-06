package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.bachelor.smartSchedules.client.view.CreateNewScenarioView;
import de.bachelor.smartSchedules.server.model.util.FileUploadServlet;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;
import de.bachelor.smartSchedules.shared.model.util.User;
import de.bachelor.smartSchedules.shared.model.util.event.XMLEvent;
import de.novanic.eventservice.client.event.Event;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * Presenter für die Erstellung eines neuen Scenarios
 * @author Dennis
 */
public class CreateNewScenarioPresenter {
	/**
	 * Prefix für die DOMAIN bei CreateNewScenario
	 * @see FileUploadServlet
	 */
	private static final String USER_DOMAIN_PREFIX = "UserID:";
	
	/**
	 * dazugehöriger View
	 */
	private final CreateNewScenarioView view;
	
	/**
	 * Aktuelle Selektion (z.B.: New Resource oder New Product)
	 */
	private int currentSelection;
	
	/**
	 * Hilfsvariable (Zähler)
	 */
	private int resourceCounter, productCounter, operationCounter, variandIDCounter;
	
	/**
	 * temporäre Map mit Resourcen
	 */
	private Map<Integer, Resource> resourceMap;
	
	/**
	 * temporäre Map mit Produkten
	 */
	private Map<Integer, Product> productMap;
	
	private final User user;
	
	/**
	 * Konstruktor
	 */
	public CreateNewScenarioPresenter(User user) {
		view = new CreateNewScenarioView();
		this.user = user;
		
		this.variandIDCounter = 0;
		resourceCounter = 0;
		productCounter = 0;
		operationCounter = 0;
		resourceMap = new HashMap<Integer, Resource>();
		productMap = new HashMap<Integer, Product>();
		
		this.addListeners(user.getUserID());
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners(int userID) {
		
		final int tmpUserID = userID;
		
		this.view.getSubmitButton().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getFileUpload().getUserIDBox().setValue(""+user.getUserID());
				view.getFileUpload().getForm().submit();}
		});
		
		/**
		 * Bei Abbuch der Scenarioerstellung den Login zeigen
		 */
		view.getAbortButton().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TabPresenter.getInstance().getLogInPresenter().getView().getCheckCreateNewScenario().setValue(false);
				TabPresenter.getInstance().showLogIn();
				RemoteEventServiceFactory.getInstance().getRemoteEventService().removeListeners(DomainFactory.getDomain(USER_DOMAIN_PREFIX +tmpUserID));
			}
		});
		
		/**
		 * Bei Klick auf "New Resource"
		 */
		view.getAddResourceMenuItem().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.resetAll();
				currentSelection = CreateNewScenarioView.NEW_RESOURCE;
				view.setInputCanvas(currentSelection);
			}
		});
		
		/**
		 * Bei Klick auf "New Product"
		 */
		view.getAddProductMenuItem().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.resetAll();
				currentSelection = CreateNewScenarioView.NEW_PRODUCT;
				view.setInputCanvas(currentSelection);
			}
		});
		
		/**
		 * Bei Klick auf "New Variant" auf einem Product
		 */
		view.getAddVariantMenuItem().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if(view.getProductListGrid().getSelectedRecord() != null) {
					view.resetAll();
					currentSelection = CreateNewScenarioView.NEW_VARIANT;
					view.setInputCanvas(currentSelection);
					
					view.getNewVariantProductChooser().setValue(((ProductRecord)(view.getProductListGrid().getSelectedRecord())).getProduct().getProductID());
				}
			}
		});
		
		/**
		 * Bei Klick auf "Delete Resource"
		 */
		view.getDeleteResourceMenuItem().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if(view.getResourceListGrid().getSelectedRecord() != null) {
					resourceMap.remove(((ResourceRecord)(view.getResourceListGrid().getSelectedRecord())).getResource().getResourceID());
				
					/*
					 * View-Combobox aktualisieren
					 */
					final LinkedHashMap<String, String> valueMapForResourceChooser = new LinkedHashMap<String, String>();
					for(int i : resourceMap.keySet()) {
						valueMapForResourceChooser.put(i+"", resourceMap.get(i).getName());
					}
					view.getNewOperationResourceChooser().setValueMap(valueMapForResourceChooser);
				
					/*
					 * View-Grid aktualisieren
					 */
					view.getResourceListGrid().removeData(view.getResourceListGrid().getSelectedRecord());
				}
			}
		});
		
		/**
		 * Bei Klick auf "Delete Product"
		 */
		view.getDeleteProductMenuItem().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if(view.getProductListGrid().getSelectedRecord() != null) {
					productMap.remove(((ProductRecord)(view.getProductListGrid().getSelectedRecord())).getProduct().getProductID());
				
					/*
					 * View-Grid aktualisieren
					 */
					view.getProductListGrid().removeData(view.getProductListGrid().getSelectedRecord());
				}
			}
		});
		
		/**
		 * Bei Klick auf Reset
		 */
		view.getResetButton().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.resetAll();
			}
		});
		
		/**
		 * Bei Bestätigung einer Operation für die initiale Variante eines Produktes
		 */
		view.getNewProductArrow().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(view.getNewOperationName().getValue() != null
				&& !view.getNewOperationName().getValueAsString().equals("")
				&& view.getNewOperationResourceChooser().getValue() != null
				&& Integer.valueOf(view.getNewOperationDurationSpinner().getValueAsString()) > 0) {
					view.getNewProductRightListGrid().addData(
						new OperationRecord(
							/*
							 * Neue Operation
							 * ScenarioID = -1; Wird auf Server gesetzt
							 */
							new Operation(-1,
								operationCounter++, 
								(Integer.valueOf(view.getNewOperationResourceChooser().getValueAsString())),
								Integer.valueOf(view.getNewOperationDurationSpinner().getValueAsString()), CreateNewScenarioPresenter.this.variandIDCounter,
								view.getNewOperationName().getValueAsString()
							)
						)
					);
					
					/*
					 * View resetten
					 */
					view.getNewOperationName().setValue("");
					view.getNewOperationDurationSpinner().setValue(0);
					view.getNewOperationResourceChooser().setValue("");
				} else {
					SC.say("Input error");
				}
			}
		});
		
		/**
		 * Bei Bestätigung einer Operation bei einer neuen Variante
		 */
		view.getNewVariantArrow().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(view.getNewOperationName2().getValue() != null
				&& !view.getNewOperationName2().getValueAsString().equals("")
				&& view.getNewOperationResourceChooser2().getValue() != null
				&& Integer.valueOf(view.getNewOperationDurationSpinner2().getValueAsString()) > 0) {
					view.getNewVariantRightListGrid().addData(
						new OperationRecord(
							/*
							 * Neue Operation
							 * ScenarioID = -1; Wird auf Server gesetzt
							 */
							new Operation(-1,
								operationCounter++, 
								(Integer.valueOf(view.getNewOperationResourceChooser2().getValueAsString())),
								Integer.valueOf(view.getNewOperationDurationSpinner2().getValueAsString()), CreateNewScenarioPresenter.this.variandIDCounter,
								view.getNewOperationName2().getValueAsString()
							)
						)
					);
					
					/*
					 * View resetten
					 */
					view.getNewOperationName2().setValue("");
					view.getNewOperationDurationSpinner2().setValue(0);
					view.getNewOperationResourceChooser2().setValue("");
				} else {
					SC.say("Input error");
				}
			}
		});
		
		/**
		 * Bei Selektion einer Resource
		 */
		view.getResourceListGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				if(event.getState()) {
					view.getResourceDetailViewer().setData(event.getSelection());
					view.getResourceDetailViewer().selectRecord(event.getRecord());
				}
			}
		});
		
		/**
		 * Bei Selektion eines Produkts
		 */
		view.getProductListGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				if(event.getState()) {
					view.getProductDetailViewer().setData(event.getSelection());
					view.getProductDetailViewer().selectRecord(event.getRecord());
				
					String variantList = "";
					String operationList = "";
					Product p = (Product) event.getRecord().getAttributeAsObject("product");
					for(Variant v : p.getVariants()) {
						operationList = "";
						for(Operation o : v.getOperations()) {
							operationList += o.getName();
							operationList += ",&nbsp;";
						}
						operationList = operationList.substring(0, operationList.length()-7);
						
						variantList += "{"+operationList+"}";
						variantList += ",&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
					}
					variantList = variantList.substring(0, variantList.length()-43);
					
					view.getProductDetailViewer().setFields(new DetailViewerField("name", "Name"),
													   		new DetailViewerField("variantAmount", "# <a href='javascript:alert(\""+variantList+"\")'>variants</a>"));
				}
			}
		});
		
		/**
		 * Bei Hinzufügen einer Resource/eines Produkts/einer Variante
		 */
		view.getIncludeButton().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				switch(currentSelection) {
				/*
				 * Neue Resource
				 */
				case CreateNewScenarioView.NEW_RESOURCE:
					if(view.getNewResourceTextItem().getValueAsString() != ""
					&& view.getNewResourceTextItem().getValue() != null) {
						/*
						 * Resource erstellen
						 * ScenarioID = -1; Wird auf Server gesetzt
						 */
						Resource r = new Resource(-1, resourceCounter++, 
												  view.getNewResourceTextItem().getValueAsString(), 
												  (int)view.getResourceAwailabilitySlider().getValue());
						
						/*
						 * Record erstellen und hinzufügen
						 */
						ResourceRecord rr = new ResourceRecord(r);
						rr.setAvailability(r.getAvailability());
						view.getResourceListGrid().addData(rr);
						
						resourceMap.put(r.getResourceID(), r);
						
						/*
						 * Resourcen-Comboboxes aktualisieren
						 */
						final LinkedHashMap<String, String> valueMapForResourceChooser = new LinkedHashMap<String, String>();
						for(int i : resourceMap.keySet()) {
							valueMapForResourceChooser.put(i+"", resourceMap.get(i).getName());
						}
						view.getNewOperationResourceChooser().setValueMap(valueMapForResourceChooser);
						view.getNewOperationResourceChooser2().setValueMap(valueMapForResourceChooser);
						
						/*
						 * Reset
						 */
						view.resetAll();
					} else {
						SC.say("Input error");
					}
					break;
				/*
				 * Neues Produkt
				 */
				case CreateNewScenarioView.NEW_PRODUCT:
					if(view.getNewProductTextItem().getValueAsString() != ""
					&& view.getNewProductTextItem().getValue() != null
					&& !view.getNewProductRightListGrid().getDataAsRecordList().isEmpty()) {
						/*
						 * Produkt erstellen
						 * ScenarioID = -1; Wird auf Server gesetzt
						 */
						Product p = new Product(-1, productCounter++, 
												view.getNewProductTextItem().getValueAsString());
						List<Operation> oList = new ArrayList<Operation>();
						for(ListGridRecord r : view.getNewProductRightListGrid().getRecords()) {
							oList.add(((OperationRecord)r).getOperation());
						}
						p.addVariant(new Variant(CreateNewScenarioPresenter.this.variandIDCounter++, 
												oList, 
												p));
						
						/*
						 * Record erstellen und hinzufügen
						 */
						ProductRecord pr = new ProductRecord(p);
						pr.setVariantAmount(1);
						view.getProductListGrid().addData(pr);
						
						productMap.put(p.getProductID(), p);
						
						/*
						 * Product-Combobox füllen
						 */
						final LinkedHashMap<String, String> valueMapForProductChooser = new LinkedHashMap<String, String>();
						for(int i : productMap.keySet()) {
							valueMapForProductChooser.put(i+"", productMap.get(i).getName());
						}
						view.getNewVariantProductChooser().setValueMap(valueMapForProductChooser);
						
						/*
						 * Reset
						 */
						view.resetAll();
					} else {
						SC.say("Input error");
					}
					break;
				/*
				 * Neue Variante
				 */
				case CreateNewScenarioView.NEW_VARIANT:
					if(!view.getNewVariantRightListGrid().getDataAsRecordList().isEmpty()) {
						/*
						 * Produkt aus temporärer Produktmap holen und Variante hinzufügen
						 */
						Product p = productMap.get(view.getNewVariantProductChooser().getValue());
						List<Operation> oList = new ArrayList<Operation>();
						for(ListGridRecord lgr : view.getNewVariantRightListGrid().getRecords()) {
							oList.add((Operation)((OperationRecord)(lgr)).getOperation());
						}
						p.addVariant(new Variant(CreateNewScenarioPresenter.this.variandIDCounter++, oList, p));
						
						/*
						 * DetailViewer updaten
						 */
						for(ListGridRecord r : view.getProductListGrid().getRecords()) {
							if(((ProductRecord)r).getProduct().getProductID() == p.getProductID()) {
								((ProductRecord)r).increaseVariantAmount();
								
								view.getProductDetailViewer().setData(new ListGridRecord[]{r});
								view.getProductDetailViewer().selectRecord(r);
							
								String variantList = "";
								String operationList = "";
								Product p2 = (Product) ((ProductRecord)r).getAttributeAsObject("product");
								for(Variant v : p2.getVariants()) {
									operationList = "";
									for(Operation o : v.getOperations()) {
										operationList += o.getName();
										operationList += ",&nbsp;";
									}
									operationList = operationList.substring(0, operationList.length()-7);
									
									variantList += "{"+operationList+"}";
									variantList += ",&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
								}
								variantList = variantList.substring(0, variantList.length()-43);
								
								view.getProductDetailViewer().setFields(new DetailViewerField("name", "Name"),
																   		new DetailViewerField("variantAmount", "# <a href='javascript:alert(\""+variantList+"\")'>variants</a>"));
							
							}
						}

						/*
						 * Reset
						 */
						view.resetAll();
					} else {
						SC.say("Input error");
					}
					break;
				}
			}
		});
		
		/**
		 * DurationSpinner-Wertabfangen
		 */
		view.getNewOperationDurationSpinner().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
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
		view.getNewOperationDurationSpinner2().addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if(Integer.valueOf(event.getValue().toString()) > 0) {
					view.getNewOperationDurationSpinner2().setValue(event.getValue());
				} else {
					view.getNewOperationDurationSpinner2().setValue(1);
				}
			}
		});
		
		/*
		 * Bei Bestätigung des gesamten Scenarios
		 */
		view.getFinishButton().addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(view.getScenarioNameTextItem().getValue() != null
				&& resourceMap.size() > 0 
				&& productMap.size() > 0) {
					Scenario scenario = new Scenario(-1, UserPresenter.getInstance().getUser().getUserID(), view.getScenarioNameTextItem().getValueAsString(), resourceMap, productMap);	
					
					ServerCommunicationsManager.getInstance().addScenario(scenario, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							SC.say("New scenario was sent to server.");
						}
						
						@Override
						public void onFailure(Throwable caught) {
							SC.say("Fail.");
						}
					});
					
					TabPresenter.getInstance().getLogInPresenter().getView().getCheckCreateNewScenario().setValue(false);
					TabPresenter.getInstance().showLogIn();
				} else {
					SC.say("Input error");
				}
			}
		});
		
		/**
		 * RemoteDomainServiceListener:
		 */
		RemoteEventServiceFactory.getInstance().getRemoteEventService().addListener(DomainFactory.getDomain(USER_DOMAIN_PREFIX +userID), new RemoteEventListener() {
			
			@Override
			public void apply(Event anEvent) {
				if(anEvent instanceof XMLEvent) {
					((XMLEvent)anEvent).process(getView());
				}
			}
		});
	}
	
	public CreateNewScenarioView getView() {
		return view;
	}
	
	/**
	 * Record für Operation
	 */
	private class OperationRecord extends ListGridRecord {
		
		/**
		 * Konstruktor
		 * @param o Operation
		 */
		private OperationRecord(Operation o) {
			this.setAttribute("name", o.getName());
			this.setAttribute("operation", o);
		}
		
		private Operation getOperation() {
			return (Operation)this.getAttributeAsObject("operation");
		}
	}
	
	/**
	 * Record für Produkt
	 */
	private class ProductRecord extends ListGridRecord {
		
		/**
		 * Konstruktor
		 * @param p Produkt
		 */
		private ProductRecord(Product p) {
			this.setAttribute("name", p.getName());
			this.setAttribute("product", p);
		}
		
		public void increaseVariantAmount() {
			this.setVariantAmount(this.getAttributeAsInt("variantAmount")+1);
		}

		private void setVariantAmount(int amount) {
			this.setAttribute("variantAmount", amount);
		}
		
		private Product getProduct() {
			return (Product) this.getAttributeAsObject("product");
		}
	}
	
	/**
	 * Record für Resource
	 * @author Dennis
	 */
	private class ResourceRecord extends ListGridRecord {
		
		/**
		 * Konstruktor
		 * @param r Resource
		 */
		private ResourceRecord(Resource r) {
			this.setAttribute("name", r.getName());
			this.setAttribute("resource", r);
		}
		
		private void setAvailability(int a) {
			this.setAttribute("availability", a);
		}
		
		private Resource getResource() {
			return (Resource) this.getAttributeAsObject("resource");
		}
	}
}
