package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioTipoCentroCusto;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCaracteristica;
import br.gov.mec.aghu.model.ScoCaracteristicaUsuarioCentroCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class CaracteristicaUserCCController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CaracteristicaUserCCController.class);

	private static final long serialVersionUID = 0L;

	private static final String CARACTERISTICA_USER_CC_LIST = "caracteristicaUserCCList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	private ScoCaracteristicaUsuarioCentroCusto  caracteristicaUserCC = new ScoCaracteristicaUsuarioCentroCusto();


	private Integer seq;
		
	private boolean consulta = false;

	private String voltarPara = null;

	//private static final Enum[] fetchArgsInnerJoin = {ScoCaracteristicaUsuarioCentroCusto.Fields.CENTRO_CUSTO, ScoCaracteristicaUsuarioCentroCusto.Fields.CARACTERISTICA};


	public String iniciar() {
	 

	 

		if (this.getSeq() == null) {
			this.setCaracteristicaUserCC(new ScoCaracteristicaUsuarioCentroCusto());
			this.getCaracteristicaUserCC().setTipoCcusto(DominioTipoCentroCusto.S);
			this.getCaracteristicaUserCC().setSlcHierarquiaCcusto(false);
			
		} else {
			caracteristicaUserCC = comprasCadastrosBasicosFacade.obterCaracteristicaUserCC(seq/*, fetchArgsInnerJoin, null*/);

			if(caracteristicaUserCC == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		return null;
	
	}

	public String gravar() {		
		try {
			if (this.getCaracteristicaUserCC() != null) {
				final boolean novo = this.getCaracteristicaUserCC().getSeq() == null;
				if (novo) {
					this.comprasCadastrosBasicosFacade.inserirCaracteristicaUserCC(this.getCaracteristicaUserCC());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CARACTERISTA_USER_CC_INSERT_SUCESSO");
				} else {
					this.comprasCadastrosBasicosFacade.alterarCaracteristicaUserCC(this.getCaracteristicaUserCC());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CARACTERISTA_USER_CC_UPDATE_SUCESSO");
				}
			}
			return cancelar();
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		seq = null;
		caracteristicaUserCC = null;
		return CARACTERISTICA_USER_CC_LIST;
	}

	// Metódo para Suggestion Box de Servidor
	public List<RapServidores> obterServidor(String objPesquisa) {

		try {
			return this.registroColaboradorFacade
					.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) {
		return this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustos(objPesquisa),listarCentroCustosSolicCount(objPesquisa));
	}

	public Long listarCentroCustosSolicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}

	/*** CARACTERISTICAS ****/
	public List<ScoCaracteristica> listarCaracteristicas(String objPesquisa) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarCaracteristicasPorCodigoOuDescricao(objPesquisa),listarCaracteristicasCount(objPesquisa));
	}

	public Long listarCaracteristicasCount(String objPesquisa) {
		return this.comprasCadastrosBasicosFacade.pesquisarCaracteristicasPorCodigoOuDescricaoCount(objPesquisa);
	}

	public void setConsulta(final boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setVoltarPara(final String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public ScoCaracteristicaUsuarioCentroCusto getCaracteristicaUserCC() {
		return caracteristicaUserCC;
	}

	public void setCaracteristicaUserCC(
			ScoCaracteristicaUsuarioCentroCusto caracteristicaUserCC) {
		this.caracteristicaUserCC = caracteristicaUserCC;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}