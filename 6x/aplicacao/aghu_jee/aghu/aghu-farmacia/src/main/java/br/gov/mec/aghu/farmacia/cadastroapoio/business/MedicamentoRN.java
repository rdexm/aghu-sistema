package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndFotoSensibilidade;
import br.gov.mec.aghu.dominio.DominioIndUnidTempoMdto;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoDAO;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoJn;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Triggers de:<br/>
 * @ORADB: <code>AFA_MEDICAMENTOS</code>
 * @author gandriotti
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength","PMD.HierarquiaONRNIncorreta"})
@Stateless
public class MedicamentoRN extends AbstractAGHUCrudRn<AfaMedicamento> {

	@EJB
	private ViaAdministracaoMedicamentoON viaAdministracaoMedicamentoON;
	
	@EJB
	private ComponenteNptON componenteNptON;
	
	@EJB
	private FormaDosagemON formaDosagemON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6902515828432902124L;
	
	private static final Log LOG = LogFactory.getLog(MedicamentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Inject
	private AfaMedicamentoJnDAO afaMedicamentoJnDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Inject
	private AfaViaAdministracaoMedicamentoDAO afaViaAdministracaoMedicamentoDAO;

	@Inject
	private AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	
	/**
	 * TODO remover quando quando CRUD de local de dispensacao for implementado
	 * 2011.02.18 -- Orientacao dada por Renato Vacaro da CGTI
	 * 
	 * @author gandriotti
	 */
	private final static boolean DESLIGAR_VALIDACAO_LOCAL_DISPENSACAO = true;

	/**
	 * Este metodo eh temporario e destina-se a permitir sobre-escrever
	 * {@link #DESLIGAR_VALIDACAO_LOCAL_DISPENSACAO} para realizacao de testes
	 * unitarios em {@link MedicamentoRNTest}.
	 * 
	 * @return
	 * @see #DESLIGAR_VALIDACAO_LOCAL_DISPENSACAO
	 */
	protected boolean desligarValidacaoLocalDispensacao() {

		return MedicamentoRN.DESLIGAR_VALIDACAO_LOCAL_DISPENSACAO;
	}

	protected AfaMedicamentoDAO getEntidadeDao() {

		return afaMedicamentoDAO;
	}

	protected AfaMedicamentoJnDAO getEntidadeJournalDao() {

		return afaMedicamentoJnDAO;
	}

	protected AfaFormaDosagemDAO getFormaDosagemDao() {

		return afaFormaDosagemDAO;
	}

	protected AfaViaAdministracaoMedicamentoDAO getViaAdmMdtoDao() {

		return afaViaAdministracaoMedicamentoDAO;
	}

	protected AfaLocalDispensacaoMdtosDAO getLocalDispMtdoDao() {

		return afaLocalDispensacaoMdtosDAO;
	}

	/**
	 * @param entidade
	 * @param operacao
	 * @return
	 * @see #getAghuFacade()
	 * @see #getDataCriacao()
	 */
	protected AfaMedicamentoJn getNewJournal(final AfaMedicamento entidade, final DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		return BaseJournalFactory.getBaseJournal(operacao,
				AfaMedicamentoJn.class,
				servidorLogado != null ? servidorLogado.getUsuario() : null);
	}

	/**
	 * <p>
	 * Ajusta a referencia ao usuario atualmente logado e a data atual.<br/>
	 * </p>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @see #getDataCriacao()
	 */
	protected void setServidorData(final AfaMedicamento entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		entidade.setRapServidores(servidorLogado);
		entidade.setCriadoEm(this.getDataCriacao());
	}

	/**
	 * <p>
	 * Ajusta a referencia ao usuario atualmente logado e a data atual.<br/>
	 * </p>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @see #getDataCriacao()
	 */
	protected void setServidorDataJn(final AfaMedicamentoJn entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		entidade.setMatricula(servidorLogado.getId().getMatricula());
		entidade.setVinCodigo(servidorLogado.getId().getVinCodigo());
		entidade.setCriadoEm(this.getDataCriacao());
	}

	/**
	 * <p>
	 * Apenas insere a entrada adequada no Journal.
	 * </p>
	 * 
	 * @param entidade
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @see {@link #getEntidadeJournalDao()}
	 * @see #getNewJournal(AfaMedicamento, DominioOperacoesJournal)
	 * @see #setServidorDataJn(AfaMedicamentoJn)
	 */
	protected boolean inserirEntradaJournal(final AfaMedicamento entidade, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {

		boolean result = false;
		AfaMedicamentoJn journal = null;
		AfaMedicamentoJnDAO dao = null;

		dao = this.getEntidadeJournalDao();
		journal = this.getNewJournal(entidade, operacao);
		if (dao != null) {
			journal.setMatCodigo(entidade.getMatCodigo());
			
			RapServidoresId servidorId = entidade.getRapServidores().getId();
			journal.setMatricula(servidorId.getMatricula());
			journal.setVinCodigo(servidorId.getVinCodigo());
			
			MpmUnidadeMedidaMedica unidadeMedidaMedica = entidade.getMpmUnidadeMedidaMedicas();
			if (unidadeMedidaMedica != null) {
				journal.setSeqUnidadeMedidaMedica(unidadeMedidaMedica.getSeq());	
			}
			
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = entidade.getMpmTipoFreqAprazamentos();
			if (tipoFrequenciaAprazamento != null) {
				journal.setSeqTipoFrequenciaAprazamento(tipoFrequenciaAprazamento.getSeq());	
			}
			
			AfaTipoUsoMdto tipoUsoMdto = entidade.getAfaTipoUsoMdtos();
			if (tipoUsoMdto != null) {
				journal.setSiglaTipoUsoMdto(tipoUsoMdto.getSigla());	
			}
			
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento = entidade.getTipoApresentacaoMedicamento();
			if (tipoApresentacaoMedicamento != null) {
				journal.setTprSigla(tipoApresentacaoMedicamento.getSigla());
			}
			
			journal.setCriadoEm(entidade.getCriadoEm());
			journal.setDescricao(entidade.getDescricao());
			journal.setIndSituacao(entidade.getIndSituacao());
			journal.setIndCalcDispensacaoFracionad(entidade.getIndCalcDispensacaoFracionad());
			journal.setIndPadronizacao(entidade.getIndPadronizacao());
			journal.setIndPermiteDoseFracionada(entidade.getIndPermiteDoseFracionada());
			journal.setIndSobraReaproveitavel(entidade.getIndSobraReaproveitavel());
			journal.setIndExigeObservacao(entidade.getIndExigeObservacao());
			journal.setIndRevisaoCadastro(entidade.getIndRevisaoCadastro());
			journal.setConcentracao(entidade.getConcentracao());
			journal.setHrioInicioAdmSugerido(entidade.getHrioInicioAdmSugerido());
			journal.setObservacao(entidade.getObservacao());
			journal.setQtdeCaloriasGrama(entidade.getQtdeCaloriasGrama());
			journal.setDoseMaximaMgKg(entidade.getDoseMaximaMgKg());
			journal.setFrequenciaUsual(entidade.getFrequenciaUsual());
			journal.setIndicacoes(entidade.getIndicacoes());
			journal.setContraIndicacoes(entidade.getContraIndicacoes());
			journal.setCuidadoConservacao(entidade.getCuidadoConservacao());
			journal.setOrientacoesAdministracao(entidade.getOrientacoesAdministracao());
			journal.setIndDiluente(entidade.getIndDiluente());
			journal.setIndGeraDispensacao(entidade.getIndGeraDispensacao());
			journal.setLinkParecerIndeferido(entidade.getLinkParecerIndeferido());
			journal.setLinkProtocoloUtilizacao(entidade.getLinkProtocoloUtilizacao());
			journal.setDescricaoEtiquetaFrasco(entidade.getDescricaoEtiquetaFrasco());
			journal.setDescricaoEtiquetaSeringa(entidade.getDescricaoEtiquetaSeringa());
			journal.setIndInteresseCcih(entidade.getIndInteresseCcih());
			journal.setIndGeladeira(entidade.getIndGeladeira());
			journal.setIndUnitariza(entidade.getIndUnitariza());
			journal.setIndFotosensibilidade(entidade.getIndFotosensibilidade());
			journal.setIndUnidadeTempo(entidade.getIndUnidadeTempo());
			journal.setTempoFotosensibilidade(entidade.getTempoFotosensibilidade());
			
			dao.persistir(journal);
			dao.flush();
			result = true;
		}

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_TP_MDTO</code> @ORADB: <code>AFA_MED_CK14</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FarmaciaExceptionCode#AFA_00250
	 * @see FarmaciaExceptionCode#AFA_00240
	 */
	protected boolean verificaTipoUsoMedicamento(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;
		AfaTipoUsoMdto tipoUso = null;

		tipoUso = entidade.getAfaTipoUsoMdtos();
		if (tipoUso == null) {
			//AFA_MED_CK14
			if (DominioSituacaoMedicamento.A.equals(entidade.getIndSituacao())) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00240);
			}
			result = true;
		} else if (!tipoUso.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00250);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_TP_FREQ</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FarmaciaExceptionCode#AFA_00246
	 * @see FarmaciaExceptionCode#AFA_00248
	 * @see FarmaciaExceptionCode#AFA_00249
	 */
	protected boolean verificaTipoFrequenciaAprazamentos(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;
		MpmTipoFrequenciaAprazamento apraz = null;

		apraz = entidade.getMpmTipoFreqAprazamentos();
		if (apraz == null) {
			throw new IllegalArgumentException();
		}
		if (!apraz.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00246);
		}
		if (!Boolean.TRUE.equals(apraz.getIndDigitaFrequencia()) && (entidade.getFrequenciaUsual() != null)) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00248);
		}
		if (Boolean.TRUE.equals(apraz.getIndDigitaFrequencia()) && (entidade.getFrequenciaUsual() == null)) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00249);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_TP_APRES</code> @ORADB: <code>AFA_MED_CK13</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FarmaciaExceptionCode#AFA_00237
	 * @see FarmaciaExceptionCode#AFA_00239
	 */
	protected boolean verificaTipoApresentacao(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;

		// quando existir o relacionamento com o tipo apresentacao do medicamento este devera estar ativo ( = 'A')
		if (entidade.getTipoApresentacaoMedicamento() == null) {
			// AFA_MED_CK13			
			if (DominioSituacaoMedicamento.A.equals(entidade.getIndSituacao())) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00239);
			}
			result = true;
		} else if (!entidade.getTipoApresentacaoMedicamento().getSituacao().isAtivo()) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00237);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_MDDA</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FarmaciaExceptionCode#AFA_00233
	 * @see FarmaciaExceptionCode#AFA_00234
	 */
	protected boolean verificaUnidadeMedidaIndConcentracao(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;
		MpmUnidadeMedidaMedica umm = null;

		umm = entidade.getMpmUnidadeMedidaMedicas();
		if (umm == null) {
			throw new IllegalArgumentException();
		}
		if (!umm.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00233);
		}
		if (!umm.getIndConcentracao().isSim()) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00234);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>AFAP_VALIDA_FOTOSENSIBILIDADE</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link FarmaciaExceptionCode#AFA_01468}
	 * @see {@link FarmaciaExceptionCode#AFA_01469}
	 */
	protected boolean verificaFotoSensibilidade(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = true;
		DominioIndFotoSensibilidade indFoto = null;
		DominioIndUnidTempoMdto unTempo = null;
		Short tempo = null;

		if (entidade == null) {
			throw new IllegalArgumentException("entidade não pode ser nulo.");
		}
		indFoto = entidade.getIndFotosensibilidade();
		unTempo = entidade.getIndUnidadeTempo();
		tempo = entidade.getTempoFotosensibilidade();
		/*
		 * Se o tipo de foto sensibilidade eh do tipo temporal ('T') 
		 * entao eh necessario fornecer o valor e unidade de tempo. 
		 * Caso contrario (do tipo nao-temporal), nenhuma destas 
		 * informacoes (adicionais) pode estar preenchido
		 */
		if (DominioIndFotoSensibilidade.T.equals(indFoto) && ((tempo == null) || (unTempo == null))) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01468);
		} else if (!DominioIndFotoSensibilidade.T.equals(indFoto) && ((tempo != null) || (unTempo != null))) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01469);
		}

		return result;
	}

	/**
	 * @ORADB: <code>AFAP_VALIDA_TIPO_QUIMIO</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see FarmaciaExceptionCode#AFA_01502
	 */
	protected boolean verificaTipoUsoQuimio(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = true;

		if (entidade == null) {
			throw new IllegalArgumentException("entidade não pode ser nulo.");
		}
		/*
		 * Se o tipo de uso do medicamento eh quimio-terapico, 
		 * o medicamento precisa ter o tipo deste deve estar marcado no 
		 * proprio medicamento
		 */
		if ((entidade.getAfaTipoUsoMdtos() != null)
				&& Boolean.TRUE.equals(entidade.getAfaTipoUsoMdtos().getIndQuimioterapico())
				&& (entidade.getTipoQuimio() == null)) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01502);
		}

		return result;
	}

	/**
	 * @see #verificaTipoUsoMedicamento(AfaMedicamento)
	 * @see #verificaTipoFrequenciaAprazamentos(AfaMedicamento)
	 * @see #verificaTipoApresentacao(AfaMedicamento)
	 * @see #verificaUnidadeMedidaIndConcentracao(AfaMedicamento)
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link #verificaTipoUsoMedicamento(AfaMedicamento)}
	 * @see #verificaTipoFrequenciaAprazamentos(AfaMedicamento)
	 * @see #verificaTipoApresentacao(AfaMedicamento)
	 * @see #verificaUnidadeMedidaIndConcentracao(AfaMedicamento)
	 * @see #verificaFotoSensibilidade(AfaMedicamento)
	 * @see FarmaciaExceptionCode
	 */
	protected boolean verificaValidadeEntidade(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = true;

		if (DominioSituacaoMedicamento.A.equals(entidade.getIndSituacao())) {
			// consiste se o tipo apresentacao do medicamento, quando existir, esteja ativo
			result = result && this.verificaTipoApresentacao(entidade);
			// consiste se o tipo uso do medicamento, quando existir, esteja ativo
			result = result && this.verificaTipoUsoMedicamento(entidade);
			// consiste se a unidade de medida medica esta ativa e se ind_concentracao = 's'		
			if (result && (entidade.getMpmUnidadeMedidaMedicas() != null)) {
				result = result && this.verificaUnidadeMedidaIndConcentracao(entidade);
			}
		} else {
			result = true;
		}
		// consiste se o tipo frequencia aprazamento, quando existir, esteja ativo e a frequencia usual em relacao ao ind_digita_frequencia
		if (result && (entidade.getMpmTipoFreqAprazamentos() != null)) {
			result = result && this.verificaTipoFrequenciaAprazamentos(entidade);
		}
		// verifica o correto preenchimento do conjunto de dados para foto sensibilidade, precisam ser sempre validos
		result = result && this.verificaFotoSensibilidade(entidade);
		// verifica o uso de medicamentos do tipo quimio terapicos, sempre validos
		result = result && this.verificaTipoUsoQuimio(entidade);

		return result;
	}

	/**
	 * @param entidade
	 * @return
	 * @see #getViaAdmMdtoDao()
	 */
	protected List<AfaViaAdministracaoMedicamento> obterListaViaAdministracaoAtiva(final AfaMedicamento entidade) {

		List<AfaViaAdministracaoMedicamento> result = null;
		AfaViaAdministracaoMedicamentoDAO dao = null;

		dao = this.getViaAdmMdtoDao();
		result = dao.obterViaAdministracaoAtivas(entidade);

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_VIA_ADM</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link #obterListaViaAdministracaoAtiva(AfaMedicamento)}
	 * @see FarmaciaExceptionCode#AFA_00251
	 */
	protected boolean verificaViaAdministracao(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;
		List<AfaViaAdministracaoMedicamento> ativos = null;

		ativos = this.obterListaViaAdministracaoAtiva(entidade);
		if ((ativos == null) || (ativos.isEmpty())) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00251);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_LCAL_DIS</code>
	 * 
	 * @param entidade
	 * @return
	 * @see #getLocalDispMtdoDao()
	 * @see #getUnidadeFuncionalDao()
	 */
	protected List<AghUnidadesFuncionais> obterListaLocalDispensacaoFaltante(final AfaMedicamento entidade) {

		List<AghUnidadesFuncionais> result = null;
		List<AghUnidadesFuncionais> unfCaract = null;
		List<AfaLocalDispensacaoMdtos> listaLocalDispMdto = null;
		AfaLocalDispensacaoMdtosDAO daoLocal = null;
		List<AghUnidadesFuncionais> listaUnF = null;
		boolean found = false;

		daoLocal = this.getLocalDispMtdoDao();
		listaLocalDispMdto = daoLocal.obterListaLocalDispensacaoMdtos(entidade);
		listaUnF = new LinkedList<AghUnidadesFuncionais>();
		unfCaract = getAghuFacade().obterListaUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO);
		listaUnF.addAll(unfCaract);
		unfCaract = getAghuFacade().obterListaUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
		listaUnF.addAll(unfCaract);
		unfCaract = getAghuFacade().obterListaUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA);
		listaUnF.addAll(unfCaract);
		unfCaract = getAghuFacade().obterListaUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais.CO);
		listaUnF.addAll(unfCaract);
		result = new LinkedList<AghUnidadesFuncionais>();
		for (AghUnidadesFuncionais unf : listaUnF) {
			found = false;
			for (AfaLocalDispensacaoMdtos localDisp : listaLocalDispMdto) {
				if (unf.equals(localDisp.getUnidadeFuncional())) {
					found = true;
					break;
				}
			}
			if (!found) {
				result.add(unf);
			}
		}

		return result;

	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_LCAL_DIS</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link #obterListaLocalDispensacaoFaltante(AfaMedicamento)}
	 * @see FarmaciaExceptionCode#AFA_00229
	 * @see #desligarValidacaoLocalDispensacao()
	 */
	protected boolean verificaLocalDispensacao(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;
		List<AghUnidadesFuncionais> localDisp = null;

		if (!this.desligarValidacaoLocalDispensacao()) {
			localDisp = this.obterListaLocalDispensacaoFaltante(entidade);
			if ((localDisp != null) && !localDisp.isEmpty()) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00229);
			}
		}
		result = true;

		return result;
	}

	/**
	 * @param entidade
	 * @return
	 * @see #getFormaDosagemDao()
	 */
	protected List<AfaFormaDosagem> obterListaFormaDosagemAtivas(final AfaMedicamento entidade) {

		List<AfaFormaDosagem> result = null;
		List<AfaFormaDosagem> listaFD = null;
		AfaFormaDosagemDAO dao = null;

		dao = this.getFormaDosagemDao();
		listaFD = dao.listaFormaDosagemMedicamento(entidade.getMatCodigo());
		result = new LinkedList<AfaFormaDosagem>();
		for (AfaFormaDosagem fd : listaFD) {
			if (fd.getIndSituacao().isAtivo()) {
				result.add(fd);
			}
		}

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_DOSAGEM</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see #obterListaFormaDosagemAtivas(AfaMedicamento)
	 * @see FarmaciaExceptionCode#AFA_00227
	 */
	protected boolean verificaFormaDosagem(final AfaMedicamento entidade) throws ApplicationBusinessException {

		boolean result = false;
		List<AfaFormaDosagem> listaFormDosAtivas = null;

		listaFormDosAtivas = this.obterListaFormaDosagemAtivas(entidade);
		if ((listaFormDosAtivas == null) || (listaFormDosAtivas.isEmpty())) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00227);
		}
		result = true;

		return result;
	}

	/**
	 * @param entidade
	 * @return
	 */
	protected AfaFormaDosagem getFormaDosagemPadrao(final AfaMedicamento entidade) {

		AfaFormaDosagem result = null;

		result = new AfaFormaDosagem();
		result.setIndSituacao(DominioSituacao.A);
		result.setIndUsualNpt(Boolean.FALSE);
		result.setIndUsualPrescricao(Boolean.FALSE);
		result.setFatorConversaoUp(BigDecimal.valueOf(1L));
		result.setAfaMedicamentos(entidade);
		result.setCriadoEm(new Date());

		return result;
	}

	/**
	 * @return
	 */
	protected FormaDosagemON getFormaDosagemON() {

		FormaDosagemON result = null;

		result = formaDosagemON;

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_ATU_DOSAGEM</code>
	 * 
	 * @param entidade
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link #getFormaDosagemPadrao(AfaMedicamento)}
	 * @see #getFormaDosagemON()
	 * @see FarmaciaExceptionCode#AFA_00266
	 */
	protected boolean atualizaFormaDosagem(final AfaMedicamento entidade, String nomeMicrocomputador) throws ApplicationBusinessException {

		boolean result = false;
		AfaFormaDosagem fd = null;
		FormaDosagemON fdON = null;

		fd = this.getFormaDosagemPadrao(entidade);
		fdON = this.getFormaDosagemON();
		try {
			fdON.inserir(fd, nomeMicrocomputador, new Date());
		} catch (IllegalStateException e1) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00266, e1);
		} catch (BaseException e2) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00266, e2);
		} catch (Exception e3) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00266, e3);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>AFAP_ENFORCE_MED_RULES('INSERT')</code>
	 * 
	 * @see #verificaViaAdministracao(AfaMedicamento)
	 * @see #verificaLocalDispensacao(AfaMedicamento)
	 * @see #verificaFormaDosagem(AfaMedicamento)
	 * @see #atualizaFormaDosagem(AfaMedicamento)
	 */
	protected boolean aplicarAsiPosInsercaoStatement(AfaMedicamento entidade)
			throws BaseException {

		boolean result = false;

		result = true;
		if (DominioSituacaoMedicamento.A.equals(entidade.getIndSituacao())) {
			result = result && this.verificaViaAdministracao(entidade);
			result = result && this.verificaLocalDispensacao(entidade);
			result = result && this.verificaFormaDosagem(entidade);
		}

		return result;
	}

	/**
	 * Remove os espacos como prefixo e sufixo.
	 * 
	 * @param entidade
	 * @see AbstractAGHUCrudRn#adequarTextoObrigatorio(String)
	 */
	protected void adequarCamposTexto(final AfaMedicamento entidade) {

		// descricoes
		entidade.setDescricao(AbstractAGHUCrudRn.adequarTextoObrigatorio(entidade.getDescricao(), true));
		entidade.setDescricaoEtiqueta(AbstractAGHUCrudRn.adequarTextoObrigatorio(entidade.getDescricaoEtiqueta(), true));
		entidade.setDescricaoEtiquetaFrasco(AbstractAGHUCrudRn.adequarTextoObrigatorio(
				entidade.getDescricaoEtiquetaFrasco(),
				false));
		entidade.setDescricaoEtiquetaSeringa(AbstractAGHUCrudRn.adequarTextoObrigatorio(
				entidade.getDescricaoEtiquetaSeringa(),
				false));
		// links
		entidade.setLinkParecerIndeferido(AbstractAGHUCrudRn.adequarTextoObrigatorio(
				entidade.getLinkParecerIndeferido(),
				false));
		entidade.setLinkProtocoloUtilizacao(AbstractAGHUCrudRn.adequarTextoObrigatorio(
				entidade.getLinkProtocoloUtilizacao(),
				false));
		if(entidade.getIndControlado() == null){
			entidade.setIndControlado(Boolean.FALSE);
		}
	}

	/**
	 * @see #verificaValidadeEntidade(AfaMedicamento)
	 * @see #setServidorData(AfaMedicamento)
	 * @see #adequarCamposTexto(AfaMedicamento)
	 * @see #aplicarAsiPosInsercaoStatement(AfaMedicamento)
	 */
	@Override

	public boolean briPreInsercaoRow(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		boolean result = false;

		this.adequarCamposTexto(entidade);
		result = this.verificaValidadeEntidade(entidade);
		result = result && this.aplicarAsiPosInsercaoStatement(entidade);
		if (result) {
			this.setServidorData(entidade);
		}

		return result;
	}

	@Override

	public boolean asiPosInsercaoStatement(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		return this.atualizaFormaDosagem(entidade, nomeMicrocomputador);
	}

	/**
	 * @param entidade
	 * @return
	 * @see #getItemPrescMdtoDAO()
	 */
	protected List<MpmItemPrescricaoMdto> obterListaItemPrescricaoParaMdto(final AfaMedicamento entidade) {

		List<MpmItemPrescricaoMdto> listaItemPrescMdto = null;

		listaItemPrescMdto = getPrescricaoMedicaFacade().obterListaItemPrescricaoParaMdto(entidade);

		return listaItemPrescMdto;
	}
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	/**
	 * Parte: @ORADB: <code>RN_MEDP_VER_DOSE_FRC</code>
	 * 
	 * @param entidade
	 * @return
	 * @see #getDataCriacao()
	 * @see #obterListaItemPrescricaoParaMdto(AfaMedicamento)
	 */
	protected List<AghAtendimentos> obterListAtendimentosDoseFracionaria(final AfaMedicamento entidade) {

		List<AghAtendimentos> result = null;
		MpmPrescricaoMdto presc = null;
		List<MpmItemPrescricaoMdto> listaItemPrescMdto = null;
		DominioPacAtendimento estadoAtend = null;
		AghAtendimentos atend = null;
		AfaFormaDosagem fd = null;
		Date valPresc = null;
		Date agora = null;
		BigDecimal fatorUp = null;
		double valFatorUp = 0.0d;

		result = new LinkedList<AghAtendimentos>();
		agora = this.getDataCriacao();
		listaItemPrescMdto = this.obterListaItemPrescricaoParaMdto(entidade);
		// obtem lista de itens de prescricoes e ...
		for (MpmItemPrescricaoMdto item : listaItemPrescMdto) {
			presc = item.getPrescricaoMedicamento();
			// se ainda estao vigentes ...
			valPresc = presc.getDthrFim();
			if ((valPresc == null) || agora.before(valPresc)) {
				atend = presc.getPrescricaoMedica().getAtendimento();
				// ... verifica se o estado do atendimento eh 'S'
				estadoAtend = atend.getIndPacAtendimento();
				if (DominioPacAtendimento.S.equals(estadoAtend)) {
					listaItemPrescMdto = presc.getItensPrescricaoMdtos();
					// ... se for, verifica cada forma de dosagem ativa por ...
					fd = item.getFormaDosagem();
					fatorUp = fd.getFatorConversaoUp();
					valFatorUp = fatorUp.doubleValue();
					// ... doses fracionarias
					if (Double.compare(Math.ceil(valFatorUp), valFatorUp) != 0) {
						// ... indicando a presenca desta dentro do atendimento.
						result.add(atend);
					}
				}
			}
		}

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_DOSE_FRC</code>
	 * 
	 * @param original
	 * @param modificada
	 * @return
	 * @throws BaseException
	 * @see {@link #obterListAtendimentosDoseFracionaria(AfaMedicamento)}
	 * @see {@link FarmaciaExceptionCode#AFA_01221}
	 * @see {@link FarmaciaExceptionCode#AFA_01222}
	 */
	protected boolean verificaDoseFracionaria(AfaMedicamento original,
			AfaMedicamento modificada) throws BaseException {
		
		boolean result = false;
		List<AghAtendimentos> listaAtendDoseFrac = null;
		Iterator<AghAtendimentos> iter = null;
		String exceptionArg = null;

		// verificar se já existe esse medicamento prescrito c/dose fracionada 
		listaAtendDoseFrac = this.obterListAtendimentosDoseFracionaria(original);
		if ((listaAtendDoseFrac == null) || listaAtendDoseFrac.isEmpty()) {
			result = true;
		} else {
			iter = listaAtendDoseFrac.iterator();
			exceptionArg = iter.next().getProntuario().toString();
			if (!iter.hasNext()) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01221, exceptionArg);
			}
			while (iter.hasNext()) {
				exceptionArg = exceptionArg.concat(", " + iter.next().getProntuario().toString());
			}
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_01222, exceptionArg);
		}

		return result;
	}

	protected ComponenteNptON getComponentNptON() {

		return componenteNptON;
	}

	/**
	 * @ORADB: <code>RN_MEDP_ATU_COMP_NPT</code>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @see #getComponentNptON()
	 * @see FarmaciaExceptionCode#AFA_00252
	 */
	protected void inativaComponentesNpt(final AfaMedicamento entidade, String nomeMicrocomputador) throws ApplicationBusinessException {

		AfaComponenteNpt comp = null;
		ComponenteNptON compON = null;

		comp = entidade.getAfaComponenteNpt();
		if (comp != null) {
			comp.setIndSituacao(DominioSituacao.I);
			compON = this.getComponentNptON();
			try {
				compON.atualizar(comp, nomeMicrocomputador, new Date());
			} catch (IllegalStateException e1) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00252, e1);
			} catch (BaseException e2) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00252, e2);
			}
		}
	}

	protected ViaAdministracaoMedicamentoON getViaAdmMdtoON() {

		return viaAdministracaoMedicamentoON;
	}

	/**
	 * @ORADB: <code>RN_MEDP_ATU_VIA_ADM</code>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @see #getViaAdmMdtoON()
	 * @see FarmaciaExceptionCode#AFA_00274
	 */
	protected void inativaViaAdministracao(final AfaMedicamento entidade, String nomeMicrocomputador) throws ApplicationBusinessException {

		ViaAdministracaoMedicamentoON viaAdmMdtoON = null;
		List<AfaViaAdministracaoMedicamento> conjViaAdmMdto = null;
		AfaViaAdministracaoMedicamentoDAO dao = null;

		viaAdmMdtoON = this.getViaAdmMdtoON();
		dao = this.getViaAdmMdtoDao();
		conjViaAdmMdto = dao.pesquisar(entidade);
		if (conjViaAdmMdto != null) {
			final Date dataFimVinculoServidor = new Date();
			for (AfaViaAdministracaoMedicamento viaAdmMdto : conjViaAdmMdto) {
				viaAdmMdto.setSituacao(DominioSituacao.I);
				try {
					viaAdmMdtoON.atualizar(viaAdmMdto, nomeMicrocomputador, dataFimVinculoServidor);
				} catch (IllegalStateException e1) {
					throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00274, e1);
				} catch (BaseException e2) {
					throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00274, e2);
				}
			}
		}
	}

	/**
	 * @ORADB: <code>RN_MEDP_ATU_DOS_INAT</code>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @see {@link #getFormaDosagemON()}
	 * @see FarmaciaExceptionCode#AFA_00275
	 */
	protected void inativaFormaDosagem(final AfaMedicamento entidade, String nomeMicrocomputador) throws ApplicationBusinessException {

		FormaDosagemON fdON = null;
		List<AfaFormaDosagem> conjFD = null;
		AfaFormaDosagemDAO dao = null;

		fdON = this.getFormaDosagemON();
		dao = this.getFormaDosagemDao();
		conjFD = dao.pesquisar(entidade);
		if (conjFD != null) {
			final Date dataFimVinculoServidor = new Date();
			for (AfaFormaDosagem fd : conjFD) {
				fd.setIndSituacao(DominioSituacao.I);
				try {
					fdON.atualizar(fd, nomeMicrocomputador, dataFimVinculoServidor);
				} catch (IllegalStateException e1) {
					throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00275, e1);
				} catch (BaseException e2) {
					throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00275, e2);
				}
			}
		}
	}

	/**
	 * Parte @ORADB: <code>AFAT_MED_ARU</code>
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 * @see #inativaComponentesNpt(AfaMedicamento)
	 * @see #inativaDiluente(AfaMedicamento)
	 * @see #inativaViaAdministracao(AfaMedicamento)
	 * @see #inativaFormaDosagem(AfaMedicamento)
	 */
	protected boolean processaInativacaoMedicamento(final AfaMedicamento original, final AfaMedicamento modificada, String nomeMicrocomputador) throws ApplicationBusinessException {

		boolean result = false;

		// ao inativar um medicamento deve-se inativar ...
		if (DominioSituacaoMedicamento.I.equals(modificada.getIndSituacao())
				&& !original.getIndSituacao().equals(modificada.getIndSituacao())) {
			// componente de npt
			this.inativaComponentesNpt(modificada, nomeMicrocomputador);
			// via adm mdto			
			this.inativaViaAdministracao(modificada, nomeMicrocomputador);
			// forma dosagem 
			this.inativaFormaDosagem(modificada, nomeMicrocomputador);
			result = true;
		} else {
			result = true;
		}

		return result;
	}

	/**
	 * @see #processaInativacaoMedicamento(AfaMedicamento)
	 * @see #necessitaJournal(AfaMedicamento, AfaMedicamento)
	 * @see #inserirEntradaJournal(AfaMedicamento, DominioOperacoesJournal)
	 */
	protected boolean aplicarAruPosAtualizacaoRow(AfaMedicamento original,
			AfaMedicamento modificada, String nomeMicrocomputador) throws BaseException {
		
		boolean result = false;

		result = this.processaInativacaoMedicamento(original, modificada, nomeMicrocomputador);
		if (result && verificarModificacaoMedicamento(original, modificada)) {
			result = this.inserirEntradaJournal(original, DominioOperacoesJournal.UPD);
		}

		return result;
	}
	
	private Boolean verificarModificacaoMedicamento(AfaMedicamento original, AfaMedicamento modificada) {
		modificada = afaMedicamentoDAO.merge(modificada);
		Boolean retorno = CoreUtil.modificados(modificada.getMatCodigo(), original.getMatCodigo())
				|| CoreUtil.modificados(modificada.getRapServidores(), original.getRapServidores()) 
				|| CoreUtil.modificados(modificada.getMpmUnidadeMedidaMedicas(), original.getMpmUnidadeMedidaMedicas())
				|| CoreUtil.modificados(modificada.getMpmTipoFreqAprazamentos(), original.getMpmTipoFreqAprazamentos())
				|| CoreUtil.modificados(modificada.getAfaTipoUsoMdtos(), original.getAfaTipoUsoMdtos())
				|| CoreUtil.modificados(modificada.getTipoApresentacaoMedicamento(), original.getTipoApresentacaoMedicamento())
				|| CoreUtil.modificados(modificada.getCriadoEm(), original.getCriadoEm())
				|| CoreUtil.modificados(modificada.getDescricao(), original.getDescricao())
				|| CoreUtil.modificados(modificada.getIndSituacao(), original.getIndSituacao())
				|| CoreUtil.modificados(modificada.getIndCalcDispensacaoFracionad(), original.getIndCalcDispensacaoFracionad())
				|| CoreUtil.modificados(modificada.getIndPadronizacao(), original.getIndPadronizacao())
				|| CoreUtil.modificados(modificada.getIndPermiteDoseFracionada(), original.getIndPermiteDoseFracionada())
				|| CoreUtil.modificados(modificada.getIndSobraReaproveitavel(), original.getIndSobraReaproveitavel())
				|| CoreUtil.modificados(modificada.getIndExigeObservacao(), original.getIndExigeObservacao())
				|| CoreUtil.modificados(modificada.getIndRevisaoCadastro(), original.getIndRevisaoCadastro())
				|| CoreUtil.modificados(modificada.getConcentracao(), original.getConcentracao())
				|| CoreUtil.modificados(modificada.getHrioInicioAdmSugerido(), original.getHrioInicioAdmSugerido())
				|| CoreUtil.modificados(modificada.getObservacao(), original.getObservacao())
				|| CoreUtil.modificados(modificada.getQtdeCaloriasGrama(), original.getQtdeCaloriasGrama())
				|| CoreUtil.modificados(modificada.getDoseMaximaMgKg(), original.getDoseMaximaMgKg())
				|| CoreUtil.modificados(modificada.getFrequenciaUsual(), original.getFrequenciaUsual())
				|| CoreUtil.modificados(modificada.getIndicacoes(), original.getIndicacoes())
				|| CoreUtil.modificados(modificada.getContraIndicacoes(), original.getContraIndicacoes())
				|| CoreUtil.modificados(modificada.getCuidadoConservacao(), original.getCuidadoConservacao())
				|| CoreUtil.modificados(modificada.getOrientacoesAdministracao(), original.getOrientacoesAdministracao())
				|| CoreUtil.modificados(modificada.getIndDiluente(), original.getIndDiluente())
				|| CoreUtil.modificados(modificada.getIndGeraDispensacao(), original.getIndGeraDispensacao())
				|| CoreUtil.modificados(modificada.getLinkParecerIndeferido(), original.getLinkParecerIndeferido())
				|| CoreUtil.modificados(modificada.getLinkProtocoloUtilizacao(), original.getLinkProtocoloUtilizacao())				
				|| CoreUtil.modificados(modificada.getDescricaoEtiquetaFrasco(), original.getDescricaoEtiquetaFrasco())
				|| CoreUtil.modificados(modificada.getDescricaoEtiquetaSeringa(), original.getDescricaoEtiquetaSeringa())
				|| CoreUtil.modificados(modificada.getIndInteresseCcih(), original.getIndInteresseCcih())
				|| CoreUtil.modificados(modificada.getIndGeladeira(), original.getIndGeladeira())
				|| CoreUtil.modificados(modificada.getIndUnitariza(), original.getIndUnitariza())
				|| CoreUtil.modificados(modificada.getIndFotosensibilidade(), original.getIndFotosensibilidade())
				|| CoreUtil.modificados(modificada.getIndUnidadeTempo(), original.getIndUnidadeTempo())
				|| CoreUtil.modificados(modificada.getTempoFotosensibilidade(), original.getTempoFotosensibilidade());
		
		afaMedicamentoDAO.desatachar(modificada);
		
		return retorno;
	}

	/**
	 * @ORADB: <code>AFAP_ENFORCE_MED_RULES('UPDATE')</code>
	 * 
	 * @see #verificaLocalDispensacao(AfaMedicamento)
	 * @see #verificaFormaDosagem(AfaMedicamento)
	 */
	protected boolean aplicarAsuPosAtualizacaoStatement(AfaMedicamento original,
			AfaMedicamento modificada) throws BaseException {
		
		boolean result = false;

		result = true;
		if (!DominioSituacaoMedicamento.A.equals(original.getIndSituacao())
				&& DominioSituacaoMedicamento.A.equals(modificada.getIndSituacao())) {
			// verificar se há um local de dispensação para cada unidade de internacao
			result = result && this.verificaLocalDispensacao(modificada);
			// verificar se há ao menos uma forma  de dosagem ativa 
			result = result && this.verificaFormaDosagem(modificada);
		}

		return result;
	}

	/**
	 * @see #verificaValidadeEntidade(AfaMedicamento)
	 * @see #verificaDoseFracionaria(AfaMedicamento, AfaMedicamento)
	 * @see #aplicarAruPosAtualizacaoRow(AfaMedicamento, AfaMedicamento)
	 * @see #aplicarAsuPosAtualizacaoStatement(AfaMedicamento, AfaMedicamento)
	 * @see #setServidorData(AfaMedicamento)
	 * @see #adequarCamposTexto(AfaMedicamento)
	 */
	@Override
	public boolean bruPreAtualizacaoRow(AfaMedicamento original,
			AfaMedicamento modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		boolean result = false;

		this.adequarCamposTexto(modificada);
		result = this.verificaValidadeEntidade(modificada);
		// Se ind_permite_dose_fracionada = 'N' consiste se o medicamento está prescrito com a dose fracionada
		if (result
				&& (!Boolean.TRUE.equals(modificada.getIndPermiteDoseFracionada()) && !modificada.getIndPermiteDoseFracionada()
						.equals(original.getIndPermiteDoseFracionada()))) {
			// afak_med_rn.rn_medp_ver_dose_frc (:new.mat_codigo);
			result = result && this.verificaDoseFracionaria(original, modificada);
		}
		result = result && this.aplicarAruPosAtualizacaoRow(original, modificada, nomeMicrocomputador);
		result = result && this.aplicarAsuPosAtualizacaoStatement(original, modificada);
		if (result) {
			this.setServidorData(modificada);
		}

		return result;
	}

	/**
	 * @see #inserirEntradaJournal(AfaMedicamento, DominioOperacoesJournal)
	 * @see #setServidorData(AfaMedicamento)
	 */
	protected boolean aplicarArdPosRemocaoRow(AfaMedicamento entidade)
			throws BaseException {
		
		boolean result = false;

		result = this.inserirEntradaJournal(entidade, DominioOperacoesJournal.DEL);
		this.setServidorData(entidade);

		return result;
	}

	/**
	 * @ORADB: <code>RN_MEDP_VER_DELECAO</code>
	 * 
	 * @see #aplicarArdPosRemocaoRow(AfaMedicamento)
	 * @see FarmaciaExceptionCode#AFA_00226
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean brdPreRemocaoRow(AfaMedicamento entidade,
			String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;

		if (entidade != null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00226);
		}
		result = this.aplicarArdPosRemocaoRow(entidade);

		return result;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
