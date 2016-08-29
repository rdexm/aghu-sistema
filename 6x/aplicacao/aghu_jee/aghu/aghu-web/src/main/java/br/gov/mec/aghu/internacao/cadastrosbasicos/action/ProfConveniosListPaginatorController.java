package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Classe responsável por pesquisar os Convênios associados aos Profissionais de
 * acordo com a Especialidade.
 */


public class ProfConveniosListPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5089854319828596032L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/** Campos entreda de dados usados na pesquisa. */
	private Integer vinCodigo;
	private Integer matricula;
	private String nome;
	private Long cpf;

	/** Variáveis usadas na LOV de Especialidade. */
	private String siglaEspecialidade;
	private String nomeEspecialidade;
	private AghEspecialidades aghEspecialidades;
	private List<AghEspecialidades> listaEspecialidade;
	
	private AghProfEspecialidades aghProfEspecialidades;

	/**
	 * Variável auxiliar usada para apresentar os detalhes do
	 * AghProfEspecialidades no modal.
	 */
	private ProfConveniosListVO profConveniosListVO;

	/**
	 * Lista de AghProfConvenios apresentada ao clicar em detalhes do
	 * AghProfEspecialidades no modal.
	 */
	private List<AghProfissionaisEspConvenio> listaAghProfConvenios = new ArrayList<AghProfissionaisEspConvenio>(
			0);
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private final String PAGE_CAD_PROF_CONVENIOS = "profConveniosCRUD";
	
	@Inject
	private ProfConveniosController profConveniosController;

	private String voltarPara;
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);	
	}
	
	/**
	 * <p>Método invocado ao acessar a tela</p>
	 * <p>Adicionado durante a implementação da estoria #8674</p>
	 * @author rafael.silvestre
	 */
	public void iniciar() {
		if (matricula != null && vinCodigo != null) {
//			servidorSelecionado = this.registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(serMatricula, serVinCodigo));
			pesquisarConvenioList();
		}

	}

	
	/**
	 * Método responsável para ação do botão 'Pesquisar'.
	 */
	public void pesquisarConvenioList() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Retorna uma lista de Especialidades.
	 * @param param
	 * @return List<AghEspecialidades>
	 */
	public List<AghEspecialidades> buscaEspecialidade(String param){
		this.listaEspecialidade = this.cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeGenerica(param);
		return this.listaEspecialidade;
	}

	/**
	 * Método responsável por obter os detalhes dos convênios associados.
	 * 
	 * @param profConveniosListVO
	 */
	public void listaConvenios(ProfConveniosListVO item) {
		this.profConveniosListVO = item;

		AghProfEspecialidades aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade
		.obterAghProfEspecialidades(Integer.valueOf(item
				.getSerMatricula()), Integer.valueOf(item
						.getVinCodigo()), Integer.valueOf(item
								.getSeqEspecialidade()));

		this.listaAghProfConvenios = new ArrayList<AghProfissionaisEspConvenio>(
				aghProfEspecialidades.getProfissionaisEspConvenio());

		Collections.sort(this.listaAghProfConvenios,
				COMPARATOR_PROFISSIONAL_CONVENIOS);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.action.AGHUPaginatorController#recuperarCount()
	 */
	@Override
	public Long recuperarCount() {
		
		if(this.aghEspecialidades != null){
			this.siglaEspecialidade = this.aghEspecialidades.getSigla();
		}else{
			siglaEspecialidade = null;
		}
		return Long.valueOf(this.cadastrosBasicosInternacaoFacade.pesquisaProfConveniosListCount(
				this.vinCodigo, this.matricula, this.nome, this.cpf,
				this.siglaEspecialidade));
	}

	
	public String editar(){		
		this.profConveniosController.setProfConveniosListVo(this.getProfConveniosListVO());
		this.profConveniosController.inicio();
		return PAGE_CAD_PROF_CONVENIOS;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.action.AGHUPaginatorController#recuperarListaPaginada
	 * (java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	public List<ProfConveniosListVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
	
		if(this.aghEspecialidades != null){
			this.siglaEspecialidade = this.aghEspecialidades.getSigla();
		}else{
			siglaEspecialidade = null;
		}
		
		return this.cadastrosBasicosInternacaoFacade.pesquisaProfConvenioslist(firstResult,
				maxResult, orderProperty, asc, this.vinCodigo, this.matricula,
				this.nome, this.cpf, this.siglaEspecialidade);
	}

	/**
	 * Método que limpa os campos da tela de pesquisa.
	 */
	public void limparPesquisaConvenioList() {
		this.vinCodigo = null;
		this.matricula = null;
		this.nome = null;
		this.cpf = null;
		this.siglaEspecialidade = null;
		this.nomeEspecialidade = null;
		this.listaEspecialidade = null;
		this.profConveniosListVO = null;
		this.aghEspecialidades = null;
		this.dataModel.limparPesquisa();

	}
	
	
	

	/**
	 * Comparator para ordenar a lista de
	 * <cdoe>AghProfissionaisEspConvenio</code>;
	 */
	private static final Comparator<AghProfissionaisEspConvenio> COMPARATOR_PROFISSIONAL_CONVENIOS = new Comparator<AghProfissionaisEspConvenio>() {
		@Override
		public int compare(AghProfissionaisEspConvenio o1,
				AghProfissionaisEspConvenio o2) {
			return o1.getId().getCnvCodigo().compareTo(
					o2.getId().getCnvCodigo());
		}
	};


	public void limparLista() {
		
		this.listaEspecialidade.clear();
	}

	// // GETs e SETs
	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setListaEspecialidade(List<AghEspecialidades> listaEspecialidade) {
		this.listaEspecialidade = listaEspecialidade;
	}

	public List<AghEspecialidades> getListaEspecialidade() {
		return listaEspecialidade;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public ProfConveniosListVO getProfConveniosListVO() {
		return profConveniosListVO;
	}

	public void setProfConveniosListVO(ProfConveniosListVO profConveniosListVO) {
		this.profConveniosListVO = profConveniosListVO;
	}

	public List<AghProfissionaisEspConvenio> getListaAghProfConvenios() {
		return listaAghProfConvenios;
	}

	public void setListaAghProfConvenios(
			List<AghProfissionaisEspConvenio> listaAghProfConvenios) {
		this.listaAghProfConvenios = listaAghProfConvenios;
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}
	public AghProfEspecialidades getAghProfEspecialidades() {
		return aghProfEspecialidades;
	}
	public void setAghProfEspecialidades(AghProfEspecialidades aghProfEspecialidades) {
		this.aghProfEspecialidades = aghProfEspecialidades;
	}

	/**
	 * <p>Ação do botão Voltar</p>
	 * <p>Adicionado durante a implementação da estoria #8674</p>
	 * @author rafael.silvestre
	 */
	public String voltar() {
		return voltarPara;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}


}