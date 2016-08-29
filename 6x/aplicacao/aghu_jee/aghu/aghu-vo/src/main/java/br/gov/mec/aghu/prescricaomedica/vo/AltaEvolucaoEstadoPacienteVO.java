package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaEvolucao;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.core.utils.StringUtil;

public class AltaEvolucaoEstadoPacienteVO implements Serializable {
	
	private static final long serialVersionUID = 663386996080081655L;
	
	private MpmAltaSumarioId id;
	
	private MpmAltaSumarioId idEstado;
	private FatSituacaoSaidaPaciente estadoPaciente;
	
	private MpmAltaSumarioId idEvolucao;
	private String descricao;
	
	/**
	 * Cria um objeto <code>MpmAltaEvolucao</code> desatachado<br> 
	 * associado a <code>MpmAltaSumario</code> e com as alteracoes realizadas.
	 * 
	 * @return <code>MpmAltaEvolucao</code>
	 */
	public MpmAltaEvolucao getModelAltaEvolucao() {
		MpmAltaEvolucao evolucao = new MpmAltaEvolucao();
		
		evolucao.setDescricao(StringUtil.trim(this.getDescricao()));
		
		return evolucao;
	}
	
	/**
	 * Cria um objeto <code>MpmAltaEstadoPaciente</code> desatachado<br> 
	 * associado a <code>MpmAltaSumario</code> e com as alteracoes realizadas.
	 * 
	 * @return <code>MpmAltaEstadoPaciente</code>
	 */
	public MpmAltaEstadoPaciente getModelAltaEstadoPaciente() {
		MpmAltaEstadoPaciente altaEstadoPaciente = new MpmAltaEstadoPaciente();
// TODO ENIO
		
		altaEstadoPaciente.setEstadoPaciente(this.getEstadoPaciente());
		
		return altaEstadoPaciente;
	}
	
	
	public void setIdEstado(MpmAltaSumarioId idEstado) {
		this.idEstado = idEstado;
	}
	public MpmAltaSumarioId getIdEstado() {
		return idEstado;
	}
	public void setEstadoPaciente(FatSituacaoSaidaPaciente estadoPaciente) {
		this.estadoPaciente = estadoPaciente;
	}
	public FatSituacaoSaidaPaciente getEstadoPaciente() {
		return estadoPaciente;
	}
	public void setIdEvolucao(MpmAltaSumarioId idEvolucao) {
		this.idEvolucao = idEvolucao;
	}
	public MpmAltaSumarioId getIdEvolucao() {
		return idEvolucao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setId(MpmAltaSumarioId id) {
		this.id = id;
	}
	public MpmAltaSumarioId getId() {
		return id;
	}
	
}
