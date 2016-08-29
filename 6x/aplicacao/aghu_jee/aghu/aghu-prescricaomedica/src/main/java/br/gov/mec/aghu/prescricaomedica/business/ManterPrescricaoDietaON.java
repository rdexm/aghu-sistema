package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAprazamentoFrequenciasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoDietaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ManterPrescricaoDietaON extends BaseBusiness {

	@EJB
	private PrescricaoMedicaON prescricaoMedicaON;
	
	@EJB
	private ManterPrescricaoDietaRN manterPrescricaoDietaRN;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;

	private static final long serialVersionUID = 6595966307819242235L;

	private static final Log LOG = LogFactory.getLog(ManterPrescricaoDietaON.class);
	
	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@EJB
	private INutricaoFacade nutricaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@Inject
	private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;
	
	public enum ManterPrescricaoDietaExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_TIPO_ITEM_DIETA_OBRIGATORIO, //
		MENSAGEM_TIPO_ITEM_DIETA_NAO_ENCONTRADO, //
		MENSAGEM_TIPO_FREQUENCIA_NAO_ENCONTRADO, //
		MENSAGEM_PRESCRICAO_DIETA_OBRIGATORIO, //
		MENSAGEM_PRESCRICAO_DIETA_NAO_ENCONTRADO, //
		MENSAGEM_PRESCRICAO_DIETA_SEM_ITEM, //
		MENSAGEM_SITUACAO_NAO_PREVSITA_ALTERACAO, //
		MENSAGEM_SITUACAO_NAO_PREVSITA_EXCLUSAO, // 
		MENSAGEM_PRESCRICAO_MEDICA_OBRIGATORIO, //
		MENSAGEM_PRESCRICAO_MEDICA_NAO_ENCONTRADO, //
		MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO, //
		MENSAGEM_ATENDIMENTO_OBRIGATORIO, //
		MENSAGEM_ITEM_PRESCRICAO_DIETA_INVALIDADA_CONSTRAINT, MPM_01310, //
		/**
		 * Não é permitido deletar prescrição de dieta já validada.
		 */
		MPM_00739, QUANTIDADE_MENOR_IGUAL_ZERO, //
		/**
		 * Quantidade obrigatória.
		 */
		MENSAGEM_QUANTIDADE_OBRIGATORIA_DIETA, //
		/**
		 * Tipo de frequencia é obrigatório.
		 */
		MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIA, MENSAGEM_ESPECIFICAR_NUM_VEZES, MENSAGEM_ITEM_UNICO;

	}

	/**
	 * Grava alterações na prescrição de dieta criando, alterando ou excluido
	 * itens associados.
	 * 
	 * @param dieta
	 *            prescrição de dieta para alteração
	 * @param novos
	 *            itens para inclusão
	 * @param alterados
	 *            itens para alteração
	 * @param excluidos
	 *            itens para exclusão
	 * @throws ApplicationBusinessException
	 * @throws CloneNotSupportedException
	 */
	public void gravar(MpmPrescricaoDieta dieta,
			List<MpmItemPrescricaoDieta> novos,
			List<MpmItemPrescricaoDieta> alterados,
			List<MpmItemPrescricaoDieta> excluidos, 
			String nomeMicrocomputador) throws BaseException,
			CloneNotSupportedException {
		this.alterar(dieta, novos, alterados, excluidos, nomeMicrocomputador);
	}

	/**
	 * Grava inclusão de prescrição de dieta criando itens associados.
	 * 
	 * @param dieta
	 *            prescrição de dieta para inclusão
	 * @param novos
	 *            itens para inclusão
	 * @throws ApplicationBusinessException
	 */
	public void gravar(MpmPrescricaoDieta dieta,
			List<MpmItemPrescricaoDieta> novos, String nomeMicrocomputador) throws BaseException {

		if (novos == null || novos.isEmpty()) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_PRESCRICAO_DIETA_SEM_ITEM);
		}

		this.inserir(dieta, nomeMicrocomputador);
		this.inserir(dieta, novos);
	}

	/**
	 * Realizar a alteração dos itens da prescrição de dieta
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void alterar(MpmItemPrescricaoDieta itemDieta)
			throws ApplicationBusinessException {
		this.getManterPrescricaoDietaRN().atualizarItemPrescricaoDieta(
				itemDieta);
	}

	/**
	 * Realizar a inserção dos itens da prescrição de dieta
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	public void inserir(MpmItemPrescricaoDieta item)
			throws BaseException {
		try {
			this.valida(item);
			this.getManterPrescricaoDietaRN().inserirItemPrescricaoDieta(item);
			// busca dieta do persistent context para atualizar
			MpmPrescricaoDieta dieta = this.getMpmPrescricaoDietaDAO()
					.obterPorChavePrimaria(item.getPrescricaoDieta().getId());
			dieta.getItemPrescricaoDieta().add(item);

		} catch (BaseRuntimeException e) {
			String erro = "";
			if (e.getCode() != null) {
				erro = e.getMessage();
			}
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_ITEM_PRESCRICAO_DIETA_INVALIDADA_CONSTRAINT,
					e, erro);
		}
	}

	/**
	 * Faz uma pre validação do item antes da prescrição de dieta existir.
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	public void preValidar(MpmItemPrescricaoDieta itemDieta,
			List<ItemPrescricaoDietaVO> listaItens) throws BaseException {

		itemDieta.setTipoItemDieta(this.validaAssociacao(itemDieta
				.getTipoItemDieta()));
		itemDieta.setTipoFreqAprazamento(this.validaAssociacao(itemDieta
				.getTipoFreqAprazamento()));

		this.validaQuantidade(itemDieta);

		// tipo de frequencia é obrigatória
		if (itemDieta.getTipoItemDieta().getIndDigitaAprazamento() == DominioRestricao.O
				&& itemDieta.getTipoFreqAprazamento() == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIA);
		}

		this.getPrescricaoMedicaRN().verificaDigitacaoFrequencia(
				itemDieta.getTipoFreqAprazamento(), itemDieta.getFrequencia());
		this.getPrescricaoMedicaRN().validaAprazamento(
				itemDieta.getTipoFreqAprazamento(), itemDieta.getFrequencia());
		this.validarNumeroVezes(itemDieta);
		this.validarItemDietaUnico(itemDieta, listaItens);
	}

	/**
	 * Validações comuns realizadas no objeto antes de inclusões ou alterações.
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void valida(MpmItemPrescricaoDieta itemDieta)
			throws ApplicationBusinessException {

		itemDieta.setTipoItemDieta(this.validaAssociacao(itemDieta
				.getTipoItemDieta()));
		itemDieta.setTipoFreqAprazamento(this.validaAssociacao(itemDieta
				.getTipoFreqAprazamento()));
		itemDieta.setPrescricaoDieta(this.validaAssociacao(itemDieta
				.getPrescricaoDieta()));

		this.validaQuantidade(itemDieta);

		// tipo de frequencia é obrigatória
		if (itemDieta.getTipoItemDieta().getIndDigitaAprazamento() == DominioRestricao.O
				&& itemDieta.getTipoFreqAprazamento() == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIA);
		}

	}

	/**
	 * Valida a quantidade do item de prescrição de dieta.
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	private void validaQuantidade(MpmItemPrescricaoDieta item)
			throws ApplicationBusinessException {
		// se obrigatório e quantidade não informada(null/zero)
		if (item.getTipoItemDieta().getIndDigitaQuantidade() == DominioRestricao.O
				&& (item.getQuantidade() == null || item.getQuantidade()
						.compareTo(BigDecimal.ZERO) == 0)) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_QUANTIDADE_OBRIGATORIA_DIETA);
		}

		if (item.getQuantidade() != null
				&& item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.QUANTIDADE_MENOR_IGUAL_ZERO);
		}
	}

	/**
	 * Verifica se encontra pela chave primária.
	 * 
	 * @param freq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmTipoFrequenciaAprazamento validaAssociacao(
			MpmTipoFrequenciaAprazamento freq) throws ApplicationBusinessException {
		if (freq == null || freq.getSeq() == null) {
			return null;
		}
		MpmTipoFrequenciaAprazamento result = this
				.getMpmTipoFrequenciaAprazamentoDAO().obterPorChavePrimaria(
						freq.getSeq());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_ENCONTRADO);
		}
		return result;
	}

	/**
	 * Verifica a obrigatoriedade do tipo de dieta e se encontra através da
	 * chave primária.
	 * 
	 * @param tipo
	 * @return
	 * @throws ApplicationBusinessException
	 *             se não informado ou não encontrado
	 */
	private AnuTipoItemDieta validaAssociacao(AnuTipoItemDieta tipo)
			throws ApplicationBusinessException {
		if (tipo == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_TIPO_ITEM_DIETA_OBRIGATORIO);
		}

		AnuTipoItemDieta result = this.getNutricaoFacade()
				.obterAnuTipoItemDietaPorChavePrimaria(tipo.getSeq());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_TIPO_ITEM_DIETA_NAO_ENCONTRADO);
		}

		return result;
	}

	private MpmPrescricaoDieta validaAssociacao(
			MpmPrescricaoDieta prescricaoDieta) throws ApplicationBusinessException {
		if (prescricaoDieta == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_PRESCRICAO_DIETA_OBRIGATORIO);
		}

		MpmPrescricaoDieta result = this.getMpmPrescricaoDietaDAO()
				.obterPorChavePrimaria(prescricaoDieta.getId());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_PRESCRICAO_DIETA_NAO_ENCONTRADO);
		}

		return result;
	}

	/**
	 * Inserir itens de dieta associados à dieta fornecida.
	 * 
	 * @param dieta
	 *            dieta
	 * @param itens
	 *            itens de dieta
	 * @throws ApplicationBusinessException
	 */
	public void inserir(MpmPrescricaoDieta dieta,
			List<MpmItemPrescricaoDieta> itens) throws BaseException {
		for (MpmItemPrescricaoDieta item : itens) {
			// montar chave composta dos itens com a chave da dieta
			MpmItemPrescricaoDietaId id = new MpmItemPrescricaoDietaId(dieta
					.getId(), item.getTipoItemDieta().getSeq());
			item.setId(id);
			this.inserir(item);
		}

	}

	public MpmItemPrescricaoDietaDAO getMpmItemPrescricaoDietaDAO() {
		return mpmItemPrescricaoDietaDAO;
	}
	
	public void inserir(MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador) throws BaseException{
		inserir(prescricaoDieta, false, nomeMicrocomputador);
	}

	/**
	 * Realizar a inclusão da prescrição de dieta
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	public void inserir(MpmPrescricaoDieta prescricaoDieta, Boolean isCopiado, String nomeMicrocomputador)
			throws BaseException {

		MpmPrescricaoMedica prescricaoMedica = this
				.validaAssociacao(prescricaoDieta.getPrescricaoMedica());
		prescricaoDieta.setPrescricaoMedica(prescricaoMedica);

		prescricaoDieta.setIndPendente(DominioIndPendenteItemPrescricao.P);
		prescricaoDieta.setIndItemRecomendadoAlta(false);
		prescricaoDieta.setCriadoEm(Calendar.getInstance().getTime());
		prescricaoDieta.setIndAvalNutricionista(false);
		prescricaoDieta.setIndItemRecTransferencia(false);

		if (this.getPrescricaoMedicaON().isPrescricaoVigente(prescricaoMedica)) {
			// prescricaoDieta.setDthrInicio(prescricaoMedica.getDthrMovimento());
			prescricaoDieta
					.setDthrInicio(prescricaoMedica.getDthrMovimento() != null ? new Timestamp(
							prescricaoMedica.getDthrMovimento().getTime())
							: null);

		} else {
			// prescricaoDieta.setDthrInicio(prescricaoMedica.getDthrInicio());
			prescricaoDieta
					.setDthrInicio(prescricaoMedica.getDthrInicio() != null ? new Timestamp(
							prescricaoMedica.getDthrInicio().getTime())
							: null);

		}

		// prescricaoDieta.setDthrFim(prescricaoMedica.getDthrFim());
		prescricaoDieta
				.setDthrFim(prescricaoMedica.getDthrFim() != null ? new Timestamp(
						prescricaoMedica.getDthrFim().getTime())
						: null);

		this.valida(prescricaoDieta);
		
		this.getManterPrescricaoDietaRN().inserirPrescricaoDieta(
				prescricaoDieta, isCopiado, nomeMicrocomputador);

	}

	/**
	 * Valida a associação da prescrição de dieta com a prescrição médica
	 * 
	 * @param prescricaoMedica
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMedica validaAssociacao(
			MpmPrescricaoMedica prescricaoMedica) throws ApplicationBusinessException {
		if (prescricaoMedica == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_PRESCRICAO_MEDICA_OBRIGATORIO);
		}
		prescricaoMedica = this.getMpmPrescricaoMedicaDAO()
				.obterPorChavePrimaria(prescricaoMedica.getId());
		if (prescricaoMedica == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_PRESCRICAO_MEDICA_NAO_ENCONTRADO);
		}

		return prescricaoMedica;
	}

	/**
	 * Valida a associação da prescrição de dieta com o atendimento
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AghAtendimentos validaAssociacao(AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_ATENDIMENTO_OBRIGATORIO);
		}
		atendimento = this.getAghuFacade().obterAtendimentoPeloSeq(
				atendimento.getSeq());
		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_ATENDIMENTO_NAO_ENCONTRADO);
		}

		return atendimento;
	}

	/**
	 * Validações da prescrição de dieta.
	 * 
	 * @param prescricaoDieta
	 */
	public void valida(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		// retira brancos da observação
		String stripObservacao = StringUtils.stripToNull(prescricaoDieta
				.getObservacao());
		prescricaoDieta.setObservacao(stripObservacao);

		prescricaoDieta.setPrescricaoMedica(this
				.validaAssociacao(prescricaoDieta.getPrescricaoMedica()));

		prescricaoDieta.getPrescricaoMedica().setAtendimento(this
				.validaAssociacao(new AghAtendimentos(prescricaoDieta
						.getPrescricaoMedica().getId().getAtdSeq())));

	}
	
	/**
	 * Realizar as regras de desdobramento da prescrição de dietas, consistir as
	 * regras de atualização e atualizar o itens
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 * @throws CloneNotSupportedException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void alterar(MpmPrescricaoDieta prescricaoDieta,
			List<MpmItemPrescricaoDieta> novos,
			List<MpmItemPrescricaoDieta> alterados,
			List<MpmItemPrescricaoDieta> excluidos,
			String nomeMicrocomputador) throws BaseException,
			CloneNotSupportedException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		MpmPrescricaoDietaDAO prescricaoDietaDAO = getMpmPrescricaoDietaDAO();

		List<DominioIndPendenteItemPrescricao> listaSetaServidor = new ArrayList<DominioIndPendenteItemPrescricao>();
		listaSetaServidor.add(DominioIndPendenteItemPrescricao.P);
		listaSetaServidor.add(DominioIndPendenteItemPrescricao.B);
		listaSetaServidor.add(DominioIndPendenteItemPrescricao.R);
		
		// se prescricao pendente, modelo básico ou represcrição não validada
		if (listaSetaServidor.contains(prescricaoDieta.getIndPendente())) {
			prescricaoDieta.setRapServidores(servidorLogado);
						
			this.getManterPrescricaoDietaRN().atualizarPrescricaoDieta(
					prescricaoDieta, nomeMicrocomputador);

			// atualizar itens da prescrição(inclusão/alteração/exclusao)
			this.atualizarItensPrescricaoDieta(prescricaoDieta, novos,
					alterados, excluidos);
		} else if (DominioIndPendenteItemPrescricao.N.equals(prescricaoDieta
				.getIndPendente())) {

			Set<MpmItemPrescricaoDieta> listaItemPrescricaoDieta = this
					.getMpmItemPrescricaoDietaDAO().obterItensPrescricaoDieta(
							prescricaoDieta);

			MpmPrescricaoMedica prescricaoMedica = prescricaoDieta
					.getPrescricaoMedica();

			// 1º PASSO ####################
			// atualiza o registro original

			// buscar a prescrição de dieta original
			// desatacha para atualizar o objeto original
			this.getMpmPrescricaoDietaDAO().desatachar(prescricaoDieta);
			MpmPrescricaoDieta prescricaoDietaOrigem = getMpmPrescricaoDietaDAO()
					.obterPorChavePrimaria(prescricaoDieta.getId());

			if (prescricaoMedica.isPrescricaoMedicaVigente()) {
				prescricaoDietaOrigem.setDthrFim(prescricaoMedica
						.getDthrMovimento() != null ? new Timestamp(
						prescricaoMedica.getDthrMovimento().getTime()) : null);
			} else {
				prescricaoDietaOrigem.setDthrFim(prescricaoMedica
						.getDthrInicio() != null ? new Timestamp(
						prescricaoMedica.getDthrInicio().getTime()) : null);
			}

			prescricaoDietaOrigem.setAlteradoEm(Calendar.getInstance()
					.getTime());
			prescricaoDietaOrigem.setServidorMovimentado(servidorLogado);
			prescricaoDietaOrigem
					.setIndPendente(DominioIndPendenteItemPrescricao.A);

			this.getManterPrescricaoDietaRN().atualizarPrescricaoDieta(
					prescricaoDietaOrigem, nomeMicrocomputador);

			// 2º PASSO ####################
			// cria registro novo relacionando com o original
			MpmPrescricaoDieta prescricaoDietaNovo = null;
			try {
				prescricaoDietaNovo = (MpmPrescricaoDieta) prescricaoDieta
						.clone();
				prescricaoDietaNovo
						.setItemPrescricaoDieta(new HashSet<MpmItemPrescricaoDieta>());
				prescricaoDietaNovo
						.setPrescricaoDieta(new HashSet<MpmPrescricaoDieta>());
				prescricaoDietaNovo.setId(new MpmPrescricaoDietaId(
						prescricaoDietaOrigem.getPrescricaoMedica().getId()
								.getAtdSeq(), null));

				// Realizar o auto-relacionamento com a prescrição anterior
				prescricaoDietaNovo
						.setMpmPrescricaoDietas(prescricaoDietaOrigem);
				// Restaurar informações de validação do novo registro
				prescricaoDietaNovo.setServidorValidacao(null);
				prescricaoDietaNovo.setServidorValidaMovimentacao(null);
				prescricaoDietaNovo.setDthrValida(null);
				prescricaoDietaNovo.setDthrValidaMovimentacao(null);
			} catch (CloneNotSupportedException e) {
				LOG.error("A classe MpmPrescricaoDieta "
						+ "não implementa a interface Cloneable.", e);
			}

			// atualiza os itens de dieta para o novo registro

			for (MpmItemPrescricaoDieta itemExcluido : excluidos) {
				listaItemPrescricaoDieta.remove(itemExcluido);
			}

			for (MpmItemPrescricaoDieta itemNovo : novos) {
				listaItemPrescricaoDieta.add(itemNovo);
			}

			for (MpmItemPrescricaoDieta itemAlterado : alterados) {
				// substitui
				listaItemPrescricaoDieta.remove(itemAlterado);
				listaItemPrescricaoDieta.add(itemAlterado);
			}

			if (listaItemPrescricaoDieta == null
					|| listaItemPrescricaoDieta.isEmpty()) {
				throw new ApplicationBusinessException(
						ManterPrescricaoDietaExceptionCode.MPM_01310);
			}

			this.inserir(prescricaoDietaNovo, nomeMicrocomputador);
			this.inserir(prescricaoDietaNovo, this.copiarItensPrescricaoDieta(
					listaItemPrescricaoDieta, prescricaoDietaNovo));

		} else {
			LOG.error(String.format("A prescrição de dieta não pode ser "
					+ "modificada porque está em situação \"%s\" .",
					prescricaoDieta.getIndPendente().getDescricao()));
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_SITUACAO_NAO_PREVSITA_ALTERACAO,
					prescricaoDieta.getIndPendente().getDescricao());
		}

		prescricaoDietaDAO.flush();
	}

	/**
	 * Atualizar os itens da prescrição de dieta
	 * 
	 * @param prescricaoDieta
	 * @param novos
	 * @param alterados
	 * @param excluidos
	 * @throws ApplicationBusinessException
	 */
	public void atualizarItensPrescricaoDieta(
			MpmPrescricaoDieta prescricaoDieta,
			List<MpmItemPrescricaoDieta> novos,
			List<MpmItemPrescricaoDieta> alterados,
			List<MpmItemPrescricaoDieta> excluidos) throws BaseException {

		if (excluidos != null && !excluidos.isEmpty()) {
			this.excluir(excluidos);
		}

		if (novos != null && !novos.isEmpty()) {
			this.inserir(prescricaoDieta, novos);
		}

		for (MpmItemPrescricaoDieta item : alterados) {
			this.alterar(item);
		}

		// verificar se após a atualização a prescrição ficou com pelo menos um
		// item
		if (!this.getMpmItemPrescricaoDietaDAO().existeItemPrescricao(
				prescricaoDieta)) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MPM_01310);
		}
	}

	/**
	 * Copiar as propriedades da lista de itens
	 * 
	 * @param itens
	 * @return
	 * @throws UnhandledException
	 *             se erro na copia dos objetos
	 */
	public List<MpmItemPrescricaoDieta> copiarItensPrescricaoDieta(
			Set<MpmItemPrescricaoDieta> itens, MpmPrescricaoDieta dieta) {

		List<MpmItemPrescricaoDieta> setNovosItens = null;
		try {
			setNovosItens = new ArrayList<MpmItemPrescricaoDieta>();
			for (MpmItemPrescricaoDieta item : itens) {
				MpmItemPrescricaoDieta novoItem = new MpmItemPrescricaoDieta();
				PropertyUtils.copyProperties(novoItem, item);
				novoItem.setId(null);
				novoItem.setPrescricaoDieta(dieta);
				setNovosItens.add(novoItem);
			}
		} catch (Exception e) {
			LOG.error(
					"Não foi possivel fazer a cópia dos objetos da classe MpmItemPrescricaoDieta.",
					e);
		}

		return setNovosItens;
	}

	/**
	 * Realizar a exclusão de item de dieta, executando as consistências
	 * necessárias
	 * 
	 * @param item
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MpmItemPrescricaoDieta item){
		// TODO: verificar necessidade de desatachar
		this.getMpmItemPrescricaoDietaDAO().desatachar(item);
		item = this.getMpmItemPrescricaoDietaDAO().obterPorChavePrimaria(
				item.getId());
		this.getMpmItemPrescricaoDietaDAO().remover(item);
		this.getMpmItemPrescricaoDietaDAO().flush();
		// retorna dieta do persistent context para atualizacao
		MpmPrescricaoDieta dieta = this.getMpmPrescricaoDietaDAO()
				.obterPorChavePrimaria(item.getPrescricaoDieta().getId());
		dieta.getItemPrescricaoDieta().remove(item);
	}

	/**
	 * Remover itens que foram excluídos na alteração da prescrição de dietas
	 * 
	 * @param prescricaoDieta
	 * @param prescricaoDietaOrigem
	 * @throws ApplicationBusinessException
	 */
	public void excluir(List<MpmItemPrescricaoDieta> itens)
			throws ApplicationBusinessException {

		for (MpmItemPrescricaoDieta itemPrescricaoDieta : itens) {
			this.excluir(itemPrescricaoDieta);
		}
	}

	public void excluirPrescricaoDieta(MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		if (DominioIndPendenteItemPrescricao.B.equals(prescricaoDieta
				.getIndPendente())) {
			// excluir fisicamente
			this.excluir(prescricaoDieta);
		} else if (DominioIndPendenteItemPrescricao.N.equals(prescricaoDieta
				.getIndPendente())) {
			prescricaoDieta.setIndPendente(DominioIndPendenteItemPrescricao.E);
			prescricaoDieta.setAlteradoEm(Calendar.getInstance().getTime());
			prescricaoDieta.setServidorMovimentado(servidorLogado);
			if (prescricaoDieta.getPrescricaoMedica()
					.isPrescricaoMedicaVigente()) {

				prescricaoDieta
						.setDthrFim(prescricaoDieta.getPrescricaoMedica()
								.getDthrMovimento() != null ? new Timestamp(
								prescricaoDieta.getPrescricaoMedica()
										.getDthrMovimento().getTime()) : null);

			} else {

				prescricaoDieta
						.setDthrFim(prescricaoDieta.getPrescricaoMedica()
								.getDthrInicio() != null ? new Timestamp(
								prescricaoDieta.getPrescricaoMedica()
										.getDthrInicio().getTime()) : null);

			}
			
			this.getManterPrescricaoDietaRN().atualizarPrescricaoDieta(
					prescricaoDieta, nomeMicrocomputador);

		} else if (DominioIndPendenteItemPrescricao.P.equals(prescricaoDieta
				.getIndPendente())) {

			// Não existe prescrição origem
			if (prescricaoDieta.getMpmPrescricaoDietas() == null) {
				this.excluir(prescricaoDieta);
				// Existe prescrição origem
			} else {
				// 1ª Etapa: atualizar a dieta que originou a prescrição que
				// será excluída
				// TODO: ver a necessidade de desatachar
				this.getMpmPrescricaoDietaDAO().desatachar(
						prescricaoDieta.getMpmPrescricaoDietas());
				MpmPrescricaoDieta prescricaoDietaOrigem = this
						.getMpmPrescricaoDietaDAO().obterPorChavePrimaria(
								prescricaoDieta.getMpmPrescricaoDietas()
										.getId());

				prescricaoDietaOrigem
						.setIndPendente(DominioIndPendenteItemPrescricao.E);
				prescricaoDietaOrigem.setServidorMovimentado(servidorLogado);
								
				this.getManterPrescricaoDietaRN().atualizarPrescricaoDieta(
						prescricaoDietaOrigem, nomeMicrocomputador);

				// 2ª Etapa: excluir fisicamente a prescrição de dieta
				excluir(prescricaoDieta);
			}
		} else {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MENSAGEM_SITUACAO_NAO_PREVSITA_EXCLUSAO);
		}

		this.getMpmPrescricaoDietaDAO().flush();
	}

	public void excluir(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		// não é permitido excluir prescrição confirmada
		if (DominioIndPendenteItemPrescricao.N.equals(prescricaoDieta
				.getIndPendente())) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaExceptionCode.MPM_00739);
		}

		for (MpmItemPrescricaoDieta itemPrescricaoDieta : prescricaoDieta
				.getItemPrescricaoDieta()) {
			// TODO: ver necessidade de desatachar
			this.getMpmItemPrescricaoDietaDAO().desatachar(itemPrescricaoDieta);
			itemPrescricaoDieta = this.getMpmItemPrescricaoDietaDAO()
					.obterPorChavePrimaria(itemPrescricaoDieta.getId());
			this.getMpmItemPrescricaoDietaDAO().remover(itemPrescricaoDieta);
			this.getMpmItemPrescricaoDietaDAO().flush();
		}

		this.getMpmPrescricaoDietaDAO().remover(prescricaoDieta);
		this.getMpmPrescricaoDietaDAO().flush();
	}

	/**
	 * Validar se a informação referente ao número de vezes está corretamente
	 * informado
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	public void validarNumeroVezes(MpmItemPrescricaoDieta itemDieta)
			throws ApplicationBusinessException {

		// Validação se TIPO DE FREQUENCIA informada
		if (itemDieta.getTipoFreqAprazamento() != null) {

			Long numVezesApraz = this
			.calculoNumeroVezesAprazamento24Horas(itemDieta.getTipoFreqAprazamento(), itemDieta.getFrequencia());

			if (itemDieta.getNumVezes() == null) {
				if (itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento() != null
						&& CoreUtil.igual(itemDieta.getTipoItemDieta()
								.getTipoFrequenciaAprazamento(), itemDieta
								.getTipoFreqAprazamento())
						&& CoreUtil.igual(itemDieta.getTipoItemDieta()
								.getFrequencia(), itemDieta.getFrequencia())) {
					throw new ApplicationBusinessException(
							ManterPrescricaoDietaExceptionCode.MENSAGEM_ESPECIFICAR_NUM_VEZES,
							numVezesApraz);
				}
			} else {
				if (itemDieta.getNumVezes() > numVezesApraz
						|| itemDieta.getNumVezes() < 1) {
					throw new ApplicationBusinessException(
							ManterPrescricaoDietaExceptionCode.MENSAGEM_ESPECIFICAR_NUM_VEZES,
							numVezesApraz);
				}
			}
		}
	}

	/**
	 * Calcula o número de vezes que um aprazamento
	 * 
	 * ORADB: Function MPMC_NUM_VEZES_APRAZ
	 * 
	 * @param tipoFrequenciaAprazamento
	 */
	public Long calculoNumeroVezesAprazamento24Horas(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento, Short frequencia
	) {

		Long quantidade = null;

		MpmTipoFrequenciaAprazamento tipoFreqAprazamento = getMpmTipoFrequenciaAprazamentoDAO()
				.obterPorChavePrimaria(
						tipoFrequenciaAprazamento.getSeq());

		if (tipoFreqAprazamento.getIndFormaAprazamento().equals(
				DominioFormaCalculoAprazamento.C)) {
			quantidade = 999l;
			return quantidade;
		}

		if (tipoFreqAprazamento.getIndFormaAprazamento().equals(
				DominioFormaCalculoAprazamento.I)) {

			Double numVezes = frequencia
					* tipoFreqAprazamento.getFatorConversaoHoras()
							.doubleValue();

			if (numVezes < 1) {
				quantidade = 0l;
			} else {
				quantidade = (long) (24 / numVezes);
			}

			return quantidade;
		}

		if (tipoFreqAprazamento.getIndFormaAprazamento().equals(
				DominioFormaCalculoAprazamento.V)) {

			if (tipoFreqAprazamento.getFatorConversaoHoras().compareTo(
					BigDecimal.ZERO) > 0) {
				quantidade = (long) ((long) 24 * (frequencia / tipoFreqAprazamento
						.getFatorConversaoHoras().doubleValue()));
			} else {
				quantidade = getMpmAprazamentoFrequenciasDAO()
						.pesquisarQuantidadeTipoFreequencia(
								tipoFreqAprazamento.getSeq());

				if (quantidade == null || quantidade == 0) {
					quantidade = 1l;
				}
			}
		}

		return quantidade;
	}

	/**
	 * Validar a regra de itens de dieta único: 1º Apresentar exceção quando
	 * estiver incluindo um item único e já existir itens inseridos;
	 * 
	 * 2º Apresentar exceção quando estiver incluído um item qualquer e já
	 * exista item único inserido;
	 * 
	 * @param itemDieta
	 * @param listaItens
	 * @throws ApplicationBusinessException
	 */
	private void validarItemDietaUnico(MpmItemPrescricaoDieta itemDieta,
			List<ItemPrescricaoDietaVO> listaItens) throws ApplicationBusinessException {

		if (listaItens == null) {
			return;
		}

		if (itemDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		if (itemDieta.getTipoItemDieta() != null) {
			if (itemDieta.getTipoItemDieta().getIndItemUnico()) {
				if (!listaItens.isEmpty()) {
					throw new ApplicationBusinessException(ManterPrescricaoDietaExceptionCode.MENSAGEM_ITEM_UNICO, itemDieta.getTipoItemDieta().getDescricao());
				}

			} else {
				for (ItemPrescricaoDietaVO item : listaItens) {
					if (item.getTipoItem().getIndItemUnico()) {
						throw new ApplicationBusinessException(ManterPrescricaoDietaExceptionCode.MENSAGEM_ITEM_UNICO,item.getTipoItem().getDescricao());
					}
				}
			}
		}
	}

	public Set<MpmItemPrescricaoDieta> obterItensPrescricaoDieta(
			MpmPrescricaoDieta dieta) {
		Set<MpmItemPrescricaoDieta> lista = this.getMpmItemPrescricaoDietaDAO().obterItensPrescricaoDieta(dieta);
		for (MpmItemPrescricaoDieta mpmItemPrescricaoDieta : lista) {
			mpmItemPrescricaoDieta.getDescricaoFormatada();
		}
		return lista;
	}
	
	public MpmPrescricaoDieta obterPrescricaoDieta(MpmPrescricaoDietaId id) {
		MpmPrescricaoDieta dieta = this.getMpmPrescricaoDietaDAO().obterPorChavePrimaria(id);
		if(dieta != null && dieta.getPrescricaoMedica()!=null && dieta.getPrescricaoMedica().getAtendimento() != null && dieta.getPrescricaoMedica().getAtendimento().getUnidadeFuncional() != null){		
			dieta.getPrescricaoMedica().getAtendimento().getUnidadeFuncional().getSeq();
		}
		return dieta;
	}

	public MpmPrescricaoDieta obterPosterior(MpmPrescricaoDieta dieta) {
		return this.getMpmPrescricaoDietaDAO().obterPosterior(dieta);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}

	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected PrescricaoMedicaON getPrescricaoMedicaON() {
		return prescricaoMedicaON;
	}

	protected ManterPrescricaoDietaRN getManterPrescricaoDietaRN() {
		return manterPrescricaoDietaRN;
	}

	protected INutricaoFacade getNutricaoFacade() {
		return nutricaoFacade;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}

	protected MpmAprazamentoFrequenciasDAO getMpmAprazamentoFrequenciasDAO() {
		return mpmAprazamentoFrequenciasDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}
