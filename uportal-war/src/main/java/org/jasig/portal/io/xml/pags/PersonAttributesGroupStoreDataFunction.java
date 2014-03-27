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

import java.util.List;

import org.jasig.portal.io.xml.IPortalData;
import org.jasig.portal.io.xml.IPortalDataType;
import org.jasig.portal.io.xml.SimpleStringPortalData;
import org.jasig.portal.pags.dao.IPersonAttributesGroupDefinitionDao;
import org.jasig.portal.pags.om.IPersonAttributesGroupDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Lists each PAGS Group definition in the database
 * 
 * @author Shawn Connolly, sconnolly@unicon.net
 */
public class PersonAttributesGroupStoreDataFunction implements Function<IPortalDataType, Iterable<? extends IPortalData>> {
    private IPersonAttributesGroupDefinitionDao personAttributesGroupDefinitionDao;
    
    @Autowired
    public void setpersonAttributesGroupDefinitionDao(IPersonAttributesGroupDefinitionDao personAttributesGroupDefinitionDao) {
        this.personAttributesGroupDefinitionDao = personAttributesGroupDefinitionDao;
    }

	@Override
    public Iterable<? extends IPortalData> apply(IPortalDataType input) {
		final List<IPersonAttributesGroupDefinition> personAttributesGroupDefinitions = this.personAttributesGroupDefinitionDao.getPersonAttributesGroupDefinitions();
        
        final List<IPortalData> portalData = Lists.transform(personAttributesGroupDefinitions, new Function<IPersonAttributesGroupDefinition, IPortalData>() {
            @Override
            public IPortalData apply(IPersonAttributesGroupDefinition personAttributesGroup) {
                return new SimpleStringPortalData(
                        personAttributesGroup.getName(),
                        null,
                        personAttributesGroup.getDescription());
            }
        });
        
        return portalData;
    }
}
