package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacientesAdmitidos;
import br.gov.mec.aghu.dominio.DominioOrigemPaciente;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesAdmitidosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por controlar as ações da tela de pesquisa pacientes com
 * admitidos
 * 
 * @author Stanley Araujo
 * 
 */


public class PesquisaPacientesAdmitidosPaginatorController  extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 380013684141819097L;

	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 * Número do Prontuário
	 * */
	private Integer prontuario;

	/**
	 * Código do Paciente
	 * 
	 * */

	private Integer codigoPaciente;

	/**
	 * Nome do paciente
	 * 
	 * */
	private String nomePaciente;
	/**
	 * Data inicial
	 * 
	 * */
	private Date dataInicial = new Date();

	/**
	 * Data final
	 * 
	 * */
	private Date dataFinal = new Date();
	
	/***
	 * 
	 * VO para apresentação dos detalhes
	 * 
	 * **/

	private PesquisaPacientesAdmitidosVO item;

	
	private boolean renderDetalhes;

	/**
	 * 
	 * Domínio de pesquisa pacientes admitidos
	 * */
	
	private DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa = DominioOrdenacaoPesquisaPacientesAdmitidos.P;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	
	@EJB
	private IPacienteFacade pacienteFacade;

	/**
	 * Codigo AinTiposMovimentoLeito
	 */
	private Integer codigoEspecialidade;
	
	/**
	 * Sigla da Especialidade
	 * 
	 * */
	private String siglaEspecialidade;

	private String descricaoEspecialidade;
	
	private Short convenioId;

	private Byte planoId;

	/**
	 * Lista de AghEspecialidades
	 * 
	 * */

	private List<AghEspecialidades> listaTiposEspecialidade = new ArrayList<AghEspecialidades>();

	/**
	 * LOV Especialidade
	 */
	private AghEspecialidades tiposEspecialidade = null;

	/** ORIGEM **/

	private DominioOrigemPaciente origemPaciente;

	/**** CLÍNICA ***/

	/**
	 * Codigo AghClinicas
	 */
	private Integer codigoClinica;

	/**
	 * Descricao AghClinicas
	 */
	private String descricaoClinica;

	/**
	 * Responsável pela pesquisa de AghClinicas
	 */
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * LOV Clínica
	 */
	private AghClinicas clinicas = null;

	/**
	 * Lista de AghClinicas
	 */
	private List<AghClinicas> listaClinicas = new ArrayList<AghClinicas>();

	/** CONVÊNIO SAÚDE */

	/**
	 * Responsável pela pesquisa de Convenio e Plano
	 */

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	/**
	 * Plano de saude sendo vinculado ao usuário.
	 */
	private FatConvenioSaudePlano plano = null;

	/**
	 * Lista de FatConvenioSaude
	 */
	private List<FatConvenioSaudePlano> listaConveniosPlano = new ArrayList<FatConvenioSaudePlano>();
	
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private final String PAGE_INTERNAR_PACIENTE = "internacao-cadastroInternacao"; 

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Metodo invocado ao pressionar o botão 'Limpar'.
	 */
	public void limparPesquisa() {
		this.tiposEspecialidade = null;
		this.origemPaciente = null;
		this.ordenacaoPesquisa = DominioOrdenacaoPesquisaPacientesAdmitidos.P;
		this.clinicas = null;
		this.convenioId = null;
		this.planoId = null;
		this.plano = null;
		this.dataInicial = new Date();
		this.dataFinal = new Date();
		this.prontuario = null;
		this.nomePaciente = null;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		try {

			this.pesquisaInternacaoFacade.validaPesquisaPacientesAdmitidos(tiposEspecialidade, clinicas, convenioId, planoId, 
					dataInicial, dataFinal, prontuario);
			
			this.dataModel.reiniciarPaginator();	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
			this.dataModel.limparPesquisa();
		}

	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			plano = this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId);		
		}
	}
	
	public void atribuirPlano() {
		if (plano != null) {
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	@Override
	public Long recuperarCount() {
		return pesquisaInternacaoFacade.pesquisaPacientesAdmitidosCount(tiposEspecialidade, origemPaciente, ordenacaoPesquisa,
				clinicas, convenioId, planoId, dataInicial,	dataFinal, codigoPaciente);
	}

	@Override
	public List<PesquisaPacientesAdmitidosVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return pesquisaInternacaoFacade.pesquisaPacientesAdmitidos(tiposEspecialidade, origemPaciente, ordenacaoPesquisa,
				clinicas, convenioId, planoId, dataInicial,	dataFinal, codigoPaciente, firstResult, maxResult, orderProperty, asc);

	}

	/**
	 * Pesquisa Especialidades para lista de valores.
	 */
	public List<AghEspecialidades> pesquisarEspecialidade(String param){
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarTodasEspecialidades(param),
				this.cadastrosBasicosInternacaoFacade.pesquisarTodasEspecialidadesCount(param));
	
	}

	/**
	 * Pesquisa Clinicas para lista de valores.
	 * 
	 * @return
	 */
	public List<AghClinicas> pesquisarClinica(String param){
		this.listaClinicas = this.aghuFacade.pesquisarClinicas(param);
		return this.listaClinicas;
	}

	/**
	 * Pesquisa o nome do paciente
	 * 
	 * */
	public void pesquisarNomeDoPacientePorProntuario() {
		if (this.prontuario != null) {
			AipPacientes paciente = null;
			paciente = this.pacienteFacade
					.pesquisarPacientePorProntuario(prontuario);
			if (paciente != null) {
				this.nomePaciente = paciente.getNome();
				this.codigoPaciente = paciente.getCodigo();

			}else{
				this.nomePaciente = null;
				this.codigoPaciente = null;
			}

		}else{
			this.nomePaciente = null;
			this.codigoPaciente = null;
		}

	}
	
	public String cadastrarInternacao() {
		return PAGE_INTERNAR_PACIENTE;
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Integer getCodigoEspecialidade() {
		return codigoEspecialidade;
	}

	public void setCodigoEspecialidade(Integer codigoEspecialidade) {
		this.codigoEspecialidade = codigoEspecialidade;
	}

	public String getDescricaoEspecialidade() {
		return descricaoEspecialidade;
	}

	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
	}

	public List<AghEspecialidades> getListaTiposEspecialidade() {
		return listaTiposEspecialidade;
	}

	public void setListaTiposEspecialidade(
			List<AghEspecialidades> listaTiposEspecialidade) {
		this.listaTiposEspecialidade = listaTiposEspecialidade;
	}

	public AghEspecialidades getTiposEspecialidade() {
		return tiposEspecialidade;
	}

	public void setTiposEspecialidade(AghEspecialidades tiposEspecialidade) {
		this.tiposEspecialidade = tiposEspecialidade;
	}

	public DominioOrigemPaciente getOrigemPaciente() {
		return origemPaciente;
	}

	public void setOrigemPaciente(DominioOrigemPaciente origemPaciente) {
		this.origemPaciente = origemPaciente;
	}

	public Integer getCodigoClinica() {
		return codigoClinica;
	}

	public void setCodigoClinica(Integer codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	public String getDescricaoClinica() {
		return descricaoClinica;
	}

	public void setDescricaoClinica(String descricaoClinica) {
		this.descricaoClinica = descricaoClinica;
	}

	public AghClinicas getClinicas() {
		return clinicas;
	}

	public void setClinicas(AghClinicas clinicas) {
		this.clinicas = clinicas;
	}

	public List<AghClinicas> getListaClinicas() {
		return listaClinicas;
	}

	public void setListaClinicas(List<AghClinicas> listaClinicas) {
		this.listaClinicas = listaClinicas;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public DominioOrdenacaoPesquisaPacientesAdmitidos getOrdenacaoPesquisa() {
		return ordenacaoPesquisa;
	}

	public void setOrdenacaoPesquisa(
			DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa) {
		this.ordenacaoPesquisa = ordenacaoPesquisa;
	}

	public FatConvenioSaudePlano getPlano() {
		return plano;
	}

	public void setPlano(FatConvenioSaudePlano plano) {
		this.plano = plano;
	}

	public List<FatConvenioSaudePlano> getListaConveniosPlano() {
		return listaConveniosPlano;
	}

	public void setListaConveniosPlano(
			List<FatConvenioSaudePlano> listaConveniosPlano) {
		this.listaConveniosPlano = listaConveniosPlano;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public PesquisaPacientesAdmitidosVO getItem() {
		return item;
	}

	public void setItem(PesquisaPacientesAdmitidosVO item) {
		this.item = item;
	}

	public boolean isRenderDetalhes() {
		return renderDetalhes;
	}

	public void setRenderDetalhes(boolean renderDetalhes) {
		this.renderDetalhes = renderDetalhes;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}
}