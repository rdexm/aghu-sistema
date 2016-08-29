package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.pesquisa.business.ExameCancelarExceptionCode;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;



public class PesquisaCancelarExameColetaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3374934927967600920L;

	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	/*	fitro da tela de pesquisa	*/
	private PesquisaExamesFiltroVO filtro = new PesquisaExamesFiltroVO();

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

		limparPesquisa();
	
	}

	public String pesquisar() {
		String retorno = "exames-cancelarExamesColeta";
		try {

			List<PesquisaExamesPacientesResultsVO> examesCancelar =	pesquisaExamesFacade.buscaDadosItensSolicitacaoPorSoeSeq(this.filtro.getNumeroSolicitacaoInfo(),null);

			if(examesCancelar == null || examesCancelar.size()==0){
				throw new ApplicationBusinessException(ExameCancelarExceptionCode.AEL_01195);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			retorno = null;
		}
		return retorno;
	}

	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tele de pesquisa de exames.
	 */
	public void limparPesquisa() {
		setFiltro(new PesquisaExamesFiltroVO());
	}
	
	/**
	 * Metodo para limpeza da suggestion box de unidade executora
	 */
	// Metódo para Suggestion Box Unidade executora
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(Object param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoUnidadesExecutorasExames(param);
	}

	public PesquisaExamesFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaExamesFiltroVO filtro) {
		this.filtro = filtro;
	}
}