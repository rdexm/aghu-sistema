package br.gov.mec.aghu.sicon.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import br.gov.mec.aghu.dominio.DominioPossuiSIASG;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;


public class LicitacaoFiltroVO {
	
	/*** Filtro ***/
	private ScoLicitacao licitacao;
	private Integer numeroAf;
	private Integer nroComplementoAf;
	private ScoMaterial material;
	private ScoServico servico;
	private ScoGrupoMaterial grupoMaterial;
	private ScoGrupoServico grupoServico;
	private DominioTipoItemContrato tipoItens;
	private DominioPossuiSIASG codSiasg;
	private ScoFornecedor fornecedor;
	
	public LicitacaoFiltroVO() {
		super();
	}
	
	public LicitacaoFiltroVO(ScoLicitacao licitacao) {
		super();
		this.licitacao = licitacao;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public DominioTipoItemContrato getTipoItens() {
		return tipoItens;
	}

	public void setTipoItens(DominioTipoItemContrato tipoItens) {
		this.tipoItens = tipoItens;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public DominioPossuiSIASG getCodSiasg() {
		return codSiasg;
	}
	
	public void setCodSiasg(DominioPossuiSIASG codSiasg) {
		this.codSiasg = codSiasg;
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Integer getNroComplementoAf() {
		return nroComplementoAf;
	}

	public void setNroComplementoAf(Integer nroComplementoAf) {
		this.nroComplementoAf = nroComplementoAf;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(licitacao).append(nroComplementoAf)
				.append(material).append(servico).append(grupoServico)
				.append(grupoMaterial).append(tipoItens).append(codSiasg)
				.append(fornecedor).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
	if (obj == null) {
		return false;
	}
	LicitacaoFiltroVO other = (LicitacaoFiltroVO) obj;
	return new EqualsBuilder().append(numeroAf, other.numeroAf)
			.append(nroComplementoAf, other.nroComplementoAf)
			.append(licitacao, other.licitacao)
			.append(material, other.material)
			.append(servico, other.servico)
			.append(grupoMaterial, other.grupoMaterial)
			.append(grupoServico, other.grupoServico)
			.append(tipoItens, other.tipoItens)
			.append(codSiasg, other.codSiasg)
			.append(fornecedor, other.fornecedor).isEquals();
	}
}
