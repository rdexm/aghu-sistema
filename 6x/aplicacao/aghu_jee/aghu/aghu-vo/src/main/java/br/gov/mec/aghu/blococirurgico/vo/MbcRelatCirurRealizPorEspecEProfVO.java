package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;
import java.util.List;

public class MbcRelatCirurRealizPorEspecEProfVO {
       
	private Short   seqEspecialidade;
	private String  especialidade;
	private String  nomePessoaFisica;
	private String  nomeUsualPessoaFisica;
	private String  cirurgiao;
	private Integer seqCirurgia;
	private String  nomePaciente;
	private Integer prontuario;
	private String  prontuarioFormatado;
	private Date    dataInicioCirurgia;
	private Short 	numeroAgenda;
	private String  convenio;
	private String 	procedimento;
	private String 	clinica;
	private String  strDataInicioCirurgia;
	private Boolean isMultiProcedimentos;
	private List<String> procedimentos;
	
	private List<MbcTotalCirurRealizPorEspecEProfVO> listaCirurgioes;

	@SuppressWarnings("ucd")
	public enum Fields {
		ESP_SEQ("seqEspecialidade"),
	    ESP_NOME("especialidade"),
	    PES_NOME("nomePessoaFisica"),
	    PES_NOME_USUAL("nomeUsualPessoaFisica"),
	    CIRURGIAO("cirurgiao"),
	    CRG_SEQ("seqCirurgia"),
	    PAC_NOME("nomePaciente"),
	    PAC_PRONTUARIO("prontuario"),
	    CRG_DTHR_INICIO_CIRG("dataInicioCirurgia"),
	    CRG_NRO_AGENDA("numeroAgenda"),
	    CNV_CONVENIO("convenio"),
	    PCI_DESCRICAO("procedimento"),
		CLC_DESCRICAO("clinica"),
		LISTA_CIRURGIOES("listaCirurgioes"),
		STR_DATA_INICIO_CIRURGIA("strDataInicioCirurgia");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}

	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}

	public String getNomeUsualPessoaFisica() {
		return nomeUsualPessoaFisica;
	}

	public void setNomeUsualPessoaFisica(String nomeUsualPessoaFisica) {
		this.nomeUsualPessoaFisica = nomeUsualPessoaFisica;
	}

	public String getCirurgiao() {
		if (cirurgiao == null){
			return getNomeUsualPessoaFisica() == null? getNomePessoaFisica(): getNomeUsualPessoaFisica();
		}else {
			return cirurgiao;
		}
	}

	public void setCirurgiao(String cirurgiao) {
		this.cirurgiao = cirurgiao;
	}

	public Integer getSeqCirurgia() {
		return seqCirurgia;
	}

	public void setSeqCirurgia(Integer seqCirurgia) {
		this.seqCirurgia = seqCirurgia;
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

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public Date getDataInicioCirurgia() {
		return dataInicioCirurgia;
	}

	public void setDataInicioCirurgia(Date dataInicioCirurgia) {
		this.dataInicioCirurgia = dataInicioCirurgia;
	}

	public Short getNumeroAgenda() {
		return numeroAgenda;
	}

	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public String getClinica() {
		return clinica;
	}

	public void setClinica(String clinica) {
		this.clinica = clinica;
	}

	public List<MbcTotalCirurRealizPorEspecEProfVO> getListaCirurgioes() {
		return listaCirurgioes;
	}

	public void setListaCirurgioes(
			List<MbcTotalCirurRealizPorEspecEProfVO> listaCirurgioes) {
		this.listaCirurgioes = listaCirurgioes;
	}
	
	public void setStrDataInicioCirurgia(String strDataInicioCirurgia) {
		this.strDataInicioCirurgia = strDataInicioCirurgia;
	}

	public String getStrDataInicioCirurgia() {
		return strDataInicioCirurgia;
	}
	
	public void setIsMultiProcedimentos(Boolean isMultiProcedimentos) {
		this.isMultiProcedimentos = isMultiProcedimentos;
	}

	public Boolean getIsMultiProcedimentos() {
		return isMultiProcedimentos;
	}

	public void setProcedimentos(List<String> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public List<String> getProcedimentos() {
		return procedimentos;
	}

	@Override
	public String toString() {
		return "MbcRelatCirurRealizPorEspecEProfVO [seqEspecialidade="
				+ seqEspecialidade + ", especialidade=" + especialidade
				+ ", nomePessoaFisica=" + nomePessoaFisica
				+ ", nomeUsualPessoaFisica=" + nomeUsualPessoaFisica
				+ ", cirurgiao=" + cirurgiao + ", seqCirurgia=" + seqCirurgia
				+ ", nomePaciente=" + nomePaciente + ", prontuario="
				+ prontuario + ", prontuarioFormatado=" + prontuarioFormatado
				+ ", dataInicioCirurgia=" + dataInicioCirurgia
				+ ", numeroAgenda=" + numeroAgenda + ", convenio=" + convenio
				+ ", procedimento=" + procedimento + ", clinica=" + clinica
				+ ", listaCirurgioes=" + listaCirurgioes + "]";
	}

}
