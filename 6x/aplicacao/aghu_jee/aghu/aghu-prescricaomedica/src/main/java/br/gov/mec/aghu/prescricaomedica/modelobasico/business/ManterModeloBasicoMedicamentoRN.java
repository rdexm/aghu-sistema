package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterModeloBasicoMedicamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterModeloBasicoMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private MpmModeloBasicoPrescricaoDAO mpmModeloBasicoPrescricaoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9078015528435147683L;

	public enum ManterModeloBasicoMedicamentoRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERT_MODELO_BASICO_MEDICAMENTO, ERRO_UPDATE_MODELO_BASICO_MEDICAMENTO, ERRO_DELETE_MODELO_BASICO_MEDICAMENTO, MPM_00790, MPM_00905, MPM_00925, MPM_00928, MPM_00929, MPM_00930, MPM_00931, MPM_00932, MPM_00933, RAP_00175;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Inserir objeto MpmModeloBasicoMedicamento.
	 * 
	 * @param {MpmModeloBasicoMedicamento} modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	public void inserirModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		try {

			this.preInserirModeloBasicoMedicamento(modeloBasicoMedicamento);
			// Utilizei o usuario logado. verificar.
			
			modeloBasicoMedicamento.setServidor(servidorLogado);
			
			// Seta valor padrão para o atributo bomba de infusão
			if(modeloBasicoMedicamento.getIndBombaInfusao() == null){
				modeloBasicoMedicamento.setIndBombaInfusao(false);
			}
			
			this.getModeloBasicoMedicamentoDAO().persistir(modeloBasicoMedicamento);
			this.getModeloBasicoMedicamentoDAO().flush();

		} catch (Exception e) {

			ManterModeloBasicoMedicamentoRNExceptionCode.ERRO_INSERT_MODELO_BASICO_MEDICAMENTO.throwException(e);

		}

	}

	/**
	 * Atualiza objeto MpmModeloBasicoMedicamento.
	 * 
	 * @param {MpmModeloBasicoMedicamento} modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	public void atualizarModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {

		try {

			this.preAtualizarModeloBasicoMedicamento(modeloBasicoMedicamento);
			this.getModeloBasicoMedicamentoDAO().merge(modeloBasicoMedicamento);
			this.getModeloBasicoMedicamentoDAO().flush();

		} catch (Exception e) {

			ManterModeloBasicoMedicamentoRNExceptionCode.ERRO_UPDATE_MODELO_BASICO_MEDICAMENTO
					.throwException(e);

		}

	}

	/**
	 * Remover objeto MpmModeloBasicoMedicamento.
	 * 
	 * @param {MpmModeloBasicoMedicamento} modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	public void removerModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {

		//try {

			this.preRemoverModeloBasicoMedicamento(modeloBasicoMedicamento);
			this.getModeloBasicoMedicamentoDAO().remover(
					modeloBasicoMedicamento);
			this.getModeloBasicoMedicamentoDAO().flush();

//		} catch (Exception e) {
//
//			ManterModeloBasicoMedicamentoRNExceptionCode.ERRO_DELETE_MODELO_BASICO_MEDICAMENTO
//					.throwException(e);
//
//		}

	}

	/**
	 * ORADB Trigger MPMT_MBM_BRI
	 * 
	 * @param {MpmModeloBasicoMedicamento} modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInserirModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (servidorLogado == null) {
			ManterModeloBasicoMedicamentoRNExceptionCode.RAP_00175
					.throwException();
		}

		this.verificarTipoFrequencia(modeloBasicoMedicamento
				.getTipoFrequenciaAprazamento().getSeq());

		this.verificarViaAdministracao(modeloBasicoMedicamento
				.getViaAdministracao().getSigla());

		this.verificarFrequencia(modeloBasicoMedicamento
				.getTipoFrequenciaAprazamento().getSeq(),
				modeloBasicoMedicamento.getFrequencia());

		this.verificarGotejo(modeloBasicoMedicamento.getGotejo(),
				modeloBasicoMedicamento.getTipoVelocidadeAdministracao());

		final Integer serMatricula = servidorLogado.getId().getMatricula();
		final Short serVinCodigo = servidorLogado.getId().getVinCodigo();		
		
		this.verificarExclusividade(modeloBasicoMedicamento
				.getModeloBasicoPrescricao().getSeq(), serMatricula, serVinCodigo);
	}

	/**
	 * ORADB Trigger MPMT_MBM_BRU
	 * 
	 * @param {MpmModeloBasicoMedicamento} modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preAtualizarModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		MpmModeloBasicoMedicamento antigoModeloBasicoMedicamento = getModeloBasicoMedicamentoDAO()
				.obterPorChavePrimaria(modeloBasicoMedicamento.getId());

		if (servidorLogado == null) {
			ManterModeloBasicoMedicamentoRNExceptionCode.RAP_00175
					.throwException();
		}

		if (CoreUtil.modificados(modeloBasicoMedicamento
				.getTipoFrequenciaAprazamento().getSeq(),
				antigoModeloBasicoMedicamento.getTipoFrequenciaAprazamento()
						.getSeq())) {

			this.verificarTipoFrequencia(modeloBasicoMedicamento
					.getTipoFrequenciaAprazamento().getSeq());

		}

		if (CoreUtil.modificados(modeloBasicoMedicamento.getViaAdministracao()
				.getSigla(), antigoModeloBasicoMedicamento
				.getViaAdministracao().getSigla())) {

			this.verificarViaAdministracao(modeloBasicoMedicamento
					.getViaAdministracao().getSigla());

		}

		if (CoreUtil.modificados(modeloBasicoMedicamento
				.getTipoFrequenciaAprazamento().getSeq(),
				antigoModeloBasicoMedicamento.getTipoFrequenciaAprazamento()
						.getSeq())
				|| CoreUtil.modificados(
						modeloBasicoMedicamento.getFrequencia(),
						antigoModeloBasicoMedicamento.getFrequencia())) {

			this.verificarFrequencia(modeloBasicoMedicamento
					.getTipoFrequenciaAprazamento().getSeq(),
					modeloBasicoMedicamento.getFrequencia());

		}

		if ((modeloBasicoMedicamento.getTipoVelocidadeAdministracao() != null && CoreUtil
				.modificados(modeloBasicoMedicamento
						.getTipoVelocidadeAdministracao(),
						antigoModeloBasicoMedicamento
								.getTipoVelocidadeAdministracao()))
				|| (modeloBasicoMedicamento.getGotejo() != null && CoreUtil
						.modificados(modeloBasicoMedicamento.getGotejo(),
								antigoModeloBasicoMedicamento.getGotejo()))) {

			this.verificarGotejo(modeloBasicoMedicamento.getGotejo(),
					modeloBasicoMedicamento.getTipoVelocidadeAdministracao());

		}

		final Integer serMatricula = servidorLogado.getId().getMatricula();
		final Short serVinCodigo = servidorLogado.getId().getVinCodigo();		
		
		if (CoreUtil.modificados(modeloBasicoMedicamento
				.getModeloBasicoPrescricao().getSeq(),
				antigoModeloBasicoMedicamento.getModeloBasicoPrescricao()
						.getSeq())
				|| CoreUtil.modificados(
						serMatricula,
						antigoModeloBasicoMedicamento.getServidor().getId()
								.getMatricula())
				|| CoreUtil.modificados(serVinCodigo,
						antigoModeloBasicoMedicamento.getServidor().getId()
								.getVinCodigo())) {

			this.verificarExclusividade(modeloBasicoMedicamento
					.getModeloBasicoPrescricao().getSeq(), serMatricula, serVinCodigo);

		}
	}

	/**
	 * ORADB Trigger MPMT_MBM_BRD
	 * 
	 * @param {MpmModeloBasicoMedicamento} modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preRemoverModeloBasicoMedicamento(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final Integer serMatricula = servidorLogado.getId().getMatricula();
		final Short serVinCodigo = servidorLogado.getId().getVinCodigo();		
		
		this.verificarExclusividade(modeloBasicoMedicamento
				.getModeloBasicoPrescricao().getSeq(), serMatricula, serVinCodigo);

	}

	/**
	 * ORADB Procedure mpmk_mbm_rn.rn_mbmp_ver_tp_freq.
	 * 
	 * Verifica seTipo Freqüência Aprazamento está ativo.
	 * 
	 * @param {Short} tfqSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarTipoFrequencia(Short tfqSeq)
			throws ApplicationBusinessException {

		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = getTipoFrequenciaAprazamentoDAO()
				.obterTipoFrequenciaAprazamentoPeloId(tfqSeq);

		if (tipoFrequenciaAprazamento != null) {

			DominioSituacao situacao = tipoFrequenciaAprazamento
					.getIndSituacao();
			if (!DominioSituacao.A.equals(situacao)) {

				ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00905
						.throwException();

			}

		} else {

			ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00925
					.throwException();

		}

	}

	/**
	 * ORADB Procedure mpmk_mbm_rn.rn_mbmp_ver_via_adm.
	 * 
	 * Verifica se Via Administração está ativa.
	 * 
	 * @param {String} vadSigla
	 * @throws ApplicationBusinessException
	 */
	public void verificarViaAdministracao(String vadSigla)
			throws ApplicationBusinessException {

		AfaViaAdministracao afaViaAdministracao = getFarmaciaFacade().obterViaAdministracao(vadSigla);

		if (afaViaAdministracao != null) {

			DominioSituacao situacao = afaViaAdministracao.getIndSituacao();
			if (!DominioSituacao.A.equals(situacao)) {

				ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00928
						.throwException();

			}

		} else {

			ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00929
					.throwException();

		}

	}

	/**
	 * ORADB Procedure mpmk_mbm_rn.rn_mbmp_ver_freq.
	 * 
	 * Verifica indicadores da Tipo Freqüência Aprazamento
	 * (ind_digita_frequencia).
	 * 
	 * @param {Short} tfqSeq
	 * @param {Short} frequencia
	 * @throws ApplicationBusinessException
	 */
	public void verificarFrequencia(Short tfqSeq, Short frequencia)
			throws ApplicationBusinessException {

		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = getTipoFrequenciaAprazamentoDAO()
				.obterTipoFrequenciaAprazamentoPeloId(tfqSeq);

		if (tipoFrequenciaAprazamento != null) {

			DominioSituacao situacao = tipoFrequenciaAprazamento
					.getIndSituacao();
			if (!DominioSituacao.A.equals(situacao)) {

				ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00905
						.throwException();

			}

			Boolean digitaFrequencia = tipoFrequenciaAprazamento
					.getIndDigitaFrequencia();
			if (digitaFrequencia.booleanValue()) {

				if (frequencia == null) {

					ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00930
							.throwException();

				}

			} else {

				if (frequencia != null) {

					ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00931
							.throwException();

				}

			}

		}

	}

	/**
	 * ORADB Procedure mpmk_mbm_rn.rn_mbmp_ver_gotejo.
	 * 
	 * Verifica relacionamento em Tipo Veloc Administração se for informado
	 * gotejo.
	 * 
	 * @param {BigDecimal} gotejo
	 * @param {AfaTipoVelocAdministracoes} tipoVelocidadeAdministracao
	 * @throws ApplicationBusinessException
	 */
	public void verificarGotejo(BigDecimal gotejo,
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
			throws ApplicationBusinessException {

		if (gotejo != null && tipoVelocidadeAdministracao != null) {

			DominioSituacao situacao = getFarmaciaFacade()
					.obtemSituacaoTipoVelocidade(
							tipoVelocidadeAdministracao.getSeq());

			if (situacao != null) {

				if (!DominioSituacao.A.equals(situacao)) {

					ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00932
							.throwException();

				}

			} else {

				ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00933
						.throwException();

			}

		}

	}

	/**
	 * ORADB Procedure mpmk_mcu_rn.rn_mcup_ver_exclusao ORADB Procedure
	 * mpmk_mbm_rn.rn_mbmp_ver_exclusao
	 * 
	 * Verificar se o modelo básico prescrição é exclusivo de um servidor para
	 * verificar se pode ser feito insert, update e delete
	 * 
	 * @param {Integer} mdbSeq
	 * @param {Integer} matriculaServidorLogado
	 * @param {Short} vinculoServidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void verificarExclusividade(Integer mdbSeq,
			Integer matriculaServidorLogado, Short vinculoServidorLogado)
			throws ApplicationBusinessException {

		if (mdbSeq == null || matriculaServidorLogado == null
				|| vinculoServidorLogado == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		MpmModeloBasicoPrescricao modeloBasicoPrescricao = getModeloBasicoPrescricaoDAO()
				.obterModeloBasicoPrescricaoPeloId(mdbSeq);

		if (modeloBasicoPrescricao != null
				&& modeloBasicoPrescricao.getServidor() != null) {

			Integer matricula = modeloBasicoPrescricao.getServidor().getId()
					.getMatricula();
			Short vinculo = modeloBasicoPrescricao.getServidor().getId()
					.getVinCodigo();

			if (matricula != null
					&& vinculo != null
					&& (matricula.intValue() != matriculaServidorLogado
							.intValue() || vinculo.shortValue() != vinculoServidorLogado
							.shortValue())) {

				ManterModeloBasicoMedicamentoRNExceptionCode.MPM_00790
						.throwException();

			}

		}

	}

	protected MpmModeloBasicoMedicamentoDAO getModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}

	protected MpmModeloBasicoPrescricaoDAO getModeloBasicoPrescricaoDAO() {
		return mpmModeloBasicoPrescricaoDAO;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
