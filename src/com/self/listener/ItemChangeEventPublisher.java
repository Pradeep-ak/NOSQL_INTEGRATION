package com.self.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.FinderException;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.common.event.DeploymentEventListener;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.versionmanager.WorkingVersion;

import com.self.util.QueueRepositoryConstants;
import com.self.util.RQLUtils;

public class ItemChangeEventPublisher extends GenericService 
		implements DeploymentEventListener{

	/** The active. */
	private boolean active = Boolean.FALSE;
	
	/** The m item queue repository. */
	private MutableRepository mItemQueueRepository;
	
	/** The m item queue rql utils. */
	private RQLUtils mItemQueueRQLUtils;
	
	/** The production target name. */
	private String productionTargetName;

	
	/* (non-Javadoc)
	 * @see atg.deployment.common.event.DeploymentEventListener#deploymentEvent(atg.deployment.common.event.DeploymentEvent)
	 */
	@Override
	public void deploymentEvent(DeploymentEvent pEvent) {
		if (isActive()
				&& pEvent.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE
				&& isProductionDeployment(pEvent)) {
			try {
				
				Map<String, Object> param = new HashMap<String, Object>();
				
				param.put(QueueRepositoryConstants.IP_DEPLOYMENTID, 
						pEvent.getDeploymentID());
				
				/*param.put(MongoQueueRepositoryConstants.IP_DEPLOYMENTDATE, 
						pEvent.getDeploymentBeginTimestamp());*/
				
				MutableRepositoryItem item = (MutableRepositoryItem) getItemQueueRQLUtils(
						).create(QueueRepositoryConstants.ID_SNAPSHOTITEMS, param);
				
				if (isLoggingDebug()) {
					logDebug("deploymentEvent: " + "Adding Snapshot item for deployment " 
								+ pEvent.getDeploymentID() + ", Snapshot id is " + item.getRepositoryId());
				}
				
				String[] deploymentProjectID = pEvent.getDeploymentProjectIDs();	
				
				if (isLoggingDebug()) {
					String projects = "";
					if (deploymentProjectID != null) {
						for (String projectId : deploymentProjectID) {
							projects = projects + projectId;
						}
					}
					logDebug("deploymentEvent: " + "Projects for deployment " 
								+ pEvent.getDeploymentID()
								+ " Projects which are part of deployment are  : " + projects);
				}
				item.setPropertyValue("snapShotId", item.getRepositoryId());
				List<RepositoryItem> items = getSynchItems(deploymentProjectID);
				item.setPropertyValue(QueueRepositoryConstants.IP_SYNCHITEMS, items);
				
				getItemQueueRepository().updateItem(item);
				
			} catch (RepositoryException | EJBException | FinderException e) {
				if (isLoggingError()) {
					logError("", e);
				}
			}
			
		}
	}

	private List<RepositoryItem> getSynchItems(String[] pDeploymentProjectID) 
			throws EJBException, FinderException, RepositoryException {
		List<RepositoryItem>  itemList = new ArrayList<RepositoryItem>();
		if (pDeploymentProjectID != null) {
			for (String projectId : pDeploymentProjectID) {
				Project project = ProjectConstants.getPersistentHomes()
						.getProjectHome().findById(projectId);
				Set<WorkingVersion> assets = project.getAssets();
				if (assets != null
						&& !assets.isEmpty()) {
					for (WorkingVersion asset : assets) {
						if (isLoggingDebug()) {
							logDebug("getSynchItems: " + "Asset are getting added to synched item list " + asset.getURI());
						}
						
						Map<String, Object> param = new HashMap<String, Object>();
						param.put(QueueRepositoryConstants.IP_ITEMID, asset.getURI().getRepositoryID());
						param.put(QueueRepositoryConstants.IP_ASSETVERSION, asset.getURI().getURIPart(
								QueueRepositoryConstants.ASSET_VERSION));
						param.put(QueueRepositoryConstants.IP_ITEMDESCRIPTORNAME, asset.getURI().getURIPart(
								QueueRepositoryConstants.ITEM_DESCRIPTOR_NAME));						
						param.put(QueueRepositoryConstants.IP_REPOSITORYNAME, asset.getURI().getURIPart(
								QueueRepositoryConstants.REPOSITORY_NAME));
						param.put(QueueRepositoryConstants.IP_ISSYNCHED, Boolean.FALSE);
						
						MutableRepositoryItem item = (MutableRepositoryItem) getItemQueueRQLUtils().create(
								QueueRepositoryConstants.ID_SYNCHITEM, param);
						itemList.add(item);
					}
				}
			}
		}
		
		return itemList;
	}

	/**
	 * Checks if is production deployment.
	 *
	 * @param event the event
	 * @return true, if is production deployment
	 */
	private boolean isProductionDeployment(DeploymentEvent event) {
		boolean result = false;
		if (event.getTarget().equalsIgnoreCase(getProductionTargetName())) {
			result = true;
		} else {
			if (isLoggingDebug()) {
				logDebug("isProductionDeployment: " + "Target is " + event.getTarget());
			}
		}

		return result;
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param pActive the active to set
	 */
	public void setActive(boolean pActive) {
		active = pActive;
	}

	/**
	 * @return the itemQueueRepository
	 */
	public MutableRepository getItemQueueRepository() {
		return mItemQueueRepository;
	}

	/**
	 * @param pItemQueueRepository the itemQueueRepository to set
	 */
	public void setItemQueueRepository(MutableRepository pItemQueueRepository) {
		mItemQueueRepository = pItemQueueRepository;
	}

	/**
	 * @return the itemQueueRQLUtils
	 */
	public RQLUtils getItemQueueRQLUtils() {
		return mItemQueueRQLUtils;
	}

	/**
	 * @param pItemQueueRQLUtils the itemQueueRQLUtils to set
	 */
	public void setItemQueueRQLUtils(RQLUtils pItemQueueRQLUtils) {
		mItemQueueRQLUtils = pItemQueueRQLUtils;
	}

	/**
	 * @return the productionTargetName
	 */
	public String getProductionTargetName() {
		return productionTargetName;
	}

	/**
	 * @param pProductionTargetName the productionTargetName to set
	 */
	public void setProductionTargetName(String pProductionTargetName) {
		productionTargetName = pProductionTargetName;
	}
	
	
}
