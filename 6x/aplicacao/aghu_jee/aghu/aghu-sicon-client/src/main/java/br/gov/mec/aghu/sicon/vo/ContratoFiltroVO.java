package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;

@SuppressWarnings("ucd")
public class ContratoFiltroVO implements Serializable {

	private static final long serialVersionUID = 1188383059328858430L;
	private ScoContrato contrato;
	private ScoAutorizacaoForn af;
	private DominioSituacaoEnvioContrato sitEnvAditivo;
	private DominioSituacaoEnvioContrato sitEnvResc;
	private ScoMaterial material;
	private ScoServico servico;
	private ScoGrupoMaterial grupoMaterial;
	private ScoGrupoServico grupoServico;
	private DominioTipoItemContrato tipoItens;
	private DominioSimNao estocavel;

	public ContratoFiltroVO(ScoContrato contrato, ScoAutorizacaoForn af) {
		super();
		this.contrato = contrato;
		this.af = af;
	}

	public ContratoFiltroVO(ScoContrato contrato) {
		super();
		this.contrato = contrato;
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public ScoAutorizacaoForn getAf() {
		return af;
	}

	public void setAf(ScoAutorizacaoForn af) {
		this.af = af;
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

	public DominioSimNao getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(DominioSimNao estocavel) {
		this.estocavel = estocavel;
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

	public DominioSituacaoEnvioContrato getSitEnvAditivo() {
		return sitEnvAditivo;
	}

	public void setSitEnvAditivo(DominioSituacaoEnvioContrato sitEnvAditivo) {
		this.sitEnvAditivo = sitEnvAditivo;
	}

	public DominioSituacaoEnvioContrato getSitEnvResc() {
		return sitEnvResc;
	}

	public void setSitEnvResc(DominioSituacaoEnvioContrato sitEnvResc) {
		this.sitEnvResc = sitEnvResc;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(af).append(contrato)
				.append(estocavel).append(grupoMaterial).append(grupoServico)
				.append(material).append(servico).append(sitEnvAditivo)
				.append(sitEnvResc)
				.append(tipoItens)
				.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		ContratoFiltroVO other = (ContratoFiltroVO) obj;
		return new EqualsBuilder().append(af, other.af)
				.append(contrato, other.contrato)
				.append(estocavel, other.estocavel)
				.append(grupoMaterial, other.grupoMaterial)
				.append(grupoServico, other.grupoServico)
				.append(servico, other.servico)
				.append(material, other.grupoServico)
				.append(tipoItens, other.tipoItens)
				.append(sitEnvAditivo, other.sitEnvAditivo)
				.append(sitEnvResc, other.sitEnvResc).isEquals();
	}
}
