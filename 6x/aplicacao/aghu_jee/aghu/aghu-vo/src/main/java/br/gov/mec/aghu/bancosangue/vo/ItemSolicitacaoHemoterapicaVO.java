package br.gov.mec.aghu.bancosangue.vo;

import java.util.Date;

public class ItemSolicitacaoHemoterapicaVO {
	
	private Integer sheAtdSeq;
	private Integer sheSeq;
	private Short sequencia;
	private String pheCodigo;
	private String csaCodigo;
	private Short tfqSeq;
	private Boolean indIrradiado;
	private Boolean indFiltrado;
	private Boolean indLavado;
	private Short frequencia;
	private Short qtdeAplicacoes;
	private Byte qtdeUnidades;
	private Short qtdeMl;
	private Date dthrExecProcedimento;
	private Date dthrDigtExecucao;
	private Integer matriculaServidor;
	private Short vinServidor;
	private Boolean indImprLaudo;
	private Boolean indAferese;
	
	public ItemSolicitacaoHemoterapicaVO() {
	}

	public ItemSolicitacaoHemoterapicaVO(Integer sheAtdSeq, Integer sheSeq,
			Short sequencia, String pheCodigo, String csaCodigo, Short tfqSeq,
			Boolean indIrradiado, Boolean indFiltrado, Boolean indLavado,
			Short frequencia, Short qtdeAplicacoes, Byte qtdeUnidades,
			Short qtdeMl, Date dthrExecProcedimento, Date dthrDigtExecucao,
			Integer matriculaServidor, Short vinServidor, Boolean indImprLaudo,
			Boolean indAferese) {
		super();
		this.sheAtdSeq = sheAtdSeq;
		this.sheSeq = sheSeq;
		this.sequencia = sequencia;
		this.pheCodigo = pheCodigo;
		this.csaCodigo = csaCodigo;
		this.tfqSeq = tfqSeq;
		this.indIrradiado = indIrradiado;
		this.indFiltrado = indFiltrado;
		this.indLavado = indLavado;
		this.frequencia = frequencia;
		this.qtdeAplicacoes = qtdeAplicacoes;
		this.qtdeUnidades = qtdeUnidades;
		this.qtdeMl = qtdeMl;
		this.dthrExecProcedimento = dthrExecProcedimento;
		this.dthrDigtExecucao = dthrDigtExecucao;
		this.matriculaServidor = matriculaServidor;
		this.vinServidor = vinServidor;
		this.indImprLaudo = indImprLaudo;
		this.indAferese = indAferese;
	}



	public Integer getSheAtdSeq() {
		return sheAtdSeq;
	}

	public void setSheAtdSeq(Integer sheAtdSeq) {
		this.sheAtdSeq = sheAtdSeq;
	}

	public Integer getSheSeq() {
		return sheSeq;
	}

	public void setSheSeq(Integer sheSeq) {
		this.sheSeq = sheSeq;
	}

	public Short getSequencia() {
		return sequencia;
	}

	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}

	public String getPheCodigo() {
		return pheCodigo;
	}

	public void setPheCodigo(String pheCodigo) {
		this.pheCodigo = pheCodigo;
	}

	public String getCsaCodigo() {
		return csaCodigo;
	}

	public void setCsaCodigo(String csaCodigo) {
		this.csaCodigo = csaCodigo;
	}

	public Short getTfqSeq() {
		return tfqSeq;
	}

	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}

	public Boolean getIndIrradiado() {
		return indIrradiado;
	}

	public void setIndIrradiado(Boolean indIrradiado) {
		this.indIrradiado = indIrradiado;
	}

	public Boolean getIndFiltrado() {
		return indFiltrado;
	}

	public void setIndFiltrado(Boolean indFiltrado) {
		this.indFiltrado = indFiltrado;
	}

	public Boolean getIndLavado() {
		return indLavado;
	}

	public void setIndLavado(Boolean indLavado) {
		this.indLavado = indLavado;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public Short getQtdeAplicacoes() {
		return qtdeAplicacoes;
	}

	public void setQtdeAplicacoes(Short qtdeAplicacoes) {
		this.qtdeAplicacoes = qtdeAplicacoes;
	}

	public Byte getQtdeUnidades() {
		return qtdeUnidades;
	}

	public void setQtdeUnidades(Byte qtdeUnidades) {
		this.qtdeUnidades = qtdeUnidades;
	}

	public Short getQtdeMl() {
		return qtdeMl;
	}

	public void setQtdeMl(Short qtdeMl) {
		this.qtdeMl = qtdeMl;
	}

	public Date getDthrExecProcedimento() {
		return dthrExecProcedimento;
	}

	public void setDthrExecProcedimento(Date dthrExecProcedimento) {
		this.dthrExecProcedimento = dthrExecProcedimento;
	}

	public Date getDthrDigtExecucao() {
		return dthrDigtExecucao;
	}

	public void setDthrDigtExecucao(Date dthrDigtExecucao) {
		this.dthrDigtExecucao = dthrDigtExecucao;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public Short getVinServidor() {
		return vinServidor;
	}

	public void setVinServidor(Short vinServidor) {
		this.vinServidor = vinServidor;
	}

	public Boolean getIndImprLaudo() {
		return indImprLaudo;
	}

	public void setIndImprLaudo(Boolean indImprLaudo) {
		this.indImprLaudo = indImprLaudo;
	}

	public Boolean getIndAferese() {
		return indAferese;
	}

	public void setIndAferese(Boolean indAferese) {
		this.indAferese = indAferese;
	}
	
	public enum Fields {
		
		SHE_ATD_SEQ("sheAtdSeq"), 
		SHE_SEQ("sheSeq"), 
		SEQUENCIA("sequencia"), 
		PHE_CODIGO("pheCodigo"), 
		CSA_CODIGO("csaCodigo"), 
		TFQ_SEQ("tfqSeq"), 
		MAT_SERVIDOR("matriculaServidor"),
		VIN_SERVIDOR("vinServidor"),
		IND_IRRADIADO("indIrradiado"), 
		FREQUENCIA("frequencia"), 
		QTDE_APLICACOES("qtdeAplicacoes"), 
		IND_FILTRADO("indFiltrado"), 
		IND_LAVADO("indLavado"), 
		QTDE_UNIDADES("qtdeUnidades"), 
		QTDE_ML("qtdeMl"), 
		DTHR_EXEC_PROCEDIMENTO("dthrExecProcedimento"), 
		DTHR_DIGT_EXECUCAO("dthrDigtExecucao"), 
		IND_IMPR_LAUDO("indImprLaudo"), 
		IND_AFERESE("indAferese");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
