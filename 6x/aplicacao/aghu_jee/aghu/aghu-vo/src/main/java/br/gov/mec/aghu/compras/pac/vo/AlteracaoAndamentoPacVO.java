package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;


/**
 * VO responsável por armazenar status de um PAC após sua alteração.
 * 
 * @author mlcruz
 */
public class AlteracaoAndamentoPacVO {
	/** ID do PAC */
	private Integer pacId;
	
	/** Local Destino */
	private ScoLocalizacaoProcesso local;
	
	/** Situação */
	private String situacao;

	private Boolean mostraAlerta;
	
	// Getters/Setters

	public Integer getPacId() {
		return pacId;
	}

	public void setPacId(Integer pacId) {
		this.pacId = pacId;
	}

	public ScoLocalizacaoProcesso getLocal() {
		return local;
	}

	public void setLocal(ScoLocalizacaoProcesso local) {
		this.local = local;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Boolean getMostraAlerta() {
		return mostraAlerta;
	}

	public void setMostraAlerta(Boolean mostraAlerta) {
		this.mostraAlerta = mostraAlerta;
	}
}