package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.business.MciTopografiaProcedimentoRN.MciTopografiaProcedimentoRNExceptionCode;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivaJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MciMvtoMedidaPreventivaRN extends BaseBusiness {


	private static final long serialVersionUID = -3085799022000783194L;
	
	private static final Log LOG = LogFactory.getLog(MciMvtoMedidaPreventivaRN.class);
	
	public enum MciMvtoMedidaPreventivaRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH;
	}

	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private MciMvtoMedidaPreventivaJnBuilder jnBuilder;
	
	@Inject
	private MciMvtoMedidaPreventivasDAO mvtoMedidaPreventivasDAO;
	
	@Inject
	private MciMvtoMedidaPreventivaJnDAO medidaPreventivaJnDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private void notNull(Object object) throws ApplicationBusinessException {
		controleInfeccaoRN.notNull(object, MciTopografiaProcedimentoRNExceptionCode.ERRO_PERSISTENCIA_CCIH);
	}

	public void persistir(MciMvtoMedidaPreventivas entity) throws ApplicationBusinessException {
		notNull(entity);

		if (entity.getSeq() == null) {
			inserir(entity);
		} else {
			atualizar(entity);
		}
	}

	private void atualizar(MciMvtoMedidaPreventivas entity) throws ApplicationBusinessException {
		
		MciMvtoMedidaPreventivas original = mvtoMedidaPreventivasDAO.obterOriginal(entity.getSeq());
		MciMvtoMedidaPreventivas entidadeAtualizada = mvtoMedidaPreventivasDAO.obterPorChavePrimaria(entity.getSeq());
		
		entidadeAtualizada.setDataInicio(entity.getDataInicio());
		entidadeAtualizada.setMciEtiologiaInfeccao(entity.getMciEtiologiaInfeccao());
		entidadeAtualizada.setPatologiaInfeccao(entity.getPatologiaInfeccao());
		
		// caso tenha sido alterado o atendimento
		if(validarAtendimento(entity, original)){
			entidadeAtualizada.setAtendimento(entity.getAtendimento());
			entidadeAtualizada.setLeito(entity.getLeito());
			entidadeAtualizada.setLeitoNotificado(entity.getLeitoNotificado());
			entidadeAtualizada.setQuarto(entity.getQuarto());
			entidadeAtualizada.setQuartoNotificado(entity.getQuartoNotificado());
			entidadeAtualizada.setUnidadeFuncional(entity.getUnidadeFuncional());
			entidadeAtualizada.setUnidadeFuncionalNotificada(entity.getUnidadeFuncionalNotificada());
		} 
		
		// caso tenha sido preenchido ou alterado o campo "confirmacao CCIH"
		if(validarConfirmacao(entity.getConfirmacaoCci(), original.getConfirmacaoCci())){
			entidadeAtualizada.setConfirmacaoCci(entity.getConfirmacaoCci());
			entidadeAtualizada.setServidorConfirmado(servidorLogadoFacade.obterServidorLogado());
		}
		
		// caso tenha sido preenchido ou alterado o campo dataFim ou motivoEncerramento
		if(validarEncerramento(entity, original)){
			entidadeAtualizada.setDataFim(entity.getDataFim());
			entidadeAtualizada.setMotivoEncerramento(entity.getMotivoEncerramento());
			entidadeAtualizada.setServidorEncerrado(servidorLogadoFacade.obterServidorLogado());
		}
		
		preAtualizar(original, entidadeAtualizada);
	}

	private void inserir(MciMvtoMedidaPreventivas entity) throws ApplicationBusinessException {
		notNull(entity);
		entity.setServidor(servidorLogadoFacade.obterServidorLogado());
		entity.setCriadoEm(new Date());
		mvtoMedidaPreventivasDAO.persistir(entity);
	}
	
	public void remover(Integer entitySeq) throws ApplicationBusinessException {
		notNull(entitySeq);
		MciMvtoMedidaPreventivas entity = mvtoMedidaPreventivasDAO.obterPorChavePrimaria(entitySeq);
		createJournal(entity, DominioOperacoesJournal.DEL);
		mvtoMedidaPreventivasDAO.removerPorId(entitySeq);
	}
	
	private boolean foiAlterado(Object a, Object b){
		return !CoreUtil.igual(a, b);
	}
	
	private void preAtualizar(final MciMvtoMedidaPreventivas original, final MciMvtoMedidaPreventivas alterado) throws ApplicationBusinessException {
		if(foiAlterado(original.getDataInicio(), alterado.getDataInicio()) ||
				foiAlterado(original.getMciEtiologiaInfeccao(), alterado.getMciEtiologiaInfeccao()) ||
				foiAlterado(original.getPatologiaInfeccao(), alterado.getPatologiaInfeccao()) ||
				validarAtendimento(original, alterado) ||
				validarConfirmacao(original.getConfirmacaoCci(), alterado.getConfirmacaoCci()) ||
				validarEncerramento(original, alterado)){
			
			createJournal(original, DominioOperacoesJournal.UPD);
		}
	}
		
	public void createJournal(MciMvtoMedidaPreventivas entity, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		medidaPreventivaJnDAO.persistir(jnBuilder.construir(entity, operacao));
	}
	
	private boolean validarConfirmacao(DominioConfirmacaoCCI confirmacao, DominioConfirmacaoCCI confirmacaoCciOriginal){
		return (confirmacao != null || ObjectUtils.notEqual(confirmacao, confirmacaoCciOriginal));
	}
	
	private boolean validarEncerramento(MciMvtoMedidaPreventivas entity, MciMvtoMedidaPreventivas entityOriginal){
		return(isNotNullDataFimEMotivoEncerramento(entity) || isAlteradosDataFimEMotivoEncerramento(entity, entityOriginal));
	}

	private boolean isAlteradosDataFimEMotivoEncerramento(MciMvtoMedidaPreventivas entity, MciMvtoMedidaPreventivas entityOriginal) {
		return ObjectUtils.notEqual(entity.getDataFim(), entityOriginal.getDataFim()) && 
				ObjectUtils.notEqual(entity.getMotivoEncerramento(), entityOriginal.getMotivoEncerramento());
	}

	private boolean isNotNullDataFimEMotivoEncerramento(MciMvtoMedidaPreventivas entity) {
		return entity.getDataFim()!= null && entity.getMotivoEncerramento() != null;
	}
	
	private boolean validarAtendimento(MciMvtoMedidaPreventivas entity, MciMvtoMedidaPreventivas entityOriginal){
		return ObjectUtils.notEqual(entity.getAtendimento(), entityOriginal.getAtendimento());
	}

}
