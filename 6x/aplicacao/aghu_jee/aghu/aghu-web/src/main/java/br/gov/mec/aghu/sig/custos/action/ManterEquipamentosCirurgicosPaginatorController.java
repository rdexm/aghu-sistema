package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.SigEquipamentoPatrimonio;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterEquipamentosCirurgicosPaginatorController extends ActionController implements ActionPaginator {

	private static final String ASSOCIAR_EQUIPAMENTO_CIRURGICO_CRUD = "associarEquipamentoCirurgicoCRUD";

	@Inject @Paginator
	private DynamicDataModel<MbcEquipamentoCirurgico> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterEquipamentosCirurgicosPaginatorController.class);

	private static final long serialVersionUID = -1502017164335662570L;
	
	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private MbcEquipamentoCirurgico equipamentoCirurgico;
	
	private Integer codigoEquipamento;
	
	private Integer seqEquipamentoCirurgico;
	
	private MbcEquipamentoCirurgico parametroSelecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarEquipamentoCirurgico","editar"));
		this.begin(conversation);
		LOG.debug("begin conversation");
	}
	
	
	public void iniciar() {
	 

		if(equipamentoCirurgico == null){
			equipamentoCirurgico = new MbcEquipamentoCirurgico();
			equipamentoCirurgico.setSituacao(DominioSituacao.A);
		}
	
	}
	
	public void pesquisar(){
		if((equipamentoCirurgico.getDescricao() == null || equipamentoCirurgico.getDescricao().equals("")) && codigoEquipamento == null){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PESQUISA_OBRIGATORIO_NOME_OU_CODIGO");
			return;
		}
		reiniciarPaginator();
	}

	public void limpar(){
		this.codigoEquipamento = null;
		equipamentoCirurgico = null;
		this.iniciar();
		setAtivo(false);
	}

	public String editar(){
		return ASSOCIAR_EQUIPAMENTO_CIRURGICO_CRUD;
	}
	
	public String visualizar(){
		return ASSOCIAR_EQUIPAMENTO_CIRURGICO_CRUD;
	}
	
	@Override
	public Long recuperarCount() {
		Short codigo = null;
		if(codigoEquipamento != null){
			codigo = codigoEquipamento.shortValue();
		}
		return blocoCirurgicoFacade.pesquisarEquipamentoCirurgicoCountModuloCusto(codigo, equipamentoCirurgico.getDescricao(), equipamentoCirurgico.getSituacao());
	}

	@Override
	public List<MbcEquipamentoCirurgico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short codigo = null;
		if(codigoEquipamento != null){
			codigo = codigoEquipamento.shortValue();
		}
		return  blocoCirurgicoFacade.pesquisarEquipamentoCirurgicoModuloCustos(codigo, equipamentoCirurgico.getDescricao(), equipamentoCirurgico.getSituacao(), firstResult, maxResult, orderProperty, asc);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	
	
	public String getDescricaoBem(SigEquipamentoPatrimonio equipamento){
		EquipamentoSistemaPatrimonioVO equipamentoVo = this.buscaEquipamentoModuloPatrimonio(equipamento);
		if(equipamentoVo == null){
			return null;	
		}else{		
			return equipamentoVo.getDescricao();
		}
	}
	
	private EquipamentoSistemaPatrimonioVO buscaEquipamentoModuloPatrimonio(SigEquipamentoPatrimonio equipamento) {
		try{
			Integer centroCustoUniversal = 0;
			return custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(equipamento.getCodPatrimonio(), centroCustoUniversal);
		}catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
			return null;
		}
	} 



	public MbcEquipamentoCirurgico getEquipamentoCirurgico() {
		return equipamentoCirurgico;
	}

	public void setEquipamentoCirurgico(MbcEquipamentoCirurgico equipamentoCirurgico) {
		this.equipamentoCirurgico = equipamentoCirurgico;
	}

	public Integer getCodigoEquipamento() {
		return codigoEquipamento;
	}

	public void setCodigoEquipamento(Integer codigoEquipamento) {
		this.codigoEquipamento = codigoEquipamento;
	}

	public Integer getSeqEquipamentoCirurgico() {
		return seqEquipamentoCirurgico;
	}

	public void setSeqEquipamentoCirurgico(Integer seqEquipamentoCirurgico) {
		this.seqEquipamentoCirurgico = seqEquipamentoCirurgico;
	}
	
	public DynamicDataModel<MbcEquipamentoCirurgico> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcEquipamentoCirurgico> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}


	public MbcEquipamentoCirurgico getParametroSelecionado() {
		return parametroSelecionado;
	}


	public void setParametroSelecionado(MbcEquipamentoCirurgico parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}
