package br.gov.mec.aghu.core.persistence;

import javax.persistence.MappedSuperclass;

import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Super classe para as entidades persistentes.
 * 
 * @author rcorvalao
 *
 */
@MappedSuperclass
public interface BaseEntity extends BaseBean {
    
    
}
