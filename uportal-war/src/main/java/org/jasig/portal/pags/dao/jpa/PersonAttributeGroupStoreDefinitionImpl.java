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

package org.jasig.portal.pags.dao.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.pags.om.IPersonAttributeGroupStoreDefinition;

/**
 * @author Shawn Connolly, sconnolly@unicon.net
 */
@Entity
@Table(name = "UP_PAG_STORE_DEF")
@SequenceGenerator(
        name="UP_PAG_STORE_DEF_GEN",
        sequenceName="UP_PAG_STORE_DEF_SEQ",
        allocationSize=5
    )
@TableGenerator(
        name="UP_PAG_STORE_DEF_GEN",
        pkColumnValue="UP_PAG_STORE_DEF",
        allocationSize=5
    )
@NaturalIdCache
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PersonAttributeGroupStoreDefinitionImpl implements IPersonAttributeGroupStoreDefinition {
    public PersonAttributeGroupStoreDefinitionImpl() {
        super();
    }
    public PersonAttributeGroupStoreDefinitionImpl(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

    //Properties are final to stop changes in code, hibernate overrides the final via reflection to set their values
    @Id
    @GeneratedValue(generator = "UP_PAG_STORE_DEF_GEN")
    @Column(name = "PAG_STORE_DEF_ID")
    private long internalPersonAttributeGroupStoreDefinitionId;
    
    @Version
    @Column(name = "ENTITY_VERSION")
    private long entityVersion;
    
    @Column(name = "NAME", length=500, nullable = true, updatable = true)
    private String name;
    
    @Column(name = "DESCRIPTION", length=500, nullable = true, updatable = true)
    private String description;
    
    
    @OneToMany(mappedBy="store")
    private Set<PersonAttributeGroupDefinitionImpl> personAttributeGroupDefinitions = new HashSet<PersonAttributeGroupDefinitionImpl>(0);

    @Override
    public EntityIdentifier getEntityIdentifier() {
        return new EntityIdentifier(String.valueOf(this.internalPersonAttributeGroupStoreDefinitionId), PersonAttributeGroupStoreDefinitionImpl.class);
    }

    @Override
    public String getDataId() {
        return this.name;
    }


    @Override
    public String getDataTitle() {
        return this.name;
    }


    @Override
    public String getDataDescription() {
        return this.description;
    }

    @Override
    public String getStoreName() {
        return name;
    }

    @Override
    public void setStoreName(String storeName) {
        this.name = storeName;
    }

    @Override
    public String getStoreDescription() {
        return description;
    }

    @Override
    public void setStoreDescription(String storeDescription) {
        this.description = storeDescription;
    }
    
    @Override
    public Set<PersonAttributeGroupDefinitionImpl> getGroups() {
        return this.personAttributeGroupDefinitions;
    }
    
    @Override
    public void setGroups(Set<PersonAttributeGroupDefinitionImpl> groups) {
        this.personAttributeGroupDefinitions = groups;
    }

}
