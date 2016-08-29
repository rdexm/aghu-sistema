package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;

public class PortalPlanejamentoCirurgiasReservaVO implements Serializable {

	private static final long serialVersionUID = -7183364481083458491L;

	private String equipe;
	private String especialidade;
	private Short espSeq;
	public Date horaInicial;
	public Date horaFinal;
	
	private Boolean cedencia;
	private Boolean bloqueio;
	
	private Integer seqpVO;
	private MbcProfAtuaUnidCirgs equipeAgenda;
	private AghEspecialidades especialidadeAgenda;
	private String anotacao;
	
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public Date getHoraInicial() {
		return horaInicial;
	}
	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}
	public Date getHoraFinal() {
		return horaFinal;
	}
	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}
	public Integer getSeqpVO() {
		return seqpVO;
	}
	public void setSeqpVO(Integer seqpVO) {
		this.seqpVO = seqpVO;
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
	public String getAnotacao() {
		return anotacao;
	}
	public void setAnotacao(String anotacao) {
		this.anotacao = anotacao;
	}
	public Boolean getCedencia() {
		return cedencia;
	}
	public void setCedencia(Boolean cedencia) {
		this.cedencia = cedencia;
	}
	public Boolean getBloqueio() {
		return bloqueio;
	}
	public void setBloqueio(Boolean bloqueio) {
		this.bloqueio = bloqueio;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getEquipe());
		umHashCodeBuilder.append(this.getEspecialidade());
		umHashCodeBuilder.append(this.getHoraInicial());
		umHashCodeBuilder.append(this.getHoraFinal());
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
		if (!(obj instanceof PortalPlanejamentoCirurgiasReservaVO)) {
			return false;
		}
		PortalPlanejamentoCirurgiasReservaVO other = (PortalPlanejamentoCirurgiasReservaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getEquipe(), other.getEquipe());
		umEqualsBuilder.append(this.getEspecialidade(), other.getEspecialidade());
		umEqualsBuilder.append(this.getHoraInicial(), other.getHoraInicial());
		umEqualsBuilder.append(this.getHoraFinal(), other.getHoraFinal());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####

}
