package br.gov.mec.aghu.estoque.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class MaterialBloqueioDesbloqueioController extends ActionController {

	private static final long serialVersionUID = 5492261471420258066L;
	
	private static final Log LOG = LogFactory.getLog(MaterialBloqueioDesbloqueioController.class);
	
	private static final String VOLTAR_PESQUISA = "estoque-pesquisarMaterialBloqueioDesbloqueio";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	/**
	 * Variavel de retorno
	 */
	private String voltarPara;

	/**
	 * Estoque Almoxarifado
	 */
	private SceEstoqueAlmoxarifado estalm;

	/**
	 * Lote do Documento
	 */
	private SceLoteDocumento loteDoc;
	
	private SceLoteDocumento loteDocSelecionado;

	/**
	 * Nota de Recebimento
	 */
	private SceItemNotaRecebimento itemNotaRecebimento;

	/**
	 * Outras Entradas
	 */
	private SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao;

	/**
	 * Marca Comercial obtida via nota
	 */
	private ScoMarcaComercial marcaComercial;

	private Integer estAlmoxSeq;
	private Integer loteDocumentoSeq;
	private String acaoBloDesb;

	/**
	 * Tipo de Movimento
	 */
	private SceTipoMovimento tipoMovimento;

	private List<SceLoteDocumento> listaDocs = new ArrayList<SceLoteDocumento>();

	/*Inserir*/
	private Integer qtdeAcaoBloqueioDesbloqueio;

	private Boolean gravouEstoque;

	private Integer qtdeEtiquetas;
	
	private boolean reducaoValidade;
	
	private Integer quantidade;
	
	private String lotCodigo;
	
	private String serie;
	
	private String tamanho;
	
	private Date dtValidade;
	
	private boolean isNovo;

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public enum MaterialBloqueioDesbloqueioControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DATA_VALIDADE_MENOR_DATA_ATUAL;
	}

	public void inicio() throws ApplicationBusinessException {
			if(estAlmoxSeq!=null){
			estalm = estoqueFacade.buscaSceEstoqueAlmoxarifadoPorId(estAlmoxSeq);
			marcaComercial = new ScoMarcaComercial();
			listaDocs = estoqueFacade.pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(estalm.getSeq(), estalm.getMaterial().getCodigo());
			gravouEstoque = false;
			cancelarEdicao();
			this.isNovo = true;
		}
	
	}
	
	
	private Boolean validarGravar(){
		if (qtdeAcaoBloqueioDesbloqueio == null) {
			if (getAcaoDesbloquear()) {
				apresentarMsgNegocio("qtdeBloquearDesbloquear", Severity.ERROR, "CAMPO_OBRIGATORIO", "Quantidade a Desbloquear");
			} else {
				apresentarMsgNegocio("qtdeBloquearDesbloquear", Severity.ERROR, "CAMPO_OBRIGATORIO", "Quantidade a Bloquear");
			}
			return true;
		}
		if (getAcaoDesbloquear()) {
			if (estalm.getQtdeBloqueada() != null && estalm.getQtdeBloqueada() <= 0) {
				apresentarMsgNegocio(Severity.ERROR,"SCE_00706b");
				return true;
			}
		}
		return false;
	}

	/**
	 * Grava o Estoque Almoxarifado
	 * @return
	 */
	public void gravar() {
		if (!validarGravar()) {
			try {
				if (getAcaoDesbloquear()) {
					this.estoqueFacade.desbloqueioQuantidadesEstoqueAlmox(estalm, qtdeAcaoBloqueioDesbloqueio, getNomeMicrocomputador());
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_DESBLOQUEIO_QTDE_MATERIAL");

				} else {
					this.estoqueFacade.bloqueioQuantidadesEstoqueAlmox(estalm, qtdeAcaoBloqueioDesbloqueio, getNomeMicrocomputador());
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_BLOQUEIO_QTDE_MATERIAL");
				}
				gravouEstoque = true;
				
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				gravouEstoque = false;
			}
		}
	}

	private String getNomeMicrocomputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		return nomeMicrocomputador;
	}

	/**
	 * Grava o Lote Documento
	 */
	public void adicionar() {

		if (!gravouEstoque) {
			if (validarGravar() != null) {
				adicionarItens();
			}
		} else {
			adicionarItens();
		}
	}

	public void adicionarItens() {
		Integer qtdeDisponivel = estalm.getQtdeDisponivel();
		Integer qtdeBloq = estalm.getQtdeBloqueada();
		
		try {
			if(loteDoc==null){
				loteDoc = new SceLoteDocumento();
			}

			Short tmvSeq = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR).getVlrNumerico().shortValue();
			SceTipoMovimento tipoMovimento = this.estoqueFacade.obterSceTipoMovimentosAtivoPorSeq(tmvSeq);

			loteDoc.setTipoMovimento(tipoMovimento);
			loteDoc.setInrEalSeq(this.estalm.getSeq());
			loteDoc.setLotMatCodigo(estalm.getMaterial().getCodigo());
			loteDoc.setEntradaSaidaSemLicitacao(this.entradaSaidaSemLicitacao);
			loteDoc.setQuantidade(quantidade);
			loteDoc.setLotCodigo(lotCodigo);
			loteDoc.setSerie(serie);
			loteDoc.setTamanho(tamanho);
			loteDoc.setDtValidade(dtValidade);
			loteDoc.setItemNotaRecebimento(itemNotaRecebimento);
			
			if (this.isNovo) {
				//this.estoqueFacade.persistirSceLoteDocumento(loteDoc);
				this.estoqueFacade.bloqueioDesbloqueioPersistirLote(estalm, qtdeAcaoBloqueioDesbloqueio, getNomeMicrocomputador(), !getAcaoDesbloquear(), loteDoc);
				if(getAcaoDesbloquear()){					
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_DESBLOQUEIO_QTDE_MATERIAL");
				} else {					
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_BLOQUEIO_QTDE_MATERIAL");
				}
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INSERT_CTRL_VAL_MATERIAL");

			} else {
				this.estoqueFacade.atualizarSceLoteDocumento(loteDoc);				
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_UPDATE_CTRL_VAL_MATERIAL");
			}

			loteDoc = new SceLoteDocumento();
			loteDocumentoSeq = null;
			this.itemNotaRecebimento = null;
			this.entradaSaidaSemLicitacao = null;
			this.marcaComercial = null;
			this.quantidade=null;
			this.lotCodigo=null;
			this.serie=null;
			this.tamanho=null;
			this.dtValidade=null;
			this.isNovo = true;
			listaDocs = estoqueFacade.pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(estalm.getSeq(), estalm.getMaterial().getCodigo());

		} catch (BaseException e) {
			estalm.setQtdeDisponivel(qtdeDisponivel);
			estalm.setQtdeBloqueada(qtdeBloq);
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelarEdicao(){
		this.loteDoc = new SceLoteDocumento();
		this.qtdeAcaoBloqueioDesbloqueio = null;
		this.loteDocumentoSeq = null;
		this.entradaSaidaSemLicitacao = null;
		this.quantidade = null;
		this.itemNotaRecebimento = null;
		this.lotCodigo=null;
		this.serie=null;
		this.tamanho=null;
		this.dtValidade=null;
		this.isNovo = true;
	}

	public String cancelar() {
		try{
			if(this.voltarPara !=null){
				estAlmoxSeq = null;
				return voltarPara;
			}else{
				if(estalm != null){
					this.estoqueFacade.verificaBotaoVoltarBloqueioDesbloqueio(estalm.getSeq());
				}
				loteDoc = new SceLoteDocumento();
				loteDocumentoSeq = null;
				listaDocs = null;
				estAlmoxSeq = null;
				loteDocumentoSeq = null;
				acaoBloDesb = null;
				return VOLTAR_PESQUISA;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return VOLTAR_PESQUISA;
		}
	}

	public boolean isItemSelecionado(SceLoteDocumento lotDoc){
		if(this.loteDocumentoSeq!=null 
				&& lotDoc.getSeq() != null
				&& Objects.equals(lotDoc.getSeq(), loteDocumentoSeq)){
			return true;
		}
		return false;
	}

	public void editar(Integer loteDocumentoSeq){
		this.loteDocumentoSeq = loteDocumentoSeq;
		this.isNovo = false;
		for (SceLoteDocumento loteDoc : listaDocs) {
			if(this.loteDocumentoSeq != null && loteDoc.getSeq().equals(this.loteDocumentoSeq)){
				this.loteDoc = loteDoc;
				this.lotCodigo = loteDoc.getLotCodigo();
				this.serie = loteDoc.getSerie();
				this.tamanho = loteDoc.getTamanho();
				this.dtValidade = loteDoc.getDtValidade();
				this.itemNotaRecebimento = this.loteDoc.getItemNotaRecebimento();
				this.quantidade = this.loteDoc.getQuantidade();
				this.entradaSaidaSemLicitacao = this.loteDoc.getEntradaSaidaSemLicitacao();
				if(itemNotaRecebimento != null){
					marcaComercial = itemNotaRecebimento.getItemAutorizacaoForn().getMarcaComercial();
				}else if(entradaSaidaSemLicitacao != null){
					marcaComercial = entradaSaidaSemLicitacao.getScoMarcaComercial();
				}
			}
		}
	}
	
	// Metodo para pesquisa na suggestion box de lotes
	public List<SceLote> listarLotes(String objPesquisa){
		return this.estoqueFacade.listarLotesPorCodigoOuMarcaComercialEMaterial(objPesquisa, this.estalm.getMaterial().getCodigo());
	}

	public List<SceItemNotaRecebimento> pesquisaNotaRecebimento(String objPesquisa){
		return this.estoqueFacade.pesquisaItensNotaRecebimentoUltimos60DiasPormaterial(this.estalm.getMaterial().getCodigo(), objPesquisa);
	}

	public List<SceEntradaSaidaSemLicitacao> pesquisaOutrasEntradas(String objPesquisa){
		return this.estoqueFacade.pesquisarEntradaSaidaPorMaterial(this.estalm.getMaterial().getCodigo(), objPesquisa);
	}

	public SceEstoqueAlmoxarifado getEstalm() {
		return estalm;
	}

	public void setEstalm(SceEstoqueAlmoxarifado estalm) {
		this.estalm = estalm;
	}

	public Integer getEstAlmoxSeq() {
		return estAlmoxSeq;
	}

	public void setEstAlmoxSeq(Integer estAlmoxSeq) {
		this.estAlmoxSeq = estAlmoxSeq;
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
		if(acaoBloDesb!=null){
			return acaoBloDesb.equalsIgnoreCase("DESB");
		}else{
			return false;
		}
	}

	public SceLoteDocumento getLoteDoc() {
		return loteDoc;
	}


	public void setLoteDoc(SceLoteDocumento loteDoc) {
		this.loteDoc = loteDoc;
	}


	public Integer getLoteDocumentoSeq() {
		return loteDocumentoSeq;
	}


	public void setLoteDocumentoSeq(Integer loteDocumentoSeq) {
		this.loteDocumentoSeq = loteDocumentoSeq;
	}


	public List<SceLoteDocumento> getListaDocs() {
		return listaDocs;
	}


	public void setListaDocs(List<SceLoteDocumento> listaDocs) {
		this.listaDocs = listaDocs;
	}


	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}


	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}


	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public SceItemNotaRecebimento getItemNotaRecebimento() {
		return itemNotaRecebimento;
	}

	public void setItemNotaRecebimento(SceItemNotaRecebimento newItemNotaRecebimento) {

		if (newItemNotaRecebimento != null) {

			loteDoc.setItemNotaRecebimento(newItemNotaRecebimento);
			marcaComercial = newItemNotaRecebimento.getItemAutorizacaoForn().getMarcaComercial();
			this.itemNotaRecebimento = newItemNotaRecebimento;

		} else {

			loteDoc.setItemNotaRecebimento(null);
			marcaComercial = null;
			this.itemNotaRecebimento = null;

		}

	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}


	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}


	public SceEntradaSaidaSemLicitacao getEntradaSaidaSemLicitacao() {
		return entradaSaidaSemLicitacao;
	}


	public void setEntradaSaidaSemLicitacao(SceEntradaSaidaSemLicitacao newEntradaSaidaSemLicitacao) {

		if (newEntradaSaidaSemLicitacao != null) {

			loteDoc.setEntradaSaidaSemLicitacao(newEntradaSaidaSemLicitacao);
			marcaComercial = newEntradaSaidaSemLicitacao.getScoMarcaComercial();
			this.entradaSaidaSemLicitacao = newEntradaSaidaSemLicitacao;

		} else {

			loteDoc.setEntradaSaidaSemLicitacao(null);
			marcaComercial = null;
			this.entradaSaidaSemLicitacao = null;

		}

	}

	public Boolean getGravouEstoque() {
		return gravouEstoque;
	}

	public void setGravouEstoque(Boolean gravouEstoque) {
		this.gravouEstoque = gravouEstoque;
	}

	public Integer getQtdeEtiquetas() {
		return qtdeEtiquetas;
	}

	public void setQtdeEtiquetas(Integer qtdeEtiquetas) {
		this.qtdeEtiquetas = qtdeEtiquetas;
	}


	public String imprimirEtiquetasUnitarizacao(){

		try {
			if(loteDoc!=null){
				RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
				String nomeMicrocomputador = getNomeMicrocomputador();

				SceLoteDocImpressao sceLoteDocImpressao = this.salvarSceLoteDocImpressaoAntesDeImprimir(loteDoc, servidorLogado);

				this.imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(sceLoteDocImpressao, nomeMicrocomputador, qtdeEtiquetas);
				qtdeEtiquetas = null;
				reducaoValidade = false;	
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA_UNITARIZACAO");
				
			}
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			qtdeEtiquetas = null;
			reducaoValidade = false;	
			return null;
		}

		return null;
	}

	public void imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(
			SceLoteDocImpressao loteDocImpressao, String nomeMicrocomputador,
			Integer qtdeEtiquetas) throws BaseException {

		AghMicrocomputador micro = this.administracaoFacade
				.obterAghMicroComputadorPorNomeOuIP(
						nomeMicrocomputador,
						DominioCaracteristicaMicrocomputador.POSSUI_IMPRESSORA_UNITARIZADORA);

		if (micro == null) {
			AghUnidadesFuncionais unidadeFuncional = this.farmaciaFacade
					.getUnidadeFuncionalAssociada(nomeMicrocomputador);
			String etiquetas = this.farmaciaFacade
					.gerarEtiquetas(loteDocImpressao);
			this.sistemaImpressao.imprimir(etiquetas, unidadeFuncional,
					TipoDocumentoImpressao.ETIQUETA_BARRAS_MEDICAMENTOS);
		} else {
			this.estoqueFacade.gerarInterfaceamentoUnitarizacao(
					loteDocImpressao, nomeMicrocomputador, qtdeEtiquetas);
		}
	}	
	
	private SceLoteDocImpressao salvarSceLoteDocImpressaoAntesDeImprimir(SceLoteDocumento loteDoc, RapServidores servidorLogado) throws IllegalStateException, BaseException{
		
		SceLoteDocImpressao loteDocImpressao = new SceLoteDocImpressao();
		loteDocImpressao.setNroNfEntrada(this.estoqueFacade.getObterNotaFiscal(loteDoc));
		loteDocImpressao.setInrNrsSeq(loteDoc.getInrNrsSeq());
		loteDocImpressao.setMaterial(this.loteDoc.getItemNotaRecebimento().getMaterial());
		loteDocImpressao.setLoteCodigo(loteDoc.getLotCodigo());
		loteDocImpressao.setDtValidade(loteDoc.getDtValidade());
		loteDocImpressao.setQtde(qtdeEtiquetas);
		loteDocImpressao.setLoteDocumento(loteDoc);
		
		ScoMarcaComercial marcaComercial = comprasFacade.obterMarcaComercialPorCodigo(loteDoc.getLotMcmCodigo());
		loteDocImpressao.setMarcaComercial(marcaComercial);

		String nomeMicrocomputador = getNomeMicrocomputador();
		
		this.estoqueFacade.efetuarInclusao(loteDocImpressao, nomeMicrocomputador, new Date(), this.reducaoValidade);
		
		return loteDocImpressao;
	}
	
	public boolean habilitarImpressao(SceLoteDocumento loteDoc){
		
		boolean retorno = false;
		try {
			
			if(loteDoc!=null && loteDoc.getItemNotaRecebimento()!=null && loteDoc.getItemNotaRecebimento().getMaterial().getGrupoMaterial()!=null){
				
				BigDecimal paramMatMedic = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC).getVlrNumerico();
				BigDecimal paramMatOrtProt = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT).getVlrNumerico();
				
				if(loteDoc.getItemNotaRecebimento().getMaterial().getGrupoMaterial().getCodigo().equals(paramMatMedic.intValue()) || 
						loteDoc.getItemNotaRecebimento().getMaterial().getGrupoMaterial().getCodigo().equals(paramMatOrtProt.intValue())){
					retorno = true;
				}
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			retorno = false;
		}
		
		return retorno;
	}
	
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public boolean isReducaoValidade() {
		return reducaoValidade;
	}

	public void setReducaoValidade(boolean reducaoValidade,SceLoteDocumento loteDoc) {
		this.reducaoValidade = reducaoValidade;
		this.loteDoc = loteDoc;
	}


	public Integer getQuantidade() {
		return quantidade;
	}


	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	public String getLotCodigo() {
		return lotCodigo;
	}


	public void setLotCodigo(String lotCodigo) {
		this.lotCodigo = lotCodigo;
	}


	public String getSerie() {
		return serie;
	}


	public void setSerie(String serie) {
		this.serie = serie;
	}


	public String getTamanho() {
		return tamanho;
	}


	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}


	public Date getDtValidade() {
		return dtValidade;
	}


	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}


	public SceLoteDocumento getLoteDocSelecionado() {
		return loteDocSelecionado;
	}


	public void setLoteDocSelecionado(SceLoteDocumento loteDocSelecionado) {
		this.loteDocSelecionado = loteDocSelecionado;
	}
	
	
}