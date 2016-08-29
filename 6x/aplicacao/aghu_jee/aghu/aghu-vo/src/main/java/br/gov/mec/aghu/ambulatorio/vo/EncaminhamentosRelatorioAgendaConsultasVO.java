package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe para carregar dados do relatório de Agenda de Consultas
 * #27521 #8236 #8233 C3
 * 
 * @author camila.barreto
 *
 */
public class EncaminhamentosRelatorioAgendaConsultasVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5192821214649510001L;

	private Integer prontuario;
	
	private Date dataConsulta;
	
	//private String horaConsulta;
	
	private Integer codigoPaciente;
	
	private String descricaoSetor;
	
	private Byte sala;
	
	//private Integer turno;
	
	private Short seqUnidadeFuncional;
	
	private Short seqEspecialidade;
	
	private Integer seqGrade;
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataConsulta() {
		return DateUtil.truncaData(dataConsulta);
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getHoraConsulta() {
		return DateUtil.obterDataFormatada(dataConsulta, "HH:mm");
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
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

	public Integer getTurno() {
		Calendar data = Calendar.getInstance();
		data.setTime(dataConsulta);
		int hora = data.get(Calendar.HOUR_OF_DAY);
		if(hora<12){
			return DominioTurno.M.getCodigo();
		} else if(hora>=12 && hora<16){
			return DominioTurno.T.getCodigo();
		} else if(hora>16 && hora<24){
			return DominioTurno.N.getCodigo();
		}
		return null;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Integer getSeqGrade() {
		return seqGrade;
	}

	public void setSeqGrade(Integer seqGrade) {
		this.seqGrade = seqGrade;
	}
	
}
