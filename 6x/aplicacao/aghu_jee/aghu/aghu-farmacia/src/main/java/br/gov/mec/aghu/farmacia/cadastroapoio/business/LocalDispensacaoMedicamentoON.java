package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.LocalDispensacaoMedicamentoRN.TipoUnfLdmEnum;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LocalDispensacaoMedicamentoON extends BaseBusiness {

	@EJB
	private LocalDispensacaoMedicamentoRN localDispensacaoMedicamentoRN;
	
	private static final Log LOG = LogFactory.getLog(LocalDispensacaoMedicamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5565289542045396285L;

	public enum LocalDispensacaoMedicamentoONExceptionCode implements
			BusinessExceptionCode {
		EXCEPTION_NAO_POSSUI_LOCAL_DISP_DISPONIVEL, AFA_00277, AFA_00278, MENSAGEM_MEDICAMENTO_UNIDADE_FUNCIONAL_EXISTE,
		SELECIONAR_UNIDADE_FUNCIONAMENTO, ERRO_NAO_EXISTE_UNIDADE_CARACTERISTICA_FARMACIA
	}

	/**
	 * Persiste lista contendo locais de dispensacao
	 * 
	 * @param LocalDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void persistirLocaisDispensacaoDisponiveis(AfaMedicamento medicamento)
			throws ApplicationBusinessException {

		// Monta lista
		List<AfaLocalDispensacaoMdtos> locaisDispensacaoPersistir = montaLocaisDispensacaoPersistir(medicamento);

		if (locaisDispensacaoPersistir != null
				&& !locaisDispensacaoPersistir.isEmpty()) {
			AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO = getAfaLocalDispensacaoMdtosDAO();
			try {
				afaLocalDispensacaoMdtosDAO
						.persisteTodosLocaisDispensacaoDisponiveis(
								locaisDispensacaoPersistir);
				// Sincroniza com a base de dados
				//afaLocalDispensacaoMdtosDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(
						LocalDispensacaoMedicamentoONExceptionCode.AFA_00278, e);
			}
		} else {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoONExceptionCode.EXCEPTION_NAO_POSSUI_LOCAL_DISP_DISPONIVEL);
		}
	}

	/**
	 * Busca unidade Funcional adotada por padrao na regra RN_MED_013 na @ORADB
	 * PROCEDURE RN_MEDP_ATU_LCAL_DIS
	 * 
	 * @param enumParametro
	 * @return unidadesDispensacao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected AghUnidadesFuncionais buscarUnfsPadraoLocalDispensacao(
			AghuParametrosEnum enumParametro) throws ApplicationBusinessException,
			ApplicationBusinessException {

		AghUnidadesFuncionais unidadeDispensacao = new AghUnidadesFuncionais();
		AghParametros parametro = null;
		try {
			IAghuFacade aghuFacade = getAghuFacade();
			IParametroFacade parametroFacade = getParametroFacade();
			parametro = parametroFacade.buscarAghParametro(enumParametro);
			unidadeDispensacao = aghuFacade
					.obterAghUnidadesFuncionaisPorChavePrimaria(
							Short.valueOf(parametro.getVlrNumerico().toString()));
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			if (AghuParametrosEnum.P_UNF_FARM_DISP.equals(enumParametro)) {
				throw new ApplicationBusinessException(
						LocalDispensacaoMedicamentoONExceptionCode.AFA_00277);
			}
			// else {
			// throw new
			// ApplicationBusinessException(LocalDispensacaoMedicamentoONExceptionCode.AFA_01179);
			// }
		}

		return unidadeDispensacao;

	}

	/**
	 * Monta lista de locais de dispensacao disponives para persistir
	 * 
	 * @param localDispensacaoBase
	 * @return localDispensacaoMdtosList
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected List<AfaLocalDispensacaoMdtos> montaLocaisDispensacaoPersistir(
			AfaMedicamento medicamento) throws ApplicationBusinessException,
			ApplicationBusinessException {

		List<AfaLocalDispensacaoMdtos> localDispensacaoMdtosList = null;
		Integer medMatCodigo = medicamento.getMatCodigo();

		// Buscar Lista de UNFsSeq de medicamento
		Short[] unfsMdto = buscaIdsUnfSolicitantesMdto(medicamento);

		List<AghUnidadesFuncionais> unfsDisponiveis = buscaIdsUnfsDisponiveis(unfsMdto);
		if (unfsDisponiveis != null && !unfsDisponiveis.isEmpty()) {
			localDispensacaoMdtosList = new ArrayList<AfaLocalDispensacaoMdtos>();
			for (AghUnidadesFuncionais unfSolicitante : unfsDisponiveis) {
				// Cria a local de dispensacao
				AfaLocalDispensacaoMdtos localDispensacaoPersistir = montarUnicoLocalDispensacao(
						medMatCodigo, unfSolicitante);
				// Valida local de dispensacao criado
				validarLocalDispensacaoPersistir(localDispensacaoPersistir);
				localDispensacaoMdtosList.add(localDispensacaoPersistir);
			}
		}

		return localDispensacaoMdtosList;
	}

	/**
	 * Monta instancia de localDispensacao para ser inserido na lista a persistir
	 * 
	 * @param localDispensacaoBase
	 * @return localDispensacaoPersistir
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected AfaLocalDispensacaoMdtos montarUnicoLocalDispensacao(
			Integer medMatCodigo, AghUnidadesFuncionais unfSolicitante)
			throws ApplicationBusinessException {

		AfaLocalDispensacaoMdtos localDispensacaoPersistir = new AfaLocalDispensacaoMdtos();
		AfaLocalDispensacaoMdtosId id = new AfaLocalDispensacaoMdtosId();

		// Montando id
		id.setUnfSeq(unfSolicitante.getSeq());
		id.setMedMatCodigo(medMatCodigo);
		localDispensacaoPersistir.setId(id);

		// Montando unidades dispensadoras com padrao P_UNF_FARM_DISP
		AghUnidadesFuncionais unidadeFuncionalPadrao = buscarUnfsPadraoLocalDispensacao(AghuParametrosEnum.P_UNF_FARM_DISP);

		// Nao é inserido automaticamente
		// AghUnidadesFuncionais unidadeFuncionalDispUsoDomPadrao =
		// buscarUnfsPadraoLocalDispensacao(AghuParametrosEnum.P_UNF_FARM_FAPE);

		localDispensacaoPersistir.setUnidadeFuncional(unfSolicitante);
		localDispensacaoPersistir
				.setUnidadeFuncionalDispDoseInt(unidadeFuncionalPadrao);
		localDispensacaoPersistir
				.setUnidadeFuncionalDispDoseFrac(unidadeFuncionalPadrao);
		localDispensacaoPersistir
				.setUnidadeFuncionalDispAlternativa(unidadeFuncionalPadrao);
		// localDispensacaoPersistir
		// .setUnidadeFuncionalDispUsoDomiciliar(unidadeFuncionalDispUsoDomPadrao);

		return localDispensacaoPersistir;
	}

	/**
	 * Valida instancia de local de dispensacao montada para persistencia da
	 * lista de acordo com a procedure AFAK_LDM_RN.RN_LDMP_VER_UNF
	 * 
	 * @param localDispensacaoPersistir
	 * @throws ApplicationBusinessException
	 */
	protected void validarLocalDispensacaoPersistir(
			AfaLocalDispensacaoMdtos localDispensacaoPersistir)
			throws ApplicationBusinessException {

		LocalDispensacaoMedicamentoRN localDispensacaoMedicamentoRN = getLocalDispensacaoMedicamentoRN();
		
		try {
			localDispensacaoMedicamentoRN.verificaUnidadeFuncional(
					localDispensacaoPersistir.getUnidadeFuncional(),
					TipoUnfLdmEnum.TIPO_UNF_I);
			localDispensacaoMedicamentoRN.verificaUnidadeFuncional(
					localDispensacaoPersistir.getUnidadeFuncionalDispDoseInt(),
					TipoUnfLdmEnum.TIPO_UNF_F);
			localDispensacaoMedicamentoRN.verificaUnidadeFuncional(
					localDispensacaoPersistir
							.getUnidadeFuncionalDispAlternativa(),
					TipoUnfLdmEnum.TIPO_UNF_F);
			localDispensacaoMedicamentoRN
					.verificaUnidadeFuncional(
							localDispensacaoPersistir
									.getUnidadeFuncionalDispDoseFrac(),
							TipoUnfLdmEnum.TIPO_UNF_F);

			// Seta valores automáticos
			localDispensacaoMedicamentoRN.verificarAtribuirCriadoEm(
					localDispensacaoPersistir);
			localDispensacaoMedicamentoRN.verificarAtribuirServidor(
					localDispensacaoPersistir);
			localDispensacaoMedicamentoRN.verificarAtribuirVersion(
					localDispensacaoPersistir);
		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoONExceptionCode.AFA_00278);
		} 

	}

	// Metodos para listas em tela

	/**
	 * Obtem unidades Funcionais que ainda nao solicitaram dispensacao
	 * @return result
	 */
	public List<AghUnidadesFuncionais> listaUnidadesFuncionaisDisponiveis(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		List<AghUnidadesFuncionais> result = null;
		Short[] idSUnfsSolicitantesMdto = buscaIdsUnfSolicitantesMdto(medicamento);
		result = getAghuFacade()
				.buscarUnidadesFuncionaisDisponivelLocalDispensacao(
						idSUnfsSolicitantesMdto, firstResult, maxResult,
						orderProperty, asc);

		return result;
	}

	/**
	 * Obtem count de unidades Funcionais que ainda nao solicitaram dispensacao
	 * @param medicamento
	 * @return count
	 */
	public Long buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(
			AfaMedicamento medicamento) {

		Short[] idSUnfsSolicitantesMdto = buscaIdsUnfSolicitantesMdto(medicamento);

		return getAghuFacade().buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(
						idSUnfsSolicitantesMdto);
	}

	/**
	 * Obtem as unidades solicitantes que ja dispensam o medicamento
	 * @param medicamento
	 * @return idSUnfsSolicitantesMdto
	 */
	public Short[] buscaIdsUnfSolicitantesMdto(AfaMedicamento medicamento) {

		Short[] idSUnfsSolicitantesMdto = getAfaLocalDispensacaoMdtosDAO()
				.listaIdsLocaisDispensacaoMdto(medicamento);

		return idSUnfsSolicitantesMdto;
	}

	/**
	 * Retorna os ids de unidades solicitantes disponiveis para o medicamento
	 * @param unfsSolicitantesMdto
	 * @return idsUnfsDisponiveis
	 */
	public List<AghUnidadesFuncionais> buscaIdsUnfsDisponiveis(
			Short[] unfsSolicitantesMdto) {
		return getAghuFacade().buscarSeqsUnidadesFuncionaisDisponivelLocalDispensacao(
						unfsSolicitantesMdto);
	}

	/**
	 * Retorna os um lista de medicamento por unidades
	 * @param unidadeFuncional
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return List<AfaLocalDispensacaoMdtos>
	 */
	public List<AfaLocalDispensacaoMdtos> pesqueisarLocalDispensacaoPorUnidadesON(
			AghUnidadesFuncionais unidadeFuncional, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc){
		return getAfaLocalDispensacaoMdtosDAO().pesquisarPorUnidade(firstResult, maxResult, orderProperty, asc, unidadeFuncional);
	}
	
	/**
	 * Retorna toda de todos medicamento por unidade funcional.
	 * @param unidadeFuncional
	 * @return Integer
	 */
	public Long pesqueisarLocalDispensacaoPorUnidadesCountON(AghUnidadesFuncionais unidadeFuncional){
		return getAfaLocalDispensacaoMdtosDAO().pesquisarPorUnidadeCount(unidadeFuncional);
	}
	
	public void vincular(AghUnidadesFuncionais unidadeFuncionalSolicitante, RapServidores servidor) throws ApplicationBusinessException {
		if (unidadeFuncionalSolicitante == null) {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoONExceptionCode.SELECIONAR_UNIDADE_FUNCIONAMENTO);
		}
		
		List<ConstanteAghCaractUnidFuncionais> listTmp = new ArrayList<ConstanteAghCaractUnidFuncionais>();
		listTmp.add(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
		List<AghCaractUnidFuncionais> listaUnfSeq = this.getAghuFacade().pesquisarCaracteristicasUnidadeFuncionalPorCaracteristica(listTmp);
		if(listaUnfSeq.isEmpty()) {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoONExceptionCode.ERRO_NAO_EXISTE_UNIDADE_CARACTERISTICA_FARMACIA);
		}
		
		List<Integer> listaMedMatCodigo = getAfaLocalDispensacaoMdtosDAO().obterLocalDispensMdtosPorUnfSeq(unidadeFuncionalSolicitante.getSeq());
		
		List<AfaMedicamento> medicamentos = this.farmaciaFacade.obterMedicamentosParaInclusaoLocalDispensacao(listaMedMatCodigo);
		
		if (!medicamentos.isEmpty()) {
			for (AfaMedicamento afaMedicamento : medicamentos) {
				AfaLocalDispensacaoMdtos afaLocalDispensacaoMdtosTmp = new AfaLocalDispensacaoMdtos();
				AfaLocalDispensacaoMdtosId afaLocalDispensacaoMdtosIdTmp = new AfaLocalDispensacaoMdtosId();
				afaLocalDispensacaoMdtosIdTmp
						.setMedMatCodigo(afaMedicamento.getMatCodigo());
				afaLocalDispensacaoMdtosIdTmp
						.setUnfSeq(unidadeFuncionalSolicitante.getSeq());
				afaLocalDispensacaoMdtosTmp
						.setId(afaLocalDispensacaoMdtosIdTmp);
				afaLocalDispensacaoMdtosTmp.setMedicamento(afaMedicamento);
				afaLocalDispensacaoMdtosTmp
						.setUnidadeFuncional(unidadeFuncionalSolicitante);
				afaLocalDispensacaoMdtosTmp.setCriadoEm(new Date());
				afaLocalDispensacaoMdtosTmp.setServidor(servidor);
				afaLocalDispensacaoMdtosTmp
						.setUnidadeFuncionalDispAlternativa(listaUnfSeq
								.get(0).getUnidadeFuncional());
				afaLocalDispensacaoMdtosTmp
						.setUnidadeFuncionalDispDoseFrac(listaUnfSeq.get(0)
								.getUnidadeFuncional());
				afaLocalDispensacaoMdtosTmp
						.setUnidadeFuncionalDispDoseInt(listaUnfSeq.get(0)
								.getUnidadeFuncional());
				afaLocalDispensacaoMdtosTmp
						.setUnidadeFuncionalDispUsoDomiciliar(listaUnfSeq
								.get(0).getUnidadeFuncional());
				getAfaLocalDispensacaoMdtosDAO().persistir(afaLocalDispensacaoMdtosTmp);
			}
		} else {
			throw new ApplicationBusinessException(
					LocalDispensacaoMedicamentoONExceptionCode.MENSAGEM_MEDICAMENTO_UNIDADE_FUNCIONAL_EXISTE);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AfaLocalDispensacaoMdtosDAO getAfaLocalDispensacaoMdtosDAO() {
		return afaLocalDispensacaoMdtosDAO;
	}

	protected LocalDispensacaoMedicamentoRN getLocalDispensacaoMedicamentoRN() {
		return localDispensacaoMedicamentoRN;
	}

}
