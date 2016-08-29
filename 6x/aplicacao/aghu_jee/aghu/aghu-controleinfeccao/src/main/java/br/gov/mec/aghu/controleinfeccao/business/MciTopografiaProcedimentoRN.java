package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciTopografiaProcedimentoJnDAO;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.model.MciTopografiaProcedimentoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciTopografiaProcedimentoRN extends BaseBusiness {

	private static final long serialVersionUID = 5162590062333513693L;
	
	private static final Log LOG = LogFactory.getLog(MciTopografiaProcedimentoRN.class);
	
	public enum MciTopografiaProcedimentoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTENCIA_CCIH;
	}
	
	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciTopografiaProcedimentoDAO topografiaProcedimentoDAO;

	@Inject
	private MciTopografiaProcedimentoJnDAO topografiaProcedimentoJnDAO;
		
	@Inject
	private BaseJournalFactory baseJournalFactory;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private void notNull(Object object) throws ApplicationBusinessException { 
		controleInfeccaoRN.notNull(object, MciTopografiaProcedimentoRNExceptionCode.ERRO_PERSISTENCIA_CCIH);
	}
	
	public void persistir(MciTopografiaProcedimento mciTopografiaInfeccao) throws ApplicationBusinessException {
		
		notNull(mciTopografiaInfeccao);
		
		if (mciTopografiaInfeccao.getSeq() == null) {
			inserir(mciTopografiaInfeccao);
		} else {
			atualizar(mciTopografiaInfeccao);
		}
	}

	private void atualizar(MciTopografiaProcedimento topografiaProcedimento) throws ApplicationBusinessException {
		
		MciTopografiaProcedimento original = topografiaProcedimentoDAO.obterOriginal(topografiaProcedimento.getSeq());
		MciTopografiaProcedimento entidadeAtualizada = topografiaProcedimentoDAO.obterPorChavePrimaria(topografiaProcedimento.getSeq());
		
		entidadeAtualizada.setDescricao(topografiaProcedimento.getDescricao());
		entidadeAtualizada.setIndSituacao(topografiaProcedimento.getIndSituacao());
		entidadeAtualizada.setIndPermSobreposicao(topografiaProcedimento.getIndPermSobreposicao());
		entidadeAtualizada.setAlteradoEm(new Date());
		entidadeAtualizada.setMovimentadoPor(servidorLogadoFacade.obterServidorLogado());
		entidadeAtualizada.setTopografiaInfeccao(topografiaProcedimento.getTopografiaInfeccao());
		
		preAtualizar(original, entidadeAtualizada);
		
	}
	
	private void inserir(MciTopografiaProcedimento topografiaProcedimento) throws ApplicationBusinessException {
		topografiaProcedimento.setServidor(servidorLogadoFacade.obterServidorLogado());
		topografiaProcedimento.setCriadoEm(new Date());
		topografiaProcedimentoDAO.persistir(topografiaProcedimento);
	}
	
	public void remover(Short seq) throws ApplicationBusinessException {
		notNull(seq);
		MciTopografiaProcedimento entidade = topografiaProcedimentoDAO.obterPorChavePrimaria(seq);
		createJournal(entidade, DominioOperacoesJournal.DEL);
		entidade.setMovimentadoPor(servidorLogadoFacade.obterServidorLogado());
		entidade.setAlteradoEm(new Date());
		topografiaProcedimentoDAO.remover(entidade);
	}
	
	private boolean foiAlterado(Object a, Object b){
		return !CoreUtil.igual(a, b);
	}
	
	private void preAtualizar(final MciTopografiaProcedimento original, final MciTopografiaProcedimento alterado) throws ApplicationBusinessException {
		if(foiAlterado(original.getDescricao() ,alterado.getDescricao()) ||
				
			foiAlterado(original.getDescricao() ,alterado.getDescricao()) ||
			foiAlterado(original.getIndSituacao() ,alterado.getIndSituacao()) ||
			foiAlterado(original.getIndPermSobreposicao() ,alterado.getIndPermSobreposicao()) ||
			foiAlterado(original.getTopografiaInfeccao() ,alterado.getTopografiaInfeccao()) ||
			foiAlterado(original.getCriadoEm() ,alterado.getCriadoEm()) ||
			foiAlterado(original.getServidor() ,alterado.getServidor()) ||
			foiAlterado(original.getMovimentadoPor() ,alterado.getMovimentadoPor())){
			
			createJournal(original, DominioOperacoesJournal.UPD);
		}	
	}
		
	private void createJournal(MciTopografiaProcedimento topografiaProcedimento, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		final MciTopografiaProcedimentoJn journal = baseJournalFactory.getBaseJournal(operacao, MciTopografiaProcedimentoJn.class, servidorLogado.getUsuario());

		journal.setSeq(topografiaProcedimento.getSeq());
		journal.setDescricao(topografiaProcedimento.getDescricao());
		journal.setIndSituacao(topografiaProcedimento.getIndSituacao());
		journal.setIndPermSobreposicao(topografiaProcedimento.getIndPermSobreposicao());
		journal.setCriadoEm(topografiaProcedimento.getCriadoEm());
		journal.setSerMatricula(topografiaProcedimento.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(topografiaProcedimento.getServidor().getId().getVinCodigo());
		journal.setToiSeq(topografiaProcedimento.getTopografiaInfeccao().getSeq());
		
		if(topografiaProcedimento.getMovimentadoPor() != null){
			journal.setAlteradoEm(topografiaProcedimento.getAlteradoEm());
			journal.setSerVinCodigoMovimentado(topografiaProcedimento.getMovimentadoPor().getId().getVinCodigo());
			journal.setSerMatriculaMovimentado(topografiaProcedimento.getMovimentadoPor().getId().getMatricula());
		}

		topografiaProcedimentoJnDAO.persistir(journal);
	}

}
