package br.gov.mec.aghu.core.commons;

import java.io.Serializable;

/**
 * Super classe para tipar e marcar os beans que devem implementar equals e hashcode.<br>
 * Obviamente que não se tem garantias que os metodos existam e estejam implementados corretamente.<br>
 * O objetivoa maior eh tipar. 
 * 
 * @author rcorvalao
 *
 */
public interface BaseBean extends Serializable {
	
    public abstract int hashCode();

    public abstract boolean equals(Object obj);

}
