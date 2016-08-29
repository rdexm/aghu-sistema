package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.business.MciTopografiaProcedimentoRN.MciTopografiaProcedimentoRNExceptionCode;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaInfeccaoJnDAO;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaInfeccaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciTopografiaInfeccaoRN extends BaseBusiness {

	private static final long serialVersionUID = 5162590062333513693L;
	
	private static final Log LOG = LogFactory.getLog(MciTopografiaInfeccaoRN.class);
	
	public enum MciTopografiaInfeccaoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH;
	}
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciTopografiaInfeccaoDAO mciTopografiaInfeccaoDAO;

	@Inject
	private MciTopografiaInfeccaoJnDAO mciTopografiaInfeccaoJnDAO;
		
	@Inject
	private BaseJournalFactory baseJournalFactory;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private void notNull(Object object) throws ApplicationBusinessException { 
		controleInfeccaoRN.notNull(object, MciTopografiaProcedimentoRNExceptionCode.ERRO_PERSISTENCIA_CCIH);
	}
	
	public void persistir(MciTopografiaInfeccao mciTopografiaInfeccao) throws ApplicationBusinessException {
		
		notNull(mciTopografiaInfeccao);
		
		if (mciTopografiaInfeccao.getSeq() == null) {
			inserir(mciTopografiaInfeccao);
		} else {
			atualizar(mciTopografiaInfeccao);
		}
	}

	private void atualizar(MciTopografiaInfeccao mciTopografiaInfeccao) throws ApplicationBusinessException {
		
		MciTopografiaInfeccao original = mciTopografiaInfeccaoDAO.obterOriginal(mciTopografiaInfeccao.getSeq());
		MciTopografiaInfeccao entidadeAtualizada = mciTopografiaInfeccaoDAO.obterPorChavePrimaria(mciTopografiaInfeccao.getSeq());
		
		entidadeAtualizada.setDescricao(mciTopografiaInfeccao.getDescricao());
		entidadeAtualizada.setSupervisao(mciTopografiaInfeccao.getSupervisao());
		entidadeAtualizada.setSituacao(mciTopografiaInfeccao.getSituacao());
		entidadeAtualizada.setPacienteInfectado(mciTopografiaInfeccao.getPacienteInfectado());
		entidadeAtualizada.setContaInfecadoMensal(mciTopografiaInfeccao.getContaInfecadoMensal());
		entidadeAtualizada.setMovimentadoPor(servidorLogadoFacade.obterServidorLogado());
		entidadeAtualizada.setAlteradoEm(new Date());
		
		preAtualizar(original, entidadeAtualizada);
		
	}
	
	private void inserir(MciTopografiaInfeccao mciTopografiaInfeccao) throws ApplicationBusinessException {
		mciTopografiaInfeccao.setServidor(servidorLogadoFacade.obterServidorLogado());
		mciTopografiaInfeccao.setCriadoEm(new Date());
		mciTopografiaInfeccaoDAO.persistir(mciTopografiaInfeccao);
	}
	
	public void remover(Short seq) throws ApplicationBusinessException {
		notNull(seq);
		MciTopografiaInfeccao mciTopografiaInfeccao = mciTopografiaInfeccaoDAO.obterPorChavePrimaria(seq);
		mciTopografiaInfeccaoDAO.removerPorId(mciTopografiaInfeccao.getSeq());
		mciTopografiaInfeccao.setMovimentadoPor(servidorLogadoFacade.obterServidorLogado());
		mciTopografiaInfeccao.setAlteradoEm(new Date());
		createJournal(mciTopografiaInfeccao, DominioOperacoesJournal.DEL);
	}
	
	private boolean foiAlterado(Object a, Object b){
		return !CoreUtil.igual(a, b);
	}
	
	private void preAtualizar(final MciTopografiaInfeccao original, final MciTopografiaInfeccao alterado) throws ApplicationBusinessException {
		
		if( foiAlterado(original.getDescricao() ,alterado.getDescricao()) ||
			foiAlterado(original.getSupervisao() ,alterado.getSupervisao()) ||
			foiAlterado(original.getSituacao() ,alterado.getSituacao()) ||
			foiAlterado(original.getPacienteInfectado() ,alterado.getPacienteInfectado()) ||
			foiAlterado(original.getContaInfecadoMensal() ,alterado.getContaInfecadoMensal()) ||
			foiAlterado(original.getMovimentadoPor() ,alterado.getMovimentadoPor())){
			
			createJournal(original, DominioOperacoesJournal.UPD);
		}	
	}
		
	private void createJournal(MciTopografiaInfeccao mciTopografiaInfeccao, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		final MciTopografiaInfeccaoJn journal = baseJournalFactory.getBaseJournal(operacao, MciTopografiaInfeccaoJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(mciTopografiaInfeccao.getSeq());
		journal.setDescricao(mciTopografiaInfeccao.getDescricao());
		journal.setIndSituacao(mciTopografiaInfeccao.getSituacao());
		journal.setIndSupervisao(mciTopografiaInfeccao.getSupervisao());
		journal.setIndPacInfectado(mciTopografiaInfeccao.getPacienteInfectado());
		journal.setIndContaInfecMensal(mciTopografiaInfeccao.getContaInfecadoMensal());
		journal.setSerVinCodigo(mciTopografiaInfeccao.getServidor().getId().getVinCodigo());
		journal.setSerMatricula(mciTopografiaInfeccao.getServidor().getId().getMatricula());
		journal.setCriadoEm(mciTopografiaInfeccao.getCriadoEm());
		
		if(mciTopografiaInfeccao.getMovimentadoPor() != null){
			journal.setAlteradoEm(mciTopografiaInfeccao.getAlteradoEm());
			journal.setSerVinCodigoMovimentado(mciTopografiaInfeccao.getMovimentadoPor().getId().getVinCodigo());
			journal.setSerMatriculaMovimentado(mciTopografiaInfeccao.getMovimentadoPor().getId().getMatricula());
		}

		mciTopografiaInfeccaoJnDAO.persistir(journal);
	}
}
