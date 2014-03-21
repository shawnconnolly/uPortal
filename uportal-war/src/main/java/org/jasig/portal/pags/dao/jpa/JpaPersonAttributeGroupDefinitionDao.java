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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.Validate;
import org.jasig.portal.jpa.BasePortalJpaDao;
import org.jasig.portal.jpa.OpenEntityManager;
import org.jasig.portal.pags.dao.IPersonAttributeGroupDefinitionDao;
import org.jasig.portal.pags.om.IPersonAttributeGroupDefinition;
import org.springframework.stereotype.Repository;

import com.google.common.base.Function;

/**
 * JPA DAO for stylesheet descriptor
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
@Repository("personAttributeGroupDefinitionDao")
public class JpaPersonAttributeGroupDefinitionDao extends BasePortalJpaDao implements IPersonAttributeGroupDefinitionDao {
    private CriteriaQuery<PersonAttributeGroupDefinitionImpl> findAllDefinitions;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        this.findAllDefinitions = this.createCriteriaQuery(new Function<CriteriaBuilder, CriteriaQuery<PersonAttributeGroupDefinitionImpl>>() {
            @Override
            public CriteriaQuery<PersonAttributeGroupDefinitionImpl> apply(CriteriaBuilder cb) {
                final CriteriaQuery<PersonAttributeGroupDefinitionImpl> criteriaQuery = cb.createQuery(PersonAttributeGroupDefinitionImpl.class);
                criteriaQuery.from(PersonAttributeGroupDefinitionImpl.class);
                return criteriaQuery;
            }
        });
    }

    @PortalTransactional
    @Override
    public IPersonAttributeGroupDefinition updatePersonAttributeGroupDefinition(IPersonAttributeGroupDefinition personAttributeGroupDefinition) {
        Validate.notNull(personAttributeGroupDefinition, "personAttributeGroupDefinition can not be null");
        
        this.getEntityManager().persist(personAttributeGroupDefinition);
        return personAttributeGroupDefinition;
    }

    @PortalTransactional
    @Override
    public void deletePersonAttributeGroupDefinition(IPersonAttributeGroupDefinition definition) {
        Validate.notNull(definition, "definition can not be null");
        
        final IPersonAttributeGroupDefinition persistentDefinition;
        final EntityManager entityManager = this.getEntityManager();
        if (entityManager.contains(definition)) {
            persistentDefinition = definition;
        }
        else {
            persistentDefinition = entityManager.merge(definition);
        }
        
        entityManager.remove(persistentDefinition);
    }

    @OpenEntityManager(unitName = PERSISTENCE_UNIT_NAME)
    @Override
    public List<PersonAttributeGroupDefinitionImpl> getPersonAttributeGroupDefinitionByName(String name) {
        CriteriaBuilder criteriaBuilder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PersonAttributeGroupDefinitionImpl> criteriaQuery = 
                criteriaBuilder.createQuery(PersonAttributeGroupDefinitionImpl.class);
        Root<PersonAttributeGroupDefinitionImpl> root = criteriaQuery.from(PersonAttributeGroupDefinitionImpl.class);
        ParameterExpression<String> nameParameter = criteriaBuilder.parameter(String.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), nameParameter));
        TypedQuery<PersonAttributeGroupDefinitionImpl> query = this.getEntityManager().createQuery(criteriaQuery);
        query.setParameter(nameParameter, name);
        return query.getResultList();
    }

    @Override
    public List<PersonAttributeGroupDefinitionImpl> getPersonAttributeGroupDefinitions() {
        final TypedQuery<PersonAttributeGroupDefinitionImpl> query = this.createCachedQuery(this.findAllDefinitions);
        return query.getResultList();
    }
    
    @PortalTransactional
    @Override
    public IPersonAttributeGroupDefinition createPersonAttributeGroupDefinition(PersonAttributeGroupStoreDefinitionImpl store, String name, String description) {
        final IPersonAttributeGroupDefinition personAttributeGroupDefinition = new PersonAttributeGroupDefinitionImpl(store, name, description);
        
        this.getEntityManager().persist(personAttributeGroupDefinition);
        
        return personAttributeGroupDefinition;
    }

}
