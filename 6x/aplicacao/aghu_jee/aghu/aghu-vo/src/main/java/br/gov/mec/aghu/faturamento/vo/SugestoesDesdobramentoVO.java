package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class SugestoesDesdobramentoVO implements BaseBean {

	/**
	 *
	 */
	private static final long serialVersionUID = 1094135274111029870L;
	
	private Date dthrSugestao;
	private String origem;
	private String ltoId;
	private String descricao;
	private Integer prontuario;
	private String nome;
	private Integer cthSeq;
	private Date dtInternacaoAdministrativa;
	private Date dtAltaAdministrativa;
	
	/**
	 * @return the dthrSugestao
	 */
	public Date getDthrSugestao() {
		return dthrSugestao;
	}

	/**
	 * @param dthrSugestao the dthrSugestao to set
	 */
	public void setDthrSugestao(Date dthrSugestao) {
		this.dthrSugestao = dthrSugestao;
	}

	/**
	 * @return the origem
	 */
	public String getOrigem() {
		return origem;
	}

	/**
	 * @param origem the origem to set
	 */
	public void setOrigem(String origem) {
		this.origem = origem;
	}

	/**
	 * @return the ltoId
	 */
	public String getLtoId() {
		return ltoId;
	}

	/**
	 * @param ltoId the ltoId to set
	 */
	public void setLtoId(String ltoId) {
		this.ltoId = ltoId;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the prontuario
	 */
	public Integer getProntuario() {
		return prontuario;
	}

	/**
	 * @param prontuario the prontuario to set
	 */
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the cthSeq
	 */
	public Integer getCthSeq() {
		return cthSeq;
	}

	/**
	 * @param cthSeq the cthSeq to set
	 */
	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	/**
	 * @return the dtInternacaoAdministrativa
	 */
	public Date getDtInternacaoAdministrativa() {
		return dtInternacaoAdministrativa;
	}

	/**
	 * @param dtInternacaoAdministrativa the dtInternacaoAdministrativa to set
	 */
	public void setDtInternacaoAdministrativa(Date dtInternacaoAdministrativa) {
		this.dtInternacaoAdministrativa = dtInternacaoAdministrativa;
	}

	/**
	 * @return the dtAltaAdministrativa
	 */
	public Date getDtAltaAdministrativa() {
		return dtAltaAdministrativa;
	}

	/**
	 * @param dtAltaAdministrativa the dtAltaAdministrativa to set
	 */
	public void setDtAltaAdministrativa(Date dtAltaAdministrativa) {
		this.dtAltaAdministrativa = dtAltaAdministrativa;
	}

	public enum Fields {
		
		CSD_DTHR_SUGESTAO("dthrSugestao"),
		CSD_ORIGEM("origem"),
		CSD_LTO_ID("ltoId"),
		MDS_DESCRICAO("descricao"),
		PAC_PRONTUARIO("prontuario"),
		PAC_NOME ("nome"),
		CSD_CTH_SEQ("cthSeq"),
		CTH_INT_ADMT("dtInternacaoAdministrativa"),
		CTH_ALTA_ADMT("dtAltaAdministrativa");
	
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
