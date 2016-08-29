package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricaoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricoesDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcProfDescricaoJn;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcProfDescricoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade MbcProfDescricoes.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class ProfDescricoesRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ProfDescricoesRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProfDescricaoJnDAO mbcProfDescricaoJnDAO;

	@Inject
	private MbcProfDescricoesDAO mbcProfDescricoesDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private DescricaoCirurgicaRN descricaoCirurgicaRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4198597322605978590L;

	public enum ProfDescricoesRNExceptionCode implements BusinessExceptionCode {
		MBC_00700, MBC_00670, MBC_00684
	}

	public void excluirProfDescricoes(final MbcProfDescricoes profDescricao) throws ApplicationBusinessException{
		MbcProfDescricoes profDescricaoOriginal = getMbcProfDescricoesDAO().obterMbcProfDescricoesPorId(profDescricao.getId());		
		executarAntesDeExcluir(profDescricaoOriginal);
		getMbcProfDescricoesDAO().remover(profDescricaoOriginal);
		executarAposExcluir(profDescricaoOriginal);
	}
	
	public void alterarProfDescricoes(MbcProfDescricoes profDescricao) throws ApplicationBusinessException{
		MbcProfDescricoes profDescricaoOriginal = getMbcProfDescricoesDAO().obterMbcProfDescricoesPorId(profDescricao.getId());		
		executarAntesDeAlterar(profDescricao, profDescricaoOriginal);
		getMbcProfDescricoesDAO().atualizar(profDescricao);
		executarAposAlterar(profDescricao, profDescricaoOriginal);
	}

	/**
	 * ORADB: MBCT_PFD_ARD
	 */
	protected void executarAposExcluir(final MbcProfDescricoes profDescricao) {
		final MbcProfDescricaoJn journal = createJournal(profDescricao, servidorLogadoFacade.obterServidorLogado().getUsuario(), DominioOperacoesJournal.DEL);
		getMbcProfDescricaoJnDAO().persistir(journal);
	}

	/**
	 * ORADB: MBCT_PFD_BRD
	 */
	protected void executarAntesDeExcluir(final MbcProfDescricoes profDescricao) throws ApplicationBusinessException {
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuario que excluiu foi o mesmo que inclui
		ddRN.validaServidorExclusao(profDescricao.getServidor());

		// não permite que se exclua o registro desta tabela se a descricao estiver concluida
		verificarDescricaoCirurgicaConcluida(profDescricao.getId().getDcgCrgSeq(), profDescricao.getId().getDcgSeqp());
	}

	/**
	 * ORADB: MBCT_PFD_ARU
	 */
	@SuppressWarnings("ucd")
	protected void executarAposAlterar(final MbcProfDescricoes profDescricao, final MbcProfDescricoes oldProfDescricao) {
		if( !CoreUtil.igual(profDescricao.getCategoria(), oldProfDescricao.getCategoria()) ||
				!CoreUtil.igual(profDescricao.getCriadoEm(), oldProfDescricao.getCriadoEm()) ||
				!CoreUtil.igual(profDescricao.getId().getDcgCrgSeq(), oldProfDescricao.getId().getDcgCrgSeq()) ||
				!CoreUtil.igual(profDescricao.getId().getDcgSeqp(), oldProfDescricao.getId().getDcgSeqp()) ||
				!CoreUtil.igual(profDescricao.getId().getSeqp(), oldProfDescricao.getId().getSeqp()) ||
				!CoreUtil.igual(profDescricao.getNome(), oldProfDescricao.getNome()) ||
				!CoreUtil.igual(profDescricao.getServidor(), oldProfDescricao.getServidor()) ||
				!CoreUtil.igual(profDescricao.getServidorProf(), oldProfDescricao.getServidorProf()) ||
				!CoreUtil.igual(profDescricao.getTipoAtuacao(), oldProfDescricao.getTipoAtuacao()) 
				){
			final MbcProfDescricaoJn journal = createJournal(oldProfDescricao, servidorLogadoFacade.obterServidorLogado().getUsuario(), DominioOperacoesJournal.UPD);
			getMbcProfDescricaoJnDAO().persistir(journal);
		}
		
	}

	/**
	 * MBCT_PFD_ARU
	 */
	@SuppressWarnings("ucd")
	protected void executarAntesDeAlterar(final MbcProfDescricoes profDescricao, final MbcProfDescricoes oldProfDescricao) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuário que está alterando é o mesmo que incluiu
		ddRN.verificaUsuarioInclusaoAlteracao(oldProfDescricao.getServidor());
		
		// não permite alterar o registro desta tabela, se a descricao estiver concluida
		verificarDescricaoCirurgicaConcluida( oldProfDescricao.getId().getDcgCrgSeq(), 
											  oldProfDescricao.getId().getDcgSeqp());
		
		// obriga o usuário informar o <nome do profissional> caso não tenha informado matricula/vinculo
		verificarNomeServidorProf(profDescricao.getServidorProf(), profDescricao.getNome());
		
		// atualiza servidor que incluiu registro
		profDescricao.setServidor(servidorLogado);
	}


	/**
	 * Insere instância de MbcProfDescricoes.
	 * 
	 * @param profDescricao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException 
	 */
	public void inserirProfDescricoes(final MbcProfDescricoes profDescricao)
												throws ApplicationBusinessException {
		executarAntesDeInserir(profDescricao);
		getMbcProfDescricoesDAO().persistir(profDescricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_PFD_BRI
	 */
	public void executarAntesDeInserir(final MbcProfDescricoes profDescricao)
													throws ApplicationBusinessException {
		
		final MbcProfDescricoesId profDescricaoId = profDescricao.getId();

		// Verifica se o usuario que está fazendo o insert,é quem criou a descricao cirurgica
		verificarServidorLogadoRealizaDescricaoCirurgica( profDescricaoId.getDcgCrgSeq(), 
														  profDescricaoId.getDcgSeqp());

		// Não permite inserir o registro desta tabela, se a descricao estiver concluida
		verificarDescricaoCirurgicaConcluida( profDescricaoId.getDcgCrgSeq(),
											  profDescricaoId.getDcgSeqp());
		
		/* Obriga o usuário informar o <nome do profissional> caso não tenha informado matricula/vinculo */
		verificarNomeServidorProf(profDescricao.getServidorProf(), profDescricao.getNome());

		/* Atualiza servidor que incluiu registro */
		profDescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		profDescricao.setCriadoEm(new Date());
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_MBC_RN.RN_MBCP_VER_INSERT 
	 * 
	 */
	public void verificarServidorLogadoRealizaDescricaoCirurgica(
			Integer dcgCrgSeq, Short dcgSeqp)
			throws ApplicationBusinessException {
		
		MbcDescricaoCirurgica descricaoCirurgica = getMbcDescricaoCirurgicaDAO()
				.obterPorChavePrimaria(new MbcDescricaoCirurgicaId(dcgCrgSeq, dcgSeqp));

		if (descricaoCirurgica != null) {
			if (!descricaoCirurgica.getServidor().equals(servidorLogadoFacade.obterServidorLogado())) {
				throw new ApplicationBusinessException(ProfDescricoesRNExceptionCode.MBC_00700);
			}
		}
	}
	
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_MBC_RN.RN_MBCP_VER_CONCLUSA
	 * 
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void verificarDescricaoCirurgicaConcluida(Integer dcgCrgSeq,
			Short dcgSeqp)
			throws ApplicationBusinessException {
		
		MbcDescricaoCirurgica descricaoCirurgica = getMbcDescricaoCirurgicaDAO()
				.obterPorChavePrimaria(new MbcDescricaoCirurgicaId(dcgCrgSeq, dcgSeqp));
		
		AghParametros paramMinAlteraDescr = null;
		paramMinAlteraDescr = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MIN_ALTERA_DESCR);
		
		if (descricaoCirurgica != null) {
			Date dthrConclusao = descricaoCirurgica.getDthrConclusao();
			
			Calendar calAtual = Calendar.getInstance();
			Date dthrAtual = calAtual.getTime();
			Date tempDthrConclusao = dthrConclusao; 
					
			if (tempDthrConclusao == null) {
				tempDthrConclusao = dthrAtual;
			}
			
			// Caso a diferença em minutos entre a data atual e dthrConclusao seja
			// maior que o valor definido no parametro P_MIN_ALTERA_DESCR e
			// situacao da descricao da cirurgia for Concluída
			if (getDescricaoCirurgicaRN().calcularDiferencaMinutosAteDataAtual(tempDthrConclusao, dthrAtual).compareTo(
					paramMinAlteraDescr.getVlrNumerico().doubleValue()) > 0
					&& DominioSituacaoDescricaoCirurgia.CON.equals(descricaoCirurgica.getSituacao())) {
				throw new ApplicationBusinessException(ProfDescricoesRNExceptionCode.MBC_00670);
			}			
		}
	}

	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_PFD_RN.RN_PFDP_VER_NOME
	 * 
	 * @param servidorProf
	 * @param nome
	 * @throws ApplicationBusinessException
	 */
	protected void verificarNomeServidorProf(RapServidores servidorProf,
			String nome) throws ApplicationBusinessException {
		
		if (servidorProf == null && nome == null) {
			throw new ApplicationBusinessException(ProfDescricoesRNExceptionCode.MBC_00684);
		}
	}
	
	private MbcProfDescricaoJn createJournal(final MbcProfDescricoes mpd,
			final String usuarioLogado, DominioOperacoesJournal dominio) {
		
		final MbcProfDescricaoJn journal =  BaseJournalFactory.getBaseJournal(dominio, 
				MbcProfDescricaoJn.class, usuarioLogado);
		
		journal.setCategoria(mpd.getCategoria());
		journal.setCriadoEm(mpd.getCriadoEm());
		journal.setDcgCrgSeq(mpd.getId().getDcgCrgSeq());
		journal.setDcgSeqp(mpd.getId().getDcgSeqp());
		journal.setNome(mpd.getNome());
		journal.setSeqp(mpd.getId().getSeqp());
		
		journal.setSerMatricula(mpd.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(mpd.getServidor().getId().getVinCodigo());
		
		if(mpd.getServidorProf() != null){
			journal.setSerMatriculaProf(mpd.getServidorProf().getId().getMatricula());
			journal.setSerVinCodigoProf(mpd.getServidorProf().getId().getVinCodigo());
		}
		
		return journal;
	}
	
	protected MbcProfDescricoesDAO getMbcProfDescricoesDAO() {
		return mbcProfDescricoesDAO;
	}
	
	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}
	
	protected DescricaoCirurgicaRN getDescricaoCirurgicaRN() {
		return descricaoCirurgicaRN;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected MbcProfDescricaoJnDAO getMbcProfDescricaoJnDAO() {
		return mbcProfDescricaoJnDAO;
	}

	private DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}
}
