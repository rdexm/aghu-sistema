package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaNotifInfeccaoDAO;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.ProcedRealizadoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.controleinfeccao.LocalNotificacaoOrigemRetornoVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoTopografiasVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioMotivoEncerramentoNotificacao;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoLocalNotificacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccao;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccaoId;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class NotificacaoTopografiaON extends BaseBusiness {

	private static final long serialVersionUID = -8623544789224930700L;
	
	private static final Log LOG = LogFactory.getLog(NotificacaoTopografiaON.class);
	private static final String HIFEN = " - ";
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private NotificacoesRN notificacoesRN;
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private MciMvtoInfeccaoTopografiaRN infeccaoTopografiaRN;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private MciMvtoInfeccaoTopografiaBuilder mvtoInfeccaoTopografiaBuilder;
	
	@EJB
	private MciMvtoMedidaPreventivaBuilder mvtoMedidaPreventivaBuilder;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private MciMvtoMedidaPreventivaRN medidaPreventivaRN;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO mciMvtoInfeccaoTopografiasDAO;
	
	@Inject
	private MciTopografiaProcedimentoDAO mciTopografiaProcedimentoDAO;
	
	@Inject
	private MamRespostaNotifInfeccaoDAO mamRespostaNotifInfeccaoDAO;

	@Inject
	private MciEtiologiaInfeccaoDAO etiologiaInfeccaoDAO;
	
	@Inject
	private MciMvtoMedidaPreventivasDAO medidaPreventivasDAO;
	
	@Inject
	public AghAtendimentoDAO atendimentoDAO;

	
	public enum NotificacaoTopografiaONExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH, MSG_NOTIF_TOPO_RESTRICAO_EXCLUSAO, MSG_NOTIF_TOPO_ETIOLOGIA_MCI_00513, MSG_NOTIF_TOPO_DATA_ENCERRAMENTO_ATUAL_MCI_00306,
		MSG_NOTIF_TOPO_VALIDACAO_ENCERRAMENTO_MCI_00262, ERRO_NOTIF_TOPO_MCIK_RN_RN_MCIP_ATU_LOCAL, MSG_NOTIF_DATA_INSTALACAO, MSG_NOTIF_TOPO_PERIODO,
		ERRO_P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO, MSG_NOTIF_TOPO_VALIDACAO_CIRURGIA_ASSOCIADA;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	public List<NotificacaoTopografiasVO> obterNotificacoesTopografia(Integer codigo) throws ApplicationBusinessException {
		List<NotificacaoTopografiasVO> list = mciMvtoInfeccaoTopografiasDAO.listarMciTopografiaProcedimentoPorSeqDescSitSeqTop(codigo);
		formatar(list);
		return list;
	}
	
	private void formatar(List<NotificacaoTopografiasVO> list) throws ApplicationBusinessException {
		if(list != null){
			for (NotificacaoTopografiasVO vo : list) {
				vo.setStrSeqDescricaoTopoProced(vo.getSeq().toString() + HIFEN + vo.getDescricaoTopografiaProcedimento());
				
				this.formatarLocalizacao(vo);
				
				String procedimento = vo.getDescricaoProcedimento() != null ? vo.getDescricaoProcedimento() : null;
				String dataCrg = null;
				if (vo.getDtHrInicioCrg() != null) {
					dataCrg = DateUtil.obterDataFormatada(vo.getDtHrInicioCrg(), "dd/MM/yyyy");
				}
				String contaminacao = vo.getPotencialContaminacao() != null ? vo.getPotencialContaminacao().getDescricao() : null;
				
				if (procedimento != null && dataCrg != null && contaminacao != null) {
					vo.setProcedDataContaminacao(procedimento + HIFEN + dataCrg + HIFEN + contaminacao);
				}
			}
		}
	}
	
	private void formatarLocalizacao(NotificacaoTopografiasVO vo) throws ApplicationBusinessException {
		AinLeitos leito = null;
		AinQuartos quarto = null;
		AghUnidadesFuncionais unidadeFuncional = null;
		if (vo.getLeito() != null) {
			leito = internacaoFacade.obterAinLeitosPorChavePrimaria(vo.getLeito());
		}
		if (vo.getNumeroQuarto() != null) {
			quarto = internacaoFacade.obterAinQuartosPorChavePrimaria(vo.getNumeroQuarto());
		}
		if (vo.getSeqUnidadesFuncional() != null) {
			unidadeFuncional = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(vo.getSeqUnidadesFuncional());
		}
		vo.setLocalizacao(prescricaoMedicaFacade.buscarResumoLocalPaciente(leito, quarto, unidadeFuncional));
	}

	public TopografiaProcedimentoVO obterTopografiaProcedimento(Short seq) throws ApplicationBusinessException {
		List<TopografiaProcedimentoVO> list = mciTopografiaProcedimentoDAO.suggestionBoxTopografiaProcedimentoPorSeqOuDescricaoSituacao(seq.toString());
		
		if(list != null){
			return list.get(0);
		}
		
		return null;
	}
	
	public OrigemInfeccoesVO obterTopografiaOrigemInfeccoes(String codigoEtiologiaInfeccao) {
		List<OrigemInfeccoesVO> list =  etiologiaInfeccaoDAO.suggestionBoxTopografiaOrigemInfeccoes(codigoEtiologiaInfeccao);
		
		if(list != null){
			return list.get(0);
		}
		
		return null;
	}
	
	public List<ProcedRealizadoVO> obterProcedimentosPorPaciente(Integer pacCodigo, String strPesquisa) throws ApplicationBusinessException {
		Date dataTruncada = DateUtil.truncaData(new Date());
		
		AghParametros paramVideoLaparoscopia = this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_GRUPO_VIDEOLAPAROSCOPIA);
		Short videoLaparoscopia = paramVideoLaparoscopia.getVlrNumerico().shortValue();
		
		Date dtRealizCrgVideo = this.obterDataCalculada(AghuParametrosEnum.P_AGHU_DIAS_REALIZACAO_CIRURGIA_VIDEO, dataTruncada);
		Date dtRealizCrgOrtese = this.obterDataCalculada(AghuParametrosEnum.P_AGHU_DIAS_REALIZACAO_CIRURGIA_ORTESE, dataTruncada);
		Date dtRealizCrgSemVideo = this.obterDataCalculada(AghuParametrosEnum.P_AGHU_DIAS_REALIZACAO_CIRURGIA_SEM_VIDEO, dataTruncada);
		
		List<ProcedRealizadoVO> listaRetorno = this.blocoCirurgicoFacade
				.obterProcedimentosPorPaciente(pacCodigo, strPesquisa, videoLaparoscopia,dtRealizCrgVideo,
						dtRealizCrgOrtese, dtRealizCrgSemVideo);
		
		for (ProcedRealizadoVO vo : listaRetorno) {
			vo.setDescricaoFormatada(vo.getDescricao().concat(HIFEN)
					.concat(DateUtil.obterDataFormatada(vo.getDthrInicioCirg(), "dd/MM/yyyy")
							.concat(HIFEN).concat(vo.getContaminacao().getDescricao())));
		}
		return listaRetorno;
	}
	
	public ProcedRealizadoVO obterProcedimentoVOPorId(Integer dcgCrgSeq, Short dcgSeqp, Integer seqp) {
		ProcedRealizadoVO retorno = this.blocoCirurgicoFacade.obterProcedimentoVOPorId(dcgCrgSeq, dcgSeqp, seqp);
		
		retorno.setDescricaoFormatada(retorno.getDescricao().concat(HIFEN)
				.concat(DateUtil.obterDataFormatada(retorno.getDthrInicioCirg(), "dd/MM/yyyy")
						.concat(HIFEN).concat(retorno.getContaminacao().getDescricao())));
		
		return retorno;
	}
	
	private Date obterDataCalculada(AghuParametrosEnum parametro, Date dataTruncada) throws ApplicationBusinessException {
		AghParametros retorno = this.parametroFacade.obterAghParametro(parametro);
		Integer numeroDias = retorno.getVlrNumerico().intValue();
		Date dataCalculada = DateUtil.adicionaDias(dataTruncada, -numeroDias);
		
		return dataCalculada;
	}

	public void persistirNotificacaoTopografia(NotificacaoTopografiasVO vo) throws BaseException {
		
		controleInfeccaoRN.notNull(vo, NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		validarNotificaoTopografia(vo);
		obterAtendimento(vo);
		
		infeccaoTopografiaRN.persistir(mvtoInfeccaoTopografiaBuilder.construir(vo));
		atualizarMedidaPreventivaAssociada(vo);
		
	}

	private void atualizarMedidaPreventivaAssociada(NotificacaoTopografiasVO vo) throws ApplicationBusinessException {
		List<MciMvtoMedidaPreventivas> listMedidaPreventivas = medidaPreventivasDAO.listarMvtosMedidasPreventivasPorseqNotificacaoTopografia(vo.getSeq());
		
		if(listMedidaPreventivas != null ){
			for (MciMvtoMedidaPreventivas medidaPreventivas : listMedidaPreventivas) {
				medidaPreventivaRN.persistir(mvtoMedidaPreventivaBuilder.construir(vo, medidaPreventivas));
			}
		}
	}
	
	public void excluirNotificacaoTopografia(NotificacaoTopografiasVO vo) throws BaseException {
		controleInfeccaoRN.notNull(vo, NotificacaoTopografiaONExceptionCode.ERRO_PERSISTENCIA_CCIH);
		notificacoesRN.validarNumeroDiasDecorridosCriacaoRegistro(vo.getCriadoEm(), NotificacaoTopografiaONExceptionCode.MSG_NOTIF_TOPO_PERIODO);
		
		List<MciMvtoMedidaPreventivas> listMedidaPreventivas = medidaPreventivasDAO.listarMvtosMedidasPreventivasPorseqNotificacaoTopografia(vo.getSeq());
		
		BaseListException listaDeErros = new BaseListException();
		
		if(listMedidaPreventivas != null ){
			for (MciMvtoMedidaPreventivas medidaPreventivas : listMedidaPreventivas) {
				listaDeErros.add(new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.MSG_NOTIF_TOPO_RESTRICAO_EXCLUSAO, medidaPreventivas.getSeq()));
			}
		}
		
		if (listaDeErros.hasException()) {
			throw listaDeErros;
		}

		infeccaoTopografiaRN.remover(vo.getSeq());
	}

	/**
	 * Integração com Procedure determinar o local de notificacao e o local
	 * de origem ORADB PROCEDURE MCIK_RN.RN_MCIP_ATU_LOCAL
	 */
	private void obterAtendimento(NotificacaoTopografiasVO vo) throws BaseException {
		try {
			
			LocalNotificacaoOrigemRetornoVO localNotificacaoOrigemRetornoVO =  null;
			
			localNotificacaoOrigemRetornoVO = notificacoesRN
					.obterLocalAtendimento(
							vo.getSeqAtendimento(),
							vo.getCodigoEtiologiaInfeccao(),
							DominioTipoMovimentoLocalNotificacao.MIT,
							vo.getDataInicio(),
							obterMamRespostaNotifInfeccao(vo.getRniPnnSeq(), vo.getRniSeqp()));
			
			setRetornoProcedure(vo, localNotificacaoOrigemRetornoVO);
			
		} catch (BaseException e) {
			throw new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.ERRO_NOTIF_TOPO_MCIK_RN_RN_MCIP_ATU_LOCAL) ;
		}
	}

	private void setRetornoProcedure(NotificacaoTopografiasVO vo, LocalNotificacaoOrigemRetornoVO localNotificacaoOrigemRetornoVO) {
		vo.setLeito(localNotificacaoOrigemRetornoVO.getLtoLtoId());
		vo.setLeitoNotificado(localNotificacaoOrigemRetornoVO.getLtoLtoIdNotificado());
		vo.setNumeroQuarto(localNotificacaoOrigemRetornoVO.getQrtNumero());
		vo.setQuartoNotificado(localNotificacaoOrigemRetornoVO.getQrtNumeroNotificado());
		vo.setSeqUnidadesFuncional(localNotificacaoOrigemRetornoVO.getUnfSeq());
		vo.setSeqUnidadeFuncionalNotificada(localNotificacaoOrigemRetornoVO.getUnfSeqNotificado());
	}

	private void validarNotificaoTopografia(NotificacaoTopografiasVO vo) throws BaseException {
		
		BaseListException listaDeErros = new BaseListException();
		
		validarEtiologia(vo.getCodigoEtiologiaInfeccao(), listaDeErros);
		
		validarDataEncerramento(vo.getDataFim(), vo.getDataInicio(), listaDeErros);

		validarDataDeInstalacao(vo.getDataInicio(), listaDeErros);

		validarEncerramentoMotivo(vo.getDataFim(), vo.getMotivoEncerramento(), listaDeErros);
		
		validarProcedimento(vo.getProcedimentoCirurgico(), vo.getProcedimento(), vo.getPotencialContaminacao(), listaDeErros);

		if (listaDeErros.hasException()) {
			throw listaDeErros;
		}
	}
	
	private MamRespostaNotifInfeccao obterMamRespostaNotifInfeccao(Long pnnSeq, Short seqp){
		return  mamRespostaNotifInfeccaoDAO.obterPorChavePrimaria(new MamRespostaNotifInfeccaoId(pnnSeq, seqp));		
	}

	private void validarEtiologia(String codigoOrigem, BaseListException listaDeErros) {
		if(StringUtils.isBlank(codigoOrigem)){
			listaDeErros.add(new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.MSG_NOTIF_TOPO_ETIOLOGIA_MCI_00513));
		}
	}
	
	public void validarDataEncerramento(Date dataEncerramento, Date dataInstalacao, BaseListException listaDeErros) {
		if(dataEncerramento != null) {
			Date dtInstalacaoTruncada = DateUtil.truncaData(dataInstalacao);
			Date dtEncerramentoTruncada = DateUtil.truncaData(dataEncerramento);
			if(!DateUtil.validaDataMenorIgual(dtInstalacaoTruncada, new Date()) || !DateUtil.validaDataMaiorIgual(dtEncerramentoTruncada, dtInstalacaoTruncada )) {
				listaDeErros.add(new  ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.MSG_NOTIF_TOPO_DATA_ENCERRAMENTO_ATUAL_MCI_00306));
			}
		}
	}
	
	/**
	 * A data de instalação deve ser menor ou igual à data de hoje. Também
	 * deve ser maior ou igual à data de hoje menos parâmetro
	 * P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO. Caso negativo exibe
	 * Mensagem_Data_Instalacao
	 * @throws BaseException 
	 */
	public void validarDataDeInstalacao(Date dataInstalacao, BaseListException listaDeErros) throws BaseException {
		Long paramDias = obterParametroLimiteDiasCadastroNotificacao();
		
		if(DateUtil.validaDataMaior(DateUtil.truncaData(dataInstalacao), new Date()) || 
				obterDiferencaEntreDatas(dataInstalacao) > paramDias){
			listaDeErros.add(new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.MSG_NOTIF_DATA_INSTALACAO, paramDias));
		}
	}
	
	private Integer obterDiferencaEntreDatas(Date dataInstalacao){
		return DateUtil.calcularDiasEntreDatas(dataInstalacao, new Date(System.currentTimeMillis()));
	}
	
	private Long obterParametroLimiteDiasCadastroNotificacao() throws ApplicationBusinessException {
		return obterParametro(AghuParametrosEnum.P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO);	
	}
	
	private Long obterParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {		
		try {
			return parametroFacade.buscarValorLong(parametrosEnum);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.ERRO_P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO);
		}		
	}
	
	/**
	 * se dataFim ou motivoEncerramento for diferente de null, é porque o
	 * usuário esta tentado encerrar, então obrigatoriamente os mesmo deveram
	 * ser informados, caso não seja encerramento os mesmo não deve ser informados.
	 * 
	 * @param dataFim
	 * @param motivoEncerramento
	 * @param exceptionCode
	 * @throws ApplicationBusinessException
	 */
	public void validarEncerramentoMotivo(Date dataFim,
			DominioMotivoEncerramentoNotificacao motivoEncerramento,
			BaseListException listaDeErros) {

		if ((dataFim != null || motivoEncerramento != null)) {
			if(dataFim == null || motivoEncerramento == null){
				listaDeErros.add(new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.MSG_NOTIF_TOPO_VALIDACAO_ENCERRAMENTO_MCI_00262));
			}
		}
	}
	
	public void validarProcedimento(Integer procedimentoCirurgico, ProcedRealizadoVO procedimento,
			DominioIndContaminacao potencialContaminacao, BaseListException listaDeErros) {
		if (procedimentoCirurgico.compareTo(1) == 0 && (procedimento == null || potencialContaminacao == null)) {
			listaDeErros.add(new ApplicationBusinessException(NotificacaoTopografiaONExceptionCode.MSG_NOTIF_TOPO_VALIDACAO_CIRURGIA_ASSOCIADA));
		}
	}
}
