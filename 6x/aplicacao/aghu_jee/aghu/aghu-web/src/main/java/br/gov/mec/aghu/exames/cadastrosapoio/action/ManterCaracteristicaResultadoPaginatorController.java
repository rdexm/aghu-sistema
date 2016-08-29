package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;

public class ManterCaracteristicaResultadoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3912312994328661993L;

	private static final String MANTER_CARACTERISTICA_RESULTADO_CRUD = "manterCaracteristicaResultadoCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject @Paginator
	private DynamicDataModel<AelResultadoCaracteristica> dataModel;
	
	private AelResultadoCaracteristica selecionado;

	private Integer codigo;
	private String descricao;
	private DominioSituacao situacao;
	private AelResultadoCaracteristica resultadoCaracteristica;
	
	private String voltarPara; 

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
	}

	private AelResultadoCaracteristica getElementoDadosBasicosExames(){
		// Cria objeto com os parâmetros para busca
		resultadoCaracteristica =  new AelResultadoCaracteristica();
		resultadoCaracteristica.setSeq(this.codigo);
		resultadoCaracteristica.setDescricao(StringUtils.trim(this.descricao));
		resultadoCaracteristica.setIndSituacao(this.situacao);
		
		return resultadoCaracteristica;
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarResultadosCaracteristicasCount(getElementoDadosBasicosExames());
	}

	@Override
	public List<AelResultadoCaracteristica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisarCaracteristicasResultados(firstResult, maxResult, orderProperty, asc, getElementoDadosBasicosExames());
	}

	public void excluir()  {
		try {
			this.cadastrosApoioExamesFacade.removerAelResultadoCaracteristica(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_CARACTERISTICA_RESULT", selecionado.getDescricao());
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_CARACTERISTICA_RESULTADO_CRUD;
	}
	
	public String editar(){
		return MANTER_CARACTERISTICA_RESULTADO_CRUD;
	}

	public String voltar() {
		return this.voltarPara;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public AelResultadoCaracteristica getResultadoCaracteristica() {
		return resultadoCaracteristica;
	}

	public void setResultadoCaracteristica(
			AelResultadoCaracteristica resultadoCaracteristica) {
		this.resultadoCaracteristica = resultadoCaracteristica;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DynamicDataModel<AelResultadoCaracteristica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AelResultadoCaracteristica> dataModel) {
		this.dataModel = dataModel;
	}

	public AelResultadoCaracteristica getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelResultadoCaracteristica selecionado) {
		this.selecionado = selecionado;
	}
}
