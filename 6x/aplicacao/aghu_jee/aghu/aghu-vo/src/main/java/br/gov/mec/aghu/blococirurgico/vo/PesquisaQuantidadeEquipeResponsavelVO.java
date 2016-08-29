package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

/**
 * Classe VO com atributos análogos aos parâmetros de saída (OUT) na PROCEDURE MBCP_BUSCA_EQUIPE_RESP
 * 
 * @author aghu
 * 
 */
public class PesquisaQuantidadeEquipeResponsavelVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1903170048072332667L;
	private CirurgiaTelaProfissionalVO profissionalResponsavel;
	private Integer contaResponsaveis;

	public CirurgiaTelaProfissionalVO getProfissionalResponsavel() {
		return profissionalResponsavel;
	}

	public void setProfissionalResponsavel(CirurgiaTelaProfissionalVO profCirurgias) {
		this.profissionalResponsavel = profCirurgias;
	}

	public Integer getContaResponsaveis() {
		return contaResponsaveis;
	}

	public void setContaResponsaveis(Integer contaResponsaveis) {
		this.contaResponsaveis = contaResponsaveis;
	}

}
