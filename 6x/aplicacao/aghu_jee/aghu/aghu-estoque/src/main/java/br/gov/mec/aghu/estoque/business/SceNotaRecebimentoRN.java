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
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoDocumentoFiscalEntrada;
import br.gov.mec.aghu.estoque.dao.FcpValorTributosDAO;
import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocImpressaoDAO;
import br.gov.mec.aghu.estoque.dao.SceLoteDocumentoDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoItensVO;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FcpValorTributos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class SceNotaRecebimentoRN extends BaseBusiness{

	@EJB
	private SceDocumentoFiscalEntradaRN sceDocumentoFiscalEntradaRN;
	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;
	@EJB
	private SceItemNotaRecebimentoRN sceItemNotaRecebimentoRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	@EJB
	private SceLoteDocumentoRN sceLoteDocumentoRN;
	@EJB
	private FcpValorTributosRN fcpValorTributosRN;
	
	private static final Log LOG = LogFactory.getLog(SceNotaRecebimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceNotaRecebimentoDAO sceNotaRecebimentoDAO;
	
	@Inject
	private SceLoteDocumentoDAO sceLoteDocumentoDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;
	
	@Inject
	private FcpValorTributosDAO fcpValorTributosDAO;
	
	@Inject
	private SceLoteDocImpressaoDAO sceLoteDocImpressaoDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private FcpTituloDAO fcpTituloDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6280584704224323281L;

	public enum SceNotaRecebimentoRNExceptionCode implements BusinessExceptionCode {
		MESSAGEM_ERRO_DATA_FINAL_MENOR_IGUAL_DATA_SITUACAO,MENSAGEM_ERRO_MESMO_FORNECEDOR_DFE_AF,SCE_00704,SCE_00593, SCE_00727, SCE_00728, SCE_00845, SCE_00790, SCE_00301, FASE_NAO_ENCONTRADA, SCE_00789, MENSAGEM_NR_NAO_ENCONTRADA, SCE_NRS_CK5;
	}
	
	/**
	 * Insere Nota de Recebimento com Solicitação de Compra Automática
	 * Obs. Método relacionado com a tarefa #16137 Gerar Nota de Recebimento sem AF
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public void inserirComSolicitacaoDeCompraAutomatica(SceNotaRecebimento notaRecebimento) throws BaseException{
		
		/*
		 * Regras da TRIGGER SCET_NRS_BRI (INSERT)
		 */
		// Seta indicador de debito para falso
		notaRecebimento.setDebitoNotaRecebimento(false); // RN1
		this.atualizarNotaRecebimentoTipoMovimento(notaRecebimento); // RN4 e RN5
		this.atualizarNotaRecebimentoServidorLogadoDataGeracao(notaRecebimento); // RN6

		// Insere Nota de Recebimento
		this.getSceNotaRecebimentoDAO().persistir(notaRecebimento);
		this.getSceNotaRecebimentoDAO().flush();
	}

	/*
	 * Métodos para Inserir SceNotaRecebimento
	 */

	/**
	 * ORADB TRIGGER SCET_NRS_BRI (INSERT)
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	private void preInserir(SceNotaRecebimento notaRecebimento) throws BaseException{

		// Seta indicador de debito para falso
		notaRecebimento.setDebitoNotaRecebimento(false); // RN1
		this.validarNotaRecebimentoFornecedorDocumentoFiscalEntrada(notaRecebimento.getAutorizacaoFornecimento(),notaRecebimento.getDocumentoFiscalEntrada()); // RN2 e RN6
		this.validarNotaRecebimentoAutorizacaoFornecimentoValida(notaRecebimento.getAutorizacaoFornecimento().getNumero()); // RN3
		this.atualizarNotaRecebimentoTipoMovimento(notaRecebimento); // RN4
		this.atualizarNotaRecebimentoServidorLogadoDataGeracao(notaRecebimento); // RN5

		// RN7 Seta indicadores da situação de tributação para falso
		notaRecebimento.setIndTributacao(false); // RN7.1
		notaRecebimento.setIndTribLiberada(false); // RN7.2
	}

	/**
	 * Inserir SceNotaRecebimento
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public SceNotaRecebimento inserir(SceNotaRecebimento notaRecebimento, Boolean flush) throws BaseException{
		this.preInserir(notaRecebimento);
		this.getSceNotaRecebimentoDAO().persistir(notaRecebimento);
		if(flush){
			this.getSceNotaRecebimentoDAO().flush();	
		}
		return notaRecebimento;
	}


	/**
	 * ORADB SCET_NRS_BRU
	 * regras de pre insert em SceNotaRecebimento
	 * @param notaRecebimento
	 * @throws BaseException 
	 */
	private void preAtualizar(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal) throws BaseException {
		
			if(!DateUtil.isDatasIguais(DateUtil.truncaData(notaRecebimento.getDtGeracao()), DateUtil.truncaData(notaRecebimentoOriginal.getDtGeracao())) ||
				!notaRecebimento.getAutorizacaoFornecimento().equals(notaRecebimentoOriginal.getAutorizacaoFornecimento()) ||
				!notaRecebimento.getServidorGeracao().equals(notaRecebimentoOriginal.getServidorGeracao()) ||
				!notaRecebimento.getTipoMovimento().equals(notaRecebimentoOriginal.getTipoMovimento()) ||
				!DateUtil.isDatasIguais(DateUtil.truncaData(notaRecebimento.getDtEstorno()), DateUtil.truncaData(notaRecebimentoOriginal.getDtEstorno())) || 
				(notaRecebimento.getServidorEstorno()!=null && notaRecebimentoOriginal.getServidorEstorno()!=null && !notaRecebimento.getServidorEstorno().equals(notaRecebimentoOriginal.getServidorEstorno()))){

			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00593);

		}
		
		this.validarDebito(notaRecebimento);

		this.verificaEstorno(notaRecebimento,notaRecebimentoOriginal);

		this.atualizarServidorEstornado(notaRecebimento,notaRecebimentoOriginal);

		this.verificarDebito(notaRecebimento,notaRecebimentoOriginal);

		this.atualizarServidorDebito(notaRecebimento,notaRecebimentoOriginal);

		if(!notaRecebimento.getIndTribLiberada().equals(notaRecebimentoOriginal.getIndTribLiberada()) && notaRecebimento.getIndTribLiberada()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			notaRecebimento.setDtLiberacaoTrib(new Date());
			notaRecebimento.setServidorLibTrib(servidorLogado);

			FcpValorTributos valorTributos = getFcpValorTributosDAO().buscarValorTributoPorNotaRecebimento(notaRecebimento);
			valorTributos.setIndLiberado(true);

			getFcpValorTributosRN().atualizar(valorTributos);

			if(!notaRecebimento.getEstorno().equals(notaRecebimentoOriginal.getEstorno()) && notaRecebimento.getEstorno()){
				if(notaRecebimento.getDocumentoFiscalEntrada() != null){
					SceDocumentoFiscalEntrada documentoFiscalEntrada = notaRecebimento.getDocumentoFiscalEntrada();
					documentoFiscalEntrada.setIndSituacao(DominioSituacaoDocumentoFiscalEntrada.R);

					getSceDocumentoFiscalEntradaRN().atualizar(documentoFiscalEntrada);
				}
			}
		}

		this.validarNotaRecebimentoFornecedorDocumentoFiscalEntrada(notaRecebimento.getAutorizacaoFornecimento(),notaRecebimento.getDocumentoFiscalEntrada());

	}
	
	/**
	 * Atualizar SceNotaRecebimento
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public void atualizar(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal, String nomeMicrocomputador, Boolean flush) throws BaseException{
		this.preAtualizar(notaRecebimento, notaRecebimentoOriginal);
		this.getSceNotaRecebimentoDAO().merge(notaRecebimento);
		if(flush){
			this.getSceNotaRecebimentoDAO().flush();	
		}
		this.posAtualizar(notaRecebimento, nomeMicrocomputador);
	}

	/**
	 * ORADB SCEP_ENFORCE_NRS_RULES
	 * @param notaRecebimento
	 *  
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizar(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		atualizarEstorno(notaRecebimento, nomeMicrocomputador);
		atualizarDebito(notaRecebimento, nomeMicrocomputador);
	}

	/**
	 * ORADB RN_NRSP_ATU_SERV_DB
	 * Atualizar servidor de debito
	 * @param notaRecebimento
	 * @param notaRecebimentoOriginal
	 * @throws ApplicationBusinessException  
	 */
	private void atualizarServidorDebito(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal) throws ApplicationBusinessException {
		if(!notaRecebimento.getDebitoNotaRecebimento().equals(notaRecebimentoOriginal.getDebitoNotaRecebimento()) && notaRecebimento.getDebitoNotaRecebimento()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			notaRecebimento.setServidorDebito(servidorLogado);
			notaRecebimento.setDtDebitoNotaRecebimento(new Date());
		}

	}

	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_VER_DEBITO
	 * Verifica se foi alterado o debito
	 * @param notaRecebimento
	 * @param notaRecebimentoOriginal
	 * @throws ApplicationBusinessException 
	 */
	private void verificarDebito(SceNotaRecebimento notaRecebimento,
			SceNotaRecebimento notaRecebimentoOriginal) throws ApplicationBusinessException {
		if(!notaRecebimento.getDebitoNotaRecebimento() && notaRecebimentoOriginal.getDebitoNotaRecebimento()){
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00790);
		}
	}

	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_ATU_SERV_EST
	 * Atualizar servidor de estorno
	 * @param notaRecebimento
	 * @param notaRecebimentoOriginal
	 * @throws ApplicationBusinessException  
	 */
	private void atualizarServidorEstornado(SceNotaRecebimento notaRecebimento,
			SceNotaRecebimento notaRecebimentoOriginal) throws ApplicationBusinessException {

		if(!notaRecebimento.getEstorno().equals(notaRecebimentoOriginal.getEstorno()) && notaRecebimento.getEstorno()){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			notaRecebimento.setDtEstorno(new Date());
			notaRecebimento.setServidorEstorno(servidorLogado);
		}
		//notaRecebimento.setServidorGeracao(getSceReqMateriaisRN().atualizarSceReqMateriaisServidorEfetivadoLogado());
	}

	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_VER_ESTORNO
	 * Verifica se NR já está estornada e se NR com títulos a pagar já pagos.
	 * @param notaRecebimento
	 * @param notaRecebimentoOriginal
	 * @throws ApplicationBusinessException 
	 */
	private void verificaEstorno(SceNotaRecebimento notaRecebimento, SceNotaRecebimento notaRecebimentoOriginal) throws ApplicationBusinessException {
		if(notaRecebimentoOriginal.getEstorno() && notaRecebimento.getNotaRecebProvisorio() == null){
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00727);
		}

		if(notaRecebimento.getEstorno() && notaRecebimentoOriginal.getEstorno() == null){
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00728);
		}

		if(verificarTitulosPagos(notaRecebimento) && notaRecebimento.getEstorno()){
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00845);
		}

	}

	/**
	 * ORADB SCEK_NRS_RN.NRSC_VER_TIT_PAGO
	 * Método que retorna true se há títulos pagos da NR
	 * @param notaRecebimento
	 * @return
	 */
	private boolean verificarTitulosPagos(SceNotaRecebimento notaRecebimento) {
		List<FcpTitulo> titulos = getFcpTituloDAO().pesquisarTitulosPagosPorNotaRecebimento(notaRecebimento);
		if(titulos != null && !titulos.isEmpty()){
			return true;
		}
		return false;
	}



	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_ATU_DEBITO
	 * Atualizar o debito das notas de recebimento
	 * @param notaRecebimento
	 */
	private void atualizarDebito(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		if(notaRecebimento.getDebitoNotaRecebimento()){
			AghParametros paramCompetencia =  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
			SceMovimentoMaterial movimentoMaterial = getSceMovimentoMaterialDAO().obterMovimentoMaterialPorNotaRecebimento(notaRecebimento);
			if(movimentoMaterial != null && DateUtil.isDatasIguais(DateUtil.truncaData(paramCompetencia.getVlrData()), DateUtil.truncaData(movimentoMaterial.getDtCompetencia()))){
				throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00789);
			}

			atualizarMovimentoDebito(notaRecebimento, nomeMicrocomputador);
		}		
	}

	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_ATU_MVTO_DB
	 * Gera movimento p/ cada item da NR, com tipo de movimento igual a débito de NR.
	 * @param notaRecebimento
	 */
	private void atualizarMovimentoDebito(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		List<SceItemNotaRecebimento> itens = getSceItemNotaRecebimentoDAO().pesquisarItensNotaRecebimentoPorNotaRecebimento(notaRecebimento);
		Boolean localizouFase = false;
		ScoFaseSolicitacao faseEncontrada = null;
		Integer novaQuantidade = 0;
		ScoNomeComercial nomeComercial = null;
		for(SceItemNotaRecebimento item : itens){
			if(item.getIndDebitoNrIaf()){
				continue;
			}
			novaQuantidade = item.getQuantidade();
			ScoItemAutorizacaoFornId itemAutorizacaoId = item.getItemAutorizacaoForn()!=null?item.getItemAutorizacaoForn().getId():null;

			if(itemAutorizacaoId != null){
				List<ScoFaseSolicitacao> listfaseSolicitacao = this.getComprasFacade().pesquisaItensAutPropostaFornecedor(itemAutorizacaoId);
				for(ScoFaseSolicitacao fase : listfaseSolicitacao){
					if(!fase.getExclusao() || !fase.getSolicitacaoDeCompra().getExclusao()){
						continue;
					}
					localizouFase = true;
					faseEncontrada = fase;
				}

				if(!localizouFase){
					throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.FASE_NAO_ENCONTRADA);
				}else{
					
					if(item.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial() != null){
						nomeComercial = item.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial();
					}

					ScoFornecedor fornHcpa = getComprasFacade().obterFornecedorPorNumero(1);

					String historico = getSceItemNotaRecebimentoRN().montaHistorico(item, item.getItemAutorizacaoForn().getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor().getNumero(), nomeComercial.getId().getMcmCodigo(), nomeComercial.getId().getNumero());
					if(getSceItemNotaRecebimentoRN().validaMovtoEstoqueMaterial(item, item.getItemAutorizacaoForn(), notaRecebimento.getDtGeracao(), item.getUnidadeMedida())){
						AghParametros parametroDebito =  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_MOV_DB_NR);
						SceTipoMovimento tipoMovimentoDebito = getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(parametroDebito.getVlrNumerico().shortValue());

						getSceMovimentoMaterialRN().atualizarMovimentoMaterial(faseEncontrada.getSolicitacaoDeCompra().getMaterial().getAlmoxarifado(),
								item.getMaterial(),faseEncontrada.getSolicitacaoDeCompra().getUnidadeMedida(), novaQuantidade,null,Boolean.FALSE,tipoMovimentoDebito,
								notaRecebimento.getTipoMovimento(),null,null,historico,fornHcpa,null,null,null,
								new BigDecimal(item.getValor()),notaRecebimento.getSeq(), nomeMicrocomputador, true);

					}

					atualizarLote(notaRecebimento,item.getItemAutorizacaoForn());
				}				
			}

			getSceItemNotaRecebimentoRN().atualizaIaf(item, faseEncontrada.getSolicitacaoDeCompra(), 
					item.getItemAutorizacaoForn(),Boolean.TRUE,null);			
		}

	}

	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_ATU_ESTORNO
	 * Realiza atualizacoes e estornos
	 * @param notaRecebimento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	private void atualizarEstorno(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador)
	throws BaseException {
		if(notaRecebimento.getEstorno()){
			AghParametros paramCompetencia =  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

			SceMovimentoMaterial movimentoMaterial = getSceMovimentoMaterialDAO().obterMovimentoMaterialPorNotaRecebimento(notaRecebimento);

			if(movimentoMaterial != null && !DateUtil.isDatasIguais(DateUtil.truncaData(paramCompetencia.getVlrData()), DateUtil.truncaData(movimentoMaterial.getId().getDtCompetencia()))){
				throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00301);
			}

			atualizarMovimentoEstorno(notaRecebimento, nomeMicrocomputador);		
			//getFcpTituloRN().atualizarEstornoTitulos(notaRecebimento);
		}
	}


	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_ATU_MVTO_EST
	 * Lançar o movimento de estorno de cada material.
	 * @param notaRecebimento
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void atualizarMovimentoEstorno(SceNotaRecebimento notaRecebimento, String nomeMicrocomputador) throws BaseException {
		List<SceItemNotaRecebimento> itens = getSceItemNotaRecebimentoDAO().pesquisarItensNotaRecebimentoPorNotaRecebimento(notaRecebimento);
		Boolean localizouFase = false;
		ScoFaseSolicitacao faseEncontrada = null;
		Integer novaQuantidade = 0;
		ScoNomeComercial nomeComercial = null;
		for(SceItemNotaRecebimento item : itens){
			if(item.getIndDebitoNrIaf()){
				continue;
			}
			novaQuantidade = item.getQuantidade();
			ScoItemAutorizacaoFornId itemAutorizacaoId = item.getItemAutorizacaoForn()!=null?item.getItemAutorizacaoForn().getId():null;

			if(itemAutorizacaoId != null){
				List<ScoFaseSolicitacao> listfaseSolicitacao = this.getComprasFacade().pesquisaItensAutPropostaFornecedor(itemAutorizacaoId);
				for(ScoFaseSolicitacao fase : listfaseSolicitacao){
					//					if(!fase.getExclusao() || !fase.getSolicitacaoDeCompra().getExclusao()){
					//						continue;
					//					}
					localizouFase = true;
					faseEncontrada = fase;
				}

				if(!localizouFase){
					throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.FASE_NAO_ENCONTRADA);
				}

				if(faseEncontrada.getSolicitacaoDeCompra() !=null && faseEncontrada.getSolicitacaoDeCompra().getMaterial()!=null && faseEncontrada.getSolicitacaoDeCompra().getMaterial().getGrupoMaterial()!=null){

					if(faseEncontrada.getSolicitacaoDeCompra().getMaterial().getGrupoMaterial().getGeraMovEstoque() || faseEncontrada.getSolicitacaoDeCompra().getMaterial().getGrupoMaterial().getCodigo() == 21){
						if(item.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial() != null){
							nomeComercial = item.getItemAutorizacaoForn().getItemPropostaFornecedor().getNomeComercial();
						}

						ScoFornecedor fornHcpa = getComprasFacade().obterFornecedorPorNumero(1);

						//TODO testar isso beeem testado! by massafra
						
						
						Integer numeroNomeComercial = nomeComercial!=null?nomeComercial.getId().getNumero():null;
						Integer codigoNomeComercial = nomeComercial!=null?nomeComercial.getId().getMcmCodigo():null;
												
						String historico = getSceItemNotaRecebimentoRN().montaHistorico(item, item.getItemAutorizacaoForn().getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor().getNumero(), codigoNomeComercial, numeroNomeComercial);
					if(getSceItemNotaRecebimentoRN().validaMovtoEstoqueMaterial(item, item.getItemAutorizacaoForn(), notaRecebimento.getDtGeracao(), item.getUnidadeMedida())){

							getSceMovimentoMaterialRN().atualizarMovimentoMaterial(faseEncontrada.getSolicitacaoDeCompra().getMaterial().getAlmoxarifado(),
									item.getMaterial(),faseEncontrada.getSolicitacaoDeCompra().getUnidadeMedida(), novaQuantidade,null,Boolean.TRUE,notaRecebimento.getTipoMovimento(),null,notaRecebimento.getSeq(),null,historico,
									fornHcpa,null,null,null,new BigDecimal(item.getValor()),null, nomeMicrocomputador, true);

						}

						atualizarLote(notaRecebimento,item.getItemAutorizacaoForn());
					}			
				}
			}

			getSceItemNotaRecebimentoRN().atualizaIaf(item, faseEncontrada.getSolicitacaoDeCompra(), 
					item.getItemAutorizacaoForn(),Boolean.TRUE,item.getNotaRecebimento().getNotaRecebProvisorio());			
		}
	}

	/**
	 * ORADB SCEK_NRS_RN.RN_NRSP_ATU_LOTE
	 * Somente permite excluir LDC (Lote X Documentos) se não existir Etiqueta já Impressa (Medimentos).
	 * @param notaRecebimento,itemAutorizacaoForn
	 * @throws BaseException
	 */
	public void atualizarLote(SceNotaRecebimento notaRecebimento,
			ScoItemAutorizacaoForn itemAutorizacaoForn) throws BaseException{

		List<SceLoteDocumento> loteDocumentos = null;
		Boolean acessaLdcpAtuDelecao = getSceLoteDocumentoDAO().pesquisarLoteDocumentoPorInrNrsSeq(notaRecebimento.getSeq());
		List<SceLoteDocImpressao> loteImpressao = getSceLoteDocImpressaoDAO().pesquisarLoteDocImpressaoPorInrNrsSeq(notaRecebimento.getSeq());
		if(acessaLdcpAtuDelecao){
			if(loteImpressao == null || loteImpressao.isEmpty()){
				loteDocumentos = getSceLoteDocumentoDAO().pesquisarLoteDocumentoPorNotaItemAutorizacao(notaRecebimento,itemAutorizacaoForn);
				for(SceLoteDocumento lote : loteDocumentos){
					getSceLoteDocumentoRN().remover(lote);
				}
			}
		}
	}

	/*
	 * RNs Inserir
	 */

	/**
	 * ORADB PROCEDURE SCEK_NRS_RN.RN_NRSP_VER_FORN_DFE e SCEK_NRS_RN.RN_NRSP_VER_DF
	 * Valida se o fornecedor da nota de recebimento é igual ao do documento fiscal de entrada
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	public void validarNotaRecebimentoFornecedorDocumentoFiscalEntrada(ScoAutorizacaoForn autorizacaoForn, SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException{

		// Obtem o parametro com o código da forma de pagamento para IMPORTAÇÃO
		AghParametros parametroFormaPagamentoImportacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORMA_PGTO_IMPORTACAO);

		
		final Integer numeroAutorizacaoForn = autorizacaoForn.getNumero();
		final Boolean isExisteAutorizacaoFornNotaImportacao = this.getAutFornecimentoFacade().existeAutorizacaoFornecimentoNotaImportacao(numeroAutorizacaoForn, parametroFormaPagamentoImportacao.getVlrNumerico().shortValue());

		// Caso não existam notas de importação
		if(!isExisteAutorizacaoFornNotaImportacao){

			final ScoFornecedor fornecedorAutorizacaoFornecimento = autorizacaoForn.getPropostaFornecedor().getFornecedor();
			final ScoFornecedor fornecedorDocumentoFiscalEntrada = documentoFiscalEntrada.getFornecedor();

			// Verifica se a autorização de fornecimento possuí o mesmo fornecedor do documento fiscal de entrada
			if(!this.compararCgcFornecedor(fornecedorAutorizacaoFornecimento, fornecedorDocumentoFiscalEntrada) 
					&& !fornecedorAutorizacaoFornecimento.equals(fornecedorDocumentoFiscalEntrada)){
				throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.MENSAGEM_ERRO_MESMO_FORNECEDOR_DFE_AF, documentoFiscalEntrada.getNumero(), autorizacaoForn.getNumero() +"/"+autorizacaoForn.getNroComplemento());
			}	

		}

	}
	
	/**
	 * Compara o CGC entre dois fornecedores
	 * @param fornecedor1
	 * @param fornecedor2
	 * @return
	 */
	private Boolean compararCgcFornecedor(ScoFornecedor fornecedor1, ScoFornecedor fornecedor2){

		CoreUtil.validaParametrosObrigatorios(fornecedor1, fornecedor2);

		final String cgcFornecedorAutorizacaoFornecimento = fornecedor1.getCgc() != null ? fornecedor1.getCgc().toString().substring(0,8) : null;
		final String cgcFornecedorDocumentoFiscalEntrada = fornecedor2.getCgc() != null ? fornecedor2.getCgc().toString().substring(0,8) : null;

		if (cgcFornecedorAutorizacaoFornecimento != null && cgcFornecedorDocumentoFiscalEntrada != null) {
			return cgcFornecedorAutorizacaoFornecimento.equals(cgcFornecedorDocumentoFiscalEntrada);
		}

		return cgcFornecedorAutorizacaoFornecimento == cgcFornecedorDocumentoFiscalEntrada;

	}

	/**
	 * ORADB PROCEDURE SCEK_NRS_RN.RN_NRSP_VER_AF_VALID
	 * Verifica a validade da nota de recebimento
	 * @param notaRecebimento
	 * @throws ApplicationBusinessException
	 */
	protected void validarNotaRecebimentoAutorizacaoFornecimentoValida(Integer numeroAutorizacaoFornecimento) throws ApplicationBusinessException{

		CoreUtil.validaParametrosObrigatorios(numeroAutorizacaoFornecimento);

		// Pesquisa autorizações de fornecimento com situação válida para inserção
		List<ScoAutorizacaoForn> listaAutorizacaoForn = this.getAutFornecimentoFacade().pesquisarAutorizacaoFornecimentoValidasInsercao(numeroAutorizacaoFornecimento);

		if(listaAutorizacaoForn != null && listaAutorizacaoForn.isEmpty()){
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_00704);
		}

	}

	/**
	 * Atualizar o tipo de movimento da nota de recebimento
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	protected void atualizarNotaRecebimentoTipoMovimento(SceNotaRecebimento notaRecebimento) throws BaseException{

		AghParametros parametroTipoMovimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);
		final SceTipoMovimento tipoMovimentos = this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(parametroTipoMovimento.getVlrNumerico().shortValue());

		notaRecebimento.setTipoMovimento(tipoMovimentos);

	}

	/**
	 * ORADB PROCEDURE SCEK_NRS_RN.RN_NRSP_ATU_GERACAO
	 * Atualiza servidor com o usuário logado e data de geração com a data atual
	 * @param material
	 * @throws ApplicationBusinessException  
	 */
	protected void atualizarNotaRecebimentoServidorLogadoDataGeracao(SceNotaRecebimento notaRecebimento) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		notaRecebimento.setServidorGeracao(servidorLogado);
		notaRecebimento.setDtGeracao(new Date());
	}


	/*
	 * RNs Atualizar
	 */

	public NotaRecebimentoVO pesquisaDadosNotaRecebimento(Integer numNotaRec, boolean isConsiderarNotaEmpenho)throws BaseException{
		
		BigDecimal frnNumeroHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NUMERO_FORNECEDOR_HU).getVlrNumerico();
		BigDecimal tmvNR_seq = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR).getVlrNumerico();
		NotaRecebimentoVO notaRecebimento = null;
		
		final AghParametros parametro =  getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_COMPRAS_ATIVO);

		// O Módulo de Compras do AGHU está INATIVO quando valor numérico do parâmetro for igual a ZERO 
		if (parametro != null && BigDecimal.ZERO.equals(parametro.getVlrNumerico())) {
		
			notaRecebimento = getSceNotaRecebimentoDAO().pesquisaDadosNotaRecebimentoSemAf(numNotaRec, tmvNR_seq.shortValue());
		
		} else {
			
			notaRecebimento = getSceNotaRecebimentoDAO().pesquisaDadosNotaRecebimento(numNotaRec, tmvNR_seq.shortValue(), isConsiderarNotaEmpenho);
			
		}
		
		if (notaRecebimento != null) {
			
			List<NotaRecebimentoItensVO> itensNotaRecebimento = getSceItemNotaRecebimentoDAO().buscaItensNotaRecebimentoImpressaoUnion(numNotaRec, frnNumeroHcpa.intValue());
			notaRecebimento.setItensNotaRecebimento(itensNotaRecebimento);	
		
		}else {
			
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.MENSAGEM_NR_NAO_ENCONTRADA);
		
		}

		return notaRecebimento;
		
	}
	
	public Boolean habilitarCampoDataFinalConsultarNotaRecebimento(Date dataSituacao) {
		return dataSituacao != null;
	}
	
	/**
	 * ORADB Restrição SCE_NRS_CK5
	 * @param notaRecebimento
	 * @throws BaseException
	 */
	private void validarDebito(SceNotaRecebimento notaRecebimento) throws BaseException {
		
		if (notaRecebimento.getDebitoNotaRecebimento() && notaRecebimento.getEstorno()) {
			
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.SCE_NRS_CK5);
			
		}
		
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

	protected SceItemNotaRecebimentoDAO getSceItemNotaRecebimentoDAO(){
		return sceItemNotaRecebimentoDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	

	public FcpTituloDAO getFcpTituloDAO() {
		return fcpTituloDAO;
	}

	public void setFcpTituloDAO(FcpTituloDAO fcpTituloDAO) {
		this.fcpTituloDAO = fcpTituloDAO;
	}

	protected FcpValorTributosDAO getFcpValorTributosDAO(){
		return fcpValorTributosDAO;
	}

	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}

	protected SceTipoMovimentosRN getSceTipoMovimentosRN(){
		return sceTipoMovimentosRN;
	}

	protected SceDocumentoFiscalEntradaRN getSceDocumentoFiscalEntradaRN(){
		return sceDocumentoFiscalEntradaRN;
	}

	protected FcpValorTributosRN getFcpValorTributosRN(){
		return fcpValorTributosRN;
	}

	protected SceItemNotaRecebimentoRN getSceItemNotaRecebimentoRN(){
		return sceItemNotaRecebimentoRN;
	}

	protected SceLoteDocumentoDAO getSceLoteDocumentoDAO(){
		return sceLoteDocumentoDAO;
	}

	protected SceLoteDocImpressaoDAO getSceLoteDocImpressaoDAO(){
		return sceLoteDocImpressaoDAO;
	}

	protected SceLoteDocumentoRN getSceLoteDocumentoRN(){
		return sceLoteDocumentoRN;
	}

	public SceMovimentoMaterialRN getSceMovimentoMaterialRN(){
		return sceMovimentoMaterialRN;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade(){
		return autFornecimentoFacade;
	}

	public void verificaDataSituacaoInicialDataSituacaoFinal(Date dataSituacao, Date dataFinal) throws ApplicationBusinessException {
		if (dataSituacao != null && dataFinal != null && !dataFinal.after(dataSituacao)) {
			throw new ApplicationBusinessException(SceNotaRecebimentoRNExceptionCode.MESSAGEM_ERRO_DATA_FINAL_MENOR_IGUAL_DATA_SITUACAO);
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
