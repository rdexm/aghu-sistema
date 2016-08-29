package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoDietaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterPrescricaoDietaController extends ActionController {

	private static final long serialVersionUID = -3648646154737926313L;
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoDietaController.class);
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "manterPrescricaoMedica";
	
	public enum ManterPrescricaoDietaControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ITEM_DIETA_JA_FOI_INSERIDO, MENSAGEM_SIGLA_TIPO_FREQUENCIA_NAO_ENCONTRADA, MENSAGEM_TIPO_ITEM_DIETA_OBRIGATORIO, MENSAGEM_SIGLA_TIPO_FREQUENCIA_NAO_INFORMADA, MENSAGEM_SUCESSO_ALTERAR_PRESCRICAO_DIETA;
	}

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private INutricaoFacade nutricaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	//@In Todo virou parâmetro
	private PrescricaoMedicaVO prescricaoMedicaVO;

	// formulario mestre
	private String observacao;
	private boolean bombaInfusao;

	private int idConversacaoAnterior;

	// formulario item
	private ItemPrescricaoDietaVO formulario = new ItemPrescricaoDietaVO();

	// condições definidas a partir do atendimento do paciente
	private AghUnidadesFuncionais unidadeFuncional = null;
	private boolean neonatologia = false;
	private boolean adulto = false;
	private boolean pediatrico = false;

	// mestre
	private MpmPrescricaoDieta prescricaoDieta = new MpmPrescricaoDieta();

	// lista de itens para persistir(adicionados)
	// private Set<ItemPrescricaoDietaVO> itensParaIncluir = new
	// HashSet<ItemPrescricaoDietaVO>();
	// lista de itens para alterar(alterados)
	private Set<ItemPrescricaoDietaVO> itensParaAlterar = new HashSet<ItemPrescricaoDietaVO>();
	// lista de itens para excluir
	private Set<ItemPrescricaoDietaVO> itensParaExcluir = new HashSet<ItemPrescricaoDietaVO>();

	// indica que está em modo de edição do item
	private boolean altera;
	// indica que está em modo de edição da dieta
	private boolean editaMestre;
	// indica que já foi feita alguma modificação.
	private boolean modificado;

	// itens gerenciados
	private List<ItemPrescricaoDietaVO> itensDieta = new ArrayList<ItemPrescricaoDietaVO>();

	// parâmetros do mestre
	private Integer atdSeq;
	private Long seq;

	private boolean frequenciaPadrao = false;

	// utilizados para Pendencia na Edição.
	private boolean modificadoEdicao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Realiza todas as inicializações necessárias para a apresentação da tela
	 * no modo de inclusão ou edição.
	 */
	public void iniciar() {
	 

		
			this.pediatrico = false;
			this.adulto = false; 
			this.editaMestre = false;
			this.itensParaAlterar.clear(); 
			this.itensParaExcluir.clear();
			this.itensDieta.clear();
			this.observacao = null;
			this.bombaInfusao = false;
			this.limpar();
			
			// unidade do atendimento
			AghAtendimentos atendimento = this.aghuFacade
					.obterAtendimentoPeloSeq(this.prescricaoMedicaVO.getId().getAtdSeq());
			this.unidadeFuncional = atendimento.getUnidadeFuncional();
	
			// verifica se unidade vigente é neonatologia através das
			// caracteristicas de unidade 'Unid Neonatologia'
			neonatologia = this.aghuFacade
					.validarCaracteristicaDaUnidadeFuncional(
							this.unidadeFuncional.getSeq(),
							ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA);

			if (neonatologia || this.prescricaoMedicaVO.getIndPacPediatrico()) {
				pediatrico = true;
			} else {
				adulto = true;
			}
	
			// se informado os parâmetros entra como edição
			if (this.atdSeq != null && this.seq != null) {
				this.editaMestre = true;
			}
			
	
			if (this.editaMestre) {
				this.prescricaoDieta = this.prescricaoMedicaFacade
						.obterPrescricaoDieta(new MpmPrescricaoDietaId(this.atdSeq,
								this.seq));
				
				//controle caso o item tenha sido excluído por outro usuário
				if(prescricaoDieta == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					// iniciando em inserir, cria objeto mestre
					this.prescricaoDieta = new MpmPrescricaoDieta(
							new MpmPrescricaoDietaId(atendimento.getSeq(), null));  
					this.prescricaoDieta
							.setIndPendente(DominioIndPendenteItemPrescricao.P);
					
				} else{
				
					Set<MpmItemPrescricaoDieta> itens = this.prescricaoMedicaFacade
							.obterItensPrescricaoDieta(this.prescricaoDieta);
					this.itensDieta = this.getValueObjets(itens);
					this.observacao = this.prescricaoDieta.getObservacao();
					this.bombaInfusao = this.prescricaoDieta.getIndBombaInfusao();
					LOG.debug("editando dieta #0 "+ this.prescricaoDieta);
				}
			} else {
				// iniciando em inserir, cria objeto mestre
				this.prescricaoDieta = new MpmPrescricaoDieta(
						new MpmPrescricaoDietaId(atendimento.getSeq(), null));  
				this.prescricaoDieta
						.setIndPendente(DominioIndPendenteItemPrescricao.P);
			}
	
	}

	/**
	 * Adiciona um item à lista de itens a ser persistidos posteriormente.<br>
	 * Aqui uma instância de {@link MpmItemPrescricaoDieta} é criada com os
	 * dados do formulário, representado pela classe
	 * {@link ItemPrescricaoDietaVO}, esta instância é validada com as regras de
	 * negócio de inclusão, o objeto validado é colocado no formulário e
	 * adicionado a lista para persistir posteriormente e na lista de itens para
	 * serem apresentados na tela.
	 */
	public void adicionar() {
		try {
			// criar persistente a partir dos dados do formulario
			MpmItemPrescricaoDieta novo = this.criar(this.formulario);

			// setando chave composta
			novo.getId().setPdtAtdSeq(this.prescricaoDieta.getId().getAtdSeq());
			novo.getId().setPdtSeq(this.prescricaoDieta.getId().getSeq());
			novo.setPrescricaoDieta(this.prescricaoDieta);

			if (this.exist(novo.getTipoItemDieta())) {
				throw new ApplicationBusinessException(
						ManterPrescricaoDietaControllerExceptionCode.MENSAGEM_ITEM_DIETA_JA_FOI_INSERIDO);
			}

			// aplicar as regras de negócio
			this.prescricaoMedicaFacade.preValidar(novo, itensDieta);

			this.formulario.setItemPrescricaoDieta(novo);
			this.formulario.setPdtAtdSeq(this.prescricaoDieta.getId().getAtdSeq());
			this.formulario.setPdtSeq(this.prescricaoDieta.getId().getSeq());
			this.formulario.setTidSeq(this.formulario.getTipoItem().getSeq());

			// this.itensParaIncluir.add(this.formulario);

			// adicionar item na lista
			this.itensDieta.add(this.formulario);

			this.modificado = true;
			this.modificadoEdicao = false;

			// limpa formulário
			this.limparFormulario();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	/**
	 * Altera o pojo e registra o item para posterior alteração.<br>
	 * O item já pode estar no banco de dados, neste caso é adicionado a uma
	 * lista que posteriormente gravará as modificações.<br>
	 * Se é um item ainda não persistido, apenas altera o pojo e faz as
	 * validações.
	 * 
	 */
	public void alterar() {
		try {

			this.modificado = true;
			this.modificadoEdicao = false;
			
			// altera os dados do clone do pojo
			MpmItemPrescricaoDieta alterado = this.alterar(this.formulario,
					true);

			// aplicar as regras de negócio
			this.prescricaoMedicaFacade.preValidar(alterado, null);

			// passando nas regras altera o original
			this.alterar(this.formulario, false);

			// se já está persistido,
			// adiciona na lista para enviar para o banco de dados
			if (this.formulario.isPersistido()) {
				this.itensParaAlterar.add(this.formulario);
			}


			// volta o clone para lista substituindo o original
			// remove pela chave, não pela instância
			this.itensDieta.remove(this.formulario);
			// adiciona nova instância
			this.itensDieta.add(this.formulario);

			this.limparFormulario();
			this.resetEdicao();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	/**
	 * Grava no banco de dados todas as modificações feitas na dieta(inclusões,
	 * alterações e exclusões de itens).<br>
	 * Após a gravação com sucesso entra em modo de edição atualizando com todos
	 * os dados gravados anteriormente.
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void gravar() throws CloneNotSupportedException {
		if (this.verificaRequiredFrequencia()
				&& this.formulario.getFrequencia() == null) {
			apresentarMsgNegocio(Severity.ERROR,
					"CAMPO_OBRIGATORIO", "Frequência");
		} else {
			try {
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção caputada:", e);
				}
				
				if (this.editaMestre) {
					// altera dieta
					this.prescricaoDieta.setObservacao(this.observacao);
					this.prescricaoDieta.setIndBombaInfusao(this.bombaInfusao);
					List<MpmItemPrescricaoDieta> alterados = this.getItensParaAlterar();
					List<MpmItemPrescricaoDieta> novos = this.getItensParaIncluir();
					List<MpmItemPrescricaoDieta> excluidos = this.getItensParaExcluir();

					this.prescricaoMedicaFacade.gravar(this.prescricaoDieta,
							novos, alterados, excluidos, nomeMicrocomputador);
					apresentarMsgNegocio(
							Severity.INFO,
							"MENSAGEM_SUCESSO_PRESCRICAO_DIETA_ALTERADA");
				} else {
					// inserir dieta
					this.prescricaoDieta
							.setPrescricaoMedica(
									this.prescricaoMedicaVO.getPrescricaoMedica());
					List<MpmItemPrescricaoDieta> novos = this
							.getItensParaIncluir();
					this.prescricaoDieta.setObservacao(this.observacao);
					this.prescricaoDieta.setIndBombaInfusao(this.bombaInfusao);
					this.prescricaoMedicaFacade.gravar(this.prescricaoDieta,
							novos, nomeMicrocomputador);
					apresentarMsgNegocio(
							Severity.INFO,
							"MENSAGEM_SUCESSO_PRESCRICAO_DIETA_INCLUIDA");
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return;
			}

			MpmPrescricaoDieta posterior = this.prescricaoMedicaFacade
					.obterPosterior(this.prescricaoDieta);
			if (posterior != null) {
				LOG.debug("encontrou dieta posterior #0"+ posterior);
				this.prescricaoDieta = posterior;
			}

			// esvazia as listas quando tudo salvo
			// this.itensParaIncluir = new HashSet<ItemPrescricaoDietaVO>();
			this.itensParaAlterar = new HashSet<ItemPrescricaoDietaVO>();
			this.itensParaExcluir = new HashSet<ItemPrescricaoDietaVO>();

			// limpa formulário
			this.limpar();
			// depois de gravado não há mais modificações pendentes
			this.modificado = false;
			this.modificadoEdicao = false;


			// se inclusão, entrar no modo de edição
			// se alteração, permanece neste modo

			// troca para modo de edição
			if (!this.editaMestre) {
				this.editaMestre = true;
			}

			this.atdSeq = this.prescricaoDieta.getId().getAtdSeq();
			this.seq = this.prescricaoDieta.getId().getSeq();

			// reinicia com dados novos
			this.iniciar();
		}
	}
	
	public void verificaFrequenciaEEdicao() {
		this.setModificadoEdicao(true);
		verificarFrequencia();
	}
	
	
	public void changeField(){
		this.setModificadoEdicao(true);
	}
	
	
	/**Prepara para Edição - usado pelo PENDÊNCIA EDIÇÃO
	 *
	 *@param item 
	 *@throws CloneNotSupportedException
	 */
	public void preparaEditar(ItemPrescricaoDietaVO item) throws CloneNotSupportedException{
		if (!isModificadoEdicao()){
			this.editar(item);
		}
	}
	
	/**
	 * Marca o item para edição e coloca os dados no formulário.
	 * 
	 * @param item
	 * @throws CloneNotSupportedException
	 */
	public void editar(ItemPrescricaoDietaVO item)
			throws CloneNotSupportedException {
		this.resetEdicao();
		item.setEdicao(true);
		this.formulario = (ItemPrescricaoDietaVO) item.clone();
		this.altera = true;
		this.frequenciaPadrao = this.isFrequenciaPadrao();
		this.modificadoEdicao = false;
		
	}

	/**
	 * Registra o item para exclusão posteriormente.<br>
	 * O item já pode estar no banco de dados, neste caso é adicionado a uma
	 * lista que posteriormente fará a exclusão.<br>
	 * Se é um item ainda não persistido, apenas retira das listas.
	 * 
	 * @param item
	 */
	public void excluir(ItemPrescricaoDietaVO item) {
		// remove de todas as listas
		this.itensDieta.remove(item);
		this.itensParaAlterar.remove(item);
		// this.itensParaIncluir.remove(item);

		// adiciona na lista de excluidos, apenas se já está persistido
		if (item.isPersistido()) {
			this.itensParaExcluir.add(item);
		}

		this.limparFormulario();
		this.resetEdicao();

		this.modificado = true;
		this.modificadoEdicao = true;

	}

	/**
	 * Retorna true se o tipo já foi incluido para a prescrição.
	 * 
	 * @param anuTipoItemDieta
	 * @return
	 */
	private boolean exist(AnuTipoItemDieta tipo) {
		for (ItemPrescricaoDietaVO vo : this.itensDieta) {
			if (tipo.equals(vo.getItemPrescricaoDieta().getTipoItemDieta())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retira todos os itens do modo de edição.
	 */
	private void resetEdicao() {
		for (ItemPrescricaoDietaVO vo : this.itensDieta) {
			vo.setEdicao(false);
		}
	}

	/**
	 * Retorna os objetos persistentes armazenados nos VOs fornecidos, que devem
	 * ser gravados no banco de dados.
	 * 
	 * @param itens
	 *            itens pendentes de inclusão
	 * @return itens para inclusao
	 */
	private List<MpmItemPrescricaoDieta> getItensParaIncluir() {
		List<MpmItemPrescricaoDieta> result = new ArrayList<MpmItemPrescricaoDieta>();
		for (ItemPrescricaoDietaVO i : this.itensDieta) {
			if (!i.isPersistido()) {
				result.add(i.getItemPrescricaoDieta());
			}
		}

		return result;
	}

	/**
	 * Retorna os objetos persistentes armazenados nos VOs fornecidos, com as
	 * propriedades modificaveis alteradas que devem ser gravados no banco de
	 * dados.
	 * 
	 * @param itens
	 *            itens pendentes de alteracao
	 * @return itens para alteracao
	 */
	private List<MpmItemPrescricaoDieta> getItensParaAlterar() {
		List<MpmItemPrescricaoDieta> result = new ArrayList<MpmItemPrescricaoDieta>();
		for (ItemPrescricaoDietaVO vo : this.itensParaAlterar) {
			if (vo.isPersistido()) {
				// seta propriedades modificaveis
				MpmItemPrescricaoDieta item = vo.getItemPrescricaoDieta();
				item.setQuantidade(vo.getQuantidade());
				item.setFrequencia(vo.getFrequencia());
				item.setNumVezes(vo.getNumVezes());
				item.setTipoFreqAprazamento(vo.getTipoAprazamento());

				result.add(item);
			}
		}

		return result;
	}

	private List<MpmItemPrescricaoDieta> getItensParaExcluir() {
		List<MpmItemPrescricaoDieta> result = new ArrayList<MpmItemPrescricaoDieta>();

		for (ItemPrescricaoDietaVO vo : this.itensParaExcluir) {
			if (vo.getItemPrescricaoDieta() != null) {
				result.add(vo.getItemPrescricaoDieta());
			}
		}

		return result;
	}

	/**
	 * Retorna a lista de value objects criados com os dados da lista de objetos
	 * persistentes fornecidos.<br>
	 * Os itens fornecidos já devem estar persistidos no banco de dados.
	 * 
	 * @param itens
	 * @return
	 */
	private List<ItemPrescricaoDietaVO> getValueObjets(
			Set<MpmItemPrescricaoDieta> itens) {
		List<ItemPrescricaoDietaVO> result = new ArrayList<ItemPrescricaoDietaVO>();
		try {
			for (MpmItemPrescricaoDieta i : itens) {
				ItemPrescricaoDietaVO valueObject;

				valueObject = new ItemPrescricaoDietaVO(
						(MpmItemPrescricaoDieta) i.clone());

				valueObject.setPersistido(true);
				result.add(valueObject);
			}
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe MpmItemPrescricaoDieta "
					+ "não implementa a interface Cloneable.", e);
		}
		return result;
	}

	/**
	 * Retorna a classe de persistência populada com os dados do formulário.
	 * 
	 * @param formulario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmItemPrescricaoDieta criar(ItemPrescricaoDietaVO formulario)
			throws ApplicationBusinessException {
		AnuTipoItemDieta tipoItem = formulario.getTipoItem();
		if (tipoItem == null || tipoItem.getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaControllerExceptionCode.MENSAGEM_TIPO_ITEM_DIETA_OBRIGATORIO);
		}

		MpmItemPrescricaoDietaId id = new MpmItemPrescricaoDietaId(null, null,
				tipoItem.getSeq());
		MpmItemPrescricaoDieta result = new MpmItemPrescricaoDieta(id);
		//
		result.setTipoItemDieta(tipoItem);
		result.setQuantidade(formulario.getQuantidade());
		result.setFrequencia(formulario.getFrequencia());
		result.setTipoFreqAprazamento(formulario.getTipoAprazamento());
		result.setNumVezes(formulario.getNumVezes());

		// coloca o item criado no vo fornecido
		formulario.setItemPrescricaoDieta(result);

		return result;

	}

	/**
	 * Retorna o clone do objeto aplicadas as alterações do formulário.
	 * 
	 * @param formulario
	 * @param clone
	 *            se true altera e retorna o clone
	 * @throws CloneNotSupportedException
	 */
	private MpmItemPrescricaoDieta alterar(ItemPrescricaoDietaVO formulario,
			boolean clone) throws CloneNotSupportedException {

		MpmItemPrescricaoDieta result = (MpmItemPrescricaoDieta) formulario
				.getItemPrescricaoDieta();

		if (clone) {
			result = (MpmItemPrescricaoDieta) result.clone();
		}

		result.setQuantidade(formulario.getQuantidade());
		result.setFrequencia(formulario.getFrequencia());
		result.setTipoFreqAprazamento(formulario.getTipoAprazamento());
		result.setNumVezes(formulario.getNumVezes());

		return result;
	}

	/**
	 * Limpa o formulario e retorna para modo de inclusão.
	 */
	public void limpar() {
		LOG.debug("++ begin limpar");
		this.limparFormulario();
		this.resetEdicao();
		LOG.debug("++ end");
		this.modificadoEdicao = false;
	}

	/**
	 * Cancela tudo e entra em modo de edição.
	 */
	public void cancelar() {
		// entra em modo de inclusão do mestre
		this.atdSeq = null;
		this.seq = null;
		this.editaMestre = false;

		// esvazia as listas
		// this.itensParaIncluir = new HashSet<ItemPrescricaoDietaVO>();
		this.itensParaAlterar = new HashSet<ItemPrescricaoDietaVO>();
		this.itensParaExcluir = new HashSet<ItemPrescricaoDietaVO>();
		this.itensDieta = new ArrayList<ItemPrescricaoDietaVO>();
		this.observacao = null;
		this.bombaInfusao = false;

		// limpa formulário
		this.limpar();
		// reset
		this.modificado = false;
		this.modificadoEdicao = false;


		this.iniciar();
	}

	/**
	 * Limpa o formulário e retorna para o modo de inclusão.
	 */
	public void limparFormulario() {
		this.formulario = new ItemPrescricaoDietaVO();
		this.altera = false;
		this.frequenciaPadrao = false;
	}

	/**
	 * Clicando no botão voltar.
	 * 
	 * @return
	 */
	public String voltar(Boolean force) {
		if (modificado && !force) {
			super.openDialog("modalConfirmacaoPendenciaWG");
			return null;
		} else {
			return PAGINA_MANTER_PRESCRICAO_MEDICA;	
		}
	}

	/**
	 * Retorna os tipos de itens de dieta.
	 * 
	 * @param itemDietaPesquisa
	 * @return
	 */
	public List<AnuTipoItemDieta> obterTiposItemDieta(String itemDietaPesquisa) {
		return this.returnSGWithCount(this.nutricaoFacade.obterTiposItemDieta(itemDietaPesquisa, unidadeFuncional, neonatologia, adulto,pediatrico), 
									   this.nutricaoFacade.obterTiposItemDietaCount(itemDietaPesquisa, unidadeFuncional, neonatologia, adulto,pediatrico));
		
	}

	/**
	 * Retorna true se o campo quantidade deve ser desabilidado.
	 * 
	 * @return
	 */
	public boolean verificaDisabledQuantidade() {
		// se já foi escolhido e carregado um tipo de item de dieta
		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {
			// se não digita, desabilita
			if (DominioRestricao.N.equals(formulario.getTipoItem()
					.getIndDigitaQuantidade())) {
				return true; // desabilitado
			} else {
				return false; // habilitado
			}
		}

		formulario.setQuantidade(null); // limpa

		// se não foi possivel verificar
		return true; // desabilitado
	}

	/**
	 * Retorna true se o campo quantidade é obrigatório.
	 * 
	 * @return
	 */
	public boolean verificaRequiredQuantidade() {
		// se já foi escolhido e carregado um item de dieta
		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {
			// se obrigatório
			if (DominioRestricao.O.equals(formulario.getTipoItem()
					.getIndDigitaQuantidade())) {
				return true; // obrigatório
			}
		}

		// se não foi possivel verificar
		return false; // opcional
	}

	public boolean verificaDisabledAprazamento() {

		if (this.frequenciaPadrao) {
			return true;
		}
		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {
			// Se tem item de dieta com digitação Opcional ou Obrigatória
			// habilita
			if (formulario.getTipoItem().getIndDigitaAprazamento() != null
					&& (formulario.getTipoItem().getIndDigitaAprazamento()
							.equals(DominioRestricao.O) || formulario
							.getTipoItem().getIndDigitaAprazamento().equals(
									DominioRestricao.C))) {
				return false;
			}
		}

		return true; // desabilita
	}

	public boolean verificaDisabledNumeroVezes() {
		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {
			// Se o tipo de item de dieta tem frequencia padrão habilita!
			if (formulario.getTipoItem().getTipoFrequenciaAprazamento() != null) {
				return false;
			}
		}
		formulario.setNumVezes(null); // limpa antes de desabilitar
		return true; // desabilita
	}

	/* ---- Controles do APRAZAMENTO ---- */

	/**
	 * Carregar frequencia padrão quando estas informações estiverem cadastradas
	 */
	public void populaDependencias() {
		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {

			if (formulario.getTipoItem().getTipoFrequenciaAprazamento() != null
					&& formulario.getTipoItem().getTipoFrequenciaAprazamento()
							.getSigla() != null) {
				formulario.setTipoAprazamento(formulario.getTipoItem()
						.getTipoFrequenciaAprazamento());
				this.frequenciaPadrao = true;
			}

			if (formulario.getTipoItem().getFrequencia() != null) {
				formulario.setFrequencia(formulario.getTipoItem()
						.getFrequencia());
				this.frequenciaPadrao = true;
			}
		}
	}

	/**
	 * Verificar se o item de dieta possui frequencia padrão. Método utilizado
	 * para bloquear (quando for frequencia padrão) a alteração dos campos
	 * frequencia e tipo de aprazamento.
	 * 
	 * @return
	 */
	private boolean isFrequenciaPadrao() {

		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {

			if (formulario.getTipoItem().getTipoFrequenciaAprazamento() != null
					&& CoreUtil.igual(formulario.getTipoItem()
							.getTipoFrequenciaAprazamento().getSigla(),
							formulario.getTipoAprazamento().getSigla())
					&& CoreUtil.igual(formulario.getTipoItem().getFrequencia(),
							formulario.getFrequencia())) {
				return true;
			}
		}

		return false;
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(
			String strPesquisa) {
		return this.prescricaoMedicaFacade
				.buscarTipoFrequenciaAprazamento((String) strPesquisa);
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.formulario
				.getTipoAprazamento());
	}


	public String buscaDescricaoTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.formulario.getFrequencia())
				: "";
	}

	public boolean verificaRequiredTipoFrequencia() {
		if (formulario.getTipoItem() != null
				&& formulario.getTipoItem().getSeq() != null) {
			if (formulario.getTipoItem().getIndDigitaAprazamento() == DominioRestricao.O) {
				return true;
			}
		}
		return false;
	}

	public boolean verificaRequiredFrequencia() {
		return this.formulario.getTipoAprazamento() != null
				&& this.formulario.getTipoAprazamento()
						.getIndDigitaFrequencia();
	}

	public boolean isModificadoEdicao() {
		return modificadoEdicao;
	}

	public void setModificadoEdicao(boolean modificadoEdicao) {
		this.modificadoEdicao = modificadoEdicao;
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.formulario.setFrequencia(null);
		}
	}

	// getters and setters

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public List<ItemPrescricaoDietaVO> getItensDieta() {
		return itensDieta;
	}

	public void setItensDieta(List<ItemPrescricaoDietaVO> itensDieta) {
		this.itensDieta = itensDieta;
	}

	public boolean isModificado() {
		return modificado;
	}

	public void setModificado(boolean modificado) {
		this.modificado = modificado;
	}

	public MpmPrescricaoDieta getPrescricaoDieta() {
		return prescricaoDieta;
	}

	public void setPrescricaoDieta(MpmPrescricaoDieta prescricaoDieta) {
		this.prescricaoDieta = prescricaoDieta;
	}

	public ItemPrescricaoDietaVO getFormulario() {
		return formulario;
	}

	public void setFormulario(ItemPrescricaoDietaVO formulario) {
		this.formulario = formulario;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isBombaInfusao() {
		return bombaInfusao;
	}

	public void setBombaInfusao(boolean bombaInfusao) {
		this.bombaInfusao = bombaInfusao;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

}