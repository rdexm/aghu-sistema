package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ListarTipoFrequenciaAprazamentoPaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -5859654119880287136L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private MpmTipoFrequenciaAprazamento entity;
	private MpmTipoFrequenciaAprazamento entityFilter;
	private List<MpmTipoFrequenciaAprazamento> entityList;
	private List<MpmAprazamentoFrequencia> aprazamentosList;
	private MpmAprazamentoFrequencia aprazamentoFrequencia;
	private DominioSimNao indDigitaFrequencia, indUsoHemoterapia, indUsoQuimioterapia;
	private Boolean excluirTipoFrequencia;
	private Boolean ativo;
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}
	
	public void inicio() {

		if (entityFilter != null && entityList != null) {
			pesquisar();
		} else if (entityList == null) {
			entityList = new ArrayList<MpmTipoFrequenciaAprazamento>();
			ativo = false;
		} else {
			if (!entityList.isEmpty()) {
				ativo = true;
			}
		}
		entity = new MpmTipoFrequenciaAprazamento();
		entityFilter = new MpmTipoFrequenciaAprazamento();
		aprazamentosList = null;
	}

	public void pesquisar() {
		entityList = recuperarListaPaginada(1, 100, "", true);
		entity = null;
		ativo = true;
	}

	public void limpar() {
		entityList = null;
		aprazamentosList = null;
		entity = null;
		entityFilter = null;
		indUsoHemoterapia = null;
		indUsoQuimioterapia = null;
		indDigitaFrequencia = null;
		setAtivo(false);
	}

	public void excluirTipoFrequencia() {
		try {
			prescricaoMedicaFacade.excluirTipoFrequenciaAprazamento(entity.getSeq());
			pesquisar();
			this.apresentarMsgNegocio(Severity.INFO,"MSG_TIPO_FREQUENCIA_APRAZAMENTO_EXCLUSAO1");
		} catch (ApplicationBusinessException ex) {
			apresentarExcecaoNegocio(ex);
		}
	}

	public void excluirFrequenciaAprazamento() {
		try {
			prescricaoMedicaFacade.excluirAprazamentoFrequencia(aprazamentoFrequencia.getId());
			aprazamentosList = new ArrayList<MpmAprazamentoFrequencia>(entity.getAprazamentoFrequencias());
			this.apresentarMsgNegocio(Severity.INFO,"MSG_TIPO_FREQUENCIA_APRAZAMENTO_EXCLUSAO2");
		} catch (ApplicationBusinessException ex) {
			apresentarExcecaoNegocio(ex);
		}
	}

	@Override
	public Long recuperarCount() {
		return prescricaoMedicaFacade.countTipoFrequenciaAprazamento(entityFilter);
	}

	@Override
	public List<MpmTipoFrequenciaAprazamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		entityFilter.setIndDigitaFrequencia(indDigitaFrequencia == null ? null : indDigitaFrequencia.isSim());
		entityFilter.setIndUsoHemoterapia(indUsoHemoterapia == null ? null : indUsoHemoterapia.isSim());
		entityFilter.setIndUsoQuimioterapia(indUsoQuimioterapia == null ? null : indUsoQuimioterapia.isSim());
		
		return prescricaoMedicaFacade.pesquisarTipoFrequenciaAprazamento( entityFilter, firstResult, maxResult, 
																			MpmTipoFrequenciaAprazamento.Fields.SEQ.toString(), true);
	}

	public DominioFormaCalculoAprazamento[] getItensForma() {
		return DominioFormaCalculoAprazamento.values();
	}

	public MpmTipoFrequenciaAprazamento getEntity() {
		return entity;
	}

	public void setEntity(MpmTipoFrequenciaAprazamento entity) {
		this.entity = entity;
	}

	public MpmTipoFrequenciaAprazamento getEntityFilter() {
		if(this.entityFilter == null){
			this.entityFilter = new MpmTipoFrequenciaAprazamento();
		}
		return entityFilter;
	}

	public void setEntityFilter(MpmTipoFrequenciaAprazamento entityFilter) {
		this.entityFilter = entityFilter;
	}

	public List<MpmAprazamentoFrequencia> getAprazamentosList() {
		if (entity != null && entity.getSeq() != null) {
			this.entity = this.prescricaoMedicaFacade.obterTipoFrequenciaAprazamentoId(entity.getSeq());
			aprazamentosList = new ArrayList<MpmAprazamentoFrequencia>(
					entity.getAprazamentoFrequencias());
		} else {
			aprazamentosList = null;
		}
		return aprazamentosList;
	}
	
	public void update(MpmTipoFrequenciaAprazamento entity){
		this.entity = entity;
	}

	public DominioSimNao getIndDigitaFrequencia() {
		return indDigitaFrequencia;
	}

	public void setIndDigitaFrequencia(DominioSimNao indDigitaFrequencia) {
		this.indDigitaFrequencia = indDigitaFrequencia;
	}

	public DominioSimNao getIndUsoHemoterapia() {
		return indUsoHemoterapia;
	}

	public void setIndUsoHemoterapia(DominioSimNao indUsoHemoterapia) {
		this.indUsoHemoterapia = indUsoHemoterapia;
	}

	public DominioSimNao getIndUsoQuimioterapia() {
		return indUsoQuimioterapia;
	}

	public void setIndUsoQuimioterapia(DominioSimNao indUsoQuimioterapia) {
		this.indUsoQuimioterapia = indUsoQuimioterapia;
	}

	public MpmAprazamentoFrequencia getAprazamentoFrequencia() {
		return aprazamentoFrequencia;
	}

	public void setAprazamentoFrequencia(
			MpmAprazamentoFrequencia aprazamentoFrequencia) {
		this.aprazamentoFrequencia = aprazamentoFrequencia;
	}

	public Boolean getExcluirTipoFrequencia() {
		return excluirTipoFrequencia;
	}

	public void setExcluirTipoFrequencia(Boolean excluirTipoFrequencia) {
		this.excluirTipoFrequencia = excluirTipoFrequencia;
	}

	public List<MpmTipoFrequenciaAprazamento> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<MpmTipoFrequenciaAprazamento> entityList) {
		this.entityList = entityList;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}
}