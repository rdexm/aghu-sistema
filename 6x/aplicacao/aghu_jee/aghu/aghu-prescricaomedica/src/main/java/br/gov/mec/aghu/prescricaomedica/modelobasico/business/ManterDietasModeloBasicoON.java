package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoDietaId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAprazamentoFrequenciasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.MpmItemModeloBasicoDietaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @author rpetter
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ManterDietasModeloBasicoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterDietasModeloBasicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private MpmItemModeloBasicoDietaDAO mpmItemModeloBasicoDietaDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@Inject
private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

@Inject
private MpmModeloBasicoPrescricaoDAO mpmModeloBasicoPrescricaoDAO;

@EJB
private INutricaoFacade nutricaoFacade;

@Inject
private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;

@Inject
private MpmModeloBasicoDietaDAO mpmModeloBasicoDietaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2828885267916279812L;

	public enum ManterDietasModeloBasicoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_DIETA_NAO_INFORMADO, MENSAGEM_DIETAPAI_NAO_INFORMADO, MENSAGEM_DIETAPAI_NAO_ENCONTRADO, //
		MENSAGEM_MODELOPAI_NAO_INFORMADO, MENSAGEM_ITEM_NAO_INFORMADO, MENSAGEM_SERVIDOR_INVALIDO, //
		MENSAGEM_TIPO_DIETA_NAO_ENCONTRADO, MENSAGEM_MODELO_OUTRO_SERVIDOR, MENSAGEM_NAO_EXISTE_ITEM_DIETA_ATIVO, //
		MENSAGEM_QTD_OBRIGATORIA, MENSAGEM_NAO_INFORMAR_QTD, MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIA, //
		MENSAGEM_NAO_INFORMAR_TIPO_FREQUENCIA, MENSAGEM_ESPECIFICAR_NUM_VEZES, //
		MENSAGEM_VEZES_APRAZAMENTO_PADRAO, MENSAGEM_VEZES_APRAZAMENTO_IGUAL_PADRAO, //
		MENSAGEM_ESPECIFICAR_NUM_VEZES_RN11, MENSAGEM_INFORME_FREQUENCIA_E_TIPO, //
		MENSAGEM_TIPO_FREQUENCIA_ATIVO, MENSAGEM_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA, //
		MENSAGEM_TIPO_FREQUENCIA_NAO_PERMITE_INFORMACAO, MENSAGEM_TIPO_FREQUENCIA_NAO_CADASTRADO, //
		MENSAGEM_ITEM_DIETA_UNICO_RN12, MENSAGEM_ITEM_DIETA_JA_CADASTRADO, MENSAGEM_FREQUENCIA_MAIOR_ZERO, //
		MENSAGEM_QUANTIDADE_MAIOR_ZERO, MENSAGEM_NAO_EXCLUIR_ULTIMO_ITEM;
	}

	// ITEM DE DIETA
	/**
	 * Busca Item do modelo de dieta pelo id
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoDietaSeq
	 * @param tipoItemDietaSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public MpmItemModeloBasicoDieta obterItemDieta(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq,
			Integer tipoItemDietaSeq) throws ApplicationBusinessException {
		MpmItemModeloBasicoDieta result = null;
		if (modeloBasicoPrescricaoSeq == null || modeloBasicoDietaSeq == null
				|| tipoItemDietaSeq == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ITEM_NAO_INFORMADO);
		}

		MpmItemModeloBasicoDietaId id = new MpmItemModeloBasicoDietaId();
		id.setModeloBasicoPrescricaoSeq(modeloBasicoPrescricaoSeq);
		id.setModeloBasicoDietaSeq(modeloBasicoDietaSeq);
		id.setTipoItemDietaSeq(tipoItemDietaSeq);
		result = this.getMpmItemModeloBasicoDietaDAO()
				.obterItemModeloBasicoDieta(id);

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ITEM_NAO_INFORMADO);
		}
		return result;
	}

	/**
	 * Busca itens do modelo de dieta informado
	 * 
	 * @param seqModelo
	 */
	public List<MpmItemModeloBasicoDieta> obterListaItensDieta(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {
		return this.getMpmItemModeloBasicoDietaDAO().pesquisar(
				modeloBasicoPrescricaoSeq, modeloBasicoDietaSeq);
	}
	
	/**
	 * Busca itens do modelo de dieta informado
	 * 
	 * @param seqModelo
	 */
	public List<MpmItemModeloBasicoDietaVO> obterListaItensDietaVO(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {
		
		List<MpmItemModeloBasicoDietaVO> retorno = new ArrayList<MpmItemModeloBasicoDietaVO>();
		List<MpmItemModeloBasicoDieta> itens = this.getMpmItemModeloBasicoDietaDAO().pesquisarParaVO(
				modeloBasicoPrescricaoSeq, modeloBasicoDietaSeq);
		if (itens != null && !itens.isEmpty()) {
			for (MpmItemModeloBasicoDieta item : itens) {
				MpmItemModeloBasicoDietaVO vo = new MpmItemModeloBasicoDietaVO();
				
				MpmItemModeloBasicoDietaId id = new MpmItemModeloBasicoDietaId();
				id.setModeloBasicoDietaSeq(item.getId().getModeloBasicoDietaSeq());
				id.setModeloBasicoPrescricaoSeq(item.getId().getModeloBasicoPrescricaoSeq());
				id.setTipoItemDietaSeq(item.getId().getTipoItemDietaSeq());
				vo.setId(id);
				
				vo.setModeloBasicoDieta(item.getModeloBasicoDieta());
				vo.setTipoItemDieta(item.getTipoItemDieta());
				vo.setTipoFrequenciaAprazamento(item.getTipoFrequenciaAprazamento());
				vo.setServidor(item.getServidor());
				vo.setCriadoEm(item.getCriadoEm());
				vo.setQuantidade(item.getQuantidade());
				vo.setFrequencia(item.getFrequencia());
				vo.setNumeroVezes(item.getNumeroVezes());
				retorno.add(vo);
			}
		}
		return retorno;
	}

	/**
	 * Método para incluir um novo item de modelo de dieta
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	public void inserir(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {

		if (itemDieta == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_DIETA_NAO_INFORMADO);
		}
		
		itemDieta.setServidor(getServidorLogadoFacade().obterServidorLogado());

		// valida associacao com o pai(modelo), se não existe inclui
		MpmModeloBasicoDieta modelo = validaAssociacao(itemDieta
				.getModeloBasicoDieta());
		if (modelo == null) {
			modelo = itemDieta.getModeloBasicoDieta();
			modelo.setServidor(getServidorLogadoFacade().obterServidorLogado());
			this.inserir(modelo);
		}
		itemDieta.setModeloBasicoDieta(modelo);

		// valida associação com o item
		itemDieta.setTipoItemDieta(validaAssociacao(itemDieta
				.getTipoItemDieta()));

		// validações de negócio
		this.valida(itemDieta);

		// validações do item selecionado
		validaItem(itemDieta);

		// cria o id do item
		// composto de modelo basico prescricao, modelo basico dieta e tipo item
		// dieta
		MpmItemModeloBasicoDietaId id = new MpmItemModeloBasicoDietaId(
				itemDieta.getModeloBasicoDieta().getModeloBasicoPrescricao()
						.getSeq(), itemDieta.getModeloBasicoDieta().getId()
						.getSeq(), itemDieta.getTipoItemDieta().getSeq());
		itemDieta.setId(id);

		// atributos de auditoria
		itemDieta.setServidor(this.validaAssociacao(itemDieta.getServidor()));
		itemDieta.setCriadoEm(new Date());

		// salva no banco
		this.getMpmItemModeloBasicoDietaDAO().persistir(itemDieta);
		this.getMpmItemModeloBasicoDietaDAO().flush();

	}

	/**
	 * Altera um item de dieta do modelo básico
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	public void alterar(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		this.valida(itemDieta);
		this.getMpmItemModeloBasicoDietaDAO().merge(itemDieta);
		this.getMpmItemModeloBasicoDietaDAO().flush();
	}

	/**
	 * Exclui um item de dieta do modelo básico
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		
		MpmItemModeloBasicoDieta itemDietaExcluir = this.getMpmItemModeloBasicoDietaDAO()
				.obterPorChavePrimaria(itemDieta.getId());
		
		// RN06 - Valida mesmo servidor
		validaServidor(itemDietaExcluir.getServidor(), itemDietaExcluir
				.getModeloBasicoDieta().getModeloBasicoPrescricao());

		// RN13 - Valida a permanência de no mínimo um item
		validaExclusaoMinimoUmItem(itemDietaExcluir);

		this.getMpmItemModeloBasicoDietaDAO().remover(itemDietaExcluir);
		this.getMpmItemModeloBasicoDietaDAO().flush();
	}

	/**
	 * Verifica Modelo de Dieta associado ao Item de Dieta caso não exista
	 * inclui novo
	 * 
	 * @param modeloBasicoDieta
	 * @return modeloBasicoDieta
	 * @throws ApplicationBusinessException
	 */
	private MpmModeloBasicoDieta validaAssociacao(
			MpmModeloBasicoDieta modeloBasicoDieta) throws ApplicationBusinessException {

		MpmModeloBasicoDieta result = null;

		if (modeloBasicoDieta == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_DIETAPAI_NAO_INFORMADO);
		}

		// se possui dieta associada carrega
		if (modeloBasicoDieta.getId() != null
				&& modeloBasicoDieta.getId().getModeloBasicoPrescricaoSeq() != null
				&& modeloBasicoDieta.getId().getSeq() != null) {
			result = this.getMpmModeloBasicoDietaDAO().obterPorChavePrimaria(
					modeloBasicoDieta.getId());
		}
		return result;
	}

	/**
	 * Valida o Tipo de Item de Dieta associado ao Item
	 * 
	 * @param tipoItemDieta
	 * @return tipoItemDieta
	 * @throws ApplicationBusinessException
	 */
	private AnuTipoItemDieta validaAssociacao(AnuTipoItemDieta tipoItemDieta)
			throws ApplicationBusinessException {

		AnuTipoItemDieta result = null;

		if (tipoItemDieta == null || tipoItemDieta.getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_DIETA_NAO_ENCONTRADO);
		}

		// se possui modelo associado carrega
		result = this.getNutricaoFacade().obterAnuTipoItemDietaPorChavePrimaria(
				tipoItemDieta.getSeq());

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_DIETA_NAO_ENCONTRADO);
		}
		return result;
	}

	/**
	 * Regras de Negócio de Inclusão/Alteração do Item de Dieta
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void valida(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		// CK1
		validaQuantidade(itemDieta.getQuantidade());

		// CK2
		validaFrequencia(itemDieta.getFrequencia());

		// RN06 - Valida mesmo servidor
		validaServidor(itemDieta.getServidor(), itemDieta
				.getModeloBasicoDieta().getModeloBasicoPrescricao());

		// RN08 - Valida tipo de frequencia
		validaTipoFrequencia(itemDieta);
	}

	/**
	 * Regras de negócio referentes ao AnuTipoItemDieta selecionado - apenas
	 * inclusão!
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void validaItem(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		// RN09 - Valida item de dieta
		validaItemDieta(itemDieta);

		// RN10 - Não permitir inclusão de itens iguais na mesma dieta
		validaCadastroUnicoDoItemDieta(itemDieta);

		// RN12 - Tipo de item único na dieta
		validaItemUnico(itemDieta);
	}

	/**
	 * Valida se o servidor logado é o mesmo que criou o modelo básico de
	 * prescrição.
	 * 
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	public void validaServidor(RapServidores servidor,
			MpmModeloBasicoPrescricao modeloBasicoPrescricao)
			throws ApplicationBusinessException {

		MpmModeloBasicoPrescricao modelo = null;

		if (modeloBasicoPrescricao == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_MODELOPAI_NAO_INFORMADO);
		}

		// se possui modelo associado carrega
		if (modeloBasicoPrescricao != null
				&& modeloBasicoPrescricao.getSeq() != null) {
			modelo = this.getMpmModeloBasicoPrescricaoDAO()
					.obterPorChavePrimaria(modeloBasicoPrescricao.getSeq());
			
			if (!Objects.equals(servidor, modelo.getServidor())) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_MODELO_OUTRO_SERVIDOR);
			}
		}

	}

	/**
	 * Valida consistência de dados de um item de dieta
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validaItemDieta(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {

		AnuTipoItemDieta anuTipoItemDieta = this.getNutricaoFacade()
				.obterAnuTipoItemDietaPorChavePrimaria(itemDieta.getTipoItemDieta().getSeq());

		Long numVezesApraz = null;

		// verifica se tipo item dieta existe e está ativo
		if (anuTipoItemDieta == null
				|| DominioSituacao.I.equals(anuTipoItemDieta.getIndSituacao())) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_NAO_EXISTE_ITEM_DIETA_ATIVO);
		}

		// Validação de Quantidade
		if (DominioRestricao.O.equals(itemDieta.getTipoItemDieta()
				.getIndDigitaQuantidade())) {
			if (itemDieta.getQuantidade() == null
					|| itemDieta.getQuantidade().equals(BigDecimal.ZERO)) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_QTD_OBRIGATORIA);
			}
		} else {
			if (DominioRestricao.N.equals(itemDieta.getTipoItemDieta()
					.getIndDigitaQuantidade())) {
				if (itemDieta.getQuantidade() != null) {
					throw new ApplicationBusinessException(
							ManterDietasModeloBasicoONExceptionCode.MENSAGEM_NAO_INFORMAR_QTD);
				}
			}
		}

		// Validação de Aprazamento
		if (DominioRestricao.O.equals(itemDieta.getTipoItemDieta()
				.getIndDigitaAprazamento())) {
			if (itemDieta.getTipoFrequenciaAprazamento() == null) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_OBRIGATORIA);
			}
		} else {
			if (DominioRestricao.N.equals(itemDieta.getTipoItemDieta()
					.getIndDigitaAprazamento())) {
				if (itemDieta.getTipoFrequenciaAprazamento() != null) {
					throw new ApplicationBusinessException(
							ManterDietasModeloBasicoONExceptionCode.MENSAGEM_NAO_INFORMAR_TIPO_FREQUENCIA);
				}
			}
		}

		// Validação se TIPO DE FREQUENCIA informada
		if (itemDieta.getTipoFrequenciaAprazamento() != null) {

			numVezesApraz = this
					.calculoNumeroVezesAprazamento24Horas(itemDieta);

			if (itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento() != null
					&& itemDieta.getTipoItemDieta()
							.getTipoFrequenciaAprazamento() == itemDieta
							.getTipoFrequenciaAprazamento()
					&& itemDieta.getTipoItemDieta().getFrequencia() == itemDieta
							.getFrequencia()
					&& itemDieta.getNumeroVezes() == null) {

				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ESPECIFICAR_NUM_VEZES,
						numVezesApraz);
			}
		}

		// Número de vezes somente para frequencia padrão
		if (itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento() == null
				&& itemDieta.getNumeroVezes() != null) {

			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_VEZES_APRAZAMENTO_PADRAO);
		}

		// Número de vezes somente igual ao padrão
		if (itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento() != null
				&& !itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento()
						.equals(itemDieta.getTipoFrequenciaAprazamento())
				&& itemDieta.getTipoItemDieta().getFrequencia() != itemDieta
						.getFrequencia() && itemDieta.getNumeroVezes() != null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_VEZES_APRAZAMENTO_IGUAL_PADRAO,
					itemDieta.getTipoItemDieta().getTipoFrequenciaAprazamento()
							.getDescricao());
		}

		if (itemDieta.getNumeroVezes() != null) {
			if ((itemDieta.getNumeroVezes() > numVezesApraz)
					|| (itemDieta.getNumeroVezes() < 1)) {

				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ESPECIFICAR_NUM_VEZES_RN11,
						numVezesApraz);
			}
		}
	}

	/**
	 * Verifica se item já existe para a dieta e para o modelo basico
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void validaCadastroUnicoDoItemDieta(
			MpmItemModeloBasicoDieta itemDieta) throws ApplicationBusinessException {

		MpmItemModeloBasicoDietaId itemModeloBasicoDietaId = new MpmItemModeloBasicoDietaId(
				itemDieta.getModeloBasicoDieta().getModeloBasicoPrescricao()
						.getSeq(), itemDieta.getModeloBasicoDieta().getId()
						.getSeq(), itemDieta.getTipoItemDieta().getSeq());

		if (this.getMpmItemModeloBasicoDietaDAO().obterPorChavePrimaria(
				itemModeloBasicoDietaId) != null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ITEM_DIETA_JA_CADASTRADO,
					itemDieta.getTipoItemDieta().getDescricao());
		}
	}

	/**
	 * RN12–Permitir somente um item de DIETA, se
	 * Anu_Tipo_Item_Dietas.Ind_Item_Unico = 'S'
	 * 
	 * @param MpmItemModeloBasicoDieta
	 * @throws ApplicationBusinessException
	 */
	private void validaItemUnico(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {

		// se este item possuir indicador de item único = S e ja existe outro
		if (itemDieta.getTipoItemDieta().getIndItemUnico()) {
			Long vcont = 0l;

			vcont = this.getMpmItemModeloBasicoDietaDAO()
					.countItemModeloBasicoDieta(
							itemDieta.getModeloBasicoDieta()
									.getModeloBasicoPrescricao().getSeq(),
							itemDieta.getModeloBasicoDieta().getId().getSeq());
			if (vcont > 0) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ITEM_DIETA_UNICO_RN12,
						itemDieta.getTipoItemDieta().getDescricao());
			}
		}
		// se ja existe no banco um item com indicador de unico
		for (MpmItemModeloBasicoDieta itemExistente : this
				.getMpmItemModeloBasicoDietaDAO().pesquisar(
						itemDieta.getModeloBasicoDieta()
								.getModeloBasicoPrescricao().getSeq(),
						itemDieta.getModeloBasicoDieta().getId().getSeq())) {
			if (itemExistente.getTipoItemDieta().getIndItemUnico()) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_ITEM_DIETA_UNICO_RN12,
						itemExistente.getTipoItemDieta().getDescricao());
			}
		}

	}

	/**
	 * RN13– Exclusão item - No mínimo um item
	 * 
	 * @param MpmItemModeloBasicoDieta
	 * @throws ApplicationBusinessException
	 */
	private void validaExclusaoMinimoUmItem(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {

		Long vcont = 0l;
		vcont = this.getMpmItemModeloBasicoDietaDAO()
				.countItemModeloBasicoDieta(
						itemDieta.getModeloBasicoDieta()
								.getModeloBasicoPrescricao().getSeq(),
						itemDieta.getModeloBasicoDieta().getId().getSeq());
		if (vcont == 1) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_NAO_EXCLUIR_ULTIMO_ITEM);
		}
	}

	/**
	 * Calcula o número de vezes que um aprazamento
	 * 
	 * @param tipoFrequenciaAprazamento
	 */
	private Long calculoNumeroVezesAprazamento24Horas(
			MpmItemModeloBasicoDieta itemDieta) {

		Long quantidade = null;

		MpmTipoFrequenciaAprazamento tipoFreqAprazamento = getMpmTipoFrequenciaAprazamentoDAO()
				.obterPorChavePrimaria(
						itemDieta.getTipoFrequenciaAprazamento().getSeq());

		if (tipoFreqAprazamento.getIndFormaAprazamento().equals(
				DominioFormaCalculoAprazamento.C)) {
			quantidade = 999l;
			return quantidade;
		}

		if (tipoFreqAprazamento.getIndFormaAprazamento().equals(
				DominioFormaCalculoAprazamento.I)) {

			Double numVezes = itemDieta.getFrequencia()
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
				quantidade = (long) ((int) 24 * (itemDieta.getFrequencia() / tipoFreqAprazamento
						.getFatorConversaoHoras().doubleValue()));
			} else {
				quantidade = getMpmAprazamentoFrequenciasDAO()
						.pesquisarQuantidadeTipoFreequencia(
								itemDieta.getTipoFrequenciaAprazamento()
										.getSeq());

				if (quantidade == null || quantidade == 0) {
					quantidade = 1l;
				}
			}
		}

		return quantidade;
	}

	// DIETA
	/**
	 * Método para salvar um novo modelo de dieta
	 * 
	 * @param modeloBasicoDieta
	 * @throws ApplicationBusinessException
	 */
	private void inserir(MpmModeloBasicoDieta modeloBasicoDieta)
			throws ApplicationBusinessException {
		if (modeloBasicoDieta == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_DIETA_NAO_INFORMADO);
		}
		// valida associações
		modeloBasicoDieta
				.setModeloBasicoPrescricao(this
						.validaAssociacao(modeloBasicoDieta
								.getModeloBasicoPrescricao()));
		modeloBasicoDieta.setServidor(this.validaAssociacao(modeloBasicoDieta
				.getServidor()));

		// validações de negócio
		//this.valida(modeloBasicoDieta);

		modeloBasicoDieta.setId(this
				.setMpmModeloBasicoDietaId(modeloBasicoDieta
						.getModeloBasicoPrescricao().getSeq()));

		// fixo porque o atributo não é mais utilizado no agh
		modeloBasicoDieta.setIndAvalNutricionista(false);

		this.getMpmModeloBasicoDietaDAO().persistir(modeloBasicoDieta);
		this.getMpmModeloBasicoDietaDAO().flush();
	}

	/**
	 * cria novo id apontando para o modelo básico associado à dieta
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @return id
	 */
	public MpmModeloBasicoDietaId setMpmModeloBasicoDietaId(
			Integer modeloBasicoPrescricaoSeq) {
		MpmModeloBasicoDietaId id = new MpmModeloBasicoDietaId();
		id.setModeloBasicoPrescricaoSeq(modeloBasicoPrescricaoSeq);
		return id;
	}

	public boolean isAlterouDieta(MpmModeloBasicoDieta dieta) {
		// se ja tem seq, ja foi salvo no banco
		if (dieta != null && dieta.getId() != null
				&& dieta.getId().getSeq() != null) {
			// busca o antigo q esta no banco
			MpmModeloBasicoDieta old = this.getMpmModeloBasicoDietaDAO()
					.obterOld(dieta);
			// se encontrou antigo
			if (old != null && old.getId() != null
					&& old.getId().getSeq() != null) {
				// e a observação mudou=ALTERAÇÃO PENDENTE
				if (!StringUtils.equals(dieta.getObservacao(), old
						.getObservacao())) {
					return true;
				}
			}
			// se não tem nada no banco e a observação foi preenchida =
			// ALTERAÇÃO PENDENTE
		} else if (dieta.getObservacao() != null) {
			return true;
		}
		return false;
	}

	/**
	 * @param modeloBasicoDieta
	 * @throws ApplicationBusinessException
	 */
	public void alterar(MpmModeloBasicoDieta modeloBasicoDieta)
			throws ApplicationBusinessException {
		if (modeloBasicoDieta == null
				|| modeloBasicoDieta.getId() == null
				|| modeloBasicoDieta.getId().getModeloBasicoPrescricaoSeq() == null
				|| modeloBasicoDieta.getId().getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_DIETA_NAO_INFORMADO);
		}
		this.getMpmModeloBasicoDietaDAO().merge(modeloBasicoDieta);
		this.getMpmModeloBasicoDietaDAO().flush();
	}

	/**
	 * Verifica Modelo Básico associado ao item
	 * 
	 * @param modeloBasico
	 * @return modeloBasico
	 * @throws ApplicationBusinessException
	 */
	public MpmModeloBasicoPrescricao validaAssociacao(
			MpmModeloBasicoPrescricao modeloBasico) throws ApplicationBusinessException {

		MpmModeloBasicoPrescricao result = null;

		if (modeloBasico == null || modeloBasico.getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_MODELOPAI_NAO_INFORMADO);
		}

		result = this.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(
				modeloBasico.getSeq());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_DIETAPAI_NAO_ENCONTRADO);
		}

		return result;
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

	// OUTROS
	/**
	 * Obtem itens de modelo basico de dieta pelo ID
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param modeloBasicoDietaSeq
	 */
	public MpmModeloBasicoDieta obterModeloBasicoDieta(
			Integer modeloBasicoPrescricaoSeq, Integer seq) {

		MpmModeloBasicoDietaId id = new MpmModeloBasicoDietaId();
		id.setModeloBasicoPrescricaoSeq(modeloBasicoPrescricaoSeq);
		id.setSeq(seq);
		return this.getMpmModeloBasicoDietaDAO().obterPorChavePrimaria(id);
	}

	/**
	 * Obtem lista de tipos de item de dieta ativos.
	 * 
	 * @return
	 */
	public List<AnuTipoItemDieta> obterTiposItemDieta(Object itemDietaPesquisa) {
		return this.getNutricaoFacade().obterTiposItemDieta(
				itemDietaPesquisa);
	}

	public Long obterTiposItemDietaCount(Object idOuDescricao) {
		return this.getNutricaoFacade().obterTiposItemDietaCount(idOuDescricao);
	}
	
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamento(
			String strPesquisa) {
		return getMpmTipoFrequenciaAprazamentoDAO()
				.obterListaTipoFrequenciaAprazamento(strPesquisa);
	}

	public Long obterListaTipoFrequenciaAprazamentoCount(String strPesquisa) {
		return getMpmTipoFrequenciaAprazamentoDAO().obterListaTipoFrequenciaAprazamentoCount(strPesquisa);
	}
	
	/**
	 * Valida tipo de frequência
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void validaTipoFrequencia(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {

		if (itemDieta.getFrequencia() != null
				&& itemDieta.getTipoFrequenciaAprazamento() == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_INFORME_FREQUENCIA_E_TIPO);
		}

		if (itemDieta.getTipoFrequenciaAprazamento() != null) {

			itemDieta.setTipoFrequenciaAprazamento(validaAssociacao(itemDieta
					.getTipoFrequenciaAprazamento()));

			if (!itemDieta.getTipoFrequenciaAprazamento().getIndSituacao()
					.isAtivo()) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_ATIVO);
			}

			if (itemDieta.getTipoFrequenciaAprazamento()
					.getIndDigitaFrequencia() == true
					&& (itemDieta.getFrequencia() == null || itemDieta
							.getFrequencia() == 0)) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA);
			}

			if (itemDieta.getTipoFrequenciaAprazamento()
					.getIndDigitaFrequencia() == false
					&& itemDieta.getFrequencia() != null) {
				throw new ApplicationBusinessException(
						ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_PERMITE_INFORMACAO);
			}
		}
	}

	/**
	 * Frequencia se informada deve ser maior que zero
	 * 
	 * @param frequencia
	 * @throws ApplicationBusinessException
	 */
	private void validaFrequencia(Short frequencia) throws ApplicationBusinessException {
		if (frequencia != null && frequencia.intValue() <= 0) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_FREQUENCIA_MAIOR_ZERO);
		}
	}

	/**
	 * Quantidade se informada deve ser maior que zero
	 * 
	 * @param quantidade
	 * @throws ApplicationBusinessException
	 */
	private void validaQuantidade(BigDecimal quantidade)
			throws ApplicationBusinessException {
		if (quantidade != null && quantidade.intValue() <= 0) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_QUANTIDADE_MAIOR_ZERO);
		}
	}

	/**
	 * Valida o Tipo de Frequencia de Aprazamento
	 * 
	 * @param mpmTipoFrequenciaAprazamento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public MpmTipoFrequenciaAprazamento validaAssociacao(
			MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento)
			throws ApplicationBusinessException {

		MpmTipoFrequenciaAprazamento result = null;

		if (mpmTipoFrequenciaAprazamento != null) {
			result = this.getMpmTipoFrequenciaAprazamentoDAO()
					.obterPorChavePrimaria(
							mpmTipoFrequenciaAprazamento.getSeq());
		}

		if (result == null || result.getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterDietasModeloBasicoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_CADASTRADO);
		}

		return result;
	}
	
	public MpmTipoFrequenciaAprazamento obterTipoFrequenciaAprazamento(
			Short seq) {
		return getMpmTipoFrequenciaAprazamentoDAO().obterPorChavePrimaria(seq);
				
	}

	// getters and setters
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected MpmModeloBasicoPrescricaoDAO getMpmModeloBasicoPrescricaoDAO() {
		return mpmModeloBasicoPrescricaoDAO;
	}

	protected MpmModeloBasicoDietaDAO getMpmModeloBasicoDietaDAO() {
		return mpmModeloBasicoDietaDAO;
	}

	protected MpmItemModeloBasicoDietaDAO getMpmItemModeloBasicoDietaDAO() {
		return mpmItemModeloBasicoDietaDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected INutricaoFacade getNutricaoFacade() {
		return this.nutricaoFacade;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected MpmAprazamentoFrequenciasDAO getMpmAprazamentoFrequenciasDAO() {
		return mpmAprazamentoFrequenciasDAO;
	}
	
}
