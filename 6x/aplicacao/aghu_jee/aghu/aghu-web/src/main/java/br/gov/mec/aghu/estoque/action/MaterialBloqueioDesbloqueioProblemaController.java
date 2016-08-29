package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;




public class MaterialBloqueioDesbloqueioProblemaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(MaterialBloqueioDesbloqueioProblemaController.class);

	private static final long serialVersionUID = 5492261471420258066L;
	
	private static final String VOLTAR_PESQUISA = "estoque-pesquisarMaterialBloqueioDesbloqueioProblema";
	private static final String MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA = "materialBloqueioDesbloqueioProblema";

	@EJB
	private IEstoqueFacade estoqueFacade;
		
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private SceHistoricoProblemaMaterial historico;

	private SceEstoqueAlmoxarifado estAlm;
	
	private SceTipoMovimento tipoMovimento;
	private SceAlmoxarifado almoxarifado;
	private ScoMaterial material;
	private ScoFornecedor fornecedor;
	private SceMotivoProblema motivoProblema;
	private ScoFornecedor fornecedorEntrega;

	private SceValidade validade;
	private Integer seqHistorico;
	private String acaoBloDesb;
	private Date dtValidade;
	private Integer qtdeDisponivel;
	private Long dtValidadeNumerica; 
	private SceMovimentoMaterial movimento;
	
	//private Boolean acaoDesbloquear = false;
	private List<SceValidade> listaValidade = new ArrayList<SceValidade>();
	private List<SceValidade> listaValidadeInicial = new ArrayList<SceValidade>();
	

	/*Inserir*/
	private Integer qtdeAcaoBloqueioDesbloqueio;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public void iniciarEdicao() throws ApplicationBusinessException {
	 		if(seqHistorico!=null){
			historico = estoqueFacade.obterHistoricosProblemaPorSeq(seqHistorico);

			if(historico != null){
				estAlm = historico.getSceEstqAlmox();
				
				this.almoxarifado = historico.getSceEstqAlmox().getAlmoxarifado();
				this.material = historico.getSceEstqAlmox().getMaterial();
				this.fornecedor = historico.getSceEstqAlmox().getFornecedor();
				
				this.motivoProblema = historico.getMotivoProblema();
				this.fornecedorEntrega = historico.getFornecedor();

				if(getMostraValidades()){
					listaValidade = estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(estAlm.getSeq());
					listaValidadeInicial = listaValidade;
				}else{
					listaValidadeInicial = estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(estAlm.getSeq());
				}
			}
		}
	}
	
	public void iniciar(){
		historico = new SceHistoricoProblemaMaterial();
		historico.setQtdeDesbloqueada(0);
		historico.setQtdeDf(0);
		historico.setQtdeProblema(0);
		estAlm = new SceEstoqueAlmoxarifado();
		tipoMovimento = null;
		almoxarifado = null;
		material = null;
		fornecedor = null;
		motivoProblema = null;
		fornecedorEntrega = null;
		qtdeAcaoBloqueioDesbloqueio = null;
	}
	

	public String gravar(){
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {

			this.estAlm.setAlmoxarifado(almoxarifado);
			this.estAlm.setMaterial(material);
			this.estAlm.setFornecedor(fornecedor);
		
			historico.setSceEstqAlmox(this.estAlm);
			
			historico.setFornecedor(fornecedorEntrega);
			historico.setMotivoProblema(motivoProblema);
			
			this.estoqueFacade.bloqueioDesbloqueioQuantidadesProblema(this.historico, this.tipoMovimento, this.qtdeAcaoBloqueioDesbloqueio, nomeMicrocomputador);

			Short seqDesb1 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TMV_DISP_BLOQ_PROB).getVlrNumerico().shortValue();
			Short seqDesb2 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TMV_BLOQ_BLOQ_PROB).getVlrNumerico().shortValue();

			if(tipoMovimento.getId().getSeq().equals(seqDesb1) || tipoMovimento.getId().getSeq().equals(seqDesb2)){
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_BLOQUEIO_QTDE_MATERIAL");
			}else{
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_DESBLOQUEIO_QTDE_MATERIAL");
			}
			this.movimento = new SceMovimentoMaterial();
			this.movimento.setQuantidade(this.qtdeAcaoBloqueioDesbloqueio);

			if(getMostraValidades()){
				SceEstoqueAlmoxarifado estoqueAlmoxarifado = this.estoqueFacade.obterEstoqueAlmoxarifadoOrigem(this.almoxarifado.getSeq(), this.material.getCodigo(), this.fornecedor.getNumero());
				this.listaValidade = estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(estoqueAlmoxarifado.getSeq());
				this.validade = new SceValidade();
			} else{
				qtdeAcaoBloqueioDesbloqueio = Integer.valueOf(0);
			}
			

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA;
	}
	
	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}
	
	/**
	 * Limpa a suggestion box de Material e Fornecedor após a seleção ou remoção do almoxarifado
	 */
	public void limparMaterialFornecedor(){
		this.material = null;
		this.limparFornecedor();
	}

	// Metodo para pesquisa na suggestion box de material
	public List<ScoMaterial> listaEstoqueMaterialPorAlmoxarifado(String paramPesq) throws ApplicationBusinessException {

		if(this.almoxarifado == null){
			apresentarMsgNegocio("sbAlmoxarifado", Severity.ERROR, "CAMPO_OBRIGATORIO", "Almoxarifado");
			return null;
		}

		return this.comprasFacade.pesquisaMateriaisPorParamAlmox(this.almoxarifado.getSeq(), paramPesq);
	}
	
	/**
	 * Limpa a suggestion box de Fornecedor após a seleção ou remoção do almoxarifado
	 */
	public void limparFornecedor(){
		this.fornecedor = null;
	}

	// Metodo para pesquisa na suggestion box de motivo de problema
	public List<SceMotivoProblema> pesquisaMotivosProblemasPorSeqDescricao(String paramPesq) throws ApplicationBusinessException {
		return this.estoqueFacade.pesquisaMotivosProblemasPorSeqDescricao(paramPesq,null);
	}
	
	// Metodo para pesquisa na suggestion box de tipo de movimento
	public List<SceTipoMovimento> obterSceTipoMovimento(String objPesquisa) throws ApplicationBusinessException {
		boolean mostrarTodos = this.historico != null && this.historico.getSeq()!=null;
		return this.estoqueFacade.obterTipoMovimentoPorSeqDescricaoBloqueioDesbloqueioComProblema(objPesquisa, mostrarTodos);
	}
	
	// Metodo para pesquisa na suggestion box de fornecedor
	public List<ScoFornecedor> obterFornecedor(String param){
		
		if(this.almoxarifado == null ){
			apresentarMsgNegocio("sbAlmoxarifado", Severity.ERROR, "CAMPO_OBRIGATORIO", "Almoxarifado");
			return null;
		}

		if(this.material == null){
			apresentarMsgNegocio("sbMaterial", Severity.ERROR, "CAMPO_OBRIGATORIO", "Material");
			return null;
		}

		return this.comprasFacade.obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(param, this.almoxarifado.getSeq(), this.material.getCodigo());
	}
	
	// Metodo para pesquisa na suggestion box de fornecedor
	public List<ScoFornecedor> obterFornecedorEntrega(String param){
		return this.comprasFacade.obterFornecedor(param);
	}
	
	
	public void  adicionar(){
		try{

			final boolean isNovo = this.validade == null || this.validade.getId() == null; 

			if(this.movimento != null){
				this.movimento.setAlmoxarifado(this.estAlm.getAlmoxarifado());
				this.movimento.setMaterial(this.estAlm.getMaterial());
				this.movimento.setFornecedor(this.estAlm.getFornecedor());
				this.movimento.setQuantidade(this.qtdeAcaoBloqueioDesbloqueio);
			}

			if(isNovo){
				
				this.validade = new SceValidade();
				
				this.validade.setId(new SceValidadeId(this.estAlm.getSeq(), this.dtValidade));
				this.validade.setQtdeDisponivel(this.qtdeDisponivel);
				this.estoqueFacade.inserirValidadeMaterial(this.validade, this.movimento);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INSERT_CTRL_VAL_MATERIAL");

			}else{
				this.estoqueFacade.atualizarValidadeMaterial(this.validade, this.movimento, this.qtdeDisponivel, true);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_UPDATE_CTRL_VAL_MATERIAL");
			}

			this.validade = new SceValidade();
			this.dtValidade = null;
			this.qtdeDisponivel = null;
			this.dtValidadeNumerica  =null;
			
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = this.estoqueFacade.obterEstoqueAlmoxarifadoOrigem(this.almoxarifado.getSeq(), this.material.getCodigo(), this.fornecedor.getNumero());
			this.listaValidade = estoqueFacade.listarValidadesPorEstoqueAlmoxarifado(estoqueAlmoxarifado.getSeq());
			

		} catch (BaseException e) {
			this.validade = new SceValidade();
			apresentarExcecaoNegocio(e);
		}
	}
	

	public void cancelarEdicao(){
		this.validade = new SceValidade();
		this.qtdeAcaoBloqueioDesbloqueio = null;
		this.dtValidadeNumerica = null;
		this.dtValidade = null;
		this.qtdeDisponivel = null;
		this.estAlm = new SceEstoqueAlmoxarifado();
		this.motivoProblema = null;
		this.fornecedorEntrega = null;
	}

	public String voltar(){
		try{
				
			estoqueFacade.verificaBotaoVoltarBloqueioDesbloqueioProblema(this.historico, this.tipoMovimento, this.listaValidadeInicial, getMostraValidades());

			this.validade = new SceValidade();
			this.dtValidade = null;
			this.qtdeDisponivel = null;
			this.listaValidade = null;
			this.dtValidadeNumerica = null;
			this.historico = null;
			this.estAlm = new SceEstoqueAlmoxarifado();
			this.acaoBloDesb = null;
			this.iniciar();

			return VOLTAR_PESQUISA;
			

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return VOLTAR_PESQUISA;
		}
	}

	public boolean isItemSelecionado(SceValidade validade){
		if(this.dtValidadeNumerica!=null && validade.getId() != null
				&& this.dtValidadeNumerica.equals(validade.getId().getDataLong())){
				return true;
			}
		return false;
	}

	public void editar(Long dtValidadeNumerica){
		this.dtValidadeNumerica = dtValidadeNumerica;

		for (SceValidade valid : listaValidade) {
			if(this.dtValidadeNumerica != null && this.dtValidadeNumerica.equals(valid.getId().getDataLong())){
				this.validade = valid;
				this.dtValidade = valid.getId().getData();
			}
		}
	}
	
	public Integer getQtdeAcaoBloqueioDesbloqueio() {
		return qtdeAcaoBloqueioDesbloqueio;
	}

	public void setQtdeAcaoBloqueioDesbloqueio(Integer qtdeAcaoBloqueioDesbloqueio) {
		this.qtdeAcaoBloqueioDesbloqueio = qtdeAcaoBloqueioDesbloqueio;
	}

	public String getAcaoBloDesb() {
		return acaoBloDesb;
	}

	public void setAcaoBloDesb(String acaoBloDesb) {
		this.acaoBloDesb = acaoBloDesb;
	}

	public Boolean getAcaoDesbloquear() {
		if(tipoMovimento != null && 
				tipoMovimento.getIndQtdeDisponivel().equals(DominioIndOperacaoBasica.CR)	
		){
			return tipoMovimento.getIndOperacaoBasica().equals(DominioIndOperacaoBasica.CR);
		}else{
			return false;
		}
	}

	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}

	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public SceHistoricoProblemaMaterial getHistorico() {
		return historico;
	}

	public void setHistorico(SceHistoricoProblemaMaterial historico) {
		this.historico = historico;
	}

	public Integer getSeqHistorico() {
		return seqHistorico;
	}

	public void setSeqHistorico(Integer seqHistorico) {
		this.seqHistorico = seqHistorico;
	}

	public ScoFornecedor getFornecedorEntrega() {
		return fornecedorEntrega;
	}

	public void setFornecedorEntrega(ScoFornecedor fornecedorEntrega) {
		this.fornecedorEntrega = fornecedorEntrega;
	}

	public SceMotivoProblema getMotivoProblema() {
		return motivoProblema;
	}

	public void setMotivoProblema(SceMotivoProblema motivoProblema) {
		this.motivoProblema = motivoProblema;
	}

	public Boolean getMostraValidades() {
		if(tipoMovimento != null 
				&& tipoMovimento.getIndQtdeDisponivelValid().equals(DominioIndOperacaoBasica.CR)
				&& (tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.CR) || tipoMovimento.getIndQtdeEntradaValid().equals(DominioIndOperacaoBasica.DB))
				&& (tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.CR) || tipoMovimento.getIndQtdeConsumoValid().equals(DominioIndOperacaoBasica.DB))){
			return true;
		}else{
			return false;
		}
		//return true;
	}

	public SceEstoqueAlmoxarifado getEstAlm() {
		return estAlm;
	}

	public void setEstAlm(SceEstoqueAlmoxarifado estAlm) {
		this.estAlm = estAlm;
	}

	public SceValidade getValidade() {
		return validade;
	}

	public void setValidade(SceValidade validade) {
		this.validade = validade;
	}

	public List<SceValidade> getListaValidade() {
		return listaValidade;
	}

	public void setListaValidade(List<SceValidade> listaValidade) {
		this.listaValidade = listaValidade;
	}

	public Date getDtValidade() {
		return dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}

	public SceMovimentoMaterial getMovimento() {
		return movimento;
	}

	public void setMovimento(SceMovimentoMaterial movimento) {
		this.movimento = movimento;
	}

	public Long getDtValidadeNumerica() {
		return dtValidadeNumerica;
	}

	public void setDtValidadeNumerica(Long dtValidadeNumerica) {
		this.dtValidadeNumerica = dtValidadeNumerica;
	}

	public Integer getQtdeDisponivel() {
		return qtdeDisponivel;
	}

	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public List<SceValidade> getListaValidadeInicial() {
		return listaValidadeInicial;
	}

	public void setListaValidadeInicial(List<SceValidade> listaValidadeInicial) {
		this.listaValidadeInicial = listaValidadeInicial;
	}

	
	
	
}