package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.MbcProcDescricoesRN.MbcProcDescricoesRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcDescricaoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcDescricoesDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcProcDescricaoJn;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcProcDescricoesRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcProcDescricoesRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcDescricoesDAO mbcProcDescricoesDAO;

	@Inject
	private MbcProcDescricaoJnDAO mbcProcDescricaoJnDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	private static final long serialVersionUID = 130713103873103950L;
	

	public enum MbcProcDescricoesRNExceptionCode implements BusinessExceptionCode {
		MBC_00683;
	}	
	
	public void excluirMbcProcDescricoes(MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException {
		procDescricao = getMbcProcDescricoesDAO().obterPorChavePrimaria(procDescricao.getId());
		executarAntesDeExcluir(procDescricao);
		getMbcProcDescricoesDAO().remover(procDescricao);
		executarAposExcluir(procDescricao);
	}

	/**
	 * ORADB: MBCT_POD_ARD
	 */
	public void executarAposExcluir(final MbcProcDescricoes procDescricao) {
		final MbcProcDescricaoJn journal = createJournal(procDescricao, DominioOperacoesJournal.DEL);
		getMbcProcDescricaoJnDAO().persistir(journal);
	}

	private MbcProcDescricaoJn createJournal(final MbcProcDescricoes procDescricao, DominioOperacoesJournal dominio) {
		final MbcProcDescricaoJn journal =  BaseJournalFactory.getBaseJournal(dominio, MbcProcDescricaoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setComplemento(procDescricao.getComplemento());
		journal.setCriadoEm(procDescricao.getCriadoEm());
		journal.setDcgCrgSeq(procDescricao.getId().getDcgCrgSeq());
		journal.setDcgSeqp(procDescricao.getId().getDcgSeqp());
		journal.setIndContaminacao(procDescricao.getIndContaminacao());
		journal.setPciSeq(procDescricao.getProcedimentoCirurgico().getSeq());
		journal.setSeqp(procDescricao.getId().getSeqp());
		journal.setSerMatricula(procDescricao.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(procDescricao.getServidor().getId().getVinCodigo());
		
		return journal;
	}

	/**
	 * ORADB: MBCT_POD_BRD
	 */
	public void executarAntesDeExcluir(final MbcProcDescricoes mpd) throws ApplicationBusinessException, ApplicationBusinessException {
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuario que excluiu foi o mesmo que inclui
		ddRN.validaServidorExclusao(mpd.getServidor());
		
		// não permite que se exclua o registro desta tabela se a descricao estiver concluida 
		ddRN.verificaDescricaoConcluida(mpd.getMbcDescricaoCirurgica());
	}


	@SuppressWarnings("ucd")
	public void alterarMbcProcDescricoes(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
		final MbcProcDescricoesDAO dao = getMbcProcDescricoesDAO();
		final MbcProcDescricoes oldProcDescricao = dao.obterOriginal(procDescricao);
		
		executarAntesDeAlterar(procDescricao, oldProcDescricao);
		dao.atualizar(procDescricao);
		executarAposAlterar(procDescricao, oldProcDescricao);
	}
	
	/**
	 * ORADB: MBCT_POD_ARU
	 */
	public void executarAposAlterar(final MbcProcDescricoes procDescricao, final MbcProcDescricoes oldProcDescricao) {
		if( !CoreUtil.igual(procDescricao.getId().getDcgCrgSeq(), oldProcDescricao.getId().getDcgCrgSeq()) ||
				!CoreUtil.igual(procDescricao.getId().getDcgSeqp(), oldProcDescricao.getId().getDcgSeqp()) ||
				!CoreUtil.igual(procDescricao.getId().getSeqp(), oldProcDescricao.getId().getSeqp()) ||
				!CoreUtil.igual(procDescricao.getIndContaminacao(), oldProcDescricao.getIndContaminacao()) ||
				!CoreUtil.igual(procDescricao.getComplemento(), oldProcDescricao.getComplemento()) ||
				!CoreUtil.igual(procDescricao.getProcedimentoCirurgico(), oldProcDescricao.getProcedimentoCirurgico()) ||
				!CoreUtil.igual(procDescricao.getCriadoEm(), oldProcDescricao.getCriadoEm()) ||
				!CoreUtil.igual(procDescricao.getServidor(), oldProcDescricao.getServidor())
				){
			
			final MbcProcDescricaoJn journal = createJournal(oldProcDescricao, DominioOperacoesJournal.UPD);
			getMbcProcDescricaoJnDAO().persistir(journal);
		}
	}
	
	/**
	 * ORADB: MBCT_POD_BRU
	 */
	public void executarAntesDeAlterar(final MbcProcDescricoes procDescricao, final MbcProcDescricoes oldProcDescricao) throws ApplicationBusinessException, ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuário que está alterando é o mesmo que incluiu
		ddRN.verificaUsuarioInclusaoAlteracao(oldProcDescricao.getServidor());
		
		// não permite alterar o registro desta tabela, se a descricao estiver concluida
		ddRN.verificaDescricaoConcluida(oldProcDescricao.getMbcDescricaoCirurgica());
		
		// verifica se o procedimento no cadastro está ativo
		verificaProcedimentoNoCadastro(procDescricao.getProcedimentoCirurgico());
		
		// atualiza servidor que incluiu registro
		procDescricao.setServidor(servidorLogado);
	}


	public void inserirMbcProcDescricoes(final MbcProcDescricoes mpd) throws ApplicationBusinessException{
		executarAntesDeInserir(mpd);
		getMbcProcDescricoesDAO().persistir(mpd);
	}
	

	/**
	 * ORADB: MBCT_POD_BRI
	 */
	public void executarAntesDeInserir(final MbcProcDescricoes procDescricao) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		procDescricao.setCriadoEm(new Date());
		
		if(procDescricao.getMbcDescricaoCirurgica() == null){
			procDescricao.setMbcDescricaoCirurgica(getMbcDescricaoCirurgicaDAO().obterPorChavePrimaria(
															new MbcDescricaoCirurgicaId(procDescricao.getId().getDcgCrgSeq(), 
																						procDescricao.getId().getDcgSeqp()
																						)
																									  )
												  );
		}

		final ProfDescricoesRN pfdRN = getProfDescricoesRN();
		
		// Verifica se o usuario que está fazendo o insert,é quem criou a descricao cirurgica
		pfdRN.verificarServidorLogadoRealizaDescricaoCirurgica( procDescricao.getId().getDcgCrgSeq(), 
																procDescricao.getId().getDcgSeqp());
		
		// não permite inserir o registro desta tabela, se a descricao estiver concluida
		pfdRN.verificarDescricaoCirurgicaConcluida( procDescricao.getId().getDcgCrgSeq(), 
													procDescricao.getId().getDcgSeqp());
		
		// verifica se o procedimento no cadastro está ativo
		verificaProcedimentoNoCadastro(procDescricao.getProcedimentoCirurgico());

		// atualiza servidor que incluiu registro
		procDescricao.setServidor(servidorLogado);
		
		if(procDescricao.getId().getSeqp() == null){
			// Obtem próximo seqp de acordo com a cirurgia
			Integer seqp = getMbcProcDescricoesDAO().obterProximoSeqp(procDescricao.getId());
			seqp = seqp == null ? 1 : ++seqp;
			procDescricao.getId().setSeqp(seqp);
		}
		
	}
	
	public MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}

	/**
	 * ORADB: MBCK_POD_RN.RN_PODP_VER_PROCED
	 */
	public void verificaProcedimentoNoCadastro(final MbcProcedimentoCirurgicos pci) throws ApplicationBusinessException{
		if(DominioSituacao.I.equals(pci.getIndSituacao())){
			throw new ApplicationBusinessException(MbcProcDescricoesRNExceptionCode.MBC_00683);
		}
	}

	public ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}

	public DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}

	public MbcProcDescricoesDAO getMbcProcDescricoesDAO() {
		return mbcProcDescricoesDAO;
	}

	public MbcProcDescricaoJnDAO getMbcProcDescricaoJnDAO() {
		return mbcProcDescricaoJnDAO;
	}
}