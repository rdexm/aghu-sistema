package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ComunicacaoUsoMaterialVO;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ItensPendentesEntregaVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3651103667410359745L;
	
	
	private BigDecimal valorTotal;

	private Short itlNumero;
	private Integer qtdeItensAF;
	private Integer qtdeEntregueItensAF;
	private BigDecimal somatorioQtdEQtdEntregue;
	private Long qtdItemNotaPreRecebimento;
	private BigDecimal qtdEntregueXFatorConversao;
	private BigDecimal valorItemNotaPreRecebimento;
	private Double valorUnitario;
	private ScoUnidadeMedida unidadeMedida;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String codpro;
	private String cplpro;
	private Integer fatorConversaoForn;
	private Integer iafNumero;
	private Boolean indTermolabil;
	private Boolean indPatrimonio;
	private Boolean indProtese;
	private Boolean indPsicotropicos;
	
	private Boolean indCorrosivo;
	private Boolean indInflamavel;
	private Boolean indRadioativo;
	private Boolean indToxico;
	private ScoAutorizacaoForn scoAutForn;
	private String materialColor;
	
	private String materialFornecedorColor;
	
	private Integer parcela;
	private Integer peaSeq;
	private Integer qtdePea;
	private Integer qtdeEntreguePea;
	private Integer diferencaQtdeSaldoPea;
	private Integer afnNumero;
	
	private Integer solicitacaoCompra;
	private Integer solicitacaoServico;
	
	private BigDecimal qtdent;
	private BigDecimal vlrctb;
	private BigDecimal vlrbic;
	private Integer numnfi;
	private Integer codfor;
	
	private ScoFornecedor scoFornecedor;
	
	private BigDecimal valorSaldo;
	private Integer codigoServico;
	private String nomeServico;
	private String colorValorSaldo;
	private String colorValorEntregue;
	
	private List<ComunicacaoUsoMaterialVO> listaComunicacaoUsoMaterial = new ArrayList<ComunicacaoUsoMaterialVO>();
	private List<ComunicacaoUsoMaterialVO> listaCumSelecionada = new ArrayList<ComunicacaoUsoMaterialVO>();
	private String codCum;
	private String nomePaciente;
	private DominioTipoFaseSolicitacao tipoSolicitacao;
	
	/**
	 * 41654
	 */
	private List<ScoSolicitacaoEntregaVO> listSolicitacaoCompraItemRecebimento = new ArrayList<ScoSolicitacaoEntregaVO>();
	private List<ScoSolicitacaoEntregaVO> listSolicitacaoServicoItemRecebimento = new ArrayList<ScoSolicitacaoEntregaVO>();
	
	public ItensPendentesEntregaVO() {
	}

	public ItensPendentesEntregaVO(Short itlNumero, BigDecimal valorSaldo, Integer codigoServico, String nomeServico, Integer iafNumero) {
		this.itlNumero = itlNumero;
		this.valorSaldo = valorSaldo;
		this.codigoServico = codigoServico;
		this.nomeServico = nomeServico;
		this.iafNumero = iafNumero;
		
	}

	public ItensPendentesEntregaVO(String codpro, String cplpro, BigDecimal qtdent, BigDecimal vlrctb, BigDecimal vlrbic) {
		this.codpro = codpro;
		this.cplpro = cplpro;
		this.qtdent = qtdent;
		this.vlrctb = vlrctb;
		this.vlrbic = vlrbic;
	}
	
	public ItensPendentesEntregaVO(Integer solicitacaoCompra, Integer solicitacaoServico) {
		this.solicitacaoCompra = solicitacaoCompra;
		this.solicitacaoServico = solicitacaoServico;
	}

	public ItensPendentesEntregaVO(Short itlNumero, BigDecimal somatorioQtdEQtdEntregue, Long qtdItemNotaPreRecebimento,
			BigDecimal valorItemNotaPreRecebimento, Double valorUnitario, ScoUnidadeMedida unidadeMedida,
			Integer codigoMaterial, String nomeMaterial, String codpro, String cplpro, Integer iafNumero, Integer fatorConversaoForn,
			Boolean indTermolabil, Boolean indCorrosivo, Boolean indInflamavel, Boolean indRadioativo, Boolean indToxico){

		this.itlNumero = itlNumero;
		this.somatorioQtdEQtdEntregue = somatorioQtdEQtdEntregue; 
		this.qtdItemNotaPreRecebimento = qtdItemNotaPreRecebimento;
		this.valorItemNotaPreRecebimento = valorItemNotaPreRecebimento;
		this.valorUnitario = valorUnitario;
		this.unidadeMedida = unidadeMedida;
		this.codigoMaterial = codigoMaterial;
		this.nomeMaterial = nomeMaterial;
		this.codpro = codpro;
		this.cplpro = cplpro;
		this.iafNumero = iafNumero;
		this.fatorConversaoForn = fatorConversaoForn;
		this.indTermolabil = indTermolabil;
		this.indCorrosivo = indCorrosivo;
		this.indInflamavel = indInflamavel;
		this.indRadioativo = indRadioativo;
		this.indToxico = indToxico;
		this.indPatrimonio = Boolean.FALSE;
		this.indProtese = Boolean.FALSE;
		this.indPsicotropicos = Boolean.FALSE;
		
		if(this.qtdItemNotaPreRecebimento != null && this.fatorConversaoForn != null){
			this.qtdEntregueXFatorConversao = new BigDecimal(qtdItemNotaPreRecebimento.intValue() * fatorConversaoForn);
		}
		
		if(this.indCorrosivo || this.indInflamavel || this.indRadioativo || this.indToxico){
			this.materialColor = "#FFA07A";
		}
		
		if(this.qtdEntregueXFatorConversao != null){
			this.colorValorEntregue = "#B0C4DE";
			if(this.somatorioQtdEQtdEntregue != null){
				if(this.somatorioQtdEQtdEntregue.compareTo(this.qtdEntregueXFatorConversao) < 0){
					this.colorValorEntregue = "#FFA07A";
				}
			}
		}
		if(this.valorItemNotaPreRecebimento != null){
			this.colorValorSaldo = "#B0C4DE";
		}
		
		if (this.codpro != null) {
			this.materialFornecedorColor = "#B0C4DE";
		}
		
	}
	
	public ItensPendentesEntregaVO(Integer parcela, Integer qtdePea, Integer qtdeEntreguePea, Integer peaSeq){
		this.parcela = parcela;
		this.peaSeq = peaSeq;
		this.qtdePea = qtdePea;
		this.qtdeEntreguePea = qtdeEntreguePea;
		if(this.qtdeEntreguePea != null){
			this.diferencaQtdeSaldoPea = this.qtdePea - this.qtdeEntreguePea;
		} else {
			this.diferencaQtdeSaldoPea = this.qtdePea;
		}
			
	}
	
	public enum Fields {

		ITL_NUMERO("itlNumero"),
		QTDE_ITENS_AF("qtdeItensAF"),
		QTDE_ENTREGUE_ITENS_AF("qtdeEntregueItensAF"),
		SOMATORIO_QTDE_QTDE_ENTREGUE("somatorioQtdEQtdEntregue"),
		QTDE_ITEM_NOTA_RECEBIMENTO("qtdItemNotaPreRecebimento"),
		VALOR_ITEM_NOTA_RECEBIMENTO("valorItemNotaPreRecebimento"),
		VALOR_UNITARIO("valorUnitario"),
		UNIDADE_MEDIDA("unidadeMedida"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		CODPRO("codpro"),
		CPLPRO("cplpro"),
		FATOR_CONVERSAO_FORN("fatorConversaoForn"),
		IAF_NUMERO("iafNumero"),
		IND_TERMOLABIL("indTermolabil"),
		IND_CORROSIVO("indCorrosivo"),
		IND_INFLAMAVEL("indInflamavel"),
		IND_RADIOATIVO("indRadioativo"),
		IND_TOXICO("indToxico"),
		SCO_AUT_FORN("scoAutForn"),
		PARCELA("parcela"),
		PEA_SEQ("peaSeq"),
		QTDE_PEA("qtdePea"),
		QTDE_ENTREGUE_PEA("qtdeEntreguePea"),
		DIFERENCA_QTDE_SALDO_PEA("diferencaQtdeSaldoPea"),
		SC("solicitacaoCompra"),
		SS("solicitacaoServico"),
		QTDENT("qtdent"),
		VLRCTB("vlrctb"),
		VLRBIC("vlrbic"),
		NUMNFI("numnfi"),
		CODFOR("codfor"),
		SCO_FORNECEDOR("scoFornecedor"),
		VALOR_SALDO("valorSaldo"),
		CODIGO_SERVICO("codigoServico"),
		NOME_SERVICO("nomeServico");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Short getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	public Integer getQtdeItensAF() {
		return qtdeItensAF;
	}

	public void setQtdeItensAF(Integer qtdeItensAF) {
		this.qtdeItensAF = qtdeItensAF;
	}

	public Integer getQtdeEntregueItensAF() {
		return qtdeEntregueItensAF;
	}

	public void setQtdeEntregueItensAF(Integer qtdeEntregueItensAF) {
		this.qtdeEntregueItensAF = qtdeEntregueItensAF;
	}

	public BigDecimal getSomatorioQtdEQtdEntregue() {
		return somatorioQtdEQtdEntregue;
	}

	public void setSomatorioQtdEQtdEntregue(BigDecimal somatorioQtdEQtdEntregue) {
		this.somatorioQtdEQtdEntregue = somatorioQtdEQtdEntregue;
	}

	public Long getQtdItemNotaPreRecebimento() {
		return qtdItemNotaPreRecebimento;
	}

	public void setQtdItemNotaPreRecebimento(Long qtdItemNotaPreRecebimento) {
		this.qtdItemNotaPreRecebimento = qtdItemNotaPreRecebimento;
	}

	public BigDecimal getValorItemNotaPreRecebimento() {
		return valorItemNotaPreRecebimento;
	}

	public void setValorItemNotaPreRecebimento(BigDecimal valorItemNotaPreRecebimento) {
		this.valorItemNotaPreRecebimento = valorItemNotaPreRecebimento;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public String getCodpro() {
		return codpro;
	}

	public void setCodpro(String codpro) {
		this.codpro = codpro;
	}

	public String getCplpro() {
		return cplpro;
	}

	public void setCplpro(String cplpro) {
		this.cplpro = cplpro;
	}

	public Integer getFatorConversaoForn() {
		return fatorConversaoForn;
	}

	public void setFatorConversaoForn(Integer fatorConversaoForn) {
		this.fatorConversaoForn = fatorConversaoForn;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Boolean getIndTermolabil() {
		return indTermolabil;
	}

	public void setIndTermolabil(Boolean indTermolabil) {
		this.indTermolabil = indTermolabil;
	}

	public Boolean getIndCorrosivo() {
		return indCorrosivo;
	}

	public void setIndCorrosivo(Boolean indCorrosivo) {
		this.indCorrosivo = indCorrosivo;
	}

	public Boolean getIndInflamavel() {
		return indInflamavel;
	}

	public void setIndInflamavel(Boolean indInflamavel) {
		this.indInflamavel = indInflamavel;
	}

	public Boolean getIndRadioativo() {
		return indRadioativo;
	}

	public void setIndRadioativo(Boolean indRadioativo) {
		this.indRadioativo = indRadioativo;
	}

	public Boolean getIndToxico() {
		return indToxico;
	}

	public void setIndToxico(Boolean indToxico) {
		this.indToxico = indToxico;
	}

	public ScoAutorizacaoForn getScoAutForn() {
		return scoAutForn;
	}

	public void setScoAutForn(ScoAutorizacaoForn scoAutForn) {
		this.scoAutForn = scoAutForn;
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public Integer getPeaSeq() {
		return peaSeq;
	}

	public void setPeaSeq(Integer peaSeq) {
		this.peaSeq = peaSeq;
	}

	public Integer getQtdePea() {
		return qtdePea;
	}

	public void setQtdePea(Integer qtdePea) {
		this.qtdePea = qtdePea;
	}

	public Integer getQtdeEntreguePea() {
		return qtdeEntreguePea;
	}

	public void setQtdeEntreguePea(Integer qtdeEntreguePea) {
		this.qtdeEntreguePea = qtdeEntreguePea;
	}

	public Integer getDiferencaQtdeSaldoPea() {
		return diferencaQtdeSaldoPea;
	}

	public void setDiferencaQtdeSaldoPea(Integer diferencaQtdeSaldoPea) {
		this.diferencaQtdeSaldoPea = diferencaQtdeSaldoPea;
	}

	public Integer getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(Integer solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public Integer getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(Integer solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public BigDecimal getQtdent() {
		return qtdent;
	}

	public void setQtdent(BigDecimal qtdent) {
		this.qtdent = qtdent;
	}

	public BigDecimal getVlrctb() {
		return vlrctb;
	}

	public void setVlrctb(BigDecimal vlrctb) {
		this.vlrctb = vlrctb;
	}

	public BigDecimal getVlrbic() {
		return vlrbic;
	}

	public void setVlrbic(BigDecimal vlrbic) {
		this.vlrbic = vlrbic;
	}

	public Integer getNumnfi() {
		return numnfi;
	}

	public void setNumnfi(Integer numnfi) {
		this.numnfi = numnfi;
	}

	public Integer getCodfor() {
		return codfor;
	}

	public void setCodfor(Integer codfor) {
		this.codfor = codfor;
	}

	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	public BigDecimal getValorSaldo() {
		return valorSaldo;
	}

	public void setValorSaldo(BigDecimal valorSaldo) {
		this.valorSaldo = valorSaldo;
	}

	public Integer getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}

	public BigDecimal getQtdEntregueXFatorConversao() {
		return qtdEntregueXFatorConversao;
	}

	public void setQtdEntregueXFatorConversao(BigDecimal qtdEntregueXFatorConversao) {
		this.qtdEntregueXFatorConversao = qtdEntregueXFatorConversao;
	}

	public String getMaterialColor() {
		return materialColor;
	}

	public void setMaterialColor(String materialColor) {
		this.materialColor = materialColor;
	}

	public String getColorValorSaldo() {
		return colorValorSaldo;
	}

	public void setColorValorSaldo(String colorValorSaldo) {
		this.colorValorSaldo = colorValorSaldo;
	}

	public String getColorValorEntregue() {
		return colorValorEntregue;
	}

	public void setColorValorEntregue(String colorValorEntregue) {
		this.colorValorEntregue = colorValorEntregue;
	}

	public String getMaterialFornecedorColor() {
		return materialFornecedorColor;
	}

	public void setMaterialFornecedorColor(String materialFornecedorColor) {
		this.materialFornecedorColor = materialFornecedorColor;
	}

	public List<ComunicacaoUsoMaterialVO> getListaComunicacaoUsoMaterial() {
		return listaComunicacaoUsoMaterial;
	}

	public void setListaComunicacaoUsoMaterial(
			List<ComunicacaoUsoMaterialVO> listaComunicacaoUsoMaterial) {
		this.listaComunicacaoUsoMaterial = listaComunicacaoUsoMaterial;
	}

	public String getCodCum() {
		return codCum;
	}

	public void setCodCum(String codCum) {
		this.codCum = codCum;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public List<ComunicacaoUsoMaterialVO> getListaCumSelecionada() {
		return listaCumSelecionada;
	}

	public void setListaCumSelecionada(
			List<ComunicacaoUsoMaterialVO> listaCumSelecionada) {
		this.listaCumSelecionada = listaCumSelecionada;
	}

	public List<ScoSolicitacaoEntregaVO> getListSolicitacaoCompraItemRecebimento() {
		return listSolicitacaoCompraItemRecebimento;
	}

	public void setListSolicitacaoCompraItemRecebimento(
			List<ScoSolicitacaoEntregaVO> listSolicitacaoCompraItemRecebimento) {
		this.listSolicitacaoCompraItemRecebimento = listSolicitacaoCompraItemRecebimento;
	}

	public List<ScoSolicitacaoEntregaVO> getListSolicitacaoServicoItemRecebimento() {
		return listSolicitacaoServicoItemRecebimento;
	}

	public void setListSolicitacaoServicoItemRecebimento(
			List<ScoSolicitacaoEntregaVO> listSolicitacaoServicoItemRecebimento) {
		this.listSolicitacaoServicoItemRecebimento = listSolicitacaoServicoItemRecebimento;
	}

	public Boolean getIndPatrimonio() {
		return indPatrimonio;
	}

	public void setIndPatrimonio(Boolean indPatrimonio) {
		this.indPatrimonio = indPatrimonio;
	}

	public Boolean getIndProtese() {
		return indProtese;
	}

	public void setIndProtese(Boolean indProtese) {
		this.indProtese = indProtese;
	}

	public Boolean getIndPsicotropicos() {
		return indPsicotropicos;
	}

	public void setIndPsicotropicos(Boolean indPsicotropicos) {
		this.indPsicotropicos = indPsicotropicos;
	}

	public DominioTipoFaseSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoFaseSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

}
