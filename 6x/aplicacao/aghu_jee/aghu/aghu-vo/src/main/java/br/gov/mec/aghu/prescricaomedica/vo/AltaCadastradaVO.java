package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSumarioAlta;
import br.gov.mec.aghu.model.MpmAltaRecomendacaoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.VMpmServRecomAltasId;

public class AltaCadastradaVO implements Serializable {

	private static final long serialVersionUID = 2113785765086942436L;

	private VMpmServRecomAltasId id;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indRecomendado;
	private DominioTipoSumarioAlta indTipoSumrAlta;
	private Boolean gravar = false;
	private Boolean emEdicao = false;
	private MpmAltaSumario altaSumario;
	private MpmAltaRecomendacaoId altaRecomendacaoId;

	public AltaCadastradaVO() {
		super();
	}

	public AltaCadastradaVO(VMpmServRecomAltasId id, String descricao,
			DominioSituacao indSituacao, Boolean indRecomendado,
			DominioTipoSumarioAlta indTipoSumrAlta, Boolean gravar) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.indRecomendado = indRecomendado;
		this.indTipoSumrAlta = indTipoSumrAlta;
		this.gravar = gravar;
	}

	public VMpmServRecomAltasId getId() {
		return id;
	}

	public void setId(VMpmServRecomAltasId id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getIndRecomendado() {
		return indRecomendado;
	}

	public void setIndRecomendado(Boolean indRecomendado) {
		this.indRecomendado = indRecomendado;
	}

	public DominioTipoSumarioAlta getIndTipoSumrAlta() {
		return indTipoSumrAlta;
	}

	public void setIndTipoSumrAlta(DominioTipoSumarioAlta indTipoSumrAlta) {
		this.indTipoSumrAlta = indTipoSumrAlta;
	}

	public Boolean getGravar() {
		return gravar;
	}

	public void setGravar(Boolean gravar) {
		this.gravar = gravar;
	}

	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public MpmAltaRecomendacaoId getAltaRecomendacaoId() {
		return altaRecomendacaoId;
	}

	public void setAltaRecomendacaoId(MpmAltaRecomendacaoId altaRecomendacaoId) {
		this.altaRecomendacaoId = altaRecomendacaoId;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
}
