package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class VerbaGestaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7080622408294341630L;

	private static final String VERBA_GESTAO_CRUD = "verbaGestaoCRUD";
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	private FsoVerbaGestao verbaGestao;
	private DominioSituacao situacao;
	private DominioSimNao convenio;
	private Boolean realizarPesquisa = false;
	private Integer seqExclusao;
	private String descricaoVerba;
	private String nroInterno;
	private BigInteger nroConvSiafi;
	private String planoInterno;

	@Inject @Paginator
	private DynamicDataModel<FsoVerbaGestao> dataModel;
	
	private FsoVerbaGestao selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {		
		if (realizarPesquisa == true){
			limparPesquisa();
			pesquisar();	
			setRealizarPesquisa(false);
		}
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		setVerbaGestao(null);
		setSituacao(null);
		setConvenio(null);		
		setRealizarPesquisa(false);
		this.descricaoVerba = null;
		this.nroInterno = null;
		this.nroConvSiafi = null;
		this.planoInterno = null;
		
	}
	
	public List<FsoVerbaGestao> pesquisarVerbaGestaoPorCodigoOuDescricao(String filter){
		return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoPorSeqOuDescricao(filter);
	}
	
	public void pesquisar(){
	  dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		if (convenio != null) {
			return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoCount(verbaGestao, situacao, convenio.isSim(), descricaoVerba, nroInterno, nroConvSiafi, planoInterno);
		} else {
			return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestaoCount(verbaGestao, situacao, null, descricaoVerba, nroInterno, nroConvSiafi, planoInterno);
		}
	}

	@Override
	public List<FsoVerbaGestao> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {		
		if (convenio != null) {
			return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestao(firstResult, maxResult, orderProperty, asc, verbaGestao, situacao, convenio.isSim(), descricaoVerba, nroInterno, nroConvSiafi, planoInterno);
		} else {
			return cadastrosBasicosOrcamentoFacade.pesquisarVerbaGestao(firstResult, maxResult, orderProperty, asc, verbaGestao, situacao, null, descricaoVerba, nroInterno, nroConvSiafi, planoInterno);
		}
	}

	public String inserir(){
		return VERBA_GESTAO_CRUD;
	}

	public String editar(){
		return VERBA_GESTAO_CRUD;
	}
	
	public void excluir() {
		try {
			this.cadastrosBasicosOrcamentoFacade.excluirVerbaGestao(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VERBA_GESTAO_EXCLUIDA_COM_SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getRealizarPesquisa() {
		return realizarPesquisa;
	}

	public void setRealizarPesquisa(Boolean realizarPesquisa) {
		this.realizarPesquisa = realizarPesquisa;
	}

	public Integer getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Integer seqExclusao) {
		this.seqExclusao = seqExclusao;
	}

	public DominioSimNao getConvenio() {
		return convenio;
	}

	public void setConvenio(DominioSimNao convenio) {
		this.convenio = convenio;
	}

	public String getDescricaoVerba() {
		return descricaoVerba;
	}

	public void setDescricaoVerba(String descricaoVerba) {
		this.descricaoVerba = descricaoVerba;
	}

	public String getNroInterno() {
		return nroInterno;
	}

	public void setNroInterno(String nroInterno) {
		this.nroInterno = nroInterno;
	}

	public BigInteger getNroConvSiafi() {
		return nroConvSiafi;
	}

	public void setNroConvSiafi(BigInteger nroConvSiafi) {
		this.nroConvSiafi = nroConvSiafi;
	}

	public String getPlanoInterno() {
		return planoInterno;
	}

	public void setPlanoInterno(String planoInterno) {
		this.planoInterno = planoInterno;
	}

	public DynamicDataModel<FsoVerbaGestao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FsoVerbaGestao> dataModel) {
		this.dataModel = dataModel;
	}

	public FsoVerbaGestao getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(FsoVerbaGestao selecionado) {
		this.selecionado = selecionado;
	}
}