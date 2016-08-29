package br.gov.mec.aghu.exames.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelProjetoProcedimentoDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoProcedimentoJnDAO;
import br.gov.mec.aghu.model.AelProjetoProcedimento;
import br.gov.mec.aghu.model.AelProjetoProcedimentoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * Classe responsável pelas regras de BANCO para AEL_PROJETO_PROCEDIMENTOS
 * 
 * @author aghu
 * 
 */
@Stateless
public class AelProjetoProcedimentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelProjetoProcedimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelProjetoProcedimentoJnDAO aelProjetoProcedimentoJnDAO;
	
	@Inject
	private AelProjetoProcedimentoDAO aelProjetoProcedimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6177310255212285516L;

	public enum AelProjetoProcedimentoRNExceptionCode implements BusinessExceptionCode {
		AEL_02795, AEL_02798, AEL_02799, AEL_02800,
	}

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir AelProjetoProcedimento
	 * 
	 * @param projetoProcedimento
	 * @throws BaseException
	 */
	public void persistirProjetoProcedimento(AelProjetoProcedimento projetoProcedimento) throws BaseException {
		if (projetoProcedimento.getId() == null) { // Inserir
			this.inserirAelProjetoProcedimento(projetoProcedimento);
		} else { // Atualizar
			this.atualizarAelProjetoProcedimento(projetoProcedimento);
		}
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER AELT_PPR_BRI (INSERT)
	 * 
	 * @param projetoProcedimento
	 * @throws BaseException
	 */
	public void preInserirAelProjetoProcedimento(AelProjetoProcedimento projetoProcedimento) throws BaseException {
		// TODO
	}

	/**
	 * Inserir AelProjetoProcedimento
	 * 
	 * @param projetoProcedimento
	 * @throws BaseException
	 */
	public void inserirAelProjetoProcedimento(AelProjetoProcedimento projetoProcedimento) throws BaseException {
		this.preInserirAelProjetoProcedimento(projetoProcedimento);
		this.aelProjetoProcedimentoDAO.persistir(projetoProcedimento);
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER AELT_PPR_BRU (UPDATE)
	 * 
	 * @param projetoProcedimento
	 * @throws BaseException
	 */
	public void preAtualizarAelProjetoProcedimento(AelProjetoProcedimento elemento) throws BaseException {

		AelProjetoProcedimento old = this.aelProjetoProcedimentoDAO.obterOriginal(elemento);
		if (CoreUtil.modificados(old.getDtInicio(), elemento.getDtInicio())
				|| CoreUtil.modificados(old.getDtFim(), elemento.getDtFim()) 
				&& elemento.getDtFim() !=null){
			this.verificarDataInicial(elemento);
		}
		/* Dt_inicio e dt_fim devem estar entre a dt_inicio e a dt_fim na tab ael_projeto_pesquisas */
		if (CoreUtil.modificados(old.getDtInicio(), elemento.getDtInicio())
				|| CoreUtil.modificados(old.getDtFim(), elemento.getDtFim())){
			this.verificarDatas(elemento);
		}
	}

	/**
	 * ORADB aelk_ppr_rn.rn_pprp_ver_dt_inic
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	private void verificarDataInicial(AelProjetoProcedimento elemento)
	throws ApplicationBusinessException {
		if(CoreUtil.isMaiorDatas(elemento.getDtInicio(), elemento.getDtFim())){
			/**
			 *  A data inicial deve ser menor ou igual a data final
			 */
			throw new ApplicationBusinessException(AelProjetoProcedimentoRNExceptionCode.AEL_02795);	
		}
	}

	/**
	 * ORADB aelk_ppr_rn.rn_pprp_ver_datas
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	private void verificarDatas(AelProjetoProcedimento elemento) throws ApplicationBusinessException {

		if(elemento.getDtFim()!=null){
			if(elemento.getAelProjetoPesquisas()!=null && (DateValidator.validaDataMenor(elemento.getDtInicio(), elemento.getAelProjetoPesquisas().getDtInicio())
					|| DateUtil.validaDataMaior(elemento.getDtFim(), elemento.getAelProjetoPesquisas().getDtFim()))){
				/* A data inicial e a data final devem estar entre a data inicial e final do projeto de pesquisas! */
				throw new ApplicationBusinessException(AelProjetoProcedimentoRNExceptionCode.AEL_02798);	
			}
		}else{
			if(elemento.getAelProjetoPesquisas()!=null && elemento.getAelProjetoPesquisas().getDtFim()!=null){
				if(DateValidator.validaDataMenor(elemento.getDtInicio(), elemento.getAelProjetoPesquisas().getDtInicio())
						|| DateUtil.validaDataMaior(elemento.getDtInicio(), elemento.getAelProjetoPesquisas().getDtFim())){
					/*A data de início do projeto componente deve estar entre a data inicial e a data final do projeto de pesquisas !*/
					throw new ApplicationBusinessException(AelProjetoProcedimentoRNExceptionCode.AEL_02799);
				}
			}else if(DateValidator.validaDataMenor(elemento.getDtInicio(), elemento.getAelProjetoPesquisas().getDtInicio())){
				/*A data de início deve ser maior que a data inicial do projeto de pesquisa!*/
				throw new ApplicationBusinessException(AelProjetoProcedimentoRNExceptionCode.AEL_02800);
			}
		}
	}				


	/**
	 * Atualizar AelProjetoProcedimento
	 * 
	 * @param projetoProcedimento
	 * @throws BaseException
	 */
	public void atualizarAelProjetoProcedimento(AelProjetoProcedimento projetoProcedimento) throws BaseException {
		this.preAtualizarAelProjetoProcedimento(projetoProcedimento);
		this.aelProjetoProcedimentoDAO.atualizar(projetoProcedimento);
		this.posAtualizarAelProjetoProcedimento(projetoProcedimento);
	}

	/**
	 * ORADB TRIGGER AELT_PPR_ARU (UPDATE)
	 * 
	 * @param projetoProcedimento
	 * @throws BaseException
	 */
	public void posAtualizarAelProjetoProcedimento(AelProjetoProcedimento elemento) throws BaseException {
		// TODO

		AelProjetoProcedimento old = this.aelProjetoProcedimentoDAO.obterOriginal(elemento);

		if (CoreUtil.modificados(old.getQtdePermitido(), elemento.getQtdePermitido())
				|| CoreUtil.modificados(old.getCriadoEm(), elemento.getCriadoEm())
				|| CoreUtil.modificados(old.getDtInicio(), elemento.getDtInicio())
				|| CoreUtil.modificados(old.getDtFim(), elemento.getDtFim())
				|| CoreUtil.modificados(old.getSituacao(), elemento.getSituacao())
				|| CoreUtil.modificados(old.getValor(), elemento.getValor())
				|| CoreUtil.modificados(old.getNumero(), elemento.getNumero())
				|| CoreUtil.modificados(old.getQtdeSolicitado(), elemento.getQtdeSolicitado())
				|| CoreUtil.modificados(old.getAelProjetoPesquisas(), elemento.getAelProjetoPesquisas())
				|| CoreUtil.modificados(old.getRapServidores(), elemento.getRapServidores())
				|| CoreUtil.modificados(old.getMbcProcedimentoCirurgicos(), elemento.getMbcProcedimentoCirurgicos())){

			this.inserirAelProjetoProcedimentoJn(elemento, DominioOperacoesJournal.UPD);
		}
	}

	private void inserirAelProjetoProcedimentoJn(AelProjetoProcedimento original, DominioOperacoesJournal op) throws BaseException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		AelProjetoProcedimentoJn jn = BaseJournalFactory.getBaseJournal(op, AelProjetoProcedimentoJn.class, servidorLogado.getUsuario());

		jn.setCriadoEm(original.getCriadoEm());
		jn.setOperacao(op);
		jn.setSerMatricula(servidorLogado.getId().getMatricula());
		jn.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		jn.setQtdePermitido(original.getQtdePermitido());
		jn.setDtInicio(original.getDtInicio());
		jn.setDtFim(original.getDtFim());
		jn.setIndSituacao(original.getSituacao().toString());
		jn.setValor(original.getValor());
		jn.setNumero(original.getNumero());
		jn.setQtdeSolicitado(original.getQtdeSolicitado());
		jn.setPjqSeq(original.getAelProjetoPesquisas().getSeq());
		jn.setPciSeq(original.getMbcProcedimentoCirurgicos().getSeq());

		this.aelProjetoProcedimentoJnDAO.persistir(jn);

	}

}
