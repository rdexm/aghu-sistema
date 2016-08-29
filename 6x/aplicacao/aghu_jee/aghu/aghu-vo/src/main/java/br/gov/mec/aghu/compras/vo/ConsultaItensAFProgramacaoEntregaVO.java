package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;

public class ConsultaItensAFProgramacaoEntregaVO implements Serializable {

	private static final long serialVersionUID = 2754239634711226991L;

	private Integer lctNumero;
	private Short numero;
	private Short nroComplemento;
	private Boolean entregaProgramada;
	private DominioSituacaoAutorizacaoFornecedor indSituacao;
	private Integer codigo;
	private String nome;
	private Boolean isEstocavel;
	private Integer qtdeSolicitada;
	private Integer fatorConversao;
	private Double valorUnitario;
	private Boolean indContrato;
	private Boolean indProgrEntgAuto;
	private Boolean indAnaliseProgrPlanej;
	private Boolean indProgrEntgBloq;
	private String descricao;
	private Integer numeroDoItem;
	
	private String situacao;
	private String situacaoDescricao;
	private String estocavel;
	private String nomeHint;
	private String estocavelHint;
	private String descricaoHint;

	public ConsultaItensAFProgramacaoEntregaVO() {
		super();
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNumero() {
		return numero;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Boolean getEntregaProgramada() {
		return entregaProgramada;
	}

	public void setEntregaProgramada(Boolean entregaProgramada) {
		this.entregaProgramada = entregaProgramada;
	}

	public DominioSituacaoAutorizacaoFornecedor getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacaoAutorizacaoFornecedor indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public String getSituacaoDescricao() {
		return situacaoDescricao;
	}

	public void setSituacaoDescricao(String situacaoDescricao) {
		this.situacaoDescricao = situacaoDescricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getIsEstocavel() {
		return isEstocavel;
	}

	public void setIsEstocavel(Boolean isEstocavel) {
		this.isEstocavel = isEstocavel;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public Boolean getIndProgrEntgAuto() {
		return indProgrEntgAuto;
	}

	public void setIndProgrEntgAuto(Boolean indProgrEntgAuto) {
		this.indProgrEntgAuto = indProgrEntgAuto;
	}

	public Boolean getIndAnaliseProgrPlanej() {
		return indAnaliseProgrPlanej;
	}

	public void setIndAnaliseProgrPlanej(Boolean indAnaliseProgrPlanej) {
		this.indAnaliseProgrPlanej = indAnaliseProgrPlanej;
	}

	public Boolean getIndProgrEntgBloq() {
		return indProgrEntgBloq;
	}

	public void setIndProgrEntgBloq(Boolean indProgrEntgBloq) {
		this.indProgrEntgBloq = indProgrEntgBloq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Integer getNumeroDoItem() {
		return numeroDoItem;
	}

	public void setNumeroDoItem(Integer numeroDoItem) {
		this.numeroDoItem = numeroDoItem;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	public String getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(String estocavel) {
		this.estocavel = estocavel;
	}
	
	public String getNomeHint() {
		return nomeHint;
	}

	public void setNomeHint(String nomeHint) {
		this.nomeHint = nomeHint;
	}
	
	public String getEstocavelHint() {
		return estocavelHint;
	}

	public void setEstocavelHint(String estocavelHint) {
		this.estocavelHint = estocavelHint;
	}
	
	public String getDescricaoHint() {
		return descricaoHint;
	}

	public void setDescricaoHint(String descricaoHint) {
		this.descricaoHint = descricaoHint;
	}

	public enum Fields {
		PFR_LCT_NUMERO("lctNumero"),
		ITL_NUMERO("numero"),
		NRO_COMPLEMENTO("nroComplemento"),
		IND_ENTREGA_PROGRAMADA("entregaProgramada"),
		IND_SITUACAO("indSituacao"),
		CODIGO("codigo"),
		NOME("nome"),
		IND_ESTOCAVEL("isEstocavel"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		FATOR_CONVERSAO("fatorConversao"),
		VALOR_UNITARIO("valorUnitario"),
		IND_CONTRATO("indContrato"),
		IND_PROGR_ENTG_AUTO("indProgrEntgAuto"),
		IND_ANALISE_PROGR_PLANEJ("indAnaliseProgrPlanej"),
		IND_PROGR_ENTG_BLOQ("indProgrEntgBloq"),
		DESCRICAO("descricao"),
		NUMERO_DO_ITEM("numeroDoItem");
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(this.getCodigo())
		.append(this.getDescricao())
		.append(this.getEntregaProgramada())
		.append(this.getFatorConversao())
		.append(this.getIndAnaliseProgrPlanej())
		.append(this.getIndContrato())
		.append(this.getIndProgrEntgAuto())
		.append(this.getIndProgrEntgBloq())
		.append(this.getIndSituacao())
		.append(this.getLctNumero())
		.append(this.getNome())
		.append(this.getNroComplemento())
		.append(this.getNumero())
		.append(this.getNumeroDoItem())
		.append(this.getQtdeSolicitada())
		.append(this.getValorUnitario())
		.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof ConsultaItensAFProgramacaoEntregaVO)) {
			return false;
		}
			
		ConsultaItensAFProgramacaoEntregaVO other = (ConsultaItensAFProgramacaoEntregaVO) obj;
		
		return new EqualsBuilder()
			.append(this.getCodigo(), other.getCodigo())
			.append(this.getDescricao(), other.getDescricao())
			.append(this.getEntregaProgramada(), other.getEntregaProgramada())
			.append(this.getFatorConversao(), other.getFatorConversao())
			.append(this.getIndAnaliseProgrPlanej(), other.getIndAnaliseProgrPlanej())
			.append(this.getIndContrato(), other.getIndContrato())
			.append(this.getIndProgrEntgAuto(), other.getIndProgrEntgAuto())
			.append(this.getIndProgrEntgBloq(), other.getIndProgrEntgBloq())
			.append(this.getIndSituacao(), other.getIndSituacao())
			.append(this.getLctNumero(), other.getLctNumero())
			.append(this.getNome(), other.getNome())
			.append(this.getNroComplemento(), other.getNroComplemento())
			.append(this.getNumero(), other.getNumero())
			.append(this.getNumeroDoItem(), other.getNumeroDoItem())
			.append(this.getQtdeSolicitada(), other.getQtdeSolicitada())
			.append(this.getValorUnitario(), other.getValorUnitario())
			.isEquals();	
	}
	
}