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
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoProcedimentoRiscoJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoProcedimentoRiscosDAO;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoLocalNotificacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscoJn;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
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
public class NotificacoesProcedimentoRiscoRN extends BaseBusiness {

	private static final long serialVersionUID = -3947047013176684273L;
	
	private static final Log LOG = LogFactory.getLog(NotificacoesProcedimentoRiscoRN.class);
	
	private enum NotificacoesRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH, P_AGHU_CCIH_NRO_DIAS_ATRAS_NOTIFICACAO,MENSAGEM_DATA_INSTALACAO_NFP, MENSAGEM_PERIODO_NPR, MENSAGEM_NOTIFICACAO_ABERTA_NFP, MENSAGEM_DATA_ENCERRAMENTO_NFP,MENSAGEM_NOTIFICACAO_ABERTA_NPR,MENSAGEM_REGISTRO_EXCLUIDO;
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
	private MciMvtoProcedimentoRiscoJnDAO mciMvtoProcedimentoRiscoJnDAO;
	
	@Inject
	private MciMvtoProcedimentoRiscosDAO mciMvtoProcedimentoRiscoDAO;
			
	
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
		MciMvtoProcedimentoRiscos obj = getMciMvtoProcedimentoRiscosDAO().verificaNotificacaoAberta(seq,notSeq);
		if(obj != null && obj.getCriadoEm() != null){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_NOTIFICACAO_ABERTA_NPR);
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
	
	
	public void inserirNotificacaoProcedimentoRisco(MciMvtoProcedimentoRiscos entity) throws BaseException, ApplicationBusinessException{
		validarDataDeInstalacao(entity.getDtInicio(),NotificacoesRNExceptionCode.MENSAGEM_DATA_INSTALACAO_NFP);
		if(entity.getDtFim() == null){
			verificarNotificacaoAberta(entity.getAtendimento().getSeq(),null);
		}else{
			validarDataEncerramento(entity.getDtFim(),entity.getDtInicio());
		}
		LocalNotificacaoOrigemRetornoVO localNotificacaoOrigemRetornoVO =  null;

		localNotificacaoOrigemRetornoVO = atualizaLocalNotificacaoOrigemRN.atualizarLocalNotificacaoOrigem(
					entity.getAtendimento().getSeq(),
					null,
					DominioTipoMovimentoLocalNotificacao.MRI,
					entity.getDtInicio(),
					null);
		
		entity.setLtoLtoId(localNotificacaoOrigemRetornoVO.getLtoLtoId());
		entity.setQrtNumero(localNotificacaoOrigemRetornoVO.getQrtNumero());
		entity.setUnfSeq(localNotificacaoOrigemRetornoVO.getUnfSeq());
		entity.setLtoLtoIdNotificado(localNotificacaoOrigemRetornoVO.getLtoLtoIdNotificado());
		entity.setQrtNumeroNotificado(localNotificacaoOrigemRetornoVO.getQrtNumeroNotificado());
		entity.setUnfSeqNotificado(localNotificacaoOrigemRetornoVO.getUnfSeqNotificado());
		getMciMvtoProcedimentoRiscosDAO().persistir(entity);
	}
	
	public void persistirJournal(MciMvtoProcedimentoRiscos obj, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
	
		final MciMvtoProcedimentoRiscoJn journal = BaseJournalFactory.getBaseJournal(operacao, MciMvtoProcedimentoRiscoJn.class, servidorLogado.getUsuario());
		journal.setAtdSeq(obj.getAtendimento().getSeq());
		journal.setPorSeq(obj.getMciProcedimentoRisco().getSeq());
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setDtFim(obj.getDtFim());
		journal.setDtInicio(obj.getDtInicio());
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
		journal.setHrInicio(obj.getAtendimento().getDthrInicio());
		journal.setHrFim(obj.getAtendimento().getDthrFim());
		journal.setSerMatriculaEncerrado(obj.getSerMatriculaEncerrado());
		journal.setSerVinCodigoEncerrado(obj.getSerVinCodigoEncerrado());
		journal.setSerVinCodigo(obj.getSerVinCodigo());
		journal.setSerMatricula(obj.getSerMatricula());

		mciMvtoProcedimentoRiscoJnDAO.persistir(journal);		
	}
	
	public void atualizarNotificacaoProcedimentoRisco(MciMvtoProcedimentoRiscos obj,Integer seq) throws BaseException, ApplicationBusinessException{
		validarDataDeInstalacao(obj.getDtInicio(),NotificacoesRNExceptionCode.MENSAGEM_DATA_INSTALACAO_NFP);
		if(obj.getDtFim() == null){
			verificarNotificacaoAberta(obj.getAtendimento().getSeq(),seq);
		}else{
			validarDataEncerramento(obj.getDtFim(),obj.getDtInicio());
		}
		MciMvtoProcedimentoRiscos entity = getMciMvtoProcedimentoRiscosDAO().obterPorChavePrimaria(seq);
		if(entity == null){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_REGISTRO_EXCLUIDO);
		}

		persistirJournal(entity,DominioOperacoesJournal.UPD);
		
		if(obj.getDtFim() != null && entity.getDtFim() != null){
			if(entity.getDtFim() != obj.getDtFim()){
				RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
				entity.setSerMatriculaEncerrado(servidor.getId().getMatricula());
				entity.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
			}
		}else if (obj.getDtFim() == null && entity.getDtFim() != null){
			//RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatriculaEncerrado(null);
			entity.setSerVinCodigoEncerrado(null);
		}else if(obj.getDtFim() != null && entity.getDtFim() == null){
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatriculaEncerrado(servidor.getId().getMatricula());
			entity.setSerVinCodigoEncerrado(servidor.getId().getVinCodigo());
		}
		entity.setMciProcedimentoRisco(obj.getMciProcedimentoRisco());
		entity.setAtendimento(obj.getAtendimento());
		entity.setDtInicio(obj.getDtInicio());
		entity.setDtFim(obj.getDtFim());
		entity.setConfirmacaoCci(obj.getConfirmacaoCci());
		
		LocalNotificacaoOrigemRetornoVO localNotificacaoOrigemRetornoVO =  null;
		
		localNotificacaoOrigemRetornoVO = atualizaLocalNotificacaoOrigemRN.atualizarLocalNotificacaoOrigem(
				entity.getAtendimento().getSeq(),
				null,
				DominioTipoMovimentoLocalNotificacao.MRI,
				entity.getDtInicio(),
				null);
	
		entity.setLtoLtoId(localNotificacaoOrigemRetornoVO.getLtoLtoId());
		entity.setQrtNumero(localNotificacaoOrigemRetornoVO.getQrtNumero());
		entity.setUnfSeq(localNotificacaoOrigemRetornoVO.getUnfSeq());
		entity.setLtoLtoIdNotificado(localNotificacaoOrigemRetornoVO.getLtoLtoIdNotificado());
		entity.setQrtNumeroNotificado(localNotificacaoOrigemRetornoVO.getQrtNumeroNotificado());
		entity.setUnfSeqNotificado(localNotificacaoOrigemRetornoVO.getUnfSeqNotificado());
		
		getMciMvtoProcedimentoRiscosDAO().merge(entity);
	}
	
	public void removerNotificacaoProcedimentoRisco(Integer seq) throws BaseException, ApplicationBusinessException{
		MciMvtoProcedimentoRiscos entity = getMciMvtoProcedimentoRiscosDAO().obterPorChavePrimaria(seq);
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		validaDiasDuracaoMedidas(parametro.getVlrNumerico().intValue(),entity.getCriadoEm());

		persistirJournal(entity,DominioOperacoesJournal.DEL);
		getMciMvtoProcedimentoRiscosDAO().remover(entity);
	}
	
	public boolean validaDiasDuracaoMedidas(Integer parametroDias,Date criadoEm) throws ApplicationBusinessException{
		Date data = new Date(System.currentTimeMillis()); 
		Integer qtd = DateUtil.calcularDiasEntreDatas(criadoEm,data);
		if(qtd > parametroDias){
			throw new ApplicationBusinessException(NotificacoesRNExceptionCode.MENSAGEM_PERIODO_NPR);
		}		
		return true;
	}
	
	protected MciMvtoProcedimentoRiscosDAO getMciMvtoProcedimentoRiscosDAO() {
		return mciMvtoProcedimentoRiscoDAO;
	}
	
}
