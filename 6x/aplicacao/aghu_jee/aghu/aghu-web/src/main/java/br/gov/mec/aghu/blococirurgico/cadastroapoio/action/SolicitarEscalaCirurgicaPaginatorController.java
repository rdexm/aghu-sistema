package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.action.RelatorioEscalaCirurgiasAghuController;
import br.gov.mec.aghu.blococirurgico.action.RelatorioSolicitacaoEscalaCirurgiasController;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgicaId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;


public class SolicitarEscalaCirurgicaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcControleEscalaCirurgica> dataModel;

	private static final Log LOG = LogFactory.getLog(SolicitarEscalaCirurgicaPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;
	private static final String SOLICITACAO = "blococirurgico-solicitarEscalaCirurgica";
	
	/*
	 * Injeções
	 */
	@Inject
	private RelatorioSolicitacaoEscalaCirurgiasController relatorioSolicitacaoEscalaCirurgiasController;
	
	@Inject
	private RelatorioEscalaCirurgiasAghuController relatorioEscalaCirurgiasAghuController;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	/*
	 * Campos do filtro
	 */
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataEscala;
	private DominioTipoEscala tipoEscala;

	// Item para exclusão
	private MbcControleEscalaCirurgica itemExclusao;
	// Item inclusao
	private MbcControleEscalaCirurgica controleEscalaCirurgica;
	private String nomeMicrocomputador = null;
	
	private Boolean outraUnidadeFuncional = Boolean.FALSE;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void iniciar() {
	 

	 


		// Garante que os resultados da pesquisa serão mantidos ao retonar na tela
		if (this.dataModel.getPesquisaAtiva()) {
			this.pesquisar();
		}
		
		if (unidadeFuncional == null ) {
			if(!this.outraUnidadeFuncional){
				unidadeFuncional = carregarUnidadeFuncionalInicial();
				if (unidadeFuncional != null) {
					this.pesquisar();
				}
			}
		}
		
	
	}
	

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limpar() {
		// Limpa filtro
		this.tipoEscala = null;
		this.dataEscala = null;
		// Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
		this.itemExclusao = null;
		this.controleEscalaCirurgica = null;
		this.outraUnidadeFuncional = Boolean.TRUE;
	}

	@Override
	public List<MbcControleEscalaCirurgica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarEscalasCirurgicas(firstResult, maxResult, orderProperty, asc, unidadeFuncional);
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarEscalasCirurgicasCount(unidadeFuncional);
	}

	/**
	 * Excluir item
	 */
	public void excluir() {
		if (this.itemExclusao != null) {
			try {
				// REMOVER
				this.blocoCirurgicoFacade.removerEscalaCirurgica(this.itemExclusao);
				this.pesquisar();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_ESCALA");

			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Cancelar exclusão
	 */
	public void cancelarExclusao() {
		this.itemExclusao = null;
	}

	/*
	 * Pesquisas das suggestion box
	 */
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e1) {
			LOG.error("Exceção capturada:", e1);
		}
		return null;
		
	}

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeFuncional(String filtro) {
		return this.returnSGWithCount(this.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(filtro),
				pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro));
	}
	
	private List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(String filtro) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(filtro);
	}
	
	private Long pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(String filtro) {
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro);
	}	

	public void gravar() {

		try {

			this.setFiltros();
			this.blocoCirurgicoFacade.inserirControleEscalaCirurgica(this.controleEscalaCirurgica, nomeMicrocomputador);
			// Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ESCALA");
			this.pesquisar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void atualizar(MbcControleEscalaCirurgica escalaCirurgica) {
		try {
			escalaCirurgica.setTipoEscala(DominioTipoEscala.D);
			this.blocoCirurgicoFacade.atualizarControleEscalaCirurgica(escalaCirurgica, nomeMicrocomputador);
			// Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_ESCALA");
			this.limpar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		this.pesquisar();
	}

	private void setFiltros() throws BaseException {
		controleEscalaCirurgica = new MbcControleEscalaCirurgica();
		controleEscalaCirurgica.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		MbcControleEscalaCirurgicaId id = new MbcControleEscalaCirurgicaId();
		controleEscalaCirurgica.setAghUnidadesFuncionais(unidadeFuncional);
		id.setUnfSeq(unidadeFuncional.getSeq());
		id.setDtEscala(dataEscala);
		controleEscalaCirurgica.setId(id);
		controleEscalaCirurgica.setTipoEscala(tipoEscala);
	}

	public String print(MbcControleEscalaCirurgica escalaCirurgica, boolean relatorioSimples) throws BaseException, JRException, SystemException, IOException, DocumentException {
		// Seta o tipo de relatório (CIRURGICA ou SIMPLES)
		this.relatorioSolicitacaoEscalaCirurgiasController.setRelatorioSimples(relatorioSimples);
		this.relatorioSolicitacaoEscalaCirurgiasController.setUnidadesFuncionais(this.unidadeFuncional);
		this.relatorioSolicitacaoEscalaCirurgiasController.setDataCirurgia(escalaCirurgica.getId().getDtEscala());
		this.relatorioSolicitacaoEscalaCirurgiasController.setTipoEscala(escalaCirurgica.getTipoEscala());
		return this.relatorioSolicitacaoEscalaCirurgiasController.print();
	}
	
	public String print(MbcControleEscalaCirurgica escalaCirurgica) throws BaseException, JRException, SystemException, IOException, DocumentException {
		this.relatorioEscalaCirurgiasAghuController.setUnidadesFuncionais(this.unidadeFuncional);
		this.relatorioEscalaCirurgiasAghuController.setDataCirurgia(escalaCirurgica.getId().getDtEscala());
		this.relatorioEscalaCirurgiasAghuController.setTipoEscala(escalaCirurgica.getTipoEscala());
		this.relatorioEscalaCirurgiasAghuController.setVoltarPara(SOLICITACAO);
		return this.relatorioEscalaCirurgiasAghuController.print();
	}

	/*
	 * Getters e Setters
	 */

	public Date getDataEscala() {
		return dataEscala;
	}

	public void setDataEscala(Date dataEscala) {
		this.dataEscala = dataEscala;
	}

	public DominioTipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(DominioTipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public MbcControleEscalaCirurgica getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(MbcControleEscalaCirurgica itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public MbcControleEscalaCirurgica getControleEscalaCirurgica() {
		return controleEscalaCirurgica;
	}

	public void setControleEscalaCirurgica(MbcControleEscalaCirurgica controleEscalaCirurgica) {
		this.controleEscalaCirurgica = controleEscalaCirurgica;
	}

	public Boolean getOutraUnidadeFuncional() {
		return outraUnidadeFuncional;
	}

	public void setOutraUnidadeFuncional(Boolean outraUnidadeFuncional) {
		this.outraUnidadeFuncional = outraUnidadeFuncional;
	}
 


	public DynamicDataModel<MbcControleEscalaCirurgica> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcControleEscalaCirurgica> dataModel) {
	 this.dataModel = dataModel;
	}
}