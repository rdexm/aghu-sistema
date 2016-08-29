package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="ptmArquivosAnexosJnSEQ", sequenceName="AGH.PTM_PAA_JN_SEQ", allocationSize = 1)
@Table(name = "PTM_ARQUIVOS_ANEXOS_JN", schema = "AGH")
@Immutable
public class PtmArquivosAnexosJn extends BaseJournal{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4137778350017686735L;
	
	
	private Date criadoEm;
	private Date alteradoEm;
	private Long seq;
	private byte[] anexo;
	private String arquivo;
	private String descricao;
	private Long bpeSeq;
	private Long irpSeq;
	private Long notSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaAlteracao;
	private Short serVinCodigoAlteracao;
	private DominioTipoDocumentoPatrimonio tipoDocumento;
	private DominioTipoProcessoPatrimonio tipoProcesso;
	private Long proSeq;
	private String tipoDocumentoOutros;
	private Integer aceiteTecnico;
	private Long version;
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ptmArquivosAnexosJnSEQ")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = true, length = 29)
	public Long getSeq() {
		return seq;
	}

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
	
	@Type(type="org.hibernate.type.BinaryType") 
	@Column(name = "ANEXO", nullable = false)
	public byte[] getAnexo() {
		return anexo;
	}

	
	public void setAnexo(byte[] anexo) {
		this.anexo = anexo;
	}
	
	@Column(name = "BPE_SEQ")
	public Long getBpeSeq() {
		return bpeSeq;
	}


	public void setBpeSeq(Long bpeSeq) {
		this.bpeSeq = bpeSeq;
	}

	@Column(name = "IRP_SEQ")
	public Long getIrpSeq() {
		return irpSeq;
	}


	public void setIrpSeq(Long irpSeq) {
		this.irpSeq = irpSeq;
	}

	@Column(name = "NOT_SEQ")
	public Long getNotSeq() {
		return notSeq;
	}


	public void setNotSeq(Long notSeq) {
		this.notSeq = notSeq;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_CRIADO_EM", nullable = true, length = 29)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ALTERACAO",  length = 29)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Column(name = "SER_MATRICULA", nullable = true, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return serMatricula;
	}
	
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = true, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}


	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name="SER_MATRICULA_ALTERACAO", nullable = true, precision = 7, scale = 0)
	public Integer getSerMatriculaAlteracao() {
		return serMatriculaAlteracao;
	}


	public void setSerMatriculaAlteracao(Integer serMatriculaAlteracao) {
		this.serMatriculaAlteracao = serMatriculaAlteracao;
	}
	
	@Column(name="SER_VIN_CODIGO_ALTERACAO", nullable = true, precision = 3, scale = 0)
	public Short getSerVinCodigoAlteracao() {
		return serVinCodigoAlteracao;
	}
	
	public void setSerVinCodigoAlteracao(Short serVinCodigoAlteracao) {
		this.serVinCodigoAlteracao = serVinCodigoAlteracao;
	}
	
	@Column(name = "TIPO_DOCUMENTO",  length = 2)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTipoDocumento") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioTipoDocumentoPatrimonio getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(DominioTipoDocumentoPatrimonio tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	
	@Column(name = "TIPO_PROCESSO",  length = 2)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioTipoProcessoPatrimonio getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(DominioTipoProcessoPatrimonio tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

	@Column(name = "PRO_SEQ")
	public Long getProSeq() {
		return proSeq;
	}


	public void setProSeq(Long proSeq) {
		this.proSeq = proSeq;
	}

	@Column(name = "TIPO_DOCUMENTO_OUTROS")
	public String getTipoDocumentoOutros() {
		return tipoDocumentoOutros;
	}


	public void setTipoDocumentoOutros(String tipoDocumentoOutros) {
		this.tipoDocumentoOutros = tipoDocumentoOutros;
	}
	
	@Column(name = "AVT_SEQ", nullable = true)
	public Integer getAceiteTecnico() {
		return aceiteTecnico;
	}

	public void setAceiteTecnico(Integer aceiteTecnico) {
		this.aceiteTecnico = aceiteTecnico;
	}
	
	@Version
	@Column(name = "VERSION")
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
