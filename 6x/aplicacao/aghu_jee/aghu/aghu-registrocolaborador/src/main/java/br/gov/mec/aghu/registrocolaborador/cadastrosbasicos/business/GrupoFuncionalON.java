package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.registrocolaborador.dao.RapGrupoFuncionalDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapGrupoFuncionalDAO.GrupoFuncionalDAOExceptionCode;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoFuncionalON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoFuncionalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private RapGrupoFuncionalDAO rapGrupoFuncionalDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7076516796330903880L;

	public enum GrupoFuncionalONExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_CONSTRAINT_GRUPO_FUNCIONAL;
	}

	public RapGrupoFuncional pesquisarGrupoFuncionalPorCodigo(short codigo) throws ApplicationBusinessException {
		return getGrupoFuncionalDAO().obterPorChavePrimaria(codigo);
	}


	public void atualizarRapGrupoFuncional(final RapGrupoFuncional grupo) throws ApplicationBusinessException {
		if (grupo == null) {
			throw new ApplicationBusinessException(GrupoFuncionalDAOExceptionCode.MENSAGEM_GRUPO_FUNCIONAL_NAO_INFORMADO);
		}
		
		Long cc = rapGrupoFuncionalDAO.countRapGrupoFuncionalDuplicado(grupo);
		
		if(cc > Long.valueOf(0)){
			throw new ApplicationBusinessException(GrupoFuncionalDAOExceptionCode.MENSAGEM_ERRO_DESCRICAO_GRUPO_FUNCIONAL_JA_EXISTE);
		}
		
		try {
			
			rapGrupoFuncionalDAO.merge(grupo);
			rapGrupoFuncionalDAO.flush();

		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(), "RAP_SER_GRF_FK1")) {
					throw new ApplicationBusinessException(GrupoFuncionalDAOExceptionCode.ERRO_ALTERAR_CONSTRAINT_GRUPO_FUNCIONAL);
				}
			}
			throw e;
		}
	}


	public void inserirRapGrupoFuncional(final RapGrupoFuncional grupo) throws ApplicationBusinessException {
		final RapGrupoFuncional aux = rapGrupoFuncionalDAO.obterPorChavePrimaria(grupo.getCodigo());
		
		if(aux != null){
			throw new ApplicationBusinessException(GrupoFuncionalDAOExceptionCode.MENSAGEM_ERRO_CODIGO_GRUPO_FUNCIONAL_JA_EXISTE);
		}
		
		Long qtdeDescricoesExistentes = rapGrupoFuncionalDAO.countDescricaoRapGrupoFuncional(grupo);
		
		if(qtdeDescricoesExistentes > Long.valueOf(0)){
			throw new ApplicationBusinessException(GrupoFuncionalDAOExceptionCode.MENSAGEM_ERRO_DESCRICAO_GRUPO_FUNCIONAL_JA_EXISTE);
		}
		
		rapGrupoFuncionalDAO.persistir(grupo);
	}

	public void excluirGrupoFuncional(Short codigo) throws ApplicationBusinessException {
		try {
			
			RapGrupoFuncional grupo = rapGrupoFuncionalDAO.obterPorChavePrimaria(codigo);
			rapGrupoFuncionalDAO.remover(grupo);
			
		} catch (Exception e) {
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				if (StringUtils.containsIgnoreCase( ((ConstraintViolationException) e.getCause()).getConstraintName(), "RAP_SER_GRF_FK1")) {
					throw new ApplicationBusinessException( GrupoFuncionalONExceptionCode.ERRO_REMOVER_CONSTRAINT_GRUPO_FUNCIONAL);
				}
			}
			
			throw e;
		}
	}

	protected RapGrupoFuncionalDAO getGrupoFuncionalDAO() {
		return rapGrupoFuncionalDAO;
	}
}
