package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioVisaoCustoPaciente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class PesquisarConsumoPacienteVO {
	private List<Integer> codigoPacientes;
	private List<Integer> codigoCentroCustos; 
	private List<Short> codigoEspecialidades; 
	private List<Integer> listaMatriculas;
	private List<Short> listaVinCodigo;
	private DominioVisaoCustoPaciente visao; 
	private Boolean pacienteEmAlta;
	private SigProcessamentoCusto processoCusto;
	
	
	public PesquisarConsumoPacienteVO(){}
	public PesquisarConsumoPacienteVO(List<Integer> codigoPacientes,
			List<Integer> codigoCentroCustos, List<Short> codigoEspecialidades,
			List<Integer> listaMatriculas, List<Short> listaVinCodigo,
			DominioVisaoCustoPaciente visao, Boolean pacienteEmAlta,
			SigProcessamentoCusto processoCusto) {
		super();
		this.codigoPacientes = codigoPacientes;
		this.codigoCentroCustos = codigoCentroCustos;
		this.codigoEspecialidades = codigoEspecialidades;
		this.listaMatriculas = listaMatriculas;
		this.listaVinCodigo = listaVinCodigo;
		this.visao = visao;
		this.pacienteEmAlta = pacienteEmAlta;
		this.processoCusto = processoCusto;
	}
	public List<Integer> getCodigoPacientes() {
		return codigoPacientes;
	}
	public void setCodigoPacientes(List<Integer> codigoPacientes) {
		this.codigoPacientes = codigoPacientes;
	}
	public List<Integer> getCodigoCentroCustos() {
		return codigoCentroCustos;
	}
	public void setCodigoCentroCustos(List<Integer> codigoCentroCustos) {
		this.codigoCentroCustos = codigoCentroCustos;
	}
	public List<Short> getCodigoEspecialidades() {
		return codigoEspecialidades;
	}
	public void setCodigoEspecialidades(List<Short> codigoEspecialidades) {
		this.codigoEspecialidades = codigoEspecialidades;
	}
	public List<Integer> getListaMatriculas() {
		return listaMatriculas;
	}
	public void setListaMatriculas(List<Integer> listaMatriculas) {
		this.listaMatriculas = listaMatriculas;
	}
	public List<Short> getListaVinCodigo() {
		return listaVinCodigo;
	}
	public void setListaVinCodigo(List<Short> listaVinCodigo) {
		this.listaVinCodigo = listaVinCodigo;
	}
	public DominioVisaoCustoPaciente getVisao() {
		return visao;
	}
	public void setVisao(DominioVisaoCustoPaciente visao) {
		this.visao = visao;
	}
	public Boolean getPacienteEmAlta() {
		return pacienteEmAlta;
	}
	public void setPacienteEmAlta(Boolean pacienteEmAlta) {
		this.pacienteEmAlta = pacienteEmAlta;
	}
	public SigProcessamentoCusto getProcessoCusto() {
		return processoCusto;
	}
	public void setProcessoCusto(SigProcessamentoCusto processoCusto) {
		this.processoCusto = processoCusto;
	}
}
