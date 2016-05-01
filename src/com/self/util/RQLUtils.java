package com.self.util;


import java.util.HashMap;
import java.util.Map;

import atg.adapter.gsa.query.Builder;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.QueryOptions;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.rql.RqlStatement;

public class RQLUtils extends GenericService {

    /**
     * The starting index - set to 0.This will be used for sorting.
     */
    private static final int STARTING_INDEX = 0;

    /**
     * The ending index - set to -1 which implies no limit on the number of
     * values.
     */
    private static final int ENDING_INDEX = -1;

    /**
	 * constant to hold string for operator - AND.
	 */
    public static final String OPERATION_AND = "AND";
    
    /**
	 * constant to hold string  for operator- OR.
	 */
    public static final String OPERATION_OR = "OR";
    
    
    
    
    /**
     * The repository to be used.
     */
    private Repository repository;

    /**
     * The repository view cache to be used.
     */
    private Map<String, RepositoryView> repositoryViewCache = new HashMap<String, RepositoryView>();

    /**
     * <p>This method is used to get the repositoryView. Calling method has to pass
     * itemDescriptor. The itemDescriptor parameter is not validated.</p>
     *
     * @param itemDescriptor the item descriptor
     * @return RepositoryView The RepositoryView object for the given
     * ItemDescriptor.
     * @throws RepositoryException the repository exception
     */
    public RepositoryView getRepositoryView(String itemDescriptor) throws RepositoryException {
        RepositoryItemDescriptor repositoryItemDescriptor;
        RepositoryView repositoryView;
        repositoryView = (RepositoryView) repositoryViewCache.get(repository.getRepositoryName() + itemDescriptor);

        if (repositoryView == null) {
            repositoryItemDescriptor = getRepository().getItemDescriptor(itemDescriptor);
            repositoryView = repositoryItemDescriptor.getRepositoryView();
            repositoryViewCache.put(repository.getRepositoryName() + itemDescriptor, repositoryView);
        }

        return repositoryView;
    }

    /**
     * <p>Method to get the repository item for the given unique ID directly from
     * repository. Use this optimized method if repository ID is available</p>
     *
     * @param itemDescriptor the item descriptor
     * @param uniqueIDs the unique i ds
     * @return The repository item for the given it.
     * @throws RepositoryException the repository exception
     */

    public RepositoryItem[] getAllItemsByUniqueID(String itemDescriptor, String[] uniqueIDs)
        throws RepositoryException {
        return repository.getItems(uniqueIDs, itemDescriptor);
    }

    /**
     * <p>Method to get the repository item for the given unique ID directly from
     * repository. Use this optimized method if repository ID is available</p>
     *
     * @param itemDescriptor the item descriptor
     * @param uniqueID the unique id
     * @return The repository item for the given it.
     * @throws RepositoryException the repository exception
     */

    public RepositoryItem getItemByUniqueID(String itemDescriptor, String uniqueID) throws RepositoryException {
        return repository.getItem(uniqueID, itemDescriptor);
    }

    /**
     * <p>This method is used to get the SortDirectives.Calling Method have to pass
     * sorting properties and sort direction
     * ie;SortDirective.DIR_ASCENDING/DIR_DESCENDING.</p>
     *
     * @param sortProperties the sort properties
     * @return SortDirectives
     */

    private SortDirectives getSortDirectives(Map<String, Integer> sortProperties) {
        if (sortProperties != null) {
            SortDirectives sortDirectives = new SortDirectives();
            int sortMethod;
            SortDirective sortDirective;

            for (String propertyName : sortProperties.keySet()) {
                sortMethod = sortProperties.get(propertyName).intValue();
                sortDirective = new SortDirective(propertyName, sortMethod);
                sortDirectives.addDirective(sortDirective);
            }

            return sortDirectives;
        }

        return null;
    }

    /**
     * <p>Method to get the Repository items with out any constraints.Calling
     * method has to pass itemDescriptor to get all the repository items in that
     * item descriptor.</p>
     *
     * @param itemDescriptor the item descriptor
     * @return RepositoryItem An array of all the items for the given item
     * descriptor.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getAllItems(String itemDescriptor) throws RepositoryException {
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
        Query query = queryBuilder.createUnconstrainedQuery();

        return repositoryView.executeQuery(query);
    }

    /**
     * <p>Method to get the repository Items.Calling method have to pas item
     * descriptor, Property name and value to be queried in a map and operation
     * ie;QueryBuilder.EQUALS If sorting need have to have a map which contains
     * sorting properties and sorting method
     * ie;SortDirective.DIR_ASCENDING/DIR_DESCENDING.</p>
     *
     * @param itemDescriptor the item descriptor
     * @param propertyValuemap the property valuemap
     * @param operation the operation
     * @param sortProperties the sort properties
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getAllItemsByCase(String itemDescriptor, Map<String, Object> propertyValuemap,
        int operation, Map<String, Integer> sortProperties) throws RepositoryException {
        RepositoryItem[] queryResult = null;

        if (propertyValuemap != null) {
            QueryExpression propertyQueryExp;
            QueryExpression valueQueryExp;
            Query query;
            String value;
            RepositoryView repositoryView = getRepositoryView(itemDescriptor);
            QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
            int propertySize = propertyValuemap.size();
            Query[] queryArray = new Query[propertySize];
            int count = 0;

            for (String property : propertyValuemap.keySet()) {
                value = (String) propertyValuemap.get(property);

                propertyQueryExp = queryBuilder.createPropertyQueryExpression(property);
                valueQueryExp = queryBuilder.createConstantQueryExpression(value);
                query = queryBuilder.createPatternMatchQuery(propertyQueryExp, valueQueryExp, operation, false);

                queryArray[count] = query;
                count++;
            }

            Query allQuery = queryBuilder.createAndQuery(queryArray);
            queryResult = executeQuery(allQuery, repositoryView, sortProperties);
        }

        return queryResult;
    }

    /**
     * <p>Method to get the repository Items .Calling method have to pas item
     * descriptor, Property name and value to be queried in a map and operation
     * ie;QueryBuilder.EQUALS.Sorting need have to have a map which contains
     * sorting properties and sorting method
     * ie;SortDirective.DIR_ASCENDING/DIR_DESCENDING.</p>
     *
     * @param itemDescriptor the item descriptor
     * @param propertyValuemap the property valuemap
     * @param operation the operation
     * @param sortProperties the sort properties
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getAllItems(String itemDescriptor, Map<String, Object> propertyValuemap, int operation,
        Map<String, Integer> sortProperties) throws RepositoryException {
        RepositoryItem[] queryResult = null;

        if (propertyValuemap != null) {
            QueryExpression propertyQueryExp;
            QueryExpression valueQueryExp;
            Query query;
            String value;
            RepositoryView repositoryView = getRepositoryView(itemDescriptor);
            QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
            int propertySize = propertyValuemap.size();
            Query[] queryArray = new Query[propertySize];
            int count = 0;

            for (String property : propertyValuemap.keySet()) {
                value = (String) propertyValuemap.get(property);

                propertyQueryExp = queryBuilder.createPropertyQueryExpression(property);
                valueQueryExp = queryBuilder.createConstantQueryExpression(value);
                query = queryBuilder.createPatternMatchQuery(propertyQueryExp, valueQueryExp, operation, true);

                queryArray[count] = query;
                count++;
            }

            Query allQuery = queryBuilder.createAndQuery(queryArray);
            queryResult = executeQuery(allQuery, repositoryView, sortProperties);
        }

        return queryResult;
    }

    /**
     * Method to get the repository item for a single property.
     *
     * @param itemDescriptor the item descriptor
     * @param propertyName the property name
     * @param propertyValue the property value
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getAllItemsByProperty(String itemDescriptor, String propertyName, String propertyValue)
        throws RepositoryException {
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
        QueryExpression queryExpression = queryBuilder.createPropertyQueryExpression(propertyName);
        QueryExpression valueExpression = queryBuilder.createConstantQueryExpression(propertyValue);
        Query query = queryBuilder.createPatternMatchQuery(queryExpression, valueExpression, QueryBuilder.EQUALS, true);

        return repositoryView.executeQuery(query);
    }

    /**
     * Method to get the repository item for a single property.
     *
     * @param itemDescriptor the item descriptor
     * @param propertyName the property name
     * @param propertyValue the property value
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getAllItemsByPropertyByCase(String itemDescriptor, String propertyName,
        String propertyValue) throws RepositoryException {
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
        QueryExpression queryExpression = queryBuilder.createPropertyQueryExpression(propertyName);
        QueryExpression valueExpression = queryBuilder.createConstantQueryExpression(propertyValue);
        Query query = queryBuilder.createPatternMatchQuery(queryExpression, valueExpression, QueryBuilder.EQUALS);

        return repositoryView.executeQuery(query);
    }

    /**
     * <p>Method to execute the query and returning the RepositoryItems. Calling
     * method have to pass query to execute ,repository view,sorting properties
     * and sort method.</p>
     *
     * @param query the query
     * @param repositoryView the repository view
     * @param sortProperties the sort properties
     * @return RepositoryItem An array of RepositoryItems that match the query.
     * @throws RepositoryException the repository exception
     */

    private RepositoryItem[] executeQuery(Query query, RepositoryView repositoryView,
        Map<String, Integer> sortProperties) throws RepositoryException {
        RepositoryItem[] queryResult = null;
        QueryOptions queryOptions = null;

        if (sortProperties != null) {
            SortDirectives sortDirectives = getSortDirectives(sortProperties);
            queryOptions = new QueryOptions(STARTING_INDEX, ENDING_INDEX, sortDirectives, null);
        }

        queryResult = repositoryView.executeQuery(query, queryOptions);

        return queryResult;
    }

    /**
     * <p>Method to execute the Query using RQL and returning the RepositoryItems.
     * Calling method have to pass constraint parameters ,
     * queryString,ItemDescriptor and repository</p>
     *
     * @param params the params
     * @param queryString the query string
     * @param itemDescriptor the item descriptor
     * @return RepositoryItem
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] executeRQLQuery(Object[] params, String queryString, String itemDescriptor)
        throws RepositoryException {
        RepositoryView view = null;
        RqlStatement statement = null;
        RepositoryItem[] items = null;

        if (itemDescriptor != null) {
            view = getRepositoryView(itemDescriptor);
        }

        if (view != null) { 
            statement = RqlStatement.parseRqlStatement(queryString);
        }

        if (statement != null) {
            items = (RepositoryItem[]) statement.executeQuery(view, params);
        }

        return items;
    }

    /**
     * <p>Method to execute the SQL Query and returning the RepositoryItems.
     * Calling method have to pass constraint parameters ,
     * queryString,ItemDescriptor and repository</p>
     *
     * @param params the params
     * @param queryString the query string
     * @param itemDescriptor the item descriptor
     * @return RepositoryItem
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] executeSQLPassthroughQuery(Object[] params, String queryString, String itemDescriptor)
        throws RepositoryException {
        RepositoryItemDescriptor repositoryItemDescriptor = getRepository().getItemDescriptor(itemDescriptor);
        RepositoryView view = null;
        Builder builder = null;
        RepositoryItem[] items = null;

        if (repositoryItemDescriptor != null) {
            view = repositoryItemDescriptor.getRepositoryView();
        }

        if (view != null) {
            builder = (Builder) view.getQueryBuilder();
            items = view.executeQuery(builder.createSqlPassthroughQuery(queryString, params));
        }

        return items;
    }

    /**
     * <p>Method to save the Repository Item to repository Calling method have to
     * pass the item descriptor. Pass a map which contains property name as key
     * and propertyValue as value</P>
     *
     * @param itemDescriptor the item descriptor
     * @param propertyMap the property map
     * @return Boolean:True if save operation is success
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem create(String itemDescriptor, Map<String, Object> propertyMap) throws RepositoryException {
        if (propertyMap != null) {
            MutableRepositoryItem createItem = getMutableRepository().createItem(itemDescriptor);
            Object property;

            for (String propertyName : propertyMap.keySet()) {
                property = propertyMap.get(propertyName);

                if (property != null) {
                    createItem.setPropertyValue(propertyName, property);
                }
            }
            if (isLoggingDebug()) {
				logDebug("Create Repository item. "
						+ "ID : " + createItem.getRepositoryId()
						+ "itemDescriptor : " + itemDescriptor);
			}
            return getMutableRepository().addItem(createItem);
        }

        return null;
    }

    /**
     * <p>Method to update the Repository Item to repository. Calling method has to
     * pass the item descriptor. It also has to pass a map which contains
     * property name as key and propertyValue as value and the id of the item to
     * be updated.</p>
     *
     * @param id the id
     * @param itemDescriptor the item descriptor
     * @param propertyMap the property map
     * @return Boolean:True if save operation is success
     * @throws RepositoryException the repository exception
     */
    public boolean update(String id, String itemDescriptor, Map<String, Object> propertyMap)
        throws RepositoryException {
        if (propertyMap != null) {
            MutableRepositoryItem updateItem = getMutableRepository().getItemForUpdate(id, itemDescriptor);
            Object property;

            for (String propertyName : propertyMap.keySet()) {
                property = propertyMap.get(propertyName);

                if (property != null) {
                    updateItem.setPropertyValue(propertyName, property);
                }
            }

            getMutableRepository().updateItem(updateItem);

            return true;
        }

        return false;
    }

    /**
     * <p>Method to delete the Repository Item to repository. Calling method has to
     * pass the item descriptor. It also has to pass the id of the item to be
     * DELETED.</p>
     *
     * @param id the id
     * @param itemDescriptor the item descriptor
     * @return Boolean:True if delete operation is success
     * @throws RepositoryException the repository exception
     */
    public boolean delete(String id, String itemDescriptor) throws RepositoryException {
        getMutableRepository().removeItem(id, itemDescriptor);

        return true;

        // return false;
    }

    /**
     * Gets the repository.
     *
     * @return MutableRepository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Returns the mutable repository by casting the repository to
     * MutableRepository.
     *
     * @return The repository which has been cast to MutableRepository
     */
    public MutableRepository getMutableRepository() {
        return (MutableRepository) repository;
    }

    /**
     * Sets the repository.
     *
     * @param repository the new repository
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     *<p>This method creates repository item if the unique id to be used is known.
     * If empty string is sent for genKey then key is auto generated else the
     * provided id is used.It does not add item into database.</p>
     *
     * @param itemDescriptor the item descriptor
     * @param genKey the gen key
     * @param propertyMap the property map
     * @return created repository item
     * @throws RepositoryException the repository exception
     */
    public MutableRepositoryItem createItemWithKnownId(String itemDescriptor, String genKey,
        Map<String, Object> propertyMap) throws RepositoryException {
        if (propertyMap != null) {
            MutableRepositoryItem createItem = null;

            if ("".equals(genKey)) {
                createItem = getMutableRepository().createItem(itemDescriptor);
            } else {
                createItem = getMutableRepository().createItem(genKey, itemDescriptor);
            }

            Object property;

            for (String propertyName : propertyMap.keySet()) {
                property = propertyMap.get(propertyName);

                if (property != null) {
                    createItem.setPropertyValue(propertyName, property);
                }
            }

            return createItem;
        }

        return null;
    }

    /**
     * This add repository item to database.
     *
     * @param mutableRepository the mutable repository
     * @throws RepositoryException the repository exception
     */
    public void addItem(MutableRepositoryItem mutableRepository) throws RepositoryException {
        getMutableRepository().addItem(mutableRepository);
    }

    /**
     * <p>This method creates and adds repository item if the unique id to be used
     * is known. If empty string is sent for genKey then key is auto generated
     * else the provided id is used.</p>
     *
     * @param itemDescriptor the item descriptor
     * @param genKey the gen key
     * @param propertyMap the property map
     * @return created repository item
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem createAndAddItemWithKnownId(String itemDescriptor, String genKey,
        Map<String, Object> propertyMap) throws RepositoryException {
        if (propertyMap != null) {
            MutableRepositoryItem createItem = null;

            if ("".equals(genKey)) {
                createItem = getMutableRepository().createItem(itemDescriptor);
            } else {
                createItem = getMutableRepository().createItem(genKey, itemDescriptor);
            }

            Object property;

            for (String propertyName : propertyMap.keySet()) {
                property = propertyMap.get(propertyName);

                if (property != null) {
                    createItem.setPropertyValue(propertyName, property);
                }
            }
            if (isLoggingDebug()) {
				logDebug("Create Repository item. "
						+ "ID : " + createItem.getRepositoryId()
						+ ", itemDescriptor : " + itemDescriptor);
			}
            return getMutableRepository().addItem(createItem);
        }

        return null;
    }

    /**
     * <p>This method updates property including null values also. If a property
     * name is mentioned in map and null value is provided against it, then null
     * value is updated</p>
     *
     * @param id the id
     * @param itemDescriptor the item descriptor
     * @param propertyMap the property map
     * @return boolean
     * @throws RepositoryException the repository exception
     */
    public boolean updateIncludingNullValues(String id, String itemDescriptor, Map<String, Object> propertyMap)
        throws RepositoryException {
        if (propertyMap != null) {
            MutableRepositoryItem updateItem = getMutableRepository().getItemForUpdate(id, itemDescriptor);
            Object property;

            for (String propertyName : propertyMap.keySet()) {
                property = propertyMap.get(propertyName);

                updateItem.setPropertyValue(propertyName, property);
            }
            if (isLoggingDebug()) {
				logDebug("Repository Items is updated. "
						+ "ID : " + updateItem.getRepositoryId()
						+ ", itemDescriptor : " + itemDescriptor);
			}
            getMutableRepository().updateItem(updateItem);

            return true;
        }

        return false;
    }

    /**
     * Method to get the repository items by comparing two property values.
     *
     * @param itemDescriptor the item descriptor
     * @param propertyNameOne the property name one
     * @param propertyValueOne the property value one
     * @param compareOperationOne the compare operation one
     * @param propertyNameTwo the property name two
     * @param propertyValueTwo the property value two
     * @param compareOperationTwo the compare operation two
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getItemByComparingProperties(String itemDescriptor, String propertyNameOne,
        Object propertyValueOne, int compareOperationOne, String propertyNameTwo, Object propertyValueTwo,
        int compareOperationTwo) throws RepositoryException {
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();

        QueryExpression queryExpressionOne = queryBuilder.createPropertyQueryExpression(propertyNameOne);
        QueryExpression valueExpressionOne = queryBuilder.createConstantQueryExpression(propertyValueOne);
        Query queryOne = queryBuilder.createComparisonQuery(queryExpressionOne, valueExpressionOne,
                compareOperationOne);

        QueryExpression queryExpressionTwo = queryBuilder.createPropertyQueryExpression(propertyNameTwo);
        QueryExpression valueExpressionTwo = queryBuilder.createConstantQueryExpression(propertyValueTwo);
        Query queryTwo = queryBuilder.createComparisonQuery(queryExpressionTwo, valueExpressionTwo,
                compareOperationTwo);

        Query[] queries = {queryOne, queryTwo};

        Query andQuery = queryBuilder.createAndQuery(queries);

        return repositoryView.executeQuery(andQuery);
    }

    /**
     * Method to get the repository item for two properties without ignoring
     * case.
     *
     * @param itemDescriptor the item descriptor
     * @param propertyNameOne the property name one
     * @param propertyValueOne the property value one
     * @param propertyNameTwo the property name two
     * @param propertyValueTwo the property value two
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getItemByProperty(String itemDescriptor, String propertyNameOne, Object propertyValueOne,
        String propertyNameTwo, Object propertyValueTwo) throws RepositoryException {
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();

        QueryExpression queryExpressionOne = queryBuilder.createPropertyQueryExpression(propertyNameOne);
        QueryExpression valueExpressionOne = queryBuilder.createConstantQueryExpression(propertyValueOne);
        Query queryOne = queryBuilder.createPatternMatchQuery(queryExpressionOne, valueExpressionOne,
                QueryBuilder.EQUALS);

        QueryExpression queryExpressionTwo = queryBuilder.createPropertyQueryExpression(propertyNameTwo);
        QueryExpression valueExpressionTwo = queryBuilder.createConstantQueryExpression(propertyValueTwo);
        Query queryTwo = queryBuilder.createPatternMatchQuery(queryExpressionTwo, valueExpressionTwo,
                QueryBuilder.EQUALS);

        Query[] queries = {queryOne, queryTwo};
        Query andQuery = queryBuilder.createAndQuery(queries);

        return repositoryView.executeQuery(andQuery);
    }

    /**
     * Method to get the repository item for one property without ignoring case.
     *
     * @param itemDescriptor the item descriptor
     * @param propertyName the property name
     * @param propertyValue the property value
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getItemByProperty(String itemDescriptor, String propertyName, Object propertyValue)
        throws RepositoryException {
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();

        QueryExpression queryExpression = queryBuilder.createPropertyQueryExpression(propertyName);
        QueryExpression valueExpression = queryBuilder.createConstantQueryExpression(propertyValue);
        Query query = queryBuilder.createComparisonQuery(queryExpression, valueExpression, QueryBuilder.EQUALS);

        return repositoryView.executeQuery(query);
    }

    /**
     * Gets the items by includes item query.
     *
     * @param itemDescriptor the item descriptor
     * @param componentItemTypeProperty the component item type property
     * @param componentItemTypeDescriptor the component item type descriptor
     * @param componentItemTypePropertyMap the component item type property map
     * @return the items by includes item query
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getItemsByIncludesItemQuery(String itemDescriptor, String componentItemTypeProperty,
        String componentItemTypeDescriptor, Map<String, Object> componentItemTypePropertyMap)
        throws RepositoryException {
        RepositoryItem[] items = null;
        RepositoryView componentItemTypeRepositoryView = getRepositoryView(componentItemTypeDescriptor);
        QueryBuilder componentItemTypeQueryBuilder = componentItemTypeRepositoryView.getQueryBuilder();
        RepositoryView repositoryView = getRepositoryView(itemDescriptor);
        QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
        int propertySize = componentItemTypePropertyMap.size();
        Query[] queryArray = new Query[propertySize];
        int count = 0;

        for (String propertyName : componentItemTypePropertyMap.keySet()) {
            QueryExpression queryExpression = componentItemTypeQueryBuilder.createPropertyQueryExpression(propertyName);
            QueryExpression valueExpression = componentItemTypeQueryBuilder.createConstantQueryExpression(
                    componentItemTypePropertyMap.get(propertyName));
            Query query = componentItemTypeQueryBuilder.createComparisonQuery(queryExpression, valueExpression,
                    QueryBuilder.EQUALS);
            queryArray[count] = query;
            count++;
        }

        Query innerQuery = componentItemTypeQueryBuilder.createAndQuery(queryArray);

        QueryExpression outerQueryExpression = queryBuilder.createPropertyQueryExpression(componentItemTypeProperty);
        Query outerQuery = queryBuilder.createIncludesItemQuery(outerQueryExpression, innerQuery);

        items = repositoryView.executeQuery(outerQuery);

        return items;
    }
    
    /**
     * <p>Method to get the repository item for two properties without ignoring
     * case using the conditions "OR" and "AND" .</p>
     *
     * @param itemDescriptor the item descriptor
     * @param propertyNameOne the property name one
     * @param propertyValueOne the property value one
     * @param propertyNameTwo the property name two
     * @param propertyValueTwo the property value two
     * @param operation the operation
     * @return RepositoryItem An array of repository items matching the query.
     * @throws RepositoryException the repository exception
     */
    public RepositoryItem[] getItemByPropertyWithOperation(String itemDescriptor, String propertyNameOne,
        String propertyValueOne, String propertyNameTwo, String propertyValueTwo, String operation)
        throws RepositoryException {
        RepositoryItem[] repositoryItems = null;

        if (operation != null) {
            if (OPERATION_OR.equals(operation)) {
                RepositoryView repositoryView = getRepositoryView(itemDescriptor);
                QueryBuilder queryBuilder = repositoryView.getQueryBuilder();

                QueryExpression queryExpressionOne = queryBuilder.createPropertyQueryExpression(propertyNameOne);
                QueryExpression valueExpressionOne = queryBuilder.createConstantQueryExpression(propertyValueOne);
                Query queryOne = queryBuilder.createPatternMatchQuery(queryExpressionOne, valueExpressionOne,
                        QueryBuilder.EQUALS);

                QueryExpression queryExpressionTwo = queryBuilder.createPropertyQueryExpression(propertyNameTwo);
                QueryExpression valueExpressionTwo = queryBuilder.createConstantQueryExpression(propertyValueTwo);
                Query queryTwo = queryBuilder.createPatternMatchQuery(queryExpressionTwo, valueExpressionTwo,
                        QueryBuilder.EQUALS);

                Query[] queries = {queryOne, queryTwo};
                Query orQuery = queryBuilder.createOrQuery(queries);

                repositoryItems = repositoryView.executeQuery(orQuery);
            } else if (OPERATION_AND.equals(operation)) {
                repositoryItems = getItemByProperty(itemDescriptor, propertyNameOne, propertyValueOne, propertyNameTwo,
                        propertyValueTwo);
            }
        }

        return repositoryItems;
    }
    
    public class  PropertyAttribute {
    	
	    /** The property name. */
	    private String propertyName;
    	
	    /** The property value. */
	    private String propertyValue;
    	
	    /** The operation. */
	    private String operation;

		/**
		 * @return the propertyName
		 */
		public String getPropertyName() {
			return propertyName;
		}

		/**
		 * @param pPropertyName the propertyName to set
		 */
		public void setPropertyName(String pPropertyName) {
			propertyName = pPropertyName;
		}

		/**
		 * @return the propertyValue
		 */
		public String getPropertyValue() {
			return propertyValue;
		}

		/**
		 * @param pPropertyValue the propertyValue to set
		 */
		public void setPropertyValue(String pPropertyValue) {
			propertyValue = pPropertyValue;
		}

		/**
		 * @return the operation
		 */
		public String getOperation() {
			return operation;
		}

		/**
		 * @param pOperation the operation to set
		 */
		public void setOperation(String pOperation) {
			operation = pOperation;
		}
    }
}
