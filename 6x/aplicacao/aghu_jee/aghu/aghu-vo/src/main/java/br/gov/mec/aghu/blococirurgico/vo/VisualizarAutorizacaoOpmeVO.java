package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VisualizarAutorizacaoOpmeVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2441867167207690343L;
	
	private String nomePaciente;
	private String convenio;
	private String planoSaude;
	private String procedimento;
	private Integer prontuario;
	private String leito;
	private Date dataProcedimento;
	private Integer codProcedimentoSus;
	private String descProcedimentoSus;
	private String medicoRequerente;
	private Date dataRequisicao;
	private String justificativa;
	private Double totalIncompativel;
	private List<InfMateriaisNaoCompativeisVO> infMateriaisNaoCompativeis;
	private List<InfAutorizacoesVO> infAutorizacoes;
	
	public enum Fields {	
	
		NOME_PACIENTE("nomePaciente"),
		CONVENIO("convenio"),
		PLANO_SAUDE("planoSaude"),
		PROCEDIMENTO("procedimento"),
		PRONTUARIO("prontuario"),
		LEITO("leito"),
		DATA_PROCEDIMENTO("dataProcedimento"),
		COD_PROCEDIMENTO_SUS("codProcedimentoSus"),
		DESC_PROCEDIMENTO_SUS("descProcedimentoSus"),
		MEDICO_REQUERENTE("medicoRequerente"),
		DATA_REQUISICAO("dataRequisicao"),
		JUSTIFICATIVA("justificativa");
	
		private String fields;
	
		private Fields(String fields) {
			this.fields = fields;
		}
	
		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getPlanoSaude() {
		return planoSaude;
	}

	public void setPlanoSaude(String planoSaude) {
		this.planoSaude = planoSaude;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public Integer getCodProcedimentoSus() {
		return codProcedimentoSus;
	}

	public void setCodProcedimentoSus(Integer codProcedimentoSus) {
		this.codProcedimentoSus = codProcedimentoSus;
	}

	public String getDescProcedimentoSus() {
		return descProcedimentoSus;
	}

	public void setDescProcedimentoSus(String descProcedimentoSus) {
		this.descProcedimentoSus = descProcedimentoSus;
	}

	public String getMedicoRequerente() {
		return medicoRequerente;
	}

	public void setMedicoRequerente(String medicoRequerente) {
		this.medicoRequerente = medicoRequerente;
	}

	public Date getDataRequisicao() {
		return dataRequisicao;
	}

	public void setDataRequisicao(Date dataRequisicao) {
		this.dataRequisicao = dataRequisicao;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Double getTotalIncompativel() {
		return totalIncompativel;
	}

	public void setTotalIncompativel(Double totalIncompativel) {
		this.totalIncompativel = totalIncompativel;
	}

	public List<InfMateriaisNaoCompativeisVO> getInfMateriaisNaoCompativeis() {
		return infMateriaisNaoCompativeis;
	}

	public void setInfMateriaisNaoCompativeis(List<InfMateriaisNaoCompativeisVO> infMateriaisNaoCompativeis) {
		this.infMateriaisNaoCompativeis = infMateriaisNaoCompativeis;
	}

	public List<InfAutorizacoesVO> getInfAutorizacoes() {
		return infAutorizacoes;
	}

	public void setInfAutorizacoes(List<InfAutorizacoesVO> infAutorizacoes) {
		this.infAutorizacoes = infAutorizacoes;
	}

}
