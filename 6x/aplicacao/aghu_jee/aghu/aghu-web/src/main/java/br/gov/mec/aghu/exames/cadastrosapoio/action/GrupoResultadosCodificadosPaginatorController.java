package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.ResultadosCodificadosVO;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoResultadosCodificadosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4755086452411990046L;

	private static final String MANTER_GRUPO_RESULTADOS_CODIFICADOS = "manterGrupoResultadosCodificados";

	@EJB
	private IExamesFacade examesFacade;
		
	private ResultadosCodificadosVO resultadosVO = new ResultadosCodificadosVO();
	private Integer grupoResultCodifSeq;
	private String voltarPara;

	@Inject @Paginator
	private DynamicDataModel<AelGrupoResultadoCodificado> dataModel;
	
	private AelGrupoResultadoCodificado selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<AelGrupoResultadoCodificado> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(orderProperty == null){
			orderProperty = AelGrupoResultadoCodificado.Fields.DESCRICAO.toString();
			asc = true;
		}
		return  examesFacade.pesquisaGrupoResultadosCodificadosPorParametros(firstResult, maxResult, orderProperty, asc, resultadosVO);
	}

	@Override
	public Long recuperarCount() {
		return  examesFacade.pesquisaGrupoResultadosCodificadosPorParametrosCount(resultadosVO);
	}
	
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		resultadosVO = new ResultadosCodificadosVO();
		dataModel.limparPesquisa(); 
	}
	
	public String editar(){
		return MANTER_GRUPO_RESULTADOS_CODIFICADOS;
	}
	
	public String inserir(){
		return MANTER_GRUPO_RESULTADOS_CODIFICADOS;
	}
	
	public void excluir() {
		try {
			examesFacade.removerGrupoResultadoCodificado(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_GRUPO_RESULTADO_CODIFICADO");
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	
	public String voltar() {
		return voltarPara;
	}

	public Integer getGrupoResultCodifSeq() {
		return grupoResultCodifSeq;
	}

	public void setGrupoResultCodifSeq(Integer grupoResultCodifSeq) {
		this.grupoResultCodifSeq = grupoResultCodifSeq;
	}

	public ResultadosCodificadosVO getResultadosVO() {
		return resultadosVO;
	}

	public void setResultadosVO(ResultadosCodificadosVO resultadosVO) {
		this.resultadosVO = resultadosVO;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DynamicDataModel<AelGrupoResultadoCodificado> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AelGrupoResultadoCodificado> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoResultadoCodificado getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoResultadoCodificado selecionado) {
		this.selecionado = selecionado;
	}
}