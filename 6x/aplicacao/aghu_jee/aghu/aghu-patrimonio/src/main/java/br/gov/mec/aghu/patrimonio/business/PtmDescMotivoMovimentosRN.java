package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.PtmDescMotivoMovimentosJN;
import br.gov.mec.aghu.model.PtmDescMotivoMovimentos;
import br.gov.mec.aghu.model.PtmSituacaoMotivoMovimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmDescMotivoMovimentosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmDescMotivoMovimentosJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmSituacaoMotivoMovimentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PtmDescMotivoMovimentosRN extends BaseBusiness implements Serializable{
	
	private static final long serialVersionUID = -8520813001170463504L;

	private static final Log LOG = LogFactory.getLog(PtmDescMotivoMovimentosRN.class);
	
	@Inject
	private PtmDescMotivoMovimentosDAO ptmDescMotivoMovimentosDAO;
	
	@Inject 
	private PtmDescMotivoMovimentosJnDAO ptmDescMotivoMovimentosJnDAO;
	
	@Inject 
	private PtmSituacaoMotivoMovimentoDAO ptmSituacaoMotivoMovimentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void inserirPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException {
		RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento = this.ptmSituacaoMotivoMovimentoDAO.obterPorChavePrimaria(ptmDescMotivoMovimentos.getPtmSituacaoMotivoMovimento().getSeq());

		ptmDescMotivoMovimentos.setPtmSituacaoMotivoMovimento(ptmSituacaoMotivoMovimento);
		ptmDescMotivoMovimentos.setServidor(servidorLogado);
		ptmDescMotivoMovimentos.setServidorUltimaAlteracao(servidorLogado);
		ptmDescMotivoMovimentos.setDatainclusao(new Date());
		ptmDescMotivoMovimentos.setDataUltimaAlteracao(new Date());
		
		ptmDescMotivoMovimentosDAO.persistir(ptmDescMotivoMovimentos);		
	}
	
	public void atualizarPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException {
		PtmDescMotivoMovimentos ptmDescMotivoMovimentosOriginal = ptmDescMotivoMovimentosDAO.obterPorChavePrimaria(ptmDescMotivoMovimentos.getSeq());
		PtmSituacaoMotivoMovimento ptmSituacaoMotivoMovimento = this.ptmSituacaoMotivoMovimentoDAO.obterPorChavePrimaria(ptmDescMotivoMovimentos.getPtmSituacaoMotivoMovimento().getSeq());
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		this.inserirJournalPtmDescMotivoMovimentos(ptmDescMotivoMovimentosOriginal, DominioOperacoesJournal.UPD);

		ptmDescMotivoMovimentos.setPtmSituacaoMotivoMovimento(ptmSituacaoMotivoMovimento);
		ptmDescMotivoMovimentos.setServidorUltimaAlteracao(servidorLogado);
		ptmDescMotivoMovimentos.setDataUltimaAlteracao(new Date());
		
		ptmDescMotivoMovimentosDAO.merge(ptmDescMotivoMovimentos);
		ptmDescMotivoMovimentosDAO.flush();
	}

	public void excluirPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentos) throws ApplicationBusinessException {
		PtmDescMotivoMovimentos ptmDescMotivoMovimentosOriginal = ptmDescMotivoMovimentosDAO.obterPorChavePrimaria(ptmDescMotivoMovimentos.getSeq());
		this.inserirJournalPtmDescMotivoMovimentos(ptmDescMotivoMovimentosOriginal, DominioOperacoesJournal.DEL);
		
		PtmDescMotivoMovimentos excluir = ptmDescMotivoMovimentosDAO.obterPorChavePrimaria(ptmDescMotivoMovimentos.getSeq());
		
		this.ptmDescMotivoMovimentosDAO.remover(excluir);
		this.ptmDescMotivoMovimentosDAO.flush();
	}
	
	private void inserirJournalPtmDescMotivoMovimentos(PtmDescMotivoMovimentos ptmDescMotivoMovimentosOriginal, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		PtmDescMotivoMovimentosJN ptmDescMotivoMotivimentosJN = BaseJournalFactory.getBaseJournal(
				operacao, PtmDescMotivoMovimentosJN.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		ptmDescMotivoMotivimentosJN.setSeqJN(ptmDescMotivoMovimentosOriginal.getSeq());
		ptmDescMotivoMotivimentosJN.setPtmSituacaoMotivoMovimento(ptmDescMotivoMovimentosOriginal.getPtmSituacaoMotivoMovimento());
		ptmDescMotivoMotivimentosJN.setDescricao(ptmDescMotivoMovimentosOriginal.getDescricao());
		ptmDescMotivoMotivimentosJN.setAtivo(ptmDescMotivoMovimentosOriginal.getAtivo());
		ptmDescMotivoMotivimentosJN.setJustificativaObrig(ptmDescMotivoMovimentosOriginal.getJustificativaObrig());
		ptmDescMotivoMotivimentosJN.setDatainclusao(ptmDescMotivoMovimentosOriginal.getDatainclusao());
		ptmDescMotivoMotivimentosJN.setDataUltimaAlteracao(ptmDescMotivoMovimentosOriginal.getDataUltimaAlteracao());
		ptmDescMotivoMotivimentosJN.setServidor(ptmDescMotivoMovimentosOriginal.getServidor());
		ptmDescMotivoMotivimentosJN.setMatriculaServidorUltimaAlt(ptmDescMotivoMovimentosOriginal.getServidorUltimaAlteracao().getId().getMatricula());
		ptmDescMotivoMotivimentosJN.setVinculoServidor(ptmDescMotivoMovimentosOriginal.getServidorUltimaAlteracao().getId().getVinCodigo());
		this.ptmDescMotivoMovimentosJnDAO.persistir(ptmDescMotivoMotivimentosJN);		
	}
}
