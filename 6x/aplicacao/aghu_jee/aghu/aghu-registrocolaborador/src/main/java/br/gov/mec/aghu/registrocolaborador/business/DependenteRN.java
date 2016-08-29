package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapDependentesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapDependentesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapDependentesDAO.DependenteDAOExceptionCode;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DependenteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DependenteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private RapDependentesDAO rapDependentesDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3646614469686700122L;

	public enum DependenteRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH, MENSAGEM_DEPENDENTE_POSSUI_UNIMED, MENSAGEM_VINCULO_NAO_PERMITE_DEPENDENTE
	}

	private class ContratoAtivoStarhException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5248835068997417213L;
	}

	private class DependentePossuiUnimedException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8175784828237738243L;
	}

	private class PermissaoDependenteException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6466369961334862254L;
	}

	public void delete(RapDependentesId id) throws ApplicationBusinessException {
		RapDependentes dependentes = rapDependentesDAO.obterPorChavePrimaria(id);
		beforeRowDelete(dependentes);		
		rapDependentesDAO.remover(dependentes);
	}

	public void insert(RapDependentes dependentes) throws ApplicationBusinessException {
		this.beforeRowInsert(dependentes);
		rapDependentesDAO.persistir(dependentes);
	}

	public void update(RapDependentes dependentes) throws ApplicationBusinessException {
		this.beforeRowUpdate(dependentes);
		rapDependentesDAO.merge(dependentes);
	}

	private void beforeRowDelete(RapDependentes dependentes)
			throws ApplicationBusinessException {
		try {
			verificarUnimedDependentes(dependentes);
			integracaoStarh(dependentes);
		} catch (DependentePossuiUnimedException e) {
			throw new ApplicationBusinessException(
					DependenteRNExceptionCode.MENSAGEM_DEPENDENTE_POSSUI_UNIMED,
					"excluído");
		} catch (ContratoAtivoStarhException e) {
			throw new ApplicationBusinessException(
					DependenteRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH,
					"excluído");
		}
	}

	private void beforeRowInsert(RapDependentes dependentes)
			throws ApplicationBusinessException {
		try {
			integracaoStarh(dependentes);
			verificarPermissaoDependente(dependentes);
		} catch (ContratoAtivoStarhException e) {
			throw new ApplicationBusinessException(
					DependenteRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH,
					"incluído");
		} catch (PermissaoDependenteException e) {
			throw new ApplicationBusinessException(
					DependenteRNExceptionCode.MENSAGEM_VINCULO_NAO_PERMITE_DEPENDENTE,
					"incluído");
		}
	}

	private void beforeRowUpdate(RapDependentes dependentes)
			throws ApplicationBusinessException {
		try {
			integracaoStarh(dependentes);
		} catch (ContratoAtivoStarhException e) {
			throw new ApplicationBusinessException(
					DependenteRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH,
					"alterado");
		}
	}

	/**
	 * 
	 * @param qualificacao
	 * @throws ApplicationBusinessException
	 *             se servidor ativo no STARH.
	 */
	private void integracaoStarh(RapDependentes dependentes)
			throws ContratoAtivoStarhException, ApplicationBusinessException {

		if (!getDependentesDAO().isOracle()) {
			return;
		}

		Boolean ativo = false;
		List<RapServidores> servidor = getServidoresDAO().obterServidorPeloDependente(dependentes);

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		for (RapServidores s : servidor) {
			if (getObjetosOracleDAO().servidorAtivoStarh(null, s, servidorLogado)) {
				// throw new ApplicationBusinessException(
				// DependenteRNExceptionCode.MENSAGEM_EXISTE_CONTRATO_ATIVO_STARH);
				ativo = true;
			}
		}

		if (ativo) {
			throw new ContratoAtivoStarhException();
		}
	}

	private void verificarUnimedDependentes(RapDependentes dependentes)
			throws DependentePossuiUnimedException, ApplicationBusinessException {
		if (!getDependentesDAO().isOracle()) {
			return;
		}
		
		Boolean possui = false;

		List<RapServidores> servidor = getServidoresDAO().obterServidorPeloDependente(dependentes);
		Date dtAtual = new Date();

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		for (RapServidores s : servidor) {
			if (getObjetosOracleDAO().verificarConvenioUnimed(s.getId()
					.getVinCodigo(), dependentes.getId().getCodigo(), s.getId()
					.getMatricula(), dtAtual.getTime(), servidorLogado)) {
				possui = true;
			}
		}

		if (possui) {
			throw new DependentePossuiUnimedException();
		}
	}

	private void verificarPermissaoDependente(RapDependentes dependentes)
			throws PermissaoDependenteException, ApplicationBusinessException {

		List<RapServidores> servidor = getServidoresDAO().obterServidorPeloDependente(dependentes);
		Boolean permite = false;

		for (RapServidores s : servidor) {
			if (s.getVinculo().getIndDependente() == DominioSimNao.S) {
				permite = true;
			}
		}

		if (permite == false) {
			throw new PermissaoDependenteException();
		}
	}

	private Integer obterCodPessoaFisica(Integer serMatricula,
			Short serVinCodigo) throws ApplicationBusinessException {

		if (serMatricula == null || serVinCodigo == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		RapServidoresId id = new RapServidoresId(serMatricula, serVinCodigo);

		RapServidores servidor = getServidoresDAO().obterPorChavePrimaria(id);
		if (servidor == null) {
			throw new ApplicationBusinessException(
					DependenteDAOExceptionCode.MENSAGEM_SERVIDOR_INEXISTENTE);
		}

		return servidor.getPessoaFisica().getCodigo();
	}

	public List<RapDependentes> pesquisarDependente(Integer pesCodigo,
			Integer codigo, Integer serMatricula, Short serVinCodigo,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {

		Integer codPessoaFisica = null;
		
		if(serMatricula != null && serVinCodigo != null){
			codPessoaFisica = obterCodPessoaFisica(serMatricula, serVinCodigo);
		}
		

		return getDependentesDAO().pesquisarDependente(pesCodigo, codigo, codPessoaFisica, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarDependenteCount(Integer pesCodigo, Integer codigo, Integer serMatricula, Short serVinCodigo) throws ApplicationBusinessException {
		
		Integer codPessoaFisica = null;

		if(serMatricula != null && serVinCodigo != null){
			codPessoaFisica = obterCodPessoaFisica(serMatricula, serVinCodigo);	
		}
		

		return getDependentesDAO().pesquisarDependenteCount(pesCodigo, codigo, codPessoaFisica);
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected RapDependentesDAO getDependentesDAO() {
		return rapDependentesDAO;
	}
	
	protected RapServidoresDAO getServidoresDAO() {
		return rapServidoresDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
