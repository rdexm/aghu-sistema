package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;



public class PesquisaExamesPorSolicitanteController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8404771509866674211L;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;


	private RapServidores servExame = new RapServidores();
	
	private Integer codigoSoeSelecionado;
	private Short iseSeqSelecionado;

	private List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa;
	private PesquisaExamesFiltroVO filtro;

	public void selecionarSolicitante(PesquisaExamesFiltroVO filtro) throws BaseException {
		setFiltro(filtro);
		setListaResultadoPesquisa(buscaExamesSolicitadosPorSolicitante());
	}

	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorSolicitante() throws BaseException {
		
		return this.pesquisaExamesFacade.buscaExamesSolicitadosPorSolicitante(filtro);
	}
	
	public boolean isDisableButtonDetalheExame(){
		return codigoSoeSelecionado==null; 
	}

	/** getters e setters **/

	public List<PesquisaExamesPacientesResultsVO> getListaResultadoPesquisa() {
		return listaResultadoPesquisa;
	}

	public void setListaResultadoPesquisa(
			List<PesquisaExamesPacientesResultsVO> listaResultadoPesquisa) {
		this.listaResultadoPesquisa = listaResultadoPesquisa;
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}

	public RapServidores getServExame() {
		return servExame;
	}

	public void setServExame(RapServidores servExame) {
		this.servExame = servExame;
	}

	public Integer getCodigoSoeSelecionado() {
		return codigoSoeSelecionado;
	}

	public void setCodigoSoeSelecionado(Integer codigoSoeSelecionado) {
		this.codigoSoeSelecionado = codigoSoeSelecionado;
	}

	public Short getIseSeqSelecionado() {
		return iseSeqSelecionado;
	}

	public void setIseSeqSelecionado(Short iseSeqSelecionado) {
		this.iseSeqSelecionado = iseSeqSelecionado;
	}
}