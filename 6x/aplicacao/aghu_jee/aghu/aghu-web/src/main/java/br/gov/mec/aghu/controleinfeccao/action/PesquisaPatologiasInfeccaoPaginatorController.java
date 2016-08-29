package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaPatologiasInfeccaoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 9089840032121263323L;

	private static final String PAGE_CADASTRO_PATOLOGIAS_INFECCAO = "cadastroPatologiasInfeccao";

	private static final String BREAK_LINE = "<br/>";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	@Inject @Paginator
	private DynamicDataModel<MciPatologiaInfeccao> dataModel;

	private MciPatologiaInfeccao parametroSelecionado;

	// Filtro principal da pesquisa
	private MciPatologiaInfeccao filtro = new MciPatologiaInfeccao();

	private boolean sliderAberto = true;

	/*
	 * Filtros adicionais
	 */
	private DominioSimNao notificaSsma; // Notificação Compulsória
	private DominioSimNao impNotificacao; // Impressão Notificação
	private DominioSimNao higienizacaoMaos;
	private DominioSimNao tecnicaAsseptica; // Uso de Técnica Asséptica ou Luvas
	private DominioSimNao usoAvental;
	private DominioSimNao usoMascara;
	private DominioSimNao usoMascaraN95;
	private DominioSimNao usoOculos;
	private DominioSimNao usoQuartoPrivativo;
	
	private static final String LISTA_PACIENTES = "controleinfeccao-listaPacientes";
	private static final String DETALHES_PACIENTE = "controleinfeccao-detalharPacienteCCIH";
	
	private String voltarPara;
	private boolean permConsultaTela = false;

	@EJB
	private IPermissionService permissionService;

	public void iniciar() {
	 
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterPatologiaInfeccao", "executar");
		
		if (LISTA_PACIENTES.equals(voltarPara) || DETALHES_PACIENTE.equals(voltarPara)) {
			this.getDataModel().setUserRemovePermission(false);
			this.setPermConsultaTela(true);
			pesquisar();
		} else {
			this.getDataModel().setUserRemovePermission(permissao);
		}
		this.getDataModel().setUserEditPermission(permissao);
	
	}
	

	/**
	 * Configura filtro da pesquisa convertendo DominioSimNao em atributos
	 * booleanos
	 */
	private void configurarFiltro() {
		this.filtro.setNotificaSsma(DominioSimNao.getBooleanInstance(this.notificaSsma));
		this.filtro.setImpNotificacao(DominioSimNao.getBooleanInstance(this.impNotificacao));
		this.filtro.setHigienizacaoMaos(DominioSimNao.getBooleanInstance(this.higienizacaoMaos));
		this.filtro.setTecnicaAsseptica(DominioSimNao.getBooleanInstance(this.tecnicaAsseptica));
		this.filtro.setUsoAvental(DominioSimNao.getBooleanInstance(this.usoAvental));
		this.filtro.setUsoMascara(DominioSimNao.getBooleanInstance(this.usoMascara));
		this.filtro.setUsoMascaraN95(DominioSimNao.getBooleanInstance(this.usoMascaraN95));
		this.filtro.setUsoOculos(DominioSimNao.getBooleanInstance(this.usoOculos));
		this.filtro.setUsoQuartoPrivativo(DominioSimNao.getBooleanInstance(this.usoQuartoPrivativo));
	}

	/*
	 * Pesquisa
	 */

	@Override
	public List<MciPatologiaInfeccao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.configurarFiltro();
		return this.controleInfeccaoFacade.pesquisarPatologiaInfeccao(firstResult, maxResult, orderProperty, asc, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		this.configurarFiltro();
		return this.controleInfeccaoFacade.pesquisarPatologiaInfeccaoCount(this.filtro);
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.sliderAberto = false;
	}

	public void limpar() {
		this.filtro = new MciPatologiaInfeccao();
		this.notificaSsma = null;
		this.impNotificacao = null;
		this.higienizacaoMaos = null;
		this.tecnicaAsseptica = null;
		this.usoAvental = null;
		this.usoMascara = null;
		this.usoMascaraN95 = null;
		this.usoOculos = null;
		this.usoQuartoPrivativo = null;
		this.dataModel.limparPesquisa();
		this.sliderAberto = true;
	}

	public String editar() {
		return PAGE_CADASTRO_PATOLOGIAS_INFECCAO;
	}

	public void excluir() {
		try {
			String descricao = this.parametroSelecionado.getDescricao();
			this.controleInfeccaoFacade.removerMciPatologiaInfeccao(this.parametroSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PATOLOGIA", descricao);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Deve exibir todos os campos do registro que não aparecem na listagem
	 * 
	 * @param item
	 * @return
	 */
	public String obterHint(MciPatologiaInfeccao item) {

		StringBuilder buffer = new StringBuilder(256);

		buffer.append("Situação: ").append(item.getSituacao().getDescricao()).append(BREAK_LINE);

		String descricaoDuracaoMedidaPreventiva = "";
		if (item.getDuracaoMedidaPreventiva() != null) {
			descricaoDuracaoMedidaPreventiva = item.getDuracaoMedidaPreventiva().getDescricao();
		}
		buffer.append("Duração da Medida Preventiva: ").append(descricaoDuracaoMedidaPreventiva).append(BREAK_LINE);

		String descricaoTopografiaInfeccao = "";
		if (item.getTopografiaInfeccao() != null) {
			descricaoDuracaoMedidaPreventiva = item.getTopografiaInfeccao().getDescricao();
		}
		buffer.append("Topografia de Infecção: ").append(descricaoTopografiaInfeccao).append(BREAK_LINE);

		String descricaoNotificacaoCompulsoria = DominioSimNao.getInstance(item.getNotificaSsma()).getDescricao();
		buffer.append("Notificacao Compulsória: ").append(descricaoNotificacaoCompulsoria).append(BREAK_LINE);

		String descricaoImprimeNotificacao = DominioSimNao.getInstance(item.getImpNotificacao()).getDescricao();
		buffer.append("Imprime Notificação: ").append(descricaoImprimeNotificacao).append(BREAK_LINE);

		String observacao = "";
		if (StringUtils.isNotBlank(item.getObservacao())) {
			observacao = StringUtils.substring(item.getObservacao(), 0, 120).concat("...");
		}
		buffer.append("Observação: ").append(observacao).append(BREAK_LINE);

		return buffer.toString();
	}

	/*
	 * Pesquisas SuggestionBox
	 */

	public List<MciDuracaoMedidaPreventiva> pesquisarDuracaoMedidaPreventiva(String parametro) {
		return this.returnSGWithCount(this.controleInfeccaoFacade.pesquisarDuracaoMedidaPreventivaPatologiaInfeccao(parametro),pesquisarDuracaoMedidaPreventivaCount(parametro));
	}

	public Long pesquisarDuracaoMedidaPreventivaCount(String parametro) {
		return this.controleInfeccaoFacade.pesquisarDuracaoMedidaPreventivaPatologiaInfeccaoCount(parametro);
	}

	public List<MciTopografiaInfeccao> pesquisarTopografiaInfeccao(String parametro) {
		return this.returnSGWithCount(this.controleInfeccaoFacade.pesquisarTopografiaInfeccaoPatologiaInfeccao(parametro),pesquisarTopografiaInfeccaoCount(parametro));
	}

	public Long pesquisarTopografiaInfeccaoCount(String parametro) {
		return this.controleInfeccaoFacade.pesquisarTopografiaInfeccaoPatologiaInfeccaoCount(parametro);
	}

	public String voltar(){
		permConsultaTela = false;
		return voltarPara;
	}
	
	/*
	 * Getters and setters
	 */

	public DynamicDataModel<MciPatologiaInfeccao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MciPatologiaInfeccao> dataModel) {
		this.dataModel = dataModel;
	}

	public MciPatologiaInfeccao getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MciPatologiaInfeccao parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public MciPatologiaInfeccao getFiltro() {
		return filtro;
	}

	public void setFiltro(MciPatologiaInfeccao filtro) {
		this.filtro = filtro;
	}

	public DominioSimNao getNotificaSsma() {
		return notificaSsma;
	}

	public void setNotificaSsma(DominioSimNao notificaSsma) {
		this.notificaSsma = notificaSsma;
	}

	public DominioSimNao getImpNotificacao() {
		return impNotificacao;
	}

	public void setImpNotificacao(DominioSimNao impNotificacao) {
		this.impNotificacao = impNotificacao;
	}

	public DominioSimNao getTecnicaAsseptica() {
		return tecnicaAsseptica;
	}

	public void setTecnicaAsseptica(DominioSimNao tecnicaAsseptica) {
		this.tecnicaAsseptica = tecnicaAsseptica;
	}

	public DominioSimNao getUsoAvental() {
		return usoAvental;
	}

	public void setUsoAvental(DominioSimNao usoAvental) {
		this.usoAvental = usoAvental;
	}

	public DominioSimNao getUsoMascara() {
		return usoMascara;
	}

	public void setUsoMascara(DominioSimNao usoMascara) {
		this.usoMascara = usoMascara;
	}

	public DominioSimNao getUsoQuartoPrivativo() {
		return usoQuartoPrivativo;
	}

	public void setUsoQuartoPrivativo(DominioSimNao usoQuartoPrivativo) {
		this.usoQuartoPrivativo = usoQuartoPrivativo;
	}

	public DominioSimNao getHigienizacaoMaos() {
		return higienizacaoMaos;
	}

	public void setHigienizacaoMaos(DominioSimNao higienizacaoMaos) {
		this.higienizacaoMaos = higienizacaoMaos;
	}

	public DominioSimNao getUsoMascaraN95() {
		return usoMascaraN95;
	}

	public void setUsoMascaraN95(DominioSimNao usoMascaraN95) {
		this.usoMascaraN95 = usoMascaraN95;
	}

	public DominioSimNao getUsoOculos() {
		return usoOculos;
	}

	public void setUsoOculos(DominioSimNao usoOculos) {
		this.usoOculos = usoOculos;
	}

	public boolean isSliderAberto() {
		return sliderAberto;
	}

	public void setSliderAberto(boolean sliderAberto) {
		this.sliderAberto = sliderAberto;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isPermConsultaTela() {
		return permConsultaTela;
	}

	public void setPermConsultaTela(boolean permConsultaTela) {
		this.permConsultaTela = permConsultaTela;
	}

}