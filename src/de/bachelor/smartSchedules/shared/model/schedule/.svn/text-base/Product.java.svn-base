package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Ein Produkt
 * @author timo
 *
 */
public class Product implements Serializable{
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 2175584086929236767L;
	private int productID, scenarioID;
	private String name;
	private List<Variant> variants;
	
	/**
	 * Default
	 */
	public Product() {
		super();
		variants = new ArrayList<Variant>();
		scenarioID = -1;
	}
	
	public Product(int scenarioID, int productID, String name) {
		this();
		this.scenarioID = scenarioID;
		this.productID = productID;
		this.name = name;
	}
	
	public Product(int productID, String name, List<Variant> variants) {
		this.productID = productID;
		this.name = name;
		this.variants = variants;
	}

	public Product(int productID, String name, Variant variant) {
		this();
		this.productID = productID;
		this.name = name;
		this.variants.add(variant);
	}
	
	public int getProductID() {
		return productID;
	}

	public String getName() {
		return name;
	}
	
	public List<Variant> getVariants() {
		return variants;
	}

	public void addVariant(Variant variant) {
		this.variants.add(variant);
	}
	
	public void addVariants(List<Variant> variantList) {
		this.variants.addAll(variantList);
	}
	
	public Variant getVariant(int id) {
		for(Variant v : variants) {
			if(v.getVariantID() == id) {
				return v;
			}
		}
		
		return null;
	}
	
	public void removeVariant(Variant variant) {
		this.variants.remove(variant);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + productID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (productID != other.productID)
			return false;
		return true;
	}
	
	/**
	 * Gibt an, ob das Product nur aus kritischen
	 * Varianten besteht.
	 * @return
	 */
	public boolean isCritical(Scenario scenario) {
		int tmpCriticalVariantCount = 0;
		
		for(Variant v : this.variants) {
			if(v.isCritical(scenario)) {
				tmpCriticalVariantCount++;
			}
		}
		
		return tmpCriticalVariantCount == this.variants.size() ? true : false;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}
}
