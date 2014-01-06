package de.bachelor.smartSchedules.server.model.xmlparser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import de.bachelor.smartSchedules.server.model.xmlparser.exception.AnythingNotDefinedException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.DurationNotDefinedForOperationException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.NameInUseException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.OperationNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.ResourceNotExistsException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.ResourcesNotDefinedForOperationException;
import de.bachelor.smartSchedules.server.model.xmlparser.exception.WrongAvailabilityException;
import de.bachelor.smartSchedules.shared.model.schedule.Operation;
import de.bachelor.smartSchedules.shared.model.schedule.Product;
import de.bachelor.smartSchedules.shared.model.schedule.Resource;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Variant;

/**
 * XML-Parser für Scenario XML Dateien.
 * @author timo
 *
 */
public class ScenarioXMLParser {
	
	/**
	 * Parst ein Scenario.
	 * @param userID
	 * @param xmlFile
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws WrongAvailabilityException 
	 * @throws NameInUseException 
	 * @throws ResourceNotExistsException 
	 * @throws OperationNotExistsException 
	 * @throws ResourcesNotDefinedForOperationException 
	 * @throws DurationNotDefinedForOperationException 
	 * @throws AnythingNotDefinedException 
	 */
	@SuppressWarnings("unchecked")
	public static Scenario parseScenario(int userID, File xmlFile) throws JDOMException, IOException, WrongAvailabilityException, NameInUseException, ResourceNotExistsException, OperationNotExistsException, ResourcesNotDefinedForOperationException, DurationNotDefinedForOperationException, AnythingNotDefinedException {
		
		
		// Element erstellen:
		Document tmpDocument = new SAXBuilder().build(xmlFile);
		Element tmpScenarioElement = tmpDocument.getRootElement();
		
		// Scenario erstellen:
		Scenario tmpScenario = new Scenario(userID, tmpScenarioElement.getChild("Name").getValue());
		
		// Resources erstellen:
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		Element tmpResourcesTagElement = tmpScenarioElement.getChild("Resources");
		
		// Falls keine Resources erstellt wurden:
		if(tmpResourcesTagElement == null) {
			return tmpScenario;
		}
		
		// Resources durchlaufen:
		for(Element tmpResourceElement : (List<Element>)tmpResourcesTagElement.getChildren("Resource")) {
			
			// Availability:
			int tmpAvailability = Integer.valueOf(tmpResourceElement.getChildText("Availability"));
			// Prüfen:
			if(tmpAvailability > 100 || tmpAvailability < 1) {
				throw new WrongAvailabilityException(tmpAvailability);
			}
			
			// Resource Name:
			String tmpResourceName = tmpResourceElement.getChildText("Name");
			// Prüfen:
			for(Resource r : tmpResourceList) {
				if(r.getName().equals(tmpResourceName)) {
					throw new NameInUseException(tmpResourceName);
				}
			}
			
			tmpResourceList.add(new Resource(1, tmpScenario.getNewIncreasedResourceIDCount(), tmpResourceName, tmpAvailability));
		}
		
		// Resources hinzufügen:
		tmpScenario.addResources(tmpResourceList);
		
		
		// Products erstellen:
		List<Product> tmpProductList = new ArrayList<Product>();
		
		// Products durchlaufen:
		if(tmpScenarioElement.getChild("Products") != null) {
			
			List<Element> tmpProductsElementList = (List<Element>)tmpScenarioElement.getChild("Products").getChildren("Product");
			if(tmpProductsElementList != null) {
				
				for(Element tmpProductElement : tmpProductsElementList) {
							
					List<Variant> tmpVariantList = new ArrayList<Variant>();
					
					// Name prüfen:
					String tmpProductName = tmpProductElement.getChildText("Name");
					for(Product p : tmpProductList) {
						if(p.getName().equals(tmpProductName)) {
							throw new NameInUseException(tmpProductName);
						}
					}
					Product tmpProduct = new Product(tmpScenario.getNewIncreasedProductIDCount(), tmpProductName, tmpVariantList);
					
					// Varianten durchlaufen:
					Element tmpVariantsElement = tmpProductElement.getChild("Variants");
					if(tmpVariantsElement == null) {
						throw new AnythingNotDefinedException("Variants", tmpProduct.getName());
					}
					for(Element tmpVariantElement : (List<Element>)tmpVariantsElement.getChildren("Variant")) {
						
						List<Operation> tmpOperationList = new ArrayList<Operation>();
						Variant tmpVariant = new Variant(tmpScenario.getNewIncreasedVariantIDCount(), tmpOperationList, tmpProduct);
						
						// Operationen durchlaufen:
						Element tmpOperationElement = tmpVariantElement.getChild("Operations");
						if(tmpOperationElement == null) {
							throw new AnythingNotDefinedException("Operations", tmpProductName);
						}
						for(Object operation : tmpOperationElement.getChildren("Operation")) {
							Element operationElement = (Element)operation;
							List<Integer> tmpResourceAlternatives = new ArrayList<Integer>();
							List<Operation> tmpPredecessorsList = new ArrayList<Operation>();
							
							// Prüfen, ob der Name schon belegt ist:
							String tmpOperationName = operationElement.getChildText("Name");

							for(Operation op : tmpOperationList) {
								if(op.getName().equals(tmpOperationName)) {
									throw new NameInUseException(tmpOperationName);
								}
							}
							
							// Resources durchlaufen:
							Element tmpOperationResourcesTagElement = operationElement.getChild("Resources");

							if(tmpOperationResourcesTagElement == null) {
								throw new ResourcesNotDefinedForOperationException(tmpOperationName);
							}
							for(Element resourceNameElement : (List<Element>)tmpOperationResourcesTagElement.getChildren("ResourceName")) {
								
								String tmpResourceName = resourceNameElement.getValue()  ;
								int tmpResourceID = -1;
								
								// Resource Suchen:
								for(Resource r : tmpResourceList) {
									if(r.getName().equals(tmpResourceName)) {
										tmpResourceID = r.getResourceID();
										break;
									}
								}
								
								// Prüfen, ob es die Resource gibt:
								if(tmpResourceID == -1) {
									throw new ResourceNotExistsException(tmpResourceName);
								}
								
								// Resource hinzufügen:
								tmpResourceAlternatives.add(tmpResourceID);
							}
							
							// Predecessors beachten:
							Object tmpPredecessors = operationElement.getChild("Predecessors");
							if(tmpPredecessors != null) {
								for(Object predecessor : ((Element)tmpPredecessors).getChildren("Name")) {
									Element predecessorElement = (Element)predecessor;
									Operation tmpPreOperation = null;
									
									// Predecessor suchen:
									for(Operation preOp : tmpOperationList) {
										if(preOp.getName().equals(predecessorElement.getValue())) {
											tmpPreOperation = preOp;
											break;
										}
									}
									
									// Prüfen:
									if(tmpPreOperation == null) {
										throw new OperationNotExistsException(predecessorElement.getValue());
									}
									
									// hinzufügen:
									tmpPredecessorsList.add(tmpPreOperation);
								}
							}
							
							// Duration prüfen:
							if(Integer.valueOf(operationElement.getChild("Duration").getValue())== null) {
								throw new DurationNotDefinedForOperationException(tmpOperationName);
							}
							
							// Operation hinzufügen:
							tmpOperationList.add(new Operation(tmpScenario.getScenarioID(), 
									tmpScenario.getNewIncreasedOperationIDCount(), tmpResourceAlternatives, Integer.valueOf(operationElement.getChild("Duration").getValue()), 
									tmpVariant.getVariantID(), operationElement.getChild("Name").getValue(), tmpPredecessorsList));
						}
						
						// Variant erstellen:
						tmpVariantList.add(tmpVariant);
					}
					
					// Product hinzufügen:
					tmpProductList.add(tmpProduct);
					
				}
				
				// Products ins Scenario:
				Map<Integer, Product> tmpProductMap = new HashMap<Integer, Product>();
				for(Product p : tmpProductList) {
					tmpProductMap.put(p.getProductID(), p);
				}
				
				tmpScenario.setProducts(tmpProductMap);
				
			}			
		}
		
		return tmpScenario;
	}
}
