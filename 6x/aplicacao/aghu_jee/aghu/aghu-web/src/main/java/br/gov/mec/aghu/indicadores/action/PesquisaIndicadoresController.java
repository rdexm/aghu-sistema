package br.gov.mec.aghu.indicadores.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.indicadores.business.IIndicadoresFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Classe responsável por controlar as ações da página de Indicadores.
 * 
 * @author Ricardo Costa
 */

public class PesquisaIndicadoresController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4458231037382850792L;

	@EJB
	private IIndicadoresFacade indicadoresFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private AghUnidadesFuncionais unidadeFuncional;

	private Date mes;

	private Date mesFim;

	private List<AinIndicadorHospitalarResumido> res = null;

	private Boolean exibirGrid = false;

	private DominioTipoIndicador tipoIndicador;

	public void iniciar() {
	 

		
		this.tipoIndicador = DominioTipoIndicador.G;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		this.mes = cal.getTime();
		//setIgnoreInitPageConfig(true);
		
	
	}

	public void pesquisar() {

		res = this.indicadoresFacade.obterTotaisIndicadoresUnidade(mes, mesFim, tipoIndicador, unidadeFuncional);

		this.exibirGrid = true;
	}

	/**
	 * Pesquisa as áreas funcionais.
	 * 
	 * @param strParam
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarAreasFuncionais(String strParam) {

		List<AghUnidadesFuncionais> res = pacienteFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(strParam, true, true, null);

		return res;
	}

	public void limparPesquisa() {
		this.exibirGrid = false;
		this.iniciar();
		this.mesFim = null;
		this.unidadeFuncional = null;
	}

	public void limparCampos() {
		this.exibirGrid = false;
		this.unidadeFuncional = null;
	}

	public Date getMes() {
		return mes;
	}

	public void setMes(Date mes) {
		this.mes = mes;
	}

	public List<AinIndicadorHospitalarResumido> getRes() {
		return res;
	}

	public void setRes(List<AinIndicadorHospitalarResumido> res) {
		this.res = res;
	}

	public Boolean getExibirGrid() {
		return exibirGrid;
	}

	public DominioTipoIndicador getTipoIndicador() {
		return tipoIndicador;
	}

	public void setTipoIndicador(DominioTipoIndicador tipoIndicador) {
		this.tipoIndicador = tipoIndicador;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getMesFim() {
		return mesFim;
	}

	public void setMesFim(Date mesFim) {
		this.mesFim = mesFim;
	}

}