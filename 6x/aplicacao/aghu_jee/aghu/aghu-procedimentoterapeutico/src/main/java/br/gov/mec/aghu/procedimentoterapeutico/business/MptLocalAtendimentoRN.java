package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptLocalAtendimentoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MptLocalAtendimentoRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2102515405489171235L;

	private static final Log LOG = LogFactory.getLog(MptLocalAtendimentoRN.class);
	
	public enum MptLocalAtendimentoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_LOCAL_POSSUI_HORARIOS_ACOMODACOES;
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;
	
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;	
	
	@Inject
	private MptLocalAtendimentoJnDAO mptLocalAtendimentoJnDAO;	
	
	public void persistirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException {
		if(mptLocalAtendimento.getSeq() != null){
			this.atualizarMptLocalAtendimento(mptLocalAtendimento);
		}else{
			this.inserirMptLocalAtendimento(mptLocalAtendimento);
		}
	}
	
	public void inserirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());

		mptLocalAtendimento.setServidor(servidorLogado);
		mptLocalAtendimento.setCriadoEm(new Date());

		this.mptLocalAtendimentoDAO.persistir(mptLocalAtendimento);
	}

	public void atualizarMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException {
		MptLocalAtendimento mptLocalAtendimentoOriginal = this.mptLocalAtendimentoDAO.obterOriginal(mptLocalAtendimento.getSeq());
		this.inserirJournalMptLocalAtendimento(mptLocalAtendimentoOriginal, DominioOperacoesJournal.UPD);
		
		this.mptLocalAtendimentoDAO.merge(mptLocalAtendimento);
	}
	
	public void excluirMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimento) throws ApplicationBusinessException {
		
		if(mptHorarioSessaoDAO.verificarExisteRegistroHorarioSessaoParaLocalAtendimento(mptLocalAtendimento.getSeq())) {
			throw new ApplicationBusinessException(MptLocalAtendimentoRNExceptionCode.MENSAGEM_LOCAL_POSSUI_HORARIOS_ACOMODACOES);
		}
		
		MptLocalAtendimento mptLocalAtendimentoOriginal = this.mptLocalAtendimentoDAO.obterOriginal(mptLocalAtendimento.getSeq());
		this.inserirJournalMptLocalAtendimento(mptLocalAtendimentoOriginal, DominioOperacoesJournal.DEL);
		
		MptLocalAtendimento excluir = this.mptLocalAtendimentoDAO.obterPorChavePrimaria(mptLocalAtendimento.getSeq());
		this.mptLocalAtendimentoDAO.remover(excluir);
	}
	
	private void inserirJournalMptLocalAtendimento(MptLocalAtendimento mptLocalAtendimentoOriginal, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		MptLocalAtendimentoJn mptLocalAtendimentoJn = BaseJournalFactory.getBaseJournal(
				operacao, MptLocalAtendimentoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		mptLocalAtendimentoJn.setSeq(mptLocalAtendimentoOriginal.getSeq());
		mptLocalAtendimentoJn.setCriadoEm(mptLocalAtendimentoOriginal.getCriadoEm());
		mptLocalAtendimentoJn.setDescricao(mptLocalAtendimentoOriginal.getDescricao());
		mptLocalAtendimentoJn.setIndSituacao(mptLocalAtendimentoOriginal.getIndSituacao());
		mptLocalAtendimentoJn.setSeqSal(mptLocalAtendimentoOriginal.getSala() != null ? mptLocalAtendimentoOriginal.getSala().getSeq() : null);
		mptLocalAtendimentoJn.setIndReserva(mptLocalAtendimentoOriginal.getIndReserva());
		mptLocalAtendimentoJn.setTipoLocal(mptLocalAtendimentoOriginal.getTipoLocal());
		mptLocalAtendimentoJn.setSerMatricula(mptLocalAtendimentoOriginal.getServidor().getId().getMatricula());
		mptLocalAtendimentoJn.setSerVinCodigo(mptLocalAtendimentoOriginal.getServidor().getId().getVinCodigo());
		this.mptLocalAtendimentoJnDAO.persistir(mptLocalAtendimentoJn);
	}
	

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	public List<MptLocalAtendimento> buscarLocalAtendimento(Object parametro, Short codigoSala) {
		
		return mptLocalAtendimentoDAO.buscarLocalAtendimento(parametro, codigoSala);
	}

	
	public Long buscarLocalAtendimentoCount(Object parametro, Short codigoSala) {
		
		return mptLocalAtendimentoDAO.buscarLocalAtendimentoCount(parametro, codigoSala);
	}
}
