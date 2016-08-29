package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioQuadroPropostasLicitacaoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 164849734521207628L;

	private String modalidade;
	private Integer pac;
	private String dtAbertura;
	private String hora;

	private List<ItemQuadroPropostasVO> listaItemPropostas;
	private List<FornecedoresParticipantesVO> listaFornecedorParticipante;
		
	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public Integer getPac() {
		return pac;
	}

	public void setPac(Integer pac) {
		this.pac = pac;
	}

	public String getDtAbertura() {
		return dtAbertura;
	}

	public void setDtAbertura(String dtAbertura) {
		this.dtAbertura = dtAbertura;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public List<ItemQuadroPropostasVO> getListaItemPropostas() {
		return listaItemPropostas;
	}
	
	public void setListaItemPropostas(List<ItemQuadroPropostasVO> listaItemPropostas) {
		this.listaItemPropostas = listaItemPropostas;
	}

	public List<FornecedoresParticipantesVO> getListaFornecedorParticipante() {
		return listaFornecedorParticipante;
	}

	public void setListaFornecedorParticipante(
			List<FornecedoresParticipantesVO> listaFornecedorParticipante) {
		this.listaFornecedorParticipante = listaFornecedorParticipante;
	}

	
}
