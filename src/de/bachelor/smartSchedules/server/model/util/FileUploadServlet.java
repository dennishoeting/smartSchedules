package de.bachelor.smartSchedules.server.model.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.bachelor.smartSchedules.client.presenter.CreateNewScenarioPresenter;
import de.bachelor.smartSchedules.server.model.ScenarioManagerImpl;
import de.bachelor.smartSchedules.server.model.xmlparser.EventXMLParser;
import de.bachelor.smartSchedules.server.model.xmlparser.ScenarioXMLParser;
import de.bachelor.smartSchedules.shared.model.util.event.XMLEvent;
import de.novanic.eventservice.client.event.domain.DomainFactory;
/**
 * Servlet für den Fileupload, der für die XML-Parser benötigt wird.
 * @author timo
 *
 */
public class FileUploadServlet extends HttpServlet { 
	
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -2986678524910794924L;
	private static int xmlIDCounter = 0;
	/**
	 * Prefix für die DOMAIN bei CreateNewScenario
	 * @see CreateNewScenarioPresenter
	 */
	private static final String USER_DOMAIN_PREFIX = "UserID:";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		
		int tmpScenarioID = -1;
		int tmpUserID = -1;
		
		if (ServletFileUpload.isMultipartContent(req)) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			String fileName = null;
			File uploadedFile = null;
			
			// Request verarbeiten:
			try {
				List<FileItem> items = upload.parseRequest(req);
				for (FileItem item : items) {
					
					// Nur UploadFiles beachten
					if (item.isFormField()) {
						continue;
					}
					
					// Nur XML-Dateien weiter verarbeien
					if(!item.getName().endsWith(".xml")) {
						throw new IOException("Only XML-files allowed!");
					}
					
					// Datei schreiben
					fileName = getNewXMLName();
					uploadedFile = new File(fileName);
					if (uploadedFile.createNewFile()) {
						item.write(uploadedFile);
					} else
						throw new IOException("The file already exists in repository.");
					}
				
					// Index = 0 == ScenarioID; Index = 1 == UserID (nur eines ist nicht -1)
					tmpScenarioID = Integer.valueOf(items.get(0).getString());
				
					// Ist eine XML mit Events
					if(tmpScenarioID > -1) {

						// Ge-parste Events hinzufügen:
						ScenarioManagerImpl.addEvents(EventXMLParser.parseXML(ScenarioManagerImpl.getScenarioStatic(tmpScenarioID), new File(fileName)));
						
						// GWT Event auslösen:
						new ScenarioManagerImpl().addEvent(ScenarioManagerImpl.getScenarioStatic(tmpScenarioID).getEventServiceDomain(), new XMLEvent(true, "Events invoked successfully!"));
						
						
					// Ist eine XML um ein Szenario zu erstellen:	
					} else {
						
						tmpUserID = Integer.valueOf(items.get(1).getString());
						
						// Scenario adden:
						ScenarioManagerImpl.addScenarioStatic(ScenarioXMLParser.parseScenario(tmpUserID, new File(fileName)));
						
						// GWT Event auslösen:
						new ScenarioManagerImpl().addEvent(DomainFactory.getDomain(USER_DOMAIN_PREFIX +tmpUserID), new XMLEvent(true, "Scenario created successfully!"));
					}
			} catch (Exception e) {

				// Event mit Fehler auslösen:
				
				// Ist ein Event-XMLEvent (InvokeEvent):
				if(tmpScenarioID > -1) {
					new ScenarioManagerImpl().addEvent(ScenarioManagerImpl.getScenarioStatic(tmpScenarioID).getEventServiceDomain(), new XMLEvent(false, e.getMessage()));
				
				// Ist ein Scenario-XMLEvent (CreateNewScenario):
				} else {
					new ScenarioManagerImpl().addEvent(DomainFactory.getDomain(USER_DOMAIN_PREFIX +tmpUserID), new XMLEvent(false, e.getMessage()));
				}
			} finally {
				// File löschen:
				uploadedFile.delete();
				
				// Event mit neuen IDs schicken:
				if(tmpScenarioID > -1) {
					ScenarioManagerImpl.addGWTIncreaseScenarioIDsEvent(tmpScenarioID);
				}
			}

		} else {
			resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
					"Request contents type is not supported by the servlet.");
		}
	}
	
	private String getNewXMLName() {
		return xmlIDCounter++ +".xml";
	}
}
