package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "SCO_SERVICOS", schema = "AGH")
@SequenceGenerator(name = "scoSrvSq1", sequenceName = "AGH.SCO_SRV_SQ1", allocationSize = 1)
public class ScoServico extends BaseEntityCodigo<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5363546499191319300L;
	private Integer codigo;
	private ScoGrupoServico grupoServico;
	private String nome;
	private DominioSituacao situacao;
	private Boolean indContrato;
	private String descricao;
	private Date dtDigitacao;
	private Date dtAlteracao;
	private Date dtDesativacao;
	private String observacao;
	private RapServidores servidor;
	private RapServidores servidorDesativado;
	private Integer version;
	private List<ScoServicoSicon> servicoSicon;
	private List<ScoSolicitacaoServico> solicitacoesServico;
	private FsoNaturezaDespesa naturezaDespesa;
	private Integer catser;

	// construtores

	public ScoServico() {
	}

	// getters & setters
	@Id
	@Column(name = "CODIGO", length = 6, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoSrvSq1")
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@JoinColumn(name = "GSV_CODIGO", referencedColumnName = "CODIGO")
	@ManyToOne(fetch = FetchType.LAZY)
	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	@Column(name = "NOME", length = 60, nullable = false)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DT_DIGITACAO", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDtDigitacao() {
		return this.dtDigitacao;
	}

	public void setDtDigitacao(Date dtDigitacao) {
		this.dtDigitacao = dtDigitacao;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_CONTRATO", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndContrato() {
		return this.indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	@Column(name = "DESCRICAO", length = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "DT_ALTERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	@Column(name = "DT_DESATIVACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtDesativacao() {
		return this.dtDesativacao;
	}

	public void setDtDesativacao(Date dtDesativacao) {
		this.dtDesativacao = dtDesativacao;
	}

	@Column(name = "OBSERVACAO", length = 500)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_DESATIVADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_DESATIVADO", referencedColumnName = "VIN_CODIGO") })	
	public RapServidores getServidorDesativado() {
		return servidorDesativado;
	}
	
	public void setServidorDesativado(RapServidores servidor) {
		this.servidorDesativado = servidor;
	}

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@OneToMany(mappedBy="servico", fetch=FetchType.LAZY)
	public List<ScoServicoSicon> getServicoSicon() {
		return servicoSicon;
	}

	public void setServicoSicon(List<ScoServicoSicon> servicoSicon) {
		this.servicoSicon = servicoSicon;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigo", this.codigo)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		ScoServico castOther = (ScoServico) other;
		return new EqualsBuilder().append(this.codigo, castOther.getCodigo())
				.isEquals();
	}
	
    @OneToMany(mappedBy="servico", fetch = FetchType.LAZY)
	public List<ScoSolicitacaoServico> getSolicitacoesServico() {
		return solicitacoesServico;
	}
	
	public void setSolicitacoesServico(List<ScoSolicitacaoServico> solicitacoesServico) {
		this.solicitacoesServico = solicitacoesServico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "NTD_GND_CODIGO", referencedColumnName = "GND_CODIGO"),
			@JoinColumn(name = "NTD_CODIGO", referencedColumnName = "CODIGO") })
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	@Column(name = "COD_CATSER")
	public Integer getCatser() {
		return catser;
	}

	public void setCatser(Integer catser) {
		this.catser = catser;
	}

	public enum Fields {
		CODIGO("codigo"), 
		NOME("nome"), 
		DT_DIGITACAO("dtDigitacao"), 
		SITUACAO("situacao"), 
		IND_CONTRATO("indContrato"), 
		DESCRICAO("descricao"), 
		DT_ALTERACAO("dtAlteracao"), 
		DT_DESATIVACAO("dtDesativacao"), 
		OBSERVACAO("observacao"), 
		GRUPO_SERVICO("grupoServico"), 
		SERVICO_SICON("servicoSicon"), 
		SERVIDOR("servidor"),
		GRUPO_SERVICO_CODIGO("grupoServico.codigo"), 
		NTD_CODIGO("naturezaDespesa.id.codigo"),
		GND_CODIGO("naturezaDespesa.id.gndCodigo"),
		NATUREZA_DESPESA("naturezaDespesa"),
		CATSER("catser"), 
		IND_ENGENHARIA("grupoServico.indEngenharia"), 
		SOLICITACAO_SERVICO("solicitacoesServico");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Transient
	public String getCodigoENome(){
		return this.codigo + " - "+ (this.nome!=null?this.nome:""); 
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigo).toHashCode();
	}
	
}