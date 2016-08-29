package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.CadastroMateriaisImpressaoNotaSalaVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSalaId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroMateriaisImpressaoNotaSalaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 473401673233275970L;

	private static final String MANTER_NOTA_SALA = "manterNotaDeSalaUnidade";
	private static final String  MANTER_CONVERSAO_UNIDADE = "manterConversaoUnidadeConsumoList";
	private static final String CADASTRO_MATERIAIS_IMPRESSAO = "cadastroMateriaisImpressaoNotaSala";
	
	/*
	 * Injeções
	 */

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	/*
	 * Parâmetros de conversação
	 */
	private Short unfSeq, seqp; // ID da MbcUnidadeNotaSala
	private String voltarPara; // Controla a navegação do botão voltar

	/*
	 * Campos somente leitura do cabeçalho
	 */
	private String especialidade;
	private String procedimentoCirurgico;

	/**
	 * Instância que será gravada
	 */
	private CadastroMateriaisImpressaoNotaSalaVO materialNotaSala;
	

	// Unidade de nota sala que será associada ao material nota sala
	private MbcUnidadeNotaSala unidadeNotaSala;

	// Lista de resultados da pesquisa
	List<CadastroMateriaisImpressaoNotaSalaVO> listaMateriaisNotaSala;

	// Controla edicão
	private boolean emEdicao;

	// Integração com cadastro de fator de conversão
	private Integer codigoMaterial;
	private String codigoUnidadeMedida;
	
	private ScoMaterial material;
	
	private ScoUnidadeMedida unidadeMedida;

	@Inject
	private ManterConversaoUnidadeConsumoPaginatorController manterConversaoUnidadeConsumoPaginatorController;
	
	private CadastroMateriaisImpressaoNotaSalaVO materialNotaSalaSelecionado; 
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 


		// Cria nova instância que será persistida
		if (codigoMaterial == null || StringUtils.isEmpty(codigoUnidadeMedida)) {
			materialNotaSala = new CadastroMateriaisImpressaoNotaSalaVO();
			emEdicao = false;
		}

		codigoMaterial = null;
		codigoUnidadeMedida = null;

		// Obtém unidade de nota sala através dos parâmetros de conversação
		Enum[] leftJoins = {MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES,MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS};
		unidadeNotaSala = blocoCirurgicoCadastroApoioFacade.obterUnidadeNotaSalaPorChavePrimaria(new MbcUnidadeNotaSalaId(unfSeq, seqp), null, leftJoins);

		// Pesquisa da lista de materiais nota sala
		pesquisarListaMateriaisNotaSala();

		// Instancia especialidade (campo SOMENTE LEITURA no formulário)
		AghEspecialidades especialidadeNotaSala = unidadeNotaSala.getAghEspecialidades();
		especialidade = especialidadeNotaSala != null ? especialidadeNotaSala.getSigla() + " - " + especialidadeNotaSala.getNomeEspecialidade() : "";

		// Instancia especialidade (campo SOMENTE LEITURA no formulário)
		MbcProcedimentoCirurgicos procedimentoCirurgicosNotaSala = unidadeNotaSala.getMbcProcedimentoCirurgicos();
		procedimentoCirurgico = procedimentoCirurgicosNotaSala != null ? procedimentoCirurgicosNotaSala.getSeq() + " - " + procedimentoCirurgicosNotaSala.getDescricao() : "";
	
	}
	
	
	public void editar() {
		if(materialNotaSalaSelecionado != null) {
			materialNotaSala = materialNotaSalaSelecionado;
			ScoMaterial material = comprasFacade.obterMaterialPorId(materialNotaSala.getCodigoMaterial());
			setMaterial(material);
			ScoUnidadeMedida medida = comprasFacade.obterScoUnidadeMedidaPorChavePrimaria(materialNotaSala.getCodigoUnidadeMedida());
			setUnidadeMedida(medida);
		}
	}
	

	/**
	 * Pesquisa da lista de materiais nota sala
	 */
	public void pesquisarListaMateriaisNotaSala() {
		listaMateriaisNotaSala = blocoCirurgicoCadastroApoioFacade.pesquisarCadastroMateriaisImpressaoNotaSala(unidadeNotaSala);
	}


	/*
	 * Métodos da SuggestionBox
	 */

	/**
	 * Obtem o material do material nota sala
	 */
	public List<ScoMaterial> obterMaterial(String objPesquisa) {
		try {
			return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.pesquisarMateriaisAtivosGrupoMaterial(objPesquisa),
					this.obterMaterialCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public Long obterMaterialCount(String objPesquisa) {
		try {
			return this.blocoCirurgicoCadastroApoioFacade.pesquisarMateriaisAtivosGrupoMaterialCount(objPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String redirecionarFatorConversao() {		
		codigoMaterial = materialNotaSala.getCodigoMaterial();
		codigoUnidadeMedida = materialNotaSala.getCodigoUnidadeMedida();
		manterConversaoUnidadeConsumoPaginatorController.limparPesquisa();
		manterConversaoUnidadeConsumoPaginatorController.setCodMaterial(codigoMaterial);
		manterConversaoUnidadeConsumoPaginatorController.setUmdCodigo(codigoUnidadeMedida);
		manterConversaoUnidadeConsumoPaginatorController.setVoltarPara(CADASTRO_MATERIAIS_IMPRESSAO);
		manterConversaoUnidadeConsumoPaginatorController.setExibirBotaoNovo(true);
		manterConversaoUnidadeConsumoPaginatorController.getDataModel().reiniciarPaginator();
		return MANTER_CONVERSAO_UNIDADE;
	}
	
	/**
	 * Obtem a unidade de impressão do material nota sala
	 */
	public List<ScoUnidadeMedida> obterUnidadeImpressao(String objPesquisa) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorSigla(objPesquisa, true),
				this.obterUnidadeImpressaoCount(objPesquisa));
	}

	public Long obterUnidadeImpressaoCount(String objPesquisa) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorSiglaCount(objPesquisa, true);
	}

	/**
	 * Realiza ações após selecionar um material
	 */
	public void posSelecionarMaterial() {

		if (materialNotaSala != null) {
			/*
			 * Após selecionar um item na SuggestionBox de material, dever carregado como sugestão a descrição do material no CAMPO NOME DE IMPRESSÃO
			 */
			ScoMaterial material = comprasFacade.obterMaterialPorId(this.material.getCodigo());
			materialNotaSala.setNomeImpressao(material != null ? formatarNomeImpressaoDescricaoMaterial(material.getNome()) : null);

			/*
			 * Após selecionar um item na SuggestionBox de material, dever carregado como sugestão a unidade de medida do material no CAMPO UNIDADE DE IMPRESSÃO
			 */
			setUnidadeMedida(material.getUnidadeMedida());
		} else {
			// Quando a Suggestion Box de material é limpa os demais campos são limpos
			materialNotaSala = new CadastroMateriaisImpressaoNotaSalaVO();
		}
	}
	
	
	public void posDeletarMaterial() {
		unidadeMedida = null;
		materialNotaSala = new CadastroMateriaisImpressaoNotaSalaVO();
	}

	/**
	 * Formata o nome da impressão quando informado pelo material O tamanho máximo do campo é 20 carácteres
	 * 
	 * @param descricao
	 * @return
	 */
	private String formatarNomeImpressaoDescricaoMaterial(String descricao) {
		if (descricao != null && descricao.length() > 20) {
			return descricao.substring(0, 20);
		}
		return descricao == null ? "" : descricao;
	}

	/*
	 * Métodos persistência
	 */

	/**
	 * Edição
	 * 
	 * @param seq
	 * @return
	 */
	public void cancelarEdicao() {
		materialNotaSala = new CadastroMateriaisImpressaoNotaSalaVO();
		emEdicao = false;
		setMaterial(null);
		setUnidadeMedida(null);
		pesquisarListaMateriaisNotaSala();
	}

	/**
	 * Gravar
	 * 
	 * @return
	 */
	public void gravar() {

		try {

			MbcMaterialImpNotaSalaUn materialImpNotaSalaUn = null;
			if (isEmEdicao()) {
				// ALTERANDO
				materialImpNotaSalaUn = blocoCirurgicoCadastroApoioFacade.obterMaterialImpNotaSalaUn(materialNotaSala.getCodigo());
			} else {
				// INSERINDO NOVA
				materialImpNotaSalaUn = new MbcMaterialImpNotaSalaUn();
			}
			
			// Popula instancia ALTERADA/NOVA
			ScoMaterial material = comprasFacade.obterMaterialPorId(getMaterial().getCodigo());
			materialImpNotaSalaUn.setMaterial(material);
			materialImpNotaSalaUn.setNomeImpressao(materialNotaSala.getNomeImpressao());
			materialImpNotaSalaUn.setUnidadeMedida(getUnidadeMedida());
			materialImpNotaSalaUn.setOrdemImpressao(materialNotaSala.getOrdemImpressao());

			// Seta unidade de nota sala
			materialImpNotaSalaUn.setUnidadeNotaSala(unidadeNotaSala);

			// Validações do FORMS/ON
			blocoCirurgicoCadastroApoioFacade.validarItemMaterialNotaSala(materialImpNotaSalaUn);

			// INSERIR
			blocoCirurgicoCadastroApoioFacade.persistirMaterialImpNotaSalaUn(materialImpNotaSalaUn);

			// Cria nova instância que será persistida
			materialNotaSala = new CadastroMateriaisImpressaoNotaSalaVO();
			emEdicao = false;
			this.material = null;
			this.unidadeMedida = null;
			// REFAZ a pesquisa da lista de materiais nota sala
			pesquisarListaMateriaisNotaSala();

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_SALVAR_MATERIAL_NOTA_SALA");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Excluir
	 */
	public void excluir() {
		if (materialNotaSalaSelecionado != null) {
			try {
				MbcMaterialImpNotaSalaUn materialImpNotaSalaUnRemover = blocoCirurgicoCadastroApoioFacade.obterMaterialImpNotaSalaUn(materialNotaSalaSelecionado.getCodigo());

				// REMOVER
				blocoCirurgicoCadastroApoioFacade.removerMaterialImpNotaSalaUn(materialImpNotaSalaUnRemover);

				pesquisarListaMateriaisNotaSala();

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_MATERIAL_NOTA_SALA");

			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
	}


	/**
	 * Método chamado para o botão voltar
	 */
	public String cancelar() {
		codigoMaterial = null;
		codigoUnidadeMedida = null;
		material = null;
		unidadeMedida = null;
		materialNotaSala = new CadastroMateriaisImpressaoNotaSalaVO();
		return MANTER_NOTA_SALA;
	}

	/**
	 * Seleciona linha na tabela da lista de materiais durante a edição
	 * 
	 * @param seq
	 * @return
	 */
	public boolean isItemSelecionado(Integer seq) {
		if (emEdicao && seq != null && seq.equals(materialNotaSala.getCodigo())) {
			return true;
		}
		return false;
	}

	/*
	 * Getters e Setters
	 */

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(String procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public CadastroMateriaisImpressaoNotaSalaVO getMaterialNotaSala() {
		return materialNotaSala;
	}

	public void setMaterialNotaSala(CadastroMateriaisImpressaoNotaSalaVO materialNotaSala) {
		this.materialNotaSala = materialNotaSala;
	}

	public MbcUnidadeNotaSala getUnidadeNotaSala() {
		return unidadeNotaSala;
	}

	public void setUnidadeNotaSala(MbcUnidadeNotaSala unidadeNotaSala) {
		this.unidadeNotaSala = unidadeNotaSala;
	}

	public List<CadastroMateriaisImpressaoNotaSalaVO> getListaMateriaisNotaSala() {
		return listaMateriaisNotaSala;
	}

	public void setListaMateriaisNotaSala(List<CadastroMateriaisImpressaoNotaSalaVO> listaMateriaisNotaSala) {
		this.listaMateriaisNotaSala = listaMateriaisNotaSala;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}

	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}


	public ScoMaterial getMaterial() {
		return material;
	}


	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}


	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}


	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}


	public CadastroMateriaisImpressaoNotaSalaVO getMaterialNotaSalaSelecionado() {
		return materialNotaSalaSelecionado;
	}


	public void setMaterialNotaSalaSelecionado(
			CadastroMateriaisImpressaoNotaSalaVO materialNotaSalaSelecionado) {
		this.materialNotaSalaSelecionado = materialNotaSalaSelecionado;
	}

}