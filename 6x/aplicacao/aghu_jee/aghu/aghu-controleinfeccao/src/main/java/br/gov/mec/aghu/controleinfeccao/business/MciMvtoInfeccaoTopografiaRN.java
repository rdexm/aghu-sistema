package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.business.MciTopografiaProcedimentoRN.MciTopografiaProcedimentoRNExceptionCode;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoInfeccaoTopografiasDAO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MciMvtoInfeccaoTopografiaRN extends BaseBusiness {

	private static final long serialVersionUID = -951259121516126857L;

	private static final Log LOG = LogFactory.getLog(MciMvtoInfeccaoTopografiaRN.class);
	
	public enum MciMvtoInfeccaoTopografiaRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH;
	}

	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
		
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private MciMvtoInfeccaoTopografiJnBuilder jnBuilder;
	
	@Inject
	private MciMvtoInfeccaoTopografiasDAO infeccaoTopografiasDAO;

	@Inject	
	private MciMvtoInfeccaoTopografiJnDAO mvtoInfeccaoTopografiJnDAO;

	@Override
	protected Log getLogger() {
		return LOG;
	}

	private void notNull(Object object) throws ApplicationBusinessException {
		controleInfeccaoRN.notNull(object, MciTopografiaProcedimentoRNExceptionCode.ERRO_PERSISTENCIA_CCIH);
	}

	public void persistir(MciMvtoInfeccaoTopografias entity) throws ApplicationBusinessException {
		notNull(entity);

		if (entity.getSeq() == null) {
			inserir(entity);
		} else {
			atualizar(entity);
		}
	}

	private void atualizar(MciMvtoInfeccaoTopografias entity) throws ApplicationBusinessException {
		
		MciMvtoInfeccaoTopografias original = infeccaoTopografiasDAO.obterOriginal(entity.getSeq());
		MciMvtoInfeccaoTopografias entidadeAtualizada = infeccaoTopografiasDAO.obterPorChavePrimaria(entity.getSeq());
		
		entidadeAtualizada.setDataInicio(entity.getDataInicio());
		entidadeAtualizada.setEtiologiaInfeccao(entity.getEtiologiaInfeccao());
		entidadeAtualizada.setTopografiaProcedimento(entity.getTopografiaProcedimento());
		entidadeAtualizada.setProcDescricoes(entity.getProcDescricoes());
		entidadeAtualizada.setIndContaminacao(entity.getIndContaminacao());
		
		if(validarAtendimento(entity, original)){
			entidadeAtualizada.setAtendimento(entity.getAtendimento());
			entidadeAtualizada.setLeito(entity.getLeito());
			entidadeAtualizada.setLeitoNotificado(entity.getLeitoNotificado());
			entidadeAtualizada.setQuarto(entity.getQuarto());
			entidadeAtualizada.setQuartoNotificado(entity.getQuartoNotificado());
			entidadeAtualizada.setUnidadeFuncional(entity.getUnidadeFuncional());
			entidadeAtualizada.setUnidadeFuncionalNotificada(entity.getUnidadeFuncionalNotificada());
		}
		
		if(validarConfirmacao(entity.getConfirmacaoCci(), original.getConfirmacaoCci())){
			entidadeAtualizada.setConfirmacaoCci(entity.getConfirmacaoCci());
			entidadeAtualizada.setServidorConfirmado(servidorLogadoFacade.obterServidorLogado());
		}
		
		if(validarEncerramento(entity, original)){
			entidadeAtualizada.setDataFim(entity.getDataFim());
			entidadeAtualizada.setMotivoEncerramento(entity.getMotivoEncerramento());
			entidadeAtualizada.setServidorEncerrado(servidorLogadoFacade.obterServidorLogado());
		}

		preAtualizar(original, entidadeAtualizada);

	}

	private boolean validarConfirmacao(DominioConfirmacaoCCI confirmacao, DominioConfirmacaoCCI confirmacaoCciOriginal){
		return (confirmacao != null || ObjectUtils.notEqual(confirmacao, confirmacaoCciOriginal));
	}
	
	private boolean validarEncerramento(MciMvtoInfeccaoTopografias entity, MciMvtoInfeccaoTopografias entityOriginal){
		return(isNotNullDataFimEMotivoEncerramento(entity) || isAlteradosDataFimEMotivoEncerramento(entity, entityOriginal));
	}

	private boolean isAlteradosDataFimEMotivoEncerramento( MciMvtoInfeccaoTopografias entity, MciMvtoInfeccaoTopografias entityOriginal) {
		return ObjectUtils.notEqual(entity.getDataFim(), (entityOriginal != null ?  entityOriginal.getDataFim() : null)) && 
				ObjectUtils.notEqual(entity.getMotivoEncerramento(), (entityOriginal != null ? entityOriginal.getMotivoEncerramento() : null));
	}
	
	private boolean validarAtendimento(MciMvtoInfeccaoTopografias entity, MciMvtoInfeccaoTopografias entityOriginal){
		return ObjectUtils.notEqual(entity.getAtendimento(), (entityOriginal != null ? entityOriginal.getAtendimento() : null));
	}

	private boolean isNotNullDataFimEMotivoEncerramento(MciMvtoInfeccaoTopografias entity) {
		return entity.getDataFim()!= null && entity.getMotivoEncerramento() != null;
	}
	
	private void inserir(MciMvtoInfeccaoTopografias entity) {
		
		entity.setServidor(servidorLogadoFacade.obterServidorLogado());
		entity.setCriadoEm(new Date());
		
		if(validarConfirmacao(entity.getConfirmacaoCci(), null)){
			entity.setConfirmacaoCci(entity.getConfirmacaoCci());
			entity.setServidorConfirmado(servidorLogadoFacade.obterServidorLogado());
		}
		
		if(validarEncerramento(entity, null)){
			entity.setDataFim(entity.getDataFim());
			entity.setMotivoEncerramento(entity.getMotivoEncerramento());
			entity.setServidorEncerrado(servidorLogadoFacade.obterServidorLogado());
		}
		
		infeccaoTopografiasDAO.persistir(entity);
		
	}
	
	public void remover(Integer seqEntity) throws ApplicationBusinessException {
		notNull(seqEntity);
		MciMvtoInfeccaoTopografias entity = infeccaoTopografiasDAO.obterPorChavePrimaria(seqEntity);
		createJournal(entity, DominioOperacoesJournal.DEL);
		infeccaoTopografiasDAO.remover(entity);
		
	}
	
	private boolean foiAlterado(Object a, Object b){
		return !CoreUtil.igual(a, b);
	}
	
	private void preAtualizar(final MciMvtoInfeccaoTopografias original, final MciMvtoInfeccaoTopografias alterado) throws ApplicationBusinessException {
		if(foiAlterado(original.getDataInicio(), alterado.getDataInicio()) ||
				foiAlterado(original.getEtiologiaInfeccao(), alterado.getEtiologiaInfeccao()) ||
				foiAlterado(original.getTopografiaProcedimento(), alterado.getTopografiaProcedimento()) ||
				foiAlterado(original.getAtendimento(), alterado.getAtendimento()) ||
				foiAlterado(original.getLeito(), alterado.getLeito()) ||
				foiAlterado(original.getLeitoNotificado(), alterado.getLeitoNotificado()) ||
				foiAlterado(original.getQuarto(), alterado.getQuarto()) ||
				foiAlterado(original.getQuartoNotificado(), alterado.getQuartoNotificado()) ||
				foiAlterado(original.getUnidadeFuncional(), alterado.getUnidadeFuncional()) ||
				foiAlterado(original.getUnidadeFuncionalNotificada(), alterado.getUnidadeFuncionalNotificada()) ||
				foiAlterado(original.getConfirmacaoCci(), alterado.getConfirmacaoCci()) ||
				foiAlterado(original.getServidorConfirmado(), alterado.getServidorConfirmado()) ||
				foiAlterado(original.getDataFim(), alterado.getDataFim()) ||
				foiAlterado(original.getMotivoEncerramento(), alterado.getMotivoEncerramento()) ||
				foiAlterado(original.getServidorEncerrado(), alterado.getServidorEncerrado())){
			
			createJournal(original, DominioOperacoesJournal.UPD);
		}	
	}
		
	public void createJournal(MciMvtoInfeccaoTopografias entity, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		mvtoInfeccaoTopografiJnDAO.persistir(jnBuilder.construir(entity, operacao));
	}
}
