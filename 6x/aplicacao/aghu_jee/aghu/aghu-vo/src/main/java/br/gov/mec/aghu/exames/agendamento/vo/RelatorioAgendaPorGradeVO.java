package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;

public class RelatorioAgendaPorGradeVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3593985700761674183L;
	
	private Short gaeUnfSeq;
	private String numeroSala;
	private Integer gaeSeqp;
	private String descricaoGrupoExames;
	private Date dthrAgenda;
	private DominioSituacaoHorario situacaoHorario;
	private Integer atdSeq;
	private Integer soeSeq;
	private String descricaoExame;
	private Integer prontuario;
	private String pacNome;
	private List<RelatorioAgendaPorGradeVO> subReport;

	
	public RelatorioAgendaPorGradeVO() {
		super();
	}

	public RelatorioAgendaPorGradeVO(Short gaeUnfSeq, String numeroSala,
			Integer gaeSeqp, String descricaoGrupoExames, Date dthrAgenda,
			DominioSituacaoHorario situacaoHorario, Integer atdSeq,
			Integer soeSeq, String descricaoExame, Integer prontuario,
			String pacNome) {
		super();
		this.gaeUnfSeq = gaeUnfSeq;
		this.numeroSala = numeroSala;
		this.gaeSeqp = gaeSeqp;
		this.descricaoGrupoExames = descricaoGrupoExames;
		this.dthrAgenda = dthrAgenda;
		this.situacaoHorario = situacaoHorario;
		this.atdSeq = atdSeq;
		this.soeSeq = soeSeq;
		this.descricaoExame = descricaoExame;
		this.prontuario = prontuario;
		this.pacNome = pacNome;
	}


	public Short getGaeUnfSeq() {
		return gaeUnfSeq;
	}


	public void setGaeUnfSeq(Short gaeUnfSeq) {
		this.gaeUnfSeq = gaeUnfSeq;
	}


	public String getNumeroSala() {
		return numeroSala;
	}


	public void setNumeroSala(String numeroSala) {
		this.numeroSala = numeroSala;
	}


	public Integer getGaeSeqp() {
		return gaeSeqp;
	}


	public void setGaeSeqp(Integer gaeSeqp) {
		this.gaeSeqp = gaeSeqp;
	}


	public String getDescricaoGrupoExames() {
		return descricaoGrupoExames;
	}


	public void setDescricaoGrupoExames(String descricaoGrupoExames) {
		this.descricaoGrupoExames = descricaoGrupoExames;
	}


	public Date getDthrAgenda() {
		return dthrAgenda;
	}


	public void setDthrAgenda(Date dthrAgenda) {
		this.dthrAgenda = dthrAgenda;
	}


	public DominioSituacaoHorario getSituacaoHorario() {
		return situacaoHorario;
	}


	public void setSituacaoHorario(DominioSituacaoHorario situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}


	public Integer getSoeSeq() {
		return soeSeq;
	}


	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}


	public String getDescricaoExame() {
		return descricaoExame;
	}


	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public String getPacNome() {
		return pacNome;
	}


	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public List<RelatorioAgendaPorGradeVO> getSubReport() {
		return subReport;
	}

	public void setSubReport(List<RelatorioAgendaPorGradeVO> subReport) {
		this.subReport = subReport;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dthrAgenda == null) ? 0 : dthrAgenda.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RelatorioAgendaPorGradeVO other = (RelatorioAgendaPorGradeVO) obj;
		if (dthrAgenda == null) {
			if (other.dthrAgenda != null) {
				return false;
			}
		} else if (!dthrAgenda.equals(other.dthrAgenda)) {
			return false;
		}
		return true;
	}
	
}