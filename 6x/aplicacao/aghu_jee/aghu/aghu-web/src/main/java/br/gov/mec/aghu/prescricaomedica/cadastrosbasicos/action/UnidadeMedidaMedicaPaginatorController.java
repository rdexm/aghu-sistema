package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class UnidadeMedidaMedicaPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -3688976514971537342L;
	
	private static final String PAGE_UNIDADE_MEDIDA_MEDICA_CRUD = "unidadeMedidaMedicaCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@Inject 
	private IParametroFacade parametroFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MpmUnidadeMedidaMedica> dataModel;
	
	private MpmUnidadeMedidaMedica parametroSelecionado;
	
	private Integer unidadeMedidaMedicaCodigo;
	private String descricaoMedidaMedica;
	private DominioSituacao indSituacao;
	private Integer codigoUnidadeMedidaMedicaExclusao;
	 
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.unidadeMedidaMedicaCodigo = null;
		this.descricaoMedidaMedica = null;
		this.indSituacao = null;
		this.getDataModel().limparPesquisa();
	}
	
	@Override
	public Long recuperarCount() {
		
		Long count = this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarUnidadesMedidaMedicaCount(
						(this.unidadeMedidaMedicaCodigo!=null)?this.unidadeMedidaMedicaCodigo:null,
						this.descricaoMedidaMedica,
						indSituacao
						);
		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MpmUnidadeMedidaMedica> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<MpmUnidadeMedidaMedica> result = this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarUnidadesMedidaMedica(firstResult, maxResult, orderProperty, asc, 
										(this.unidadeMedidaMedicaCodigo!=null)?this.unidadeMedidaMedicaCodigo:null,
										this.descricaoMedidaMedica,
										this.indSituacao
										);
		if (result == null) {
			result = new ArrayList<MpmUnidadeMedidaMedica>();
		}

		return result;
	}
	
	public void excluir()  {
		MpmUnidadeMedidaMedica unidade = this.cadastrosBasicosPrescricaoMedicaFacade.obterUnidadeMedicaPorId(this.parametroSelecionado.getSeq());
		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);
			
			Integer vlrParam = 0;
			
			if(parametro.getVlrNumerico() != null){
				vlrParam = parametro.getVlrNumerico().intValue();
			}
			
			if (unidade != null) {
				this.cadastrosBasicosPrescricaoMedicaFacade
					.removerUnidadeMedidaMedica(this.parametroSelecionado.getSeq(), vlrParam);
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_UNIDADE_MEDICA",unidade.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_REMOVER_UNIDADE_MEDIDA_MEDICA1");
			}
			
		this.unidadeMedidaMedicaCodigo = null;
		this.getDataModel().reiniciarPaginator();
		
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally{
			this.codigoUnidadeMedidaMedicaExclusao = null;
		}
	}
	
	public String inserirEditar() {
		this.setParametroSelecionado(null);
		return PAGE_UNIDADE_MEDIDA_MEDICA_CRUD;
	}

	/*
	 * Getters and Setters
	 */
	public DynamicDataModel<MpmUnidadeMedidaMedica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmUnidadeMedidaMedica> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmUnidadeMedidaMedica getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmUnidadeMedidaMedica parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	
	public Integer getUnidadeMedidaMedicaCodigo() {
		return unidadeMedidaMedicaCodigo;
	}

	public void setUnidadeMedidaMedicaCodigo(Integer unidadeMedidaMedicaCodigo) {
		this.unidadeMedidaMedicaCodigo = unidadeMedidaMedicaCodigo;
	}

	public String getDescricaoMedidaMedica() {
		return descricaoMedidaMedica;
	}

	public void setDescricaoMedidaMedica(String descricaoMedidaMedica) {
		this.descricaoMedidaMedica = descricaoMedidaMedica;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getCodigoUnidadeMedidaMedicaExclusao() {
		return codigoUnidadeMedidaMedicaExclusao;
	}
	
	public void setCodigoUnidadeMedidaMedicaExclusao(
			Integer codigoUnidadeMedidaMedicaExclusao) {
		this.codigoUnidadeMedidaMedicaExclusao = codigoUnidadeMedidaMedicaExclusao;
	}
}