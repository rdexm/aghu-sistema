package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.contratualizacao.util.Item;
import br.gov.mec.aghu.exames.contratualizacao.util.Itens;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.dao.AelIntervaloColetaDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ItemSolicitacaoExamesContratualizacaoCommand extends ContratualizacaoCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3159723262502849379L;

	private static final Log LOG = LogFactory.getLog(ItemSolicitacaoExamesContratualizacaoCommand.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private IParametroFacade parametroFacade;

	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

	@Inject
	private AelIntervaloColetaDAO aelIntervaloColetaDAO;

	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	protected enum ItemSolicitacaoExamesContratualizacaoActionExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INTERVALO_COLETA_OBRIGATORIO, 
		MENSAGEM_REGIAO_ANATOMICA, 
		MENSAGEM_UNF_SEQ_OBRIGATORIO, 
		MENSAGEM_EMA_EXA_SIGLA_OBRIGATORIO, 
		MENSAGEM_EMA_MAN_SEQ_OBRIGATORIO, 
		MENSAGEM_EXAME_INEXISTENTE, 
		MENSAGEM_INTERVALO_COLETA_INVALIDO, 
		MENSAGEM_DATA_HORA_COLETA_INVALIDA,
		MENSAGEM_DATA_HORA_COLETA_OBRIGATORIO;
	}


	@Override
	Map<String, Object> executar(Map<String, Object> parametros) throws BaseException {
		SolicitacaoExame solicitacao = (SolicitacaoExame) parametros.get(ContratualizacaoCommand.SOLICITACAO_INTEGRACAO);

		List<ItemContratualizacaoVO> listaItens = new ArrayList<ItemContratualizacaoVO>();

		Itens itens = solicitacao.getItens();


		AghParametros parametroAC = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		AelSitItemSolicitacoes sitAC = getAelSitItemSolicitacoesDAO().obterPeloId(parametroAC.getVlrTexto());
		
		AghParametros parametroAX = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
		AelSitItemSolicitacoes sitAX = getAelSitItemSolicitacoesDAO().obterPeloId(parametroAX.getVlrTexto());

		for (Item item : itens.getItem()) {
			String  mensagemErro = "";
			AelItemSolicitacaoExames itemSolicitacaoExame = new AelItemSolicitacaoExames();
			try {
				AelUnfExecutaExames aelUnfExecutaExames = buscarItemSolicitacaoExame(item);
				itemSolicitacaoExame.setAelUnfExecutaExames(aelUnfExecutaExames);

				itemSolicitacaoExame.setExame(aelUnfExecutaExames.getAelExamesMaterialAnalise().getAelExames());
				itemSolicitacaoExame.setMaterialAnalise(aelUnfExecutaExames.getAelExamesMaterialAnalise().getAelMateriaisAnalises());
				itemSolicitacaoExame.setUnidadeFuncional(aelUnfExecutaExames.getUnidadeFuncional());
				itemSolicitacaoExame.setTipoColeta(DominioTipoColeta.N);
				itemSolicitacaoExame.setDthrProgramada(this.getDataProgramadaColeta(solicitacao));
				if(aelUnfExecutaExames.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndColetavel()){
					itemSolicitacaoExame.setSituacaoItemSolicitacao(sitAC);
				}else{
					itemSolicitacaoExame.setSituacaoItemSolicitacao(sitAX);
				}
				itemSolicitacaoExame.setIndUsoO2(false);
				itemSolicitacaoExame.setIndGeradoAutomatico(false);
				itemSolicitacaoExame.setIndPossuiImagem(false);
				itemSolicitacaoExame.setIndImprimiuTicket(false);
				itemSolicitacaoExame.setIndUsoO2Un(false);

				this.verificarRegiaoAnatomica(itemSolicitacaoExame);
				this.verificarNumeroAmostras(itemSolicitacaoExame);
				itemSolicitacaoExame.setIntervaloColeta(this.buscarIntervaloColeta(item, itemSolicitacaoExame));
			} catch (BaseException e) {
				mensagemErro = e.getMessage();
				logError(e.getMessage(), e);
			} catch (Exception e) {
				mensagemErro = getResourceBundleValue("ERRO_GENERICO_CONTRATUALIZACAO");
				logError(e.getMessage(), e);
			}
			ItemContratualizacaoVO itemContratualizacaoVO = new ItemContratualizacaoVO();
			itemContratualizacaoVO.setItemSolicitacaoExames(itemSolicitacaoExame);
			itemContratualizacaoVO.setIdExterno(item.getIdExterno());
			itemContratualizacaoVO.setMensagemErro(mensagemErro);
			itemContratualizacaoVO.setExame(item.getSiglaExame());
			itemContratualizacaoVO.setMaterialAnalise(item.getMaterialAnalise());
			itemContratualizacaoVO.setUnidadeFuncional(item.getUnidadeExecutora());

			listaItens.add(itemContratualizacaoVO);
		}
		parametros.put(ITENS_SOLICITACAO_INTEGRACAO, listaItens);

		return parametros;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	/**
	 * Valida e busca a data programada da coleta dos ítens.
	 * @param solicitacao
	 * @return
	 * @throws BaseException
	 */
	protected Date getDataProgramadaColeta(SolicitacaoExame solicitacao) throws BaseException {
		String dataHoraColeta = solicitacao.getDataHoraColeta();
		Date dthrColeta = null;

		if (StringUtils.isEmpty(dataHoraColeta)) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_OBRIGATORIO);
		} else {
			Pattern p = Pattern.compile(RegexUtil.DATE_PATTERN + RegexUtil.HOUR_PATTERN_HHMM);
			Matcher m = p.matcher(dataHoraColeta);
			if (!m.matches() || !RegexUtil.validarDias(dataHoraColeta)) {
				throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_INVALIDA, dataHoraColeta);
			} else {		
				DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
				try {
					dthrColeta = dateFormat.parse(dataHoraColeta);
				} catch (ParseException e) {
					throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_DATA_HORA_COLETA_INVALIDA, dataHoraColeta);
				}
			}
		}
		return dthrColeta;
	}


	/**
	 * Busca um intervalo de coleta, caso necessite e seja informado no xml.
	 * 
	 * 4.2.5 INTERVALO DE COLETAS
	 * @param item
	 * @param ise
	 * @return
	 * @throws BaseException Caso o exame use intervalo e não tenha sido informado ou então se for um intervlo inválido. 
	 */
	protected AelIntervaloColeta buscarIntervaloColeta(Item item, AelItemSolicitacaoExames ise) throws BaseException {
		AelIntervaloColeta aelIntervaloColeta = null;

		int intervaloColetaSeq = 0;

		String intervaloColetaString = StringUtils.trimToNull(item.getIntervaloColeta());
		if (StringUtils.isNotEmpty(intervaloColetaString)) {
			if (StringUtils.isNumeric(intervaloColetaString)) {
				intervaloColetaSeq = Integer.parseInt(intervaloColetaString);
			} else {
				throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_INVALIDO, intervaloColetaString);
			}
		}

		if (ise != null &&
				ise.getAelUnfExecutaExames() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndUsaIntervaloCadastrado() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndUsaIntervaloCadastrado()) {
			if (intervaloColetaSeq == 0) {
				throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_OBRIGATORIO, ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelExames().getSigla(), ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getDescricao());
			} else {
				try{
					aelIntervaloColeta = getAelIntervaloColetaDAO().obterPeloId(Short.parseShort(String.valueOf(intervaloColetaSeq)));

					this.verificarIntervaloPertencenteExame(getAelIntervaloColetaDAO().listarAtivosPorExameMaterial(ise.getExame().getSigla(), ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getSeq()), aelIntervaloColeta, intervaloColetaSeq);

				} catch (BaseException e) {
					throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_INVALIDO, intervaloColetaSeq);
				} catch (PersistenceException e) {
					throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_INVALIDO, intervaloColetaSeq);
				}
			}
		}

		return aelIntervaloColeta;
	}

	/**
	 * Verifica se o intervalo de coleta informado no xml está presente nos intervalos cadastrados para um exame
	 * @param listarPorExameMaterial
	 * @param aelIntervaloColeta
	 */
	protected void verificarIntervaloPertencenteExame(List<AelIntervaloColeta> listarPorExameMaterial,AelIntervaloColeta aelIntervaloColeta, int intervaloColetaSeq) throws BaseException {
		if (listarPorExameMaterial == null || !listarPorExameMaterial.contains(aelIntervaloColeta)) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_INTERVALO_COLETA_INVALIDO, intervaloColetaSeq);
		}
	}


	protected AelIntervaloColetaDAO getAelIntervaloColetaDAO() {
		return aelIntervaloColetaDAO;
	}

	/**
	 * Busca o POJO AelUnfExecutaExames, que identifica um ítem de solicitação de exames formado por seus 3 ids (Sigla, material e unidade executora)
	 * com as informações fornecidas no xml.
	 * @param item
	 * @return
	 * @throws BaseException
	 */
	protected AelUnfExecutaExames buscarItemSolicitacaoExame(Item item) throws BaseException {
		String unfSeqString = StringUtils.trimToNull(item.getUnidadeExecutora());
		if (StringUtils.isEmpty(unfSeqString) || ! StringUtils.isNumeric(unfSeqString)) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_UNF_SEQ_OBRIGATORIO);
		}

		String emaExaSigla = item.getSiglaExame();
		if (StringUtils.isEmpty(emaExaSigla)) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_EMA_EXA_SIGLA_OBRIGATORIO);
		}
		String matAnaliseString = StringUtils.trimToNull(item.getMaterialAnalise());
		if (StringUtils.isEmpty(matAnaliseString) || ! StringUtils.isNumeric(matAnaliseString)) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_EMA_MAN_SEQ_OBRIGATORIO);
		}
		Integer emaManSeq = Integer.valueOf(matAnaliseString);
		Short unfSeq = Short.valueOf(unfSeqString);
		AelUnfExecutaExames aelUnfExecutaExames = getAelUnfExecutaExamesDAO().obterAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);
		if (aelUnfExecutaExames == null) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_EXAME_INEXISTENTE);
		}

		return aelUnfExecutaExames;
	}

	/**
	 * 4.2.3 REGIÃO ANATÔMICA
	 * 
	 * Se o ind_exige_regiao_anatomica = 'S' a aplicação deve exibir o erro “Para este exame é 
	 * obrigatório a informação de região anatômica e esta informação não foi contemplada pela integração dos sistemas”
	 * @param ise
	 * @throws BaseException
	 */
	protected void verificarRegiaoAnatomica(AelItemSolicitacaoExames ise) throws BaseException {
		if (ise != null &&
				ise.getAelUnfExecutaExames() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndExigeRegiaoAnatomica() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndExigeRegiaoAnatomica()) {
			throw new ApplicationBusinessException(ItemSolicitacaoExamesContratualizacaoActionExceptionCode.MENSAGEM_REGIAO_ANATOMICA);
		}
	}


	/**4.2.4 NÚMERO DE AMOSTRAS 
	 * Quando o ind_solic_informa_coletas da tabela ael exames material analise = S, é obrigatório o preenchimento do número de amostras
                    Preencher o campo nro_amostras com o valor default =1 (o caso principal é o exame de fezes = 
                    EPF e foi definido que o a aplicação assumirá sempre uma amostra e o madya deverá enviar um item para cada amostra coletada!)
	 * @param ise
	 * @throws BaseException
	 */
	protected void verificarNumeroAmostras(AelItemSolicitacaoExames ise) throws BaseException {
		if (ise != null &&
				ise.getAelUnfExecutaExames() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndSolicInformaColetas() != null &&
				ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndSolicInformaColetas()) {
			ise.setNroAmostras(Byte.valueOf("1"));
			if (ise.getAelUnfExecutaExames().getAelExamesMaterialAnalise().getUnidTempoColetaAmostras() == DominioUnidTempo.H) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR, 1);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				ise.setIntervaloHoras(cal.getTime());
			} else {
				ise.setIntervaloDias(Byte.valueOf("1"));
			}
		}
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	@Override
	boolean comitar() {
		return false; //Será comitado pelo EJB, caso ocorra tudo certo entre ítens e solicitações.
	}

}
