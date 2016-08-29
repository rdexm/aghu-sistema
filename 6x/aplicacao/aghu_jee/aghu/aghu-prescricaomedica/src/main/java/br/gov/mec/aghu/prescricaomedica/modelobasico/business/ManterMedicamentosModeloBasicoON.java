package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.ManterDietasModeloBasicoON.ManterDietasModeloBasicoONExceptionCode;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @author rpetter
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ManterMedicamentosModeloBasicoON extends BaseBusiness {


@EJB
private ManterDietasModeloBasicoON manterDietasModeloBasicoON;

private static final Log LOG = LogFactory.getLog(ManterMedicamentosModeloBasicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

@Inject
private MpmItemModeloBasicoMedicamentoDAO mpmItemModeloBasicoMedicamentoDAO;

@EJB
private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1733838723740859557L;

	public enum ManterMedicamentosModeloBasicoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ITEM_MED_NAO_INFORMADO, MENSAGEM_MODELO_MED_NAO_INFORMADO, MENSAGEM_TIPO_FREQUENCIA_NAO_EXISTE, MENSAGEM_TIPO_FREQUENCIA_INATIVO, MENSAGEM_FREQUENCIA_OBRIGATORIA, MENSAGEM_NAO_INFORMAR_FREQUENCIA, MENSAGEM_VIA_ADMINISTRACAO_NAO_EXISTE, MENSAGEM_VIA_ADMINISTRACAO_INATIVA, MENSAGEM_TIPO_VELOC_ADMINISTRACAO_NAO_EXISTE, MENSAGEM_TIPO_VELOC_ADMINISTRACAO_INATIVA, MENSAGEM_MODELO_BASICO_MEDICAMENTO_NAO_EXISTE, MENSAGEM_MEDICAMENTO_NAO_EXISTE, MENSAGEM_MEDICAMENTO_INATIVO, MENSAGEM_DOSE_NAO_PODE_FRACIONADA, MENSAGEM_FORMA_DE_DOSAGEM_NAO_EXISTE, MENSAGEM_FORMA_DE_DOSAGEM_INATIVA, MENSAGEM_FORMA_DE_DOSAGEM_NAO_COMPATIVEL, MENSAGEM_EXIGE_OBSERVACAO, MENSAGEM_EXIGE_GOTEJO, MENSAGEM_EXIGE_TIPO_VELOC_ADMINISTRACAO, MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIO, MENSAGEM_GOTEJO_MAIOR_ZERO, MENSAGEM_ERRO_HIBERNATE_VALIDATION, MENSAGEM_ERRO_PERSISTIR_DADOS,
		DOSE_MENOR_IGUAL_ZERO, GOTEJO_MENOR_IGUAL_ZERO, QUANTIDADE_HORAS_CORRER_MENOR_IGUAL_ZERO;
	}

	/**
	 * Retorna Modelo Basico de Medicamento pelo id
	 * 
	 * @param seqModelo
	 * @param seqItemModelo
	 * @return
	 */
		public MpmModeloBasicoMedicamento obterModeloBasicoMedicamento(
				Integer seqModelo, Integer seqItemModelo) {
			return this.getMpmModeloBasicoMedicamentoDAO().obterPorChavePrimaria(
					new MpmModeloBasicoMedicamentoId(seqModelo, seqItemModelo));
		}

	/**
	 * Lista de modelos de medicamentos de um modelo básico de prescrição
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @return
	 */
	public List<MpmModeloBasicoMedicamento> obterListaMedicamentos(
			Integer modeloBasicoPrescricaoSeq) {
		return this.getMpmModeloBasicoMedicamentoDAO().listarMedicamentos(
				modeloBasicoPrescricaoSeq);
	}

	/**
	 * Lista de itens de medicamentos de um modelo básico de prescrição
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MpmItemModeloBasicoMedicamento> obterListaItensMedicamentos(
			Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoMedicamentoSeq) throws ApplicationBusinessException {
		return this.getMpmItemModeloBasicoMedicamentoDAO()
				.obterItensMedicamento(modeloBasicoPrescricaoSeq,
						modeloBasicoMedicamentoSeq);
	}

	/**
	 * Insere novo item, a partir do item insere o pai
	 * 
	 * @param itemMedicamento
	 * @throws ApplicationBusinessException
	 */
	public void inserir(MpmItemModeloBasicoMedicamento itemMedicamento)
			throws ApplicationBusinessException {

		if (itemMedicamento == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_ITEM_MED_NAO_INFORMADO);
		}
		// valida associacao com o pai(modelo de medicamento)
		MpmModeloBasicoMedicamento modelo = validaAssociacao(itemMedicamento
				.getModeloBasicoMedicamento());
		// se não existe inclui
		if (modelo == null) {
			modelo = itemMedicamento.getModeloBasicoMedicamento();
			this.inserir(modelo);
		}
		itemMedicamento.setModeloBasicoMedicamento(modelo);

		// cria o novo id
		itemMedicamento.setId(this.getItemMedicamentoId(itemMedicamento));

		// atributos de auditoria
		itemMedicamento.setServidor(validaAssociacao(itemMedicamento
				.getServidor()));

		// RN07 - Verificar se o medicamento está ativo
		this.validaMedicamento(itemMedicamento);

		// valida item
		this.valida(itemMedicamento);

		// salva no banco
		try {	
			this.getMpmItemModeloBasicoMedicamentoDAO().persistir(itemMedicamento);
			this.getMpmItemModeloBasicoMedicamentoDAO().flush();
		} catch (final ConstraintViolationException ise) {
			String mensagem = "";
			Set<ConstraintViolation<?>> arr = ise.getConstraintViolations();
			for (ConstraintViolation item : arr) {
				if (!"".equals(item)) {
					mensagem = item.getMessage();
					if (mensagem.isEmpty()) {
						mensagem = " Valor inválido para o campo "
								+ item.getPropertyPath();
					}
				}
			}
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_ERRO_PERSISTIR_DADOS,
					e.getMessage());
		}
			
			
	}
	
	/**
	 * Método que verifica campos do item de medicamento que, quando informados, não podem ser zero
	 * @param itemMedicamento
	 */
	private void verificarCamposZeroItem(MpmItemModeloBasicoMedicamento itemMedicamento)
	throws ApplicationBusinessException{
		
		if (itemMedicamento.getDose().compareTo(BigDecimal.ZERO) <= 0){
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.DOSE_MENOR_IGUAL_ZERO);
		}

	}
	
	/**
	 * Método que verifica campos do modelo que, quando informados, não podem ser zero
	 * @param itemMedicamento
	 */
	private void verificarCamposZeroModelo(MpmModeloBasicoMedicamento modeloBasicoMedicamento)
	throws ApplicationBusinessException{
		
		BigDecimal gotejo = modeloBasicoMedicamento.getGotejo();
		if (!(gotejo == null || (gotejo != null && gotejo.intValue() > 0))) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.GOTEJO_MENOR_IGUAL_ZERO);
		}
		Byte quantidadeHorasCorrer = modeloBasicoMedicamento.getQuantidadeHorasCorrer();
		if (!(quantidadeHorasCorrer == null || (quantidadeHorasCorrer != null && quantidadeHorasCorrer > 0))) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.QUANTIDADE_HORAS_CORRER_MENOR_IGUAL_ZERO);
		}

	}

	/**
	 * cria o id do item, composto de modelo basico prescricao, modelo basico
	 * medicamento, medicamento e seqp=1
	 * 
	 * @param itemMedicamento
	 * @return id
	 */
	private MpmItemModeloBasicoMedicamentoId getItemMedicamentoId(
			MpmItemModeloBasicoMedicamento itemMedicamento) {
		MpmItemModeloBasicoMedicamentoId id = new MpmItemModeloBasicoMedicamentoId(
				itemMedicamento.getModeloBasicoMedicamento()
						.getModeloBasicoPrescricao().getSeq(), itemMedicamento
						.getModeloBasicoMedicamento().getId().getSeq(),
				itemMedicamento.getMedicamento().getMatCodigo(),
				Integer.valueOf(1));
		// medicamento sempre tem apenas 1!
		return id;
	}

	private MpmModeloBasicoMedicamento validaAssociacao(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {

		MpmModeloBasicoMedicamento result = null;

		if (modeloBasicoMedicamento != null) {
			// se possui dieta associada carrega
			if (modeloBasicoMedicamento.getId() != null
					&& modeloBasicoMedicamento.getId()
							.getModeloBasicoPrescricaoSeq() != null
					&& modeloBasicoMedicamento.getId().getSeq() != null) {
				result = this.getMpmModeloBasicoMedicamentoDAO()
						.obterPorChavePrimaria(modeloBasicoMedicamento.getId());
			}
		}
		return result;
	}

	/**
	 * @param modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void inserir(MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		if (modeloBasicoMedicamento == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MODELO_MED_NAO_INFORMADO);
		}
		// cria o id
		modeloBasicoMedicamento.setId(new MpmModeloBasicoMedicamentoId());
		modeloBasicoMedicamento.getId().setModeloBasicoPrescricaoSeq(
				modeloBasicoMedicamento.getModeloBasicoPrescricao().getSeq());
		// é medicamento
		modeloBasicoMedicamento.setIndSolucao(false);

		// valida modelo
		this.valida(modeloBasicoMedicamento);

		if (modeloBasicoMedicamento.getIndSeNecessario() == null) {
			modeloBasicoMedicamento.setIndSeNecessario(false);
		}

		this.getMpmModeloBasicoMedicamentoDAO()
				.persistir(modeloBasicoMedicamento);
		this.getMpmModeloBasicoMedicamentoDAO().flush();
	}

	/**
	 * @param modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	public void alterar(MpmItemModeloBasicoMedicamento itemMedicamento)
			throws ApplicationBusinessException {
		if (itemMedicamento == null || itemMedicamento.getId() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_ITEM_MED_NAO_INFORMADO);
		}
		this.valida(itemMedicamento);
		this.valida(itemMedicamento.getModeloBasicoMedicamento());
		this.getMpmModeloBasicoMedicamentoDAO().merge(
				itemMedicamento.getModeloBasicoMedicamento());
		this.getMpmItemModeloBasicoMedicamentoDAO().merge(itemMedicamento);
		this.getMpmModeloBasicoMedicamentoDAO().flush();
	}

	// VALIDACOES

	/**
	 * Regras de Negócio MpmModeloBasicoMedicamento
	 * 
	 * @param modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void valida(MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		// Valida o modelo de prescrição
		modeloBasicoMedicamento.setModeloBasicoPrescricao(this
				.getManterDietasModeloBasicoON().validaAssociacao(
						modeloBasicoMedicamento.getModeloBasicoPrescricao()));

		//verifica campos do modelo que, quando informados, não podem ser zero
		this.verificarCamposZeroModelo(modeloBasicoMedicamento);
		
		// Valida o servidor associado
		modeloBasicoMedicamento
				.setServidor(validaAssociacao(modeloBasicoMedicamento
						.getServidor()));

		// valida se é o mesmo servidor do modelo
		this.getManterDietasModeloBasicoON().validaServidor(
				modeloBasicoMedicamento.getServidor(),
				modeloBasicoMedicamento.getModeloBasicoPrescricao());

		// RN03 - Verifica se tipo freqüência aprazamento está ativo e se digita
		// a freqüência
		this.validaFrequencia(modeloBasicoMedicamento);

		// RN04 - Verifica se via administração Existe e está ativa
		this.validaViaAdministracao(modeloBasicoMedicamento);

		// RN05 - Verifica se Tipo Veloc Administração (gotejo) Existe e está
		// ativo, caso esteja informado
		this.validaTipoVelocAdministracoes(modeloBasicoMedicamento);

		// VALIDA gotejo x Tipo Veloc Administração
		if (modeloBasicoMedicamento.getGotejo() != null
				&& modeloBasicoMedicamento.getTipoVelocidadeAdministracao() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_EXIGE_GOTEJO);
		} else if (modeloBasicoMedicamento.getGotejo() == null
				&& modeloBasicoMedicamento.getTipoVelocidadeAdministracao() != null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_EXIGE_TIPO_VELOC_ADMINISTRACAO);
		}

		// valida numéricos maior que zero
		if (modeloBasicoMedicamento.getGotejo() != null
				&& modeloBasicoMedicamento.getGotejo().intValue() <= 0) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_GOTEJO_MAIOR_ZERO);
		}
	}

	/**
	 * Regras de Negócio para Item : MpmItemModeloBasicoMedicamento
	 * 
	 * @param itemModeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void valida(
			MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento)
			throws ApplicationBusinessException {

		// valida associação com modeloBasicoMedicamento
		if (itemModeloBasicoMedicamento.getModeloBasicoMedicamento() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_MEDICAMENTO_NAO_EXISTE);
		}
		
		//Verifica se a dose não é zero
		this.verificarCamposZeroItem(itemModeloBasicoMedicamento);

		// verifica se a dose não pode ser fracionada
		if (!itemModeloBasicoMedicamento.getMedicamento()
				.getIndPermiteDoseFracionada()) {
			// mpm_item_mod_basico_mdtos.dose !=
			// trunc(mpm_item_mod_basico_mdtos.dose),
			if (!itemModeloBasicoMedicamento.getDose().equals(
					itemModeloBasicoMedicamento.getDose().setScale(0,
							RoundingMode.DOWN))) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_DOSE_NAO_PODE_FRACIONADA);

			}
		}

		// RN08 - Verificar se a forma de dosagem está ativa e se está
		// compatível com o medicamento prescrito
		this.validaFormaDosagem(itemModeloBasicoMedicamento);

		// RN09 - Verificar se a observação deve ser obrigatória ou não
		this.validaObservacao(itemModeloBasicoMedicamento);
	}

	// RN03
	private void validaFrequencia(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {

		if (modeloBasicoMedicamento.getTipoFrequenciaAprazamento() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIO);
		}

		if (modeloBasicoMedicamento.getTipoFrequenciaAprazamento() != null) {

			MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento = this
					.getMpmTipoFrequenciaAprazamentoDAO()
					.obterPorChavePrimaria(
							modeloBasicoMedicamento
									.getTipoFrequenciaAprazamento().getSeq());

			if (mpmTipoFrequenciaAprazamento == null) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_EXISTE);
			}

			if (!mpmTipoFrequenciaAprazamento.getIndSituacao().isAtivo()) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INATIVO);
			}

			if (mpmTipoFrequenciaAprazamento.getIndDigitaFrequencia()
					&& modeloBasicoMedicamento.getFrequencia() == null) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_FREQUENCIA_OBRIGATORIA);
			} else if (!mpmTipoFrequenciaAprazamento.getIndDigitaFrequencia()
					&& modeloBasicoMedicamento.getFrequencia() != null) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_NAO_INFORMAR_FREQUENCIA);
			}
		}

		if (modeloBasicoMedicamento.getFrequencia() != null
				&& modeloBasicoMedicamento.getFrequencia().intValue() <= 0) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_FREQUENCIA_MAIOR_ZERO);
		}
	}

	/**
	 * RN04 - Verifica se via administração existe e está ativa
	 * 
	 * @param modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void validaViaAdministracao(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {

		if (modeloBasicoMedicamento.getViaAdministracao() == null
				|| modeloBasicoMedicamento.getViaAdministracao().getSigla() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_VIA_ADMINISTRACAO_NAO_EXISTE);
		}

		AfaViaAdministracao viaAdministracao = getFarmaciaFacade()
				.obterViaAdministracao(modeloBasicoMedicamento
						.getViaAdministracao().getSigla());

		if (viaAdministracao == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_VIA_ADMINISTRACAO_NAO_EXISTE);
		}

		if (!viaAdministracao.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_VIA_ADMINISTRACAO_INATIVA);
		}
	}

	/**
	 * RN05 - Verifica se Tipo Velocidade Administração existe e está ativa,
	 * caso tenha sido informada
	 * 
	 * @param modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void validaTipoVelocAdministracoes(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		if (modeloBasicoMedicamento.getTipoVelocidadeAdministracao() != null
				&& modeloBasicoMedicamento.getTipoVelocidadeAdministracao()
						.getSeq() != null) {
			AfaTipoVelocAdministracoes tipoVelocAdministracao = this
					.obterTipoVelocAdministracoes(modeloBasicoMedicamento
							.getTipoVelocidadeAdministracao().getSeq());

			if (tipoVelocAdministracao == null) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_VELOC_ADMINISTRACAO_NAO_EXISTE);
			}

			if (!tipoVelocAdministracao.getIndSituacao().isAtivo()) {
				throw new ApplicationBusinessException(
						ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_TIPO_VELOC_ADMINISTRACAO_INATIVA);
			}
		}
	}

	/**
	 * RN07 - Verificar se o medicamento existe está ativo e se a dose pode ser
	 * fracionada ou não
	 * 
	 * @param itemModeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void validaMedicamento(
			MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento)
			throws ApplicationBusinessException {

		if (itemModeloBasicoMedicamento.getMedicamento() == null
				|| itemModeloBasicoMedicamento.getMedicamento().getMatCodigo() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MEDICAMENTO_NAO_EXISTE);
		}

		AfaMedicamento medicamento = getFarmaciaFacade()
				.obterMedicamento(itemModeloBasicoMedicamento.getMedicamento()
						.getMatCodigo());

		if (medicamento == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MEDICAMENTO_NAO_EXISTE);
		}

		if (!DominioSituacaoMedicamento.A.equals(medicamento.getIndSituacao())) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MEDICAMENTO_INATIVO);
		}
	}

	/**
	 * RN08 - Verificar se a forma de dosagem está ativa e se está compatível
	 * com o medicamento prescrito fracionada ou não
	 * 
	 * @param modeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void validaFormaDosagem(
			MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento)
			throws ApplicationBusinessException {

		if (itemModeloBasicoMedicamento.getFormaDosagem() == null
				|| itemModeloBasicoMedicamento.getFormaDosagem().getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_FORMA_DE_DOSAGEM_NAO_EXISTE);
		}

		AfaFormaDosagem formaDosagens = this
				.obterFormaDosagens(itemModeloBasicoMedicamento
						.getFormaDosagem().getSeq());

		if (formaDosagens == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_FORMA_DE_DOSAGEM_NAO_EXISTE);
		}

		if (!formaDosagens.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_FORMA_DE_DOSAGEM_INATIVA);
		}

		// se forma de dosagem está compatível com o medicamento prescrito
		if (!formaDosagens
				.getAfaMedicamentos()
				.getMatCodigo()
				.equals(itemModeloBasicoMedicamento.getMedicamento()
						.getMatCodigo())) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_FORMA_DE_DOSAGEM_NAO_COMPATIVEL);
		}
	}

	/**
	 * RN09 - Verificar se a observação deve ser obrigatória ou não
	 * 
	 * @param itemModeloBasicoMedicamento
	 * @throws ApplicationBusinessException
	 */
	private void validaObservacao(
			MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento)
			throws ApplicationBusinessException {

		if (itemModeloBasicoMedicamento.getMedicamento() == null
				|| itemModeloBasicoMedicamento.getMedicamento().getMatCodigo() == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MEDICAMENTO_NAO_EXISTE);
		}

		AfaMedicamento medicamento = getFarmaciaFacade()
				.obterMedicamento(itemModeloBasicoMedicamento.getMedicamento()
						.getMatCodigo());

		if (medicamento == null) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_MEDICAMENTO_NAO_EXISTE);
		}

		if (medicamento.getIndExigeObservacao()
				&& StringUtils.isBlank(itemModeloBasicoMedicamento
						.getObservacao())) {
			throw new ApplicationBusinessException(
					ManterMedicamentosModeloBasicoONExceptionCode.MENSAGEM_EXIGE_OBSERVACAO);
		}

	}

	public AfaTipoVelocAdministracoes obterTipoVelocAdministracoes(Short seq) {
		return this.getFarmaciaFacade().obterAfaTipoVelocAdministracoesDAO(seq);
	}

//	public AfaMedicamento obterMedicamento(Integer matCodigo) {
//		return this.getAfaMedicamentoDAO().obterPorChavePrimaria(matCodigo);
//	}

	public AfaFormaDosagem obterFormaDosagens(Integer seq) {
		return this.getFarmaciaFacade().obterAfaFormaDosagem(seq);
	}

	/**
	 * Verifica se o servidor associado é válido
	 * 
	 * @param servidor
	 * @return servidor carregado
	 * @throws ApplicationBusinessException
	 */
	private RapServidores validaAssociacao(RapServidores servidor)
			throws ApplicationBusinessException {
		// não informado
		if (servidor == null || servidor.getId() == null
				| servidor.getId().getVinCodigo() == null
				|| servidor.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		RapServidores result = this.getRegistroColaboradorFacade().buscaServidor(
				servidor.getId());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		return result;
	}

	// getters and setters
	private MpmModeloBasicoMedicamentoDAO getMpmModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}

	protected MpmItemModeloBasicoMedicamentoDAO getMpmItemModeloBasicoMedicamentoDAO() {
		return mpmItemModeloBasicoMedicamentoDAO;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected ManterDietasModeloBasicoON getManterDietasModeloBasicoON() {
		return manterDietasModeloBasicoON;
	}
	
}