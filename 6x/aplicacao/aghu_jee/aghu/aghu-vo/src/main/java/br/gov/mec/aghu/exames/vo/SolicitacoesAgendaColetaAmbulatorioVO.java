package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SolicitacoesAgendaColetaAmbulatorioVO implements Serializable{

	private static final long serialVersionUID = -30756643131198155L;

	private Integer gradeSeqp;
	private String gradeDescricao;
	private String salaColeta;
	private String responsavelColeta;
	
	private Date horaColeta;
	private Integer prontuario;
	private String nomePaciente;
	private String convenio;

	private Integer soeSeq;
	private Short amoSeqp;
	private String tempo;
	private Integer nroUnico;
	private String recipiente;
	private String antiCoagualante;
	private String tipoColeta;
	
	/*SubList*/
	private List<SolicitacoesAgendaColetaAmbulatorioVO> subListExames = new ArrayList<SolicitacoesAgendaColetaAmbulatorioVO>();
	private String desativaTempColeta;
	private Date dthrReativaTemp;
	private String motivoDesativacao;
	private String examesDescUsual;
	private Short laboratorio;
	private String indJejum;
	
	public Integer getGradeSeqp() {
		return gradeSeqp;
	}
	public void setGradeSeqp(Integer gradeSeqp) {
		this.gradeSeqp = gradeSeqp;
	}
	public String getGradeDescricao() {
		return gradeDescricao;
	}
	public void setGradeDescricao(String gradeDescricao) {
		this.gradeDescricao = gradeDescricao;
	}
	public String getSalaColeta() {
		return salaColeta;
	}
	public void setSalaColeta(String salaColeta) {
		this.salaColeta = salaColeta;
	}
	public String getResponsavelColeta() {
		return responsavelColeta;
	}
	public void setResponsavelColeta(String responsavelColeta) {
		this.responsavelColeta = responsavelColeta;
	}
	public Date getHoraColeta() {
		return horaColeta;
	}
	public void setHoraColeta(Date horaColeta) {
		this.horaColeta = horaColeta;
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
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getAmoSeqp() {
		return amoSeqp;
	}
	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
	}
	public String getTempo() {
		return tempo;
	}
	public void setTempo(String tempo) {
		this.tempo = tempo;
	}
	public Integer getNroUnico() {
		return nroUnico;
	}
	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}
	public String getRecipiente() {
		return recipiente;
	}
	public void setRecipiente(String recipiente) {
		this.recipiente = recipiente;
	}
	public String getAntiCoagualante() {
		return antiCoagualante;
	}
	public void setAntiCoagualante(String antiCoagualante) {
		this.antiCoagualante = antiCoagualante;
	}
	public List<SolicitacoesAgendaColetaAmbulatorioVO> getSubListExames() {
		return subListExames;
	}
	public void setSubListExames(
			List<SolicitacoesAgendaColetaAmbulatorioVO> subListExames) {
		this.subListExames = subListExames;
	}
	public String getDesativaTempColeta() {
		return desativaTempColeta;
	}
	public void setDesativaTempColeta(String desativaTempColeta) {
		this.desativaTempColeta = desativaTempColeta;
	}
	public String getExamesDescUsual() {
		return examesDescUsual;
	}
	public void setExamesDescUsual(String examesDescUsual) {
		this.examesDescUsual = examesDescUsual;
	}
	public Short getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(Short laboratorio) {
		this.laboratorio = laboratorio;
	}
	public String getIndJejum() {
		return indJejum;
	}
	public void setIndJejum(String indJejum) {
		this.indJejum = indJejum;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public Date getDthrReativaTemp() {
		return dthrReativaTemp;
	}
	public void setDthrReativaTemp(Date dthrReativaTemp) {
		this.dthrReativaTemp = dthrReativaTemp;
	}
	public String getMotivoDesativacao() {
		return motivoDesativacao;
	}
	public void setMotivoDesativacao(String motivoDesativacao) {
		this.motivoDesativacao = motivoDesativacao;
	}
	public String getTipoColeta() {
		return tipoColeta;
	}
	public void setTipoColeta(String tipoColeta) {
		this.tipoColeta = tipoColeta;
	}
}