package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ClinicaCRUD extends BaseBusiness {
private static final String AGH_CLINICAS = "AGH_CLINICAS";
    
private static final Log LOG = LogFactory.getLog(ClinicaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade iFaturamentoFacade;

@EJB
private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8701077389855820612L;

	/**
	 * Enumeração com os códigos de mensagens de exceções negociais deste
	 * cadastro. Cada entrada nesta enumeracao deve corresponder a um chave no
	 * message bundle de cadastros básicos de internação. Porém se não houver
	 * uma este valor será enviado como mensagem de execeção sem localização
	 * alguma.
	 */
	public enum ExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_CLINICA, CODIGO_CLINICA_JA_EXISTENTE, CODIGO_CLINICA_OBRIGATORIO, DESCRICAO_CLINICA_OBRIGATORIO, ERRO_REMOVER_CLINICA, ERRO_VALIDACAO_MODELO_CLINICA, ERRO_FK_CLINICA, AGH_00070, AGH_00071, AGH_00114, AGH_00118, AGH_00204, ERRO_IDADE_PAC_INTERNACAO, ERRO_IDADE_PAC_AMBULATORIO, ERRO_CAPACIDADE_ZERO, DESCRICAO_CLINICA_JA_EXISTENTE, CLINICA_ESPECIALIDADES_DEPENTENTES, CLINICA_MOTIVOS_DESDOBRAMENTOS_DEPENTENTES
	}

	private enum Constraint {

		// Primary key constraint
		AGH_CLC_PK(AGH_CLINICAS, ExceptionCode.CODIGO_CLINICA_JA_EXISTENTE,
				""),

		// Foreign key constraints
		AAC_NBC_CLC_FK1("AAC_NIVEL_BUSCAS", ExceptionCode.ERRO_FK_CLINICA,
				"Níveis Busca"), AGH_ESP_CLC_FK1("AGH_ESPECIALIDADES",
				ExceptionCode.ERRO_FK_CLINICA, "Especialidades"), AGH_UNF_CLC_FK1(
				"AGH_UNIDADES_FUNCIONAIS", ExceptionCode.ERRO_FK_CLINICA,
				"Unidades Funcionais"), AIN_ATU_CLC_FK1(
				"AIN_ATENDIMENTOS_URGENCIA", ExceptionCode.ERRO_FK_CLINICA,
				"Atendimentos de Urgência"), AIN_INH_CLC_FK1(
				"AIN_INDICADORES_HOSPITALARES", ExceptionCode.ERRO_FK_CLINICA,
				"Indicadores Hospitalares"), AIN_INHT_CLC_FK1(
				"AIN_IND_HOSPITALARES_TEMP", ExceptionCode.ERRO_FK_CLINICA,
				"Indicadores Hospitalares Temp."), AIN_MPU_CLC_FK1(
				"AIN_MOVIMENTOS_ATEND_URGENCIA", ExceptionCode.ERRO_FK_CLINICA,
				"Movimentos Atend. Urgência"), AIN_QRT_CLC_FK1("AIN_QUARTOS",
				ExceptionCode.ERRO_FK_CLINICA, "Quartos"), FAT_DCI_CLC_FK1(
				"FAT_DOCUMENTO_COBRANCA_AIHS", ExceptionCode.ERRO_FK_CLINICA,
				"Documentos Cobrança AIHS"), FAT_IPH_CLC_FK1(
				"FAT_ITENS_PROCED_HOSPITALAR", ExceptionCode.ERRO_FK_CLINICA,
				"Itens Proced Hospitalar"), FAT_MVC_CLC_FK1(
				"FAT_MOTIVOS_DESDOBR_CLINICA", ExceptionCode.ERRO_FK_CLINICA,
				"Motivos Desdobr Clinica"), MAM_CEQ_CLC_FK1(
				"MAM_CRIT_ESC_QUESTS", ExceptionCode.ERRO_FK_CLINICA,
				"Journalled Crit Esc Quests"), MPM_CUC_CLC_FK1(
				"MPM_CUIDADO_USUAL_CLINICAS", ExceptionCode.ERRO_FK_CLINICA,
				"Cuidado Usual Clinicas"),
		// As FKs abaixo não existem no banco, mas são usadas no código do form.
		MPM_VCD_CLC_FK1("", ExceptionCode.ERRO_FK_CLINICA, "Cuidados usuais"), AGH_VUF_CLC_FK1(
				"", ExceptionCode.ERRO_FK_CLINICA, "V_AGH_UNID_FUNCIONAL"), AIN_QMS$CTRL_CLC_FK2(
				"", ExceptionCode.ERRO_FK_CLINICA, "QMS_CTRL_BLOCK"), OCO_10117440(
				"", ExceptionCode.ERRO_FK_CLINICA, "Especialidades"),
		// AGH_UNIDADES_FUNCIONAIS?
		// AGH_ESPECIALIDADES?

		// Check Constraints
		AGH_CLC_CK1(AGH_CLINICAS, ExceptionCode.AGH_00070, ""), AGH_CLC_CK2(
				AGH_CLINICAS, ExceptionCode.AGH_00071, ""), AGH_CLC_CK3(
				AGH_CLINICAS, ExceptionCode.AGH_00114, ""), AGH_CLC_CK4(
				AGH_CLINICAS, ExceptionCode.AGH_00118, ""), AGH_CLC_CK5(
				AGH_CLINICAS, ExceptionCode.AGH_00204, "");
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final String nome_tabela;
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final ExceptionCode exception_code;
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final String parametro;

		Constraint(String nome_tabela, ExceptionCode exception_code,
				String parametro) {
			this.nome_tabela = nome_tabela;
			this.exception_code = exception_code;
			this.parametro = parametro;
		}

		public String getNomeTabela() {
			return this.nome_tabela;
		}

		public ExceptionCode getExceptionCode() {
			return this.exception_code;
		}

		public String getParametro() {
			return this.parametro;
		}

	}

	/**
	 * Valida se existe clínica com o código informado.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public boolean codigoExistente(Integer codigo) {
		AghClinicas cliExistente = this.getAghuFacade().obterClinica(codigo);
		return (cliExistente != null);
	}

	/**
	 * Lança o erro correposndente a constraint que falhou.
	 * 
	 * @param clinica
	 * @param persistence_exception
	 * @throws ApplicationBusinessException
	 */
	private void lancaErroConstraint(AghClinicas clinica,
			PersistenceException pe, ExceptionCode exception_code_padrao)
			throws ApplicationBusinessException {
		Throwable cause = pe.getCause();
		if (cause instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) cause;
			try {
				if (cve.getConstraintName() == null) {
					throw new IllegalArgumentException();
				}
				String constraint_name = cve.getConstraintName().substring(
						cve.getConstraintName().indexOf(".") + 1);
				Constraint constraint = Constraint.valueOf(constraint_name); // throws
																				// IllegalArgumentException
				throw new ApplicationBusinessException(constraint.getExceptionCode(),
						clinica.getDescricao(), constraint.getParametro());
			} catch (IllegalArgumentException iae) {
				logError("ConstraintViolationException - Constraint não identificada",
								pe);
				throw new ApplicationBusinessException(exception_code_padrao);
			}
		}
	}

	/**
	 * Método responsável por inserir uma clínica
	 * 
	 * @dbtables AghClinicas select,insert
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */
	public void criar(AghClinicas clinica) throws ApplicationBusinessException {
		this.validarDados(clinica);
		// Validação adicional para evitar o erro NonUniqueObjectException
		if (codigoExistente(clinica.getCodigo())) {
			throw new ApplicationBusinessException(
					ExceptionCode.CODIGO_CLINICA_JA_EXISTENTE);
		}
		try {
			getAghuFacade().inserirAghClinicas(clinica);
			
		} catch (PersistenceException e) {
			logError("Erro ao incluir a clínica.", e);
			lancaErroConstraint(clinica, e, ExceptionCode.ERRO_PERSISTIR_CLINICA);
		} catch (Exception e) {
			logError("Erro ao incluir a clínica.", e);
			throw new ApplicationBusinessException(ExceptionCode.ERRO_PERSISTIR_CLINICA);
		}

	}

	/**
	 * Método responsável por atualizar uma clínica
	 * 
	 * @dbtables AghClinicas select,update
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */
	public void atualizar(AghClinicas clinica) throws ApplicationBusinessException {
		this.validarDados(clinica);
		try {
			
			getAghuFacade().atualizarAghClinicasDepreciado(clinica);
			
		} catch (PersistenceException e) {
			logError("Erro ao atualizar a clínica.", e);
			lancaErroConstraint(clinica, e,
					ExceptionCode.ERRO_PERSISTIR_CLINICA);
		} catch (BaseRuntimeException e) {
			logError("Erro ao atualizar a clínica.", e);
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_VALIDACAO_MODELO_CLINICA);
		} catch (Exception e) {
			logError("Erro ao atualizar a clínica.", e);
			throw new ApplicationBusinessException(ExceptionCode.ERRO_PERSISTIR_CLINICA);
		}

	}

	/**
	 * Apaga uma clínica do banco de dados.
	 * 
	 * @dbtables AghClinicas delete
	 * 
	 * @param clinica
	 *            Clínica a ser removida.
	 * @throws ApplicationBusinessException
	 */
	public void remover(Integer codigo) throws ApplicationBusinessException {
		
		AghClinicas clinica = getAghuFacade().obterClinica(codigo);
		
		try {			
			verificarDependencias(clinica);
			
			getAghuFacade().removerAghClinicas(clinica);
			flush();
			
		} catch (PersistenceException e) {
			lancaErroConstraint(clinica, e, ExceptionCode.ERRO_REMOVER_CLINICA);
		}
	}

	/**
	 * Verifica se a clínica não possui dependências para poder ser excluída
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */
	public void verificarDependencias(AghClinicas clinica)
			throws ApplicationBusinessException {
		List<AghEspecialidades> listaEspecialidades = getAghuFacade()
				.pesquisarEspecialidadesPorClinica(clinica);
		if (!listaEspecialidades.isEmpty()) {
			throw new ApplicationBusinessException(
					ExceptionCode.CLINICA_ESPECIALIDADES_DEPENTENTES);
		}
		
		List<FatMotivoDesdobrClinica> listaMotivosDesdClinicas = getFaturamentoFacade()
				.pesquisarMotivoDesdobrClinicaPorClinica(clinica);
		if (!listaMotivosDesdClinicas.isEmpty()) {
			throw new ApplicationBusinessException(
					ExceptionCode.CLINICA_MOTIVOS_DESDOBRAMENTOS_DEPENTENTES);
		}
	}

	public boolean validarDescricaoDuplicada(AghClinicas clinica) {
		AghClinicas outraClinicaMesmaDescricao = getAghuFacade().obterClinicaPelaDescricaoExata(clinica.getDescricao());
		// se existe clinica com a mesma descrição
		// e o código é diferente então retorna true
		return (outraClinicaMesmaDescricao != null && !clinica.equals(outraClinicaMesmaDescricao));
	}

	/**
	 * Método responsável pelas validações dos dados, utilizado na inclusão e
	 * atualização.
	 * 
	 * @param clinica
	 * @throws ApplicationBusinessException
	 */
	private void validarDados(AghClinicas clinica) throws ApplicationBusinessException {
		if (clinica.getCodigo() == null) {
			throw new ApplicationBusinessException(
					ExceptionCode.CODIGO_CLINICA_OBRIGATORIO);
		}
		if (StringUtils.isBlank(clinica.getDescricao())) {
			throw new ApplicationBusinessException(
					ExceptionCode.DESCRICAO_CLINICA_OBRIGATORIO);
		}
		if (validarDescricaoDuplicada(clinica)) {
			throw new ApplicationBusinessException(
					ExceptionCode.DESCRICAO_CLINICA_JA_EXISTENTE);
		}
		if (clinica.getCapacReferencial().intValue() == 0) {
			throw new ApplicationBusinessException(ExceptionCode.ERRO_CAPACIDADE_ZERO);
		}
		if (clinica.getIdadeMinPacInternacao() > clinica
				.getIdadeMaxPacInternacao()) {
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_IDADE_PAC_INTERNACAO);
		}
		if (clinica.getIdadeMinPacAmbulatorio() > clinica
				.getIdadeMaxPacAmbulatorio()) {
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_IDADE_PAC_AMBULATORIO);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}

}
