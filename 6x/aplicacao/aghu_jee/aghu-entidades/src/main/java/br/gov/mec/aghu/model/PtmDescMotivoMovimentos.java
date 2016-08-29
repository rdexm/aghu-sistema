package br.gov.mec.aghu.model;

import java.io.Serializable;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "PTM_DESC_MOTIVO_MOVIMENTOS", schema = "AGH")
@SequenceGenerator(name="ptmDmmSeq", sequenceName="AGH.PTM_DMM_SEQ", allocationSize = 1)
public class PtmDescMotivoMovimentos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 5171136744121726007L;

	private Integer seq;
	private PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento;
	private Boolean ativo;
	private Boolean justificativaObrig;
	private String descricao;
	private RapServidores servidor;
	private Date datainclusao;
	private Date dataUltimaAlteracao;
	private RapServidores servidorUltimaAlteracao;
	private Integer version;
	
	public PtmDescMotivoMovimentos(){
		
	}
	
	public PtmDescMotivoMovimentos(Integer seq, PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento, Boolean ativo,
								   Boolean justificativaObrig, String descricao, RapServidores servidor, Date datainclusao,
								   Date dataUltimaAlteracao, RapServidores servidorUltimaAlteracao){
		this.seq = seq;
		this.ptmSituacaoMotivoMovimento = ptmSituacaoMotivoMovimento;
		this.ativo = ativo;
		this.justificativaObrig = justificativaObrig;
		this.descricao = descricao;
		this.servidor = servidor;
		this.datainclusao = datainclusao;
		this.dataUltimaAlteracao = dataUltimaAlteracao;
		this.servidorUltimaAlteracao = servidorUltimaAlteracao;
	}
	
	@Id
	@Column(name="SEQ", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator="ptmDmmSeq")
	public Integer getSeq() {
		return seq;
	}

	
	public void setSeq(Integer seq) {
		this.seq = seq;
		
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ULTIMA_ALT", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ULTIMA_ALT", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorUltimaAlteracao() {
		return servidorUltimaAlteracao;
	}

	public void setServidorUltimaAlteracao(RapServidores servidorUltimaAlteracao) {
		this.servidorUltimaAlteracao = servidorUltimaAlteracao;
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
		PTM_SITUACAO_MOTIVO_MOVIMENTO("ptmSituacaoMotivoMovimento"),
		PTM_SITUACAO_MOTIVO_MOVIMENTO_SEQ("ptmSituacaoMotivoMovimento.seq"),
		ATIVO("ativo"),
		JUSTIFICATIVA_OBRIG("justificativaObrig"),
		DESCRICAO("descricao"),
		SERVIDOR("servidor"),
		DATA_INCLUSAO("datainclusao"),
		DATA_ULTIMA_ALTERACAO("dataUltimaAlteracao"),
		SERVIDOR_ULTIMA_ALTERACAO("servidorUltimaAlteracao"),
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
