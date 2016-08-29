package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.registrocolaborador.dao.RapInstituicaoQualificadoraDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class InstituicaoQualificadoraON extends BaseBusiness {
	
	@EJB
	private InstituicaoQualificadoraRN instituicaoQualificadoraRN;
	
	private static final Log LOG = LogFactory.getLog(InstituicaoQualificadoraON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private RapInstituicaoQualificadoraDAO rapInstituicaoQualificadoraDAO;
	
	private static final long serialVersionUID = -6153005559702474853L;

	public enum InstituicaoQualificadoraONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INSTITUICAO_QUALIFICADORA_NAO_INFORMADA, MENSAGEM_INSTITUICAO_QUALIFICADORA_JA_CADASTRADA;
	}

	
	public void salvar(RapInstituicaoQualificadora rapInstituicaoQualificadora, boolean alteracao) throws ApplicationBusinessException {
		if (! alteracao) { // esta cadastrando um novo
			if (rapInstituicaoQualificadora.getCodigo() != null) {
				RapInstituicaoQualificadora instExistente = obterInstituicaoQualificadora(rapInstituicaoQualificadora.getCodigo());
				
				if (instExistente != null) {
					throw new ApplicationBusinessException( InstituicaoQualificadoraONExceptionCode.MENSAGEM_INSTITUICAO_QUALIFICADORA_JA_CADASTRADA);
				} 
			}
			
			rapInstituicaoQualificadoraDAO.persistir(rapInstituicaoQualificadora);
			
		} else {
			if (rapInstituicaoQualificadora == null || rapInstituicaoQualificadora.getCodigo() == 0) {
				throw new ApplicationBusinessException( InstituicaoQualificadoraONExceptionCode.MENSAGEM_INSTITUICAO_QUALIFICADORA_NAO_INFORMADA);
			}
			
			rapInstituicaoQualificadoraDAO.merge(rapInstituicaoQualificadora);
		}		
	}

	public RapInstituicaoQualificadora obterInstituicaoQualificadora(Integer id) {
		if (id == null) {
			throw new IllegalArgumentException("parametro obrigatório");
		}
		return rapInstituicaoQualificadoraDAO.obterPorChavePrimaria(id);
	}
	
	
	public void excluirInstituicaoQualificadora(Integer codInstituicaoQualificadora) throws ApplicationBusinessException  {
		RapInstituicaoQualificadora paraExcluir = obterInstituicaoQualificadora(codInstituicaoQualificadora);
		getInstituicaoQualificadoraRN().validaExclusaoInstituicaoQualificadora(paraExcluir);
		rapInstituicaoQualificadoraDAO.remover(paraExcluir);
	}

	protected InstituicaoQualificadoraRN getInstituicaoQualificadoraRN() {
		return instituicaoQualificadoraRN;
	}
	
	protected RapInstituicaoQualificadoraDAO getInstituicaoQualificadoraDAO() {
		return rapInstituicaoQualificadoraDAO;
	}
}