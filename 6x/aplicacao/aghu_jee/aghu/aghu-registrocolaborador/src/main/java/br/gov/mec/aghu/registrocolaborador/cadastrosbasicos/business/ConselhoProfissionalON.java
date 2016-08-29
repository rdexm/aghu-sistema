package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.registrocolaborador.dao.RapConselhosProfissionaisDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConselhoProfissionalON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ConselhoProfissionalON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapConselhosProfissionaisDAO rapConselhosProfissionaisDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6136589430399525036L;

	private enum ConselhoProfissionalONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_TIPO_QUALIFICACAO,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_EXAME,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_PERFIL_PROFISSIONAIS,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_ACOES_PROFISSIONAIS,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_GRUPO_AVALIADORES,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_MPA_POP,
		MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL;
	}

	/**
	 * Método para buscar um ConselhoProfissional na base de dados
	 */
	public RapConselhosProfissionais obterConselhoProfissional(Short codigoConselhoProfissional) throws ApplicationBusinessException {
		if (codigoConselhoProfissional == null) {
			throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		return getConselhosProfissionaisDAO().obterPorChavePrimaria(codigoConselhoProfissional); 
	}

	/**
	 * Método para excluir um vínculo
	 */
	
	public void excluirConselhoProfissional(Short codigo) throws ApplicationBusinessException {

		try {
		
			RapConselhosProfissionais conselhoProfissional = rapConselhosProfissionaisDAO.obterPorChavePrimaria(codigo);
			rapConselhosProfissionaisDAO.remover(conselhoProfissional);
			rapConselhosProfissionaisDAO.flush();
		} catch (Exception e) {
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "CSE_POC_CPR_FK1")) {
					throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_PERFIL_PROFISSIONAIS);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "CSE_ACP_CPR_FK1")) {
					throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_ACOES_PROFISSIONAIS);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "MPA_CGR_CPR_FK1")) {
					throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_GRUPO_AVALIADORES);
				}
				
				
				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "AEL_ECP_CPR_FK1")) {
					throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_EXAME);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "RAP_TQL_CPR_FK1")) {
					throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_TIPO_QUALIFICACAO);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(), "MPA_POP_CPR_FK1")) {
					throw new ApplicationBusinessException(ConselhoProfissionalONExceptionCode.MENSAGEM_ERRO_REMOVER_CONSELHO_PROFISSIONAL_COM_MPA_POP);
				}
			
			}
			throw e;
		}
	}
	
	protected RapConselhosProfissionaisDAO getConselhosProfissionaisDAO() {
		return rapConselhosProfissionaisDAO;
	}
}
