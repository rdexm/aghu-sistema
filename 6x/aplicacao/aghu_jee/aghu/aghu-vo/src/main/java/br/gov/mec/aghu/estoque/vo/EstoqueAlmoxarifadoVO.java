package br.gov.mec.aghu.estoque.vo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateFormatUtil;


public class EstoqueAlmoxarifadoVO implements BaseBean{
	
	private static final long serialVersionUID = 902042832907741549L;
	private SceAlmoxarifado almoxarifado;
	private ScoFornecedor fornecedor;
	private ScoMaterial material;
	private Integer qtdDisp;
	private Integer qtdBloq;
	private Integer qtdProb;
	private Integer qtdBloqTransf;
	private Integer qtdBloqConsumo;
	private Integer qtdEstqMin;
	private Integer qtdEstqMax;
	private Integer qtdPtPedido;
	private QtdeRpVO qtdeRp;
	private Integer tempoReposicao;
	private Boolean estocavel;
	private SceEstoqueAlmoxarifado estoque;
	
	private String quantidadeBloqDisp;
	private String quantidadeProb;
	private String quantidadeBloqTransf;
	private String quantidadeBloqConsumo;
	private String quantidadeEstqMin;
	private String quantidadeEstqMax;
	private String quantidadePtPedido;
	
	private Integer quantidadeDisponivelTodosEstoques;
	
	Locale locBR = new Locale("pt", "BR");
	DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
	DecimalFormat format;
	Double qtdSaldoSC = 0.0;
	
	public enum MessagesKey{
		TITLE_CALCULO_ESTOQUE_MINIMO,
		TITLE_CALCULO_P_PEDIDO,
		TITLE_SOLICITACAO_COMPRA_CONTRATO,
		TITLE_QUANTIDADE_SALDO_SOLICITACAO_COMPRA,
		TITLE_QUANTTIDADE_TOTAL,
		TITLE_ULTIMA_COMPRA,
		TITLE_ULTIMO_CONSUMO,
		TITLE_ATIVO_REPOSICAO;
	}
	
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}
	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public Integer getQtdDisp() {
		return qtdDisp;
	}
	public void setQtdDisp(Integer qtdDisp) {
		this.qtdDisp = qtdDisp;
	}
	public Integer getQtdBloq() {
		return qtdBloq;
	}
	public void setQtdBloq(Integer qtdBloq) {
		this.qtdBloq = qtdBloq;
	}
	public Integer getQtdProb() {
		return qtdProb;
	}
	public void setQtdProb(Integer qtdProb) {
		this.qtdProb = qtdProb;
	}
	public Integer getQtdBloqTransf() {
		return qtdBloqTransf;
	}
	public void setQtdBloqTransf(Integer qtdBloqTransf) {
		this.qtdBloqTransf = qtdBloqTransf;
	}
	public Integer getQtdBloqConsumo() {
		return qtdBloqConsumo;
	}
	public void setQtdBloqConsumo(Integer qtdBloqConsumo) {
		this.qtdBloqConsumo = qtdBloqConsumo;
	}
	public Integer getQtdEstqMin() {
		return qtdEstqMin;
	}
	public void setQtdEstqMin(Integer qtdEstqMin) {
		this.qtdEstqMin = qtdEstqMin;
	}
	public Integer getQtdEstqMax() {
		return qtdEstqMax;
	}
	public void setQtdEstqMax(Integer qtdEstqMax) {
		this.qtdEstqMax = qtdEstqMax;
	}
	public Integer getQtdPtPedido() {
		return qtdPtPedido;
	}
	public void setQtdPtPedido(Integer qtdPtPedido) {
		this.qtdPtPedido = qtdPtPedido;
	}
	public QtdeRpVO getQtdeRp() {
		return qtdeRp;
	}
	public void setQtdeRp(QtdeRpVO qtdeRp) {
		this.qtdeRp = qtdeRp;
	}
	public Integer getTempoReposicao() {
		return tempoReposicao;
	}
	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}
	public Boolean getEstocavel() {
		return estocavel;
	}
	public void setEstocavel(Boolean boolean1) {
		this.estocavel = boolean1;
	}
	public SceEstoqueAlmoxarifado getEstoque() {
		return estoque;
	}
	public void setEstoque(SceEstoqueAlmoxarifado estoque) {
		this.estoque = estoque;
	}

	public String getQuantidadeProb() {
		if(qtdProb != null) {
			quantidadeProb = abreviarValorMonetario(qtdProb);
		}
		return quantidadeProb;
	}
	public String getQuantidadeBloqTransf() {
		if(qtdBloqTransf != null) {
			quantidadeBloqTransf = abreviarValorMonetario(qtdBloqTransf);
		}
		return quantidadeBloqTransf;
	}
	public String getQuantidadeBloqConsumo() {
		if(qtdBloqConsumo != null) {
			quantidadeBloqConsumo = abreviarValorMonetario(qtdBloqConsumo);
		}
		return quantidadeBloqConsumo;
	}
	public String getQuantidadeEstqMin() {
		if(qtdEstqMin != null) {
			quantidadeEstqMin = abreviarValorMonetario(qtdEstqMin);
		}
		return quantidadeEstqMin;
	}
	public String getQuantidadeEstqMax() {
		if(qtdEstqMax != null) {
			quantidadeEstqMax = abreviarValorMonetario(qtdEstqMax);
		}
		return quantidadeEstqMax;
	}
	public String getQuantidadePtPedido() {
		if(qtdPtPedido != null) {
			quantidadePtPedido = abreviarValorMonetario(qtdPtPedido);
		}
		return quantidadePtPedido;
	}
	
	public Integer getQuantidadeDisponivelTodosEstoques() {
		return quantidadeDisponivelTodosEstoques;
	}
	public void setQuantidadeDisponivelTodosEstoques(Integer quantidadeDisponivelTodosEstoques) {
		this.quantidadeDisponivelTodosEstoques = quantidadeDisponivelTodosEstoques;
	}

	public String getIndEstqMinCalc(){
		
		String indCalcEstqMin="";
		
		if(estoque.getIndEstqMinCalc()){
			indCalcEstqMin = DominioSimNao.S.getDescricao();
		}
		else{
			indCalcEstqMin = DominioSimNao.N.getDescricao();
		}
		
		return indCalcEstqMin;
	}
	
	public String getIndPontoPedidoCalc(){
		
		String indPPedido="";
		
		if(estoque.getIndPontoPedidoCalc()){
			indPPedido = DominioSimNao.S.getDescricao();
		}
		else{
			indPPedido = DominioSimNao.N.getDescricao();
		}	
		return indPPedido;
	}
	
	public String getQtdSaldoSC(){
		
		dfSymbols.setDecimalSeparator(',');
		Double qtdSaldoSC = 0.0;
		
		if(estoque.getQtdeBloqueada() != null){
			qtdSaldoSC +=estoque.getQtdeBloqueada();
		}
		if(estoque.getQtdeDisponivel() != null){
			qtdSaldoSC +=estoque.getQtdeDisponivel();
		}
		format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
		return format.format(qtdSaldoSC);
	}
	
	public String getQtdTotal(){
		
		dfSymbols.setDecimalSeparator(',');	
		Double qtdTotal = 0.0;
		
		if(estoque.getQtdeBloqueada() != null){
			qtdTotal +=estoque.getQtdeBloqueada();
		}
		if(estoque.getQtdeDisponivel() != null){
			qtdTotal +=estoque.getQtdeDisponivel();
		}
		if(estoque.getQtdeBloqConsumo() != null){
			qtdTotal +=estoque.getQtdeBloqConsumo();
		}
		if(estoque.getQtdeBloqEntrTransf() != null){
			qtdTotal +=estoque.getQtdeBloqEntrTransf();
		}
		if(this.getQtdProb() != null){
			qtdTotal +=this.getQtdProb();
		}
		format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
		return format.format(qtdTotal);
	}
	
	public String getDtUltimaCompra(){
		return DateFormatUtil.fomataDiaMesAno(estoque.getDtUltimaCompra());
	}
	
	public String getDtUltimoConsumo(){
		return DateFormatUtil.fomataDiaMesAno(estoque.getDtUltimoConsumo());
	}
	
	public String getIndSituacao(){
		String indSituacao = "";
		if(estoque.getIndSituacao().equals(DominioSituacao.A)){
			indSituacao = DominioSimNao.S.getDescricao();
		}
		else{
			indSituacao = DominioSimNao.N.getDescricao();
		}
		return indSituacao;
	}
	
	public String getQuantidadeBloqDisp() {
		if(estoque.getQtdeBloqDispensacao() == null) {
			quantidadeBloqDisp = "0";
		} else {
			quantidadeBloqDisp =  estoque.getQtdeBloqDispensacao().toString();
		}
		return quantidadeBloqDisp;
	}
	
	public String getQtdeBloqDisp() {
		if(estoque.getQtdeBloqDispensacao() == null) {
			quantidadeBloqDisp = "0";
		} else {
			quantidadeBloqDisp =  abreviarValorMonetario(estoque.getQtdeBloqDispensacao());
		}
		return quantidadeBloqDisp;
	}
	
	private String abreviarValorMonetario(Integer valor) {
		return AghuNumberFormat.abreviarValorMonetario(valor, Boolean.FALSE);
	}
	
}
