package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PesquisaFoneticaPrescricaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaFoneticaPrescricaoController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3916308114350996368L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private AghUnidadesFuncionais unidadeFuncional;
	private AinQuartos quarto;
	private AinLeitos leito;
	private AipPacientes aipPaciente;
	
	private final String PAGE_CONFIGURAR_LISTA_PACIENTES= "prescricaomedica-configurarListaPacientes";
	
	private List<AghAtendimentos> listaPacientesInternados;
	
	@Inject @Paginator
	private DynamicDataModel<AipPacientes> dataModel;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private DominioListaOrigensAtendimentos listaOrigensAtendimentos = DominioListaOrigensAtendimentos.INTERNACAO;
	
	private String cameFrom;
	
    @Inject
    private ConfigurarListaPacientesController configurarListaPacientesController;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void iniciar() {
		aipPaciente = new AipPacientes();
	}
	
	
	/**
	 * 
	 * @param strPesquisa
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String strPesquisa) {
		return cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncional(strPesquisa == null ? null : strPesquisa);
	}


	/**
	 * Método que retorna uma coleções de leitos p/ preencher a suggestion box,
	 * de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitos(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarLeitos(parametro);
	}

	/**
	 * Método que retorna uma coleções de quartos p/ preencher a suggestion box,
	 * de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinQuartos> pesquisarQuartos(String paramPesquisa) {
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(paramPesquisa == null ? null : paramPesquisa),pesquisarQuartosCount(paramPesquisa));
	}
	
	
	public Integer pesquisarQuartosCount(String paramPesquisa) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(paramPesquisa == null ? null : paramPesquisa).size();
	}

	
	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void listarPacientesInternados() {
		 listaPacientesInternados = this.aghuFacade.pesquisarPacientesInternados(aipPaciente, quarto, leito, unidadeFuncional);
	}

	/**
	 * Método para atribuir quarto selecionado na suggestion ao atributo
	 * this.quarto da controller.
	 * 
	 * @param quarto
	 */
	public void atribuirQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	/**
	 * Método para atribuir unidade funcional selecionada na suggestion ao
	 * atributo unidadeFuncional da controller.
	 * 
	 * @param unidadeFuncional
	 */

	public void atribuirUnidade(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	/**
	 * Método para atribuir clinica selecionado na suggestion ao atributo
	 * this.clinica da controller.
	 * 
	 * @param clinica
	 */
	public void atribuirLeito(AinLeitos leito) {
		this.leito = leito;
	}

	
	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public String  cancelar(){
		this.setAtivo(false);
		if(cameFrom != null){
			if (cameFrom.equalsIgnoreCase(PAGE_CONFIGURAR_LISTA_PACIENTES) ){
				this.limparCampos();
				return PAGE_CONFIGURAR_LISTA_PACIENTES;
			}
		}
		return "pesquisaFoneticaPrescricao";
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}


	/**
	 * @return the leito
	 */
	public AinLeitos getLeito() {
		return leito;
	}

	/**
	 * @param leito
	 *            the leito to set
	 */
	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	/**
	 * Método get para montar a descrição completa de um leito, composta pelo
	 * leitoID, andar, ala e descrição que o mesmo se encontra.
	 * 
	 * @return String com descrição completa
	 */
	public String getDescricaoCompletaLeito() {
		if (leito==null){
			return "";
		}
		return cadastrosBasicosInternacaoFacade.obterDescricaoCompletaLeito(leito.getLeitoID());
	}

	/**
	 * Método get para montar a descrição completa de um quarto, composta pelo
	 * andar, ala e descrição que o mesmo se encontra.
	 * 
	 * @return String com descrição completa
	 */
	public String getDescricaoCompletaQuarto() {
		if (quarto==null){
			return "";
		}
		return cadastrosBasicosInternacaoFacade.obterDescricaoCompletaQuarto(quarto.getNumero());
	}

	public List<AghAtendimentos> getListaPacientesInternados() {
		return listaPacientesInternados;
	}

	public void setListaPacientesInternados(
			List<AghAtendimentos> listaPacientesInternados) {
		this.listaPacientesInternados = listaPacientesInternados;
	}
	
	
	public void pesquisarFonetica() {
		if (StringUtils.isBlank(aipPaciente.getNome())) {
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_PESQUISA_FONETICA_PRESCRICAO_NOME_OBRIGATORIO");
		} else {
			this.dataModel.reiniciarPaginator();
		}
	}

	@Override
	public Long recuperarCount() {
		try {
			return this.prescricaoMedicaFacade.pesquisarPorFonemasPrescricaoCount(aipPaciente.getNome(), null, true, null, listaOrigensAtendimentos);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}

	@Override
	public List<PesquisaFoneticaPrescricaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return prescricaoMedicaFacade.pesquisarPorFonemasPrescricao(firstResult, maxResult, aipPaciente.getNome(), null, true, null, listaOrigensAtendimentos);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public void limparCampos(){
		this.aipPaciente.setProntuario(null);
		this.aipPaciente.setCodigo(null);
		this.aipPaciente.setNome(null);
		this.setAtivo(false);
	}
	
	public String selecionarPaciente(PesquisaFoneticaPrescricaoVO paciente) {
		this.setAtivo(false);
		if(cameFrom != null){
			if (cameFrom.equalsIgnoreCase(PAGE_CONFIGURAR_LISTA_PACIENTES) ){
				this.configurarListaPacientesController.setNumeroProntuario(paciente.getProntuario());
				this.configurarListaPacientesController.setLeitoID(paciente.getLeitoId());
				this.configurarListaPacientesController.obterPacienteAtendimento();
				this.configurarListaPacientesController.adicionarAtendimento();
				this.configurarListaPacientesController.setConfirmaVoltar(false);
				this.limparCampos();
				return PAGE_CONFIGURAR_LISTA_PACIENTES;
			}
		}
		return "pesquisaFoneticaPrescricao";
	}

	public DynamicDataModel<AipPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPacientes> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioListaOrigensAtendimentos getListaOrigensAtendimentos() {
		return listaOrigensAtendimentos;
	}

	public void setListaOrigensAtendimentos(
			DominioListaOrigensAtendimentos listaOrigensAtendimentos) {
		this.listaOrigensAtendimentos = listaOrigensAtendimentos;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	

}