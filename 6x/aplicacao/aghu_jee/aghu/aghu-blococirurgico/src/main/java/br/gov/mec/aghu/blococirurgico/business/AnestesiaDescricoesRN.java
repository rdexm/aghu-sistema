package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.AnestesiaDescricoesRN.AnestesiaDescricoesRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaDescricaoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcTipoAnestesiasDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAnestesiaDescricaoJn;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoes;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoesId;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade MbcAnestesiaDescricoes. 
 * 
 * @author dpacheco
 *
 */
@Stateless
public class AnestesiaDescricoesRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(AnestesiaDescricoesRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAnestesiaDescricaoJnDAO mbcAnestesiaDescricaoJnDAO;

	@Inject
	private MbcTipoAnestesiasDAO mbcTipoAnestesiasDAO;

	@Inject
	private MbcAnestesiaDescricoesDAO mbcAnestesiaDescricoesDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -639550022022971803L;
	
	protected enum AnestesiaDescricoesRNExceptionCode implements BusinessExceptionCode {
		MBC_00682
	}	
	
	public void excluirMbcAnestesiaDescricoes(MbcAnestesiaDescricoes mbcAnestesiaDescricoes) throws ApplicationBusinessException, ApplicationBusinessException{
		mbcAnestesiaDescricoes = mbcAnestesiaDescricoesDAO.obterPorChavePrimaria(mbcAnestesiaDescricoes.getId());
		antesDeExcluir(mbcAnestesiaDescricoes);
		getMbcAnestesiaDescricoesDAO().remover(mbcAnestesiaDescricoes);
		depoisDeExcluir(mbcAnestesiaDescricoes);
	}
	
	/**
	 * ORADB: MBCT_ANE_BRD
	 */
	void antesDeExcluir(final MbcAnestesiaDescricoes anestesiaDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
		final DiagnosticoDescricaoRN  diagnosticoDescricaoRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuario que exclui foi o mesmo que incluiu
		diagnosticoDescricaoRN.validaServidorExclusao(anestesiaDescricao.getServidor());
		
		// não permite que se exclua o registro desta tabela se a descricao estiver concluida
		diagnosticoDescricaoRN.verificaDescricaoConcluida(anestesiaDescricao.getMbcDescricaoCirurgica());
	}

	
	/**
	 * ORADB: MBCT_ANE_ARD
	 */
	void depoisDeExcluir(final MbcAnestesiaDescricoes anestesiaDescricao) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MbcAnestesiaDescricaoJn jn = createJournal(anestesiaDescricao, servidorLogado.getUsuario(), DominioOperacoesJournal.DEL);
		getMbcAnestesiaDescricaoJnDAO().persistir(jn);
	}
	
	
	public void alterarAnestesiaDescricoes(final MbcAnestesiaDescricoes anestesiaDescricao, MbcTipoAnestesias tipoAnestesia)
							throws ApplicationBusinessException, ApplicationBusinessException {
		
		final MbcAnestesiaDescricoesDAO dao = getMbcAnestesiaDescricoesDAO();
		final MbcAnestesiaDescricoes oldAnestesiaDescricao = dao.obterOriginal(anestesiaDescricao);
		final MbcAnestesiaDescricoes novaAnestesiaDescricao = new MbcAnestesiaDescricoes();
		
		novaAnestesiaDescricao.setId(new MbcAnestesiaDescricoesId(anestesiaDescricao.getId().getDcgCrgSeq(), anestesiaDescricao.getId().getDcgSeqp(), tipoAnestesia.getSeq()));
		novaAnestesiaDescricao.setMbcDescricaoCirurgica(mbcDescricaoCirurgicaDAO.obterPorChavePrimaria(anestesiaDescricao.getMbcDescricaoCirurgica().getId()));
		novaAnestesiaDescricao.setTipoAnestesia(tipoAnestesia);
		excluirMbcAnestesiaDescricoes(anestesiaDescricao);
		
		antesDeAlterar(novaAnestesiaDescricao, oldAnestesiaDescricao);

		inserirAnestesiaDescricoes(novaAnestesiaDescricao);
		
		depoisDeAlterar(novaAnestesiaDescricao, oldAnestesiaDescricao);
	}
	
	/**
	 * ORADB: MBCT_ANE_BRU
	 */
	void antesDeAlterar(final MbcAnestesiaDescricoes anestesiaDescricao, final MbcAnestesiaDescricoes oldAnestesiaDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		DiagnosticoDescricaoRN  diagnosticoDescricaoRN = getDiagnosticoDescricaoRN();
		
		/*verifica se o usuário que está alterando é o mesmo que incluiu*/
		diagnosticoDescricaoRN.verificaUsuarioInclusaoAlteracao(oldAnestesiaDescricao.getServidor());

		// não permite alterar o registro desta tabela, se a descricao estiver concluida
		diagnosticoDescricaoRN.verificaDescricaoConcluida(oldAnestesiaDescricao.getMbcDescricaoCirurgica());
		
		// verifica se o novo tipo anestesia está ativo
		verificarTipoAnestesiaAtivo(anestesiaDescricao);
		  
		// atualiza servidor que incluiu registro
		anestesiaDescricao.setServidor(servidorLogado);
	}

	/**
	 * ORADB: MBCT_ANE_ARU
	 */
	void depoisDeAlterar(final MbcAnestesiaDescricoes anestesiaDescricao, final MbcAnestesiaDescricoes oldAnestesiaDescricao) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!CoreUtil.igual(anestesiaDescricao.getCriadoEm(), oldAnestesiaDescricao.getCriadoEm()) ||
				!CoreUtil.igual(anestesiaDescricao.getServidor(), oldAnestesiaDescricao.getServidor()) ||
				!CoreUtil.igual(anestesiaDescricao.getId().getDcgCrgSeq(), oldAnestesiaDescricao.getId().getDcgCrgSeq()) ||
				!CoreUtil.igual(anestesiaDescricao.getId().getDcgSeqp(), oldAnestesiaDescricao.getId().getDcgSeqp()) ||
				!CoreUtil.igual(anestesiaDescricao.getId().getTanSeq(), oldAnestesiaDescricao.getId().getTanSeq()) 
				){
			
			final MbcAnestesiaDescricaoJn jn = createJournal(oldAnestesiaDescricao, servidorLogado.getUsuario(), DominioOperacoesJournal.UPD);
			getMbcAnestesiaDescricaoJnDAO().persistir(jn);
		}
	}
	
	private MbcAnestesiaDescricaoJn createJournal(final MbcAnestesiaDescricoes mbcAnestesiaDescricoes,
			final String usuarioLogado, DominioOperacoesJournal dominio) {
		
		final MbcAnestesiaDescricaoJn journal =  BaseJournalFactory.getBaseJournal(dominio, 
				MbcAnestesiaDescricaoJn.class, usuarioLogado);
		
		journal.setCriadoEm(mbcAnestesiaDescricoes.getCriadoEm());
		journal.setDcgCrgSeq(mbcAnestesiaDescricoes.getId().getDcgCrgSeq());
		journal.setDcgSeqp(mbcAnestesiaDescricoes.getId().getDcgSeqp());
		journal.setTanSeq(mbcAnestesiaDescricoes.getId().getTanSeq());
		journal.setSerMatricula(mbcAnestesiaDescricoes.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(mbcAnestesiaDescricoes.getServidor().getId().getVinCodigo());
		
		return journal;
	}
	
	/**
	 * Insere instância de MbcAnestesiaDescricoes.
	 * 
	 * @param anestesiaDescricao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void inserirAnestesiaDescricoes(
			MbcAnestesiaDescricoes anestesiaDescricao
			)
			throws ApplicationBusinessException {
		
		executarAntesDeInserir(anestesiaDescricao);
		getMbcAnestesiaDescricoesDAO().persistir(anestesiaDescricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_ANE_BRI
	 * 
	 * @param anestesiaDescricao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesDeInserir(
			MbcAnestesiaDescricoes anestesiaDescricao
			)
			throws ApplicationBusinessException {
		
		final Integer dcgCrgSeq = anestesiaDescricao.getId().getDcgCrgSeq();
		final Short dcgSeqp = anestesiaDescricao.getId().getDcgSeqp();

		if(anestesiaDescricao.getMbcDescricaoCirurgica() == null){
			anestesiaDescricao.setMbcDescricaoCirurgica(getMbcDescricaoCirurgicaDAO().obterPorChavePrimaria(
															new MbcDescricaoCirurgicaId( dcgCrgSeq, dcgSeqp)));
		}

		ProfDescricoesRN profDescricoesRN = getProfDescricoesRN();

		/*
		 * Verifica se o usuario que está fazendo o insert é quem criou a
		 * descricao cirurgica
		 */
		profDescricoesRN.verificarServidorLogadoRealizaDescricaoCirurgica(dcgCrgSeq, dcgSeqp);

		/*
		 * Não permite inserir o registro desta tabela, se a descricao estiver
		 * concluida
		 */
		profDescricoesRN.verificarDescricaoCirurgicaConcluida(dcgCrgSeq, dcgSeqp);
		
		/* Verifica se o novo tipo anestesia está ativo */
		if(anestesiaDescricao.getId().getTanSeq() != null){
			verificarTipoAnestesiaAtivo(anestesiaDescricao);
		}
		
		/* Atualiza servidor que incluiu registro */
		anestesiaDescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		anestesiaDescricao.setCriadoEm(new Date());
		
		if(anestesiaDescricao.getId().getTanSeq() == null){
			// Obtem próximo tanseq de acordo com a cirurgia
			Short tanseq = getMbcAnestesiaDescricoesDAO().obterProximoTanseq(anestesiaDescricao.getId());
			tanseq = tanseq == null ? 1 : ++tanseq;
			anestesiaDescricao.getId().setTanSeq(tanseq);
		}
	}
	
	private MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}

	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_ANE_RN.RN_ANEP_VER_ANESTES
	 * 
	 * @param anestesiaDescricoes
	 * @throws ApplicationBusinessException
	 */
	protected void verificarTipoAnestesiaAtivo(MbcAnestesiaDescricoes anestesiaDescricoes) 
			throws ApplicationBusinessException {
		
		MbcTipoAnestesias tipoAnestesia = getMbcTipoAnestesiasDAO()
				.obterPorChavePrimaria(anestesiaDescricoes.getId().getTanSeq());
		
		if (tipoAnestesia != null
				&& DominioSituacao.I.equals(tipoAnestesia.getSituacao())) {
			throw new ApplicationBusinessException(
					AnestesiaDescricoesRNExceptionCode.MBC_00682);
		}
	}
	
	
	protected MbcAnestesiaDescricoesDAO getMbcAnestesiaDescricoesDAO() {
		return mbcAnestesiaDescricoesDAO;
	}
	
	protected MbcTipoAnestesiasDAO getMbcTipoAnestesiasDAO() {
		return mbcTipoAnestesiasDAO;
	}
	
	protected ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}

	protected DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}
	
	protected MbcAnestesiaDescricaoJnDAO getMbcAnestesiaDescricaoJnDAO() {
		return mbcAnestesiaDescricaoJnDAO;
	}
}
