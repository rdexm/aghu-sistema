package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidadoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCuidadosModeloBasicoON extends BaseBusiness {
	
	@EJB
	private ManterDietasModeloBasicoON manterDietasModeloBasicoON;
	
	private static final Log LOG = LogFactory.getLog(ManterCuidadosModeloBasicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmModeloBasicoCuidadoDAO mpmModeloBasicoCuidadoDAO;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8906761632719346919L;

	public enum ManterCuidadosModeloBasicoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_CUIDADO_NAO_INFORMADO, MENSAGEM_TIPO_FREQUENCIA_NAO_EXISTE, MENSAGEM_CUIDADO_USUAL_NAO_EXISTE, MENSAGEM_CUIDADO_USUAL_INATIVO, MENSAGEM_CUIDADO_USUAL_EXIGE_COMPLEMENTO, MENSAGEM_FREQUENCIA_INFORMADA_SEM_TIPO, MENSAGEM_TIPO_FREQUENCIA_INATIVO, MENSAGEM_FREQUENCIA_OBRIGATORIA, MENSAGEM_NAO_INFORMAR_FREQUENCIA;
	}

	/**
	 * Obtem lista de tipos de item de dieta ativos.
	 * 
	 * @return
	 */
	public List<MpmCuidadoUsual> obterListaCuidadoUsual(
			Object cuidadoUsualPesquisa) {
		return this.getMpmCuidadoUsualDAO().obterListaCuidadoUsual(
				cuidadoUsualPesquisa);
	}

	public Long obterListaCuidadoUsualCount(Object cuidadoUsualPesquisa) {
		return this.getMpmCuidadoUsualDAO().obterListaCuidadoUsualCount(
				cuidadoUsualPesquisa);
	}
	
	public void incluir(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (modeloBasicoCuidado == null) {
			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_NAO_INFORMADO);
		}

		try {

			this.valida(modeloBasicoCuidado);

			MpmModeloBasicoCuidadoId id = new MpmModeloBasicoCuidadoId();

			id.setModeloBasicoPrescricaoSeq(modeloBasicoCuidado
					.getModeloBasicoPrescricao().getSeq());

			modeloBasicoCuidado.setId(id);
			modeloBasicoCuidado.setServidor(servidorLogado);
			this.getMpmModeloBasicoCuidadoDAO().persistir(modeloBasicoCuidado);
			this.getMpmModeloBasicoCuidadoDAO().flush();

		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(e.getCode());
		}
	}

	private void valida(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException {

		// validação das regras
		// RN 06
		modeloBasicoCuidado.setModeloBasicoPrescricao(this
				.getManterDietasModeloBasicoON().validaAssociacao(
						modeloBasicoCuidado.getModeloBasicoPrescricao()));
		this.getManterDietasModeloBasicoON().validaServidor(
				modeloBasicoCuidado.getServidor(),
				modeloBasicoCuidado.getModeloBasicoPrescricao());

		// RN 07
		this.validaCuidadoUsual(modeloBasicoCuidado);
		// RN 08
		this.validaFrequencia(modeloBasicoCuidado);
		// RN 04
		this.validaFKCuidadoUsual(modeloBasicoCuidado);
		this.getManterDietasModeloBasicoON().validaAssociacao(
				modeloBasicoCuidado.getTipoFrequenciaAprazamento());
	}

	private void validaFrequencia(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException {

		if (modeloBasicoCuidado.getTipoFrequenciaAprazamento() == null
				&& modeloBasicoCuidado.getFrequencia() != null) {
			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_FREQUENCIA_INFORMADA_SEM_TIPO);
		}

		if (modeloBasicoCuidado.getTipoFrequenciaAprazamento() != null) {

			MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento = this
					.getMpmTipoFrequenciaAprazamentoDAO()
					.obterPorChavePrimaria(
							modeloBasicoCuidado.getTipoFrequenciaAprazamento()
									.getSeq());

			if (mpmTipoFrequenciaAprazamento == null) {
				throw new ApplicationBusinessException(
						ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_EXISTE);
			}

			if (!mpmTipoFrequenciaAprazamento.getIndSituacao().isAtivo()) {
				throw new ApplicationBusinessException(
						ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INATIVO);
			}

			if (mpmTipoFrequenciaAprazamento.getIndDigitaFrequencia()
					&& modeloBasicoCuidado.getFrequencia() == null) {
				throw new ApplicationBusinessException(
						ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_FREQUENCIA_OBRIGATORIA);
			} else if (!mpmTipoFrequenciaAprazamento.getIndDigitaFrequencia()
					&& modeloBasicoCuidado.getFrequencia() != null) {
				throw new ApplicationBusinessException(
						ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_NAO_INFORMAR_FREQUENCIA);
			}
		}
	}

	/**
	 * Verifica se cuidado usual está ativo e se exige complemento
	 * 
	 * @param modeloBasicoCuidado
	 * @throws ApplicationBusinessException
	 */
	private void validaCuidadoUsual(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException {

		if (modeloBasicoCuidado.getCuidadoUsual() == null
				|| modeloBasicoCuidado.getCuidadoUsual().getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_USUAL_NAO_EXISTE);
		}

		MpmCuidadoUsual mpmCuidadoUsual = this
				.obterCuidadoUsual(modeloBasicoCuidado.getCuidadoUsual()
						.getSeq());

		if (mpmCuidadoUsual == null) {
			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_USUAL_NAO_EXISTE);
		}

		if (mpmCuidadoUsual.getIndSituacao().isAtivo()) {

			if (mpmCuidadoUsual.getIndDigitaComplemento()
					&& StringUtils.isBlank(modeloBasicoCuidado.getDescricao())) {
				throw new ApplicationBusinessException(
						ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_USUAL_EXIGE_COMPLEMENTO);
			}

		} else {

			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_USUAL_INATIVO);
		}

	}

	/**
	 * Verifica se o cuidado usual existe
	 * 
	 * @param modeloBasicoCuidado
	 * @throws ApplicationBusinessException
	 */
	private void validaFKCuidadoUsual(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException {

		if (modeloBasicoCuidado.getCuidadoUsual() == null
				|| modeloBasicoCuidado.getCuidadoUsual().getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_USUAL_NAO_EXISTE);
		}

		if (this.getMpmCuidadoUsualDAO().obterPorChavePrimaria(
				modeloBasicoCuidado.getCuidadoUsual().getSeq()) == null) {
			throw new ApplicationBusinessException(
					ManterCuidadosModeloBasicoONExceptionCode.MENSAGEM_CUIDADO_USUAL_NAO_EXISTE);
		}
	}

	public void alterar(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException {

		if (modeloBasicoCuidado == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		this.valida(modeloBasicoCuidado);
		this.getMpmModeloBasicoCuidadoDAO().merge(modeloBasicoCuidado);
		this.getMpmModeloBasicoCuidadoDAO().flush();
	}

	public void excluir(Object object) throws ApplicationBusinessException {
		MpmModeloBasicoCuidado modeloCuidadoAux = (MpmModeloBasicoCuidado) object;
		MpmModeloBasicoCuidadoId id = new MpmModeloBasicoCuidadoId();
		id.setModeloBasicoPrescricaoSeq(modeloCuidadoAux.getId().getModeloBasicoPrescricaoSeq());
		id.setSeq(modeloCuidadoAux.getId().getSeq());
		
		MpmModeloBasicoCuidado modeloBasicoCuidado = this.getMpmModeloBasicoCuidadoDAO().obterPorChavePrimaria(id);
		this.getManterDietasModeloBasicoON().validaServidor(
				modeloBasicoCuidado.getServidor(),
				modeloBasicoCuidado.getModeloBasicoPrescricao());

		this.getMpmModeloBasicoCuidadoDAO().remover(modeloBasicoCuidado);
		this.getMpmModeloBasicoCuidadoDAO().flush();
	}

	public MpmModeloBasicoCuidado obterModeloBasicoCuidado(
			Integer modeloBasicoPrescricaoSeq, Integer seq) {

		MpmModeloBasicoCuidadoId mpmModeloBasicoCuidadoId = new MpmModeloBasicoCuidadoId(
				modeloBasicoPrescricaoSeq, seq);
		return this.getMpmModeloBasicoCuidadoDAO().obterPorChavePrimaria(
				mpmModeloBasicoCuidadoId);
	}

	public MpmCuidadoUsual obterCuidadoUsual(Integer seq) {	
		return this.getMpmCuidadoUsualDAO().obterCuidadoUsual(seq);
	}
	
	public String obterDescricaoEditadaModeloBasicoCuidado(MpmModeloBasicoCuidado modeloBasicoCuidado) {
		return obterDescricaoEditadaModeloBasicoCuidado(modeloBasicoCuidado.getId().getModeloBasicoPrescricaoSeq(), modeloBasicoCuidado.getId().getSeq());
	}

	public String obterDescricaoEditadaModeloBasicoCuidado(final Integer modeloBasicoPrescricaoSeq, final Integer seq) {

		MpmModeloBasicoCuidado modeloBasicoCuidado = this.getMpmModeloBasicoCuidadoDAO().obterPorChavePrimaria(new MpmModeloBasicoCuidadoId(modeloBasicoPrescricaoSeq, seq), true,
				MpmModeloBasicoCuidado.Fields.TIPO_FREQUENCIA_APRAZAMENTO);

		StringBuffer sintaxeDieta = new StringBuffer();
		String descTfq = null;
		if (modeloBasicoCuidado.getTipoFrequenciaAprazamento() != null) {
			if (modeloBasicoCuidado.getTipoFrequenciaAprazamento().getSintaxe() != null) {

				if (modeloBasicoCuidado.getFrequencia() != null) {
					descTfq = modeloBasicoCuidado.getTipoFrequenciaAprazamento().getSintaxe().replace("#", modeloBasicoCuidado.getFrequencia().toString());
				} else {
					descTfq = modeloBasicoCuidado.getTipoFrequenciaAprazamento().getSintaxe();
				}

			} else if (modeloBasicoCuidado.getTipoFrequenciaAprazamento().getDescricao() != null) {
				descTfq = modeloBasicoCuidado.getTipoFrequenciaAprazamento().getDescricao();
			}
		}
		if (modeloBasicoCuidado.getCuidadoUsual() != null && modeloBasicoCuidado.getCuidadoUsual().getDescricao() != null) {

			if (StringUtils.isNotBlank(modeloBasicoCuidado.getDescricao())) {
				sintaxeDieta.append(modeloBasicoCuidado.getCuidadoUsual().getDescricao());
				sintaxeDieta.append(" - ");
				sintaxeDieta.append(modeloBasicoCuidado.getDescricao());
			} else {
				sintaxeDieta.append(modeloBasicoCuidado.getCuidadoUsual().getDescricao());
			}
		}

		if (descTfq != null) {
			sintaxeDieta.append(", ");
			sintaxeDieta.append(descTfq);
		}
		sintaxeDieta.append(" ; ");

		return sintaxeDieta.toString();
	}

	/**
	 * Retorna lista de cuidados do modelo informado
	 * 
	 * @return Lista de cuidados de um modelo básico de prescrição
	 */
	public List<MpmModeloBasicoCuidado> obterListaCuidados(
			Integer modeloBasicoPrescricaoSeq) {
		return this.getMpmModeloBasicoCuidadoDAO().listar(
				modeloBasicoPrescricaoSeq);
	}

	public MpmModeloBasicoCuidado obterItemCuidado(MpmModeloBasicoCuidadoId id) {
		return this.getMpmModeloBasicoCuidadoDAO().obterPorChavePrimaria(id, true, MpmModeloBasicoCuidado.Fields.TIPO_FREQUENCIA_APRAZAMENTO, MpmModeloBasicoCuidado.Fields.CDU);
	}

	// getters & setters

	protected MpmModeloBasicoCuidadoDAO getMpmModeloBasicoCuidadoDAO() {
		return mpmModeloBasicoCuidadoDAO;
	}

	private MpmCuidadoUsualDAO getMpmCuidadoUsualDAO() {
		return mpmCuidadoUsualDAO;
	}

	private MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected ManterDietasModeloBasicoON getManterDietasModeloBasicoON() {
		return manterDietasModeloBasicoON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
