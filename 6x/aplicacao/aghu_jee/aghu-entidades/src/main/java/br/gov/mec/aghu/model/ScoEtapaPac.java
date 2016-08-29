package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

/**
 * Modulo: Compras
 * @author jccouto (Jean Couto)
 * @since 22/08/2014
 */
@Entity
@Table(name = "SCO_ETAPA_PAC", schema = "AGH")
@SequenceGenerator(name = "scoEtpSq1", sequenceName = "AGH.SCO_ETP_SQ1", allocationSize = 1)
public class ScoEtapaPac extends BaseEntityCodigo<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4197607084919521754L;

	private Integer codigo;
	private ScoLicitacao licitacao;
	private ScoLocalizacaoProcesso localizacaoProcesso;
	private String descricaoEtapa;
	private DominioSituacaoEtapaPac situacao;
	private Short tempoPrevisto;
	private String apontamentoUsuario;
	private RapServidores servidor;
	private Date dataApontamento;
	private Integer version;

	public ScoEtapaPac(){
		
	}
	
	public ScoEtapaPac(Integer codigo, ScoLicitacao licitacao,
						ScoLocalizacaoProcesso localizacaoProcesso, String descricaoEtapa,
						DominioSituacaoEtapaPac situacao, Short tempoPrevisto,
						String apontamentoUsuario, RapServidores servidor,
						Date dataApontamento, Integer version) {

		this.codigo = codigo;
		this.licitacao = licitacao;
		this.localizacaoProcesso = localizacaoProcesso;
		this.descricaoEtapa = descricaoEtapa;
		this.situacao = situacao;
		this.tempoPrevisto = tempoPrevisto;
		this.apontamentoUsuario = apontamentoUsuario;
		this.servidor = servidor;
		this.dataApontamento = dataApontamento;
		this.version = version;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoEtpSq1")
	@Column(name = "CODIGO", unique = true, nullable = false, precision = 4, scale = 0)
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LCT_NUMERO", referencedColumnName = "NUMERO")
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LCP_CODIGO", referencedColumnName = "CODIGO")
	public ScoLocalizacaoProcesso getLocalizacaoProcesso() {
		return localizacaoProcesso;
	}

	public void setLocalizacaoProcesso(ScoLocalizacaoProcesso localizacaoProcesso) {
		this.localizacaoProcesso = localizacaoProcesso;
	}

	@Column(name = "DESCRICAO_ETAPA", nullable = false, length = 60)
	public String getDescricaoEtapa() {
		return descricaoEtapa;
	}

	public void setDescricaoEtapa(String descricaoEtapa) {
		this.descricaoEtapa = descricaoEtapa;
	}

	@Column(name = "SITUACAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEtapaPac getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoEtapaPac situacao) {
		this.situacao = situacao;
	}

	@Column(name = "TEMPO_PREVISTO", precision = 3, scale = 0)
	public Short getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Short tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	@Column(name = "APONTAMENTO_USUARIO", length = 60)
	public String getApontamentoUsuario() {
		return apontamentoUsuario;
	}

	public void setApontamentoUsuario(String apontamentoUsuario) {
		this.apontamentoUsuario = apontamentoUsuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_APONTAMENTO", length = 7)
	public Date getDataApontamento() {
		return dataApontamento;
	}

	public void setDataApontamento(Date dataApontamento) {
		this.dataApontamento = dataApontamento;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {

		CODIGO("codigo"),
		LICITACAO("licitacao"),
		LCT_NUMERO("licitacao.numero"),
		LOCALIZACAO_PROCESSO("localizacaoProcesso"),
		LCP_CODIGO("localizacaoProcesso.codigo"),
		LCP_DESCRICAO("localizacaoProcesso.descricao"),
		DESCRICAO_ETAPA("descricaoEtapa"),
		SITUACAO("situacao"),
		TEMPO_PREVISTO("tempoPrevisto"),
		APONTAMENTO_USUARIO("apontamentoUsuario"),
		SERVIDOR("servidor"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		DATA_APONTAMENTO("dataApontamento");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		ScoEtapaPac other = (ScoEtapaPac) obj;
		if (codigo == null) {
			if (other.codigo != null){
				return false;
			}
		} else if (!codigo.equals(other.codigo)){
			return false;
		}
		return true;
	}

}
