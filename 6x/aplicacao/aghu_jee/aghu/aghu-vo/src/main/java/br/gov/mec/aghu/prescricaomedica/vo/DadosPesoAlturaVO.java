package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;

public class DadosPesoAlturaVO implements Serializable {

	private static final long serialVersionUID = -8207049101534747901L;
	
	private Double peso;
	private DominioTipoMedicaoPeso realEstimadoPeso;
	private BigDecimal altura;
	private DominioTipoMedicaoPeso realEstimadoAltura;
	private Integer idadeEmDias;
	private Integer idadeEmMeses;
	private Short idadeEmAnos;
	private AipAlturaPacientes alturaPaciente;
	private AipPesoPacientes pesoPaciente;
	
	/*
	 * #989 - Calcular Nutrição Parenteral Total: Campos da estória
	 */
	private Double pesoAnterior;
	private Double sc;
	private Boolean indPacPediatrico;
	private Date pcaCriadoEm;
	private boolean mostrouMensTrocaPeso;
	// Após exibir na controller, setar os valores para false.
	private boolean exibirMpm3410;
	private boolean exibirMpm3411;
	
	
	public BigDecimal getAltura() {
		return altura;
	}
	
	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public Double getPeso() {
		return peso;
	}

	public void setPeso(Double peso) {
		this.peso = peso;
	}

	public DominioTipoMedicaoPeso getRealEstimadoPeso() {
		return realEstimadoPeso;
	}

	public void setRealEstimadoPeso(DominioTipoMedicaoPeso realEstimadoPeso) {
		this.realEstimadoPeso = realEstimadoPeso;
	}

	public DominioTipoMedicaoPeso getRealEstimadoAltura() {
		return realEstimadoAltura;
	}

	public void setRealEstimadoAltura(DominioTipoMedicaoPeso realEstimadoAltura) {
		this.realEstimadoAltura = realEstimadoAltura;
	}

	public Integer getIdadeEmDias() {
		return idadeEmDias;
	}

	public void setIdadeEmDias(Integer idadeEmDias) {
		this.idadeEmDias = idadeEmDias;
	}

	public Integer getIdadeEmMeses() {
		return idadeEmMeses;
	}

	public void setIdadeEmMeses(Integer idadeEmMeses) {
		this.idadeEmMeses = idadeEmMeses;
	}

	public Short getIdadeEmAnos() {
		return idadeEmAnos;
	}

	public void setIdadeEmAnos(Short idadeEmAnos) {
		this.idadeEmAnos = idadeEmAnos;
	}

	public AipAlturaPacientes getAlturaPaciente() {
		return alturaPaciente;
	}

	public void setAlturaPaciente(AipAlturaPacientes alturaPaciente) {
		this.alturaPaciente = alturaPaciente;
	}

	public AipPesoPacientes getPesoPaciente() {
		return pesoPaciente;
	}

	public void setPesoPaciente(AipPesoPacientes pesoPaciente) {
		this.pesoPaciente = pesoPaciente;
	}

	public Double getPesoAnterior() {
		return pesoAnterior;
	}

	public void setPesoAnterior(Double pesoAnterior) {
		this.pesoAnterior = pesoAnterior;
	}

	public Double getSc() {
		return sc;
	}

	public void setSc(Double sc) {
		this.sc = sc;
	}

	public Boolean getIndPacPediatrico() {
		return indPacPediatrico;
	}

	public void setIndPacPediatrico(Boolean indPacPediatrico) {
		this.indPacPediatrico = indPacPediatrico;
	}

	public Date getPcaCriadoEm() {
		return pcaCriadoEm;
	}

	public void setPcaCriadoEm(Date pcaCriadoEm) {
		this.pcaCriadoEm = pcaCriadoEm;
	}

	public boolean isMostrouMensTrocaPeso() {
		return mostrouMensTrocaPeso;
	}

	public void setMostrouMensTrocaPeso(boolean mostrouMensTrocaPeso) {
		this.mostrouMensTrocaPeso = mostrouMensTrocaPeso;
	}

	public boolean isExibirMpm3410() {
		return exibirMpm3410;
	}

	public void setExibirMpm3410(boolean exibirMpm3410) {
		this.exibirMpm3410 = exibirMpm3410;
	}

	public boolean isExibirMpm3411() {
		return exibirMpm3411;
	}

	public void setExibirMpm3411(boolean exibirMpm3411) {
		this.exibirMpm3411 = exibirMpm3411;
	}
	
}
