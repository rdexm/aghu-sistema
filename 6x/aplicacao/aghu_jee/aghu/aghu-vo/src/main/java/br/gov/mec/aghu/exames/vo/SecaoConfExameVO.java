package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioWorkflowExamePatologia;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelSecaoConfExames;

public class SecaoConfExameVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1342807936211169574L;
	
	private AelConfigExLaudoUnico configExameLaudoUnico;
	private AelSecaoConfExames secaoConfExames;
	private Boolean ativo;
	private Boolean obrigatorio;
	private Boolean impressao;
	private DominioWorkflowExamePatologia secaoObrigatoria;

	// GETTERS E SETTERS
	
	public AelConfigExLaudoUnico getConfigExameLaudoUnico() {
		return configExameLaudoUnico;
	}

	public void setConfigExameLaudoUnico(AelConfigExLaudoUnico configExameLaudoUnico) {
		this.configExameLaudoUnico = configExameLaudoUnico;
	}	

	public AelSecaoConfExames getSecaoConfExames() {
		return secaoConfExames;
	}

	public void setSecaoConfExames(AelSecaoConfExames secaoConfExames) {
		this.secaoConfExames = secaoConfExames;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
		if(!ativo){
			this.impressao = Boolean.FALSE;
			this.obrigatorio = Boolean.FALSE;
			this.secaoObrigatoria = null;
		}
	}

	public Boolean getObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(Boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
		if(!obrigatorio){
			this.secaoObrigatoria = null;
		}
	}

	public Boolean getImpressao() {
		return impressao;
	}

	public void setImpressao(Boolean impressao) {
		this.impressao = impressao;
	}

	public DominioWorkflowExamePatologia getSecaoObrigatoria() {
		return secaoObrigatoria;
	}

	public void setSecaoObrigatoria(DominioWorkflowExamePatologia secaoObrigatoria) {
		this.secaoObrigatoria = secaoObrigatoria;
	}
	
	public Boolean getHabilitaSecaoObrigatoria(){
		return obrigatorio;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((secaoConfExames == null) ? 0 : secaoConfExames.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof SecaoConfExameVO)){
			return false;
		}
		SecaoConfExameVO other = (SecaoConfExameVO) obj;
		if (secaoConfExames == null && other.secaoConfExames != null) {
			return false;
		} else if (!secaoConfExames.equals(other.secaoConfExames)){
			return false;
		}
		return true;
	}
	
}