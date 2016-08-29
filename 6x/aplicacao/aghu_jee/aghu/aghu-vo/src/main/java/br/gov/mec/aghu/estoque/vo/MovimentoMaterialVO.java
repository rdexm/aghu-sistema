package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.model.SceMovimentoMaterialId;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class MovimentoMaterialVO implements BaseEntity, Serializable {

	private static final long serialVersionUID = 2151458615986639034L;
	
	// Pesquisa do Movimento
	private SceMovimentoMaterialId id;
	private String mes;
	private String tipo;
	private Integer documento;
	private Boolean estornado;
	private String dtGeracao;
	private Integer centroCusto;
	private Short almoxarifado;
	private Integer fornecedor;
	private String material;
	private BigDecimal valor;
	private String unidade;
	private Integer quantidade;
	private String nomeMaterial;
		
	//Modal com detalhes do movimento
	private String almoxarifadoModal;
	private String almoxarifadoComplementoModal;
	private String centroCustoAplicacaoModal;
	private String centroCustoReqModal;
	private Short itemDocGeracaoModal;
	private Integer nroDocRefereModal;
	private String historicoModal;
	private Integer qtdeRequisitadaModal;
	private Long qtdePosMovimentoModal;
	private String custoMedioPonderadoGerModal;
	private BigDecimal residuo;
	private String motivoModal;
	private String usuarioModal;
	private String descricaoModal;
	private Integer nroDocGeracao;
	
	//Parametros retorno SCEP_MMT_CALC_VAL
	private BigDecimal custoMedioPonderado;
	private Double valorTotalAnt;
	private Integer qtdeTotalAnt;
	private Double residuoAnt;
	
	private Integer numeroProntuario;
	private Integer numeroAtendimento;
	private String nomePaciente;
	private String mesAno;
	private Date competencia;
	private Integer seq;
	private Short tipoMovimento; 
	
	public BigDecimal getCustoMedioPonderado() {
		return custoMedioPonderado;
	}
	public void setCustoMedioPonderado(BigDecimal custoMedioPonderado) {
		this.custoMedioPonderado = custoMedioPonderado;
	}
	public Double getValorTotalAnt() {
		return valorTotalAnt;
	}
	public void setValorTotalAnt(Double valorTotalAnt) {
		this.valorTotalAnt = valorTotalAnt;
	}
	public Integer getQtdeTotalAnt() {
		return qtdeTotalAnt;
	}
	public void setQtdeTotalAnt(Integer qtdeTotalAnt) {
		this.qtdeTotalAnt = qtdeTotalAnt;
	}
	public Double getResiduoAnt() {
		return residuoAnt;
	}
	public void setResiduoAnt(Double residuoAnt) {
		this.residuoAnt = residuoAnt;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public Integer getDocumento() {
		return documento;
	}
	public void setDocumento(Integer documento) {
		this.documento = documento;
	}
	public Boolean getEstornado() {
		return estornado;
	}
	public void setEstornado(Boolean estornado) {
		this.estornado = estornado;
	}
	public String getDtGeracao() {
		return dtGeracao;
	}
	public void setDtGeracao(String dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	public Integer getCentroCusto() {
		return centroCusto;
	}
	public void setCentroCusto(Integer centroCusto) {
		this.centroCusto = centroCusto;
	}
	public Short getAlmoxarifado() {
		return almoxarifado;
	}
	public void setAlmoxarifado(Short almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	public Integer getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(Integer fornecedor) {
		this.fornecedor = fornecedor;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public SceMovimentoMaterialId getId() {
		return id;
	}
	public void setId(SceMovimentoMaterialId id) {
		this.id = id;
	}
	public String getAlmoxarifadoModal() {
		return almoxarifadoModal;
	}
	public void setAlmoxarifadoModal(String almoxarifadoModal) {
		this.almoxarifadoModal = almoxarifadoModal;
	}
	public String getAlmoxarifadoComplementoModal() {
		return almoxarifadoComplementoModal;
	}
	public void setAlmoxarifadoComplementoModal(String almoxarifadoComplementoModal) {
		this.almoxarifadoComplementoModal = almoxarifadoComplementoModal;
	}
	public String getCentroCustoAplicacaoModal() {
		return centroCustoAplicacaoModal;
	}
	public void setCentroCustoAplicacaoModal(String centroCustoAplicacaoModal) {
		this.centroCustoAplicacaoModal = centroCustoAplicacaoModal;
	}
	public String getCentroCustoReqModal() {
		return centroCustoReqModal;
	}
	public void setCentroCustoReqModal(String centroCustoReqModal) {
		this.centroCustoReqModal = centroCustoReqModal;
	}
	public Short getItemDocGeracaoModal() {
		return itemDocGeracaoModal;
	}
	public void setItemDocGeracaoModal(Short itemDocGeracaoModal) {
		this.itemDocGeracaoModal = itemDocGeracaoModal;
	}
	public Integer getNroDocRefereModal() {
		return nroDocRefereModal;
	}
	public void setNroDocRefereModal(Integer nroDocRefereModal) {
		this.nroDocRefereModal = nroDocRefereModal;
	}
	public String getHistoricoModal() {
		return historicoModal;
	}
	public void setHistoricoModal(String historicoModal) {
		this.historicoModal = historicoModal;
	}
	public Integer getQtdeRequisitadaModal() {
		return qtdeRequisitadaModal;
	}
	public void setQtdeRequisitadaModal(Integer qtdeRequisitadaModal) {
		this.qtdeRequisitadaModal = qtdeRequisitadaModal;
	}
	public Long getQtdePosMovimentoModal() {
		return qtdePosMovimentoModal;
	}
	public void setQtdePosMovimentoModal(Long qtdePosMovimentoModal) {
		this.qtdePosMovimentoModal = qtdePosMovimentoModal;
	}
	public String getCustoMedioPonderadoGerModal() {
		return custoMedioPonderadoGerModal;
	}
	public void setCustoMedioPonderadoGerModal(
			String custoMedioPonderadoGerModal) {
		this.custoMedioPonderadoGerModal = custoMedioPonderadoGerModal;
	}
	public BigDecimal getResiduo() {
		return residuo;
	}
	public void setResiduo(BigDecimal residuo) {
		this.residuo = residuo;
	}
	public String getMotivoModal() {
		return motivoModal;
	}
	public void setMotivoModal(String motivoModal) {
		this.motivoModal = motivoModal;
	}
	public String getUsuarioModal() {
		return usuarioModal;
	}
	public void setUsuarioModal(String usuarioModal) {
		this.usuarioModal = usuarioModal;
	}
	public String getDescricaoModal() {
		return descricaoModal;
	}
	public void setDescricaoModal(String descricaoModal) {
		this.descricaoModal = descricaoModal;
	}
	public Integer getNroDocGeracao() {
		return nroDocGeracao;
	}
	public void setNroDocGeracao(Integer nroDocGeracao) {
		this.nroDocGeracao = nroDocGeracao;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	
	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}
	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}
	public Integer getNumeroAtendimento() {
		return numeroAtendimento;
	}
	public void setNumeroAtendimento(Integer numeroAtendimento) {
		this.numeroAtendimento = numeroAtendimento;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	
	
	

	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((competencia == null) ? 0 : competencia.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mesAno == null) ? 0 : mesAno.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MovimentoMaterialVO other = (MovimentoMaterialVO) obj;
		if (competencia == null) {
			if (other.competencia != null){
				return false;
			}
		} else if (!competencia.equals(other.competencia)){
			return false;
		}
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		if (mesAno == null) {
			if (other.mesAno != null){
				return false;
			}
		} else if (!mesAno.equals(other.mesAno)){
			return false;
		}
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
	public String getMesAno() {
		mesAno = DateUtil.dataToString(competencia, "MM/yyyy");
		return mesAno;
	}
	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}

	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getTipoMovimento() {
		return tipoMovimento;
	}
	public void setTipoMovimento(Short tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public enum Fields {
		SEQ("seq"),
		DT_COMPETENCIA("competencia"),
		TIPO_MOVIMENTO_SEQ("tipoMovimento"),
		VALOR("valor"),
		IND_ESTORNO("estornado"),
		MES_ANO("mesAno");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	
	
}
