package br.gov.mec.aghu.dominio;

import java.io.Serializable;

/**
 * Exporta a enumeracao de Grupo Sanguíneo (valores especificos para
 * Atendimento Diverso) para ser usado nas telas do JSF.
 * 
 * @author dpacheco
 * 
 */
//@Name("dominioGrupoSanguineoAtendimentoDiversoItens")
//@Scope(ScopeType.APPLICATION)
//@BypassInterceptors
//@AutoCreate
public class DominioGrupoSanguineoAtendimentoDiversoItens implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2044573878250629920L;
	
	//@Unwrap
	public DominioGrupoSanguineoAtendimentoDiverso[] getDominioGrupoSanguineoAtendimentoDiverso() {
		return DominioGrupoSanguineoAtendimentoDiverso.values();
	}

}