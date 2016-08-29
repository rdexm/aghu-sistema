package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.ResultadosCodificadosVO;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ResultadosCodificadosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4755086452411990046L;

	private static final String MANTER_GRUPO_RESULTADOS_CODIFICADOS_PESQUISA = "manterGrupoResultadosCodificadosPesquisa";

	@EJB
	private IExamesFacade examesFacade;

	private ResultadosCodificadosVO resultadosVO = new ResultadosCodificadosVO();
	private AelResultadoCodificado resultadoCodificado = new AelResultadoCodificado();

	@Inject @Paginator
	private DynamicDataModel<AelResultadoCodificado> dataModel;
	private AelResultadoCodificado selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public List<AelResultadoCodificado> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(orderProperty == null){
			orderProperty = AelResultadoCodificado.Fields.DESCRICAO.toString();
			asc = true;
		}
		
		return  examesFacade.pesquisaResultadosCodificadosPorParametros(firstResult, maxResult, orderProperty, asc, resultadosVO);
	}

	@Override
	public Long recuperarCount() {
		return  examesFacade.pesquisaResultadosCodificadosPorParametrosCount(resultadosVO);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		resultadosVO = new ResultadosCodificadosVO();
		resultadoCodificado = new AelResultadoCodificado();
		dataModel.limparPesquisa();
	}
	
	public void limpaResultadoPesquisa(){
		limparPesquisa();
	}
	
	public void gravar() {

		resultadoCodificado.setGrupoResulCodificado(resultadosVO.getGrupoResultadoCodificado());

		try{
			String message = null;				
			if (resultadoCodificado != null && resultadoCodificado.getId() == null) {
				message = "MENSAGEM_SUCESSO_INSERIR_RESULTADO_CODIFICADO";
			}else{
				message = "MENSAGEM_SUCESSO_ALTERAR_RESULTADO_CODIFICADO";
			}
			
			examesFacade.persistirResultadoCodificado(resultadoCodificado);
			this.apresentarMsgNegocio(Severity.INFO, message, resultadoCodificado.getDescricao());
			resultadoCodificado = new AelResultadoCodificado();
			dataModel.reiniciarPaginator();
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return;				
		}
	}
	
	public void excluir() {
		try {
			examesFacade.removerResultadoCodificado(selecionado.getId());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_RESULTADO_CODIFICADO", selecionado.getDescricao());

			resultadoCodificado = new AelResultadoCodificado();
			dataModel.reiniciarPaginator();

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public void editar(AelResultadoCodificado resultado){
		this.resultadoCodificado = resultado;
	}
	
	public String manterGrupos(){
		return  MANTER_GRUPO_RESULTADOS_CODIFICADOS_PESQUISA;
	}

	public void cancelarEdicao(){
		this.resultadoCodificado = new AelResultadoCodificado();
	}

	public List<AelGrupoResultadoCodificado> pesquisarGrupoResultadoCodificado(String objPesquisa) {
		return this.examesFacade.pesquisarGrupoResultadoCodificadoPorSeqDescricao(objPesquisa);
	}

	public ResultadosCodificadosVO getResultadosVO() {
		return resultadosVO;
	}

	public void setResultadosVO(ResultadosCodificadosVO resultadosVO) {
		this.resultadosVO = resultadosVO;
	}

	public AelResultadoCodificado getResultadoCodificado() {
		return resultadoCodificado;
	}

	public void setResultadoCodificado(AelResultadoCodificado resultadoCodificado) {
		this.resultadoCodificado = resultadoCodificado;
	}

	public DynamicDataModel<AelResultadoCodificado> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelResultadoCodificado> dataModel) {
		this.dataModel = dataModel;
	}

	public AelResultadoCodificado getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelResultadoCodificado selecionado) {
		this.selecionado = selecionado;
	}
}
