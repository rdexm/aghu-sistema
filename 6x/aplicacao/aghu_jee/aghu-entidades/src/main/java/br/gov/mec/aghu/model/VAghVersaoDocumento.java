package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "V_AGH_VERSOES_DOCUMENTOS", schema = "AGH")
@Immutable
public class VAghVersaoDocumento extends BaseEntitySeq<Integer> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3291968855623296280L;
	private Integer seq;
	private Date criadoEm;
	private DominioSituacaoVersaoDocumento situacao;
	private DominioTipoDocumento tipo;
	private Integer codigoPaciente;
	private Integer prontuarioPaciente;
	private String nomePaciente;
	private Integer matriculaResp;
	private Short vinCodigoResp;
	private Integer codigoCCLotacaoResp;
	private Integer codigoCCAtuacaoResp;
	private String nomeResp;

	public VAghVersaoDocumento() {
	}
	
	public VAghVersaoDocumento(Integer seq, Date criadoEm,
			DominioSituacaoVersaoDocumento situacao, DominioTipoDocumento tipo,
			Integer codigoPaciente, Integer prontuarioPaciente,
			String nomePaciente, Integer matriculaResp, Short vinCodigoResp,
			Integer codigoCCLotacaoResp, Integer codigoCCAtuacaoResp,
			String nomeResp) {
		super();
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
		this.tipo = tipo;
		this.codigoPaciente = codigoPaciente;
		this.prontuarioPaciente = prontuarioPaciente;
		this.nomePaciente = nomePaciente;
		this.matriculaResp = matriculaResp;
		this.vinCodigoResp = vinCodigoResp;
		this.codigoCCLotacaoResp = codigoCCLotacaoResp;
		this.codigoCCAtuacaoResp = codigoCCAtuacaoResp;
		this.nomeResp = nomeResp;
	}

	@Id
	@Column(name = "DOV_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DOV_CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DOV_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoVersaoDocumento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersaoDocumento situacao) {
		this.situacao = situacao;
	}

	@Column(name = "DOK_TIPO", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioTipoDocumento getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoDocumento tipo) {
		this.tipo = tipo;
	}

	@Column(name = "PAC_CODIGO", precision = 8, scale = 0)
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	@Column(name = "PAC_PRONTUARIO", precision = 8, scale = 0)
	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	@Column(name = "PAC_NOME", nullable = false, length = 50)
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getMatriculaResp() {
		return matriculaResp;
	}

	public void setMatriculaResp(Integer matriculaResp) {
		this.matriculaResp = matriculaResp;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getVinCodigoResp() {
		return vinCodigoResp;
	}

	public void setVinCodigoResp(Short vinCodigoResp) {
		this.vinCodigoResp = vinCodigoResp;
	}

	@Column(name = "SER_CCT_CODIGO", nullable = false, precision = 6, scale = 0)
	public Integer getCodigoCCLotacaoResp() {
		return codigoCCLotacaoResp;
	}

	public void setCodigoCCLotacaoResp(Integer codigoCCLotacaoResp) {
		this.codigoCCLotacaoResp = codigoCCLotacaoResp;
	}

	@Column(name = "SER_CCT_CODIGO_ATUA", nullable = false, precision = 6, scale = 0)
	public Integer getCodigoCCAtuacaoResp() {
		return codigoCCAtuacaoResp;
	}

	public void setCodigoCCAtuacaoResp(Integer codigoCCAtuacaoResp) {
		this.codigoCCAtuacaoResp = codigoCCAtuacaoResp;
	}

	@Column(name = "PES_NOME", nullable = false, length = 50)
	public String getNomeResp() {
		return nomeResp;
	}

	public void setNomeResp(String nomeResp) {
		this.nomeResp = nomeResp;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seq == null) ? 0 : seq.hashCode());
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
		VAghVersaoDocumento other = (VAghVersaoDocumento) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
				}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}	
	
	public enum Fields {
		
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SITUACAO("situacao"),
		TIPO("tipo"),
		CODIGO_PACIENTE("codigoPaciente"),
		PRONTUARIO_PACIENTE("prontuarioPaciente"),
		NOME_PACIENTE("nomePaciente"),
		MATRICULA_RESP("matriculaResp"),
		VINCULO_RESP("vinCodigoResp"),
		CODIGO_CC_LOTACAO("codigoCCLotacaoResp"),
		CODIGO_CC_ATUACAO("codigoCCAtuacaoResp"),
		NOME_RESP("nomeResp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	
}
