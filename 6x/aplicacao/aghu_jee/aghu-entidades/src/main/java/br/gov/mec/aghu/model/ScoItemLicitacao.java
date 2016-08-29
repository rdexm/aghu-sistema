package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioFrequenciaEntrega;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "SCO_ITENS_LICITACOES", schema = "AGH")
public class ScoItemLicitacao extends BaseEntityId<ScoItemLicitacaoId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7120182287984089210L;
	private ScoItemLicitacaoId id;	
	private Short classifItem;
	private BigDecimal valorUnitario;
	private Boolean exclusao;
	private String motivoExclusao;
	private Date dtExclusao;
	private DominioMotivoCancelamentoComissaoLicitacao motivoCancel;
	private Boolean propostaEscolhida;
	private Boolean emAf;
	private BigDecimal valorOriginalItem;
	private Boolean julgParcial;
	private Date dtJulgParcial;
	private ScoLoteLicitacao loteLicitacao;
	private RapServidores servidorJulgParcial;
	private ScoLicitacao licitacao;
	private Set<ScoFaseSolicitacao> fasesSolicitacao;
	private Set<ScoCondicaoPgtoLicitacao> condicoesPagamento;
	private DominioFrequenciaEntrega indFrequenciaEntrega;
	private Integer frequenciaEntrega;
	private DominioSituacaoJulgamento situacaoJulgamento;
	
	
	private Short numeroItemLicitacao;
	
	/*
	 * Este método traz o número da solicitação de compras ou o número da solicitação de serviço
	 * Não utilizar, fora do padrão de arquitetura aghu
	 */
	@Transient
	@Deprecated
	public Integer getNroScSs(){
		
		Integer nroScSs = null;
		
		if(fasesSolicitacao!=null && !fasesSolicitacao.isEmpty()){
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fasesSolicitacao).get(0);//recuperar fasesSolicitacao.
			if(fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null){
				nroScSs = fase.getSolicitacaoDeCompra().getNumero();
			}
			else{
				nroScSs = fase.getSolicitacaoServico().getNumero();
			}
				
		}
		return nroScSs;
	}
	
	
	/*
	 * Este método traz a descrição da solicitação de compras ou a descrição da solicitação de serviço
	 * Não utilizar, fora do padrão de arquitetura aghu
	 */
	@Deprecated
	@Transient
	public String getDescScSs(){
		
		String descScSs = null;
		
		if(fasesSolicitacao!=null && !fasesSolicitacao.isEmpty()){
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fasesSolicitacao).get(0);//recuperar fasesSolicitacao.
			if(fase.getSolicitacaoDeCompra() != null ){
				descScSs = fase.getSolicitacaoDeCompra().getDescricao()!=null?fase.getSolicitacaoDeCompra().getDescricao():"";
			}
			else{
				descScSs = fase.getSolicitacaoServico().getDescricao()!=null?fase.getSolicitacaoServico().getDescricao():"";
			}
				
		}
		return descScSs;
	}
	
	/*
	 * Este método traz o código do material ou o código do serviço
	 * Não utilizar, fora do padrão de arquitetura aghu
	 */
	@Transient
	@Deprecated
	public Integer getCodMatServ(){
		
		Integer codMatServ = null;
		
		if(fasesSolicitacao!=null && !fasesSolicitacao.isEmpty()){
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fasesSolicitacao).get(0);//recuperar fasesSolicitacao.
			if(fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null){
				codMatServ = fase.getSolicitacaoDeCompra().getMaterial().getCodigo();
			}
			else{
				codMatServ = fase.getSolicitacaoServico().getServico().getCodigo();
			}
				
		}
		return codMatServ;
	}
	
	/*
	 * Este método traz o nome do material ou o código do serviço
	 * * Não utilizar, fora do padrão de arquitetura aghu
	 */
	@Transient
	@Deprecated
	public String getNomeMatServ(){
		
		String nomeMatServ = null;
		
		if(fasesSolicitacao!=null && !fasesSolicitacao.isEmpty()){
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fasesSolicitacao).get(0);//recuperar fasesSolicitacao.
			if(fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getNome() != null){
				nomeMatServ = fase.getSolicitacaoDeCompra().getMaterial().getNome();
			}
			else{
				nomeMatServ = fase.getSolicitacaoServico().getServico().getNome();
			}
				
		}
		return nomeMatServ;
	}
	
	/*
	 * Este método traz a descrição do material ou a descrição do serviço
	 * Não utilizar, fora do padrão de arquitetura aghu
	 */
	@Transient
	@Deprecated
	public String getDescricaoMatServ(){
		
		String descricaoMatServ = null;
		
		if(fasesSolicitacao!=null && !fasesSolicitacao.isEmpty()){
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(fasesSolicitacao).get(0);//recuperar fasesSolicitacao.
			if(fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null){
				descricaoMatServ = fase.getSolicitacaoDeCompra().getMaterial().getDescricao();
			}
			else{
				descricaoMatServ = fase.getSolicitacaoServico().getServico().getDescricao();
			}
				
		}
		return descricaoMatServ;
	}
	
	

	// construtores

	public ScoItemLicitacao() {
	}

	public ScoItemLicitacao(ScoItemLicitacaoId id) {
		this.id = id;
	}

	// getters & setters

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "scoLicitacao", column = @Column(name = "LCT_NUMERO", nullable = false, length = 7)),
			@AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false, length = 3)) })
	public ScoItemLicitacaoId getId() {
		return this.id;
	}

	public void setId(ScoItemLicitacaoId id) {
		this.id = id;
	}

	@Column(name = "CLASSIF_ITEM", length = 3)
	public Short getClassifItem() {
		return this.classifItem;
	}

	public void setClassifItem(Short classifItem) {
		this.classifItem = classifItem;
	}

	@Column(name = "VALOR_UNITARIO", precision=22, scale=4)
	public BigDecimal getValorUnitario() {
		return this.valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Column(name = "IND_EXCLUSAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

	@Column(name = "MOTIVO_EXCLUSAO", length = 60)
	public String getMotivoExclusao() {
		return this.motivoExclusao;
	}

	public void setMotivoExclusao(String motivoExclusao) {
		this.motivoExclusao = motivoExclusao;
	}

	@Column(name = "DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	@Column(name = "IND_FREQ_ENTREGA", length = 1)
	@Enumerated(EnumType.ORDINAL)
	public DominioFrequenciaEntrega getIndFrequenciaEntrega() {
		return this.indFrequenciaEntrega;
	}

	public void setIndFrequenciaEntrega(DominioFrequenciaEntrega indFrequenciaEntrega) {
		this.indFrequenciaEntrega = indFrequenciaEntrega;
	}

	@Column(name = "FREQUENCIA_ENTREGA", length = 3)
	public Integer getFrequenciaEntrega() {
		return this.frequenciaEntrega;
	}

	public void setFrequenciaEntrega(Integer frequenciaEntrega) {
		this.frequenciaEntrega = frequenciaEntrega;
	}
	
	@Column(name = "IND_PROPOSTA_ESCOLHIDA", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPropostaEscolhida() {
		return propostaEscolhida;
	}

	public void setPropostaEscolhida(Boolean propostaEscolhida) {
		this.propostaEscolhida = propostaEscolhida;
	}

	@Column(name = "IND_EM_AF", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEmAf() {
		return emAf;
	}

	public void setEmAf(Boolean emAf) {
		this.emAf = emAf;
	}

	@Column(name = "VALOR_ORIGINAL_ITEM", length = 22)
	public BigDecimal getValorOriginalItem() {
		return this.valorOriginalItem;
	}

	public void setValorOriginalItem(BigDecimal valorOriginalItem) {
		this.valorOriginalItem = valorOriginalItem;
	}

	@Column(name = "IND_JULG_PARCIAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getJulgParcial() {
		return julgParcial;
	}

	public void setJulgParcial(Boolean julgParcial) {
		this.julgParcial = julgParcial;
	}

	@Column(name = "DT_JULG_PARCIAL")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtJulgParcial() {
		return this.dtJulgParcial;
	}

	public void setDtJulgParcial(Date dtJulgParcial) {
		this.dtJulgParcial = dtJulgParcial;
	}

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_JULG_PARCIAL", referencedColumnName = "MATRICULA", nullable = true),
			@JoinColumn(name = "SER_VIN_CODIGO_JULG_PARCIAL", referencedColumnName = "VIN_CODIGO", nullable = true) })
	public RapServidores getServidorJulgParcial() {
		return servidorJulgParcial;
	}

	public void setServidorJulgParcial(RapServidores servidorJulgParcial) {
		this.servidorJulgParcial = servidorJulgParcial;
	}

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "LLC_LCT_NUMERO", referencedColumnName = "LCT_NUMERO"),
			@JoinColumn(name = "LLC_NUMERO", referencedColumnName = "NUMERO") })
	public ScoLoteLicitacao getLoteLicitacao() {
		return loteLicitacao;
	}

	public void setLoteLicitacao(ScoLoteLicitacao loteLicitacao) {
		this.loteLicitacao = loteLicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LCT_NUMERO", referencedColumnName = "NUMERO", insertable=false, updatable=false)
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemLicitacao")
	public Set<ScoFaseSolicitacao> getFasesSolicitacao() {
		return fasesSolicitacao;
	}

	public void setFasesSolicitacao(Set<ScoFaseSolicitacao> fasesSolicitacao) {
		this.fasesSolicitacao = fasesSolicitacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "itemLicitacao")
	public Set<ScoCondicaoPgtoLicitacao> getCondicoesPagamento() {
		return condicoesPagamento;
	}

	public void setCondicoesPagamento(
			Set<ScoCondicaoPgtoLicitacao> condicoesPagamento) {
		this.condicoesPagamento = condicoesPagamento;
	}

	@Transient
	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}

	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}
	
	@Column(name = "SITUACAO_JULGAMENTO", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoJulgamento getSituacaoJulgamento() {
		return situacaoJulgamento;
	}

	public void setSituacaoJulgamento(DominioSituacaoJulgamento situacaoJulgamento) {
		this.situacaoJulgamento = situacaoJulgamento;
	}

	@Column(name = "MOTIVO_CANCEL", length = 2)
	@Enumerated(EnumType.STRING)
	public DominioMotivoCancelamentoComissaoLicitacao getMotivoCancel() {
		return this.motivoCancel;
	}

	public void setMotivoCancel(DominioMotivoCancelamentoComissaoLicitacao motivoCancel) {
		this.motivoCancel = motivoCancel;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.id).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoItemLicitacao other = (ScoItemLicitacao) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}

	

	public enum Fields {
		NUMERO("id.numero"), 
		NUMERO_LICITACAO("id.lctNumero"),
		CLASSIF_ITEM("classifItem"), 
		VALOR_UNITARIO("valorUnitario"), 
		IND_EXCLUSAO("exclusao"), 
		MOTIVO_EXCLUSAO("motivoExclusao"), 
		DT_EXCLUSAO("dtExclusao"), 
		MOTIVO_CANCEL("motivoCancel"), 
		IND_PROPOSTA_ESCOLHIDA("propostaEscolhida"), 
		IND_EM_AF("emAf"), 
		VALOR_ORIGINAL_ITEM("valorOriginalItem"), 
		IND_JULG_PARCIAL("julgParcial"), 
		DT_JULG_PARCIAL("dtJulgParcial"), 
		SERVIDOR_JULG_PARCIAL("servidorJulgParcial"),
		LICITACAO("licitacao"), 
		LOTE_LICITACAO("loteLicitacao"), 
		FASES_SOLICITACAO("fasesSolicitacao"),
		NUMERO_ITEM_LICITACAO("numeroItemLicitacao"),
		IND_FREQUENCIA_ENTREGA("indFrequenciaEntrega"),
		FREQUENCIA_ENTREGA("frequenciaEntrega"),
		SITUACAO_JULGAMENTO("situacaoJulgamento");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	

}
