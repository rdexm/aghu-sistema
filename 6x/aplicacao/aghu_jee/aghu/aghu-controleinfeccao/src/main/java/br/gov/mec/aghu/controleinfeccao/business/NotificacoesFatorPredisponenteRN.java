package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.LocalNotificacaoOrigemRetornoVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoFatorPredisponentJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoFatorPredisponentesDAO;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoLocalNotificacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentJn;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class NotificacoesFatorPredisponenteRN extends BaseBusiness {

	private static final long serialVersionUID = -3947047013176684273L;
	
	private static final Log LOG = LogFactory.getLog(NotificacoesFatorPredisponenteRN.class);
	
	private enum NotificacoesRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH, P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO,MENSAGEM_DATA_INSTALACAO_NFP, MENSAGEM_PERIODO_NFP, MENSAGEM_NOTIFICACAO_ABERTA_NFP, MENSAGEM_DATA_ENCERRAMENTO_NFP,MENSAGEM_REGISTRO_EXCLUIDO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private AtualizaLocalNotificacaoOrigemRN atualizaLocalNotificacaoOrigemRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciMvtoFatorPredisponentesDAO mciMvtoFatorPredisponentesDAO;
	
	@Inject
	private MciMvtoFatorPredisponentJnDAO mciMvtoFatorPredisponentJnDAO;
	
	public void validarNumeroDiasDecorridosCriacaoRegistro(Date dataCriacao, BusinessExceptionCode exceptionCode)throws BaseException {
		controleInfeccaoRN.validarNumeroDiasDecorridosCriacaoRegistro(dataCriacao, exceptionCode);
	}
	
	/**
	 * 3. A data de instalação deve ser menor ou igual à data de hoje. Também
	 * deve ser maior ou igual à data de hoje menos parâmetro
	 * P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO. Caso negativo exibe
	 * Mensagem_Data_Instalacao
	 */
	public void validarDataDeInstalacao(Date dataInstalacao, BusinessExceptionCode exceptionCode)throws BaseException, ApplicationBusinessException {
		Long paramDias = obterParametroLimiteDiasCadastroNotificacao();
		if(DateUtil.validaDataMaior(DateUtil.truncaData(dataInstalacao), new Date()) || 
				obterDiferencaEntreDatas(dataInstalacao) > paramDias){
			throw new ApplicationBusinessException(exceptionCode, paramDias);
		}
	}
	
	private Integer obterDiferencaEntreDatas(Date dataInstalacao){
		return DateUtil.calcularDiasEntreDatas(dataInstalacao, new Date(System.currentTimeMillis()));
	}
	
	private Long obterParametroLimiteDiasCadastroNotificacao() throws BaseException {
		return obterParametro(AghuParametrosEnum.P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO);	
	}
	
	private Long obterParametro(AghuParametrosEnum parametrosEnum) throws BaseException {		
		try {
			return parametroFacade.buscarValorLong(parametrosEnum);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO);
		}		
	}
	
	public void verificarNotificacaoAberta(Integer seq,Integer notSeq) throws ApplicationBusinessException{
		MciMvtoFatorPredisponentes obj = getMciMvtoFatorPredisponentesDAO().verificaNotificacaoAberta(seq,notSeq);
		if(obj != null && obj.getCriadoEm() != null){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_NOTIFICACAO_ABERTA_NFP);
		}
	}
	
	public void validarDataEncerramento(Date dataEncerramento, Date dataInstalacao) throws ApplicationBusinessException{
		if(!DateUtil.validaDataMenorIgual(dataEncerramento, new Date())){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_DATA_ENCERRAMENTO_NFP);
		}
		if(!DateUtil.validaDataMaiorIgual(dataEncerramento, dataInstalacao)){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_DATA_ENCERRAMENTO_NFP);
		}
	}
	
	
	public void inserirNotificacaoFatorPredisponente(MciMvtoFatorPredisponentes entity) throws BaseException, ApplicationBusinessException{
		validarDataDeInstalacao(entity.getDataInicio(),NotificacoesRNExceptionCode.MENSAGEM_DATA_INSTALACAO_NFP);
		if(entity.getDataFim() == null){
			verificarNotificacaoAberta(entity.getAtendimento().getSeq(),null);
		}else{
			validarDataEncerramento(entity.getDataFim(),entity.getDataInicio());
		}
		LocalNotificacaoOrigemRetornoVO localNotificacaoOrigemRetornoVO =  null;

		localNotificacaoOrigemRetornoVO = atualizaLocalNotificacaoOrigemRN.atualizarLocalNotificacaoOrigem(
					entity.getAtendimento().getSeq(),
					null,
					DominioTipoMovimentoLocalNotificacao.MFP,
					entity.getDataInicio(),
					null);
		
		entity.setLtoLtoId(localNotificacaoOrigemRetornoVO.getLtoLtoId());
		entity.setQrtNumero(localNotificacaoOrigemRetornoVO.getQrtNumero());
		entity.setUnfSeq(localNotificacaoOrigemRetornoVO.getUnfSeq());
		entity.setLtoLtoIdNotificado(localNotificacaoOrigemRetornoVO.getLtoLtoIdNotificado());
		entity.setQrtNumeroNotificado(localNotificacaoOrigemRetornoVO.getQrtNumeroNotificado());
		entity.setUnfSeqNotificado(localNotificacaoOrigemRetornoVO.getUnfSeqNotificado());
		getMciMvtoFatorPredisponentesDAO().persistir(entity);
	}
	
	public void persistirJournal(MciMvtoFatorPredisponentes obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
	
		final MciMvtoFatorPredisponentJn journal = BaseJournalFactory.getBaseJournal(operacao, MciMvtoFatorPredisponentJn.class, servidorLogado.getUsuario());
		journal.setAtdSeq(obj.getAtendimento().getSeq());
		journal.setCifSeq(obj.getCifSeq());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setDtFim(obj.getDataFim());
		journal.setDtInicio(obj.getDataInicio());
		journal.setFpdSeq(obj.getFatorPredisponente().getSeq());
		journal.setIndGmr(obj.getIndGmr());
		journal.setIndIsolamento(obj.getIsolamento());
		journal.setLtoLtoId(obj.getLtoLtoId());
		journal.setLtoLtoIdNotificado(obj.getLtoLtoIdNotificado());
		journal.setPacCodigo(obj.getPaciente().getCodigo());
		journal.setQrtNumero(obj.getQrtNumero());
		journal.setQrtNumeroNotificado(obj.getQrtNumeroNotificado());
		journal.setSeq(obj.getSeq());
		journal.setSerMatricula(obj.getSerMatricula());
		journal.setSerVinCodigo(obj.getSerVinCodigo());
		journal.setSerMatriculaEncerrado(obj.getSerMatriculaEncerrado());
		journal.setSerVinCodigoEncerrado(obj.getSerVinCodigoEncerrado());
		journal.setUnfSeq(obj.getUnfSeq());
		journal.setUnfSeqNotificado(obj.getUnfSeqNotificado());
		//journal.setDescricao(obj.getDescricao());
		//journal.setIndSituacao(obj.getSituacao());
		//journal.setCriadoEm(obj.getCriadoEm());
		//journal.setAlteradoEm(obj.getAlteradoEm());
		
		//DuracaoMedidasPreventivasVO vo = getMciDuracaoMedidaPreventivasDAO().obterDuracaoMedidaPreventivaComRelacionamento(obj.getSeq());
		//journal.setSerVinCodigo(vo.getVinCodigo());
		//journal.setSerMatricula(vo.getMatricula());

		//if(vo.getMatriculaMovi() != null){
			//journal.setSerVinCodigoMovimentado(vo.getVinCodigoMovi());
			//journal.setSerMatriculaMovimentado(vo.getMatriculaMovi());
		//}
		mciMvtoFatorPredisponentJnDAO.persistir(journal);		
	}
	
	public void atualizarNotificacaoFatorPredisponente(MciMvtoFatorPredisponentes obj,Integer seq) throws BaseException, ApplicationBusinessException{
		validarDataDeInstalacao(obj.getDataInicio(),NotificacoesRNExceptionCode.MENSAGEM_DATA_INSTALACAO_NFP);
		if(obj.getDataFim() == null){
			verificarNotificacaoAberta(obj.getAtendimento().getSeq(),seq);
		}else{
			validarDataEncerramento(obj.getDataFim(),obj.getDataInicio());
		}
		MciMvtoFatorPredisponentes entity = getMciMvtoFatorPredisponentesDAO().obterPorChavePrimaria(seq);
		if(entity == null){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_REGISTRO_EXCLUIDO);
		}
		persistirJournal(entity,DominioOperacoesJournal.UPD);
		
		if(obj.getDataFim() != null && entity.getDataFim() != null){
			if(entity.getDataFim() != obj.getDataFim()){
				RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
				entity.setSerMatriculaEncerrado(servidor.getId().getMatricula());
				entity.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
			}
		}else if (obj.getDataFim() == null && entity.getDataFim() != null){
			//RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatriculaEncerrado(null);
			entity.setSerVinCodigoEncerrado(null);
		}else if(obj.getDataFim() != null && entity.getDataFim() == null){
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatriculaEncerrado(servidor.getId().getMatricula());
			entity.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
		}
		entity.setFatorPredisponente(obj.getFatorPredisponente());
		entity.setAtendimento(obj.getAtendimento());
		entity.setDataInicio(obj.getDataInicio());
		entity.setDataFim(obj.getDataFim());
		
		LocalNotificacaoOrigemRetornoVO localNotificacaoOrigemRetornoVO =  null;
		
		localNotificacaoOrigemRetornoVO = atualizaLocalNotificacaoOrigemRN.atualizarLocalNotificacaoOrigem(
				entity.getAtendimento().getSeq(),
				null,
				DominioTipoMovimentoLocalNotificacao.MFP,
				entity.getDataInicio(),
				null);
	
		entity.setLtoLtoId(localNotificacaoOrigemRetornoVO.getLtoLtoId());
		entity.setQrtNumero(localNotificacaoOrigemRetornoVO.getQrtNumero());
		entity.setUnfSeq(localNotificacaoOrigemRetornoVO.getUnfSeq());
		entity.setLtoLtoIdNotificado(localNotificacaoOrigemRetornoVO.getLtoLtoIdNotificado());
		entity.setQrtNumeroNotificado(localNotificacaoOrigemRetornoVO.getQrtNumeroNotificado());
		entity.setUnfSeqNotificado(localNotificacaoOrigemRetornoVO.getUnfSeqNotificado());
		
		getMciMvtoFatorPredisponentesDAO().merge(entity);
	}
	
	public void removerNotificacaoFatorPredisponente(Integer seq) throws BaseException, ApplicationBusinessException{
		MciMvtoFatorPredisponentes entity = getMciMvtoFatorPredisponentesDAO().obterPorChavePrimaria(seq);
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		validaDiasDuracaoMedidas(parametro.getVlrNumerico().intValue(),entity.getCriadoEm());

		persistirJournal(entity,DominioOperacoesJournal.DEL);
		getMciMvtoFatorPredisponentesDAO().remover(entity);
	}
	
	public boolean validaDiasDuracaoMedidas(Integer parametroDias,Date criadoEm) throws ApplicationBusinessException{
		Date data = new Date(System.currentTimeMillis()); 
		Integer qtd = DateUtil.calcularDiasEntreDatas(criadoEm,data);
		if(qtd > parametroDias){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_PERIODO_NFP);
		}		
		return true;
	}
	
	
	protected MciMvtoFatorPredisponentesDAO getMciMvtoFatorPredisponentesDAO() {
		return mciMvtoFatorPredisponentesDAO;
	}
	
}
