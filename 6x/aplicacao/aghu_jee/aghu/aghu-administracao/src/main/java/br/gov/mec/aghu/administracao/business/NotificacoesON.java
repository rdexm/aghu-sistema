package br.gov.mec.aghu.administracao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.dao.AghNotificacaoDestinosDAO;
import br.gov.mec.aghu.administracao.dao.AghNotificacaoDestinosJnDAO;
import br.gov.mec.aghu.administracao.dao.AghNotificacoesDAO;
import br.gov.mec.aghu.administracao.dao.AghNotificacoesJnDAO;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.scheduler.NotificacaoJobEnum;
import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghNotificacaoDestinos;
import br.gov.mec.aghu.model.AghNotificacaoDestinosJn;
import br.gov.mec.aghu.model.AghNotificacoes;
import br.gov.mec.aghu.model.AghNotificacoesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


@Stateless
public class NotificacoesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(NotificacoesON.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = 5950500354025867167L;
	
	public enum NotificacoesONExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUIR_NOTIFICACAO_COM_DESTINO, ERRO_ADICIONAR_NOVO_DESTINO, ERRO_NUMERO_CELULAR_INVALIDO,
		ERRO_INCLUIR_NOTIFICACAO_DESCRICAO_EXISTENTE;
	}
	
	@Inject
	private AghNotificacoesDAO notificacoesDAO;
	@Inject
	private AghNotificacoesJnDAO notificacoesJnDAO;
	@Inject
	private AghNotificacaoDestinosDAO notificacaoDestinosDAO;
	@Inject
	private AghNotificacaoDestinosJnDAO notificacaoDestinosJnDAO;
	@EJB
	private ISchedulerFacade schedulerFacade;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	public void gravarNotificacao(AghNotificacoes notificacao, List<NotificacaoDestinoVO> notificacaoDestinos,
			List<Integer> notificacaoDestinosExcluidos, String nomeMicrocomputador) throws BaseException {
		if(notificacao.getSeq() == null) {
			incluirNotificacao(notificacao, nomeMicrocomputador);
		} else {
			alterarNotificacao(notificacao, nomeMicrocomputador);
		}
		for(Integer ntdSeq : notificacaoDestinosExcluidos) {
			removerDestino(ntdSeq);
		}
		for(NotificacaoDestinoVO destVO : notificacaoDestinos) {
			if(destVO.getSeq() == null) {
				criarNovoDestino(notificacao, destVO);
			} else {
				alterarDestino(destVO);
			}
		}
	}
	
	public void excluirNotificacao(AghNotificacoes notificacao) throws BaseException {
		if(notificacaoDestinosDAO.existemDestinosAssociadosDeterminadaNotificacao(notificacao.getSeq())) {
			throw new ApplicationBusinessException(NotificacoesONExceptionCode.ERRO_EXCLUIR_NOTIFICACAO_COM_DESTINO);
		}
		AghNotificacoes original = notificacoesDAO.obterOriginal(notificacao);
		
		notificacao = notificacoesDAO.obterPorChavePrimaria(notificacao.getSeq());
		notificacoesDAO.remover(notificacao);
		inserirAghNotificacoesJn(original, DominioOperacoesJournal.DEL);
		
		AghJobDetail job = schedulerFacade.obterAghJobDetailPorNome(obterNomeProcesso(notificacao));
		if(job != null) {
			this.schedulerFacade.removerAghJobDetail(job, Boolean.TRUE);
		}
	}
	
	private void incluirNotificacao(AghNotificacoes notificacao, String nomeMicrocomputador) throws BaseException {
		if(notificacoesDAO.existeNotificacaoComDeterminadaDescricao(notificacao.getDescricao())) {
			throw new ApplicationBusinessException(NotificacoesONExceptionCode.ERRO_INCLUIR_NOTIFICACAO_DESCRICAO_EXISTENTE);
		}
		notificacao.setCriadoEm(new Date());
		notificacao.setServidor(servidorLogadoFacade.obterServidorLogado());
		schedulerFacade.agendarRotinaAutomatica(obterEnumProcesso(notificacao.getNomeProcesso(), notificacao.getDescricao()),
				obterCronExpression(notificacao.getHorarioAgendamento()), null, nomeMicrocomputador, notificacao.getServidor());
		notificacoesDAO.persistir(notificacao);
	}
	
	private NotificacaoJobEnum obterEnumProcesso(String nomeProcesso, String descricaoNotificacao) {
		for(NotificacaoJobEnum jobEnum : NotificacaoJobEnum.values()) {
			if(jobEnum.getDescricao().equals(nomeProcesso)) {
				jobEnum.setTriggerName(nomeProcesso.concat("-").concat(descricaoNotificacao));
				return jobEnum;
			}
		}
		return null;
	}
	
	private String obterNomeProcesso(AghNotificacoes notificacao) {
		NotificacaoJobEnum jobEnum = obterEnumProcesso(notificacao.getNomeProcesso(), notificacao.getDescricao());
		return jobEnum.getTriggerName();
	}
	
	private String obterCronExpression(Date horaAgendamento) {
		Calendar horaAgenda = Calendar.getInstance();
		horaAgenda.setTime(horaAgendamento);
		
		StringBuilder sb = new StringBuilder();
		sb.append("0 ").append(horaAgenda.get(Calendar.MINUTE)).append(' ').append(horaAgenda.get(Calendar.HOUR_OF_DAY))
			.append(" * * ?");
		
		return sb.toString();
	}

	public void alterarNotificacao(AghNotificacoes notificacao, String nomeMicrocomputador) throws BaseException {
		AghNotificacoes original = notificacoesDAO.obterOriginal(notificacao);
		notificacao.setAlteradoEm(new Date());
		notificacao.setServidor(servidorLogadoFacade.obterServidorLogado());
		notificacoesDAO.atualizar(notificacao);
		
		if(nomeMicrocomputador != null) {
			AghJobDetail job = schedulerFacade.obterAghJobDetailPorNome(obterNomeProcesso(notificacao));
			if(job != null) {
				this.schedulerFacade.removerAghJobDetail(job, Boolean.TRUE);
			}
			schedulerFacade.agendarRotinaAutomatica(obterEnumProcesso(notificacao.getNomeProcesso(), notificacao.getDescricao()),
					obterCronExpression(notificacao.getHorarioAgendamento()), null, nomeMicrocomputador, notificacao.getServidor());
		}
		
		inserirAghNotificacoesJn(original, DominioOperacoesJournal.UPD);
	}
	
	private void inserirAghNotificacoesJn(AghNotificacoes original, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AghNotificacoesJn jn= BaseJournalFactory.getBaseJournal(operacao, AghNotificacoesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		jn.setSeq(original.getSeq());
		jn.setCriadoEm(original.getCriadoEm());
		jn.setDescricao(original.getDescricao());
		jn.setNomeProcesso(original.getNomeProcesso());
		jn.setInicioEm(original.getInicioEm());
		jn.setHorarioAgendamento(original.getHorarioAgendamento());
		jn.setIndTerminoNotificacoes(original.getIndTerminoNotificacoes());
		jn.setTerminaEm(original.getTerminaEm());
		jn.setTerminaApos(original.getTerminaApos());
		jn.setSerMatricula(original.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(original.getServidor().getId().getVinCodigo());
		
		notificacoesJnDAO.persistir(jn);
	}
	
	private void inserirAghNotificacaoDestinosJn(AghNotificacaoDestinos original, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AghNotificacaoDestinosJn jn= BaseJournalFactory.getBaseJournal(operacao, AghNotificacaoDestinosJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		jn.setSeq(original.getSeq());
		jn.setCriadoEm(original.getCriadoEm());
		jn.setAlteradoEm(original.getAlteradoEm());
		jn.setSerMatricula(original.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(original.getServidor().getId().getVinCodigo());
		jn.setSerMatriculaContato(original.getServidorContato().getId().getMatricula());
		jn.setSerVinCodigoContato(original.getServidorContato().getId().getVinCodigo());
		jn.setDddCelular(original.getDddCelular());
		jn.setCelular(original.getCelular());
		jn.setNtsSeq(original.getNotificacao().getSeq());
		
		notificacaoDestinosJnDAO.persistir(jn);
	}

	private void criarNovoDestino(AghNotificacoes notificacao,
			NotificacaoDestinoVO destinoVO) throws ApplicationBusinessException {
		RapServidores servidorDestino = registroColaboradorFacade
				.obterRapServidorPorVinculoMatricula(destinoVO.getMatriculaContato(), destinoVO.getVinCodigoContato());
		AghNotificacaoDestinos destino = new AghNotificacaoDestinos();
		destino.setDddCelular(destinoVO.getDddCelular());
		destino.setCelular(destinoVO.getCelular());
		destino.setServidorContato(servidorDestino);
		destino.setServidor(servidorLogadoFacade.obterServidorLogado());
		destino.setCriadoEm(new Date());
		destino.setNotificacao(notificacao);
		notificacaoDestinosDAO.persistir(destino);
	}
	
	private void alterarDestino(NotificacaoDestinoVO destinoVO) throws ApplicationBusinessException {
		RapServidores servidorDestino = registroColaboradorFacade
				.obterRapServidorPorVinculoMatricula(destinoVO.getMatriculaContato(), destinoVO.getVinCodigoContato());
		AghNotificacaoDestinos destino = notificacaoDestinosDAO.obterPorChavePrimaria(destinoVO.getSeq());
		AghNotificacaoDestinos original = notificacaoDestinosDAO.obterOriginal(destino);
		destino.setDddCelular(destinoVO.getDddCelular());
		destino.setCelular(destinoVO.getCelular());
		destino.setServidorContato(servidorDestino);
		destino.setServidor(servidorLogadoFacade.obterServidorLogado());
		destino.setAlteradoEm(new Date());
		notificacaoDestinosDAO.atualizar(destino);
		inserirAghNotificacaoDestinosJn(original, DominioOperacoesJournal.UPD);
	}
	
	private void removerDestino(Integer ntdSeq) {
		AghNotificacaoDestinos destino = notificacaoDestinosDAO.obterPorChavePrimaria(ntdSeq);
		AghNotificacaoDestinos original = notificacaoDestinosDAO.obterOriginal(destino);
		notificacaoDestinosDAO.remover(destino);
		inserirAghNotificacaoDestinosJn(original, DominioOperacoesJournal.DEL);
	}
	
	public void validarDestino(RapServidores servidorDestino, Long celular)
			throws ApplicationBusinessException {
		if(servidorDestino == null || celular == null) {
			throw new ApplicationBusinessException(NotificacoesONExceptionCode.ERRO_ADICIONAR_NOVO_DESTINO);
		} else if(celular.toString().length() < 10
				|| (obtemNumeroCelular(celular).toString().charAt(0) != '9'
					&& obtemNumeroCelular(celular).toString().charAt(0) != '8'
					&& obtemNumeroCelular(celular).toString().charAt(0) != '7')) {
			throw new ApplicationBusinessException(NotificacoesONExceptionCode.ERRO_NUMERO_CELULAR_INVALIDO);
		}
	}
	
	public Short obtemDddCelular(Long celular) {
		String retorno = celular.toString();
		retorno = retorno.substring(0, 2);
		return Short.valueOf(retorno);
	}
	
	public String obtemNumeroCelular(Long celular) {
		String retorno = celular.toString();
		retorno = retorno.substring(2, retorno.length());
		return retorno;
	}
	
}