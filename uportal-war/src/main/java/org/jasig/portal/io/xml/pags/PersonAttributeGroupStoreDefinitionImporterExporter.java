/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.io.xml.pags;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jasig.portal.io.xml.AbstractJaxbDataHandler;
import org.jasig.portal.io.xml.IPortalData;
import org.jasig.portal.io.xml.IPortalDataType;
import org.jasig.portal.io.xml.PortalDataKey;
import org.jasig.portal.io.xml.pags.ExternalGroupStoreDefinition.Group;
import org.jasig.portal.io.xml.pags.ExternalGroupStoreDefinition.Group.Members;
import org.jasig.portal.io.xml.pags.ExternalGroupStoreDefinition.Group.SelectionTest;
import org.jasig.portal.io.xml.pags.ExternalGroupStoreDefinition.Group.SelectionTest.TestGroup;
import org.jasig.portal.io.xml.pags.ExternalGroupStoreDefinition.Group.SelectionTest.TestGroup.Test;
import org.jasig.portal.pags.dao.IPersonAttributeGroupDefinitionDao;
import org.jasig.portal.pags.dao.IPersonAttributeGroupStoreDefinitionDao;
import org.jasig.portal.pags.dao.IPersonAttributeGroupTestDefinitionDao;
import org.jasig.portal.pags.dao.IPersonAttributeGroupTestGroupDefinitionDao;
import org.jasig.portal.pags.dao.jpa.PersonAttributeGroupDefinitionImpl;
import org.jasig.portal.pags.dao.jpa.PersonAttributeGroupStoreDefinitionImpl;
import org.jasig.portal.pags.dao.jpa.PersonAttributeGroupTestGroupDefinitionImpl;
import org.jasig.portal.pags.om.IPersonAttributeGroupDefinition;
import org.jasig.portal.pags.om.IPersonAttributeGroupStoreDefinition;
import org.jasig.portal.pags.om.IPersonAttributeGroupTestGroupDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Shawn Connolly, sconnolly@unicon.net
 */
public class PersonAttributeGroupStoreDefinitionImporterExporter 
        extends AbstractJaxbDataHandler<ExternalGroupStoreDefinition> {
	
    private PersonAttributeGroupStorePortalDataType personAttributeGroupStorePortalDataType;
    private IPersonAttributeGroupStoreDefinitionDao personAttributeGroupStoreDefinitionDao;
    private IPersonAttributeGroupDefinitionDao personAttributeGroupDefinitionDao;
    private IPersonAttributeGroupTestDefinitionDao personAttributeGroupTestDefinitionDao;
    private IPersonAttributeGroupTestGroupDefinitionDao personAttributeGroupTestGroupDefinitionDao;
    private Logger logger = LoggerFactory.getLogger(PersonAttributeGroupStoreDefinitionImporterExporter.class);
    private boolean errorOnChannel = true;

    @Value("${org.jasig.portal.io.errorOnChannel}")
	public void setErrorOnChannel(boolean errorOnChannel) {
		this.errorOnChannel = errorOnChannel;
	}

	@Autowired
    public void setpersonAttributeGroupStorePortalDataType(PersonAttributeGroupStorePortalDataType personAttributeGroupStorePortalDataType) {
        this.personAttributeGroupStorePortalDataType = personAttributeGroupStorePortalDataType;
    }

	@Autowired
    public void setPersonAttributeGroupStoreDefinitionDao(IPersonAttributeGroupStoreDefinitionDao personAttributeGroupStoreDefinitionDao) {
        this.personAttributeGroupStoreDefinitionDao = personAttributeGroupStoreDefinitionDao;
    }

	@Autowired
    public void setPersonAttributeGroupDefinitionDao(IPersonAttributeGroupDefinitionDao personAttributeGroupDefinitionDao) {
        this.personAttributeGroupDefinitionDao = personAttributeGroupDefinitionDao;
    }

	@Autowired
    public void setPersonAttributeGroupTestDefinitionDao(IPersonAttributeGroupTestDefinitionDao personAttributeGroupTestDefinitionDao) {
        this.personAttributeGroupTestDefinitionDao = personAttributeGroupTestDefinitionDao;
    }

	@Autowired
    public void setPersonAttributeGroupTestGroupDefinitionDao(IPersonAttributeGroupTestGroupDefinitionDao personAttributeGroupTestGroupDefinitionDao) {
        this.personAttributeGroupTestGroupDefinitionDao = personAttributeGroupTestGroupDefinitionDao;
    }
	
    @Override
    public Set<PortalDataKey> getImportDataKeys() {
        return new HashSet<PortalDataKey>(personAttributeGroupStorePortalDataType.getDataKeyImportOrder());
    }

    @Override
    public Iterable<? extends IPortalData> getPortalData() {
        return null;//this.personAttributeGroupStoreDefinitionDao.getPersonAttributeGroupStoreDefinitions();
    }

    /**
     * Entry point for importing of data
     */
    @Transactional
    @Override
    public void importData(ExternalGroupStoreDefinition groupStoreRep) {
        String name = groupStoreRep.getStoreName();
        String description = groupStoreRep.getStoreDescription();
        logStoreInfo(name, description);
        IPersonAttributeGroupStoreDefinition personAttributeGroupStoreDefinition = 
                personAttributeGroupStoreDefinitionDao.createPersonAttributeGroupStoreDefinition(name, description);
        List<Group> groups = groupStoreRep.getGroups();
        importGroups(personAttributeGroupStoreDefinition, groups);
        // Circle back and populate the members of groups, now that all the groups have been created
        importGroupMembers(groups);
    }

    private void importGroups(IPersonAttributeGroupStoreDefinition personAttributeGroupStoreDefinition, List<Group> groups) {
        for(Group group: groups) {
            String groupName = group.getGroupName();
            String groupDescription = group.getGroupDescription();
            logGroupsInfo(group, groupName);
            IPersonAttributeGroupDefinition personAttributeGroupDefinition = 
                    personAttributeGroupDefinitionDao.createPersonAttributeGroupDefinition((PersonAttributeGroupStoreDefinitionImpl)personAttributeGroupStoreDefinition, 
                                                                                            groupName, 
                                                                                            groupDescription);
            SelectionTest selectionTest = group.getSelectionTest();
            if (null != selectionTest) {
                logger.trace("------- Test Groups -------");
                importTestGroups(personAttributeGroupDefinition, selectionTest);
            }
        }
    }

    private void importTestGroups(IPersonAttributeGroupDefinition personAttributeGroupDefinition, SelectionTest selectionTest) {
        List<TestGroup> testGroups = selectionTest.getTestGroups();
        for(TestGroup testGroup: testGroups) {
            String testGroupName = testGroup.getTestGroupName();
            String testGroupDescription = testGroup.getTestGroupDescription();
            logTestGroupsInfo(testGroupName, testGroupDescription);
            IPersonAttributeGroupTestGroupDefinition createdTestGroup =
                    personAttributeGroupTestGroupDefinitionDao.createPersonAttributeGroupTestGroupDefinition((PersonAttributeGroupDefinitionImpl) personAttributeGroupDefinition,
                                                                                                             testGroupName,
                                                                                                             testGroupDescription);
            List<Test> tests = testGroup.getTests();
            importTests(createdTestGroup, tests);
        }
    }

    private void importTests(IPersonAttributeGroupTestGroupDefinition createdTestGroup, List<Test> tests) {
        for(Test test: tests) {
            String attributeName = test.getAttributeName();
            String testerClass = test.getTesterClass();
            String testValue = test.getTestValue();
            logTestInfo(attributeName, testerClass, testValue);
            personAttributeGroupTestDefinitionDao.createPersonAttributeGroupTestDefinition((PersonAttributeGroupTestGroupDefinitionImpl)createdTestGroup,
                                                                                           attributeName,
                                                                                           testerClass,
                                                                                           testValue);
        }
    }
    
    private void importGroupMembers(List<Group> groups) {
        for(Group group: groups) {
            Members members = group.getMembers();
            if (null != members) {
                List<PersonAttributeGroupDefinitionImpl> parentPag =
                        personAttributeGroupDefinitionDao.getPersonAttributeGroupDefinitionByName(group.getGroupName());
                Set<PersonAttributeGroupDefinitionImpl> membersToSave = new HashSet<PersonAttributeGroupDefinitionImpl>();
                List<String> memberKeys = members.getMemberKeies();
                logger.trace("------ Members -------");
                for(String memberKey: memberKeys) {
                    logger.trace("Member key: {}", memberKey);
                    List<PersonAttributeGroupDefinitionImpl> memberPag =
                            personAttributeGroupDefinitionDao.getPersonAttributeGroupDefinitionByName(memberKey);
                    membersToSave.add(memberPag.get(0));
                }
                parentPag.get(0).setMembers(membersToSave);
                personAttributeGroupDefinitionDao.updatePersonAttributeGroupDefinition(parentPag.get(0));
            }
        }
        
    }
    
    private void logStoreInfo(String name, String description) {
        logger.trace("Store Name: {}", name);
        logger.trace("Store Description: {}", description);
    }
    
    private void logGroupsInfo(Group group, String groupKey) {
        logger.trace("------ Group ------");
        logger.trace("Group Key: {}", groupKey);
        logger.trace("Group Name: {}", group.getGroupName());
        logger.trace("Group Description: {}", group.getGroupDescription());
    }
    
    private void logTestGroupsInfo(String testGroupName, String testGroupDescription) {
        logger.trace("Test Group Name: {}", testGroupName);
        logger.trace("Test Group Description: {}", testGroupDescription);
    }
    
    private void logTestInfo(String attributeName, String testerClass, String testValue) {
        logger.trace("Test Attribute Name: {}", attributeName);
        logger.trace("Tester Class: {}", testerClass);
        logger.trace("Test Value: {}", testValue);
    }
    
    @Override
    public IPortalDataType getPortalDataType() {
        return personAttributeGroupStorePortalDataType;
    }

    @Override
    public ExternalGroupStoreDefinition exportData(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFileName(ExternalGroupStoreDefinition data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExternalGroupStoreDefinition deleteData(String id) {
        // TODO Auto-generated method stub
        return null;
    }

}
