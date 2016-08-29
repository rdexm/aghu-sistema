package br.gov.mec.aghu.compras.autfornecimento.action;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.EntregaProgramadaGrupoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroGrupoMaterialEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaGrupoMaterialEntregaProgramadaController extends ActionController {
	
	private static final String PROGRAMACAO_ENTREGA_GLOBAL_FORNECEDORES = "programacaoEntregaGlobalFornecedores";

	private static final long serialVersionUID = 128015135703844211L;
	
	@EJB
	protected IComprasFacade comprasFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	private FiltroGrupoMaterialEntregaProgramadaVO 	filtro = new FiltroGrupoMaterialEntregaProgramadaVO();
	
	private List<EntregaProgramadaGrupoMaterialVO> listagem;
	private BigDecimal valorTotalSaldoProgramado = BigDecimal.ZERO;
	private BigDecimal valorTotalValorLiberar = BigDecimal.ZERO;
	private BigDecimal valorTotalValorLiberado = BigDecimal.ZERO;
	private BigDecimal valorTotalValorAtraso = BigDecimal.ZERO;
	
	private Date dataLiberacao;
	
	private Integer gmtCodigo;
	private String gmtDescricao;
	
	private String dataLimite;
	private String dataInicialLote;
	private String dataFinalLote;
	
	private EntregasGlobaisAcesso tipoValor;
	
	private ProgramacaoEntregaGlobalTotalizadorVO totalizador;
	
	@Inject
	private ProgramacaoEntregaGlobalFornecedoresController programacaoEntregaGlobalFornecedoresController;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
//		resetTela();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		Calendar cData = Calendar.getInstance();
		cData.set(Calendar.DAY_OF_MONTH, 10);
		cData.add(Calendar.MONTH, 1);
		dataLimite = sdf.format(cData.getTime());
		cData.set(Calendar.DAY_OF_MONTH, 11);
		dataInicialLote = sdf.format(cData.getTime());
		cData = Calendar.getInstance();
		cData.set(Calendar.DAY_OF_MONTH, 10);
		cData.add(Calendar.MONTH, 2);
		dataFinalLote = sdf.format(cData.getTime());
		
	}
	

	private void resetTela() {
		filtro = new FiltroGrupoMaterialEntregaProgramadaVO();
		dataLiberacao = null;
		resetValores();
		listagem = null;
		totalizador = new ProgramacaoEntregaGlobalTotalizadorVO();
	}

	private void resetValores() {
		valorTotalSaldoProgramado = BigDecimal.ZERO;
		valorTotalValorLiberar = BigDecimal.ZERO;
		valorTotalValorLiberado = BigDecimal.ZERO;
		valorTotalValorAtraso = BigDecimal.ZERO;
	}

	public void pesquisar() {
		resetValores();
		try {
			String mensagem =comprasFacade.validaDatas(filtro); 
			if (!"".equals(mensagem)) {
				this.apresentarMsgNegocio(Severity.INFO, mensagem);
				return;
			}
			dataLiberacao = comprasFacade.calculaDataLiberacao(filtro);
			listagem = this.comprasFacade.obtemGrupoMateriaEntregaProgramada(filtro, dataLiberacao);
			
			totalizaValores();
			
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.INFO, "ERRO_INESPERADO");
		}
	}

	private void totalizaValores() {
		totalizador = comprasFacade.totalizaValores(listagem);
	}

	public void limpar() {
		resetTela();
	}
	
	public String redirecionaConsultaPorFornecedores(EntregaProgramadaGrupoMaterialVO item) {
		if(item.getGmtDescricao()!=null){
			this.programacaoEntregaGlobalFornecedoresController.setDescricaoGrupoMaterial(item.getGmtDescricao());
		}
		return redirecionaConsultaPorFornecedores(item, null);
	}
	
	public String redirecionaConsultaPorFornecedores(EntregaProgramadaGrupoMaterialVO item, EntregasGlobaisAcesso entregasGlobaisAcesso) {
		this.tipoValor = entregasGlobaisAcesso;
		String tipoValorString = null;
		if(this.tipoValor!= null){
			tipoValorString = tipoValor.toString();
		}
		this.gmtCodigo = item.getGmtCodigo();
		this.gmtDescricao = item.getGmtDescricao();
		this.programacaoEntregaGlobalFornecedoresController.setFornecedor(this.filtro.getFornecedor());
		this.programacaoEntregaGlobalFornecedoresController.setDataInicial(this.filtro.getDataInicioEntrega());
		this.programacaoEntregaGlobalFornecedoresController.setDataFinal(this.filtro.getDataFimEntrega());
		this.programacaoEntregaGlobalFornecedoresController.setTipoValor(tipoValorString);
		this.programacaoEntregaGlobalFornecedoresController.setCodigoGrupoMaterial(this.gmtCodigo);
		this.programacaoEntregaGlobalFornecedoresController.setVoltarPara("pesquisaGrupoMaterialEntregaProgramada");
		return PROGRAMACAO_ENTREGA_GLOBAL_FORNECEDORES;
	}
	public String redirecionaConsultaPorFornecedoresSaldoProgramado(EntregaProgramadaGrupoMaterialVO item) {
		if(item.getGmtDescricao()!=null){
			this.programacaoEntregaGlobalFornecedoresController.setDescricaoGrupoMaterial(item.getGmtDescricao());
		}
		return redirecionaConsultaPorFornecedores(item, EntregasGlobaisAcesso.SALDO_PROGRAMADO);
	}
	public String redirecionaConsultaPorFornecedoresValorLiberar(EntregaProgramadaGrupoMaterialVO item) {
		if(item.getGmtDescricao()!=null){
			this.programacaoEntregaGlobalFornecedoresController.setDescricaoGrupoMaterial(item.getGmtDescricao());
		}
		return redirecionaConsultaPorFornecedores(item, EntregasGlobaisAcesso.VALOR_LIBERAR);
	}
	public String redirecionaConsultaPorFornecedoresValorLiberado(EntregaProgramadaGrupoMaterialVO item) {
		if(item.getGmtDescricao()!=null){
			this.programacaoEntregaGlobalFornecedoresController.setDescricaoGrupoMaterial(item.getGmtDescricao());
		}
		return redirecionaConsultaPorFornecedores(item, EntregasGlobaisAcesso.VALOR_LIBERADO);
	}
	public String redirecionaConsultaPorFornecedoresValorAtraso(EntregaProgramadaGrupoMaterialVO item) {
		if(item.getGmtDescricao()!=null){
			this.programacaoEntregaGlobalFornecedoresController.setDescricaoGrupoMaterial(item.getGmtDescricao());
		}
		return redirecionaConsultaPorFornecedores(item, EntregasGlobaisAcesso.VALOR_ATRASO);
	}

	public List<ScoGrupoMaterial> obterGrupos(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro),pesquisarGrupoMaterialCount(parametro));
	}
	
	public Long pesquisarGrupoMaterialCount(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(parametro);
	}
	
	public List<ScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.listarFornecedoresAtivos(parametro, null, 100, null, true),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}

	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.listarFornecedoresAtivosCount(parametro);		
	}

	public FiltroGrupoMaterialEntregaProgramadaVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroGrupoMaterialEntregaProgramadaVO filtro) {
		this.filtro = filtro;
	}

	public List<EntregaProgramadaGrupoMaterialVO> getListagem() {
		return listagem;
	}

	public void setListagem(List<EntregaProgramadaGrupoMaterialVO> listagem) {
		this.listagem = listagem;
	}

	public BigDecimal getValorTotalSaldoProgramado() {
		return valorTotalSaldoProgramado;
	}

	public void setValorTotalSaldoProgramado(BigDecimal valorTotalSaldoProgramado) {
		this.valorTotalSaldoProgramado = valorTotalSaldoProgramado;
	}

	public BigDecimal getValorTotalValorLiberar() {
		return valorTotalValorLiberar;
	}

	public void setValorTotalValorLiberar(BigDecimal valorTotalValorLiberar) {
		this.valorTotalValorLiberar = valorTotalValorLiberar;
	}

	public BigDecimal getValorTotalValorLiberado() {
		return valorTotalValorLiberado;
	}

	public void setValorTotalValorLiberado(BigDecimal valorTotalValorLiberado) {
		this.valorTotalValorLiberado = valorTotalValorLiberado;
	}

	public BigDecimal getValorTotalValorAtraso() {
		return valorTotalValorAtraso;
	}

	public void setValorTotalValorAtraso(BigDecimal valorTotalValorAtraso) {
		this.valorTotalValorAtraso = valorTotalValorAtraso;
	}

	public Date getDataLiberacao() {
		return dataLiberacao;
	}

	public void setDataLiberacao(Date dataLiberacao) {
		this.dataLiberacao = dataLiberacao;
	}

	public String getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(String dataLimite) {
		this.dataLimite = dataLimite;
	}

	public String getDataInicialLote() {
		return dataInicialLote;
	}

	public void setDataInicialLote(String dataInicialLote) {
		this.dataInicialLote = dataInicialLote;
	}

	public String getDataFinalLote() {
		return dataFinalLote;
	}

	public void setDataFinalLote(String dataFinalLote) {
		this.dataFinalLote = dataFinalLote;
	}

	public EntregasGlobaisAcesso getTipoValor() {
		return tipoValor;
	}

	public void setTipoValor(EntregasGlobaisAcesso tipoValor) {
		this.tipoValor = tipoValor;
	}

	public ProgramacaoEntregaGlobalTotalizadorVO getTotalizador() {
		return totalizador;
	}

	public void setTotalizador(ProgramacaoEntregaGlobalTotalizadorVO totalizador) {
		this.totalizador = totalizador;
	}

	public Integer getGmtCodigo() {
		return gmtCodigo;
	}

	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	public String getGmtDescricao() {
		return gmtDescricao;
	}

	public void setGmtDescricao(String gmtDescricao) {
		this.gmtDescricao = gmtDescricao;
	}

}
