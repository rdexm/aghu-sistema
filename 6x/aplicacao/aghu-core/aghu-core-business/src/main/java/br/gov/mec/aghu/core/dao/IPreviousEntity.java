package br.gov.mec.aghu.core.dao;

import java.io.Serializable;

import br.gov.mec.aghu.core.persistence.BaseEntity;

/**
 * @deprecated usar PreviousEntitySearcher
 * @author cvagheti
 *
 */
public interface IPreviousEntity {

	public <E extends BaseEntity> E getPrevious(Class<E> clazz, Serializable id);

}
