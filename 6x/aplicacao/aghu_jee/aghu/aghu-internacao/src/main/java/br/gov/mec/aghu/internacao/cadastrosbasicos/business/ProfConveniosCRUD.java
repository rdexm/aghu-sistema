package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.dao.AinEscalasProfissionalIntDAO;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de convenios para profissionais.
 */
@SuppressWarnings("deprecation")
@Stateless
public class ProfConveniosCRUD extends BaseBusiness {


@Inject
private AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO;

private static final Log LOG = LogFactory.getLog(ProfConveniosCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IProcedimentoTerapeuticoFacade iProcedimentoTerapeuticoFacade;

@EJB
private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8294283815806897162L;

	private enum ProfConveniosCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_CONVENIO_PROFISSIONAL_OBRIGATORIO, ERRO_PROFISSIONAL_OBRIGATORIO, ERRO_PERSISTIR_CONVENIO_PROFISSIONAL, ERRO_CONVENIO_JA_EXISTENTE, 
		ERRO_REMOVER_CONVENIO_PROFISSIONAL_EPI_PEC_FK1, ERRO_EXCLUSAO_CONVENIO_DEPENDENCIA, ERRO_REMOVER_CONVENIO_PROFISSIONAL, ERRO_DE_CONSTRAINT,
		ERRO_EXCLUSAO_CONVENIO_DEPENDENCIA_ESCALA;
	}

	/**
	 * @dbtables AghProfEspecialidades select
	 * @param matricula
	 * @param vinculo
	 * @param esp
	 * @return
	 */
	public AghProfEspecialidades obterAghProfEspecialidades(Integer matricula,
			Integer vinculo, Integer esp) {
		AghProfEspecialidades retorno = null;

		List<AghProfEspecialidades> res = this.getAghuFacade().listarProfEspecialidades(matricula, vinculo, esp);
		Iterator<AghProfEspecialidades> it = res.iterator();
		if (it.hasNext()) {
			retorno = it.next();
		}
		return retorno;

	}

	/**
	 * @dbtables MptProfCredAssinatLaudo select
	 * @param matricula
	 * @param vinculo
	 * @param esp
	 * @param convenio
	 * @return
	 */
	public Boolean verificarExclusao(Integer matricula, Integer vinculo,
			Integer esp, Short convenio) {
		Long count = this.getProcedimentoTerapeuticoFacade().listarProfCredAssinatLaudoCount(matricula, vinculo, esp, convenio);
		return count > 0;
	}

	/**
	 * Remover AghProfEspConvenios de AghProfEspecialidades.
	 * 
	 * @dbtables AghProfissionaisEspConvenio select,delete
	 * @param item
	 */
	public void removerAghProfEspConvenios(AghProfissionaisEspConvenio item)
			throws ApplicationBusinessException {
		IAghuFacade aghuFacade = this.getAghuFacade();
		
		aghuFacade.limparEntityManager();
		
		List<AinEscalasProfissionalInt> listaEscalasProfissionais = getAinEscalasProfissionalIntDAO()
		.pesquisarEscalasPorConvenioEspecialidade(item);

		if (listaEscalasProfissionais.size() > 0){
			throw new ApplicationBusinessException(ProfConveniosCRUDExceptionCode.ERRO_EXCLUSAO_CONVENIO_DEPENDENCIA_ESCALA);
		}

		try {
			AghProfissionaisEspConvenio aux = aghuFacade.obterAghProfissionaisEspConvenioPorChavePrimaria(item.getId());
			if (aux != null) {
				aghuFacade.removerAghProfissionaisEspConvenio(aux, true);
			}
		} catch (Exception e) {
			if (e.getCause() != null
					&& ConstraintViolationException.class.equals(e.getCause()
							.getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e
						.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),"AIN_EPI_PEC_FK1")) {
					throw new ApplicationBusinessException(
							ProfConveniosCRUDExceptionCode.ERRO_REMOVER_CONVENIO_PROFISSIONAL_EPI_PEC_FK1);
				}

				throw new ApplicationBusinessException(
						ProfConveniosCRUDExceptionCode.ERRO_EXCLUSAO_CONVENIO_DEPENDENCIA);
			}
			logError("Erro ao remover AghProfissionaisEspConvenio.", e);
			throw new ApplicationBusinessException(
					ProfConveniosCRUDExceptionCode.ERRO_REMOVER_CONVENIO_PROFISSIONAL);
		}
	}

	public void inserirAghProfEspConvenios(AghProfissionaisEspConvenio aghProfissionaisEspConvenio) throws ApplicationBusinessException {
		IAghuFacade aghuFacade = this.getAghuFacade();

		aghuFacade.limparEntityManager();
		
		aghuFacade.persistirAghProfissionaisEspConvenio(aghProfissionaisEspConvenio);
		
		try {
			flush();
		} catch (Exception e) {
			logError("Erro ao atualizar AghProfissionaisEspConvenio.", e);
			throw new ApplicationBusinessException(
					ProfConveniosCRUDExceptionCode.ERRO_PERSISTIR_CONVENIO_PROFISSIONAL);
		}
	}

	public void atualizarAghProfEspConvenios(AghProfissionaisEspConvenio aghProfissionaisEspConvenio) throws ApplicationBusinessException {
		IAghuFacade aghuFacade = this.getAghuFacade();

		aghuFacade.limparEntityManager();
		
		aghuFacade.atualizarAghProfissionaisEspConvenio(aghProfissionaisEspConvenio);
		
		try {
			flush();
		} catch (Exception e) {
			logError("Erro ao atualizar AghProfissionaisEspConvenio.", e);
			throw new ApplicationBusinessException(
					ProfConveniosCRUDExceptionCode.ERRO_PERSISTIR_CONVENIO_PROFISSIONAL);
		}
	}

	// AGH_UNF_PK
	// Associa constraints que podem ser lançadas pelo banco aos erros de
	// negócio
	private enum Constraint {
		AIN_EPI_PEC_FK1(ProfConveniosCRUDExceptionCode.ERRO_DE_CONSTRAINT);

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final ProfConveniosCRUDExceptionCode exception_code;

		Constraint(ProfConveniosCRUDExceptionCode exception_code) {
			this.exception_code = exception_code;
		}

		public ProfConveniosCRUDExceptionCode getExceptionCode() {
			return this.exception_code;
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return this.iProcedimentoTerapeuticoFacade;
	}
	
	protected AinEscalasProfissionalIntDAO getAinEscalasProfissionalIntDAO(){
		return ainEscalasProfissionalIntDAO;
	}

}
