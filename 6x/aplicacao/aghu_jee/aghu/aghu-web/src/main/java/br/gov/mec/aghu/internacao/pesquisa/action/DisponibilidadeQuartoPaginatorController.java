package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.action.SolicitacoesTransferenciaPacientePaginatorController;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.transferir.action.TransferirPacientePaginatorController;
import br.gov.mec.aghu.internacao.vo.QuartoDisponibilidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AGHUUtil;

public class DisponibilidadeQuartoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3816163494363540093L;
	private static final String PAGE_PESQUISAR_DISPONIBILIDADE_LEITO = "pesquisarDisponibilidadeLeito";
	private static final String PAGE_SOLICITACOES_TRANSFERENCIA_PACIENTE_LIST = "internacao-solicitacoesTransfPaciente";
	private static final String PAGE_TRANSFERIR_PACIENTE_LIST = "internacao-transferirPacienteList";
	private static final String PAGE_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	private static final String PAGE_PESQUISA_PACIENTE = "paciente-pesquisaPaciente";
	private static final String PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE = "pesquisarDisponibilidadeUnidade";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private DisponibilidadeLeitoPaginatorController disponibilidadeLeitoPaginatorController;
	
	@Inject
	private SolicitacoesTransferenciaPacientePaginatorController solicitacoesTransferenciaPacientePaginatorController;
	
	@Inject 
	private TransferirPacientePaginatorController transferirPacientePaginatorController;
	
	@Inject
	private CadastroInternacaoController cadastroInternacaoController;
	
	@Inject 
	private PesquisaPacienteController pesquisaPacienteController;
	
	@Inject @Paginator
	private DynamicDataModel<QuartoDisponibilidadeVO> dataModel;

	private Integer codigoPaciente;

	private AipPacientes pacienteInternacao;

	private AinQuartos quarto;
	private AghClinicas clinica = null;
	private AghUnidadesFuncionais unidadeFuncional = null;

	private Short codigoQuarto = null;
	private Short codigoUnidadeFuncional = null;
	private Integer codigoClinica = null;
	private Integer seqAtendimentoUrgencia = null;

	private String voltarPara = null;
	private String modalMessage;
	
	

	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void iniciar() {
		if (!this.dataModel.getPesquisaAtiva() && (this.codigoQuarto != null || this.codigoUnidadeFuncional != null || this.codigoClinica != null)) {

			if (this.codigoUnidadeFuncional != null) {
				this.unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.codigoUnidadeFuncional);
			}

			if (this.codigoQuarto != null) {
				this.quarto = this.cadastrosBasicosInternacaoFacade.obterQuarto(this.codigoQuarto);
			}

			if (this.codigoClinica != null) {
				this.clinica = this.aghuFacade.obterClinica(this.codigoClinica);
			}

			if (this.codigoPaciente != null) {
				this.pacienteInternacao = this.pacienteFacade.obterPaciente(this.codigoPaciente);
			}

			this.pesquisar();
		}
	}

	/**
	 * Método que retorna uma coleções de quartos p/ preencher a suggestion box,
	 * de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinQuartos> pesquisarQuartos(final String paramPesquisa) {
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(paramPesquisa == null ? null
				: paramPesquisa),pesquisarQuartosCount(paramPesquisa));
	}


	public Integer pesquisarQuartosCount(final String paramPesquisa) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(paramPesquisa == null ? null : paramPesquisa).size();
	}

	/**
	 * Método para limpar tela de pesquisa
	 */
	public void limparPesquisa() {
		this.quarto = null;
		this.clinica = null;
		this.unidadeFuncional = null;
		this.dataModel.setPesquisaAtiva(false);

		this.codigoQuarto = null;
		this.codigoUnidadeFuncional = null;
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public String pesquisarDisponibilidadeLeito(Short codigoQuarto, Integer codigoClinica){	
		this.disponibilidadeLeitoPaginatorController.setPacCodigo(this.codigoPaciente);
		this.disponibilidadeLeitoPaginatorController.setQuarto(this.cadastrosBasicosInternacaoFacade.obterQuarto(codigoQuarto));
		this.disponibilidadeLeitoPaginatorController.setClinica(this.aghuFacade.obterClinica(codigoClinica));
		return PAGE_PESQUISAR_DISPONIBILIDADE_LEITO;
	}
	
	public String solicitarTransferenciaPaciente(){		
		this.solicitacoesTransferenciaPacientePaginatorController.setVoltarPara("pesquisarDisponibilidadeQuarto");
		return PAGE_SOLICITACOES_TRANSFERENCIA_PACIENTE_LIST;
	}
	
	
	public String transferirPaciente(){		
		this.transferirPacientePaginatorController.setVoltarPara("pesquisarDisponibilidadeQuarto");
		return PAGE_TRANSFERIR_PACIENTE_LIST;
	}

	/**
	 * Método para atribuir quarto selecionado na suggestion ao atributo
	 * this.quarto da controller.
	 * 
	 * @param quarto
	 */
	public void atribuirQuarto(final AinQuartos quarto) {
		this.quarto = quarto;
	}

	/**
	 * Método que retorna uma coleções de clínicas p/ preencher a suggestion
	 * box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghClinicas> pesquisarClinicas(final String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicas(paramPesquisa == null ? null
				: paramPesquisa);
	}

	/**
	 * Método para atribuir clinica selecionado na suggestion ao atributo
	 * this.clinica da controller.
	 * 
	 * @param clinica
	 */
	public void atribuirClinica(final AghClinicas clinica) {
		this.clinica = clinica;
	}

	@Override
	public Long recuperarCount() {
		this.codigoQuarto = this.quarto == null ? null : this.quarto.getNumero();
		this.codigoClinica = this.clinica == null ? null : this.clinica.getCodigo();
		this.codigoUnidadeFuncional = this.unidadeFuncional == null ? null
				: this.unidadeFuncional.getSeq();

		// Passando 0 para firstResult e MaxResult (1º e 2º parametros do método
		// pesquisarQuartos) a busca não é paginada
		final List<QuartoDisponibilidadeVO> quartosList = this.pesquisaInternacaoFacade.pesquisarDisponibilidadeQuartosVO(0, 0, this.codigoQuarto, this.codigoClinica, this.codigoUnidadeFuncional);
		return Long.valueOf(quartosList.size());
	}

	@Override
	public List<QuartoDisponibilidadeVO> recuperarListaPaginada(
			final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc) {

		this.codigoQuarto = this.quarto == null ? null : this.quarto.getNumero();
		this.codigoClinica = this.clinica == null ? null : this.clinica.getCodigo();
		this.codigoUnidadeFuncional = this.unidadeFuncional == null ? null
				: this.unidadeFuncional.getSeq();

		//FIXME : Fazer paginação em memória para que a ordenação funcione corretamente by: Geraldo Maciel
		final List<QuartoDisponibilidadeVO> quartoList = this.pesquisaInternacaoFacade.pesquisarDisponibilidadeQuartosVO(firstResult, maxResult, this.codigoQuarto, this.codigoClinica, this.codigoUnidadeFuncional);
		this.pesquisaInternacaoFacade.pesquisarCapacIntQrt(quartoList);
		this.pesquisaInternacaoFacade.pesquisarTotalIntQrt(quartoList);
		this.pesquisaInternacaoFacade.pesquisarTotSolTransPndQrt(quartoList);
		this.pesquisaInternacaoFacade.pesquisarDispVagasQrt(quartoList);
		AGHUUtil.ordenarLista(quartoList, orderProperty, asc);
		return quartoList;
	}

	public String actionInternar(final QuartoDisponibilidadeVO quartoVO) {
		this.codigoQuarto = quartoVO.getQuarto();
		final AinQuartos quarto = this.cadastrosBasicosInternacaoFacade.obterQuarto(this.codigoQuarto);
		try {
			this.pesquisaInternacaoFacade.consistirQuarto(quarto);
			if (this.pacienteInternacao != null) {
				this.pesquisaInternacaoFacade.consistirSexoQuarto(this.pacienteInternacao, quarto);
			}
		}
		catch (final ApplicationBusinessException ex) {
			this.setModalMessage(WebUtil.initLocalizedMessage(ex.getCode().toString(), null));
			return null;
		}
		if (this.pacienteInternacao != null) {
			return this.redirecionarPaginaCadastroInternacao();
		} else {
			return this.redirecionarPaginaPesquisaPaciente();
		}
	}
	
	public String redirecionarPaginaCadastroInternacao(){
		this.cadastroInternacaoController.setAipPacCodigo(this.codigoPaciente);
		this.cadastroInternacaoController.setAinQuartoNumero(this.codigoQuarto.intValue());	
		this.cadastroInternacaoController.setAinAtendimentoUrgenciaSeq(this.seqAtendimentoUrgencia);
		return PAGE_CADASTRO_INTERNACAO;
	}
	
	public String redirecionarPaginaPesquisaPaciente(){
		this.pesquisaPacienteController.setQuartoNumero(this.codigoQuarto);
		this.pesquisaPacienteController.setCameFrom("pesquisarDisponibilidadeQuarto");
		return PAGE_PESQUISA_PACIENTE;
	}

	public String voltar() {
		if(voltarPara != null){
			if(voltarPara.equalsIgnoreCase("pesquisarDisponibilidadeUnidade")){
				return PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE;
			}
			else if(voltarPara.equalsIgnoreCase("internacao")){
				return this.redirecionarPaginaCadastroInternacao();
			}
			else if(voltarPara.equalsIgnoreCase("pesquisa_paciente")){
				return this.redirecionarPaginaPesquisaPaciente();
			}
			else if(voltarPara.equalsIgnoreCase("internacao-pesquisarDisponibilidadeLeito")) {
				return PAGE_PESQUISAR_DISPONIBILIDADE_LEITO;
			}
		}
		return null;
	}

	public boolean exibirVoltar() {
		return this.voltarPara != null;
	}

	public AinQuartos getQuarto() {
		return this.quarto;
	}

	public void setQuarto(final AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AghClinicas getClinica() {
		return this.clinica;
	}

	public void setClinica(final AghClinicas clinica) {
		this.clinica = clinica;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(
			final String paramPesquisa) {
		return this.cadastrosBasicosInternacaoFacade
		.pesquisarUnidadeFuncionalPorCodigoEDescricao(paramPesquisa == null ? null
				: paramPesquisa);
	}

	public void atribuirUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(final AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Short getCodigoQuarto() {
		return this.codigoQuarto;
	}

	public void setCodigoQuarto(final Short codigoQuarto) {
		this.codigoQuarto = codigoQuarto;
	}

	public Short getCodigoUnidadeFuncional() {
		return this.codigoUnidadeFuncional;
	}

	public void setCodigoUnidadeFuncional(final Short codigoUnidadeFuncional) {
		this.codigoUnidadeFuncional = codigoUnidadeFuncional;
	}

	/**
	 * Método get para montar a descrição completa de um quarto, composta pelo
	 * andar, ala e descrição que o mesmo se encontra.
	 * 
	 * @return String com descrição completa
	 */
	public String getDescricaoCompletaQuarto() {
		return this.quarto != null ? this.cadastrosBasicosInternacaoFacade.obterDescricaoCompletaQuarto(this.quarto.getNumero()) : "";

	}

	public Integer getCodigoClinica() {
		return this.codigoClinica;
	}

	public void setCodigoClinica(final Integer codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	public Integer getCodigoPaciente() {
		return this.codigoPaciente;
	}

	public void setCodigoPaciente(final Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPacienteInternacao() {
		return this.pacienteInternacao;
	}

	public void setPacienteInternacao(final AipPacientes pacienteInternacao) {
		this.pacienteInternacao = pacienteInternacao;
	}

	public void setVoltarPara(final String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return this.voltarPara;
	}

	public void setModalMessage(final String modalMessage) {
		this.modalMessage = modalMessage;
	}

	public String getModalMessage() {
		return this.modalMessage;
	}

	public void setSeqAtendimentoUrgencia(final Integer seqAtendimentoUrgencia) {
		this.seqAtendimentoUrgencia = seqAtendimentoUrgencia;
	}

	public Integer getSeqAtendimentoUrgencia() {
		return this.seqAtendimentoUrgencia;
	}

	public DynamicDataModel<QuartoDisponibilidadeVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<QuartoDisponibilidadeVO> dataModel) {
		this.dataModel = dataModel;
	}
}