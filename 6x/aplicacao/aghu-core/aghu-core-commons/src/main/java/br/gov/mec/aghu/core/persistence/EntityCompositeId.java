package br.gov.mec.aghu.core.persistence;

import java.io.Serializable;

/**
 * Marcador para classes que são Pk composta no sistema.
 * 
 * @author rcorvalao
 *
 */
public interface EntityCompositeId extends Serializable {
	
	boolean equals(Object other);
	
	int hashCode();

}
