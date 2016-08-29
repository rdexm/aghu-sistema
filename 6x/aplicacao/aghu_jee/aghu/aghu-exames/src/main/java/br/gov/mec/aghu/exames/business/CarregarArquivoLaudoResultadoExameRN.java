package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelDocResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicConsultadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicConsultadoHistDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicConsultadoHist;
import br.gov.mec.aghu.model.AelItemSolicConsultadoHistId;
import br.gov.mec.aghu.model.AelItemSolicConsultadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class CarregarArquivoLaudoResultadoExameRN extends BaseBusiness {

	@EJB
	private AelItemSolicConsultadoRN aelItemSolicConsultadoRN;
	
	@EJB
	private AelAmostrasRN aelAmostrasRN;
	
	@EJB
	private AelAmostraItemExamesRN aelAmostraItemExamesRN;
	
	private static final Log LOG = LogFactory.getLog(CarregarArquivoLaudoResultadoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Inject
	private AelItemSolicConsultadoHistDAO aelItemSolicConsultadoHistDAO;
	
	@Inject
	private AelItemSolicConsultadoDAO aelItemSolicConsultadoDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelDocResultadoExameDAO aelDocResultadoExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 38068247043786394L;

	public enum LaudoResultadoExameRNExceptionCode implements BusinessExceptionCode {		
		MSG_USO_SISTEMA_LAUDO;
	}

	/**
	 * Obtem a descricao da unidade funcional solicitante
	 * @param seq
	 * @return
	 * @throws BaseException
	 */
	public String obterDescricaoVAghUnidFuncional(Short seq) throws BaseException {
		return getAghuFacade().obterDescricaoVAghUnidFuncional(seq);
	}
	
	/**
	 * Pesquisa itens de solicitação de exame com a situacao AE (Area executora) ou LI (Liberado)
	 * @param unidadeExecutora
	 * @param solicitacaoExame
	 * @param seqp
	 * @return
	 * @throws BaseException
	 */
	public List<AelItemSolicitacaoExames> pesquisarCarregarArquivoLaudoResultadoExame(AghUnidadesFuncionais unidadeExecutora, AelSolicitacaoExames solicitacaoExame, Short amostraSeqp) throws BaseException {

		// Verifica se exite a ocorrencia de uma solicitacao de exame em VAelArcoSolicitacao
		final boolean isExisteAelAmostrasPorSolicitacaoExame = this.getAelSolicitacaoExameDAO().existeAelAmostrasPorSolicitacaoExame(solicitacaoExame);
		
		// Verifica se o material de analise NAO e coletavel
		boolean isNaoColetavel = false;
		for (AelItemSolicitacaoExames  i : solicitacaoExame.getItensSolicitacaoExame()) {
			if (!i.getMaterialAnalise().getIndColetavel()){
				isNaoColetavel =  true;
				break;
			}
		}
		
		// Caso existam amostras por solicitacao de exame ou o material de analise nao e coletavel
		if(isExisteAelAmostrasPorSolicitacaoExame || isNaoColetavel){ 
			
			// Pesquisa os itens de solicitacao de exame e considera o numero da amostra
			List<AelItemSolicitacaoExames> listaOriginal = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSituacaoAreaExecutoraLiberado(solicitacaoExame.getSeq(), amostraSeqp, unidadeExecutora);
			// Instancia uma lista de retorno filtrada, que remove os itens duplicados atraves da solicitacao e sequencial
			List<AelItemSolicitacaoExames> listaRetornoFiltrada = new LinkedList<AelItemSolicitacaoExames>();
			
			/*
			 * Filtra os resultados duplicados da lista original 
			 * e acrescenta os itens na lista filtrada de retorno
			 * Obs. Essa medida foi tomada para "simular o comportamento" 
			 * da tela de carregamento de laudo baseada de DELPHI
			 */
			for (AelItemSolicitacaoExames itemListaOriginal : listaOriginal) {
				if(!listaRetornoFiltrada.contains(itemListaOriginal)){
					listaRetornoFiltrada.add(itemListaOriginal);
				}
			}
			
			return listaRetornoFiltrada;
			
		}

		return null;
	}
	
	/**
	 * Pesquisa ou realiza um extrato dos exames consultados
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExames(Integer iseSoeSeq, Short iseSeqp) {
		 return getAelItemSolicConsultadoRN().pesquisarAelItemSolicConsultadosResultadosExames(iseSoeSeq, iseSeqp);
	}
	
	/**
	 * Pesquisa ou realiza um extrato dos exames consultados
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public List<AelItemSolicConsultadoVO> pesquisarAelItemSolicConsultadosResultadosExamesHist(Integer iseSoeSeq, Short iseSeqp) {
		 return getAelItemSolicConsultadoRN().pesquisarAelItemSolicConsultadosResultadosExamesHist(iseSoeSeq, iseSeqp);
	}
	
	/**
	 * Persiste visualizacao ou download de anexo 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @throws ApplicationBusinessException  
	 */
	public void persistirVisualizacaoDownloadAnexo(Integer iseSoeSeq, Short iseSeqp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Instancia ID e popula
		AelItemSolicConsultadoId id = new AelItemSolicConsultadoId();
		
		// Seta dados do item de solicitacao
		id.setIseSoeSeq(iseSoeSeq);
		id.setIseSeqp(iseSeqp);
		
		// Atualiza o servidor que esta criando de acordo com o usuario logado
		id.setSerMatricula(servidorLogado.getId().getMatricula());
		id.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		
		// Seta "criado em" com a data e hora atual.
		id.setCriadoEm(new Date());
		
		// Intancia um item de solicitacao consultado
		AelItemSolicConsultado itemSolicConsultado = new AelItemSolicConsultado();
		itemSolicConsultado.setId(id);
		
		// Insere dados
		getAelItemSolicConsultadoRN().inserir(itemSolicConsultado, true);
	}
	
	
	/**
	 * Persiste visualizacao ou download de anexo em base historico.
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @throws ApplicationBusinessException  
	 */
	public void persistirVisualizacaoDownloadAnexoHist(Integer iseSoeSeq, Short iseSeqp) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Instancia ID e popula
		AelItemSolicConsultadoHistId id = new AelItemSolicConsultadoHistId();
		
		// Seta dados do item de solicitacao
		id.setIseSoeSeq(iseSoeSeq);
		id.setIseSeqp(iseSeqp);
		
		// Atualiza o servidor que esta criando de acordo com o usuario logado
		id.setSerMatricula(servidorLogado.getId().getMatricula());
		id.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		
		// Seta "criado em" com a data e hora atual.
		id.setCriadoEm(new Date());
		
		// Intancia um item de solicitacao consultado
		AelItemSolicConsultadoHist itemSolicConsultadoHist = new AelItemSolicConsultadoHist();
		itemSolicConsultadoHist.setId(id);
		
		// Insere dados
		getAelItemSolicConsultadoHistDAO().persistir(itemSolicConsultadoHist);
		getAelItemSolicConsultadoHistDAO().flush();
	}
	
	/**
	 * Verifica a existencia de um documento de resultado de exames
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public Boolean existeDocumentoAnexado(Integer iseSoeSeq, Short iseSeqp) {
		return getAelDocResultadoExameDAO().existeDocumentoAnexado(iseSoeSeq, iseSeqp);
	}
	
	/** Pesquisa resultados de exames consultados
	 * Obtem um documento de resultado de exames
	 * @param iseSoeSeq
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public AelDocResultadoExame obterDocumentoAnexado(Integer iseSoeSeq, Short iseSeqp) {
		return getAelDocResultadoExameDAO().obterDocumentoAnexado(iseSoeSeq, iseSeqp);
	}
	
	/**
	 * PROCEDURE DE INSERT
	 */
	
	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * Inserir
	 */
	public void inserirAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador) throws BaseException{
		// Seta a anulacao do documento para falso
		doc.setIndAnulacaoDoc(false);
		
		// Atualiza o servidor que esta criando de acordo com o usuario logado.
		this.atualizarServidorLogadoCriadoEm(doc);
		
		// Inserir no banco o documento de laudo
		this.getAelDocResultadoExameDAO().persistir(doc);
		this.getAelDocResultadoExameDAO().flush();

		// Resgata item de solicitacao de exame do documento
		AelItemSolicitacaoExames itemSolicitacaoExame = doc.getItemSolicitacaoExame();
		
		// Atualiza o status do item de solicitacao de exame para LIBERADO
		this.atualizarAelItemSolicitacaoExamesSituacaoLiberado(itemSolicitacaoExame);

		// Altera amostras do item de exame de recebido para executando
		this.atualizarAelAmostraItemExamesRecebidoParaExecutado(itemSolicitacaoExame.getAelAmostraItemExames());
		
		// UPDATE/Atualiza no banco item de solicitacao de exame
		this.getSolicitacaoExameFacade().atualizar(itemSolicitacaoExame, nomeMicrocomputador);
		
		// Integracao com as triggers de AELP_ENFORCE_AIE_RULES
		this.posAtualizartAelAmostraItemExames(itemSolicitacaoExame, nomeMicrocomputador);
		
		//Verifica se todos os itens de solicitacao de exame CONTEM documento anexo
		this.atualizarAmostras(itemSolicitacaoExame, unidadeExecutora,false);
		
	}
	
	
	/**
	 * TRIGGERS DE INSERT
	 */
	
	/**
	 * ORADB AELT_DRE_BRI
	 * Atualiza o servidor que esta criando de acordo com o usuario logado.
	 * Seta "criado em" com a data e hora atual.
	 * @param docResultadoExames
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarServidorLogadoCriadoEm(AelDocResultadoExame docResultadoExames) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Atualiza o servidor que esta criando de acordo com o usuario logado
		docResultadoExames.setServidor(servidorLogado);
		
		// Seta "criado em" com a data e hora atual
		docResultadoExames.setCriadoEm(new Date());
		
	}
	
	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * Atualiza o status do item de solicitacao de exame para LIBERADO
	 * @throws BaseException 
	 */
	public void atualizarAelItemSolicitacaoExamesSituacaoLiberado(AelItemSolicitacaoExames itemSolicitacaoExames) throws BaseException{
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AelSitItemSolicitacoes vIseSituacao = this.getAelSitItemSolicitacoesDAO().obterPeloId(parametro.getVlrTexto());
		itemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);
	}
	
	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * Atualizar AEL_AMOSTRA_ITEM_EXAMES onde for Recebido para Executado
	 */
	public void atualizarAelAmostraItemExamesRecebidoParaExecutado(List<AelAmostraItemExames> listaAmostras){
		if(listaAmostras != null){
			for (AelAmostraItemExames item : listaAmostras) {
				if(DominioSituacaoAmostra.R.equals(item.getSituacao())){
					item.setSituacao(DominioSituacaoAmostra.E);
				}
				
			}	
		}
	}
	
	/**
	 * PROCEDURE DE DELETE/UPDATE
	 */
	
	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * Inserir
	 */
	public void removerAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador) throws BaseException{
		// Seta o indicador de anulacao de documento para verdadeiro 
		doc.setIndAnulacaoDoc(Boolean.TRUE);
		
		// Atualiza o servidor anulacao de acordo com o usuario logado
		this.atualizarServidorAnulacao(doc);
		
		// Atualiza (delete) documento de laudo no banco
		this.getAelDocResultadoExameDAO().atualizar(doc);
		
		// Resgata item de solicitacao de exame do documento
		AelItemSolicitacaoExames itemSolicitacaoExame = doc.getItemSolicitacaoExame();
		
		//  Atualiza o status do item de solicitacao de exame para AREA EXECUTORA
		this.atualizarAelItemSolicitacaoExamesSituacaoAreaExecutora(itemSolicitacaoExame);
		
		// UPDATE/Atualiza no banco item de solicitacao de exame
		this.getSolicitacaoExameFacade().atualizarSemFlush(itemSolicitacaoExame, nomeMicrocomputador, true);
		
		// Integracao com as triggers de AELP_ENFORCE_AIE_RULES
		this.posAtualizartAelAmostraItemExames(itemSolicitacaoExame, nomeMicrocomputador);
		
		// Verifica se todos os itens de solicitacao de exame NAO contem documento anexo
		this.atualizarAmostras(itemSolicitacaoExame, unidadeExecutora, true);
		
	}
	

	/**
	 * TRIGGERS DE DELETE/UPDATE
	 */
	
	/**
	 * ORADB TRIGGER AELT_DRE_BRU (AEL_DOC_RESULTADO_EXAMES)
	 * Atualiza o servidor anulacao de acordo com o usuario logado.
	 * Seta "data e hora" da anulacao com a data e hora atual.
	 * @param docResultadoExames
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarServidorAnulacao(AelDocResultadoExame docResultadoExames) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Atualiza o servidor anulacao de acordo com o usuario logado
		docResultadoExames.setServidorAnulacao(servidorLogado);
		
		// Seta "data e hora" da anulacao com a data e hora atual
		docResultadoExames.setDthrAnulacaoDoc(new Date());
		
	}
	
	
	/**
	 * ORADB aelk_ael_rn.rn_aelp_atu_servidor
	 * Atualiza o status do item de solicitacao de exame para AREA EXECUTORA
	 * @throws BaseException 
	 */
	public void atualizarAelItemSolicitacaoExamesSituacaoAreaExecutora(AelItemSolicitacaoExames itemSolicitacaoExames) throws BaseException{
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AelSitItemSolicitacoes vIseSituacao = this.getAelSitItemSolicitacoesDAO().obterPeloId(parametro.getVlrTexto());
		itemSolicitacaoExames.setSituacaoItemSolicitacao(vIseSituacao);
	}
	
	/**
	 * ORADB AELT_AIE_ASU - AELP_ENFORCE_AIE_RULES (AEL_AMOSTRA_ITEM_EXAMES)
	 * @param doc
	 * @throws BaseException
	 */
	private void posAtualizartAelAmostraItemExames(AelItemSolicitacaoExames itemSolicitacaoExame, String nomeMicrocomputador) throws BaseException {
		
		List<AelAmostraItemExames> list = itemSolicitacaoExame.getAelAmostraItemExames();
		
		for (AelAmostraItemExames amostra : list) {
			this.getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(amostra, true, false, nomeMicrocomputador);
		}
		
		
	}

	/**
	 * Atualiza amostra para o status EXECUTADO 
	 * caso ambos todos os itens de amostra contenham documentos anexados
	 * @param doc
	 * @param unidadeExecutora
	 * @param amostraSeqp
	 * @return
	 * @throws BaseException
	 */
	public void atualizarAmostras(AelItemSolicitacaoExames itemSolicitacao, AghUnidadesFuncionais unidadeExecutor, boolean isRemoverLaudo) throws BaseException {

	
		List<AelAmostraItemExames> list =  this.getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(itemSolicitacao);

		// Instancia uma lista contendo as amostras da amostra de item de exame
		List<AelAmostras> amostras = new ArrayList<AelAmostras>();
		
		// Popula as amostras
		for (AelAmostraItemExames itemExames: list) {
			amostras.add(itemExames.getAelAmostras());
		}
		
		// Percorre todas as amostras dos itens de amostra
		for (AelAmostras amostra : amostras) {
			
			List<AelAmostraItemExames> listaTodosAmostraItensExames =  this.getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorAmostra(amostra.getSolicitacaoExame().getSeq(), amostra.getId().getSeqp().intValue());
			
			if(listaTodosAmostraItensExames != null && !listaTodosAmostraItensExames.isEmpty()){
				
				// Obtem a quantidade de amostra itens exame da AMOSTRA ATUAL
				int tamanhoLista = listaTodosAmostraItensExames.size();
				/* 
				 * Acumulador que contabiliza amostras na situacao de origem
				 * para atualizacao da AMOSTRA ATUAL. 
				 * Vide: Quando um laudo for inserido a situacao de origem desejada sera E (Executada),
				 * logo a AMOSTRA ATUAL sera atualizada quando todos itens de amostra estiverem nessa situacao
				 */
				int contaAmostrasSituacaoOrigem = 0; 
				
				for (AelAmostraItemExames amostraItemExames : listaTodosAmostraItensExames) {
					
					if(isRemoverLaudo){
						/* 
						 * Quando removido um laudo a ocorrencia de 1 item de amostra diferente de
						 * E (Executada) faz com que a AMOSTRA ATUAL passe para situacao R (Recebida)
						 */
						if(!amostraItemExames.getSituacao().equals(DominioSituacaoAmostra.E)){
							contaAmostrasSituacaoOrigem++;
						}
						
					} else{
						// Na insercao de laudo serao contabilizados os itens de amostrana situacao E (Executada)
						if(amostraItemExames.getSituacao().equals(DominioSituacaoAmostra.E)){
							contaAmostrasSituacaoOrigem++;
						}
					}
					
				}
				
				// Testa a existencia de documentos anexos em ambos itens de amostra
				if(contaAmostrasSituacaoOrigem == tamanhoLista){
					// Atualiza a AMOSTRA ATUAL de acordo com o tipo de operacao (Insercao ou Remocao de laudo)
					amostra.setSituacao(isRemoverLaudo ? DominioSituacaoAmostra.R : DominioSituacaoAmostra.E);
					this.getAelAmostrasRN().atualizarAelAmostra(amostra, true);
				}
				
			}
		}
	}

	public List<AelItemSolicitacaoExames> pesquisarInformarSolicitacaoExameDigitacaoController(final Integer solicitacaoExameSeq, final Integer amostraSeqp, final Short seqUnidadeFuncional) throws ApplicationBusinessException {

		List<AelItemSolicitacaoExames> listItens = getAelAmostraItemExamesDAO().pesquisarInformarSolicitacaoExameDigitacaoController(solicitacaoExameSeq, amostraSeqp, seqUnidadeFuncional);
		if(listItens != null && listItens.size() > 0){
			
			AghParametros parametroUnidadeAnatomica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_ANATOMIA);
			AghParametros parametroDataImplLaudoUnico = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DATA_IMPL_LAUDO_UNICO);
			
			for (AelItemSolicitacaoExames item : listItens) {
				if(seqUnidadeFuncional.equals(parametroUnidadeAnatomica.getVlrNumerico().shortValue())
						&& DateUtil.validaDataMaiorIgual(new Date(), parametroDataImplLaudoUnico.getVlrData())
						&& item.getAelUnfExecutaExames() != null && item.getAelUnfExecutaExames().getIndLaudoUnico().isSim()){
					throw new ApplicationBusinessException(LaudoResultadoExameRNExceptionCode.MSG_USO_SISTEMA_LAUDO);
				}
			}
		}
		return listItens;
	}
	
	/*
	 * Dependencias
	 */
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelDocResultadoExameDAO getAelDocResultadoExameDAO(){
		return aelDocResultadoExameDAO;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO(){
		return aelAmostraItemExamesDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO(){
		return aelSolicitacaoExameDAO;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelItemSolicConsultadoDAO getAelItemSolicConsultadoDAO(){
		return aelItemSolicConsultadoDAO;
	}
	
	protected AelItemSolicConsultadoHistDAO getAelItemSolicConsultadoHistDAO(){
		return aelItemSolicConsultadoHistDAO;
	}	
	
	protected AelItemSolicConsultadoRN getAelItemSolicConsultadoRN(){
		return aelItemSolicConsultadoRN;
	}
	
	protected AelAmostraItemExamesRN getAelAmostraItemExamesRN(){
		return aelAmostraItemExamesRN;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	private AelAmostrasRN getAelAmostrasRN() {
		return aelAmostrasRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
