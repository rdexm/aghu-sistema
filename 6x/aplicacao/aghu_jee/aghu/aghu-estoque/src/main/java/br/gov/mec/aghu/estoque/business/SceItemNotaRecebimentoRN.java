package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceConversaoUnidMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDevolucaoFornecedorDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.GeracaoMovimentoEstoqueVO;
import br.gov.mec.aghu.estoque.vo.ItemNrVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceItemRecebProvisorio;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class SceItemNotaRecebimentoRN extends BaseBusiness{

	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;
	@EJB
	private SceNotaRecebimentoProvisorioRN sceNotaRecebimentoProvisorioRN;
	@EJB
	private SceNotaRecebimentoRN sceNotaRecebimentoRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	
	private static final Log LOG = LogFactory.getLog(SceItemNotaRecebimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@Inject
	private SceConversaoUnidMateriaisDAO sceConversaoUnidMateriaisDAO;
	
	@Inject
	private SceItemDevolucaoFornecedorDAO sceItemDevolucaoFornecedorDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;
	
	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;
	
	@Inject
	private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -541017255696866172L;

	public enum SceItemNotaRecebimentoRNExceptionCode implements BusinessExceptionCode {
		SCE_00714,SCE_00729,SCE_00730,SCE_00804,SCE_00659,SCE_00281,SCE_00720,SCE_00396,SCE_00721,SCE_00715,SCE_00719,MENSAGEM_VALOR_UNITARIO_FORA_DO_INTERVALO,MENSAGEM_VALOR_SERVICO,
		SCE_00651,SCE_00296,SCE_00754,USUARIO_NAO_TEM_CENTRO_DE_CUSTO_DE_ATUACAO,NAO_EXISTE_SALDO_ASS_SUFICIENTE, ERRO_DESFAZER_DEBITO_NR_POR_ITEM_AF,ERRO_DEBITO_NR_POR_ITEM_AF_ESTORNADA,
		ERRO_DEBITO_NR_POR_ITEM_AF_DEBITO,ERRO_DEBITO_NR_POR_ITEM_AF_COMPETENCIA;
	}

	/*
	 * Métodos para Inserir SceItemNotaRecebimento
	 */

	/**
	 * @ORADB TRIGGER SCET_INR_BRI (INSERT)
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	private void preInserir(SceItemNotaRecebimento itemNotaRecebimento, ItemNrVO vo) throws BaseException{

		this.validaItensDocumento(itemNotaRecebimento, vo);//RN1
		this.validaQtdeItemNr(itemNotaRecebimento); //RN2
		this.atualizaCodigoServico(itemNotaRecebimento);//RN3   
		if(itemNotaRecebimento.getServico()!=null && itemNotaRecebimento.getServico().getCodigo()!=null){
			this.atualizaCodigoMaterial(itemNotaRecebimento);//RN4 
		}
		this.validaDebitoNr(itemNotaRecebimento);


	}
	
	
	/**
	 * Inserir SceNotaRecebimento
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	public void inserir(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador, boolean flush)
			throws BaseException {
		
		ItemNrVO vo = new ItemNrVO();

		this.preInserir(itemNotaRecebimento, vo);
		this.getSceItemNotaRecebimentoDAO().persistir(itemNotaRecebimento);
		this.posInserir(itemNotaRecebimento, vo, nomeMicrocomputador, flush);

	}
	  
	
	public void atualizar(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException {
		this.preAtualizar(itemNotaRecebimentoOld, itemNotaRecebimento);
		this.getSceItemNotaRecebimentoDAO().atualizar(itemNotaRecebimento);
		this.posAtualizar(itemNotaRecebimentoOld, itemNotaRecebimento,nomeMicrocomputador);
	}
	
	/**
	 * ORADB Trigger SCET_INR_BRU
	 * @param itemNotaRecebimento
	 * @throws ApplicationBusinessException  
	 */
	public void preAtualizar(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException{
		this.verificarDebitoNRIaf(itemNotaRecebimentoOld, itemNotaRecebimento);
		this.verificarNREstornoDebito(itemNotaRecebimento);
		this.verificarMovimentoNR(itemNotaRecebimentoOld, itemNotaRecebimento);
		this.atualizarServidor(itemNotaRecebimentoOld, itemNotaRecebimento);
	}
	
	/**
	 * ORADB Trigger SCET_INR_ASU
	 * @param itemNotaRecebimento
	 * @throws BaseException 
	 */
	public void posAtualizar(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException{
		this.gerarMovimentoAtualizarIaf(itemNotaRecebimentoOld, itemNotaRecebimento, nomeMicrocomputador);
	}
	
	/**
	 * ORADB SCEK_INR_RN.RN_INRP_ATU_MVTO_DBI
	 * @throws BaseException 
	 */
	public void gerarMovimentoAtualizarIaf(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException{
			if(itemNotaRecebimentoOld.getIndDebitoNrIaf().equals(false)&&itemNotaRecebimento.getIndDebitoNrIaf().equals(true)){
				if(itemNotaRecebimento.getMaterial()!=null){
					Integer matCodigo = null;
					if(itemNotaRecebimento.getMaterial()!=null){
						matCodigo = itemNotaRecebimento.getMaterial().getCodigo(); 
					}
					GeracaoMovimentoEstoqueVO geracaoMovimentoEstoqueVO = this.getComprasFacade().obterGeracaoMovimentoEstoque(itemNotaRecebimento.getItemAutorizacaoForn().getId().getAfnNumero(), itemNotaRecebimento.getItemAutorizacaoForn().getId().getNumero(), matCodigo);
					if(geracaoMovimentoEstoqueVO.getIndGeraMovimentoEstoque().equals(true)){
						
						Integer frnNumero = itemNotaRecebimento.getItemAutorizacaoForn().getItemPropostaFornecedor().getId().getPfrFrnNumero();
						Integer mcmCodigo = null;
						Integer ncNumero = null;
						if(itemNotaRecebimento.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial()!=null){
							mcmCodigo = itemNotaRecebimento.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial().getId().getMcmCodigo();
							ncNumero = itemNotaRecebimento.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial().getId().getNumero();	
						}
						String historico = this.montaHistorico(itemNotaRecebimento, frnNumero, mcmCodigo, ncNumero);
						SceAlmoxarifado almoxarifado = this.getSceAlmoxarifadoDAO().obterPorChavePrimaria(geracaoMovimentoEstoqueVO.getAlmSeq());
						AghParametros parametroTipoMovimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_MOV_DB_IAF);
						Short seq = parametroTipoMovimento.getVlrNumerico().shortValue();
						SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(seq);
						SceTipoMovimento tipoMovimentoDocumento = itemNotaRecebimento.getNotaRecebimento().getTipoMovimento();
						AghParametros parametroFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
						ScoFornecedor fornecedor = this.getComprasFacade().obterScoFornecedorPorChavePrimaria(parametroFornecedor.getVlrNumerico().intValue());
						BigDecimal valor = new BigDecimal(itemNotaRecebimento.getValor());
						this.getSceMovimentoMaterialRN().atualizarMovimentoMaterial(almoxarifado, itemNotaRecebimento.getMaterial(), itemNotaRecebimento.getUnidadeMedida(), itemNotaRecebimento.getQuantidade(), 
								null, false, tipoMovimento, tipoMovimentoDocumento, itemNotaRecebimento.getId().getNrsSeq(), null, historico, fornecedor, null, null, 
								null, valor, itemNotaRecebimento.getId().getNrsSeq(), nomeMicrocomputador, true);
						this.getSceNotaRecebimentoRN().atualizarLote(itemNotaRecebimento.getNotaRecebimento(), itemNotaRecebimento.getItemAutorizacaoForn());
						//this.atualizaIaf(itemNotaRecebimento, solicitacaoDeCompra, itemAutorizacaoForn, estorno, notaRecebProvisorio);
					}
				}
				
				
			}
			
	}
	
	
	/**
	 * ORADB SCEK_INR_RN.RN_INRP_VER_DBIAF 
	 * @param itemNotaRecebimento
	 * @throws ApplicationBusinessException 
	 */
	public void verificarDebitoNRIaf(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException{
		if(itemNotaRecebimentoOld.getIndDebitoNrIaf().equals(true)&&itemNotaRecebimento.getIndDebitoNrIaf().equals(false)){
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.ERRO_DESFAZER_DEBITO_NR_POR_ITEM_AF);
		}
	}
	
	/**
	 * ORADB SCEK_INR_RN.RN_INRP_VER_DBIAF_ES  
	 * @param itemNotaRecebimento
	 * @throws ApplicationBusinessException 
	 */
	public void verificarNREstornoDebito(SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException{
		if(itemNotaRecebimento.getNotaRecebimento().getEstorno().equals(true)){
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.ERRO_DEBITO_NR_POR_ITEM_AF_ESTORNADA);
		}
		if(itemNotaRecebimento.getNotaRecebimento().getDebitoNotaRecebimento().equals(true)){
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.ERRO_DEBITO_NR_POR_ITEM_AF_DEBITO);
		}
	}
	
	/**
	 * ORADB SCEK_INR_RN.RN_INRP_VER_DBIAF_CP
	 * @param itemNotaRecebimento
	 * @throws ApplicationBusinessException  
	 */
	public void verificarMovimentoNR(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException{
			if(itemNotaRecebimentoOld.getIndDebitoNrIaf().equals(false)&&itemNotaRecebimento.getIndDebitoNrIaf().equals(true)){
				Date dataRetorno = this.getSceMovimentoMaterialDAO().obterDataCompetencia(itemNotaRecebimento.getNotaRecebimento().getTipoMovimento().getId().getSeq(), itemNotaRecebimento.getNotaRecebimento().getTipoMovimento().getId().getComplemento().intValue(), itemNotaRecebimento.getNotaRecebimento().getSeq());
				AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
				Date vlrData = parametro.getVlrData();
				if(vlrData.equals(dataRetorno)){
					throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.ERRO_DEBITO_NR_POR_ITEM_AF_COMPETENCIA);
				}
		}
	}
	
	
	
	/**
	 * ORADB SCEK_INR_RN.RN_INRP_ATU_SR_DBIAF 
	 */
	public void atualizarServidor(SceItemNotaRecebimento itemNotaRecebimentoOld, SceItemNotaRecebimento itemNotaRecebimento){
		if(itemNotaRecebimentoOld.getIndDebitoNrIaf().equals(false)&&itemNotaRecebimento.getIndDebitoNrIaf().equals(true)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			itemNotaRecebimento.setDtDebitoNrIaf(new Date());
			itemNotaRecebimento.setServidorDebitado(servidorLogado);
		}
	}
	
	
	protected void validaCentroCustoAtuacao() throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado.getCentroCustoAtuacao() == null ){
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.USUARIO_NAO_TEM_CENTRO_DE_CUSTO_DE_ATUACAO);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE SCEP_ENFORCE_INR_RULES (INSERT)
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	private void posInserir(SceItemNotaRecebimento itemNotaRecebimento, ItemNrVO vo, String nomeMicrocomputador, boolean flush)
			throws ApplicationBusinessException {

		ScoItemAutorizacaoForn itemAutorizacaoForn = new ScoItemAutorizacaoForn();		
		ScoSolicitacaoDeCompra solicitacaoDeCompra = this.atualizaCamposSolicCompras(itemNotaRecebimento);// RN1

		if (itemNotaRecebimento.getMaterial() != null) {
			this.validaMateriais(itemNotaRecebimento, vo);// RN2
			this.validaEstoqueAlmoxarifado(itemNotaRecebimento);// RN3
		}

		this.validaInrpIafVal(itemNotaRecebimento, itemAutorizacaoForn, vo, solicitacaoDeCompra.getDtSolicitacao(), solicitacaoDeCompra.getUnidadeMedida()); 

		if (itemNotaRecebimento.getMaterial() != null) {
			this.validarMaterialGeraMovimentoPequenoPorteValor(itemNotaRecebimento, vo, itemAutorizacaoForn, solicitacaoDeCompra, nomeMicrocomputador, flush); // RN4 e RN4.1
		}

		final Boolean estorno = itemNotaRecebimento.getNotaRecebimento().getEstorno();
		this.atualizaIaf(itemNotaRecebimento, solicitacaoDeCompra, itemAutorizacaoForn, estorno, null);// RN5

		this.validaSaldoAssAf(itemNotaRecebimento.getItemAutorizacaoForn()); // RN6

	}
	
	/**
	 * Insere Item Nota de Recebimento com Solicitação de Compra Automática
	 * Obs. Método relacionado com a tarefa #16137 Gerar Nota de Recebimento sem AF
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	public void inserirComSolicitacaoDeCompraAutomatica(SceItemNotaRecebimento itemNotaRecebimento, String nomeMicrocomputador) throws BaseException{
		
		final ItemNrVO vo = new ItemNrVO();
		
		/*
		 * Servidor sem centro de custo de atuacao issue #28524
		 */
		this.validaCentroCustoAtuacao();
		
		
		/*
		 * Regras da TRIGGER SCET_INR_BRI (INSERT)
		 */
		this.validaItensDocumento(itemNotaRecebimento, vo);//RN1 de SCET_INR_BRI
		this.validaQtdeItemNr(itemNotaRecebimento); //RN2 de SCET_INR_BRI
		
		// Insere Item de Nota de Recebimento
		this.getSceItemNotaRecebimentoDAO().persistir(itemNotaRecebimento);
		this.getSceItemNotaRecebimentoDAO().flush();
		
		/*i
		 * Regras da PROCEDURE SCEP_ENFORCE_INR_RULES (INSERT)
		 */
		ScoItemAutorizacaoForn itemAutorizacaoForn = new ScoItemAutorizacaoForn();		
		ScoSolicitacaoDeCompra solicitacaoDeCompra = this.atualizaCamposSolicCompras(itemNotaRecebimento);// RN1
		
		this.validaMateriais(itemNotaRecebimento, vo);// RN2 de SCEP_ENFORCE_INR_RULES
		this.validaEstoqueAlmoxarifado(itemNotaRecebimento);// RN3 de SCEP_ENFORCE_INR_RULES
		
		this.validarMaterialGeraMovimentoPequenoPorteValor(itemNotaRecebimento, vo, itemAutorizacaoForn, solicitacaoDeCompra, nomeMicrocomputador, true); // RN4 e RN4.1
	}

	/**
	 * ORADB SCEK_INR_RN.RN_INRP_VER_DBIAF_IN
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	protected void validaDebitoNr(SceItemNotaRecebimento itemNotaRecebimento) throws BaseException{

		if(Boolean.TRUE.equals(itemNotaRecebimento.getIndDebitoNrIaf())){
			//Não é permitido efetuar Débito de NR por Item de AF durante a inclusão de um Item de NR
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00804);
		}

	}




	/**
	 * ORADB PROCEDURE SCEK_INR_RN.INRP_VER_IAF_MATERIA
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	protected void atualizaCodigoMaterial(SceItemNotaRecebimento itemNotaRecebimento) throws BaseException{

		ScoItemAutorizacaoFornId itemAutorizacaoId = itemNotaRecebimento.getItemAutorizacaoForn() !=null ? itemNotaRecebimento.getItemAutorizacaoForn().getId():null;

		if(itemAutorizacaoId != null){

			List<ScoMaterial> listServico = this.getComprasFacade().pesquisaMaterialSolicitacaoCompras(itemAutorizacaoId);
			if(listServico != null && !listServico.isEmpty()){

				itemNotaRecebimento.setMaterial(listServico.get(0));

			}

		}
	}


	/**
	 * ORADB PROCEDURE INRP_VER_IAF_SERVICO e FUNCTION SCEK_INR_RN.INRC_VER_SERVICO
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	protected void atualizaCodigoServico(SceItemNotaRecebimento itemNotaRecebimento) throws BaseException{

		ScoItemAutorizacaoFornId itemAutorizacaoId = itemNotaRecebimento.getItemAutorizacaoForn()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getId():null;

		if(itemAutorizacaoId != null){
			Boolean servico =  this.getComprasFacade().pesquisaTipoFaseSolicitacao(itemAutorizacaoId);

			if(servico.equals(Boolean.TRUE)){

				List<ScoServico> listServico = this.getComprasFacade().pesquisaCodigoSolicitacaoServico(itemAutorizacaoId);

				if(listServico != null && !listServico.isEmpty()){

					itemNotaRecebimento.setServico(listServico.get(0));

				}

			}

		}

	}

	/**
	 * ORADB PROCEDURE SCEK_INR_RN.RN_INRP_VER_QTDE_VAL
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	protected void validaQtdeItemNr(SceItemNotaRecebimento itemNotaRecebimento) throws BaseException{

		// A quantidade do item NR não pode ser zero
		if(itemNotaRecebimento.getQuantidade() != null && itemNotaRecebimento.getQuantidade() <= 0){
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00729);
		}

		// O valor do item NR não pode ser zero
		if(itemNotaRecebimento.getValor() !=null && itemNotaRecebimento.getValor().floatValue() <= 0){
			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00730);
		}

	}

	/**
	 * ORADB PROCEDURE SCEK_INR_RN.RN_INRP_VER_INS_FILH
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	protected void validaItensDocumento(SceItemNotaRecebimento itemNotaRecebimento, ItemNrVO vo) throws BaseException{

		// Trecho abaixo foi comentado pois existem casos em que a nota de recebimento associada ao item ainda não está persistida no banco (Estória #25016 - Confirmacao de Recebimento)
		//SceNotaRecebimento notaRecebimento = this.getSceNotaRecebimentoDAO().obterSceNotaRecebimento(itemNotaRecebimento.getNotaRecebimento().getSeq());

		if (itemNotaRecebimento.getNotaRecebimento() != null) {

			if (Boolean.TRUE.equals(itemNotaRecebimento.getNotaRecebimento().getIndGerado())) {

				throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00714);

			}
			
			vo.setTipoMovimento(itemNotaRecebimento.getNotaRecebimento().getTipoMovimento());

		}

	}






	/**
	 * ORADB RN_INRP_VER_SLDO_ASS
	 * Verifica se o Item tem Saldo Assinado na AF
	 * @param itemAutorizacaoForn
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validaSaldoAssAf(ScoItemAutorizacaoForn itemAutorizacaoForn) throws ApplicationBusinessException {

		Double valorAssinadoItem = null;
		Double percVarPreco = null;
		Double valorEfetivadoItem = null; 
		Double valorAssinadoConsiderado = null;

		ScoItemAutorizacaoFornJn itemAutorizacaoFornJn = this.getComprasFacade().obterValorAssinadoPorItemNotaPorItemAutorizacaoForn(itemAutorizacaoForn);

		if (itemAutorizacaoFornJn == null) {
			
			valorAssinadoItem = new Double(0);
			percVarPreco = new Double(0);

		} else {

			Integer qtdeSolicitada = itemAutorizacaoFornJn.getQtdeSolicitada()!=null?itemAutorizacaoFornJn.getQtdeSolicitada():0;
			Integer qtdeRecebida = itemAutorizacaoFornJn.getQtdeRecebida()!=null?itemAutorizacaoFornJn.getQtdeRecebida():0;

			Double valorUnitario = ((qtdeSolicitada - qtdeRecebida) * itemAutorizacaoFornJn.getValorUnitario()) > 0?((qtdeSolicitada - qtdeRecebida) * itemAutorizacaoFornJn.getValorUnitario()):itemAutorizacaoFornJn.getValorUnitario();
			valorAssinadoItem = valorUnitario + itemAutorizacaoFornJn.getValorEfetivado();
			percVarPreco = itemAutorizacaoFornJn.getPercVarPreco();

		}

		Double valorNrItem = this.getSceItemNotaRecebimentoDAO().pesquisarValorEmNrPorItemAutorizacaoForn(itemAutorizacaoForn);

		if (valorNrItem == null) {

			valorNrItem = new Double(0);

		}		

		Double valorDfItem =  this.getItemDevolucaoFornecedorDAO().pesquisarValorDevolucaoFornItem(itemAutorizacaoForn);

		if (valorDfItem == null) {

			valorDfItem = new Double(0);

		}

		/* O Valor Efetivado a ser comparado com a Assinatura deve considerar o Valor Devolvido (DF) e o Percentual de Variação de
		 * Valor Unitário (para cima), cadastrado no Item da AF (pode variar no momento da Entrada), permitido para entrada do material.
		 */
		valorEfetivadoItem = valorNrItem - valorDfItem;
		valorAssinadoConsiderado = (valorAssinadoItem + (valorAssinadoItem * (percVarPreco/100)));

		if (valorEfetivadoItem > valorAssinadoConsiderado) {

			throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.NAO_EXISTE_SALDO_ASS_SUFICIENTE,
					valorAssinadoItem, 
					valorEfetivadoItem, 
					valorNrItem,
					percVarPreco, 
					valorDfItem, 
					itemAutorizacaoForn.getId().getNumero());
			
		}

	}


	/**
	 * PROCEDURE SCEK_INR_RN.RN_INRP_ATU_IAF
	 * @param itemNotaRecebimento
	 * @param dtSolicitacao
	 * @param umdSolic
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void atualizaIaf(SceItemNotaRecebimento itemNotaRecebimento,ScoSolicitacaoDeCompra solicitacaoDeCompra, 
			ScoItemAutorizacaoForn itemAutorizacaoForn, Boolean estorno, 
 SceNotaRecebProvisorio notaRecebProvisorio) throws ApplicationBusinessException {

		Integer quantidade = 0;
		Double valor = new Double(0);
		Boolean indConsignado = null; 
		Double valorEfetivado = new Double(0);

		if (itemNotaRecebimento.getUnidadeMedida() != null && solicitacaoDeCompra != null && (!solicitacaoDeCompra.getUnidadeMedida().equals(itemNotaRecebimento.getUnidadeMedida()))) {

			quantidade = this.buscarConversaoUnidade(itemNotaRecebimento.getMaterial().getCodigo(), itemNotaRecebimento.getQuantidade(), solicitacaoDeCompra.getDtSolicitacao(), solicitacaoDeCompra.getUnidadeMedida().getCodigo(), itemNotaRecebimento.getUnidadeMedida().getCodigo());

		}
		else {
			
			quantidade = itemNotaRecebimento.getQuantidade();
		}

		if (itemAutorizacaoForn.getFatorConversao() != null && itemAutorizacaoForn.getFatorConversao() > 0) {

			quantidade = quantidade / itemAutorizacaoForn.getFatorConversao();

		}

		valor = itemNotaRecebimento.getValor();

		if (estorno.equals(Boolean.TRUE)) {

			quantidade = 0 -  quantidade;
			valor = 0 -  valor;

		}

		/* Verifica se material da SC ligada ao IAF é consignado */
		ScoItemAutorizacaoFornId itemAutorizacaoId = itemNotaRecebimento.getItemAutorizacaoForn()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getId():null;

		indConsignado = this.getComprasFacade().pesquisaIndConsignado(itemAutorizacaoId);

		if (indConsignado == null) {
			
			indConsignado = Boolean.FALSE;
			
		}

		DominioTipoFaseSolicitacao tipoFase = this.getComprasFacade().pesquisaTipoFaseSolic(itemAutorizacaoId);

		if (tipoFase != null) {

			if (itemNotaRecebimento.getItemAutorizacaoForn() == null) {
				//Item AF não encontrado para atualizar quantidade recebida
				throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00754);
			
			}

			if (tipoFase.equals(DominioTipoFaseSolicitacao.C)) {  //Compra(Material)

				Integer qtdeRecebida = itemNotaRecebimento.getItemAutorizacaoForn().getQtdeRecebida()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getQtdeRecebida():0;
				valorEfetivado = itemNotaRecebimento.getItemAutorizacaoForn().getValorEfetivado()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getValorEfetivado():new Double(0);

				itemNotaRecebimento.getItemAutorizacaoForn().setQtdeRecebida(qtdeRecebida + quantidade);
				itemNotaRecebimento.getItemAutorizacaoForn().setValorEfetivado(valorEfetivado + valor);
				itemNotaRecebimento.getItemAutorizacaoForn().setIndRecebimento(Boolean.FALSE);

				/* Atualiza Ítem AF Material */
				
				this.getAutForncecimentoFacade().atualizarItemAutorizacaoFornecimento(itemNotaRecebimento.getItemAutorizacaoForn());

			} else {

				Integer qtdeSolicitada = itemNotaRecebimento.getItemAutorizacaoForn().getQtdeSolicitada()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getQtdeSolicitada():0;
				Integer qtdeRecebida = itemNotaRecebimento.getItemAutorizacaoForn().getQtdeRecebida()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getQtdeRecebida():0;

				itemNotaRecebimento.getItemAutorizacaoForn().setQtdeSolicitada(qtdeSolicitada + 1);
				itemNotaRecebimento.getItemAutorizacaoForn().setQtdeRecebida(qtdeRecebida +1 );

				valorEfetivado = itemNotaRecebimento.getItemAutorizacaoForn().getValorEfetivado()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getValorEfetivado():new Double(0);

				itemNotaRecebimento.getItemAutorizacaoForn().setValorEfetivado(valorEfetivado + valor);
				itemNotaRecebimento.getItemAutorizacaoForn().setIndRecebimento(Boolean.FALSE);

				/* Atualiza Ítem AF Serviços - Soma sempre a entrada de um Serviço e deixa um saldo - A efetivação é pelo Valor*/
				this.getAutForncecimentoFacade().atualizarItemAutorizacaoFornecimento(itemNotaRecebimento.getItemAutorizacaoForn());
			}

		}

		if (indConsignado.equals(Boolean.FALSE)) {

			if (estorno.equals(Boolean.TRUE)) {

				if (notaRecebProvisorio != null) {

					Boolean existeItens = Boolean.FALSE;

					List<SceItemRecebProvisorio> listItemRecebProv =  this.getItemRecebProvisorioDAO().pesquisaItemRecebProvisorio(notaRecebProvisorio.getSeq());

					for (SceItemRecebProvisorio itemRecebProv : listItemRecebProv) {

						Integer qtdeEstorno  = itemRecebProv.getQuantidade();
						Double  valorEstorno = itemRecebProv.getValor();

						Integer iafNumero = itemRecebProv.getIafNumero();
						Integer iafAfnNumero = itemRecebProv.getIafAfnNumero();
						Integer qtdeEntregue =null;

						List<ScoProgEntregaItemAutorizacaoFornecimento> listPea = this.getComprasFacade().pesquisaProgEntregaItemAf(iafAfnNumero,iafNumero, true, true);

						for (ScoProgEntregaItemAutorizacaoFornecimento pea: listPea) {

							qtdeEntregue = pea.getQtdeEntregue();
							itemAutorizacaoForn.setFatorConversao( pea.getScoItensAutorizacaoForn()!=null?pea.getScoItensAutorizacaoForn().getFatorConversao():null);

							if (itemAutorizacaoForn.getFatorConversao() != null && qtdeEntregue > (qtdeEstorno / itemAutorizacaoForn.getFatorConversao())) {
							
								qtdeEntregue = (qtdeEstorno / itemAutorizacaoForn.getFatorConversao());

							}

							valorEfetivado = pea.getValorEfetivado();

							if (valorEfetivado > valorEstorno) {

								valorEfetivado = valorEstorno;

							}

							Integer qtdeEntregueUpdate = pea.getQtdeEntregue()!=null?pea.getQtdeEntregue():0;
							pea.setQtdeEntregue(qtdeEntregueUpdate - qtdeEntregue);
							Double valorEfetivadoUpdate = pea.getValorEfetivado()!=null?pea.getValorEfetivado():new Double(0);
							pea.setValorEfetivado(valorEfetivadoUpdate - valorEfetivado);

							this.getAutForncecimentoFacade().persistirProgEntregaItemAf(pea);

							qtdeEstorno = qtdeEstorno - (qtdeEntregue * itemAutorizacaoForn.getFatorConversao());
							valorEstorno = valorEstorno - valorEfetivado;

						}//fim for

						existeItens = Boolean.TRUE;

						/*Verifica se sobrou Qtde Entregue ou Valor Efetivado no Item,
						 *  em caso de não haver mais nenhuma NRP NÃO Estornada
						 */
						SceItemRecebProvisorio itemRecQtdeEntregue = getItemRecebProvisorioDAO().pesquisaQtdeEntregue(iafAfnNumero, iafNumero, notaRecebProvisorio.getSeq());

						Integer qtdeRecebida = itemRecQtdeEntregue!=null?itemRecQtdeEntregue.getQuantidade():null;
						Double valorRecebido = itemRecQtdeEntregue!=null?itemRecQtdeEntregue.getValor():null;


						List<ScoProgEntregaItemAutorizacaoFornecimento> listPeaAss = this.getComprasFacade().pesquisaProgEntregaItemAfAss(iafAfnNumero, iafNumero);

						for (ScoProgEntregaItemAutorizacaoFornecimento peaAss: listPeaAss) {

							Integer qtdeParcela = peaAss.getQtde();
							Double valorParcela = peaAss.getValorTotal();
							Integer fatorConversaoAss = peaAss.getScoItensAutorizacaoForn().getFatorConversao();

							if (qtdeRecebida != null && qtdeRecebida > 0) {

								if (qtdeParcela < (qtdeRecebida / fatorConversaoAss)) {

									qtdeEntregue = qtdeParcela;

								} else {
									
									qtdeEntregue = (qtdeRecebida / fatorConversaoAss);
									
								}

								if (valorParcela < valorRecebido) {

									valorEfetivado = valorParcela;

								} else {

									valorEfetivado = valorRecebido;

								}

								if (qtdeEntregue == (qtdeRecebida/ fatorConversaoAss) && valorEfetivado != valorRecebido) {

									valorEfetivado = valorRecebido;

								}

								peaAss.setQtdeEntregue(qtdeEntregue);
								peaAss.setValorEfetivado(valorEfetivado);

								this.getAutForncecimentoFacade().persistirProgEntregaItemAf(peaAss);

								qtdeRecebida = qtdeRecebida - (qtdeEntregue * itemAutorizacaoForn.getFatorConversao());
								valorRecebido = valorRecebido - valorEfetivado;

							} else {

								peaAss.setQtdeEntregue(null);
								peaAss.setValorEfetivado(null);

								this.getAutForncecimentoFacade().persistirProgEntregaItemAf(peaAss);

							}

						}

					}//fim for

					if (existeItens.equals(Boolean.TRUE)) {

						/**
						 * Atualiza tabela: sce_nota_receb_provisorios.
						 */
						notaRecebProvisorio.setIndConfirmado(Boolean.FALSE);
						// TODO falta implementar método atualizar
						//this.getNotaRecebProvisorioRN().atualizar(notaRecebProvisorio);

					}

				}

			}

		}

	}
	
	/**
	 * Esta função se propõe a transformar uma quantidade de material expressa na unidade de medida em outra unidade,
     * sendo que esta outra fora uma unidade de medida anterior a unidade origem. A unidade destino é mais atualizada que a
     * unidade origem. Deve-se buscar a conversão e varrer na ordem inversa, fazendo as conversões na medida do necessário.
	 * @ORADB SCEC_BUSC_CONV_UN_R
	 * @param matCodigo
	 * @param quantidade
	 * @param dtSolicitacao
	 * @param umdSolic
	 * @param umdCodigoDest
	 * @return
	 * @throws BaseException
	 */
	public Integer buscarConversaoUnidade(Integer matCodigo, Integer quantidade, Date dtSolicitacao, String umdSolic, String umdCodigoDest)
			throws ApplicationBusinessException {
		Integer quantConv = quantidade;
		
		/* Melhoria em Desenvolvimento #31009
		if (!umdSolic.equals(umdCodigoDest)) {

			String unidCorrente = umdCodigoDest;
			
			
			List<SceConversaoUnidMateriais> conversaoUnidades = getSceConversaoUnidMateriaisDAO().
					pesquisarConversaoUnidadesPorMatDtGeracao(matCodigo ,dtSolicitacao);
			
			for (SceConversaoUnidMateriais convUnid : conversaoUnidades) {
				if (unidCorrente.equals(convUnid.getConversaoUnidade().getId().getUmdCodigoDestino())) {
					Integer restoDiv = quantConv % convUnid.getConversaoUnidade().getId().getFatorConversao().intValue();
					if (restoDiv == 0) {
						quantConv = quantConv / convUnid.getConversaoUnidade().getId().getFatorConversao().intValue();
						unidCorrente =  convUnid.getConversaoUnidade().getId().getUmdCodigo();
					} else {
						throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00651);
					}
				} else {
					throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00296);
				}

				if (convUnid.getConversaoUnidade().getId().getUmdCodigo().equals(umdSolic)) {
					break;				
				}
			}
			
		}*/

		return quantConv;
	}

	/**
	 * ORADB FUNCTION RN_INRC_VER_NAT_DESP - VERSAO AGHU
	 * 
	 * @param itemNotaRecebimento
	 * @param almSeq
	 * @throws ApplicationBusinessException
	 */
	public Boolean validaMovtoEstoqueMaterial(SceItemNotaRecebimento itemNotaRecebimento, ScoItemAutorizacaoForn itemAutorizacaoForn, Date dtSolicitacao,
			ScoUnidadeMedida umdSolic) throws ApplicationBusinessException {

		final Integer limite = 100;
		
		if (itemNotaRecebimento.getMaterial() != null && itemNotaRecebimento.getMaterial().getGrupoMaterial() != null) {

			if (itemNotaRecebimento.getMaterial().getGrupoMaterial().getGeraMvtoCondVlr().equals(Boolean.TRUE)) {

				Double vlrUnitario = itemNotaRecebimento.getValor() / itemNotaRecebimento.getQuantidade();

				AghParametros paramLimMatPeqPorte = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VLR_LIM_MAT_PEQ_PORTE);

				Integer vlrLimMatPeqPorte = paramLimMatPeqPorte.getVlrNumerico().intValue() / limite;

				if (vlrUnitario <= vlrLimMatPeqPorte) { 

					return true;

				} 
				
				return false;

			}

		}
		
		return true;

	}

	/**
	 * ORADB SCEK_INR_RN.RN_INRC_VER_HISTORIC
	 * @param itemNotaRecebimento
	 * @param frnNumero
	 * @param mcmCodigo
	 * @param ncNumero
	 */
	public String montaHistorico(SceItemNotaRecebimento itemNotaRecebimento, Integer frnNumero, Integer mcmCodigo, Integer ncNumero) throws BaseException {

		String marca = "";
		String nome = "";

		/* Concatena dados do documento fiscal de entrada. */
		Integer dfeSeq =  itemNotaRecebimento.getNotaRecebimento().getDocumentoFiscalEntrada().getSeq();
		StringBuilder historico = new StringBuilder(50);
		historico.append("DOCUMENTO FISCAL DE ENTRADA: ").append(dfeSeq);					

		/* Concatena dados do fornecedor. */
		ScoFornecedor fornecedor = this.getComprasFacade().obterFornecedorPorNumero(frnNumero);

		String nomeFornecedor = fornecedor!=null?fornecedor.getNomeFantasia():"";

		historico.append(" - Fornecedor: ").append(nomeFornecedor);

		/* Concatena dados da marca comercial. */
		if (mcmCodigo != null) {

			ScoMarcaComercial marcaComercial =  this.getComprasFacade().obterMarcaComercialPorCodigo(mcmCodigo);
			marca = marcaComercial!=null?marcaComercial.getDescricao():"";
			historico.append(" - Marca Comercial: ").append(marca);

		}

		/* Concatena dados do nome comercial. */
		if (ncNumero != null) {

			ScoNomeComercial nomeComercial = this.getComprasFacade().obterNomeComercialPorMcmCodigoNumero(mcmCodigo, ncNumero);
			nome = nomeComercial!=null?nomeComercial.getNome():"";
			historico.append(" - Nome Comercial: ").append(nome);

		}

		return historico.toString();

	}

	/**
	 * ORADB PROCEDURE SCEK_INR_RN.RN_INRP_VER_IAF_VAL
	 * @param itemNotaRecebimento
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void validaInrpIafVal(SceItemNotaRecebimento itemNotaRecebimento, ScoItemAutorizacaoForn itemAutorizacaoForn, ItemNrVO vo, Date dtSolicitacao, ScoUnidadeMedida umdSolic) 
 throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		Integer qtdeIaf = 0;
		Integer qtdeRecIaf = 0;

		Double saldoBruto = new Double(0);
		Double desconto1  = new Double(0);
		Double desconto2   = new Double(0);
		Double descTotal  = new Double(0);
		Double acrescimo1  = new Double(0);
		Double acrescimo2  = new Double(0);
		Double acrescTotal = new Double(0);
		Double valorIpi    = new Double(0);
		Double valorTotal  = new Double(0);

		Boolean validaSituacaoItem = false;
		Boolean indConsignado = false; 

		Double valUnitIaf = new Double(0);
		Double valUnitInrMax = new Double(0); 
		Double valUnitInrMin = new Double(0);
		Double valUnitInr = new Double(0); 

		ScoItemAutorizacaoFornId itemAutorizacaoId = itemNotaRecebimento.getItemAutorizacaoForn()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getId():null;

		if (itemAutorizacaoId != null) {

			List<ScoFaseSolicitacao> listfaseSolicitacao = this.getComprasFacade().pesquisaItensAutPropostaFornecedor(itemAutorizacaoId);

			if (listfaseSolicitacao != null && !listfaseSolicitacao.isEmpty()) {

				for (ScoFaseSolicitacao faseSolicitacao: listfaseSolicitacao) {

					if (faseSolicitacao.getItemAutorizacaoForn() != null && this.validaSituacaoAf(faseSolicitacao.getItemAutorizacaoForn().getIndSituacao())) {

						validaSituacaoItem = true;
						break;

					} else {

						ScoItemAutorizacaoForn o = faseSolicitacao.getItemAutorizacaoForn();

						itemAutorizacaoForn.setAutorizacoesForn(o.getAutorizacoesForn());
						itemAutorizacaoForn.setDtEstorno(o.getDtEstorno());
						itemAutorizacaoForn.setDtExclusao(o.getDtExclusao());
						itemAutorizacaoForn.setFatorConversao(o.getFatorConversao());
						itemAutorizacaoForn.setFatorConversaoForn(o.getFatorConversaoForn());
						itemAutorizacaoForn.setId(o.getId());
						itemAutorizacaoForn.setIndAnaliseProgrPlanej(o.getIndAnaliseProgrPlanej());
						itemAutorizacaoForn.setIndConsignado(o.getIndConsignado());
						itemAutorizacaoForn.setIndContrato(o.getIndContrato());
						itemAutorizacaoForn.setIndEstorno(o.getIndEstorno());
						itemAutorizacaoForn.setIndExclusao(o.getIndExclusao());
						itemAutorizacaoForn.setIndPreferencialCum(o.getIndPreferencialCum());
						itemAutorizacaoForn.setIndProgrEntgAuto(o.getIndProgrEntgAuto());
						itemAutorizacaoForn.setIndProgrEntgBloq(o.getIndProgrEntgBloq());
						itemAutorizacaoForn.setIndRecebimento(o.getIndRecebimento());
						itemAutorizacaoForn.setIndSituacao(o.getIndSituacao());
						itemAutorizacaoForn.setItemNotaRecebimento(o.getItemNotaRecebimento());
						itemAutorizacaoForn.setItemPropostaFornecedor(o.getItemPropostaFornecedor());
						itemAutorizacaoForn.setMarcaComercial(o.getMarcaComercial());
						itemAutorizacaoForn.setNomeComercial(o.getNomeComercial());
						itemAutorizacaoForn.setPercAcrescimo(o.getPercAcrescimo());
						itemAutorizacaoForn.setPercAcrescimoItem(o.getPercAcrescimoItem());
						itemAutorizacaoForn.setPercDesconto(o.getPercDesconto());
						itemAutorizacaoForn.setPercDescontoItem(o.getPercDescontoItem());
						itemAutorizacaoForn.setPercIpi(o.getPercIpi());
						itemAutorizacaoForn.setPercVarPreco(o.getPercVarPreco());
						itemAutorizacaoForn.setQtdeRecebida(o.getQtdeRecebida());
						itemAutorizacaoForn.setQtdeSolicitada(o.getQtdeSolicitada());
						itemAutorizacaoForn.setScoFaseSolicitacao(o.getScoFaseSolicitacao());
						itemAutorizacaoForn.setSequenciaAlteracao(o.getSequenciaAlteracao());
						itemAutorizacaoForn.setServidor(o.getServidor());
						itemAutorizacaoForn.setServidorEstorno(o.getServidorEstorno());
						itemAutorizacaoForn.setUmdCodigoForn(o.getUmdCodigoForn());
						itemAutorizacaoForn.setUnidadeMedida(o.getUnidadeMedida());
						itemAutorizacaoForn.setValorEfetivado(o.getValorEfetivado());
						itemAutorizacaoForn.setValorUnitario(o.getValorUnitario());

						qtdeIaf = faseSolicitacao.getItemAutorizacaoForn().getQtdeSolicitada();
						
						qtdeRecIaf = faseSolicitacao.getItemAutorizacaoForn().getQtdeRecebida() != null?
								     faseSolicitacao.getItemAutorizacaoForn().getQtdeRecebida():null;

						if (itemNotaRecebimento.getMaterial() != null && itemNotaRecebimento.getMaterial().getCodigo() != null) {
							if (faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null && faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor() != null) {
								vo.setFrnNumero(faseSolicitacao.getItemAutorizacaoForn().getItemPropostaFornecedor().getId().getPfrFrnNumero());
							}

							vo.setMcmCodigo(faseSolicitacao.getItemAutorizacaoForn().getMarcaComercial() != null?
									        faseSolicitacao.getItemAutorizacaoForn().getMarcaComercial().getCodigo():null);
							
							if (faseSolicitacao.getItemAutorizacaoForn().getNomeComercial() != null) {
								vo.setMcmCodigo(faseSolicitacao.getItemAutorizacaoForn().getNomeComercial().getId().getMcmCodigo());
								vo.setNcNumero(faseSolicitacao.getItemAutorizacaoForn().getNomeComercial().getId().getNumero());
							}

							if (itemAutorizacaoForn.getFatorConversao() > 0) {
								qtdeIaf = qtdeIaf * itemAutorizacaoForn.getFatorConversao();
								qtdeRecIaf = qtdeRecIaf * itemAutorizacaoForn.getFatorConversao();
							}
						}

						indConsignado = this.getComprasFacade().pesquisaIndConsignado(itemAutorizacaoId);

						if (faseSolicitacao != null && DominioTipoFaseSolicitacao.C.equals(faseSolicitacao.getTipo()) && Boolean.FALSE.equals(indConsignado)) {

							/**
							 *  Se o material é consignado (e está em contrato)  NÃO  verifica o saldo da AF.
							 *  Para estes casos o saldo da AF já foi verificado e atualizado na entrada de Consignação (CONSE).
							 */
							if (qtdeIaf < (qtdeRecIaf + itemNotaRecebimento.getQuantidade())) {

								//A quantidade do item NR somada à quantidade já recebida ultrapassa a quantidade do item AF.									
								throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00719);

							}

						}

						if (faseSolicitacao.getItemAutorizacaoForn() != null) {
							//Cálculo do valor unitário do item
							Integer qtdeSolic = faseSolicitacao.getItemAutorizacaoForn().getQtdeSolicitada()!=null?faseSolicitacao.getItemAutorizacaoForn().getQtdeSolicitada():0;
							Integer qtdeRec = faseSolicitacao.getItemAutorizacaoForn().getQtdeRecebida()!=null?faseSolicitacao.getItemAutorizacaoForn().getQtdeRecebida():0;
							Double vlUnit = faseSolicitacao.getItemAutorizacaoForn().getValorUnitario()!=null?faseSolicitacao.getItemAutorizacaoForn().getValorUnitario():new Double(0);

							saldoBruto =  (qtdeSolic - qtdeRec) * vlUnit;
							Double percDescItem = faseSolicitacao.getItemAutorizacaoForn().getPercDescontoItem()!=null?faseSolicitacao.getItemAutorizacaoForn().getPercDescontoItem():new Double(0);

							if (percDescItem > 0) {

								desconto1 = saldoBruto * (percDescItem/100);

							}

							Double percDesc = faseSolicitacao.getItemAutorizacaoForn().getPercDesconto()!=null?faseSolicitacao.getItemAutorizacaoForn().getPercDesconto():new Double(0);

							if (percDesc > 0) {

								desconto2 = (saldoBruto - desconto1) * (percDesc/100);

							}

							descTotal = desconto1 + desconto2;
							Double percAcrescItem = faseSolicitacao.getItemAutorizacaoForn().getPercAcrescimoItem()!=null?faseSolicitacao.getItemAutorizacaoForn().getPercAcrescimoItem():new Double(0);

							if (percAcrescItem > 0) {

								acrescimo1 = (saldoBruto - descTotal) * (percAcrescItem/100);

							}

							Double percAcresc = faseSolicitacao.getItemAutorizacaoForn().getPercAcrescimo()!=null?faseSolicitacao.getItemAutorizacaoForn().getPercAcrescimo():new Double(0);

							if (percAcresc > 0) {

								acrescimo1 = (saldoBruto - descTotal + acrescimo1) * (percAcresc/100);

							}

							Double percIpi = faseSolicitacao.getItemAutorizacaoForn().getPercIpi()!=null?faseSolicitacao.getItemAutorizacaoForn().getPercIpi():new Double(0);
							acrescTotal = acrescimo1 + acrescimo2;

							if (percIpi > 0) {

								valorIpi = (saldoBruto - descTotal + acrescTotal) * (percIpi/100);

							}

							valorTotal = (saldoBruto - descTotal + acrescTotal + valorIpi);

							valUnitIaf = (valorTotal / (qtdeSolic - qtdeRec ));

							Double percVarPreco = faseSolicitacao.getItemAutorizacaoForn().getPercVarPreco()!=null?faseSolicitacao.getItemAutorizacaoForn().getPercVarPreco():new Double(0);

							valUnitInrMax = valUnitIaf + (( valUnitIaf * percVarPreco ) / 100);

							valUnitInrMin = valUnitIaf -  (( valUnitIaf * percVarPreco ) / 100);

							if (itemAutorizacaoForn.getFatorConversao() > 0 ) {
								valUnitInr = (itemNotaRecebimento.getValor() / itemNotaRecebimento.getQuantidade()) * itemAutorizacaoForn.getFatorConversao() ;
							} else {
								valUnitInr = itemNotaRecebimento.getValor() / itemNotaRecebimento.getQuantidade();
							}

							if (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C)) {
								//Quando for material
								if ((valUnitInr > valUnitInrMax) || (valUnitInr < valUnitInrMin)) {
									if(!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "excederPerVariaValor")) {
										Integer erroMatCod = itemNotaRecebimento.getMaterial().getCodigo();
										throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.MENSAGEM_VALOR_UNITARIO_FORA_DO_INTERVALO,
												erroMatCod, valUnitInr, percVarPreco, valUnitIaf);
									}
								}
							} else {

								Double valrEfetivado = faseSolicitacao.getItemAutorizacaoForn().getValorEfetivado()!=null?faseSolicitacao.getItemAutorizacaoForn().getValorEfetivado():new Double(0);

								//Quando for Serviço
								if (valrEfetivado > vlUnit) {
									throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.MENSAGEM_VALOR_SERVICO);
								}
							}
						}
					}
				}

				if (validaSituacaoItem) {
					throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00715);
				}
			}
		}
	}
	
	/**
	 * Verifica se material gera movimento de estoque ou é pequeno porte e com valor 
	 * @param itemNotaRecebimento
	 * @param vo
	 * @param itemAutorizacaoForn
	 * @param solicitacaoDeCompra
	 * @throws BaseException
	 */
	public void validarMaterialGeraMovimentoPequenoPorteValor(SceItemNotaRecebimento itemNotaRecebimento, ItemNrVO vo,
			ScoItemAutorizacaoForn itemAutorizacaoForn, ScoSolicitacaoDeCompra solicitacaoDeCompra, String nomeMicrocomputador,
			boolean flush) throws ApplicationBusinessException {

		if (validaMovtoEstoqueMaterial(itemNotaRecebimento,itemAutorizacaoForn, solicitacaoDeCompra.getDtSolicitacao(), solicitacaoDeCompra.getUnidadeMedida())) {
			
			AghParametros paramFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			ScoFornecedor fornecedor = null;
			
			if (paramFrnHcpa != null && paramFrnHcpa.getVlrNumerico() != null) {
				
				fornecedor = getComprasFacade().obterFornecedorPorNumero(paramFrnHcpa.getVlrNumerico().intValue());
			
			}
			try {
				String historico = this.montaHistorico(itemNotaRecebimento, vo.getFrnNumero(), vo.getMcmCodigo(), vo.getNcNumero());
				getSceMovimentoMaterialRN().atualizarMovimentoMaterial(vo.getAlmoxarifado(), itemNotaRecebimento.getMaterial(),
						itemNotaRecebimento.getUnidadeMedida(), itemNotaRecebimento.getQuantidade(), null, Boolean.FALSE, vo.getTipoMovimento(), null,
						itemNotaRecebimento.getId().getNrsSeq(), null, historico, fornecedor, null, null, null,
						BigDecimal.valueOf(itemNotaRecebimento.getValor()), null, nomeMicrocomputador, flush);

			} catch (BaseException e) {
				throw new ApplicationBusinessException(e);
			}
		}
		
		
	}


	/**
	 * ORADB SCEC_BUSC_CONV_UN
	 * Esta função se propõe a transformar uma quantidade de material expressa na unidade de medida original do
	 * documento numa nova quantidade expressa na unidade de medida atual do material. Para isso, deve-se buscar
	 * todas as conversões de unidades ocorridas para o material e aplicar os fatores de conversão sobre a quantidade original
	 * até encontrar a quantidade na unidade de medida final.
	 * @param item
	 * @param sceReqMateriais
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public Integer buscaQtdeIaf(SceItemNotaRecebimento itemNotaRecebimento, Integer qtdeIaf, Date dtSolicitacao, String umdSolic ) throws ApplicationBusinessException {

		Integer quantidade = qtdeIaf;

		// A Regra foi depreciada no AGH e AGHU
		/*if (!umdSolic.equals(itemNotaRecebimento.getUnidadeMedida().getCodigo())) {

			unidCorrente = umdSolic;
			Boolean erroUnidadeConversao = false;

			List<SceConversaoUnidMateriais> conversaoUnidades = getSceConversaoUnidMateriaisDAO().pesquisarConversaoUnidadesPorMaterialDtGeracao(itemNotaRecebimento.getMaterial(), dtSolicitacao);

			for (SceConversaoUnidMateriais convUnid : conversaoUnidades) {

				if (unidCorrente.equals(convUnid.getScoUnidadeMedida().getCodigo())) {

					quantidade = quantidade * (convUnid.getCvuFatorConversao().intValue());
					unidCorrente = convUnid.getScoUnidadeMedidaDestino().getCodigo();

				} else {

					erroUnidadeConversao = true;
					break;
					
				}

				if (convUnid.getScoUnidadeMedidaDestino() != null && (convUnid.getScoUnidadeMedidaDestino().getCodigo().equals(itemNotaRecebimento.getUnidadeMedida().getCodigo()))) {

					break;

				}

			} //Fim for

			if (erroUnidadeConversao) {
		
				throw new ApplicationBusinessException(SceConversaoUnidMateriaisRNExceptionCode.SCE_00296);
			
			}

		}*/

		return quantidade;

	}

	/**
	 * Valida Situacao AF
	 * @param indSituacao
	 * @return
	 */
	private Boolean validaSituacaoAf(DominioSituacaoAutorizacaoFornecedor indSituacao) {

		Boolean retorno = false;

		if (indSituacao.equals(DominioSituacaoAutorizacaoFornecedor.ES) || indSituacao.equals(DominioSituacaoAutorizacaoFornecedor.EX) ||
				indSituacao.equals(DominioSituacaoAutorizacaoFornecedor.EP) || indSituacao.equals(DominioSituacaoAutorizacaoFornecedor.EF)) {

			retorno = true;

		}

		return retorno;
		
	}


	/**
	 * ORADB PROCEDURE SCEK_INR_RN.RN_INRP_VER_MAT_PI
	 * @param itemNotaRecebimento
	 * @param dtSolicitacao
	 * @throws BaseException
	 */
	private void validaMateriais(SceItemNotaRecebimento itemNotaRecebimento, ItemNrVO vo) throws ApplicationBusinessException {

		if (itemNotaRecebimento.getMaterial() != null && itemNotaRecebimento.getMaterial().getCodigo() != null) {

			if (itemNotaRecebimento.getMaterial() == null) {

				throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00659);

			}

			if (itemNotaRecebimento.getUnidadeMedida() != null && !itemNotaRecebimento.getUnidadeMedida().equals(itemNotaRecebimento.getMaterial().getUnidadeMedida())) {

				throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00281);

			}

			if (itemNotaRecebimento.getMaterial().getIndProducaoInterna() != null) {

				throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00720);

			}
			
			vo.setAlmoxarifado(itemNotaRecebimento.getMaterial().getAlmoxarifado());

		}

	}


	/**
	 * ORADB PROCEDURE SCEK_INR_RN.RN_INRP_VER_EAL_ATIV
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	private void validaEstoqueAlmoxarifado(SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException {

		if (itemNotaRecebimento.getMaterial() != null && itemNotaRecebimento.getMaterial().getCodigo() != null) {

			AghParametros paramFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);

			if (itemNotaRecebimento.getMaterial().getAlmoxarifado() != null && paramFrnHcpa != null) {

				List<SceEstoqueAlmoxarifado> listEstqAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoParaNotaRecebimento(itemNotaRecebimento.getMaterial().getCodigo(), itemNotaRecebimento.getMaterial().getAlmoxarifado().getSeq(), paramFrnHcpa.getVlrNumerico().intValue());

				if (listEstqAlmoxarifado == null || listEstqAlmoxarifado.isEmpty()) {

					throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00396);

				} else {

					SceEstoqueAlmoxarifado estqAlm = listEstqAlmoxarifado.get(0);

					if  (estqAlm.getIndSituacao().equals(DominioSituacao.I)) {

						throw new ApplicationBusinessException(SceItemNotaRecebimentoRNExceptionCode.SCE_00721);

					}

				}

			}

		}

	}

	/**
	 * ORADB PROCEDURE SCEK_INR_RN.INRP_VER_UMD_SC
	 * @param itemNotaRecebimento
	 * @throws BaseException
	 */
	private ScoSolicitacaoDeCompra atualizaCamposSolicCompras(SceItemNotaRecebimento itemNotaRecebimento) throws ApplicationBusinessException {

		ScoSolicitacaoDeCompra solicCompras = new ScoSolicitacaoDeCompra();
		ScoItemAutorizacaoFornId itemAutorizacaoId = itemNotaRecebimento.getItemAutorizacaoForn()!=null?itemNotaRecebimento.getItemAutorizacaoForn().getId():null;

		if (itemAutorizacaoId != null) {

			Boolean servico =  this.getComprasFacade().pesquisaTipoFaseSolicitacao(itemAutorizacaoId);

			if (servico.equals(Boolean.FALSE)) {

				solicCompras = this.getComprasFacade().pesquisaSolicitacaoCompras(itemAutorizacaoId);

				if (solicCompras != null && solicCompras.getUnidadeMedida() != null) {

					itemNotaRecebimento.setUnidadeMedida(solicCompras.getUnidadeMedida());

				}

			}

		}

		return solicCompras;

	}
	
	/**
	 * 
	 * @ORADB: Function SCEK_INR_RN.INRC_VER_VAL_LIQ_IAF
	 */
	public Double getValorUnitarioLiquido(
			Double valorUnitarioItemAutorizacaoFornecimento,
			Integer quantidadeRecebida, Double percAcrescimo,
			Double percAcrescimoItem, Double percDesconto,
			Double percDescontoItem, Double percIpi) {
		Double valorBruto = 0.0;
		Double valorLiquido = 0.0;
		Double valorDescontoItem = 0.0;
		Double valorDesconto = 0.0;
		Double valorAcrescimoItem = 0.0;
		Double valorAcrescimo = 0.0;
		Double valorIpi = 0.0;
		Double custoUnitLiq = 0.0;
		
		if (valorUnitarioItemAutorizacaoFornecimento != null && quantidadeRecebida != null){
			valorBruto = (quantidadeRecebida * valorUnitarioItemAutorizacaoFornecimento);
			valorLiquido = valorBruto;
			if(percDescontoItem != null && percDescontoItem > 0){
				valorDescontoItem = (valorBruto*(percDescontoItem / 100));
				valorLiquido = (valorLiquido - valorDescontoItem);
			}
			if (percDesconto != null && percDesconto > 0){
				valorDesconto = (valorBruto*(percDesconto / 100));
				valorLiquido = (valorLiquido - valorDesconto);
			}
			if (percAcrescimoItem != null && percAcrescimoItem > 0){
				valorAcrescimoItem = (valorBruto*(percAcrescimoItem / 100));
				valorLiquido = (valorLiquido + valorAcrescimoItem);
			}
			if (percAcrescimo != null && percAcrescimo > 0){
				valorAcrescimo = (valorBruto*(percAcrescimo / 100));
				valorLiquido = (valorLiquido + valorAcrescimo);
			}
			if (percIpi != null && percIpi > 0){
				valorIpi = (valorLiquido * (percIpi / 100));
				valorLiquido = (valorLiquido + valorIpi);
			}
			if (quantidadeRecebida == 0){
				quantidadeRecebida = 1;
			}
			custoUnitLiq =(valorLiquido / quantidadeRecebida);
		}
		return custoUnitLiq;
	}
	
	/**
	 * Function F5 da estória #6635
	 * ORADB_ENTR_SERV_DIA
	 * @param dataGeracao 
	 * @return
	 */
	 public Double obterQuantidadeServicosDia(Date dataGeracao){
	   Double somaServicos = getSceItemNotaRecebimentoDAO().pesquisarEntradaServicos(dataGeracao, Boolean.FALSE);
	   return somaServicos;
	 }
	 
	 /**
	 * Function F8 da estória #6635
	 * ORADB_ENTR_ACUM_SERV_MES
	 * @param dataGeracao
	 * @return
	 */
	 public Double obterQuantidadeServicosMes(Date dataGeracao){
	   Double somaServicos = getSceItemNotaRecebimentoDAO().pesquisarEntradaServicos(dataGeracao, Boolean.TRUE);
	   return somaServicos;
	 }

	
	/**
	 * Getters para RNs e DAOs
	 */

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceNotaRecebimentoDAO getSceNotaRecebimentoDAO(){
		return sceNotaRecebimentoDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}

	public SceItemRecebProvisorioDAO getItemRecebProvisorioDAO(){
		return sceItemRecebProvisorioDAO;
	}

	public SceNotaRecebimentoProvisorioRN getNotaRecebProvisorioRN(){
		return sceNotaRecebimentoProvisorioRN;
	}

	public SceItemDevolucaoFornecedorDAO getItemDevolucaoFornecedorDAO(){
		return sceItemDevolucaoFornecedorDAO;
	}

	public SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO(){
		return sceItemNotaRecebimentoDAO;
	}

	public SceConversaoUnidMateriaisDAO getSceConversaoUnidMateriaisDAO(){
		return sceConversaoUnidMateriaisDAO;
	}
	
	public SceAlmoxarifadoDAO getSceAlmoxarifadoDAO(){
		return sceAlmoxarifadoDAO;
	}
	
	public SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}
	
	public SceMovimentoMaterialRN getSceMovimentoMaterialRN(){
		return sceMovimentoMaterialRN;
	}
	
	public SceNotaRecebimentoRN getSceNotaRecebimentoRN(){
		return sceNotaRecebimentoRN;
	}
	
	public SceTipoMovimentosRN getSceTipoMovimentosRN(){
		return sceTipoMovimentosRN;
	}
	
	protected IAutFornecimentoFacade getAutForncecimentoFacade() {
		return autFornecimentoFacade;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}