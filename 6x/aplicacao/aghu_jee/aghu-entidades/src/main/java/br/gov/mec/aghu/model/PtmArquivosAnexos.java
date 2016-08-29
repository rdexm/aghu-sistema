package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="PTM_ARQUIVOS_ANEXOS", schema = "AGH")
@SequenceGenerator(name="ptmArquivosAnexosSEQ", sequenceName="AGH.PTM_PAA_SEQ", allocationSize = 1)
public class PtmArquivosAnexos extends BaseEntitySeq<Long>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1805246381134059504L;
	private Long seq;
	private byte[] anexo;
	private String arquivo;
	private String descricao;
	private PtmBemPermanentes ptmBemPermanentes;
	private PtmItemRecebProvisorios ptmItemRecebProvisorios;
	private PtmNotificacaoTecnica ptmNotificacaoTecnica;
	private RapServidores servidor;
	private RapServidores servidorAlteracao;
	private DominioTipoDocumentoPatrimonio tipoDocumento;
	private DominioTipoProcessoPatrimonio tipoProcesso;
	private PtmProcessos ptmProcessos;
	private String tipoDocumentoOutros;
	private Date criadoEm;
	private Date alteradoEm;
	private Long version;
	private Integer aceiteTecnico;
	 
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmArquivosAnexosSEQ")
	@Override
	public Long getSeq() {
		return seq;
	}

	@Override
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	@Column(name = "ARQUIVO", length = 250)
	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}
	@Column(name = "DESCRICAO", length = 500)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BPE_SEQ")
	public PtmBemPermanentes getPtmBemPermanentes() {
		return ptmBemPermanentes;
	}

	public void setPtmBemPermanentes(PtmBemPermanentes ptmBemPermanentes) {
		this.ptmBemPermanentes = ptmBemPermanentes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IRP_SEQ")
	public PtmItemRecebProvisorios getPtmItemRecebProvisorios() {
		return ptmItemRecebProvisorios;
	}

	public void setPtmItemRecebProvisorios(PtmItemRecebProvisorios ptmItemRecebProvisorios) {
		this.ptmItemRecebProvisorios = ptmItemRecebProvisorios;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NOT_SEQ")
	public PtmNotificacaoTecnica getPtmNotificacaoTecnica() {
		return ptmNotificacaoTecnica;
	}

	public void setPtmNotificacaoTecnica(PtmNotificacaoTecnica ptmNotificacaoTecnica) {
		this.ptmNotificacaoTecnica = ptmNotificacaoTecnica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA",nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO",nullable=false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ 
			@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA",nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO",nullable=false) })
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}

	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}

	@Column(name = "TIPO_DOCUMENTO",  length = 2)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioTipoDocumentoPatrimonio getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(DominioTipoDocumentoPatrimonio tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRO_SEQ")
	public PtmProcessos getPtmProcessos() {
		return ptmProcessos;
	}

	public void setPtmProcessos(PtmProcessos ptmProcessos) {
		this.ptmProcessos = ptmProcessos;
	}
	@Column(name = "TIPO_DOCUMENTO_OUTROS", length = 100)
	public String getTipoDocumentoOutros() {
		return tipoDocumentoOutros;
	}

	public void setTipoDocumentoOutros(String tipoDocumentoOutros) {
		this.tipoDocumentoOutros = tipoDocumentoOutros;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTERACAO", length = 29)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	@Column(name = "TIPO_PROCESSO",  length = 2)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioTipoProcessoPatrimonio getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(DominioTipoProcessoPatrimonio tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	
	@Column(name = "ANEXO", nullable = false)
	public byte[] getAnexo() {
		return anexo;
	}

	public void setAnexo(byte[] anexo) {
		this.anexo = anexo;
	}
	
	@Column(name = "AVT_SEQ", nullable = true)
	public Integer getAceiteTecnico() {
		return aceiteTecnico;
	}

	public void setAceiteTecnico(Integer aceiteTecnico) {
		this.aceiteTecnico = aceiteTecnico;
	}


	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PtmArquivosAnexos)) {
			return false;
		}
		PtmArquivosAnexos other = (PtmArquivosAnexos) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getArquivo(), other.getArquivo());
		return umEqualsBuilder.isEquals();
	}
	
	public enum Fields {
		SEQ("seq"),
		ANEXO("anexo"),
		ARQUIVO("arquivo"),
		DESCRICAO("descricao"),
		PTM_BEM_PERMANENTES("ptmBemPermanentes"),
		PTM_ITEM_RECEB_PROVISORIOS("ptmItemRecebProvisorios"),
		PTM_NOTIFICACAO_TECNICA("ptmNotificacaoTecnica"),
		NOT_SEQ("ptmNotificacaoTecnica.seq"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERACAO("servidorAlteracao"),
		TIPO_DOCUMENTO("tipoDocumento"),
		TIPO_PROCESSO("tipoProcesso"),
		PTM_PROCESSOS("ptmProcessos"),
		TIPO_DOCUMENTO_OUTROS("tipoDocumentoOutros"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		MATRICLA_INCLUSAO("servidor.id.matricula"),
		VIN_CODIGO_INCLUSAO("servidor.id.vinCodigo"),
		MATRICLA_ALTERACAO("servidorAlteracao.id.matricula"),
		VIN_CODIGO_ALTERACAO("servidorAlteracao.id.vinCodigo"),
		AVT_SEQ("aceiteTecnico")
		
		;
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
