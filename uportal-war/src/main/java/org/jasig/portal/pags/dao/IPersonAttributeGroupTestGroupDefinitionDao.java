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

package org.jasig.portal.pags.dao;

import java.util.List;

import org.jasig.portal.pags.dao.jpa.PersonAttributeGroupDefinitionImpl;
import org.jasig.portal.pags.dao.jpa.PersonAttributeGroupTestGroupDefinitionImpl;
import org.jasig.portal.pags.om.IPersonAttributeGroupTestDefinition;
import org.jasig.portal.pags.om.IPersonAttributeGroupTestGroupDefinition;

/**
 * Provides APIs for creating, storing and retrieving {@link IPersonAttributeGroupTestGroupDefinition} objects.
 * 
 * @author Shawn Connolly, sconnolly@unicon.net
 */
public interface IPersonAttributeGroupTestGroupDefinitionDao {

    IPersonAttributeGroupTestGroupDefinition updatePersonAttributeGroupTestGroupDefinition(IPersonAttributeGroupTestGroupDefinition personAttributeGroupTestGroupDefinition);
	void deletePersonAttributeGroupTestGroupDefinition(IPersonAttributeGroupTestGroupDefinition definition);
    IPersonAttributeGroupTestGroupDefinition getPersonAttributeGroupTestGroupDefinition(Long testId);
    List<PersonAttributeGroupTestGroupDefinitionImpl> getPersonAttributeGroupTestGroupDefinitionByName(String name);
    IPersonAttributeGroupTestGroupDefinition createPersonAttributeGroupTestGroupDefinition(PersonAttributeGroupDefinitionImpl group, String name, String description);
    List<IPersonAttributeGroupTestGroupDefinition> getPersonAttributeGroupTestGroupDefinitions();

}
