package br.gov.mec.aghu.blococirurgico.business;

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoPadraoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicasDAO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcDescricaoTecnicaJn;
import br.gov.mec.aghu.model.MbcDescricaoTecnicas;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MbcDescricaoTecnicaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcDescricaoTecnicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoTecnicasDAO mbcDescricaoTecnicasDAO;

	@Inject
	private MbcDescricaoTecnicaJnDAO mbcDescricaoTecnicaJnDAO;

	@Inject
	private MbcDescricaoPadraoDAO mbcDescricaoPadraoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1251875489743787228L;

	public enum MbcDescricaoTecnicaRNExceptionCode implements BusinessExceptionCode {
		MBC_00700, MBC_00701;
	}	

	public void persistir(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		if(descricaoTecnica.getVersion() != null) {
			this.preAtualizar(descricaoTecnica);
			getMbcDescricaoTecnicasDAO().atualizar(descricaoTecnica);
			this.posAtualizar(descricaoTecnica);
		} else {
			this.preInserir(descricaoTecnica);
			getMbcDescricaoTecnicasDAO().persistir(descricaoTecnica);
		}
	}
	
	public void excluir(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		descricaoTecnica = getMbcDescricaoTecnicasDAO().obterPorChavePrimaria(descricaoTecnica.getId());
		this.preDelete(descricaoTecnica);
		getMbcDescricaoTecnicasDAO().remover(descricaoTecnica);
		this.posDelete(descricaoTecnica);
	}
	/**
	 * @ORADB: MBCT_DTC_BRI
	 */
	protected void preInserir(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		/*Verifica se o usuario que está fazendo o insert,é quem criou a descricao cirurgica*/
		this.rnMbcpVerInsert(descricaoTecnica.getMbcDescricaoCirurgica());
	}

	/**
	 * @ORADB: MBCT_DTC_BRU
	 */
	protected void preAtualizar(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		/*Verifica se a descricao técnica está sendo alterada ou excluida pelo usuario que a criou*/
		this.rnDtcpVerUser(descricaoTecnica.getMbcDescricaoCirurgica());
	}

	/**
	 * @ORADB: MBCT_DTC_ARU
	 */
	protected void posAtualizar(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcDescricaoTecnicas original = getMbcDescricaoTecnicasDAO().obterOriginal(descricaoTecnica);
		if(CoreUtil.modificados(descricaoTecnica.getMbcDescricaoCirurgica(), original.getMbcDescricaoCirurgica()) 
				|| CoreUtil.modificados(descricaoTecnica.getDescricaoTecnica(), original.getDescricaoTecnica())) {
			final MbcDescricaoTecnicaJn jn = createJournal(original, servidorLogado.getUsuario(), DominioOperacoesJournal.UPD);
			getMbcDescricaoTecnicaJnDAO().persistir(jn);
		}
	}

	/**
	 * @ORADB: MBCT_DTC_BRD
	 */
	protected void preDelete(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
		/*Verifica se a descricao técnica está sendo alterada ou excluida pelo usuario que a criou*/
		this.rnDtcpVerUser(descricaoTecnica.getMbcDescricaoCirurgica());
	}

	
	/**
	 * @ORADB: MBCT_DTC_ARD
	 */
	protected void posDelete(MbcDescricaoTecnicas descricaoTecnica) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcDescricaoTecnicas original = getMbcDescricaoTecnicasDAO().obterOriginal(descricaoTecnica);
		if(CoreUtil.modificados(descricaoTecnica.getMbcDescricaoCirurgica(), original.getMbcDescricaoCirurgica()) 
				|| CoreUtil.modificados(descricaoTecnica.getDescricaoTecnica(), original.getDescricaoTecnica())) {
			final MbcDescricaoTecnicaJn jn = createJournal(original, servidorLogado.getUsuario(), DominioOperacoesJournal.DEL);
			getMbcDescricaoTecnicaJnDAO().persistir(jn);
		}
	}

	private MbcDescricaoTecnicaJn createJournal(final MbcDescricaoTecnicas descricaoTecnica,
			final String usuarioLogado, DominioOperacoesJournal dominio) {
		
		final MbcDescricaoTecnicaJn journal =  BaseJournalFactory.getBaseJournal(dominio, 
				MbcDescricaoTecnicaJn.class, usuarioLogado);
		
		journal.setDcgCrgSeq(descricaoTecnica.getId().getCrgSeq());
		journal.setDcgSeqp(descricaoTecnica.getId().getSeqp());
		journal.setDescricaoTecnica(descricaoTecnica.getDescricaoTecnica());

		return journal;
	}

	
	/**
	 * @ORADB: MBCK_MBC_RN.RN_MBCP_VER_INSERT
	 */
	private void rnMbcpVerInsert(MbcDescricaoCirurgica descricaoCirurgica) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!servidorLogado.equals(descricaoCirurgica.getServidor())) {
			throw new ApplicationBusinessException(
					MbcDescricaoTecnicaRNExceptionCode.MBC_00700);
		}
	}
	
	/**
	 * @ORADB: MBCK_DTC_RN.RN_DTCP_VER_USER
	 */
	private void rnDtcpVerUser(MbcDescricaoCirurgica descricaoCirurgica) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(!servidorLogado.equals(descricaoCirurgica.getServidor())) {
			throw new ApplicationBusinessException(
					MbcDescricaoTecnicaRNExceptionCode.MBC_00701);
		}
	}

	/**
	 * ORADB: P_GERA_DADOS_ESP
	 */
	public List<AghEspecialidades> pGeraDadosEsp() {
		List<AghEspecialidades> especialidades = getMbcDescricaoPadraoDAO().buscarEspecialidadesDaDescricaoPadraoComProcedimentoAtivo();
		final BeanComparator ordenarSigla = new BeanComparator(AghEspecialidades.Fields.SIGLA.toString(), new NullComparator(false));
		Collections.sort(especialidades, ordenarSigla);
		
		return especialidades;
	}

	/**
	 * ORADB: P_GERA_DADOS_PROC
	 */
	public List<MbcProcedimentoCirurgicos> pGeraDadosProc(Short espSeq) {
		List<MbcProcedimentoCirurgicos> procedimentos = getMbcDescricaoPadraoDAO().buscarProcCirurgicosAtivosDaDescricaoPadraoPelaEspecialidade(espSeq);
		final BeanComparator ordenarDescricao = new BeanComparator(MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), new NullComparator(false));
		Collections.sort(procedimentos, ordenarDescricao);

		return procedimentos;
	}

	/**
	 * ORADB: P_GERA_DADOS_PROC - TITULO
	 */
	public List<MbcDescricaoPadrao> buscarDescricaoPadraoPelaEspecialidadeEProcedimento(Short espSeq, Integer procSeq) {
		return  getMbcDescricaoPadraoDAO().buscarDescricaoPadraoPelaEspecialidadeEProcedimento(espSeq, procSeq);
	}

	protected MbcDescricaoPadraoDAO getMbcDescricaoPadraoDAO() {
		return mbcDescricaoPadraoDAO;
	}

	protected MbcDescricaoTecnicasDAO getMbcDescricaoTecnicasDAO() {
		return mbcDescricaoTecnicasDAO;
	}

	protected MbcDescricaoTecnicaJnDAO getMbcDescricaoTecnicaJnDAO() {
		return mbcDescricaoTecnicaJnDAO;
	}

}
