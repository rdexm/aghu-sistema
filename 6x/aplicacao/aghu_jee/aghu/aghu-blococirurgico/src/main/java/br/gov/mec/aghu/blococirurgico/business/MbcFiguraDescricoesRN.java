package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcFiguraDescricaoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFiguraDescricoesDAO;
import br.gov.mec.aghu.model.MbcFiguraDescricaoJn;
import br.gov.mec.aghu.model.MbcFiguraDescricoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcFiguraDescricoesRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcFiguraDescricoesRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcFiguraDescricaoJnDAO mbcFiguraDescricaoJnDAO;

	@Inject
	private MbcFiguraDescricoesDAO mbcFiguraDescricoesDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	private static final long serialVersionUID = -2644990561085330345L;

	public void excluirMbcFiguraDescricoes(final MbcFiguraDescricoes figuraDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
		antesDeExcluir(figuraDescricao);
		getMbcFiguraDescricoesDAO().remover(figuraDescricao);
		depoisDeExcluir(figuraDescricao);
	}
	
	/**
	 * ORADB: MBCT_FDC_BRD
	 */
	void antesDeExcluir(final MbcFiguraDescricoes figuraDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();

		// verifica se o usuario que excluiu foi o mesmo que inclui
		ddRN.validaServidorExclusao(figuraDescricao.getServidor());
		
		// não permite que se exclua o registro desta tabela se a descricao estiver concluida
		ddRN.verificaDescricaoConcluida(figuraDescricao.getMbcDescricaoCirurgica());
	}

	
	/**
	 * ORADB: MBCT_FDC_ARD
	 */
	void depoisDeExcluir(final MbcFiguraDescricoes figuraDescricao) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MbcFiguraDescricaoJn jn = createJournal(figuraDescricao, servidorLogado.getUsuario(), DominioOperacoesJournal.DEL);
		getMbcFiguraDescricaoJnDAO().persistir(jn);
	}
	
	@SuppressWarnings("ucd")
	public void alterarMbcFiguraDescricoes(final MbcFiguraDescricoes figuraDescricao)
			throws ApplicationBusinessException, ApplicationBusinessException {
		
		final MbcFiguraDescricoesDAO dao = getMbcFiguraDescricoesDAO();
		final MbcFiguraDescricoes oldFiguraDescricao = dao.obterOriginal(figuraDescricao);
		
		antesDeAlterar(figuraDescricao, oldFiguraDescricao);
		dao.persistir(figuraDescricao);
		depoisDeAlterar(figuraDescricao, oldFiguraDescricao);
	}
	
	/**
	 * ORADB: MBCT_FDC_BRU
	 */
	void antesDeAlterar(final MbcFiguraDescricoes figuraDescricao, final MbcFiguraDescricoes oldFiguraDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuário que está alterando é o mesmo que incluiu
		ddRN.verificaUsuarioInclusaoAlteracao(oldFiguraDescricao.getServidor());
		
		// não permite alterar o registro desta tabela, se a descricao estiver concluida
		ddRN.verificaDescricaoConcluida(oldFiguraDescricao.getMbcDescricaoCirurgica());

		// atualiza servidor que alterou registro
		figuraDescricao.setServidor(servidorLogado);
	}

	/**
	 * ORADB: MBCT_FDC_ARU
	 */
	void depoisDeAlterar(final MbcFiguraDescricoes figuraDescricao, final MbcFiguraDescricoes oldFiguraDescricao) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!CoreUtil.igual(figuraDescricao.getId().getDcgCrgSeq(), oldFiguraDescricao.getId().getDcgCrgSeq()) ||
				!CoreUtil.igual(figuraDescricao.getId().getDcgSeqp(), oldFiguraDescricao.getId().getDcgSeqp()) ||
				!CoreUtil.igual(figuraDescricao.getId().getSeqp(), oldFiguraDescricao.getId().getSeqp()) ||
				!CoreUtil.igual(figuraDescricao.getCriadoEm(), oldFiguraDescricao.getCriadoEm()) ||
				!CoreUtil.igual(figuraDescricao.getServidor(), oldFiguraDescricao.getServidor()) ||
				!CoreUtil.igual(figuraDescricao.getOrdem(), oldFiguraDescricao.getOrdem()) ||
				!CoreUtil.igual(figuraDescricao.getTexto(), oldFiguraDescricao.getTexto()) 
				){
			final MbcFiguraDescricaoJn jn = createJournal(oldFiguraDescricao, servidorLogado.getUsuario(), DominioOperacoesJournal.UPD);
			getMbcFiguraDescricaoJnDAO().persistir(jn);
		}
	}
	

	private MbcFiguraDescricaoJn createJournal(final MbcFiguraDescricoes mbcFiguraDescricoes,
			final String usuarioLogado, DominioOperacoesJournal dominio) {
		
		final MbcFiguraDescricaoJn journal =  BaseJournalFactory.getBaseJournal(dominio, 
				MbcFiguraDescricaoJn.class, usuarioLogado);
		
		journal.setDcgCrgSeq(mbcFiguraDescricoes.getId().getDcgCrgSeq());
		journal.setDcgSeqp(mbcFiguraDescricoes.getId().getDcgSeqp());
		journal.setSeqp(mbcFiguraDescricoes.getId().getSeqp());
		journal.setTexto(mbcFiguraDescricoes.getTexto());
		journal.setCriadoEm(mbcFiguraDescricoes.getCriadoEm());
		journal.setOrdem(mbcFiguraDescricoes.getOrdem());
		journal.setSerMatricula(mbcFiguraDescricoes.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(mbcFiguraDescricoes.getServidor().getId().getVinCodigo());
		
		return journal;
	}
	
	@SuppressWarnings("ucd")
	public void inserirMbcFiguraDescricoes( final MbcFiguraDescricoes figuraDescricao)
												throws ApplicationBusinessException, ApplicationBusinessException {
		executarAntesDeInserir(figuraDescricao);
		getMbcFiguraDescricoesDAO().persistir(figuraDescricao);
	}
	
	/**
	 * ORADB: MBCT_FDC_BRI
	 */
	private void executarAntesDeInserir(final MbcFiguraDescricoes figuraDescricao)
			throws ApplicationBusinessException, ApplicationBusinessException {

		// Verifica se o usuario que está fazendo o insert,é quem criou a descricao cirurgica
		getProfDescricoesRN().verificarServidorLogadoRealizaDescricaoCirurgica(figuraDescricao.getId().getDcgCrgSeq(), figuraDescricao.getId().getDcgSeqp());
		
		// não permite inserir o registro desta tabela, se a descricao estiver concluida
		getDiagnosticoDescricaoRN().verificaDescricaoConcluida(figuraDescricao.getMbcDescricaoCirurgica());
		
		// atualiza servidor que incluiu registro
		figuraDescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
	}
	
	protected MbcFiguraDescricoesDAO getMbcFiguraDescricoesDAO() {
		return mbcFiguraDescricoesDAO;
	}
	
	protected DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}

	protected ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}

	private MbcFiguraDescricaoJnDAO getMbcFiguraDescricaoJnDAO() {
		return mbcFiguraDescricaoJnDAO;
	}
}