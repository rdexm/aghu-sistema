package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author cicero.artifon
 */

public class ManterEquipesPorProfissionalPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final String PAGE_MANTER_EQUIPES_POR_PROFISSIONAL_LIST = "manterEquipesPorProfissionalList";
	@Inject @Paginator
	private DynamicDataModel<AghProfissionaisEquipe> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterEquipesPorProfissionalPaginatorController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1696368190410263628L;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private boolean exibirFieldsResultado = false;
	
	private AghProfissionaisEquipe equipePorProfissional;
	private RapServidores servidorSelecionado;
	private AghEquipes equipe;
	
	private Integer serMatricula;
	private Short serVinCodigo;
	private String voltarPara;

	
	/**
	 * <p>Método invocado ao acessar a tela</p>
	 * <p>Adicionado durante a implementação da estoria #8674</p>
	 * @author rafael.silvestre
	 */
	public void iniciar() {
		if (serMatricula != null && serVinCodigo != null) {
			servidorSelecionado = this.registroColaboradorFacade.obterRapServidorPorVinculoMatricula(serMatricula, serVinCodigo);
			pesquisar();
		}

	}

	
	/**
	 * Verifica se tem permissão <br>
	 * para mostrar o link de editar na tela.
	 * 
	 * @return
	 */
	public Boolean verificarPermissoes() {

		if (this.permissionService.usuarioTemPermissao(
				this.obterLoginUsuarioLogado(), "manterEquipesPorProfissional",
				"pesquisar")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirFieldsResultado = true;
		equipe = null;
	}
	
	public void limparPesquisa() {
		exibirFieldsResultado = false;
		equipe = null;
		servidorSelecionado = null;
	}
	
	public List<RapServidores> obterListaRapServidores(String filtro) {
		return  this.returnSGWithCount(registroColaboradorFacade.pesquisarServidoresOrdenadoPorVinCodigo(filtro),obterListaRapServidoresCount(filtro));
	}
	
	public Long obterListaRapServidoresCount(String filtro) {
		return this.registroColaboradorFacade.pesquisarServidoresCount(filtro);
	}
	
	public List<AghEquipes> obterListaAghEquipes(String filtro) {
		return  this.returnSGWithCount(this.internacaoFacade.pesquisarListaAghEquipes((String) filtro),obterListaAghEquipesCount(filtro));
	}
	
	public Long obterListaAghEquipesCount(String filtro) {
		return this.internacaoFacade.pesquisarListaAghEquipesCount((String) filtro);
	}
	
	@Override
	public Long recuperarCount() {
		return internacaoFacade.pesquisarListaEquipesPorProfissionalPorServidorCount(servidorSelecionado);
	}
	
	@Override
	public List<AghProfissionaisEquipe> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AghProfissionaisEquipe> resultadoBusca = internacaoFacade.pesquisarListaEquipesPorProfissionalPorServidorPaginado(firstResult, maxResult, orderProperty, asc, servidorSelecionado);
		if (resultadoBusca == null) {
			resultadoBusca = new ArrayList<AghProfissionaisEquipe>();
		}
		return resultadoBusca;
	}
	
	public String excluir(AghProfissionaisEquipe equipeExcluida) {
		AghProfissionaisEquipeId equipePorProfissionalIdParaExcluir = new AghProfissionaisEquipeId();
		equipePorProfissionalIdParaExcluir.setEqpSeq(equipeExcluida.getId().getEqpSeq());
		equipePorProfissionalIdParaExcluir.setSerMatricula(equipeExcluida.getId().getSerMatricula());
		equipePorProfissionalIdParaExcluir.setSerVinCodigo(equipeExcluida.getId().getSerVinCodigo());
		
		internacaoFacade.excluirEquipePorProfissional(equipePorProfissionalIdParaExcluir);
		apresentarMsgNegocio(Severity.INFO, "MSG_EXCLUIDO_SUCESSO", this.getBundle().getString("LABEL_EQUIPE_PROFISSIONAL_EQUIPE"));
		this.dataModel.reiniciarPaginator();
		return PAGE_MANTER_EQUIPES_POR_PROFISSIONAL_LIST;
	}
	
	public void salvar() {
		
		if (servidorSelecionado == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_CAMPO_OBRIGATORIO", this.getBundle().getString("LABEL_EQUIPES_POR_PROFICIONAL"));
			return ;
		}
		
		try {
			
			internacaoFacade.persistirEquipePorProfissional(servidorSelecionado, equipe);
			
			apresentarMsgNegocio(Severity.INFO, "MSG_INCLUIDO_SUCESSO", this.getBundle().getString("LABEL_EQUIPE_PROFISSIONAL_EQUIPE"));
			pesquisar();
			this.dataModel.reiniciarPaginator();
			
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada", e);
			if (e.getCause() instanceof ConstraintViolationException || e.getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio(Severity.ERROR, "AGH-00036");
			}
			return ;
		}
	}
	
	/**
	 * <p>Ação do botão Voltar</p>
	 * <p>Adicionado durante a implementação da estoria #8674</p>
	 * @author rafael.silvestre
	 */
	public String voltar() {
		return voltarPara;
	}

	
	public boolean isExibirFieldsResultado() {
		return exibirFieldsResultado;
	}
	
	public void setExibirFieldsResultado(boolean exibirFieldsResultado) {
		this.exibirFieldsResultado = exibirFieldsResultado;
	}
	
	public AghProfissionaisEquipe getEquipePorProfissional() {
		return equipePorProfissional;
	}
	
	public void setEquipePorProfissional(AghProfissionaisEquipe equipePorProfissional) {
		this.equipePorProfissional = equipePorProfissional;
	}
	
	public AghEquipes getEquipe() {
		return equipe;
	}
	
	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}
	
	public RapServidores getServidorSelecionado() {
		return servidorSelecionado;
	}
	
	public void setServidorSelecionado(RapServidores servidorSelecionado) {
		this.servidorSelecionado = servidorSelecionado;
	}

	public DynamicDataModel<AghProfissionaisEquipe> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghProfissionaisEquipe> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}
