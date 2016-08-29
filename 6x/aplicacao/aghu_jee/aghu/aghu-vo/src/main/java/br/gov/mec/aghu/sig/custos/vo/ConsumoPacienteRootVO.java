package br.gov.mec.aghu.sig.custos.vo;

import java.util.ArrayList;
import java.util.List;


public class ConsumoPacienteRootVO {
	
	private String rootNome;
	private Integer prontuario;
	private String nomePaciente;
	
	private List<ConsumoPacienteNodoVO> listaConsumoPacienteNodoVO;
	
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
	
	public void setRootNome(String rootNome) {
		this.rootNome = rootNome;
	}
	public String getRootNome() {
		return rootNome;
	}
	public void setListaConsumoPacienteNodoVO(
			List<ConsumoPacienteNodoVO> listaConsumoPacienteNodoVO) {
		this.listaConsumoPacienteNodoVO = listaConsumoPacienteNodoVO;
	}
	public List<ConsumoPacienteNodoVO> getListaConsumoPacienteNodoVO() {
		if(listaConsumoPacienteNodoVO == null){
			listaConsumoPacienteNodoVO = new ArrayList<ConsumoPacienteNodoVO>();
		}
		return listaConsumoPacienteNodoVO;
	}
}
