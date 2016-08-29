package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPrescricaoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoUsoReceituario;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.vo.ItemReceituarioVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class ManterSumarioAltaReceitasController extends ActionController {

	
	private static final String MENSAGEM_ERRO_ORDENACAO_EM_EDICAO = "MENSAGEM_ERRO_ORDENACAO_EM_EDICAO";

	private static final Log LOG = LogFactory.getLog(ManterSumarioAltaReceitasController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5416662313387595271L;

	private Integer currentSlider;
	private Integer novoSlider;
	
	private final String DESCRICAO = "descricao";
	private final String DESCRICAO_ESPECIAL = "descricaoEspecial";
	private final String USO = "radioUsoRadio";
	private final String USO_ESPECIAL = "radioUsoEspecialRadio";

	public enum ManterSumarioAltaReceitasControllerExceptionCode implements
	BusinessExceptionCode {
		MENSAGEM_USO_NAO_INFORMADO, 
		MENSAGEM_DESCRICAO_NAO_INFORMADO,
		MENSAGEM_ERRO_ORDENACAO_EM_EDICAO,
		MENSAGEM_ERRO_GRAVAR_SEM_ADICIONAR;
	}
	
	private static final String TAB_0 = "tabReceita0";
	private static final String TAB_1 = "tabReceita1";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	// RECEITA GERAL
	// mestre
	private MamReceituarios receituario = new MamReceituarios();
	private Byte nroVias = 2; // valor inicial do nro de vias do receituário
	// item
	private ItemReceituarioVO formulario = new ItemReceituarioVO();
	// indica que está em modo de edição do item
	private boolean altera;
	// indica que está em modo de edição do receituarios
	private boolean editaMestre;
	// indica que já foi feita alguma modificação.
	private boolean modificado;
	// lista de itens para alterar(alterados)
	private Set<ItemReceituarioVO> itensParaAlterar = new HashSet<ItemReceituarioVO>();
	// lista de itens para excluir
	private Set<ItemReceituarioVO> itensParaExcluir = new HashSet<ItemReceituarioVO>();
	// parâmetros do mestre
	private Long rctSeq;
	private Short seqp;
	private List<ItemReceituarioVO> listaItensReceita;
	// Sumario de Alta
	private MpmAltaSumario altaSumario;
	// ordem dos itens da Receita
	private Byte ordem = 0;
	// usdado para controlar o label do campo descricao
	private boolean formula = false;
	private Integer ultimoIndice;
	// usado para controlar a exibição de confirmação da validade
	private boolean confirmaValidade = false;
	private Integer indiceTabela;
	private ItemReceituarioVO itemReceituarioSelecionado;


	// RECEITA ESPECIAL
	// mestre
	private MamReceituarios receituarioEspecial = new MamReceituarios();
	private Byte nroViasEspecial = 2; // valor inicial do nro de vias do receituário
	// item
	private ItemReceituarioVO formularioEspecial = new ItemReceituarioVO();
	// indica que está em modo de edição do item
	private boolean alteraEspecial;
	// indica que está em modo de edição do receituarios
	private boolean editaMestreEspecial;
	// indica que já foi feita alguma modificação.
	private boolean modificadoEspecial;
	// lista de itens para alterar(alterados)
	private Set<ItemReceituarioVO> itensParaAlterarEspecial = new HashSet<ItemReceituarioVO>();
	// lista de itens para excluir
	private Set<ItemReceituarioVO> itensParaExcluirEspecial = new HashSet<ItemReceituarioVO>();
	// parâmetros do mestre
	private Long rctSeqEspecial;
	private Short seqpEspecial;
	private List<ItemReceituarioVO> listaItensReceitaEspecial;
	// ordem dos itens da Receita
	private Byte ordemEspecial = 0;
	// usdado para controlar o label do campo descricao
	private boolean formulaEspecial = false;
	private Integer ultimoIndiceEspecial;
	// usado para controlar a exibição de confirmação da validade
	private boolean confirmaValidadeEspecial = false;
	private Integer indiceTabelaEspecial;
	private ItemReceituarioVO itemReceituarioSelecionadoEspecial;

	private String voltarPara;

	public void trocaSlider() {
		this.setCurrentSlider(novoSlider);
	}

	//Suggestion Box Medicamentos
	private VAfaDescrMdto descricaoMedicamento;

	//Suggestion Box Medicamentos
	private VAfaDescrMdto descricaoMedicamentoEspecial;


	// método executado ao iniciar a "aba" de receitas
	public void renderAbaReceitas(MpmAltaSumario altaSumario) {
		this.setCurrentSlider(0);
		this.altaSumario = altaSumario;
		//novo item
		this.formulario = new ItemReceituarioVO();
		this.formulario.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		this.iniciar();
		//novo item especial
		this.formularioEspecial = new ItemReceituarioVO();
		this.formularioEspecial.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		this.iniciarEspecial();
	}

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Realiza todas as inicializações necessárias para a apresentação da tela
	 * no modo de inclusão ou edição.
	 */
	public void iniciar() {
		DominioTipoReceituario tipo = DominioTipoReceituario.G;

		// busca receituario pela altaSumario + tipo 
		this.receituario = this.ambulatorioFacade.obterMamReceituario(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp(),tipo);

		//atualiza lista de itens
		this.listaItensReceita = this.buscarItensReceita(this.altaSumario);

		// se existir receituario
		if (this.receituario != null) {
			this.rctSeq = this.receituario.getSeq();
			this.ultimoIndice = this.listaItensReceita.size() - 1;
			this.nroVias = this.receituario.getNroVias();
			this.editaMestre = true;
		} else {
			this.receituario = new MamReceituarios();
			this.editaMestre = false;
		}
	}

	public void salvarItem(){
		if(this.altera) {
			alterar();
		} else {
			adicionar();
		}
	}

	public void salvarItemEspecial(){
		if(this.alteraEspecial) {
			alterarEspecial();
		} else {
			adicionarEspecial();
		}
	}

	/**
	 * Realiza todas as inicializações necessárias para a apresentação da tela
	 * no modo de inclusão ou edição.
	 */
	public void iniciarEspecial() {
		DominioTipoReceituario tipo = DominioTipoReceituario.E;

		// busca receituario pela altaSumario + tipo 
		this.receituarioEspecial = this.ambulatorioFacade.obterMamReceituario(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp(),tipo);

		//atualiza lista de itens
		this.listaItensReceitaEspecial = this.buscarItensReceitaEspecial(this.altaSumario);

		// se existir receituario
		if (this.receituarioEspecial != null) {
			this.rctSeqEspecial = this.receituarioEspecial.getSeq();
			this.ultimoIndiceEspecial = this.listaItensReceitaEspecial.size() - 1;
			this.nroViasEspecial = this.receituarioEspecial.getNroVias();
			this.editaMestreEspecial = true;
		} else {
			this.receituarioEspecial = new MamReceituarios();
			this.editaMestreEspecial = false;
		}
	}


	/**
	 * Adiciona um item à lista de itens a ser persistidos posteriormente.<br>
	 * Aqui uma instância de {@link MamItemReceituario} é criada com os dados do
	 * formulário, representado pela classe {@link ItemReceituarioVO}, esta
	 * instância é validada com as regras de negócio de inclusão, o objeto
	 * validado é colocado no formulário e adicionado a lista para persistir
	 * posteriormente e na lista de itens para serem apresentados na tela.
	 */
	public void adicionar() {
		try {

			if (!validarCampos(this.formulario, this.USO, this.DESCRICAO)) {
				return;
			}
			
			// criar persistente a partir dos dados do formulario
			MamItemReceituario novo = this.criar(this.formulario);

			// Faz pré-validações antes de incluir
			this.ambulatorioFacade.preValidar(novo, "incluir");

			this.formulario.setItemReceituario(novo);
			this.formulario.setPersistido(false);

			// seta ordem no VO igual do item
			this.formulario.setOrdem(novo.getOrdem());

			// adicionar item na lista
			this.listaItensReceita.add(this.formulario);
			this.ultimoIndice = this.listaItensReceita.size() - 1;
			this.modificado = true;
			// limpa formulário
			this.limparFormulario();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Adiciona um item à lista de itens a ser persistidos posteriormente.<br>
	 * Aqui uma instância de {@link MamItemReceituario} é criada com os dados do
	 * formulário, representado pela classe {@link ItemReceituarioVO}, esta
	 * instância é validada com as regras de negócio de inclusão, o objeto
	 * validado é colocado no formulário e adicionado a lista para persistir
	 * posteriormente e na lista de itens para serem apresentados na tela.
	 */
	public void adicionarEspecial() {
		try {
			if (!validarCampos(this.formularioEspecial, this.USO_ESPECIAL, this.DESCRICAO_ESPECIAL)) {
				return;
			}
			
			// criar persistente a partir dos dados do formulario
			MamItemReceituario novo = this.criar(this.formularioEspecial);

			// Faz pré-validações antes de incluir
			this.ambulatorioFacade.preValidar(novo, "incluir");

			this.formularioEspecial.setItemReceituario(novo);
			this.formularioEspecial.setPersistido(false);

			// seta ordem no VO igual do item
			this.formularioEspecial.setOrdem(novo.getOrdem());

			// adicionar item na lista
			this.listaItensReceitaEspecial.add(this.formularioEspecial);
			this.ultimoIndiceEspecial = this.listaItensReceitaEspecial.size() - 1;
			this.modificadoEspecial = true;
			// limpa formulário
			this.limparFormularioEspecial();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private boolean validarCampos(ItemReceituarioVO formulario, String campoIndInterno, String campoDescricao) throws BaseException {
		boolean validou = true;
		
		if (formulario.getIndInterno() == null) {
            apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", getBundle().getString("LABEL_SUMARIO_RECEITA_USO"));
			validou = false;
		}
		if (StringUtils.isBlank(formulario.getDescricao())) {
            apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", getBundle().getString("LABEL_SUMARIO_RECEITA_DESCRICAO"));
			validou = false;
		}
		
		return validou;
	}


	public void verificaTipo() {
		this.formula = false;
		if (DominioTipoPrescricaoReceituario.F.equals(this.formulario
				.getTipoPrescricao())) {
			this.formula = true;
			if (StringUtils.isBlank(this.formulario.getDescricao())) {
				this.formulario.setDescricao("Fórmula:");
			}
		}
	}

	public void verificaTipoEspecial() {
		this.formulaEspecial = false;
		if (DominioTipoPrescricaoReceituario.F.equals(this.formularioEspecial
				.getTipoPrescricao())) {
			this.formulaEspecial = true;
			if (StringUtils.isBlank(this.formularioEspecial.getDescricao())) {
				this.formularioEspecial.setDescricao("Fórmula:");
			}
		}
	}

	public void verificaValidade() {
		this.confirmaValidade = false;
		if (this.formulario.getIndUsoContinuo()) {
			if (this.formulario.getValidadeMeses() == null) {
				this.confirmaValidade = true;
			}
		}
	}

	public void verificaValidadeEspecial() {
		this.confirmaValidade = false;
		if (this.formularioEspecial.getIndUsoContinuo()) {
			if (this.formularioEspecial.getValidadeMeses() == null) {
				this.confirmaValidade = true;
			}
		}
	}

	public void atualizaValidade() {
		byte meses = (byte) 6;
		if(this.currentSlider.equals(0)){
			this.formulario.setValidadeMeses(meses); 
		} else {
			this.formularioEspecial.setValidadeMeses(meses);
		}
		this.confirmaValidade = false;	
	}

	public void naoAtualizaValidade() {
		this.confirmaValidade = false;
	}


	/**
	 * Retorna a classe de persistência populada com os dados do formulário.
	 * 
	 * @param itemReceituario
	 * @return
	 */
	private MamItemReceituario criar(ItemReceituarioVO itemReceituario)
	throws ApplicationBusinessException {

		MamItemReceituario result = new MamItemReceituario();

		result.setDescricao(itemReceituario.getDescricao());
		result.setFormaUso(itemReceituario.getFormaUso());
		result.setQuantidade(itemReceituario.getQuantidade());
		if (DominioTipoUsoReceituario.S.equals(itemReceituario.getIndInterno())) {
			result.setIndInterno(DominioSimNao.S);
		} else {
			result.setIndInterno(DominioSimNao.N);
		}
		if (itemReceituario.getIndUsoContinuo()) {
			result.setIndUsoContinuo(DominioSimNao.S);
		} else {
			result.setIndUsoContinuo(DominioSimNao.N);
		}
		result.setIndSituacao(DominioSituacao.A);
		result.setTipoPrescricao(itemReceituario.getTipoPrescricao());
		result.setNroGrupoImpressao(itemReceituario.getNroGrupoImpressao());
		result.setOrdem(++this.ordem);
		result.setIndValidadeProlongada(itemReceituario.getIndValidadeProlongada());
		result.setValidadeMeses(itemReceituario.getValidadeMeses());

		// coloca o item criado no vo fornecido
		itemReceituario.setItemReceituario(result);

		return result;

	}

	/**
	 * Grava no banco de dados todas as modificações feitas na
	 * receita(inclusões, alterações e exclusões de itens).<br>
	 * Após a gravação com sucesso entra em modo de edição atualizando com todos
	 * os dados gravados anteriormente.
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void gravar() throws CloneNotSupportedException {

		try {

			if(this.altera||!StringUtils.isBlank(this.formulario.getDescricao())){
				throw new ApplicationBusinessException(
						ManterSumarioAltaReceitasControllerExceptionCode.MENSAGEM_ERRO_GRAVAR_SEM_ADICIONAR);				
			}

			this.receituario.setTipo(DominioTipoReceituario.G);
			this.receituario.setMpmAltaSumario(altaSumario);
			this.receituario.setNroVias(nroVias);

			if (this.editaMestre) {
				// altera receituario

				List<MamItemReceituario> alterados = this.getItensParaAlterar();
				List<MamItemReceituario> novos = this.getItensParaIncluir();
				List<MamItemReceituario> excluidos = this.getItensParaExcluir();

				this.ambulatorioFacade.gravarMamReceituario(this.receituario, novos,
						alterados, excluidos);

				if (!alterados.isEmpty() || !novos.isEmpty()) {
					apresentarMsgNegocio(
							Severity.INFO,
					"MENSAGEM_SUCESSO_RECEITUARIO_ALTERADO");
				} 

				// OBS: Não deve exibir mensagem se a exclusão da receita ocorreu devido a
				// exclusao do ultimo item da lista (MENSAGEM_SUCESSO_EXCLUSAO_RECEITUARIO)					
			} else {
				// inserir receituario
				List<MamItemReceituario> novos = this.getItensParaIncluir();
				this.ambulatorioFacade.gravar(this.receituario, novos);

				apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_RECEITUARIO_INCLUIDO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}


		// esvazia as listas quando tudo salvo
		this.itensParaAlterar = new HashSet<ItemReceituarioVO>();
		this.itensParaExcluir = new HashSet<ItemReceituarioVO>();

		// limpa formulário
		this.limpar();
		// depois de gravado não há mais modificações pendentes
		this.modificado = false;

		// se inclusão, entrar no modo de edição
		// se alteração, permanece neste modo

		// troca para modo de edição
		if (!this.editaMestre) {
			this.editaMestre = true;
		}

		this.rctSeq = this.receituario.getSeq();

		// reinicia com dados novos
		this.iniciar();
	}

	/**
	 * Grava no banco de dados todas as modificações feitas na
	 * receita(inclusões, alterações e exclusões de itens).<br>
	 * Após a gravação com sucesso entra em modo de edição atualizando com todos
	 * os dados gravados anteriormente.
	 * 
	 * @throws CloneNotSupportedException
	 */
	public void gravarEspecial() throws CloneNotSupportedException {

		try {
			if(this.alteraEspecial||!StringUtils.isBlank(this.formularioEspecial.getDescricao())){
				throw new ApplicationBusinessException(
						ManterSumarioAltaReceitasControllerExceptionCode.MENSAGEM_ERRO_GRAVAR_SEM_ADICIONAR);				
			}

			this.receituarioEspecial.setTipo(DominioTipoReceituario.E);
			this.receituarioEspecial.setMpmAltaSumario(altaSumario);
			this.receituarioEspecial.setNroVias(nroViasEspecial);

			if (this.editaMestreEspecial) {
				// altera receituario

				List<MamItemReceituario> alterados = this.getItensParaAlterarEspecial();
				List<MamItemReceituario> novos = this.getItensParaIncluirEspecial();
				List<MamItemReceituario> excluidos = this.getItensParaExcluirEspecial();

				this.ambulatorioFacade.gravarMamReceituario(this.receituarioEspecial, novos,
						alterados, excluidos);

				if (!alterados.isEmpty() || !novos.isEmpty()) {
					apresentarMsgNegocio(
							Severity.INFO,
					"MENSAGEM_SUCESSO_RECEITUARIOESPECIAL_ALTERADO");
				}

			} else {
				// inserir receituario
				List<MamItemReceituario> novos = this.getItensParaIncluirEspecial();
				this.ambulatorioFacade.gravar(this.receituarioEspecial, novos);

				apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_RECEITUARIOESPECIAL_INCLUIDO");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}


		// esvazia as listas quando tudo salvo
		this.itensParaAlterarEspecial = new HashSet<ItemReceituarioVO>();
		this.itensParaExcluirEspecial = new HashSet<ItemReceituarioVO>();

		// limpa formulário
		this.limparEspecial();
		// depois de gravado não há mais modificações pendentes
		this.modificadoEspecial = false;

		// se inclusão, entrar no modo de edição
		// se alteração, permanece neste modo

		// troca para modo de edição
		if (!this.editaMestreEspecial) {
			this.editaMestreEspecial = true;
		}

		this.rctSeqEspecial = this.receituarioEspecial.getSeq();

		// reinicia com dados novos
		this.iniciarEspecial();
	}

	/**
	 * Marca o item para edição e coloca os dados no formulário. Chamado pela
	 * view para Edição
	 * 
	 * @param item
	 * @throws CloneNotSupportedException
	 */
	public void editarItemGrid()
	throws CloneNotSupportedException {
		this.resetEdicao();
		this.getItemReceituarioSelecionado().setEdicao(true);
		this.indiceTabela = this.getIndiceTabela();
		this.formulario = (ItemReceituarioVO) this.getItemReceituarioSelecionado().clone();
		this.altera = true;
	}

	/**
	 * Marca o item para edição e coloca os dados no formulário. Chamado pela
	 * view para Edição
	 * 
	 * @param item
	 * @throws CloneNotSupportedException
	 */
	public void editarItemGridEspecial() throws CloneNotSupportedException {
		this.resetEdicaoEspecial();
		this.getItemReceituarioSelecionadoEspecial().setEdicao(true);
		this.indiceTabelaEspecial = this.getIndiceTabelaEspecial();
		this.formularioEspecial = (ItemReceituarioVO) this.getItemReceituarioSelecionadoEspecial().clone();
		this.alteraEspecial = true;
	}


	/**
	 * Registra o item para exclusão posteriormente.<br>
	 * O item já pode estar no banco de dados, neste caso é adicionado a uma
	 * lista que posteriormente fará a exclusão.<br>
	 * Se é um item ainda não persistido, apenas retira das listas.
	 * 
	 * @param item
	 */
	public void excluirItemGrid() {
		// remove de todas as listas
		this.listaItensReceita.remove(getItemReceituarioSelecionado());
		this.itensParaAlterar.remove(getItemReceituarioSelecionado());
		// this.itensParaIncluir.remove(item);

		this.ultimoIndice = this.listaItensReceita.size() - 1;

		// adiciona na lista de excluidos, apenas se já está persistido
		if (getItemReceituarioSelecionado().isPersistido()) {
			this.itensParaExcluir.add(getItemReceituarioSelecionado());
		}

		this.limparFormulario();
		this.modificado = true;
	}

	/**
	 * Registra o item para exclusão posteriormente.<br>
	 * O item já pode estar no banco de dados, neste caso é adicionado a uma
	 * lista que posteriormente fará a exclusão.<br>
	 * Se é um item ainda não persistido, apenas retira das listas.
	 * 
	 * @param item
	 */
	public void excluirItemGridEspecial() {
		// remove de todas as listas
		this.listaItensReceitaEspecial.remove(this.getItemReceituarioSelecionadoEspecial());
		this.itensParaAlterarEspecial.remove(this.getItemReceituarioSelecionadoEspecial());

		this.ultimoIndiceEspecial = this.listaItensReceitaEspecial.size() - 1;

		// adiciona na lista de excluidos, apenas se já está persistido
		if (this.getItemReceituarioSelecionadoEspecial().isPersistido()) {
			this.itensParaExcluirEspecial.add(this.getItemReceituarioSelecionadoEspecial());
		}

		this.limparFormularioEspecial();
		this.modificadoEspecial = true;
	}

	/**
	 * Decide qual exclusão chamar!
	 * @param receituario
	 * @param list
	 */
	public void excluir(){
		if(this.currentSlider.equals(0)){
			excluirGeral();
		} 
		if(this.currentSlider.equals(1)){
			excluirEspecial();
		}
	}

	/**
	 * Exclui a receita e os itens da receita GERAL
	 * 
	 * @param receituario
	 * @param list
	 */
	public void excluirGeral(){

		if(this.rctSeq != null){

			try{

				//busca novamente receituario por causa do cascade dos itens 
				this.receituario = this.ambulatorioFacade.obterReceituarioPeloSeq(this.rctSeq);
				this.ambulatorioFacade.excluir(this.receituario);
				this.limpar();
				this.listaItensReceita.clear();
				this.nroVias = 2;
				apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EXCLUSAO_RECEITUARIO");

				// Reinicia com dados novos
				this.editaMestre = false;
				this.iniciar();

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

		}
	}
	/**
	 * Exclui a receita e os itens da receita ESPECIAL
	 * 
	 * @param receituario
	 * @param list
	 */
	public void excluirEspecial(){

		if(this.rctSeqEspecial != null){

			try{

				//busca novamente receituario por causa do cascade dos itens 
				this.receituarioEspecial = this.ambulatorioFacade.obterReceituarioPeloSeq(this.rctSeqEspecial);
				this.ambulatorioFacade.excluir(this.receituarioEspecial);
				this.limparEspecial();
				this.listaItensReceitaEspecial.clear();
				this.nroViasEspecial = 2;
				apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EXCLUSAO_RECEITUARIO");

				// Reinicia com dados novos
				this.editaMestreEspecial = false;
				this.iniciarEspecial();

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}

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
	private List<MamItemReceituario> getItensParaIncluir() {
		List<MamItemReceituario> result = new ArrayList<MamItemReceituario>();
		for (ItemReceituarioVO i : this.listaItensReceita) {
			if (!i.isPersistido()) {
				MamItemReceituario item = i.getItemReceituario();
				if (i.getIndUsoContinuo()) {
					item.setIndUsoContinuo(DominioSimNao.S);
				} else {
					item.setIndUsoContinuo(DominioSimNao.N);
				}
				result.add(item);
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
	private List<MamItemReceituario> getItensParaAlterar() {
		List<MamItemReceituario> result = new ArrayList<MamItemReceituario>();
		for (ItemReceituarioVO vo : this.itensParaAlterar) {
			if (vo.isPersistido() || vo.isEdicao()) {
				// seta propriedades modificaveis
				MamItemReceituario item = vo.getItemReceituario();
				item.setDescricao(vo.getDescricao());
				item.setFormaUso(vo.getFormaUso());
				item.setQuantidade(vo.getQuantidade());
				if (DominioTipoUsoReceituario.S.equals(vo.getIndInterno())) {
					item.setIndInterno(DominioSimNao.S);
				} else {
					item.setIndInterno(DominioSimNao.N);
				}
				if (vo.getIndUsoContinuo()) {
					item.setIndUsoContinuo(DominioSimNao.S);
				} else {
					item.setIndUsoContinuo(DominioSimNao.N);
				}
				item.setIndSituacao(DominioSituacao.A);
				item.setTipoPrescricao(vo.getTipoPrescricao());
				item.setNroGrupoImpressao(vo.getNroGrupoImpressao());
				item.setOrdem(vo.getOrdem());
				item.setIndValidadeProlongada(vo.getIndValidadeProlongada());
				item.setValidadeMeses(vo.getValidadeMeses());

				result.add(item);
			}
		}

		return result;
	}

	private List<MamItemReceituario> getItensParaExcluir() {
		List<MamItemReceituario> result = new ArrayList<MamItemReceituario>();

		for (ItemReceituarioVO vo : this.itensParaExcluir) {
			if (vo.getItemReceituario() != null) {
				result.add(vo.getItemReceituario());
			}
		}

		return result;
	}

	/**
	 * Retorna os objetos persistentes armazenados nos VOs fornecidos, que devem
	 * ser gravados no banco de dados.
	 * 
	 * @param itens
	 *            itens pendentes de inclusão
	 * @return itens para inclusao
	 */
	private List<MamItemReceituario> getItensParaIncluirEspecial() {
		List<MamItemReceituario> result = new ArrayList<MamItemReceituario>();
		for (ItemReceituarioVO i : this.listaItensReceitaEspecial) {
			if (!i.isPersistido()) {
				MamItemReceituario item = i.getItemReceituario();
				if (i.getIndUsoContinuo()) {
					item.setIndUsoContinuo(DominioSimNao.S);
				} else {
					item.setIndUsoContinuo(DominioSimNao.N);
				}
				result.add(item);
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
	private List<MamItemReceituario> getItensParaAlterarEspecial() {
		List<MamItemReceituario> result = new ArrayList<MamItemReceituario>();
		for (ItemReceituarioVO vo : this.itensParaAlterarEspecial) {
			if (vo.isPersistido() || vo.isEdicao()) {
				// seta propriedades modificaveis
				MamItemReceituario item = vo.getItemReceituario();
				item.setDescricao(vo.getDescricao());
				item.setFormaUso(vo.getFormaUso());
				item.setQuantidade(vo.getQuantidade());
				if (DominioTipoUsoReceituario.S.equals(vo.getIndInterno())) {
					item.setIndInterno(DominioSimNao.S);
				} else {
					item.setIndInterno(DominioSimNao.N);
				}
				if (vo.getIndUsoContinuo()) {
					item.setIndUsoContinuo(DominioSimNao.S);
				} else {
					item.setIndUsoContinuo(DominioSimNao.N);
				}
				item.setIndSituacao(DominioSituacao.A);
				item.setTipoPrescricao(vo.getTipoPrescricao());
				item.setNroGrupoImpressao(vo.getNroGrupoImpressao());
				item.setOrdem(vo.getOrdem());
				item.setIndValidadeProlongada(vo.getIndValidadeProlongada());
				item.setValidadeMeses(vo.getValidadeMeses());

				result.add(item);
			}
		}

		return result;
	}

	private List<MamItemReceituario> getItensParaExcluirEspecial() {
		List<MamItemReceituario> result = new ArrayList<MamItemReceituario>();

		for (ItemReceituarioVO vo : this.itensParaExcluirEspecial) {
			if (vo.getItemReceituario() != null) {
				result.add(vo.getItemReceituario());
			}
		}

		return result;
	}

	/**
	 * Limpa o formulario e retorna para modo de inclusão.
	 */
	public void limpar() {
		//debug("++ begin limpar");
		this.limparFormulario();
		this.formula = false;
		this.confirmaValidade = false;
		this.nroVias=2;
		this.resetEdicao();
		//debug("++ end");
	}

	/**
	 * Limpa o formulario e retorna para modo de inclusão.
	 */
	public void limparEspecial() {
		this.limparFormularioEspecial();
		this.formulaEspecial = false;
		this.confirmaValidadeEspecial = false;
		this.nroViasEspecial=2;
		this.resetEdicaoEspecial();
	}

	/**
	 * Cancela tudo e entra em modo de edição.
	 */
	public void cancelar() {
		// entra em modo de inclusão do mestre
		this.rctSeq = null;
		this.seqp = null;

		this.editaMestre = false;

		// esvazia as listas
		// this.itensParaIncluir = new HashSet<ItemPrescricaoDietaVO>();
		this.itensParaAlterar = new HashSet<ItemReceituarioVO>();
		this.itensParaExcluir = new HashSet<ItemReceituarioVO>();
		this.listaItensReceita = new ArrayList<ItemReceituarioVO>();
		this.nroVias = null;

		// limpa formulário
		this.limpar();
		// reset
		this.modificado = false;

		this.iniciar();
	}

	/**
	 * Cancela tudo e entra em modo de edição.
	 */
	public void cancelarEspecial() {
		// entra em modo de inclusão do mestre
		this.rctSeqEspecial = null;
		this.seqpEspecial = null;

		this.editaMestreEspecial = false;

		// esvazia as listas
		this.itensParaAlterarEspecial = new HashSet<ItemReceituarioVO>();
		this.itensParaExcluirEspecial = new HashSet<ItemReceituarioVO>();
		this.listaItensReceitaEspecial = new ArrayList<ItemReceituarioVO>();
		this.nroViasEspecial = null;

		// limpa formulário
		this.limparEspecial();
		// reset
		this.modificadoEspecial = false;

		this.iniciarEspecial();
	}


	/**
	 * Limpa o formulário e retorna para o modo de inclusão.
	 */
	public void limparFormulario() {
		this.formulario = new ItemReceituarioVO();
		this.descricaoMedicamento = null;	
		this.formulario.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		this.altera = false;
	}

	/**
	 * Limpa o formulário e retorna para o modo de inclusão.
	 */
	public void limparFormularioEspecial() {
		this.formularioEspecial = new ItemReceituarioVO();
		this.descricaoMedicamentoEspecial = null;	
		this.formularioEspecial.setTipoPrescricao(DominioTipoPrescricaoReceituario.M);
		this.alteraEspecial = false;
	}

	/**
	 * Retira todos os itens do modo de edição.
	 */
	private void resetEdicao() {
		for (ItemReceituarioVO vo : this.listaItensReceita) {
			vo.setEdicao(false);
		}
	}

	/**
	 * Retira todos os itens do modo de edição.
	 */
	private void resetEdicaoEspecial() {
		for (ItemReceituarioVO vo : this.listaItensReceitaEspecial) {
			vo.setEdicao(false);
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
			if (!validarCampos(this.formulario, this.USO, this.DESCRICAO )) {
				return;
			}
			
			// altera os dados do clone do pojo
			MamItemReceituario alterado = this.alterar(this.formulario, true);

			// Faz pré-validações antes de alterar
			this.ambulatorioFacade.preValidar(alterado, "alterar");

			// passando nas regras altera o original
			this.alterar(this.formulario, false);

			// se já está persistido,
			// adiciona na lista para enviar para o banco de dados
			if (this.formulario.isPersistido()) {
				this.itensParaAlterar.add(this.formulario);
			}

			this.modificado = true;

			// remove pela instância pelo índice
			this.listaItensReceita.remove(this.indiceTabela.intValue());

			// adiciona nova instância
			this.listaItensReceita.add(this.formulario);

			// Ordena pela atributo: Ordem , para não mexer na posição da lista
			Collections.sort(this.listaItensReceita);

			this.limparFormulario();
			this.resetEdicao();

			// } catch (AGHUNegocioException e) {
			// apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
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
	public void alterarEspecial() {
		try {
			if (!validarCampos(this.formularioEspecial, this.USO_ESPECIAL, this.DESCRICAO_ESPECIAL)) {
				return;
			}
			
			// altera os dados do clone do pojo
			MamItemReceituario alterado = this.alterar(this.formularioEspecial, true);

			// Faz pré-validações antes de alterar
			this.ambulatorioFacade.preValidar(alterado, "alterar");

			// passando nas regras altera o original
			this.alterar(this.formularioEspecial, false);

			// se já está persistido,
			// adiciona na lista para enviar para o banco de dados
			if (this.formularioEspecial.isPersistido()) {
				this.itensParaAlterarEspecial.add(this.formularioEspecial);
			}

			this.modificadoEspecial = true;

			// remove pela instância pelo índice
			this.listaItensReceitaEspecial.remove(this.indiceTabelaEspecial.intValue());

			// adiciona nova instância
			this.listaItensReceitaEspecial.add(this.formularioEspecial);

			// Ordena pela atributo: Ordem , para não mexer na posição da lista
			Collections.sort(this.listaItensReceitaEspecial);

			this.limparFormularioEspecial();
			this.resetEdicaoEspecial();

			// } catch (AGHUNegocioException e) {
			// apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
	}

	/**
	 * Retorna o clone do objeto aplicadas as alterações do formulário.
	 * 
	 * @param formulario
	 * @param clone
	 *            se true altera e retorna o clone
	 * @throws CloneNotSupportedException
	 */
	private MamItemReceituario alterar(ItemReceituarioVO formulario,
			boolean clone) throws CloneNotSupportedException {

		MamItemReceituario result = (MamItemReceituario) formulario
		.getItemReceituario();

		if (clone) {
			result = (MamItemReceituario) result.clone();
		}

		result.setDescricao(formulario.getDescricao());
		result.setFormaUso(formulario.getFormaUso());
		result.setQuantidade(formulario.getQuantidade());
		if (DominioTipoUsoReceituario.S.equals(formulario.getIndInterno())) {
			result.setIndInterno(DominioSimNao.S);
		} else {
			result.setIndInterno(DominioSimNao.N);
		}
		if (formulario.getIndUsoContinuo()) {
			result.setIndUsoContinuo(DominioSimNao.S);
		} else {
			result.setIndUsoContinuo(DominioSimNao.N);
		}
		result.setIndSituacao(DominioSituacao.A);
		result.setTipoPrescricao(formulario.getTipoPrescricao());
		result.setNroGrupoImpressao(formulario.getNroGrupoImpressao());
		result.setOrdem(formulario.getOrdem());
		result.setIndValidadeProlongada(formulario.getIndValidadeProlongada());
		result.setValidadeMeses(formulario.getValidadeMeses());

		return result;
	}

	/**
	 * Método para busca da lista de itens da receita
	 */
	public List<ItemReceituarioVO> buscarItensReceita(MpmAltaSumario altaSumario) {
		// slider corrente determina o tipo de receita a ser consultada.
		DominioTipoReceituario tipo = DominioTipoReceituario.G;
		List<MamItemReceituario> itens = this.ambulatorioFacade
		.buscarItensReceita(altaSumario, tipo);
		// O itens do Receituário são populados como Persisitido em
		// ItemReceituarioVO
		List<ItemReceituarioVO> result = new ArrayList<ItemReceituarioVO>();
		try {
			this.ordem = 0;
			for (MamItemReceituario i : itens) {
				ItemReceituarioVO valueObject;

				valueObject = new ItemReceituarioVO(
						(MamItemReceituario) i.clone());

				// lendo do banco coloca como persistido
				valueObject.setPersistido(true);

				// incrementa a ordem
				this.ordem++;
				// se ordem é nula então seta ordem sequencial
				if (valueObject.getOrdem() == null) {
					valueObject.setOrdem(this.ordem);
				} else if (valueObject.getOrdem() > this.ordem) {
					// se a ordem do banco for maior coloca a ordem do banco
					this.ordem = valueObject.getOrdem();
				}

				//ind_continuo
				if(DominioSimNao.S.equals(i.getIndUsoContinuo())){
					valueObject.setIndUsoContinuo(true);
				} else{
					valueObject.setIndUsoContinuo(false);
				}

				result.add(valueObject);
			}
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe MamItemReceituario "
					+ "não implementa a interface Cloneable.", e);
		}
		return result;
	}

	/**
	 * Método para busca da lista de itens da receita
	 */
	public List<ItemReceituarioVO> buscarItensReceitaEspecial(MpmAltaSumario altaSumario) {
		DominioTipoReceituario tipo = DominioTipoReceituario.E;
		List<MamItemReceituario> itens = this.ambulatorioFacade
		.buscarItensReceita(altaSumario, tipo);

		// O itens do Receituário são populados como Persisitido em
		// ItemReceituarioVO
		List<ItemReceituarioVO> result = new ArrayList<ItemReceituarioVO>();
		try {
			this.ordemEspecial = 0;
			for (MamItemReceituario i : itens) {
				ItemReceituarioVO valueObject;

				valueObject = new ItemReceituarioVO(
						(MamItemReceituario) i.clone());

				// lendo do banco coloca como persistido
				valueObject.setPersistido(true);

				// incrementa a ordem
				this.ordemEspecial++;
				// se ordem é nula então seta ordem sequencial
				if (valueObject.getOrdem() == null) {
					valueObject.setOrdem(this.ordemEspecial);
				} else if (valueObject.getOrdem() > this.ordemEspecial) {
					// se a ordem do banco for maior coloca a ordem do banco
					this.ordemEspecial = valueObject.getOrdem();
				}

				//ind_continuo
				if(DominioSimNao.S.equals(i.getIndUsoContinuo())){
					valueObject.setIndUsoContinuo(true);
				} else{
					valueObject.setIndUsoContinuo(false);
				}

				result.add(valueObject);
			}
		} catch (CloneNotSupportedException e) {
			LOG.error("A classe MamItemReceituario "
					+ "não implementa a interface Cloneable.", e);
		}
		return result;
	}

	public void ordenarListaItensReceitaCima() {
		if(!this.ordenarListaItensReceita(this.getItemReceituarioSelecionado(), true, this.getIndiceTabela())){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_ORDENACAO_EM_EDICAO);
		}
	}

	public void ordenarListaItensReceitaBaixo() {
		if(!this.ordenarListaItensReceita(this.getItemReceituarioSelecionado(), false, this.getIndiceTabela())){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_ORDENACAO_EM_EDICAO);
		}
	}

	/**
	 * 
	 * @param item
	 *            Item(VO) da receita
	 * @param sobe
	 *            Se for true o item deve subir uma posicao na lista
	 * @param indice
	 *            Recebe o indice da linha do item na tabela
	 */
	private boolean ordenarListaItensReceita(ItemReceituarioVO item, boolean sobe,
			int indice) {

		// Verifica se algum item da lista esta em edicao, caso encontre, gera mensagem de erro poi nao e permitido
		//ordenar a lista quando algum item estiver em edicao
		boolean erroOrdenacao = false;
		for(ItemReceituarioVO vo : this.listaItensReceita){
			if(vo.isEdicao()){					
				erroOrdenacao = true;
				break;
			}
		}

		if(erroOrdenacao){
			return false;
		}

		// Obtem a ordem de apresentacao do item
		byte ordem = item.getItemReceituario().getOrdem();

		ItemReceituarioVO aux = null;
		this.ultimoIndice = this.listaItensReceita.size() - 1;

		if (sobe) {
			// Se for o primeiro da lista nao deve subir o item na lista
			if (indice != 0) {

				aux = this.listaItensReceita.get(indice - 1);

				item.setOrdem((byte) (ordem - 1));
				item.getItemReceituario().setOrdem((byte) (ordem - 1));
				//item.setEdicao(true);
				this.listaItensReceita.set(indice - 1, item);

				aux.setOrdem(ordem);
				aux.getItemReceituario().setOrdem(ordem);
				//aux.setEdicao(true);
				this.listaItensReceita.set(indice, aux);
			}

		} else {
			// Se for o ultimo da lista nao deve descer o elemento na lista
			if (indice != this.listaItensReceita.size() - 1) {

				aux = this.listaItensReceita.get(indice + 1);

				item.setOrdem((byte) (ordem + 1));
				item.getItemReceituario().setOrdem((byte) (ordem + 1));
				//item.setEdicao(true);
				this.listaItensReceita.set(indice + 1, item);

				aux.setOrdem(ordem);
				aux.getItemReceituario().setOrdem(ordem);
				//aux.setEdicao(true);
				this.listaItensReceita.set(indice, aux);
			}

		}

		this.itensParaAlterar.add(item);
		this.itensParaAlterar.add(aux);
		return true;

	}

	public void ordenarListaItensReceitaCimaEspecial() {
		if(!this.ordenarListaItensReceitaEspecial(this.getItemReceituarioSelecionadoEspecial(), true, this.getIndiceTabelaEspecial())){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_ORDENACAO_EM_EDICAO);
		}
	}

	public void ordenarListaItensReceitaBaixoEspecial() {
		if(!this.ordenarListaItensReceitaEspecial(this.getItemReceituarioSelecionadoEspecial(), false, this.getIndiceTabelaEspecial())){
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_ERRO_ORDENACAO_EM_EDICAO);
		}
	}

	/**
	 * 
	 * @param item
	 *            Item(VO) da receita
	 * @param sobe
	 *            Se for true o item deve subir uma posicao na lista
	 * @param indice
	 *            Recebe o indice da linha do item na tabela
	 */
	private boolean ordenarListaItensReceitaEspecial(ItemReceituarioVO item, boolean sobe,
			int indice) {

		// Verifica se algum item da lista esta em edicao, caso encontre, gera mensagem de erro poi nao e permitido
		//ordenar a lista quando algum item estiver em edicao
		boolean erroOrdenacao = false;
		for(ItemReceituarioVO vo : this.listaItensReceitaEspecial){
			if(vo.isEdicao()){					
				erroOrdenacao = true;
				break;
			}
		}

		if(erroOrdenacao){
			return false;
		}

		// Obtem a ordem de apresentacao do item
		byte ordem = item.getItemReceituario().getOrdem();

		ItemReceituarioVO aux = null;
		this.ultimoIndiceEspecial = this.listaItensReceitaEspecial.size() - 1;

		if (sobe) {
			// Se for o primeiro da lista nao deve subir o item na lista
			if (indice != 0) {

				aux = this.listaItensReceitaEspecial.get(indice - 1);

				item.setOrdem((byte) (ordem - 1));
				item.getItemReceituario().setOrdem((byte) (ordem - 1));
				//item.setEdicao(true);
				this.listaItensReceitaEspecial.set(indice - 1, item);

				aux.setOrdem(ordem);
				aux.getItemReceituario().setOrdem(ordem);
				//aux.setEdicao(true);
				this.listaItensReceitaEspecial.set(indice, aux);
			}

		} else {
			// Se for o ultimo da lista nao deve descer o elemento na lista
			if (indice != this.listaItensReceitaEspecial.size() - 1) {

				aux = this.listaItensReceitaEspecial.get(indice + 1);

				item.setOrdem((byte) (ordem + 1));
				item.getItemReceituario().setOrdem((byte) (ordem + 1));
				//item.setEdicao(true);
				this.listaItensReceitaEspecial.set(indice + 1, item);

				aux.setOrdem(ordem);
				aux.getItemReceituario().setOrdem(ordem);
				//aux.setEdicao(true);
				this.listaItensReceitaEspecial.set(indice, aux);
			}

		}

		this.itensParaAlterarEspecial.add(item);
		this.itensParaAlterarEspecial.add(aux);
		return true;

	}

	/**
	 * Obtem lista de medicamentos
	 */
	public List<VAfaDescrMdto> obterMedicamentosReceitaVO(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosReceitaVO((String) strPesquisa),obterMedicamentosReceitaVOCount(strPesquisa));
	}

	/**
	 * Obtem o total de registros encontrados de medicamentos para receita
	 */
	public Long obterMedicamentosReceitaVOCount(String strPesquisa) {
		return this.farmaciaFacade
		.obterMedicamentosReceitaVOCount((String) strPesquisa);
	}
	
	public List<VAfaDescrMdto> obterMedicamentosEnfermeiroObstetraReceitaVO(Object strPesquisa) {
		return this.farmaciaFacade.obterMedicamentosEnfermeiroObstetraReceitaVO((String) strPesquisa);
	}

	public void atualizarDescMedicamento() {

		StringBuilder descricao = new StringBuilder("");

		if (StringUtils.isNotBlank(descricaoMedicamento.getId()
				.getDescricaoMat())) {
			descricao.append(descricaoMedicamento.getId().getDescricaoMat());
		}

		if (StringUtils.isNotBlank(descricaoMedicamento
				.getConcentracaoFormatada())) {
			descricao.append(' ');
			descricao.append(descricaoMedicamento.getConcentracaoFormatada());
		}

		if (descricaoMedicamento.getUnidadeMedidaMedicas() != null
				&& StringUtils.isNotBlank(descricaoMedicamento
						.getUnidadeMedidaMedicas().getDescricao())) {
			descricao.append(' ');
			descricao.append(descricaoMedicamento.getUnidadeMedidaMedicas()
					.getDescricao());
		}

		if(novoSlider != null){
			this.setCurrentSlider(novoSlider);
		}

		if (currentSlider.equals(0)){
			this.formulario.setDescricao(descricao.toString());
		}
		else{
			this.formularioEspecial.setDescricao(descricao.toString());
		}

		// limpa o conteúdo da Suggestion
		this.descricaoMedicamento = null;
	}
	
	public void onTabChange(TabChangeEvent event) {
		if(event != null && event.getTab() != null) {
			if(TAB_0.equals(event.getTab().getId())) {
				this.currentSlider = 0;
			} else if (TAB_1.equals(event.getTab().getId())){
				this.currentSlider = 1;
			}
		}
	}

	//TODO
	public String voltaPara(){
		this.limpar();
		this.limparEspecial();
		return this.voltarPara;
	}

	//GETTERS AND SETTERS!
	public List<ItemReceituarioVO> getListaItensReceita() {
		return listaItensReceita;
	}

	public void setListaItensReceita(List<ItemReceituarioVO> listaItensReceita) {
		this.listaItensReceita = listaItensReceita;
	}

	public Byte getNroVias() {
		return nroVias;
	}

	public void setNroVias(Byte nroVias) {
		this.nroVias = nroVias;
	}

	public ItemReceituarioVO getFormulario() {
		return formulario;
	}

	public void setFormulario(ItemReceituarioVO formulario) {
		this.formulario = formulario;
	}

	public MamReceituarios getReceituario() {
		return receituario;
	}

	public void setReceituario(MamReceituarios receituario) {
		this.receituario = receituario;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}

	public boolean isEditaMestre() {
		return editaMestre;
	}

	public void setEditaMestre(boolean editaMestre) {
		this.editaMestre = editaMestre;
	}

	public boolean isModificado() {
		return modificado;
	}

	public void setModificado(boolean modificado) {
		this.modificado = modificado;
	}

	public Long getRctSeq() {
		return rctSeq;
	}

	public void setRctSeq(Long rctSeq) {
		this.rctSeq = rctSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public void setItensParaAlterar(Set<ItemReceituarioVO> itensParaAlterar) {
		this.itensParaAlterar = itensParaAlterar;
	}

	public void setItensParaExcluir(Set<ItemReceituarioVO> itensParaExcluir) {
		this.itensParaExcluir = itensParaExcluir;
	}

	public Integer getCurrentSlider() {
		return currentSlider;
	}

	public void setCurrentSlider(Integer currentSlider) {
		this.currentSlider = currentSlider;
	}

	public Byte getOrdem() {
		return ordem;
	}

	public void setOrdem(Byte ordem) {
		this.ordem = ordem;
	}

	public Integer getUltimoIndice() {
		return ultimoIndice;
	}

	public void setUltimoIndice(Integer ultimoIndice) {
		this.ultimoIndice = ultimoIndice;
	}

	public boolean isFormula() {
		return formula;
	}

	public void setFormula(boolean formula) {
		this.formula = formula;
	}

	public boolean isConfirmaValidade() {
		return confirmaValidade;
	}

	public void setConfirmaValidade(boolean confirmaValidade) {
		this.confirmaValidade = confirmaValidade;
	}

	public VAfaDescrMdto getDescricaoMedicamento() {
		return descricaoMedicamento;
	}

	public void setDescricaoMedicamento(VAfaDescrMdto descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}

	public Integer getNovoSlider() {
		return novoSlider;
	}

	public void setNovoSlider(Integer novoSlider) {
		this.novoSlider = novoSlider;
	}


	//VARIAVEIS DE CONTROLE DA RECEITA ESPECIAL
	public MamReceituarios getReceituarioEspecial() {
		return receituarioEspecial;
	}


	public void setReceituarioEspecial(MamReceituarios receituarioEspecial) {
		this.receituarioEspecial = receituarioEspecial;
	}


	public Byte getNroViasEspecial() {
		return nroViasEspecial;
	}


	public void setNroViasEspecial(Byte nroViasEspecial) {
		this.nroViasEspecial = nroViasEspecial;
	}


	public ItemReceituarioVO getFormularioEspecial() {
		return formularioEspecial;
	}


	public void setFormularioEspecial(ItemReceituarioVO formularioEspecial) {
		this.formularioEspecial = formularioEspecial;
	}


	public boolean isAlteraEspecial() {
		return alteraEspecial;
	}


	public void setAlteraEspecial(boolean alteraEspecial) {
		this.alteraEspecial = alteraEspecial;
	}


	public boolean isEditaMestreEspecial() {
		return editaMestreEspecial;
	}


	public void setEditaMestreEspecial(boolean editaMestreEspecial) {
		this.editaMestreEspecial = editaMestreEspecial;
	}


	public boolean isModificadoEspecial() {
		return modificadoEspecial;
	}


	public void setModificadoEspecial(boolean modificadoEspecial) {
		this.modificadoEspecial = modificadoEspecial;
	}


	public Long getRctSeqEspecial() {
		return rctSeqEspecial;
	}


	public void setRctSeqEspecial(Long rctSeqEspecial) {
		this.rctSeqEspecial = rctSeqEspecial;
	}


	public Short getSeqpEspecial() {
		return seqpEspecial;
	}


	public void setSeqpEspecial(Short seqpEspecial) {
		this.seqpEspecial = seqpEspecial;
	}


	public List<ItemReceituarioVO> getListaItensReceitaEspecial() {
		return listaItensReceitaEspecial;
	}


	public void setListaItensReceitaEspecial(
			List<ItemReceituarioVO> listaItensReceitaEspecial) {
		this.listaItensReceitaEspecial = listaItensReceitaEspecial;
	}


	public Byte getOrdemEspecial() {
		return ordemEspecial;
	}


	public void setOrdemEspecial(Byte ordemEspecial) {
		this.ordemEspecial = ordemEspecial;
	}


	public boolean isFormulaEspecial() {
		return formulaEspecial;
	}


	public void setFormulaEspecial(boolean formulaEspecial) {
		this.formulaEspecial = formulaEspecial;
	}


	public Integer getUltimoIndiceEspecial() {
		return ultimoIndiceEspecial;
	}


	public void setUltimoIndiceEspecial(Integer ultimoIndiceEspecial) {
		this.ultimoIndiceEspecial = ultimoIndiceEspecial;
	}


	public boolean isConfirmaValidadeEspecial() {
		return confirmaValidadeEspecial;
	}


	public void setConfirmaValidadeEspecial(boolean confirmaValidadeEspecial) {
		this.confirmaValidadeEspecial = confirmaValidadeEspecial;
	}


	public VAfaDescrMdto getDescricaoMedicamentoEspecial() {
		return descricaoMedicamentoEspecial;
	}


	public void setDescricaoMedicamentoEspecial(
			VAfaDescrMdto descricaoMedicamentoEspecial) {
		this.descricaoMedicamentoEspecial = descricaoMedicamentoEspecial;
	}

	public void setItensParaAlterarEspecial(
			Set<ItemReceituarioVO> itensParaAlterarEspecial) {
		this.itensParaAlterarEspecial = itensParaAlterarEspecial;
	}

	public Integer getIndiceTabela() {
		return indiceTabela;
	}

	public void setIndiceTabela(Integer indiceTabela) {
		this.indiceTabela = indiceTabela;
	}

	public Integer getIndiceTabelaEspecial() {
		return indiceTabelaEspecial;
	}

	public void setIndiceTabelaEspecial(Integer indiceTabelaEspecial) {
		this.indiceTabelaEspecial = indiceTabelaEspecial;
	}

	public void setItensParaExcluirEspecial(
			Set<ItemReceituarioVO> itensParaExcluirEspecial) {
		this.itensParaExcluirEspecial = itensParaExcluirEspecial;
	}

	public ItemReceituarioVO getItemReceituarioSelecionado() {
		return itemReceituarioSelecionado;
	}

	public void setItemReceituarioSelecionado(ItemReceituarioVO itemReceituarioSelecionado) {
		this.itemReceituarioSelecionado = itemReceituarioSelecionado;
	}

	public ItemReceituarioVO getItemReceituarioSelecionadoEspecial() {
		return itemReceituarioSelecionadoEspecial;
	}

	public void setItemReceituarioSelecionadoEspecial(
			ItemReceituarioVO itemReceituarioSelecionadoEspecial) {
		this.itemReceituarioSelecionadoEspecial = itemReceituarioSelecionadoEspecial;
	}

	public String getVoltarPara() {
		return voltarPara;
}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
