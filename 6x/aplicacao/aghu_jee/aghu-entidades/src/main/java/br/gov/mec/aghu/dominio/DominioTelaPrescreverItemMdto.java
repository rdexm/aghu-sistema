package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTelaPrescreverItemMdto implements Dominio {

	MPMF_NOTIFICACAO_TB("cadastroSinam.xhtml"), 
	MPMF_JUST_QT("cadastroJustificativaMedicamentoUsoQuimioterapico.xhtml"), 
	MPMF_JUST_UR("cadastroJustificativaMedicamentoUsoRestrito.xhtml"), 
	MPMF_JUST_NS("cadastroJustificativaMedicamentoNaoSelecionado.xhtml"),
	MPMF_JUST_UR_MICROB("cadastroJustificativaMedicamentoUsoRestritoAntimicrobiano.xhtml"), 
	MPMF_JUST_NS_MICROB("cadastroJustificativaMedicamentoUsoNsAntimicrobiano.xhtml"), 
	MPMF_ATU_JUST_MDTO("atualizaJustificativaUsoMedicamento.xhtml");

	private String xhtml;

	private DominioTelaPrescreverItemMdto(String xhtml) {
		this.xhtml = xhtml;
	}

	@Override
	public String toString() {
		return this.xhtml;
	}

	@Override
	public int getCodigo() {
		return 0;
	}

	@Override
	public String getDescricao() {
		return this.xhtml;
	}

	public String getXhtml() {
		return xhtml;
	}

}