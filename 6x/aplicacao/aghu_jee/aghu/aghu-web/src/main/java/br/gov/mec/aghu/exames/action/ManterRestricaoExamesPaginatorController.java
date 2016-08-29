package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExigenciaExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterRestricaoExamesPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 4375862282112364375L;

	private static final String MANTER_RESTRICAO_EXAMES = "manterRestricaoExames";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private VAelUnfExecutaExames vAelUnfExecutaExames;
	private AelUnfExecutaExames unfExecutaExames;
	private AghUnidadesFuncionais unidadeFuncional;
	private	DominioSituacao situacao;

	@Inject @Paginator
	private DynamicDataModel<AelExigenciaExame> dataModel;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarExigenciaExameCount(unfExecutaExames, unidadeFuncional, situacao);
	}

	@Override
	public List<AelExigenciaExame> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisarExigenciaExame(firstResult, maxResult, orderProperty, asc, unfExecutaExames, unidadeFuncional, situacao);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		vAelUnfExecutaExames = null;
		unfExecutaExames = null;
		unidadeFuncional = null;
		situacao = null;
	}
	
	public String editar(){
		return MANTER_RESTRICAO_EXAMES;
	}
	
	public String inserir(){
		return MANTER_RESTRICAO_EXAMES;
	}

	public void limparUnidadeExecutora() {
		unfExecutaExames = null;
	}
	
	public void setarUnidadeExecutora() {
		if(vAelUnfExecutaExames != null) {
			unfExecutaExames = this.examesFacade.obterAelUnfExecutaExames(vAelUnfExecutaExames.getId().getSigla(), vAelUnfExecutaExames.getId().getManSeq(), vAelUnfExecutaExames.getId().getUnfSeq());
		}
	}
	
	public void ativarDesativar(AelExigenciaExame exigenciaExame) {
		try {
			Boolean ativar = false;
			if(!exigenciaExame.getIndSituacao().isAtivo()) {
				ativar = true;
			}
			
			exigenciaExame.setIndSituacao(DominioSituacao.getInstance(!exigenciaExame.getIndSituacao().isAtivo()));
			this.examesFacade.persistirAelExigenciaExame(exigenciaExame);
			
			if(ativar) {
				this.apresentarMsgNegocio(Severity.INFO,"RESTRICAO_EXAME_ATIVADA_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"RESTRICAO_EXAME_DESATIVADA_SUCESSO");
			}
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<VAelUnfExecutaExames> pesquisarPorSiglaOuMaterialOuExameOuUnidade(String filtro){
		return this.examesFacade.pesquisarPorSiglaOuMaterialOuExameOuUnidade((String) filtro);
	}
	
	public Long pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount(Object filtro){
		return this.examesFacade.pesquisarPorSiglaOuMaterialOuExameOuUnidadeCount((String) filtro);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(String filtro) {
		return this.aghuFacade.listarAghUnidadesFuncionais(filtro);
	}

	public VAelUnfExecutaExames getvAelUnfExecutaExames() {
		return vAelUnfExecutaExames;
	}

	public void setvAelUnfExecutaExames(VAelUnfExecutaExames vAelUnfExecutaExames) {
		this.vAelUnfExecutaExames = vAelUnfExecutaExames;
	}

	public AelUnfExecutaExames getUnfExecutaExames() {
		return unfExecutaExames;
	}

	public void setUnfExecutaExames(AelUnfExecutaExames unfExecutaExames) {
		this.unfExecutaExames = unfExecutaExames;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<AelExigenciaExame> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelExigenciaExame> dataModel) {
		this.dataModel = dataModel;
	}
}
