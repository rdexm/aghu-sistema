package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosJnDAO;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosJn;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class LocalDispensacaoMedicamentoRN extends
		AbstractAGHUCrudRn<AfaLocalDispensacaoMdtos> {

	private static final long serialVersionUID = -7102244288602831970L;

	private static final Log LOG = LogFactory.getLog(LocalDispensacaoMedicamentoRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public enum LocalDispensacaoMedicamentoRNExceptionCode implements
			BusinessExceptionCode {
		AFA_00169, AFA_00242, AFA_00244, AFA_00245, AFA_00172, AFA_00173,VIOLATION_UNIQUE;
	}

	@Inject
	private AfaLocalDispensacaoMdtosJnDAO afaLocalDispensacaoMdtosJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO;
	
	//********TRIGGERS********
	
	//PADRAO
	public void verificarAtribuirCriadoEm(
			AfaLocalDispensacaoMdtos localDispensacao) {
		localDispensacao.setCriadoEm(new Date());

	}

	public void verificarAtribuirServidor(
			AfaLocalDispensacaoMdtos localDispensacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		localDispensacao.setServidor(servidorLogado);
		if (localDispensacao.getServidor() == null) {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoRNExceptionCode.AFA_00169);
		}
	}

	public void verificarAtribuirVersion(
			AfaLocalDispensacaoMdtos localDispensacao) {
		localDispensacao.setVersion(1);
	}


	// BEFORE INSERT (AFAT_LDM_BRI) EM AFA_LOCAL_DISPENSACAO_MDTOS
	/**
	 * @ORADB TRIGGER "AGH".AFAT_LDM_BRI
	 * 
	 * @param localDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean briPreInsercaoRow(AfaLocalDispensacaoMdtos localDispensacao,
			String nomeMicrocomputador,
			Date dataFimVinculoServidor)
			throws BaseException, ApplicationBusinessException {
		
		//Verifica se ja existe, para nao violar unique constraint
		verificaLocalDispensacaoExistente(localDispensacao.getId());

		verificaUnidadeFuncional(localDispensacao.getUnidadeFuncional(),
				TipoUnfLdmEnum.TIPO_UNF_I);
		verificaUnidadeFuncional(localDispensacao
				.getUnidadeFuncionalDispDoseInt(), TipoUnfLdmEnum.TIPO_UNF_F);
		verificaUnidadeFuncional(localDispensacao
				.getUnidadeFuncionalDispAlternativa(),
				TipoUnfLdmEnum.TIPO_UNF_F);
		verificaUnidadeFuncional(localDispensacao
				.getUnidadeFuncionalDispDoseFrac(), TipoUnfLdmEnum.TIPO_UNF_F);

		// Seta valores automáticos
		this.verificarAtribuirCriadoEm(localDispensacao);
		this.verificarAtribuirServidor(localDispensacao);
		this.verificarAtribuirVersion(localDispensacao);

		return true;

	}

	// BEFORE UPDATE (AFAT_LDM_BRU) EM AFA_LOCAL_DISPENSACAO_MDTOS
	/**
	 * @ORADB TRIGGER "AGH".AFAT_LDM_BRU
	 * 
	 * @param localDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean bruPreAtualizacaoRow(AfaLocalDispensacaoMdtos original,
			AfaLocalDispensacaoMdtos modificada, String nomeMicrocomputador,
			Date dataFimVinculoServidor)
			throws ApplicationBusinessException {

		// Verifica alteracao UnidadeFuncional
		if (CoreUtil.modificados(modificada.getUnidadeFuncional(), original
				.getUnidadeFuncional())) {
			verificaUnidadeFuncional(modificada.getUnidadeFuncional(),
					TipoUnfLdmEnum.TIPO_UNF_I);
		}
		if (CoreUtil.modificados(modificada.getUnidadeFuncionalDispDoseInt(),
				original.getUnidadeFuncionalDispDoseInt())) {
			verificaUnidadeFuncional(modificada
					.getUnidadeFuncionalDispDoseInt(),
					TipoUnfLdmEnum.TIPO_UNF_F);
		}
		if (CoreUtil.modificados(modificada
				.getUnidadeFuncionalDispAlternativa(), original
				.getUnidadeFuncionalDispAlternativa())) {
			verificaUnidadeFuncional(modificada
					.getUnidadeFuncionalDispAlternativa(),
					TipoUnfLdmEnum.TIPO_UNF_F);
		}
		if (CoreUtil.modificados(modificada.getUnidadeFuncionalDispDoseFrac(),
				original.getUnidadeFuncionalDispDoseFrac())) {
			verificaUnidadeFuncional(modificada
					.getUnidadeFuncionalDispDoseFrac(),
					TipoUnfLdmEnum.TIPO_UNF_F);
		}
		// Apos validacao
		this.verificarAtribuirServidor(modificada);

		return true;

	}

	// BEFORE DELETE EM AFA_LOCAL_DISPENSACAO_MDTOS
	/**
	 * @ORADB TRIGGER "AGH".AFAT_LDM_BRD
	 * 
	 * @param localDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean brdPreRemocaoRow(AfaLocalDispensacaoMdtos entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {

		AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_DIAS_PERM_DEL_AFA;
		AghParametros parametroDias = null;
		parametroDias = getParametroFacade().buscarAghParametro(parametroEnum);
		// Calcula a diferença em dias
		verificarDiferencaDias(parametroDias, entidade.getCriadoEm());

		this.verificarAtribuirServidor(entidade);

		return true;
	}

	// AFTER DELETE (AFAT_LDM_ARD) EM AFA_LOCAL_DISPENSACAO_MDTOS
	/**
	 * @ORADB TRIGGER "AGH".AFAT_LDM_ARD
	 * 
	 * @param localDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean ardPosRemocaoRow(AfaLocalDispensacaoMdtos entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {

		AfaLocalDispensacaoMdtosJn afaLocalDispensacaoMdtosJn = this
				.criarJournal(entidade, DominioOperacoesJournal.DEL);
		this.insertAfaLocalDispensacaoMdtosJn(afaLocalDispensacaoMdtosJn);

		return true;
	}

	// AFTER UPDATE (AFAT_LDM_ARU) EM AFA_LOCAL_DISPENSACAO_MDTOS
	/**
	 * @ORADB TRIGGER "AGH".AFAT_LDM_ARU
	 * 
	 * @param localDispensacaoOriginal
	 * @param localDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean aruPosAtualizacaoRow(AfaLocalDispensacaoMdtos original,
			AfaLocalDispensacaoMdtos modificada, String nomeMicrocomputador,
			final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {

		if (!CoreUtil.igual(original.getServidor(), modificada.getServidor())
				|| !CoreUtil.igual(original.getCriadoEm(), modificada
						.getCriadoEm())
				|| !CoreUtil.igual(original.getUnidadeFuncional(), modificada
						.getUnidadeFuncional())
				|| !CoreUtil.igual(original.getUnidadeFuncionalDispDoseInt(),
						modificada.getUnidadeFuncionalDispDoseInt())
				|| !CoreUtil.igual(original.getUnidadeFuncionalDispDoseFrac(),
						modificada.getUnidadeFuncionalDispDoseFrac())
				|| !CoreUtil.igual(original
						.getUnidadeFuncionalDispAlternativa(), modificada
						.getUnidadeFuncionalDispAlternativa())
				|| !CoreUtil.igual(original
						.getUnidadeFuncionalDispUsoDomiciliar(), modificada
						.getUnidadeFuncionalDispUsoDomiciliar())) {

			AfaLocalDispensacaoMdtosJn afaLocalDispensacaoMdtosJn = this
					.criarJournal(original, DominioOperacoesJournal.UPD);
			this.insertAfaLocalDispensacaoMdtosJn(afaLocalDispensacaoMdtosJn);
		}

		return true;
	}

	//********PROCEDURES E FUNCTIONS********

	/**
	 * @ORADB procedure AFAK_RN.RN_AFAP_VER_DEL
	 * 
	 * @param parametroDias
	 * @param criadoEm
	 * @throws ApplicationBusinessException
	 */
	public void verificarDiferencaDias(AghParametros parametroDias,
			Date criadoEm) throws ApplicationBusinessException {
		Float diferenca = CoreUtil.diferencaEntreDatasEmDias(Calendar
				.getInstance().getTime(), criadoEm);
		if (diferenca > parametroDias.getVlrNumerico().floatValue()) {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoRNExceptionCode.AFA_00172);
		}
	}

	/**
	 * @ORADB procedure AFAK_LDM_RN.RN_LDMP_VER_UNF Implementacao de Procedure
	 * para verificar unidadeFuncional Exemplo
	 * afak_ldm_rn.rn_ldmp_ver_unf(:new.unf_seq, 'I');
	 * 
	 * @param unidadeFuncional
	 * @param tipoUnidadeFuncional
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void verificaUnidadeFuncional(
			AghUnidadesFuncionais unidadeFuncional,
			TipoUnfLdmEnum tipoUnidadeFuncional)
			throws ApplicationBusinessException {

		AghUnidadesFuncionais unfValidacao = getAghuFacade().obterUnidadeFuncionalPorChavePrimaria(unidadeFuncional);

		verificaSituacaoUnidadeFuncional(unfValidacao);

		String[] caracteristicasArray = null;

		if (tipoUnidadeFuncional.equals(TipoUnfLdmEnum.TIPO_UNF_I)) {
			caracteristicasArray = new String[]{ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO.getCodigo(),
					ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA.getCodigo(),ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA.getCodigo(),
					ConstanteAghCaractUnidFuncionais.CO.getCodigo(), ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO.getCodigo()};
			if (DominioSimNao.N
					.equals(existeCaractUnFuncional(unfValidacao, caracteristicasArray))) {
				throw new ApplicationBusinessException(
						LocalDispensacaoMedicamentoRNExceptionCode.AFA_00244);
			}
		} else {
			caracteristicasArray = new String[]{ConstanteAghCaractUnidFuncionais.UNID_FARMACIA.getCodigo()};
			if (DominioSimNao.N.equals(existeCaractUnFuncional(unfValidacao,caracteristicasArray))) {
				throw new ApplicationBusinessException(
						LocalDispensacaoMedicamentoRNExceptionCode.AFA_00245);
			}

		}

	}
	
	public void verificaSituacaoUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		if (!unidadeFuncional.getIndSitUnidFunc().isAtivo()) {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoRNExceptionCode.AFA_00242);
		}
	}
	
	
	
	/**
	 * @ORADB FUNCTION AGHC_VER_CARACT_UNF
	 * 
	 * Verifica se uma unidade funcional tem determinada característica, se
	 * existir retorna 'S', se não existir retorna 'N'
	 * 
	 * @param unidadeFuncional
	 * @throws ApplicationBusinessException
	 */
	public DominioSimNao existeCaractUnFuncional(
			AghUnidadesFuncionais unidadeFuncional, String[] caracteristicaArray) {

		DominioSimNao retorno = DominioSimNao.N;

		Set<AghCaractUnidFuncionais> caracteristicas = unidadeFuncional
				.getCaracteristicas();
		boolean contem = false;
		if (caracteristicas != null && caracteristicas.size() > 0) {
			for (AghCaractUnidFuncionais caracteristicaUnf : caracteristicas) {
				for (int i = 0; i < caracteristicaArray.length; i++) {
					if (caracteristicaUnf.getId().getCaracteristica()
							.getCodigo().equals(caracteristicaArray[i])) {
						retorno = DominioSimNao.S;
						contem = true;
						break;
					}
				}
				if (contem) {
					break;
				}
			}
		}
		return retorno;
	}
	
	// ********UTIL********
	
	public void verificaLocalDispensacaoExistente(AfaLocalDispensacaoMdtosId localDispensacaoId) throws ApplicationBusinessException {
		
		AfaLocalDispensacaoMdtos localDispensacaoBusca = getAfalocalDispensacaoMdtosDAO().obterPorChavePrimaria(localDispensacaoId);
		if (localDispensacaoBusca != null) {
			throw new ApplicationBusinessException(LocalDispensacaoMedicamentoRNExceptionCode.VIOLATION_UNIQUE);
		}
		
	}

	public AfaLocalDispensacaoMdtosJnDAO getAfaLocalDispensacaoMdtosJnDAO() {
		return afaLocalDispensacaoMdtosJnDAO;
	}
	
	public IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public AfaLocalDispensacaoMdtosDAO getAfalocalDispensacaoMdtosDAO() {
		return afaLocalDispensacaoMdtosDAO;
	}

	
	//********JOURNAL********
	
	@SuppressWarnings("deprecation")
	public void insertAfaLocalDispensacaoMdtosJn(
			final AfaLocalDispensacaoMdtosJn afaLocalDispensacaoMdtosJn)
			throws ApplicationBusinessException {

		this.getAfaLocalDispensacaoMdtosJnDAO().persistir(
				afaLocalDispensacaoMdtosJn);
		this.getAfaLocalDispensacaoMdtosJnDAO().flush();
	}

	public AfaLocalDispensacaoMdtosJn criarJournal(
			final AfaLocalDispensacaoMdtos original,
			final DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AfaLocalDispensacaoMdtosJn afaLocalDispensacaoMdtosJn = BaseJournalFactory.getBaseJournal(operacao,
						AfaLocalDispensacaoMdtosJn.class, servidorLogado.getUsuario());

		afaLocalDispensacaoMdtosJn.setMedicamento(original.getMedicamento());
		afaLocalDispensacaoMdtosJn.setServidor(original.getServidor());
		afaLocalDispensacaoMdtosJn.setCriadoEm(original.getCriadoEm());
		afaLocalDispensacaoMdtosJn.setUnidadeFuncional(original.getUnidadeFuncional());
		afaLocalDispensacaoMdtosJn.setUnidadeFuncionalDispDoseInt(original.getUnidadeFuncionalDispDoseInt());
		afaLocalDispensacaoMdtosJn.setUnidadeFuncionalDispDoseFrac(original.getUnidadeFuncionalDispDoseFrac());
		afaLocalDispensacaoMdtosJn.setUnidadeFuncionalDispAlternativa(original.getUnidadeFuncionalDispAlternativa());
				
		if (original.getUnidadeFuncionalDispUsoDomiciliar() != null 
				&& original.getUnidadeFuncionalDispUsoDomiciliar().getSeq() != null) {
			afaLocalDispensacaoMdtosJn.setUnidadeFuncionalDispUsoDomiciliar(original.getUnidadeFuncionalDispUsoDomiciliar());
		}

		return afaLocalDispensacaoMdtosJn;
	}

	// ********ENUMS********

	/**
	 * Enumeracao de <em>TipoUnfLdm</em>
	 * 
	 */
	public static enum TipoUnfLdmEnum {

		TIPO_UNF_I("I"), TIPO_UNF_F("F");

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final String str;

		TipoUnfLdmEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
