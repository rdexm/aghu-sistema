package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.MbcNotaAdicionaisRN.MbcNotaAdicionaisRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNotaAdicionaisDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNotaAdicionalJNDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcNotaAdicionais;
import br.gov.mec.aghu.model.MbcNotaAdicionalJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcNotaAdicionaisRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcNotaAdicionaisRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcNotaAdicionalJNDAO mbcNotaAdicionalJNDAO;

	@Inject
	private MbcNotaAdicionaisDAO mbcNotaAdicionaisDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	private static final long serialVersionUID = 492066814721290058L;

	public enum MbcNotaAdicionaisRNExceptionCode implements
			BusinessExceptionCode {
		MBC_00685;
	}

	public void persistirMbcNotaAdicionais(MbcNotaAdicionais notaAdicional) throws ApplicationBusinessException {
		if(notaAdicional.getMbcDescricaoCirurgica() == null){
			notaAdicional.setMbcDescricaoCirurgica(getMbcDescricaoCirurgicaDAO().obterPorChavePrimaria(
					new MbcDescricaoCirurgicaId( notaAdicional.getId().getDcgCrgSeq(),notaAdicional.getId().getDcgSeqp())));
		}
		
		if(notaAdicional.getId().getSeqp() != null){
			alterarMbcNotaAdicionais(notaAdicional);
		} else {
			inserirMbcNotaAdicionais(notaAdicional);
		}
	}
	
	private MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO(){
		return mbcDescricaoCirurgicaDAO;
	}

	public void excluirMbcNotaAdicionais(MbcNotaAdicionais mbcNotaAdicionais) throws ApplicationBusinessException {
		mbcNotaAdicionais = mbcNotaAdicionaisDAO.obterPorChavePrimaria(mbcNotaAdicionais.getId());
		antesDeExcluir(mbcNotaAdicionais);
		getMbcNotaAdicionaisDAO().remover(mbcNotaAdicionais);
		depoisDeExcluir(mbcNotaAdicionais);
	}

	/**
	 * ORADB: MBCT_NTA_BRD
	 */
	void antesDeExcluir(final MbcNotaAdicionais notaAdicional)
			throws ApplicationBusinessException {
		// VERIFICA SE O USUARIO QUE EXCLUI FOI O MESMO QUE INCLUIU
		getDiagnosticoDescricaoRN().validaServidorExclusao(
				notaAdicional.getServidor());
	}

	/**
	 * ORADB: MBCT_NTA_ARD
	 */
	void depoisDeExcluir(final MbcNotaAdicionais notaAdicional)
			throws ApplicationBusinessException {
		final MbcNotaAdicionalJn jn = createJournal(notaAdicional,
				DominioOperacoesJournal.DEL);
		getMbcNotaAdicionalJNDAO().persistir(jn);
	}

	private void alterarMbcNotaAdicionais(final MbcNotaAdicionais notaAdicional)
			throws ApplicationBusinessException {
		final MbcNotaAdicionaisDAO dao = getMbcNotaAdicionaisDAO();
		final MbcNotaAdicionais oldNotaAdicional = dao
				.obterOriginal(notaAdicional);

		antesDeAlterar(notaAdicional, oldNotaAdicional);
		dao.atualizar(notaAdicional);
		depoisDeAlterar(notaAdicional, oldNotaAdicional);
	}

	/**
	 * ORADB: MBCT_NTA_ARU
	 */
	void depoisDeAlterar(final MbcNotaAdicionais notaAdicional,
			final MbcNotaAdicionais oldNotaAdicional)
			throws ApplicationBusinessException {
		if (!CoreUtil.igual(notaAdicional.getNotasAdicionais(),
				oldNotaAdicional.getNotasAdicionais())
				|| !CoreUtil.igual(notaAdicional.getCriadoEm(),
						oldNotaAdicional.getCriadoEm())
				|| !CoreUtil.igual(notaAdicional.getServidor(),
						oldNotaAdicional.getServidor())
				|| !CoreUtil.igual(notaAdicional.getId().getDcgCrgSeq(),
						oldNotaAdicional.getId().getDcgCrgSeq())
				|| !CoreUtil.igual(notaAdicional.getId().getDcgSeqp(),
						oldNotaAdicional.getId().getDcgSeqp())
				|| !CoreUtil.igual(notaAdicional.getId().getSeqp(),
						oldNotaAdicional.getId().getSeqp())) {

			final MbcNotaAdicionalJn jn = createJournal(oldNotaAdicional,
					DominioOperacoesJournal.UPD);
			getMbcNotaAdicionalJNDAO().persistir(jn);
		}
	}

	private MbcNotaAdicionalJn createJournal(
			final MbcNotaAdicionais notaAdicional,
			DominioOperacoesJournal dominio) {

		final MbcNotaAdicionalJn journal = BaseJournalFactory
				.getBaseJournal(dominio, MbcNotaAdicionalJn.class,
						servidorLogadoFacade.obterServidorLogado().getUsuario());

		journal.setCriadoEm(notaAdicional.getCriadoEm());
		journal.setDcgCrgSeq(notaAdicional.getId().getDcgCrgSeq());
		journal.setDcgSeqp(notaAdicional.getId().getDcgSeqp());
		journal.setNotasAdicionais(notaAdicional.getNotasAdicionais());
		journal.setSeqp(notaAdicional.getId().getSeqp());
		journal.setSerMatricula(notaAdicional.getServidor().getId()
				.getMatricula());
		journal.setSerVinCodigo(notaAdicional.getServidor().getId()
				.getVinCodigo());

		return journal;
	}

	/**
	 * ORADB: MBCT_NTA_BRU
	 */
	void antesDeAlterar(final MbcNotaAdicionais notaAdicional,
			final MbcNotaAdicionais oldNotaAdicional)
			throws ApplicationBusinessException {

		// verifica se o usuário que está alterando é o mesmo que incluiu
		getDiagnosticoDescricaoRN().verificaUsuarioInclusaoAlteracao(
				oldNotaAdicional.getServidor());

		// verifica se a descricao cirurgica já está concluida
		verificaDescricaoCirurgica(notaAdicional);

		notaAdicional.setServidor(servidorLogadoFacade.obterServidorLogado());
	}

	private void inserirMbcNotaAdicionais(final MbcNotaAdicionais notaAdicional)
			throws ApplicationBusinessException {
		antesDeInserir(notaAdicional);
		getMbcNotaAdicionaisDAO().persistir(notaAdicional);
	}

	/**
	 * ORADB: MBCT_NTA_BRI
	 */
	void antesDeInserir(final MbcNotaAdicionais notaAdicional) throws ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(notaAdicional.getId().getSeqp() == null){
			Integer seqp = getMbcNotaAdicionaisDAO().obterMaxNTASeqp(notaAdicional.getId().getDcgCrgSeq(), notaAdicional.getId().getDcgSeqp());
			if(seqp != null){
				notaAdicional.getId().setSeqp(++seqp);
			} else {
				notaAdicional.getId().setSeqp(1);
			}
		}
		notaAdicional.setCriadoEm(new Date());
		verificaDescricaoCirurgica(notaAdicional);

		//atualiza servidor que incluiu registro
		notaAdicional.setServidor(servidorLogado);
	}

	/**
	 * Verifica se a descricao cirurgica já está concluida
	 * 
	 * ORADB: MBCK_NTA_RN.RN_NTAP_VER_CONCLUSA
	 */
	void verificaDescricaoCirurgica(final MbcNotaAdicionais mbcNotaAdicionais)
			throws ApplicationBusinessException {
		if (DominioSituacaoDescricaoCirurgia.PEN.equals(mbcNotaAdicionais
				.getMbcDescricaoCirurgica().getSituacao())) {
			throw new ApplicationBusinessException(
					MbcNotaAdicionaisRNExceptionCode.MBC_00685);
		}
	}

	protected DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}

	protected MbcNotaAdicionaisDAO getMbcNotaAdicionaisDAO() {
		return mbcNotaAdicionaisDAO;
	}

	protected MbcNotaAdicionalJNDAO getMbcNotaAdicionalJNDAO() {
		return mbcNotaAdicionalJNDAO;
	}
}