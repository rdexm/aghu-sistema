package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacaoAelExameAp;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelAnatomoPatologicoDAO;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.exames.patologia.business.AelAnatomoPatologicoRN;
import br.gov.mec.aghu.exames.patologia.business.AelExameApRN;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExame2Facade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.AmostraVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MaterialVO;
import br.gov.mec.aghu.exames.vo.AelAmostraRecebidaVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelApXPatologistaId;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExameApItemSolicId;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AgruparExamesRN extends BaseBusiness  {

	private static final Log LOG = LogFactory.getLog(AgruparExamesRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;

	@Inject
	private AelAmostrasDAO aelAmostrasDAO;

//	@Inject
//	private AelItemConfigExameDAO aelItemConfigExameDAO;

	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;

	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;

	@Inject
	private AelAnatomoPatologicoDAO aelAnatomoPatologicoDAO;

	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;

//	@Inject
//	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

	@Inject
	private AelExameApDAO aelExameApDAO;

	@Inject
	private AelMaterialAnaliseDAO aelMaterialAnaliseDAO;

	@Inject
	private AelExamesDAO aelExamesDAO;


	@EJB
	private IExamesPatologiaFacade iExamesPatologiaFacade;

	@EJB
	private ListarAmostrasSolicitacaoRecebimentoRN listarAmostrasSolicitacaoRecebimentoRN;

	@EJB
	private ReceberTodasAmostrasSolicitacaoRN receberTodasAmostrasSolicitacaoRN;

	@EJB
	private AelExameApRN aelExameApRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private AelAmostrasRN aelAmostrasRN;

	@EJB
	private AelAnatomoPatologicoRN aelAnatomoPatologicoRN;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private ISolicitacaoExame2Facade solicitacaoExame2Facade;
	
	private static final long serialVersionUID = -900769161446661185L;

	public enum AgruparExamesRNExceptionCode implements BusinessExceptionCode {
		MSG_ERRO_IMPRIMIR_NRO_AP, ERRO_CRIACAO_AP, ERRO_CONFIGURACAO_EXAME;
	}
	/**
	 * Obtem os exames em andamento dado um numero de solcitacao, numero de prontuario e um ou mais numero de amostras
	 * 
	 * @param solicitacaoNumero numero de solicitacao de um exame
	 * @param listaSeqpAmostrasRecebidas uma ou mais codigos de amostras
	 * @param pacCodigo numero de prontuario
	 * @return List<ExameAndamentoVO> lista de exames em andamento
	 * @throws BaseException 
	 */
	public List<ExameAndamentoVO> obterExamesEmAndamento(Integer pacCodigo, Short unidadeSeq) throws BaseException {
		
		final String situacaoAreaExecutora = getParametroFacade().getAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA).getVlrTexto();

		final String situacaoCancelado = getParametroFacade().getAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO).getVlrTexto();

		final List<AelExameAp> examesAp = this.getAelExamesDAO().obterExamesEmAndamento(pacCodigo, unidadeSeq,
				situacaoAreaExecutora, DominioSituacaoExamePatologia.LA, false);
		examesAp.addAll(this.getAelExamesDAO().obterExamesEmAndamento(pacCodigo, unidadeSeq, situacaoAreaExecutora,
				DominioSituacaoExamePatologia.LA, true));

		final List<ExameAndamentoVO> retorno = new ArrayList<ExameAndamentoVO>(examesAp.size());
		for (final AelExameAp exame : examesAp) {
			this.getAelAnatomoPatologicoDAO().refresh(exame.getAelAnatomoPatologicos());

			final List<AelItemSolicitacaoExames> listaUltimoItemSolicitacaoExames = getAelItemSolicitacaoExameDAO()	.obterAelItemSolicitacaoExamesPorNumeroAPNaoCancelado
					(exame.getAelAnatomoPatologicos().getNumeroAp(), exame.getAelAnatomoPatologicos().getConfigExame().getSeq(), situacaoCancelado);
			
			if (!listaUltimoItemSolicitacaoExames.isEmpty()) {
				final AelItemSolicitacaoExames ultimoItemSolicitacaoExames = listaUltimoItemSolicitacaoExames.get(0);
	
				final AelExtratoItemSolicitacao aelExtratoItemSolicitacao = this.getAelExtratoItemSolicitacaoDAO().obterUltimoItemSolicitacaoSitCodigo(
						ultimoItemSolicitacaoExames.getId().getSoeSeq(), ultimoItemSolicitacaoExames.getId().getSeqp(), situacaoAreaExecutora);
				final ExameAndamentoVO andamentoVO = new ExameAndamentoVO();
				andamentoVO.setTipoExame(exame.getAelAnatomoPatologicos().getConfigExame());
				andamentoVO.setExame(exame.getAelAnatomoPatologicos().getConfigExame().getSigla());
				andamentoVO.setNumeroExame(exame.getAelAnatomoPatologicos().getNumeroAp());
				andamentoVO.setExameSeq(exame.getSeq());
				andamentoVO.setLumSeq(exame.getAelAnatomoPatologicos().getSeq());
				if (exame.getAelAnatomoPatologicos().getAelApXPatologistas() != null && !exame.getAelAnatomoPatologicos().getAelApXPatologistas().isEmpty()) {
					final List<AelPatologista> aelPatologistas = new ArrayList<AelPatologista>();
					for (AelApXPatologista pato : exame.getAelAnatomoPatologicos().getAelApXPatologistas()) {
						aelPatologistas.add(pato.getAelPatologista());
						if (pato.getAelPatologista().getFuncao().equals(DominioFuncaoPatologista.C)
								|| pato.getAelPatologista().getFuncao().equals(DominioFuncaoPatologista.P)) {
							andamentoVO.setPatologista(pato.getAelPatologista().getServidor().getPessoaFisica().getNome());
							andamentoVO.setPatologistaResponsavel(pato.getAelPatologista());
							break;
						}
					}
					andamentoVO.setAelPatologistas(aelPatologistas);
				}
				andamentoVO.setDataRecebimento(aelExtratoItemSolicitacao.getDataHoraEvento());
				retorno.add(andamentoVO);
			}
		}
		if (retorno != null && !retorno.isEmpty()) {
			Collections.sort(retorno);
		}
		return retorno;
	}

	/**
	 * Lista de amostras dado um numero de solicitacao de exame
	 * 
	 * @param solicitacaoNumero numero de solicitacao de exame
	 * @return List<AmostraVO> lista de amostras dado um numero de solicitacao de exame 
	 */
	public List<AmostraVO> obterAmostrasSolicitacao(Integer solicitacaoNumero, List<Integer> numerosAmostras, Map<AghuParametrosEnum, String> situacao) {
		List<AmostraVO> amostras = new ArrayList<AmostraVO>();
		List<MaterialVO> materiais = getAelMaterialAnaliseDAO().obterAmostrasSolicitacao(solicitacaoNumero, numerosAmostras, situacao);
		for (MaterialVO materialVO : materiais) {
			AmostraVO amostra = new AmostraVO();
			amostra.setMaterialAmostra(materialVO);
			AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(
					materialVO.getSolicitacaoExameSeq(), materialVO.getIseSeq());
			amostra.setItemSolicitacaoExame(itemSolicitacaoExame);
			AelConfigExLaudoUnico tipoExameAmostra = getAelConfigExLaudoUnicoDAO().obterConfigExLaudoUnico(itemSolicitacaoExame);
			amostra.setTipoExame(tipoExameAmostra);
			amostra.setNumeroAmostra(materialVO.getNumeroAmostra());
			amostras.add(amostra);
		}
		return amostras;
	}

	/**
	 * Processo para agrupar amostras entre si ou amostras com exames ou apenas
	 * criar novo exame para amostras nao agrupadas
	 * 
	 * @param listaAmostras
	 *            lista de amostras selecionado na grid
	 * @param solicitacaoNumero
	 *            numero de solicitacao de exame
	 * @param servidorLogado
	 *            usuario logado no AGHU
	 * @throws BaseException
	 */
	/**
	 * @param listaAmostras
	 * @param solicitacaoNumero
	 * @param servidorLogadofinal
	 * @param unidadeExecutora
	 * @param listaExamesAndamento
	 * @param nomeMicrocomputador
	 * @param situacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AelAmostraRecebidaVO> gravarAmostras(final List<AmostraVO> listaAmostras, final Integer solicitacaoNumero, final RapServidores servidorLogadofinal,
			final AghUnidadesFuncionais unidadeExecutora, final List<ExameAndamentoVO> listaExamesAndamento, final String nomeMicrocomputador, 
			final Map<AghuParametrosEnum, String> situacao) throws ApplicationBusinessException {
		AelExameAp aelExameAp = null;
		final List<AelAmostraRecebidaVO> examesRecebidos = new ArrayList<AelAmostraRecebidaVO>();
		try {
			for (final AmostraVO amostraVO : listaAmostras) {
				if (amostraVO.getExameAndamentoVO() != null) {
					// foi agrupada com exame. 
					// apagar patologistas reponsaveis anteriores.
					final List<AelApXPatologista> patologistas = getAelApXPatologistaDAO().listarAelApXPatologistaAtivos(
							amostraVO.getExameAndamentoVO().getLumSeq(), null);
					for (final AelApXPatologista patologista : patologistas) {
						this.getExamesPatologiaFacade().excluirAelApXPatologistaPorPatologista(patologista.getId().getLuiSeq(),
								patologista.getId().getLumSeq());
					}
				}

				if (amostraVO.getAmostrasAgrupadas() == null || amostraVO.getAmostrasAgrupadas().isEmpty()) {
					// não é agrupada criar apenas um item para o exame criado
					final AelAmostraRecebidaVO amostraRecebidaVO = this.gravarAmostra(unidadeExecutora, amostraVO, listaExamesAndamento,
							nomeMicrocomputador, servidorLogadofinal, null, situacao, false);
					aelExameAp = amostraRecebidaVO.getAelExamep();
					examesRecebidos.add(amostraRecebidaVO);
				} else {
					// Se for amostras agrupadas dele criar um item por amostra
					AelAnatomoPatologico aelAnatomoPatologico = null;
					// Controla as informações clínicas para não duplicar as informações pertencentes a mesma solicitação.
					List<Integer> exameInfoClinica = new ArrayList<Integer>();
					for (final AmostraVO amostraAgrupada : amostraVO.getAmostrasAgrupadas()) {
						amostraAgrupada.setExameAndamentoVO(amostraVO.getExameAndamentoVO());
						final AelAmostraRecebidaVO amostraRecebidaVO = this.gravarAmostra(unidadeExecutora, amostraAgrupada, listaExamesAndamento,
								nomeMicrocomputador, servidorLogadofinal, aelAnatomoPatologico, situacao, 
								exameInfoClinica.contains(amostraAgrupada.getItemSolicitacaoExame().getId().getSoeSeq()));						
						exameInfoClinica.add(amostraAgrupada.getItemSolicitacaoExame().getId().getSoeSeq());
						aelExameAp = amostraRecebidaVO.getAelExamep();
						// guarda o ap gerado para usar o mesmo em todas as amostras agrupadas
						aelAnatomoPatologico = aelExameAp.getAelAnatomoPatologicos();
						examesRecebidos.add(amostraRecebidaVO);
					}
				}
				
				// adiciona o exame de origem, quando houver				
//				if ((numeroApOrigem != null) && 
//						(iAghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES))
//					) {
//					
//					AelAnatomoPatologico aelAnatomoPatologicoOrigem = getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoPorNumeroAp(numeroApOrigem);
//					
//					aelExameAp.setAelAnatomoPatologicoOrigem(aelAnatomoPatologicoOrigem);
//					getAelExameApDAO().atualizar(aelExameAp);
//					getAelExameApDAO().flush();
//				}
				
				if (aelExameAp != null) {
					this.criarPatologistasExameAp(amostraVO, aelExameAp.getAelAnatomoPatologicos());
				}
			}
			
			vinculaApComTransOperatorio(examesRecebidos, listaExamesAndamento);
			
			if (!examesRecebidos.isEmpty()) {
				vinculaApComTransOperatorioLiberado(examesRecebidos);
			}
			
			return examesRecebidos;
		} catch (BaseException ex) {
			throw new ApplicationBusinessException(ex);
		}
	}
	
	/**
	 * Se tiver recebendo um AP e foi feito um transoperatório nos ultimos P_DIAS_UTEIS_AGRUP_EXAMES
	 * vincula no AP que está sendo recebido
	 * 
	 * @param examesRecebidos
	 * @throws BaseException 
	 */
	private void vinculaApComTransOperatorioLiberado(List<AelAmostraRecebidaVO> examesRecebidos) throws BaseException {
		
		Integer numeroDiasUteis = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_UTEIS_AGRUP_EXAMES).getVlrNumerico().intValue();
		final Calendar dataAtual = Calendar.getInstance();
		switch (dataAtual.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			dataAtual.add(Calendar.DAY_OF_MONTH, -2);
			break;
		case Calendar.SATURDAY:
			dataAtual.add(Calendar.DAY_OF_MONTH, -1);
			break;
		default:
		}

		dataAtual.set(Calendar.HOUR_OF_DAY, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.set(Calendar.MILLISECOND, 0);
		while (getAghuFacade().obterFeriado(dataAtual.getTime()) != null) {
			dataAtual.add(Calendar.DAY_OF_MONTH, -1);
		}

		dataAtual.add(Calendar.DAY_OF_MONTH, (-1 * numeroDiasUteis));

		Date diasValidos = dataAtual.getTime();
		
		Integer pacCodigo = null;
		Integer seqTO = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_SEQ_CONFIGURACAO_TO);
		AelAmostraRecebidaVO recebidosVO = examesRecebidos.get(0);
		if (recebidosVO.getAelExamep().getAelAnatomoPatologicos().getAtendimento() != null) {
			pacCodigo = recebidosVO.getAelExamep().getAelAnatomoPatologicos().getAtendimento().getPacCodigo();
		}
		else if (recebidosVO.getAelExamep().getAelAnatomoPatologicos().getAtendimentoDiversos() != null) {
			pacCodigo = recebidosVO.getAelExamep().getAelAnatomoPatologicos().getAtendimentoDiversos().getAipPaciente().getCodigo();
		}
		if (pacCodigo != null) {
			Long lumSeq = getAelAnatomoPatologicoDAO().obterUltimoAelAnatomoPatologicoPorPacCodigo(pacCodigo, seqTO, diasValidos);
			if (lumSeq != null) {
				AelAnatomoPatologico anatomoPatologico = getAelAnatomoPatologicoDAO().obterPorChavePrimaria(lumSeq);
				
				for (AelAmostraRecebidaVO vo : examesRecebidos) {
					AelExameAp ap = vo.getAelExamep();
					ap.setAelAnatomoPatologicoOrigem(anatomoPatologico);
					atualizaExameAP(ap);
				}
			}
		}

	}

	/**
	 * Vincula exame Anatomopatológico com Transoperatório
	 * Sempre que é feito um transoperatório será feito um anatomopatológico
	 * e o médico precisa saber que determinado anatomopatológico é referente
	 * a outro transoperatório.
	 * 
	 * @param examesRecebidos
	 * @throws BaseException 
	 * @throws AGHUNegocioException 
	 */
	private void vinculaApComTransOperatorio(List<AelAmostraRecebidaVO> examesRecebidos, final List<ExameAndamentoVO> listaExamesAndamento) throws BaseException {
		
		Integer seqAP = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_SEQ_CONFIGURACAO_AP);
		Integer seqTO = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_SEQ_CONFIGURACAO_TO);
		
		AelAnatomoPatologico transOperatorio = null;
		for (AelAmostraRecebidaVO vo : examesRecebidos) { //verifica se recebeu um TO
			if (seqTO.equals(vo.getAelExamep().getAelAnatomoPatologicos().getConfigExame().getSeq())) { 
				transOperatorio = vo.getAelExamep().getAelAnatomoPatologicos();
			}
		}
		if (transOperatorio != null) { //se recebeu um TO vincula os APs recebidos na mesma solicitacao
			for (AelAmostraRecebidaVO vo : examesRecebidos) {
				AelExameAp ap = vo.getAelExamep();
				if (seqAP.equals(ap.getAelAnatomoPatologicos().getConfigExame().getSeq())) {
					ap.setAelAnatomoPatologicoOrigem(transOperatorio);
					atualizaExameAP(ap);
				}
			}
			
			if (listaExamesAndamento != null) { //se recebeu um TO e já recebeu um AP em outro momento vincula
				for (ExameAndamentoVO andamentoVO : listaExamesAndamento) {
					AelAnatomoPatologico anatomoPatologico = getAelAnatomoPatologicoDAO().obterPorChavePrimaria(andamentoVO.getLumSeq());
					if (!anatomoPatologico.getConfigExame().getSeq().equals(seqTO)) {
						AelExameAp ap = getAelExameApDAO().obterAelExameApPorAelAnatomoPatologicos(anatomoPatologico);
						ap.setAelAnatomoPatologicoOrigem(transOperatorio);
						atualizaExameAP(ap);
					}
				}
			}
		}
		
		//se recebeu um AP e tiver um TO em andamento vincula
		transOperatorio = null;
		for (ExameAndamentoVO andamentoVO : listaExamesAndamento) {
			AelAnatomoPatologico anatomoPatologico = getAelAnatomoPatologicoDAO().obterPorChavePrimaria(andamentoVO.getLumSeq());
			AelExameAp transOper = getAelExameApDAO().obterAelExameApPorAelAnatomoPatologicos(anatomoPatologico);
			if (seqTO.equals(transOper.getAelAnatomoPatologicos().getConfigExame().getSeq())) { 
				transOperatorio = anatomoPatologico;
			}
		}
		if (transOperatorio != null) { //TO em andamento vincula o AP com o TO
			for (AelAmostraRecebidaVO vo : examesRecebidos) {
				AelExameAp ap = vo.getAelExamep();
				if (seqAP.equals(ap.getAelAnatomoPatologicos().getConfigExame().getSeq())) { 
					ap.setAelAnatomoPatologicoOrigem(transOperatorio);
					atualizaExameAP(ap);
				}
			}
		}

	}
	
	private void atualizaExameAP(AelExameAp ap) throws BaseException {
		//getAelExameApDAO().atualizar(ap);
		getAelExameApRN().atualizarAelExameApRN(ap);
		getAelExameApDAO().flush();
	}	

	private AelAmostraRecebidaVO gravarAmostra(final AghUnidadesFuncionais unidadeExecutora, final AmostraVO amostraVO,
			final List<ExameAndamentoVO> listaExamesAndamento, final String nomeMicrocomputador,
			final RapServidores servidorLogadofinal, AelAnatomoPatologico aelAnatomoPatologico, final Map<AghuParametrosEnum, String> situacao, Boolean existeInfoClinica)
			throws BaseException {
		final String QUEBRA_LINHA = "\n";
		final AelAmostras amostra = this.getAelAmostrasDAO().buscarAmostrasPorId(amostraVO.getItemSolicitacaoExame().getId().getSoeSeq(),
				amostraVO.getNumeroAmostra().shortValue());
		//		final ImprimeEtiquetaVO etiquetaVo = this.getReceberTodasAmostrasSolicitacaoRN().receberAmostraUnidadeExecutoraExames(unidadeExecutora, amostra,
		//				amostraVO.getTipoExame(), numeroApOrigem, nomeMicrocomputador, servidorLogadofinal);
		final ImprimeEtiquetaVO etiquetaVo = this.getListarAmostrasSolicitacaoRecebimentoRN().receberAmostra(unidadeExecutora, amostra,
				amostra.getNroFrascoFabricante(), listaExamesAndamento, nomeMicrocomputador);

		final AelAmostraRecebidaVO exameRecebido = new AelAmostraRecebidaVO();
		amostraVO.setItemSolicitacaoExame(this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(amostraVO.getItemSolicitacaoExame().getId()));
		// criar aelExameAp, aelAnatomoPatologico e AelExameApItemSolics
		final AelExameAp aelExameAp = this.atualizarLaudoUnico(amostraVO, amostraVO.getTipoExame(), unidadeExecutora, aelAnatomoPatologico, situacao);
		exameRecebido.setAelExamep(aelExameAp);
		if (aelExameAp != null) {
			if (aelExameAp.getAelAnatomoPatologicos() != null) {
				etiquetaVo.setNroAp(aelExameAp.getAelAnatomoPatologicos().getNumeroAp());
				etiquetaVo.setNumeroAp(aelExameAp.getAelAnatomoPatologicos().getNumeroAp());
				etiquetaVo.setSigla(aelExameAp.getAelAnatomoPatologicos().getConfigExame().getSigla());
			}// atualiza informação clínica, só adiciona caso ainda não tenha adicionado as informações clínicas da solicitação através de outro exame.
			if(!existeInfoClinica) {
				AelInformacaoClinicaAP informacaoClinicaAP = aelExameAp.getAelInformacaoClinicaAP();
				if (informacaoClinicaAP == null) {
					informacaoClinicaAP = new AelInformacaoClinicaAP();
					informacaoClinicaAP.setAelExameAp(aelExameAp);
					informacaoClinicaAP.setInformacaoClinica(amostraVO.getItemSolicitacaoExame().getSolicitacaoExame().getInformacoesClinicas());
					informacaoClinicaAP.setRapServidores(servidorLogadofinal);
					aelExameAp.setAelInformacaoClinicaAP(informacaoClinicaAP);
				} else {
					final StringBuffer infCli = new StringBuffer(informacaoClinicaAP.getInformacaoClinica());
					infCli.append(QUEBRA_LINHA).append(amostraVO.getItemSolicitacaoExame().getSolicitacaoExame().getInformacoesClinicas());
					informacaoClinicaAP.setInformacaoClinica(infCli.toString());
				}
	
				this.getExamesPatologiaFacade().persistirAelInformacaoClinicaAP(informacaoClinicaAP);
			}
		}
		// criar materialAps
		this.getExamesPatologiaFacade().atualizaMateriais(aelExameAp, amostraVO.getItemSolicitacaoExame().getId());

		if (exameRecebido != null) {
			if (exameRecebido.getEtiquetas() == null) {
				exameRecebido.setEtiquetas(new ArrayList<ImprimeEtiquetaVO>());
			}
			exameRecebido.getEtiquetas().add(etiquetaVo);
		}
		return exameRecebido;
	}

	private AelExameAp atualizarLaudoUnico(AmostraVO amostra, AelConfigExLaudoUnico tipoExame, AghUnidadesFuncionais unidadeExecutora,
			AelAnatomoPatologico aelAnatomoPatologico, final Map<AghuParametrosEnum, String> situacoes
	/* final String situacaoCancelado, final String situacaoAreaExecutora, */)
			throws BaseException {
		AelExameAp aelExameAp = null;
		boolean situacoesOk = false;
		if (situacoes != null) {
			for (String situacao : situacoes.values()) {
				situacoesOk = situacoesOk || situacao.equals(amostra.getItemSolicitacaoExame().getSituacaoItemSolicitacao().getCodigo());
			}
		}
		if (amostra.getItemSolicitacaoExame() != null && situacoesOk) {

			DominioSituacaoAelExameAp situacao = DominioSituacaoAelExameAp.I;
			//			Boolean indRevisaoLaudo = false;
			if (amostra.getExameAndamentoVO() != null) {
				aelAnatomoPatologico = this.getAelAnatomoPatologicoDAO().obterAelAnatomoPatologicoByNumeroAp(amostra.getExameAndamentoVO().getNumeroExame(),
						amostra.getExameAndamentoVO().getTipoExame().getSeq());
			}
			if (aelAnatomoPatologico == null) {
				aelAnatomoPatologico = this.criarAelAnatomoPatologico(amostra, tipoExame);
			}

			aelExameAp = this.getAelExameApDAO().obterPorAelAnatomoPatologicoAelConfigExLaudoUnico(aelAnatomoPatologico.getSeq(), tipoExame.getSeq());
			if (aelExameAp == null) {
				aelExameAp = this.criarAelExameAp(amostra, situacao, false, DominioSituacaoExamePatologia.RE, aelAnatomoPatologico);
			} else {
				String materiais = null;
				//				if (!indRevisaoLaudo) {
					materiais = this.obterMateriais(amostra.getItemSolicitacaoExame());
				//				}
				final Long nrExtratoItemSolicitacao = getAelExtratoItemSolicitacaoDAO().contarExtratoItemSolicitacaoPorAreaExecutora(
						amostra.getItemSolicitacaoExame().getId(), (situacoes == null ? null : situacoes.get(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA)));

				if (amostra.getItemSolicitacaoExame().getSituacaoItemSolicitacao().getCodigo()
						.equals((situacoes == null ? null : situacoes.get(AghuParametrosEnum.P_SITUACAO_CANCELADO)))
						|| nrExtratoItemSolicitacao <= 1) {
					if (materiais != null) {
						aelExameAp.setMateriais(aelExameAp.getMateriais() + ";" + materiais);
					}
					aelExameAp.setSituacao(situacao);
					aelExameAp.setIndRevisaoLaudo(false);//indRevisaoLaudo);
					this.getExamesPatologiaFacade().persistirAelExameAp(aelExameAp);
				}
			}
			if (!this.getAelExameApItemSolicDAO().hasAelExameApItemSolicPorItemSolicitacaoExame(amostra.getItemSolicitacaoExame().getId())) {
				final AelExameApItemSolic elemento = new AelExameApItemSolic();
				elemento.setId(new AelExameApItemSolicId(aelExameAp.getSeq(), amostra.getItemSolicitacaoExame().getId().getSoeSeq(), amostra
						.getItemSolicitacaoExame().getId().getSeqp()));
				this.getSolicitacaoExame2Facade().inserirAelExameApItemSolic(elemento);
			}
		}
		return aelExameAp;
	}

	private AelAnatomoPatologico criarAelAnatomoPatologico(AmostraVO amostra, AelConfigExLaudoUnico tipoExame)
			throws BaseException {
		final AelAnatomoPatologico aelAnatomoPatologico = new AelAnatomoPatologico();

		Long numeroAP = this.getAelAmostrasRN().calcularNumeroAp(amostra.getItemSolicitacaoExame());
		aelAnatomoPatologico.setNumeroAp(numeroAP);
		aelAnatomoPatologico.setAtendimento(amostra.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento());
		aelAnatomoPatologico.setAtendimentoDiversos(amostra.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso());
		aelAnatomoPatologico.setConfigExame(this.getAelConfigExLaudoUnicoDAO().obterPorChavePrimaria(tipoExame.getSeq()));
		aelAnatomoPatologico.setLu2VersaoConf(this.getAelSecaoConfExamesDAO().buscarMaxVersaoConfPorLu2Seq(tipoExame.getSeq()));
		if (aelAnatomoPatologico.getLu2VersaoConf() == null) {
			throw new ApplicationBusinessException(AgruparExamesRNExceptionCode.ERRO_CONFIGURACAO_EXAME, aelAnatomoPatologico.getConfigExame().getSeq());
		}		
		try {
			this.getAelAnatomoPatologicoRN().persistir(aelAnatomoPatologico);
			return aelAnatomoPatologico;
		} catch (final Exception e) {
			LOG.error(AgruparExamesRNExceptionCode.ERRO_CRIACAO_AP.toString() + "\nAP: [" + numeroAP + "]\nTipo Exame: ["
					+ aelAnatomoPatologico.getConfigExame().getSeq() + "]\n");
			throw new ApplicationBusinessException(AgruparExamesRNExceptionCode.ERRO_CRIACAO_AP, numeroAP, aelAnatomoPatologico.getConfigExame().getSeq());
		}
	}

	private AelExameAp criarAelExameAp(AmostraVO amostra, DominioSituacaoAelExameAp situacao, Boolean indRevisaoLaudo,
			DominioSituacaoExamePatologia etapaLaudo, AelAnatomoPatologico aelAnatomoPatologico) throws BaseException {
		final AelExameAp aelExameAp = new AelExameAp();

		aelExameAp.setMateriais(this.obterMateriais(amostra.getItemSolicitacaoExame()));
		aelExameAp.setSituacao(situacao);
		aelExameAp.setIndRevisaoLaudo(indRevisaoLaudo);
		aelExameAp.setEtapasLaudo(etapaLaudo);
		aelExameAp.setIndLaudoVisual(false);
		aelExameAp.setIndImpresso(false);
		aelExameAp.setAelAnatomoPatologicos(aelAnatomoPatologico);
		aelExameAp.setConfigExLaudoUnico(aelAnatomoPatologico.getConfigExame());

		this.getExamesPatologiaFacade().persistirAelExameAp(aelExameAp);
		return aelExameAp;
	}

	private String obterMateriais(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		final String regiaoAnatomica = this.obterRegiaoAnatomica(itemSolicitacaoExame);

		String materialAnalise = this.obterDescricaoMaterialAnalise(itemSolicitacaoExame);

		if (!StringUtils.isEmpty(regiaoAnatomica)) {
			// O código original teve q ser mudado por causa do PMD, regra UseStringBufferForStringAppends!
			// materialAnalise = materialAnalise == null ? regiaoAnatomica : new StringBuffer(regiaoAnatomica).append(':').append(materialAnalise).toString();
			materialAnalise = new StringBuffer((materialAnalise == null) ? regiaoAnatomica : new StringBuffer(regiaoAnatomica).append(':')
					.append(materialAnalise).toString()).toString();
		}
		return materialAnalise;
	}

	private String obterDescricaoMaterialAnalise(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		if (itemSolicitacaoExame.getDescMaterialAnalise() == null) {
			return itemSolicitacaoExame.getMaterialAnalise() == null ? null : itemSolicitacaoExame.getMaterialAnalise().getDescricao();
		} else {
			return itemSolicitacaoExame.getDescMaterialAnalise();
		}
	}

	private String obterRegiaoAnatomica(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		if (itemSolicitacaoExame.getDescRegiaoAnatomica() == null) {
			return itemSolicitacaoExame.getRegiaoAnatomica() == null ? null : itemSolicitacaoExame.getRegiaoAnatomica().getDescricao();
		} else {
			return itemSolicitacaoExame.getDescRegiaoAnatomica();
		}
	}

	private void criarPatologistasExameAp(final AmostraVO amostraVO, final AelAnatomoPatologico aelAnatomoPatologico)
			throws BaseException {
		for (final AelPatologista patologista : amostraVO.getPatologistasResponsaveis()) {
			getExamesPatologiaFacade().persistirAelApXPatologista(this.gerarAelApXPatologista(patologista, aelAnatomoPatologico));
		}
	}

	private AelApXPatologista gerarAelApXPatologista(final AelPatologista patologistaResponsavel, final AelAnatomoPatologico aelAnatomoPatologico) {
		AelApXPatologista aelApXPatologista = new AelApXPatologista();
		final AelApXPatologistaId aelApXPatologistaId = new AelApXPatologistaId();
		aelApXPatologistaId.setLuiSeq(patologistaResponsavel.getSeq());
		aelApXPatologistaId.setLumSeq(aelAnatomoPatologico.getSeq());
		aelApXPatologista.setId(aelApXPatologistaId);

		aelApXPatologista.setAelAnatomoPatologicos(aelAnatomoPatologico);
		aelApXPatologista.setAelPatologista(patologistaResponsavel);
		return aelApXPatologista;
	}
	
	protected AelMaterialAnaliseDAO getAelMaterialAnaliseDAO() {
		return aelMaterialAnaliseDAO;
	}
	
	protected AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO() {
		return aelConfigExLaudoUnicoDAO;
	}
	
	protected AelSecaoConfExamesDAO getAelSecaoConfExamesDAO() {
		return aelSecaoConfExamesDAO;
	}
	
	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}
	
	protected AelAnatomoPatologicoDAO getAelAnatomoPatologicoDAO() {
		return aelAnatomoPatologicoDAO;
	}
	
	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}
	
	protected AelExameApRN getAelExameApRN() {
		return aelExameApRN;
	}
		
	protected AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected AelAmostrasRN getAelAmostrasRN() {
		return aelAmostrasRN;
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return iExamesPatologiaFacade;
	}

	
	protected AelAnatomoPatologicoRN getAelAnatomoPatologicoRN() {
		return aelAnatomoPatologicoRN;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}

	protected ReceberTodasAmostrasSolicitacaoRN getReceberTodasAmostrasSolicitacaoRN() {
		return receberTodasAmostrasSolicitacaoRN;
	}

	protected ListarAmostrasSolicitacaoRecebimentoRN getListarAmostrasSolicitacaoRecebimentoRN() {
		return listarAmostrasSolicitacaoRecebimentoRN;
	}

	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}

	//
	//	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
	//		return aelAmostraItemExamesDAO;
	//	}
	//
	//	protected AelItemConfigExameDAO getAelItemConfigExameDAO() {
	//		return aelItemConfigExameDAO;
	//	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected ISolicitacaoExame2Facade getSolicitacaoExame2Facade() {
		return this.solicitacaoExame2Facade;
	}
	
}
