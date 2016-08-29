package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;


public class PesquisaExamesPacientesResultsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5310490335804354433L;
	private Integer codigoSoe;
	private Short iseSeq;
	private Date dtHrProgramada;
	private Date dtHrEvento;
	private String situacaoItem;
	private String situacaoCodigo;
	private String motivoCancelamentoItem;
	private String tipoColeta;
	private DominioOrigemAtendimento origemAtendimento;
	private String etiologia;
	private String exame;
	private String materialAnalise;
	private Integer prontuario;
	private Integer codPaciente;
	private String paciente;
	private String infoClinica;
	private boolean podeCancelar = false;
	private boolean podeEstornar = false;
	private boolean verResultado = false;
	private boolean existeDocAnexado = false;

	public Integer getCodigoSoe() {
		return codigoSoe;
	}
	public void setCodigoSoe(Integer codigoSoe) {
		this.codigoSoe = codigoSoe;
	}
	public Short getIseSeq() {
		return iseSeq;
	}
	public void setIseSeq(Short iseSeq) {
		this.iseSeq = iseSeq;
	}
	public Date getDtHrProgramada() {
		return dtHrProgramada;
	}
	public void setDtHrProgramada(Date dtHrProgramada) {
		this.dtHrProgramada = dtHrProgramada;
	}
	public Date getDtHrEvento() {
		return dtHrEvento;
	}
	public void setDtHrEvento(Date dtHrEvento) {
		this.dtHrEvento = dtHrEvento;
	}
	public String getSituacaoItem() {
		return situacaoItem;
	}
	public void setSituacaoItem(String situacaoItem) {
		this.situacaoItem = situacaoItem;
	}
	public String getTipoColeta() {
		return tipoColeta;
	}
	public void setTipoColeta(String tipoColeta) {
		this.tipoColeta = tipoColeta;
	}
	public DominioOrigemAtendimento getOrigemAtendimento() {
		return origemAtendimento;
	}
	public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}
	public String getEtiologia() {
		return etiologia;
	}
	public void setEtiologia(String etiologia) {
		this.etiologia = etiologia;
	}
	public String getExame() {
		return exame;
	}
	public void setExame(String exame) {
		this.exame = exame;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getPaciente() {
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public String getInfoClinica() {
		return infoClinica;
	}
	public void setInfoClinica(String infoClinica) {
		this.infoClinica = infoClinica;
	}
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public String getMotivoCancelamentoItem() {
		return motivoCancelamentoItem;
	}
	public void setMotivoCancelamentoItem(String motivoCancelamentoItem) {
		this.motivoCancelamentoItem = motivoCancelamentoItem;
	}
	public String getMaterialAnalise() {
		return materialAnalise;
	}
	public String getExameMaterialAnalise() {
		return exame + " / " + materialAnalise;
	}
	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}
	public String getSituacaoCodigo() {
		return situacaoCodigo;
	}
	public void setSituacaoCodigo(String situacaoCodigo) {
		this.situacaoCodigo = situacaoCodigo;
	}
	public boolean isPodeCancelar() {
		return podeCancelar;
	}
	public void setPodeCancelar(boolean podeCancelar) {
		this.podeCancelar = podeCancelar;
	}
	public boolean isPodeEstornar() {
		return podeEstornar;
	}
	public void setPodeEstornar(boolean podeEstornar) {
		this.podeEstornar = podeEstornar;
	}
	public boolean isVerResultado() {
		return verResultado;
	}
	public void setVerResultado(boolean verResultado) {
		this.verResultado = verResultado;
	}
	public boolean isExisteDocAnexado() {
		return existeDocAnexado;
	}
	public void setExisteDocAnexado(boolean existeDocAnexado) {
		this.existeDocAnexado = existeDocAnexado;
	}
}