/**
 * 
 */
package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTelaPrescreverItemMdto;

/**
 * @author aghu
 * 
 */
public class VerificarDadosItensJustificativaPrescricaoVO implements Serializable {

	private static final long serialVersionUID = 556160714978547386L;
	
	private DominioTelaPrescreverItemMdto tela;
	private Integer seqNotificacao;
	
	public DominioTelaPrescreverItemMdto getTela() {
		return tela;
	}
	public void setTela(DominioTelaPrescreverItemMdto tela) {
		this.tela = tela;
	}
	public Integer getSeqNotificacao() {
		return seqNotificacao;
	}
	public void setSeqNotificacao(Integer seqNotificacao) {
		this.seqNotificacao = seqNotificacao;
	}

}
