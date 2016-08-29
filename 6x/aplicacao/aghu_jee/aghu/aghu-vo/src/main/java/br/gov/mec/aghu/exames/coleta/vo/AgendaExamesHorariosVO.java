package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;

/**
 * 
 * @author fpalma
 *
 */
public class AgendaExamesHorariosVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8979821465180714064L;
	
	private Short seqVO;
	private Integer soeSeq;
	private Integer atdSeq;
	private Integer atvSeq;
	private Date hedDthrAgenda;
	private Short hedGaeUnfSeq;
	private Integer hedGaeSeqp;
	private Short tmeSeq;
	private DominioSituacaoHorario hedSituacaoHorario;
	private Boolean hedIndHorarioExtra;
	private Integer pacCodigo;
	private Integer prontuario;
	private String nomePaciente;
	private Short seqP;
	
	
	public AgendaExamesHorariosVO() {
		
	}

	public AgendaExamesHorariosVO(Short seqVO, Integer soeSeq, Integer atdSeq, Integer atvSeq, Date hedDthrAgenda, Short hedGaeUnfSeq, Integer hedGaeSeqp,
			Short tmeSeq, DominioSituacaoHorario hedSituacaoHorario, Boolean hedIndHorarioExtra, Integer pacCodigo, Integer prontuario,
			String nomePaciente, Short seqP) {
		this.seqVO = seqVO;
		this.soeSeq = soeSeq;
		this.atdSeq = atdSeq;
		this.atvSeq = atvSeq;
		this.hedDthrAgenda = hedDthrAgenda;
		this.hedGaeUnfSeq = hedGaeUnfSeq;
		this.hedGaeSeqp = hedGaeSeqp;
		this.tmeSeq = tmeSeq;
		this.hedSituacaoHorario = hedSituacaoHorario;
		this.hedIndHorarioExtra = hedIndHorarioExtra;
		this.pacCodigo = pacCodigo;
		this.prontuario = prontuario;
		this.nomePaciente = nomePaciente;
		this.seqP = seqP;
	}
	
	public enum Fields {
		SOE_SEQ("soeSeq"),
		ATD_SEQ("atdSeq"),
		ATV_SEQ("atvSeq"),
		HED_DTHR_AGENDA("hedDthrAgenda"),
		HED_GAE_UNF_SEQ("hedGaeUnfSeq"),
		HED_GAE_SEQP("hedGaeSeqp"),
		TME_SEQ("tmeSeq"),
		HED_SITUACAO_HORARIO("hedSituacaoHorario"),
		HED_IND_HORARIO_EXTRA("hedIndHorarioExtra"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		NOME_PACIENTE("nomePaciente");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtvSeq() {
		return atvSeq;
	}

	public void setAtvSeq(Integer atvSeq) {
		this.atvSeq = atvSeq;
	}

	public Date getHedDthrAgenda() {
		return hedDthrAgenda;
	}

	public void setHedDthrAgenda(Date hedDthrAgenda) {
		this.hedDthrAgenda = hedDthrAgenda;
	}

	public Short getHedGaeUnfSeq() {
		return hedGaeUnfSeq;
	}

	public void setHedGaeUnfSeq(Short hedGaeUnfSeq) {
		this.hedGaeUnfSeq = hedGaeUnfSeq;
	}

	public Integer getHedGaeSeqp() {
		return hedGaeSeqp;
	}

	public void setHedGaeSeqp(Integer hedGaeSeqp) {
		this.hedGaeSeqp = hedGaeSeqp;
	}

	public Short getTmeSeq() {
		return tmeSeq;
	}

	public void setTmeSeq(Short tmeSeq) {
		this.tmeSeq = tmeSeq;
	}

	public DominioSituacaoHorario getHedSituacaoHorario() {
		return hedSituacaoHorario;
	}

	public void setHedSituacaoHorario(DominioSituacaoHorario hedSituacaoHorario) {
		this.hedSituacaoHorario = hedSituacaoHorario;
	}

	public Boolean getHedIndHorarioExtra() {
		return hedIndHorarioExtra;
	}

	public void setHedIndHorarioExtra(Boolean hedIndHorarioExtra) {
		this.hedIndHorarioExtra = hedIndHorarioExtra;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Short getSeqP() {
		return seqP;
	}

	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}

	public Short getSeqVO() {
		return seqVO;
	}

	public void setSeqVO(Short seqVO) {
		this.seqVO = seqVO;
	}
	
}
