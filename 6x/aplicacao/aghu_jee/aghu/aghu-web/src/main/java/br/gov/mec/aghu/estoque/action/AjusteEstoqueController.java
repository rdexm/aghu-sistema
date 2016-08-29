package br.gov.mec.aghu.estoque.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.pesquisa.action.ImprimirDetalhesAjusteEstoqueController;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceMotivoMovimento;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.SceValidadeId;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class AjusteEstoqueController extends ActionController {

	private static final Log LOG = LogFactory.getLog(AjusteEstoqueController.class);

	private static final long serialVersionUID = 5492261471420258066L;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ImprimirDetalhesAjusteEstoqueController imprimirDetalhesAjusteEstoqueController;

	@EJB
	private IComprasFacade comprasFacade;

	private SceMovimentoMaterial movimento;
	private SceValidade validade;
	private SceLoteFornecedor loteFornecedor;
	private List<SceValidade> validades = new ArrayList<SceValidade>();
	private List<SceLoteFornecedor> lotes = new ArrayList<SceLoteFornecedor>();
	private Integer counterValidades;
	private Integer counterLotes;

	/*Inserir*/
	private Integer qtdeValidade;
	private Integer qtdeLote;

	/*Edição de validade*/
	private Long dataValidadeAux;
	private Long dataValidadeAuxLote;
	private Integer qtdeValidadeAuxLote;
	private Integer ealSeqAux;
	private Integer loteFornAux;
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

	private String codigoUnidadeMedida;	
	private String geradoEmFormatado;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public void inicio() throws ApplicationBusinessException {
	 

	 

		movimento = new SceMovimentoMaterial();
		cancelarEdicao();
		cancelarEdicaoLote();
		validades = new ArrayList<SceValidade>();
		lotes = new ArrayList<SceLoteFornecedor>();
		counterValidades = 0;
		counterLotes = 0;
		codigoUnidadeMedida = null;
	
	}
	

	public void adicionar(){

		if((this.dataValidadeAux!=null && this.ealSeqAux!=null)){
			for (SceValidade val : validades) {
				if(val.getId().getEalSeq() != null && val.getId().getEalSeq().equals(ealSeqAux) 
					&& val.getId().getData() != null && ((Long)val.getId().getData().getTime()).equals(this.dataValidadeAux)){
					Integer validadeDisponivelAnterior = 0;
					try{

						validadeDisponivelAnterior = val.getQtdeDisponivel(); 
						val.setQtdeDisponivel(qtdeValidade);

						//atualiza a validade
						this.estoqueFacade.atualizarValidadeMaterial(validade, this.movimento, atualizaCounterValidades(), false);
						apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_VALIDADE");
						
						cancelarEdicao();
						
					} catch (BaseException e) {
						val.setQtdeDisponivel(validadeDisponivelAnterior);
						apresentarExcecaoNegocio(e);
					}
				}
			}
		}else{
			try{
				atualizaCounterValidades();
				counterValidades += qtdeValidade; 
				validade.setQtdeDisponivel(qtdeValidade);
				//insere a validade
				this.estoqueFacade.inserirValidadeMaterial(validade, this.movimento, counterValidades);
				
				this.validades.add(validade);
				
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_VALIDADE");
				
				cancelarEdicao();
				
			} catch (BaseException e) {
				counterValidades = counterValidades - qtdeValidade;
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	
	public void adicionarLote(){

		if(this.loteFornAux!=null){
			
			for (SceLoteFornecedor loteForn : lotes) {
				
				if(loteForn.getSeq() != null && loteForn.getSeq().equals(loteFornAux)){
					Integer loteQtdeAnterior = 0;
					try{

						loteQtdeAnterior = loteForn.getQuantidade(); 
						loteForn.setQuantidade(qtdeLote);

						this.estoqueFacade.atualizarLoteFornecedor(loteForn, this.movimento, this.qtdeValidadeAuxLote, atualizaCounterLotes(), loteQtdeAnterior);

						apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_LOTE");
						
						cancelarEdicaoLote();
						
					} catch (BaseException e) {
						loteForn.setQuantidade(loteQtdeAnterior);
						apresentarExcecaoNegocio(e);
					}
				}
			}
		}else{
			try{
				loteFornecedor.setQuantidade(qtdeLote);
				this.loteFornecedor.setDtValidade(new Date(this.dataValidadeAuxLote));

				atualizaCounterLotes();
				counterLotes += qtdeLote; 
				loteFornecedor.setQuantidade(qtdeLote);

				//insere o lote
				this.estoqueFacade.inserirLoteFornecedor(loteFornecedor, movimento, this.qtdeValidadeAuxLote, counterLotes);

				this.lotes.add(loteFornecedor);

				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_LOTE");
				
				cancelarEdicaoLote();
				
			} catch (BaseException e) {
				counterLotes = counterLotes - qtdeLote;
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void gravar() throws JRException, SystemException, IOException{
		try {

			ScoUnidadeMedida unidadeMedida = this.comprasFacade.obterUnidadeMedidaPorSeq(this.codigoUnidadeMedida);
			this.movimento.setUnidadeMedida(unidadeMedida);
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			this.estoqueBeanFacade.gravarMovimentoMaterial(this.movimento, nomeMicrocomputador);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_AJUSTE");

			//PARAMETRIZAR IMPRESSAO - VERIFICAR SE DEVE IMPRIMIR OU NAO

			AghParametros paramImpAjusteEstoque = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_IMP_AJUSTE_ESTOQUE);	

			if ("S".equalsIgnoreCase(paramImpAjusteEstoque.getVlrTexto())) {
				imprimirDetalhesAjusteEstoqueController.setMmtSeq(this.movimento.getId().getSeq());
				imprimirDetalhesAjusteEstoqueController.directPrint();
			}

			validade = new SceValidade();
			validade.setId(new SceValidadeId());
			counterValidades = 0;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao(){
		validade = new SceValidade();
		validade.setId(new SceValidadeId());
		this.dataValidadeAux = null;
		this.qtdeValidade = null;
		this.ealSeqAux = null;
	}

	public void cancelarEdicaoLote(){
		loteFornecedor = new SceLoteFornecedor();
		this.qtdeLote = null;
		this.loteFornAux = null;
	}
	
	private Integer atualizaCounterValidades(){
		this.counterValidades = 0;
		for (SceValidade val : validades) {
			this.counterValidades += val.getQtdeDisponivel();	
		}
		return this.counterValidades;
	}
	
	private Integer atualizaCounterLotes(){
		this.counterLotes = 0;
		for (SceLoteFornecedor lot : lotes) {
			this.counterLotes += lot.getQuantidade();	
		}
		return this.counterLotes;
	}

	public void verificaDisableData(){
		getVerificaDisableData();
	}
	
	public void verificaDisableSB(){
		getVerificaDisableSB();
	}

	public boolean isItemSelecionado(Object val1){
		
		if(val1 instanceof SceValidade){
			SceValidade val = (SceValidade) val1;
			if((this.dataValidadeAux!=null && this.ealSeqAux!=null && val.getId().getEalSeq() != null && val.getId().getEalSeq().equals(ealSeqAux) 
					&& val.getId().getData() != null && ((Long)val.getId().getData().getTime()).equals(this.dataValidadeAux))
					|| (this.dataValidadeAuxLote!=null && val.getId().getData() != null && ((Long)val.getId().getData().getTime()).equals(this.dataValidadeAuxLote))
					){
					return true;
				}
			}
		return false;
	}

	public boolean isLoteSelecionado(SceLoteFornecedor lotForn1){
		if(lotForn1 instanceof SceLoteFornecedor){
			SceLoteFornecedor lotForn = (SceLoteFornecedor) lotForn1;
			if(this.loteFornAux!=null && this.loteFornAux!=null && lotForn.getSeq() != null && lotForn.getSeq().equals(this.loteFornAux)){
				return true;
			}
		}
		return false;
	}

	public void editarAux(){
		cancelarEdicao();
		editar();
	}
	
	public void editar(){
		for (SceValidade val : validades) {
			if(this.dataValidadeAux!=null && this.ealSeqAux!=null && val.getId().getEalSeq() != null && val.getId().getEalSeq().equals(ealSeqAux) 
				&& val.getId().getData() != null && ((Long)val.getId().getData().getTime()).equals(this.dataValidadeAux)){
				this.validade = val;
				this.qtdeValidade = val.getQtdeDisponivel();
				this.qtdeLote = null;
				
				if(dataValidadeAuxLote != null && qtdeValidadeAuxLote != null){
					dataValidadeAuxLote = null;
					qtdeValidadeAuxLote = null;
				}
			}
		}

		verificaDisableData();
		if(this.dataValidadeAuxLote!=null){
			this.lotes = this.estoqueFacade.pesquisarLoteFornecedorPorMaterialValidade(this.movimento.getMaterial().getCodigo(), new Date(dataValidadeAuxLote));
			this.loteFornecedor = new SceLoteFornecedor();
			this.qtdeLote = null;
		}
	}

	public void editarLote(){
		for (SceLoteFornecedor lote : lotes) {
			if(this.loteFornAux!=null && lote.getSeq() != null && lote.getSeq().equals(loteFornAux)){
				this.loteFornecedor = lote;
				this.qtdeLote = lote.getQuantidade();
			}
		}
		verificaDisableSB();
	}

	// Metodo para pesquisa na suggestion box de tipo de movimento
	public List<SceTipoMovimento> obterSceTipoMovimento(String objPesquisa) throws ApplicationBusinessException {
		return this.estoqueFacade.obterTipoMovimentoPorSeqDescricaoAjustes(objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}

	// Metodo para pesquisa na suggestion box de lotes
	public List<SceLote> listarLotes(String objPesquisa){
		return this.estoqueFacade.listarLotesPorCodigoOuMarcaComercialEMaterial(objPesquisa, this.movimento.getMaterial().getCodigo());
	}

	// Metodo para pesquisa na suggestion box de material
	public List<ScoMaterial> listaEstoqueMaterialPorAlmoxarifado(String paramPesq) throws ApplicationBusinessException {
		Short almoSeq = (movimento.getAlmoxarifado()!=null)?movimento.getAlmoxarifado().getSeq():null;
		return this.comprasFacade.pesquisaMateriaisPorParamAlmox(almoSeq, paramPesq);
	}

	/**
	 * atualiza a unidade de medida do material
	 */
	public void atualizaUnidade(){
		if(this.movimento==null || this.movimento.getMaterial()==null){
			this.setCodigoUnidadeMedida(null);
		}else{
			if(this.movimento.getMaterial() != null && this.movimento.getMaterial().getUnidadeMedida() != null){
				this.setCodigoUnidadeMedida(this.movimento.getMaterial().getUnidadeMedida().getCodigo());
				this.movimento.setUnidadeMedida(this.movimento.getMaterial().getUnidadeMedida());
			}else{
				this.setCodigoUnidadeMedida(null);
				this.movimento.setUnidadeMedida(null);
			}
		}
	}

	/**
	 * atualiza a suggestion box de motivo
	 */
	public void atualizaMotivo(){
		if(this.movimento != null && this.movimento.getTipoMovimento() == null){
			this.movimento.setMotivoMovimento(null);
		}
	}

	// Metodo para pesquisa na suggestion box de fornecedor
	public List<ScoFornecedor> obterFornecedor(String param){
		Short almox = null;
		Integer materialCodigo = null;

		if(this.movimento.getAlmoxarifado()!=null){
			almox = this.movimento.getAlmoxarifado().getSeq();
		}

		if(this.movimento.getMaterial()!=null){
			materialCodigo = this.movimento.getMaterial().getCodigo();
		}

		return this.comprasFacade.obterFornecedorPorSeqDescricaoEAlmoxarifadoMaterial(param, almox, materialCodigo);
	}
	
	// Metodo para pesquisa na suggestion box de tipo de movimento
	public List<SceMotivoMovimento> obterMotivoMovimento(String objPesquisa) {
		
		if(this.movimento.getTipoMovimento()==null){
			apresentarMsgNegocio(Severity.ERROR,"TITLE_TIPO_MOVIMENTO");
			return new ArrayList<SceMotivoMovimento>();
		}else{
			return this.estoqueFacade.obterMotivoMovimentoPorSeqDescricaoETMV(
					this.movimento.getTipoMovimento().getId().getSeq(), 
					this.movimento.getTipoMovimento().getId().getComplemento(), 
					objPesquisa);
		}
	}

	public String getGeradoEmFormatado() throws ApplicationBusinessException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if(this.movimento == null || this.movimento.getDtGeracao()==null){
			geradoEmFormatado = null;
		}else{
			RapPessoasFisicas pessoaFisica = registroColaboradorFacade.obterPessoaFisica(this.movimento.getServidor().getPessoaFisica().getCodigo());
			geradoEmFormatado = sdf.format(this.movimento.getDtGeracao()) + " - " + pessoaFisica.getNome();
		}

		return geradoEmFormatado;
	}
	
	public void limparMaterial() {
		this.getMovimento().setMaterial(null);
	}

	public SceMovimentoMaterial getMovimento() {
		return movimento;
	}

	public void setMovimento(SceMovimentoMaterial movimento) {
		this.movimento = movimento;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

	public void setGeradoEmFormatado(String geradoEmFormatado) {
		this.geradoEmFormatado = geradoEmFormatado;
	}

	public SceValidade getValidade() {
		return validade;
	}

	public void setValidade(SceValidade validade) {
		this.validade = validade;
	}

	public List<SceValidade> getValidades() {
		return validades;
	}

	public void setValidades(List<SceValidade> validades) {
		this.validades = validades;
	}


	public Long getDataValidadeAux() {
		return dataValidadeAux;
	}


	public void setDataValidadeAux(Long dataValidadeAux) {
		this.dataValidadeAux = dataValidadeAux;
	}


	public Integer getEalSeqAux() {
		return ealSeqAux;
	}


	public void setEalSeqAux(Integer ealSeqAux) {
		this.ealSeqAux = ealSeqAux;
	}


	public Integer getCounterValidades() {
		return counterValidades;
	}


	public void setCounterValidades(Integer counterValidades) {
		this.counterValidades = counterValidades;
	}

	public Boolean getVerificaDisableData() {
		return this.dataValidadeAux!=null && this.ealSeqAux!=null;
	}

	public Integer getQtdeValidade() {
		return qtdeValidade;
	}

	public void setQtdeValidade(Integer qtdeValidade) {
		this.qtdeValidade = qtdeValidade;
	}

	public SceLoteFornecedor getLoteFornecedor() {
		return loteFornecedor;
	}

	public void setLoteFornecedor(SceLoteFornecedor loteFornecedor) {
		this.loteFornecedor = loteFornecedor;
	}

	public List<SceLoteFornecedor> getLotes() {
		return lotes;
	}

	public void setLotes(List<SceLoteFornecedor> lotes) {
		this.lotes = lotes;
	}

	public Integer getQtdeLote() {
		return qtdeLote;
	}

	public void setQtdeLote(Integer qtdeLote) {
		this.qtdeLote = qtdeLote;
	}

	public Boolean getVerificaDisableSB() {
		return this.loteFornecedor.getSeq()!=null;
	}

	public Integer getLoteFornAux() {
		return loteFornAux;
	}

	public void setLoteFornAux(Integer loteFornAux) {
		this.loteFornAux = loteFornAux;
	}

	public Long getDataValidadeAuxLote() {
		return dataValidadeAuxLote;
	}

	public void setDataValidadeAuxLote(Long dataValidadeAuxLote) {
		this.dataValidadeAuxLote = dataValidadeAuxLote;
	}

	public Integer getQtdeValidadeAuxLote() {
		return qtdeValidadeAuxLote;
	}

	public void setQtdeValidadeAuxLote(Integer qtdeValidadeAuxLote) {
		this.qtdeValidadeAuxLote = qtdeValidadeAuxLote;
	}
}