package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe para carregar dados do relatório de Agenda de Consultas #27521 #8236
 * #8233 C1
 * 
 * @author camila.barreto
 *
 */
public class CabecalhoRelatorioAgendaConsultasVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2883777509608331993L;

	private Date dataConsulta;

	private String dia;

	private String descricaoSetor;

	private Byte sala;

	private Short seqEspecialidade;

	private String nomeEspecialidade;

	private String nomeEquipe;

	private String nomeMedico;

	private Integer seqGrade;

	private Short seqUnidadeFuncional;
	
	private boolean possuiUbs;

	private List<ConsultasRelatorioAgendaConsultasVO> infConsultas;

	private List<EncaminhamentosRelatorioAgendaConsultasVO> infEncaminhamentos;

	public CabecalhoRelatorioAgendaConsultasVO(Date dataConsulta,
			String descricaoSetor, Byte sala, Short seqEspecialidade,
			String nomeEspecialidade, String nomeEquipe, String nomeMedico,
			Integer seqGrade, Short seqUnidadeFuncional) {
		this.dataConsulta = dataConsulta;
		this.dia = this.obterDiaSemanaSigla(dataConsulta);
		this.descricaoSetor = descricaoSetor;
		this.sala = sala;
		this.seqEspecialidade = seqEspecialidade;
		this.nomeEspecialidade = nomeEspecialidade;
		this.nomeEquipe = nomeEquipe;
		this.nomeMedico = nomeMedico;
		this.seqGrade = seqGrade;
		this.infConsultas = null;
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public Integer getTurno() {
		Calendar data = Calendar.getInstance();
		data.setTime(dataConsulta);

		int hora = data.get(Calendar.HOUR_OF_DAY);
		if (hora < 12) {
			return DominioTurno.M.getCodigo();
		} else if (hora >= 12 && hora < 16) {
			return DominioTurno.T.getCodigo();
		} else if (hora >= 16 && hora < 24) {
			return DominioTurno.N.getCodigo();
		}
		return null;
	}

	public String getDescricaoSetor() {
		return descricaoSetor;
	}

	public void setDescricaoSetor(String descricaoSetor) {
		this.descricaoSetor = descricaoSetor;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public boolean isPossuiUbs() {
		return possuiUbs;
	}

	public void setPossuiUbs(boolean possuiUbs) {
		this.possuiUbs = possuiUbs;
	}

	public List<ConsultasRelatorioAgendaConsultasVO> getInfConsultas() {
		return infConsultas;
	}

	public void setInfConsultas(
			List<ConsultasRelatorioAgendaConsultasVO> infConsultas) {
		this.infConsultas = infConsultas;
	}

	public List<EncaminhamentosRelatorioAgendaConsultasVO> getInfEncaminhamentos() {
		return infEncaminhamentos;
	}

	public void setInfEncaminhamentos(
			List<EncaminhamentosRelatorioAgendaConsultasVO> infEncaminhamentos) {
		this.infEncaminhamentos = infEncaminhamentos;
	}

	private String obterDiaSemanaSigla(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			return "DOM";
		case 2:
			return "SEG";
		case 3:
			return "TER";
		case 4:
			return "QUA";
		case 5:
			return "QUI";
		case 6:
			return "SEX";
		case 7:
			return "SAB";
		default:
			return null;
		}
	}

	public enum Fields {
		DATA_CONSULTA("dataConsulta"), DESCRICAO_SETOR("descricaoSetor"), SALA(
				"sala"), SEQ_ESPECIALIDADE("seqEspecialidade"), SEQ_UND_FUNCIONAL(
				"seqUnidadeFuncional"), NOME_ESPECIALIDADE("nomeEspecialidade"), NOME_EQUIPE(
				"nomeEquipe"), NOME_MEDICO("nomeMedico"), SEQ_GRADE("seqGrade");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(DateUtil.truncaData(this.getDataConsulta()));
		umHashCodeBuilder.append(this.getTurno());
		umHashCodeBuilder.append(this.getDescricaoSetor());
		umHashCodeBuilder.append(this.getNomeEquipe());
		umHashCodeBuilder.append(this.getNomeEspecialidade());
		umHashCodeBuilder.append(this.getNomeMedico());
		umHashCodeBuilder.append(this.getSala());
		umHashCodeBuilder.append(this.getSeqEspecialidade());
		umHashCodeBuilder.append(this.getSeqGrade());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		CabecalhoRelatorioAgendaConsultasVO other = (CabecalhoRelatorioAgendaConsultasVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(DateUtil.truncaData(this.getDataConsulta()),
				DateUtil.truncaData(other.getDataConsulta()));
		umEqualsBuilder.append(this.getTurno(), other.getTurno());
		umEqualsBuilder.append(this.getDescricaoSetor(),
				other.getDescricaoSetor());
		umEqualsBuilder.append(this.getNomeEquipe(), other.getNomeEquipe());
		umEqualsBuilder.append(this.getNomeEspecialidade(),
				other.getNomeEspecialidade());
		umEqualsBuilder.append(this.getNomeMedico(), other.getNomeMedico());
		umEqualsBuilder.append(this.getSala(), other.getSala());
		umEqualsBuilder.append(this.getSeqEspecialidade(),
				other.getSeqEspecialidade());
		umEqualsBuilder.append(this.getSeqGrade(), other.getSeqGrade());
		return umEqualsBuilder.isEquals();
	}
}
