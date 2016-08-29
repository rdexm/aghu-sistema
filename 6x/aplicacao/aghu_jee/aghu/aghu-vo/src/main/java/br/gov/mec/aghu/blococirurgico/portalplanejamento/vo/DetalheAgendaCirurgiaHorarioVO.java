package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.core.utils.DateUtil;

public class DetalheAgendaCirurgiaHorarioVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7690255501955157242L;

	private Integer seqpVO;
	private String paciente;
	private String procedimento;
	private String equipe;
	private String especialidade;
	private Boolean overbooking;
	private Boolean reservada;
	private Boolean realizada;
	private Boolean escala;
	private Boolean selecionado;
	private Boolean planejado;
	private Boolean indisponivel;
	private Boolean bloqueado;
	private Boolean vago;
	private Double height;
	
	private Integer seqAgenda;
	public Date horaInicial;
	public Date horaFinal;
	private MbcProfAtuaUnidCirgs equipeAgenda;
	private AghEspecialidades especialidadeAgenda;
	
	
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getPaciente() {
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public Boolean getOverbooking() {
		return overbooking;
	}
	public void setOverbooking(Boolean overbooking) {
		this.overbooking = overbooking;
	}
	public Boolean getReservada() {
		return reservada;
	}
	public void setReservada(Boolean reservada) {
		this.reservada = reservada;
	}
	public Boolean getRealizada() {
		return realizada;
	}
	public void setRealizada(Boolean realizada) {
		this.realizada = realizada;
	}
	public Boolean getEscala() {
		return escala;
	}
	public void setEscala(Boolean escala) {
		this.escala = escala;
	}
	public Boolean getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	public Boolean getPlanejado() {
		return planejado;
	}
	public void setPlanejado(Boolean planejado) {
		this.planejado = planejado;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}
	public String getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}
	public Integer getSeqpVO() {
		return seqpVO;
	}
	public void setSeqpVO(Integer seqpVO) {
		this.seqpVO = seqpVO;
	}
	public Integer getSeqAgenda() {
		return seqAgenda;
	}
	public void setSeqAgenda(Integer seqAgenda) {
		this.seqAgenda = seqAgenda;
	}
	public Date getHoraInicial() {
		return horaInicial;
	}
	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}
	public Date getHoraFinal() {
		if(horaFinal != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(horaFinal);
			if(cal.get(Calendar.HOUR_OF_DAY) == 0 && cal.get(Calendar.MINUTE) == 0) {
				return DateUtil.obterDataComHoraFinal(horaFinal);
			}
		}
		return horaFinal;
	}
	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}
	public Boolean getIndisponivel() {
		return indisponivel;
	}
	public void setIndisponivel(Boolean indisponivel) {
		this.indisponivel = indisponivel;
	}
	public Boolean getBloqueado() {
		return bloqueado;
	}
	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public MbcProfAtuaUnidCirgs getEquipeAgenda() {
		return equipeAgenda;
	}
	public void setEquipeAgenda(MbcProfAtuaUnidCirgs equipeAgenda) {
		this.equipeAgenda = equipeAgenda;
	}
	public AghEspecialidades getEspecialidadeAgenda() {
		return especialidadeAgenda;
	}
	public void setEspecialidadeAgenda(AghEspecialidades especialidadeAgenda) {
		this.especialidadeAgenda = especialidadeAgenda;
	}
	public Boolean getVago() {
		return vago;
	}
	public void setVago(Boolean vago) {
		this.vago = vago;
	}
	
}
