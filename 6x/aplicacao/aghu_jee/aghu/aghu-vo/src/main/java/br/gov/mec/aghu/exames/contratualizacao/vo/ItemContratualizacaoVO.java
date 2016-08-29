package br.gov.mec.aghu.exames.contratualizacao.vo;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

public class ItemContratualizacaoVO {
	
	private String mensagemErro;

	private AelItemSolicitacaoExames itemSolicitacaoExames;

	private String idExterno;
	
	private String exame;
	private String materialAnalise;
	private String unidadeFuncional;

	public String getMensagemErro() {
		return mensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	public AelItemSolicitacaoExames getItemSolicitacaoExames() {
		return itemSolicitacaoExames;
	}

	public void setItemSolicitacaoExames(
			AelItemSolicitacaoExames itemSolicitacaoExames) {
		this.itemSolicitacaoExames = itemSolicitacaoExames;
	}

	public String getIdExterno() {
		return idExterno;
	}

	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getMaterialAnalise() {
		return materialAnalise;
	}

	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public String getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(String unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
}
