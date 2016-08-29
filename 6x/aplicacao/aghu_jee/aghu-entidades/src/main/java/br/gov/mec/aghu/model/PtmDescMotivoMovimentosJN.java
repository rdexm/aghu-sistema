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

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "PTM_DESC_MOTIVO_MOVIMENTOS_JN", schema = "AGH")
@SequenceGenerator(name="ptmDmmJnSeq", sequenceName="AGH.PTM_DMM_JN_SEQ", allocationSize = 1)
public class PtmDescMotivoMovimentosJN extends BaseJournal {

	private static final long serialVersionUID = 78681682784108141L;

	private Integer seq;
	private Integer seqJN;
	private PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento;
	private Boolean ativo;
	private Boolean justificativaObrig;
	private String descricao;
	private RapServidores servidor;
	private Date datainclusao;
	private Date dataUltimaAlteracao;
	private Integer matriculaServidorUltimaAlt;
	private Short vinculoServidorUltimaAlt;
	private Integer version;
	
	public PtmDescMotivoMovimentosJN(){
		
	}
	
	public PtmDescMotivoMovimentosJN(Integer seq, Integer seqJN, PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento, Boolean ativo,
								   Boolean justificativaObrig, String descricao, RapServidores servidor, Date datainclusao,
								   Date dataUltimaAlteracao, Short vinculoServidorUltimaAlt, Integer matriculaServidorUltimaAlt){
		this.seq = seq;
		this.seqJN = seqJN;
		this.ptmSituacaoMotivoMovimento = ptmSituacaoMotivoMovimento;
		this.ativo = ativo;
		this.justificativaObrig = justificativaObrig;
		this.descricao = descricao;
		this.servidor = servidor;
		this.datainclusao = datainclusao;
		this.dataUltimaAlteracao = dataUltimaAlteracao;
		this.vinculoServidorUltimaAlt = vinculoServidorUltimaAlt;
	}
	
	@Id
	@Column(name="SEQ", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator="ptmDmmJnSeq")
	public Integer getSeq() {
		return seq;
	}

	
	public void setSeq(Integer seq) {
		this.seq = seq;
		
	}
	
	@Column(name="SEQ_JN", nullable = false)
	public Integer getSeqJN() {
		return seqJN;
	}

	public void setSeqJN(Integer seqJN) {
		this.seqJN = seqJN;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_SMM", referencedColumnName = "SEQ")
	public PtmSituacaoMotivoMovimento getPtmSituacaoMotivoMovimento() {
		return ptmSituacaoMotivoMovimento;
	}

	public void setPtmSituacaoMotivoMovimento(PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento) {
		this.ptmSituacaoMotivoMovimento = ptmSituacaoMotivoMovimento;
	}

	@Column(name = "ATIVO", length = 1, nullable = false)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Column(name = "JUST_OBRIGATORIA", length = 1, nullable = false)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getJustificativaObrig() {
		return justificativaObrig;
	}

	public void setJustificativaObrig(Boolean justificativaObrig) {
		this.justificativaObrig = justificativaObrig;
	}

	@Column(name = "DESCRICAO", length = 200, nullable = false)
	@Length(max = 200)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INCLUSAO", nullable = true)
	public Date getDatainclusao() {
		return datainclusao;
	}

	public void setDatainclusao(Date datainclusao) {
		this.datainclusao = datainclusao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMA_ALTERACAO", nullable = true)
	public Date getDataUltimaAlteracao() {
		return dataUltimaAlteracao;
	}

	public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
		this.dataUltimaAlteracao = dataUltimaAlteracao;
	}

	@Column(name = "SER_MATRICULA_ULTIMA_ALT")
	public Integer getMatriculaServidorUltimaAlt() {
		return matriculaServidorUltimaAlt;
	}

	public void setMatriculaServidorUltimaAlt(Integer matriculaServidorUltimaAlt) {
		this.matriculaServidorUltimaAlt = matriculaServidorUltimaAlt;
	}

	@Column(name = "SER_VIN_CODIGO_ULTIMA_ALT")
	public Short getVinculoServidor() {
		return vinculoServidorUltimaAlt;
	}

	public void setVinculoServidor(Short vinculoServidorUltimaAlt) {
		this.vinculoServidorUltimaAlt = vinculoServidorUltimaAlt;
	}
	
	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		SEQ_JN("seqJN"),
		PTM_SITUACAO_MOTIVO_MOVIMENTO("ptmSituacaoMotivoMovimento"),
		ATIVO("ativo"),
		JUSTIFICATIVA_OBRIG("justificativaObrig"),
		DESCRICAO("descricao"),
		SERVIDOR("servidor"),
		DATA_INCLUSAO("datainclusao"),
		DATA_ULTIMA_ALTERACAO("dataUltimaAlteracao"),
		MATRICULA_SERVIDOR_ULTIMA_ALT("matriculaServidorUltimaAlt"),
		VINCULO_SERVIDOR_ULTIMA_ALT("vinculoServidorUltimaAlt"),
		DATA_HORA_JN("dataHoraJN"),
		OPERATION_JN("operationJN"),
		USUARIO_JN("usuarioJN"),
		VERSION("version");	
		
		private String fields;
		
		private Fields(String fields){
			this.fields = fields;
		}
		
		@Override
		public String toString(){
			return this.fields;
		}
	}
}




