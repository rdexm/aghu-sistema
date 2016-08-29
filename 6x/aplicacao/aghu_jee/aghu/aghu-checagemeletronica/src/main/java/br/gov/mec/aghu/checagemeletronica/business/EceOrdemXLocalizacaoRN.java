package br.gov.mec.aghu.checagemeletronica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.checagemeletronica.dao.EceOrdemDeAdministracaoDAO;
import br.gov.mec.aghu.checagemeletronica.dao.EceOrdemXLocalizacaoDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.EceOrdemDeAdministracao;
import br.gov.mec.aghu.model.EceOrdemXLocalizacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EceOrdemXLocalizacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EceOrdemXLocalizacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EceOrdemXLocalizacaoDAO eceOrdemXLocalizacaoDAO;

@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private EceOrdemDeAdministracaoDAO eceOrdemDeAdministracaoDAO;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private static final long serialVersionUID = -273532371591508065L;

	public enum EceOrdemXLocalizacaoRNExceptionCode implements BusinessExceptionCode {
		ERRO_NAO_TRATADO;
	}
	
	/**
	 * @ORADB AGHP_ENFORCE_ATD_RULES.existe_localizacao
	 */
	public boolean existeLocalizacao(final Integer newSeq, final Date truncaData, final Short newUnfSeq) {
		EceOrdemXLocalizacaoDAO dao = getEceOrdemXLocalizacaoDAO();
		return dao.existeLocalizacao1(newSeq, truncaData, newUnfSeq) || dao.existeLocalizacao2(newSeq, truncaData, newUnfSeq)
				|| dao.existeLocalizacao3(newSeq, truncaData, newUnfSeq);
	}

	/**
	 *  
	 * @ORADB AGHP_ENFORCE_ATD_RULES.insere_ordem_localizacao
	 */
	public void inserirOrdemLocalizacao(Integer newSeq, Date ontem, Short newUnfSeq, Short newQtoNum, String newLeitoId) throws ApplicationBusinessException {
		final EceOrdemDeAdministracaoDAO administracaoDAO = getEceOrdemDeAdministracaoDAO();
		final EceOrdemXLocalizacaoDAO localizacaoDAO = getEceOrdemXLocalizacaoDAO();
		final ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade = getCadastrosBasicosInternacaoFacade();
		final IInternacaoFacade internacaoFacade = getInternacaoFacade();
		final IAghuFacade aghuFacade = getAghuFacade();

		final List<EceOrdemDeAdministracao> result = administracaoDAO.buscarEceOrdemDeAdministracaoInserirOrdemLocalizacao(newSeq, ontem);
		for (EceOrdemDeAdministracao eceOrdemDeAdministracao : result) {
			final EceOrdemXLocalizacao eceOrdemXLocalizacao = new EceOrdemXLocalizacao();
			eceOrdemXLocalizacao.setOrdemAdministracao(eceOrdemDeAdministracao);
			if (newQtoNum != null) {
				eceOrdemXLocalizacao.setQuarto(internacaoFacade.obterQuartoPorId(newQtoNum));
			}
			if (StringUtils.isNotEmpty(newLeitoId)) {
				eceOrdemXLocalizacao.setLeito(cadastrosBasicosInternacaoFacade.obterLeitoPorId(newLeitoId));
			}
			eceOrdemXLocalizacao.setSituacao(DominioSituacao.A);
			eceOrdemXLocalizacao.setUnidadesFuncionais(aghuFacade.obterUnidadeFuncional(newUnfSeq));
			eceOrdemXLocalizacao.setCriadoEm(new Date());

			localizacaoDAO.persistir(eceOrdemXLocalizacao);
		}
		localizacaoDAO.flush();
	}
	
	/**
	 *  
	 * @ORADB AGHP_ENFORCE_ATD_RULES.altera_ordem_localizacao
	 */
	public void alterarOrdemLocalizacao(Integer newSeq, Date dataReferencia, Short newUnfSeq, Short newQtoNum, String newLeitoId)  throws ApplicationBusinessException  {
		final EceOrdemXLocalizacaoDAO localizacaoDAO = getEceOrdemXLocalizacaoDAO();

		final List<EceOrdemXLocalizacao> result = localizacaoDAO.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao(newSeq, dataReferencia, newUnfSeq);
		for (final EceOrdemXLocalizacao eceOrdemXLocalizacao : result) {
			/*
         UPDATE ece_ordem_x_localizacao
         SET qrt_numero   = p_quarto
            ,lto_lto_id   = p_leito
            ,unf_seq      = p_unf_seq_new
            ,situacao     = 'A'
         WHERE oda_seq    =  r.oda_seq
           AND unf_seq    =  r.unf_seq
           AND NVL(qrt_numero,1) =  NVL(r.qrt_numero,1)
           AND NVL(lto_lto_id,'1') =  NVL(r.lto_lto_id,'1');
            */
			Short unidade = eceOrdemXLocalizacao.getUnidadesFuncionais() != null ? eceOrdemXLocalizacao.getUnidadesFuncionais().getSeq() : null;
			Short quarto = eceOrdemXLocalizacao.getQuarto() != null ? eceOrdemXLocalizacao.getQuarto().getNumero() : null;
			String leito = eceOrdemXLocalizacao.getLeito() != null ? eceOrdemXLocalizacao.getLeito().getLeitoID() : null;
			List<EceOrdemXLocalizacao> eceOrdemXLocalizacaos = localizacaoDAO.buscarEceOrdemXLocalizacaoAlterarOrdemLocalizacao(eceOrdemXLocalizacao
					.getOrdemAdministracao().getSeq(), unidade, quarto, leito);
			for (EceOrdemXLocalizacao eceOrdemXLocalizacaoAlterar : eceOrdemXLocalizacaos) {
				eceOrdemXLocalizacaoAlterar.setQuarto(eceOrdemXLocalizacao.getQuarto());
				eceOrdemXLocalizacaoAlterar.setLeito(eceOrdemXLocalizacao.getLeito());
				eceOrdemXLocalizacaoAlterar.setUnidadesFuncionais(eceOrdemXLocalizacao.getUnidadesFuncionais());
				eceOrdemXLocalizacaoAlterar.setSituacao(eceOrdemXLocalizacao.getSituacao());
				if(eceOrdemXLocalizacaoAlterar.getCriadoEm() == null){
					eceOrdemXLocalizacaoAlterar.setCriadoEm(new Date());
				}
				
				localizacaoDAO.atualizar(eceOrdemXLocalizacaoAlterar);
			}
		}
		localizacaoDAO.flush();
	}

	protected EceOrdemXLocalizacaoDAO getEceOrdemXLocalizacaoDAO() {
		return eceOrdemXLocalizacaoDAO;
	}

	protected EceOrdemDeAdministracaoDAO getEceOrdemDeAdministracaoDAO() {
		return eceOrdemDeAdministracaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return cadastrosBasicosInternacaoFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

}
