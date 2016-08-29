package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

public class ProcedimentoDaRequisicaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6568631533570216566L;

	private Short ropSeq;
	private Integer agdSeq;
	private String nomePaciente;
	private Integer prontuario;
	private Integer leito;
	private Integer plano;
	private Integer convenio;
	private String cnvDescricao;
	private String cspDescricao;
	private Integer pciSeq;
	private String procedimento;
	private Date dataProcedimento;
	private String procedimentoSUS;
	private Integer matMedicoRequerente;
	private Short vinMedicoRequerente;
	private String nomeMedicoRequerente;
	private Date dataRequisicao;
	private String justificativa;

	public Short getRopSeq() {
		return ropSeq;
	}

	public void setRopSeq(Short ropSeq) {
		this.ropSeq = ropSeq;
	}

	public Integer getAgdSeq() {
		return agdSeq;
	}

	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getLeito() {
		return leito;
	}

	public void setLeito(Integer leito) {
		this.leito = leito;
	}

	public Integer getPlano() {
		return plano;
	}

	public void setPlano(Integer plano) {
		this.plano = plano;
	}

	public Integer getConvenio() {
		return convenio;
	}

	public void setConvenio(Integer convenio) {
		this.convenio = convenio;
	}

	public String getCnvDescricao() {
		return cnvDescricao;
	}

	public void setCnvDescricao(String cnvDescricao) {
		this.cnvDescricao = cnvDescricao;
	}

	public String getCspDescricao() {
		return cspDescricao;
	}

	public void setCspDescricao(String cspDescricao) {
		this.cspDescricao = cspDescricao;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public Date getDataProcedimento() {
		return dataProcedimento;
	}

	public void setDataProcedimento(Date dataProcedimento) {
		this.dataProcedimento = dataProcedimento;
	}

	public String getProcedimentoSUS() {
		return procedimentoSUS;
	}

	public void setProcedimentoSUS(String procedimentoSUS) {
		this.procedimentoSUS = procedimentoSUS;
	}

	public Integer getMatMedicoRequerente() {
		return matMedicoRequerente;
	}

	public void setMatMedicoRequerente(Integer matMedicoRequerente) {
		this.matMedicoRequerente = matMedicoRequerente;
	}

	public Short getVinMedicoRequerente() {
		return vinMedicoRequerente;
	}

	public void setVinMedicoRequerente(Short vinMedicoRequerente) {
		this.vinMedicoRequerente = vinMedicoRequerente;
	}

	public String getNomeMedicoRequerente() {
		return nomeMedicoRequerente;
	}

	public void setNomeMedicoRequerente(String nomeMedicoRequerente) {
		this.nomeMedicoRequerente = nomeMedicoRequerente;
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

	public enum Fields {
		PCI_SEQ("pciSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}