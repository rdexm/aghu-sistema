package br.gov.mec.aghu.comissoes.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;

public class VMpmItemPrcrMdtosVO {

	
	private Integer pacCodigo;
	
	private Integer prontuario;
	
	private Integer medMatCodigo;
	
	private Integer pmdAtdSeq;
	
	private Integer jumSeq;
	
	private String nomePaciente;
	
	private String descricaoMed;
	
	private String nomeSolicitante;
	
	private String ltoLtoId;
	
	private Integer qrtNumero;
	
	private Integer unfSeq;
	
	private Date dthrFim;
	
	private Date criadoEm;
	
	private String indSituacao;
	
	private String indGmr;

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getDescricaoMed() {
		return descricaoMed;
	}

	public void setDescricaoMed(String descricaoMed) {
		this.descricaoMed = descricaoMed;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Integer getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Integer qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public Integer getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Integer unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public String getIndGmr() {
		return indGmr;
	}

	public void setIndGmr(String indGmr) {
		this.indGmr = indGmr;
	}
	
	public String retornarPorExtenso(){
		
		if(!indSituacao.isEmpty()){
			if(indSituacao.equals("A")){
				return DominioSituacaoSolicitacaoMedicamento.A.getDescricao();
			}
			if(indSituacao.equals("P")){
				return DominioSituacaoSolicitacaoMedicamento.P.getDescricao();
			}
			if(indSituacao.equals("T")){
				return DominioSituacaoSolicitacaoMedicamento.T.getDescricao();
			}
		}
		return "";
	}

	public enum Fields {
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		MED_MAT_CODIGO("medMatCodigo"),
		PMD_ATD_SEQ("pmdAtdSeq"),
		JUM_SEQ("jumSeq"),
		NOME_PACIENTE("nomePaciente"),
		DESCRICAO_MED("descricaoMed"),
		NOME_SOLICITANTE("nomeSolicitante"),
		LTO_LTO_ID("ltoLtoId"),
		QRT_NUMERO("qrtNumero"),
		DTHR_FIM("dthrFim"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		IND_GMR("indGmr"),
		UNF_SEQ("unfSeq");
		

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}
